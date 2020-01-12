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

import org.elasticsearch.painless.Location;
import org.elasticsearch.painless.Scope;
import org.elasticsearch.painless.ir.ClassNode;
import org.elasticsearch.painless.ir.NullNode;
import org.elasticsearch.painless.ir.TypeNode;
import org.elasticsearch.painless.lookup.PainlessLookupUtility;
import org.elasticsearch.painless.symbol.ScriptRoot;

/**
 * Represents a null constant.
 */
public final class ENull extends AExpression {

    public ENull(Location location) {
        super(location);
    }

    @Override
    void analyze(ScriptRoot scriptRoot, Scope scope) {
        if (!read) {
            throw createError(new IllegalArgumentException("Must read from null constant."));
        }

        if (expected != null) {
            if (expected.isPrimitive()) {
                throw createError(new IllegalArgumentException(
                    "Cannot cast null to a primitive type [" + PainlessLookupUtility.typeToCanonicalTypeName(expected) + "]."));
            }

            actual = expected;
        } else {
            actual = Object.class;
        }
    }

    @Override
    NullNode write(ClassNode classNode) {
        return new NullNode()
                .setTypeNode(new TypeNode()
                        .setLocation(location)
                        .setType(actual)
                )
                .setLocation(location);
    }

    @Override
    public String toString() {
        return singleLineToString();
    }
}
