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

import java.util.List;

public final class NewObjectNode extends ArgumentsExpressionNode {

    private final PainlessConstructor constructor;

    public NewObjectNode(Location location, List<ExpressionNode> argumentNodes,
                         PainlessConstructor constructor, Class<?> expressionType) {
        super(location, argumentNodes, expressionType);
        this.constructor = constructor;
    }

    public PainlessConstructor getConstructor() { return constructor; }

    public NewObjectNode withArgumentNodes(List<ExpressionNode> argumentNodes) {
        return new NewObjectNode(getLocation(), argumentNodes, constructor, getExpressionType());
    }

    public NewObjectNode withExpressionType(Class<?> expressionType) {
        return new NewObjectNode(getLocation(), getArgumentNodes(), constructor, expressionType);
    }

    public NewObjectNode withConstructor(PainlessConstructor constructor) {
        return new NewObjectNode(getLocation(), getArgumentNodes(), constructor, getExpressionType());
    }
}
