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

public final class BlockNode extends StatementNode {

    private final List<StatementNode> statementNodes;
    private final boolean allEscape;

    public BlockNode(Location location, List<StatementNode> statementNodes, boolean allEscape) {
        super(location);
        this.statementNodes = List.copyOf(statementNodes);
        this.allEscape = allEscape;
    }

    public List<StatementNode> getStatementNodes() { return statementNodes; }
    public boolean isAllEscape() { return allEscape; }

    public BlockNode withStatementNodes(List<StatementNode> statementNodes) {
        return new BlockNode(getLocation(), statementNodes, allEscape);
    }

    public BlockNode withAllEscape(boolean allEscape) {
        return new BlockNode(getLocation(), statementNodes, allEscape);
    }
}
