/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.painless;

import org.elasticsearch.painless.lookup.PainlessLookupBuilder;
import org.elasticsearch.painless.spi.PainlessTestScript;
import org.elasticsearch.painless.spi.Whitelist;
import org.elasticsearch.painless.spi.WhitelistLoader;
import org.elasticsearch.script.ScriptContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;

/**
 * End-to-end tests for PR 6 {@code @allocates_dynamic} pre-checks: the estimator named in the whitelist annotation is invoked
 * with a replay of the call's operands and its (sanitized) result is charged before the allocating call executes. Covers the
 * built-in estimators on {@code String.substring(int,int)} and {@code new ArrayList(Collection)}, misbehaving-estimator
 * sanitization via test-only whitelisted methods, and load-time failures for badly declared estimators.
 */
public class AllocationEstimatorTests extends AllocationTestCase {

    @Override
    protected Map<ScriptContext<?>, List<Whitelist>> scriptContexts() {
        Map<ScriptContext<?>, List<Whitelist>> contexts = new HashMap<>();
        List<Whitelist> whitelists = new ArrayList<>(PAINLESS_BASE_WHITELIST);
        whitelists.add(WhitelistLoader.loadFromResourceFiles(PainlessPlugin.class, "org.elasticsearch.painless.allocation-estimator"));
        contexts.put(PainlessTestScript.CONTEXT, whitelists);
        return contexts;
    }

    public void testSubstringChargedFromArguments() {
        // The estimator sees the receiver and both indices: 5 chars at 2 bytes each plus the result overhead.
        long expected = AllocationEstimators.substringBytes("hello world", 0, 5);
        assertEquals(expected, allocatedBytes("String s = \"hello world\"; s.substring(0, 5); return \"x\";"));
    }

    public void testSubstringChargeVariesWithArguments() {
        // Same method, different arguments, different charge — the point of the dynamic form.
        long expected = AllocationEstimators.substringBytes("hello world", 0, 11);
        assertEquals(expected, allocatedBytes("String s = \"hello world\"; s.substring(0, 11); return \"x\";"));
    }

    public void testSubstringTripsLimit() {
        assertTripsLimit("String s = \"hello world\"; s.substring(0, 5); return \"x\";");
    }

    public void testConstructorChargedFromArguments() {
        // Inner new ArrayList() charges its constant 40; the outer copy constructor charges shell + backing array via the
        // estimator (empty source, so just the shell and an empty-array header).
        long expected = 40L + AllocationEstimators.arrayListCollectionBytes(new ArrayList<>());
        assertEquals(expected, allocatedBytes("new ArrayList(new ArrayList()); return \"x\";"));
    }

    public void testConstructorArgumentsEvaluatedExactlyOnce() {
        // The dynamic-constructor emission reorders evaluation (args before NEW); side effects must still happen exactly once.
        PainlessTestScript script = compile(
            "List once(List l) { l.add(1); return l; } List src = new ArrayList(); new ArrayList(once(src)); return src.size();",
            "1mb"
        );
        assertEquals(1, script.execute());
    }

    public void testNegativeEstimateFallsBackConservatively() {
        // A buggy estimator returning a negative size charges the conservative fallback instead.
        assertEquals(
            AllocationGuard.ESTIMATE_FALLBACK_BYTES,
            allocatedBytes("AllocationEstimatorTestObject.negativeEstimated(); return \"x\";")
        );
    }

    public void testHugeEstimateTripsAnyLimit() {
        // Long.MAX_VALUE from an estimator must trip even a roomy limit (and not overflow the running total).
        PainlessTestScript script = compile("AllocationEstimatorTestObject.hugeEstimated(); return \"x\";", "1mb");
        Exception e = expectThrows(Exception.class, script::execute);
        for (Throwable t = e; t != null; t = t.getCause()) {
            if (t.getMessage() != null && t.getMessage().contains("allocation limit exceeded")) {
                return;
            }
        }
        throw new AssertionError("expected an allocation limit error, but got: " + e, e);
    }

    public void testAugmentedMethodEstimatorSeesReceiverFirst() {
        // The augmentation's estimator matches the underlying Java static signature: (String receiver, int n). The charge
        // proves the estimator saw both the receiver ("abc".length() == 3) and the argument.
        assertEquals(3 * 100L + 7, allocatedBytes("String s = \"abc\"; s.augmentedEstimated(7); return \"x\";"));
    }

    public void testConflictingAnnotationsAcrossWhitelistsRejected() {
        // Two whitelists (e.g. two plugins) annotating the same method differently follow the existing duplicate-entry rule:
        // the entries are not equivalent, so loading fails.
        IllegalArgumentException e = expectThrows(IllegalArgumentException.class, () -> {
            List<Whitelist> whitelists = new ArrayList<>(PAINLESS_BASE_WHITELIST);
            whitelists.add(WhitelistLoader.loadFromResourceFiles(PainlessPlugin.class, "org.elasticsearch.painless.allocation-estimator"));
            whitelists.add(
                WhitelistLoader.loadFromResourceFiles(PainlessPlugin.class, "org.elasticsearch.painless.allocation-estimator-conflict")
            );
            PainlessLookupBuilder.buildFromWhitelists(whitelists, new HashMap<>(), new HashMap<>());
        });
        assertThat(e.getCause().getMessage(), containsString("cannot add methods with the same name and arity"));
    }

    public void testMissingEstimatorClassFailsAtLoadTime() {
        IllegalArgumentException e = expectThrows(
            IllegalArgumentException.class,
            () -> loadTestWhitelist("org.elasticsearch.painless.allocation-estimator-missing-class")
        );
        assertThat(e.getCause().getMessage(), containsString("estimator class [org.elasticsearch.painless.DoesNotExist] not found"));
    }

    public void testMissingEstimatorMethodFailsAtLoadTime() {
        IllegalArgumentException e = expectThrows(
            IllegalArgumentException.class,
            () -> loadTestWhitelist("org.elasticsearch.painless.allocation-estimator-missing-method")
        );
        assertThat(e.getCause().getMessage(), containsString("#nope"));
        assertThat(e.getCause().getMessage(), containsString("not found"));
    }

    public void testNonLongEstimatorFailsAtLoadTime() {
        IllegalArgumentException e = expectThrows(
            IllegalArgumentException.class,
            () -> loadTestWhitelist("org.elasticsearch.painless.allocation-estimator-not-long")
        );
        assertThat(e.getCause().getMessage(), containsString("must be public static and return long"));
    }

    public void testBothAnnotationsRejectedAtLoadTime() {
        IllegalArgumentException e = expectThrows(
            IllegalArgumentException.class,
            () -> loadTestWhitelist("org.elasticsearch.painless.allocation-estimator-both-annotations")
        );
        assertThat(e.getCause().getMessage(), containsString("cannot use both [@allocates_constant] and [@allocates_dynamic]"));
    }

    private static void loadTestWhitelist(String resource) {
        // Load alongside the base whitelist, as a plugin whitelist would be, so common type names resolve.
        List<Whitelist> whitelists = new ArrayList<>(PAINLESS_BASE_WHITELIST);
        whitelists.add(WhitelistLoader.loadFromResourceFiles(PainlessPlugin.class, resource));
        PainlessLookupBuilder.buildFromWhitelists(whitelists, new HashMap<>(), new HashMap<>());
    }
}
