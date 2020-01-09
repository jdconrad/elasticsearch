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

import org.elasticsearch.painless.ir.BlockNode;
import org.elasticsearch.painless.ir.CallNode;
import org.elasticsearch.painless.ir.CallSubNode;
import org.elasticsearch.painless.ir.CatchNode;
import org.elasticsearch.painless.ir.ClassNode;
import org.elasticsearch.painless.ir.ConstantNode;
import org.elasticsearch.painless.ir.DeclarationNode;
import org.elasticsearch.painless.ir.FieldNode;
import org.elasticsearch.painless.ir.FunctionNode;
import org.elasticsearch.painless.ir.ReturnNode;
import org.elasticsearch.painless.ir.StatementNode;
import org.elasticsearch.painless.ir.StaticNode;
import org.elasticsearch.painless.ir.ThrowNode;
import org.elasticsearch.painless.ir.TryNode;
import org.elasticsearch.painless.ir.TypeNode;
import org.elasticsearch.painless.ir.UnboundCallNode;
import org.elasticsearch.painless.ir.UnboundFieldLoadNode;
import org.elasticsearch.painless.ir.VariableNode;
import org.elasticsearch.painless.lookup.PainlessLookup;
import org.elasticsearch.painless.lookup.PainlessMethod;
import org.elasticsearch.painless.symbol.FunctionTable.LocalFunction;
import org.elasticsearch.painless.symbol.ScriptRoot;
import org.elasticsearch.script.ScriptException;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.Method;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.Map;

public class ScriptInjectionPhase {

    public static void phase(ScriptRoot scriptRoot, ClassNode classNode) {
        injectStaticFieldsAndGetters(classNode);
        injectNeedsMethods(scriptRoot, classNode);
        injectGetsDeclarations(scriptRoot, classNode);
        injectSandboxExceptions(classNode);
    }

    // TODO: gather static constants here for ScriptRoot?
    // adds static fields and getters required by PainlessScript for exception handling
    protected static void injectStaticFieldsAndGetters(ClassNode classNode) {
        Location location = new Location("$internal$ScriptInjectionPhase$injectStaticFields", 0);
        int modifiers = Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC;

        classNode.addFieldNode(new FieldNode()
                .setTypeNode(new TypeNode()
                        .setLocation(location)
                        .setType(String.class)
                )
                .setLocation(location)
                .setModifiers(modifiers)
                .setName("$NAME")
        );

        classNode.addFieldNode(new FieldNode()
                .setTypeNode(new TypeNode()
                        .setLocation(location)
                        .setType(String.class)
                )
                .setLocation(location)
                .setModifiers(modifiers)
                .setName("$SOURCE")
        );

        classNode.addFieldNode(new FieldNode()
                .setTypeNode(new TypeNode()
                        .setLocation(location)
                        .setType(BitSet.class)
                )
                .setLocation(location)
                .setModifiers(modifiers)
                .setName("$STATEMENTS")
        );

        classNode.addFunctionNode(new FunctionNode()
                .setBlockNode(new BlockNode()
                        .addStatementNode(new ReturnNode()
                                .setExpressionNode(new UnboundFieldLoadNode()
                                        .setTypeNode(new TypeNode()
                                                .setLocation(location)
                                                .setType(String.class)
                                        )
                                        .setLocation(location)
                                        .setName("$NAME")
                                        .setStatic(true)
                                )
                                .setLocation(location)
                        )
                        .setLocation(location)
                        .setAllEscape(true)
                        .setStatementCount(1)
                )
                .setLocation(location)
                .setName("getName")
                .setReturnType(String.class)
                .setStatic(false)
                .setVarArgs(false)
                .setSynthetic(true)
                .setMaxLoopCounter(0)
        );

        classNode.addFunctionNode(new FunctionNode()
                .setBlockNode(new BlockNode()
                        .addStatementNode(new ReturnNode()
                                .setExpressionNode(new UnboundFieldLoadNode()
                                        .setTypeNode(new TypeNode()
                                                .setLocation(location)
                                                .setType(String.class)
                                        )
                                        .setLocation(location)
                                        .setName("$SOURCE")
                                        .setStatic(true)
                                )
                                .setLocation(location)
                        )
                        .setLocation(location)
                        .setAllEscape(true)
                        .setStatementCount(1)
                )
                .setLocation(location)
                .setName("getSource")
                .setReturnType(String.class)
                .setStatic(false)
                .setVarArgs(false)
                .setSynthetic(true)
                .setMaxLoopCounter(0)
        );

        classNode.addFunctionNode(new FunctionNode()
                .setBlockNode(new BlockNode()
                        .addStatementNode(new ReturnNode()
                                .setExpressionNode(new UnboundFieldLoadNode()
                                        .setTypeNode(new TypeNode()
                                                .setLocation(location)
                                                .setType(BitSet.class)
                                        )
                                        .setLocation(location)
                                        .setName("$STATEMENTS")
                                        .setStatic(true)
                                )
                                .setLocation(location)
                        )
                        .setLocation(location)
                        .setAllEscape(true)
                        .setStatementCount(1)
                )
                .setLocation(location)
                .setName("getStatements")
                .setReturnType(BitSet.class)
                .setStatic(false)
                .setVarArgs(false)
                .setSynthetic(true)
                .setMaxLoopCounter(0)
        );
    }

