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

public final class ConstantNode extends ExpressionNode {

    private final Object constant;
    private final String constantFieldName;

    public ConstantNode(Location location, Object constant, String constantFieldName, Class<?> expressionType) {
        super(location, expressionType);
        this.constant = constant;
        this.constantFieldName = constantFieldName;
    }

    public Object getConstant() { return constant; }
    public String getConstantFieldName() { return constantFieldName; }

    public ConstantNode withExpressionType(Class<?> expressionType) {
        return new ConstantNode(getLocation(), constant, constantFieldName, expressionType);
    }

    public ConstantNode withConstant(Object constant) {
        return new ConstantNode(getLocation(), constant, constantFieldName, getExpressionType());
    }

    public ConstantNode withConstantFieldName(String constantFieldName) {
        return new ConstantNode(getLocation(), constant, constantFieldName, getExpressionType());
    }
}
