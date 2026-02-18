/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.javascript;

public class ComparisonTests extends ScriptTestCase {

    public void testDefEq() {
        assertEquals(true, exec("let x = 7; let y = 7; return x == y"));
        assertEquals(true, exec("let x = 6; let y = 6; return x == y"));
        assertEquals(true, exec("let x = 5; let y = 5; return x == y"));
        assertEquals(true, exec("let x = 4; let y = 4; return x == y"));
        assertEquals(false, exec("let x = 5; let y = 3; return x == y"));
        assertEquals(false, exec("let x = 6; let y = 2; return x == y"));
        assertEquals(false, exec("let x = 7; let y = 1; return x == y"));

        assertEquals(true, exec("let x = 7; let y = 7; return x == y"));
        assertEquals(true, exec("let x = 6; let y = 6; return x == y"));
        assertEquals(true, exec("let x = 5; let y = 5; return x == y"));
        assertEquals(true, exec("let x = 4; let y = 4; return x == y"));
        assertEquals(false, exec("let x = 5; let y = 3; return x == y"));
        assertEquals(false, exec("let x = 6; let y = 2; return x == y"));
        assertEquals(false, exec("let x = 7; let y = 1; return x == y"));

        assertEquals(true, exec("let x = 7; let y = 7; return x == y"));
        assertEquals(true, exec("let x = 6; let y = 6; return x == y"));
        assertEquals(true, exec("let x = 5; let y = 5; return x == y"));
        assertEquals(true, exec("let x = 4; let y = 4; return x == y"));
        assertEquals(false, exec("let x = 5; let y = 3; return x == y"));
        assertEquals(false, exec("let x = 6; let y = 2; return x == y"));
        assertEquals(false, exec("let x = 7; let y = 1; return x == y"));

        assertEquals(true, exec("let x = 7; let y = 7; return x == y"));
        assertEquals(true, exec("let x = 6; let y = 6; return x == y"));
        assertEquals(true, exec("let x = 5; let y = 5; return x == y"));
        assertEquals(true, exec("let x = 4; let y = 4; return x == y"));
        assertEquals(false, exec("let x = 5; let y = 3; return x == y"));
        assertEquals(false, exec("let x = 6; let y = 2; return x == y"));
        assertEquals(false, exec("let x = 7; let y = 1; return x == y"));

        assertEquals(true, exec("let x = 7; let y = 7; return x == y"));
        assertEquals(true, exec("let x = 6; let y = 6; return x == y"));
        assertEquals(true, exec("let x = 5; let y = 5; return x == y"));
        assertEquals(true, exec("let x = 4; let y = 4; return x == y"));
        assertEquals(false, exec("let x = 5; let y = 3; return x == y"));
        assertEquals(false, exec("let x = 6; let y = 2; return x == y"));
        assertEquals(false, exec("let x = 7; let y = 1; return x == y"));

        assertEquals(false, exec("let x = false; let y = true; return x == y"));
        assertEquals(false, exec("let x = true; let y = false; return x == y"));
        assertEquals(false, exec("let x = true; let y = null; return x == y"));
        assertEquals(false, exec("let x = null; let y = true; return x == y"));
        assertEquals(true, exec("let x = true; let y = true; return x == y"));
        assertEquals(true, exec("let x = false; let y = false; return x == y"));

        assertEquals(true, exec("let x = new HashMap(); let y = new HashMap(); return x == y"));
        assertEquals(false, exec("let x = new HashMap(); x.put(3, 3); let y = new HashMap(); return x == y"));
        assertEquals(true, exec("let x = new HashMap(); x.put(3, 3); let y = new HashMap(); y.put(3, 3); return x == y"));
        assertEquals(true, exec("let x = new HashMap(); let y = x; x.put(3, 3); y.put(3, 3); return x == y"));
    }

    public void testDefEqTypedLHS() {
        assertEquals(true, exec("let x = 7; let y = 7; return x == y"));
        assertEquals(true, exec("let x = 6; let y = 6; return x == y"));
        assertEquals(true, exec("let x = 5; let y = 5; return x == y"));
        assertEquals(true, exec("let x = 4; let y = 4; return x == y"));
        assertEquals(false, exec("let x = 5; let y = 3; return x == y"));
        assertEquals(false, exec("let x = 6; let y = 2; return x == y"));
        assertEquals(false, exec("let x = 7; let y = 1; return x == y"));

        assertEquals(true, exec("let x = 7; let y = 7; return x == y"));
        assertEquals(true, exec("let x = 6; let y = 6; return x == y"));
        assertEquals(true, exec("let x = 5; let y = 5; return x == y"));
        assertEquals(true, exec("let x = 4; let y = 4; return x == y"));
        assertEquals(false, exec("let x = 5; let y = 3; return x == y"));
        assertEquals(false, exec("let x = 6; let y = 2; return x == y"));
        assertEquals(false, exec("let x = 7; let y = 1; return x == y"));

        assertEquals(true, exec("let x = 7; let y = 7; return x == y"));
        assertEquals(true, exec("let x = 6; let y = 6; return x == y"));
        assertEquals(true, exec("let x = 5; let y = 5; return x == y"));
        assertEquals(true, exec("let x = 4; let y = 4; return x == y"));
        assertEquals(false, exec("let x = 5; let y = 3; return x == y"));
        assertEquals(false, exec("let x = 6; let y = 2; return x == y"));
        assertEquals(false, exec("let x = 7; let y = 1; return x == y"));

        assertEquals(true, exec("let x = 7; let y = 7; return x == y"));
        assertEquals(true, exec("let x = 6; let y = 6; return x == y"));
        assertEquals(true, exec("let x = 5; let y = 5; return x == y"));
        assertEquals(true, exec("let x = 4; let y = 4; return x == y"));
        assertEquals(false, exec("let x = 5; let y = 3; return x == y"));
        assertEquals(false, exec("let x = 6; let y = 2; return x == y"));
        assertEquals(false, exec("let x = 7; let y = 1; return x == y"));

        assertEquals(true, exec("let x = 7; let y = 7; return x == y"));
        assertEquals(true, exec("let x = 6; let y = 6; return x == y"));
        assertEquals(true, exec("let x = 5; let y = 5; return x == y"));
        assertEquals(true, exec("let x = 4; let y = 4; return x == y"));
        assertEquals(false, exec("let x = 5; let y = 3; return x == y"));
        assertEquals(false, exec("let x = 6; let y = 2; return x == y"));
        assertEquals(false, exec("let x = 7; let y = 1; return x == y"));

        assertEquals(false, exec("let x = false; let y = true; return x == y"));
        assertEquals(false, exec("let x = true; let y = false; return x == y"));
        assertEquals(false, exec("let x = true; let y = null; return x == y"));
        assertEquals(true, exec("let x = true; let y = true; return x == y"));
        assertEquals(true, exec("let x = false; let y = false; return x == y"));

        assertEquals(true, exec("let x = new HashMap(); let y = new HashMap(); return x == y"));
        assertEquals(false, exec("let x = new HashMap(); x.put(3, 3); let y = new HashMap(); return x == y"));
        assertEquals(true, exec("let x = new HashMap(); x.put(3, 3); let y = new HashMap(); y.put(3, 3); return x == y"));
        assertEquals(true, exec("let x = new HashMap(); let y = x; x.put(3, 3); y.put(3, 3); return x == y"));
    }

