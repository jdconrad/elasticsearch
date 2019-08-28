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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class VariableTable {

    public static class Variable {

        public final String name;

        public Variable(String name) {
            this.name = name;
        }
    }

    public interface Scope {

        Variable add(String name);
        Variable get(String name);
    }

    public static class FunctionScope implements Scope {

        protected final Map<String, Variable> variables = new HashMap<>();
        protected final Set<String> used = new HashSet<>();

        @Override
        public Variable add(String name) {
            Variable variable = new Variable(name);
            variables.put(name, variable);
            return variable;
        }

        @Override
        public Variable get(String name) {
            Variable variable = variables.get(name);

            if (variable != null) {
                used.add(name);
            }

            return variables.get(name);
        }

        public Set<String> used() {
            return Collections.unmodifiableSet(used);
        }
    }

    public static class LambdaScope implements Scope {

        protected final Scope parent;

        protected final Map<String, Variable> variables = new HashMap<>();
        protected final Set<String> captures = new HashSet<>();

        public LambdaScope(Scope parent) {
            this.parent = parent;
        }

        @Override
        public Variable add(String name) {
            Variable variable = new Variable(name);
            variables.put(name, variable);
            return variable;
        }

        @Override
        public Variable get(String name) {
            Variable variable = variables.get(name);

            if (variable == null) {
                captures.add(name);
                variable = parent.get(name);
            }

            return variable;
        }

        public Set<String> captures() {
            return Collections.unmodifiableSet(captures);
        }
    }

    public static class LocalScope implements Scope {

        protected final Scope parent;

        protected final Map<String, Variable> variables = new HashMap<>();

        public LocalScope(Scope parent) {
            this.parent = parent;
        }

        @Override
        public Variable add(String name) {
            Variable variable = new Variable(name);
            variables.put(name, variable);
            return variable;
        }

        @Override
        public Variable get(String name) {
            Variable variable = variables.get(name);

            if (variable == null) {
                variable = parent.get(name);
            }

            return variable;
        }
    }

    protected Map<ANode, Scope> scopes = new HashMap<>();

    public FunctionScope newFunctionScope(ANode node) {
        FunctionScope scope = new FunctionScope();
        scopes.put(node, scope);
        return scope;
    }

    public LambdaScope newLambdaScope(ANode node) {
        Scope parent = getScope(node.parent);
        LambdaScope scope = new LambdaScope(parent);
        scopes.put(node, scope);
        return scope;
    }

    public LocalScope newLocalScope(ANode node) {
        Scope parent = getScope(node.parent);
        LocalScope scope = new LocalScope(parent);
        scopes.put(node, scope);
        return scope;
    }

    public Scope getScope(ANode node) {
        Scope scope = scopes.get(node);

        if (scope == null && node.parent != null) {
            scope = getScope(node.parent);
        }

        return scope;
    }
}
