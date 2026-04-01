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
 * Source-level field/property access ({@code a.b} or {@code a?.b}). Produced by the
 * Walker; the LoweringPhase resolves it into a load ({@link LoadDotNode},
 * {@link LoadDotDefNode}, etc.) or store variant based on context.
 */
public final class DotAccessNode extends ExpressionNode {

    private final ExpressionNode prefixNode;
    private final String fieldName;
    private final boolean isNullSafe;

    public DotAccessNode(Location location, ExpressionNode prefixNode, String fieldName,
                         boolean isNullSafe, Class<?> expressionType) {
        super(location, expressionType);
        this.prefixNode = prefixNode;
        this.fieldName = fieldName;
        this.isNullSafe = isNullSafe;
    }

    public ExpressionNode getPrefixNode() { return prefixNode; }
    public String getFieldName() { return fieldName; }
    public boolean isNullSafe() { return isNullSafe; }

    public DotAccessNode withPrefixNode(ExpressionNode prefixNode) {
        return new DotAccessNode(getLocation(), prefixNode, fieldName, isNullSafe, getExpressionType());
    }

    public DotAccessNode withExpressionType(Class<?> expressionType) {
        return new DotAccessNode(getLocation(), prefixNode, fieldName, isNullSafe, expressionType);
    }
}
