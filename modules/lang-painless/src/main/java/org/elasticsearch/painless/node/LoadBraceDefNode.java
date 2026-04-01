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

public final class LoadBraceDefNode extends ExpressionNode {

    private final Class<?> indexType;

    public LoadBraceDefNode(Location location, Class<?> indexType, Class<?> expressionType) {
        super(location, expressionType);
        this.indexType = indexType;
    }

    public Class<?> getIndexType() { return indexType; }

    public LoadBraceDefNode withExpressionType(Class<?> expressionType) {
        return new LoadBraceDefNode(getLocation(), indexType, expressionType);
    }

    public LoadBraceDefNode withIndexType(Class<?> indexType) {
        return new LoadBraceDefNode(getLocation(), indexType, getExpressionType());
    }
}
