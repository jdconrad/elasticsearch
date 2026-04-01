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

public final class StoreDotNode extends UnaryExpressionNode {

    private final PainlessField field;

    public StoreDotNode(Location location, ExpressionNode childNode, PainlessField field, Class<?> expressionType) {
        super(location, childNode, expressionType);
        this.field = field;
    }

    public PainlessField getField() { return field; }

    public StoreDotNode withChildNode(ExpressionNode childNode) {
        return new StoreDotNode(getLocation(), childNode, field, getExpressionType());
    }

    public StoreDotNode withExpressionType(Class<?> expressionType) {
        return new StoreDotNode(getLocation(), getChildNode(), field, expressionType);
    }

    public StoreDotNode withField(PainlessField field) {
        return new StoreDotNode(getLocation(), getChildNode(), field, getExpressionType());
    }
}