    public void testDefEqTypedRHS() {
        assertEquals(true, exec("let x = 7; let y = 7; return x == y"));
        assertEquals(true, exec("let x = 6; let y = 6; return x == y"));
        assertEquals(true, exec("let x = 5; let y = 5; return x == y"));
        assertEquals(true, exec("let x = 4; let y = 4; return x == y"));
        assertEquals(false, exec("let x = 5; let y = 3; return x == y"));
        assertEquals(false, exec("let x = 6; let y = 2; return x == y"));
        assertEquals(false, exec("let x = 7; let y = 1; return x == y"));

        assertEquals(true, exec("let x = 7; let y = 7; return x == y"));
        assertEquals(true, exec("let x = 6; let y = 6; return x == y"));
        assertEquals(true, exec("let x = 5; let y = 5; return x == y"));
        assertEquals(true, exec("let x = 4; let y = 4; return x == y"));
        assertEquals(false, exec("let x = 5; let y = 3; return x == y"));
        assertEquals(false, exec("let x = 6; let y = 2; return x == y"));
        assertEquals(false, exec("let x = 7; let y = 1; return x == y"));

        assertEquals(true, exec("let x = 7; let y = 7; return x == y"));
        assertEquals(true, exec("let x = 6; let y = 6; return x == y"));
        assertEquals(true, exec("let x = 5; let y = 5; return x == y"));
        assertEquals(true, exec("let x = 4; let y = 4; return x == y"));
        assertEquals(false, exec("let x = 5; let y = 3; return x == y"));
        assertEquals(false, exec("let x = 6; let y = 2; return x == y"));
        assertEquals(false, exec("let x = 7; let y = 1; return x == y"));

        assertEquals(true, exec("let x = 7; let y = 7; return x == y"));
        assertEquals(true, exec("let x = 6; let y = 6; return x == y"));
        assertEquals(true, exec("let x = 5; let y = 5; return x == y"));
        assertEquals(true, exec("let x = 4; let y = 4; return x == y"));
        assertEquals(false, exec("let x = 5; let y = 3; return x == y"));
        assertEquals(false, exec("let x = 6; let y = 2; return x == y"));
        assertEquals(false, exec("let x = 7; let y = 1; return x == y"));

        assertEquals(true, exec("let x = 7; let y = 7; return x == y"));
        assertEquals(true, exec("let x = 6; let y = 6; return x == y"));
        assertEquals(true, exec("let x = 5; let y = 5; return x == y"));
        assertEquals(true, exec("let x = 4; let y = 4; return x == y"));
        assertEquals(false, exec("let x = 5; let y = 3; return x == y"));
        assertEquals(false, exec("let x = 6; let y = 2; return x == y"));
        assertEquals(false, exec("let x = 7; let y = 1; return x == y"));

        assertEquals(false, exec("let x = false; let y = true; return x == y"));
        assertEquals(false, exec("let x = true; let y = false; return x == y"));
        assertEquals(false, exec("let x = null; let y = true; return x == y"));
        assertEquals(true, exec("let x = true; let y = true; return x == y"));
        assertEquals(true, exec("let x = false; let y = false; return x == y"));

        assertEquals(true, exec("let x = new HashMap(); let y = new HashMap(); return x == y"));
        assertEquals(false, exec("let x = new HashMap(); x.put(3, 3); let y = new HashMap(); return x == y"));
        assertEquals(true, exec("let x = new HashMap(); x.put(3, 3); let y = new HashMap(); y.put(3, 3); return x == y"));
        assertEquals(true, exec("let x = new HashMap(); let y = x; x.put(3, 3); y.put(3, 3); return x == y"));
    }

    public void testDefEqr() {
        assertEquals(false, exec("let x = 7; let y = 7; return x === y"));
        assertEquals(false, exec("let x = 6; let y = 6; return x === y"));
        assertEquals(false, exec("let x = 5; let y = 5; return x === y"));
        assertEquals(true, exec("let x = 4; let y = 4; return x === y"));
        assertEquals(false, exec("let x = 5; let y = 3; return x === y"));
        assertEquals(false, exec("let x = 6; let y = 2; return x === y"));
        assertEquals(false, exec("let x = 7; let y = 1; return x === y"));

        assertEquals(false, exec("let x = false; let y = true; return x === y"));

        assertEquals(false, exec("let x = new HashMap(); let y = new HashMap(); return x === y"));
        assertEquals(false, exec("let x = new HashMap(); x.put(3, 3); let y = new HashMap(); return x === y"));
        assertEquals(false, exec("let x = new HashMap(); x.put(3, 3); let y = new HashMap(); y.put(3, 3); return x === y"));
        assertEquals(true, exec("let x = new HashMap(); let y = x; x.put(3, 3); y.put(3, 3); return x === y"));
    }

