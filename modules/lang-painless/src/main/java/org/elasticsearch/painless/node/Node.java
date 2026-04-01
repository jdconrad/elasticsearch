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

import java.util.Objects;

/**
 * Base class for all nodes in the Painless compiler tree. Nodes are pure data —
 * they carry no traversal logic, no decoration maps, and no visitor methods.
 * All traversal is handled by {@link org.elasticsearch.painless.phase.TreeTransformer}
 * using traversal functions registered in {@link org.elasticsearch.painless.phase.NodeTraversals}.
 */
public abstract class Node {

    private final Location location;

    public Node(Location location) {
        this.location = Objects.requireNonNull(location);
    }

    /**
     * The script location of this node, used for error reporting.
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Create an error with location information pointing to this node.
     */
    public RuntimeException createError(RuntimeException exception) {
        return location.createError(exception);
    }
}
