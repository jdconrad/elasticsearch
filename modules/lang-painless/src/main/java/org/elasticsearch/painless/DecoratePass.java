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

import org.elasticsearch.painless.ir.ClassNode;
import org.elasticsearch.painless.ir.DeclarationNode;
import org.elasticsearch.painless.ir.FunctionNode;
import org.elasticsearch.painless.ir.StatementNode;
import org.elasticsearch.painless.ir.TypeNode;
import org.elasticsearch.painless.ir.UnboundCallNode;
import org.elasticsearch.painless.symbol.FunctionTable.LocalFunction;
import org.elasticsearch.painless.symbol.ScriptRoot;
import org.objectweb.asm.commons.Method;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DecoratePass {

    public static void pass(ScriptRoot scriptRoot, ClassNode classNode) {
        for (FunctionNode functionNode : classNode.getFunctionsNodes()) {
            if (functionNode.getName().equals("execute")) {
                List<DeclarationNode> removeDeclarationNodes = new ArrayList<>();

                for (StatementNode statementNode : functionNode.getBlockNode().getStatementsNodes()) {
                    if (statementNode instanceof DeclarationNode) {
                        DeclarationNode declarationNode = (DeclarationNode)statementNode;

                        for (int index = 0; index < scriptRoot.getScriptClassInfo().getGetMethods().size(); ++index) {
                            Class<?> getType = scriptRoot.getScriptClassInfo().getGetReturns().get(index);
                            Method getMethod = scriptRoot.getScriptClassInfo().getGetMethods().get(index);
                            String name = getMethod.getName().substring(3);
                            name = Character.toLowerCase(name.charAt(0)) + name.substring(1);

                            if (declarationNode.getName().equals(name)) {
                                if (scriptRoot.getUsedVariables().contains(name)) {
                                    declarationNode.setExpressionNode(new UnboundCallNode()
                                            .setTypeNode(new TypeNode()
                                                    .setLocation(declarationNode.getLocation())
                                                    .setType(getType)
                                            )
                                            .setLocation(declarationNode.getLocation())
                                            .setLocalFunction(new LocalFunction(
                                                    getMethod.getName(), getType, Collections.emptyList(), false, true)
                                            )
                                    );
                                } else {
                                    removeDeclarationNodes.add(declarationNode);
                                }
                            }
                        }
                    }
                }

                functionNode.getBlockNode().getStatementsNodes().removeAll(removeDeclarationNodes);

                break;
            }
        }
    }
}
