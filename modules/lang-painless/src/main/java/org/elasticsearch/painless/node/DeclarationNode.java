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

/**
 * A single variable declaration ({@code Type name = expr} or {@code Type name}).
 *
 * <p>Before semantic analysis, {@code declarationTypeName} holds the string type name from
 * source; {@code declarationType} is null. The SemanticAnalysisPhase resolves the name
 * and fills in {@code declarationType}.
 */
public final class DeclarationNode extends StatementNode {

    private final ExpressionNode expressionNode;
    // resolved by SemanticAnalysisPhase; null before that phase
    private final Class<?> declarationType;
    // string name from source; set by Walker, used by SemanticAnalysisPhase
    private final String declarationTypeName;
    private final String name;

    public DeclarationNode(Location location, ExpressionNode expressionNode,
                           Class<?> declarationType, String declarationTypeName, String name) {
        super(location);
        this.expressionNode = expressionNode;
        this.declarationType = declarationType;
        this.declarationTypeName = declarationTypeName;
        this.name = name;
    }

    public ExpressionNode getExpressionNode() { return expressionNode; }
    public Class<?> getDeclarationType() { return declarationType; }
    public String getDeclarationTypeName() { return declarationTypeName; }
    public String getName() { return name; }

    public DeclarationNode withExpressionNode(ExpressionNode expressionNode) {
        return new DeclarationNode(getLocation(), expressionNode, declarationType, declarationTypeName, name);
    }

    public DeclarationNode withDeclarationType(Class<?> declarationType) {
        return new DeclarationNode(getLocation(), expressionNode, declarationType, declarationTypeName, name);
    }
}
