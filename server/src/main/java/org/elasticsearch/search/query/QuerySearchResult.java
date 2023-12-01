/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */

package org.elasticsearch.search.query;

import org.apache.lucene.search.FieldDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.TransportVersions;
import org.elasticsearch.common.io.stream.DelayableWriteable;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.lucene.search.TopDocsAndMaxScore;
import org.elasticsearch.core.AbstractRefCounted;
import org.elasticsearch.core.RefCounted;
import org.elasticsearch.core.Releasable;
import org.elasticsearch.core.Releasables;
import org.elasticsearch.search.DocValueFormat;
import org.elasticsearch.search.RescoreDocIds;
import org.elasticsearch.search.SearchPhaseResult;
import org.elasticsearch.search.SearchShardTarget;
import org.elasticsearch.search.aggregations.InternalAggregation;
import org.elasticsearch.search.aggregations.InternalAggregations;
import org.elasticsearch.search.internal.ShardSearchContextId;
import org.elasticsearch.search.internal.ShardSearchRequest;
import org.elasticsearch.search.profile.SearchProfileDfsPhaseResult;
import org.elasticsearch.search.profile.SearchProfileQueryPhaseResult;
import org.elasticsearch.search.rank.RankShardResult;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.transport.LeakTracker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.elasticsearch.common.lucene.Lucene.readTopDocs;
import static org.elasticsearch.common.lucene.Lucene.writeTopDocs;

public final class QuerySearchResult extends SearchPhaseResult {

    private List<SingleQuerySearchResult> singleQuerySearchResults = new ArrayList<>();
    private RankShardResult rankShardResult;
    /**
     * Aggregation results. We wrap them in
     * {@linkplain DelayableWriteable} because
     * {@link InternalAggregation} is usually made up of many small objects
     * which have a fairly high overhead in the JVM. So we delay deserializing
     * them until just before we need them.
     */
    private DelayableWriteable<InternalAggregations> aggregations;
    private boolean hasAggs;
    private Suggest suggest;
    private boolean searchTimedOut;

    private final boolean isNull;

    private final RefCounted refCounted;

    private final List<Releasable> toRelease;

    public QuerySearchResult() {
        this(false);
    }

    public QuerySearchResult(StreamInput in) throws IOException {
        this(in, false);
    }

    /**
     * Read the object, but using a delayed aggregations field when delayedAggregations=true. Using this, the caller must ensure that
     * either `consumeAggs` or `releaseAggs` is called if `hasAggs() == true`.
     * @param delayedAggregations whether to use delayed aggregations or not
     */
    public QuerySearchResult(StreamInput in, boolean delayedAggregations) throws IOException {
        super(in);
        if (in.getTransportVersion().onOrAfter(TransportVersions.V_7_7_0)) {
            isNull = in.readBoolean();
        } else {
            isNull = false;
        }
        if (isNull == false) {
            ShardSearchContextId id = new ShardSearchContextId(in);
            readFromWithId(id, in, delayedAggregations);
        }
        refCounted = null;
        toRelease = null;
    }

    public QuerySearchResult(ShardSearchContextId contextId, SearchShardTarget shardTarget, ShardSearchRequest shardSearchRequest) {
        this.contextId = contextId;
        setSearchShardTarget(shardTarget);
        isNull = false;
        setShardSearchRequest(shardSearchRequest);
        this.toRelease = new ArrayList<>();
        this.refCounted = LeakTracker.wrap(AbstractRefCounted.of(() -> Releasables.close(toRelease)));
    }

    private QuerySearchResult(boolean isNull) {
        this.isNull = isNull;
        this.refCounted = null;
        toRelease = null;
    }

    public SingleQuerySearchResult addSingleQueryResult() {
        SingleQuerySearchResult singleQuerySearchResult = new SingleQuerySearchResult();
        singleQuerySearchResults.add(singleQuerySearchResult);
        return singleQuerySearchResult;
    }

    /**
     * Returns an instance that contains no response.
     */
    public static QuerySearchResult nullInstance() {
        return new QuerySearchResult(true);
    }

    /**
     * Returns true if the result doesn't contain any useful information.
     * It is used by the search action to avoid creating an empty response on
     * shard request that rewrites to match_no_docs.
     *
     * TODO: Currently we need the concrete aggregators to build empty responses. This means that we cannot
     *       build an empty response in the coordinating node so we rely on this hack to ensure that at least one shard
     *       returns a valid empty response. We should move the ability to create empty responses to aggregation builders
     *       in order to allow building empty responses directly from the coordinating node.
     */
    public boolean isNull() {
        return isNull;
    }

