/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.javascript;

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
}
