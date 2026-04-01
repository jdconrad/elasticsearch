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

public final class IfElseNode extends StatementNode {

    private final ExpressionNode conditionNode;
    private final BlockNode ifBlockNode;
    private final BlockNode elseBlockNode;
    private final boolean allEscape;

    public IfElseNode(Location location, ExpressionNode conditionNode, BlockNode ifBlockNode,
                      BlockNode elseBlockNode, boolean allEscape) {
        super(location);
        this.conditionNode = conditionNode;
        this.ifBlockNode = ifBlockNode;
        this.elseBlockNode = elseBlockNode;
        this.allEscape = allEscape;
    }

    public ExpressionNode getConditionNode() { return conditionNode; }
    public BlockNode getIfBlockNode() { return ifBlockNode; }
    public BlockNode getElseBlockNode() { return elseBlockNode; }
    public boolean isAllEscape() { return allEscape; }

    public IfElseNode withConditionNode(ExpressionNode conditionNode) {
        return new IfElseNode(getLocation(), conditionNode, ifBlockNode, elseBlockNode, allEscape);
    }

    public IfElseNode withIfBlockNode(BlockNode ifBlockNode) {
        return new IfElseNode(getLocation(), conditionNode, ifBlockNode, elseBlockNode, allEscape);
    }

    public IfElseNode withElseBlockNode(BlockNode elseBlockNode) {
        return new IfElseNode(getLocation(), conditionNode, ifBlockNode, elseBlockNode, allEscape);
    }
}
