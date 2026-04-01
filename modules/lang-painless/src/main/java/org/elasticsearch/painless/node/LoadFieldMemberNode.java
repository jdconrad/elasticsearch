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

public final class LoadFieldMemberNode extends ExpressionNode {

    private final boolean isStatic;
    private final String name;

    public LoadFieldMemberNode(Location location, boolean isStatic, String name, Class<?> expressionType) {
        super(location, expressionType);
        this.isStatic = isStatic;
        this.name = name;
    }

    public boolean isStatic() { return isStatic; }
    public String getName() { return name; }

    public LoadFieldMemberNode withExpressionType(Class<?> expressionType) {
        return new LoadFieldMemberNode(getLocation(), isStatic, name, expressionType);
    }
}
