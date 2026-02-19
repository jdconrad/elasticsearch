/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.javascript;

import org.apache.lucene.util.BytesRef;
import org.elasticsearch.javascript.lookup.JavascriptLookup;
import org.elasticsearch.javascript.lookup.JavascriptMethod;
import org.elasticsearch.javascript.spi.JavascriptTestScript;
import org.elasticsearch.javascript.spi.Whitelist;
import org.elasticsearch.javascript.spi.WhitelistLoader;
import org.elasticsearch.script.ScriptContext;
import org.elasticsearch.script.field.vectors.FloatRankVectors;
import org.elasticsearch.script.field.vectors.KnnDenseVector;
import org.elasticsearch.script.field.vectors.VectorIterator;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

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

    public void testJsonClassAndMethodAliasesAcrossContexts() {
        List<String> whitelistResources = List.of(
            "org.elasticsearch.json.txt",
            "org.elasticsearch.script.ingest.txt",
            "org.elasticsearch.script.update.txt",
            "org.elasticsearch.script.update_by_query.txt",
            "org.elasticsearch.script.reindex.txt"
        );
        String script = "let value = JSON.parse('{\"x\":1}');"
            + "let compact = JSON.stringify(value);"
            + "let pretty = JSON.stringify(value, true);"
            + "return Json.parse(compact).x + Json.parse(pretty).x;";
        for (String whitelistResource : whitelistResources) {
            assertEquals(2, execWithWhitelist(whitelistResource, script));
        }
    }

    public void testRegExpClassAlias() {
        assertEquals("\\Qa.b\\E", exec("return RegExp.quote('a.b')"));
        assertEquals("\\Qa.b\\E", exec("return Pattern.quote('a.b')"));
    }

    public void testRegExpSourceAlias() {
        Map<String, Object> vars = Map.of("p", Pattern.compile("a+b"));
        assertEquals("a+b", exec("return params.p.source()", vars, true));
        assertEquals(true, exec("return params.p instanceof RegExp", vars, true));
        assertEquals(true, exec("return params.p instanceof Pattern", vars, true));
    }

    public void testVectorGetterAliases() {
        Map<String, Object> vars = new HashMap<>();
        vars.put("dv", new KnnDenseVector(new float[] { 3.0f, 4.0f }));
        byte[] magnitudes = ByteBuffer.allocate(2 * Float.BYTES).putFloat(5.0f).putFloat(5.0f).array();
        vars.put(
            "rv",
            new FloatRankVectors(
                VectorIterator.from(new float[][] { new float[] { 3.0f, 4.0f }, new float[] { 0.0f, 5.0f } }),
                new BytesRef(magnitudes),
                2,
                2
            )
        );

        assertEquals(
            28.0d,
            ((Number) exec(
                "return params.dv.getDims() + params.dv.dims()"
                    + "  + params.dv.getVector().length + params.dv.vector().length"
                    + "  + params.dv.getMagnitude() + params.dv.magnitude()"
                    + "  + params.rv.getDims() + params.rv.dims()"
                    + "  + params.rv.getMagnitudes().length + params.rv.magnitudes().length"
                    + "  + (params.rv.getVectors().hasNext() ? 1 : 0)"
                    + "  + (params.rv.vectors().hasNext() ? 1 : 0);",
                vars,
                true
            )).doubleValue(),
            0.0d
        );
    }

    public void testScriptFieldGetterAliasesAreRegistered() {
        JavascriptLookup lookup = scriptEngine.getContextsToLookups().get(JavascriptTestScript.CONTEXT);
        assertNotNull(lookup);

        assertMethodAlias(lookup, "org.elasticsearch.index.fielddata.ScriptDocValues.Strings", "getValue", "value", 0);
        assertMethodAlias(lookup, "org.elasticsearch.index.fielddata.ScriptDocValues.Longs", "getValue", "value", 0);
        assertMethodAlias(lookup, "org.elasticsearch.index.fielddata.ScriptDocValues.Dates", "getValue", "value", 0);
        assertMethodAlias(lookup, "org.elasticsearch.index.fielddata.ScriptDocValues.Doubles", "getValue", "value", 0);
        assertMethodAlias(lookup, "org.elasticsearch.index.fielddata.ScriptDocValues.GeoPoints", "getValue", "value", 0);
        assertMethodAlias(lookup, "org.elasticsearch.index.fielddata.ScriptDocValues.Booleans", "getValue", "value", 0);
        assertMethodAlias(lookup, "org.elasticsearch.index.fielddata.ScriptDocValues.BytesRefs", "getValue", "value", 0);

        assertMethodAlias(lookup, "org.elasticsearch.search.lookup.FieldLookup", "getValue", "value", 0);
        assertMethodAlias(lookup, "org.elasticsearch.search.lookup.FieldLookup", "getValues", "values", 0);

        assertMethodAlias(lookup, "org.elasticsearch.index.mapper.vectors.DenseVectorScriptDocValues", "getVectorValue", "vector", 0);
        assertMethodAlias(lookup, "org.elasticsearch.index.mapper.vectors.DenseVectorScriptDocValues", "getMagnitude", "magnitude", 0);
        assertMethodAlias(lookup, "org.elasticsearch.index.mapper.vectors.RankVectorsScriptDocValues", "getVectorValues", "vectors", 0);
        assertMethodAlias(lookup, "org.elasticsearch.index.mapper.vectors.RankVectorsScriptDocValues", "getMagnitudes", "magnitudes", 0);

        assertMethodAlias(lookup, "org.elasticsearch.script.field.vectors.DenseVector", "getVector", "vector", 0);
        assertMethodAlias(lookup, "org.elasticsearch.script.field.vectors.DenseVector", "getMagnitude", "magnitude", 0);
        assertMethodAlias(lookup, "org.elasticsearch.script.field.vectors.DenseVector", "getDims", "dims", 0);
        assertMethodAlias(lookup, "org.elasticsearch.script.field.vectors.RankVectors", "getVectors", "vectors", 0);
        assertMethodAlias(lookup, "org.elasticsearch.script.field.vectors.RankVectors", "getMagnitudes", "magnitudes", 0);
        assertMethodAlias(lookup, "org.elasticsearch.script.field.vectors.RankVectors", "getDims", "dims", 0);
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

    private void assertMethodAlias(
        JavascriptLookup lookup,
        String canonicalClassName,
        String originalMethodName,
        String aliasMethodName,
        int arity
    ) {
        JavascriptMethod originalMethod = lookup.lookupJavascriptMethod(canonicalClassName, false, originalMethodName, arity);
        JavascriptMethod aliasMethod = lookup.lookupJavascriptMethod(canonicalClassName, false, aliasMethodName, arity);
        assertNotNull(originalMethod);
        assertNotNull(aliasMethod);
        assertEquals(originalMethod.javaMethod(), aliasMethod.javaMethod());
    }
}
