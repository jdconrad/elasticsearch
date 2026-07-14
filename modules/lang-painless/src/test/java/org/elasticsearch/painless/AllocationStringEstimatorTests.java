/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.painless;

import java.util.Locale;

/**
 * End-to-end tests for the {@code String} {@code @allocates_dynamic} estimators (concat, substring, toCharArray, case mapping,
 * trim): each charges its result's byte cost, computed from the receiver/argument lengths, before the allocating call runs.
 * String literals in the scripts are constant-pool loads and are not charged, so the observed total is the method's alone.
 */
public class AllocationStringEstimatorTests extends AllocationTestCase {

    public void testConcatCharged() {
        assertEquals(
            AllocationEstimators.concatBytes("hello", "world"),
            allocatedBytes("String s = \"hello\"; s.concat(\"world\"); return \"x\";")
        );
    }

    public void testSubstringFromCharged() {
        assertEquals(
            AllocationEstimators.substringBytes("hello world", 6),
            allocatedBytes("String s = \"hello world\"; s.substring(6); return \"x\";")
        );
    }

    public void testToCharArrayCharged() {
        assertEquals(
            AllocationEstimators.toCharArrayBytes("hello"),
            allocatedBytes("String s = \"hello\"; s.toCharArray(); return \"x\";")
        );
    }

    public void testToLowerCaseCharged() {
        assertEquals(AllocationEstimators.recaseBytes("HELLO"), allocatedBytes("String s = \"HELLO\"; s.toLowerCase(); return \"x\";"));
    }

    public void testToUpperCaseLocaleCharged() {
        // Exercises the (String, Locale) estimator overload resolving against the toUpperCase(Locale) signature.
        assertEquals(
            AllocationEstimators.recaseBytes("hello", Locale.ROOT),
            allocatedBytes("String s = \"hello\"; s.toUpperCase(Locale.ROOT); return \"x\";")
        );
    }

    public void testTrimCharged() {
        assertEquals(AllocationEstimators.recaseBytes("  hi  "), allocatedBytes("String s = \"  hi  \"; s.trim(); return \"x\";"));
    }

    public void testConcatTripsLimit() {
        assertTripsLimit("String s = \"hello\"; s.concat(\"world\"); return \"x\";");
    }

    public void testToCharArrayTripsLimit() {
        assertTripsLimit("String s = \"hello\"; s.toCharArray(); return \"x\";");
    }

    // Generic estimators reused by x-pack SQL string functions (repeat/space/substring/left/right). No base allowlist method
    // uses them, so they are exercised directly; the x-pack SQL whitelist resolution is validated in SQL CI.

    public void testStringRepeatBytes() {
        // "ab" x 5 = a 10-char String; null value or non-positive count charges just the overhead.
        assertEquals(AllocationEstimators.substringBytes("", 0, 10), AllocationEstimators.stringRepeatBytes("ab", 5));
        assertEquals(AllocationEstimators.substringBytes("", 0, 0), AllocationEstimators.stringRepeatBytes(null, 5));
        assertEquals(AllocationEstimators.substringBytes("", 0, 0), AllocationEstimators.stringRepeatBytes("ab", -1));
    }

    public void testStringOfCountBytes() {
        assertEquals(AllocationEstimators.substringBytes("", 0, 7), AllocationEstimators.stringOfCountBytes(7));
        assertEquals(AllocationEstimators.substringBytes("", 0, 0), AllocationEstimators.stringOfCountBytes(null));
    }

    public void testBoundedStringBytes() {
        // Full-length bound of the source across the (String), (String, Number), and (String, Number, Number) overloads.
        long expected = AllocationEstimators.substringBytes("", 0, 5);
        assertEquals(expected, AllocationEstimators.boundedStringBytes("hello"));
        assertEquals(expected, AllocationEstimators.boundedStringBytes("hello", 2));
        assertEquals(expected, AllocationEstimators.boundedStringBytes("hello", 1, 4));
    }
}
