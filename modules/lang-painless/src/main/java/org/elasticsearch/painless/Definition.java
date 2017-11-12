/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.painless;

import org.objectweb.asm.Type;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;

/**
 * The entire API for Painless.  Also used as a whitelist for checking for legal
 * methods and fields during at both compile-time and runtime.
 */
public final class Definition {

    private static final String[] DEFINITION_FILES = new String[] {
            "org.elasticsearch.txt",
            "java.lang.txt",
            "java.math.txt",
            "java.text.txt",
            "java.time.txt",
            "java.time.chrono.txt",
            "java.time.format.txt",
            "java.time.temporal.txt",
            "java.time.zone.txt",
            "java.util.txt",
            "java.util.function.txt",
            "java.util.regex.txt",
            "java.util.stream.txt",
            "joda.time.txt"
    };

    /**
     * Whitelist that is "built in" to Painless and required by all scripts.
     */
    public static final Definition DEFINITION = new Definition(
        Collections.singletonList(WhitelistLoader.loadFromResourceFiles(Definition.class, DEFINITION_FILES)));

    /** Marker class for def type to be used during type analysis. */
    public static final class def {
        private def() {

        }
    }

    public static class Method {
        public final String name;
        public final Struct owner;
        public final Class<?> augmentation;
        public final Class<?> rtn;
        public final List<Class<?>> arguments;
        public final org.objectweb.asm.commons.Method method;
        public final int modifiers;
        public final MethodHandle handle;

        public Method(String name, Struct owner, Class<?> augmentation, Class<?> rtn, List<Class<?>> arguments,
                      org.objectweb.asm.commons.Method method, int modifiers, MethodHandle handle) {
            this.name = name;
            this.augmentation = augmentation;
            this.owner = owner;
            this.rtn = rtn;
            this.arguments = Collections.unmodifiableList(arguments);
            this.method = method;
            this.modifiers = modifiers;
            this.handle = handle;
        }

        /**
         * Returns MethodType for this method.
         * <p>
         * This works even for user-defined Methods (where the MethodHandle is null).
         */
        public MethodType getMethodType() {
            // we have a methodhandle already (e.g. whitelisted class)
            // just return its type
            if (handle != null) {
                return handle.type();
            }
            // otherwise compute it
            final Class<?> params[];
            final Class<?> returnValue;
            if (augmentation != null) {
                // static method disguised as virtual/interface method
                params = new Class<?>[1 + arguments.size()];
                params[0] = augmentation;
                for (int i = 0; i < arguments.size(); i++) {
                    params[i + 1] = convertPainlessTypeToJavaType(arguments.get(i));
                }
                returnValue = convertPainlessTypeToJavaType(rtn);
            } else if (Modifier.isStatic(modifiers)) {
                // static method: straightforward copy
                params = new Class<?>[arguments.size()];
                for (int i = 0; i < arguments.size(); i++) {
                    params[i] = convertPainlessTypeToJavaType(arguments.get(i));
                }
                returnValue = convertPainlessTypeToJavaType(rtn);
            } else if ("<init>".equals(name)) {
                // constructor: returns the owner class
                params = new Class<?>[arguments.size()];
                for (int i = 0; i < arguments.size(); i++) {
                    params[i] = convertPainlessTypeToJavaType(arguments.get(i));
                }
                returnValue = owner.clazz;
            } else {
                // virtual/interface method: add receiver class
                params = new Class<?>[1 + arguments.size()];
                params[0] = owner.clazz;
                for (int i = 0; i < arguments.size(); i++) {
                    params[i + 1] = convertPainlessTypeToJavaType(arguments.get(i));
                }
                returnValue = convertPainlessTypeToJavaType(rtn);
            }
            return MethodType.methodType(returnValue, params);
        }

        public void write(MethodWriter writer) {
            final org.objectweb.asm.Type type;
            if (augmentation != null) {
                assert java.lang.reflect.Modifier.isStatic(modifiers);
                type = org.objectweb.asm.Type.getType(augmentation);
            } else {
                type = owner.type;
            }

            if (java.lang.reflect.Modifier.isStatic(modifiers)) {
                writer.invokeStatic(type, method);
            } else if (java.lang.reflect.Modifier.isInterface(owner.clazz.getModifiers())) {
                writer.invokeInterface(type, method);
            } else {
                writer.invokeVirtual(type, method);
            }
        }
    }

    public static final class Field {
        public final String name;
        public final Struct owner;
        public final Class<?> clazz;
        public final String javaName;
        public final int modifiers;
        private final MethodHandle getter;
        private final MethodHandle setter;

        private Field(String name, String javaName, Struct owner, Class<?> clazz, int modifiers, MethodHandle getter, MethodHandle setter) {
            this.name = name;
            this.javaName = javaName;
            this.owner = owner;
            this.clazz = clazz;
            this.modifiers = modifiers;
            this.getter = getter;
            this.setter = setter;
        }
    }

    // TODO: instead of hashing on this, we could have a 'next' pointer in Method itself, but it would make code more complex
    // please do *NOT* under any circumstances change this to be the crappy Tuple from elasticsearch!
    /**
     * Key for looking up a method.
     * <p>
     * Methods are keyed on both name and arity, and can be overloaded once per arity.
     * This allows signatures such as {@code String.indexOf(String) vs String.indexOf(String, int)}.
     * <p>
     * It is less flexible than full signature overloading where types can differ too, but
     * better than just the name, and overloading types adds complexity to users, too.
     */
    public static final class MethodKey {
        public final String name;
        public final int arity;

