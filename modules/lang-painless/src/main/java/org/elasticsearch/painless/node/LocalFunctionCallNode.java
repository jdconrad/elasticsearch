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
 * Source-level call to a user-defined function in the same script ({@code foo(a, b)}).
 * Produced by the Walker. The SemanticAnalysisPhase resolves this to an
 * {@link InvokeCallMemberNode}.
 */
public final class LocalFunctionCallNode extends ExpressionNode {

    private final String methodName;
    private final List<ExpressionNode> argumentNodes;

    public LocalFunctionCallNode(Location location, String methodName,
                                 List<ExpressionNode> argumentNodes, Class<?> expressionType) {
        super(location, expressionType);
        this.methodName = methodName;
        this.argumentNodes = List.copyOf(argumentNodes);
    }

    public String getMethodName() { return methodName; }
    public List<ExpressionNode> getArgumentNodes() { return argumentNodes; }

    public LocalFunctionCallNode withArgumentNodes(List<ExpressionNode> argumentNodes) {
        return new LocalFunctionCallNode(getLocation(), methodName, argumentNodes, getExpressionType());
    }

    public LocalFunctionCallNode withExpressionType(Class<?> expressionType) {
        return new LocalFunctionCallNode(getLocation(), methodName, argumentNodes, expressionType);
    }
}
