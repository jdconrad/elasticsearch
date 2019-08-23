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

import java.util.Set;

/**
 * Represents a throw statement.
 */
public final class SThrow extends AStatement {


    public SThrow(Location location) {
        super(location);
    }

    @Override
    void storeSettings(CompilerSettings settings) {
        children.get(0).storeSettings(settings);
    }

    @Override
    void extractVariables(Set<String> variables) {
        children.get(0).extractVariables(variables);
    }

    @Override
    void analyze(Locals locals) {
        AExpression expression = (AExpression)children.get(0);

        expression.expected = Exception.class;
        expression.analyze(locals);
        children.set(0, expression.cast(locals));

        methodEscape = true;
        loopEscape = true;
        allEscape = true;
        statementCount = 1;
    }

    @Override
    void write(MethodWriter writer, Globals globals) {
        writer.writeStatementOffset(location);
        children.get(0).write(writer, globals);
        writer.throwException();
    }

    @Override
    public String toString() {
        return singleLineToString(children.get(0));
    }
}
