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
 * Source-level function/method reference ({@code Type::method}, {@code this::method},
 * {@code Type::new}). Produced by the Walker for class/local/constructor function refs.
 * The SemanticAnalysisPhase resolves this to a {@link TypedInterfaceReferenceNode} or
 * {@link DefInterfaceReferenceNode}.
 */
public final class FunctionReferenceNode extends ExpressionNode {

    private final String symbol;
    private final String methodName;

    public FunctionReferenceNode(Location location, String symbol, String methodName,
                                 Class<?> expressionType) {
        super(location, expressionType);
        this.symbol = symbol;
        this.methodName = methodName;
    }

    public String getSymbol() { return symbol; }
    public String getMethodName() { return methodName; }

    public FunctionReferenceNode withExpressionType(Class<?> expressionType) {
        return new FunctionReferenceNode(getLocation(), symbol, methodName, expressionType);
    }
}
