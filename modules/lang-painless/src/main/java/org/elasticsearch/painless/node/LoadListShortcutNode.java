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
import org.elasticsearch.painless.lookup.PainlessMethod;

public final class LoadListShortcutNode extends ExpressionNode {

    private final PainlessMethod getter;

    public LoadListShortcutNode(Location location, PainlessMethod getter, Class<?> expressionType) {
        super(location, expressionType);
        this.getter = getter;
    }

    public PainlessMethod getGetter() { return getter; }

    public LoadListShortcutNode withExpressionType(Class<?> expressionType) {
        return new LoadListShortcutNode(getLocation(), getter, expressionType);
    }

    public LoadListShortcutNode withGetter(PainlessMethod getter) {
        return new LoadListShortcutNode(getLocation(), getter, getExpressionType());
    }
}
