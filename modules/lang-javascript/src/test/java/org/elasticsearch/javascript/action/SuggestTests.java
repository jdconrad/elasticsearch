/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.javascript.action;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.Token;
import org.elasticsearch.javascript.ScriptTestCase;
import org.elasticsearch.javascript.antlr.EnhancedSuggestLexer;
import org.elasticsearch.javascript.antlr.SuggestLexer;
import org.elasticsearch.javascript.spi.JavascriptTestScript;

import java.util.List;

public class SuggestTests extends ScriptTestCase {

    private List<? extends Token> getSuggestTokens(String source) {
        ANTLRInputStream stream = new ANTLRInputStream(source);
        SuggestLexer lexer = new EnhancedSuggestLexer(stream, scriptEngine.getContextsToLookups().get(JavascriptTestScript.CONTEXT));
        lexer.removeErrorListeners();
        return lexer.getAllTokens();
    }

    private void compareTokens(List<? extends Token> tokens, String... expected) {
        assertEquals(expected.length % 2, 0);
        assertEquals(tokens.size(), expected.length / 2);

        int index = 0;
        for (Token token : tokens) {
            assertEquals(SuggestLexer.VOCABULARY.getDisplayName(token.getType()), expected[index++]);
            assertEquals(token.getText(), expected[index++]);
        }
    }

    public void testSuggestLexer() {
        compareTokens(getSuggestTokens("test"), SuggestLexer.VOCABULARY.getDisplayName(SuggestLexer.ID), "test");

        // Painless-only syntax: int/ArrayList/def type declarations; no obvious JS lexer equivalent for same token expectations
        // compareTokens(
        // getSuggestTokens("int test;"),
        // SuggestLexer.VOCABULARY.getDisplayName(SuggestLexer.TYPE),
        // "int",
        // ...
        // );
        // compareTokens(getSuggestTokens("ArrayList test;"), ...);
        // compareTokens(getSuggestTokens("def test;"), ...);
        // compareTokens(getSuggestTokens("int[] test;"), ...);
        // compareTokens(getSuggestTokens("ArrayList[] test;"), ...);
        // compareTokens(getSuggestTokens("def[] test;"), ...);

        // Painless-only: List/ArrayList/def/int type syntax; no obvious JS equivalent for same token expectations
        // compareTokens(getSuggestTokens("List test = new ArrayList(); test."), ...);
        // compareTokens(getSuggestTokens("List test = new ArrayList(); test.add"), ...);
        // compareTokens(getSuggestTokens("List test = new ArrayList(); test.add("), ...);
        // compareTokens(getSuggestTokens("def test(int param) {return param;} test(2);"), ...);
    }
}
