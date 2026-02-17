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
import org.elasticsearch.javascript.phase.IRTreeVisitor;
import org.elasticsearch.javascript.phase.UserTreeVisitor;
import org.elasticsearch.javascript.spi.JavascriptTestScript;
import org.elasticsearch.javascript.spi.Whitelist;
import org.elasticsearch.javascript.symbol.ScriptScope;
import org.elasticsearch.javascript.symbol.WriteScope;
import org.objectweb.asm.util.Textifier;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;

import static org.elasticsearch.javascript.ScriptTestCase.JAVASCRIPT_BASE_WHITELIST;

/** quick and dirty tools for debugging */
final class Debugger {

    /** compiles source to bytecode, and returns debugging output */
    static String toString(final String source) {
        return toString(JavascriptTestScript.class, source, new CompilerSettings(), JAVASCRIPT_BASE_WHITELIST);
    }

    /** compiles to bytecode, and returns debugging output */
    static String toString(Class<?> iface, String source, CompilerSettings settings, List<Whitelist> whitelists) {
        StringWriter output = new StringWriter();
        PrintWriter outputWriter = new PrintWriter(output);
        Textifier textifier = new Textifier();
        try {
            new Compiler(iface, null, null, JavascriptLookupBuilder.buildFromWhitelists(whitelists, new HashMap<>(), new HashMap<>()))
                .compile("<debugging>", source, settings, textifier);
        } catch (RuntimeException e) {
            textifier.print(outputWriter);
            e.addSuppressed(new Exception("current bytecode: \n" + output));
            throw e;
        }

        textifier.print(outputWriter);
        return output.toString();
    }

    /** compiles to bytecode, and returns debugging output */
    private static String tree(
        Class<?> iface,
        String source,
        CompilerSettings settings,
        List<Whitelist> whitelists,
        UserTreeVisitor<ScriptScope> semanticPhaseVisitor,
        UserTreeVisitor<ScriptScope> irPhaseVisitor,
        IRTreeVisitor<WriteScope> asmPhaseVisitor
    ) {
        StringWriter output = new StringWriter();
        PrintWriter outputWriter = new PrintWriter(output);
        Textifier textifier = new Textifier();
        try {
            new Compiler(iface, null, null, JavascriptLookupBuilder.buildFromWhitelists(whitelists, new HashMap<>(), new HashMap<>()))
                .compile("<debugging>", source, settings, textifier, semanticPhaseVisitor, irPhaseVisitor, asmPhaseVisitor);
        } catch (RuntimeException e) {
            textifier.print(outputWriter);
            e.addSuppressed(new Exception("current bytecode: \n" + output));
            throw e;
        }

        textifier.print(outputWriter);
        return output.toString();
    }

    static void phases(
        final String source,
        UserTreeVisitor<ScriptScope> semanticPhaseVisitor,
        UserTreeVisitor<ScriptScope> irPhaseVisitor,
        IRTreeVisitor<WriteScope> asmPhaseVisitor
    ) {
        tree(
            JavascriptTestScript.class,
            source,
            new CompilerSettings(),
            JAVASCRIPT_BASE_WHITELIST,
            semanticPhaseVisitor,
            irPhaseVisitor,
            asmPhaseVisitor
        );
    }
}
