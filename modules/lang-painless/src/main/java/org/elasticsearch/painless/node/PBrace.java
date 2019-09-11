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

import org.elasticsearch.painless.Globals;
import org.elasticsearch.painless.Location;
import org.elasticsearch.painless.MethodWriter;
import org.elasticsearch.painless.builder.SymbolTable;
import org.elasticsearch.painless.lookup.PainlessLookupUtility;
import org.elasticsearch.painless.lookup.def;

import java.util.List;
import java.util.Map;

/**
 * Represents an array load/store and defers to a child subnode.
 */
public final class PBrace extends AExpression {

    public PBrace(Location location) {
        super(location);
    }

    @Override
    void analyze(SymbolTable table) {
        PPostfixBridge bridge = (PPostfixBridge)parent;
        AExpression brace;

        if (bridge.actual.isArray()) {
            if (write == null) {
                brace = new PArrayRead(location, bridge.actual);
            } else {
                brace = new PArrayWrite(location, bridge.actual);
            }
        } else if (bridge.actual == def.class) {
            if (write == null) {
                brace = new PDefArrayRead(location);
            } else {
                brace = new PDefArrayWrite(location);
            }
        } else if (Map.class.isAssignableFrom(bridge.actual)) {
            if (write == null) {
                brace = new PMapRead(location, bridge.actual);
            } else {
                brace = new PMapWrite(location, bridge.actual);
            }
        } else if (List.class.isAssignableFrom(bridge.actual)) {
            if (write == null) {
                brace = new PListRead(location, bridge.actual);
            } else {
                brace = new PListWrite(location, bridge.actual);
            }
        } else {
            throw createError(new IllegalArgumentException("Illegal array access on type " +
                    "[" + PainlessLookupUtility.typeToCanonicalTypeName(bridge.actual) + "]."));
        }

        brace.children.add(children.get(0));

        if (write != null) {
            brace.children.add(children.get(1));
        }

        brace.write = write;
        brace.read = read;
        brace.expected = expected;
        brace.explicit = explicit;
        brace.internal = internal;
        brace.analyze(table);
        //replace(brace);
        actual = brace.actual;
        children.clear();
        children.add(brace);
    }

    @Override
    void write(MethodWriter writer, Globals globals) {
        //throw createError(new IllegalStateException("illegal tree structure"));
        children.get(0).write(writer, globals);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " (" + children.get(0) + ") (" + children.get(1) + ")";
    }
}
