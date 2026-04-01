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
 * Container for a for-each loop. The {@code conditionNode} is either a
 * {@link ForEachSubArrayNode} or a {@link ForEachSubIterableNode}.
 */
public final class ForEachLoopNode extends StatementNode {

    private final StatementNode conditionNode;

    public ForEachLoopNode(Location location, StatementNode conditionNode) {
        super(location);
        this.conditionNode = conditionNode;
    }

    public StatementNode getConditionNode() { return conditionNode; }

    public ForEachLoopNode withConditionNode(StatementNode conditionNode) {
        return new ForEachLoopNode(getLocation(), conditionNode);
    }
}
