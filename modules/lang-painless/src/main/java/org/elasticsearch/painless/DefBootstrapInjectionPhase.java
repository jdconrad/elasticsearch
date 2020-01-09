package org.elasticsearch.painless;/*
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

import org.elasticsearch.painless.ir.BlockNode;
import org.elasticsearch.painless.ir.CallNode;
import org.elasticsearch.painless.ir.CallSubNode;
import org.elasticsearch.painless.ir.ClassNode;
import org.elasticsearch.painless.ir.FieldNode;
import org.elasticsearch.painless.ir.FunctionNode;
import org.elasticsearch.painless.ir.ReturnNode;
import org.elasticsearch.painless.ir.StaticNode;
import org.elasticsearch.painless.ir.TypeNode;
import org.elasticsearch.painless.ir.UnboundFieldNode;
import org.elasticsearch.painless.ir.VariableNode;
import org.elasticsearch.painless.lookup.PainlessLookup;
import org.elasticsearch.painless.lookup.PainlessMethod;
import org.elasticsearch.painless.symbol.FunctionTable;
import org.objectweb.asm.Opcodes;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.util.Arrays;

public class DefBootstrapInjectionPhase {

    public static void phase(ClassNode classNode) {
        injectStaticFields(classNode);
        injectDefBootstrapMethod(classNode);
    }

    // TODO: gather static constants here for ScriptRoot?
    // adds static fields required for def bootstrapping
    protected static void injectStaticFields(ClassNode classNode) {
        Location location = new Location("$internal$DefBootstrapInjectionPhase$injectStaticFields", 0);
        int modifiers = Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC;

        classNode.addFieldNode(new FieldNode()
                .setTypeNode(new TypeNode()
                        .setLocation(location)
                        .setType(PainlessLookup.class)
                )
                .setLocation(location)
                .setModifiers(modifiers)
                .setName("$DEFINITION")
        );

        classNode.addFieldNode(new FieldNode()
                .setTypeNode(new TypeNode()
                        .setLocation(location)
                        .setType(FunctionTable.class)
                )
                .setLocation(location)
                .setModifiers(modifiers)
                .setName("$FUNCTIONS")
        );
    }

    // adds the bootstrap method required for dynamic binding for def type resolution
    protected static void injectDefBootstrapMethod(ClassNode classNode) {
        Location location = new Location("$internal$DefBootstrapInjectionPhase$injectDefBootstrapMethod", 0);

        try {
            classNode.addFunctionNode(new FunctionNode()
                    .setBlockNode(new BlockNode()
                            .addStatementNode(new ReturnNode()
                                    .setExpressionNode(new CallNode()
                                            .setPrefixNode(new StaticNode()
                                                    .setTypeNode(new TypeNode()
                                                            .setLocation(location)
                                                            .setType(DefBootstrap.class)
                                                    )
                                                    .setLocation(location)
                                            )
                                            .setChildNode(new CallSubNode()
                                                    .setTypeNode(new TypeNode()
                                                            .setLocation(location)
                                                            .setType(CallSite.class)
                                                    )
                                                    .addArgumentNode(new UnboundFieldNode()
                                                            .setTypeNode(new TypeNode()
                                                                    .setLocation(location)
                                                                    .setType(PainlessLookup.class)
                                                            )
                                                            .setLocation(location)
                                                            .setName("$DEFINITION")
                                                            .setStatic(true)
                                                    )
                                                    .addArgumentNode(new UnboundFieldNode()
                                                            .setTypeNode(new TypeNode()
                                                                    .setLocation(location)
                                                                    .setType(FunctionTable.class)
                                                            )
                                                            .setLocation(location)
                                                            .setName("$FUNCTIONS")
                                                            .setStatic(true)
                                                    )
                                                    .addArgumentNode(new VariableNode()
                                                            .setTypeNode(new TypeNode()
                                                                    .setLocation(location)
                                                                    .setType(Lookup.class)
                                                            )
                                                            .setLocation(location)
                                                            .setName("methodHandlesLookup")
                                                    )
                                                    .addArgumentNode(new VariableNode()
                                                            .setTypeNode(new TypeNode()
                                                                    .setLocation(location)
                                                                    .setType(String.class)
                                                            )
                                                            .setLocation(location)
                                                            .setName("name")
                                                    )
                                                    .addArgumentNode(new VariableNode()
                                                            .setTypeNode(new TypeNode()
                                                                    .setLocation(location)
                                                                    .setType(MethodType.class)
                                                            )
                                                            .setLocation(location)
                                                            .setName("type")
                                                    )
                                                    .addArgumentNode(new VariableNode()
                                                            .setTypeNode(new TypeNode()
                                                                    .setLocation(location)
                                                                    .setType(int.class)
                                                            )
                                                            .setLocation(location)
                                                            .setName("initialDepth")
                                                    )
                                                    .addArgumentNode(new VariableNode()
                                                            .setTypeNode(new TypeNode()
                                                                    .setLocation(location)
                                                                    .setType(int.class)
                                                            )
                                                            .setLocation(location)
                                                            .setName("flavor")
                                                    )
                                                    .addArgumentNode(new VariableNode()
                                                            .setTypeNode(new TypeNode()
                                                                    .setLocation(location)
                                                                    .setType(Object[].class)
                                                            )
                                                            .setLocation(location)
                                                            .setName("args")
                                                    )
                                                    .setLocation(location)
                                                    .setMethod(new PainlessMethod(
                                                            DefBootstrap.class.getMethod("bootstrap",
                                                                    PainlessLookup.class,
                                                                    FunctionTable.class,
                                                                    Lookup.class,
                                                                    String.class,
                                                                    MethodType.class,
                                                                    int.class,
                                                                    int.class,
                                                                    Object[].class),
                                                            DefBootstrap.class,
                                                            CallSite.class,
                                                            Arrays.asList(
                                                                    PainlessLookup.class,
                                                                    FunctionTable.class,
                                                                    Lookup.class,
                                                                    String.class,
                                                                    MethodType.class,
                                                                    int.class,
                                                                    int.class,
                                                                    Object[].class),
                                                            null,
                                                            null,
                                                            null
                                                            )
                                                    )
                                                    .setBox(DefBootstrap.class)
                                            )
                                            .setTypeNode(new TypeNode()
                                                    .setLocation(location)
                                                    .setType(CallSite.class)
                                            )
                                            .setLocation(location)
                                    )
                                    .setLocation(location)
                            )
                            .setLocation(location)
                            .setAllEscape(true)
                            .setStatementCount(1)
                    )
                    .setLocation(location)
                    .setReturnType(CallSite.class)
                    .setName("$bootstrapDef")
                    .addTypeParameters(Lookup.class, String.class, MethodType.class, int.class, int.class, Object[].class)
                    .addParameterNames("methodHandlesLookup", "name", "type", "initialDepth", "flavor", "args")
                    .setStatic(true)
                    .setVarArgs(true)
                    .setSynthetic(true)
                    .setMaxLoopCounter(0)
            );
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    private DefBootstrapInjectionPhase() {
        // do nothing
    }
}
