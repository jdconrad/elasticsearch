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

import org.elasticsearch.painless.builder.ScopeTable.Scope;
import org.elasticsearch.painless.node.ANode;
import org.elasticsearch.painless.node.SDeclaration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ResolveSlotsPass implements SemanticPass {

    public interface Visitor {
        void visit(ANode node, SymbolTable table);
    }

    protected final Map<Class<? extends ANode>, Visitor> enters;

    public ResolveSlotsPass() {
        this(Collections.emptyMap());
    }

    public ResolveSlotsPass(Map<Class<? extends ANode>, Visitor> enters) {
        Map<Class<? extends ANode>, Visitor> baseEnters = buildBaseEnters();
        baseEnters.putAll(enters);
        this.enters = Collections.unmodifiableMap(baseEnters);
    }

    protected Map<Class<? extends ANode>, Visitor> buildBaseEnters() {
        Map<Class<? extends ANode>, Visitor> baseEnters = new HashMap<>();

        baseEnters.put(SDeclaration.class, (node, table) -> {
            SDeclaration declaration = (SDeclaration)node;
            Scope scope = table.scopes().getNodeScope(declaration);
            scope.setVariableSlot(declaration.name);
        });

        return baseEnters;
    }

    public void pass(ANode root, SymbolTable table, Map<String, Object> data) {
        visit(root, table);
    }

    protected void visit(ANode node, SymbolTable table) {
        Visitor enter = enters.get(node.getClass());

        if (enter != null) {
            enter.visit(node, table);
        }

        for (ANode child : node.children) {
            if (child != null) {
                visit(child, table);
            }
        }
    }
}
