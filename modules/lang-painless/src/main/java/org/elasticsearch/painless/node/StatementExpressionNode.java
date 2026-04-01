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

public final class StatementExpressionNode extends StatementNode {

    private final ExpressionNode expressionNode;

    public StatementExpressionNode(Location location, ExpressionNode expressionNode) {
        super(location);
        this.expressionNode = expressionNode;
    }

    public ExpressionNode getExpressionNode() { return expressionNode; }

    public StatementExpressionNode withExpressionNode(ExpressionNode expressionNode) {
        return new StatementExpressionNode(getLocation(), expressionNode);
    }
}
