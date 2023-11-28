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

public abstract class ShardRetriever extends SearchContext {

    protected final SearchContext parentSearchContext;
    protected final DfsPhase dfsPhase;
    protected final FetchPhase fetchPhase;

    public ShardRetriever(SearchContext parentSearchContext, DfsPhase dfsPhase, FetchPhase fetchPhase) {
        this.parentSearchContext = parentSearchContext;
        this.dfsPhase = dfsPhase;
        this.fetchPhase = fetchPhase;
    }

    public abstract void executeDfsPhase();

    public abstract void executeQueryPhase();

    public abstract void executeFetchPhase(int[] docIdsToLoad);
}
