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

import java.util.List;

/**
 * Array creation ({@code new Type[dim]} or {@code new Type[] { elems }}).
 *
 * <p>{@code canonicalTypeName} is set by the Walker; {@code expressionType} is null
 * until the SemanticAnalysisPhase resolves the element type.
 */
public final class NewArrayNode extends ArgumentsExpressionNode {

    private final String canonicalTypeName;
    private final boolean initialize;

    public NewArrayNode(Location location, String canonicalTypeName,
                        List<ExpressionNode> argumentNodes, boolean initialize,
                        Class<?> expressionType) {
        super(location, argumentNodes, expressionType);
        this.canonicalTypeName = canonicalTypeName;
        this.initialize = initialize;
    }

    public String getCanonicalTypeName() { return canonicalTypeName; }
    public boolean isInitialize() { return initialize; }

    public NewArrayNode withArgumentNodes(List<ExpressionNode> argumentNodes) {
        return new NewArrayNode(getLocation(), canonicalTypeName, argumentNodes, initialize, getExpressionType());
    }

    public NewArrayNode withExpressionType(Class<?> expressionType) {
        return new NewArrayNode(getLocation(), canonicalTypeName, getArgumentNodes(), initialize, expressionType);
    }
}
