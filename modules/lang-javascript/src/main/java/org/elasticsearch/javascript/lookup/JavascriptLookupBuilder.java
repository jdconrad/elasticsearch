/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.javascript.lookup;

import org.elasticsearch.common.util.Maps;
import org.elasticsearch.core.Strings;
import org.elasticsearch.javascript.Def;
import org.elasticsearch.javascript.spi.Whitelist;
import org.elasticsearch.javascript.spi.WhitelistClass;
import org.elasticsearch.javascript.spi.WhitelistClassBinding;
import org.elasticsearch.javascript.spi.WhitelistConstructor;
import org.elasticsearch.javascript.spi.WhitelistField;
import org.elasticsearch.javascript.spi.WhitelistInstanceBinding;
import org.elasticsearch.javascript.spi.WhitelistMethod;
import org.elasticsearch.javascript.spi.annotation.AliasAnnotation;
import org.elasticsearch.javascript.spi.annotation.AliasAnnotation.AliasType;
import org.elasticsearch.javascript.spi.annotation.AugmentedAnnotation;
import org.elasticsearch.javascript.spi.annotation.CompileTimeOnlyAnnotation;
import org.elasticsearch.javascript.spi.annotation.InjectConstantAnnotation;
import org.elasticsearch.javascript.spi.annotation.NoImportAnnotation;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import static org.elasticsearch.javascript.lookup.JavascriptLookupUtility.DEF_CLASS_NAME;
import static org.elasticsearch.javascript.lookup.JavascriptLookupUtility.buildJavascriptConstructorKey;
import static org.elasticsearch.javascript.lookup.JavascriptLookupUtility.buildJavascriptFieldKey;
import static org.elasticsearch.javascript.lookup.JavascriptLookupUtility.buildJavascriptMethodKey;
import static org.elasticsearch.javascript.lookup.JavascriptLookupUtility.typeToCanonicalTypeName;
import static org.elasticsearch.javascript.lookup.JavascriptLookupUtility.typeToJavaType;
import static org.elasticsearch.javascript.lookup.JavascriptLookupUtility.typesToCanonicalTypeNames;

public final class JavascriptLookupBuilder {

    private static final Pattern CLASS_NAME_PATTERN = Pattern.compile("^[_a-zA-Z][._a-zA-Z0-9]*$");
    private static final Pattern METHOD_AND_FIELD_NAME_PATTERN = Pattern.compile("^[_a-zA-Z][_a-zA-Z0-9]*$");

    public static JavascriptLookup buildFromWhitelists(
        List<Whitelist> whitelists,
        Map<Object, Object> dedup,
        Map<JavascriptMethod, JavascriptMethod> filteredMethodCache
    ) {
        JavascriptLookupBuilder javascriptLookupBuilder = new JavascriptLookupBuilder();
        String origin = "internal error";

        try {
            for (Whitelist whitelist : whitelists) {
                for (WhitelistClass whitelistClass : whitelist.whitelistClasses) {
                    origin = whitelistClass.origin;
                    javascriptLookupBuilder.addJavascriptClass(
                        whitelist.classLoader,
                        whitelistClass.javaClassName,
                        whitelistClass.javascriptAnnotations
                    );
                }
            }

            for (Whitelist whitelist : whitelists) {
                for (WhitelistClass whitelistClass : whitelist.whitelistClasses) {
                    String targetCanonicalClassName = whitelistClass.javaClassName.replace('$', '.');

                    for (WhitelistConstructor whitelistConstructor : whitelistClass.whitelistConstructors) {
                        origin = whitelistConstructor.origin;
                        javascriptLookupBuilder.addJavascriptConstructor(
                            targetCanonicalClassName,
                            whitelistConstructor.canonicalTypeNameParameters,
                            whitelistConstructor.javascriptAnnotations,
                            dedup
                        );
                    }

                    for (WhitelistMethod whitelistMethod : whitelistClass.whitelistMethods) {
                        origin = whitelistMethod.origin;
                        javascriptLookupBuilder.addJavascriptMethod(
                            whitelist.classLoader,
                            targetCanonicalClassName,
                            whitelistMethod.augmentedCanonicalClassName,
                            whitelistMethod.methodName,
                            whitelistMethod.returnCanonicalTypeName,
                            whitelistMethod.canonicalTypeNameParameters,
                            whitelistMethod.javascriptAnnotations,
                            dedup
                        );
                    }

                    for (WhitelistField whitelistField : whitelistClass.whitelistFields) {
                        origin = whitelistField.origin;
                        javascriptLookupBuilder.addJavascriptField(
                            whitelist.classLoader,
                            targetCanonicalClassName,
                            whitelistField.fieldName,
                            whitelistField.canonicalTypeNameParameter,
                            whitelistField.javascriptAnnotations,
                            dedup
                        );
                    }
                }

                for (WhitelistMethod whitelistStatic : whitelist.whitelistImportedMethods) {
                    origin = whitelistStatic.origin;
                    javascriptLookupBuilder.addImportedJavascriptMethod(
                        whitelist.classLoader,
                        whitelistStatic.augmentedCanonicalClassName,
                        whitelistStatic.methodName,
                        whitelistStatic.returnCanonicalTypeName,
                        whitelistStatic.canonicalTypeNameParameters,
                        whitelistStatic.javascriptAnnotations,
                        dedup
                    );
                }

                for (WhitelistClassBinding whitelistClassBinding : whitelist.whitelistClassBindings) {
                    origin = whitelistClassBinding.origin;
                    javascriptLookupBuilder.addJavascriptClassBinding(
                        whitelist.classLoader,
                        whitelistClassBinding.targetJavaClassName,
                        whitelistClassBinding.methodName,
                        whitelistClassBinding.returnCanonicalTypeName,
                        whitelistClassBinding.canonicalTypeNameParameters,
                        whitelistClassBinding.javascriptAnnotations,
                        dedup
                    );
                }

                for (WhitelistInstanceBinding whitelistInstanceBinding : whitelist.whitelistInstanceBindings) {
                    origin = whitelistInstanceBinding.origin;
                    javascriptLookupBuilder.addJavascriptInstanceBinding(
                        whitelistInstanceBinding.targetInstance,
                        whitelistInstanceBinding.methodName,
                        whitelistInstanceBinding.returnCanonicalTypeName,
                        whitelistInstanceBinding.canonicalTypeNameParameters,
                        whitelistInstanceBinding.javascriptAnnotations,
                        dedup
                    );
                }
            }
        } catch (Exception exception) {
            throw new IllegalArgumentException("error loading whitelist(s) " + origin, exception);
        }

        return javascriptLookupBuilder.build(dedup, filteredMethodCache);
    }

    // javaClassNamesToClasses is all the classes that need to be available to the custom classloader
    // including classes used as part of imported methods and class bindings but not necessarily whitelisted
    // individually. The values of javaClassNamesToClasses are a superset of the values of
    // canonicalClassNamesToClasses.
    private final Map<String, Class<?>> javaClassNamesToClasses;
    // canonicalClassNamesToClasses is all the whitelisted classes available in a Javascript script including
    // classes with imported canonical names but does not include classes from imported methods or class
    // bindings unless also whitelisted separately. The values of canonicalClassNamesToClasses are a subset
    // of the values of javaClassNamesToClasses.
    private final Map<String, Class<?>> canonicalClassNamesToClasses;
    private final Map<Class<?>, JavascriptClassBuilder> classesToJavascriptClassBuilders;
    private final Map<Class<?>, Set<Class<?>>> classesToDirectSubClasses;

    private final Map<String, JavascriptMethod> javascriptMethodKeysToImportedJavascriptMethods;
    private final Map<String, JavascriptClassBinding> javascriptMethodKeysToJavascriptClassBindings;
    private final Map<String, JavascriptInstanceBinding> javascriptMethodKeysToJavascriptInstanceBindings;

    public JavascriptLookupBuilder() {
        javaClassNamesToClasses = new HashMap<>();
        canonicalClassNamesToClasses = new HashMap<>();
        classesToJavascriptClassBuilders = new HashMap<>();
        classesToDirectSubClasses = new HashMap<>();

        javascriptMethodKeysToImportedJavascriptMethods = new HashMap<>();
        javascriptMethodKeysToJavascriptClassBindings = new HashMap<>();
        javascriptMethodKeysToJavascriptInstanceBindings = new HashMap<>();
    }

    private Class<?> canonicalTypeNameToType(String canonicalTypeName) {
        return JavascriptLookupUtility.canonicalTypeNameToType(canonicalTypeName, canonicalClassNamesToClasses);
    }

    private boolean isValidType(Class<?> type) {
        while (type.getComponentType() != null) {
            type = type.getComponentType();
        }

        return type == def.class || classesToJavascriptClassBuilders.containsKey(type);
    }

    private static Class<?> loadClass(ClassLoader classLoader, String javaClassName, Supplier<String> errorMessage) {
        try {
            return Class.forName(javaClassName, true, classLoader);
        } catch (ClassNotFoundException cnfe) {
            try {
                // Javascript provides some api classes that are available only through the javascript implementation.
                return Class.forName(javaClassName);
            } catch (ClassNotFoundException cnfe2) {
                IllegalArgumentException iae = new IllegalArgumentException(errorMessage.get(), cnfe2);
                cnfe2.addSuppressed(cnfe);
                throw iae;
            }
        }
    }

    /**
     * Returns a lookup with the capability of looking up members in the target
     * class.
     *
     * <p> If the target class is in the same module as this module, then the
     * returned lookup has this class as its lookup class and holds the
     * {@link Lookup#MODULE} mode. If the target class is not in this module,
     * then the returned lookup has the target class as its lookup class and
     * holds the {@link Lookup#UNCONDITIONAL} mode.
     */
    private static Lookup lookup(Class<?> targetClass) {
        if (targetClass.getModule() == JavascriptLookupBuilder.class.getModule()) {
            var l = MethodHandles.lookup().dropLookupMode(Lookup.PACKAGE);
            assert l.lookupModes() == (Lookup.PUBLIC | Lookup.MODULE) : "lookup modes:" + Integer.toHexString(l.lookupModes());
            return l;
        } else {
            return MethodHandles.publicLookup().in(targetClass);
        }
    }

