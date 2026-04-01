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

public final class ComparisonNode extends BinaryExpressionNode {

    private final Operation operation;
    private final Class<?> comparisonType;

    public ComparisonNode(Location location, ExpressionNode leftNode, ExpressionNode rightNode,
                          Operation operation, Class<?> comparisonType, Class<?> expressionType) {
        super(location, leftNode, rightNode, expressionType);
        this.operation = operation;
        this.comparisonType = comparisonType;
    }

    public Operation getOperation() { return operation; }
    public Class<?> getComparisonType() { return comparisonType; }

    public ComparisonNode withLeftNode(ExpressionNode leftNode) {
        return new ComparisonNode(getLocation(), leftNode, getRightNode(), operation, comparisonType, getExpressionType());
    }

    public ComparisonNode withRightNode(ExpressionNode rightNode) {
        return new ComparisonNode(getLocation(), getLeftNode(), rightNode, operation, comparisonType, getExpressionType());
    }

    public ComparisonNode withExpressionType(Class<?> expressionType) {
        return new ComparisonNode(getLocation(), getLeftNode(), getRightNode(), operation, comparisonType, expressionType);
    }

    public ComparisonNode withComparisonType(Class<?> comparisonType) {
        return new ComparisonNode(getLocation(), getLeftNode(), getRightNode(), operation, comparisonType, getExpressionType());
    }
}
