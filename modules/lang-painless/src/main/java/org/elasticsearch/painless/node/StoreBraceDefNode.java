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

public final class StoreBraceDefNode extends UnaryExpressionNode {

    private final Class<?> indexType;
    private final Class<?> storeType;

    public StoreBraceDefNode(Location location, ExpressionNode childNode,
                             Class<?> indexType, Class<?> storeType, Class<?> expressionType) {
        super(location, childNode, expressionType);
        this.indexType = indexType;
        this.storeType = storeType;
    }

    public Class<?> getIndexType() { return indexType; }
    public Class<?> getStoreType() { return storeType; }

    public StoreBraceDefNode withChildNode(ExpressionNode childNode) {
        return new StoreBraceDefNode(getLocation(), childNode, indexType, storeType, getExpressionType());
    }

    public StoreBraceDefNode withExpressionType(Class<?> expressionType) {
        return new StoreBraceDefNode(getLocation(), getChildNode(), indexType, storeType, expressionType);
    }
}
