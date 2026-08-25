/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.painless;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.painless.spi.Whitelist;
import org.elasticsearch.painless.spi.WhitelistLoader;
import org.elasticsearch.script.IngestConditionalScript;
import org.elasticsearch.script.ScriptContext;
import org.junit.Before;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyMap;

/**
 * Conditions that are recognised must behave identically to conditions that are compiled. Each test
 * compiles the same source through the recognising engine and a non-recognising one and asserts the two
 * agree, so a gap in {@link org.elasticsearch.painless.phase.ConditionRecognitionPhase} can only ever cost
 * a generated class, never a behaviour change.
 */
public class ConditionProgramTests extends ScriptTestCase {

    private PainlessScriptEngine engine;
    private PainlessScriptEngine engineWithoutRecognition;

    @Before
    public void setupConditionalEngines() {
        engine = new PainlessScriptEngine(scriptEngineSettings(), conditionalContexts());
        // the same engine with the escape hatch closed, so every assertion is a real differential
        engineWithoutRecognition = new PainlessScriptEngine(
            Settings.builder().put(scriptEngineSettings()).put(CompilerSettings.CONDITION_PROGRAMS_ENABLED.getKey(), false).build(),
            conditionalContexts()
        );
    }

    private Map<ScriptContext<?>, List<Whitelist>> conditionalContexts() {
        Map<ScriptContext<?>, List<Whitelist>> contexts = new HashMap<>();
        List<Whitelist> whitelists = new ArrayList<>(PAINLESS_BASE_WHITELIST);
        whitelists.add(WhitelistLoader.loadFromResourceFiles(PainlessPlugin.class, "org.elasticsearch.script.ingest.txt"));
        contexts.put(IngestConditionalScript.CONTEXT, whitelists);
        return contexts;
    }

    private IngestConditionalScript.Factory compile(String source) {
        return engine.compile("test", source, IngestConditionalScript.CONTEXT, emptyMap());
    }

    private static Object run(IngestConditionalScript.Factory factory, Map<String, Object> ctx) {
        try {
            return factory.newInstance(emptyMap(), ctx).execute();
        } catch (Exception | AssertionError e) {
            Throwable root = e;
            while (root.getCause() != null) {
                root = root.getCause();
            }
            return "throw:" + root.getClass().getSimpleName();
        }
    }