    public void testDefNe() {
        assertEquals(false, exec("let x = 7; let y = 7; return x != y"));
        assertEquals(false, exec("let x = 6; let y = 6; return x != y"));
        assertEquals(false, exec("let x = 5; let y = 5; return x != y"));
        assertEquals(false, exec("let x = 4; let y = 4; return x != y"));
        assertEquals(true, exec("let x = 5; let y = 3; return x != y"));
        assertEquals(true, exec("let x = 6; let y = 2; return x != y"));
        assertEquals(true, exec("let x = 7; let y = 1; return x != y"));

        assertEquals(false, exec("let x = 7; let y = 7; return x != y"));
        assertEquals(false, exec("let x = 6; let y = 6; return x != y"));
        assertEquals(false, exec("let x = 5; let y = 5; return x != y"));
        assertEquals(false, exec("let x = 4; let y = 4; return x != y"));
        assertEquals(true, exec("let x = 5; let y = 3; return x != y"));
        assertEquals(true, exec("let x = 6; let y = 2; return x != y"));
        assertEquals(true, exec("let x = 7; let y = 1; return x != y"));

        assertEquals(false, exec("let x = 7; let y = 7; return x != y"));
        assertEquals(false, exec("let x = 6; let y = 6; return x != y"));
        assertEquals(false, exec("let x = 5; let y = 5; return x != y"));
        assertEquals(false, exec("let x = 4; let y = 4; return x != y"));
        assertEquals(true, exec("let x = 5; let y = 3; return x != y"));
        assertEquals(true, exec("let x = 6; let y = 2; return x != y"));
        assertEquals(true, exec("let x = 7; let y = 1; return x != y"));

        assertEquals(false, exec("let x = 7; let y = 7; return x != y"));
        assertEquals(false, exec("let x = 6; let y = 6; return x != y"));
        assertEquals(false, exec("let x = 5; let y = 5; return x != y"));
        assertEquals(false, exec("let x = 4; let y = 4; return x != y"));
        assertEquals(true, exec("let x = 5; let y = 3; return x != y"));
        assertEquals(true, exec("let x = 6; let y = 2; return x != y"));
        assertEquals(true, exec("let x = 7; let y = 1; return x != y"));

        assertEquals(false, exec("let x = 7; let y = 7; return x != y"));
        assertEquals(false, exec("let x = 6; let y = 6; return x != y"));
        assertEquals(false, exec("let x = 5; let y = 5; return x != y"));
        assertEquals(false, exec("let x = 4; let y = 4; return x != y"));
        assertEquals(true, exec("let x = 5; let y = 3; return x != y"));
        assertEquals(true, exec("let x = 6; let y = 2; return x != y"));
        assertEquals(true, exec("let x = 7; let y = 1; return x != y"));

        assertEquals(false, exec("let x = new HashMap(); let y = new HashMap(); return x != y"));
        assertEquals(true, exec("let x = new HashMap(); x.put(3, 3); let y = new HashMap(); return x != y"));
        assertEquals(false, exec("let x = new HashMap(); x.put(3, 3); let y = new HashMap(); y.put(3, 3); return x != y"));
        assertEquals(false, exec("let x = new HashMap(); let y = x; x.put(3, 3); y.put(3, 3); return x != y"));

        assertEquals(false, exec("let x = true;  let y = true; return x != y"));
        assertEquals(true, exec("let x = true;  let y = false; return x != y"));
        assertEquals(true, exec("let x = false; let y = true; return x != y"));
        assertEquals(false, exec("let x = false; let y = false; return x != y"));
    }

    public void testDefNeTypedLHS() {
        assertEquals(false, exec("let x = 7; let y = 7; return x != y"));
        assertEquals(false, exec("let x = 6; let y = 6; return x != y"));
        assertEquals(false, exec("let x = 5; let y = 5; return x != y"));
        assertEquals(false, exec("let x = 4; let y = 4; return x != y"));
        assertEquals(true, exec("let x = 5; let y = 3; return x != y"));
        assertEquals(true, exec("let x = 6; let y = 2; return x != y"));
        assertEquals(true, exec("let x = 7; let y = 1; return x != y"));

        assertEquals(false, exec("let x = 7; let y = 7; return x != y"));
        assertEquals(false, exec("let x = 6; let y = 6; return x != y"));
        assertEquals(false, exec("let x = 5; let y = 5; return x != y"));
        assertEquals(false, exec("let x = 4; let y = 4; return x != y"));
        assertEquals(true, exec("let x = 5; let y = 3; return x != y"));
        assertEquals(true, exec("let x = 6; let y = 2; return x != y"));
        assertEquals(true, exec("let x = 7; let y = 1; return x != y"));

        assertEquals(false, exec("let x = 7; let y = 7; return x != y"));
        assertEquals(false, exec("let x = 6; let y = 6; return x != y"));
        assertEquals(false, exec("let x = 5; let y = 5; return x != y"));
        assertEquals(false, exec("let x = 4; let y = 4; return x != y"));
        assertEquals(true, exec("let x = 5; let y = 3; return x != y"));
        assertEquals(true, exec("let x = 6; let y = 2; return x != y"));
        assertEquals(true, exec("let x = 7; let y = 1; return x != y"));

        assertEquals(false, exec("let x = 7; let y = 7; return x != y"));
        assertEquals(false, exec("let x = 6; let y = 6; return x != y"));
        assertEquals(false, exec("let x = 5; let y = 5; return x != y"));
        assertEquals(false, exec("let x = 4; let y = 4; return x != y"));
        assertEquals(true, exec("let x = 5; let y = 3; return x != y"));
        assertEquals(true, exec("let x = 6; let y = 2; return x != y"));
        assertEquals(true, exec("let x = 7; let y = 1; return x != y"));

        assertEquals(false, exec("let x = 7; let y = 7; return x != y"));
        assertEquals(false, exec("let x = 6; let y = 6; return x != y"));
        assertEquals(false, exec("let x = 5; let y = 5; return x != y"));
        assertEquals(false, exec("let x = 4; let y = 4; return x != y"));
        assertEquals(true, exec("let x = 5; let y = 3; return x != y"));
        assertEquals(true, exec("let x = 6; let y = 2; return x != y"));
        assertEquals(true, exec("let x = 7; let y = 1; return x != y"));

        assertEquals(false, exec("let x = new HashMap(); let y = new HashMap(); return x != y"));
        assertEquals(true, exec("let x = new HashMap(); x.put(3, 3); let y = new HashMap(); return x != y"));
        assertEquals(false, exec("let x = new HashMap(); x.put(3, 3); let y = new HashMap(); y.put(3, 3); return x != y"));
        assertEquals(false, exec("let x = new HashMap(); let y = x; x.put(3, 3); y.put(3, 3); return x != y"));

        assertEquals(false, exec("let x = true;  let y = true; return x != y"));
        assertEquals(true, exec("let x = true;  let y = false; return x != y"));
        assertEquals(true, exec("let x = false; let y = true; return x != y"));
        assertEquals(false, exec("let x = false; let y = false; return x != y"));
    }

