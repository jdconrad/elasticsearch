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
import org.elasticsearch.painless.lookup.PainlessLookupUtility;
import org.elasticsearch.painless.lookup.PainlessMethod;

import java.util.Objects;

/**
 * Represents a map load/store shortcut. (Internal only.)
 */
final class PMapRead extends AExpression {

    private final Class<?> targetClass;

    private PainlessMethod getter;

    PMapRead(Location location, Class<?> targetClass) {
        super(location);

        this.targetClass = Objects.requireNonNull(targetClass);
    }

    @Override
    void analyze(SymbolTable table) {
        String canonicalClassName = PainlessLookupUtility.typeToCanonicalTypeName(targetClass);

        getter = table.lookup().lookupPainlessMethod(targetClass, false, "get", 1);

        if (getter == null) {
            throw createError(new IllegalArgumentException("Illegal map shortcut for type [" + canonicalClassName + "]."));
        }

        if (getter.returnType == void.class || getter.typeParameters.size() != 1) {
            throw createError(new IllegalArgumentException("Illegal map get shortcut for type [" + canonicalClassName + "]."));
        }

        if (children.get(0) != null) {
            AExpression index = (AExpression) children.get(0);
            index.expected = getter.typeParameters.get(0);
            index.analyze(table);
            children.set(0, index.cast(table));
        }

        actual = getter.returnType;
    }

    @Override
    void write(MethodWriter writer, Globals globals) {
        if (children.get(0) != null) {
            children.get(0).write(writer, globals);
        }

        writer.writeDebugInfo(location);
        writer.invokeMethodCall(getter);

        if (getter.returnType != getter.javaMethod.getReturnType()) {
            writer.checkCast(MethodWriter.getType(getter.returnType));
        }

        if (read && write == Operation.POST) {
            writer.writeDup(MethodWriter.getType(actual).getSize(), 2);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + children.get(0) + ")";
    }
}
