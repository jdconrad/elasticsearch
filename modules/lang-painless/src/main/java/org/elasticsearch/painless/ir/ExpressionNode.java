/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */

package org.elasticsearch.painless.ir;

import org.elasticsearch.painless.Location;
import org.elasticsearch.painless.lookup.PainlessLookupUtility;

import java.util.Objects;

public abstract class ExpressionNode extends IRNode {

    /* ---- begin node data ---- */

    private final Class<?> expressionType;

    public Class<?> getExpressionType() {
        return expressionType;
    }

    public String getCanonicalExpressionTypeName() {
        return PainlessLookupUtility.typeToCanonicalTypeName(expressionType);
    }

    /* ---- end node data ---- */

    public ExpressionNode(Location location, Class<?> expressionType) {
        super(location);

        this.expressionType = Objects.requireNonNull(expressionType);
    }

}