    public void testDefNeTypedRHS() {
        assertEquals(false, exec("let x = 7; let y = 7; return x != y"));
        assertEquals(false, exec("let x = 6; let y = 6; return x != y"));
        assertEquals(false, exec("let x = 5; let y = 5; return x != y"));
        assertEquals(false, exec("let x = 4; let y = 4; return x != y"));
        assertEquals(true, exec("let x = 5; let y = 3; return x != y"));
        assertEquals(true, exec("let x = 6; let y = 2; return x != y"));
        assertEquals(true, exec("let x = 7; let y = 1; return x != y"));

        assertEquals(false, exec("let x = 7; let y = 7; return x != y"));
        assertEquals(false, exec("let x = 6; let y = 6; return x != y"));
        assertEquals(false, exec("let x = 5; let y = 5; return x != y"));
        assertEquals(false, exec("let x = 4; let y = 4; return x != y"));
        assertEquals(true, exec("let x = 5; let y = 3; return x != y"));
        assertEquals(true, exec("let x = 6; let y = 2; return x != y"));
        assertEquals(true, exec("let x = 7; let y = 1; return x != y"));

        assertEquals(false, exec("let x = 7; let y = 7; return x != y"));
        assertEquals(false, exec("let x = 6; let y = 6; return x != y"));
        assertEquals(false, exec("let x = 5; let y = 5; return x != y"));
        assertEquals(false, exec("let x = 4; let y = 4; return x != y"));
        assertEquals(true, exec("let x = 5; let y = 3; return x != y"));
        assertEquals(true, exec("let x = 6; let y = 2; return x != y"));
        assertEquals(true, exec("let x = 7; let y = 1; return x != y"));

        assertEquals(false, exec("let x = 7; let y = 7; return x != y"));
        assertEquals(false, exec("let x = 6; let y = 6; return x != y"));
        assertEquals(false, exec("let x = 5; let y = 5; return x != y"));
        assertEquals(false, exec("let x = 4; let y = 4; return x != y"));
        assertEquals(true, exec("let x = 5; let y = 3; return x != y"));
        assertEquals(true, exec("let x = 6; let y = 2; return x != y"));
        assertEquals(true, exec("let x = 7; let y = 1; return x != y"));

        assertEquals(false, exec("let x = 7; let y = 7; return x != y"));
        assertEquals(false, exec("let x = 6; let y = 6; return x != y"));
        assertEquals(false, exec("let x = 5; let y = 5; return x != y"));
        assertEquals(false, exec("let x = 4; let y = 4; return x != y"));
        assertEquals(true, exec("let x = 5; let y = 3; return x != y"));
        assertEquals(true, exec("let x = 6; let y = 2; return x != y"));
        assertEquals(true, exec("let x = 7; let y = 1; return x != y"));

        assertEquals(false, exec("let x = new HashMap(); let y = new HashMap(); return x != y"));
        assertEquals(true, exec("let x = new HashMap(); x.put(3, 3); let y = new HashMap(); return x != y"));
        assertEquals(false, exec("let x = new HashMap(); x.put(3, 3); let y = new HashMap(); y.put(3, 3); return x != y"));
        assertEquals(false, exec("let x = new HashMap(); let y = x; x.put(3, 3); y.put(3, 3); return x != y"));

        assertEquals(false, exec("let x = true;  let y = true; return x != y"));
        assertEquals(true, exec("let x = true;  let y = false; return x != y"));
        assertEquals(true, exec("let x = false; let y = true; return x != y"));
        assertEquals(false, exec("let x = false; let y = false; return x != y"));
    }

    public void testDefNer() {
        assertEquals(true, exec("let x = 7; let y = 7; return x !== y"));
        assertEquals(true, exec("let x = 6; let y = 6; return x !== y"));
        assertEquals(true, exec("let x = 5; let y = 5; return x !== y"));
        assertEquals(false, exec("let x = 4; let y = 4; return x !== y"));
        assertEquals(true, exec("let x = 5; let y = 3; return x !== y"));
        assertEquals(true, exec("let x = 6; let y = 2; return x !== y"));
        assertEquals(true, exec("let x = 7; let y = 1; return x !== y"));

        assertEquals(true, exec("let x = new HashMap(); let y = new HashMap(); return x !== y"));
        assertEquals(true, exec("let x = new HashMap(); x.put(3, 3); let y = new HashMap(); return x !== y"));
        assertEquals(true, exec("let x = new HashMap(); x.put(3, 3); let y = new HashMap(); y.put(3, 3); return x !== y"));
        assertEquals(false, exec("let x = new HashMap(); let y = x; x.put(3, 3); y.put(3, 3); return x !== y"));
    }

    public void testDefLt() {
        assertEquals(true, exec("let x = 1; let y = 7; return x < y"));
        assertEquals(true, exec("let x = 2; let y = 6; return x < y"));
        assertEquals(true, exec("let x = 3; let y = 5; return x < y"));
        assertEquals(false, exec("let x = 4; let y = 4; return x < y"));
        assertEquals(false, exec("let x = 5; let y = 3; return x < y"));
        assertEquals(false, exec("let x = 6; let y = 2; return x < y"));
        assertEquals(false, exec("let x = 7; let y = 1; return x < y"));

        assertEquals(true, exec("let x = 1; let y = 7; return x < y"));
        assertEquals(true, exec("let x = 2; let y = 6; return x < y"));
        assertEquals(true, exec("let x = 3; let y = 5; return x < y"));
        assertEquals(false, exec("let x = 4; let y = 4; return x < y"));
        assertEquals(false, exec("let x = 5; let y = 3; return x < y"));
        assertEquals(false, exec("let x = 6; let y = 2; return x < y"));
        assertEquals(false, exec("let x = 7; let y = 1; return x < y"));

        assertEquals(true, exec("let x = 1; let y = 7; return x < y"));
        assertEquals(true, exec("let x = 2; let y = 6; return x < y"));
        assertEquals(true, exec("let x = 3; let y = 5; return x < y"));
        assertEquals(false, exec("let x = 4; let y = 4; return x < y"));
        assertEquals(false, exec("let x = 5; let y = 3; return x < y"));
        assertEquals(false, exec("let x = 6; let y = 2; return x < y"));
        assertEquals(false, exec("let x = 7; let y = 1; return x < y"));

        assertEquals(true, exec("let x = 1; let y = 7; return x < y"));
        assertEquals(true, exec("let x = 2; let y = 6; return x < y"));
        assertEquals(true, exec("let x = 3; let y = 5; return x < y"));
        assertEquals(false, exec("let x = 4; let y = 4; return x < y"));
        assertEquals(false, exec("let x = 5; let y = 3; return x < y"));
        assertEquals(false, exec("let x = 6; let y = 2; return x < y"));
        assertEquals(false, exec("let x = 7; let y = 1; return x < y"));
    }

