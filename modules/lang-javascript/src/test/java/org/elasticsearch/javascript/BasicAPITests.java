/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.javascript;

import java.text.MessageFormat.Field;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class BasicAPITests extends ScriptTestCase {

    public void testListIterator() {
        assertEquals(
            3,
            exec(
                "let x = new ArrayList(); x.add(2); x.add(3); x.add(-2); let y = x.iterator(); "
                    + "let total = 0; while (y.hasNext()) total += y.next(); return total;"
            )
        );
        assertEquals(
            "abc",
            exec(
                "let x = new ArrayList(); x.add(\"a\"); x.add(\"b\"); x.add(\"c\"); "
                    + "let y = x.iterator(); let total = \"\"; while (y.hasNext()) total += y.next(); return total;"
            )
        );
        assertEquals(
            3,
            exec(
                "let x = new ArrayList(); x.add(2); x.add(3); x.add(-2); let y = x.iterator(); "
                    + "let total = 0; while (y.hasNext()) total += y.next(); return total;"
            )
        );
    }

    public void testSetIterator() {
        assertEquals(
            3,
            exec(
                "let x = new HashSet(); x.add(2); x.add(3); x.add(-2); let y = x.iterator(); "
                    + "let total = 0; while (y.hasNext()) total += y.next(); return total;"
            )
        );
        assertEquals(
            "abc",
            exec(
                "let x = new HashSet(); x.add(\"a\"); x.add(\"b\"); x.add(\"c\"); "
                    + "let y = x.iterator(); let total = \"\"; while (y.hasNext()) total += y.next(); return total;"
            )
        );
        assertEquals(
            3,
            exec(
                "let x = new HashSet(); x.add(2); x.add(3); x.add(-2); let y = x.iterator(); "
                    + "let total = 0; while (y.hasNext()) total += (int)y.next(); return total;"
            )
        );
    }

    public void testMapIterator() {
        assertEquals(
            3,
            exec(
                "let x = new HashMap(); x.put(2, 2); x.put(3, 3); x.put(-2, -2); let y = x.keySet().iterator(); "
                    + "let total = 0; while (y.hasNext()) total += (int)y.next(); return total;"
            )
        );
        assertEquals(
            3,
            exec(
                "let x = new HashMap(); x.put(2, 2); x.put(3, 3); x.put(-2, -2); let y = x.values().iterator(); "
                    + "let total = 0; while (y.hasNext()) total += (int)y.next(); return total;"
            )
        );
    }

    /** Test loads and stores with a map */
    public void testMapLoadStore() {
        assertEquals(5, exec("let x = new HashMap(); x.abc = 5; return x.abc;"));
        assertEquals(5, exec("let x = new HashMap(); x['abc'] = 5; return x['abc'];"));
    }

    /** Test loads and stores with update script equivalent */
    public void testUpdateMapLoadStore() {
        Map<String, Object> load = new HashMap<>();
        Map<String, Object> _source = new HashMap<>();
        Map<String, Object> ctx = new HashMap<>();
        Map<String, Object> params = new HashMap<>();

        load.put("load5", "testvalue");
        _source.put("load", load);
        ctx.put("_source", _source);
        params.put("ctx", ctx);

        assertEquals("testvalue", exec("params.ctx._source['load'].5 = params.ctx._source['load'].remove('load5')", params, true));
    }

    /** Test loads and stores with a list */
    public void testListLoadStore() {
        assertEquals(5, exec("let x = new ArrayList(); x.add(3); x[0] = 5; return x[0];"));
        assertEquals(5, exec("let x = new ArrayList(); x.add(3); x[0] = 5; return x[0];"));
    }

    /** Test shortcut for getters with isXXXX */
    public void testListEmpty() {
        assertEquals(true, exec("let x = new ArrayList(); return x.empty;"));
        assertEquals(true, exec("let x = new HashMap(); return x.empty;"));
    }

    /** Test list method invocation */
    public void testListGet() {
        assertEquals(5, exec("let x = new ArrayList(); x.add(5); return x.get(0);"));
        assertEquals(5, exec("let x = new ArrayList(); x.add(5); let index = 0; return x.get(index);"));
    }

    public void testListAsArray() {
        assertEquals(1, exec("let x = new ArrayList(); x.add(5); return x.length"));
        assertEquals(5, exec("let x = new ArrayList(); x.add(5); return x[0]"));
        assertEquals(1, exec("let x = new ArrayList(); x.add('Hallo'); return x.length"));
    }

    public void testDefAssignments() {
        // JS has no (int) cast; use Math.floor for integer truncation (may return Double 2.0)
        assertEquals(2, ((Number) exec("let x; let y = 2.0; x = Math.floor(y); return x;")).intValue());
    }

    public void testInternalBoxing() {
        assertBytecodeExists("let x = true", "INVOKESTATIC java/lang/Boolean.valueOf (Z)Ljava/lang/Boolean;");
        assertBytecodeExists("let x = (byte)1", "INVOKESTATIC java/lang/Byte.valueOf (B)Ljava/lang/Byte;");
        assertBytecodeExists("let x = (short)1", "INVOKESTATIC java/lang/Short.valueOf (S)Ljava/lang/Short;");
        assertBytecodeExists("let x = (char)1", "INVOKESTATIC java/lang/Character.valueOf (C)Ljava/lang/Character;");
        assertBytecodeExists("let x = 1", "INVOKESTATIC java/lang/Integer.valueOf (I)Ljava/lang/Integer;");
        assertBytecodeExists("let x = 1L", "INVOKESTATIC java/lang/Long.valueOf (J)Ljava/lang/Long;");
        assertBytecodeExists("let x = 1F", "INVOKESTATIC java/lang/Float.valueOf (F)Ljava/lang/Float;");
        assertBytecodeExists("let x = 1D", "INVOKESTATIC java/lang/Double.valueOf (D)Ljava/lang/Double;");
    }

    public void testInterfaceDefaultMethods() {
        assertEquals(1, exec("let map = new HashMap(); return map.getOrDefault(5,1);"));
        assertEquals(1, exec("let map = new HashMap(); return map.getOrDefault(5,1);"));
    }

    public void testInterfacesHaveObject() {
        assertEquals("{}", exec("let map = new HashMap(); return map.toString();"));
        assertEquals("{}", exec("let map = new HashMap(); return map.toString();"));
    }

    public void testPrimitivesHaveMethods() {
        assertEquals(5, exec("let x = 5; return x.intValue();"));
        assertEquals("5", exec("let x = 5; return x.toString();"));
        assertEquals(0, exec("let x = 5; return x.compareTo(5);"));
    }

    public void testPublicMemberAccess() {
        assertEquals(5, exec("let ft = new org.elasticsearch.javascript.FeatureTestObject();" + " ft.z = 5; return ft.z;"));
    }

    public void testSetterShortcut() {
        assertEquals(25, exec("let ft = new org.elasticsearch.javascript.FeatureTestObject();" + " ft.y = 25; return ft.y;"));
    }

    public void testNoSemicolon() {
        assertEquals(true, exec("let x = true; if (x) return x"));
    }

    public void testStatic() {
        assertEquals(10, exec("staticAddIntsTest(7, 3)"));
        assertEquals(15.5f, exec("staticAddFloatsTest(6.5f, 9.0f)"));
    }

    public void testRandomUUID() {
        assertTrue(
            Pattern.compile("\\p{XDigit}{8}(-\\p{XDigit}{4}){3}-\\p{XDigit}{12}")
                .matcher(
                    (String) exec(
                        "let a = UUID.randomUUID();"
                            + " let s = a.toString(); "
                            + " let b = UUID.fromString(s);"
                            + " if (a.equals(b) == false) {"
                            + "   throw new RuntimeException('uuids did not match');"
                            + " }"
                            + " return s;"
                    )
                )
                .matches()
        );
    }

    public void testStaticInnerClassResolution() {
        assertEquals(Field.ARGUMENT, exec("MessageFormat.Field.ARGUMENT"));
        assertEquals(Normalizer.Form.NFD, exec("Normalizer.Form.NFD"));
    }
}
