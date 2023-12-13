/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */
package org.elasticsearch.action.search;

import org.apache.logging.log4j.LogManager;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchPhaseResult;
import org.elasticsearch.search.SearchShardTarget;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.builder.SubSearchSourceBuilder;
import org.elasticsearch.search.dfs.AggregatedDfs;
import org.elasticsearch.search.dfs.DfsKnnResults;
import org.elasticsearch.search.dfs.DfsSearchResult;
import org.elasticsearch.search.internal.ShardSearchRequest;
import org.elasticsearch.search.query.QuerySearchRequest;
import org.elasticsearch.search.query.QuerySearchResult;
import org.elasticsearch.search.retriever.RetrieverBuilder;
import org.elasticsearch.search.vectors.KnnScoreDocQueryBuilder;
import org.elasticsearch.transport.Transport;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

/**
 * This search phase fans out to every shards to execute a distributed search with a pre-collected distributed frequencies for all
 * search terms used in the actual search query. This phase is very similar to a the default query-then-fetch search phase but it doesn't
 * retry on another shard if any of the shards are failing. Failures are treated as shard failures and are counted as a non-successful
 * operation.
 * @see CountedCollector#onFailure(int, SearchShardTarget, Exception)
 */
final class DfsQueryPhase extends SearchPhase {
    private final SearchPhaseResults<SearchPhaseResult> queryResult;
    private final List<DfsSearchResult> searchResults;
    private final AggregatedDfs dfs;
    private final List<DfsKnnResults> knnResults;
    private final Function<SearchPhaseResults<SearchPhaseResult>, SearchPhase> nextPhaseFactory;
    private final SearchPhaseContext context;
    private final SearchTransportService searchTransportService;
    private final SearchProgressListener progressListener;

    DfsQueryPhase(
        List<DfsSearchResult> searchResults,
        AggregatedDfs dfs,
        List<DfsKnnResults> knnResults,
        SearchPhaseResults<SearchPhaseResult> queryResult,
        Function<SearchPhaseResults<SearchPhaseResult>, SearchPhase> nextPhaseFactory,
        SearchPhaseContext context
    ) {
        super("dfs_query");
        this.progressListener = context.getTask().getProgressListener();
        this.queryResult = queryResult;
        this.searchResults = searchResults;
        this.dfs = dfs;
        this.knnResults = knnResults;
        this.nextPhaseFactory = nextPhaseFactory;
        this.context = context;
        this.searchTransportService = context.getSearchTransport();

        // register the release of the query consumer to free up the circuit breaker memory
        // at the end of the search
        context.addReleasable(queryResult::decRef);
    }

