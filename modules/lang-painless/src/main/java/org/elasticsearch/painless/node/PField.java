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
    void storeSettings(CompilerSettings settings) {
    }

    @Override
    void analyze(Locals locals) {
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
            PainlessField access = locals.getPainlessLookup().lookupPainlessField(
                    bridge.actual, bridge.children.get(0) instanceof EStatic, value);

            if (access == null) {
                PainlessMethod getter;
                PainlessMethod setter;

                getter = locals.getPainlessLookup().lookupPainlessMethod(bridge.actual, false,
                        "get" + Character.toUpperCase(value.charAt(0)) + value.substring(1), 0);

                if (getter == null) {
                    getter = locals.getPainlessLookup().lookupPainlessMethod(bridge.actual, false,
                            "is" + Character.toUpperCase(value.charAt(0)) + value.substring(1), 0);
                }

                setter = locals.getPainlessLookup().lookupPainlessMethod(bridge.actual, false,
                        "set" + Character.toUpperCase(value.charAt(0)) + value.substring(1), 0);

                if (getter != null && write == null || setter != null && write != null) {
                    if (write == null) {
                        field = new PShortcutWrite(
                                location, value, PainlessLookupUtility.typeToCanonicalTypeName(bridge.actual), setter, getter);
                    } else {
                        field = new PShortcutRead(location, value, PainlessLookupUtility.typeToCanonicalTypeName(bridge.actual), getter);
                    }
                } else {
                    EConstant index = new EConstant(location, value);
                    index.analyze(locals);

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

        field.write = write;
        field.read = read;
        field.expected = expected;
        field.explicit = explicit;
        field.internal = internal;
        field.analyze(locals);
        replace(field);
    }

    @Override
    void write(MethodWriter writer, Globals globals) {
        throw createError(new IllegalStateException("illegal tree structure"));
    }

    @Override
    public String toString() {
        if (nullSafe) {
            return singleLineToString("nullSafe", children.get(0), value);
        }
        return singleLineToString(children.get(0), value);
    }
}
