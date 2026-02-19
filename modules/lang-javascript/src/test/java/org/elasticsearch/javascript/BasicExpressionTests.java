/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.javascript;

import java.util.Collections;

import static java.util.Collections.singletonMap;

public class BasicExpressionTests extends ScriptTestCase {

    /** simple tests returning a constant value */
    public void testReturnConstant() {
        assertEquals(5, exec("return 5"));
        assertEquals(6L, exec("return 6"));
        assertEquals(7L, exec("return 7"));
        assertEquals(7.0d, exec("return 7.0"));
        assertEquals(18.0d, exec("return 18"));
        assertEquals(19.0d, exec("return 19.0"));
        assertEquals(20.0d, exec("return 20"));
        assertEquals(21.0d, exec("return 21.0"));
        assertEquals(32.0F, exec("return 32.0"));
        assertEquals(33.0F, exec("return 33"));
        assertEquals(34.0F, exec("return 34.0"));
        assertEquals(35.0F, exec("return 35"));
        assertEquals((byte) 255, exec("return 255"));
        assertEquals((short) 5, exec("return 5"));
        assertEquals("string", exec("return \"string\""));
        assertEquals("string", exec("return 'string'"));
        assertEquals(true, exec("return true"));
        assertEquals(false, exec("return false"));
        assertNull(exec("return null"));
    }

    @org.junit.Ignore("Painless-only: char type")
    public void testReturnConstantChar() {
        assertEquals('x', exec("return (char)'x';"));
    }

    @org.junit.Ignore("Painless-only: char cast")
    public void testConstantCharTruncation() {
        assertEquals('蚠', exec("return (char)100000;"));
    }

    public void testStringEscapes() {
        // The readability of this test suffers from having to escape `\` and `"` in java strings. Please be careful. Sorry!
        // `\\` is a `\`
        assertEquals("\\string", exec("\"\\\\string\""));
        assertEquals("\\string", exec("'\\\\string'"));
        // `\"` is a `"` if surrounded by `"`s
        assertEquals("\"string", exec("\"\\\"string\""));
        // JavaScript allows \" in single-quoted strings (escape produces "); Painless restricts to \\ and \' only.
        assertEquals("\"string", exec("'\\\"string'", false));
        // `\'` is a `'` if surrounded by `'`s
        // JavaScript allows \' in double-quoted strings (escape produces '); Painless restricts to \\ and \" only.
        assertEquals("'string", exec("\"\\'string\"", false));
        assertEquals("'string", exec("'\\'string'"));
        // We don't break native escapes like new line (use \\n in Java so script contains \n escape)
        assertEquals("\nstring", exec("\"\\nstring\""));
        assertEquals("\nstring", exec("'\\nstring'"));

        // And we're ok with strings with multiple escape sequences
        assertEquals("\\str\"in\\g", exec("\"\\\\str\\\"in\\\\g\""));
        assertEquals("st\\r'i\\ng", exec("'st\\\\r\\'i\\\\ng'"));
    }

    public void testStringTermination() {
        // `'` inside of a string delimited with `"` should be ok
        assertEquals("test'", exec("\"test'\""));
        // `"` inside of a string delimited with `'` should be ok
        assertEquals("test\"", exec("'test\"'"));
    }

    /** declaring variables for primitive types */
    public void testDeclareVariable() {
        assertEquals(5, exec("let i = 5; return i;"));
        assertEquals(7L, exec("let l = 7; return l;"));
        assertEquals(7.0, exec("let d = 7; return d;"));
        assertEquals(32.0F, exec("let f = 32; return f;"));
        assertEquals((byte) 255, exec("let b = 255; return b;"));
        assertEquals((short) 5, exec("let s = 5; return s;"));
        assertEquals("string", exec("let s = \"string\"; return s;"));
        assertEquals(true, exec("let v = true; return v;"));
        assertEquals(false, exec("let v = false; return v;"));
    }

    public void testCast() {
        assertEquals(1, exec("return 1;"));
        assertEquals(100, exec("let x = 100; return x;"));

        assertEquals(3, exec("""
            let x = new HashMap();
            let y = x;
            y.put(2, 3);
            return x.get(2);
            """));
    }