    public void addJavascriptClass(ClassLoader classLoader, String javaClassName, Map<Class<?>, Object> annotations) {

        Objects.requireNonNull(classLoader);
        Objects.requireNonNull(javaClassName);

        Class<?> clazz;

        if ("void".equals(javaClassName)) clazz = void.class;
        else if ("boolean".equals(javaClassName)) clazz = boolean.class;
        else if ("byte".equals(javaClassName)) clazz = byte.class;
        else if ("short".equals(javaClassName)) clazz = short.class;
        else if ("char".equals(javaClassName)) clazz = char.class;
        else if ("int".equals(javaClassName)) clazz = int.class;
        else if ("long".equals(javaClassName)) clazz = long.class;
        else if ("float".equals(javaClassName)) clazz = float.class;
        else if ("double".equals(javaClassName)) clazz = double.class;
        else {
            clazz = loadClass(classLoader, javaClassName, () -> "class [" + javaClassName + "] not found");
        }

        addJavascriptClass(clazz, annotations);
    }

    private static IllegalArgumentException lookupException(String formatText, Object... args) {
        return new IllegalArgumentException(Strings.format(formatText, args));
    }

    private static IllegalArgumentException lookupException(Throwable cause, String formatText, Object... args) {
        return new IllegalArgumentException(Strings.format(formatText, args), cause);
    }

    private static String methodIdentifier(Class<?> targetClass, String methodName, int methodArity) {
        return typeToCanonicalTypeName(targetClass) + "#" + methodName + "/" + methodArity;
    }

    private static AliasAnnotation requireAliasType(AliasAnnotation alias, AliasType aliasType, String context) {
        if (alias.type() != aliasType) {
            throw new IllegalArgumentException("invalid alias type [" + alias.type().name().toLowerCase(Locale.ROOT) + "] for " + context);
        }
        return alias;
    }

    private void addMethodAlias(
        Map<String, JavascriptMethod> methods,
        Class<?> targetClass,
        String methodName,
        int methodArity,
        AliasAnnotation alias,
        JavascriptMethod javascriptMethod
    ) {
        String targetCanonicalClassName = typeToCanonicalTypeName(targetClass);
        String methodAlias = requireAliasType(
            alias,
            AliasType.METHOD,
            "method [" + methodIdentifier(targetClass, methodName, methodArity) + "]"
        ).alias();

        if (METHOD_AND_FIELD_NAME_PATTERN.matcher(methodAlias).matches() == false) {
            throw new IllegalArgumentException(
                "invalid method alias name [" + methodAlias + "] for target class [" + targetCanonicalClassName + "]."
            );
        }

        String methodAliasKey = buildJavascriptMethodKey(methodAlias, methodArity);

        if (buildJavascriptMethodKey(methodName, methodArity).equals(methodAliasKey)) {
            return;
        }

        JavascriptMethod existingMethod = methods.get(methodAliasKey);
        if (existingMethod == null) {
            methods.put(methodAliasKey.intern(), javascriptMethod);
        } else if (existingMethod.equals(javascriptMethod) == false) {
            throw lookupException(
                "Cannot add method alias [%s] for [%s] that shadows method [%s]",
                methodAlias,
                methodIdentifier(targetClass, methodName, methodArity),
                methodIdentifier(targetClass, existingMethod.javaMethod().getName(), existingMethod.typeParameters().size())
            );
        }
    }

    private void addImportedMethodAlias(
        String methodName,
        int methodArity,
        AliasAnnotation alias,
        JavascriptMethod importedJavascriptMethod
    ) {
        String methodAlias = requireAliasType(alias, AliasType.METHOD, "imported method [" + methodName + "/" + methodArity + "]").alias();

        if (METHOD_AND_FIELD_NAME_PATTERN.matcher(methodAlias).matches() == false) {
            throw new IllegalArgumentException("invalid imported method alias name [" + methodAlias + "].");
        }

        String methodAliasKey = buildJavascriptMethodKey(methodAlias, methodArity);
        if (buildJavascriptMethodKey(methodName, methodArity).equals(methodAliasKey)) {
            return;
        }

        if (javascriptMethodKeysToJavascriptClassBindings.containsKey(methodAliasKey)) {
            throw new IllegalArgumentException("imported method and class binding cannot have the same name [" + methodAlias + "]");
        }

        if (javascriptMethodKeysToJavascriptInstanceBindings.containsKey(methodAliasKey)) {
            throw new IllegalArgumentException("imported method and instance binding cannot have the same name [" + methodAlias + "]");
        }

        JavascriptMethod existingImportedJavascriptMethod = javascriptMethodKeysToImportedJavascriptMethods.get(methodAliasKey);
        if (existingImportedJavascriptMethod == null) {
            javascriptMethodKeysToImportedJavascriptMethods.put(methodAliasKey.intern(), importedJavascriptMethod);
        } else if (existingImportedJavascriptMethod.equals(importedJavascriptMethod) == false) {
            throw lookupException(
                "cannot add imported method alias [%s] for method [%s/%s] that shadows imported method [%s/%s]",
                methodAlias,
                methodName,
                methodArity,
                existingImportedJavascriptMethod.javaMethod().getName(),
                existingImportedJavascriptMethod.typeParameters().size()
            );
        }
    }

    private void addJavascriptClass(Class<?> clazz, Map<Class<?>, Object> annotations) {
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(annotations);

        if (clazz == def.class) {
            throw new IllegalArgumentException("cannot add reserved class [" + DEF_CLASS_NAME + "]");
        }

        String canonicalClassName = typeToCanonicalTypeName(clazz);

        if (clazz.isArray()) {
            throw new IllegalArgumentException("cannot add array type [" + canonicalClassName + "] as a class");
        }

        if (CLASS_NAME_PATTERN.matcher(canonicalClassName).matches() == false) {
            throw new IllegalArgumentException("invalid class name [" + canonicalClassName + "]");
        }

        Class<?> existingClass = javaClassNamesToClasses.get(clazz.getName());

        if (existingClass == null) {
            javaClassNamesToClasses.put(clazz.getName().intern(), clazz);
        } else if (existingClass != clazz) {
            throw lookupException(
                "class [%s] cannot represent multiple java classes with the same name from different class loaders",
                canonicalClassName
            );
        }

        existingClass = canonicalClassNamesToClasses.get(canonicalClassName);

        if (existingClass != null && existingClass != clazz) {
            throw lookupException(
                "class [%s] cannot represent multiple java classes with the same name from different class loaders",
                canonicalClassName
            );
        }

        JavascriptClassBuilder existingJavascriptClassBuilder = classesToJavascriptClassBuilders.get(clazz);

        if (existingJavascriptClassBuilder == null) {
            JavascriptClassBuilder javascriptClassBuilder = new JavascriptClassBuilder();
            javascriptClassBuilder.annotations.putAll(annotations);

            canonicalClassNamesToClasses.put(canonicalClassName.intern(), clazz);
            classesToJavascriptClassBuilders.put(clazz, javascriptClassBuilder);
        }

        String javaClassName = clazz.getName();
        String importedCanonicalClassName = javaClassName.substring(javaClassName.lastIndexOf('.') + 1).replace('$', '.');
        boolean importClassName = annotations.containsKey(NoImportAnnotation.class) == false;

        if (canonicalClassName.equals(importedCanonicalClassName)) {
            if (importClassName) {
                throw new IllegalArgumentException("must use no_import parameter on class [" + canonicalClassName + "] with no package");
            }
        } else {
            Class<?> importedClass = canonicalClassNamesToClasses.get(importedCanonicalClassName);

            if (importedClass == null) {
                if (importClassName) {
                    if (existingJavascriptClassBuilder != null) {
                        throw new IllegalArgumentException("inconsistent no_import parameter found for class [" + canonicalClassName + "]");
                    }

                    canonicalClassNamesToClasses.put(importedCanonicalClassName.intern(), clazz);
                    if (annotations.get(AliasAnnotation.class) instanceof AliasAnnotation alias) {
                        String classAlias = requireAliasType(alias, AliasType.CLASS, "class [" + canonicalClassName + "]").alias();
                        Class<?> existing = canonicalClassNamesToClasses.put(classAlias, clazz);
                        if (existing != null) {
                            throw lookupException("Cannot add alias [%s] for [%s] that shadows class [%s]", classAlias, clazz, existing);
                        }
                    }
                }
            } else if (importedClass != clazz) {
                throw lookupException(
                    "imported class [%s] cannot represent multiple classes [%s] and [%s]",
                    importedCanonicalClassName,
                    canonicalClassName,
                    typeToCanonicalTypeName(importedClass)
                );
            } else if (importClassName == false) {
                throw new IllegalArgumentException("inconsistent no_import parameter found for class [" + canonicalClassName + "]");
            }
        }
    }

    private void addJavascriptConstructor(
        String targetCanonicalClassName,
        List<String> canonicalTypeNameParameters,
        Map<Class<?>, Object> annotations,
        Map<Object, Object> dedup
    ) {
        Objects.requireNonNull(targetCanonicalClassName);
        Objects.requireNonNull(canonicalTypeNameParameters);

        Class<?> targetClass = canonicalClassNamesToClasses.get(targetCanonicalClassName);

        if (targetClass == null) {
            throw lookupException(
                "target class [%s] not found for constructor [[%s], %s]",
                targetCanonicalClassName,
                targetCanonicalClassName,
                canonicalTypeNameParameters
            );
        }

        List<Class<?>> typeParameters = new ArrayList<>(canonicalTypeNameParameters.size());

        for (String canonicalTypeNameParameter : canonicalTypeNameParameters) {
            Class<?> typeParameter = canonicalTypeNameToType(canonicalTypeNameParameter);

            if (typeParameter == null) {
                throw lookupException(
                    "type parameter [%s] not found for constructor [[%s], %s]",
                    canonicalTypeNameParameter,
                    targetCanonicalClassName,
                    canonicalTypeNameParameters
                );
            }

            typeParameters.add(typeParameter);
        }

        addJavascriptConstructor(targetClass, typeParameters, annotations, dedup);
    }

