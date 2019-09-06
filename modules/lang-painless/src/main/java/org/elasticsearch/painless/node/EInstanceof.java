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
import org.elasticsearch.painless.lookup.PainlessLookupUtility;

import java.util.Map;

/**
 * Represents {@code instanceof} operator.
 * <p>
 * Unlike java's, this works for primitive types too.
 */
public final class EInstanceof extends AExpression {
    private Class<?> resolvedType;
    private Class<?> expressionType;
    private boolean primitiveExpression;

    public EInstanceof(Location location) {
        super(location);
    }

    @Override
    void storeSettings(CompilerSettings settings) {
        children.get(1).storeSettings(settings);
    }

    public static void enter(ANode node, SymbolTable table, Map<String, Object> data) {
        EInstanceof of = (EInstanceof)node;
        Class<?> clazz = ((DTypeClass)of.children.get(0)).type;

        // map to wrapped type for primitive types
        of.resolvedType = clazz.isPrimitive() ? PainlessLookupUtility.typeToBoxedType(clazz) :
                PainlessLookupUtility.typeToJavaType(clazz);
    }

    public static void exit(ANode node, SymbolTable table, Map<String, Object> data) {
        EInstanceof of = (EInstanceof)node;
        AExpression expression = (AExpression)of.children.get(1);

        Class<?> clazz = ((DTypeClass)of.children.get(0)).type;

        expression.expected = expression.actual;
        of.children.set(0, expression = expression.cast());

        // record if the expression returns a primitive
        of.primitiveExpression = expression.actual.isPrimitive();
        // map to wrapped type for primitive types
        of.expressionType = expression.actual.isPrimitive() ?
                PainlessLookupUtility.typeToBoxedType(expression.actual) :
                PainlessLookupUtility.typeToJavaType(clazz);

        of.actual = boolean.class;
    }

    @Override
    void write(MethodWriter writer, Globals globals) {
        AExpression expression = (AExpression)children.get(0);

        // primitive types
        if (primitiveExpression) {
            // run the expression anyway (who knows what it does)
            expression.write(writer, globals);
            // discard its result
            writer.writePop(MethodWriter.getType(expression.actual).getSize());
            // push our result: its a primitive so it cannot be null.
            writer.push(resolvedType.isAssignableFrom(expressionType));
        } else {
            // ordinary instanceof
            expression.write(writer, globals);
            writer.instanceOf(org.objectweb.asm.Type.getType(resolvedType));
        }
    }

    @Override
    public String toString() {
        return null;
    }
}
