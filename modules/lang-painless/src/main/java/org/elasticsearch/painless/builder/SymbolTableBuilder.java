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
import org.elasticsearch.painless.node.SFunction;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SymbolTableBuilder {

    public interface SymbolTableVisitor {
        void buildSymbolTable(ANode node, SymbolTable table);
    }

    protected final PainlessLookup lookup;
    private final Map<Class<? extends ANode>, SymbolTableVisitor> visitors;

    public SymbolTableBuilder(PainlessLookup lookup) {
        this(lookup, Collections.emptyMap());
    }

    public SymbolTableBuilder(PainlessLookup lookup, Map<Class<? extends ANode>, SymbolTableVisitor> visitors) {
        this.lookup = lookup;

        Map<Class<? extends ANode>, SymbolTableVisitor> base = new HashMap<>();

        base.put(SFunction.class, (node, table) -> {
                    SFunction function = (SFunction)node;
                    function.generateSignature(lookup);
                    if (table.addLocalFunction(function.name, function.returnType, function.typeParameters) != null) {
                        throw function.createError(new IllegalArgumentException("illegal duplicate functions with key [" +
                                SymbolTable.LocalFunction.buildKey(function.name, function.typeParameters.size()) + "]"));
                    }
                });

        base.putAll(visitors);

        this.visitors = Collections.unmodifiableMap(base);
    }

    public SymbolTable visit(ANode root) {
        SymbolTable table = new SymbolTable();
        visit(root, table);

        return table;
    }

    protected void visit(ANode parent, SymbolTable table) {
        SymbolTableVisitor visitor = visitors.get(parent.getClass());

        if (visitor != null) {
            visitor.buildSymbolTable(parent, table);
        }

        for (ANode child : parent.children) {
            if (child != null) {
                visit(child, table);
            }
        }
    }
}
