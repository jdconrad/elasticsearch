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
import org.elasticsearch.painless.Locals.Variable;
import org.elasticsearch.painless.Location;
import org.elasticsearch.painless.MethodWriter;
import org.objectweb.asm.Opcodes;

import java.util.Objects;

/**
 * Represents a single variable declaration.
 */
public final class SDeclaration extends AStatement {

    public final String name;
    public final boolean initialize;

    private Variable variable = null;

    public SDeclaration(Location location, String name, boolean initialize) {
        super(location);

        this.name = Objects.requireNonNull(name);
        this.initialize = initialize;
    }

    @Override
    void storeSettings(CompilerSettings settings) {
        if (children.size() > 1 && children.get(1) != null) {
            children.get(1).storeSettings(settings);
        }
    }

    @Override
    void analyze(Locals locals) {
        Class<?> clazz = ((DTypeClass)children.get(0)).type;

        AExpression expression = (AExpression) children.get(1);

        if (expression != null) {
            expression.expected = clazz;
            expression.analyze(locals);
            children.set(1, expression.cast(locals));
        }

        variable = locals.addVariable(location, clazz, name, false);
    }

    @Override
    void write(MethodWriter writer, Globals globals) {
        writer.writeStatementOffset(location);

        if (initialize) {
            if (children.get(1) == null) {
                Class<?> sort = variable.clazz;

                if (sort == void.class || sort == boolean.class || sort == byte.class ||
                        sort == short.class || sort == char.class || sort == int.class) {
                    writer.push(0);
                } else if (sort == long.class) {
                    writer.push(0L);
                } else if (sort == float.class) {
                    writer.push(0F);
                } else if (sort == double.class) {
                    writer.push(0D);
                } else {
                    writer.visitInsn(Opcodes.ACONST_NULL);
                }
            } else {
                children.get(1).write(writer, globals);
            }

            writer.visitVarInsn(MethodWriter.getType(variable.clazz).getOpcode(Opcodes.ISTORE), variable.getSlot());
        }
    }

    @Override
    public String toString() {
        return null;
    }
}
