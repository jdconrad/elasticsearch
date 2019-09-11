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

import org.elasticsearch.painless.AnalyzerCaster;
import org.elasticsearch.painless.Globals;
import org.elasticsearch.painless.Location;
import org.elasticsearch.painless.MethodWriter;
import org.elasticsearch.painless.builder.ScopeTable.Scope;
import org.elasticsearch.painless.builder.ScopeTable.Variable;
import org.elasticsearch.painless.builder.SymbolTable;
import org.elasticsearch.painless.lookup.PainlessCast;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import java.util.Objects;

/**
 * Represents a for-each loop for arrays.
 */
final class SSubEachArray extends AStatement {
    private final Variable variable;

    private PainlessCast cast = null;
    private Variable array = null;
    private Variable index = null;
    private Class<?> indexed = null;

    SSubEachArray(Location location, Variable variable) {
        super(location);

        this.variable = Objects.requireNonNull(variable);
    }

    @Override
    void analyze(SymbolTable table) {
        Scope scope = table.scopes().getNodeScope(this);
        AExpression expression = (AExpression)children.get(0);

        // We must store the array and index as variables for securing slots on the stack, and
        // also add the location offset to make the names unique in case of nested for each loops.
        scope.addVariable("#array" + location.getOffset(), true);
        array = scope.setVariableType("#array" + location.getOffset(), expression.actual);
        scope.addVariable("#index" + location.getOffset(), true);
        index = scope.setVariableType("#index" + location.getOffset(), int.class);
        indexed = expression.actual.getComponentType();
        cast = AnalyzerCaster.getLegalCast(location, indexed, variable.getType(), true, true);
    }

    @Override
    void write(MethodWriter writer, Globals globals) {
        AExpression expression = (AExpression)children.get(0);
        SBlock block = (SBlock)children.get(1);

        writer.writeStatementOffset(location);

        expression.write(writer, globals);
        writer.visitVarInsn(MethodWriter.getType(array.getType()).getOpcode(Opcodes.ISTORE), array.getSlot());
        writer.push(-1);
        writer.visitVarInsn(MethodWriter.getType(index.getType()).getOpcode(Opcodes.ISTORE), index.getSlot());

        Label begin = new Label();
        Label end = new Label();

        writer.mark(begin);

        writer.visitIincInsn(index.getSlot(), 1);
        writer.visitVarInsn(MethodWriter.getType(index.getType()).getOpcode(Opcodes.ILOAD), index.getSlot());
        writer.visitVarInsn(MethodWriter.getType(array.getType()).getOpcode(Opcodes.ILOAD), array.getSlot());
        writer.arrayLength();
        writer.ifICmp(MethodWriter.GE, end);

        writer.visitVarInsn(MethodWriter.getType(array.getType()).getOpcode(Opcodes.ILOAD), array.getSlot());
        writer.visitVarInsn(MethodWriter.getType(index.getType()).getOpcode(Opcodes.ILOAD), index.getSlot());
        writer.arrayLoad(MethodWriter.getType(indexed));
        writer.writeCast(cast);
        writer.visitVarInsn(MethodWriter.getType(variable.getType()).getOpcode(Opcodes.ISTORE), variable.getSlot());

        if (loopCounter != null) {
            writer.writeLoopCounter(loopCounter.getSlot(), statementCount, location);
        }

        block.continu = begin;
        block.brake = end;
        block.write(writer, globals);

        writer.goTo(begin);
        writer.mark(end);
    }

    @Override
    public String toString() {
        throw new UnsupportedOperationException("unexpected node");
    }
}
