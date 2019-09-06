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
import org.elasticsearch.painless.lookup.def;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

import java.util.Map;

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
    void storeSettings(CompilerSettings settings) {
        for (ANode argument : children) {
            if (argument instanceof AData) {
                continue;
            }

            argument.storeSettings(settings);
        }
    }

    public static void enter(ANode node, SymbolTable table, Map<String, Object> data) {
        ENewObj obj = (ENewObj) node;

        obj.actual = ((DTypeClass) obj.children.get(0)).type;

        int size = obj.children.size() - 1;
        obj.constructor = table.painlessLookup.lookupPainlessConstructor(obj.actual, size);

        if (obj.constructor == null) {
            throw obj.createError(new IllegalArgumentException(
                    "constructor [" + typeToCanonicalTypeName(obj.actual) + ", <init>/" + size + "] not found"));
        }

        obj.statement = true;
    }

    public static void before(ANode node, ANode child, int index, SymbolTable table, Map<String, Object> data) {
        if (index > 0) {
            ENewObj obj = (ENewObj) node;
            AExpression expression = (AExpression)child;

            expression.expected = obj.constructor.typeParameters.get(index - 1);
            expression.internal = true;
        }
    }

    public static void after(ANode node, ANode child, int index, SymbolTable table, Map<String, Object> data) {
        AExpression expression = (AExpression)child;
        node.children.set(index, expression.cast());
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
