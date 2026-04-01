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

public final class StoreDotShortcutNode extends UnaryExpressionNode {

    private final PainlessMethod setter;

    public StoreDotShortcutNode(Location location, ExpressionNode childNode, PainlessMethod setter, Class<?> expressionType) {
        super(location, childNode, expressionType);
        this.setter = setter;
    }

    public PainlessMethod getSetter() { return setter; }

    public StoreDotShortcutNode withChildNode(ExpressionNode childNode) {
        return new StoreDotShortcutNode(getLocation(), childNode, setter, getExpressionType());
    }

    public StoreDotShortcutNode withExpressionType(Class<?> expressionType) {
        return new StoreDotShortcutNode(getLocation(), getChildNode(), setter, expressionType);
    }

    public StoreDotShortcutNode withSetter(PainlessMethod setter) {
        return new StoreDotShortcutNode(getLocation(), getChildNode(), setter, getExpressionType());
    }
}
