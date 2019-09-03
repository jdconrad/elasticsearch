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

package org.elasticsearch.painless.node;

import org.elasticsearch.painless.CompilerSettings;
import org.elasticsearch.painless.Globals;
import org.elasticsearch.painless.Locals;
import org.elasticsearch.painless.Locals.Variable;
import org.elasticsearch.painless.Location;
import org.elasticsearch.painless.MethodWriter;
import org.elasticsearch.painless.lookup.PainlessLookupUtility;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

import java.lang.invoke.MethodType;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a user-defined function.
 */
public final class SFunction extends AStatement {

    public final String name;
    public final boolean auto;
    public final boolean statik;
    public final boolean synthetic;

    private CompilerSettings settings;

    MethodType methodType;

    org.objectweb.asm.commons.Method method;

    private Variable loop = null;

    public SFunction(Location location, String name,
            boolean auto, boolean statik, boolean synthetic) {
        super(location);

        this.name = Objects.requireNonNull(name);
        this.auto = auto;
        this.statik = statik;
        this.synthetic = synthetic;
    }

    @Override
    void storeSettings(CompilerSettings settings) {
        if (children.get(2) != null) {
            children.get(2).storeSettings(settings);
        }
        this.settings = settings;
    }

    @Override
    void extractVariables(Set<String> variables) {
        for (ANode statement : children) {
            // we reset the list for function scope
            // note this is not stored for this node
            // but still required for lambdas
            statement.extractVariables(new HashSet<>());
        }
    }

    public void generateSignature() {
        Class<?> returnType = ((DTypeClass)children.get(0)).type;

        int parametersSize = children.get(1).children.size();
        Class<?>[] paramClasses = new Class<?>[parametersSize];

        for (int parameterIndex = 0; parameterIndex < parametersSize; ++parameterIndex) {
            Class<?> paramType = ((DTypeClass)children.get(1).children.get(parameterIndex).children.get(0)).type;
            paramClasses[parameterIndex] = PainlessLookupUtility.typeToJavaType(paramType);
        }

        methodType = MethodType.methodType(PainlessLookupUtility.typeToJavaType(returnType), paramClasses);
        method = new org.objectweb.asm.commons.Method(name, MethodType.methodType(
                PainlessLookupUtility.typeToJavaType(returnType), paramClasses).toMethodDescriptorString());
    }

    @Override
    void analyze(Locals locals) {
        if (children.get(2) == null) {
            throw createError(new IllegalArgumentException("Cannot generate an empty function [" + name + "]."));
        }

//        locals = Locals.newLocalScope(locals);

        /*AStatement last = (AStatement)children.get(children.size() - 1);

        for (ANode node : children) {
            AStatement statement = (AStatement)node;

            // Note that we do not need to check after the last statement because
            // there is no statement that can be unreachable after the last.
            if (allEscape) {
                throw createError(new IllegalArgumentException("Unreachable statement."));
            }

            statement.lastSource = statement == last;

            statement.analyze(locals);

            methodEscape = statement.methodEscape;
            allEscape = statement.allEscape;
        }*/

        SBlock block = (SBlock)children.get(2);
        block.lastSource = true;
        block.analyze(locals);
        methodEscape = block.methodEscape;

        if (!auto && !methodEscape && ((DTypeClass)children.get(0)).type != void.class) {
            throw createError(new IllegalArgumentException("Not all paths provide a return value for method [" + name + "]."));
        }

        if (settings.getMaxLoopCounter() > 0) {
            loop = locals.getVariable(null, Locals.LOOP);
        }
    }

    /** Writes the function to given ClassVisitor. */
    void write(ClassVisitor writer, Globals globals) {
        int access = Opcodes.ACC_PUBLIC;
        if (statik) {
            access |= Opcodes.ACC_STATIC;
        }
        if (synthetic) {
            access |= Opcodes.ACC_SYNTHETIC;
        }
        final MethodWriter function = new MethodWriter(access, method, writer, globals.getStatements(), settings);
        function.visitCode();
        write(function, globals);
        function.endMethod();
    }

    @Override
    void write(MethodWriter function, Globals globals) {
        if (settings.getMaxLoopCounter() > 0) {
            // if there is infinite loop protection, we do this once:
            // int #loop = settings.getMaxLoopCounter()
            function.push(settings.getMaxLoopCounter());
            function.visitVarInsn(Opcodes.ISTORE, loop.getSlot());
        }

        children.get(2).write(function, globals);

        boolean isVoid = ((DTypeClass)children.get(0)).type != void.class;

        if (isVoid || methodEscape) {
            function.returnValue();
        } else if (auto) {
            switch (method.getReturnType().getSort()) {
                case org.objectweb.asm.Type.VOID:
                    break;
                case org.objectweb.asm.Type.BOOLEAN:
                    function.push(false);
                    break;
                case org.objectweb.asm.Type.BYTE:
                case org.objectweb.asm.Type.SHORT:
                case org.objectweb.asm.Type.INT:
                    function.push(0);
                    break;
                case org.objectweb.asm.Type.LONG:
                    function.push(0L);
                    break;
                case org.objectweb.asm.Type.FLOAT:
                    function.push(0f);
                    break;
                case org.objectweb.asm.Type.DOUBLE:
                    function.push(0d);
                    break;
                default:
                    function.visitInsn(Opcodes.ACONST_NULL);
            }

            function.returnValue();
        } else {
            throw createError(new IllegalStateException("illegal tree structure"));
        }
    }

    @Override
    public String toString() {
        /*List<Object> description = new ArrayList<>();
        description.add(rtnTypeStr);
        description.add(name);
        if (false == (paramTypeStrs.isEmpty() && paramNameStrs.isEmpty())) {
            description.add(joinWithName("Args", pairwiseToString(paramTypeStrs, paramNameStrs), emptyList()));
        }
        return multilineToString(description, children);*/
        return "SFUNCTION";
    }
}
