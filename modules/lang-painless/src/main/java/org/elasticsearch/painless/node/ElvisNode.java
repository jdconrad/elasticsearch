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

public final class ElvisNode extends BinaryExpressionNode {

    public ElvisNode(Location location, ExpressionNode leftNode, ExpressionNode rightNode, Class<?> expressionType) {
        super(location, leftNode, rightNode, expressionType);
    }

    public ElvisNode withLeftNode(ExpressionNode leftNode) {
        return new ElvisNode(getLocation(), leftNode, getRightNode(), getExpressionType());
    }

    public ElvisNode withRightNode(ExpressionNode rightNode) {
        return new ElvisNode(getLocation(), getLeftNode(), rightNode, getExpressionType());
    }

    public ElvisNode withExpressionType(Class<?> expressionType) {
        return new ElvisNode(getLocation(), getLeftNode(), getRightNode(), expressionType);
    }
}
