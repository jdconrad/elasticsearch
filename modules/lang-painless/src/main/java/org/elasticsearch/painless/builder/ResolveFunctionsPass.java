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
import org.elasticsearch.painless.node.DParameter;
import org.elasticsearch.painless.node.DParameters;
import org.elasticsearch.painless.node.DTypeClass;
import org.elasticsearch.painless.node.DTypeString;
import org.elasticsearch.painless.node.SFunction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResolveFunctionsPass implements SemanticPass {

    public interface Visitor {
        void visit(ANode node, PainlessLookup lookup, SymbolTable table);
    }

    public PainlessLookup lookup;

    protected final Map<Class<? extends ANode>, Visitor> enters;
    protected final Map<Class<? extends ANode>, Visitor> exits;

    public ResolveFunctionsPass(PainlessLookup lookup) {
        this(lookup, Collections.emptyMap(), Collections.emptyMap());
    }

    public ResolveFunctionsPass(PainlessLookup lookup,
            Map<Class<? extends ANode>, Visitor> enters,
            Map<Class<? extends ANode>, Visitor> exits) {

        this.lookup = lookup;

        Map<Class<? extends ANode>, Visitor> baseEnters = buildBaseEnters();
        baseEnters.putAll(enters);
        this.enters = Collections.unmodifiableMap(baseEnters);

        Map<Class<? extends ANode>, Visitor> baseExits = buildBaseExits();
        baseExits.putAll(exits);
        this.exits = Collections.unmodifiableMap(baseExits);
    }

    protected Map<Class<? extends ANode>, Visitor> buildBaseEnters() {
        Map<Class<? extends ANode>, Visitor> baseEnters = new HashMap<>();

        baseEnters.put(DTypeString.class, (node, lookup, table) -> {
            String canonicalTypeName = ((DTypeString)node).type;
            Class<?> type = lookup.canonicalTypeNameToType(canonicalTypeName);

            if (type == null) {
                throw node.createError(new IllegalArgumentException("cannot resolve type [" + canonicalTypeName + "]"));
            }

            node.replace(new DTypeClass(node.location, type));
        });

        return baseEnters;
    }

    protected Map<Class<? extends ANode>, Visitor> buildBaseExits() {
        Map<Class<? extends ANode>, Visitor> baseExits = new HashMap<>();

        baseExits.put(SFunction.class, (node, lookup, table) -> {
            SFunction function = (SFunction)node;
            Class<?> returnType = ((DTypeClass)function.children.get(0)).type;
            DParameters parameters = (DParameters)function.children.get(1);
            List<Class<?>> typeParameters = new ArrayList<>();
            List<String> parameterNames = new ArrayList<>();

            for (ANode child : parameters.children) {
                DParameter parameter = (DParameter)child;
                typeParameters.add(((DTypeClass)child.children.get(0)).type);
                parameterNames.add(parameter.name);
            }

            table.definedFunctions.add(function.name, returnType, typeParameters, parameterNames);
        });

        return baseExits;
    }

    @Override
    public Object pass(ANode root, Map<String, Object> data) {
        SymbolTable table = (SymbolTable)data.get(SymbolTable.SYMBOL_TABLE);
        visit(root, table);

        return null;
    }

    protected void visit(ANode parent, SymbolTable table) {
        Visitor enter = enters.get(parent.getClass());

        if (enter != null) {
            enter.visit(parent, lookup, table);
        }

        for (ANode child : parent.children) {
            if (child != null) {
                visit(child, table);
            }
        }

        Visitor exit = exits.get(parent.getClass());

        if (exit != null) {
            exit.visit(parent, lookup, table);
        }
    }
}
