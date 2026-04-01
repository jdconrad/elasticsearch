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

public final class StoreFieldMemberNode extends UnaryExpressionNode {

    private final boolean isStatic;
    private final String name;
    private final Class<?> storeType;

    public StoreFieldMemberNode(Location location, ExpressionNode childNode,
                                boolean isStatic, String name, Class<?> storeType, Class<?> expressionType) {
        super(location, childNode, expressionType);
        this.isStatic = isStatic;
        this.name = name;
        this.storeType = storeType;
    }

    public boolean isStatic() { return isStatic; }
    public String getName() { return name; }
    public Class<?> getStoreType() { return storeType; }

    public StoreFieldMemberNode withChildNode(ExpressionNode childNode) {
        return new StoreFieldMemberNode(getLocation(), childNode, isStatic, name, storeType, getExpressionType());
    }

    public StoreFieldMemberNode withExpressionType(Class<?> expressionType) {
        return new StoreFieldMemberNode(getLocation(), getChildNode(), isStatic, name, storeType, expressionType);
    }
}