    public void testDefLtTypedLHS() {
        assertEquals(true, exec("let x = 1; let y = 7; return x < y"));
        assertEquals(true, exec("let x = 2; let y = 6; return x < y"));
        assertEquals(true, exec("let x = 3; let y = 5; return x < y"));
        assertEquals(false, exec("let x = 4; let y = 4; return x < y"));
        assertEquals(false, exec("let x = 5; let y = 3; return x < y"));
        assertEquals(false, exec("let x = 6; let y = 2; return x < y"));
        assertEquals(false, exec("let x = 7; let y = 1; return x < y"));

        assertEquals(true, exec("let x = 1; let y = 7; return x < y"));
        assertEquals(true, exec("let x = 2; let y = 6; return x < y"));
        assertEquals(true, exec("let x = 3; let y = 5; return x < y"));
        assertEquals(false, exec("let x = 4; let y = 4; return x < y"));
        assertEquals(false, exec("let x = 5; let y = 3; return x < y"));
        assertEquals(false, exec("let x = 6; let y = 2; return x < y"));
        assertEquals(false, exec("let x = 7; let y = 1; return x < y"));

        assertEquals(true, exec("let x = 1; let y = 7; return x < y"));
        assertEquals(true, exec("let x = 2; let y = 6; return x < y"));
        assertEquals(true, exec("let x = 3; let y = 5; return x < y"));
        assertEquals(false, exec("let x = 4; let y = 4; return x < y"));
        assertEquals(false, exec("let x = 5; let y = 3; return x < y"));
        assertEquals(false, exec("let x = 6; let y = 2; return x < y"));
        assertEquals(false, exec("let x = 7; let y = 1; return x < y"));

        assertEquals(true, exec("let x = 1; let y = 7; return x < y"));
        assertEquals(true, exec("let x = 2; let y = 6; return x < y"));
        assertEquals(true, exec("let x = 3; let y = 5; return x < y"));
        assertEquals(false, exec("let x = 4; let y = 4; return x < y"));
        assertEquals(false, exec("let x = 5; let y = 3; return x < y"));
        assertEquals(false, exec("let x = 6; let y = 2; return x < y"));
        assertEquals(false, exec("let x = 7; let y = 1; return x < y"));
    }

    public void testDefLtTypedRHS() {
        assertEquals(true, exec("let x = 1; let y = 7; return x < y"));
        assertEquals(true, exec("let x = 2; let y = 6; return x < y"));
        assertEquals(true, exec("let x = 3; let y = 5; return x < y"));
        assertEquals(false, exec("let x = 4; let y = 4; return x < y"));
        assertEquals(false, exec("let x = 5; let y = 3; return x < y"));
        assertEquals(false, exec("let x = 6; let y = 2; return x < y"));
        assertEquals(false, exec("let x = 7; let y = 1; return x < y"));

        assertEquals(true, exec("let x = 1; let y = 7; return x < y"));
        assertEquals(true, exec("let x = 2; let y = 6; return x < y"));
        assertEquals(true, exec("let x = 3; let y = 5; return x < y"));
        assertEquals(false, exec("let x = 4; let y = 4; return x < y"));
        assertEquals(false, exec("let x = 5; let y = 3; return x < y"));
        assertEquals(false, exec("let x = 6; let y = 2; return x < y"));
        assertEquals(false, exec("let x = 7; let y = 1; return x < y"));

        assertEquals(true, exec("let x = 1; let y = 7; return x < y"));
        assertEquals(true, exec("let x = 2; let y = 6; return x < y"));
        assertEquals(true, exec("let x = 3; let y = 5; return x < y"));
        assertEquals(false, exec("let x = 4; let y = 4; return x < y"));
        assertEquals(false, exec("let x = 5; let y = 3; return x < y"));
        assertEquals(false, exec("let x = 6; let y = 2; return x < y"));
        assertEquals(false, exec("let x = 7; let y = 1; return x < y"));

        assertEquals(true, exec("let x = 1; let y = 7; return x < y"));
        assertEquals(true, exec("let x = 2; let y = 6; return x < y"));
        assertEquals(true, exec("let x = 3; let y = 5; return x < y"));
        assertEquals(false, exec("let x = 4; let y = 4; return x < y"));
        assertEquals(false, exec("let x = 5; let y = 3; return x < y"));
        assertEquals(false, exec("let x = 6; let y = 2; return x < y"));
        assertEquals(false, exec("let x = 7; let y = 1; return x < y"));
    }

    public void testDefLte() {
        assertEquals(true, exec("let x = 1; let y = 7; return x <= y"));
        assertEquals(true, exec("let x = 2; let y = 6; return x <= y"));
        assertEquals(true, exec("let x = 3; let y = 5; return x <= y"));
        assertEquals(true, exec("let x = 4; let y = 4; return x <= y"));
        assertEquals(false, exec("let x = 5; let y = 3; return x <= y"));
        assertEquals(false, exec("let x = 6; let y = 2; return x <= y"));
        assertEquals(false, exec("let x = 7; let y = 1; return x <= y"));

        assertEquals(true, exec("let x = 1; let y = 7; return x <= y"));
        assertEquals(true, exec("let x = 2; let y = 6; return x <= y"));
        assertEquals(true, exec("let x = 3; let y = 5; return x <= y"));
        assertEquals(true, exec("let x = 4; let y = 4; return x <= y"));
        assertEquals(false, exec("let x = 5; let y = 3; return x <= y"));
        assertEquals(false, exec("let x = 6; let y = 2; return x <= y"));
        assertEquals(false, exec("let x = 7; let y = 1; return x <= y"));

        assertEquals(true, exec("let x = 1; let y = 7; return x <= y"));
        assertEquals(true, exec("let x = 2; let y = 6; return x <= y"));
        assertEquals(true, exec("let x = 3; let y = 5; return x <= y"));
        assertEquals(true, exec("let x = 4; let y = 4; return x <= y"));
        assertEquals(false, exec("let x = 5; let y = 3; return x <= y"));
        assertEquals(false, exec("let x = 6; let y = 2; return x <= y"));
        assertEquals(false, exec("let x = 7; let y = 1; return x <= y"));

        assertEquals(true, exec("let x = 1; let y = 7; return x <= y"));
        assertEquals(true, exec("let x = 2; let y = 6; return x <= y"));
        assertEquals(true, exec("let x = 3; let y = 5; return x <= y"));
        assertEquals(true, exec("let x = 4; let y = 4; return x <= y"));
        assertEquals(false, exec("let x = 5; let y = 3; return x <= y"));
        assertEquals(false, exec("let x = 6; let y = 2; return x <= y"));
        assertEquals(false, exec("let x = 7; let y = 1; return x <= y"));
    }

