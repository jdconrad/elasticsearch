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
 * Source-level floating-point literal. Produced by the Walker. The SemanticAnalysisPhase
 * parses the {@code decimal} string, determines whether the result is a {@code float} or
 * {@code double} (via trailing {@code F}/{@code f}), and replaces this node with a
 * {@link ConstantNode}.
 */
public final class DecimalNode extends ExpressionNode {

    private final String decimal;

    public DecimalNode(Location location, String decimal) {
        super(location, null);
        this.decimal = decimal;
    }

    public String getDecimal() { return decimal; }
}