    private void addJavascriptConstructor(
        Class<?> targetClass,
        List<Class<?>> typeParameters,
        Map<Class<?>, Object> annotations,
        Map<Object, Object> dedup
    ) {
        Objects.requireNonNull(targetClass);
        Objects.requireNonNull(typeParameters);

        if (targetClass == def.class) {
            throw new IllegalArgumentException("cannot add constructor to reserved class [" + DEF_CLASS_NAME + "]");
        }

        String targetCanonicalClassName = targetClass.getCanonicalName();
        JavascriptClassBuilder javascriptClassBuilder = classesToJavascriptClassBuilders.get(targetClass);

        if (javascriptClassBuilder == null) {
            throw lookupException(
                "target class [%s] not found for constructor [[%s], %s]",
                targetCanonicalClassName,
                targetCanonicalClassName,
                typesToCanonicalTypeNames(typeParameters)
            );
        }

        int typeParametersSize = typeParameters.size();
        List<Class<?>> javaTypeParameters = new ArrayList<>(typeParametersSize);

        for (Class<?> typeParameter : typeParameters) {
            if (isValidType(typeParameter) == false) {
                throw lookupException(
                    "type parameter [%s] not found for constructor [[%s], %s]",
                    typeToCanonicalTypeName(typeParameter),
                    targetCanonicalClassName,
                    typesToCanonicalTypeNames(typeParameters)
                );
            }

            javaTypeParameters.add(typeToJavaType(typeParameter));
        }

        Constructor<?> javaConstructor;

        try {
            javaConstructor = targetClass.getConstructor(javaTypeParameters.toArray(Class<?>[]::new));
        } catch (NoSuchMethodException nsme) {
            throw lookupException(
                nsme,
                "reflection object not found for constructor [[%s], %s]",
                targetCanonicalClassName,
                typesToCanonicalTypeNames(typeParameters)
            );
        }

        MethodHandle methodHandle;

        try {
            methodHandle = lookup(targetClass).unreflectConstructor(javaConstructor);
        } catch (IllegalAccessException iae) {
            throw lookupException(
                iae,
                "method handle not found for constructor [[%s], %s]",
                targetCanonicalClassName,
                typesToCanonicalTypeNames(typeParameters)
            );
        }

        if (annotations.containsKey(CompileTimeOnlyAnnotation.class)) {
            throw new IllegalArgumentException("constructors can't have @" + CompileTimeOnlyAnnotation.NAME);
        }

        MethodType methodType = methodHandle.type();

        String javascriptConstructorKey = buildJavascriptConstructorKey(typeParametersSize);
        JavascriptConstructor existingJavascriptConstructor = javascriptClassBuilder.constructors.get(javascriptConstructorKey);
        JavascriptConstructor newJavascriptConstructor = new JavascriptConstructor(
            javaConstructor,
            typeParameters,
            methodHandle,
            methodType,
            annotations
        );

        if (existingJavascriptConstructor == null) {
            newJavascriptConstructor = (JavascriptConstructor) dedup.computeIfAbsent(newJavascriptConstructor, Function.identity());
            javascriptClassBuilder.constructors.put(javascriptConstructorKey.intern(), newJavascriptConstructor);
        } else if (newJavascriptConstructor.equals(existingJavascriptConstructor) == false) {
            throw lookupException(
                "cannot add constructors with the same arity but are not equivalent for constructors [[%s], %s] and [[%s], %s]",
                targetCanonicalClassName,
                typesToCanonicalTypeNames(typeParameters),
                targetCanonicalClassName,
                typesToCanonicalTypeNames(existingJavascriptConstructor.typeParameters())
            );
        }
    }

    private void addJavascriptMethod(
        ClassLoader classLoader,
        String targetCanonicalClassName,
        String augmentedCanonicalClassName,
        String methodName,
        String returnCanonicalTypeName,
        List<String> canonicalTypeNameParameters,
        Map<Class<?>, Object> annotations,
        Map<Object, Object> dedup
    ) {

        Objects.requireNonNull(classLoader);
        Objects.requireNonNull(targetCanonicalClassName);
        Objects.requireNonNull(methodName);
        Objects.requireNonNull(returnCanonicalTypeName);
        Objects.requireNonNull(canonicalTypeNameParameters);
        Objects.requireNonNull(annotations);

        Class<?> targetClass = canonicalClassNamesToClasses.get(targetCanonicalClassName);

        if (targetClass == null) {
            throw lookupException(
                "target class [%s] not found for method [[%s], [%s], %s]",
                targetCanonicalClassName,
                targetCanonicalClassName,
                methodName,
                canonicalTypeNameParameters
            );
        }

        Class<?> augmentedClass = null;

        if (augmentedCanonicalClassName != null) {
            augmentedClass = loadClass(
                classLoader,
                augmentedCanonicalClassName,
                () -> Strings.format(
                    "augmented class [%s] not found for method [[%s], [%s], %s]",
                    augmentedCanonicalClassName,
                    targetCanonicalClassName,
                    methodName,
                    canonicalTypeNameParameters
                )
            );
        }

        List<Class<?>> typeParameters = new ArrayList<>(canonicalTypeNameParameters.size());

        for (String canonicalTypeNameParameter : canonicalTypeNameParameters) {
            Class<?> typeParameter = canonicalTypeNameToType(canonicalTypeNameParameter);

            if (typeParameter == null) {
                throw lookupException(
                    "type parameter [%s] not found for method [[%s], [%s], %s]",
                    canonicalTypeNameParameter,
                    targetCanonicalClassName,
                    methodName,
                    canonicalTypeNameParameters
                );
            }

            typeParameters.add(typeParameter);
        }

        Class<?> returnType = canonicalTypeNameToType(returnCanonicalTypeName);

        if (returnType == null) {
            throw lookupException(
                "return type [%s] not found for method [[%s], [%s], %s]",
                returnCanonicalTypeName,
                targetCanonicalClassName,
                methodName,
                canonicalTypeNameParameters
            );
        }

        addJavascriptMethod(targetClass, augmentedClass, methodName, returnType, typeParameters, annotations, dedup);
    }

    public void addJavascriptMethod(
        Class<?> targetClass,
        Class<?> augmentedClass,
        String methodName,
        Class<?> returnType,
        List<Class<?>> typeParameters,
        Map<Class<?>, Object> annotations,
        Map<Object, Object> dedup
    ) {

        Objects.requireNonNull(targetClass);
        Objects.requireNonNull(methodName);
        Objects.requireNonNull(returnType);
        Objects.requireNonNull(typeParameters);
        Objects.requireNonNull(annotations);

        if (targetClass == def.class) {
            throw new IllegalArgumentException("cannot add method to reserved class [" + DEF_CLASS_NAME + "]");
        }

        String targetCanonicalClassName = typeToCanonicalTypeName(targetClass);

        if (METHOD_AND_FIELD_NAME_PATTERN.matcher(methodName).matches() == false) {
            throw new IllegalArgumentException(
                "invalid method name [" + methodName + "] for target class [" + targetCanonicalClassName + "]."
            );
        }

        JavascriptClassBuilder javascriptClassBuilder = classesToJavascriptClassBuilders.get(targetClass);

        if (javascriptClassBuilder == null) {
            throw lookupException(
                "target class [%s] not found for method [[%s], [%s], %s]",
                targetCanonicalClassName,
                targetCanonicalClassName,
                methodName,
                typesToCanonicalTypeNames(typeParameters)
            );
        }

        int typeParametersSize = typeParameters.size();
        int augmentedParameterOffset = augmentedClass == null ? 0 : 1;
        List<Class<?>> javaTypeParameters = new ArrayList<>(typeParametersSize + augmentedParameterOffset);

        if (augmentedClass != null) {
            javaTypeParameters.add(targetClass);
        }

        for (Class<?> typeParameter : typeParameters) {
            if (isValidType(typeParameter) == false) {
                throw lookupException(
                    "type parameter [%s] not found for method [[%s], [%s], %s]",
                    typeToCanonicalTypeName(typeParameter),
                    targetCanonicalClassName,
                    methodName,
                    typesToCanonicalTypeNames(typeParameters)
                );
            }

            javaTypeParameters.add(typeToJavaType(typeParameter));
        }

        if (isValidType(returnType) == false) {
            throw lookupException(
                "return type [%s] not found for method [[%s], [%s], %s]",
                typeToCanonicalTypeName(returnType),
                targetCanonicalClassName,
                methodName,
                typesToCanonicalTypeNames(typeParameters)
            );
        }

        Method javaMethod;

        if (augmentedClass == null) {
            try {
                javaMethod = targetClass.getMethod(methodName, javaTypeParameters.toArray(Class<?>[]::new));
            } catch (NoSuchMethodException nsme) {
                throw lookupException(
                    nsme,
                    "reflection object not found for method [[%s], [%s], %s]",
                    targetCanonicalClassName,
                    methodName,
                    typesToCanonicalTypeNames(typeParameters)
                );
            }
        } else {
            try {
                javaMethod = augmentedClass.getMethod(methodName, javaTypeParameters.toArray(Class<?>[]::new));

                if (Modifier.isStatic(javaMethod.getModifiers()) == false) {
                    throw lookupException(
                        "method [[%s], [%s], %s] with augmented class [%s] must be static",
                        targetCanonicalClassName,
                        methodName,
                        typesToCanonicalTypeNames(typeParameters),
                        typeToCanonicalTypeName(augmentedClass)
                    );
                }
            } catch (NoSuchMethodException nsme) {
                throw lookupException(
                    nsme,
                    "reflection object not found for method [[%s], [%s], %s] with augmented class [%s]",
                    targetCanonicalClassName,
                    methodName,
                    typesToCanonicalTypeNames(typeParameters),
                    typeToCanonicalTypeName(augmentedClass)
                );
            }
        }

        // injections alter the type parameters required for the user to call this method, since some are injected by compiler
        InjectConstantAnnotation inject = (InjectConstantAnnotation) annotations.get(InjectConstantAnnotation.class);
        if (inject != null) {
            int numInjections = inject.injects().size();

            if (numInjections > 0) {
                typeParameters.subList(0, numInjections).clear();
            }

            typeParametersSize = typeParameters.size();
        }

        if (javaMethod.getReturnType() != typeToJavaType(returnType)) {
            throw lookupException(
                "return type [%s] does not match the specified returned type [%s] for method [[%s], [%s], %s]",
                typeToCanonicalTypeName(javaMethod.getReturnType()),
                typeToCanonicalTypeName(returnType),
                targetClass.getCanonicalName(),
                methodName,
                typesToCanonicalTypeNames(typeParameters)
            );
        }

        MethodHandle methodHandle;

        if (augmentedClass == null) {
            try {
                methodHandle = lookup(targetClass).unreflect(javaMethod);
            } catch (IllegalAccessException iae) {
                throw lookupException(
                    iae,
                    "method handle not found for method [[%s], [%s], %s], with lookup [%s]",
                    targetClass.getCanonicalName(),
                    methodName,
                    typesToCanonicalTypeNames(typeParameters),
                    lookup(targetClass)
                );
            }
        } else {
            try {
                methodHandle = lookup(augmentedClass).unreflect(javaMethod);
            } catch (IllegalAccessException iae) {
                throw lookupException(
                    iae,
                    "method handle not found for method [[%s], [%s], %s] with augmented class [%s]",
                    targetClass.getCanonicalName(),
                    methodName,
                    typesToCanonicalTypeNames(typeParameters),
                    typeToCanonicalTypeName(augmentedClass)
                );
            }
        }

        if (annotations.containsKey(CompileTimeOnlyAnnotation.class)) {
            throw new IllegalArgumentException("regular methods can't have @" + CompileTimeOnlyAnnotation.NAME);
        }

        MethodType methodType = methodHandle.type();
        boolean isStatic = augmentedClass == null && Modifier.isStatic(javaMethod.getModifiers());
        String javascriptMethodKey = buildJavascriptMethodKey(methodName, typeParametersSize);
        Map<String, JavascriptMethod> javascriptMethods = isStatic ? javascriptClassBuilder.staticMethods : javascriptClassBuilder.methods;
        JavascriptMethod existingJavascriptMethod = javascriptMethods.get(javascriptMethodKey);
        JavascriptMethod newJavascriptMethod = new JavascriptMethod(
            javaMethod,
            targetClass,
            returnType,
            typeParameters,
            methodHandle,
            methodType,
            annotations
        );

        if (existingJavascriptMethod == null) {
            newJavascriptMethod = (JavascriptMethod) dedup.computeIfAbsent(newJavascriptMethod, Function.identity());
            javascriptMethods.put(javascriptMethodKey.intern(), newJavascriptMethod);
        } else if (newJavascriptMethod.equals(existingJavascriptMethod) == false) {
            throw lookupException(
                "cannot add methods with the same name and arity but are not equivalent for methods "
                    + "[[%s], [%s], [%s], %s] and [[%s], [%s], [%s], %s]",
                targetCanonicalClassName,
                methodName,
                typeToCanonicalTypeName(returnType),
                typesToCanonicalTypeNames(typeParameters),
                targetCanonicalClassName,
                methodName,
                typeToCanonicalTypeName(existingJavascriptMethod.returnType()),
                typesToCanonicalTypeNames(existingJavascriptMethod.typeParameters())
            );
        }

        JavascriptMethod javascriptMethod = existingJavascriptMethod == null ? newJavascriptMethod : existingJavascriptMethod;
        if (annotations.get(AliasAnnotation.class) instanceof AliasAnnotation alias) {
            addMethodAlias(javascriptMethods, targetClass, methodName, typeParametersSize, alias, javascriptMethod);
        }
    }

