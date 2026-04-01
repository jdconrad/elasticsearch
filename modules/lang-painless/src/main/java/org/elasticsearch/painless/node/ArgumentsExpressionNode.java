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

/**
 * Abstract base for expression nodes that take a list of expression arguments
 * (calls, constructors, list/map initializers, etc.).
 */
public abstract class ArgumentsExpressionNode extends ExpressionNode {

    private final List<ExpressionNode> argumentNodes;

    public ArgumentsExpressionNode(Location location, List<ExpressionNode> argumentNodes, Class<?> expressionType) {
        super(location, expressionType);
        this.argumentNodes = List.copyOf(argumentNodes);
    }

    public List<ExpressionNode> getArgumentNodes() {
        return argumentNodes;
    }
}
