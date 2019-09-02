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

package org.elasticsearch.painless.builder;

import org.elasticsearch.painless.ScriptClassInfo;
import org.elasticsearch.painless.node.ANode;
import org.elasticsearch.painless.node.DParameter;
import org.elasticsearch.painless.node.ELambda;
import org.elasticsearch.painless.node.EVariable;
import org.elasticsearch.painless.node.SCatch;
import org.elasticsearch.painless.node.SDeclaration;
import org.elasticsearch.painless.node.SDo;
import org.elasticsearch.painless.node.SEach;
import org.elasticsearch.painless.node.SFor;
import org.elasticsearch.painless.node.SFunction;
import org.elasticsearch.painless.node.SIf;
import org.elasticsearch.painless.node.SIfElse;
import org.elasticsearch.painless.node.SSource;
import org.elasticsearch.painless.node.STry;
import org.elasticsearch.painless.node.SWhile;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ResolveSymbolsPass implements SemanticPass {

    public interface Enter {
        void enter(ANode node, SymbolTable table);
    }

    private final Map<Class<? extends ANode>, Enter> enters;

    public ResolveSymbolsPass() {
        this(Collections.emptyMap());
    }

    public ResolveSymbolsPass(Map<Class<? extends ANode>, Enter> enters) {
        Map<Class<? extends ANode>, Enter> baseEnters = new HashMap<>();

        baseEnters.put(SSource.class, (node, table) -> {
            SSource source = (SSource)node;
            ScopeTable.FunctionScope scope = table.variableTable.newFunctionScope(node);
            scope.addVariable("#this", true);
            for (ScriptClassInfo.MethodArgument arg : source.scriptClassInfo.getExecuteArguments()) {
                String name = arg.getName();
                if (scope.getVariable(name) != null) {
                    throw node.createError(new IllegalArgumentException("variable [" + name + "] is already defined in the scope"));
                }
                scope.addVariable(name, true);
            }
            for (int get = 0; get < source.scriptClassInfo.getGetMethods().size(); ++get) {
                org.objectweb.asm.commons.Method method = source.scriptClassInfo.getGetMethods().get(get);
                String name = method.getName().substring(3);
                name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
                if (scope.getVariable(name) != null) {
                    throw node.createError(new IllegalArgumentException("variable [" + name + "] is already defined in the scope"));
                }
                scope.addVariable(name, true);
            }
            source.scope = scope;
        });

        baseEnters.put(SFunction.class, (node, table) -> {
            SFunction function = (SFunction)node;
            ScopeTable.FunctionScope scope = table.variableTable.newFunctionScope(node);
            for (ANode parameter : function.children.get(1).children) {
                String parameterName = ((DParameter)parameter).name;
                if (scope.getVariable(parameterName) != null) {
                    throw node.createError(
                            new IllegalArgumentException("variable [" + parameterName + "] is already defined in the scope"));
                }
                scope.addVariable(parameterName, false);
            }
        });

        baseEnters.put(SDeclaration.class, (node, table) -> {
            SDeclaration declaration = (SDeclaration)node;
            ScopeTable.Scope scope = table.variableTable.getScope(declaration);
            String name = declaration.name;
            if (scope.getVariable(name) != null) {
                throw node.createError(new IllegalArgumentException("variable [" + name + "] is already defined in the scope"));
            }
            scope.addVariable(name, true);
        });

        baseEnters.put(SFor.class, (node, table) -> table.variableTable.newLocalScope(node));
        baseEnters.put(SEach.class, (node, table) -> table.variableTable.newLocalScope(node));
        baseEnters.put(SWhile.class, (node, table) -> table.variableTable.newLocalScope(node));
        baseEnters.put(SDo.class, (node, table) -> table.variableTable.newLocalScope(node));
        baseEnters.put(SIf.class, (node, table) -> table.variableTable.newLocalScope(node.children.get(0)));
        baseEnters.put(SIfElse.class, (node, table) -> {
            table.variableTable.newLocalScope(node.children.get(0));
            table.variableTable.newLocalScope(node.children.get(1));
        });
        baseEnters.put(STry.class, (node, table) -> table.variableTable.newLocalScope(node.children.get(0)));
        baseEnters.put(SCatch.class, (node, table) -> table.variableTable.newLocalScope(node));

        baseEnters.put(ELambda.class, (node, table) -> {
            ELambda lambda = (ELambda)node;
            ScopeTable.LambdaScope scope = table.variableTable.newLambdaScope(node);
            for (ANode parameter : lambda.children.get(0).children) {
                String parameterName = ((DParameter)parameter).name;
                if (scope.getVariable(parameterName) != null) {
                    throw node.createError(
                            new IllegalArgumentException("variable [" + parameterName + "] is already defined in the scope"));
                }
                scope.addVariable(parameterName, false);
            }
            lambda.scope = scope;
        });

        baseEnters.put(EVariable.class, (node, table) -> {
            EVariable variable = (EVariable)node;
            if (table.variableTable.getScope(node).getVariable(variable.name) == null) {
                throw node.createError(new IllegalArgumentException("cannot resolve symbol [" + variable.name + "]"));
            }
        });

        baseEnters.putAll(enters);
        this.enters = Collections.unmodifiableMap(baseEnters);
    }

    @Override
    public Object pass(ANode root, Map<String, Object> data) {
        SymbolTable table = (SymbolTable)data.get(SymbolTable.SYMBOL_TABLE);
        visit(root, table);

        return null;
    }

    protected void visit(ANode parent, SymbolTable table) {
        Enter enter = enters.get(parent.getClass());

        if (enter != null) {
            enter.enter(parent, table);
        }

        for (ANode child : parent.children) {
            if (child != null) {
                visit(child, table);
            }
        }
    }
}