    private void addJavascriptField(
        ClassLoader classLoader,
        String targetCanonicalClassName,
        String fieldName,
        String canonicalTypeNameParameter,
        Map<Class<?>, Object> annotations,
        Map<Object, Object> dedup
    ) {

        Objects.requireNonNull(classLoader);
        Objects.requireNonNull(targetCanonicalClassName);
        Objects.requireNonNull(fieldName);
        Objects.requireNonNull(canonicalTypeNameParameter);
        Objects.requireNonNull(annotations);

        Class<?> targetClass = canonicalClassNamesToClasses.get(targetCanonicalClassName);

        if (targetClass == null) {
            throw lookupException(
                "target class [%s] not found for field [[%s], [%s], [%s]]",
                targetCanonicalClassName,
                targetCanonicalClassName,
                fieldName,
                canonicalTypeNameParameter
            );
        }

        String augmentedCanonicalClassName = annotations.containsKey(AugmentedAnnotation.class)
            ? ((AugmentedAnnotation) annotations.get(AugmentedAnnotation.class)).augmentedCanonicalClassName()
            : null;

        Class<?> augmentedClass = null;

        if (augmentedCanonicalClassName != null) {
            augmentedClass = loadClass(
                classLoader,
                augmentedCanonicalClassName,
                () -> Strings.format(
                    "augmented class [%s] not found for field [[%s], [%s]]",
                    augmentedCanonicalClassName,
                    targetCanonicalClassName,
                    fieldName
                )
            );
        }

        Class<?> typeParameter = canonicalTypeNameToType(canonicalTypeNameParameter);

        if (typeParameter == null) {
            throw lookupException(
                "type parameter [%s] not found for field [[%s], [%s]]",
                canonicalTypeNameParameter,
                targetCanonicalClassName,
                fieldName
            );
        }

        addJavascriptField(targetClass, augmentedClass, fieldName, typeParameter, annotations, dedup);
    }

    private void addJavascriptField(
        Class<?> targetClass,
        Class<?> augmentedClass,
        String fieldName,
        Class<?> typeParameter,
        Map<Class<?>, Object> annotations,
        Map<Object, Object> dedup
    ) {

        Objects.requireNonNull(targetClass);
        Objects.requireNonNull(fieldName);
        Objects.requireNonNull(typeParameter);
        Objects.requireNonNull(annotations);

        if (targetClass == def.class) {
            throw new IllegalArgumentException("cannot add field to reserved class [" + DEF_CLASS_NAME + "]");
        }

        String targetCanonicalClassName = typeToCanonicalTypeName(targetClass);

        if (METHOD_AND_FIELD_NAME_PATTERN.matcher(fieldName).matches() == false) {
            throw new IllegalArgumentException(
                "invalid field name [" + fieldName + "] for target class [" + targetCanonicalClassName + "]."
            );
        }

        JavascriptClassBuilder javascriptClassBuilder = classesToJavascriptClassBuilders.get(targetClass);

        if (javascriptClassBuilder == null) {
            throw lookupException(
                "target class [%s] not found for field [[%s], [%s], [%s]]",
                targetCanonicalClassName,
                targetCanonicalClassName,
                fieldName,
                typeToCanonicalTypeName(typeParameter)
            );
        }

        if (isValidType(typeParameter) == false) {
            throw lookupException(
                "type parameter [%s] not found for field [[%s], [%s], [%s]]",
                typeToCanonicalTypeName(typeParameter),
                targetCanonicalClassName,
                fieldName,
                typeToCanonicalTypeName(typeParameter)
            );
        }

        Field javaField;

        if (augmentedClass == null) {
            try {
                javaField = targetClass.getField(fieldName);
            } catch (NoSuchFieldException nsfe) {
                throw lookupException(
                    nsfe,
                    "reflection object not found for field [[%s], [%s], [%s]]",
                    targetCanonicalClassName,
                    fieldName,
                    typeToCanonicalTypeName(typeParameter)
                );
            }
        } else {
            try {
                javaField = augmentedClass.getField(fieldName);

                if (Modifier.isStatic(javaField.getModifiers()) == false || Modifier.isFinal(javaField.getModifiers()) == false) {
                    throw lookupException(
                        "field [[%s], [%s]] with augmented class [%s] must be static and final",
                        targetCanonicalClassName,
                        fieldName,
                        typeToCanonicalTypeName(augmentedClass)
                    );
                }
            } catch (NoSuchFieldException nsfe) {
                throw lookupException(
                    nsfe,
                    "reflection object not found for field [[%s], [%s], [%s]] with augmented class [%s]",
                    targetCanonicalClassName,
                    fieldName,
                    typeToCanonicalTypeName(typeParameter),
                    typeToCanonicalTypeName(augmentedClass)
                );
            }
        }

        if (javaField.getType() != typeToJavaType(typeParameter)) {
            throw lookupException(
                "type parameter [%s] does not match the specified type parameter [%s] for field [[%s], [%s]]",
                typeToCanonicalTypeName(javaField.getType()),
                typeToCanonicalTypeName(typeParameter),
                targetCanonicalClassName,
                fieldName
            );
        }

        MethodHandle methodHandleGetter;

        try {
            methodHandleGetter = MethodHandles.publicLookup().unreflectGetter(javaField);
        } catch (IllegalAccessException iae) {
            throw new IllegalArgumentException(
                "getter method handle not found for field [[" + targetCanonicalClassName + "], [" + fieldName + "]]"
            );
        }

        String javascriptFieldKey = buildJavascriptFieldKey(fieldName);

        if (Modifier.isStatic(javaField.getModifiers())) {
            if (Modifier.isFinal(javaField.getModifiers()) == false) {
                throw new IllegalArgumentException("static field [[" + targetCanonicalClassName + "], [" + fieldName + "]] must be final");
            }

            JavascriptField existingJavascriptField = javascriptClassBuilder.staticFields.get(javascriptFieldKey);
            JavascriptField newJavascriptField = new JavascriptField(javaField, typeParameter, annotations, methodHandleGetter, null);

            if (existingJavascriptField == null) {
                newJavascriptField = (JavascriptField) dedup.computeIfAbsent(newJavascriptField, Function.identity());
                javascriptClassBuilder.staticFields.put(javascriptFieldKey.intern(), newJavascriptField);
            } else if (newJavascriptField.equals(existingJavascriptField) == false) {
                throw lookupException(
                    "cannot add fields with the same name but are not equivalent for fields [[%s], [%s], [%s]] and [[%s], [%s], [%s]]"
                        + " with the same name and different type parameters",
                    targetCanonicalClassName,
                    fieldName,
                    typeToCanonicalTypeName(typeParameter),
                    targetCanonicalClassName,
                    existingJavascriptField.javaField().getName(),
                    typeToCanonicalTypeName(existingJavascriptField.typeParameter())
                );
            }
        } else {
            MethodHandle methodHandleSetter;

            try {
                methodHandleSetter = MethodHandles.publicLookup().unreflectSetter(javaField);
            } catch (IllegalAccessException iae) {
                throw new IllegalArgumentException(
                    "setter method handle not found for field [[" + targetCanonicalClassName + "], [" + fieldName + "]]"
                );
            }

            JavascriptField existingJavascriptField = javascriptClassBuilder.fields.get(javascriptFieldKey);
            JavascriptField newJavascriptField = new JavascriptField(
                javaField,
                typeParameter,
                annotations,
                methodHandleGetter,
                methodHandleSetter
            );

            if (existingJavascriptField == null) {
                newJavascriptField = (JavascriptField) dedup.computeIfAbsent(newJavascriptField, Function.identity());
                javascriptClassBuilder.fields.put(javascriptFieldKey.intern(), newJavascriptField);
            } else if (newJavascriptField.equals(existingJavascriptField) == false) {
                throw lookupException(
                    "cannot add fields with the same name but are not equivalent for fields [[%s], [%s], [%s]] and [[%s], [%s], [%s]]"
                        + " with the same name and different type parameters",
                    targetCanonicalClassName,
                    fieldName,
                    typeToCanonicalTypeName(typeParameter),
                    targetCanonicalClassName,
                    existingJavascriptField.javaField().getName(),
                    typeToCanonicalTypeName(existingJavascriptField.typeParameter())
                );
            }
        }
    }

