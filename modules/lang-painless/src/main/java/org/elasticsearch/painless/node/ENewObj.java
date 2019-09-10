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
import org.elasticsearch.painless.builder.SymbolTable;
import org.elasticsearch.painless.lookup.PainlessConstructor;
import org.elasticsearch.painless.lookup.PainlessLookupUtility;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

import static org.elasticsearch.painless.lookup.PainlessLookupUtility.typeToCanonicalTypeName;

/**
 * Represents and object instantiation.
 */
public final class ENewObj extends AExpression {

    private PainlessConstructor constructor;

    public ENewObj(Location location) {
        super(location);
    }

    @Override
    void analyze(SymbolTable table) {
        actual = ((DTypeClass)children.get(0)).type;

        int size = children.size() - 1;
        constructor = table.lookup().lookupPainlessConstructor(actual, size);

        if (constructor == null) {
            throw createError(new IllegalArgumentException(
                    "constructor [" + typeToCanonicalTypeName(actual) + ", <init>/" + size + "] not found"));
        }

        Class<?>[] types = new Class<?>[constructor.typeParameters.size()];
        constructor.typeParameters.toArray(types);

        if (constructor.typeParameters.size() != size) {
            throw createError(new IllegalArgumentException(
                    "When calling constructor on type [" + PainlessLookupUtility.typeToCanonicalTypeName(actual) + "] " +
                    "expected [" + constructor.typeParameters.size() + "] arguments, but found [" + size + "]."));
        }

        for (int argument = 1; argument < size + 1; ++argument) {
            AExpression expression = (AExpression)children.get(argument);

            expression.expected = types[argument - 1];
            expression.internal = true;
            expression.analyze(table);
            children.set(argument, expression.cast(table));
        }

        statement = true;
    }

    @Override
    void write(MethodWriter writer, Globals globals) {
        writer.writeDebugInfo(location);

        writer.newInstance(MethodWriter.getType(actual));

        if (read) {
            writer.dup();
        }

        for (int argument = 1; argument < children.size(); ++argument) {
            children.get(argument).write(writer, globals);
        }

        writer.invokeConstructor(
                    Type.getType(constructor.javaConstructor.getDeclaringClass()), Method.getMethod(constructor.javaConstructor));
    }

    @Override
    public String toString() {
        return null;
    }
}
