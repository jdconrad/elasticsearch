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

import java.util.List;

public final class TypedCaptureReferenceNode extends ExpressionNode {

    private final String name;
    private final List<String> captureNames;
    private final boolean captureBox;

    public TypedCaptureReferenceNode(Location location, String name, List<String> captureNames,
                                     boolean captureBox, Class<?> expressionType) {
        super(location, expressionType);
        this.name = name;
        this.captureNames = captureNames != null ? List.copyOf(captureNames) : null;
        this.captureBox = captureBox;
    }

    public String getName() { return name; }
    public List<String> getCaptureNames() { return captureNames; }
    public boolean isCaptureBox() { return captureBox; }

    public TypedCaptureReferenceNode withExpressionType(Class<?> expressionType) {
        return new TypedCaptureReferenceNode(getLocation(), name, captureNames, captureBox, expressionType);
    }
}
