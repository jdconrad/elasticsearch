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

public final class TryNode extends StatementNode {

    private final BlockNode blockNode;
    private final List<CatchNode> catchNodes;
    private final boolean allEscape;

    public TryNode(Location location, BlockNode blockNode, List<CatchNode> catchNodes, boolean allEscape) {
        super(location);
        this.blockNode = blockNode;
        this.catchNodes = List.copyOf(catchNodes);
        this.allEscape = allEscape;
    }

    public BlockNode getBlockNode() { return blockNode; }
    public List<CatchNode> getCatchNodes() { return catchNodes; }
    public boolean isAllEscape() { return allEscape; }

    public TryNode withBlockNode(BlockNode blockNode) {
        return new TryNode(getLocation(), blockNode, catchNodes, allEscape);
    }

    public TryNode withCatchNodes(List<CatchNode> catchNodes) {
        return new TryNode(getLocation(), blockNode, catchNodes, allEscape);
    }
}
