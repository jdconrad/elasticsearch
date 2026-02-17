/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.javascript;

import org.elasticsearch.javascript.spi.Whitelist;
import org.elasticsearch.javascript.spi.WhitelistLoader;
import org.elasticsearch.script.ScriptContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmitTests extends ScriptTestCase {
    @Override
    protected Map<ScriptContext<?>, List<Whitelist>> scriptContexts() {
        Map<ScriptContext<?>, List<Whitelist>> contexts = new HashMap<>();
        List<Whitelist> whitelists = new ArrayList<>(JAVASCRIPT_BASE_WHITELIST);
        whitelists.add(WhitelistLoader.loadFromResourceFiles(JavascriptPlugin.class, "org.elasticsearch.javascript.test"));
        contexts.put(TestFieldScript.CONTEXT, whitelists);
        return contexts;
    }

    @Override
    public TestFieldScript exec(String script) {
        TestFieldScript.Factory factory = scriptEngine.compile(null, script, TestFieldScript.CONTEXT, new HashMap<>());
        TestFieldScript testScript = factory.newInstance();
        testScript.execute();
        return testScript;
    }

    public void testEmit() {
        TestFieldScript script = exec("emit(1L)");
        assertNotNull(script);
        assertArrayEquals(new long[] { 1L }, script.fetchValues());
    }

    public void testEmitFromUserFunction() {
        TestFieldScript script = exec("void doEmit(long l) { emit(l) } doEmit(1L); doEmit(100L)");
        assertNotNull(script);
        assertArrayEquals(new long[] { 1L, 100L }, script.fetchValues());
    }
}
