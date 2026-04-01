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

public final class StoreDotDefNode extends UnaryExpressionNode {

    private final String value;
    private final Class<?> storeType;

    public StoreDotDefNode(Location location, ExpressionNode childNode,
                           String value, Class<?> storeType, Class<?> expressionType) {
        super(location, childNode, expressionType);
        this.value = value;
        this.storeType = storeType;
    }

    public String getValue() { return value; }
    public Class<?> getStoreType() { return storeType; }

    public StoreDotDefNode withChildNode(ExpressionNode childNode) {
        return new StoreDotDefNode(getLocation(), childNode, value, storeType, getExpressionType());
    }

    public StoreDotDefNode withExpressionType(Class<?> expressionType) {
        return new StoreDotDefNode(getLocation(), getChildNode(), value, storeType, expressionType);
    }
}
