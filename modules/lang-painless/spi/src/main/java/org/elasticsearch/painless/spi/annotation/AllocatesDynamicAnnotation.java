/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.painless.spi.annotation;

/**
 * Marks a whitelisted constructor or method whose call allocates an argument-dependent number of bytes, sized at runtime by an
 * <i>estimator</i>: a {@code public static} method returning {@code long} that takes the same parameter types as the annotated
 * target (for an instance method, the receiver is the estimator's first parameter). When per-context allocation tracking is
 * enabled, the compiler invokes the estimator and charges its result before the annotated call executes.
 * <p>
 * The estimator is named by a fully-qualified {@code Class#method} reference resolved at whitelist load time against the same
 * class loader as the annotated class, so plugins may ship their own estimators. Inner classes use the JVM {@code $} form
 * (e.g. {@code com.example.Outer$Inner#estimate}). The estimator's returned cost is the <b>total</b> heap allocation
 * attributable to the call, including transitive JDK-internal allocations.
 *
 * @param estimatorClassName fully-qualified binary class name declaring the estimator (JVM {@code $} form for inner classes)
 * @param estimatorMethodName name of the {@code public static long} estimator method
 */
public record AllocatesDynamicAnnotation(String estimatorClassName, String estimatorMethodName) {

    public static final String NAME = "allocates_dynamic";
}
