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
import org.elasticsearch.painless.lookup.PainlessCast;
import org.elasticsearch.painless.lookup.PainlessLookupUtility;

import java.util.Objects;

/**
 * Represents a cast that is inserted into the tree replacing other casts.  (Internal only.)
 */
final class ECast extends AExpression {

    private final PainlessCast cast;

    ECast(Location location, AExpression child, PainlessCast cast) {
        super(location);

        children.add(Objects.requireNonNull(child));
        this.cast = Objects.requireNonNull(cast);
    }

    @Override
    void analyze(SymbolTable table) {
        throw createError(new IllegalStateException("Illegal tree structure."));
    }

    @Override
    void write(MethodWriter writer, Globals globals) {
        children.get(0).write(writer, globals);
        writer.writeDebugInfo(location);
        writer.writeCast(cast);
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(getClass().getSimpleName())
                .append("[")
                .append(PainlessLookupUtility.typeToCanonicalTypeName(actual))
                .append("] [")
                .append(PainlessLookupUtility.typeToCanonicalTypeName(expected))
                .append("] [")
                .append(explicit ? "explicit" : "implicit")
                .append("] [")
                .append(internal ? "internal" : "external")
                .append("] (")
                .append(children.get(0))
                .append(")")
                .toString();
    }
}
