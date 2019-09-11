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
import org.elasticsearch.painless.Operation;
import org.elasticsearch.painless.builder.SymbolTable;
import org.elasticsearch.painless.lookup.PainlessField;
import org.elasticsearch.painless.lookup.PainlessLookupUtility;
import org.objectweb.asm.Type;

import java.lang.reflect.Modifier;
import java.util.Objects;

/**
 * Represents a field load/store.
 */
final class PFieldWrite extends AExpression {

    private final PainlessField field;

    PFieldWrite(Location location, PainlessField field) {
        super(location);

        this.field = Objects.requireNonNull(field);
    }

    @Override
    void analyze(SymbolTable table) {
        if (write != null && Modifier.isFinal(field.javaField.getModifiers())) {
            throw createError(new IllegalArgumentException("Cannot write to read-only field [" + field.javaField.getName() + "] " +
                    "for type [" + PainlessLookupUtility.typeToCanonicalTypeName(field.javaField.getDeclaringClass()) + "]."));
        }

        actual = field.typeParameter;

        AExpression rhs = (AExpression)children.get(0);

        if (write == Operation.POST || write == Operation.PRE || write == Operation.COMPOUND) {
            PFieldRead fr = new PFieldRead(location, field);
            fr.write = write;
            fr.read = read;
            rhs.children.set(0, fr);
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

        if (java.lang.reflect.Modifier.isStatic(field.javaField.getModifiers())) {
            writer.putStatic(Type.getType(
                    field.javaField.getDeclaringClass()), field.javaField.getName(), MethodWriter.getType(field.typeParameter));
        } else {
            writer.putField(Type.getType(
                    field.javaField.getDeclaringClass()), field.javaField.getName(), MethodWriter.getType(field.typeParameter));
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" + field.javaField.getName() + "] (" + children.get(0) + ")";
    }
}
