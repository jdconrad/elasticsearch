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
import org.elasticsearch.painless.lookup.PainlessCast;

public final class CastNode extends UnaryExpressionNode {

    private final PainlessCast cast;

    public CastNode(Location location, ExpressionNode childNode, PainlessCast cast, Class<?> expressionType) {
        super(location, childNode, expressionType);
        this.cast = cast;
    }

    public PainlessCast getCast() { return cast; }

    public CastNode withChildNode(ExpressionNode childNode) {
        return new CastNode(getLocation(), childNode, cast, getExpressionType());
    }

    public CastNode withExpressionType(Class<?> expressionType) {
        return new CastNode(getLocation(), getChildNode(), cast, expressionType);
    }

    public CastNode withCast(PainlessCast cast) {
        return new CastNode(getLocation(), getChildNode(), cast, getExpressionType());
    }
}
