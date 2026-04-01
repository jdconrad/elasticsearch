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
 * {@code initializerNode} may be a {@link DeclarationBlockNode} or an {@link ExpressionNode},
 * or null if the initializer is omitted.
 */
public final class ForLoopNode extends StatementNode {

    private final Node initializerNode;
    private final ExpressionNode conditionNode;
    private final ExpressionNode afterthoughtNode;
    private final BlockNode blockNode;
    private final boolean isContinuous;

    public ForLoopNode(Location location, Node initializerNode, ExpressionNode conditionNode,
                       ExpressionNode afterthoughtNode, BlockNode blockNode, boolean isContinuous) {
        super(location);
        this.initializerNode = initializerNode;
        this.conditionNode = conditionNode;
        this.afterthoughtNode = afterthoughtNode;
        this.blockNode = blockNode;
        this.isContinuous = isContinuous;
    }

    public Node getInitializerNode() { return initializerNode; }
    public ExpressionNode getConditionNode() { return conditionNode; }
    public ExpressionNode getAfterthoughtNode() { return afterthoughtNode; }
    public BlockNode getBlockNode() { return blockNode; }
    public boolean isContinuous() { return isContinuous; }

    public ForLoopNode withInitializerNode(Node initializerNode) {
        return new ForLoopNode(getLocation(), initializerNode, conditionNode, afterthoughtNode, blockNode, isContinuous);
    }

    public ForLoopNode withConditionNode(ExpressionNode conditionNode) {
        return new ForLoopNode(getLocation(), initializerNode, conditionNode, afterthoughtNode, blockNode, isContinuous);
    }

    public ForLoopNode withAfterthoughtNode(ExpressionNode afterthoughtNode) {
        return new ForLoopNode(getLocation(), initializerNode, conditionNode, afterthoughtNode, blockNode, isContinuous);
    }

    public ForLoopNode withBlockNode(BlockNode blockNode) {
        return new ForLoopNode(getLocation(), initializerNode, conditionNode, afterthoughtNode, blockNode, isContinuous);
    }
}
