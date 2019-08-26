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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class VariableTable {

    /* --------------------------- */
    /* --------- CLASSES --------- */
    /* --------------------------- */

    public static class Variable {
        public final Class<?> type;
        public final String name;

        public Variable(Class<?> type, String name) {
            this.type = type;
            this.name = name;
        }
    }

    private static class VariableScope {
        protected final VariableScope parent;

        protected final Map<String, Variable> variables;
        protected final List<Variable> ordered;

        protected final Set<String> used;

        protected VariableScope(VariableScope parent) {
            this.parent = parent;

            this.variables = new HashMap<>();
            this.ordered = new ArrayList<>();

            this.used = new HashSet<>();
        }
    }

    /* --------------------------- */
    /* ------ BUILD SCOPES ------- */
    /* --------------------------- */

    protected Map<ANode, VariableScope> scopes;
    protected VariableScope current;

    public VariableTable() {
        scopes = new HashMap<>();
        current = null;
    }

    public void pushScope(boolean top) {
        current = new VariableScope(top ? null : current);
    }

    public void popNodeScope() {
        current = current.parent;
    }

    public void putNodeScope(ANode node) {
        scopes.putIfAbsent(node, current);
    }

    public void setNodeScope(ANode node) {
        current = scopes.get(node);
    }

    public Variable defineVariable(ANode node, Class<?> type, String name, boolean end) {
        Objects.requireNonNull(node);
        Objects.requireNonNull(type);
        Objects.requireNonNull(name);

        VariableScope scope = scopes.get(node);

        if (scope == null) {
            throw node.createError(new IllegalStateException("variable scope not found for node [" + node + "]"));
        }

        Variable variable = null;

        while (scope != null && variable == null) {
            variable = scope.variables.get(name);
            scope = scope.parent;
        }

        if (variable != null) {
            throw node.createError(new IllegalArgumentException("variable [" + name + "] already defined"));
        }

        scope = scopes.get(node);
        variable = new Variable(type, name);
        scope.variables.put(name, variable);

        if (end) {
            scope.ordered.add(variable);
        } else {
            scope.ordered.add(0, variable);
        }

        return variable;
    }

    /* --------------------------- */
    /* --- RETRIEVE VARIABLES ---- */
    /* --------------------------- */

    public Variable getVariable(ANode node, String name) {
        Objects.requireNonNull(node);
        Objects.requireNonNull(name);

        VariableScope scope = scopes.get(node);

        if (scope == null) {
            throw node.createError(new IllegalStateException("variable scope not found for node [" + node + "]"));
        }

        Variable variable = null;

        while (scope != null && variable == null) {
            variable = scope.variables.get(name);
            scope = scope.parent;
        }

        if (variable == null) {
            throw node.createError(new IllegalArgumentException("variable [" + name + "] not defined"));
        }

        return variable;
    }
}
