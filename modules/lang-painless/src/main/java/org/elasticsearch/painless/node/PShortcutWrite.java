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
import org.elasticsearch.painless.Operation;
import org.elasticsearch.painless.builder.SymbolTable;
import org.elasticsearch.painless.lookup.PainlessMethod;

import java.util.Objects;

/**
 * Represents a field load/store shortcut.  (Internal only.)
 */
final class PShortcutWrite extends AExpression {

    private final String value;
    private final String type;
    private final PainlessMethod setter;
    private final PainlessMethod getter;

    PShortcutWrite(Location location, String value, String type, PainlessMethod setter, PainlessMethod getter) {
        super(location);

        this.value = Objects.requireNonNull(value);
        this.type = Objects.requireNonNull(type);
        this.setter = Objects.requireNonNull(setter);
        this.getter = getter;
    }

    @Override
    void analyze(SymbolTable table) {
        if (setter.returnType != void.class || setter.typeParameters.size() != 1) {
            throw createError(new IllegalArgumentException(
                    "Illegal set shortcut on field [" + value + "] for type [" + type + "]."));
        }

        actual = setter.typeParameters.get(0);

        AExpression rhs = (AExpression)children.get(0);

        if (write == Operation.POST || write == Operation.PRE || write == Operation.COMPOUND) {
            if (getter == null) {
                throw createError(new IllegalArgumentException(
                        "Illegal compound shortcut on field [" + value + "] for type [" + type + "]."));
            }

            PShortcutRead sr = new PShortcutRead(location, value, type, getter);
            sr.write = write;
            sr.read = read;
            rhs.children.set(0, sr);
            rhs.explicit = true;
        }

        rhs.expected = actual;
        rhs.analyze(table);
        children.set(0, rhs.cast(table));
    }

    @Override
    void write(MethodWriter writer, Globals globals) {
        if (write == Operation.POST || write == Operation.PRE || write == Operation.COMPOUND) {
            writer.writeDup(1, 0);
        }

        children.get(0).write(writer, globals);

        if (read && write != Operation.POST) {
            writer.writeDup(MethodWriter.getType(actual).getSize(), 1);
        }

        writer.writeDebugInfo(location);
        writer.invokeMethodCall(setter);
        writer.writePop(MethodWriter.getType(setter.returnType).getSize());
    }

    @Override
    public String toString() {
        throw new UnsupportedOperationException("unexpected node");
    }
}
