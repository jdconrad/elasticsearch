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

/**
 * Represents an if block.
 */
public final class SIf extends AStatement {

    public SIf(Location location) {
        super(location);
    }

    @Override
    void analyze(SymbolTable table) {
        AExpression condition = (AExpression)children.get(0);
        SBlock ifblock = (SBlock)children.get(1);

        condition.expected = boolean.class;
        condition.analyze(table);
        children.set(0, condition.cast(table));

        if (condition.constant != null) {
            throw createError(new IllegalArgumentException("Extraneous if statement."));
        }

        if (ifblock == null) {
            throw createError(new IllegalArgumentException("Extraneous if statement."));
        }

        ifblock.lastSource = lastSource;
        ifblock.inLoop = inLoop;
        ifblock.lastLoop = lastLoop;

        ifblock.analyze(table);

        anyContinue = ifblock.anyContinue;
        anyBreak = ifblock.anyBreak;
        statementCount = ifblock.statementCount;
    }

    @Override
    void write(MethodWriter writer, Globals globals) {
        SBlock ifblock = (SBlock)children.get(1);

        writer.writeStatementOffset(location);

        Label fals = new Label();

        children.get(0).write(writer, globals);
        writer.ifZCmp(Opcodes.IFEQ, fals);

        ifblock.continu = continu;
        ifblock.brake = brake;
        ifblock.write(writer, globals);

        writer.mark(fals);
    }

    @Override
    public String toString() {
        return singleLineToString(children.get(0), children.get(1));
    }
}
