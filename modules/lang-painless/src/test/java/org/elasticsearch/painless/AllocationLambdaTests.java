/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.painless;

import org.elasticsearch.painless.spi.PainlessTestScript;
import org.elasticsearch.painless.spi.Whitelist;
import org.elasticsearch.painless.spi.WhitelistLoader;
import org.elasticsearch.script.ScriptContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Allocation tracking for lambdas and method references (PR 8). The test context ({@link PainlessTestScript}) does not
 * support cancellation, so a static lambda / method reference has no reachable script pointer of its own; before this
 * change allocations reached through them leaked uncharged. These tests confirm:
 * <ul>
 *   <li>allocations inside a static lambda body are charged (a synthetic {@code #scriptThis} capture is injected),</li>
 *   <li>constructor references to {@code @allocates} targets are charged per invocation,</li>
 *   <li>static-method references to {@code @allocates} targets are charged per invocation,</li>
 * </ul>
 * while ordinary bounded lambdas/references still run to completion. Instance-method references are covered separately.
 */
public class AllocationLambdaTests extends AllocationTestCase {

    @Override
    protected Map<ScriptContext<?>, List<Whitelist>> scriptContexts() {
        // Add the shared @allocates test allowlist (AllocationEstimatorTestObject) so static-method references can target a
        // controlled, well-behaved estimator on top of the base allowlist used for constructor references.
        Map<ScriptContext<?>, List<Whitelist>> contexts = new HashMap<>();
        List<Whitelist> whitelists = new ArrayList<>(PAINLESS_BASE_WHITELIST);
        whitelists.add(WhitelistLoader.loadFromResourceFiles(PainlessPlugin.class, "org.elasticsearch.painless.allocation-estimator"));
        contexts.put(PainlessTestScript.CONTEXT, whitelists);
        return contexts;
    }

    public void testStaticLambdaBodyArrayAllocationTrips() {
        // A static lambda (no user captures), invoked because the Optional is empty. The 1kb limit survives building the
        // lambda instance but the body's array allocation, charged only because #scriptThis is now injected, trips.
        assertTripsLimit("return Optional.empty().orElseGet(() -> { return new int[1000000]; });", "1kb");
    }

    public void testStaticLambdaBodyAllocationCounted() {
        // The body allocation lands in the running counter, proving the static lambda body reaches the script instance.
        long bytes = allocatedBytes("Optional.empty().orElseGet(() -> { return new int[100]; }); return null;");
        assertTrue("expected the static lambda body allocation to be counted, but only [" + bytes + "] bytes charged", bytes >= 400);
    }

    public void testBoundedStaticLambdaCompletes() {
        // A static lambda whose body allocates a small, bounded amount runs to completion well under the limit.
        Object result = compile("int[] a = (int[]) Optional.empty().orElseGet(() -> { return new int[4]; }); return a.length;", "1mb")
            .execute();
        assertEquals(4, result);
    }

    public void testConstructorReferenceChargedPerInvocation() {
        // ArrayList::new is an @allocates-annotated no-arg constructor. Each Supplier.get() allocates a fresh list; the
        // per-invocation charge accumulates across the loop and trips long before the loop could exhaust the heap.
        assertTripsLimit("int c(Supplier s) { for (int i = 0; i < 1000000; ++i) { s.get(); } return 1; } return c(ArrayList::new);", "1mb");
    }

    public void testStaticMethodReferenceTripsInSingleCall() {
        // AllocationEstimatorTestObject::staticAllocating is annotated with an estimator returning 16 * n. Invoked once with
        // a large argument its charge alone exceeds the limit, proving the static-method reference is charged per invocation.
        assertTripsLimit(
            "int c(IntUnaryOperator op) { return op.applyAsInt(1000000); } return c(AllocationEstimatorTestObject::staticAllocating);",
            "1mb"
        );
    }

    public void testStaticMethodReferenceCounted() {
        // Two invocations of the static-method reference charge 16 * n each; the counter reflects both (plus the small
        // capture-object cost), proving the estimator runs with the actual argument on every invocation.
        long bytes = allocatedBytes(
            "int c(IntUnaryOperator op) { return op.applyAsInt(10) + op.applyAsInt(20); } "
                + "c(AllocationEstimatorTestObject::staticAllocating); return null;"
        );
        assertTrue("expected per-invocation static-method-reference charges to be counted, but only [" + bytes + "] charged", bytes >= 480);
    }

    public void testBoundedConstructorReferenceCompletes() {
        // A single constructor-reference invocation stays far under the limit and returns normally.
        Object result = compile("int c(Supplier s) { return ((List) s.get()).size(); } return c(ArrayList::new);", "1mb").execute();
        assertEquals(0, result);
    }

    public void testInstanceMethodReferenceTrips() {
        // An unbound instance-method reference: the receiver is the first functional-interface argument. Its estimator
        // returns a huge value (sanitized to trip), proving the unbound instance reference is charged per invocation.
        assertTripsLimit(
            "int c(ToIntFunction f) { return f.applyAsInt(new AllocationEstimatorTestObject()); } "
                + "return c(AllocationEstimatorTestObject::hugeAllocatingInstance);",
            "1mb"
        );
    }

    public void testInstanceMethodReferenceCounted() {
        // The estimator sees the receiver on each invocation; constantAllocating charges 48 per call, twice here.
        long bytes = allocatedBytes(
            "int c(ToIntFunction f) { AllocationEstimatorTestObject o = new AllocationEstimatorTestObject(); "
                + "return f.applyAsInt(o) + f.applyAsInt(o); } "
                + "c(AllocationEstimatorTestObject::constantAllocating); return null;"
        );
        assertTrue(
            "expected per-invocation instance-method-reference charges to be counted, but only [" + bytes + "] charged",
            bytes >= 96
        );
    }
}
