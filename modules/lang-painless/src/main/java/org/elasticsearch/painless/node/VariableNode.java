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
 * Source-level variable reference. Produced by the Walker for bare identifiers.
 * The LoweringPhase converts this to a {@link LoadVariableNode} or
 * {@link StoreVariableNode} based on context.
 */
public final class VariableNode extends ExpressionNode {

    private final String name;

    public VariableNode(Location location, String name, Class<?> expressionType) {
        super(location, expressionType);
        this.name = name;
    }

    public String getName() { return name; }

    public VariableNode withExpressionType(Class<?> expressionType) {
        return new VariableNode(getLocation(), name, expressionType);
    }
}
