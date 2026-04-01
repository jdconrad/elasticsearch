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

public final class UnaryMathNode extends UnaryExpressionNode {

    private final Operation operation;
    private final Class<?> unaryType;
    private final int flags;
    private final boolean isOriginallyDefType;

    public UnaryMathNode(Location location, ExpressionNode childNode,
                         Operation operation, Class<?> unaryType, int flags,
                         boolean isOriginallyDefType, Class<?> expressionType) {
        super(location, childNode, expressionType);
        this.operation = operation;
        this.unaryType = unaryType;
        this.flags = flags;
        this.isOriginallyDefType = isOriginallyDefType;
    }

    public Operation getOperation() { return operation; }
    public Class<?> getUnaryType() { return unaryType; }
    public int getFlags() { return flags; }
    public boolean isOriginallyDefType() { return isOriginallyDefType; }

    public UnaryMathNode withChildNode(ExpressionNode childNode) {
        return new UnaryMathNode(getLocation(), childNode, operation, unaryType, flags, isOriginallyDefType, getExpressionType());
    }

    public UnaryMathNode withExpressionType(Class<?> expressionType) {
        return new UnaryMathNode(getLocation(), getChildNode(), operation, unaryType, flags, isOriginallyDefType, expressionType);
    }

    public UnaryMathNode withUnaryType(Class<?> unaryType) {
        return new UnaryMathNode(getLocation(), getChildNode(), operation, unaryType, flags, isOriginallyDefType, getExpressionType());
    }
}