    // injects needs methods as defined by ScriptClassInfo
    protected static void injectNeedsMethods(ScriptRoot scriptRoot, ClassNode classNode) {
        Location location = new Location("$internal$ScriptInjectionPhase$injectNeedsMethods", 0);

        for (org.objectweb.asm.commons.Method needsMethod : scriptRoot.getScriptClassInfo().getNeedsMethods()) {
            String name = needsMethod.getName();
            name = name.substring(5);
            name = Character.toLowerCase(name.charAt(0)) + name.substring(1);

            classNode.addFunctionNode(new FunctionNode()
                    .setBlockNode(new BlockNode()
                            .addStatementNode(new ReturnNode()
                                    .setExpressionNode(new ConstantNode()
                                            .setTypeNode(new TypeNode()
                                                    .setLocation(location)
                                                    .setType(boolean.class)
                                            )
                                            .setLocation(location)
                                            .setConstant(scriptRoot.getUsedVariables().contains(name))
                                    )
                                    .setLocation(location)
                            )
                            .setLocation(location)
                            .setAllEscape(true)
                            .setStatementCount(1)
                    )
                    .setLocation(location)
                    .setName(needsMethod.getName())
                    .setReturnType(boolean.class)
                    .setStatic(false)
                    .setVarArgs(false)
                    .setSynthetic(true)
                    .setMaxLoopCounter(0)
            );
        }
    }