    public void testDefLteTypedLHS() {
        assertEquals(true, exec("let x = 1; let y = 7; return x <= y"));
        assertEquals(true, exec("let x = 2; let y = 6; return x <= y"));
        assertEquals(true, exec("let x = 3; let y = 5; return x <= y"));
        assertEquals(true, exec("let x = 4; let y = 4; return x <= y"));
        assertEquals(false, exec("let x = 5; let y = 3; return x <= y"));
        assertEquals(false, exec("let x = 6; let y = 2; return x <= y"));
        assertEquals(false, exec("let x = 7; let y = 1; return x <= y"));

        assertEquals(true, exec("let x = 1; let y = 7; return x <= y"));
        assertEquals(true, exec("let x = 2; let y = 6; return x <= y"));
        assertEquals(true, exec("let x = 3; let y = 5; return x <= y"));
        assertEquals(true, exec("let x = 4; let y = 4; return x <= y"));
        assertEquals(false, exec("let x = 5; let y = 3; return x <= y"));
        assertEquals(false, exec("let x = 6; let y = 2; return x <= y"));
        assertEquals(false, exec("let x = 7; let y = 1; return x <= y"));

        assertEquals(true, exec("let x = 1; let y = 7; return x <= y"));
        assertEquals(true, exec("let x = 2; let y = 6; return x <= y"));
        assertEquals(true, exec("let x = 3; let y = 5; return x <= y"));
        assertEquals(true, exec("let x = 4; let y = 4; return x <= y"));
        assertEquals(false, exec("let x = 5; let y = 3; return x <= y"));
        assertEquals(false, exec("let x = 6; let y = 2; return x <= y"));
        assertEquals(false, exec("let x = 7; let y = 1; return x <= y"));

        assertEquals(true, exec("let x = 1; let y = 7; return x <= y"));
        assertEquals(true, exec("let x = 2; let y = 6; return x <= y"));
        assertEquals(true, exec("let x = 3; let y = 5; return x <= y"));
        assertEquals(true, exec("let x = 4; let y = 4; return x <= y"));
        assertEquals(false, exec("let x = 5; let y = 3; return x <= y"));
        assertEquals(false, exec("let x = 6; let y = 2; return x <= y"));
        assertEquals(false, exec("let x = 7; let y = 1; return x <= y"));
    }

    public void testDefLteTypedRHS() {
        assertEquals(true, exec("let x = 1; let y = 7; return x <= y"));
        assertEquals(true, exec("let x = 2; let y = 6; return x <= y"));
        assertEquals(true, exec("let x = 3; let y = 5; return x <= y"));
        assertEquals(true, exec("let x = 4; let y = 4; return x <= y"));
        assertEquals(false, exec("let x = 5; let y = 3; return x <= y"));
        assertEquals(false, exec("let x = 6; let y = 2; return x <= y"));
        assertEquals(false, exec("let x = 7; let y = 1; return x <= y"));

        assertEquals(true, exec("let x = 1; let y = 7; return x <= y"));
        assertEquals(true, exec("let x = 2; let y = 6; return x <= y"));
        assertEquals(true, exec("let x = 3; let y = 5; return x <= y"));
        assertEquals(true, exec("let x = 4; let y = 4; return x <= y"));
        assertEquals(false, exec("let x = 5; let y = 3; return x <= y"));
        assertEquals(false, exec("let x = 6; let y = 2; return x <= y"));
        assertEquals(false, exec("let x = 7; let y = 1; return x <= y"));

        assertEquals(true, exec("let x = 1; let y = 7; return x <= y"));
        assertEquals(true, exec("let x = 2; let y = 6; return x <= y"));
        assertEquals(true, exec("let x = 3; let y = 5; return x <= y"));
        assertEquals(true, exec("let x = 4; let y = 4; return x <= y"));
        assertEquals(false, exec("let x = 5; let y = 3; return x <= y"));
        assertEquals(false, exec("let x = 6; let y = 2; return x <= y"));
        assertEquals(false, exec("let x = 7; let y = 1; return x <= y"));

        assertEquals(true, exec("let x = 1; let y = 7; return x <= y"));
        assertEquals(true, exec("let x = 2; let y = 6; return x <= y"));
        assertEquals(true, exec("let x = 3; let y = 5; return x <= y"));
        assertEquals(true, exec("let x = 4; let y = 4; return x <= y"));
        assertEquals(false, exec("let x = 5; let y = 3; return x <= y"));
        assertEquals(false, exec("let x = 6; let y = 2; return x <= y"));
        assertEquals(false, exec("let x = 7; let y = 1; return x <= y"));
    }

    public void testDefGt() {
        assertEquals(false, exec("let x = 1; let y = 7; return x > y"));
        assertEquals(false, exec("let x = 2; let y = 6; return x > y"));
        assertEquals(false, exec("let x = 3; let y = 5; return x > y"));
        assertEquals(false, exec("let x = 4; let y = 4; return x > y"));
        assertEquals(true, exec("let x = 5; let y = 3; return x > y"));
        assertEquals(true, exec("let x = 6; let y = 2; return x > y"));
        assertEquals(true, exec("let x = 7; let y = 1; return x > y"));

        assertEquals(false, exec("let x = 1; let y = 7; return x > y"));
        assertEquals(false, exec("let x = 2; let y = 6; return x > y"));
        assertEquals(false, exec("let x = 3; let y = 5; return x > y"));
        assertEquals(false, exec("let x = 4; let y = 4; return x > y"));
        assertEquals(true, exec("let x = 5; let y = 3; return x > y"));
        assertEquals(true, exec("let x = 6; let y = 2; return x > y"));
        assertEquals(true, exec("let x = 7; let y = 1; return x > y"));

        assertEquals(false, exec("let x = 1; let y = 7; return x > y"));
        assertEquals(false, exec("let x = 2; let y = 6; return x > y"));
        assertEquals(false, exec("let x = 3; let y = 5; return x > y"));
        assertEquals(false, exec("let x = 4; let y = 4; return x > y"));
        assertEquals(true, exec("let x = 5; let y = 3; return x > y"));
        assertEquals(true, exec("let x = 6; let y = 2; return x > y"));
        assertEquals(true, exec("let x = 7; let y = 1; return x > y"));

        assertEquals(false, exec("let x = 1; let y = 7; return x > y"));
        assertEquals(false, exec("let x = 2; let y = 6; return x > y"));
        assertEquals(false, exec("let x = 3; let y = 5; return x > y"));
        assertEquals(false, exec("let x = 4; let y = 4; return x > y"));
        assertEquals(true, exec("let x = 5; let y = 3; return x > y"));
        assertEquals(true, exec("let x = 6; let y = 2; return x > y"));
        assertEquals(true, exec("let x = 7; let y = 1; return x > y"));
    }

