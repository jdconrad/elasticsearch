/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */

package org.elasticsearch.search.rank;

import org.apache.lucene.search.Query;
import org.elasticsearch.TransportVersion;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.index.query.AbstractQueryBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryRewriteContext;
import org.elasticsearch.index.query.SearchExecutionContext;
import org.elasticsearch.xcontent.XContentBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class RRFRankQueryBuilder extends AbstractQueryBuilder<RRFRankQueryBuilder> {

    public static final String NAME = "rerank";

    private final List<QueryBuilder> queryBuilders;
    private QueryBuilder compoundQueryBuilder;

    public RRFRankQueryBuilder() {
        queryBuilders = new ArrayList<>();
        compoundQueryBuilder = null;
    }

    public RRFRankQueryBuilder(StreamInput in) throws IOException {
        super(in);
        queryBuilders = readQueries(in);
        compoundQueryBuilder = in.readOptionalNamedWriteable(QueryBuilder.class);
    }

    @Override
    protected void doWriteTo(StreamOutput out) throws IOException {
        writeQueries(out, queryBuilders);
        out.writeOptionalNamedWriteable(compoundQueryBuilder);
    }

    @Override
    protected void doXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject(getName());
        builder.array("queries", queryBuilders);
        if (compoundQueryBuilder != null) {
            builder.field("compound", compoundQueryBuilder);
        }
        builder.endObject();
    }

    @Override
    public String getWriteableName() {
        return NAME;
    }

    @Override
    public TransportVersion getMinimalSupportedVersion() {
        return TransportVersion.V_8_7_0;
    }

    @Override
    public String getName() {
        return getWriteableName();
    }

    public RRFRankQueryBuilder addQuery(QueryBuilder queryBuilder) {
        queryBuilders.add(queryBuilder);
        return this;
    }

    @Override
    public QueryBuilder doRewrite(QueryRewriteContext queryRewriteContext) throws IOException {
        RRFRankQueryBuilder rrfRankQueryBuilder = new RRFRankQueryBuilder();
        BoolQueryBuilder compoundQueryBuilder = new BoolQueryBuilder();
        boolean changed = false;
        for (QueryBuilder queryBuilder : queryBuilders) {
            QueryBuilder rewrittenQueryBuilder = queryBuilder.rewrite(queryRewriteContext);
            rrfRankQueryBuilder.addQuery(rewrittenQueryBuilder);
            compoundQueryBuilder.should(rewrittenQueryBuilder);
            changed |= rewrittenQueryBuilder != queryBuilder;
        }
        this.compoundQueryBuilder = compoundQueryBuilder.rewrite(queryRewriteContext);
        if (changed) {
            rrfRankQueryBuilder.compoundQueryBuilder = this.compoundQueryBuilder;
            return rrfRankQueryBuilder;
        }
        return this;
    }

    @Override
    protected Query doToQuery(SearchExecutionContext context) throws IOException {
        return compoundQueryBuilder.toQuery(context);
    }

    public List<QueryBuilder> queryBuilders() {
        return Collections.unmodifiableList(queryBuilders);
    }

    @Override
    public boolean doEquals(RRFRankQueryBuilder o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (super.equals(o) == false) return false;
        return Objects.equals(queryBuilders, o.queryBuilders) && Objects.equals(compoundQueryBuilder, o.compoundQueryBuilder);
    }

    @Override
    public int doHashCode() {
        return Objects.hash(super.hashCode(), queryBuilders, compoundQueryBuilder);
    }
}
