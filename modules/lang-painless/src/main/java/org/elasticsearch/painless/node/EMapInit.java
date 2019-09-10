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
import org.elasticsearch.painless.builder.SymbolTable;
import org.elasticsearch.painless.lookup.PainlessConstructor;
import org.elasticsearch.painless.lookup.PainlessMethod;
import org.elasticsearch.painless.lookup.def;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.elasticsearch.painless.lookup.PainlessLookupUtility.typeToCanonicalTypeName;

/**
 * Represents a map initialization shortcut.
 */
public final class EMapInit extends AExpression {
    private PainlessConstructor constructor = null;
    private PainlessMethod method = null;

    public EMapInit(Location location) {
        super(location);
    }

    @Override
    void analyze(SymbolTable table) {
        if (!read) {
            throw createError(new IllegalArgumentException("Must read from map initializer."));
        }

        if (children.size()%2 == 1) {
            throw createError(new IllegalStateException("illegal tree structure"));
        }

        actual = HashMap.class;

        constructor = table.lookup().lookupPainlessConstructor(actual, 0);

        if (constructor == null) {
            throw createError(new IllegalArgumentException(
                    "constructor [" + typeToCanonicalTypeName(actual) + ", <init>/0] not found"));
        }

        method = table.lookup().lookupPainlessMethod(actual, false, "put", 2);

        if (method == null) {
            throw createError(new IllegalArgumentException("method [" + typeToCanonicalTypeName(actual) + ", put/2] not found"));
        }

        for (int index = 0; index < children.size(); ++index) {
            AExpression expression = (AExpression)children.get(index);

            expression.expected = def.class;
            expression.internal = true;
            expression.analyze(table);
            children.set(index, expression.cast(table));
        }
    }

    @Override
    void write(MethodWriter writer, Globals globals) {
        writer.writeDebugInfo(location);

        writer.newInstance(MethodWriter.getType(actual));
        writer.dup();
        writer.invokeConstructor(
                    Type.getType(constructor.javaConstructor.getDeclaringClass()), Method.getMethod(constructor.javaConstructor));

        for (int index = 0; index < children.size(); ++index) {
            AExpression key = (AExpression)children.get(index++);
            AExpression value = (AExpression)children.get(index);

            writer.dup();
            key.write(writer, globals);
            value.write(writer, globals);
            writer.invokeMethodCall(method);
            writer.pop();
        }
    }

    @Override
    public String toString() {
        List<ANode> keys = new ArrayList<>();
        List<ANode> values = new ArrayList<>();

        for (int index = 0; index < children.size(); ++index) {
            keys.add(children.get(index++));
            values.add(children.get(index));
        }

        return singleLineToString(pairwiseToString(keys, values));
    }
}
