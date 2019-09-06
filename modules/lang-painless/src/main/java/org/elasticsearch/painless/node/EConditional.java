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

import org.elasticsearch.painless.AnalyzerCaster;
import org.elasticsearch.painless.CompilerSettings;
import org.elasticsearch.painless.Globals;
import org.elasticsearch.painless.Locals;
import org.elasticsearch.painless.Location;
import org.elasticsearch.painless.MethodWriter;
import org.elasticsearch.painless.builder.SymbolTable;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import java.util.Map;

/**
 * Represents a conditional expression.
 */
public final class EConditional extends AExpression {

    public EConditional(Location location) {
        super(location);
    }

    @Override
    void storeSettings(CompilerSettings settings) {
        children.get(0).storeSettings(settings);
        children.get(1).storeSettings(settings);
        children.get(2).storeSettings(settings);
    }

    public static void enter(ANode node, SymbolTable table, Map<String, Object> data) {
        EConditional conditional = (EConditional)node;
        AExpression condition = (AExpression)conditional.children.get(0);
        AExpression lhs = (AExpression)conditional.children.get(1);
        AExpression rhs = (AExpression)conditional.children.get(2);

        condition.expected = boolean.class;
        lhs.expected = conditional.expected;
        lhs.explicit = conditional.explicit;
        lhs.internal = conditional.internal;
        rhs.expected = conditional.expected;
        rhs.explicit = conditional.explicit;
        rhs.internal = conditional.internal;
    }

    public static void exit(ANode node, SymbolTable table, Map<String, Object> data) {
        EConditional conditional = (EConditional)node;
        AExpression condition = (AExpression)conditional.children.get(0);
        AExpression lhs = (AExpression)conditional.children.get(1);
        AExpression rhs = (AExpression)conditional.children.get(2);

        conditional.children.set(0, condition.cast());

        if (conditional.expected == null) {
            Class<?> promote = AnalyzerCaster.promoteConditional(lhs.actual, rhs.actual, lhs.constant, rhs.constant);

            lhs.expected = promote;
            rhs.expected = promote;
            conditional.actual = promote;
        } else {
            conditional.actual = conditional.expected;
        }

        conditional.children.set(1, condition.cast());
        conditional.children.set(2, condition.cast());
    }

    @Override
    void write(MethodWriter writer, Globals globals) {
        writer.writeDebugInfo(location);

        Label fals = new Label();
        Label end = new Label();

        children.get(0).write(writer, globals);
        writer.ifZCmp(Opcodes.IFEQ, fals);

        children.get(1).write(writer, globals);
        writer.goTo(end);
        writer.mark(fals);
        children.get(2).write(writer, globals);
        writer.mark(end);
    }

    @Override
    public String toString() {
        return singleLineToString(children.get(0), children.get(1), children.get(2));
    }
}