    public void addImportedJavascriptMethod(
        ClassLoader classLoader,
        String targetJavaClassName,
        String methodName,
        String returnCanonicalTypeName,
        List<String> canonicalTypeNameParameters,
        Map<Class<?>, Object> annotations,
        Map<Object, Object> dedup
    ) {

        Objects.requireNonNull(classLoader);
        Objects.requireNonNull(targetJavaClassName);
        Objects.requireNonNull(methodName);
        Objects.requireNonNull(returnCanonicalTypeName);
        Objects.requireNonNull(canonicalTypeNameParameters);

        Class<?> targetClass = loadClass(classLoader, targetJavaClassName, () -> "class [" + targetJavaClassName + "] not found");
        String targetCanonicalClassName = typeToCanonicalTypeName(targetClass);

        List<Class<?>> typeParameters = new ArrayList<>(canonicalTypeNameParameters.size());

        for (String canonicalTypeNameParameter : canonicalTypeNameParameters) {
            Class<?> typeParameter = canonicalTypeNameToType(canonicalTypeNameParameter);

            if (typeParameter == null) {
                throw lookupException(
                    "type parameter [%s] not found for imported method [[%s], [%s], %s]",
                    canonicalTypeNameParameter,
                    targetCanonicalClassName,
                    methodName,
                    canonicalTypeNameParameters
                );
            }

            typeParameters.add(typeParameter);
        }

        Class<?> returnType = canonicalTypeNameToType(returnCanonicalTypeName);

        if (returnType == null) {
            throw lookupException(
                "return type [%s] not found for imported method [[%s], [%s], %s]",
                returnCanonicalTypeName,
                targetCanonicalClassName,
                methodName,
                canonicalTypeNameParameters
            );
        }

        addImportedJavascriptMethod(targetClass, methodName, returnType, typeParameters, annotations, dedup);
    }

    public void addImportedJavascriptMethod(
        Class<?> targetClass,
        String methodName,
        Class<?> returnType,
        List<Class<?>> typeParameters,
        Map<Class<?>, Object> annotations,
        Map<Object, Object> dedup
    ) {
        Objects.requireNonNull(targetClass);
        Objects.requireNonNull(methodName);
        Objects.requireNonNull(returnType);
        Objects.requireNonNull(typeParameters);

        if (targetClass == def.class) {
            throw new IllegalArgumentException("cannot add imported method from reserved class [" + DEF_CLASS_NAME + "]");
        }

        String targetCanonicalClassName = typeToCanonicalTypeName(targetClass);
        Class<?> existingTargetClass = javaClassNamesToClasses.get(targetClass.getName());

        if (existingTargetClass == null) {
            javaClassNamesToClasses.put(targetClass.getName().intern(), targetClass);
        } else if (existingTargetClass != targetClass) {
            throw lookupException(
                "class [%s] cannot represent multiple java classes with the same name from different class loaders",
                targetCanonicalClassName
            );
        }

        if (METHOD_AND_FIELD_NAME_PATTERN.matcher(methodName).matches() == false) {
            throw new IllegalArgumentException(
                "invalid imported method name [" + methodName + "] for target class [" + targetCanonicalClassName + "]."
            );
        }

        int typeParametersSize = typeParameters.size();
        List<Class<?>> javaTypeParameters = new ArrayList<>(typeParametersSize);

        for (Class<?> typeParameter : typeParameters) {
            if (isValidType(typeParameter) == false) {
                throw lookupException(
                    "type parameter [%s] not found for imported method [[%s], [%s], %s]",
                    typeToCanonicalTypeName(typeParameter),
                    targetCanonicalClassName,
                    methodName,
                    typesToCanonicalTypeNames(typeParameters)
                );
            }

            javaTypeParameters.add(typeToJavaType(typeParameter));
        }

        if (isValidType(returnType) == false) {
            throw lookupException(
                "return type [%s] not found for imported method [[%s], [%s], %s]",
                typeToCanonicalTypeName(returnType),
                targetCanonicalClassName,
                methodName,
                typesToCanonicalTypeNames(typeParameters)
            );
        }

        Method javaMethod;

        try {
            javaMethod = targetClass.getMethod(methodName, javaTypeParameters.toArray(new Class<?>[typeParametersSize]));
        } catch (NoSuchMethodException nsme) {
            throw lookupException(
                nsme,
                "imported method reflection object [[%s], [%s], %s] not found",
                targetCanonicalClassName,
                methodName,
                typesToCanonicalTypeNames(typeParameters)
            );
        }

        if (javaMethod.getReturnType() != typeToJavaType(returnType)) {
            throw lookupException(
                "return type [%s] does not match the specified returned type [%s] for imported method [[%s], [%s], %s]",
                typeToCanonicalTypeName(javaMethod.getReturnType()),
                typeToCanonicalTypeName(returnType),
                targetClass.getCanonicalName(),
                methodName,
                typesToCanonicalTypeNames(typeParameters)
            );
        }

        if (Modifier.isStatic(javaMethod.getModifiers()) == false) {
            throw lookupException(
                "imported method [[%s], [%s], %s] must be static",
                targetClass.getCanonicalName(),
                methodName,
                typesToCanonicalTypeNames(typeParameters)
            );
        }

        String javascriptMethodKey = buildJavascriptMethodKey(methodName, typeParametersSize);

        if (javascriptMethodKeysToJavascriptClassBindings.containsKey(javascriptMethodKey)) {
            throw new IllegalArgumentException("imported method and class binding cannot have the same name [" + methodName + "]");
        }

        if (javascriptMethodKeysToJavascriptInstanceBindings.containsKey(javascriptMethodKey)) {
            throw new IllegalArgumentException("imported method and instance binding cannot have the same name [" + methodName + "]");
        }

        MethodHandle methodHandle;

        try {
            methodHandle = lookup(targetClass).unreflect(javaMethod);
        } catch (IllegalAccessException iae) {
            throw lookupException(
                iae,
                "imported method handle [[%s], [%s], %s] not found",
                targetClass.getCanonicalName(),
                methodName,
                typesToCanonicalTypeNames(typeParameters)
            );
        }

        MethodType methodType = methodHandle.type();

        JavascriptMethod existingImportedJavascriptMethod = javascriptMethodKeysToImportedJavascriptMethods.get(javascriptMethodKey);
        JavascriptMethod newImportedJavascriptMethod = new JavascriptMethod(
            javaMethod,
            targetClass,
            returnType,
            typeParameters,
            methodHandle,
            methodType,
            annotations
        );

        if (existingImportedJavascriptMethod == null) {
            newImportedJavascriptMethod = (JavascriptMethod) dedup.computeIfAbsent(newImportedJavascriptMethod, Function.identity());
            javascriptMethodKeysToImportedJavascriptMethods.put(javascriptMethodKey.intern(), newImportedJavascriptMethod);
        } else if (newImportedJavascriptMethod.equals(existingImportedJavascriptMethod) == false) {
            throw lookupException(
                "cannot add imported methods with the same name and arity but do not have equivalent methods "
                    + "[[%s], [%s], [%s], %s] and [[%s], [%s], [%s], %s]",
                targetCanonicalClassName,
                methodName,
                typeToCanonicalTypeName(returnType),
                typesToCanonicalTypeNames(typeParameters),
                targetCanonicalClassName,
                methodName,
                typeToCanonicalTypeName(existingImportedJavascriptMethod.returnType()),
                typesToCanonicalTypeNames(existingImportedJavascriptMethod.typeParameters())
            );
        }

        JavascriptMethod importedJavascriptMethod = existingImportedJavascriptMethod == null
            ? newImportedJavascriptMethod
            : existingImportedJavascriptMethod;
        if (annotations.get(AliasAnnotation.class) instanceof AliasAnnotation alias) {
            addImportedMethodAlias(methodName, typeParametersSize, alias, importedJavascriptMethod);
        }
    }

    public void addJavascriptClassBinding(
        ClassLoader classLoader,
        String targetJavaClassName,
        String methodName,
        String returnCanonicalTypeName,
        List<String> canonicalTypeNameParameters,
        Map<Class<?>, Object> annotations,
        Map<Object, Object> dedup
    ) {

        Objects.requireNonNull(classLoader);
        Objects.requireNonNull(targetJavaClassName);
        Objects.requireNonNull(methodName);
        Objects.requireNonNull(returnCanonicalTypeName);
        Objects.requireNonNull(canonicalTypeNameParameters);

        Class<?> targetClass = loadClass(classLoader, targetJavaClassName, () -> "class [" + targetJavaClassName + "] not found");
        String targetCanonicalClassName = typeToCanonicalTypeName(targetClass);
        List<Class<?>> typeParameters = new ArrayList<>(canonicalTypeNameParameters.size());

        for (String canonicalTypeNameParameter : canonicalTypeNameParameters) {
            Class<?> typeParameter = canonicalTypeNameToType(canonicalTypeNameParameter);

            if (typeParameter == null) {
                throw lookupException(
                    "type parameter [%s] not found for class binding [[%s], [%s], %s]",
                    canonicalTypeNameParameter,
                    targetCanonicalClassName,
                    methodName,
                    canonicalTypeNameParameters
                );
            }

            typeParameters.add(typeParameter);
        }

        Class<?> returnType = canonicalTypeNameToType(returnCanonicalTypeName);

        if (returnType == null) {
            throw lookupException(
                "return type [%s] not found for class binding [[%s], [%s], %s]",
                returnCanonicalTypeName,
                targetCanonicalClassName,
                methodName,
                canonicalTypeNameParameters
            );
        }

        addJavascriptClassBinding(targetClass, methodName, returnType, typeParameters, annotations, dedup);
    }