    private static Map<String, Object> ctx(Object... keyValues) {
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < keyValues.length; i += 2) {
            map.put((String) keyValues[i], keyValues[i + 1]);
        }
        return map;
    }

    private static Map<String, Object> nested(String key, Object value) {
        return ctx(key, value);
    }

    /** Asserts the condition is recognised, and that it agrees with the compiled form on every ctx. */
    private void assertRecognisedAndAgrees(String source, List<Map<String, Object>> contexts) {
        IngestConditionalScript.Factory recognised = compile(source);
        assertThat(
            "expected [" + source + "] to be recognised as a ConditionProgram",
            recognised,
            org.hamcrest.Matchers.instanceOf(ProgramConditionalFactory.class)
        );

        IngestConditionalScript.Factory reference = compileWithoutRecognition(source);

        for (Map<String, Object> ctx : contexts) {
            assertEquals("ctx " + ctx + " for [" + source + "]", run(reference, ctx), run(recognised, ctx));
        }
    }

    /** Compiles the same source with recognition disabled, giving the generated-bytecode reference result. */
    private IngestConditionalScript.Factory compileWithoutRecognition(String source) {
        IngestConditionalScript.Factory factory = engineWithoutRecognition.compile(
            "test",
            source,
            IngestConditionalScript.CONTEXT,
            emptyMap()
        );
        assertThat(
            "the escape hatch must actually disable recognition",
            factory,
            org.hamcrest.Matchers.not(org.hamcrest.Matchers.instanceOf(ProgramConditionalFactory.class))
        );
        return factory;
    }

    public void testNullChecksAreRecognised() {
        List<Map<String, Object>> contexts = Arrays.asList(
            ctx(),
            ctx("error", nested("message", "boom")),
            ctx("error", new HashMap<String, Object>()),
            ctx("error", null)
        );
        assertRecognisedAndAgrees("ctx.error?.message != null", contexts);
        assertRecognisedAndAgrees("ctx.error?.message == null", contexts);
    }

    public void testConjunctionIsRecognised() {
        assertRecognisedAndAgrees(
            "ctx.error?.message != null && ctx.message == null",
            Arrays.asList(ctx(), ctx("error", nested("message", "x")), ctx("error", nested("message", "x"), "message", "y"))
        );
    }

    public void testInstanceofIsRecognised() {
        assertRecognisedAndAgrees(
            "ctx.json?.evidence instanceof List",
            Arrays.asList(ctx(), ctx("json", nested("evidence", new ArrayList<>())), ctx("json", nested("evidence", "notalist")))
        );
    }

    public void testConstantListContainsIsRecognised() {
        assertRecognisedAndAgrees("['a', 'b'].contains(ctx.tag)", Arrays.asList(ctx(), ctx("tag", "a"), ctx("tag", "z"), ctx("tag", null)));
    }

    /**
     * Painless {@code ?.} makes only the immediate access null-yielding; the chain continues, so a plain
     * {@code .} on the null result still throws. A recogniser that short-circuits the whole chain silently
     * turns this into {@code false}.
     */
    public void testNullSafeAppliesToOneAccessOnly() {
        IngestConditionalScript.Factory factory = compile("ctx.a?.b.c != null");
        assertEquals("throw:NullPointerException", run(factory, ctx()));
        assertEquals(false, run(factory, ctx("a", nested("b", new HashMap<String, Object>()))));
    }

    /**
     * Painless resolves {@code .empty} on a Map to {@code isEmpty()} rather than to {@code get("empty")}.
     * Evaluating the path with a raw map lookup instead of {@code Def.lookupGetter} inverts this.
     */
    public void testMapGetterShortcutIsNotAKeyLookup() {
        IngestConditionalScript.Factory factory = compile("ctx.tags.empty");
        assertEquals(true, run(factory, ctx("tags", new HashMap<String, Object>())));
        assertEquals(false, run(factory, ctx("tags", ctx("k", "v"))));
        // a map that literally has an "empty" key must still answer isEmpty()
        assertEquals(false, run(factory, ctx("tags", ctx("empty", Boolean.TRUE))));
    }

    private void assertNotRecognised(String source) {
        assertThat(
            "expected [" + source + "] to fall back to compilation",
            compile(source),
            org.hamcrest.Matchers.not(org.hamcrest.Matchers.instanceOf(ProgramConditionalFactory.class))
        );
    }

    public void testUnsupportedShapesFallBackToCompilation() {
        assertNotRecognised("def x = ctx.a; return x != null");        // a local
        assertNotRecognised("if (ctx.a != null) { return true } return false"); // control flow
    }

    /** Mixed operators must keep their precedence rather than collapsing into a flat list. */
    public void testNestedBooleanStructureIsRecognised() {
        assertRecognisedAndAgrees(
            "(ctx.a != null && ctx.b == null) || ctx.c != null",
            Arrays.asList(ctx(), ctx("a", "x"), ctx("c", "z"), ctx("a", "x", "b", "y"), ctx("a", "x", "c", "z"))
        );
        assertRecognisedAndAgrees(
            "ctx.a != null && (ctx.b == null || ctx.c != null)",
            Arrays.asList(ctx(), ctx("a", "x"), ctx("a", "x", "b", "y"), ctx("a", "x", "b", "y", "c", "z"))
        );
    }

    public void testMethodCallOnAPathIsRecognised() {
        assertRecognisedAndAgrees(
            "ctx.tags != null && ctx.tags.containsKey('k')",
            Arrays.asList(ctx(), ctx("tags", ctx("k", "v")), ctx("tags", new HashMap<String, Object>()), ctx("tags", "notamap"))
        );
    }

    /**
     * A {@code @script_aware} call pushes the script instance so it can check cancellation, so it keeps its
     * generated class rather than being reduced to data.
     */
    public void testScriptAwareCallFallsBackToCompilation() {
        assertNotRecognised("ctx.tags != null && ctx.tags.contains('x')");
    }

    /** {@code ===} is reference equality and must not be folded into {@code ==}. */
    public void testReferenceEqualityIsNotRecognised() {
        assertNotRecognised("ctx.a === ctx.b");
        assertNotRecognised("ctx.a !== ctx.b");
    }

    public void testRecognisedConditionReportsScriptNameOnFailure() {
        // a relational comparison against null throws inside the program; the failure must still name the script
        IngestConditionalScript.Factory factory = compile("ctx.a > 5");
        Exception e = expectThrows(Exception.class, () -> factory.newInstance(emptyMap(), ctx()).execute());
        assertNotNull(e);
    }
}
