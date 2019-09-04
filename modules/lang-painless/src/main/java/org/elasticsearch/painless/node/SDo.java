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

/**
 * Represents a do-while loop.
 */
public final class SDo extends AStatement {

    private boolean continuous = false;

    public SDo(Location location) {
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
    void analyze(Locals locals) {
        AExpression condition = (AExpression)children.get(0);
        AStatement block = (AStatement)children.get(1);

        locals = Locals.newLocalScope(locals);

        if (block == null) {
            throw createError(new IllegalArgumentException("Extraneous do while loop."));
        }

        block.beginLoop = true;
        block.inLoop = true;

        block.analyze(locals);

        if (block.loopEscape && !block.anyContinue) {
            throw createError(new IllegalArgumentException("Extraneous do while loop."));
        }

        condition.expected = boolean.class;
        condition.analyze(locals);
        children.set(0, condition = condition.cast(locals));

        if (condition.constant != null) {
            continuous = (boolean)condition.constant;

            if (!continuous) {
                throw createError(new IllegalArgumentException("Extraneous do while loop."));
            }

            if (!block.anyBreak) {
                methodEscape = true;
                allEscape = true;
            }
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

        Label start = new Label();
        Label begin = new Label();
        Label end = new Label();

        writer.mark(start);

        block.continu = begin;
        block.brake = end;
        block.write(writer, globals);

        writer.mark(begin);

        if (!continuous) {
            condition.write(writer, globals);
            writer.ifZCmp(Opcodes.IFEQ, end);
        }

        if (loopCounter != null) {
            writer.writeLoopCounter(loopCounter.getSlot(), Math.max(1, block.statementCount), location);
        }

        writer.goTo(start);
        writer.mark(end);
    }

    @Override
    public String toString() {
        return singleLineToString(children.get(0), children.get(1));
    }
}
