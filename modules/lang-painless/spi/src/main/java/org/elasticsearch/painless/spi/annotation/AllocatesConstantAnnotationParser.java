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
 * Parses {@code @allocates_constant[bytes="N"]} into an {@link AllocatesConstantAnnotation}. Requires a single {@code bytes} argument holding a
 * non-negative {@code long}; any other shape is rejected at whitelist load time.
 */
public class AllocatesConstantAnnotationParser implements WhitelistAnnotationParser {

    public static final String BYTES = "bytes";

    public static final AllocatesConstantAnnotationParser INSTANCE = new AllocatesConstantAnnotationParser();

    private AllocatesConstantAnnotationParser() {}

    @Override
    public Object parse(Map<String, String> arguments) {
        if (arguments.size() != 1 || arguments.containsKey(BYTES) == false) {
            throw new IllegalArgumentException("[@" + AllocatesConstantAnnotation.NAME + "] requires a single [" + BYTES + "] argument");
        }

        String value = arguments.get(BYTES).trim();
        long bytes;

        try {
            bytes = Long.parseLong(value);
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException(
                "[@" + AllocatesConstantAnnotation.NAME + "] [" + BYTES + "] argument must be a long [" + value + "]",
                nfe
            );
        }

        if (bytes < 0) {
            throw new IllegalArgumentException(
                "[@" + AllocatesConstantAnnotation.NAME + "] [" + BYTES + "] argument must not be negative [" + bytes + "]"
            );
        }

        return new AllocatesConstantAnnotation(bytes);
    }
}
