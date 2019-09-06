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

import org.elasticsearch.painless.node.ANode;
import org.elasticsearch.painless.node.DTypeClass;
import org.elasticsearch.painless.node.DTypeString;
import org.elasticsearch.painless.node.ELambda;
import org.elasticsearch.painless.node.EVariable;
import org.elasticsearch.painless.node.SCatch;
import org.elasticsearch.painless.node.SDeclBlock;
import org.elasticsearch.painless.node.SDeclaration;
import org.elasticsearch.painless.node.SDo;
import org.elasticsearch.painless.node.SEach;
import org.elasticsearch.painless.node.SFor;
import org.elasticsearch.painless.node.SFunction;
import org.elasticsearch.painless.node.SIf;
import org.elasticsearch.painless.node.SIfElse;
import org.elasticsearch.painless.node.STry;
import org.elasticsearch.painless.node.SWhile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResolveSymbolsPass implements SemanticPass {

    public interface Visitor {
        void visit(ANode node, SymbolTable table, Map<String, Object> data);
    }

    private final Map<Class<? extends ANode>, Visitor> enters;
    private final Map<Class<? extends ANode>, Visitor> exits;

    public ResolveSymbolsPass() {
        this(Collections.emptyMap(), Collections.emptyMap());
    }

    public ResolveSymbolsPass(
            Map<Class<? extends ANode>, Visitor> enters,
            Map<Class<? extends ANode>, Visitor> exits) {

        Map<Class<? extends ANode>, Visitor> baseEnters = buildBaseEnters();
        baseEnters.putAll(enters);
        this.enters = Collections.unmodifiableMap(baseEnters);

        Map<Class<? extends ANode>, Visitor> baseExits = buildBaseExits();
        baseExits.putAll(exits);
        this.exits = Collections.unmodifiableMap(baseExits);
    }

    protected Map<Class<? extends ANode>, Visitor> buildBaseEnters() {
        Map<Class<? extends ANode>, Visitor> baseEnters = new HashMap<>();

        baseEnters.put(SFunction.class, (node, table, data) -> {
            SFunction function = (SFunction)node;
            ScopeTable.FunctionScope functionScope = table.scopeTable.newFunctionScope(function);
            if (function.statik == false) {
                functionScope.addVariable("#this", true);
            }
            // TODO: move this to a validation pass?
            if (function.children.get(3) == null) {
                throw node.createError(new IllegalArgumentException("function [" + function.getKey() + "] cannot have an empty body"));
            }
            ScopeTable.LocalScope localScope = table.scopeTable.newLocalScope(function.children.get(3));
            if (table.compilerSettings.getMaxLoopCounter() > 0) {
                localScope.addVariable("#loop", false);
            }
        });

        baseEnters.put(SDeclaration.class, (node, table, data) -> {
            SDeclaration declaration = (SDeclaration)node;
            ScopeTable.Scope scope = table.scopeTable.getNodeScope(declaration);
            String name = declaration.name;
            if (scope.getVariable(name) != null) {
                throw node.createError(new IllegalArgumentException("variable [" + name + "] is already defined in the scope"));
            }
            scope.addVariable(name, true);
        });

        baseEnters.put(SFor.class, (node, table, data) -> table.scopeTable.newLocalScope(node));
        baseEnters.put(SEach.class, (node, table, data) -> table.scopeTable.newLocalScope(node));
        baseEnters.put(SWhile.class, (node, table, data) -> table.scopeTable.newLocalScope(node));
        baseEnters.put(SDo.class, (node, table, data) -> table.scopeTable.newLocalScope(node));
        baseEnters.put(SIf.class, (node, table, data) -> table.scopeTable.newLocalScope(node.children.get(0)));
        baseEnters.put(SIfElse.class, (node, table, data) -> {
            table.scopeTable.newLocalScope(node.children.get(0));
            table.scopeTable.newLocalScope(node.children.get(1));
        });
        baseEnters.put(STry.class, (node, table, data) -> table.scopeTable.newLocalScope(node.children.get(0)));
        baseEnters.put(SCatch.class, (node, table, data) -> table.scopeTable.newLocalScope(node));

        baseEnters.put(ELambda.class, (node, table, data) -> {
            ELambda lambda = (ELambda)node;
            table.scopeTable.newLambdaScope(lambda);
        });

        baseEnters.put(EVariable.class, (node, table, data) -> {
            EVariable variable = (EVariable)node;
            if (table.scopeTable.getNodeScope(node).getVariable(variable.name) == null) {
                throw node.createError(new IllegalArgumentException("cannot resolve symbol [" + variable.name + "]"));
            }
        });

        baseEnters.put(DTypeString.class, (node, table, data) -> {
            String canonicalTypeName = ((DTypeString)node).type;
            Class<?> type = table.painlessLookup.canonicalTypeNameToType(canonicalTypeName);

            if (type == null) {
                throw node.createError(new IllegalArgumentException("cannot resolve type [" + canonicalTypeName + "]"));
            }

            node.replace(new DTypeClass(node.location, type));
        });

        return baseEnters;
    }

    protected Map<Class<? extends ANode>, Visitor> buildBaseExits() {
        Map<Class<? extends ANode>, Visitor> baseExits = new HashMap<>();

        baseExits.put(SFunction.class, (node, table, data) -> {
            SFunction function = (SFunction)node;
            Class<?> returnType = ((DTypeClass)function.children.get(0)).type;
            SDeclBlock parameters = (SDeclBlock)function.children.get(1);
            List<Class<?>> typeParameters = new ArrayList<>();
            List<String> parameterNames = new ArrayList<>();

            for (ANode child : parameters.children) {
                SDeclaration parameter = (SDeclaration) child;
                typeParameters.add(((DTypeClass)child.children.get(0)).type);
                parameterNames.add(parameter.name);
            }

            table.functionTable.addFunction(function.name, function.internal, returnType, typeParameters, parameterNames);
        });

        return baseExits;
    }

    @Override
    public void pass(ANode root, SymbolTable table, Map<String, Object> data) {
        visit(root, table, data);
    }

    protected void visit(ANode node, SymbolTable table, Map<String, Object> data) {
        Visitor enter = enters.get(node.getClass());

        if (enter != null) {
            enter.visit(node, table, data);
        }

        for (ANode child : node.children) {
            if (child != null) {
                visit(child, table, data);
            }
        }

        Visitor exit = exits.get(node.getClass());

        if (exit != null) {
            exit.visit(node, table, data);
        }
    }
}