    private void addJavascriptClassBinding(
        Class<?> targetClass,
        String methodName,
        Class<?> returnType,
        List<Class<?>> typeParameters,
        Map<Class<?>, Object> annotations,
        Map<Object, Object> dedup
    ) {
        Objects.requireNonNull(targetClass);
        Objects.requireNonNull(methodName);
        Objects.requireNonNull(returnType);
        Objects.requireNonNull(typeParameters);

        if (targetClass == def.class) {
            throw new IllegalArgumentException("cannot add class binding as reserved class [" + DEF_CLASS_NAME + "]");
        }

        String targetCanonicalClassName = typeToCanonicalTypeName(targetClass);
        Class<?> existingTargetClass = javaClassNamesToClasses.get(targetClass.getName());

        if (existingTargetClass == null) {
            javaClassNamesToClasses.put(targetClass.getName().intern(), targetClass);
        } else if (existingTargetClass != targetClass) {
            throw lookupException(
                "class [%s] cannot represent multiple java classes with the same name from different class loaders",
                targetCanonicalClassName
            );
        }

        Constructor<?>[] javaConstructors = targetClass.getConstructors();
        Constructor<?> javaConstructor = null;

        for (Constructor<?> eachJavaConstructor : javaConstructors) {
            if (eachJavaConstructor.getDeclaringClass() == targetClass) {
                if (javaConstructor != null) {
                    throw new IllegalArgumentException(
                        "class binding [" + targetCanonicalClassName + "] cannot have multiple constructors"
                    );
                }

                javaConstructor = eachJavaConstructor;
            }
        }

        if (javaConstructor == null) {
            throw new IllegalArgumentException("class binding [" + targetCanonicalClassName + "] must have exactly one constructor");
        }

        Class<?>[] constructorParameterTypes = javaConstructor.getParameterTypes();

        for (int typeParameterIndex = 0; typeParameterIndex < constructorParameterTypes.length; ++typeParameterIndex) {
            Class<?> typeParameter = typeParameters.get(typeParameterIndex);

            if (isValidType(typeParameter) == false) {
                throw lookupException(
                    "type parameter [%s] not found for class binding [[%s], %s]",
                    typeToCanonicalTypeName(typeParameter),
                    targetCanonicalClassName,
                    typesToCanonicalTypeNames(typeParameters)
                );
            }

            Class<?> javaTypeParameter = constructorParameterTypes[typeParameterIndex];

            if (isValidType(javaTypeParameter) == false) {
                throw lookupException(
                    "type parameter [%s] not found for class binding [[%s], %s]",
                    typeToCanonicalTypeName(typeParameter),
                    targetCanonicalClassName,
                    typesToCanonicalTypeNames(typeParameters)
                );
            }

            if (javaTypeParameter != typeToJavaType(typeParameter)) {
                throw lookupException(
                    "type parameter [%s] does not match the specified type parameter [%s] for class binding [[%s], %s]",
                    typeToCanonicalTypeName(javaTypeParameter),
                    typeToCanonicalTypeName(typeParameter),
                    targetClass.getCanonicalName(),
                    typesToCanonicalTypeNames(typeParameters)
                );
            }
        }

        if (METHOD_AND_FIELD_NAME_PATTERN.matcher(methodName).matches() == false) {
            throw new IllegalArgumentException(
                "invalid method name [" + methodName + "] for class binding [" + targetCanonicalClassName + "]."
            );
        }

        if (annotations.containsKey(CompileTimeOnlyAnnotation.class)) {
            throw new IllegalArgumentException("class bindings can't have @" + CompileTimeOnlyAnnotation.NAME);
        }

        Method[] javaMethods = targetClass.getMethods();
        Method javaMethod = null;

        for (Method eachJavaMethod : javaMethods) {
            if (eachJavaMethod.getDeclaringClass() == targetClass) {
                if (javaMethod != null) {
                    throw new IllegalArgumentException("class binding [" + targetCanonicalClassName + "] cannot have multiple methods");
                }

                javaMethod = eachJavaMethod;
            }
        }

        if (javaMethod == null) {
            throw new IllegalArgumentException("class binding [" + targetCanonicalClassName + "] must have exactly one method");
        }

        Class<?>[] methodParameterTypes = javaMethod.getParameterTypes();

        for (int typeParameterIndex = 0; typeParameterIndex < methodParameterTypes.length; ++typeParameterIndex) {
            Class<?> typeParameter = typeParameters.get(constructorParameterTypes.length + typeParameterIndex);

            if (isValidType(typeParameter) == false) {
                throw lookupException(
                    "type parameter [%s] not found for class binding [[%s], %s]",
                    typeToCanonicalTypeName(typeParameter),
                    targetCanonicalClassName,
                    typesToCanonicalTypeNames(typeParameters)
                );
            }

            Class<?> javaTypeParameter = javaMethod.getParameterTypes()[typeParameterIndex];

            if (isValidType(javaTypeParameter) == false) {
                throw lookupException(
                    "type parameter [%s] not found for class binding [[%s], %s]",
                    typeToCanonicalTypeName(typeParameter),
                    targetCanonicalClassName,
                    typesToCanonicalTypeNames(typeParameters)
                );
            }

            if (javaTypeParameter != typeToJavaType(typeParameter)) {
                throw lookupException(
                    "type parameter [%s] does not match the specified type parameter [%s] for class binding [[%s], %s]",
                    typeToCanonicalTypeName(javaTypeParameter),
                    typeToCanonicalTypeName(typeParameter),
                    targetClass.getCanonicalName(),
                    typesToCanonicalTypeNames(typeParameters)
                );
            }
        }

        if (isValidType(returnType) == false) {
            throw lookupException(
                "return type [%s] not found for class binding [[%s], [%s], %s]",
                typeToCanonicalTypeName(returnType),
                targetCanonicalClassName,
                methodName,
                typesToCanonicalTypeNames(typeParameters)
            );
        }

        if (javaMethod.getReturnType() != typeToJavaType(returnType)) {
            throw lookupException(
                "return type [%s] does not match the specified returned type [%s] for class binding [[%s], [%s], %s]",
                typeToCanonicalTypeName(javaMethod.getReturnType()),
                typeToCanonicalTypeName(returnType),
                targetClass.getCanonicalName(),
                methodName,
                typesToCanonicalTypeNames(typeParameters)
            );
        }

        String javascriptMethodKey = buildJavascriptMethodKey(methodName, constructorParameterTypes.length + methodParameterTypes.length);

        if (javascriptMethodKeysToImportedJavascriptMethods.containsKey(javascriptMethodKey)) {
            throw new IllegalArgumentException("class binding and imported method cannot have the same name [" + methodName + "]");
        }

        if (javascriptMethodKeysToJavascriptInstanceBindings.containsKey(javascriptMethodKey)) {
            throw new IllegalArgumentException("class binding and instance binding cannot have the same name [" + methodName + "]");
        }

        if (Modifier.isStatic(javaMethod.getModifiers())) {
            throw lookupException(
                "class binding [[%s], [%s], %s] cannot be static",
                targetClass.getCanonicalName(),
                methodName,
                typesToCanonicalTypeNames(typeParameters)
            );
        }

        JavascriptClassBinding existingJavascriptClassBinding = javascriptMethodKeysToJavascriptClassBindings.get(javascriptMethodKey);
        JavascriptClassBinding newJavascriptClassBinding = new JavascriptClassBinding(
            javaConstructor,
            javaMethod,
            returnType,
            typeParameters,
            annotations
        );

        if (existingJavascriptClassBinding == null) {
            newJavascriptClassBinding = (JavascriptClassBinding) dedup.computeIfAbsent(newJavascriptClassBinding, Function.identity());
            javascriptMethodKeysToJavascriptClassBindings.put(javascriptMethodKey.intern(), newJavascriptClassBinding);
        } else if (newJavascriptClassBinding.equals(existingJavascriptClassBinding) == false) {
            throw lookupException(
                "cannot add class bindings with the same name and arity but do not have equivalent methods "
                    + "[[%s], [%s], [%s], %s] and [[%s], [%s], [%s], %s]",
                targetCanonicalClassName,
                methodName,
                typeToCanonicalTypeName(returnType),
                typesToCanonicalTypeNames(typeParameters),
                targetCanonicalClassName,
                methodName,
                typeToCanonicalTypeName(existingJavascriptClassBinding.returnType()),
                typesToCanonicalTypeNames(existingJavascriptClassBinding.typeParameters())
            );
        }
    }

    public void addJavascriptInstanceBinding(
        Object targetInstance,
        String methodName,
        String returnCanonicalTypeName,
        List<String> canonicalTypeNameParameters,
        Map<Class<?>, Object> javascriptAnnotations,
        Map<Object, Object> dedup
    ) {

        Objects.requireNonNull(targetInstance);
        Objects.requireNonNull(methodName);
        Objects.requireNonNull(returnCanonicalTypeName);
        Objects.requireNonNull(canonicalTypeNameParameters);

        Class<?> targetClass = targetInstance.getClass();
        String targetCanonicalClassName = typeToCanonicalTypeName(targetClass);
        List<Class<?>> typeParameters = new ArrayList<>(canonicalTypeNameParameters.size());

        for (String canonicalTypeNameParameter : canonicalTypeNameParameters) {
            Class<?> typeParameter = canonicalTypeNameToType(canonicalTypeNameParameter);

            if (typeParameter == null) {
                throw lookupException(
                    "type parameter [%s] not found for instance binding [[%s], [%s], %s]",
                    canonicalTypeNameParameter,
                    targetCanonicalClassName,
                    methodName,
                    canonicalTypeNameParameters
                );
            }

            typeParameters.add(typeParameter);
        }

        Class<?> returnType = canonicalTypeNameToType(returnCanonicalTypeName);

        if (returnType == null) {
            throw lookupException(
                "return type [%s] not found for class binding [[%s], [%s], %s]",
                returnCanonicalTypeName,
                targetCanonicalClassName,
                methodName,
                canonicalTypeNameParameters
            );
        }

        addJavascriptInstanceBinding(targetInstance, methodName, returnType, typeParameters, javascriptAnnotations, dedup);
    }

