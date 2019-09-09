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

public class PPostfixBridge extends AExpression {

    public PPostfixBridge(Location location) {
        super(location);
    }

    @Override
    void storeSettings(CompilerSettings settings) {

    }

    @Override
    void analyze(Locals locals) {
        AExpression lhs = (AExpression)children.get(0);
        AExpression rhs = (AExpression)children.get(1);

        lhs.analyze(locals);
        rhs.expected = lhs.actual;
        rhs.write = write;
        rhs.analyze(locals);
        actual = rhs.actual;
        statement = rhs.statement;
    }

    @Override
    void write(MethodWriter writer, Globals globals) {
        AExpression lhs = (AExpression)children.get(0);
        AExpression rhs = (AExpression)children.get(1);

        lhs.write(writer, globals);
        rhs.write(writer, globals);
    }

    @Override
    public String toString() {
        return null;
    }
}
