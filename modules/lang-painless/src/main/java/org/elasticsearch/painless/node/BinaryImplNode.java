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

public final class BinaryImplNode extends BinaryExpressionNode {

    private final Operation operation;

    public BinaryImplNode(Location location, ExpressionNode leftNode, ExpressionNode rightNode,
                          Operation operation, Class<?> expressionType) {
        super(location, leftNode, rightNode, expressionType);
        this.operation = operation;
    }

    public Operation getOperation() { return operation; }

    public BinaryImplNode withLeftNode(ExpressionNode leftNode) {
        return new BinaryImplNode(getLocation(), leftNode, getRightNode(), operation, getExpressionType());
    }

    public BinaryImplNode withRightNode(ExpressionNode rightNode) {
        return new BinaryImplNode(getLocation(), getLeftNode(), rightNode, operation, getExpressionType());
    }

    public BinaryImplNode withExpressionType(Class<?> expressionType) {
        return new BinaryImplNode(getLocation(), getLeftNode(), getRightNode(), operation, expressionType);
    }
}
