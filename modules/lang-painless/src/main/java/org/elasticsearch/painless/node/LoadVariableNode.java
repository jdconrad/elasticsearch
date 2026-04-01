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

public final class LoadVariableNode extends ExpressionNode {

    private final String name;

    public LoadVariableNode(Location location, String name, Class<?> expressionType) {
        super(location, expressionType);
        this.name = name;
    }

    public String getName() { return name; }

    public LoadVariableNode withExpressionType(Class<?> expressionType) {
        return new LoadVariableNode(getLocation(), name, expressionType);
    }
}
