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
import org.elasticsearch.painless.lookup.PainlessMethod;

import java.util.Objects;

/**
 * Represents a field load/store shortcut.  (Internal only.)
 */
final class PShortcutRead extends AExpression {

    private final String value;
    private final String type;
    private final PainlessMethod getter;

    PShortcutRead(Location location, String value, String type, PainlessMethod getter) {
        super(location);

        this.value = Objects.requireNonNull(value);
        this.type = Objects.requireNonNull(type);
        this.getter = Objects.requireNonNull(getter);
    }

    @Override
    void analyze(SymbolTable table) {
        if (getter.returnType == void.class || !getter.typeParameters.isEmpty()) {
            throw createError(new IllegalArgumentException(
                    "Illegal get shortcut on field [" + value + "] for type [" + type + "]."));
        }

        actual = getter.returnType;
    }

    @Override
    void write(MethodWriter writer, Globals globals) {
        writer.writeDebugInfo(location);
        writer.invokeMethodCall(getter);

        if (!getter.returnType.equals(getter.javaMethod.getReturnType())) {
            writer.checkCast(MethodWriter.getType(getter.returnType));
        }

        if (read && write == Operation.POST) {
            writer.writeDup(MethodWriter.getType(actual).getSize(), 1);
        }
    }

    @Override
    public String toString() {
        throw new UnsupportedOperationException("unexpected node");
    }
}
