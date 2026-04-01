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
import org.elasticsearch.painless.lookup.PainlessField;

public final class LoadDotNode extends ExpressionNode {

    private final PainlessField field;

    public LoadDotNode(Location location, PainlessField field, Class<?> expressionType) {
        super(location, expressionType);
        this.field = field;
    }

    public PainlessField getField() { return field; }

    public LoadDotNode withExpressionType(Class<?> expressionType) {
        return new LoadDotNode(getLocation(), field, expressionType);
    }

    public LoadDotNode withField(PainlessField field) {
        return new LoadDotNode(getLocation(), field, getExpressionType());
    }
}