    public void testDefGtTypedLHS() {
        assertEquals(false, exec("let x = 1; let y = 7; return x > y"));
        assertEquals(false, exec("let x = 2; let y = 6; return x > y"));
        assertEquals(false, exec("let x = 3; let y = 5; return x > y"));
        assertEquals(false, exec("let x = 4; let y = 4; return x > y"));
        assertEquals(true, exec("let x = 5; let y = 3; return x > y"));
        assertEquals(true, exec("let x = 6; let y = 2; return x > y"));
        assertEquals(true, exec("let x = 7; let y = 1; return x > y"));

        assertEquals(false, exec("let x = 1; let y = 7; return x > y"));
        assertEquals(false, exec("let x = 2; let y = 6; return x > y"));
        assertEquals(false, exec("let x = 3; let y = 5; return x > y"));
        assertEquals(false, exec("let x = 4; let y = 4; return x > y"));
        assertEquals(true, exec("let x = 5; let y = 3; return x > y"));
        assertEquals(true, exec("let x = 6; let y = 2; return x > y"));
        assertEquals(true, exec("let x = 7; let y = 1; return x > y"));

        assertEquals(false, exec("let x = 1; let y = 7; return x > y"));
        assertEquals(false, exec("let x = 2; let y = 6; return x > y"));
        assertEquals(false, exec("let x = 3; let y = 5; return x > y"));
        assertEquals(false, exec("let x = 4; let y = 4; return x > y"));
        assertEquals(true, exec("let x = 5; let y = 3; return x > y"));
        assertEquals(true, exec("let x = 6; let y = 2; return x > y"));
        assertEquals(true, exec("let x = 7; let y = 1; return x > y"));

        assertEquals(false, exec("let x = 1; let y = 7; return x > y"));
        assertEquals(false, exec("let x = 2; let y = 6; return x > y"));
        assertEquals(false, exec("let x = 3; let y = 5; return x > y"));
        assertEquals(false, exec("let x = 4; let y = 4; return x > y"));
        assertEquals(true, exec("let x = 5; let y = 3; return x > y"));
        assertEquals(true, exec("let x = 6; let y = 2; return x > y"));
        assertEquals(true, exec("let x = 7; let y = 1; return x > y"));
    }

    public void testDefGtTypedRHS() {
        assertEquals(false, exec("let x = 1; let y = 7; return x > y"));
        assertEquals(false, exec("let x = 2; let y = 6; return x > y"));
        assertEquals(false, exec("let x = 3; let y = 5; return x > y"));
        assertEquals(false, exec("let x = 4; let y = 4; return x > y"));
        assertEquals(true, exec("let x = 5; let y = 3; return x > y"));
        assertEquals(true, exec("let x = 6; let y = 2; return x > y"));
        assertEquals(true, exec("let x = 7; let y = 1; return x > y"));

        assertEquals(false, exec("let x = 1; let y = 7; return x > y"));
        assertEquals(false, exec("let x = 2; let y = 6; return x > y"));
        assertEquals(false, exec("let x = 3; let y = 5; return x > y"));
        assertEquals(false, exec("let x = 4; let y = 4; return x > y"));
        assertEquals(true, exec("let x = 5; let y = 3; return x > y"));
        assertEquals(true, exec("let x = 6; let y = 2; return x > y"));
        assertEquals(true, exec("let x = 7; let y = 1; return x > y"));

        assertEquals(false, exec("let x = 1; let y = 7; return x > y"));
        assertEquals(false, exec("let x = 2; let y = 6; return x > y"));
        assertEquals(false, exec("let x = 3; let y = 5; return x > y"));
        assertEquals(false, exec("let x = 4; let y = 4; return x > y"));
        assertEquals(true, exec("let x = 5; let y = 3; return x > y"));
        assertEquals(true, exec("let x = 6; let y = 2; return x > y"));
        assertEquals(true, exec("let x = 7; let y = 1; return x > y"));

        assertEquals(false, exec("let x = 1; let y = 7; return x > y"));
        assertEquals(false, exec("let x = 2; let y = 6; return x > y"));
        assertEquals(false, exec("let x = 3; let y = 5; return x > y"));
        assertEquals(false, exec("let x = 4; let y = 4; return x > y"));
        assertEquals(true, exec("let x = 5; let y = 3; return x > y"));
        assertEquals(true, exec("let x = 6; let y = 2; return x > y"));
        assertEquals(true, exec("let x = 7; let y = 1; return x > y"));
    }

    public void testDefGte() {
        assertEquals(false, exec("let x = 1; let y = 7; return x >= y"));
        assertEquals(false, exec("let x = 2; let y = 6; return x >= y"));
        assertEquals(false, exec("let x = 3; let y = 5; return x >= y"));
        assertEquals(true, exec("let x = 4; let y = 4; return x >= y"));
        assertEquals(true, exec("let x = 5; let y = 3; return x >= y"));
        assertEquals(true, exec("let x = 6; let y = 2; return x >= y"));
        assertEquals(true, exec("let x = 7; let y = 1; return x >= y"));

        assertEquals(false, exec("let x = 1; let y = 7; return x >= y"));
        assertEquals(false, exec("let x = 2; let y = 6; return x >= y"));
        assertEquals(false, exec("let x = 3; let y = 5; return x >= y"));
        assertEquals(true, exec("let x = 4; let y = 4; return x >= y"));
        assertEquals(true, exec("let x = 5; let y = 3; return x >= y"));
        assertEquals(true, exec("let x = 6; let y = 2; return x >= y"));
        assertEquals(true, exec("let x = 7; let y = 1; return x >= y"));

        assertEquals(false, exec("let x = 1; let y = 7; return x >= y"));
        assertEquals(false, exec("let x = 2; let y = 6; return x >= y"));
        assertEquals(false, exec("let x = 3; let y = 5; return x >= y"));
        assertEquals(true, exec("let x = 4; let y = 4; return x >= y"));
        assertEquals(true, exec("let x = 5; let y = 3; return x >= y"));
        assertEquals(true, exec("let x = 6; let y = 2; return x >= y"));
        assertEquals(true, exec("let x = 7; let y = 1; return x >= y"));

        assertEquals(false, exec("let x = 1; let y = 7; return x >= y"));
        assertEquals(false, exec("let x = 2; let y = 6; return x >= y"));
        assertEquals(false, exec("let x = 3; let y = 5; return x >= y"));
        assertEquals(true, exec("let x = 4; let y = 4; return x >= y"));
        assertEquals(true, exec("let x = 5; let y = 3; return x >= y"));
        assertEquals(true, exec("let x = 6; let y = 2; return x >= y"));
        assertEquals(true, exec("let x = 7; let y = 1; return x >= y"));
    }

