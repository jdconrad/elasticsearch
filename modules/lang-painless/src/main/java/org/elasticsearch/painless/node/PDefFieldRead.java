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

import org.elasticsearch.painless.DefBootstrap;
import org.elasticsearch.painless.Globals;
import org.elasticsearch.painless.Location;
import org.elasticsearch.painless.MethodWriter;
import org.elasticsearch.painless.Operation;
import org.elasticsearch.painless.builder.SymbolTable;
import org.elasticsearch.painless.lookup.def;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Represents a field load/store or shortcut on a def type.  (Internal only.)
 */
final class PDefFieldRead extends AExpression {

    private final String value;

    PDefFieldRead(Location location, String value) {
        super(location);

        this.value = Objects.requireNonNull(value);
    }

    @Override
    void analyze(SymbolTable table) {
        // TODO: remove ZonedDateTime exception when JodaCompatibleDateTime is removed
        actual = expected == null || expected == ZonedDateTime.class || explicit ? def.class : expected;
    }

    @Override
    void write(MethodWriter writer, Globals globals) {
        writer.writeDebugInfo(location);

        org.objectweb.asm.Type methodType =
            org.objectweb.asm.Type.getMethodType(MethodWriter.getType(actual), org.objectweb.asm.Type.getType(Object.class));
        writer.invokeDefCall(value, methodType, DefBootstrap.LOAD);

        if (read && write == Operation.POST) {
            writer.writeDup(MethodWriter.getType(actual).getSize(), 1);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" + value + "]";
    }
}