    public void addJavascriptInstanceBinding(
        Object targetInstance,
        String methodName,
        Class<?> returnType,
        List<Class<?>> typeParameters,
        Map<Class<?>, Object> javascriptAnnotations,
        Map<Object, Object> dedup
    ) {
        Objects.requireNonNull(targetInstance);
        Objects.requireNonNull(methodName);
        Objects.requireNonNull(returnType);
        Objects.requireNonNull(typeParameters);

        Class<?> targetClass = targetInstance.getClass();

        if (targetClass == def.class) {
            throw new IllegalArgumentException("cannot add instance binding as reserved class [" + DEF_CLASS_NAME + "]");
        }

        String targetCanonicalClassName = typeToCanonicalTypeName(targetClass);
        Class<?> existingTargetClass = javaClassNamesToClasses.get(targetClass.getName());

        if (existingTargetClass == null) {
            javaClassNamesToClasses.put(targetClass.getName().intern(), targetClass);
        } else if (existingTargetClass != targetClass) {
            throw lookupException(
                "class [%s] cannot represent multiple java classes with the same name from different class loaders",
                targetCanonicalClassName
            );
        }

        if (METHOD_AND_FIELD_NAME_PATTERN.matcher(methodName).matches() == false) {
            throw new IllegalArgumentException(
                "invalid method name [" + methodName + "] for instance binding [" + targetCanonicalClassName + "]."
            );
        }

        int typeParametersSize = typeParameters.size();
        List<Class<?>> javaTypeParameters = new ArrayList<>(typeParametersSize);

        for (Class<?> typeParameter : typeParameters) {
            if (isValidType(typeParameter) == false) {
                throw lookupException(
                    "type parameter [%s] not found for instance binding [[%s], [%s], %s]",
                    typeToCanonicalTypeName(typeParameter),
                    targetCanonicalClassName,
                    methodName,
                    typesToCanonicalTypeNames(typeParameters)
                );
            }

            javaTypeParameters.add(typeToJavaType(typeParameter));
        }

        if (isValidType(returnType) == false) {
            throw lookupException(
                "return type [%s] not found for imported method [[%s], [%s], %s]",
                typeToCanonicalTypeName(returnType),
                targetCanonicalClassName,
                methodName,
                typesToCanonicalTypeNames(typeParameters)
            );
        }

        Method javaMethod;

        try {
            javaMethod = targetClass.getMethod(methodName, javaTypeParameters.toArray(new Class<?>[typeParametersSize]));
        } catch (NoSuchMethodException nsme) {
            throw lookupException(
                nsme,
                "instance binding reflection object [[%s], [%s], %s] not found",
                targetCanonicalClassName,
                methodName,
                typesToCanonicalTypeNames(typeParameters)
            );
        }

        if (javaMethod.getReturnType() != typeToJavaType(returnType)) {
            throw lookupException(
                "return type [%s] does not match the specified returned type [%s] for instance binding [[%s], [%s], %s]",
                typeToCanonicalTypeName(javaMethod.getReturnType()),
                typeToCanonicalTypeName(returnType),
                targetClass.getCanonicalName(),
                methodName,
                typesToCanonicalTypeNames(typeParameters)
            );
        }

        if (Modifier.isStatic(javaMethod.getModifiers())) {
            throw lookupException(
                "instance binding [[%s], [%s], %s] cannot be static",
                targetClass.getCanonicalName(),
                methodName,
                typesToCanonicalTypeNames(typeParameters)
            );
        }

        String javascriptMethodKey = buildJavascriptMethodKey(methodName, typeParametersSize);

        if (javascriptMethodKeysToImportedJavascriptMethods.containsKey(javascriptMethodKey)) {
            throw new IllegalArgumentException("instance binding and imported method cannot have the same name [" + methodName + "]");
        }

        if (javascriptMethodKeysToJavascriptClassBindings.containsKey(javascriptMethodKey)) {
            throw new IllegalArgumentException("instance binding and class binding cannot have the same name [" + methodName + "]");
        }

        JavascriptInstanceBinding existingJavascriptInstanceBinding = javascriptMethodKeysToJavascriptInstanceBindings.get(
            javascriptMethodKey
        );
        JavascriptInstanceBinding newJavascriptInstanceBinding = new JavascriptInstanceBinding(
            targetInstance,
            javaMethod,
            returnType,
            typeParameters,
            javascriptAnnotations
        );

        if (existingJavascriptInstanceBinding == null) {
            newJavascriptInstanceBinding = (JavascriptInstanceBinding) dedup.computeIfAbsent(
                newJavascriptInstanceBinding,
                Function.identity()
            );
            javascriptMethodKeysToJavascriptInstanceBindings.put(javascriptMethodKey.intern(), newJavascriptInstanceBinding);
        } else if (newJavascriptInstanceBinding.equals(existingJavascriptInstanceBinding) == false) {
            throw lookupException(
                "cannot add instances bindings with the same name and arity but do not have equivalent methods "
                    + "[[%s], [%s], [%s], %s], %s and [[%s], [%s], [%s], %s], %s",
                targetCanonicalClassName,
                methodName,
                typeToCanonicalTypeName(returnType),
                typesToCanonicalTypeNames(typeParameters),
                javascriptAnnotations,
                targetCanonicalClassName,
                methodName,
                typeToCanonicalTypeName(existingJavascriptInstanceBinding.returnType()),
                typesToCanonicalTypeNames(existingJavascriptInstanceBinding.typeParameters()),
                existingJavascriptInstanceBinding.annotations()
            );
        }
    }

    public JavascriptLookup build(Map<Object, Object> dedup, Map<JavascriptMethod, JavascriptMethod> filteredMethodCache) {
        buildJavascriptClassHierarchy();
        setFunctionalInterfaceMethods();
        generateRuntimeMethods(filteredMethodCache);
        cacheRuntimeHandles();

        Map<Class<?>, JavascriptClass> classesToJavascriptClasses = Maps.newMapWithExpectedSize(classesToJavascriptClassBuilders.size());

        for (Map.Entry<Class<?>, JavascriptClassBuilder> javascriptClassBuilderEntry : classesToJavascriptClassBuilders.entrySet()) {
            classesToJavascriptClasses.put(
                javascriptClassBuilderEntry.getKey(),
                (JavascriptClass) dedup.computeIfAbsent(javascriptClassBuilderEntry.getValue().build(), Function.identity())
            );
        }

        if (javaClassNamesToClasses.values().containsAll(canonicalClassNamesToClasses.values()) == false) {
            throw new IllegalArgumentException(
                "the values of java class names to classes must be a superset of the values of canonical class names to classes"
            );
        }

        if (javaClassNamesToClasses.values().containsAll(classesToJavascriptClasses.keySet()) == false) {
            throw new IllegalArgumentException(
                "the values of java class names to classes must be a superset of the keys of classes to javascript classes"
            );
        }

        if (canonicalClassNamesToClasses.values().containsAll(classesToJavascriptClasses.keySet()) == false
            || classesToJavascriptClasses.keySet().containsAll(canonicalClassNamesToClasses.values()) == false) {
            throw new IllegalArgumentException(
                "the values of canonical class names to classes must have the same classes as the keys of classes to javascript classes"
            );
        }

        classesToDirectSubClasses.replaceAll((key, set) -> Set.copyOf(set)); // save some memory, especially when set is empty
        return new JavascriptLookup(
            javaClassNamesToClasses,
            canonicalClassNamesToClasses,
            classesToJavascriptClasses,
            classesToDirectSubClasses,
            javascriptMethodKeysToImportedJavascriptMethods,
            javascriptMethodKeysToJavascriptClassBindings,
            javascriptMethodKeysToJavascriptInstanceBindings
        );
    }

    private void buildJavascriptClassHierarchy() {
        for (Class<?> targetClass : classesToJavascriptClassBuilders.keySet()) {
            classesToDirectSubClasses.put(targetClass, new HashSet<>());
        }

        for (Class<?> subClass : classesToJavascriptClassBuilders.keySet()) {
            Deque<Class<?>> superInterfaces = new ArrayDeque<>(Arrays.asList(subClass.getInterfaces()));

            // we check for Object.class as part of the allow listed classes because
            // it is possible for the compiler to work without Object
            if (subClass.isInterface() && superInterfaces.isEmpty() && classesToJavascriptClassBuilders.containsKey(Object.class)) {
                classesToDirectSubClasses.get(Object.class).add(subClass);
            } else {
                Class<?> superClass = subClass.getSuperclass();

                // this finds the nearest super class for a given sub class
                // because the allow list may have gaps between classes
                // example:
                // class A {} // allowed
                // class B extends A // not allowed
                // class C extends B // allowed
                // in this case C is considered a direct sub class of A
                while (superClass != null) {
                    if (classesToJavascriptClassBuilders.containsKey(superClass)) {
                        break;
                    } else {
                        // this ensures all interfaces from a sub class that
                        // is not allow listed are checked if they are
                        // considered a direct super class of the sub class
                        // because these interfaces may still be allow listed
                        // even if their sub class is not
                        superInterfaces.addAll(Arrays.asList(superClass.getInterfaces()));
                    }

                    superClass = superClass.getSuperclass();
                }

                if (superClass != null) {
                    classesToDirectSubClasses.get(superClass).add(subClass);
                }
            }

            Set<Class<?>> resolvedInterfaces = new HashSet<>();

            while (superInterfaces.isEmpty() == false) {
                Class<?> superInterface = superInterfaces.removeFirst();

                if (resolvedInterfaces.add(superInterface)) {
                    if (classesToJavascriptClassBuilders.containsKey(superInterface)) {
                        classesToDirectSubClasses.get(superInterface).add(subClass);
                    } else {
                        superInterfaces.addAll(Arrays.asList(superInterface.getInterfaces()));
                    }
                }
            }
        }
    }

