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
 * Source-level method invocation on a receiver ({@code a.foo(b, c)} or
 * {@code a?.foo(b, c)}). Produced by the Walker. The SemanticAnalysisPhase resolves
 * this to an {@link InvokeCallNode} or {@link InvokeCallDefNode}.
 */
public final class MethodCallNode extends ExpressionNode {

    private final ExpressionNode prefixNode;
    private final String methodName;
    private final List<ExpressionNode> argumentNodes;
    private final boolean isNullSafe;

    public MethodCallNode(Location location, ExpressionNode prefixNode, String methodName,
                          List<ExpressionNode> argumentNodes, boolean isNullSafe,
                          Class<?> expressionType) {
        super(location, expressionType);
        this.prefixNode = prefixNode;
        this.methodName = methodName;
        this.argumentNodes = List.copyOf(argumentNodes);
        this.isNullSafe = isNullSafe;
    }

    public ExpressionNode getPrefixNode() { return prefixNode; }
    public String getMethodName() { return methodName; }
    public List<ExpressionNode> getArgumentNodes() { return argumentNodes; }
    public boolean isNullSafe() { return isNullSafe; }

    public MethodCallNode withPrefixNode(ExpressionNode prefixNode) {
        return new MethodCallNode(getLocation(), prefixNode, methodName, argumentNodes, isNullSafe, getExpressionType());
    }

    public MethodCallNode withArgumentNodes(List<ExpressionNode> argumentNodes) {
        return new MethodCallNode(getLocation(), prefixNode, methodName, argumentNodes, isNullSafe, getExpressionType());
    }

    public MethodCallNode withExpressionType(Class<?> expressionType) {
        return new MethodCallNode(getLocation(), prefixNode, methodName, argumentNodes, isNullSafe, expressionType);
    }
}
