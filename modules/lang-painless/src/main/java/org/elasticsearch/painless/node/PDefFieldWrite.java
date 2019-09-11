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

import org.elasticsearch.painless.DefBootstrap;
import org.elasticsearch.painless.Globals;
import org.elasticsearch.painless.Location;
import org.elasticsearch.painless.MethodWriter;
import org.elasticsearch.painless.Operation;
import org.elasticsearch.painless.builder.SymbolTable;

import java.util.Objects;

/**
 * Represents a field load/store or shortcut on a def type.  (Internal only.)
 */
final class PDefFieldWrite extends AExpression {

    private final String value;

    PDefFieldWrite(Location location, String value) {
        super(location);

        this.value = Objects.requireNonNull(value);
    }

    @Override
    void analyze(SymbolTable table) {
        AExpression rhs = (AExpression)children.get(0);

        if (write == Operation.POST || write == Operation.PRE || write == Operation.COMPOUND) {
            PDefFieldRead dfr = new PDefFieldRead(location, value);
            dfr.write = write;
            dfr.read = read;
            rhs.children.set(0, dfr);
            rhs.explicit = true;
        }

        rhs.analyze(table);

        if (rhs.actual == void.class) {
            throw createError(new IllegalArgumentException("cannot assign [void]"));
        }

        rhs.expected = rhs.actual;
        children.set(0, rhs.cast(table));

        actual = rhs.actual;
    }

    @Override
    void write(MethodWriter writer, Globals globals) {
        writer.writeDebugInfo(location);

        if (write == Operation.POST || write == Operation.PRE || write == Operation.COMPOUND) {
            writer.writeDup(1, 0);
        }

        children.get(0).write(writer, globals);

        if (read && write != Operation.POST) {
            writer.writeDup(MethodWriter.getType(actual).getSize(), 1);
        }

        org.objectweb.asm.Type methodType = org.objectweb.asm.Type.getMethodType(
                org.objectweb.asm.Type.getType(void.class), org.objectweb.asm.Type.getType(Object.class), MethodWriter.getType(actual));
        writer.invokeDefCall(value, methodType, DefBootstrap.STORE);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" + value + "] (" + children.get(0) + ")";
    }
}