        /**
         * Create a new lookup key
         * @param name name of the method
         * @param arity number of parameters
         */
        public MethodKey(String name, int arity) {
            this.name = Objects.requireNonNull(name);
            this.arity = arity;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + arity;
            result = prime * result + name.hashCode();
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            MethodKey other = (MethodKey) obj;
            if (arity != other.arity) return false;
            if (!name.equals(other.name)) return false;
            return true;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(name);
            sb.append('/');
            sb.append(arity);
            return sb.toString();
        }
    }

    public static final class Struct {
        public final String name;
        public final Class<?> clazz;
        public final org.objectweb.asm.Type type;

        public final Map<MethodKey, Method> constructors;
        public final Map<MethodKey, Method> staticMethods;
        public final Map<MethodKey, Method> methods;

        public final Map<String, Field> staticMembers;
        public final Map<String, Field> members;

        public final Map<String, MethodHandle> getters;
        public final Map<String, MethodHandle> setters;

        public final Method functionalMethod;

        private Struct(String name, Class<?> clazz, org.objectweb.asm.Type type) {
            this.name = name;
            this.clazz = clazz;
            this.type = type;

            constructors = new HashMap<>();
            staticMethods = new HashMap<>();
            methods = new HashMap<>();

            staticMembers = new HashMap<>();
            members = new HashMap<>();

            getters = new HashMap<>();
            setters = new HashMap<>();

            functionalMethod = null;
        }

        private Struct(Struct struct, Method functionalMethod) {
            name = struct.name;
            clazz = struct.clazz;
            type = struct.type;

            constructors = Collections.unmodifiableMap(struct.constructors);
            staticMethods = Collections.unmodifiableMap(struct.staticMethods);
            methods = Collections.unmodifiableMap(struct.methods);

            staticMembers = Collections.unmodifiableMap(struct.staticMembers);
            members = Collections.unmodifiableMap(struct.members);

            getters = Collections.unmodifiableMap(struct.getters);
            setters = Collections.unmodifiableMap(struct.setters);

            this.functionalMethod = functionalMethod;
        }

        private Struct freeze(Method functionalMethod) {
            return new Struct(this, functionalMethod);
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }

            if (object == null || getClass() != object.getClass()) {
                return false;
            }

            Struct struct = (Struct)object;

