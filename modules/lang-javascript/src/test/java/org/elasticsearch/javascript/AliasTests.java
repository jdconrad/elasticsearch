/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.javascript;

import org.elasticsearch.javascript.lookup.JavascriptLookupBuilder;
import org.elasticsearch.javascript.spi.JavascriptTestScript;
import org.elasticsearch.javascript.spi.Whitelist;
import org.elasticsearch.javascript.spi.WhitelistLoader;
import org.elasticsearch.script.ScriptContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AliasTests extends ScriptTestCase {

    @Override
    protected Map<ScriptContext<?>, List<Whitelist>> scriptContexts() {
        Map<ScriptContext<?>, List<Whitelist>> contexts = new HashMap<>();
        List<Whitelist> whitelists = new ArrayList<>(JAVASCRIPT_BASE_WHITELIST);
        whitelists.add(WhitelistLoader.loadFromResourceFiles(JavascriptPlugin.class, "org.elasticsearch.javascript.alias"));
        contexts.put(JavascriptTestScript.CONTEXT, whitelists);
        return contexts;
    }

    public void testNoShadowing() {
        IllegalArgumentException err = expectThrows(
            IllegalArgumentException.class,
            () -> JavascriptLookupBuilder.buildFromWhitelists(
                List.of(WhitelistLoader.loadFromResourceFiles(JavascriptPlugin.class, "org.elasticsearch.javascript.alias-shadow")),
                new HashMap<>(),
                new HashMap<>()
            )
        );
        assertEquals(
            "Cannot add alias [AliasedTestInnerClass] for [class org.elasticsearch.javascript.AliasTestClass$AliasedTestInnerClass]"
                + " that shadows class [class org.elasticsearch.javascript.AliasedTestInnerClass]",
            err.getCause().getMessage()
        );
    }

    public void testDefAlias() {
        assertEquals(5, exec("let a = AliasTestClass.getInnerAliased(); let b = a; b.plus(2, 3)"));
    }

    public void testInnerAlias() {
        assertEquals(5, exec("let a = AliasTestClass.getInnerAliased(); a.plus(2, 3)"));
        assertEquals(5, exec("let a = AliasTestClass.getInnerAliased(); a.plus(2, 3)"));
    }

    public void testInnerNoAlias() {
        assertEquals(-1, exec("let a = AliasTestClass.getInnerUnaliased(); a.minus(2, 3)"));
        // Painless also tested that "UnaliasedTestInnerClass a = ..." throws (unresolved type); no JS equivalent for typed declaration.
    }
}
