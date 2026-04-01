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
 * Source-level {@code instanceof} expression. Produced by the Walker.
 * The SemanticAnalysisPhase resolves {@code canonicalTypeName} and lowers this to an
 * {@link InstanceofNode} with the resolved {@code instanceType}.
 *
 * <p>Unlike Java's {@code instanceof}, Painless's works for primitive types too.
 */
public final class SourceInstanceofNode extends ExpressionNode {

    private final ExpressionNode expressionNode;
    private final String canonicalTypeName;

    public SourceInstanceofNode(Location location, ExpressionNode expressionNode,
                                String canonicalTypeName, Class<?> expressionType) {
        super(location, expressionType);
        this.expressionNode = expressionNode;
        this.canonicalTypeName = canonicalTypeName;
    }

    public ExpressionNode getExpressionNode() { return expressionNode; }
    public String getCanonicalTypeName() { return canonicalTypeName; }

    public SourceInstanceofNode withExpressionNode(ExpressionNode expressionNode) {
        return new SourceInstanceofNode(getLocation(), expressionNode, canonicalTypeName, getExpressionType());
    }

    public SourceInstanceofNode withExpressionType(Class<?> expressionType) {
        return new SourceInstanceofNode(getLocation(), expressionNode, canonicalTypeName, expressionType);
    }
}
