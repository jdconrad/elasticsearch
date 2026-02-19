/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.javascript;

import java.util.Map;

public class FirstClassFunctionValueTests extends ScriptTestCase {

    public void testAssignArrowFunctionToVariableAndInvokeWithCallSyntax() {
        assertEquals(2, exec("let increment = (x) => x + 1; increment(1)"));
    }

    public void testAssignAnonymousFunctionToVariableAndInvokeWithCallSyntax() {
        assertEquals(2, exec("let increment = function(x) { x + 1; }; increment(1)"));
    }

    public void testAssignZeroArgArrowFunctionToVariableAndInvokeWithCallSyntax() {
        assertEquals(1, exec("let one = () => 1; one()"));
    }

    public void testStoreRuntimeCallableInCollectionAndInvokeWithCallSyntax() {
        assertEquals(2, exec("let increment = (x) => x + 1; let callables = [increment]; let fn = callables[0]; fn(1)"));
    }

    public void testInvokeCallableValuesFromNestedCollections() {
        assertEquals(5, exec("let z = [() => 1, [(a) => a + 3]]; let x = z[0]; let y = z[1][0]; x() + y(1)"));
    }

    public void testFunctionDeclarationCanBeCalled() {
        assertEquals(3, exec("function add(a, b) { a + b; } add(1, 2)"));
    }

    public void testAssignBareFunctionValueToVariableAndInvokeWithCallSyntax() {
        assertEquals(3, exec("function add(a, b) { a + b; } let fn = add; fn(1, 2)"));
    }

    public void testAssignBareOverloadedFunctionValueToVariableAndInvokeWithCallSyntax() {
        assertEquals(7, exec("function sum(a) { a + 1; } function sum(a, b) { a + b; } let fn = sum; fn(2) + fn(1, 3)"));
    }

    public void testPassBareFunctionValueAsMethodArgument() {
        assertEquals(100, exec("function myCompare(a, b) { b - a; } let values = [1, 100, -100]; values.sort(myCompare); values[0]"));
    }

    public void testBareFunctionValuePrefersVariableShadowing() {
        assertEquals(2, exec("function add(a, b) { a + b; } let add = (a, b) => a - b; let fn = add; fn(5, 3)"));
    }

    public void testPassRuntimeCallableToDynamicMethodCall() {
        assertEquals(1, exec("let opt = Optional.empty(); let one = () => 1; opt.orElseGet(one)"));
    }

    public void testCallSyntaxRejectsNonCallableDefValue() {
        IllegalArgumentException e = expectScriptThrows(IllegalArgumentException.class, () -> exec("let value = 1; value(1)"));
        assertEquals("value of type [java.lang.Integer] is not callable", e.getMessage());
    }

    public void testCallSyntaxRejectsNonCallableParameterValue() {
        IllegalArgumentException e = expectScriptThrows(
            IllegalArgumentException.class,
            () -> exec("let fn = params.fn; fn(1)", Map.of("fn", 1), true)
        );
        assertEquals("value of type [java.lang.Integer] is not callable", e.getMessage());
    }

    public void testCallSyntaxRejectsWrongArityForCallableValue() {
        IllegalArgumentException e = expectScriptThrows(
            IllegalArgumentException.class,
            () -> exec("let increment = (x) => x + 1; increment(1, 2)")
        );
        assertTrue(e.getMessage().contains("does not support [2] arguments"));
    }
}
