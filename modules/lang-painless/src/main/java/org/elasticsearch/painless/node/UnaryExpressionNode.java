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
 * Abstract base for expression nodes that have exactly one expression child.
 */
public abstract class UnaryExpressionNode extends ExpressionNode {

    private final ExpressionNode childNode;

    public UnaryExpressionNode(Location location, ExpressionNode childNode, Class<?> expressionType) {
        super(location, expressionType);
        this.childNode = childNode;
    }

    public ExpressionNode getChildNode() {
        return childNode;
    }
}
