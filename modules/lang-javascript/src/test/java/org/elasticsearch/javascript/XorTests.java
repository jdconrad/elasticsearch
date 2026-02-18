/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.javascript;

/** Tests for xor operator across all types */
public class XorTests extends ScriptTestCase {

    public void testBasics() throws Exception {
        assertEquals(9 ^ 3, exec("return 9 ^ 3;"));
        assertEquals(9L ^ 3, exec("return 9 ^ 3;"));
        assertEquals(9 ^ 3L, exec("return 9 ^ 3;"));
        assertEquals(10, exec("let x = 9; let y = 3; return x ^ y;"));
    }

    public void testInt() throws Exception {
        assertEquals(5 ^ 12, exec("let x = 5; let y = 12; return x ^ y;"));
        assertEquals(5 ^ -12, exec("let x = 5; let y = -12; return x ^ y;"));
        assertEquals(7 ^ 15 ^ 3, exec("let x = 7; let y = 15; let z = 3; return x ^ y ^ z;"));
    }

    public void testIntConst() throws Exception {
        assertEquals(5 ^ 12, exec("return 5 ^ 12;"));
        assertEquals(5 ^ -12, exec("return 5 ^ -12;"));
        assertEquals(7 ^ 15 ^ 3, exec("return 7 ^ 15 ^ 3;"));
    }

    public void testLong() throws Exception {
        assertEquals(5L ^ 12L, exec("let x = 5; let y = 12; return x ^ y;"));
        assertEquals(5L ^ -12L, exec("let x = 5; let y = -12; return x ^ y;"));
        assertEquals(7L ^ 15L ^ 3L, exec("let x = 7; let y = 15; let z = 3; return x ^ y ^ z;"));
    }

    public void testLongConst() throws Exception {
        assertEquals(5L ^ 12L, exec("return 5L ^ 12L;"));
        assertEquals(5L ^ -12L, exec("return 5L ^ -12L;"));
        assertEquals(7L ^ 15L ^ 3L, exec("return 7L ^ 15L ^ 3L;"));
    }

    public void testBool() throws Exception {
        assertEquals(false, exec("let x = true; let y = true; return x ^ y;"));
        assertEquals(true, exec("let x = true; let y = false; return x ^ y;"));
        assertEquals(true, exec("let x = false; let y = true; return x ^ y;"));
        assertEquals(false, exec("let x = false; let y = false; return x ^ y;"));
    }

    public void testBoolConst() throws Exception {
        assertEquals(false, exec("return true ^ true;"));
        assertEquals(true, exec("return true ^ false;"));
        assertEquals(true, exec("return false ^ true;"));
        assertEquals(false, exec("return false ^ false;"));
    }

    public void testIllegal() throws Exception {
        // No JavaScript equivalent: Painless disallows float/double in ^; JS has no such type distinction.
        // expectScriptThrows(ClassCastException.class, () -> { exec("float x = (float)4; int y = 1; return x ^ y"); });
        // expectScriptThrows(ClassCastException.class, () -> { exec("double x = (double)4; int y = 1; return x ^ y"); });
    }

