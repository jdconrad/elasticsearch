/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.painless.phase;

import org.elasticsearch.painless.node.ClassNode;

/**
 * Handler for class nodes in a {@link TreeTransformer}. The return type is
 * constrained to {@link ClassNode}, enforced at compile time. Using a distinct
 * functional interface (rather than {@code Function<T, ClassNode>}) allows
 * {@link TreeTransformer#on} to be overloaded per node category without erasure clashes.
 */
@FunctionalInterface
public interface ClassHandler<T extends ClassNode> {
    ClassNode apply(T node);
}
