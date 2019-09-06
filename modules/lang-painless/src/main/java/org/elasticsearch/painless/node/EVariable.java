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
import org.elasticsearch.painless.Location;
import org.elasticsearch.painless.MethodWriter;
import org.elasticsearch.painless.builder.ScopeTable;
import org.elasticsearch.painless.builder.SymbolTable;
import org.objectweb.asm.Opcodes;

import java.util.Map;
import java.util.Objects;

/**
 * Represents a variable load/store.
 */
public final class EVariable extends AStoreable {

    public final String name;

    private ScopeTable.Variable variable = null;

    public EVariable(Location location, String name) {
        super(location);

        this.name = Objects.requireNonNull(name);
    }

    @Override
    void storeSettings(CompilerSettings settings) {
        // do nothing
    }

    public static void enter(ANode node, SymbolTable table, Map<String, Object> data) {
        EVariable variableNode = (EVariable)node;
        variableNode.variable = table.scopeTable.getNodeScope(variableNode).getVariable(variableNode.name);

        if (variableNode.write && variableNode.variable.isReadonly()) {
            throw variableNode.createError(
                    new IllegalArgumentException("variable [" + variableNode.variable.getName() + "] is read-only."));
        }

        variableNode.actual = variableNode.variable.getClass();
    }

    @Override
    void write(MethodWriter writer, Globals globals) {
        writer.visitVarInsn(MethodWriter.getType(actual).getOpcode(Opcodes.ILOAD), variable.getSlot());
    }

    @Override
    int accessElementCount() {
        return 0;
    }

    @Override
    boolean isDefOptimized() {
        return false;
    }

    @Override
    void updateActual(Class<?> actual) {
        throw new IllegalArgumentException("Illegal tree structure.");
    }

    @Override
    void setup(MethodWriter writer, Globals globals) {
        // Do nothing.
    }

    @Override
    void load(MethodWriter writer, Globals globals) {
        writer.visitVarInsn(MethodWriter.getType(actual).getOpcode(Opcodes.ILOAD), variable.getSlot());
    }

    @Override
    void store(MethodWriter writer, Globals globals) {
        writer.visitVarInsn(MethodWriter.getType(actual).getOpcode(Opcodes.ISTORE), variable.getSlot());
    }

    @Override
    public String toString() {
        return singleLineToString(name);
    }
}
