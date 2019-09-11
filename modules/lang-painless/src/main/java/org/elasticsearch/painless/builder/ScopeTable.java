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
import org.objectweb.asm.Type;

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

        protected void setSlot(int slot) {
            this.slot = slot;
        }

        public int getSlot() {
            return slot;
        }
    }

    public abstract class Scope {

        public abstract Class<?> getReturnType();

        public abstract Variable addVariable(String name, boolean readonly);
        public abstract Variable setVariableType(String name, Class<?> type);
        public abstract Variable setVariableSlot(String name);
        public abstract Variable getVariable(String name);

        protected abstract int getNextSlot();
    }

    public class FunctionScope extends Scope {

        protected final Map<String, Variable> variables = new HashMap<>();
        protected final Set<String> usedVariables = new HashSet<>();
        protected int nextSlot;

        protected FunctionScope(boolean isStatic) {
            nextSlot = isStatic ? 0 : 1;
        }

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
        public Variable setVariableType(String name, Class<?> type) {
            Variable variable = variables.get(name);

            if (variable != null) {
                variable.setType(type);
            }

            return variable;
        }

        @Override
        public Variable setVariableSlot(String name) {
            Variable variable = variables.get(name);

            if (variable != null) {
                variable.setSlot(nextSlot);
                nextSlot += Type.getType(variable.type).getSize();
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

        protected int getNextSlot() {
            return nextSlot;
        }

        public Set<String> getUsedVariables() {
            return Collections.unmodifiableSet(usedVariables);
        }
    }

    public class LambdaScope extends Scope {

        protected final ANode node;

        protected final Map<String, Variable> variables = new HashMap<>();
        protected final List<Variable> capturedVariables = new ArrayList<>();
        protected int nextSlot = -1;

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
        public Variable setVariableType(String name, Class<?> type) {
            Variable variable = getVariable(name);

            if (variable != null) {
                variable.setType(type);
            }

            return variable;
        }

        @Override
        public Variable setVariableSlot(String name) {
            /*Variable variable = getVariable(name);

            if (variable != null) {
                if (nextSlot == -1) {
                    nextSlot = getParent().getNextSlot();
                }

                variable.setSlot(nextSlot);
                nextSlot += Type.getType(variable.type).getSize();
            }

            return variable;*/
            return null;
        }

        @Override
        public Variable getVariable(String name) {
            Variable variable = variables.get(name);

            if (variable == null) {
                variable = getParent().getVariable(name);

                if (variable != null && capturedVariables.contains(variable) == false) {
                    capturedVariables.add(variable);
                }
            }

            return variable;
        }

        protected int getNextSlot() {
            if (nextSlot == -1) {
                nextSlot = getParent().getNextSlot();
            }

            return nextSlot;
        }

        public List<Variable> getCapturedVariables() {
            return Collections.unmodifiableList(capturedVariables);
        }
    }

    public class LocalScope extends Scope {

        protected final ANode node;

        protected final Map<String, Variable> variables = new HashMap<>();
        protected int nextSlot = -1;

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
        public Variable setVariableType(String name, Class<?> type) {
            Variable variable = getVariable(name);

            if (variable != null) {
                variable.setType(type);
            }

            return variable;
        }

        @Override
        public Variable setVariableSlot(String name) {
            Variable variable = getVariable(name);

            if (variable != null) {
                if (nextSlot == -1) {
                    nextSlot = getParent().getNextSlot();
                }

                variable.setSlot(nextSlot);
                nextSlot += Type.getType(variable.type).getSize();
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

        protected int getNextSlot() {
            if (nextSlot == -1) {
                nextSlot = getParent().getNextSlot();
            }

            return nextSlot;
        }
    }

    protected Map<ANode, Scope> nodeScopes = new HashMap<>();
    protected Map<String, Scope> namedScopes = new HashMap<>();

    public FunctionScope newFunctionScope(SFunction node) {
        FunctionScope scope = new FunctionScope(node.statik);
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

    public Scope getNodeScope(ANode node) {
        Scope scope = nodeScopes.get(node);

        if (scope == null && node.parent != null) {
            scope = getNodeScope(node.parent);
        }

        return scope;
    }

    public void setNamedScope(String name, Scope scope) {
        namedScopes.put(name, scope);
    }

    public Scope getNamedScope(String name) {
        return namedScopes.get(name);
    }
}
