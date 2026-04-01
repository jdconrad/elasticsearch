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
 * Source-level regex literal ({@code /pattern/flags}). All regexes are constants;
 * the StaticConstantExtractionPhase compiles the pattern and hoists it to a static field.
 */
public final class RegexNode extends ExpressionNode {

    private final String pattern;
    private final String flags;

    public RegexNode(Location location, String pattern, String flags, Class<?> expressionType) {
        super(location, expressionType);
        this.pattern = pattern;
        this.flags = flags;
    }

    public String getPattern() { return pattern; }
    public String getFlags() { return flags; }

    public RegexNode withExpressionType(Class<?> expressionType) {
        return new RegexNode(getLocation(), pattern, flags, expressionType);
    }
}
