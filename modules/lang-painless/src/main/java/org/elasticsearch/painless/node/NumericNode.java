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
 * Source-level integer/long literal. Produced by the Walker. The SemanticAnalysisPhase
 * parses the {@code numeric} string with the given {@code radix}, determines whether
 * the result is an {@code int} or {@code long} (via trailing {@code L}/{@code l}), and
 * replaces this node with a {@link ConstantNode}.
 */
public final class NumericNode extends ExpressionNode {

    private final String numeric;
    private final int radix;

    public NumericNode(Location location, String numeric, int radix) {
        super(location, null);
        this.numeric = numeric;
        this.radix = radix;
    }

    public String getNumeric() { return numeric; }
    public int getRadix() { return radix; }
}
