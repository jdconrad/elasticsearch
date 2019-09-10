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
import org.elasticsearch.painless.Operation;
import org.elasticsearch.painless.builder.ASTBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an assignment with the lhs and rhs as child nodes.
 */
public final class EAssignment extends AExpression {

    private final boolean pre;
    private final boolean post;
    private Operation operation;

    public EAssignment(Location location, boolean pre, boolean post, Operation operation) {
        super(location);

        this.pre = pre;
        this.post = post;
        this.operation = operation;
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
        AExpression lhs = (AExpression)children.get(0);

        lhs.read = read;

        ASTBuilder builder = new ASTBuilder();

        if (pre) {
            lhs.write = Operation.PRE;

            builder.visitBinary(location, Operation.ADD)
                    .visitEmpty()
                    .visitConstant(location, 1)
            .endVisit();
        } else if (post) {
            lhs.write = Operation.POST;
        } else if (operation != null) {
            lhs.write = Operation.COMPOUND;

            builder.visitBinary(location, Operation.SUB)
                    .visitEmpty()
                    .visitConstant(location, 1)
            .endVisit();
        } else {
            lhs.write = Operation.ASSIGN;
        }

        if (lhs.children.isEmpty()) {
            lhs.children.add(builder.endBuild());
        } else {
            lhs.children.get(1).children.add(builder.endBuild());
        }

        children.remove(1);

        lhs.analyze(locals);

        this.statement = true;
        this.actual = read ? lhs.actual : void.class;
    }

    /**
     * Handles writing byte code for variable/method chains for all given possibilities
     * including String concatenation, compound assignment, regular assignment, and simple
     * reads.  Includes proper duplication for chained assignments and assignments that are
     * also read from.
     */
    @Override
    void write(MethodWriter writer, Globals globals) {
        writer.writeDebugInfo(location);
        children.get(0).write(writer, globals);
    }

    @Override
    public String toString() {
        List<Object> subs = new ArrayList<>();
        subs.add(children.get(0));
        if (children.get(1) != null) {
            // Make sure "=" is in the symbol so this is easy to read at a glance
            subs.add(operation == null ? "=" : operation.symbol + "=");
            subs.add(children.get(1));
            return singleLineToString(subs);
        }
        subs.add(operation.symbol);
        if (pre) {
            subs.add("pre");
        }
        if (post) {
            subs.add("post");
        }
        return singleLineToString(subs);
    }
}