    @Override
    public QuerySearchResult queryResult() {
        return this;
    }

    public void searchTimedOut(boolean searchTimedOut) {
        this.searchTimedOut = searchTimedOut;
    }

    public boolean searchTimedOut() {
        return searchTimedOut;
    }

    public void setRankShardResult(RankShardResult rankShardResult) {
        this.rankShardResult = rankShardResult;
    }

    public RankShardResult getRankShardResult() {
        return rankShardResult;
    }

    /**
     * Returns <code>true</code> if this query result has unconsumed aggregations
     */
    public boolean hasAggs() {
        return hasAggs;
    }

    /**
     * Returns and nulls out the aggregation for this search results. This allows to free up memory once the aggregation is consumed.
     * @throws IllegalStateException if the aggregations have already been consumed.
     */
    public InternalAggregations consumeAggs() {
        if (aggregations == null) {
            throw new IllegalStateException("aggs already consumed");
        }
        try {
            return aggregations.expand();
        } finally {
            aggregations.close();
            aggregations = null;
        }
    }

    public void releaseAggs() {
        if (aggregations != null) {
            aggregations.close();
            aggregations = null;
        }
    }

    public void addReleasable(Releasable releasable) {
        toRelease.add(releasable);
    }

    public void aggregations(InternalAggregations aggregations) {
        assert this.aggregations == null : "aggregations already set to [" + this.aggregations + "]";
        this.aggregations = aggregations == null ? null : DelayableWriteable.referencing(aggregations);
        hasAggs = aggregations != null;
    }

    public DelayableWriteable<InternalAggregations> aggregations() {
        return aggregations;
    }

    public void consumeAll() {
        for (SingleQuerySearchResult singleQuerySearchResult : singleQuerySearchResults) {
            if (singleQuerySearchResult.hasProfileResults()) {
                singleQuerySearchResult.consumeProfileResult();
            }
            if (singleQuerySearchResult.hasConsumedTopDocs() == false) {
                singleQuerySearchResult.consumeTopDocs();
            }
        }
        releaseAggs();
    }



    public Suggest suggest() {
        return suggest;
    }

    public void suggest(Suggest suggest) {
        this.suggest = suggest;
    }

    /**
     * Returns <code>true</code> if this result has any suggest score docs
     */
    public boolean hasSuggestHits() {
        return (suggest != null && suggest.hasScoreDocs());
    }

    public boolean hasSearchContext() {
        boolean hasSearchContext = false;
        for (SingleQuerySearchResult singleQuerySearchResult : singleQuerySearchResults) {
            hasSearchContext |= singleQuerySearchResult.hasScoreDocs();
        }
        return hasSearchContext || hasSuggestHits() || rankShardResult != null;
    }

    public void readFromWithId(ShardSearchContextId id, StreamInput in) throws IOException {
        readFromWithId(id, in, false);
    }

    private void readFromWithId(ShardSearchContextId id, StreamInput in, boolean delayedAggregations) throws IOException {
        this.contextId = id;
        if (in.getTransportVersion().onOrAfter(TransportVersions.MULTI_QUERY_RESULTS_ADDED)) {
            singleQuerySearchResults = in.readCollectionAsList(SingleQuerySearchResult::new);
        } else {
            SingleQuerySearchResult singleQuerySearchResult = this.addSingleQueryResult();
            singleQuerySearchResult.from(in.readVInt());
            singleQuerySearchResult.size(in.readVInt());
            int numSortFieldsPlus1 = in.readVInt();
            DocValueFormat[] sortValueFormats;
            if (numSortFieldsPlus1 == 0) {
                sortValueFormats = null;
            } else {
                sortValueFormats = new DocValueFormat[numSortFieldsPlus1 - 1];
                for (int i = 0; i < sortValueFormats.length; ++i) {
                    sortValueFormats[i] = in.readNamedWriteable(DocValueFormat.class);
                }
            }
            TopDocsAndMaxScore topDocs = readTopDocs(in);
            singleQuerySearchResult.topDocs(topDocs, sortValueFormats);
        }
        hasAggs = in.readBoolean();
        boolean success = false;
        try {
            if (hasAggs) {
                if (delayedAggregations) {
                    aggregations = DelayableWriteable.delayed(InternalAggregations::readFrom, in);
                } else {
                    aggregations = DelayableWriteable.referencing(InternalAggregations::readFrom, in);
                }
            }
            if (in.readBoolean()) {
                suggest = new Suggest(in);
            }
            searchTimedOut = in.readBoolean();
            if (in.getTransportVersion().before(TransportVersions.MULTI_QUERY_RESULTS_ADDED)) {
                SingleQuerySearchResult singleQuerySearchResult = singleQuerySearchResults.get(0);
                singleQuerySearchResult.terminatedEarly(in.readOptionalBoolean());
                singleQuerySearchResult.profileResults(in.readOptionalWriteable(SearchProfileQueryPhaseResult::new));
                singleQuerySearchResult.serviceTimeEWMA(in.readZLong());
                singleQuerySearchResult.nodeQueueSize(in.readInt());
            }
            if (in.getTransportVersion().onOrAfter(TransportVersions.V_7_10_0)) {
                setShardSearchRequest(in.readOptionalWriteable(ShardSearchRequest::new));
                setRescoreDocIds(new RescoreDocIds(in));
            }
            if (in.getTransportVersion().onOrAfter(TransportVersions.V_8_8_0)) {
                rankShardResult = in.readOptionalNamedWriteable(RankShardResult.class);
            }
            success = true;
        } finally {
            if (success == false) {
                // in case we were not able to deserialize the full message we must release the aggregation buffer
                Releasables.close(aggregations);
            }
        }
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        // we do not know that it is being sent over transport, but this at least protects all writes from happening, including sending.
        if (aggregations != null && aggregations.isSerialized()) {
            throw new IllegalStateException("cannot send serialized version since it will leak");
        }
        if (out.getTransportVersion().onOrAfter(TransportVersions.V_7_7_0)) {
            out.writeBoolean(isNull);
        }
        if (isNull == false) {
            contextId.writeTo(out);
            writeToNoId(out);
        }
    }

