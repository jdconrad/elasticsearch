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

public final class ConditionalNode extends ExpressionNode {

    private final ExpressionNode conditionNode;
    private final ExpressionNode leftNode;
    private final ExpressionNode rightNode;

    public ConditionalNode(Location location, ExpressionNode conditionNode, ExpressionNode leftNode,
                           ExpressionNode rightNode, Class<?> expressionType) {
        super(location, expressionType);
        this.conditionNode = conditionNode;
        this.leftNode = leftNode;
        this.rightNode = rightNode;
    }

    public ExpressionNode getConditionNode() { return conditionNode; }
    public ExpressionNode getLeftNode() { return leftNode; }
    public ExpressionNode getRightNode() { return rightNode; }

    public ConditionalNode withConditionNode(ExpressionNode conditionNode) {
        return new ConditionalNode(getLocation(), conditionNode, leftNode, rightNode, getExpressionType());
    }

    public ConditionalNode withLeftNode(ExpressionNode leftNode) {
        return new ConditionalNode(getLocation(), conditionNode, leftNode, rightNode, getExpressionType());
    }

    public ConditionalNode withRightNode(ExpressionNode rightNode) {
        return new ConditionalNode(getLocation(), conditionNode, leftNode, rightNode, getExpressionType());
    }

    public ConditionalNode withExpressionType(Class<?> expressionType) {
        return new ConditionalNode(getLocation(), conditionNode, leftNode, rightNode, expressionType);
    }
}
