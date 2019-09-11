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

import org.elasticsearch.painless.FunctionRef;
import org.elasticsearch.painless.Globals;
import org.elasticsearch.painless.Location;
import org.elasticsearch.painless.MethodWriter;
import org.elasticsearch.painless.builder.ASTBuilder;
import org.elasticsearch.painless.builder.ScopeTable.FunctionScope;
import org.elasticsearch.painless.builder.ScopeTable.LambdaScope;
import org.elasticsearch.painless.builder.ScopeTable.Variable;
import org.elasticsearch.painless.builder.SymbolTable;
import org.elasticsearch.painless.lookup.PainlessLookupUtility;
import org.elasticsearch.painless.lookup.PainlessMethod;
import org.elasticsearch.painless.lookup.def;
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.List;

/**
 * Lambda expression node.
 * <p>
 * This can currently only be the direct argument of a call (method/constructor).
 * When the argument is of a known type, it uses
 * <a href="http://cr.openjdk.java.net/~briangoetz/lambda/lambda-translation.html">
 * Java's lambda translation</a>. However, if its a def call, then we don't have
 * enough information, and have to defer this until link time. In that case a placeholder
 * and all captures are pushed onto the stack and folded into the signature of the parent call.
 * <p>
 * For example:
 * <br>
 * {@code def list = new ArrayList(); int capture = 0; list.sort((x,y) -> x - y + capture)}
 * <br>
 * is converted into a call (pseudocode) such as:
 * <br>
 * {@code sort(list, lambda$0, capture)}
 * <br>
 * At link time, when we know the interface type, this is decomposed with MethodHandle
 * combinators back into (pseudocode):
 * <br>
 * {@code sort(list, lambda$0(capture))}
 */
public final class ELambda extends AExpression implements ILambda {

    // captured variables
    private List<Variable> captures;
    // static parent, static lambda
    private FunctionRef ref;
    // dynamic parent, deferred until link time
    private String defPointer;

    public ELambda(Location location) {
        super(location);
    }

    @Override
    void analyze(SymbolTable table) {
        LambdaScope lambdaScope = (LambdaScope)table.scopes().getNodeScope(this);
        SDeclBlock parameters = (SDeclBlock)children.get(0);

        Class<?> returnType;
        List<Class<?>> paramTypes = new ArrayList<>();
        List<String> paramNames = new ArrayList<>();
        PainlessMethod interfaceMethod;

        // inspect the target first, set interface method if we know it.
        if (expected == null) {
            interfaceMethod = null;
            // we don't know anything: treat as def
            returnType = def.class;
            // don't infer any types, replace any null types with def
            for (ANode node : parameters.children) {
                SDeclaration parameter = (SDeclaration) node;
                paramNames.add(parameter.name);

                if (parameter.children.get(0) == null) {
                    paramTypes.add(def.class);
                } else {
                    paramTypes.add(((DTypeClass)parameter.children.get(0)).type);
                }

                lambdaScope.setVariableType(parameter.name, paramTypes.get(paramTypes.size() - 1));
            }
        } else {
            // we know the method statically, infer return type and any unknown/def types
            interfaceMethod = table.lookup().lookupFunctionalInterfacePainlessMethod(expected);
            if (interfaceMethod == null) {
                throw createError(new IllegalArgumentException("Cannot pass lambda to " +
                        "[" + PainlessLookupUtility.typeToCanonicalTypeName(expected) + "], not a functional interface"));
            }
            // check arity before we manipulate parameters
            if (interfaceMethod.typeParameters.size() != parameters.children.size())
                throw new IllegalArgumentException("Incorrect number of parameters for [" + interfaceMethod.javaMethod.getName() +
                        "] in [" + PainlessLookupUtility.typeToCanonicalTypeName(expected) + "]");
            // for method invocation, its allowed to ignore the return value
            if (interfaceMethod.returnType == void.class) {
                returnType = def.class;
            } else {
                returnType = interfaceMethod.returnType;
            }
            // replace any null types with the actual type
            for (int i = 0; i < parameters.children.size(); i++) {
                SDeclaration parameter = (SDeclaration)parameters.children.get(i);
                paramNames.add(parameter.name);

                if (parameter.children.get(0) == null) {
                    paramTypes.add(interfaceMethod.typeParameters.get(i));
                } else {
                    paramTypes.add(((DTypeClass)parameter.children.get(0)).type);
                }

                lambdaScope.setVariableType(parameter.name, paramTypes.get(paramTypes.size() - 1));
            }
        }
        // any of those variables defined in our scope need to be captured
        captures = lambdaScope.getCapturedVariables();
        // prepend capture list to lambda's arguments
        for (int index = 0; index < captures.size(); ++index) {
            Variable var = captures.get(index);
            paramTypes.add(index, var.getType());
            paramNames.add(index, var.getName());
        }

        String synthetic = table.nextSyntheticName("lambda");
        ASTBuilder builder = new ASTBuilder();
        builder.visitFunction(location, synthetic, true, false, true, true)
                .visitTypeClass(location, returnType).endVisit()
                .visitDeclBlock(location);
                        for (int index = 0; index < paramTypes.size(); ++index) {
                                builder.visitDeclaration(location, paramNames.get(index), true, false)
                                        .visitTypeClass(location, paramTypes.get(index)).endVisit()
                                        .visitEmpty()
                                .endVisit();
                        }
                builder.endVisit()
                .visitEmpty()
                .visitNode(children.get(1)).endVisit()
        .endVisit();

        SFunction function = (SFunction)builder.endBuild();
        children.set(1, function);

        FunctionScope functionScope = table.scopes().newFunctionScope(function);
        for (int index = 0; index < paramTypes.size(); ++index) {
            String name = paramNames.get(index);
            Class<?> type = paramTypes.get(index);
            functionScope.addVariable(name, true);
            functionScope.setVariableType(name, type);
        }
        functionScope.setReturnType(returnType);
        table.functions().addFunction(function.name, true, returnType, paramTypes, paramNames);
        function.analyze(table);

        // setup method reference to synthetic method
        if (expected == null) {
            ref = null;
            actual = String.class;
            defPointer = "Sthis." + synthetic + "," + captures.size();
        } else {
            defPointer = null;
            ref = FunctionRef.create(table.lookup(), table.functions(), location, expected, "this", function.name, captures.size());
            actual = expected;
        }
    }

    @Override
    void write(MethodWriter writer, Globals globals) {
        writer.writeDebugInfo(location);

        if (ref != null) {
            writer.writeDebugInfo(location);
            // load captures
            for (Variable capture : captures) {
                writer.visitVarInsn(MethodWriter.getType(capture.getType()).getOpcode(Opcodes.ILOAD), capture.getSlot());
            }

            writer.invokeLambdaCall(ref);
        } else {
            // placeholder
            writer.push((String)null);
            // load captures
            for (Variable capture : captures) {
                writer.visitVarInsn(MethodWriter.getType(capture.getType()).getOpcode(Opcodes.ILOAD), capture.getSlot());
            }
        }

        SFunction function = (SFunction)children.get(1);
        function.write(globals.visitor, globals);
    }

    @Override
    public String getPointer() {
        return defPointer;
    }

    @Override
    public org.objectweb.asm.Type[] getCaptures() {
        org.objectweb.asm.Type[] types = new org.objectweb.asm.Type[captures.size()];
        for (int i = 0; i < types.length; i++) {
            types[i] = MethodWriter.getType(captures.get(i).getType());
        }
        return types;
    }

    @Override
    public String toString() {
        return null;
    }
}
