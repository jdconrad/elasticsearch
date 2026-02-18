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
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import static org.elasticsearch.javascript.lookup.JavascriptLookupUtility.DEF_CLASS_NAME;
import static org.elasticsearch.javascript.lookup.JavascriptLookupUtility.buildJavascriptConstructorKey;
import static org.elasticsearch.javascript.lookup.JavascriptLookupUtility.buildJavascriptFieldKey;
import static org.elasticsearch.javascript.lookup.JavascriptLookupUtility.buildJavascriptMethodKey;
import static org.elasticsearch.javascript.lookup.JavascriptLookupUtility.typeToBoxedType;

public final class JavascriptLookup {

    private final Map<String, Class<?>> javaClassNamesToClasses;
    private final Map<String, Class<?>> canonicalClassNamesToClasses;
    private final Map<Class<?>, JavascriptClass> classesToJavascriptClasses;
    private final Map<Class<?>, Set<Class<?>>> classesToDirectSubClasses;

    private final Map<String, JavascriptMethod> javascriptMethodKeysToImportedJavascriptMethods;
    private final Map<String, JavascriptClassBinding> javascriptMethodKeysToJavascriptClassBindings;
    private final Map<String, JavascriptInstanceBinding> javascriptMethodKeysToJavascriptInstanceBindings;

    JavascriptLookup(
        Map<String, Class<?>> javaClassNamesToClasses,
        Map<String, Class<?>> canonicalClassNamesToClasses,
        Map<Class<?>, JavascriptClass> classesToJavascriptClasses,
        Map<Class<?>, Set<Class<?>>> classesToDirectSubClasses,
        Map<String, JavascriptMethod> javascriptMethodKeysToImportedJavascriptMethods,
        Map<String, JavascriptClassBinding> javascriptMethodKeysToJavascriptClassBindings,
        Map<String, JavascriptInstanceBinding> javascriptMethodKeysToJavascriptInstanceBindings
    ) {
        this.javaClassNamesToClasses = Map.copyOf(javaClassNamesToClasses);
        this.canonicalClassNamesToClasses = Map.copyOf(canonicalClassNamesToClasses);
        this.classesToJavascriptClasses = Map.copyOf(classesToJavascriptClasses);
        this.classesToDirectSubClasses = Map.copyOf(classesToDirectSubClasses);

        this.javascriptMethodKeysToImportedJavascriptMethods = Map.copyOf(javascriptMethodKeysToImportedJavascriptMethods);
        this.javascriptMethodKeysToJavascriptClassBindings = Map.copyOf(javascriptMethodKeysToJavascriptClassBindings);
        this.javascriptMethodKeysToJavascriptInstanceBindings = Map.copyOf(javascriptMethodKeysToJavascriptInstanceBindings);
    }

    public Class<?> javaClassNameToClass(String javaClassName) {
        return javaClassNamesToClasses.get(javaClassName);
    }

    public boolean isValidCanonicalClassName(String canonicalClassName) {
        Objects.requireNonNull(canonicalClassName);

        return DEF_CLASS_NAME.equals(canonicalClassName) || canonicalClassNamesToClasses.containsKey(canonicalClassName);
    }

    public Class<?> canonicalTypeNameToType(String canonicalTypeName) {
        Objects.requireNonNull(canonicalTypeName);

        return JavascriptLookupUtility.canonicalTypeNameToType(canonicalTypeName, canonicalClassNamesToClasses);
    }

    public Set<Class<?>> getClasses() {
        return classesToJavascriptClasses.keySet();
    }

    public Set<Class<?>> getDirectSubClasses(Class<?> superClass) {
        return classesToDirectSubClasses.get(superClass);
    }

    public Set<String> getImportedJavascriptMethodsKeys() {
        return javascriptMethodKeysToImportedJavascriptMethods.keySet();
    }

    public Set<String> getJavascriptClassBindingsKeys() {
        return javascriptMethodKeysToJavascriptClassBindings.keySet();
    }

    public Set<String> getJavascriptInstanceBindingsKeys() {
        return javascriptMethodKeysToJavascriptInstanceBindings.keySet();
    }

    public JavascriptClass lookupJavascriptClass(Class<?> targetClass) {
        return classesToJavascriptClasses.get(targetClass);
    }

    public JavascriptConstructor lookupJavascriptConstructor(String targetCanonicalClassName, int constructorArity) {
        Objects.requireNonNull(targetCanonicalClassName);

        Class<?> targetClass = canonicalTypeNameToType(targetCanonicalClassName);

        if (targetClass == null) {
            return null;
        }

        return lookupJavascriptConstructor(targetClass, constructorArity);
    }

