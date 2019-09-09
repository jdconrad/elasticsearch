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
import org.elasticsearch.painless.lookup.PainlessLookupUtility;
import org.elasticsearch.painless.lookup.PainlessMethod;

import java.util.Objects;

/**
 * Represents a map load/store shortcut. (Internal only.)
 */
final class PMapWrite extends AExpression {

    private final Class<?> targetClass;

    private PainlessMethod setter;

    PMapWrite(Location location, Class<?> targetClass) {
        super(location);

        this.targetClass = Objects.requireNonNull(targetClass);
    }

    @Override
    void storeSettings(CompilerSettings settings) {
        throw createError(new IllegalStateException("illegal tree structure"));
    }

    @Override
    void analyze(Locals locals) {
        String canonicalClassName = PainlessLookupUtility.typeToCanonicalTypeName(targetClass);

        setter = locals.getPainlessLookup().lookupPainlessMethod(targetClass, false, "put", 2);

        if (setter == null) {
            throw createError(new IllegalArgumentException("Illegal map shortcut for type [" + canonicalClassName + "]."));
        }

        if (setter.typeParameters.size() != 2) {
            throw createError(new IllegalArgumentException("Illegal map set shortcut for type [" + canonicalClassName + "]."));
        }

        AExpression index = (AExpression)children.get(0);
        index.expected = setter.typeParameters.get(0);
        index.analyze(locals);
        children.set(0, index.cast(locals));

        AExpression rhs = (AExpression)children.get(1);

        if (write == Operation.POST || write == Operation.PRE || write == Operation.COMPOUND) {
            PMapRead mr = new PMapRead(location, targetClass);
            mr.write = write;
            mr.read = read;
            rhs.children.set(0, mr);
            rhs.explicit = true;
        }

        rhs.analyze(locals);
        rhs.expected = rhs.actual;
        children.set(1, rhs.cast(locals));

        actual = setter.typeParameters.get(1);
    }

    @Override
    void write(MethodWriter writer, Globals globals) {
        children.get(0).write(writer, globals);

        if (write == Operation.POST || write == Operation.PRE || write == Operation.COMPOUND) {
            writer.writeDup(2, 0);
        }

        children.get(1).write(writer, globals);

        if (read && write != Operation.POST) {
            writer.writeDup(MethodWriter.getType(actual).getSize(), 2);
        }

        writer.writeDebugInfo(location);
        writer.writeDebugInfo(location);
        writer.invokeMethodCall(setter);
        writer.writePop(MethodWriter.getType(setter.returnType).getSize());
    }

    @Override
    public String toString() {
        throw new UnsupportedOperationException("unexpected node");
    }
}
