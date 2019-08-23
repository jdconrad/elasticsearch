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
import org.elasticsearch.painless.lookup.PainlessLookupUtility;
import org.elasticsearch.painless.lookup.def;

import java.util.Objects;
import java.util.Set;

/**
 * Represents a for-each loop and defers to subnodes depending on type.
 */
public class SEach extends AStatement {

    private final String type;
    private final String name;

    private AStatement sub = null;

    public SEach(Location location, String type, String name) {
        super(location);

        this.type = Objects.requireNonNull(type);
        this.name = Objects.requireNonNull(name);
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
        variables.add(name);

        children.get(0).extractVariables(variables);

        if (children.get(1) != null) {
            children.get(1).extractVariables(variables);
        }
    }

    @Override
    void analyze(Locals locals) {
        AExpression expression = (AExpression)children.get(0);
        SBlock block = (SBlock)children.get(1);

        expression.analyze(locals);
        expression.expected = expression.actual;
        expression = expression.cast(locals);

        Class<?> clazz = locals.getPainlessLookup().canonicalTypeNameToType(this.type);

        if (clazz == null) {
            throw createError(new IllegalArgumentException("Not a type [" + this.type + "]."));
        }

        locals = Locals.newLocalScope(locals);
        Variable variable = locals.addVariable(location, clazz, name, true);

        if (expression.actual.isArray()) {
            sub = new SSubEachArray(location, variable, expression, block);
        } else if (expression.actual == def.class || Iterable.class.isAssignableFrom(expression.actual)) {
            sub = new SSubEachIterable(location, variable, expression, block);
        } else {
            throw createError(new IllegalArgumentException("Illegal for each type " +
                    "[" + PainlessLookupUtility.typeToCanonicalTypeName(expression.actual) + "]."));
        }

        sub.analyze(locals);

        if (block == null) {
            throw createError(new IllegalArgumentException("Extraneous for each loop."));
        }

        block.beginLoop = true;
        block.inLoop = true;
        block.analyze(locals);
        block.statementCount = Math.max(1, block.statementCount);

        if (block.loopEscape && !block.anyContinue) {
            throw createError(new IllegalArgumentException("Extraneous for loop."));
        }

        statementCount = 1;

        if (locals.hasVariable(Locals.LOOP)) {
            sub.loopCounter = locals.getVariable(location, Locals.LOOP);
        }
    }

    @Override
    void write(MethodWriter writer, Globals globals) {
        sub.write(writer, globals);
    }

    @Override
    public String toString() {
        return singleLineToString(type, name, children.get(0), children.get(1));
    }
}
