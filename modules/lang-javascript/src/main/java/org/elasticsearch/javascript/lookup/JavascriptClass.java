/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.javascript.lookup;

import java.lang.invoke.MethodHandle;
import java.util.Map;
import java.util.Objects;

public final class JavascriptClass {

    public final Map<String, JavascriptConstructor> constructors;
    public final Map<String, JavascriptMethod> staticMethods;
    public final Map<String, JavascriptMethod> methods;
    public final Map<String, JavascriptField> staticFields;
    public final Map<String, JavascriptField> fields;
    public final JavascriptMethod functionalInterfaceMethod;
    public final Map<Class<?>, Object> annotations;

    public final Map<String, JavascriptMethod> runtimeMethods;
    public final Map<String, MethodHandle> getterMethodHandles;
    public final Map<String, MethodHandle> setterMethodHandles;

    JavascriptClass(
        Map<String, JavascriptConstructor> constructors,
        Map<String, JavascriptMethod> staticMethods,
        Map<String, JavascriptMethod> methods,
        Map<String, JavascriptField> staticFields,
        Map<String, JavascriptField> fields,
        JavascriptMethod functionalInterfaceMethod,
        Map<Class<?>, Object> annotations,
        Map<String, JavascriptMethod> runtimeMethods,
        Map<String, MethodHandle> getterMethodHandles,
        Map<String, MethodHandle> setterMethodHandles
    ) {

        this.constructors = Map.copyOf(constructors);
        this.staticMethods = Map.copyOf(staticMethods);
        this.methods = Map.copyOf(methods);
        this.staticFields = Map.copyOf(staticFields);
        this.fields = Map.copyOf(fields);
        this.functionalInterfaceMethod = functionalInterfaceMethod;
        this.annotations = Map.copyOf(annotations);

        this.getterMethodHandles = Map.copyOf(getterMethodHandles);
        this.setterMethodHandles = Map.copyOf(setterMethodHandles);
        this.runtimeMethods = runtimeMethods.equals(methods) ? this.methods : Map.copyOf(runtimeMethods);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        JavascriptClass that = (JavascriptClass) object;

        return Objects.equals(constructors, that.constructors)
            && Objects.equals(staticMethods, that.staticMethods)
            && Objects.equals(methods, that.methods)
            && Objects.equals(staticFields, that.staticFields)
            && Objects.equals(fields, that.fields)
            && Objects.equals(functionalInterfaceMethod, that.functionalInterfaceMethod)
            && Objects.equals(annotations, that.annotations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(constructors, staticMethods, methods, staticFields, fields, functionalInterfaceMethod, annotations);
    }
}
