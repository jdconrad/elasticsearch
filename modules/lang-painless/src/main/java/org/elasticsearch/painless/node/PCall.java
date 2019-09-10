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

package org.elasticsearch.painless.node;

import org.elasticsearch.painless.CompilerSettings;
import org.elasticsearch.painless.Globals;
import org.elasticsearch.painless.Locals;
import org.elasticsearch.painless.Location;
import org.elasticsearch.painless.MethodWriter;
import org.elasticsearch.painless.builder.SymbolTable;
import org.elasticsearch.painless.lookup.PainlessMethod;
import org.elasticsearch.painless.lookup.def;

import java.util.Objects;

import static org.elasticsearch.painless.lookup.PainlessLookupUtility.typeToCanonicalTypeName;

/**
 * Represents a method call and defers to a child subnode.
 */
public final class PCall extends AExpression {

    private final String name;
    private final boolean nullSafe;

    public PCall(Location location, String name, boolean nullSafe) {
        super(location);

        this.name = Objects.requireNonNull(name);
        this.nullSafe = nullSafe;
    }

    @Override
    void analyze(SymbolTable table) {
        AExpression bridge = (PPostfixBridge)parent;
        AExpression call;

        if (bridge.actual == def.class) {
            call = new PDefCallInvoke(location, name);
        } else {
            PainlessMethod method = table.lookup().lookupPainlessMethod(
                    bridge.actual, bridge.children.get(0) instanceof EStatic, name, children.size());

            if (method == null) {
                throw createError(new IllegalArgumentException(
                        "method [" + typeToCanonicalTypeName(bridge.actual) + ", " + name + "/" + (children.size()) + "] not found"));
            }

            call = new PCallInvoke(location, method, bridge.actual);
        }

        call.children.addAll(children);

        if (nullSafe) {
            AExpression ns = new PNullSafeCallInvoke(location);
            ns.children.add(call);
            call = ns;
        }

        call.expected = expected;
        call.explicit = explicit;
        call.internal = internal;
        call.analyze(table);
        //replace(call);
        actual = call.actual;
        children.clear();
        children.add(call);
        statement = true;
    }

    @Override
    void write(MethodWriter writer, Globals globals) {
        //throw createError(new IllegalStateException("illegal tree structure"));
        children.get(0).write(writer, globals);
    }

    @Override
    public String toString() {
        return singleLineToStringWithOptionalArgs(children.subList(1, children.size()), children.get(0), name);
    }
}
