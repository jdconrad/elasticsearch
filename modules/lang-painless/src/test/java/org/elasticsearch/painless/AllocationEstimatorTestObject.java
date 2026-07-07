/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.painless;

/**
 * Support class for {@code @allocates_dynamic} tests: whitelisted methods paired with deliberately misbehaving estimators (see
 * the {@code org.elasticsearch.painless.allocation-estimator*} test whitelist resources). Resolving these estimators by FQCN
 * from a whitelist file also covers the plugin-style path where the estimator lives outside the Painless module's own sources.
 */
public class AllocationEstimatorTestObject {

    /** Whitelisted with an estimator that misbehaves by returning a negative size. */
    public static int negativeEstimated() {
        return 1;
    }

    /** Whitelisted with an estimator that signals "definitely over any limit". */
    public static int hugeEstimated() {
        return 2;
    }

    /** Estimator for {@link #negativeEstimated()}: buggy on purpose; sanitization must substitute the conservative fallback. */
    public static long negativeEstimate() {
        return -1;
    }

    /** Estimator for {@link #hugeEstimated()}: {@code Long.MAX_VALUE} must trip any configurable limit without overflowing. */
    public static long hugeEstimate() {
        return Long.MAX_VALUE;
    }

    /** Estimator with a non-{@code long} return type; referencing it from a whitelist must fail at load time. */
    public static int notLongEstimate() {
        return 0;
    }

    /** Augmentation surfaced as {@code String.augmentedEstimated(int)}; the receiver is the leading Java parameter. */
    public static int augmentedEstimated(String receiver, int n) {
        return n;
    }

    /**
     * Estimator for {@link #augmentedEstimated}: matches the augmentation's underlying Java static signature (receiver first),
     * not the Painless surface signature. Returns a value derived from both parameters so tests can prove the estimator saw
     * the receiver and the argument.
     */
    public static long augmentedEstimate(String receiver, int n) {
        return receiver.length() * 100L + n;
    }
}