    // - injects the initial value for declarations based on gets methods
    // - removes unused gets methods declarations
    protected static void injectGetsDeclarations(ScriptRoot scriptRoot, ClassNode classNode) {
        Location location = new Location("$internal$ScriptInjectionPhase$injectGetsDeclarations", 0);
        FunctionNode executeFunctionNode = null;

        for (FunctionNode functionNode : classNode.getFunctionsNodes()) {
            if ("execute".equals(functionNode.getName())) {
                executeFunctionNode = functionNode;
                break;
            }
        }

        BlockNode blockNode = executeFunctionNode.getBlockNode();

        int statementIndex = 0;

        while (statementIndex < blockNode.getStatementsNodes().size()) {
            StatementNode statementNode = blockNode.getStatementNode(statementIndex);

            if (statementNode instanceof DeclarationNode) {
                DeclarationNode declarationNode = (DeclarationNode) statementNode;
                boolean isRemoved = false;

                for (int getIndex = 0; getIndex < scriptRoot.getScriptClassInfo().getGetMethods().size(); ++getIndex) {
                    Class<?> returnType = scriptRoot.getScriptClassInfo().getGetReturns().get(getIndex);
                    Method getMethod = scriptRoot.getScriptClassInfo().getGetMethods().get(getIndex);
                    String name = getMethod.getName().substring(3);
                    name = Character.toLowerCase(name.charAt(0)) + name.substring(1);

                    if (name.equals(declarationNode.getName())) {
                        if (scriptRoot.getUsedVariables().contains(name)) {
                            declarationNode.setExpressionNode(new UnboundCallNode()
                                    .setTypeNode(new TypeNode()
                                            .setLocation(location)
                                            .setType(declarationNode.getDeclarationType())
                                    )
                                    .setLocation(location)
                                    .setLocalFunction(new LocalFunction(
                                            getMethod.getName(), returnType, Collections.emptyList(), true, false
                                    ))
                            );
                        } else {
                            blockNode.removeStatementNode(statementIndex);
                            isRemoved = true;
                        }

                        break;
                    }
                }

                if (isRemoved == false) {
                    ++statementIndex;
                }
            } else {
                ++statementIndex;
            }
        }
    }

