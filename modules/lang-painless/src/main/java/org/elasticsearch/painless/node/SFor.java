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
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import static java.util.Collections.emptyList;

/**
 * Represents a for loop.
 */
public final class SFor extends AStatement {

    private boolean continuous = false;

    public SFor(Location location) {
        super(location);
    }

    @Override
    void analyze(SymbolTable table) {
        AExpression condition = (AExpression)children.get(1);
        AExpression afterthought = (AExpression)children.get(2);
        SBlock block = (SBlock)children.get(3);

        if (children.get(0) != null) {
            if (children.get(0) instanceof SDeclBlock) {
                children.get(0).analyze(table);
            } else if (children.get(0) instanceof AExpression) {
                AExpression initializer = (AExpression)children.get(0);

                initializer.read = false;
                initializer.analyze(table);

                if (!initializer.statement) {
                    throw createError(new IllegalArgumentException("Not a statement."));
                }

                initializer.expected = initializer.actual;
                children.set(0, initializer.cast(table));
            } else {
                throw createError(new IllegalStateException("Illegal tree structure."));
            }
        }

        if (condition != null) {
            condition.expected = boolean.class;
            condition.analyze(table);
            children.set(1, condition = condition.cast(table));

            if (condition.constant != null) {
                continuous = (boolean)condition.constant;

                if (!continuous) {
                    throw createError(new IllegalArgumentException("Extraneous for loop."));
                }

                if (block == null) {
                    throw createError(new IllegalArgumentException("For loop has no escape."));
                }
            }
        } else {
            continuous = true;
        }

        if (afterthought != null) {
            afterthought.read = false;
            afterthought.analyze(table);

            if (!afterthought.statement) {
                throw createError(new IllegalArgumentException("Not a statement."));
            }

            afterthought.expected = afterthought.actual;
            children.set(2, afterthought.cast(table));
        }

        if (block != null) {
            block.beginLoop = true;
            block.inLoop = true;

            block.analyze(table);

            if (block.loopEscape && !block.anyContinue) {
                throw createError(new IllegalArgumentException("Extraneous for loop."));
            }

            if (continuous && !block.anyBreak) {
                methodEscape = true;
                allEscape = true;
            }

            block.statementCount = Math.max(1, block.statementCount);
        }

        statementCount = 1;
        loopCounter = table.scopes().getNodeScope(this).getVariable("#loop");
    }

    @Override
    void write(MethodWriter writer, Globals globals) {
        AExpression condition = (AExpression)children.get(1);
        AExpression afterthought = (AExpression)children.get(2);
        SBlock block = (SBlock)children.get(3);

        writer.writeStatementOffset(location);

        Label start = new Label();
        Label begin = afterthought == null ? start : new Label();
        Label end = new Label();

        if (children.get(0) instanceof SDeclBlock) {
            children.get(0).write(writer, globals);
        } else if (children.get(0) instanceof AExpression) {
            AExpression initializer = (AExpression)children.get(0);

            initializer.write(writer, globals);
            writer.writePop(MethodWriter.getType(initializer.expected).getSize());
        }

        writer.mark(start);

        if (condition != null && !continuous) {
            condition.write(writer, globals);
            writer.ifZCmp(Opcodes.IFEQ, end);
        }

        boolean allEscape = false;

        if (block != null) {
            allEscape = block.allEscape;

            int statementCount = Math.max(1, block.statementCount);

            if (afterthought != null) {
                ++statementCount;
            }

            if (loopCounter != null) {
                writer.writeLoopCounter(loopCounter.getSlot(), statementCount, location);
            }

            block.continu = begin;
            block.brake = end;
            block.write(writer, globals);
        } else {
            if (loopCounter != null) {
                writer.writeLoopCounter(loopCounter.getSlot(), 1, location);
            }
        }

        if (afterthought != null) {
            writer.mark(begin);
            afterthought.write(writer, globals);
            writer.writePop(MethodWriter.getType(afterthought.expected).getSize());
        }

        if (afterthought != null || !allEscape) {
            writer.goTo(start);
        }

        writer.mark(end);
    }

    @Override
    public String toString() {
        return multilineToString(emptyList(), children);
    }
}
