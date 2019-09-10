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
import org.elasticsearch.painless.Operation;
import org.elasticsearch.painless.builder.SymbolTable;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import java.util.Objects;

/**
 * Represents a boolean expression.
 */
public final class EBool extends AExpression {

    private final Operation operation;

    public EBool(Location location, Operation operation) {
        super(location);

        this.operation = Objects.requireNonNull(operation);
    }

    @Override
    void analyze(SymbolTable table) {
        AExpression left = (AExpression)children.get(0);
        AExpression right = (AExpression)children.get(1);

        left.expected = boolean.class;
        left.analyze(table);
        children.set(0, left.cast(table));

        right.expected = boolean.class;
        right.analyze(table);
        children.set(1, right.cast(table));

        actual = boolean.class;
    }

    @Override
    void write(MethodWriter writer, Globals globals) {
        AExpression left = (AExpression)children.get(0);
        AExpression right = (AExpression)children.get(1);

        if (operation == Operation.AND) {
            Label fals = new Label();
            Label end = new Label();

            left.write(writer, globals);
            writer.ifZCmp(Opcodes.IFEQ, fals);
            right.write(writer, globals);
            writer.ifZCmp(Opcodes.IFEQ, fals);

            writer.push(true);
            writer.goTo(end);
            writer.mark(fals);
            writer.push(false);
            writer.mark(end);
        } else if (operation == Operation.OR) {
            Label tru = new Label();
            Label fals = new Label();
            Label end = new Label();

            left.write(writer, globals);
            writer.ifZCmp(Opcodes.IFNE, tru);
            right.write(writer, globals);
            writer.ifZCmp(Opcodes.IFEQ, fals);

            writer.mark(tru);
            writer.push(true);
            writer.goTo(end);
            writer.mark(fals);
            writer.push(false);
            writer.mark(end);
        } else {
            throw createError(new IllegalStateException("Illegal tree structure."));
        }
    }

    @Override
    public String toString() {
        return singleLineToString(children.get(0), operation.symbol, children.get(1));
    }
}
