/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.painless.node;

import org.elasticsearch.painless.Location;

/**
 * Source-level subscript access ({@code a[b]}). Produced by the Walker; the
 * LoweringPhase resolves it into a load ({@link LoadBraceNode}, {@link LoadBraceDefNode})
 * or store variant based on context.
 */
public final class BraceAccessNode extends ExpressionNode {

    private final ExpressionNode prefixNode;
    private final ExpressionNode indexNode;

    public BraceAccessNode(Location location, ExpressionNode prefixNode, ExpressionNode indexNode,
                           Class<?> expressionType) {
        super(location, expressionType);
        this.prefixNode = prefixNode;
        this.indexNode = indexNode;
    }

    public ExpressionNode getPrefixNode() { return prefixNode; }
    public ExpressionNode getIndexNode() { return indexNode; }

    public BraceAccessNode withPrefixNode(ExpressionNode prefixNode) {
        return new BraceAccessNode(getLocation(), prefixNode, indexNode, getExpressionType());
    }

    public BraceAccessNode withIndexNode(ExpressionNode indexNode) {
        return new BraceAccessNode(getLocation(), prefixNode, indexNode, getExpressionType());
    }

    public BraceAccessNode withExpressionType(Class<?> expressionType) {
        return new BraceAccessNode(getLocation(), prefixNode, indexNode, expressionType);
    }
}
