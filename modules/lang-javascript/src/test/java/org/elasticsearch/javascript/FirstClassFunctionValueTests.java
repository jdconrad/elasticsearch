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
            exec(
                "IntUnaryOperator increment = (x) -> x + 1; List callables = [increment]; IntUnaryOperator fn = (IntUnaryOperator)callables[0]; fn.applyAsInt(4)"
            )
        );
    }

    public void testInvokeTypedCallableVariableWithCallSyntax() {
        assertEquals(2, exec("IntUnaryOperator increment = (x) -> x + 1; increment(1)"));
    }

    public void testInvokeRawSupplierWithPrimitiveTarget() {
        assertEquals(1, exec("Supplier one = () -> 1; int value = one(); value"));
    }

    public void testInvokeRawSupplierInPrimitiveReturningFunction() {
        assertEquals(1, exec("int read(Supplier one) { one() } read(() -> 1)"));
    }

    public void testAssignLambdaToDefVariableAndInvokeWithCallSyntax() {
        assertEquals(2, exec("def increment = (x) -> x + 1; increment(1)"));
    }

    public void testAssignZeroArgLambdaToDefVariableAndInvokeWithCallSyntax() {
        assertEquals(1, exec("def one = () -> 1; one()"));
    }

    public void testAssignFunctionReferenceToDefVariableAndInvokeWithCallSyntax() {
        assertEquals(1, exec("def compare = Integer::compare; compare(2, 1)"));
    }

    public void testPassDefLambdaValueToDynamicMethodCall() {
        assertEquals(1, exec("def opt = Optional.empty(); def one = () -> 1; opt.orElseGet(one)"));
    }

    public void testAdaptRuntimeCallableDefToTypedInterface() {
        assertEquals(2, exec("def increment = (x) -> x + 1; Function f = increment; ((Integer)f.apply(1)).intValue()"));
    }

    public void testStoreRuntimeCallableDefInCollectionAndInvokeWithCallSyntax() {
        assertEquals(2, exec("def increment = (x) -> x + 1; List callables = [increment]; def fn = callables[0]; fn(1)"));
    }

    public void testInvokeNestedMapCallableValuesWithCallSyntax() {
        assertEquals(5, exec("def z = ['x': () -> 1, 'y': ['z': (a) -> a + 3]]; def x = z.x; def y = z.y.z; x() + y(1)"));
    }

    public void testInvokeNestedMapCallableValuesWithBraceAccessAndCallSyntax() {
        assertEquals(5, exec("def z = ['x': () -> 1, 'y': ['z': (a) -> a + 3]]; def x = z['x']; def y = z['y']['z']; x() + y(1)"));
    }

    public void testInvokeCallableValuesFromMixedNestedCollections() {
        assertEquals(5, exec("def z = ['x': () -> 1, 'y': [(a) -> a + 3]]; def x = z.x; def y = z.y[0]; x() + y(1)"));
    }

    public void testAdaptAliasedRuntimeCallableDefToRawBiFunctionWithCallSyntax() {
        assertEquals(3, exec("def x = (a, b) -> a + b; def y = x; BiFunction z = y; z(1, 2);"));
    }

    public void testRoundTripBetweenDefAndRawBiFunctionWithCallSyntax() {
        assertEquals(8, exec("def x = (a, b) -> a + b; BiFunction y = x; def z = y; y(2, 3) + z(1, 2)"));
    }

    public void testInvokeTypedCallableFromCollectionWithCallSyntax() {
        assertEquals(
            5,
            exec(
                "IntUnaryOperator increment = (x) -> x + 1; List callables = [increment]; IntUnaryOperator fn = (IntUnaryOperator)callables[0]; fn(4)"
            )
        );
    }

    public void testCallSyntaxRejectsNonCallableValueType() {
        IllegalArgumentException e = expectScriptThrows(IllegalArgumentException.class, () -> exec("int value = 1; value(1)"));
        assertEquals("variable [value] of type [int] is not callable", e.getMessage());
    }

    public void testCallSyntaxInvokesDefCallableValue() {
        assertEquals(2, exec("IntUnaryOperator increment = (x) -> x + 1; def fn = increment; fn(1)"));
    }

    public void testCallSyntaxRejectsNonCallableDefValue() {
        IllegalArgumentException e = expectScriptThrows(
            IllegalArgumentException.class,
            () -> exec("def fn = params.fn; fn(1)", Map.of("fn", 1), true)
        );
        assertEquals("value of type [java.lang.Integer] is not callable", e.getMessage());
    }

    public void testCallSyntaxRejectsWrongArityForDefCallable() {
        IllegalArgumentException e = expectScriptThrows(
            IllegalArgumentException.class,
            () -> exec("IntUnaryOperator increment = (x) -> x + 1; def fn = increment; fn(1, 2)")
        );
        assertEquals(
            "incorrect number of arguments for callable value [java.util.function.IntUnaryOperator], expected [1] but found [2]",
            e.getMessage()
        );
    }

    public void testAdaptDefCallableToCompatibleInterfaceVariable() {
        assertEquals(
            2,
            exec(
                "IntUnaryOperator increment = (x) -> x + 1; "
                    + "def fn = increment; "
                    + "Function func = fn; "
                    + "((Integer)func.apply(1)).intValue()"
            )
        );
    }

    public void testPassDefCallableToCompatibleInterfaceArgument() {
        assertEquals(
            6,
            exec(
                "int apply(Function func, int x) { ((Integer)func.apply(x)).intValue() } "
                    + "IntUnaryOperator increment = (x) -> x + 1; "
                    + "def fn = increment; "
                    + "apply(fn, 5)"
            )
        );
    }

    public void testAdaptDefCallableRejectsIncompatibleInterface() {
        expectScriptThrows(
            ClassCastException.class,
            () -> exec("IntUnaryOperator increment = (x) -> x + 1; def fn = increment; Predicate predicate = fn; predicate.test(1)")
        );
    }

    public void testCallSyntaxRejectsWrongArityForTypedCallable() {
        IllegalArgumentException e = expectScriptThrows(
            IllegalArgumentException.class,
            () -> exec("IntUnaryOperator increment = (x) -> x + 1; increment(1, 2)")
        );
        assertEquals("incorrect number of arguments for callable value [increment], expected [1] but found [2]", e.getMessage());
    }
}
