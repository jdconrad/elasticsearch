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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
                    params[i + 1] = defClassToObjectClass(arguments.get(i));
                }
                returnValue = defClassToObjectClass(rtn);
            } else if (Modifier.isStatic(modifiers)) {
                // static method: straightforward copy
                params = new Class<?>[arguments.size()];
                for (int i = 0; i < arguments.size(); i++) {
                    params[i] = defClassToObjectClass(arguments.get(i));
                }
                returnValue = defClassToObjectClass(rtn);
            } else if ("<init>".equals(name)) {
                // constructor: returns the owner class
                params = new Class<?>[arguments.size()];
                for (int i = 0; i < arguments.size(); i++) {
                    params[i] = defClassToObjectClass(arguments.get(i));
                }
                returnValue = owner.clazz;
            } else {
                // virtual/interface method: add receiver class
                params = new Class<?>[1 + arguments.size()];
                params[0] = owner.clazz;
                for (int i = 0; i < arguments.size(); i++) {
                    params[i + 1] = defClassToObjectClass(arguments.get(i));
                }
                returnValue = defClassToObjectClass(rtn);
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

    public static Class<?> defClassToObjectClass(Class<?> clazz) {
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

        return clazz.getCanonicalName().replace('$', '.');
    }

    private final Map<String, Class<?>> painlessTypesToJavaClasses;
    private final Map<Class<?>, Struct> javaClassesToPainlessStructs;

    private Definition(List<Whitelist> whitelists) {
        painlessTypesToJavaClasses = new HashMap<>();
        javaClassesToPainlessStructs = new HashMap<>();

        String origin = null;

        // add the universal def type
        painlessTypesToJavaClasses.put("def", def.class);
        javaClassesToPainlessStructs.put(def.class, new Struct("def", Object.class, Type.getType(Object.class)));

        try {
            // first iteration collects all the Painless type names that
            // are used for validation during the second iteration
            for (Whitelist whitelist : whitelists) {
                for (Whitelist.Struct whitelistStruct : whitelist.whitelistStructs) {
                    String painlessTypeName = whitelistStruct.javaClassName.replace('$', '.');
                    Struct painlessStruct = javaClassesToPainlessStructs.get(painlessTypesToJavaClasses.get(painlessTypeName));

                    if (painlessStruct != null && painlessStruct.clazz.getName().equals(whitelistStruct.javaClassName) == false) {
                        throw new IllegalArgumentException("struct [" + painlessStruct.name + "] cannot represent multiple classes " +
                            "[" + painlessStruct.clazz.getName() + "] and [" + whitelistStruct.javaClassName + "]");
                    }

                    origin = whitelistStruct.origin;
                    addStruct(whitelist.javaClassLoader, whitelistStruct);

                    painlessStruct = javaClassesToPainlessStructs.get(painlessTypesToJavaClasses.get(painlessTypeName));
                    javaClassesToPainlessStructs.put(painlessStruct.clazz, painlessStruct);
                }
            }

            // second iteration adds all the constructors, methods, and fields that will
            // be available in Painless along with validating they exist and all their types have
            // been white-listed during the first iteration
            for (Whitelist whitelist : whitelists) {
                for (Whitelist.Struct whitelistStruct : whitelist.whitelistStructs) {
                    String painlessTypeName = whitelistStruct.javaClassName.replace('$', '.');

                    for (Whitelist.Constructor whitelistConstructor : whitelistStruct.whitelistConstructors) {
                        origin = whitelistConstructor.origin;
                        addConstructor(painlessTypeName, whitelistConstructor);
                    }

                    for (Whitelist.Method whitelistMethod : whitelistStruct.whitelistMethods) {
                        origin = whitelistMethod.origin;
                        addMethod(whitelist.javaClassLoader, painlessTypeName, whitelistMethod);
                    }

                    for (Whitelist.Field whitelistField : whitelistStruct.whitelistFields) {
                        origin = whitelistField.origin;
                        addField(painlessTypeName, whitelistField);
                    }
                }
            }
        } catch (Exception exception) {
            throw new IllegalArgumentException("error loading whitelist(s) " + origin, exception);
        }

        // goes through each Painless struct and determines the inheritance list,
        // and then adds all inherited types to the Painless struct's whitelist
        for (Class<?> javaClass : javaClassesToPainlessStructs.keySet()) {
            Struct painlessStruct = javaClassesToPainlessStructs.get(javaClass);

            List<String> painlessSuperStructs = new ArrayList<>();
            Class<?> javaSuperClass = painlessStruct.clazz.getSuperclass();

            Stack<Class<?>> javaInteraceLookups = new Stack<>();
            javaInteraceLookups.push(painlessStruct.clazz);

            // adds super classes to the inheritance list
            if (javaSuperClass != null && javaSuperClass.isInterface() == false) {
                while (javaSuperClass != null) {
                    Struct painlessSuperStruct = javaClassesToPainlessStructs.get(javaSuperClass);

                    if (painlessSuperStruct != null) {
                        painlessSuperStructs.add(painlessSuperStruct.name);
                    }

                    javaInteraceLookups.push(javaSuperClass);
                    javaSuperClass = javaSuperClass.getSuperclass();
                }
            }

            // adds all super interfaces to the inheritance list
            while (javaInteraceLookups.isEmpty() == false) {
                Class<?> javaInterfaceLookup = javaInteraceLookups.pop();

                for (Class<?> javaSuperInterface : javaInterfaceLookup.getInterfaces()) {
                    Struct painlessInterfaceStruct = javaClassesToPainlessStructs.get(javaSuperInterface);

                    if (painlessInterfaceStruct != null) {
                        String painlessInterfaceStructName = painlessInterfaceStruct.name;

                        if (painlessSuperStructs.contains(painlessInterfaceStructName) == false) {
                            painlessSuperStructs.add(painlessInterfaceStructName);
                        }

                        for (Class<?> javaPushInterface : javaInterfaceLookup.getInterfaces()) {
                            javaInteraceLookups.push(javaPushInterface);
                        }
                    }
                }
            }

            // copies methods and fields from super structs to the parent struct
            copyStruct(painlessStruct.name, painlessSuperStructs);

            // copies methods and fields from Object into interface types
            if (painlessStruct.clazz.isInterface() || ("def").equals(painlessStruct.name)) {
                Struct painlessObjectStruct = javaClassesToPainlessStructs.get(Object.class);

                if (painlessObjectStruct != null) {
                    copyStruct(painlessStruct.name, Collections.singletonList(painlessObjectStruct.name));
                }
            }
        }

        // precompute runtime classes
        for (Struct painlessStruct : javaClassesToPainlessStructs.values()) {
            addRuntimeClass(painlessStruct);
        }

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

    private void addStruct(ClassLoader whitelistClassLoader, Whitelist.Struct whitelistStruct) {
        String painlessTypeName = whitelistStruct.javaClassName.replace('$', '.');
        String importedPainlessTypeName = painlessTypeName;

        if (painlessTypeName.matches("^[_a-zA-Z][._a-zA-Z0-9]*") == false) {
            throw new IllegalArgumentException("invalid struct type name [" + painlessTypeName + "]");
        }

        int index = whitelistStruct.javaClassName.lastIndexOf('.');

        if (index != -1) {
            importedPainlessTypeName = whitelistStruct.javaClassName.substring(index + 1).replace('$', '.');
        }

        Class<?> javaClass;

        if      ("void".equals(whitelistStruct.javaClassName))    javaClass = void.class;
        else if ("boolean".equals(whitelistStruct.javaClassName)) javaClass = boolean.class;
        else if ("byte".equals(whitelistStruct.javaClassName))    javaClass = byte.class;
        else if ("short".equals(whitelistStruct.javaClassName))   javaClass = short.class;
        else if ("char".equals(whitelistStruct.javaClassName))    javaClass = char.class;
        else if ("int".equals(whitelistStruct.javaClassName))     javaClass = int.class;
        else if ("long".equals(whitelistStruct.javaClassName))    javaClass = long.class;
        else if ("float".equals(whitelistStruct.javaClassName))   javaClass = float.class;
        else if ("double".equals(whitelistStruct.javaClassName))  javaClass = double.class;
        else {
            try {
                javaClass = Class.forName(whitelistStruct.javaClassName, true, whitelistClassLoader);
            } catch (ClassNotFoundException cnfe) {
                throw new IllegalArgumentException("invalid java class name [" + whitelistStruct.javaClassName + "]" +
                        " for struct [" + painlessTypeName + "]");
            }
        }

        Struct existingStruct = javaClassesToPainlessStructs.get(javaClass);

        if (existingStruct == null) {
            Struct struct = new Struct(painlessTypeName, javaClass, org.objectweb.asm.Type.getType(javaClass));
            painlessTypesToJavaClasses.put(painlessTypeName, javaClass);
            javaClassesToPainlessStructs.put(javaClass, struct);
        } else if (existingStruct.clazz.equals(javaClass) == false) {
            throw new IllegalArgumentException("struct [" + painlessTypeName + "] is used to " +
                    "illegally represent multiple java classes [" + whitelistStruct.javaClassName + "] and " +
                    "[" + existingStruct.clazz.getName() + "]");
        }

        if (whitelistStruct.importJavaClassName) {
            Class<?> existingClass = painlessTypesToJavaClasses.get(importedPainlessTypeName);

            if (existingStruct == null) {
                painlessTypesToJavaClasses.put(importedPainlessTypeName, javaClass);
            } else if (existingClass == javaClass) {
                throw new IllegalArgumentException("imported name [" + painlessTypeName + "] is used to " +
                    "illegally represent multiple java classes [" + whitelistStruct.javaClassName + "] " +
                    "and [" + existingStruct.clazz.getName() + "]");
            }
        }
    }

    private void addConstructor(String ownerStructName, Whitelist.Constructor whitelistConstructor) {
        Struct ownerStruct = javaClassesToPainlessStructs.get(painlessTypesToJavaClasses.get(ownerStructName));

        if (ownerStruct == null) {
            throw new IllegalArgumentException("owner struct [" + ownerStructName + "] not defined for constructor with " +
                    "parameters " + whitelistConstructor.painlessParameterTypeNames);
        }

        List<Class<?>> painlessParametersTypes = new ArrayList<>(whitelistConstructor.painlessParameterTypeNames.size());
        Class<?>[] javaClassParameters = new Class<?>[whitelistConstructor.painlessParameterTypeNames.size()];

        for (int parameterCount = 0; parameterCount < whitelistConstructor.painlessParameterTypeNames.size(); ++parameterCount) {
            String painlessParameterTypeName = whitelistConstructor.painlessParameterTypeNames.get(parameterCount);

            try {
                Class<?> painlessParameterClass = getJavaClassFromPainlessType(painlessParameterTypeName);

                painlessParametersTypes.add(painlessParameterClass);
                javaClassParameters[parameterCount] = defClassToObjectClass(painlessParameterClass);
            } catch (IllegalArgumentException iae) {
                throw new IllegalArgumentException("struct not defined for constructor parameter [" + painlessParameterTypeName + "] " +
                        "with owner struct [" + ownerStructName + "] and constructor parameters " +
                        whitelistConstructor.painlessParameterTypeNames, iae);
            }
        }

        java.lang.reflect.Constructor<?> javaConstructor;

        try {
            javaConstructor = ownerStruct.clazz.getConstructor(javaClassParameters);
        } catch (NoSuchMethodException exception) {
            throw new IllegalArgumentException("constructor not defined for owner struct [" + ownerStructName + "] " +
                    " with constructor parameters " + whitelistConstructor.painlessParameterTypeNames, exception);
        }

        MethodKey painlessMethodKey = new MethodKey("<init>", whitelistConstructor.painlessParameterTypeNames.size());
        Method painlessConstructor = ownerStruct.constructors.get(painlessMethodKey);

        if (painlessConstructor == null) {
            org.objectweb.asm.commons.Method asmConstructor = org.objectweb.asm.commons.Method.getMethod(javaConstructor);
            MethodHandle javaHandle;

            try {
                javaHandle = MethodHandles.publicLookup().in(ownerStruct.clazz).unreflectConstructor(javaConstructor);
            } catch (IllegalAccessException exception) {
                throw new IllegalArgumentException("constructor not defined for owner struct [" + ownerStructName + "] " +
                        " with constructor parameters " + whitelistConstructor.painlessParameterTypeNames);
            }

            painlessConstructor = new Method("<init>", ownerStruct, null, void.class, painlessParametersTypes,
                asmConstructor, javaConstructor.getModifiers(), javaHandle);
            ownerStruct.constructors.put(painlessMethodKey, painlessConstructor);
        } else if (painlessConstructor.equals(painlessParametersTypes) == false){
            throw new IllegalArgumentException(
                    "illegal duplicate constructors [" + painlessMethodKey + "] found within the struct [" + ownerStruct.name + "] " +
                    "with parameters " + painlessParametersTypes + " and " + painlessConstructor.arguments);
        }
    }

    private void addMethod(ClassLoader whitelistClassLoader, String ownerStructName, Whitelist.Method whitelistMethod) {
        Struct ownerStruct = javaClassesToPainlessStructs.get(painlessTypesToJavaClasses.get(ownerStructName));

        if (ownerStruct == null) {
            throw new IllegalArgumentException("owner struct [" + ownerStructName + "] not defined for method with " +
                    "name [" + whitelistMethod.javaMethodName + "] and parameters " + whitelistMethod.painlessParameterTypeNames);
        }

        if (!whitelistMethod.javaMethodName.matches("^[_a-zA-Z][_a-zA-Z0-9]*$")) {
            throw new IllegalArgumentException("invalid method name" +
                    " [" + whitelistMethod.javaMethodName + "] for owner struct [" + ownerStructName + "].");
        }

        Class<?> javaAugmentedClass = null;

        if (whitelistMethod.javaAugmentedClassName != null) {
            try {
                javaAugmentedClass = Class.forName(whitelistMethod.javaAugmentedClassName, true, whitelistClassLoader);
            } catch (ClassNotFoundException cnfe) {
                throw new IllegalArgumentException("augmented class [" + whitelistMethod.javaAugmentedClassName + "] " +
                        "not found for method with name [" + whitelistMethod.javaMethodName + "] " +
                        "and parameters " + whitelistMethod.painlessParameterTypeNames, cnfe);
            }
        }

        int augmentedOffset = javaAugmentedClass == null ? 0 : 1;

        List<Class<?>> painlessParametersTypes = new ArrayList<>(whitelistMethod.painlessParameterTypeNames.size());
        Class<?>[] javaClassParameters = new Class<?>[whitelistMethod.painlessParameterTypeNames.size() + augmentedOffset];

        if (javaAugmentedClass != null) {
            javaClassParameters[0] = ownerStruct.clazz;
        }

        for (int parameterCount = 0; parameterCount < whitelistMethod.painlessParameterTypeNames.size(); ++parameterCount) {
            String painlessParameterTypeName = whitelistMethod.painlessParameterTypeNames.get(parameterCount);

            try {
                Class<?> painlessParameterClass = getJavaClassFromPainlessType(painlessParameterTypeName);

                painlessParametersTypes.add(painlessParameterClass);
                javaClassParameters[parameterCount + augmentedOffset] = defClassToObjectClass(painlessParameterClass);
            } catch (IllegalArgumentException iae) {
                throw new IllegalArgumentException("struct not defined for method parameter [" + painlessParameterTypeName + "] " +
                        "with owner struct [" + ownerStructName + "] and method with name [" + whitelistMethod.javaMethodName + "] " +
                        "and parameters " + whitelistMethod.painlessParameterTypeNames, iae);
            }
        }

        Class<?> javaImplClass = javaAugmentedClass == null ? ownerStruct.clazz : javaAugmentedClass;
        java.lang.reflect.Method javaMethod;

        try {
            javaMethod = javaImplClass.getMethod(whitelistMethod.javaMethodName, javaClassParameters);
        } catch (NoSuchMethodException nsme) {
            throw new IllegalArgumentException("method with name [" + whitelistMethod.javaMethodName + "] " +
                    "and parameters " + whitelistMethod.painlessParameterTypeNames + " not found for class [" +
                    javaImplClass.getName() + "]", nsme);
        }

        Class<?> painlessReturnClass;

        try {
            painlessReturnClass = getJavaClassFromPainlessType(whitelistMethod.painlessReturnTypeName);
        } catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException("struct not defined for return type [" + whitelistMethod.painlessReturnTypeName + "] " +
                    "with owner struct [" + ownerStructName + "] and method with name [" + whitelistMethod.javaMethodName + "] " +
                    "and parameters " + whitelistMethod.painlessParameterTypeNames, iae);
        }

        if (javaMethod.getReturnType() != defClassToObjectClass(painlessReturnClass)) {
            throw new IllegalArgumentException("specified return type class [" + painlessReturnClass + "] " +
                    "does not match the return type class [" + javaMethod.getReturnType() + "] for the " +
                    "method with name [" + whitelistMethod.javaMethodName + "] " +
                    "and parameters " + whitelistMethod.painlessParameterTypeNames);
        }

        MethodKey painlessMethodKey = new MethodKey(whitelistMethod.javaMethodName, whitelistMethod.painlessParameterTypeNames.size());

        if (javaAugmentedClass == null && Modifier.isStatic(javaMethod.getModifiers())) {
            Method painlessMethod = ownerStruct.staticMethods.get(painlessMethodKey);

            if (painlessMethod == null) {
                org.objectweb.asm.commons.Method asmMethod = org.objectweb.asm.commons.Method.getMethod(javaMethod);
                MethodHandle javaMethodHandle;

                try {
                    javaMethodHandle = MethodHandles.publicLookup().in(javaImplClass).unreflect(javaMethod);
                } catch (IllegalAccessException exception) {
                    throw new IllegalArgumentException("method handle not found for method with name " +
                        "[" + whitelistMethod.javaMethodName + "] and parameters " + whitelistMethod.painlessParameterTypeNames);
                }

                painlessMethod = new Method(whitelistMethod.javaMethodName, ownerStruct, null, painlessReturnClass,
                    painlessParametersTypes, asmMethod, javaMethod.getModifiers(), javaMethodHandle);
                ownerStruct.staticMethods.put(painlessMethodKey, painlessMethod);
            } else if ((painlessMethod.name.equals(whitelistMethod.javaMethodName) && painlessMethod.rtn == painlessReturnClass &&
                    painlessMethod.arguments.equals(painlessParametersTypes)) == false) {
                throw new IllegalArgumentException("illegal duplicate static methods [" + painlessMethodKey + "] " +
                        "found within the struct [" + ownerStruct.name + "] with name [" + whitelistMethod.javaMethodName + "], " +
                        "return types [" + painlessReturnClass + "] and [" + painlessMethod.rtn + "], " +
                        "and parameters " + painlessParametersTypes + " and " + painlessMethod.arguments);
            }
        } else {
            Method painlessMethod = ownerStruct.methods.get(painlessMethodKey);

            if (painlessMethod == null) {
                org.objectweb.asm.commons.Method asmMethod = org.objectweb.asm.commons.Method.getMethod(javaMethod);
                MethodHandle javaMethodHandle;

                try {
                    javaMethodHandle = MethodHandles.publicLookup().in(javaImplClass).unreflect(javaMethod);
                } catch (IllegalAccessException exception) {
                    throw new IllegalArgumentException("method handle not found for method with name " +
                        "[" + whitelistMethod.javaMethodName + "] and parameters " + whitelistMethod.painlessParameterTypeNames);
                }

                painlessMethod = new Method(whitelistMethod.javaMethodName, ownerStruct, javaAugmentedClass, painlessReturnClass,
                    painlessParametersTypes, asmMethod, javaMethod.getModifiers(), javaMethodHandle);
                ownerStruct.methods.put(painlessMethodKey, painlessMethod);
            } else if ((painlessMethod.name.equals(whitelistMethod.javaMethodName) && painlessMethod.rtn.equals(painlessReturnClass) &&
                painlessMethod.arguments.equals(painlessParametersTypes)) == false) {
                throw new IllegalArgumentException("illegal duplicate member methods [" + painlessMethodKey + "] " +
                    "found within the struct [" + ownerStruct.name + "] with name [" + whitelistMethod.javaMethodName + "], " +
                    "return types [" + painlessReturnClass + "] and [" + painlessMethod.rtn + "], " +
                    "and parameters " + painlessParametersTypes + " and " + painlessMethod.arguments);
            }
        }
    }

    private void addField(String ownerStructName, Whitelist.Field whitelistField) {
        Struct ownerStruct = javaClassesToPainlessStructs.get(painlessTypesToJavaClasses.get(ownerStructName));

        if (ownerStruct == null) {
            throw new IllegalArgumentException("owner struct [" + ownerStructName + "] not defined for method with " +
                    "name [" + whitelistField.javaFieldName + "] and type " + whitelistField.painlessFieldTypeName);
        }

        if (!whitelistField.javaFieldName.matches("^[_a-zA-Z][_a-zA-Z0-9]*$")) {
            throw new IllegalArgumentException("invalid field name " +
                    "[" + whitelistField.painlessFieldTypeName + "] for owner struct [" + ownerStructName + "].");
        }

        java.lang.reflect.Field javaField;

        try {
            javaField = ownerStruct.clazz.getField(whitelistField.javaFieldName);
        } catch (NoSuchFieldException exception) {
            throw new IllegalArgumentException("field [" + whitelistField.javaFieldName + "] " +
                    "not found for class [" + ownerStruct.clazz.getName() + "].");
        }

        Class<?> painlessFieldClass;

        try {
            painlessFieldClass = getJavaClassFromPainlessType(whitelistField.painlessFieldTypeName);
        } catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException("struct not defined for return type [" + whitelistField.painlessFieldTypeName + "] " +
                "with owner struct [" + ownerStructName + "] and field with name [" + whitelistField.javaFieldName + "]", iae);
        }

        if (Modifier.isStatic(javaField.getModifiers())) {
            if (Modifier.isFinal(javaField.getModifiers()) == false) {
                throw new IllegalArgumentException("static [" + whitelistField.javaFieldName + "] " +
                        "with owner struct [" + ownerStruct.name + "] is not final");
            }

            Field painlessField = ownerStruct.staticMembers.get(whitelistField.javaFieldName);

            if (painlessField == null) {
                painlessField = new Field(whitelistField.javaFieldName, javaField.getName(),
                    ownerStruct, painlessFieldClass, javaField.getModifiers(), null, null);
                ownerStruct.staticMembers.put(whitelistField.javaFieldName, painlessField);
            } else if (painlessField.clazz != painlessFieldClass) {
                throw new IllegalArgumentException("illegal duplicate static fields [" + whitelistField.javaFieldName + "] " +
                    "found within the struct [" + ownerStruct.name + "] with type [" + whitelistField.painlessFieldTypeName + "]");
            }
        } else {
            MethodHandle javaMethodHandleGetter = null;
            MethodHandle javaMethodHandleSetter = null;

            try {
                if (Modifier.isStatic(javaField.getModifiers()) == false) {
                    javaMethodHandleGetter = MethodHandles.publicLookup().unreflectGetter(javaField);
                    javaMethodHandleSetter = MethodHandles.publicLookup().unreflectSetter(javaField);
                }
            } catch (IllegalAccessException exception) {
                throw new IllegalArgumentException("getter/setter [" + whitelistField.javaFieldName + "]" +
                    " not found for class [" + ownerStruct.clazz.getName() + "].");
            }

            Field painlessField = ownerStruct.staticMembers.get(whitelistField.javaFieldName);

            if (painlessField == null) {
                painlessField = new Field(whitelistField.javaFieldName, javaField.getName(),
                    ownerStruct, painlessFieldClass, javaField.getModifiers(), javaMethodHandleGetter, javaMethodHandleSetter);
                ownerStruct.staticMembers.put(whitelistField.javaFieldName, painlessField);
            } else if (painlessField.clazz != painlessFieldClass) {
                throw new IllegalArgumentException("illegal duplicate member fields [" + whitelistField.javaFieldName + "] " +
                    "found within the struct [" + ownerStruct.name + "] with type [" + whitelistField.painlessFieldTypeName + "]");
            }
        }
    }

    private void copyStruct(String struct, List<String> children) {
        final Struct owner = javaClassesToPainlessStructs.get(painlessTypesToJavaClasses.get(struct));

        if (owner == null) {
            throw new IllegalArgumentException("Owner struct [" + struct + "] not defined for copy.");
        }

        for (int count = 0; count < children.size(); ++count) {
            final Struct child = javaClassesToPainlessStructs.get(painlessTypesToJavaClasses.get(children.get(count)));

            if (child == null) {
                throw new IllegalArgumentException("Child struct [" + children.get(count) + "]" +
                    " not defined for copy to owner struct [" + owner.name + "].");
            }

            if (!child.clazz.isAssignableFrom(owner.clazz)) {
                throw new ClassCastException("Child struct [" + child.name + "]" +
                    " is not a super type of owner struct [" + owner.name + "] in copy.");
            }

            for (Map.Entry<MethodKey,Method> kvPair : child.methods.entrySet()) {
                MethodKey methodKey = kvPair.getKey();
                Method method = kvPair.getValue();
                if (owner.methods.get(methodKey) == null) {
                    // TODO: some of these are no longer valid or outright don't work
                    // TODO: since classes may not come from the Painless classloader
                    // TODO: and it was dependent on the order of the extends which
                    // TODO: which no longer exists since this is generated automatically
                    // sanity check, look for missing covariant/generic override
                    /*if (owner.clazz.isInterface() && child.clazz == Object.class) {
                        // ok
                    } else if (child.clazz == Spliterator.OfPrimitive.class || child.clazz == PrimitiveIterator.class) {
                        // ok, we rely on generics erasure for these (its guaranteed in the javadocs though!!!!)
                    } else if (Constants.JRE_IS_MINIMUM_JAVA9 && owner.clazz == LocalDate.class) {
                        // ok, java 9 added covariant override for LocalDate.getEra() to return IsoEra:
                        // https://bugs.openjdk.java.net/browse/JDK-8072746
                    } else {
                        try {
                            // TODO: we *have* to remove all these public members and use getter methods to encapsulate!
                            final Class<?> impl;
                            final Class<?> arguments[];
                            if (method.augmentation != null) {
                                impl = method.augmentation;
                                arguments = new Class<?>[method.arguments.size() + 1];
                                arguments[0] = method.owner.clazz;
                                for (int i = 0; i < method.arguments.size(); i++) {
                                    arguments[i + 1] = method.arguments.get(i).clazz;
                                }
                            } else {
                                impl = owner.clazz;
                                arguments = new Class<?>[method.arguments.size()];
                                for (int i = 0; i < method.arguments.size(); i++) {
                                    arguments[i] = method.arguments.get(i).clazz;
                                }
                            }
                            java.lang.reflect.Method m = impl.getMethod(method.method.getName(), arguments);
                            if (m.getReturnType() != method.rtn.clazz) {
                                throw new IllegalStateException("missing covariant override for: " + m + " in " + owner.name);
                            }
                            if (m.isBridge() && !Modifier.isVolatile(method.modifiers)) {
                                // its a bridge in the destination, but not in the source, but it might still be ok, check generics:
                                java.lang.reflect.Method source = child.clazz.getMethod(method.method.getName(), arguments);
                                if (!Arrays.equals(source.getGenericParameterTypes(), source.getParameterTypes())) {
                                    throw new IllegalStateException("missing generic override for: " + m + " in " + owner.name);
                                }
                            }
                        } catch (ReflectiveOperationException e) {
                            throw new AssertionError(e);
                        }
                    }*/
                    owner.methods.put(methodKey, method);
                }
            }

            for (Field field : child.members.values()) {
                if (owner.members.get(field.name) == null) {
                    owner.members.put(field.name,
                        new Field(field.name, field.javaName, owner, field.clazz, field.modifiers, field.getter, field.setter));
                }
            }
        }
    }

    /**
     * Precomputes a more efficient structure for dynamic method/field access.
     */
    private void addRuntimeClass(final Struct struct) {
        // add all getters/setters
        for (Map.Entry<MethodKey, Method> method : struct.methods.entrySet()) {
            String name = method.getKey().name;
            Method m = method.getValue();

            if (m.arguments.size() == 0 &&
                name.startsWith("get") &&
                name.length() > 3 &&
                Character.isUpperCase(name.charAt(3))) {
                StringBuilder newName = new StringBuilder();
                newName.append(Character.toLowerCase(name.charAt(3)));
                newName.append(name.substring(4));
                struct.getters.putIfAbsent(newName.toString(), m.handle);
            } else if (m.arguments.size() == 0 &&
                name.startsWith("is") &&
                name.length() > 2 &&
                Character.isUpperCase(name.charAt(2))) {
                StringBuilder newName = new StringBuilder();
                newName.append(Character.toLowerCase(name.charAt(2)));
                newName.append(name.substring(3));
                struct.getters.putIfAbsent(newName.toString(), m.handle);
            }

            if (m.arguments.size() == 1 &&
                name.startsWith("set") &&
                name.length() > 3 &&
                Character.isUpperCase(name.charAt(3))) {
                StringBuilder newName = new StringBuilder();
                newName.append(Character.toLowerCase(name.charAt(3)));
                newName.append(name.substring(4));
                struct.setters.putIfAbsent(newName.toString(), m.handle);
            }
        }

        // add all members
        for (Map.Entry<String, Field> member : struct.members.entrySet()) {
            struct.getters.put(member.getKey(), member.getValue().getter);
            struct.setters.put(member.getKey(), member.getValue().setter);
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

    public boolean isSimplePainlessType(String painlessType) {
        return painlessTypesToJavaClasses.containsKey(painlessType);
    }

    public Struct getPainlessStructFromJavaClass(Class<?> clazz) {
        return javaClassesToPainlessStructs.get(clazz);
    }

    public Class<?> getJavaClassFromPainlessType(String painlessType) {
        Class<?> javaClass = painlessTypesToJavaClasses.get(painlessType);

        if (javaClass != null) {
            return javaClass;
        }

        int arrayDimensions = 0;
        int arrayIndex = painlessType.indexOf('[');

        if (arrayIndex != -1) {
            int length = painlessType.length();

            while (arrayIndex < length) {
                if (painlessType.charAt(arrayIndex) == '[' && ++arrayIndex < length && painlessType.charAt(arrayIndex++) == ']') {
                    ++arrayDimensions;
                } else {
                    throw new IllegalArgumentException("invalid painless type [" + painlessType + "].");
                }
            }

            painlessType = painlessType.substring(0, painlessType.indexOf('['));
            javaClass = painlessTypesToJavaClasses.get(painlessType);

            char braces[] = new char[arrayDimensions];
            Arrays.fill(braces, '[');
            String descriptor = new String(braces);

            if (javaClass == boolean.class) {
                descriptor += "Z";
            } else if (javaClass == byte.class) {
                descriptor += "B";
            } else if (javaClass == short.class) {
                descriptor += "S";
            } else if (javaClass == char.class) {
                descriptor += "C";
            } else if (javaClass == int.class) {
                descriptor += "I";
            } else if (javaClass == long.class) {
                descriptor += "J";
            } else if (javaClass == float.class) {
                descriptor += "F";
            } else if (javaClass == double.class) {
                descriptor += "D";
            } else {
                descriptor += "L" + javaClass.getName() + ";";
            }

            try {
                return Class.forName(descriptor);
            } catch (ClassNotFoundException cnfe) {
                throw new IllegalStateException("invalid painless type [" + painlessType + "]", cnfe);
            }
        }

        throw new IllegalArgumentException("invalid painless type [" + painlessType + "]");
    }
}
