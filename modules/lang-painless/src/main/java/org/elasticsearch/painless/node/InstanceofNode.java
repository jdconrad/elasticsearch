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

public final class InstanceofNode extends UnaryExpressionNode {

    private final Class<?> instanceType;

    public InstanceofNode(Location location, ExpressionNode childNode, Class<?> instanceType, Class<?> expressionType) {
        super(location, childNode, expressionType);
        this.instanceType = instanceType;
    }

    public Class<?> getInstanceType() { return instanceType; }

    public InstanceofNode withChildNode(ExpressionNode childNode) {
        return new InstanceofNode(getLocation(), childNode, instanceType, getExpressionType());
    }

    public InstanceofNode withExpressionType(Class<?> expressionType) {
        return new InstanceofNode(getLocation(), getChildNode(), instanceType, expressionType);
    }

    public InstanceofNode withInstanceType(Class<?> instanceType) {
        return new InstanceofNode(getLocation(), getChildNode(), instanceType, getExpressionType());
    }
}
