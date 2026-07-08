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
import org.elasticsearch.script.ScriptException;

/**
 * End-to-end allocation-limit tests using scripts shaped like ones a user might actually write, run under realistic per-context
 * limits (not the {@code 1b} smoke-test limit). These exercise the whole feature: a runaway allocation is preempted before it
 * can OOM the node, while an ordinary bounded script runs to completion without being falsely tripped.
 */
public class AllocationRealisticScriptTests extends AllocationTestCase {

    private void assertTripsLimit(String source, String limit) {
        PainlessTestScript script = compile(source, limit);
        ScriptException e = expectThrows(ScriptException.class, script::execute);
        for (Throwable t = e; t != null; t = t.getCause()) {
            if (t.getMessage() != null && t.getMessage().contains("allocation limit exceeded")) {
                return;
            }
        }
        throw new AssertionError("expected an allocation limit error for [" + source + "] under [" + limit + "], but got: " + e, e);
    }

    public void testRunawayStringConcatDoublingIsPreempted() {
        // The classic accidental OOM: repeatedly doubling a string. The static-type concat pre-check charges each result and
        // trips within a couple dozen iterations, long before the loop's 1000 rounds (or the node's heap) are reached.
        assertTripsLimit("""
            String s = "wat";
            for (int i = 0; i < 1000; ++i) {
                s = s + s;
            }
            return s;
            """, "1mb");
    }

    public void testRunawayDefMethodAllocationIsPreempted() {
        // A def-typed value sliced in a loop: each def-dispatched substring charges against the limit, so an unbounded loop is
        // preempted rather than churning allocations forever.
        assertTripsLimit("""
            def s = "a fairly long-ish string value to slice";
            for (int i = 0; i < 1000000; ++i) {
                s.substring(0, 20);
            }
            return "done";
            """, "1mb");
    }

    public void testRunawayArrayAllocationIsPreempted() {
        // Repeatedly allocating arrays in a loop trips before exhausting the heap.
        assertTripsLimit("""
            for (int i = 0; i < 1000000; ++i) {
                int[] buffer = new int[100000];
            }
            return "done";
            """, "64mb");
    }

    public void testOrdinaryBoundedScriptRunsUnderGenerousLimit() {
        // A realistic, bounded script (slice a value a fixed number of times) must complete without being falsely tripped, and
        // the running total must reflect exactly the charged def-dispatched allocations.
        long perCall = AllocationEstimators.substringBytes("hello world", 0, 5);
        PainlessTestScript script = compile("""
            def s = "hello world";
            for (int i = 0; i < 100; ++i) {
                s.substring(0, 5);
            }
            return "done";
            """, "64mb");
        assertEquals("done", script.execute());
        assertEquals(100 * perCall, ((PainlessScript) script).getAllocBytes());
    }
}