    public void testDefGteTypedLHS() {
        assertEquals(false, exec("let x = 1; let y = 7; return x >= y"));
        assertEquals(false, exec("let x = 2; let y = 6; return x >= y"));
        assertEquals(false, exec("let x = 3; let y = 5; return x >= y"));
        assertEquals(true, exec("let x = 4; let y = 4; return x >= y"));
        assertEquals(true, exec("let x = 5; let y = 3; return x >= y"));
        assertEquals(true, exec("let x = 6; let y = 2; return x >= y"));
        assertEquals(true, exec("let x = 7; let y = 1; return x >= y"));

        assertEquals(false, exec("let x = 1; let y = 7; return x >= y"));
        assertEquals(false, exec("let x = 2; let y = 6; return x >= y"));
        assertEquals(false, exec("let x = 3; let y = 5; return x >= y"));
        assertEquals(true, exec("let x = 4; let y = 4; return x >= y"));
        assertEquals(true, exec("let x = 5; let y = 3; return x >= y"));
        assertEquals(true, exec("let x = 6; let y = 2; return x >= y"));
        assertEquals(true, exec("let x = 7; let y = 1; return x >= y"));

        assertEquals(false, exec("let x = 1; let y = 7; return x >= y"));
        assertEquals(false, exec("let x = 2; let y = 6; return x >= y"));
        assertEquals(false, exec("let x = 3; let y = 5; return x >= y"));
        assertEquals(true, exec("let x = 4; let y = 4; return x >= y"));
        assertEquals(true, exec("let x = 5; let y = 3; return x >= y"));
        assertEquals(true, exec("let x = 6; let y = 2; return x >= y"));
        assertEquals(true, exec("let x = 7; let y = 1; return x >= y"));

        assertEquals(false, exec("let x = 1; let y = 7; return x >= y"));
        assertEquals(false, exec("let x = 2; let y = 6; return x >= y"));
        assertEquals(false, exec("let x = 3; let y = 5; return x >= y"));
        assertEquals(true, exec("let x = 4; let y = 4; return x >= y"));
        assertEquals(true, exec("let x = 5; let y = 3; return x >= y"));
        assertEquals(true, exec("let x = 6; let y = 2; return x >= y"));
        assertEquals(true, exec("let x = 7; let y = 1; return x >= y"));
    }

    public void testDefGteTypedRHS() {
        assertEquals(false, exec("let x = 1; let y = 7; return x >= y"));
        assertEquals(false, exec("let x = 2; let y = 6; return x >= y"));
        assertEquals(false, exec("let x = 3; let y = 5; return x >= y"));
        assertEquals(true, exec("let x = 4; let y = 4; return x >= y"));
        assertEquals(true, exec("let x = 5; let y = 3; return x >= y"));
        assertEquals(true, exec("let x = 6; let y = 2; return x >= y"));
        assertEquals(true, exec("let x = 7; let y = 1; return x >= y"));

        assertEquals(false, exec("let x = 1; let y = 7; return x >= y"));
        assertEquals(false, exec("let x = 2; let y = 6; return x >= y"));
        assertEquals(false, exec("let x = 3; let y = 5; return x >= y"));
        assertEquals(true, exec("let x = 4; let y = 4; return x >= y"));
        assertEquals(true, exec("let x = 5; let y = 3; return x >= y"));
        assertEquals(true, exec("let x = 6; let y = 2; return x >= y"));
        assertEquals(true, exec("let x = 7; let y = 1; return x >= y"));

        assertEquals(false, exec("let x = 1; let y = 7; return x >= y"));
        assertEquals(false, exec("let x = 2; let y = 6; return x >= y"));
        assertEquals(false, exec("let x = 3; let y = 5; return x >= y"));
        assertEquals(true, exec("let x = 4; let y = 4; return x >= y"));
        assertEquals(true, exec("let x = 5; let y = 3; return x >= y"));
        assertEquals(true, exec("let x = 6; let y = 2; return x >= y"));
        assertEquals(true, exec("let x = 7; let y = 1; return x >= y"));

        assertEquals(false, exec("let x = 1; let y = 7; return x >= y"));
        assertEquals(false, exec("let x = 2; let y = 6; return x >= y"));
        assertEquals(false, exec("let x = 3; let y = 5; return x >= y"));
        assertEquals(true, exec("let x = 4; let y = 4; return x >= y"));
        assertEquals(true, exec("let x = 5; let y = 3; return x >= y"));
        assertEquals(true, exec("let x = 6; let y = 2; return x >= y"));
        assertEquals(true, exec("let x = 7; let y = 1; return x >= y"));
    }

    public void testInstanceOf() {
        assertEquals(true, exec("let x = 5; return x instanceof int"));
        assertEquals(true, exec("let x = 5; return x instanceof Number"));
        assertEquals(true, exec("let x = 5; return x instanceof Integer"));
        assertEquals(true, exec("let x = 5; return x instanceof def"));
        assertEquals(true, exec("let x = 5; return x instanceof Object"));
        assertEquals(true, exec("let x = 5; return x instanceof int"));
        assertEquals(true, exec("let x = 5; return x instanceof def"));
        assertEquals(true, exec("let x = 5; return x instanceof Object"));
        assertEquals(true, exec("let x = 5; return x instanceof Integer"));
        assertEquals(true, exec("let x = 5; return x instanceof Number"));
        assertEquals(false, exec("let x = 5; return x instanceof float"));
        assertEquals(false, exec("let x = 5; return x instanceof Map"));
        assertEquals(true, exec("List l = new ArrayList(); return l instanceof List"));
        assertEquals(false, exec("List l = null; return l instanceof List"));
        assertEquals(true, exec("List l = new ArrayList(); return l instanceof Collection"));
        assertEquals(false, exec("List l = new ArrayList(); return l instanceof Map"));
        assertEquals(true, exec("int[] x = new int[] { 5 }; return x instanceof int[]"));
        assertEquals(false, exec("int[] x = new int[] { 5 }; return x instanceof float[]"));
        assertEquals(false, exec("int[] x = new int[] { 5 }; return x instanceof int[][]"));
    }
}
