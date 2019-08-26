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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class VariableTable {

    public static class Variable {

        public final Class<?> type;
        public final String name;
        public final boolean readonly;

        public Variable(Class<?> type, String name, boolean readonly) {
            this.type = type;
            this.name = name;
            this.readonly = readonly;
        }
    }

    public static class Scope {

        protected final Map<String, Variable> variables = new HashMap<>();
        protected final Set<String> used = new HashSet<>();

        protected final Scope parent;

        public Scope(Scope parent) {
            this.parent = parent;
        }

        Variable add(Class<?> type, String name, boolean readonly) {
            Variable variable = new Variable(type, name, readonly);
            variables.put(name, variable);
            return variable;
        }

        Variable get(String name) {
            Variable variable = variables.get(name);

            if (variable == null && parent != null) {
                variable = parent.get(name);
            }

            return variable;
        }

        void markUsed(String name) {
            used.add(name);
        }

        boolean isUsed(String name) {
            return used.contains(name);
        }
    }

    protected Map<ANode, Scope> scopes = new HashMap<>();

    public Scope addScope(ANode node, Scope parent) {
        Scope scope = new Scope(parent);
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
