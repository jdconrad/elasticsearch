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

public final class LoadDotArrayLengthNode extends ExpressionNode {

    public LoadDotArrayLengthNode(Location location, Class<?> expressionType) {
        super(location, expressionType);
    }

    public LoadDotArrayLengthNode withExpressionType(Class<?> expressionType) {
        return new LoadDotArrayLengthNode(getLocation(), expressionType);
    }
}
