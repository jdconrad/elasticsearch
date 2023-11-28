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
import org.elasticsearch.search.query.QueryPhase;

public class ClassicShardRetriever extends ShardRetriever {

    public ClassicShardRetriever(SearchContext searchContext, DfsPhase dfsPhase, FetchPhase fetchPhase) {
        super(searchContext, dfsPhase, fetchPhase);
    }

    @Override
    public void executeDfsPhase() {
        dfsPhase.execute(searchContext);
    }

    @Override
    public void executeQueryPhase() {
        QueryPhase.execute(searchContext);
    }

    @Override
    public void executeFetchPhase(int[] docIdsToLoad) {
        fetchPhase.execute(searchContext, docIdsToLoad);
    }
}
