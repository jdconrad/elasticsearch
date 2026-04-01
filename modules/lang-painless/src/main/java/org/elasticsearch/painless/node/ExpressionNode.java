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

/**
 * Base class for all nodes that produce a value. Expression node handlers in
 * {@link org.elasticsearch.painless.phase.TreeTransformer} must return an
 * {@code ExpressionNode}, enforced at compile time via typed {@code on()} overloads.
 */
public abstract class ExpressionNode extends Node {

    public ExpressionNode(Location location) {
        super(location);
    }
}