    @org.junit.Ignore("Painless-only: def and implicit cast")
    public void testIllegalDefCast() {
        Exception exception = expectScriptThrows(ClassCastException.class, () -> { exec("def x = 1.0; int y = x; return y;"); });
        assertTrue(exception.getMessage().contains("cannot implicitly cast"));

        exception = expectScriptThrows(ClassCastException.class, () -> { exec("def x = (short)1; byte y = x; return y;"); });
        assertTrue(exception.getMessage().contains("cannot implicitly cast"));
    }

    public void testCat() {
        assertEquals("aaabbb", exec("return \"aaa\" + \"bbb\";"));
        assertEquals("aaabbb", exec("let aaa = \"aaa\", bbb = \"bbb\"; return aaa + bbb;"));

        assertEquals("aaabbbbbbbbb", exec("""
            let aaa = "aaa", bbb = "bbb"; let x = 0;
            for (; x < 3; ++x)
                aaa += bbb;
            return aaa;"""));
    }

    public void testComp() {
        assertEquals(true, exec("return 2 < 3;"));
        assertEquals(false, exec("let x = 4; let y = 2; return x < y;"));
        assertEquals(true, exec("return 3 <= 3;"));
        assertEquals(true, exec("let x = 3; let y = 3; return x <= y;"));
        assertEquals(false, exec("return 2 > 3;"));
        assertEquals(true, exec("let x = 4; let y = 2; return x > y;"));
        assertEquals(false, exec("return 3 >= 4;"));
        assertEquals(true, exec("let x = 3; let y = 3; return x >= y;"));
        assertEquals(false, exec("return 3 == 4;"));
        assertEquals(true, exec("let x = 3; let y = 3; return x == y;"));
        assertEquals(true, exec("return 3 != 4;"));
        assertEquals(false, exec("let x = 3; let y = 3; return x != y;"));
    }

    /**
     * Test boxed def objects in various places
     */
    public void testBoxing() {
        // return
        assertEquals(4, exec("return params.get(\"x\");", Collections.singletonMap("x", 4), true));
        // assignment
        assertEquals(4, exec("let y = params.get(\"x\"); return y;", Collections.singletonMap("x", 4), true));
        // comparison
        assertEquals(true, exec("return 5 > params.get(\"x\");", Collections.singletonMap("x", 4), true));
    }

    public void testBool() {
        assertEquals(true, exec("return true && true;"));
        assertEquals(false, exec("let a = true, b = false; return a && b;"));
        assertEquals(true, exec("return true || true;"));
        assertEquals(true, exec("let a = true, b = false; return a || b;"));
    }

    public void testConditional() {
        assertEquals(1, exec("let x = 5; return x > 3 ? 1 : 0;"));
        assertEquals(0, exec("let a = null; return a != null ? 1 : 0;"));
    }

    public void testPrecedence() {
        assertEquals(2, exec("let x = 5; return (x+x)/x;"));
        assertEquals(true, exec("let t = true, f = false; return t && (f || t);"));
    }

