/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.painless.node;

import org.elasticsearch.painless.FunctionRef;
import org.elasticsearch.painless.Location;

import java.util.List;

public final class TypedInterfaceReferenceNode extends ExpressionNode {

    private final boolean isInstanceCapture;
    private final List<String> captureNames;
    private final boolean captureBox;
    private final FunctionRef reference;

    public TypedInterfaceReferenceNode(Location location, boolean isInstanceCapture, List<String> captureNames,
                                       boolean captureBox, FunctionRef reference, Class<?> expressionType) {
        super(location, expressionType);
        this.isInstanceCapture = isInstanceCapture;
        this.captureNames = captureNames != null ? List.copyOf(captureNames) : null;
        this.captureBox = captureBox;
        this.reference = reference;
    }

    public boolean isInstanceCapture() { return isInstanceCapture; }
    public List<String> getCaptureNames() { return captureNames; }
    public boolean isCaptureBox() { return captureBox; }
    public FunctionRef getReference() { return reference; }

    public TypedInterfaceReferenceNode withExpressionType(Class<?> expressionType) {
        return new TypedInterfaceReferenceNode(getLocation(), isInstanceCapture, captureNames,
            captureBox, reference, expressionType);
    }

    public TypedInterfaceReferenceNode withReference(FunctionRef reference) {
        return new TypedInterfaceReferenceNode(getLocation(), isInstanceCapture, captureNames,
            captureBox, reference, getExpressionType());
    }
}
