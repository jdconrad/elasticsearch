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
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import java.util.Objects;

/**
 * Represents an array load/store.
 */
final class PArrayRead extends AExpression {

    private final Class<?> type;

    PArrayRead(Location location, Class<?> type) {
        super(location);

        this.type = Objects.requireNonNull(type);
    }

    @Override
    void storeSettings(CompilerSettings settings) {
        throw createError(new IllegalStateException("illegal tree structure"));
    }

    @Override
    void analyze(Locals locals) {
        if (children.get(0) != null) {
            AExpression index = (AExpression)children.get(0);
            index.expected = int.class;
            index.analyze(locals);
            children.set(0, index.cast(locals));
        }

        actual = type.getComponentType();
    }

    @Override
    void write(MethodWriter writer, Globals globals) {
        if (children.get(0) != null) {
            children.get(0).write(writer, globals);

            Label noFlip = new Label();
            writer.dup();
            writer.ifZCmp(Opcodes.IFGE, noFlip);
            writer.swap();
            writer.dupX1();
            writer.arrayLength();
            writer.visitInsn(Opcodes.IADD);
            writer.mark(noFlip);
        }

        writer.writeDebugInfo(location);
        writer.arrayLoad(MethodWriter.getType(actual));

        if (read && write == Operation.POST) {
            writer.writeDup(MethodWriter.getType(actual).getSize(), 2);
        }
    }

    @Override
    public String toString() {
        throw new UnsupportedOperationException("unexpected node");
    }
}
