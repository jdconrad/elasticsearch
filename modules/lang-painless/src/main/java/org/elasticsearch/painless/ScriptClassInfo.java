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

import org.elasticsearch.painless.builder.ASTBuilder;
import org.elasticsearch.painless.lookup.PainlessLookup;
import org.elasticsearch.painless.lookup.PainlessLookupUtility;
import org.elasticsearch.painless.lookup.def;
import org.objectweb.asm.util.Printer;

import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static java.util.Collections.unmodifiableList;
import static org.elasticsearch.painless.WriterConstants.NEEDS_PARAMETER_METHOD_TYPE;

/**
 * Information about the interface being implemented by the painless script.
 */
public class ScriptClassInfo {

    private final Class<?> baseClass;
    private final org.objectweb.asm.commons.Method executeMethod;
    private final Class<?> executeMethodReturnType;
    private final List<MethodArgument> executeArguments;
    private final List<org.objectweb.asm.commons.Method> needsMethods;
    private final List<org.objectweb.asm.commons.Method> getMethods;
    private final List<Class<?>> getReturns;

    public ScriptClassInfo(PainlessLookup painlessLookup, Class<?> baseClass) {
        this.baseClass = baseClass;

        // Find the main method and the uses$argName methods
        java.lang.reflect.Method executeMethod = null;
        List<org.objectweb.asm.commons.Method> needsMethods = new ArrayList<>();
        List<org.objectweb.asm.commons.Method> getMethods = new ArrayList<>();
        List<Class<?>> getReturns = new ArrayList<>();
        for (java.lang.reflect.Method m : baseClass.getMethods()) {
            if (m.isDefault()) {
                continue;
            }
            if (m.getName().equals("execute")) {
                if (executeMethod == null) {
                    executeMethod = m;
                } else {
                    throw new IllegalArgumentException(
                            "Painless can only implement interfaces that have a single method named [execute] but [" + baseClass.getName()
                                    + "] has more than one.");
                }
            }
            if (m.getName().startsWith("needs") && m.getReturnType() == boolean.class && m.getParameterTypes().length == 0) {
                needsMethods.add(new org.objectweb.asm.commons.Method(m.getName(), NEEDS_PARAMETER_METHOD_TYPE.toMethodDescriptorString()));
            }
            if (m.getName().startsWith("get") && m.getName().equals("getClass") == false && Modifier.isStatic(m.getModifiers()) == false) {
                getReturns.add(
                    definitionTypeForClass(painlessLookup, m.getReturnType(), componentType -> "[" + m.getName() + "] has unknown return " +
                        "type [" + componentType.getName() + "]. Painless can only support getters with return types that are " +
                        "whitelisted."));

                getMethods.add(new org.objectweb.asm.commons.Method(m.getName(),
                    MethodType.methodType(m.getReturnType()).toMethodDescriptorString()));

            }
        }
        MethodType methodType = MethodType.methodType(executeMethod.getReturnType(), executeMethod.getParameterTypes());
        this.executeMethod = new org.objectweb.asm.commons.Method(executeMethod.getName(), methodType.toMethodDescriptorString());
        executeMethodReturnType = definitionTypeForClass(painlessLookup, executeMethod.getReturnType(),
                componentType -> "Painless can only implement execute methods returning a whitelisted type but [" + baseClass.getName()
                        + "#execute] returns [" + componentType.getName() + "] which isn't whitelisted.");

        // Look up the argument
        List<MethodArgument> arguments = new ArrayList<>();
        String[] argumentNamesConstant = readArgumentNamesConstant(baseClass);
        Class<?>[] types = executeMethod.getParameterTypes();
        if (argumentNamesConstant.length != types.length) {
            throw new IllegalArgumentException("[" + baseClass.getName() + "#ARGUMENTS] has length [2] but ["
                    + baseClass.getName() + "#execute] takes [1] argument.");
        }
        for (int arg = 0; arg < types.length; arg++) {
            arguments.add(methodArgument(painlessLookup, types[arg], argumentNamesConstant[arg]));
        }
        this.executeArguments = unmodifiableList(arguments);
        this.needsMethods = unmodifiableList(needsMethods);
        this.getMethods = unmodifiableList(getMethods);
        this.getReturns = unmodifiableList(getReturns);
    }

    /**
     * The interface that the Painless script should implement.
     */
    public Class<?> getBaseClass() {
        return baseClass;
    }

    /**
     * An asm method descriptor for the {@code execute} method.
     */
    public org.objectweb.asm.commons.Method getExecuteMethod() {
        return executeMethod;
    }

    /**
     * The Painless {@link Class} or the return type of the {@code execute} method. This is used to generate the appropriate
     * return bytecode.
     */
    public Class<?> getExecuteMethodReturnType() {
        return executeMethodReturnType;
    }

    /**
     * Painless {@link Class}s and names of the arguments to the {@code execute} method. The names are exposed to the Painless
     * script.
     */
    public List<MethodArgument> getExecuteArguments() {
        return executeArguments;
    }

