/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */

package org.elasticsearch.search.retriever;

import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.FieldDoc;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.search.SearchShardTask;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.cache.bitset.BitsetFilterCache;
import org.elasticsearch.index.mapper.IdLoader;
import org.elasticsearch.index.mapper.SourceLoader;
import org.elasticsearch.index.query.AbstractQueryBuilder;
import org.elasticsearch.index.query.ParsedQuery;
import org.elasticsearch.index.query.SearchExecutionContext;
import org.elasticsearch.index.shard.IndexShard;
import org.elasticsearch.search.DefaultSearchContext;
import org.elasticsearch.search.SearchExtBuilder;
import org.elasticsearch.search.SearchShardTarget;
import org.elasticsearch.search.aggregations.SearchContextAggregations;
import org.elasticsearch.search.collapse.CollapseContext;
import org.elasticsearch.search.dfs.DfsPhase;
import org.elasticsearch.search.dfs.DfsSearchResult;
import org.elasticsearch.search.fetch.FetchPhase;
import org.elasticsearch.search.fetch.FetchSearchResult;
import org.elasticsearch.search.fetch.StoredFieldsContext;
import org.elasticsearch.search.fetch.subphase.FetchDocValuesContext;
import org.elasticsearch.search.fetch.subphase.FetchFieldsContext;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.fetch.subphase.ScriptFieldsContext;
import org.elasticsearch.search.fetch.subphase.highlight.SearchHighlightContext;
import org.elasticsearch.search.internal.ContextIndexSearcher;
import org.elasticsearch.search.internal.ReaderContext;
import org.elasticsearch.search.internal.ScrollContext;
import org.elasticsearch.search.internal.SearchContext;
import org.elasticsearch.search.internal.ShardSearchContextId;
import org.elasticsearch.search.internal.ShardSearchRequest;
import org.elasticsearch.search.profile.Profilers;
import org.elasticsearch.search.query.QueryPhase;
import org.elasticsearch.search.query.QuerySearchResult;
import org.elasticsearch.search.rank.RankShardContext;
import org.elasticsearch.search.rescore.RescoreContext;
import org.elasticsearch.search.sort.SortAndFormats;
import org.elasticsearch.search.suggest.SuggestionSearchContext;

import java.util.ArrayList;
import java.util.List;

public class ClassicShardRetriever extends ShardRetriever {

    private ParsedQuery parsedQuery;
    private Query query;
    private FieldDoc searchAfter;
    private int terminateAfter = DEFAULT_TERMINATE_AFTER;
    private SortAndFormats sort;
    private Float minimumScore;
    private ParsedQuery postFilter;
    private List<RescoreContext> rescore;
    private CollapseContext collapse;

    private DfsSearchResult dfsResult;
    private QuerySearchResult queryResult;
    private FetchSearchResult fetchResult;

    public ClassicShardRetriever(SearchContext parentSearchContext, DfsPhase dfsPhase, FetchPhase fetchPhase) {
        super(parentSearchContext, dfsPhase, fetchPhase);
    }

    @Override
    public void executeDfsPhase() {
        dfsPhase.execute(this);
    }

    @Override
    public void executeQueryPhase() {
        QueryPhase.execute(this);
    }

    @Override
    public void executeFetchPhase(int[] docIdsToLoad) {
        fetchPhase.execute(this, docIdsToLoad);
    }

    @Override
    public void setTask(SearchShardTask task) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SearchShardTask getTask() {
        return parentSearchContext.getTask();
    }

    @Override
    public boolean isCancelled() {
        return parentSearchContext.isCancelled();
    }

    @Override
    public void preProcess() {
        if (query == null) {
            parsedQuery(ParsedQuery.parsedMatchAllQuery());
        }
        float indexBoost = request().indexBoost();
        if (indexBoost != AbstractQueryBuilder.DEFAULT_BOOST) {
            parsedQuery(new ParsedQuery(new BoostQuery(query, indexBoost), parsedQuery()));
        }
        this.query = buildFilteredQuery(query);
    }

    @Override
    public Query buildFilteredQuery(Query query) {
        return parentSearchContext.buildFilteredQuery(query);
    }

    @Override
    public ShardSearchContextId id() {
        return parentSearchContext.id();
    }