    // injects exceptions to sandbox the user-defined code
    // the following exceptions sandbox the entirety of the script:
    // PainlessExplainError, PainlessError, BootstrapMethodError, OutOfMemoryError, StackOverflowError, and Exception
    // these are caught and rethrown as a ScriptException to prevent node crashes
    protected static void injectSandboxExceptions(ClassNode classNode) {
        Location location = new Location("$internal$ScriptInjectionPhase$injectSandboxExceptions", 0);
        FunctionNode executeFunctionNode = null;

            for (FunctionNode functionNode : classNode.getFunctionsNodes()) {
            if ("execute".equals(functionNode.getName())) {
                executeFunctionNode = functionNode;
                break;
            }
        }

        BlockNode blockNode = executeFunctionNode.getBlockNode();

        try {
            TryNode tryNode = new TryNode()
                    .setBlockNode(blockNode)
                    .addCatchNode(new CatchNode()
                            .setDeclarationNode(new DeclarationNode()
                                    .setDeclarationTypeNode(new TypeNode()
                                            .setLocation(location)
                                            .setType(PainlessExplainError.class)
                                    )
                                    .setName("painlessExplainError")
                                    .setRequiresDefault(false)
                                    .setLocation(location)
                            )
                            .setBlockNode(new BlockNode()
                                    .addStatementNode(new ThrowNode()
                                            .setExpressionNode(new UnboundCallNode()
                                                    .setTypeNode(new TypeNode()
                                                            .setLocation(location)
                                                            .setType(ScriptException.class)
                                                    )
                                                    .addArgumentNode(new VariableNode()
                                                            .setTypeNode(new TypeNode()
                                                                    .setLocation(location)
                                                                    .setType(ScriptException.class)
                                                            )
                                                            .setLocation(location)
                                                            .setName("painlessExplainError")
                                                    )
                                                    .addArgumentNode(new CallNode()
                                                            .setTypeNode(new TypeNode()
                                                                    .setLocation(location)
                                                                    .setType(Map.class)
                                                            )
                                                            .setPrefixNode(new VariableNode()
                                                                    .setTypeNode(new TypeNode()
                                                                            .setLocation(location)
                                                                            .setType(PainlessExplainError.class)
                                                                    )
                                                                    .setLocation(location)
                                                                    .setName("painlessExplainError")
                                                            )
                                                            .setChildNode(new CallSubNode()
                                                                    .setTypeNode(new TypeNode()
                                                                            .setLocation(location)
                                                                            .setType(Map.class)
                                                                    )
                                                                    .addArgumentNode(new UnboundFieldLoadNode()
                                                                            .setTypeNode(new TypeNode()
                                                                                    .setLocation(location)
                                                                                    .setType(PainlessLookup.class)
                                                                            )
                                                                            .setLocation(location)
                                                                            .setName("$DEFINITION")
                                                                            .setStatic(true)
                                                                    )
                                                                    .setBox(PainlessExplainError.class)
                                                                    .setMethod(new PainlessMethod(
                                                                                    PainlessExplainError.class.getMethod(
                                                                                            "getHeaders",
                                                                                            PainlessLookup.class),
                                                                                    PainlessExplainError.class,
                                                                                    null,
                                                                                    Collections.emptyList(),
                                                                                    null,
                                                                                    null,
                                                                                    null
                                                                            )
                                                                    )
                                                                    .setLocation(location)
                                                            )
                                                            .setLocation(location)
                                                    )
                                                    .setLocalFunction(new LocalFunction(
                                                                    "convertToScriptException",
                                                                    ScriptException.class,
                                                                    Arrays.asList(Throwable.class, Map.class),
                                                                    true,
                                                                    false
                                                            )
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
                    )
                    .setLocation(location);

            for (Class<?> throwable : new Class<?>[] {
                    PainlessError.class, BootstrapMethodError.class, OutOfMemoryError.class, StackOverflowError.class, Exception.class}) {

                String name = throwable.getSimpleName();
                name = Character.toLowerCase(name.charAt(0)) + name.substring(1);

                tryNode.addCatchNode(new CatchNode()
                        .setDeclarationNode(new DeclarationNode()
                                .setDeclarationTypeNode(new TypeNode()
                                        .setLocation(location)
                                        .setType(throwable)
                                )
                                .setName(name)
                                .setRequiresDefault(false)
                                .setLocation(location)
                        )
                        .setBlockNode(new BlockNode()
                                .addStatementNode(new ThrowNode()
                                        .setExpressionNode(new UnboundCallNode()
                                                .setTypeNode(new TypeNode()
                                                        .setLocation(location)
                                                        .setType(ScriptException.class)
                                                )
                                                .addArgumentNode(new VariableNode()
                                                        .setTypeNode(new TypeNode()
                                                                .setLocation(location)
                                                                .setType(ScriptException.class)
                                                        )
                                                        .setLocation(location)
                                                        .setName(name)
                                                )
                                                .addArgumentNode(new CallNode()
                                                        .setTypeNode(new TypeNode()
                                                                .setLocation(location)
                                                                .setType(Map.class)
                                                        )
                                                        .setPrefixNode(new StaticNode()
                                                                .setTypeNode(new TypeNode()
                                                                        .setLocation(location)
                                                                        .setType(Collections.class)
                                                                )
                                                                .setLocation(location)
                                                        )
                                                        .setChildNode(new CallSubNode()
                                                                .setTypeNode(new TypeNode()
                                                                        .setLocation(location)
                                                                        .setType(Map.class)
                                                                )
                                                                .setBox(Collections.class)
                                                                .setMethod(new PainlessMethod(
                                                                                Collections.class.getMethod("emptyMap"),
                                                                                Collections.class,
                                                                                null,
                                                                                Collections.emptyList(),
                                                                                null,
                                                                                null,
                                                                                null
                                                                        )
                                                                )
                                                                .setLocation(location)
                                                        )
                                                        .setLocation(location)
                                                )
                                                .setLocalFunction(new LocalFunction(
                                                                "convertToScriptException",
                                                                ScriptException.class,
                                                                Arrays.asList(Throwable.class, Map.class),
                                                                true,
                                                                false
                                                        )
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
                );
            }

            blockNode = new BlockNode()
                    .addStatementNode(tryNode)
                    .setLocation(location)
                    .setAllEscape(blockNode.doAllEscape())
                    .setStatementCount(blockNode.getStatementCount());

            executeFunctionNode.setBlockNode(blockNode);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    protected ScriptInjectionPhase() {
        // do nothing
    }
}