    /**
     * The {@code uses$varName} methods that must be implemented by Painless to complete implementing the interface.
     */
    public List<org.objectweb.asm.commons.Method> getNeedsMethods() {
        return needsMethods;
    }

    /**
     * The {@code getVarName} methods that must be implemented by Painless to complete implementing the interface.
     */
    public List<org.objectweb.asm.commons.Method> getGetMethods() {
        return getMethods;
    }

    /**
     * The {@code getVarName} methods return types.
     */
    public List<Class<?>> getGetReturns() {
        return getReturns;
    }

    /**
     * Painless {@link Class}es and name of the argument to the {@code execute} method.
     */
    public static class MethodArgument {
        private final Class<?> clazz;
        private final String name;

        public MethodArgument(Class<?> clazz, String name) {
            this.clazz = clazz;
            this.name = name;
        }

        public Class<?> getClazz() {
            return clazz;
        }

        public String getName() {
            return name;
        }
    }

    private MethodArgument methodArgument(PainlessLookup painlessLookup, Class<?> clazz, String argName) {
        Class<?> defClass = definitionTypeForClass(painlessLookup, clazz, componentType -> "[" + argName + "] is of unknown type ["
                + componentType.getName() + ". Painless interfaces can only accept arguments that are of whitelisted types.");
        return new MethodArgument(defClass, argName);
    }

    private static Class<?> definitionTypeForClass(PainlessLookup painlessLookup, Class<?> type,
                                                   Function<Class<?>, String> unknownErrorMessageSource) {
        type = PainlessLookupUtility.javaTypeToType(type);
        Class<?> componentType = type;

        while (componentType.isArray()) {
            componentType = componentType.getComponentType();
        }

        if (componentType != def.class && painlessLookup.lookupPainlessClass(componentType) == null) {
            throw new IllegalArgumentException(unknownErrorMessageSource.apply(componentType));
        }

        return type;
    }

    private static String[] readArgumentNamesConstant(Class<?> iface) {
        Field argumentNamesField;
        try {
            argumentNamesField = iface.getField("PARAMETERS");
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("Painless needs a constant [String[] PARAMETERS] on all interfaces it implements with the "
                    + "names of the method arguments but [" + iface.getName() + "] doesn't have one.", e);
        }
        if (false == argumentNamesField.getType().equals(String[].class)) {
            throw new IllegalArgumentException("Painless needs a constant [String[] PARAMETERS] on all interfaces it implements with the "
                    + "names of the method arguments but [" + iface.getName() + "] doesn't have one.");
        }
        try {
            return (String[]) argumentNamesField.get(null);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new IllegalArgumentException("Error trying to read [" + iface.getName() + "#ARGUMENTS]", e);
        }
    }

