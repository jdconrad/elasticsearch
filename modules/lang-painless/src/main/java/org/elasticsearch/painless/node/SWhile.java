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
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import java.util.Set;

/**
 * Represents a while loop.
 */
public final class SWhile extends AStatement {

    private boolean continuous = false;

    public SWhile(Location location) {
        super(location);
    }

    @Override
    void storeSettings(CompilerSettings settings) {
        children.get(0).storeSettings(settings);

        if (children.get(1) != null) {
            children.get(1).storeSettings(settings);
        }
    }

    @Override
    void extractVariables(Set<String> variables) {
        children.get(0).extractVariables(variables);

        if (children.get(1) != null) {
            children.get(1).extractVariables(variables);
        }
    }

    @Override
    void analyze(Locals locals) {
        AExpression condition = (AExpression)children.get(0);
        AStatement block = (AStatement)children.get(1);

        locals = Locals.newLocalScope(locals);

        condition.expected = boolean.class;
        condition.analyze(locals);
        children.set(0, condition = condition.cast(locals));

        if (condition.constant != null) {
            continuous = (boolean)condition.constant;

            if (!continuous) {
                throw createError(new IllegalArgumentException("Extraneous while loop."));
            }

            if (block == null) {
                throw createError(new IllegalArgumentException("While loop has no escape."));
            }
        }

        if (block != null) {
            block.beginLoop = true;
            block.inLoop = true;

            block.analyze(locals);

            if (block.loopEscape && !block.anyContinue) {
                throw createError(new IllegalArgumentException("Extraneous while loop."));
            }

            if (continuous && !block.anyBreak) {
                methodEscape = true;
                allEscape = true;
            }

            block.statementCount = Math.max(1, block.statementCount);
        }

        statementCount = 1;

        if (locals.hasVariable(Locals.LOOP)) {
            loopCounter = locals.getVariable(location, Locals.LOOP);
        }
    }

    @Override
    void write(MethodWriter writer, Globals globals) {
        AExpression condition = (AExpression)children.get(0);
        AStatement block = (AStatement)children.get(1);

        writer.writeStatementOffset(location);

        Label begin = new Label();
        Label end = new Label();

        writer.mark(begin);

        if (!continuous) {
            condition.write(writer, globals);
            writer.ifZCmp(Opcodes.IFEQ, end);
        }

        if (block != null) {
            if (loopCounter != null) {
                writer.writeLoopCounter(loopCounter.getSlot(), Math.max(1, block.statementCount), location);
            }

            block.continu = begin;
            block.brake = end;
            block.write(writer, globals);
        } else {
            if (loopCounter != null) {
                writer.writeLoopCounter(loopCounter.getSlot(), 1, location);
            }
        }

        if (block == null || !block.allEscape) {
            writer.goTo(begin);
        }

        writer.mark(end);
    }

    @Override
    public String toString() {
        return singleLineToString(children.get(0), children.get(1));
    }
}
