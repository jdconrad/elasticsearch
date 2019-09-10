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

import static java.util.Collections.emptyList;

/**
 * Represents a series of declarations.
 */
public final class SDeclBlock extends AStatement {

    public SDeclBlock(Location location) {
        super(location);
    }

    @Override
    void analyze(SymbolTable table) {
        for (ANode child : children) {
            SDeclaration declaration = (SDeclaration)child;
            declaration.analyze(table);
        }

        statementCount = children.size();
    }

    @Override
    void write(MethodWriter writer, Globals globals) {
        for (ANode declaration : children) {
            declaration.write(writer, globals);
        }
    }

    @Override
    public String toString() {
        return multilineToString(emptyList(), children);
    }
}
