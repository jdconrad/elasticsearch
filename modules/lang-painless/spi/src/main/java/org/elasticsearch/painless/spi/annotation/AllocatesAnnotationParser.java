/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.painless.spi.annotation;

import org.elasticsearch.ElasticsearchParseException;
import org.elasticsearch.common.unit.ByteSizeValue;

import java.util.Map;

/**
 * Parses {@code @allocates}: {@code [bytes="40b"]} (non-negative {@link ByteSizeValue}, unit required except {@code "0"}) xor
 * {@code [class=…, method=…]} (shape only; the estimator is resolved at allowlist load time).
 */
public class AllocatesAnnotationParser implements WhitelistAnnotationParser {

    public static final String BYTES = "bytes";
    public static final String CLASS = "class";
    public static final String METHOD = "method";

    public static final AllocatesAnnotationParser INSTANCE = new AllocatesAnnotationParser();

    private AllocatesAnnotationParser() {}

    @Override
    public Object parse(Map<String, String> arguments) {
        boolean hasBytes = arguments.containsKey(BYTES);
        boolean hasClass = arguments.containsKey(CLASS);
        boolean hasMethod = arguments.containsKey(METHOD);

        if (hasBytes) {
            if (arguments.size() != 1) {
                throw new IllegalArgumentException(
                    "[@" + AllocatesAnnotation.NAME + "] [" + BYTES + "] cannot be combined with other arguments"
                );
            }
            return AllocatesAnnotation.constant(parseBytes(arguments.get(BYTES).trim()));
        }

        if (hasClass || hasMethod) {
            if (arguments.size() != 2 || hasClass == false || hasMethod == false) {
                throw new IllegalArgumentException(
                    "[@" + AllocatesAnnotation.NAME + "] dynamic form requires [" + CLASS + "] and [" + METHOD + "] arguments"
                );
            }

            String className = arguments.get(CLASS).trim();
            String methodName = arguments.get(METHOD).trim();

            if (className.isEmpty()) {
                throw new IllegalArgumentException("[@" + AllocatesAnnotation.NAME + "] [" + CLASS + "] must not be empty");
            }
            if (methodName.isEmpty()) {
                throw new IllegalArgumentException("[@" + AllocatesAnnotation.NAME + "] [" + METHOD + "] must not be empty");
            }

            return AllocatesAnnotation.dynamic(className, methodName);
        }

        throw new IllegalArgumentException(
            "[@" + AllocatesAnnotation.NAME + "] requires either [" + BYTES + "] or [" + CLASS + "] and [" + METHOD + "] arguments"
        );
    }

    private static long parseBytes(String value) {
        long bytes;
        try {
            bytes = ByteSizeValue.parseBytesSizeValue(value, "[@" + AllocatesAnnotation.NAME + "] [" + BYTES + "]").getBytes();
        } catch (ElasticsearchParseException epe) {
            throw new IllegalArgumentException(
                "[@" + AllocatesAnnotation.NAME + "] [" + BYTES + "] argument must be a byte size value [" + value + "]",
                epe
            );
        }

        if (bytes < 0) {
            throw new IllegalArgumentException(
                "[@" + AllocatesAnnotation.NAME + "] [" + BYTES + "] argument must not be negative [" + value + "]"
            );
        }

        return bytes;
    }
}