            return name.equals(struct.name);
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }
    }

    public static class Cast {
        public static Cast standard(Class<?> from, Class<?> to, boolean explicit) {
            return new Cast(from, to, explicit, null, null, null, null);
        }

        public static Cast unboxFrom(Class<?> from, Class<?> to, boolean explicit, Class<?> unboxFrom) {
            return new Cast(from, to, explicit, unboxFrom, null, null, null);
        }

        public static Cast unboxTo(Class<?> from, Class<?> to, boolean explicit, Class<?> unboxTo) {
            return new Cast(from, to, explicit, null, unboxTo, null, null);
        }

        public static Cast boxFrom(Class<?> from, Class<?> to, boolean explicit, Class<?> boxFrom) {
            return new Cast(from, to, explicit, null, null, boxFrom, null);
        }

        public static Cast boxTo(Class<?> from, Class<?> to, boolean explicit, Class<?> boxTo) {
            return new Cast(from, to, explicit, null, null, null, boxTo);
        }

        public final Class<?> from;
        public final Class<?> to;
        public final boolean explicit;
        public final Class<?> unboxFrom;
        public final Class<?> unboxTo;
        public final Class<?> boxFrom;
        public final Class<?> boxTo;

        private Cast(Class<?> from, Class<?> to, boolean explicit, Class<?> unboxFrom, Class<?> unboxTo, Class<?> boxFrom, Class<?> boxTo) {
            this.from = from;
            this.to = to;
            this.explicit = explicit;
            this.unboxFrom = unboxFrom;
            this.unboxTo = unboxTo;
            this.boxFrom = boxFrom;
            this.boxTo = boxTo;
        }
    }

    public static Class<?> getBoxedType(Class<?> clazz) {
        if (clazz == boolean.class) {
            return Boolean.class;
        } else if (clazz == byte.class) {
            return Byte.class;
        } else if (clazz == short.class) {
            return Short.class;
        } else if (clazz == char.class) {
            return Character.class;
        } else if (clazz == int.class) {
            return Integer.class;
        } else if (clazz == long.class) {
            return Long.class;
        } else if (clazz == float.class) {
            return Float.class;
        } else if (clazz == double.class) {
            return Double.class;
        }

        return clazz;
    }

    public static Class<?> getUnboxedype(Class<?> clazz) {
        if (clazz == Boolean.class) {
            return boolean.class;
        } else if (clazz == Byte.class) {
            return byte.class;
        } else if (clazz == Short.class) {
            return short.class;
        } else if (clazz == Character.class) {
            return char.class;
        } else if (clazz == Integer.class) {
            return int.class;
        } else if (clazz == Long.class) {
            return long.class;
        } else if (clazz == Float.class) {
            return float.class;
        } else if (clazz == Double.class) {
            return double.class;
        }

        return clazz;
    }

    public static boolean isConstantType(Class<?> clazz) {
        return clazz == boolean.class ||
               clazz == byte.class    ||
               clazz == short.class   ||
               clazz == char.class    ||
               clazz == int.class     ||
               clazz == long.class    ||
               clazz == float.class   ||
               clazz == double.class  ||
               clazz == String.class;
    }

    public static Class<?> ObjectClassTodefClass(Class<?> clazz) {
        if (clazz.isArray()) {
            Class<?> component = clazz.getComponentType();
            int dimensions = 1;

            while (component.isArray()) {
                component = component.getComponentType();
                ++dimensions;
            }

            if (component == Object.class) {
                char[] braces = new char[dimensions];
                Arrays.fill(braces, '[');

                String descriptor = new String(braces) + org.objectweb.asm.Type.getType(def.class).getDescriptor();
                org.objectweb.asm.Type type = org.objectweb.asm.Type.getType(descriptor);

                try {
                    return Class.forName(type.getInternalName().replace('/', '.'));
                } catch (ClassNotFoundException exception) {
                    throw new IllegalStateException("internal error", exception);
                }
            }
        } else if (clazz == Object.class) {
            return def.class;
        }

        return clazz;
    }

    public static Class<?> convertPainlessTypeToJavaType(Class<?> clazz) {
        if (clazz.isArray()) {
            Class<?> component = clazz.getComponentType();
            int dimensions = 1;

            while (component.isArray()) {
                component = component.getComponentType();
                ++dimensions;
            }

            if (component == def.class) {
                char[] braces = new char[dimensions];
                Arrays.fill(braces, '[');

                String descriptor = new String(braces) + org.objectweb.asm.Type.getType(Object.class).getDescriptor();
                org.objectweb.asm.Type type = org.objectweb.asm.Type.getType(descriptor);

                try {
                    return Class.forName(type.getInternalName().replace('/', '.'));
                } catch (ClassNotFoundException exception) {
                    throw new IllegalStateException("internal error", exception);
                }
            }
        } else if (clazz == def.class) {
            return Object.class;
        }

        return clazz;
    }

    public static String ClassToName(Class<?> clazz) {
        if (clazz.isLocalClass() || clazz.isAnonymousClass()) {
            return null;
        } else if (clazz.isArray()) {
            Class<?> component = clazz.getComponentType();
            int dimensions = 1;

            while (component.isArray()) {
                component = component.getComponentType();
                ++dimensions;
            }

            if (component == def.class) {
                StringBuilder builder = new StringBuilder("def");

                for (int dimension = 0; dimension < dimensions; dimensions++) {
                    builder.append("[]");
                }

                return builder.toString();
            }
        } else if (clazz == def.class) {
            return "def";
        }

        return clazz.getCanonicalName();
    }

    private final Map<String, Class<?>> painlessStructNamesToJavaClasses;
    private final Map<Class<?>, Struct> javaClassesToPainlessStructs;

    public static Definition create(List<Whitelist> whitelists) {
        return new Definition(whitelists).freeze();
    }

    private Definition(List<Whitelist> whitelists) {
        painlessStructNamesToJavaClasses = new HashMap<>();
        javaClassesToPainlessStructs = new HashMap<>();

        // add the universal def type
        painlessStructNamesToJavaClasses.put("def", def.class);
        javaClassesToPainlessStructs.put(def.class, new Struct("def", Object.class, Type.getType(Object.class)));

        addFromWhitelists(whitelists);
        buildInheritance();

        // copy all structs to make them unmodifiable for outside users:
        for (Map.Entry<Class<?>,Struct> entry : javaClassesToPainlessStructs.entrySet()) {
            entry.setValue(entry.getValue().freeze(computeFunctionalInterfaceMethod(entry.getValue())));
        }

        /*voidType = getType("void");
        booleanType = getType("boolean");
        BooleanType = getType("Boolean");
        byteType = getType("byte");
        ByteType = getType("Byte");
        shortType = getType("short");
        ShortType = getType("Short");
        intType = getType("int");
        IntegerType = getType("Integer");
        longType = getType("long");
        LongType = getType("Long");
        floatType = getType("float");
        FloatType = getType("Float");
        doubleType = getType("double");
        DoubleType = getType("Double");
        charType = getType("char");
        CharacterType = getType("Character");
        ObjectType = getType("Object");
        DefType = getType("def");
        NumberType = getType("Number");
        StringType = getType("String");
        ExceptionType = getType("Exception");
        PatternType = getType("Pattern");
        MatcherType = getType("Matcher");
        IteratorType = getType("Iterator");
        ArrayListType = getType("ArrayList");
        HashMapType = getType("HashMap");*/
    }

    private Definition(Definition definition) {
        this.painlessStructNamesToJavaClasses = Collections.unmodifiableMap(definition.painlessStructNamesToJavaClasses);
        this.javaClassesToPainlessStructs = Collections.unmodifiableMap(definition.javaClassesToPainlessStructs);
    }

    private Definition freeze() {
        return new Definition(this);
    }

    private void addFromWhitelists(List<Whitelist> whitelists) {
        String origin = "[none]";

        try {
            // first iteration collects all the painless structs that
            // are necessary for validation during the second iteration
            for (Whitelist whitelist : whitelists) {
                for (Whitelist.Struct whitelistStruct : whitelist.whitelistStructs) {
                    origin = whitelistStruct.origin;
                    addStruct(whitelist.javaClassLoader, whitelistStruct);
                }
            }

            // second iteration adds all the constructors, methods, and fields that will
            // be available in Painless along with validating they exist and all their types have
            // been white-listed during the first iteration
            for (Whitelist whitelist : whitelists) {
                for (Whitelist.Struct whitelistStruct : whitelist.whitelistStructs) {
                    String painlessStructName = whitelistStruct.javaClassName.replace('$', '.');
                    Class<?> javaClass = painlessStructNamesToJavaClasses.get(painlessStructName);
                    Struct painlessStruct = javaClassesToPainlessStructs.get(javaClass);

                    for (Whitelist.Constructor whitelistConstructor : whitelistStruct.whitelistConstructors) {
                        origin = whitelistConstructor.origin;
                        addConstructor(whitelistConstructor, javaClass, painlessStruct);
                    }

                    for (Whitelist.Method whitelistMethod : whitelistStruct.whitelistMethods) {
                        origin = whitelistMethod.origin;
                        addMethod(whitelist.javaClassLoader, whitelistMethod, javaClass, painlessStruct);
                    }

                    for (Whitelist.Field whitelistField : whitelistStruct.whitelistFields) {
                        origin = whitelistField.origin;
                        addField(whitelistField, javaClass, painlessStruct);
                    }
                }
            }
        } catch (Exception exception) {
            throw new IllegalArgumentException("error loading painless whitelist " + origin, exception);
        }
    }

    private void addStruct(ClassLoader whitelistClassLoader, Whitelist.Struct whitelistStruct) {
        Class<?> newJavaClass;

        if      (void   .class.getName().equals(whitelistStruct.javaClassName)) newJavaClass = void.class   ;
        else if (boolean.class.getName().equals(whitelistStruct.javaClassName)) newJavaClass = boolean.class;
        else if (byte   .class.getName().equals(whitelistStruct.javaClassName)) newJavaClass = byte.class   ;
        else if (short  .class.getName().equals(whitelistStruct.javaClassName)) newJavaClass = short.class  ;
        else if (char   .class.getName().equals(whitelistStruct.javaClassName)) newJavaClass = char.class   ;
        else if (int    .class.getName().equals(whitelistStruct.javaClassName)) newJavaClass = int.class    ;
        else if (long   .class.getName().equals(whitelistStruct.javaClassName)) newJavaClass = long.class   ;
        else if (float  .class.getName().equals(whitelistStruct.javaClassName)) newJavaClass = float.class  ;
        else if (double .class.getName().equals(whitelistStruct.javaClassName)) newJavaClass = double.class ;
        else {
            try {
                newJavaClass = Class.forName(whitelistStruct.javaClassName, true, whitelistClassLoader);
            } catch (ClassNotFoundException cnfe) {
                throw new IllegalArgumentException("invalid java class name [" + whitelistStruct.javaClassName + "]", cnfe);
            }
        }

        String fullPainlessStructName = newJavaClass.getCanonicalName();

        if (fullPainlessStructName.matches("^[_a-zA-Z][._a-zA-Z0-9]*") == false) {
            throw new IllegalArgumentException(
                    "invalid painless struct name [" + fullPainlessStructName + "]; must match [_a-zA-Z][._a-zA-Z0-9]*");
        }

        Struct existingPainlessStruct = javaClassesToPainlessStructs.get(newJavaClass);

        if (existingPainlessStruct == null) {
            Struct newPainlessStruct = new Struct(fullPainlessStructName, newJavaClass, Type.getType(newJavaClass));
            painlessStructNamesToJavaClasses.put(fullPainlessStructName, newJavaClass);
            javaClassesToPainlessStructs.put(newJavaClass, newPainlessStruct);
        }

        if (whitelistStruct.importJavaClassName) {
            String simplePainlessStructName = newJavaClass.getName();
            int index = simplePainlessStructName.lastIndexOf('.');

            if (index != -1) {
                simplePainlessStructName = simplePainlessStructName.substring(index + 1);
            }

            simplePainlessStructName = simplePainlessStructName.replace('$', '.');

            Class<?> existingJavaClass = painlessStructNamesToJavaClasses.get(simplePainlessStructName);

            if (existingJavaClass == null) {
                painlessStructNamesToJavaClasses.put(simplePainlessStructName, newJavaClass);
            } else if (newJavaClass != existingJavaClass) {
                throw new IllegalArgumentException("simple painless struct name [" + simplePainlessStructName + "] defined " +
                    "for multiple java classes [" + newJavaClass.getName() + "] and [" + existingJavaClass.getName() + "]; " +
                    "at least one whitelisted painless class must require the fully qualified name parameter [only_fqn]");
            }
        }
    }

    private void addConstructor(Whitelist.Constructor whitelistConstructor, Class<?> javaClass, Struct painlessStruct) {
        int parametersLength = whitelistConstructor.painlessParameterTypeNames.size();
        List<Class<?>> painlessTypeParameters = new ArrayList<>(parametersLength);
        Class<?>[] javaTypeParameters = new Class<?>[parametersLength];

        for (int parametersCount = 0; parametersCount < parametersLength; ++parametersCount) {
            String painlessTypeName = whitelistConstructor.painlessParameterTypeNames.get(parametersCount);

            try {
                Class<?> painlessType = getPainlessType(painlessTypeName);
                Class<?> javaType = convertPainlessTypeToJavaType(painlessType);

                painlessTypeParameters.add(painlessType);
                javaTypeParameters[parametersCount] = javaType;
            } catch (IllegalArgumentException iae) {
                throw new IllegalArgumentException("invalid painless type name [" + painlessTypeName + "] " +
                        "for painless constructor with painless struct [" + javaClass.getCanonicalName() + "] " +
                        "and painless type parameters " + whitelistConstructor.painlessParameterTypeNames, iae);
            }
        }

        java.lang.reflect.Constructor<?> javaConstructor;

        try {
            javaConstructor = javaClass.getConstructor(javaTypeParameters);
        } catch (NoSuchMethodException nsme) {
            throw new IllegalArgumentException("java constructor not defined for painless struct [" + javaClass.getCanonicalName() +
                    "] with java type parameters " + Arrays.asList(javaTypeParameters), nsme);
        }

        MethodKey painlessMethodKey = new MethodKey("<init>", parametersLength);
        Method painlessConstructor = painlessStruct.constructors.get(painlessMethodKey);

        if (painlessConstructor == null) {
            org.objectweb.asm.commons.Method asmConstructor = org.objectweb.asm.commons.Method.getMethod(javaConstructor);
            MethodHandle javaHandle;

            try {
                javaHandle = MethodHandles.publicLookup().in(javaClass).unreflectConstructor(javaConstructor);
            } catch (IllegalAccessException iae) {
                throw new IllegalArgumentException("java constructor handle not defined for painless struct [" +
                    javaClass.getCanonicalName() + "] with java type parameters " + Arrays.asList(javaTypeParameters), iae);
            }

            painlessConstructor = new Method("<init>", painlessStruct, null, void.class, painlessTypeParameters,
                asmConstructor, javaConstructor.getModifiers(), javaHandle);
            painlessStruct.constructors.put(painlessMethodKey, painlessConstructor);
        } else if (painlessConstructor.arguments.equals(painlessTypeParameters) == false){
            throw new IllegalArgumentException("duplicate painless constructors [" + painlessMethodKey + "]" +
                    "defined for painless struct [" + javaClass.getCanonicalName() + "] " +
                    "with painless type parameters " + painlessTypeParameters + " and " + painlessConstructor.arguments);
        }
    }

    private void addMethod(ClassLoader whitelistClassLoader, Whitelist.Method whitelistMethod, Class<?> javaClass, Struct painlessStruct) {
        String painlessMethodName = whitelistMethod.javaMethodName;

        if (painlessMethodName.matches("^[_a-zA-Z][_a-zA-Z0-9]*$") == false) {
            throw new IllegalArgumentException("invalid painless method name [" + painlessMethodName + "] " +
                    "for painless struct [" + javaClass.getCanonicalName() + "]; must match [_a-zA-Z][_a-zA-Z0-9]*");
        }

        Class<?> augmentedJavaClass = null;

        if (whitelistMethod.javaAugmentedClassName != null) {
            try {
                augmentedJavaClass = Class.forName(whitelistMethod.javaAugmentedClassName, true, whitelistClassLoader);
            } catch (ClassNotFoundException cnfe) {
                throw new IllegalArgumentException("invalid java class name [" + whitelistMethod.javaAugmentedClassName + "] " +
                        "for augmented painless method [" + painlessMethodName + "] " +
                        "with painless struct [" + javaClass.getCanonicalName() + "] " +
                        "and painless type parameters " + whitelistMethod.painlessParameterTypeNames, cnfe);
            }
        }

        int augmentedOffset = augmentedJavaClass == null ? 0 : 1;
        int parametersLength = whitelistMethod.painlessParameterTypeNames.size();
        List<Class<?>> painlessTypeParameters = new ArrayList<>(parametersLength);
        Class<?>[] javaTypeParameters = new Class<?>[augmentedOffset + parametersLength];

        if (augmentedJavaClass != null) {
            javaTypeParameters[0] = javaClass;
        }

        for (int parametersCount = 0; parametersCount < parametersLength; ++parametersCount) {
            String painlessTypeName = whitelistMethod.painlessParameterTypeNames.get(parametersCount);

            try {
                Class<?> painlessType = getPainlessType(painlessTypeName);
                Class<?> javaType = convertPainlessTypeToJavaType(painlessType);

                painlessTypeParameters.add(painlessType);
                javaTypeParameters[augmentedOffset + parametersCount] = javaType;
            } catch (IllegalArgumentException iae) {
                throw new IllegalArgumentException("invalid painless type name [" + painlessTypeName + "] " +
                        "for painless method [" + painlessMethodName + "] " +
                        "with painless struct [" + javaClass.getCanonicalName() + "] " +
                        "and painless type parameters " + whitelistMethod.painlessParameterTypeNames, iae);
            }
        }

        Class<?> javaClassImpl = augmentedJavaClass == null ? javaClass : augmentedJavaClass;
        java.lang.reflect.Method javaMethod;

        try {
            javaMethod = javaClassImpl.getMethod(painlessMethodName, javaTypeParameters);
        } catch (NoSuchMethodException nsme) {
            throw new IllegalArgumentException("java method [" + painlessMethodName + "] " +
                    "not defined for painless struct [" + javaClass.getCanonicalName() + "] " +
                    "with java type parameters " + Arrays.asList(javaTypeParameters), nsme);
        }

        Class<?> returnPainlessType;

        try {
            returnPainlessType = getPainlessType(whitelistMethod.painlessReturnTypeName);
        } catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException("invalid painless type name [" + whitelistMethod.painlessReturnTypeName + "] " +
                    "for painless method [" + painlessMethodName + "] " +
                    "with painless struct [" + javaClass.getCanonicalName() + "] " +
                    "and painless type parameters " + whitelistMethod.painlessParameterTypeNames, iae);
        }

        if (javaMethod.getReturnType() != convertPainlessTypeToJavaType(returnPainlessType)) {
            throw new IllegalArgumentException("return painless type [" + returnPainlessType.getCanonicalName() + "] " +
                    "does not match the return java type [" + javaMethod.getReturnType().getCanonicalName() + "] " +
                    "for painless method [" + painlessMethodName + "] " +
                    "with painless struct [" + javaClass.getCanonicalName() + "] " +
                    "and painless type parameters " + whitelistMethod.painlessParameterTypeNames);
        }

        MethodKey painlessMethodKey = new MethodKey(painlessMethodName, whitelistMethod.painlessParameterTypeNames.size());
        Map<MethodKey, Method> painlessMethods =
                augmentedJavaClass == null && Modifier.isStatic(javaMethod.getModifiers()) ?
                painlessStruct.staticMethods : painlessStruct.methods;

        Method painlessMethod = painlessMethods.get(painlessMethodKey);

        if (painlessMethod == null) {
            org.objectweb.asm.commons.Method asmMethod = org.objectweb.asm.commons.Method.getMethod(javaMethod);
            MethodHandle javaMethodHandle;

            try {
                javaMethodHandle = MethodHandles.publicLookup().in(javaClassImpl).unreflect(javaMethod);
            } catch (IllegalAccessException exception) {
                throw new IllegalArgumentException("java handle not found for painless method [" + painlessMethodName + "]" +
                        "with painless struct [" + javaClass.getCanonicalName() + "] " +
                        "and painless type parameters " + whitelistMethod.painlessParameterTypeNames);
            }

            painlessMethod = new Method(painlessMethodName, painlessStruct, augmentedJavaClass, returnPainlessType,
                painlessTypeParameters, asmMethod, javaMethod.getModifiers(), javaMethodHandle);
            painlessMethods.put(painlessMethodKey, painlessMethod);

            if (parametersLength == 0 && painlessMethodName.startsWith("get") && painlessMethodName.length() > 3 &&
                    Character.isUpperCase(painlessMethodName.charAt(3)) && returnPainlessType != void.class) {
                String shortcutPainlessMethodName = Character.toLowerCase(painlessMethodName.charAt(3)) + painlessMethodName.substring(4);
                painlessStruct.getters.put(shortcutPainlessMethodName, javaMethodHandle);
            } else if (parametersLength == 0 && painlessMethodName.startsWith("is") && painlessMethodName.length() > 2 &&
                    Character.isUpperCase(painlessMethodName.charAt(2)) && returnPainlessType != void.class) {
                String shortcutPainlessMethodName = Character.toLowerCase(painlessMethodName.charAt(2)) + painlessMethodName.substring(3);
                painlessStruct.getters.putIfAbsent(shortcutPainlessMethodName, javaMethodHandle);
            } else if (parametersLength == 1 && painlessMethodName.startsWith("set") && painlessMethodName.length() > 3 &&
                Character.isUpperCase(painlessMethodName.charAt(3)) && returnPainlessType == void.class) {
                String shortcutPainlessMethodName = Character.toLowerCase(painlessMethodName.charAt(3)) + painlessMethodName.substring(4);
                painlessStruct.setters.put(shortcutPainlessMethodName, javaMethodHandle);
            }
        } else if ((painlessMethod.rtn == returnPainlessType && painlessMethod.arguments.equals(painlessTypeParameters)) == false) {
            throw new IllegalArgumentException("duplicate painless methods [" + painlessMethodKey + "]" +
                "defined for painless struct [" + javaClass.getCanonicalName() + "] " +
                "with painless type parameters " + painlessTypeParameters + " and " + painlessMethod.arguments);
        }
    }

    private void addField(Whitelist.Field whitelistField, Class<?> javaClass, Struct painlessStruct) {
        String painlessFieldName = whitelistField.javaFieldName;

        if (painlessFieldName.matches("^[_a-zA-Z][_a-zA-Z0-9]*$") == false) {
            throw new IllegalArgumentException("invalid painless field name [" + painlessFieldName + "] " +
                    "for painless struct [" + javaClass.getCanonicalName() + "]; must match [_a-zA-Z][_a-zA-Z0-9]*");
        }

        Class<?> fieldPainlessType;

        try {
            fieldPainlessType = getPainlessType(whitelistField.painlessFieldTypeName);
        } catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException("invalid painless type name [" + whitelistField.painlessFieldTypeName + "] " +
                    "for painless field [" + painlessFieldName + "] " +
                    "with painless struct [" + javaClass.getCanonicalName() + "] ", iae);
        }

        java.lang.reflect.Field javaField;

        try {
            javaField = javaClass.getField(painlessFieldName);
        } catch (NoSuchFieldException nsme) {
            throw new IllegalArgumentException("java field [" + painlessFieldName + "] " +
                "not defined for painless struct [" + javaClass.getCanonicalName() + "] " +
                "with java type [" + convertPainlessTypeToJavaType(fieldPainlessType) +"]", nsme);
        }

        MethodHandle javaMethodHandleGetter = null;
        MethodHandle javaMethodHandleSetter = null;

        if (Modifier.isStatic(javaField.getModifiers()) == false) {
            try {
                javaMethodHandleGetter = MethodHandles.publicLookup().unreflectGetter(javaField);
                javaMethodHandleSetter = MethodHandles.publicLookup().unreflectSetter(javaField);

                painlessStruct.getters.put(painlessFieldName, javaMethodHandleGetter);
                painlessStruct.setters.put(painlessFieldName, javaMethodHandleSetter);
            } catch (IllegalAccessException exception) {
                throw new IllegalArgumentException("java handles [getter] and [setter] not defined " +
                        "for painless field [" + painlessFieldName + "]" +
                        "with painless struct [" + javaClass.getCanonicalName() + "]" +
                        "and painless type [" + whitelistField.painlessFieldTypeName + "]");
            }
        } else if (Modifier.isFinal(javaField.getModifiers()) == false) {
            throw new IllegalArgumentException("static painless field [" + painlessFieldName + "] must be final " +
                "with painless struct [" + javaClass.getCanonicalName() + "]" +
                "and painless type [" + whitelistField.painlessFieldTypeName + "]");
        }

        Map<String, Field> painlessFields =
            Modifier.isStatic(javaField.getModifiers()) ? painlessStruct.staticMembers : painlessStruct.members;
        Field painlessField = painlessFields.get(painlessFieldName);

        if (painlessField == null) {
            painlessField = new Field(painlessFieldName, javaField.getName(),
                painlessStruct, fieldPainlessType, javaField.getModifiers(), javaMethodHandleGetter, javaMethodHandleSetter);
            painlessFields.put(painlessFieldName, painlessField);
        } else if (painlessField.clazz != fieldPainlessType) {
            throw new IllegalArgumentException("duplicate painless fields [" + painlessFieldName + "]" +
                "defined for painless struct [" + javaClass.getCanonicalName() + "] " +
                "with painless types [" + fieldPainlessType.getCanonicalName() + " and " + painlessField.clazz.getCanonicalName());
        }
    }

    private void buildInheritance() {
        Set<Class<?>> javaClasses = new HashSet<>(javaClassesToPainlessStructs.keySet());
        javaClasses.removeIf(javaClass -> javaClass == def.class || javaClass.isPrimitive());

        List<Class<?>> javaClassesHierarchy = new ArrayList<>();

        for (Class<?> newJavaClass : javaClasses) {
            if (newJavaClass.isInterface()) {
                int javaClassIndex = 0;

                for (Class<?> existingJavaClass : javaClassesHierarchy) {
                    if (newJavaClass.isAssignableFrom(existingJavaClass)) {
                        break;
                    }

                    ++javaClassIndex;
                }

                javaClassesHierarchy.add(javaClassIndex, newJavaClass);
            }
        }

        for (Class<?> newJavaClass : javaClasses) {
            if (newJavaClass.isInterface() == false) {
                int javaClassIndex = 0;

                for (Class<?> existingJavaClass : javaClassesHierarchy) {
                    if (newJavaClass.isAssignableFrom(existingJavaClass)) {
                        break;
                    }

                    ++javaClassIndex;
                }

                javaClassesHierarchy.add(javaClassIndex, newJavaClass);
            }
        }

        for (int copyToIndex = javaClassesHierarchy.size() - 1; copyToIndex >= 0; --copyToIndex) {
            Class<?> javaClassCopyTo = javaClassesHierarchy.get(copyToIndex);
            Struct painlessStructCopyTo = javaClassesToPainlessStructs.get(javaClassCopyTo);

            for (int copyFromIndex = copyToIndex - 1; copyFromIndex >= 0; --copyFromIndex) {
                Class<?> javaClassCopyFrom = javaClassesHierarchy.get(copyFromIndex);

                if (javaClassCopyFrom.isAssignableFrom(javaClassCopyTo)) {
                    Struct painlessStructCopyFrom = javaClassesToPainlessStructs.get(javaClassCopyFrom);

                    for (Map.Entry<MethodKey, Method> painlessMethodEntry : painlessStructCopyFrom.methods.entrySet()) {
                        if (painlessStructCopyTo.methods.containsKey(painlessMethodEntry.getKey()) == false) {
                            painlessStructCopyTo.methods.put(painlessMethodEntry.getKey(), painlessMethodEntry.getValue());
                        } else {
                            throw new IllegalStateException("WTF method");
                        }
                    }

                    for (Map.Entry<String, Field> painlessFieldEntry : painlessStructCopyFrom.members.entrySet()) {
                        if (painlessStructCopyTo.members.containsKey(painlessFieldEntry.getKey()) == false) {
                            painlessStructCopyTo.members.put(painlessFieldEntry.getKey(), painlessFieldEntry.getValue());
                        } else {
                            throw new IllegalStateException("WTF member");
                        }
                    }

                    for (Map.Entry<String, MethodHandle> painlessGetterEntry : painlessStructCopyFrom.getters.entrySet()) {
                        if (painlessStructCopyTo.getters.containsKey(painlessGetterEntry.getKey()) == false) {
                            painlessStructCopyTo.getters.put(painlessGetterEntry.getKey(), painlessGetterEntry.getValue());
                        } else {
                            throw new IllegalStateException("WTF getter");
                        }
                    }

                    for (Map.Entry<String, MethodHandle> painlessSetterEntry : painlessStructCopyFrom.setters.entrySet()) {
                        if (painlessStructCopyTo.setters.containsKey(painlessSetterEntry.getKey()) == false) {
                            painlessStructCopyTo.setters.put(painlessSetterEntry.getKey(), painlessSetterEntry.getValue());
                        } else {
                            throw new IllegalStateException("WTF setter");
                        }
                    }
                }
            }
        }
    }

    /** computes the functional interface method for a class, or returns null */
    private Method computeFunctionalInterfaceMethod(Struct clazz) {
        if (!clazz.clazz.isInterface()) {
            return null;
        }
        // if its marked with this annotation, we fail if the conditions don't hold (means whitelist bug)
        // otherwise, this annotation is pretty useless.
        boolean hasAnnotation = clazz.clazz.isAnnotationPresent(FunctionalInterface.class);
        List<java.lang.reflect.Method> methods = new ArrayList<>();
        for (java.lang.reflect.Method m : clazz.clazz.getMethods()) {
            // default interface methods don't count
            if (m.isDefault()) {
                continue;
            }
            // static methods don't count
            if (Modifier.isStatic(m.getModifiers())) {
                continue;
            }
            // if its from Object, it doesn't count
            try {
                Object.class.getMethod(m.getName(), m.getParameterTypes());
                continue;
            } catch (ReflectiveOperationException e) {
                // it counts
            }
            methods.add(m);
        }
        if (methods.size() != 1) {
            if (hasAnnotation) {
                throw new IllegalArgumentException("Class: " + clazz.name +
                    " is marked with FunctionalInterface but doesn't fit the bill: " + methods);
            }
            return null;
        }
        // inspect the one method found from the reflection API, it should match the whitelist!
        java.lang.reflect.Method oneMethod = methods.get(0);
        Method painless = clazz.methods.get(new Definition.MethodKey(oneMethod.getName(), oneMethod.getParameterCount()));
        if (painless == null || painless.method.equals(org.objectweb.asm.commons.Method.getMethod(oneMethod)) == false) {
            throw new IllegalArgumentException("Class: " + clazz.name + " is functional but the functional " +
                "method is not whitelisted!");
        }
        return painless;
    }

    public boolean isPainlessStruct(String painlessTypeName) {
        return painlessStructNamesToJavaClasses.containsKey(painlessTypeName);
    }

    public Struct getPainlessStruct(Class<?> javaClass) {
        return javaClassesToPainlessStructs.get(javaClass);
    }

    public Class<?> getPainlessType(String painlessTypeName) {
        Class<?> painlessType = painlessStructNamesToJavaClasses.get(painlessTypeName);

        if (painlessType != null) {
            return painlessType;
        }

        int arrayDimensions = 0;
        int arrayIndex = painlessTypeName.indexOf('[');

        if (arrayIndex != -1) {
            int length = painlessTypeName.length();

            while (arrayIndex < length) {
                if (painlessTypeName.charAt(arrayIndex) == '[' && ++arrayIndex < length && painlessTypeName.charAt(arrayIndex++) == ']') {
                    ++arrayDimensions;
                } else {
                    throw new IllegalArgumentException("invalid painless type name [" + painlessTypeName + "].");
                }
            }

            painlessTypeName = painlessTypeName.substring(0, painlessTypeName.indexOf('['));
            painlessType = painlessStructNamesToJavaClasses.get(painlessTypeName);

            char braces[] = new char[arrayDimensions];
            Arrays.fill(braces, '[');
            String descriptor = new String(braces);

            if (painlessType == boolean.class)     descriptor += "Z";
            else if (painlessType == byte.class)   descriptor += "B";
            else if (painlessType == short.class)  descriptor += "S";
            else if (painlessType == char.class)   descriptor += "C";
            else if (painlessType == int.class)    descriptor += "I";
            else if (painlessType == long.class)   descriptor += "J";
            else if (painlessType == float.class)  descriptor += "F";
            else if (painlessType == double.class) descriptor += "D";
            else {
                descriptor += "L" + painlessType.getName() + ";";
            }

            try {
                return Class.forName(descriptor);
            } catch (ClassNotFoundException cnfe) {
                throw new IllegalStateException("invalid painless type name [" + painlessTypeName + "]", cnfe);
            }
        }

        throw new IllegalArgumentException("invalid painless type name [" + painlessTypeName + "]");
    }
}
