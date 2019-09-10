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
import org.elasticsearch.painless.node.ELambda;
import org.elasticsearch.painless.node.SFunction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ScopeTable {

    public static class Variable {

        protected final String name;
        protected final boolean readonly;

        protected Class<?> type;
        protected int slot;

        public Variable(String name, boolean readonly) {
            this.name = name;
            this.readonly = readonly;
        }

        public String getName() {
            return name;
        }

        public boolean isReadonly() {
            return readonly;
        }

        protected void setType(Class<?> type) {
            this.type = type;
        }

        public Class<?> getType() {
            return type;
        }

        public int getSlot() {
            return slot;
        }
    }

    public interface Scope {

        Class<?> getReturnType();

        Variable addVariable(String name, boolean readonly);
        Variable updateVariable(String name, Class<?> type);
        Variable getVariable(String name);
    }

    public class FunctionScope implements Scope {

        protected final Map<String, Variable> variables = new HashMap<>();
        protected final Set<String> usedVariables = new HashSet<>();

        protected Class<?> returnType;

        public void setReturnType(Class<?> type) {
            returnType = type;
        }

        @Override
        public Class<?> getReturnType() {
            return returnType;
        }

        @Override
        public Variable addVariable(String name, boolean readonly) {
            Variable variable = new Variable(name, readonly);
            variables.put(name, variable);
            return variable;
        }

        @Override
        public Variable updateVariable(String name, Class<?> type) {
            Variable variable = getVariable(name);

            if (variable != null) {
                variable.setType(type);
            }

            return variable;
        }

        @Override
        public Variable getVariable(String name) {
            Variable variable = variables.get(name);

            if (variable != null) {
                usedVariables.add(name);
            }

            return variables.get(name);
        }

        public Set<String> getUsedVariables() {
            return Collections.unmodifiableSet(usedVariables);
        }
    }

    public class LambdaScope implements Scope {

        protected final ANode node;

        protected final Map<String, Variable> variables = new HashMap<>();
        protected final List<Variable> captures = new ArrayList<>();

        protected Class<?> returnType;

        public LambdaScope(ANode node) {
            this.node = node;
        }

        protected Scope getParent() {
            ANode parent = node;
            Scope scope = null;

            while (scope == null) {
                parent = parent.parent;
                scope = nodeScopes.get(parent);
            }

            return scope;
        }

        public void setReturnType(Class<?> type) {
            returnType = type;
        }

        @Override
        public Class<?> getReturnType() {
            return returnType;
        }

        @Override
        public Variable addVariable(String name, boolean readonly) {
            Variable variable = new Variable(name, readonly);
            variables.put(name, variable);
            return variable;
        }

        @Override
        public Variable updateVariable(String name, Class<?> type) {
            Variable variable = getVariable(name);

            if (variable != null) {
                variable.setType(type);
            }

            return variable;
        }

        @Override
        public Variable getVariable(String name) {
            Variable variable = variables.get(name);

            if (variable == null) {
                variable = getParent().getVariable(name);

                if (captures.contains(variable) == false) {
                    captures.add(variable);
                }
            }

            return variable;
        }

        public List<Variable> captures() {
            return Collections.unmodifiableList(captures);
        }
    }

    public class LocalScope implements Scope {

        protected final ANode node;
        protected final Map<String, Variable> variables = new HashMap<>();

        public LocalScope(ANode node) {
            this.node = node;
        }

        protected Scope getParent() {
            ANode parent = node;
            Scope scope = null;

            while (scope == null) {
                parent = parent.parent;
                scope = nodeScopes.get(parent);
            }

            return scope;
        }

        @Override
        public Class<?> getReturnType() {
            return getParent().getReturnType();
        }

        @Override
        public Variable addVariable(String name, boolean readonly) {
            Variable variable = new Variable(name, readonly);
            variables.put(name, variable);
            return variable;
        }

        @Override
        public Variable updateVariable(String name, Class<?> type) {
            Variable variable = getVariable(name);

            if (variable != null) {
                variable.setType(type);
            }

            return variable;
        }

        @Override
        public Variable getVariable(String name) {
            Variable variable = variables.get(name);

            if (variable == null) {
                variable = getParent().getVariable(name);
            }

            return variable;
        }
    }

    protected Map<String, FunctionScope> functionScopes = new HashMap<>();
    protected Map<ANode, Scope> nodeScopes = new HashMap<>();

    public FunctionScope newFunctionScope(SFunction node) {
        FunctionScope scope = new FunctionScope();
        functionScopes.put(node.getKey(), scope);
        nodeScopes.put(node, scope);
        return scope;
    }

    public LambdaScope newLambdaScope(ELambda node) {
        LambdaScope scope = new LambdaScope(node);
        nodeScopes.put(node, scope);
        return scope;
    }

    public LocalScope newLocalScope(ANode node) {
        LocalScope scope = new LocalScope(node);
        nodeScopes.put(node, scope);
        return scope;
    }

    public FunctionScope getFunctionScope(String key) {
        return functionScopes.get(key);
    }

    public Scope getNodeScope(ANode node) {
        Scope scope = nodeScopes.get(node);

        if (scope == null && node.parent != null) {
            scope = getNodeScope(node.parent);
        }

        return scope;
    }
}
