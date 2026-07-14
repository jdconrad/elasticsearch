/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.painless;

import org.elasticsearch.painless.spi.annotation.AllocatesAnnotation;
import org.elasticsearch.painless.spi.annotation.AllocatesAnnotationParser;
import org.elasticsearch.test.ESTestCase;

import java.util.Map;

/** Unit tests for the {@code @allocates} allowlist annotation parser (constant and dynamic forms). */
public class AllocatesAnnotationParserTests extends ESTestCase {

    private static final AllocatesAnnotationParser ALLOCATES = AllocatesAnnotationParser.INSTANCE;

    private static AllocatesAnnotation parse(Map<String, String> arguments) {
        return (AllocatesAnnotation) ALLOCATES.parse(arguments);
    }

    public void testConstantParsesBytes() {
        AllocatesAnnotation annotation = parse(Map.of(AllocatesAnnotationParser.BYTES, "40b"));
        assertTrue(annotation.isConstant());
        assertEquals(40L, annotation.bytes());
    }

    public void testConstantParsesUnits() {
        assertEquals(1024L, parse(Map.of(AllocatesAnnotationParser.BYTES, "1kb")).bytes());
    }

    public void testConstantAcceptsZero() {
        // @allocates[bytes="0"] is a valid no-op ("audited: does not allocate").
        assertEquals(0L, parse(Map.of(AllocatesAnnotationParser.BYTES, "0")).bytes());
    }

    public void testConstantRejectsMissingUnits() {
        // ByteSizeValue requires a unit for anything but "0"; a bare number must not parse.
        expectThrows(IllegalArgumentException.class, () -> parse(Map.of(AllocatesAnnotationParser.BYTES, "40")));
    }

    public void testConstantRejectsNegative() {
        IllegalArgumentException e = expectThrows(
            IllegalArgumentException.class,
            () -> parse(Map.of(AllocatesAnnotationParser.BYTES, "-1"))
        );
        assertTrue(e.getMessage(), e.getMessage().contains("must not be negative"));
    }

    public void testConstantRejectsNonNumeric() {
        expectThrows(IllegalArgumentException.class, () -> parse(Map.of(AllocatesAnnotationParser.BYTES, "big")));
    }

    public void testRejectsMissingArguments() {
        expectThrows(IllegalArgumentException.class, () -> parse(Map.of()));
    }

    public void testRejectsUnknownArgument() {
        expectThrows(IllegalArgumentException.class, () -> parse(Map.of("size", "40")));
    }

    public void testDynamicParsesClassAndMethod() {
        AllocatesAnnotation annotation = parse(
            Map.of(AllocatesAnnotationParser.CLASS, "com.example.Foo", AllocatesAnnotationParser.METHOD, "estimate")
        );
        assertFalse(annotation.isConstant());
        assertEquals("com.example.Foo", annotation.estimatorClassName());
        assertEquals("estimate", annotation.estimatorMethodName());
    }

    public void testDynamicAcceptsInnerClassDollarForm() {
        AllocatesAnnotation annotation = parse(
            Map.of(AllocatesAnnotationParser.CLASS, "com.example.Outer$Inner", AllocatesAnnotationParser.METHOD, "estimate")
        );
        assertEquals("com.example.Outer$Inner", annotation.estimatorClassName());
    }

    public void testDynamicRejectsMissingClass() {
        expectThrows(IllegalArgumentException.class, () -> parse(Map.of(AllocatesAnnotationParser.METHOD, "estimate")));
    }

    public void testDynamicRejectsMissingMethod() {
        expectThrows(IllegalArgumentException.class, () -> parse(Map.of(AllocatesAnnotationParser.CLASS, "com.example.Foo")));
    }

    public void testDynamicRejectsEmptyValues() {
        expectThrows(
            IllegalArgumentException.class,
            () -> parse(Map.of(AllocatesAnnotationParser.CLASS, " ", AllocatesAnnotationParser.METHOD, "estimate"))
        );
        expectThrows(
            IllegalArgumentException.class,
            () -> parse(Map.of(AllocatesAnnotationParser.CLASS, "com.example.Foo", AllocatesAnnotationParser.METHOD, " "))
        );
    }

    public void testRejectsMixingBytesWithEstimator() {
        // The two forms are mutually exclusive.
        expectThrows(
            IllegalArgumentException.class,
            () -> parse(Map.of(AllocatesAnnotationParser.BYTES, "40b", AllocatesAnnotationParser.CLASS, "com.example.Foo"))
        );
    }

    public void testDynamicRejectsUnknownArgument() {
        expectThrows(
            IllegalArgumentException.class,
            () -> parse(
                Map.of(AllocatesAnnotationParser.CLASS, "com.example.Foo", AllocatesAnnotationParser.METHOD, "estimate", "extra", "nope")
            )
        );
    }
}
