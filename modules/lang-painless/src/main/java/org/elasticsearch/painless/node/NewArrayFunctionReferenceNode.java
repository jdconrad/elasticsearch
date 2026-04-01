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
 * Source-level new-array function reference ({@code Type[]::new}). Produced by the
 * Walker. The SemanticAnalysisPhase resolves this to a {@link TypedInterfaceReferenceNode}
 * or {@link DefInterfaceReferenceNode}.
 */
public final class NewArrayFunctionReferenceNode extends ExpressionNode {

    private final String canonicalTypeName;

    public NewArrayFunctionReferenceNode(Location location, String canonicalTypeName,
                                        Class<?> expressionType) {
        super(location, expressionType);
        this.canonicalTypeName = canonicalTypeName;
    }

    public String getCanonicalTypeName() { return canonicalTypeName; }

    public NewArrayFunctionReferenceNode withExpressionType(Class<?> expressionType) {
        return new NewArrayFunctionReferenceNode(getLocation(), canonicalTypeName, expressionType);
    }
}
