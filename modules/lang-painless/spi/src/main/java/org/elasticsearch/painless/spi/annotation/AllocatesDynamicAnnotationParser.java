/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.painless.spi.annotation;

import java.util.Map;

/**
 * Parses {@code @allocates_dynamic[estimator="fully.qualified.Class#methodName"]} into an {@link AllocatesDynamicAnnotation}.
 * Validates the {@code Class#methodName} shape (exactly one {@code #}, non-empty class and method); the referenced class and
 * method are resolved later at whitelist load time so that a missing estimator fails loudly rather than silently disabling the
 * pre-check.
 */
public class AllocatesDynamicAnnotationParser implements WhitelistAnnotationParser {

    public static final String ESTIMATOR = "estimator";

    public static final AllocatesDynamicAnnotationParser INSTANCE = new AllocatesDynamicAnnotationParser();

    private AllocatesDynamicAnnotationParser() {}

    @Override
    public Object parse(Map<String, String> arguments) {
        if (arguments.size() != 1 || arguments.containsKey(ESTIMATOR) == false) {
            throw new IllegalArgumentException(
                "[@"
                    + AllocatesDynamicAnnotation.NAME
                    + "] requires a single ["
                    + ESTIMATOR
                    + "] argument of the form [fully.qualified.Class#methodName]"
            );
        }

        String estimator = arguments.get(ESTIMATOR).trim();
        int hash = estimator.indexOf('#');

        if (hash < 1 || hash != estimator.lastIndexOf('#') || hash == estimator.length() - 1) {
            throw new IllegalArgumentException(
                "[@"
                    + AllocatesDynamicAnnotation.NAME
                    + "] ["
                    + ESTIMATOR
                    + "] must be of the form [fully.qualified.Class#methodName] ["
                    + estimator
                    + "]"
            );
        }

        String className = estimator.substring(0, hash).trim();
        String methodName = estimator.substring(hash + 1).trim();

        if (className.isEmpty() || methodName.isEmpty()) {
            throw new IllegalArgumentException(
                "[@"
                    + AllocatesDynamicAnnotation.NAME
                    + "] ["
                    + ESTIMATOR
                    + "] must be of the form [fully.qualified.Class#methodName] ["
                    + estimator
                    + "]"
            );
        }

        return new AllocatesDynamicAnnotation(className, methodName);
    }
}
