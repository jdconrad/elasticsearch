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
import org.elasticsearch.painless.node.EUsed;
import org.elasticsearch.painless.node.SFunction;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ResolveUsedPass {

    public interface Visitor {
        void visit(ANode node, SymbolTable table, Map<String, Set<String>> used);
    }
    
    protected final Map<Class<? extends ANode>, Visitor> enters;

    public ResolveUsedPass() {
        this(Collections.emptyMap());
    }

    public ResolveUsedPass(Map<Class<? extends ANode>, Visitor> enters) {
        Map<Class<? extends ANode>, Visitor> baseEnters = buildBaseEnters();
        baseEnters.putAll(enters);
        this.enters = Collections.unmodifiableMap(baseEnters);
    }

    protected Map<Class<? extends ANode>, Visitor> buildBaseEnters() {
        Map<Class<? extends ANode>, Visitor> baseEnters = new HashMap<>();

        baseEnters.put(SFunction.class, (node, table, used) -> {
            SFunction function = (SFunction)node;
            ScopeTable.FunctionScope scope = (ScopeTable.FunctionScope)table.scopeTable.getNodeScope(node);

            String key = function.getKey();
            used.putIfAbsent(key, new HashSet<>());
            used.get(key).addAll(scope.usedVariables);
        });

        baseEnters.put(EUsed.class, (node, table, used) -> {
            EUsed usedNode = (EUsed)node;

            ScopeTable.FunctionScope scope = table.scopeTable.getFunctionScope(usedNode.key);
            usedNode.used = scope.getUsedVariables().contains(usedNode.name);
        });

        return baseEnters;
    }

    public Object pass(ANode root, SymbolTable table) {
        Map<String, Set<String>> used = new HashMap<>();
        visit(root, table, used);

        return used;
    }

    protected void visit(ANode node, SymbolTable table, Map<String, Set<String>> used) {
        Visitor enter = enters.get(node.getClass());

        if (enter != null) {
            enter.visit(node, table, used);
        }

        for (ANode child : node.children) {
            if (child != null) {
                visit(child, table, used);
            }
        }
    }
}
