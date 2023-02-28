/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */

package org.elasticsearch.search.rank;

import org.apache.lucene.search.ScoreDoc;
import org.elasticsearch.action.search.SearchPhaseController.SortedTopDocs;
import org.elasticsearch.action.search.SearchPhaseController.TopDocsStats;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.query.QuerySearchResult;

import java.util.List;

/**
 * {@code RankContext} is a base class used to generate ranking
 * results on the coordinator and then set the rank for any
 * search hits that are found.
 */
public abstract class RankContext {

    protected final List<QueryBuilder> queryBuilders;
    protected final int size;
    protected final int from;

    public RankContext(List<QueryBuilder> queryBuilders, int size, int from) {
        this.queryBuilders = queryBuilders;
        this.size = size;
        this.from = from;
    }

    /**
     * This is used to pull information passed back from the shards as part
     * of {@link QuerySearchResult#getRankShardResult()} and return a {@link SortedTopDocs}
     * of the final rank results. Note that {@link TopDocsStats} is included so that
     * appropriate stats may be updated based on rank results. This is called at the end
     * of the query phase prior to the fetch phase.
     */
    public abstract SortedTopDocs rank(List<QuerySearchResult> querySearchResults, TopDocsStats topDocStats);

    /**
     * This is used to update the rank field of a {@link SearchHit} when the search hits
     * are generated. Called once per {@link SearchHit}.
     */
    public abstract void decorateSearchHit(ScoreDoc scoreDoc, SearchHit searchHit);
}
