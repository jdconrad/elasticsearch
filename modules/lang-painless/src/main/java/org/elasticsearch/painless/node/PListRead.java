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
import org.elasticsearch.painless.WriterConstants;
import org.elasticsearch.painless.builder.SymbolTable;
import org.elasticsearch.painless.lookup.PainlessLookupUtility;
import org.elasticsearch.painless.lookup.PainlessMethod;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import java.util.Objects;

/**
 * Represents a list load/store shortcut.  (Internal only.)
 */
final class PListRead extends AExpression {

    private final Class<?> targetClass;

    private PainlessMethod getter;

    PListRead(Location location, Class<?> targetClass) {
        super(location);

        this.targetClass = Objects.requireNonNull(targetClass);
    }

    @Override
    void analyze(SymbolTable table) {
        String canonicalClassName = PainlessLookupUtility.typeToCanonicalTypeName(targetClass);

        getter = table.lookup().lookupPainlessMethod(targetClass, false, "get", 1);

        if (getter == null) {
            throw createError(new IllegalArgumentException("Illegal list shortcut for type [" + canonicalClassName + "]."));
        }

        if (getter.returnType == void.class || getter.typeParameters.size() != 1 || getter.typeParameters.get(0) != int.class) {
            throw createError(new IllegalArgumentException("Illegal list get shortcut for type [" + canonicalClassName + "]."));
        }

        if (children.isEmpty() == false) {
            AExpression index = (AExpression) children.get(0);

            index.expected = int.class;
            index.analyze(table);
            children.set(0, index.cast(table));
        }

        actual = getter.returnType;
    }

    @Override
    void write(MethodWriter writer, Globals globals) {
        if (children.isEmpty() == false) {
            children.get(0).write(writer, globals);

            Label noFlip = new Label();
            writer.dup();
            writer.ifZCmp(Opcodes.IFGE, noFlip);
            writer.swap();
            writer.dupX1();
            writer.invokeInterface(WriterConstants.COLLECTION_TYPE, WriterConstants.COLLECTION_SIZE);
            writer.visitInsn(Opcodes.IADD);
            writer.mark(noFlip);
        }

        writer.writeDebugInfo(location);
        writer.invokeMethodCall(getter);

        if (getter.returnType == getter.javaMethod.getReturnType()) {
            writer.checkCast(MethodWriter.getType(getter.returnType));
        }

        if (read && write == Operation.POST) {
            writer.writeDup(MethodWriter.getType(actual).getSize(), 2);
        }
    }

    @Override
    public String toString() {
        throw new UnsupportedOperationException("unexpected node");
    }
}