    public ASTBuilder startBuild(String scriptName, String scriptSource, Printer debugStream, Location location) {
        ASTBuilder builder = new ASTBuilder();

        builder.visitSource(scriptName, scriptSource, baseClass, debugStream, location);

        for (org.objectweb.asm.commons.Method needsMethod : needsMethods) {
            String name = needsMethod.getName().substring(5);
            name = Character.toLowerCase(name.charAt(0)) + name.substring(1);

            builder.visitFunction(location, needsMethod.getName(), false, false, true)
                    .visitTypeClass(location, boolean.class).endVisit()
                    .visitParameters(location).endVisit()
                    .visitBlock(location)
                            .visitReturn(location)
                                .visitUsed(location, name).endVisit()
                            .endVisit()
                    .endVisit()
            .endVisit();
        }

        builder.visitFunction(location, executeMethod.getName(), true, false, true)
                .visitTypeClass(location, executeMethodReturnType).endVisit()
                .visitParameters(location);

        for (MethodArgument argument : executeArguments) {
            builder.visitParameter(location, argument.getName())
                    .visitTypeClass(location, argument.getClazz()).endVisit()
            .endVisit();
        }

        builder.endVisit()
                .visitBlock(location)
                        .visitTry(location)
                            .visitBlock(location);

        for (int index = 0; index < getMethods.size(); ++index) {
            org.objectweb.asm.commons.Method method = getMethods.get(index);
            String name = method.getName().substring(3);
            name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
            Class<?> type = getReturns.get(index);

            builder.visitDeclaration(location, name, true)
                    .visitTypeClass(location, type).endVisit()
                    .visitDirectCallInvoke(location, method, false, false)
                            .visitThis(location).endVisit()
                    .endVisit()
            .endVisit();
        }

        builder.save("execute");

        return builder;
        /*    Label startTry = new Label();
        Label endTry = new Label();
        Label startExplainCatch = new Label();
        Label startOtherCatch = new Label();
        Label endCatch = new Label();
        writer.mark(startTry);

        if (settings.getMaxLoopCounter() > 0) {
            // if there is infinite loop protection, we do this once:
            // int #loop = settings.getMaxLoopCounter()

            Variable loop = mainMethod.getVariable(null, Locals.LOOP);

            writer.push(settings.getMaxLoopCounter());
            writer.visitVarInsn(Opcodes.ISTORE, loop.getSlot());
        }

        for (org.objectweb.asm.commons.Method method : getMethods) {
            String name = method.getName().substring(3);
            name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
            Variable variable = mainMethod.getVariable(null, name);

            writer.loadThis();
            writer.invokeVirtual(Type.getType(baseClass), method);
            writer.visitVarInsn(method.getReturnType().getOpcode(Opcodes.ISTORE), variable.getSlot());
        }

        for (int statementIndex = functionCount; statementIndex < children.size(); ++statementIndex) {
            children.get(statementIndex).write(writer, globals);
        }

        if (!methodEscape) {
            switch (scriptClassInfo.getExecuteMethod().getReturnType().getSort()) {
                case org.objectweb.asm.Type.VOID:
                    break;
                case org.objectweb.asm.Type.BOOLEAN:
                    writer.push(false);
                    break;
                case org.objectweb.asm.Type.BYTE:
                    writer.push(0);
                    break;
                case org.objectweb.asm.Type.SHORT:
                    writer.push(0);
                    break;
                case org.objectweb.asm.Type.INT:
                    writer.push(0);
                    break;
                case org.objectweb.asm.Type.LONG:
                    writer.push(0L);
                    break;
                case org.objectweb.asm.Type.FLOAT:
                    writer.push(0f);
                    break;
                case org.objectweb.asm.Type.DOUBLE:
                    writer.push(0d);
                    break;
                default:
                    writer.visitInsn(Opcodes.ACONST_NULL);
            }
            writer.returnValue();
        }

        writer.mark(endTry);
        writer.goTo(endCatch);
        // This looks like:
        // } catch (PainlessExplainError e) {
        //   throw this.convertToScriptException(e, e.getHeaders($DEFINITION))
        // }
        writer.visitTryCatchBlock(startTry, endTry, startExplainCatch, PAINLESS_EXPLAIN_ERROR_TYPE.getInternalName());
        writer.mark(startExplainCatch);
        writer.loadThis();
        writer.swap();
        writer.dup();
        writer.getStatic(CLASS_TYPE, "$DEFINITION", DEFINITION_TYPE);
        writer.invokeVirtual(PAINLESS_EXPLAIN_ERROR_TYPE, PAINLESS_EXPLAIN_ERROR_GET_HEADERS_METHOD);
        writer.invokeInterface(BASE_INTERFACE_TYPE, CONVERT_TO_SCRIPT_EXCEPTION_METHOD);
        writer.throwException();
        // This looks like:
        // } catch (PainlessError | BootstrapMethodError | OutOfMemoryError | StackOverflowError | Exception e) {
        //   throw this.convertToScriptException(e, e.getHeaders())
        // }
        // We *think* it is ok to catch OutOfMemoryError and StackOverflowError because Painless is stateless
        writer.visitTryCatchBlock(startTry, endTry, startOtherCatch, PAINLESS_ERROR_TYPE.getInternalName());
        writer.visitTryCatchBlock(startTry, endTry, startOtherCatch, BOOTSTRAP_METHOD_ERROR_TYPE.getInternalName());
        writer.visitTryCatchBlock(startTry, endTry, startOtherCatch, OUT_OF_MEMORY_ERROR_TYPE.getInternalName());
        writer.visitTryCatchBlock(startTry, endTry, startOtherCatch, STACK_OVERFLOW_ERROR_TYPE.getInternalName());
        writer.visitTryCatchBlock(startTry, endTry, startOtherCatch, EXCEPTION_TYPE.getInternalName());
        writer.mark(startOtherCatch);
        writer.loadThis();
        writer.swap();
        writer.invokeStatic(COLLECTIONS_TYPE, EMPTY_MAP_METHOD);
        writer.invokeInterface(BASE_INTERFACE_TYPE, CONVERT_TO_SCRIPT_EXCEPTION_METHOD);
        writer.throwException();
        writer.mark(endCatch);*/
    }

    public ASTBuilder endBuild(ASTBuilder builder) {
        Location location = new Location("", 0);

        builder.endVisit();

        builder.visitCatch(location)
                .visitTypeClass(location, Throwable.class).endVisit()
                .visitDeclaration(location, "#e", false)
                        .visitTypeClass(location, PainlessExplainError.class).endVisit()
                        .visitEmpty()
                .endVisit()
                .visitBlock(location)
                        .visitThrow(location)
                                .visitNewObj(location)
                                        .visitTypeClass(location, IllegalArgumentException.class).endVisit()
                                        .visitString(location, "threw an exception").endVisit()
                                .endVisit()
                        .endVisit()
                .endVisit()
        .endVisit();

        builder.endVisit().endVisit().endVisit().endVisit();

        return builder;
    }
}
