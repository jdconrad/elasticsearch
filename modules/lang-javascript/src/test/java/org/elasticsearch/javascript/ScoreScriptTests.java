/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.javascript;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.IndexService;
import org.elasticsearch.index.query.SearchExecutionContext;
import org.elasticsearch.javascript.spi.Whitelist;
import org.elasticsearch.javascript.spi.WhitelistLoader;
import org.elasticsearch.script.ScoreScript;
import org.elasticsearch.script.ScriptContext;
import org.elasticsearch.test.ESSingleNodeTestCase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static org.elasticsearch.javascript.ScriptTestCase.JAVASCRIPT_BASE_WHITELIST;

public class ScoreScriptTests extends ESSingleNodeTestCase {
    /**
     * Test that needTermStats() is reported correctly depending on whether _termStats is used
     */
    public void testNeedsTermStats() {
        IndexService index = createIndex("test", Settings.EMPTY, "d", "type=double");

        Map<ScriptContext<?>, List<Whitelist>> contexts = new HashMap<>();
        List<Whitelist> whitelists = new ArrayList<>(JAVASCRIPT_BASE_WHITELIST);
        whitelists.add(WhitelistLoader.loadFromResourceFiles(JavascriptPlugin.class, "org.elasticsearch.script.score.txt"));
        contexts.put(ScoreScript.CONTEXT, whitelists);
        JavascriptScriptEngine service = new JavascriptScriptEngine(Settings.EMPTY, contexts);

        SearchExecutionContext searchExecutionContext = index.newSearchExecutionContext(0, 0, null, () -> 0, null, emptyMap(), null, null);

        ScoreScript.Factory factory = service.compile(null, "1.2", ScoreScript.CONTEXT, Collections.emptyMap());
        ScoreScript.LeafFactory ss = factory.newFactory(Collections.emptyMap(), searchExecutionContext.lookup());
        assertFalse(ss.needs_termStats());

        factory = service.compile(null, "doc['d'].value", ScoreScript.CONTEXT, Collections.emptyMap());
        ss = factory.newFactory(Collections.emptyMap(), searchExecutionContext.lookup());
        assertFalse(ss.needs_termStats());

        factory = service.compile(null, "1/_termStats.totalTermFreq().getAverage()", ScoreScript.CONTEXT, Collections.emptyMap());
        ss = factory.newFactory(Collections.emptyMap(), searchExecutionContext.lookup());
        assertTrue(ss.needs_termStats());

        factory = service.compile(null, "doc['d'].value * _termStats.docFreq().getSum()", ScoreScript.CONTEXT, Collections.emptyMap());
        ss = factory.newFactory(Collections.emptyMap(), searchExecutionContext.lookup());
        assertTrue(ss.needs_termStats());
    }
}