    public void testDef() {
        // No JS equivalent: Painless ClassCastException for float/double in ^
        // expectScriptThrows(ClassCastException.class, () -> { exec("def x = (float)4; def y = (byte)1; return x ^ y"); });
        // expectScriptThrows(ClassCastException.class, () -> { exec("def x = (double)4; def y = (byte)1; return x ^ y"); });
        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5L, exec("let x = 4; let y = 1; return x ^ y"));

        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5L, exec("let x = 4; let y = 1; return x ^ y"));

        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5L, exec("let x = 4; let y = 1; return x ^ y"));

        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5L, exec("let x = 4; let y = 1; return x ^ y"));

        assertEquals(5L, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5L, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5L, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5L, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5L, exec("let x = 4; let y = 1; return x ^ y"));

        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5L, exec("let x = 4; let y = 1; return x ^ y"));

        assertEquals(false, exec("let x = true;  let y = true; return x ^ y"));
        assertEquals(true, exec("let x = true;  let y = false; return x ^ y"));
        assertEquals(true, exec("let x = false; let y = true; return x ^ y"));
        assertEquals(false, exec("let x = false; let y = false; return x ^ y"));
    }

    public void testDefTypedLHS() {
        // No JS equivalent: Painless ClassCastException for float/double in ^
        // expectScriptThrows(ClassCastException.class, () -> { exec("float x = (float)4; def y = (byte)1; return x ^ y"); });
        // expectScriptThrows(ClassCastException.class, () -> { exec("double x = (double)4; def y = (byte)1; return x ^ y"); });
        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5L, exec("let x = 4; let y = 1; return x ^ y"));

        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5L, exec("let x = 4; let y = 1; return x ^ y"));

        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5L, exec("let x = 4; let y = 1; return x ^ y"));

        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5L, exec("let x = 4; let y = 1; return x ^ y"));

        assertEquals(5L, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5L, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5L, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5L, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5L, exec("let x = 4; let y = 1; return x ^ y"));

        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5L, exec("let x = 4; let y = 1; return x ^ y"));

        assertEquals(false, exec("let x = true;  let y = true; return x ^ y"));
        assertEquals(true, exec("let x = true;  let y = false; return x ^ y"));
        assertEquals(true, exec("let x = false; let y = true; return x ^ y"));
        assertEquals(false, exec("let x = false; let y = false; return x ^ y"));
    }

    public void testDefTypedRHS() {
        // No JS equivalent: Painless ClassCastException for float/double in ^
        // expectScriptThrows(ClassCastException.class, () -> { exec("def x = (float)4; byte y = (byte)1; return x ^ y"); });
        // expectScriptThrows(ClassCastException.class, () -> { exec("def x = (double)4; byte y = (byte)1; return x ^ y"); });
        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5L, exec("let x = 4; let y = 1; return x ^ y"));

        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5L, exec("let x = 4; let y = 1; return x ^ y"));

        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5L, exec("let x = 4; let y = 1; return x ^ y"));

        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5L, exec("let x = 4; let y = 1; return x ^ y"));

        assertEquals(5L, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5L, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5L, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5L, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5L, exec("let x = 4; let y = 1; return x ^ y"));

        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5, exec("let x = 4; let y = 1; return x ^ y"));
        assertEquals(5L, exec("let x = 4; let y = 1; return x ^ y"));

        assertEquals(false, exec("let x = true;  let y = true; return x ^ y"));
        assertEquals(true, exec("let x = true;  let y = false; return x ^ y"));
        assertEquals(true, exec("let x = false; let y = true; return x ^ y"));
        assertEquals(false, exec("let x = false; let y = false; return x ^ y"));
    }

    public void testCompoundAssignment() {
        // boolean: in JS ^ coerces to number, so result is 0 or 1
        assertEquals(0, exec("let x = true; x ^= true; return x;"));
        assertEquals(1, exec("let x = true; x ^= false; return x;"));
        assertEquals(1, exec("let x = false; x ^= true; return x;"));
        assertEquals(0, exec("let x = false; x ^= false; return x;"));
        // No JS equivalent: boolean[] and def[] not in JS
        // assertEquals(false, exec("boolean[] x = new boolean[1]; x[0] = true; x[0] ^= true; return x[0];"));
        // assertEquals(true, exec("boolean[] x = new boolean[1]; x[0] = true; x[0] ^= false; return x[0];"));
        // assertEquals(true, exec("boolean[] x = new boolean[1]; x[0] = false; x[0] ^= true; return x[0];"));
        // assertEquals(false, exec("boolean[] x = new boolean[1]; x[0] = false; x[0] ^= false; return x[0];"));

        assertEquals(13 ^ 14, exec("let x = 13; x ^= 14; return x;"));
        assertEquals(13 ^ 14, exec("let x = 13; x ^= 14; return x;"));
        assertEquals(13 ^ 14, exec("let x = 13; x ^= 14; return x;"));
        assertEquals(13 ^ 14, exec("let x = 13; x ^= 14; return x;"));
        assertEquals((long) (13 ^ 14), exec("let x = 13; x ^= 14; return x;"));
    }

    public void testBogusCompoundAssignment() {
        // No JavaScript equivalent: Painless ClassCastException for float/double in ^=
        // expectScriptThrows(ClassCastException.class, () -> { exec("float x = 4; int y = 1; x ^= y"); });
        // expectScriptThrows(ClassCastException.class, () -> { exec("double x = 4; int y = 1; x ^= y"); });
        // expectScriptThrows(ClassCastException.class, () -> { exec("int x = 4; float y = 1; x ^= y"); });
        // expectScriptThrows(ClassCastException.class, () -> { exec("int x = 4; double y = 1; x ^= y"); });
    }

    public void testCompoundAssignmentDef() {
        // In JS ^ coerces to number
        assertEquals(0, exec("let x = true; x ^= true; return x;"));
        assertEquals(1, exec("let x = true; x ^= false; return x;"));
        assertEquals(1, exec("let x = false; x ^= true; return x;"));
        assertEquals(0, exec("let x = false; x ^= false; return x;"));
        // No JS equivalent: def[] not in JavaScript
        // assertEquals(false, exec("def[] x = new def[1]; x[0] = true; x[0] ^= true; return x[0];"));
        // assertEquals(true, exec("def[] x = new def[1]; x[0] = true; x[0] ^= false; return x[0];"));
        // assertEquals(true, exec("def[] x = new def[1]; x[0] = false; x[0] ^= true; return x[0];"));
        // assertEquals(false, exec("def[] x = new def[1]; x[0] = false; x[0] ^= false; return x[0];"));

        assertEquals(13 ^ 14, exec("let x = 13; x ^= 14; return x;"));
        assertEquals(13 ^ 14, exec("let x = 13; x ^= 14; return x;"));
        assertEquals(13 ^ 14, exec("let x = 13; x ^= 14; return x;"));
        assertEquals(13 ^ 14, exec("let x = 13; x ^= 14; return x;"));
        assertEquals((long) (13 ^ 14), exec("let x = 13; x ^= 14; return x;"));
    }

    public void testDefBogusCompoundAssignment() {
        // No JavaScript equivalent: Painless ClassCastException for float/double in ^=
        // expectScriptThrows(ClassCastException.class, () -> { exec("def x = 4F; int y = 1; x ^= y"); });
        // expectScriptThrows(ClassCastException.class, () -> { exec("def x = 4D; int y = 1; x ^= y"); });
        // expectScriptThrows(ClassCastException.class, () -> { exec("int x = 4; def y = (float)1; x ^= y"); });
        // expectScriptThrows(ClassCastException.class, () -> { exec("int x = 4; def y = (double)1; x ^= y"); });
    }
}
