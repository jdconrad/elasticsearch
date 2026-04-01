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
 * Abstract base for expression nodes that have exactly two expression children.
 */
public abstract class BinaryExpressionNode extends ExpressionNode {

    private final ExpressionNode leftNode;
    private final ExpressionNode rightNode;

    public BinaryExpressionNode(Location location, ExpressionNode leftNode, ExpressionNode rightNode, Class<?> expressionType) {
        super(location, expressionType);
        this.leftNode = leftNode;
        this.rightNode = rightNode;
    }

    public ExpressionNode getLeftNode() {
        return leftNode;
    }

    public ExpressionNode getRightNode() {
        return rightNode;
    }
}