    public JavascriptConstructor lookupJavascriptConstructor(Class<?> targetClass, int constructorArity) {
        Objects.requireNonNull(targetClass);

        JavascriptClass targetJavascriptClass = classesToJavascriptClasses.get(targetClass);
        String javascriptConstructorKey = buildJavascriptConstructorKey(constructorArity);

        if (targetJavascriptClass == null) {
            return null;
        }

        JavascriptConstructor javascriptConstructor = targetJavascriptClass.constructors.get(javascriptConstructorKey);

        if (javascriptConstructor == null) {
            return null;
        }

        return javascriptConstructor;
    }

    public JavascriptMethod lookupJavascriptMethod(String targetCanonicalClassName, boolean isStatic, String methodName, int methodArity) {
        Objects.requireNonNull(targetCanonicalClassName);

        Class<?> targetClass = canonicalTypeNameToType(targetCanonicalClassName);

        if (targetClass == null) {
            return null;
        }

        return lookupJavascriptMethod(targetClass, isStatic, methodName, methodArity);
    }

    public JavascriptMethod lookupJavascriptMethod(Class<?> targetClass, boolean isStatic, String methodName, int methodArity) {
        Objects.requireNonNull(targetClass);
        Objects.requireNonNull(methodName);

        if (classesToJavascriptClasses.containsKey(targetClass) == false) {
            return null;
        }

        if (targetClass.isPrimitive()) {
            targetClass = typeToBoxedType(targetClass);

            if (classesToJavascriptClasses.containsKey(targetClass) == false) {
                return null;
            }
        }

        String javascriptMethodKey = buildJavascriptMethodKey(methodName, methodArity);
        Function<JavascriptClass, JavascriptMethod> objectLookup = isStatic
            ? targetJavascriptClass -> targetJavascriptClass.staticMethods.get(javascriptMethodKey)
            : targetJavascriptClass -> targetJavascriptClass.methods.get(javascriptMethodKey);

        return lookupJavascriptObject(targetClass, objectLookup);
    }

    public List<JavascriptMethod> lookupJavascriptSubClassesMethod(Class<?> targetClass, String methodName, int methodArity) {
        Objects.requireNonNull(targetClass);
        Objects.requireNonNull(methodName);

        if (classesToJavascriptClasses.containsKey(targetClass) == false) {
            return null;
        }

        if (targetClass.isPrimitive()) {
            targetClass = typeToBoxedType(targetClass);

            if (classesToJavascriptClasses.containsKey(targetClass) == false) {
                return null;
            }
        }

        String javascriptMethodKey = buildJavascriptMethodKey(methodName, methodArity);
        Deque<Class<?>> subClasses = new ArrayDeque<>(classesToDirectSubClasses.get(targetClass));
        Set<Class<?>> resolvedSubClasses = new HashSet<>();
        List<JavascriptMethod> subMethods = null;

        Class<?> subClass;
        while ((subClass = subClasses.pollFirst()) != null) {

            if (resolvedSubClasses.add(subClass)) {
                subClasses.addAll(classesToDirectSubClasses.get(subClass));

                JavascriptClass javascriptClass = classesToJavascriptClasses.get(subClass);
                JavascriptMethod javascriptMethod = javascriptClass.methods.get(javascriptMethodKey);

                if (javascriptMethod != null) {
                    if (subMethods == null) {
                        subMethods = new ArrayList<>();
                    }

                    subMethods.add(javascriptMethod);
                }
            }
        }

        return subMethods;
    }

    public JavascriptField lookupJavascriptField(Class<?> targetClass, boolean isStatic, String fieldName) {
        Objects.requireNonNull(targetClass);
        Objects.requireNonNull(fieldName);

        if (classesToJavascriptClasses.containsKey(targetClass) == false) {
            return null;
        }

        String javascriptFieldKey = buildJavascriptFieldKey(fieldName);
        Function<JavascriptClass, JavascriptField> objectLookup = isStatic
            ? targetJavascriptClass -> targetJavascriptClass.staticFields.get(javascriptFieldKey)
            : targetJavascriptClass -> targetJavascriptClass.fields.get(javascriptFieldKey);

        return lookupJavascriptObject(targetClass, objectLookup);
    }

    public JavascriptMethod lookupImportedJavascriptMethod(String methodName, int arity) {
        Objects.requireNonNull(methodName);

        String javascriptMethodKey = buildJavascriptMethodKey(methodName, arity);

        return javascriptMethodKeysToImportedJavascriptMethods.get(javascriptMethodKey);
    }