    private void setFunctionalInterfaceMethods() {
        classesToJavascriptClassBuilders.forEach(this::setFunctionalInterfaceMethod);
    }

    private void setFunctionalInterfaceMethod(Class<?> targetClass, JavascriptClassBuilder targetJavascriptClassBuilder) {
        if (targetClass.isInterface()) {
            List<java.lang.reflect.Method> javaMethods = new ArrayList<>();

            for (java.lang.reflect.Method javaMethod : targetClass.getMethods()) {
                if (javaMethod.isDefault() == false && Modifier.isStatic(javaMethod.getModifiers()) == false) {
                    try {
                        Object.class.getMethod(javaMethod.getName(), javaMethod.getParameterTypes());
                    } catch (ReflectiveOperationException roe) {
                        javaMethods.add(javaMethod);
                    }
                }
            }

            if (javaMethods.size() != 1 && targetClass.isAnnotationPresent(FunctionalInterface.class)) {
                throw lookupException(
                    "class [%s] is illegally marked as a FunctionalInterface with java methods %s",
                    typeToCanonicalTypeName(targetClass),
                    javaMethods
                );
            } else if (javaMethods.size() == 1) {
                java.lang.reflect.Method javaMethod = javaMethods.get(0);
                String javascriptMethodKey = buildJavascriptMethodKey(javaMethod.getName(), javaMethod.getParameterCount());

                Deque<Class<?>> superInterfaces = new ArrayDeque<>();
                Set<Class<?>> resolvedInterfaces = new HashSet<>();

                superInterfaces.addLast(targetClass);

                Class<?> superInterface;
                while ((superInterface = superInterfaces.pollFirst()) != null) {

                    if (resolvedInterfaces.add(superInterface)) {
                        JavascriptClassBuilder functionalInterfaceJavascriptClassBuilder = classesToJavascriptClassBuilders.get(
                            superInterface
                        );

                        if (functionalInterfaceJavascriptClassBuilder != null) {
                            targetJavascriptClassBuilder.functionalInterfaceMethod = functionalInterfaceJavascriptClassBuilder.methods.get(
                                javascriptMethodKey
                            );

                            if (targetJavascriptClassBuilder.functionalInterfaceMethod != null) {
                                break;
                            }
                        }

                        superInterfaces.addAll(Arrays.asList(superInterface.getInterfaces()));
                    }
                }
            }
        }
    }

    /**
     * Creates a {@link Map} of JavascriptMethodKeys to {@link JavascriptMethod}s per {@link JavascriptClass} stored as
     * {@link JavascriptClass#runtimeMethods} identical to {@link JavascriptClass#methods} with the exception of generated
     * bridge methods. A generated bridge method is created for each whitelisted method that has at least one parameter
     * with a boxed type to cast from other numeric primitive/boxed types in a symmetric was not handled by
     * {@link MethodHandle#asType(MethodType)}. As an example {@link MethodHandle#asType(MethodType)} legally casts
     * from {@link Integer} to long but not from int to {@link Long}. Generated bridge methods cover the latter case.
     * A generated bridge method replaces the method its a bridge to in the {@link JavascriptClass#runtimeMethods}
     * {@link Map}. The {@link JavascriptClass#runtimeMethods} {@link Map} is used exclusively to look up methods at
     * run-time resulting from calls with a def type value target.
     */
    private void generateRuntimeMethods(Map<JavascriptMethod, JavascriptMethod> filteredMethodCache) {
        for (Map.Entry<Class<?>, JavascriptClassBuilder> javascriptClassBuilderEntry : classesToJavascriptClassBuilders.entrySet()) {
            Class<?> targetClass = javascriptClassBuilderEntry.getKey();
            JavascriptClassBuilder javascriptClassBuilder = javascriptClassBuilderEntry.getValue();
            javascriptClassBuilder.runtimeMethods.putAll(javascriptClassBuilder.methods);

            for (Map.Entry<String, JavascriptMethod> javascriptMethodEntry : javascriptClassBuilder.runtimeMethods.entrySet()) {
                String javascriptMethodKey = javascriptMethodEntry.getKey();
                JavascriptMethod javascriptMethod = javascriptMethodEntry.getValue();
                for (Class<?> typeParameter : javascriptMethod.typeParameters()) {
                    if (typeParameter == Byte.class
                        || typeParameter == Short.class
                        || typeParameter == Character.class
                        || typeParameter == Integer.class
                        || typeParameter == Long.class
                        || typeParameter == Float.class
                        || typeParameter == Double.class) {
                        generateFilteredMethod(
                            targetClass,
                            javascriptClassBuilder,
                            javascriptMethodKey,
                            javascriptMethod,
                            filteredMethodCache
                        );
                    }
                }
            }
        }
    }

    private static void generateFilteredMethod(
        Class<?> targetClass,
        JavascriptClassBuilder javascriptClassBuilder,
        String javascriptMethodKey,
        JavascriptMethod javascriptMethod,
        Map<JavascriptMethod, JavascriptMethod> filteredMethodCache
    ) {
        JavascriptMethod filteredJavascriptMethod = filteredMethodCache.get(javascriptMethod);

        if (filteredJavascriptMethod == null) {
            Method javaMethod = javascriptMethod.javaMethod();
            boolean isStatic = Modifier.isStatic(javascriptMethod.javaMethod().getModifiers());
            int filteredTypeParameterOffset = isStatic ? 0 : 1;
            List<Class<?>> filteredTypeParameters = new ArrayList<>(javaMethod.getParameterCount() + filteredTypeParameterOffset);

            if (isStatic == false) {
                filteredTypeParameters.add(javaMethod.getDeclaringClass());
            }

            for (Class<?> typeParameter : javaMethod.getParameterTypes()) {
                if (typeParameter == Byte.class
                    || typeParameter == Short.class
                    || typeParameter == Character.class
                    || typeParameter == Integer.class
                    || typeParameter == Long.class
                    || typeParameter == Float.class
                    || typeParameter == Double.class) {
                    filteredTypeParameters.add(Object.class);
                } else {
                    filteredTypeParameters.add(typeParameter);
                }
            }

            MethodType filteredMethodType = MethodType.methodType(javascriptMethod.returnType(), filteredTypeParameters);
            MethodHandle filteredMethodHandle = javascriptMethod.methodHandle();

            try {
                Class<?>[] methodParameters = javaMethod.getParameterTypes();
                for (int typeParameterCount = 0; typeParameterCount < methodParameters.length; ++typeParameterCount) {
                    Class<?> typeParameter = methodParameters[typeParameterCount];
                    MethodHandle castMethodHandle = Def.DEF_TO_BOXED_TYPE_IMPLICIT_CAST.get(typeParameter);

                    if (castMethodHandle != null) {
                        filteredMethodHandle = MethodHandles.filterArguments(
                            filteredMethodHandle,
                            typeParameterCount + filteredTypeParameterOffset,
                            castMethodHandle
                        );
                    }
                }

                filteredJavascriptMethod = new JavascriptMethod(
                    javascriptMethod.javaMethod(),
                    targetClass,
                    javascriptMethod.returnType(),
                    filteredTypeParameters,
                    filteredMethodHandle,
                    filteredMethodType,
                    Map.of()
                );
                javascriptClassBuilder.runtimeMethods.put(javascriptMethodKey.intern(), filteredJavascriptMethod);
                filteredMethodCache.put(javascriptMethod, filteredJavascriptMethod);
            } catch (Exception exception) {
                throw new IllegalStateException(
                    "internal error occurred attempting to generate a runtime method [" + javascriptMethodKey + "]",
                    exception
                );
            }
        } else {
            javascriptClassBuilder.runtimeMethods.put(javascriptMethodKey.intern(), filteredJavascriptMethod);
        }
    }

    private void cacheRuntimeHandles() {
        classesToJavascriptClassBuilders.values().forEach(JavascriptLookupBuilder::cacheRuntimeHandles);
    }

    private static void cacheRuntimeHandles(JavascriptClassBuilder javascriptClassBuilder) {
        for (Map.Entry<String, JavascriptMethod> javascriptMethodEntry : javascriptClassBuilder.methods.entrySet()) {
            String methodKey = javascriptMethodEntry.getKey();
            JavascriptMethod javascriptMethod = javascriptMethodEntry.getValue();
            JavascriptMethod bridgeJavascriptMethod = javascriptClassBuilder.runtimeMethods.get(methodKey);
            String methodName = javascriptMethod.javaMethod().getName();
            int typeParametersSize = javascriptMethod.typeParameters().size();

            if (typeParametersSize == 0
                && methodName.startsWith("get")
                && methodName.length() > 3
                && Character.isUpperCase(methodName.charAt(3))) {
                javascriptClassBuilder.getterMethodHandles.putIfAbsent(
                    Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4),
                    bridgeJavascriptMethod.methodHandle()
                );
            } else if (typeParametersSize == 0
                && methodName.startsWith("is")
                && methodName.length() > 2
                && Character.isUpperCase(methodName.charAt(2))) {
                    javascriptClassBuilder.getterMethodHandles.putIfAbsent(
                        Character.toLowerCase(methodName.charAt(2)) + methodName.substring(3),
                        bridgeJavascriptMethod.methodHandle()
                    );
                } else if (typeParametersSize == 1
                    && methodName.startsWith("set")
                    && methodName.length() > 3
                    && Character.isUpperCase(methodName.charAt(3))) {
                        javascriptClassBuilder.setterMethodHandles.putIfAbsent(
                            Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4),
                            bridgeJavascriptMethod.methodHandle()
                        );
                    }
        }

        for (JavascriptField javascriptField : javascriptClassBuilder.fields.values()) {
            javascriptClassBuilder.getterMethodHandles.put(
                javascriptField.javaField().getName().intern(),
                javascriptField.getterMethodHandle()
            );
            javascriptClassBuilder.setterMethodHandles.put(
                javascriptField.javaField().getName().intern(),
                javascriptField.setterMethodHandle()
            );
        }
    }
}
