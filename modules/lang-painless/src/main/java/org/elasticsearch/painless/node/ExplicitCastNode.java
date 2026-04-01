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
 * Source-level explicit cast expression {@code (Type) expr}. Produced by the Walker.
 * The SemanticAnalysisPhase resolves {@code canonicalTypeName} to a {@link CastNode}.
 */
public final class ExplicitCastNode extends ExpressionNode {

    private final String canonicalTypeName;
    private final ExpressionNode childNode;

    public ExplicitCastNode(Location location, String canonicalTypeName, ExpressionNode childNode,
                            Class<?> expressionType) {
        super(location, expressionType);
        this.canonicalTypeName = canonicalTypeName;
        this.childNode = childNode;
    }

    public String getCanonicalTypeName() { return canonicalTypeName; }
    public ExpressionNode getChildNode() { return childNode; }

    public ExplicitCastNode withChildNode(ExpressionNode childNode) {
        return new ExplicitCastNode(getLocation(), canonicalTypeName, childNode, getExpressionType());
    }

    public ExplicitCastNode withExpressionType(Class<?> expressionType) {
        return new ExplicitCastNode(getLocation(), canonicalTypeName, childNode, expressionType);
    }
}
