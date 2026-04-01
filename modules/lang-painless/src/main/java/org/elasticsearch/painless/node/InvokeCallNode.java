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
import org.elasticsearch.painless.lookup.PainlessMethod;

import java.util.List;

public final class InvokeCallNode extends ArgumentsExpressionNode {

    private final PainlessMethod method;
    private final Class<?> box;

    public InvokeCallNode(Location location, List<ExpressionNode> argumentNodes,
                          PainlessMethod method, Class<?> box, Class<?> expressionType) {
        super(location, argumentNodes, expressionType);
        this.method = method;
        this.box = box;
    }

    public PainlessMethod getMethod() { return method; }
    public Class<?> getBox() { return box; }

    public InvokeCallNode withArgumentNodes(List<ExpressionNode> argumentNodes) {
        return new InvokeCallNode(getLocation(), argumentNodes, method, box, getExpressionType());
    }

    public InvokeCallNode withExpressionType(Class<?> expressionType) {
        return new InvokeCallNode(getLocation(), getArgumentNodes(), method, box, expressionType);
    }

    public InvokeCallNode withMethod(PainlessMethod method) {
        return new InvokeCallNode(getLocation(), getArgumentNodes(), method, box, getExpressionType());
    }

    public InvokeCallNode withBox(Class<?> box) {
        return new InvokeCallNode(getLocation(), getArgumentNodes(), method, box, getExpressionType());
    }
}
