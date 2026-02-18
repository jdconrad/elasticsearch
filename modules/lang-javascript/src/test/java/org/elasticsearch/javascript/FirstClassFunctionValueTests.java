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

    public void testAssignLambdaToTypedInterfaceVariable() {
        assertEquals(2, exec("IntUnaryOperator increment = (x) -> x + 1; increment.applyAsInt(1)"));
    }

    public void testAssignFunctionRefToTypedInterfaceVariable() {
        assertEquals(1, exec("IntBinaryOperator compare = Integer::compare; compare.applyAsInt(2, 1)"));
    }

    public void testAssignNewArrayFunctionRefToTypedInterfaceVariable() {
        assertEquals(3, exec("IntFunction ctor = Double[]::new; Double[] values = (Double[])ctor.apply(3); values.length"));
    }

    public void testStoreTypedCallableInCollection() {
        assertEquals(
            5,
            exec("IntUnaryOperator increment = (x) -> x + 1; List callables = [increment]; IntUnaryOperator fn = (IntUnaryOperator)callables[0]; fn.applyAsInt(4)")
        );
    }

    public void testInvokeTypedCallableVariableWithCallSyntax() {
        assertEquals(2, exec("IntUnaryOperator increment = (x) -> x + 1; increment(1)"));
    }

    public void testInvokeTypedCallableFromCollectionWithCallSyntax() {
        assertEquals(
            5,
            exec("IntUnaryOperator increment = (x) -> x + 1; List callables = [increment]; IntUnaryOperator fn = (IntUnaryOperator)callables[0]; fn(4)")
        );
    }

    public void testCallSyntaxRejectsNonCallableValueType() {
        IllegalArgumentException e = expectScriptThrows(IllegalArgumentException.class, () -> exec("int value = 1; value(1)"));
        assertEquals("variable [value] of type [int] is not callable", e.getMessage());
    }

    public void testCallSyntaxRejectsDefCallableUntilDynamicPathExists() {
        IllegalArgumentException e = expectScriptThrows(
            IllegalArgumentException.class,
            () -> exec("def fn = params.fn; fn(1)", Map.of("fn", 1), true)
        );
        assertEquals("cannot invoke [def] value [fn] with local call syntax", e.getMessage());
    }

    public void testCallSyntaxRejectsWrongArityForTypedCallable() {
        IllegalArgumentException e = expectScriptThrows(
            IllegalArgumentException.class,
            () -> exec("IntUnaryOperator increment = (x) -> x + 1; increment(1, 2)")
        );
        assertEquals("incorrect number of arguments for callable value [increment], expected [1] but found [2]", e.getMessage());
    }
}
