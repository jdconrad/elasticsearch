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
import org.elasticsearch.painless.DefBootstrap;
import org.elasticsearch.painless.Globals;
import org.elasticsearch.painless.Locals;
import org.elasticsearch.painless.Location;
import org.elasticsearch.painless.MethodWriter;
import org.elasticsearch.painless.Operation;
import org.elasticsearch.painless.lookup.def;
import org.objectweb.asm.Type;

import java.time.ZonedDateTime;

/**
 * Represents an array load/store or shortcut on a def type.  (Internal only.)
 */
final class PDefArrayRead extends AExpression {

    PDefArrayRead(Location location) {
        super(location);
    }

    @Override
    void storeSettings(CompilerSettings settings) {
        throw createError(new IllegalStateException("illegal tree structure"));
    }

    @Override
    void analyze(Locals locals) {
        if (children.get(0) instanceof DTypeClass == false) {
            AExpression index = (AExpression) children.get(0);
            index.analyze(locals);
            index.expected = index.actual;
            children.set(0, index.cast(locals));
        }

        // TODO: remove ZonedDateTime exception when JodaCompatibleDateTime is removed
        actual = expected == null || expected == ZonedDateTime.class || explicit ? def.class : expected;
    }

    @Override
    void write(MethodWriter writer, Globals globals) {
        Class<?> indexType;

        if (children.get(0) instanceof DTypeClass) {
            DTypeClass index = (DTypeClass) children.get(0);
            indexType = index.type;
        } else {
            AExpression index = (AExpression)children.get(0);
            indexType = index.actual;

            writer.dup();
            index.write(writer, globals);
            Type indexMethodType = Type.getMethodType(
                    MethodWriter.getType(index.actual), Type.getType(Object.class), MethodWriter.getType(index.actual));
            writer.invokeDefCall("normalizeIndex", indexMethodType, DefBootstrap.INDEX_NORMALIZE);
        }

        writer.writeDebugInfo(location);

        Type loadMethodType =
                Type.getMethodType(MethodWriter.getType(actual), Type.getType(Object.class), MethodWriter.getType(indexType));
        writer.invokeDefCall("arrayLoad", loadMethodType, DefBootstrap.ARRAY_LOAD);

        if (read && write == Operation.POST) {
            writer.writeDup(MethodWriter.getType(actual).getSize(), 2);
        }
    }

    @Override
    public String toString() {
        throw new UnsupportedOperationException("unexpected node");
    }
}
