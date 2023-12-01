/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */

package org.elasticsearch.search.query;

import org.apache.lucene.search.FieldDoc;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.io.stream.Writeable;
import org.elasticsearch.common.lucene.search.TopDocsAndMaxScore;
import org.elasticsearch.search.DocValueFormat;
import org.elasticsearch.search.profile.SearchProfileDfsPhaseResult;
import org.elasticsearch.search.profile.SearchProfileQueryPhaseResult;

import java.io.IOException;

public class SingleQuerySearchResult implements Writeable {

    private int from;
    private int size;
    private TopDocsAndMaxScore topDocsAndMaxScore;
    private boolean hasScoreDocs;
    private TotalHits totalHits;
    private float maxScore = Float.NaN;
    private DocValueFormat[] sortValueFormats;
    private Boolean terminatedEarly = null;
    private SearchProfileQueryPhaseResult profileShardResults;
    private boolean hasProfileResults;
    private long serviceTimeEWMA = -1;
    private int nodeQueueSize = -1;

    public SingleQuerySearchResult() {
 
    }

    public SingleQuerySearchResult(StreamInput in) throws IOException {

    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {

    }

    public DocValueFormat[] sortValueFormats() {
        return sortValueFormats;
    }

    public void terminatedEarly(Boolean terminatedEarly) {
        this.terminatedEarly = terminatedEarly;
    }

    public Boolean terminatedEarly() {
        return this.terminatedEarly;
    }

    public boolean hasScoreDocs() {
        return hasScoreDocs;
    }

    public TopDocsAndMaxScore topDocs() {
        if (topDocsAndMaxScore == null) {
            throw new IllegalStateException("topDocs already consumed");
        }
        return topDocsAndMaxScore;
    }

    /**
     * Returns <code>true</code> iff the top docs have already been consumed.
     */
    public boolean hasConsumedTopDocs() {
        return topDocsAndMaxScore == null;
    }

    /**
     * Returns and nulls out the top docs for this search results. This allows to free up memory once the top docs are consumed.
     * @throws IllegalStateException if the top docs have already been consumed.
     */
    public TopDocsAndMaxScore consumeTopDocs() {
        TopDocsAndMaxScore topDocsAndMaxScore = this.topDocsAndMaxScore;
        if (topDocsAndMaxScore == null) {
            throw new IllegalStateException("topDocs already consumed");
        }
        this.topDocsAndMaxScore = null;
        return topDocsAndMaxScore;
    }

    public void topDocs(TopDocsAndMaxScore topDocs, DocValueFormat[] sortValueFormats) {
        setTopDocs(topDocs);
        if (topDocs.topDocs.scoreDocs.length > 0 && topDocs.topDocs.scoreDocs[0] instanceof FieldDoc) {
            int numFields = ((FieldDoc) topDocs.topDocs.scoreDocs[0]).fields.length;
            if (numFields != sortValueFormats.length) {
                throw new IllegalArgumentException(
                    "The number of sort fields does not match: " + numFields + " != " + sortValueFormats.length
                );
            }
        }
        this.sortValueFormats = sortValueFormats;
    }

    private void setTopDocs(TopDocsAndMaxScore topDocsAndMaxScore) {
        this.topDocsAndMaxScore = topDocsAndMaxScore;
        this.totalHits = topDocsAndMaxScore.topDocs.totalHits;
        this.maxScore = topDocsAndMaxScore.maxScore;
        this.hasScoreDocs = topDocsAndMaxScore.topDocs.scoreDocs.length > 0;
    }

    public void setSearchProfileDfsPhaseResult(SearchProfileDfsPhaseResult searchProfileDfsPhaseResult) {
        if (profileShardResults == null) {
            return;
        }
        profileShardResults.setSearchProfileDfsPhaseResult(searchProfileDfsPhaseResult);
    }

    /**
     * Returns and nulls out the profiled results for this search, or potentially null if result was empty.
     * This allows to free up memory once the profiled result is consumed.
     * @throws IllegalStateException if the profiled result has already been consumed.
     */
    public SearchProfileQueryPhaseResult consumeProfileResult() {
        if (profileShardResults == null) {
            throw new IllegalStateException("profile results already consumed");
        }
        SearchProfileQueryPhaseResult result = profileShardResults;
        profileShardResults = null;
        return result;
    }

    public boolean hasProfileResults() {
        return hasProfileResults;
    }

    /**
     * Sets the finalized profiling results for this query
     * @param shardResults The finalized profile
     */
    public void profileResults(SearchProfileQueryPhaseResult shardResults) {
        this.profileShardResults = shardResults;
        hasProfileResults = shardResults != null;
    }

    public int from() {
        return from;
    }

    public void from(int from) {
        this.from = from;
    }

    /**
     * Returns the maximum size of this results top docs.
     */
    public int size() {
        return size;
    }

    public void size(int size) {
        this.size = size;
    }

    public long serviceTimeEWMA() {
        return this.serviceTimeEWMA;
    }

    public void serviceTimeEWMA(long serviceTimeEWMA) {
        this.serviceTimeEWMA = serviceTimeEWMA;
    }

    public int nodeQueueSize() {
        return this.nodeQueueSize;
    }

    public void nodeQueueSize(int nodeQueueSize) {
        this.nodeQueueSize = nodeQueueSize;
    }

    public TotalHits getTotalHits() {
        return totalHits;
    }

    public float getMaxScore() {
        return maxScore;
    }
}
