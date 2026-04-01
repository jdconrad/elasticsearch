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

import java.util.List;

public final class InvokeCallDefNode extends ArgumentsExpressionNode {

    private final String name;

    public InvokeCallDefNode(Location location, List<ExpressionNode> argumentNodes,
                             String name, Class<?> expressionType) {
        super(location, argumentNodes, expressionType);
        this.name = name;
    }

    public String getName() { return name; }

    public InvokeCallDefNode withArgumentNodes(List<ExpressionNode> argumentNodes) {
        return new InvokeCallDefNode(getLocation(), argumentNodes, name, getExpressionType());
    }

    public InvokeCallDefNode withExpressionType(Class<?> expressionType) {
        return new InvokeCallDefNode(getLocation(), getArgumentNodes(), name, expressionType);
    }
}