    public JavascriptClassBinding lookupJavascriptClassBinding(String methodName, int arity) {
        Objects.requireNonNull(methodName);

        String javascriptMethodKey = buildJavascriptMethodKey(methodName, arity);

        return javascriptMethodKeysToJavascriptClassBindings.get(javascriptMethodKey);
    }

    public JavascriptInstanceBinding lookupJavascriptInstanceBinding(String methodName, int arity) {
        Objects.requireNonNull(methodName);

        String javascriptMethodKey = buildJavascriptMethodKey(methodName, arity);

        return javascriptMethodKeysToJavascriptInstanceBindings.get(javascriptMethodKey);
    }

    public JavascriptMethod lookupFunctionalInterfaceJavascriptMethod(Class<?> targetClass) {
        JavascriptClass targetJavascriptClass = classesToJavascriptClasses.get(targetClass);

        if (targetJavascriptClass == null) {
            return null;
        }

        return targetJavascriptClass.functionalInterfaceMethod;
    }

    public JavascriptMethod lookupRuntimeJavascriptMethod(Class<?> originalTargetClass, String methodName, int methodArity) {
        Objects.requireNonNull(originalTargetClass);
        Objects.requireNonNull(methodName);

        String javascriptMethodKey = buildJavascriptMethodKey(methodName, methodArity);
        Function<JavascriptClass, JavascriptMethod> objectLookup = targetJavascriptClass -> targetJavascriptClass.runtimeMethods.get(
            javascriptMethodKey
        );

        return lookupJavascriptObject(originalTargetClass, objectLookup);
    }

    public MethodHandle lookupRuntimeGetterMethodHandle(Class<?> originalTargetClass, String getterName) {
        Objects.requireNonNull(originalTargetClass);
        Objects.requireNonNull(getterName);

        Function<JavascriptClass, MethodHandle> objectLookup = targetJavascriptClass -> targetJavascriptClass.getterMethodHandles.get(
            getterName
        );

        return lookupJavascriptObject(originalTargetClass, objectLookup);
    }

    public MethodHandle lookupRuntimeSetterMethodHandle(Class<?> originalTargetClass, String setterName) {
        Objects.requireNonNull(originalTargetClass);
        Objects.requireNonNull(setterName);

        Function<JavascriptClass, MethodHandle> objectLookup = targetJavascriptClass -> targetJavascriptClass.setterMethodHandles.get(
            setterName
        );

        return lookupJavascriptObject(originalTargetClass, objectLookup);
    }

    private <T> T lookupJavascriptObject(Class<?> originalTargetClass, Function<JavascriptClass, T> objectLookup) {
        Objects.requireNonNull(originalTargetClass);
        Objects.requireNonNull(objectLookup);

        Class<?> currentTargetClass = originalTargetClass;

        while (currentTargetClass != null) {
            JavascriptClass targetJavascriptClass = classesToJavascriptClasses.get(currentTargetClass);

            if (targetJavascriptClass != null) {
                T javascriptObject = objectLookup.apply(targetJavascriptClass);

                if (javascriptObject != null) {
                    return javascriptObject;
                }
            }

            currentTargetClass = currentTargetClass.getSuperclass();
        }

        if (originalTargetClass.isInterface()) {
            JavascriptClass targetJavascriptClass = classesToJavascriptClasses.get(Object.class);

            if (targetJavascriptClass != null) {
                T javascriptObject = objectLookup.apply(targetJavascriptClass);

                if (javascriptObject != null) {
                    return javascriptObject;
                }
            }
        }

        currentTargetClass = originalTargetClass;
        Set<Class<?>> resolvedInterfaces = new HashSet<>();

        while (currentTargetClass != null) {
            Deque<Class<?>> targetInterfaces = new ArrayDeque<>(Arrays.asList(currentTargetClass.getInterfaces()));

            Class<?> targetInterface;
            while ((targetInterface = targetInterfaces.pollFirst()) != null) {

                if (resolvedInterfaces.add(targetInterface)) {
                    JavascriptClass targetJavascriptClass = classesToJavascriptClasses.get(targetInterface);

                    if (targetJavascriptClass != null) {
                        T javascriptObject = objectLookup.apply(targetJavascriptClass);

                        if (javascriptObject != null) {
                            return javascriptObject;
                        }
                    }
                    targetInterfaces.addAll(Arrays.asList(targetInterface.getInterfaces()));
                }
            }

            currentTargetClass = currentTargetClass.getSuperclass();
        }

        return null;
    }
}
