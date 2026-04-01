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

public final class StoreListShortcutNode extends UnaryExpressionNode {

    private final PainlessMethod setter;

    public StoreListShortcutNode(Location location, ExpressionNode childNode, PainlessMethod setter, Class<?> expressionType) {
        super(location, childNode, expressionType);
        this.setter = setter;
    }

    public PainlessMethod getSetter() { return setter; }

    public StoreListShortcutNode withChildNode(ExpressionNode childNode) {
        return new StoreListShortcutNode(getLocation(), childNode, setter, getExpressionType());
    }

    public StoreListShortcutNode withExpressionType(Class<?> expressionType) {
        return new StoreListShortcutNode(getLocation(), getChildNode(), setter, expressionType);
    }

    public StoreListShortcutNode withSetter(PainlessMethod setter) {
        return new StoreListShortcutNode(getLocation(), getChildNode(), setter, getExpressionType());
    }
}
