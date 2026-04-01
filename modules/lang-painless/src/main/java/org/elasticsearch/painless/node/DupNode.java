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

public final class DupNode extends UnaryExpressionNode {

    private final int size;
    private final int depth;

    public DupNode(Location location, ExpressionNode childNode, int size, int depth, Class<?> expressionType) {
        super(location, childNode, expressionType);
        this.size = size;
        this.depth = depth;
    }

    public int getSize() { return size; }
    public int getDepth() { return depth; }

    public DupNode withChildNode(ExpressionNode childNode) {
        return new DupNode(getLocation(), childNode, size, depth, getExpressionType());
    }

    public DupNode withExpressionType(Class<?> expressionType) {
        return new DupNode(getLocation(), getChildNode(), size, depth, expressionType);
    }
}
