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
import org.elasticsearch.painless.lookup.PainlessMethod;
import org.elasticsearch.painless.lookup.def;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

import java.util.ArrayList;
import java.util.Map;

import static org.elasticsearch.painless.lookup.PainlessLookupUtility.typeToCanonicalTypeName;

/**
 * Represents a list initialization shortcut.
 */
public final class EListInit extends AExpression {
    private PainlessConstructor constructor = null;
    private PainlessMethod method = null;

    public EListInit(Location location) {
        super(location);
    }

    @Override
    void storeSettings(CompilerSettings settings) {
        for (ANode value : children) {
            value.storeSettings(settings);
        }
    }

    public static void enter(ANode node, SymbolTable table, Map<String, Object> data) {
        EListInit init = (EListInit)node;

        if (!init.read) {
            throw init.createError(new IllegalArgumentException("must read from list initializer"));
        }

        init.actual = ArrayList.class;
        init.constructor = table.painlessLookup.lookupPainlessConstructor(init.actual, 0);

        if (init.constructor == null) {
            throw init.createError(new IllegalArgumentException(
                    "constructor [" + typeToCanonicalTypeName(init.actual) + ", <init>/0] not found"));
        }

        init.method = table.painlessLookup.lookupPainlessMethod(init.actual, false, "add", 1);

        if (init.method == null) {
            throw init.createError(new IllegalArgumentException("method [" + typeToCanonicalTypeName(init.actual) + ", add/1] not found"));
        }
    }

    public static void before(ANode node, ANode child, int index, SymbolTable table, Map<String, Object> data) {
        AExpression expression = (AExpression)child;

        expression.expected = def.class;
        expression.internal = true;
    }

    public static void after(ANode node, ANode child, int index, SymbolTable table, Map<String, Object> data) {
        AExpression expression = (AExpression)child;
        node.children.set(index, expression.cast());
    }

    @Override
    void write(MethodWriter writer, Globals globals) {
        writer.writeDebugInfo(location);

        writer.newInstance(MethodWriter.getType(actual));
        writer.dup();
        writer.invokeConstructor(
                    Type.getType(constructor.javaConstructor.getDeclaringClass()), Method.getMethod(constructor.javaConstructor));

        for (ANode value : children) {
            writer.dup();
            value.write(writer, globals);
            writer.invokeMethodCall(method);
            writer.pop();
        }
    }

    @Override
    public String toString() {
        return singleLineToString(children);
    }
}
