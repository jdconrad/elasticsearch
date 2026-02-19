/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.javascript;

import org.elasticsearch.javascript.spi.JavascriptTestScript;
import org.elasticsearch.javascript.spi.Whitelist;
import org.elasticsearch.javascript.spi.WhitelistLoader;
import org.elasticsearch.script.ScriptContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuiltinAliasTests extends ScriptTestCase {

    public void testStringAliases() {
        assertEquals(true, exec("return 'foobarbaz'.includes('bar')"));
        assertEquals(false, exec("return 'foobarbaz'.includes('qux')"));
        assertArrayEquals(new String[] { "a", "", "b" }, (String[]) exec("return 'a,,b'.split(',')"));
        assertArrayEquals((String[]) exec("return 'a,,b'.splitOnToken(',', 2)"), (String[]) exec("return 'a,,b'.split(',', 2)"));
    }

    public void testCollectionAndListAliases() {
        assertEquals(true, exec("let l = new ArrayList(); l.add('x'); return l.includes('x')"));
        assertEquals("y", exec("let l = new ArrayList(); l.add('x'); l.add('y'); return l.at(1)"));
    }

    public void testSetAndMapAliases() {
        assertEquals(true, exec("let s = new HashSet(); s.add('a'); return s.has('a')"));
        assertEquals(true, exec("let m = new TreeMap(); m.put('a',1); return m.has('a')"));
        assertEquals(true, exec("let m = new TreeMap(); m.put('a',1); return m.keys().has('a')"));
        assertEquals(
            "a1",
            exec("let m = new TreeMap(); m.put('a',1); let e = m.entries().iterator().next(); return e.getKey() + e.getValue()")
        );
    }

    public void testJsonMethodAliasesAcrossContexts() {
        List<String> whitelistResources = List.of(
            "org.elasticsearch.json.txt",
            "org.elasticsearch.script.ingest.txt",
            "org.elasticsearch.script.update.txt",
            "org.elasticsearch.script.update_by_query.txt",
            "org.elasticsearch.script.reindex.txt"
        );
        String script = "let value = Json.parse('{\"x\":1}');"
            + "let compact = Json.stringify(value);"
            + "let pretty = Json.stringify(value, true);"
            + "return Json.parse(compact).x + Json.parse(pretty).x;";
        for (String whitelistResource : whitelistResources) {
            assertEquals(2, execWithWhitelist(whitelistResource, script));
        }
    }

    private Object execWithWhitelist(String whitelistResource, String script) {
        Map<ScriptContext<?>, List<Whitelist>> contexts = new HashMap<>();
        List<Whitelist> whitelists = new ArrayList<>(JAVASCRIPT_BASE_WHITELIST);
        whitelists.add(WhitelistLoader.loadFromResourceFiles(JavascriptPlugin.class, whitelistResource));
        contexts.put(JavascriptTestScript.CONTEXT, whitelists);

        JavascriptScriptEngine engine = new JavascriptScriptEngine(scriptEngineSettings(), contexts);
        JavascriptTestScript.Factory factory = engine.compile(null, script, JavascriptTestScript.CONTEXT, Collections.emptyMap());
        return factory.newInstance(Collections.emptyMap()).execute();
    }
}