    @Override
    public void run() {
        SearchSourceBuilder searchSourceBuilder = context.getRequest().source();
        RetrieverBuilder<?> retrieverBuilder = searchSourceBuilder == null ? null : context.getRequest().source().getRetrieverBuilder();

        // TODO we can potentially also consume the actual per shard results from the initial phase here in the aggregateDfs
        // to free up memory early
        final CountedCollector<SearchPhaseResult> counter = new CountedCollector<>(
            queryResult,
            searchResults.size() * (retrieverBuilder == null ? 1 : retrieverBuilder.getQueryCount(searchSourceBuilder)),
            () -> context.executeNextPhase(this, nextPhaseFactory.apply(queryResult)),
            context
        );

        LogManager.getLogger(DfsQueryPhase.class).info("DFS QUERY START");

        for (final DfsSearchResult dfsResult : searchResults) {
            final SearchShardTarget shardTarget = dfsResult.getSearchShardTarget();
            Transport.Connection connection = context.getConnection(shardTarget.getClusterAlias(), shardTarget.getNodeId());
            ShardSearchRequest shardRequest = dfsResult.getShardSearchRequest();
            List<SearchSourceBuilder> queriesPerShard = new ArrayList<>();
            if (retrieverBuilder == null) {
                queriesPerShard.add(rewriteShardSearchRequest(shardRequest.shardRequestIndex(), shardRequest.source()));
            } else {
                queriesPerShard = retrieverBuilder.buildQuerySearchSourceBuilders(
                    shardRequest.shardRequestIndex(),
                    knnResults,
                    shardRequest.source()
                );
            }
            for (SearchSourceBuilder queryPerShard : queriesPerShard) {
                if (queriesPerShard.size() > 1) {
                    shardRequest = new ShardSearchRequest(dfsResult.getShardSearchRequest());
                }
                shardRequest.source(queryPerShard);
                QuerySearchRequest querySearchRequest = new QuerySearchRequest(
                    context.getOriginalIndices(dfsResult.getShardIndex()),
                    dfsResult.getContextId(),
                    shardRequest,
                    dfs
                );
                LogManager.getLogger(DfsQueryPhase.class).info("CONTEXT ID: " + querySearchRequest.contextId());
                final int shardIndex = dfsResult.getShardIndex();
                LogManager.getLogger(DfsQueryPhase.class).info("DFS QUERY: " + queryPerShard.query());
                searchTransportService.sendExecuteQuery(
                    connection,
                    querySearchRequest,
                    context.getTask(),
                    new SearchActionListener<>(shardTarget, shardIndex) {

                        @Override
                        protected void innerOnResponse(QuerySearchResult response) {
                            try {
                                StringBuilder builder = new StringBuilder();
                                for (ScoreDoc doc : response.topDocs().topDocs.scoreDocs) {
                                    builder.append(" | ");
                                    builder.append(doc);
                                }
                                LogManager.getLogger(DfsQueryPhase.class)
                                    .info("RESPONSE: " + response.getQueryIndex() + " | " + response.getShardIndex() + builder.toString());
                                response.setSearchProfileDfsPhaseResult(dfsResult.searchProfileDfsPhaseResult());
                                counter.onResult(response);
                            } catch (Exception e) {
                                context.onPhaseFailure(DfsQueryPhase.this, "", e);
                            }
                        }

                        @Override
                        public void onFailure(Exception exception) {
                            try {
                                context.getLogger()
                                    .debug(() -> "[" + querySearchRequest.contextId() + "] Failed to execute query phase", exception);
                                progressListener.notifyQueryFailure(shardIndex, shardTarget, exception);
                                counter.onFailure(shardIndex, shardTarget, exception);
                            } finally {
                                if (context.isPartOfPointInTime(querySearchRequest.contextId()) == false) {
                                    // the query might not have been executed at all (for example because thread pool rejected
                                    // execution) and the search context that was created in dfs phase might not be released.
                                    // release it again to be in the safe side
                                    context.sendReleaseSearchContext(
                                        querySearchRequest.contextId(),
                                        connection,
                                        context.getOriginalIndices(shardIndex)
                                    );
                                }
                            }
                        }
                    }
                );
            }
        }
    }

    // package private for testing
    SearchSourceBuilder rewriteShardSearchRequest(int shardIndex, SearchSourceBuilder source) {
        if (source == null || source.knnSearch().isEmpty()) {
            return source;
        }

        List<SubSearchSourceBuilder> subSearchSourceBuilders = new ArrayList<>(source.subSearches());

        int i = 0;
        for (DfsKnnResults dfsKnnResults : knnResults) {
            List<ScoreDoc> scoreDocs = new ArrayList<>();
            for (ScoreDoc scoreDoc : dfsKnnResults.scoreDocs()) {
                if (scoreDoc.shardIndex == shardIndex) {
                    scoreDocs.add(scoreDoc);
                }
            }
            scoreDocs.sort(Comparator.comparingInt(scoreDoc -> scoreDoc.doc));
            String nestedPath = dfsKnnResults.getNestedPath();
            QueryBuilder query = new KnnScoreDocQueryBuilder(scoreDocs.toArray(new ScoreDoc[0]));
            if (nestedPath != null) {
                query = new NestedQueryBuilder(nestedPath, query, ScoreMode.Max).innerHit(source.knnSearch().get(i).innerHit());
            }
            subSearchSourceBuilders.add(new SubSearchSourceBuilder(query));
            i++;
        }

        source = source.shallowCopy().subSearches(subSearchSourceBuilders).knnSearch(List.of());
        return source;
    }
}
