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
 * Marks a whitelisted constructor or method whose call allocates a fixed, compile-time-known number of {@code bytes} on the
 * heap. When per-context allocation tracking is enabled, the Painless compiler charges {@code bytes} against the running total
 * and trips the limit before the annotated call executes.
 * <p>
 * The declared cost is the <b>total</b> heap allocation attributable to the call, including any transitive allocations the JDK
 * makes internally (e.g. a collection's backing array), not merely the returned object's own header. Use this for allocations
 * whose size does not depend on the call arguments; use {@link AllocatesDynamicAnnotation} when the size is argument-dependent.
 * <p>
 * A value of {@code 0} is a valid no-op ("audited: does not allocate") and emits no pre-check. Negative values are rejected at
 * whitelist load time.
 */
public record AllocatesConstantAnnotation(long bytes) {

    public static final String NAME = "allocates_constant";
}
