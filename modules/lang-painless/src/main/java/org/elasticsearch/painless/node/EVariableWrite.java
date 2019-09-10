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
import org.elasticsearch.painless.Operation;
import org.objectweb.asm.Opcodes;

import java.util.Objects;

/**
 * Represents a variable load/store.
 */
public final class EVariableWrite extends AExpression {

    public final String name;
    public final Variable variable;

    public EVariableWrite(Location location, String name, Variable variable) {
        super(location);

        this.name = Objects.requireNonNull(name);
        this.variable = Objects.requireNonNull(variable);
    }

    @Override
    void storeSettings(CompilerSettings settings) {
        // do nothing
    }

    @Override
    void analyze(Locals locals) {
        if (write != null && variable.readonly) {
            throw createError(new IllegalArgumentException("Variable [" + variable.name + "] is read-only."));
        }

        actual = variable.clazz;
        statement = true;

        AExpression rhs = (AExpression)children.get(0);

        if (write == Operation.POST || write == Operation.PRE || write == Operation.COMPOUND) {
            EVariableRead vr = new EVariableRead(location, name, variable);
            vr.write = write;
            vr.read = read;
            rhs.children.set(0, vr);
            rhs.explicit = true;
        }

        rhs.expected = actual;
        rhs.analyze(locals);
        children.set(0, rhs.cast(locals));
    }

    @Override
    void write(MethodWriter writer, Globals globals) {
        children.get(0).write(writer, globals);

        if (read && write != Operation.POST) {
            writer.writeDup(MethodWriter.getType(actual).getSize(), 0);
        }

        writer.visitVarInsn(MethodWriter.getType(actual).getOpcode(Opcodes.ISTORE), variable.getSlot());
    }

    @Override
    public String toString() {
        return singleLineToString(name);
    }
}
