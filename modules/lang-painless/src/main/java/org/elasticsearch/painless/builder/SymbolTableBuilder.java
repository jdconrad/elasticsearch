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
import org.elasticsearch.painless.lookup.PainlessLookup;
import org.elasticsearch.painless.node.ANode;
import org.elasticsearch.painless.node.ECallLocal;
import org.elasticsearch.painless.node.EVariable;
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

public class SymbolTableBuilder {

    public interface Enter {
        void enter(ANode node, SymbolTable table);
    }

    public interface Exit {
        void exit(ANode node, SymbolTable table);
    }

    protected final PainlessLookup lookup;
    private final Map<Class<? extends ANode>, Enter> enters;
    private final Map<Class<? extends ANode>, Exit> exits;

    public SymbolTableBuilder(PainlessLookup lookup) {
        this(lookup, Collections.emptyMap(), Collections.emptyMap());
    }

    public SymbolTableBuilder(PainlessLookup lookup, Map<Class<? extends ANode>, Enter> enters, Map<Class<? extends ANode>, Exit> exits) {
        this.lookup = lookup;

        Map<Class<? extends ANode>, Enter> baseEnters = new HashMap<>();

        baseEnters.put(SSource.class, (node, table) -> {
            SSource source = (SSource)node;
            VariableTable.Scope scope = table.variableTable.addScope(node, null);
            scope.add(Object.class, "this", true);
            for (ScriptClassInfo.MethodArgument arg : source.scriptClassInfo.getExecuteArguments()) {
                scope.add(arg.getClazz(), arg.getName(), true);
            }
            for (int get = 0; get < source.scriptClassInfo.getGetMethods().size(); ++get) {
                Class<?> type = source.scriptClassInfo.getGetReturns().get(get);
                org.objectweb.asm.commons.Method method = source.scriptClassInfo.getGetMethods().get(get);
                String name = method.getName().substring(3);
                name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
                scope.add(type, name, true);
            }
        });

        baseEnters.put(SFunction.class, (node, table) -> {
            SFunction function = (SFunction)node;
            function.generateSignature(lookup);
            if (table.definedFunctions.get(FunctionTable.buildKey(function.name, function.typeParameters.size())) != null) {
                throw function.createError(new IllegalArgumentException("illegal duplicate functions with key " +
                        "[" + FunctionTable.buildKey(function.name, function.typeParameters.size()) + "]"));
            }
            FunctionTable.LocalFunction localFunction =
                    table.definedFunctions.add(function.name, function.returnType, function.typeParameters, function.paramNameStrs);
            VariableTable.Scope scope = table.variableTable.addScope(node, null);
            for (int parameterIndex = 0; parameterIndex < localFunction.typeParameters.size(); ++ parameterIndex) {
                scope.add(localFunction.typeParameters.get(parameterIndex), localFunction.parameterNames.get(0), false);
            }
        });

        baseEnters.put(SDeclaration.class, (node, table) -> {
            SDeclaration declaration = (SDeclaration)node;
            Class<?> type = lookup.canonicalTypeNameToType(declaration.type);
            if (type == null) {
                throw node.createError(new IllegalArgumentException(
                        "type [" + declaration.type + "] not defined for declaration of variable [" + declaration.name + "]"));
            }
            VariableTable.Scope scope = table.variableTable.getScope(declaration);
            scope.add(type, declaration.name, false);
        });

        baseEnters.put(SFor.class, (node, table) -> table.variableTable.addScope(node, table.variableTable.getScope(node)));
        baseEnters.put(SEach.class, (node, table) -> table.variableTable.addScope(node, table.variableTable.getScope(node)));
        baseEnters.put(SWhile.class, (node, table) -> table.variableTable.addScope(node, table.variableTable.getScope(node)));
        baseEnters.put(SDo.class, (node, table) -> table.variableTable.addScope(node, table.variableTable.getScope(node)));
        baseEnters.put(SIf.class, (node, table) -> table.variableTable.addScope(node.children.get(0), table.variableTable.getScope(node)));
        baseEnters.put(SIfElse.class, (node, table) -> {
            VariableTable.Scope parent = table.variableTable.getScope(node);
            table.variableTable.addScope(node.children.get(0), parent);
            table.variableTable.addScope(node.children.get(1), parent);
        });
        baseEnters.put(STry.class, (node, table) -> {
            VariableTable.Scope parent = table.variableTable.getScope(node);
            for (ANode child : node.children) {
                table.variableTable.addScope(child, parent);
            }
        });

        baseEnters.put(ECallLocal.class, (node, table) -> {
            ECallLocal callLocal = (ECallLocal)node;
            String key = FunctionTable.buildKey(callLocal.name, callLocal.children.size());
            if (table.definedFunctions.get(key) != null) {
                table.definedFunctions.markUsed(key);
            }
        });

        baseEnters.put(EVariable.class, (node, table) -> {
            EVariable variable = (EVariable)node;
            table.variableTable.getScope(node).markUsed(variable.name);
        });

        baseEnters.putAll(enters);
        this.enters = Collections.unmodifiableMap(baseEnters);

        Map<Class<? extends ANode>, Exit> baseExits = new HashMap<>();

        baseExits.putAll(exits);
        this.exits = Collections.unmodifiableMap(baseExits);
    }

    public SymbolTable visit(ANode root) {
        SymbolTable table = new SymbolTable();
        visit(root, table);

        return table;
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

        Exit exit = exits.get(parent.getClass());

        if (exit != null) {
            exit.exit(parent, table);
        }
    }
}