    public void writeToNoId(StreamOutput out) throws IOException {
        if (out.getTransportVersion().onOrAfter(TransportVersions.MULTI_QUERY_RESULTS_ADDED)) {
            out.writeCollection(singleQuerySearchResults);
        } else {
            if (singleQuerySearchResults.size() > 1) {
                throw new IllegalArgumentException("cannot write multiple queries to version [" + out.getTransportVersion() + "]");
            }
            SingleQuerySearchResult singleQuerySearchResult = singleQuerySearchResults.get(0);
            out.writeVInt(singleQuerySearchResult.from());
            out.writeVInt(singleQuerySearchResult.size());

            if (singleQuerySearchResult.sortValueFormats() == null) {
                out.writeVInt(0);
            } else {
                out.writeVInt(1 + singleQuerySearchResult.sortValueFormats().length);
                for (int i = 0; i < singleQuerySearchResult.sortValueFormats().length; ++i) {
                    out.writeNamedWriteable(singleQuerySearchResult.sortValueFormats()[i]);
                }
            }
            writeTopDocs(out, singleQuerySearchResult.topDocs());
        }
        out.writeOptionalWriteable(aggregations);
        if (suggest == null) {
            out.writeBoolean(false);
        } else {
            out.writeBoolean(true);
            suggest.writeTo(out);
        }
        out.writeBoolean(searchTimedOut);
        if (out.getTransportVersion().before(TransportVersions.MULTI_QUERY_RESULTS_ADDED)) {
            SingleQuerySearchResult singleQuerySearchResult = singleQuerySearchResults.get(0);
            out.writeOptionalBoolean(singleQuerySearchResult.terminatedEarly());
            out.writeOptionalWriteable(singleQuerySearchResult.consumeProfileResult());
            out.writeZLong(singleQuerySearchResult.serviceTimeEWMA());
            out.writeInt(singleQuerySearchResult.nodeQueueSize());
        }
        if (out.getTransportVersion().onOrAfter(TransportVersions.V_7_10_0)) {
            out.writeOptionalWriteable(getShardSearchRequest());
            getRescoreDocIds().writeTo(out);
        }
        if (out.getTransportVersion().onOrAfter(TransportVersions.V_8_8_0)) {
            out.writeOptionalNamedWriteable(rankShardResult);
        } else if (rankShardResult != null) {
            throw new IllegalArgumentException("cannot serialize [rank] to version [" + out.getTransportVersion() + "]");
        }
    }

    @Override
    public void incRef() {
        if (refCounted != null) {
            refCounted.incRef();
        } else {
            super.incRef();
        }
    }

    @Override
    public boolean tryIncRef() {
        if (refCounted != null) {
            return refCounted.tryIncRef();
        }
        return super.tryIncRef();
    }

    @Override
    public boolean decRef() {
        if (refCounted != null) {
            return refCounted.decRef();
        }
        return super.decRef();
    }

    @Override
    public boolean hasReferences() {
        if (refCounted != null) {
            return refCounted.hasReferences();
        }
        return super.hasReferences();
    }
}
