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

import org.elasticsearch.painless.lookup.PainlessLookup;
import org.elasticsearch.painless.node.ANode;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ResolveExpressionsPass implements SemanticPass {

    public interface Visitor {
        void visit(ANode node, PainlessLookup lookup, SymbolTable table);
    }

    public interface Callback {
        void visit(ANode node, ANode child, PainlessLookup lookup, SymbolTable table);
    }

    public PainlessLookup lookup;

    protected final Map<Class<? extends ANode>, Visitor> enters;
    protected final Map<Class<? extends ANode>, Callback> callbacks;
    protected final Map<Class<? extends ANode>, Visitor> exits;

    public ResolveExpressionsPass(PainlessLookup lookup) {
        this(lookup, Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap());
    }

    public ResolveExpressionsPass(PainlessLookup lookup,
            Map<Class<? extends ANode>, Visitor> enters,
            Map<Class<? extends ANode>, Callback> callbacks,
            Map<Class<? extends ANode>, Visitor> exits) {

        this.lookup = lookup;

        Map<Class<? extends ANode>, Visitor> baseEnters = buildBaseEnters();
        baseEnters.putAll(enters);
        this.enters = Collections.unmodifiableMap(baseEnters);

        Map<Class<? extends ANode>, Callback> baseCallbacks = buildBaseCallbacks();
        baseCallbacks.putAll(callbacks);
        this.callbacks = Collections.unmodifiableMap(baseCallbacks);

        Map<Class<? extends ANode>, Visitor> baseExits = buildBaseExits();
        baseExits.putAll(exits);
        this.exits = Collections.unmodifiableMap(baseExits);
    }

    protected Map<Class<? extends ANode>, Visitor> buildBaseEnters() {
        Map<Class<? extends ANode>, Visitor> baseEnters = new HashMap<>();

        return baseEnters;
    }

    protected Map<Class<? extends ANode>, Callback> buildBaseCallbacks() {
        Map<Class<? extends ANode>, Callback> baseCallbacks = new HashMap<>();

        return baseCallbacks;
    }

    protected Map<Class<? extends ANode>, Visitor> buildBaseExits() {
        Map<Class<? extends ANode>, Visitor> baseExits = new HashMap<>();

        return baseExits;
    }

    @Override
    public Object pass(ANode root, Map<String, Object> data) {
        SymbolTable table = (SymbolTable)data.get(SymbolTable.SYMBOL_TABLE);
        visit(root, table);

        return null;
    }

    protected void visit(ANode node, SymbolTable table) {
        Visitor enter = enters.get(node.getClass());

        if (enter != null) {
            enter.visit(node, lookup, table);
        }

        for (ANode child : node.children) {
            if (child != null) {
                visit(child, table);

                Callback callback = callbacks.get(node.getClass());

                if (callback != null) {
                    callback.visit(node, child, lookup, table);
                }
            }
        }

        Visitor exit = exits.get(node.getClass());

        if (exit != null) {
            exit.visit(node, lookup, table);
        }
    }
}
