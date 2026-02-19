/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.javascript;

import org.junit.Ignore;
import org.hamcrest.Matcher;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;

import static org.hamcrest.Matchers.equalTo;

/** Tests for working with arrays. Uses JavaScript array syntax (new Array(n), []). */
public class ArrayTests extends ArrayLikeObjectTestCase {
    @Override
    protected String declType(String valueType) {
        return "let";
    }

    @Override
    protected String defDeclType() {
        return "let";
    }

    /** JavaScript: create array of given size with new Array(size). */
    @Override
    protected String valueCtorCall(String valueType, int size) {
        return "new Array(" + size + ")";
    }

    @Override
    protected Matcher<String> outOfBoundsExceptionMessageMatcher(int index, int size) {
        return equalTo("Index " + Integer.toString(index) + " out of bounds for length " + Integer.toString(size));
    }

    public void testArrayLengthHelper() throws Throwable {
        assertArrayLength(2, new int[2]);
        assertArrayLength(3, new long[3]);
        assertArrayLength(4, new byte[4]);
        assertArrayLength(5, new float[5]);
        assertArrayLength(6, new double[6]);
        assertArrayLength(7, new char[7]);
        assertArrayLength(8, new short[8]);
        assertArrayLength(9, new Object[9]);
        assertArrayLength(10, new Integer[10]);
        assertArrayLength(11, new String[11][2]);
    }

    private void assertArrayLength(int length, Object array) throws Throwable {
        final MethodHandle mh = Def.arrayLengthGetter(array.getClass());
        assertSame(array.getClass(), mh.type().parameterType(0));
        assertEquals(length, (int) mh.asType(MethodType.methodType(int.class, Object.class)).invokeExact(array));
    }

    public void testJacksCrazyExpression1() {
        assertEquals(1, exec("let x; let y = [1]; x = y[0]; return x;"));
    }

    public void testJacksCrazyExpression2() {
        assertEquals(1, exec("let x; let y = [1]; x = y[0]; return x;"));
    }

    public void testArrayVariable() {
        assertEquals(1, exec("let x = 1; let y = [x]; return y.length"));
    }

    public void testForLoop() {
        assertEquals(
            999 * 1000 / 2,
            exec(
                "let a = new Array(1000); for (let x = 0; x < a.length; x++) { a[x] = x; } "
                    + "let total = 0; for (let x = 0; x < a.length; x++) { total += a[x]; } return total;"
            )
        );
    }

    /**
     * Make sure we don't try and convert the {@code /} after the {@code ]} into a regex....
     */
    public void testDivideArray() {
        assertEquals(1, exec("let x = [2]; return x[0] / 2"));
    }

    /** Iteration over array literals using JavaScript for-of; successful cases only. */
    public void testPrimitiveIteration() {
        assertEquals(true, exec("let x = [true, false]; let s = false; for (let l of x) s = s || l; return s"));

        assertEquals(30, exec("let x = [10, 20]; let s = 0; for (let l of x) s += l; return s"));
        assertEquals(20, exec("let x = [10, 20]; let s = 0; for (let l of x) s = l; return s"));
        assertEquals(300, exec("let x = [100, 200]; let s = 0; for (let l of x) s += l; return s"));
        assertEquals(200, exec("let x = [100, 200]; let s = 0; for (let l of x) s = l; return s"));

        assertEquals("b", exec("let x = ['a', 'b']; let s = 0; for (let l of x) s = l; return s"));
    }

    @Ignore("Painless-only: typed for-each with wrong element type yields ClassCastException; JS has no typed iteration")
    public void testPrimitiveIterationWrongTypeThrows() {
        expectScriptThrows(
            ClassCastException.class,
            () -> exec("let x = new boolean[] { true, false }; let s = 0; for (short l : x) s += l; return s")
        );
        expectScriptThrows(
            ClassCastException.class,
            () -> exec("let x = new boolean[] { true, false }; let s = 0; for (char l : x) s = l; return s")
        );
        expectScriptThrows(
            ClassCastException.class,
            () -> exec("let x = new boolean[] { true, false }; let s = 0; for (int l : x) s += l; return s")
        );
        expectScriptThrows(
            ClassCastException.class,
            () -> exec("let x = new boolean[] { true, false }; let s = 0; for (long l : x) s += l; return s")
        );
        expectScriptThrows(
            ClassCastException.class,
            () -> exec("let x = new boolean[] { true, false }; let s = 0; for (float l : x) s += l; return s")
        );
        expectScriptThrows(
            ClassCastException.class,
            () -> exec("let x = new boolean[] { true, false }; let s = 0; for (double l : x) s += l; return s")
        );
        expectScriptThrows(
            ClassCastException.class,
            () -> exec("let x = new String[] { 'foo', 'bar' }; let s = false; for (boolean l : x) s |= l; return s")
        );
        expectScriptThrows(
            ClassCastException.class,
            () -> exec("let x = new String[] { 'foo', 'bar' }; let s = 0; for (byte l : x) s += l; return s")
        );
        expectScriptThrows(
            ClassCastException.class,
            () -> exec("let x = new String[] { 'foo', 'bar' }; let s = 0; for (short l : x) s += l; return s")
        );
        expectScriptThrows(
            ClassCastException.class,
            () -> exec("let x = new String[] { 'foo', 'bar' }; let s = 0; for (char l : x) s = l; return s")
        );
        expectScriptThrows(
            ClassCastException.class,
            () -> exec("let x = new String[] { 'foo', 'bar' }; let s = 0; for (int l : x) s += l; return s")
        );
        expectScriptThrows(
            ClassCastException.class,
            () -> exec("let x = new String[] { 'foo', 'bar' }; let s = 0; for (long l : x) s += l; return s")
        );
        expectScriptThrows(
            ClassCastException.class,
            () -> exec("let x = new String[] { 'foo', 'bar' }; let s = 0; for (float l : x) s += l; return s")
        );
        expectScriptThrows(
            ClassCastException.class,
            () -> exec("let x = new String[] { 'foo', 'bar' }; let s = 0; for (double l : x) s += l; return s")
        );
    }
}
