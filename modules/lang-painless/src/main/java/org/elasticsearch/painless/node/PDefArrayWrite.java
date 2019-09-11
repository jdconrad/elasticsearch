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
import org.objectweb.asm.Type;

/**
 * Represents an array load/store or shortcut on a def type.  (Internal only.)
 */
final class PDefArrayWrite extends AExpression {

    PDefArrayWrite(Location location) {
        super(location);
    }

    @Override
    void analyze(SymbolTable table) {
        AExpression index = (AExpression)children.get(0);

        index.analyze(table);
        index.expected = index.actual;
        children.set(0, index.cast(table));

        AExpression rhs = (AExpression)children.get(1);

        if (write == Operation.POST || write == Operation.PRE || write == Operation.COMPOUND) {
            PDefArrayRead dar = new PDefArrayRead(location);
            dar.write = write;
            dar.read = read;
            dar.children.add(new DTypeClass(location, index.actual));
            rhs.children.set(0, dar);
            rhs.explicit = true;
        }

        rhs.analyze(table);
        rhs.expected = rhs.actual;
        children.set(1, rhs.cast(table));

        actual = rhs.actual;
    }

    @Override
    void write(MethodWriter writer, Globals globals) {
        AExpression index = (AExpression)children.get(0);
        writer.dup();
        index.write(writer, globals);
        Type indexMethodType = Type.getMethodType(
                MethodWriter.getType(index.actual), Type.getType(Object.class), MethodWriter.getType(index.actual));
        writer.invokeDefCall("normalizeIndex", indexMethodType, DefBootstrap.INDEX_NORMALIZE);

        if (write == Operation.POST || write == Operation.PRE || write == Operation.COMPOUND) {
            writer.writeDup(2, 0);
        }

        children.get(1).write(writer, globals);

        if (read && write != Operation.POST) {
            writer.writeDup(MethodWriter.getType(actual).getSize(), 2);
        }

        writer.writeDebugInfo(location);

        Type storeMethodType = Type.getMethodType(
                Type.getType(void.class), Type.getType(Object.class), MethodWriter.getType(index.actual), MethodWriter.getType(actual));
        writer.invokeDefCall("arrayStore", storeMethodType, DefBootstrap.ARRAY_STORE);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " (" + children.get(0) + ") (" + children.get(1) + ")";
    }
}
