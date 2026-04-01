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
 * Source-level lambda expression. Produced by the Walker. Parameter type names are
 * strings (null entries indicate inferred types); the SemanticAnalysisPhase resolves
 * them and lowers the lambda to a {@link TypedInterfaceReferenceNode} or
 * {@link DefInterfaceReferenceNode}.
 */
public final class LambdaNode extends ExpressionNode {

    private final List<String> typeNameParameters;
    private final List<String> parameterNames;
    private final BlockNode blockNode;

    public LambdaNode(Location location, List<String> typeNameParameters, List<String> parameterNames,
                      BlockNode blockNode, Class<?> expressionType) {
        super(location, expressionType);
        this.typeNameParameters = List.copyOf(typeNameParameters);
        this.parameterNames = List.copyOf(parameterNames);
        this.blockNode = blockNode;
    }

    public List<String> getTypeNameParameters() { return typeNameParameters; }
    public List<String> getParameterNames() { return parameterNames; }
    public BlockNode getBlockNode() { return blockNode; }

    public LambdaNode withBlockNode(BlockNode blockNode) {
        return new LambdaNode(getLocation(), typeNameParameters, parameterNames, blockNode, getExpressionType());
    }

    public LambdaNode withExpressionType(Class<?> expressionType) {
        return new LambdaNode(getLocation(), typeNameParameters, parameterNames, blockNode, expressionType);
    }
}
