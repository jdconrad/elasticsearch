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

public final class WhileLoopNode extends StatementNode {

    private final ExpressionNode conditionNode;
    private final BlockNode blockNode;
    private final boolean isContinuous;

    public WhileLoopNode(Location location, ExpressionNode conditionNode, BlockNode blockNode, boolean isContinuous) {
        super(location);
        this.conditionNode = conditionNode;
        this.blockNode = blockNode;
        this.isContinuous = isContinuous;
    }

    public ExpressionNode getConditionNode() { return conditionNode; }
    public BlockNode getBlockNode() { return blockNode; }
    public boolean isContinuous() { return isContinuous; }

    public WhileLoopNode withConditionNode(ExpressionNode conditionNode) {
        return new WhileLoopNode(getLocation(), conditionNode, blockNode, isContinuous);
    }

    public WhileLoopNode withBlockNode(BlockNode blockNode) {
        return new WhileLoopNode(getLocation(), conditionNode, blockNode, isContinuous);
    }
}
