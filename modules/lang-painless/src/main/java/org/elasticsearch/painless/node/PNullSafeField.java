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

/**
 * Implements a field who's value is null if the prefix is null rather than throwing an NPE.
 */
public class PNullSafeField extends AExpression {

    public PNullSafeField(Location location) {
        super(location);
    }

    @Override
    void analyze(SymbolTable table) {
        if (write != null) {
            throw createError(new IllegalArgumentException("Can't write to null safe reference"));
        }

        AExpression expression = (AExpression)children.get(0);
        expression.analyze(table);
        actual = expression.actual;

        if (actual.isPrimitive()) {
            throw new IllegalArgumentException("Result of null safe operator must be nullable");
        }
    }

    @Override
    void write(MethodWriter writer, Globals globals) {
        Label end = new Label();
        writer.dup();
        writer.ifNull(end);
        children.get(0).write(writer, globals);
        writer.mark(end);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + children.get(0) + ")";
    }
}
