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

public final class DeclarationNode extends StatementNode {

    private final ExpressionNode expressionNode;
    private final Class<?> declarationType;
    private final String name;

    public DeclarationNode(Location location, ExpressionNode expressionNode, Class<?> declarationType, String name) {
        super(location);
        this.expressionNode = expressionNode;
        this.declarationType = declarationType;
        this.name = name;
    }

    public ExpressionNode getExpressionNode() { return expressionNode; }
    public Class<?> getDeclarationType() { return declarationType; }
    public String getName() { return name; }

    public DeclarationNode withExpressionNode(ExpressionNode expressionNode) {
        return new DeclarationNode(getLocation(), expressionNode, declarationType, name);
    }
}
