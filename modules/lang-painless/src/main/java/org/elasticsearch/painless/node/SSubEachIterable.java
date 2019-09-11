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
import org.elasticsearch.painless.DefBootstrap;
import org.elasticsearch.painless.Globals;
import org.elasticsearch.painless.Location;
import org.elasticsearch.painless.MethodWriter;
import org.elasticsearch.painless.builder.ScopeTable.Scope;
import org.elasticsearch.painless.builder.ScopeTable.Variable;
import org.elasticsearch.painless.builder.SymbolTable;
import org.elasticsearch.painless.lookup.PainlessCast;
import org.elasticsearch.painless.lookup.PainlessMethod;
import org.elasticsearch.painless.lookup.def;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import java.util.Iterator;
import java.util.Objects;

import static org.elasticsearch.painless.WriterConstants.ITERATOR_HASNEXT;
import static org.elasticsearch.painless.WriterConstants.ITERATOR_NEXT;
import static org.elasticsearch.painless.WriterConstants.ITERATOR_TYPE;
import static org.elasticsearch.painless.lookup.PainlessLookupUtility.typeToCanonicalTypeName;

/**
 * Represents a for-each loop for iterables.
 */
final class SSubEachIterable extends AStatement {

    private final Variable variable;

    private PainlessCast cast = null;
    private Variable iterator = null;
    private PainlessMethod method = null;

    SSubEachIterable(Location location, Variable variable) {
        super(location);

        this.variable = Objects.requireNonNull(variable);
    }

    @Override
    void analyze(SymbolTable table) {
        Scope scope = table.scopes().getNodeScope(this);
        AExpression expression = (AExpression)children.get(0);

        // We must store the iterator as a variable for securing a slot on the stack, and
        // also add the location offset to make the name unique in case of nested for each loops.
        scope.addVariable("#itr" + location.getOffset(), true);
        iterator = scope.setVariableType("#itr" + location.getOffset(), Iterator.class);

        if (expression.actual == def.class) {
            method = null;
        } else {
            method = table.lookup().lookupPainlessMethod(expression.actual, false, "iterator", 0);

            if (method == null) {
                    throw createError(new IllegalArgumentException(
                            "method [" + typeToCanonicalTypeName(expression.actual) + ", iterator/0] not found"));
            }
        }

        cast = AnalyzerCaster.getLegalCast(location, def.class, variable.getType(), true, true);
    }

    @Override
    void write(MethodWriter writer, Globals globals) {
        AExpression expression = (AExpression)children.get(0);
        SBlock block = (SBlock)children.get(1);

        writer.writeStatementOffset(location);

        expression.write(writer, globals);

        if (method == null) {
            org.objectweb.asm.Type methodType = org.objectweb.asm.Type
                    .getMethodType(org.objectweb.asm.Type.getType(Iterator.class), org.objectweb.asm.Type.getType(Object.class));
            writer.invokeDefCall("iterator", methodType, DefBootstrap.ITERATOR);
        } else {
            writer.invokeMethodCall(method);
        }

        writer.visitVarInsn(MethodWriter.getType(iterator.getType()).getOpcode(Opcodes.ISTORE), iterator.getSlot());

        Label begin = new Label();
        Label end = new Label();

        writer.mark(begin);

        writer.visitVarInsn(MethodWriter.getType(iterator.getType()).getOpcode(Opcodes.ILOAD), iterator.getSlot());
        writer.invokeInterface(ITERATOR_TYPE, ITERATOR_HASNEXT);
        writer.ifZCmp(MethodWriter.EQ, end);

        writer.visitVarInsn(MethodWriter.getType(iterator.getType()).getOpcode(Opcodes.ILOAD), iterator.getSlot());
        writer.invokeInterface(ITERATOR_TYPE, ITERATOR_NEXT);
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
