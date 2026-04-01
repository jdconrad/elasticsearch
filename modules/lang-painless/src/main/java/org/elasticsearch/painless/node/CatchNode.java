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

public final class CatchNode extends StatementNode {

    private final BlockNode blockNode;
    private final Class<?> exceptionType;
    private final String name;

    public CatchNode(Location location, BlockNode blockNode, Class<?> exceptionType, String name) {
        super(location);
        this.blockNode = blockNode;
        this.exceptionType = exceptionType;
        this.name = name;
    }

    public BlockNode getBlockNode() { return blockNode; }
    public Class<?> getExceptionType() { return exceptionType; }
    public String getName() { return name; }

    public CatchNode withBlockNode(BlockNode blockNode) {
        return new CatchNode(getLocation(), blockNode, exceptionType, name);
    }
}
