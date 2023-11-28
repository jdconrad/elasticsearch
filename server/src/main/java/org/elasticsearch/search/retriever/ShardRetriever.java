/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */

package org.elasticsearch.search.retriever;

import org.elasticsearch.search.dfs.DfsPhase;
import org.elasticsearch.search.fetch.FetchPhase;
import org.elasticsearch.search.internal.SearchContext;

public abstract class ShardRetriever {

    protected final SearchContext searchContext;
    protected final DfsPhase dfsPhase;
    protected final FetchPhase fetchPhase;

    public ShardRetriever(SearchContext searchContext, DfsPhase dfsPhase, FetchPhase fetchPhase) {
        this.searchContext = searchContext;
        this.dfsPhase = dfsPhase;
        this.fetchPhase = fetchPhase;
    }

    public SearchContext getSearchContext() {
        return searchContext;
    }

    public abstract void executeDfsPhase();

    public abstract void executeQueryPhase();

    public abstract void executeFetchPhase(int[] docIdsToLoad);
}
