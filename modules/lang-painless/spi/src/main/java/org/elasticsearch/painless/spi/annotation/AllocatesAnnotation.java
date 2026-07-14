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
 * Marks an allowlisted method/constructor that allocates, charged against the per-context limit before the call. Two forms,
 * exactly one present ({@link #isConstant()}): constant {@code @allocates[bytes="40b"]} (fixed size; {@code "0"} = audited
 * no-op), or dynamic {@code @allocates[class=…, method=…]} naming a {@code public static long} estimator that takes the
 * target's Java signature (receiver first) and is invoked at runtime.
 *
 * @param bytes fixed size for the constant form, else {@code -1}
 * @param estimatorClassName estimator's binary class name for the dynamic form, else {@code null}
 * @param estimatorMethodName estimator method name for the dynamic form, else {@code null}
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
