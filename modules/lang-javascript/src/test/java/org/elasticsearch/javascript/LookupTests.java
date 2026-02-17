/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.javascript;

import org.elasticsearch.javascript.lookup.JavascriptLookup;
import org.elasticsearch.javascript.lookup.JavascriptLookupBuilder;
import org.elasticsearch.javascript.lookup.JavascriptMethod;
import org.elasticsearch.javascript.spi.WhitelistLoader;
import org.elasticsearch.test.ESTestCase;
import org.junit.Before;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class LookupTests extends ESTestCase {

    protected JavascriptLookup javascriptLookup;

    @Before
    public void setup() {
        javascriptLookup = JavascriptLookupBuilder.buildFromWhitelists(
            Collections.singletonList(WhitelistLoader.loadFromResourceFiles(JavascriptPlugin.class, "org.elasticsearch.javascript.lookup")),
            new HashMap<>(),
            new HashMap<>()
        );
    }

    public static class A {}                                                   // in whitelist

    public static class B extends A {}                                         // not in whitelist

    public static class C extends B {                                           // in whitelist
        public String getString0() {
            return "C/0";
        }                                // in whitelist
    }

    public static class D extends B {                                           // in whitelist
        public String getString0() {
            return "D/0";
        }                                // in whitelist

        public String getString1(int param0) {
            return "D/1 (" + param0 + ")";
        }     // in whitelist
    }

    public interface Z {}              // in whitelist

    public interface Y {}              // not in whitelist

    public interface X extends Y, Z {} // not in whitelist

    public interface V extends Y, Z {} // in whitelist

    public interface U extends X {      // in whitelist
        String getString2(int x, int y);    // in whitelist

        String getString1(int param0);      // in whitelist

        String getString0();                // not in whitelist
    }

    public interface T extends V {      // in whitelist
        String getString1(int param0);      // in whitelist

        int getInt0();                      // in whitelist
    }

    public interface S extends U, X {} // in whitelist

    public static class AA implements X {}                           // in whitelist

    public static class AB extends AA implements S {                  // not in whitelist
        public String getString2(int x, int y) {
            return "" + x + y;
        }     // not in whitelist

        public String getString1(int param0) {
            return "" + param0;
        }      // not in whitelist

        public String getString0() {
            return "";
        }                         // not in whitelist
    }

    public static class AC extends AB implements V {                  // in whitelist
        public String getString2(int x, int y) {
            return "" + x + y;
        }     // in whitelist
    }

    public static class AD extends AA implements X, S, T {            // in whitelist
        public String getString2(int x, int y) {
            return "" + x + y;
        }     // in whitelist

        public String getString1(int param0) {
            return "" + param0;
        }      // in whitelist

        public String getString0() {
            return "";
        }                         // not in whitelist

        public int getInt0() {
            return 0;
        }                                // in whitelist
    }

    public void testDirectSubClasses() {
        Set<Class<?>> directSubClasses = javascriptLookup.getDirectSubClasses(Object.class);
        assertEquals(4, directSubClasses.size());
        assertTrue(directSubClasses.contains(String.class));
        assertTrue(directSubClasses.contains(A.class));
        assertTrue(directSubClasses.contains(Z.class));
        assertTrue(directSubClasses.contains(AA.class));

        directSubClasses = javascriptLookup.getDirectSubClasses(A.class);
        assertEquals(2, directSubClasses.size());
        assertTrue(directSubClasses.contains(D.class));
        assertTrue(directSubClasses.contains(C.class));

        directSubClasses = javascriptLookup.getDirectSubClasses(B.class);
        assertNull(directSubClasses);

        directSubClasses = javascriptLookup.getDirectSubClasses(C.class);
        assertTrue(directSubClasses.isEmpty());

        directSubClasses = javascriptLookup.getDirectSubClasses(D.class);
        assertTrue(directSubClasses.isEmpty());

        directSubClasses = javascriptLookup.getDirectSubClasses(Z.class);
        assertEquals(5, directSubClasses.size());
        assertTrue(directSubClasses.contains(V.class));
        assertTrue(directSubClasses.contains(U.class));
        assertTrue(directSubClasses.contains(S.class));
        assertTrue(directSubClasses.contains(AA.class));
        assertTrue(directSubClasses.contains(AD.class));

        directSubClasses = javascriptLookup.getDirectSubClasses(Y.class);
        assertNull(directSubClasses);

        directSubClasses = javascriptLookup.getDirectSubClasses(X.class);
        assertNull(directSubClasses);

        directSubClasses = javascriptLookup.getDirectSubClasses(V.class);
        assertEquals(2, directSubClasses.size());
        assertTrue(directSubClasses.contains(T.class));
        assertTrue(directSubClasses.contains(AC.class));

        directSubClasses = javascriptLookup.getDirectSubClasses(U.class);
        assertEquals(1, directSubClasses.size());
        assertTrue(directSubClasses.contains(S.class));

        directSubClasses = javascriptLookup.getDirectSubClasses(T.class);
        assertEquals(1, directSubClasses.size());
        assertTrue(directSubClasses.contains(AD.class));

        directSubClasses = javascriptLookup.getDirectSubClasses(S.class);
        assertEquals(2, directSubClasses.size());
        assertTrue(directSubClasses.contains(AC.class));
        assertTrue(directSubClasses.contains(AD.class));

        directSubClasses = javascriptLookup.getDirectSubClasses(AA.class);
        assertEquals(2, directSubClasses.size());
        assertTrue(directSubClasses.contains(AC.class));
        assertTrue(directSubClasses.contains(AD.class));

        directSubClasses = javascriptLookup.getDirectSubClasses(AB.class);
        assertNull(directSubClasses);

        directSubClasses = javascriptLookup.getDirectSubClasses(AC.class);
        assertTrue(directSubClasses.isEmpty());

        directSubClasses = javascriptLookup.getDirectSubClasses(AD.class);
        assertTrue(directSubClasses.isEmpty());
    }

    public void testDirectSubClassMethods() {
        JavascriptMethod CgetString0 = javascriptLookup.lookupJavascriptMethod(C.class, false, "getString0", 0);
        JavascriptMethod DgetString0 = javascriptLookup.lookupJavascriptMethod(D.class, false, "getString0", 0);
        List<JavascriptMethod> subMethods = javascriptLookup.lookupJavascriptSubClassesMethod(A.class, "getString0", 0);
        assertNotNull(subMethods);
        assertEquals(2, subMethods.size());
        assertTrue(subMethods.contains(CgetString0));
        assertTrue(subMethods.contains(DgetString0));

        JavascriptMethod DgetString1 = javascriptLookup.lookupJavascriptMethod(D.class, false, "getString1", 1);
        subMethods = javascriptLookup.lookupJavascriptSubClassesMethod(A.class, "getString1", 1);
        assertNotNull(subMethods);
        assertEquals(1, subMethods.size());
        assertTrue(subMethods.contains(DgetString1));

        subMethods = javascriptLookup.lookupJavascriptSubClassesMethod(A.class, "getString2", 0);
        assertNull(subMethods);

        JavascriptMethod ACgetString2 = javascriptLookup.lookupJavascriptMethod(AC.class, false, "getString2", 2);
        JavascriptMethod ADgetString2 = javascriptLookup.lookupJavascriptMethod(AD.class, false, "getString2", 2);
        subMethods = javascriptLookup.lookupJavascriptSubClassesMethod(AA.class, "getString2", 2);
        assertNotNull(subMethods);
        assertEquals(2, subMethods.size());
        assertTrue(subMethods.contains(ACgetString2));
        assertTrue(subMethods.contains(ADgetString2));

        JavascriptMethod ADgetString1 = javascriptLookup.lookupJavascriptMethod(AD.class, false, "getString1", 1);
        subMethods = javascriptLookup.lookupJavascriptSubClassesMethod(AA.class, "getString1", 1);
        assertNotNull(subMethods);
        assertEquals(1, subMethods.size());
        assertTrue(subMethods.contains(ADgetString1));

        subMethods = javascriptLookup.lookupJavascriptSubClassesMethod(AA.class, "getString0", 0);
        assertNull(subMethods);

        JavascriptMethod ADgetInt0 = javascriptLookup.lookupJavascriptMethod(AD.class, false, "getInt0", 0);
        subMethods = javascriptLookup.lookupJavascriptSubClassesMethod(AA.class, "getInt0", 0);
        assertNotNull(subMethods);
        assertEquals(1, subMethods.size());
        assertTrue(subMethods.contains(ADgetInt0));

        JavascriptMethod UgetString2 = javascriptLookup.lookupJavascriptMethod(U.class, false, "getString2", 2);
        subMethods = javascriptLookup.lookupJavascriptSubClassesMethod(Z.class, "getString2", 2);
        assertNotNull(subMethods);
        assertEquals(3, subMethods.size());
        assertTrue(subMethods.contains(UgetString2));
        assertTrue(subMethods.contains(ACgetString2));
        assertTrue(subMethods.contains(ADgetString2));

        JavascriptMethod UgetString1 = javascriptLookup.lookupJavascriptMethod(U.class, false, "getString1", 1);
        JavascriptMethod TgetString1 = javascriptLookup.lookupJavascriptMethod(T.class, false, "getString1", 1);
        subMethods = javascriptLookup.lookupJavascriptSubClassesMethod(Z.class, "getString1", 1);
        assertNotNull(subMethods);
        assertEquals(3, subMethods.size());
        assertTrue(subMethods.contains(UgetString1));
        assertTrue(subMethods.contains(TgetString1));
        assertTrue(subMethods.contains(ADgetString1));

        subMethods = javascriptLookup.lookupJavascriptSubClassesMethod(Z.class, "getString0", 0);
        assertNull(subMethods);

        JavascriptMethod TgetInt0 = javascriptLookup.lookupJavascriptMethod(T.class, false, "getInt0", 0);
        subMethods = javascriptLookup.lookupJavascriptSubClassesMethod(Z.class, "getInt0", 0);
        assertNotNull(subMethods);
        assertEquals(2, subMethods.size());
        assertTrue(subMethods.contains(TgetInt0));
        assertTrue(subMethods.contains(ADgetInt0));

        subMethods = javascriptLookup.lookupJavascriptSubClassesMethod(V.class, "getString2", 2);
        assertNotNull(subMethods);
        assertEquals(2, subMethods.size());
        assertTrue(subMethods.contains(ACgetString2));
        assertTrue(subMethods.contains(ADgetString2));

        subMethods = javascriptLookup.lookupJavascriptSubClassesMethod(V.class, "getString1", 1);
        assertNotNull(subMethods);
        assertEquals(2, subMethods.size());
        assertTrue(subMethods.contains(TgetString1));
        assertTrue(subMethods.contains(ADgetString1));

        subMethods = javascriptLookup.lookupJavascriptSubClassesMethod(V.class, "getString0", 0);
        assertNull(subMethods);

        subMethods = javascriptLookup.lookupJavascriptSubClassesMethod(V.class, "getInt0", 0);
        assertNotNull(subMethods);
        assertEquals(2, subMethods.size());
        assertTrue(subMethods.contains(TgetInt0));
        assertTrue(subMethods.contains(ADgetInt0));

        subMethods = javascriptLookup.lookupJavascriptSubClassesMethod(U.class, "getString2", 2);
        assertNotNull(subMethods);
        assertEquals(2, subMethods.size());
        assertTrue(subMethods.contains(ACgetString2));
        assertTrue(subMethods.contains(ADgetString2));

        subMethods = javascriptLookup.lookupJavascriptSubClassesMethod(U.class, "getString1", 1);
        assertNotNull(subMethods);
        assertEquals(1, subMethods.size());
        assertTrue(subMethods.contains(ADgetString1));

        subMethods = javascriptLookup.lookupJavascriptSubClassesMethod(U.class, "getString0", 0);
        assertNull(subMethods);

        subMethods = javascriptLookup.lookupJavascriptSubClassesMethod(U.class, "getInt0", 0);
        assertNotNull(subMethods);
        assertEquals(1, subMethods.size());
        assertTrue(subMethods.contains(ADgetInt0));

        subMethods = javascriptLookup.lookupJavascriptSubClassesMethod(V.class, "getInt0", 0);
        assertNotNull(subMethods);
        assertEquals(2, subMethods.size());
        assertTrue(subMethods.contains(TgetInt0));
        assertTrue(subMethods.contains(ADgetInt0));

        subMethods = javascriptLookup.lookupJavascriptSubClassesMethod(S.class, "getString2", 2);
        assertNotNull(subMethods);
        assertEquals(2, subMethods.size());
        assertTrue(subMethods.contains(ACgetString2));
        assertTrue(subMethods.contains(ADgetString2));

        subMethods = javascriptLookup.lookupJavascriptSubClassesMethod(S.class, "getString1", 1);
        assertNotNull(subMethods);
        assertEquals(1, subMethods.size());
        assertTrue(subMethods.contains(ADgetString1));

        subMethods = javascriptLookup.lookupJavascriptSubClassesMethod(S.class, "getString0", 0);
        assertNull(subMethods);

        subMethods = javascriptLookup.lookupJavascriptSubClassesMethod(S.class, "getInt0", 0);
        assertNotNull(subMethods);
        assertEquals(1, subMethods.size());
        assertTrue(subMethods.contains(ADgetInt0));
    }
}
