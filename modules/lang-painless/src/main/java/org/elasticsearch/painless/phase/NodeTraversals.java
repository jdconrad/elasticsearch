/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.painless.phase;

import org.elasticsearch.painless.node.Node;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Registry of traversal functions for every concrete {@link Node} type. A traversal
 * function rebuilds a node with its children replaced by the results of transforming
 * them through a {@link TreeTransformer}. When no children change (determined by
 * reference equality), the original node is returned as-is, enabling structural sharing
 * across tree rebuilds.
 *
 * <p>Every concrete node type used in the compiler must be registered here before
 * compilation begins. {@code CoreNodeRegistrar} registers all built-in node types.
 * Consumers add custom node types via the {@code Compiler.Builder#nodes} method.
 *
 * <p>This class is not thread-safe during registration. After all nodes are registered
 * (i.e., after the Compiler is built), instances are effectively immutable and safe
 * for concurrent use.
 */
public class NodeTraversals {

    private final Map<Class<? extends Node>, BiFunction<Node, TreeTransformer, Node>> registry = new HashMap<>();

    /**
     * Register a traversal function for the given node type. The function receives a
     * typed node and the active {@link TreeTransformer}, and must return a node of a
     * compatible type. For leaf nodes with no children, register {@code (node, t) -> node}.
     *
     * <p>Registering a type that already has a traversal replaces the previous registration.
     */
    public <T extends Node> NodeTraversals register(Class<T> type, BiFunction<T, TreeTransformer, Node> fn) {
        registry.put(type, (node, t) -> fn.apply(type.cast(node), t));
        return this;
    }

    /**
     * Invoke the registered traversal for the given node. Throws if no traversal has
     * been registered for the node's exact runtime class.
     */
    Node traverse(Node node, TreeTransformer transformer) {
        var fn = registry.get(node.getClass());
        if (fn == null) {
            throw new IllegalStateException(
                "No traversal registered for node type [" + node.getClass().getName() + "]. "
                    + "Register it via NodeTraversals.register() before building the Compiler."
            );
        }
        return fn.apply(node, transformer);
    }
}
