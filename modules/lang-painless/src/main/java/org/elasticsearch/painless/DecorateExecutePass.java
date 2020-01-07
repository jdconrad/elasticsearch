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
import org.elasticsearch.painless.ir.DeclarationNode;
import org.elasticsearch.painless.ir.FunctionNode;
import org.elasticsearch.painless.ir.StatementNode;
import org.elasticsearch.painless.ir.StaticNode;
import org.elasticsearch.painless.ir.ThrowNode;
import org.elasticsearch.painless.ir.TryNode;
import org.elasticsearch.painless.ir.TypeNode;
import org.elasticsearch.painless.ir.UnboundCallNode;
import org.elasticsearch.painless.ir.UnboundFieldNode;
import org.elasticsearch.painless.ir.VariableNode;
import org.elasticsearch.painless.lookup.PainlessLookup;
import org.elasticsearch.painless.lookup.PainlessMethod;
import org.elasticsearch.painless.symbol.FunctionTable.LocalFunction;
import org.elasticsearch.painless.symbol.ScriptRoot;
import org.elasticsearch.script.ScriptException;
import org.objectweb.asm.commons.Method;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class DecorateExecutePass {

    public static void pass(ScriptRoot scriptRoot, ClassNode classNode) {
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
                                            .setLocation(declarationNode.getLocation())
                                            .setType(declarationNode.getDeclarationType())
                                    )
                                    .setLocation(declarationNode.getLocation())
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

        // } catch (PainlessExplainError e) {
        //   throw this.convertToScriptException(e, e.getHeaders($DEFINITION))
        // }
        try {
            TryNode tryNode = new TryNode()
                    .setBlockNode(blockNode)
                    .addCatchNode(new CatchNode()
                            .setDeclarationNode(new DeclarationNode()
                                    .setDeclarationTypeNode(new TypeNode()
                                            .setLocation(blockNode.getLocation())
                                            .setType(PainlessExplainError.class)
                                    )
                                    .setName("painlessExplainError")
                                    .setRequiresDefault(false)
                                    .setLocation(blockNode.getLocation())
                            )
                            .setBlockNode(new BlockNode()
                                    .addStatementNode(new ThrowNode()
                                            .setExpressionNode(new UnboundCallNode()
                                                    .setTypeNode(new TypeNode()
                                                            .setLocation(blockNode.getLocation())
                                                            .setType(ScriptException.class)
                                                    )
                                                    .addArgumentNode(new VariableNode()
                                                            .setTypeNode(new TypeNode()
                                                                    .setLocation(blockNode.getLocation())
                                                                    .setType(ScriptException.class)
                                                            )
                                                            .setLocation(blockNode.getLocation())
                                                            .setName("painlessExplainError")
                                                    )
                                                    .addArgumentNode(new CallNode()
                                                            .setTypeNode(new TypeNode()
                                                                    .setLocation(blockNode.getLocation())
                                                                    .setType(Map.class)
                                                            )
                                                            .setPrefixNode(new VariableNode()
                                                                    .setTypeNode(new TypeNode()
                                                                            .setLocation(blockNode.getLocation())
                                                                            .setType(PainlessExplainError.class)
                                                                    )
                                                                    .setLocation(blockNode.getLocation())
                                                                    .setName("painlessExplainError")
                                                            )
                                                            .setChildNode(new CallSubNode()
                                                                    .setTypeNode(new TypeNode()
                                                                            .setLocation(blockNode.getLocation())
                                                                            .setType(Map.class)
                                                                    )
                                                                    .addArgumentNode(new UnboundFieldNode()
                                                                            .setTypeNode(new TypeNode()
                                                                                    .setLocation(blockNode.getLocation())
                                                                                    .setType(PainlessLookup.class)
                                                                            )
                                                                            .setLocation(blockNode.getLocation())
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
                                                                    .setLocation(blockNode.getLocation())
                                                            )
                                                            .setLocation(blockNode.getLocation())
                                                    )
                                                    .setLocalFunction(new LocalFunction(
                                                                    "convertToScriptException",
                                                                    ScriptException.class,
                                                                    Arrays.asList(Throwable.class, Map.class),
                                                                    true,
                                                                    false
                                                            )
                                                    )
                                                    .setLocation(blockNode.getLocation())
                                            )
                                            .setLocation(blockNode.getLocation())
                                    )
                                    .setLocation(blockNode.getLocation())
                                    .setAllEscape(true)
                                    .setStatementCount(1)
                            )
                            .setLocation(blockNode.getLocation())
                    )
                    .setLocation(blockNode.getLocation());

            for (Class<?> throwable : new Class<?>[] {
                    PainlessError.class, BootstrapMethodError.class, OutOfMemoryError.class, StackOverflowError.class, Exception.class}) {

                String name = throwable.getSimpleName();
                name = Character.toLowerCase(name.charAt(0)) + name.substring(1);

                tryNode.addCatchNode(new CatchNode()
                        .setDeclarationNode(new DeclarationNode()
                                .setDeclarationTypeNode(new TypeNode()
                                        .setLocation(blockNode.getLocation())
                                        .setType(throwable)
                                )
                                .setName(name)
                                .setRequiresDefault(false)
                                .setLocation(blockNode.getLocation())
                        )
                        .setBlockNode(new BlockNode()
                                .addStatementNode(new ThrowNode()
                                        .setExpressionNode(new UnboundCallNode()
                                                .setTypeNode(new TypeNode()
                                                        .setLocation(blockNode.getLocation())
                                                        .setType(ScriptException.class)
                                                )
                                                .addArgumentNode(new VariableNode()
                                                        .setTypeNode(new TypeNode()
                                                                .setLocation(blockNode.getLocation())
                                                                .setType(ScriptException.class)
                                                        )
                                                        .setLocation(blockNode.getLocation())
                                                        .setName(name)
                                                )
                                                .addArgumentNode(new CallNode()
                                                        .setTypeNode(new TypeNode()
                                                                .setLocation(blockNode.getLocation())
                                                                .setType(Map.class)
                                                        )
                                                        .setPrefixNode(new StaticNode()
                                                                .setTypeNode(new TypeNode()
                                                                        .setLocation(blockNode.getLocation())
                                                                        .setType(Collections.class)
                                                                )
                                                                .setLocation(blockNode.getLocation())
                                                        )
                                                        .setChildNode(new CallSubNode()
                                                                .setTypeNode(new TypeNode()
                                                                        .setLocation(blockNode.getLocation())
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
                                                                .setLocation(blockNode.getLocation())
                                                        )
                                                        .setLocation(blockNode.getLocation())
                                                )
                                                .setLocalFunction(new LocalFunction(
                                                                "convertToScriptException",
                                                                ScriptException.class,
                                                                Arrays.asList(Throwable.class, Map.class),
                                                                true,
                                                                false
                                                        )
                                                )
                                                .setLocation(blockNode.getLocation())
                                        )
                                        .setLocation(blockNode.getLocation())
                                )
                                .setLocation(blockNode.getLocation())
                                .setAllEscape(true)
                                .setStatementCount(1)
                        )
                        .setLocation(blockNode.getLocation())
                );
            }

            blockNode = new BlockNode()
                    .addStatementNode(tryNode)
                    .setLocation(blockNode.getLocation())
                    .setAllEscape(blockNode.doAllEscape())
                    .setStatementCount(blockNode.getStatementCount());

            executeFunctionNode.setBlockNode(blockNode);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }

    /*
            if ("execute".equals(name)) {
            methodWriter.mark(endTry);
            methodWriter.goTo(endCatch);
            // This looks like:
            // } catch (PainlessExplainError e) {
            //   throw this.convertToScriptException(e, e.getHeaders($DEFINITION))
            // }
            methodWriter.visitTryCatchBlock(startTry, endTry, startExplainCatch, PAINLESS_EXPLAIN_ERROR_TYPE.getInternalName());
            methodWriter.mark(startExplainCatch);
            methodWriter.loadThis();
            methodWriter.swap();
            methodWriter.dup();
            methodWriter.getStatic(CLASS_TYPE, "$DEFINITION", DEFINITION_TYPE);
            methodWriter.invokeVirtual(PAINLESS_EXPLAIN_ERROR_TYPE, PAINLESS_EXPLAIN_ERROR_GET_HEADERS_METHOD);
            methodWriter.invokeInterface(BASE_INTERFACE_TYPE, CONVERT_TO_SCRIPT_EXCEPTION_METHOD);
            methodWriter.throwException();
            // This looks like:
            // } catch (PainlessError | BootstrapMethodError | OutOfMemoryError | StackOverflowError | Exception e) {
            //   throw this.convertToScriptException(e, e.getHeaders())
            // }
            // We *think* it is ok to catch OutOfMemoryError and StackOverflowError because Painless is stateless
            methodWriter.visitTryCatchBlock(startTry, endTry, startOtherCatch, PAINLESS_ERROR_TYPE.getInternalName());
            methodWriter.visitTryCatchBlock(startTry, endTry, startOtherCatch, BOOTSTRAP_METHOD_ERROR_TYPE.getInternalName());
            methodWriter.visitTryCatchBlock(startTry, endTry, startOtherCatch, OUT_OF_MEMORY_ERROR_TYPE.getInternalName());
            methodWriter.visitTryCatchBlock(startTry, endTry, startOtherCatch, STACK_OVERFLOW_ERROR_TYPE.getInternalName());
            methodWriter.visitTryCatchBlock(startTry, endTry, startOtherCatch, EXCEPTION_TYPE.getInternalName());
            methodWriter.mark(startOtherCatch);
            methodWriter.loadThis();
            methodWriter.swap();
            methodWriter.invokeStatic(COLLECTIONS_TYPE, EMPTY_MAP_METHOD);
            methodWriter.invokeInterface(BASE_INTERFACE_TYPE, CONVERT_TO_SCRIPT_EXCEPTION_METHOD);
            methodWriter.throwException();
            methodWriter.mark(endCatch);
        }
        // TODO: end
     */
    }

    protected DecorateExecutePass() {
        // do nothing
    }
}
