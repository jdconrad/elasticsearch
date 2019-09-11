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
import org.elasticsearch.painless.lookup.PainlessField;
import org.elasticsearch.painless.lookup.PainlessLookupUtility;
import org.elasticsearch.painless.lookup.PainlessMethod;
import org.elasticsearch.painless.lookup.def;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.elasticsearch.painless.lookup.PainlessLookupUtility.typeToCanonicalTypeName;

/**
 * Represents a field load/store and defers to a child subnode.
 */
public final class PField extends AExpression {

    private final boolean nullSafe;
    private final String value;

    public PField(Location location, boolean nullSafe, String value) {
        super(location);

        this.nullSafe = nullSafe;
        this.value = Objects.requireNonNull(value);
    }

    @Override
    void analyze(SymbolTable table) {
        PPostfixBridge bridge = (PPostfixBridge)parent;
        AExpression field = null;

        if (bridge.actual.isArray()) {
            field = new PArrayLength(location, PainlessLookupUtility.typeToCanonicalTypeName(bridge.actual), value);
        } else if (bridge.actual == def.class) {
            if (write == null) {
                field = new PDefFieldRead(location, value);
            } else {
                field = new PDefFieldWrite(location, value);
            }
        } else {
            PainlessField access = table.lookup().lookupPainlessField(
                    bridge.actual, bridge.children.get(0) instanceof EStatic, value);

            if (access == null) {
                PainlessMethod getter;
                PainlessMethod setter;

                getter = table.lookup().lookupPainlessMethod(bridge.actual, false,
                        "get" + Character.toUpperCase(value.charAt(0)) + value.substring(1), 0);

                if (getter == null) {
                    getter = table.lookup().lookupPainlessMethod(bridge.actual, false,
                            "is" + Character.toUpperCase(value.charAt(0)) + value.substring(1), 0);
                }

                setter = table.lookup().lookupPainlessMethod(bridge.actual, false,
                        "set" + Character.toUpperCase(value.charAt(0)) + value.substring(1), 0);

                if (getter != null && write == null || setter != null && write != null) {
                    if (write == null) {
                        field = new PShortcutRead(location, value, PainlessLookupUtility.typeToCanonicalTypeName(bridge.actual), getter);
                    } else {
                        field = new PShortcutWrite(
                                location, value, PainlessLookupUtility.typeToCanonicalTypeName(bridge.actual), setter, getter);
                    }
                } else {
                    EConstant index = new EConstant(location, value);
                    index.analyze(table);

                    if (Map.class.isAssignableFrom(bridge.actual)) {
                        if (write == null) {
                            field = new PMapRead(location, bridge.actual);
                        } else {
                            field = new PMapWrite(location, bridge.actual);
                        }
                    }

                    if (List.class.isAssignableFrom(bridge.actual)) {
                        if (write == null) {
                            field = new PListRead(location, bridge.actual);
                        } else {
                            field = new PListWrite(location, bridge.actual);
                        }
                    }

                    if (field != null) {
                        field.children.add(index);
                    }
                }

                if (field == null) {
                    throw createError(new IllegalArgumentException(
                            "field [" + typeToCanonicalTypeName(bridge.actual) + ", " + value + "] not found"));
                }
            } else {
                if (write == null) {
                    field = new PFieldRead(location, access);
                } else {
                    field = new PFieldWrite(location, access);
                }
            }
        }

        if (nullSafe) {
            AExpression ns = new PNullSafeCallInvoke(location);
            ns.children.add(field);
            field = ns;
        }

        if (write != null) {
            field.children.add(children.get(0));
        }

        field.write = write;
        field.read = read;
        field.expected = expected;
        field.explicit = explicit;
        field.internal = internal;
        field.analyze(table);
        //replace(field);
        children.clear();
        children.add(field);
        actual = field.actual;
    }

    @Override
    void write(MethodWriter writer, Globals globals) {
        //throw createError(new IllegalStateException("illegal tree structure"));
        children.get(0).write(writer, globals);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" + value + "]";
    }
}
