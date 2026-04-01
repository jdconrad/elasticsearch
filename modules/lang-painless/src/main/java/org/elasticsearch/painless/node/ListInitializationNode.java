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
import org.elasticsearch.painless.lookup.PainlessConstructor;
import org.elasticsearch.painless.lookup.PainlessMethod;

import java.util.List;

public final class ListInitializationNode extends ArgumentsExpressionNode {

    private final PainlessConstructor constructor;
    private final PainlessMethod addMethod;

    public ListInitializationNode(Location location, List<ExpressionNode> argumentNodes,
                                  PainlessConstructor constructor, PainlessMethod addMethod, Class<?> expressionType) {
        super(location, argumentNodes, expressionType);
        this.constructor = constructor;
        this.addMethod = addMethod;
    }

    public PainlessConstructor getConstructor() { return constructor; }
    public PainlessMethod getAddMethod() { return addMethod; }

    public ListInitializationNode withArgumentNodes(List<ExpressionNode> argumentNodes) {
        return new ListInitializationNode(getLocation(), argumentNodes, constructor, addMethod, getExpressionType());
    }

    public ListInitializationNode withExpressionType(Class<?> expressionType) {
        return new ListInitializationNode(getLocation(), getArgumentNodes(), constructor, addMethod, expressionType);
    }

    public ListInitializationNode withConstructor(PainlessConstructor constructor) {
        return new ListInitializationNode(getLocation(), getArgumentNodes(), constructor, addMethod, getExpressionType());
    }

    public ListInitializationNode withAddMethod(PainlessMethod addMethod) {
        return new ListInitializationNode(getLocation(), getArgumentNodes(), constructor, addMethod, getExpressionType());
    }
}
