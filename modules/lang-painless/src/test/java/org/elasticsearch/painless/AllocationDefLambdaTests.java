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
 * Allocation tracking for {@code def}-typed lambdas and method references (PR 8.5). When the functional-interface target is
 * unknown at compile time (a lambda/reference passed to or resolved through {@code def}) it routes through the runtime
 * reference path, which PR 8 did not cover. Calling a method on a {@code def} receiver makes the lambda argument's target
 * {@code def}. These tests confirm allocations inside a def static lambda body are charged.
 */
public class AllocationDefLambdaTests extends AllocationTestCase {

    public void testDefStaticLambdaBodyArrayAllocationTrips() {
        // opt is def, so orElseGet is a def call and the lambda's target is def; its body array allocation is charged.
        assertTripsLimit("def opt = Optional.empty(); return opt.orElseGet(() -> { return new int[1000000]; });", "1kb");
    }

    public void testDefStaticLambdaBodyAllocationCounted() {
        // The body allocation reaches the counter, proving the def static lambda body reaches the script instance.
        long bytes = allocatedBytes("def opt = Optional.empty(); opt.orElseGet(() -> { return new int[100]; }); return null;");
        assertTrue("expected the def static lambda body allocation to be counted, but only [" + bytes + "] bytes charged", bytes >= 400);
    }

    public void testBoundedDefStaticLambdaCompletes() {
        // A bounded def static lambda body runs to completion well under the limit.
        Object result = compile(
            "def opt = Optional.empty(); int[] a = (int[]) opt.orElseGet(() -> { return new int[4]; }); return a.length;",
            "1mb"
        ).execute();
        assertEquals(4, result);
    }

    public void testDefConstructorReferenceChargedPerInvocation() {
        // opt is def, so orElseGet is a def call and ArrayList::new is a def constructor reference to an annotated ctor; the
        // per-invocation charge accumulates across the loop and trips.
        assertTripsLimit(
            "def opt = Optional.empty(); for (int i = 0; i < 1000000; ++i) { opt.orElseGet(ArrayList::new); } return 1;",
            "1mb"
        );
    }

    public void testDefInstanceMethodReferenceChargedPerInvocation() {
        // opt is def, so map is a def call and String::toUpperCase is a def (unbound) instance-method reference to an
        // annotated target; each invocation charges the recase allocation, tripping across the loop.
        assertTripsLimit(
            "def opt = Optional.of('abcdefghij'); for (int i = 0; i < 1000000; ++i) { opt.map(String::toUpperCase); } return 1;",
            "1mb"
        );
    }

    public void testDefReferenceToUnannotatedTargetCompletes() {
        // A def reference whose target is not annotated is not charge-captured (pre-filter) and resolves normally.
        Object result = compile("def opt = Optional.of('hello'); return opt.map(String::length).get();", "1mb").execute();
        assertEquals(5, result);
    }

    public void testDefBoundInstanceMethodReferenceChargedPerInvocation() {
        // opt is def, so map is a def call and s::concat is a def bound instance-method reference (typed receiver) to an
        // annotated target; the concat allocation is charged per invocation, tripping across the loop.
        assertTripsLimit(
            "String s = 'abcdefghij'; def opt = Optional.of(s); for (int i = 0; i < 1000000; ++i) { opt.map(s::concat); } return 1;",
            "1mb"
        );
    }

    public void testDefReferenceNotChargedWhenTrackingOff() {
        // With tracking off, an annotated def constructor reference is not charge-captured and resolves normally.
        Object result = compile("def opt = Optional.empty(); return ((List) opt.orElseGet(ArrayList::new)).size();", "-1b").execute();
        assertEquals(0, result);
    }
}
