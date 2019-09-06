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
import org.elasticsearch.painless.Location;
import org.elasticsearch.painless.MethodWriter;
import org.elasticsearch.painless.builder.FunctionTable;
import org.elasticsearch.painless.builder.SymbolTable;
import org.elasticsearch.painless.lookup.PainlessClassBinding;
import org.elasticsearch.painless.lookup.PainlessInstanceBinding;
import org.elasticsearch.painless.lookup.PainlessMethod;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.elasticsearch.painless.WriterConstants.CLASS_TYPE;

/**
 * Represents a user-defined call.
 */
public final class ECallLocal extends AExpression {

    public final String name;

    private FunctionTable.LocalFunction localFunction = null;
    private PainlessMethod importedMethod = null;
    private PainlessClassBinding classBinding = null;
    private int classBindingOffset = 0;
    private PainlessInstanceBinding instanceBinding = null;

    private List<Class<?>> typeParameters;

    public ECallLocal(Location location, String name) {
        super(location);

        this.name = Objects.requireNonNull(name);
    }

    @Override
    void storeSettings(CompilerSettings settings) {
        for (ANode argument : children) {
            argument.storeSettings(settings);
        }
    }

    public static void enter(ANode node, SymbolTable table, Map<String, Object> data) {
        ECallLocal local = (ECallLocal) node;
        String name = local.name;
        List<ANode> children = local.children;

        local.localFunction = table.functionTable.getFunction(name, children.size());

        if (local.localFunction.internal == true) {
            local.localFunction = null;
        }

        if (local.localFunction == null) {
            local.importedMethod = table.painlessLookup.lookupImportedPainlessMethod(name, children.size());

            if (local.importedMethod == null) {
                local.classBinding = table.painlessLookup.lookupPainlessClassBinding(name, children.size());

                // check to see if this class binding requires an implicit this reference
                if (local.classBinding != null && local.classBinding.typeParameters.isEmpty() == false &&
                        local.classBinding.typeParameters.get(0) == table.baseClass) {
                    local.classBinding = null;
                }

                if (local.classBinding == null) {
                    // This extra check looks for a possible match where the class binding requires an implicit this
                    // reference. This is a temporary solution to allow the class binding access to data from the
                    // base script class without need for a user to add additional function arguments. A long term solution
                    // will likely involve adding a class instance binding where any instance can have a class binding
                    // as part of its API. However, the situation at run-time is difficult and requires modifications that
                    // are a substantial change if even possible to do.
                    local.classBinding = table.painlessLookup.lookupPainlessClassBinding(name, children.size() + 1);

                    if (local.classBinding != null) {
                        if (local.classBinding.typeParameters.isEmpty() == false &&
                                local.classBinding.typeParameters.get(0) == table.baseClass) {
                            local.classBindingOffset = 1;
                        } else {
                            local.classBinding = null;
                        }
                    }

                    if (local.classBinding == null) {
                        local.instanceBinding = table.painlessLookup.lookupPainlessInstanceBinding(name, children.size());

                        if (local.instanceBinding == null) {
                            throw local.createError(new IllegalArgumentException(
                                    "unknown function call [" + name + "] with [" + children.size() + "] arguments"));
                        }
                    }
                }
            }
        }

        if (local.localFunction != null) {
            local.typeParameters = new ArrayList<>(local.localFunction.typeParameters);
            local.actual = local.localFunction.returnType;
        } else if (local.importedMethod != null) {
            local.typeParameters = new ArrayList<>(local.importedMethod.typeParameters);
            local.actual = local.importedMethod.returnType;
        } else if (local.classBinding != null) {
            local.typeParameters = new ArrayList<>(local.classBinding.typeParameters);
            local.actual = local.classBinding.returnType;
        } else if (local.instanceBinding != null) {
            local.typeParameters = new ArrayList<>(local.instanceBinding.typeParameters);
            local.actual = local.instanceBinding.returnType;
        } else {
            throw new IllegalStateException("illegal tree structure");
        }

        local.statement = true;
    }

    public static void before(ANode node, ANode child, int index, SymbolTable table, Map<String, Object> data) {
        // if the class binding is using an implicit this reference then the arguments counted must
        // be incremented by 1 as the this reference will not be part of the arguments passed into
        // the class binding call
        ECallLocal local = (ECallLocal) node;
        AExpression expression = (AExpression)child;

        expression.expected = local.typeParameters.get(index + local.classBindingOffset);
        expression.internal = true;
    }

    public static void after(ANode node, ANode child, int index, SymbolTable table, Map<String, Object> data) {
        AExpression expression = (AExpression)child;
        node.children.set(index, expression.cast());
    }

    @Override
    void write(MethodWriter writer, Globals globals) {
        writer.writeDebugInfo(location);

        if (localFunction != null) {
            for (ANode argument : children) {
                argument.write(writer, globals);
            }

            writer.invokeStatic(CLASS_TYPE, new Method(localFunction.name, localFunction.methodType.toMethodDescriptorString()));
        } else if (importedMethod != null) {
            for (ANode argument : children) {
                argument.write(writer, globals);
            }

            writer.invokeStatic(Type.getType(importedMethod.targetClass),
                    new Method(importedMethod.javaMethod.getName(), importedMethod.methodType.toMethodDescriptorString()));
        } else if (classBinding != null) {
            String name = "$class_binding$" + Globals.counter++;
            String descriptor = Type.getType(classBinding.javaConstructor.getDeclaringClass()).getDescriptor();
            globals.visitor.visitField(Opcodes.ACC_PRIVATE, name, descriptor, null, null).visitEnd();

            Type type = Type.getType(classBinding.javaConstructor.getDeclaringClass());
            int javaConstructorParameterCount = classBinding.javaConstructor.getParameterCount() - classBindingOffset;

            Label nonNull = new Label();

            writer.loadThis();
            writer.getField(CLASS_TYPE, name, type);
            writer.ifNonNull(nonNull);
            writer.loadThis();
            writer.newInstance(type);
            writer.dup();

            if (classBindingOffset == 1) {
                writer.loadThis();
            }

            for (int argument = 0; argument < javaConstructorParameterCount; ++argument) {
                children.get(argument).write(writer, globals);
            }

            writer.invokeConstructor(type, Method.getMethod(classBinding.javaConstructor));
            writer.putField(CLASS_TYPE, name, type);

            writer.mark(nonNull);
            writer.loadThis();
            writer.getField(CLASS_TYPE, name, type);

            for (int argument = 0; argument < classBinding.javaMethod.getParameterCount(); ++argument) {
                children.get(argument + javaConstructorParameterCount).write(writer, globals);
            }

            writer.invokeVirtual(type, Method.getMethod(classBinding.javaMethod));
        } else if (instanceBinding != null) {
            String name = "$instance_binding$" + Globals.counter++;
            globals.statics.put(name, instanceBinding.targetInstance);

            Type type = Type.getType(instanceBinding.targetInstance.getClass());
            globals.visitor.visitField(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, name, type.getDescriptor(), null, null).visitEnd();

            writer.loadThis();
            writer.getStatic(CLASS_TYPE, name, type);

            for (int argument = 0; argument < instanceBinding.javaMethod.getParameterCount(); ++argument) {
                children.get(argument).write(writer, globals);
            }

            writer.invokeVirtual(type, Method.getMethod(instanceBinding.javaMethod));
        } else {
            throw new IllegalStateException("Illegal tree structure.");
        }
    }

    @Override
    public String toString() {
        return singleLineToStringWithOptionalArgs(children, name);
    }
}