    @Override
    public String source() {
        return parentSearchContext.source();
    }

    @Override
    public ShardSearchRequest request() {
        return parentSearchContext.request();
    }

    @Override
    public SearchType searchType() {
        return parentSearchContext.searchType();
    }

    @Override
    public SearchShardTarget shardTarget() {
        return parentSearchContext.shardTarget();
    }

    @Override
    public int numberOfShards() {
        return parentSearchContext.numberOfShards();
    }

    @Override
    public ScrollContext scrollContext() {
        return parentSearchContext.scrollContext();
    }

    @Override
    public SearchContextAggregations aggregations() {
        return parentSearchContext instanceof DefaultSearchContext ? parentSearchContext.aggregations() : null;
    }

    @Override
    public SearchContext aggregations(SearchContextAggregations aggregations) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addSearchExt(SearchExtBuilder searchExtBuilder) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SearchExtBuilder getSearchExt(String name) {
        return parentSearchContext.getSearchExt(name);
    }

    @Override
    public SearchHighlightContext highlight() {
        return parentSearchContext instanceof DefaultSearchContext ? parentSearchContext.highlight() : null;
    }

    @Override
    public void highlight(SearchHighlightContext highlight) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SuggestionSearchContext suggest() {
        return parentSearchContext instanceof DefaultSearchContext ? parentSearchContext.suggest() : null;
    }

