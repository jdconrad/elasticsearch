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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

final class JavascriptClassBuilder {

    final Map<String, JavascriptConstructor> constructors;
    final Map<String, JavascriptMethod> staticMethods;
    final Map<String, JavascriptMethod> methods;
    final Map<String, JavascriptField> staticFields;
    final Map<String, JavascriptField> fields;
    JavascriptMethod functionalInterfaceMethod;
    final Map<Class<?>, Object> annotations;

    final Map<String, JavascriptMethod> runtimeMethods;
    final Map<String, MethodHandle> getterMethodHandles;
    final Map<String, MethodHandle> setterMethodHandles;

    JavascriptClassBuilder() {
        constructors = new HashMap<>();
        staticMethods = new HashMap<>();
        methods = new HashMap<>();
        staticFields = new HashMap<>();
        fields = new HashMap<>();
        functionalInterfaceMethod = null;
        annotations = new HashMap<>();

        runtimeMethods = new HashMap<>();
        getterMethodHandles = new HashMap<>();
        setterMethodHandles = new HashMap<>();
    }

    JavascriptClass build() {
        return new JavascriptClass(
            constructors,
            staticMethods,
            methods,
            staticFields,
            fields,
            functionalInterfaceMethod,
            annotations,
            runtimeMethods,
            getterMethodHandles,
            setterMethodHandles
        );
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        JavascriptClassBuilder that = (JavascriptClassBuilder) object;

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
