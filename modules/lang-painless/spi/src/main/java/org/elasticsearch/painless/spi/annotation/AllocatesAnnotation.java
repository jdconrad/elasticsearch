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
 * Marks an allowlisted constructor or method that allocates heap, charged against the per-context allocation limit before the
 * call executes. The declared cost is the total allocation attributable to the call, including transitive JDK-internal
 * allocations. Two forms:
 * <ul>
 *   <li><b>constant</b> — {@code @allocates[bytes="40b"]}: a fixed {@link org.elasticsearch.common.unit.ByteSizeValue} size
 *   (a unit is required except for {@code "0"}, which is a valid no-op meaning "audited: does not allocate").</li>
 *   <li><b>dynamic</b> — {@code @allocates[class="fully.qualified.Class", method="estimate"]}: an <i>estimator</i>, a
 *   {@code public static long} method taking the target's full Java signature (receiver first for instance methods), invoked at
 *   runtime and its result charged. The estimator is resolved at allowlist load time through the annotated class's class loader
 *   so plugins may ship their own.</li>
 * </ul>
 * Exactly one form is present: {@link #isConstant()} distinguishes them ({@code bytes} is {@code -1} for the dynamic form).
 *
 * @param bytes the fixed allocation size for the constant form, or {@code -1} for the dynamic form
 * @param estimatorClassName fully-qualified binary class name declaring the estimator (dynamic form), else {@code null}
 * @param estimatorMethodName name of the estimator method (dynamic form), else {@code null}
 */
public record AllocatesAnnotation(long bytes, String estimatorClassName, String estimatorMethodName) {

    public static final String NAME = "allocates";

    /** A constant, fixed-size allocation of {@code bytes}. */
    public static AllocatesAnnotation constant(long bytes) {
        return new AllocatesAnnotation(bytes, null, null);
    }

    /** A dynamic allocation sized at runtime by the named estimator method. */
    public static AllocatesAnnotation dynamic(String estimatorClassName, String estimatorMethodName) {
        return new AllocatesAnnotation(-1L, estimatorClassName, estimatorMethodName);
    }

    /** True for the constant form (a fixed {@link #bytes()} size); false for the dynamic (estimator) form. */
    public boolean isConstant() {
        return estimatorClassName == null;
    }
}
