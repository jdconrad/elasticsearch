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
import org.elasticsearch.painless.builder.ScopeTable.Variable;
import org.elasticsearch.painless.builder.SymbolTable;
import org.elasticsearch.painless.lookup.PainlessLookupUtility;
import org.elasticsearch.painless.lookup.def;

/**
 * Represents a for-each loop and defers to subnodes depending on type.
 */
public class SEach extends AStatement {

    public SEach(Location location) {
        super(location);
    }

    @Override
    void analyze(SymbolTable table) {
        AStatement each;
        SDeclaration declaration = (SDeclaration)children.get(0);
        AExpression expression = (AExpression)children.get(1);
        SBlock block = (SBlock)children.get(2);

        expression.analyze(table);
        expression.expected = expression.actual;
        expression = expression.cast(table);

        declaration.analyze(table);

        Variable variable = table.scopes().getNodeScope(this).getVariable(declaration.name);

        if (expression.actual.isArray()) {
            each = new SSubEachArray(location, variable);
        } else if (expression.actual == def.class || Iterable.class.isAssignableFrom(expression.actual)) {
            each = new SSubEachIterable(location, variable);
        } else {
            throw createError(new IllegalArgumentException("Illegal for each type " +
                    "[" + PainlessLookupUtility.typeToCanonicalTypeName(expression.actual) + "]."));
        }

        each.children.add(expression);
        each.children.add(block);

        each.analyze(table);

        if (block == null) {
            throw createError(new IllegalArgumentException("Extraneous for each loop."));
        }

        block.beginLoop = true;
        block.inLoop = true;
        block.analyze(table);
        block.statementCount = Math.max(1, block.statementCount);

        if (block.loopEscape && !block.anyContinue) {
            throw createError(new IllegalArgumentException("Extraneous for loop."));
        }

        statementCount = 1;
        loopCounter = table.scopes().getNodeScope(this).getVariable("#loop");
    }

    @Override
    void write(MethodWriter writer, Globals globals) {
        children.get(0).write(writer, globals);
    }

    @Override
    public String toString() {
        return null;
    }
}