    @Override
    public void suggest(SuggestionSearchContext suggest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RankShardContext rankShardContext() {
        return parentSearchContext instanceof DefaultSearchContext ? parentSearchContext.rankShardContext() : null;
    }

    @Override
    public void rankShardContext(RankShardContext rankShardContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<RescoreContext> rescore() {
        if (rescore == null) {
            return List.of();
        }
        return rescore;
    }

    @Override
    public void addRescore(RescoreContext rescore) {
        if (this.rescore == null) {
            this.rescore = new ArrayList<>();
        }
        this.rescore.add(rescore);
    }

    @Override
    public boolean hasScriptFields() {
        return parentSearchContext.hasScriptFields();
    }

    @Override
    public ScriptFieldsContext scriptFields() {
        return parentSearchContext.scriptFields();
    }

    @Override
    public boolean sourceRequested() {
        return parentSearchContext.sourceRequested();
    }

    @Override
    public FetchSourceContext fetchSourceContext() {
        return parentSearchContext.fetchSourceContext();
    }

    @Override
    public SearchContext fetchSourceContext(FetchSourceContext fetchSourceContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FetchDocValuesContext docValuesContext() {
        return parentSearchContext.docValuesContext();
    }

    @Override
    public SearchContext docValuesContext(FetchDocValuesContext docValuesContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FetchFieldsContext fetchFieldsContext() {
        return parentSearchContext.fetchFieldsContext();
    }

    @Override
    public SearchContext fetchFieldsContext(FetchFieldsContext fetchFieldsContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ContextIndexSearcher searcher() {
        return parentSearchContext.searcher();
    }

    @Override
    public IndexShard indexShard() {
        return parentSearchContext.indexShard();
    }

    @Override
    public BitsetFilterCache bitsetFilterCache() {
        return parentSearchContext.bitsetFilterCache();
    }

    @Override
    public TimeValue timeout() {
        return parentSearchContext.timeout();
    }

    @Override
    public void timeout(TimeValue timeout) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int terminateAfter() {
        return this.terminateAfter;
    }

    @Override
    public void terminateAfter(int terminateAfter) {
        this.terminateAfter = terminateAfter;
    }

    @Override
    public boolean lowLevelCancellation() {
        return parentSearchContext.lowLevelCancellation();
    }

    @Override
    public SearchContext minimumScore(float minimumScore) {
        this.minimumScore = minimumScore;
        return this;
    }

    @Override
    public Float minimumScore() {
        return this.minimumScore;
    }

    @Override
    public SearchContext sort(SortAndFormats sort) {
        this.sort = sort;
        return this;
    }

    @Override
    public SortAndFormats sort() {
        return this.sort;
    }

    @Override
    public SearchContext trackScores(boolean trackScores) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean trackScores() {
        return parentSearchContext instanceof DefaultSearchContext && parentSearchContext.trackScores();
    }

    @Override
    public SearchContext trackTotalHitsUpTo(int trackTotalHits) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int trackTotalHitsUpTo() {
        return parentSearchContext instanceof DefaultSearchContext ? parentSearchContext.trackTotalHitsUpTo() : TRACK_TOTAL_HITS_DISABLED;
    }

    @Override
    public SearchContext searchAfter(FieldDoc searchAfter) {
        this.searchAfter = searchAfter;
        return this;
    }

    @Override
    public FieldDoc searchAfter() {
        return this.searchAfter;
    }

    @Override
    public SearchContext collapse(CollapseContext collapse) {
        this.collapse = collapse;
        return this;
    }

    @Override
    public CollapseContext collapse() {
        return collapse;
    }

    @Override
    public SearchContext parsedPostFilter(ParsedQuery postFilter) {
        this.postFilter = postFilter;
        return this;
    }

    @Override
    public ParsedQuery parsedPostFilter() {
        return this.postFilter;
    }

    @Override
    public SearchContext parsedQuery(ParsedQuery parsedQuery) {
        this.parsedQuery = parsedQuery;
        this.query = parsedQuery.query();
        return this;
    }

    @Override
    public ParsedQuery parsedQuery() {
        return this.parsedQuery;
    }

    @Override
    public Query query() {
        return this.query;
    }

    @Override
    public int from() {
        return parentSearchContext.from();
    }

    @Override
    public SearchContext from(int from) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return parentSearchContext.size();
    }

    @Override
    public SearchContext size(int size) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasStoredFields() {
        return parentSearchContext.hasStoredFields();
    }

    @Override
    public StoredFieldsContext storedFieldsContext() {
        return parentSearchContext.storedFieldsContext();
    }

    @Override
    public SearchContext storedFieldsContext(StoredFieldsContext storedFieldsContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean explain() {
        return parentSearchContext.explain();
    }

    @Override
    public void explain(boolean explain) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> groupStats() {
        return parentSearchContext.groupStats();
    }

    @Override
    public void groupStats(List<String> groupStats) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean version() {
        return parentSearchContext.version();
    }

    @Override
    public void version(boolean version) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean seqNoAndPrimaryTerm() {
        return parentSearchContext.seqNoAndPrimaryTerm();
    }

    @Override
    public void seqNoAndPrimaryTerm(boolean seqNoAndPrimaryTerm) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DfsSearchResult dfsResult() {
        return dfsResult;
    }

    @Override
    public void addDfsResult() {
        this.dfsResult = new DfsSearchResult(readerContext().id(), shardTarget(), request());
    }

    @Override
    public QuerySearchResult queryResult() {
        return queryResult;
    }

    @Override
    public void addQueryResult() {
        this.queryResult = new QuerySearchResult(readerContext().id(), shardTarget(), request());
        addReleasable(queryResult::decRef);
    }

    @Override
    public TotalHits getTotalHits() {
        if (queryResult != null) {
            return queryResult.getTotalHits();
        }
        return null;
    }

    @Override
    public float getMaxScore() {
        if (queryResult != null) {
            return queryResult.getMaxScore();
        }
        return Float.NaN;
    }

    @Override
    public FetchPhase fetchPhase() {
        return fetchPhase;
    }

    @Override
    public FetchSearchResult fetchResult() {
        return fetchResult;
    }

    @Override
    public void addFetchResult() {
        this.fetchResult = new FetchSearchResult(readerContext().id(), shardTarget());
        addReleasable(fetchResult::decRef);
    }

    @Override
    public Profilers getProfilers() {
        return parentSearchContext.getProfilers();
    }

    @Override
    public long getRelativeTimeInMillis() {
        return parentSearchContext.getRelativeTimeInMillis();
    }

    @Override
    public SearchExecutionContext getSearchExecutionContext() {
        return parentSearchContext.getSearchExecutionContext();
    }

    @Override
    public ReaderContext readerContext() {
        return parentSearchContext.readerContext();
    }

    @Override
    public SourceLoader newSourceLoader() {
        return parentSearchContext.newSourceLoader();
    }

    @Override
    public IdLoader newIdLoader() {
        return parentSearchContext.newIdLoader();
    }
}
