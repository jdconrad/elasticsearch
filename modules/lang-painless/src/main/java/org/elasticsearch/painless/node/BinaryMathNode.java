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

public final class BinaryMathNode extends BinaryExpressionNode {

    private final Operation operation;
    private final Class<?> binaryType;
    private final Class<?> shiftType;
    private final boolean isOriginallyDefType;

    public BinaryMathNode(Location location, ExpressionNode leftNode, ExpressionNode rightNode,
                          Operation operation, Class<?> binaryType, Class<?> shiftType,
                          boolean isOriginallyDefType, Class<?> expressionType) {
        super(location, leftNode, rightNode, expressionType);
        this.operation = operation;
        this.binaryType = binaryType;
        this.shiftType = shiftType;
        this.isOriginallyDefType = isOriginallyDefType;
    }

    public Operation getOperation() { return operation; }
    public Class<?> getBinaryType() { return binaryType; }
    public Class<?> getShiftType() { return shiftType; }
    public boolean isOriginallyDefType() { return isOriginallyDefType; }

    public BinaryMathNode withLeftNode(ExpressionNode leftNode) {
        return new BinaryMathNode(getLocation(), leftNode, getRightNode(), operation,
            binaryType, shiftType, isOriginallyDefType, getExpressionType());
    }

    public BinaryMathNode withRightNode(ExpressionNode rightNode) {
        return new BinaryMathNode(getLocation(), getLeftNode(), rightNode, operation,
            binaryType, shiftType, isOriginallyDefType, getExpressionType());
    }

    public BinaryMathNode withExpressionType(Class<?> expressionType) {
        return new BinaryMathNode(getLocation(), getLeftNode(), getRightNode(), operation,
            binaryType, shiftType, isOriginallyDefType, expressionType);
    }

    public BinaryMathNode withBinaryType(Class<?> binaryType) {
        return new BinaryMathNode(getLocation(), getLeftNode(), getRightNode(), operation,
            binaryType, shiftType, isOriginallyDefType, getExpressionType());
    }

    public BinaryMathNode withShiftType(Class<?> shiftType) {
        return new BinaryMathNode(getLocation(), getLeftNode(), getRightNode(), operation,
            binaryType, shiftType, isOriginallyDefType, getExpressionType());
    }
}
