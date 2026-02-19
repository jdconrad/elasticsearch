/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.javascript.spi.annotation;

/**
 * Creates an alias in JavascriptLookupBuilder for the given class or method. Class aliases can be used to expose an inner class without
 * scripts and whitelists needing to scope it by the outer class.
 *
 * For class
 *
 * <pre>
 * public class Outer {
 *     public static class Inner {
 *
 *     }
 *     public Inner inner() {
 *         return new Inner();
 *     }
 * }
 * </pre>
 *
 * Normally scripts would need to reference {@code Outer.Inner}.
 *
 * With an alias annotation {@code @alias[class="Inner"]} on the class
 * <pre>
 * class Outer$Inner @alias[class="AliasedTestInnerClass"] {
 * }
 * </pre>
 *
 * Then whitelist can have {@code Inner} as the return value for {@code inner} instead of {@code Outer.Inner}
 * <pre>
 * class Outer {
 *   Inner inner()
 * }
 * </pre>
 *
 * And scripts refer can to {@code Inner} directly, {@code Inner inner = Outer.inner()}, instead of using the outer class to scope
 * the type name {@code Outer.Inner} as would normally be required {@code Outer.Inner inner = Outer.inner()}
 *
 * Method aliases are defined on whitelist methods using {@code @alias[method="foo"]}.
 * The underlying Java method name remains unchanged and the alias is an additional callable script name.
 *
 * @param type identifies whether this alias targets a class or method
 * @param alias the other name for the class or method
 */
public record AliasAnnotation(AliasType type, String alias) {
    public static final String NAME = "alias";

    public enum AliasType {
        CLASS,
        METHOD
    }
}
