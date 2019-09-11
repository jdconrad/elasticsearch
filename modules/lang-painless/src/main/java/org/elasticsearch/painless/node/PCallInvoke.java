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
import org.elasticsearch.painless.lookup.PainlessMethod;

import java.util.Objects;

/**
 * Represents a method call.
 */
final class PCallInvoke extends AExpression {

    private final PainlessMethod method;
    private final Class<?> box;

    PCallInvoke(Location location, PainlessMethod method, Class<?> box) {
        super(location);

        this.method = Objects.requireNonNull(method);
        this.box = box;
    }

    @Override
    void analyze(SymbolTable table) {
        for (int argument = 0; argument < children.size(); ++argument) {
            AExpression expression = (AExpression)children.get(argument);

            expression.expected = method.typeParameters.get(argument);
            expression.internal = true;
            expression.analyze(table);
            children.set(argument, expression.cast(table));
        }

        statement = true;
        actual = method.returnType;
    }

    @Override
    void write(MethodWriter writer, Globals globals) {
        writer.writeDebugInfo(location);

        if (box.isPrimitive()) {
            writer.box(MethodWriter.getType(box));
        }

        for (ANode argument : children) {
            argument.write(writer, globals);
        }

        writer.invokeMethodCall(method);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder
                .append(getClass().getSimpleName())
                .append(" [")
                .append(method.javaMethod.getName())
                .append("]");

        for (ANode child : children) {
            builder.append(" (")
                    .append(child)
                    .append(")");
        }

        return builder.toString();
    }
}
