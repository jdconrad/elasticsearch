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
import org.elasticsearch.painless.Locals.Variable;
import org.elasticsearch.painless.Location;
import org.elasticsearch.painless.MethodWriter;

import java.util.Objects;

/**
 * Represents a variable load/store.
 */
public final class EVariable extends AExpression {

    public final String name;

    public EVariable(Location location, String name) {
        super(location);

        this.name = Objects.requireNonNull(name);
    }

    @Override
    void storeSettings(CompilerSettings settings) {
        // do nothing
    }

    @Override
    void analyze(Locals locals) {
        Variable variable = locals.getVariable(location, name);
        AExpression expression;

        if (write != null) {
            expression = new EVariableWrite(location, name, variable);
        } else {
            expression = new EVariableRead(location, name, variable);
        }

        expression.write = write;
        expression.read = read;
        expression.expected = expected;
        expression.explicit = explicit;
        expression.internal = internal;
        expression.analyze(locals);
        replace(expression);
    }

    @Override
    void write(MethodWriter writer, Globals globals) {
        throw createError(new IllegalStateException("illegal tree structure"));
    }

    @Override
    public String toString() {
        return singleLineToString(name);
    }
}
