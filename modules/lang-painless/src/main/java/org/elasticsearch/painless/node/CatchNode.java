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
 * A catch block as part of a try-catch statement.
 *
 * <p>Before semantic analysis, {@code exceptionTypeName} holds the string type name from
 * source; {@code exceptionType} is null. The SemanticAnalysisPhase resolves the name and
 * fills in {@code exceptionType}.
 */
public final class CatchNode extends StatementNode {

    private final BlockNode blockNode;
    // resolved by SemanticAnalysisPhase; null before that phase
    private final Class<?> exceptionType;
    // string name from source; set by Walker, used by SemanticAnalysisPhase
    private final String exceptionTypeName;
    private final String name;

    public CatchNode(Location location, BlockNode blockNode, Class<?> exceptionType,
                     String exceptionTypeName, String name) {
        super(location);
        this.blockNode = blockNode;
        this.exceptionType = exceptionType;
        this.exceptionTypeName = exceptionTypeName;
        this.name = name;
    }

    public BlockNode getBlockNode() { return blockNode; }
    public Class<?> getExceptionType() { return exceptionType; }
    public String getExceptionTypeName() { return exceptionTypeName; }
    public String getName() { return name; }

    public CatchNode withBlockNode(BlockNode blockNode) {
        return new CatchNode(getLocation(), blockNode, exceptionType, exceptionTypeName, name);
    }

    public CatchNode withExceptionType(Class<?> exceptionType) {
        return new CatchNode(getLocation(), blockNode, exceptionType, exceptionTypeName, name);
    }
}
