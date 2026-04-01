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

public final class StringConcatenationNode extends ArgumentsExpressionNode {

    public StringConcatenationNode(Location location, List<ExpressionNode> argumentNodes, Class<?> expressionType) {
        super(location, argumentNodes, expressionType);
    }

    public StringConcatenationNode withArgumentNodes(List<ExpressionNode> argumentNodes) {
        return new StringConcatenationNode(getLocation(), argumentNodes, getExpressionType());
    }

    public StringConcatenationNode withExpressionType(Class<?> expressionType) {
        return new StringConcatenationNode(getLocation(), getArgumentNodes(), expressionType);
    }
}
