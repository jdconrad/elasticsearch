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
import org.elasticsearch.painless.lookup.PainlessConstructor;
import org.elasticsearch.painless.lookup.PainlessMethod;

import java.util.List;

public final class MapInitializationNode extends ExpressionNode {

    private final List<ExpressionNode> keyNodes;
    private final List<ExpressionNode> valueNodes;
    private final PainlessConstructor constructor;
    private final PainlessMethod putMethod;

    public MapInitializationNode(Location location, List<ExpressionNode> keyNodes,
                                 List<ExpressionNode> valueNodes, PainlessConstructor constructor,
                                 PainlessMethod putMethod, Class<?> expressionType) {
        super(location, expressionType);
        this.keyNodes = List.copyOf(keyNodes);
        this.valueNodes = List.copyOf(valueNodes);
        this.constructor = constructor;
        this.putMethod = putMethod;
    }

    public List<ExpressionNode> getKeyNodes() { return keyNodes; }
    public List<ExpressionNode> getValueNodes() { return valueNodes; }
    public PainlessConstructor getConstructor() { return constructor; }
    public PainlessMethod getPutMethod() { return putMethod; }

    public MapInitializationNode withKeyNodes(List<ExpressionNode> keyNodes) {
        return new MapInitializationNode(getLocation(), keyNodes, valueNodes, constructor, putMethod, getExpressionType());
    }

    public MapInitializationNode withValueNodes(List<ExpressionNode> valueNodes) {
        return new MapInitializationNode(getLocation(), keyNodes, valueNodes, constructor, putMethod, getExpressionType());
    }

    public MapInitializationNode withExpressionType(Class<?> expressionType) {
        return new MapInitializationNode(getLocation(), keyNodes, valueNodes, constructor, putMethod, expressionType);
    }

    public MapInitializationNode withConstructor(PainlessConstructor constructor) {
        return new MapInitializationNode(getLocation(), keyNodes, valueNodes, constructor, putMethod, getExpressionType());
    }

    public MapInitializationNode withPutMethod(PainlessMethod putMethod) {
        return new MapInitializationNode(getLocation(), keyNodes, valueNodes, constructor, putMethod, getExpressionType());
    }
}