    @org.junit.Ignore(
        "null-safe method resolution: Unknown call [toString] when receiver is def-typed; semantic phase needs to resolve method on nullable receiver"
    )
    public void testNullSafeDeref() {
        // Objects in general
        // Call
        assertNull(exec("let a = null;  return a?.toString()"));
        assertEquals("foo", exec("let a = 'foo'; return a?.toString()"));
        assertNull(exec("let a = null;  return a?.toString()"));
        assertEquals("foo", exec("let a = 'foo'; return a?.toString()"));
        // Call with primitive result (JS allows primitive from ?.; no "must be nullable" rule)
        assertNull(exec("let a = null;  return a?.length()"));
        assertEquals(3, exec("let a = 'foo'; return a?.length()"));
        // Read shortcut
        assertNull(exec("let a = null; return a?.x"));
        assertEquals(0, exec("let a = new org.elasticsearch.javascript.FeatureTestObject(); return a?.x"));

        // Maps
        // Call
        assertNull(exec("let a = null;        return a?.toString()"));
        assertEquals("{}", exec("let a = {};         return a?.toString()"));
        // Call with primitive result (JS allows primitive from ?.)
        assertNull(exec("let a = null;        return a?.size()"));
        assertEquals(0, exec("let a = {};         return a?.size()"));
        // Read shortcut
        assertNull(exec("let a = null;        return a?.other"));       // Read shortcut
        assertEquals(1, exec("let a = {'other':1}; return a?.other"));       // Read shortcut
        assertNull(exec("let a = null;        return a?.other"));       // Read shortcut
        assertEquals(1, exec("let a = {'other':1}; return a?.other"));       // Read shortcut

        // Array (JS allows primitive from ?.)
        assertNull(exec("let a = null;               return a?.length"));
        assertEquals(2, exec("let a = [2, 3];   return a?.length"));

        // Results from maps (should just work but let's test anyway)
        FeatureTestObject t = new FeatureTestObject();
        assertNull(exec("let a = {'thing': params.t}; return a.other?.getX()", singletonMap("t", t), true));
        assertNull(exec("let a = {'thing': params.t}; return a.other?.x", singletonMap("t", t), true));
        assertNull(exec("let a = {'thing': params.t}; return a.other?.getX()", singletonMap("t", t), true));
        assertNull(exec("let a = {'thing': params.t}; return a.other?.x", singletonMap("t", t), true));
        assertEquals(0, exec("let a = {'other': params.t}; return a.other?.getX()", singletonMap("t", t), true));
        assertEquals(0, exec("let a = {'other': params.t}; return a.other?.x", singletonMap("t", t), true));
        assertEquals(0, exec("let a = {'other': params.t}; return a.other?.getX()", singletonMap("t", t), true));
        assertEquals(0, exec("let a = {'other': params.t}; return a.other?.x", singletonMap("t", t), true));

        // Chains
        assertNull(exec("let a = {'thing': {'cat': params.t}}; return a.other?.cat?.getX()", singletonMap("t", t), true));
        assertNull(exec("let a = {'thing': {'cat': params.t}}; return a.other?.cat?.x", singletonMap("t", t), true));
        assertNull(exec("let a = {'thing': {'cat': params.t}}; return a.other?.cat?.getX()", singletonMap("t", t), true));
        assertNull(exec("let a = {'thing': {'cat': params.t}}; return a.other?.cat?.x", singletonMap("t", t), true));
        assertEquals(0, exec("let a = {'other': {'cat': params.t}}; return a.other?.cat?.getX()", singletonMap("t", t), true));
        assertEquals(0, exec("let a = {'other': {'cat': params.t}}; return a.other?.cat?.x", singletonMap("t", t), true));
        assertEquals(0, exec("let a = {'other': {'cat': params.t}}; return a.other?.cat?.getX()", singletonMap("t", t), true));
        assertEquals(0, exec("let a = {'other': {'cat': params.t}}; return a.other?.cat?.x", singletonMap("t", t), true));

        // Assignments
        assertNull(exec("""
            let a = {};
            a.missing_length = a.missing?.length();
            return a.missing_length""", true));
        assertEquals(3, exec("""
            let a = {};
            a.missing = 'foo';
            a.missing_length = a.missing?.length();
            return a.missing_length""", true));

        // Writes, all unsupported at this point
        // assertEquals(null, exec("org.elasticsearch.javascript.FeatureTestObject a = null; return a?.x")); // Read field
        // assertEquals(null, exec("org.elasticsearch.javascript.FeatureTestObject a = null; a?.x = 7; return a?.x")); // Write field
        // assertEquals(null, exec("Map a = null; a?.other = 'wow'; return a?.other")); // Write shortcut
        // assertEquals(null, exec("def a = null; a?.other = 'cat'; return a?.other")); // Write shortcut
        // assertEquals(null, exec("Map a = ['thing': 'bar']; a.other?.cat = 'no'; return a.other?.cat"));
        // assertEquals(null, exec("def a = ['thing': 'bar']; a.other?.cat = 'no'; return a.other?.cat"));
        // assertEquals(null, exec("Map a = ['thing': 'bar']; a.other?.cat?.dog = 'wombat'; return a.other?.cat?.dog"));
        // assertEquals(null, exec("def a = ['thing': 'bar']; a.other?.cat?.dog = 'wombat'; return a.other?.cat?.dog"));
    }

    @org.junit.Ignore("Painless-only: Comparator.comparing static interface method and lambda; JS grammar does not support this script")
    public void testStaticInterfaceMethod() {
        assertEquals(4, exec("let values = [1, 4, 3, 2]; values.sort(Comparator.comparing(p -> p)); return values[3]"));
    }

}
