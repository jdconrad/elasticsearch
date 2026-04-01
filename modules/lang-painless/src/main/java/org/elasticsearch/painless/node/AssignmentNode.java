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
import org.elasticsearch.painless.Operation;

/**
 * Source-level assignment expression (simple or compound). Produced by the Walker for
 * {@code =}, {@code +=}, {@code -=}, etc., and for prefix/postfix {@code ++}/{@code --}.
 * The LoweringPhase resolves the left-hand side into the appropriate store node.
 *
 * <p>{@code operation} is null for simple assignment ({@code =}); non-null for
 * compound assignment ({@code +=}, {@code *=}, ...). {@code postIfRead} is true
 * for postfix {@code ++}/{@code --}.
 */
public final class AssignmentNode extends ExpressionNode {

    private final ExpressionNode leftNode;
    private final ExpressionNode rightNode;
    private final boolean postIfRead;
    private final Operation operation;

    public AssignmentNode(Location location, ExpressionNode leftNode, ExpressionNode rightNode,
                          boolean postIfRead, Operation operation, Class<?> expressionType) {
        super(location, expressionType);
        this.leftNode = leftNode;
        this.rightNode = rightNode;
        this.postIfRead = postIfRead;
        this.operation = operation;
    }

    public ExpressionNode getLeftNode() { return leftNode; }
    public ExpressionNode getRightNode() { return rightNode; }
    public boolean isPostIfRead() { return postIfRead; }
    public Operation getOperation() { return operation; }

    public AssignmentNode withLeftNode(ExpressionNode leftNode) {
        return new AssignmentNode(getLocation(), leftNode, rightNode, postIfRead, operation, getExpressionType());
    }

    public AssignmentNode withRightNode(ExpressionNode rightNode) {
        return new AssignmentNode(getLocation(), leftNode, rightNode, postIfRead, operation, getExpressionType());
    }

    public AssignmentNode withExpressionType(Class<?> expressionType) {
        return new AssignmentNode(getLocation(), leftNode, rightNode, postIfRead, operation, expressionType);
    }
}
