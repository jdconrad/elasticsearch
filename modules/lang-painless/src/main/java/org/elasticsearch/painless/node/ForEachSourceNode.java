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
 * Source-level for-each loop ({@code for (Type var : iterable) { ... }}). Produced by
 * the Walker. The SemanticAnalysisPhase resolves {@code canonicalTypeName} and lowers
 * this to a {@link ForEachLoopNode} wrapping either a {@link ForEachSubArrayNode} or
 * {@link ForEachSubIterableNode}.
 */
public final class ForEachSourceNode extends StatementNode {

    private final String canonicalTypeName;
    private final String variableName;
    private final ExpressionNode iterableNode;
    private final BlockNode blockNode;

    public ForEachSourceNode(Location location, String canonicalTypeName, String variableName,
                             ExpressionNode iterableNode, BlockNode blockNode) {
        super(location);
        this.canonicalTypeName = canonicalTypeName;
        this.variableName = variableName;
        this.iterableNode = iterableNode;
        this.blockNode = blockNode;
    }

    public String getCanonicalTypeName() { return canonicalTypeName; }
    public String getVariableName() { return variableName; }
    public ExpressionNode getIterableNode() { return iterableNode; }
    public BlockNode getBlockNode() { return blockNode; }

    public ForEachSourceNode withIterableNode(ExpressionNode iterableNode) {
        return new ForEachSourceNode(getLocation(), canonicalTypeName, variableName, iterableNode, blockNode);
    }

    public ForEachSourceNode withBlockNode(BlockNode blockNode) {
        return new ForEachSourceNode(getLocation(), canonicalTypeName, variableName, iterableNode, blockNode);
    }
}
