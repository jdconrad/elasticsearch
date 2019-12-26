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

package org.elasticsearch.painless.ir;

import org.elasticsearch.painless.ClassWriter;
import org.elasticsearch.painless.DefBootstrap;
import org.elasticsearch.painless.Globals;
import org.elasticsearch.painless.Location;
import org.elasticsearch.painless.MethodWriter;
import org.elasticsearch.painless.lookup.PainlessCast;
import org.elasticsearch.painless.lookup.PainlessMethod;
import org.elasticsearch.painless.symbol.ScopeTable;
import org.elasticsearch.painless.symbol.ScopeTable.Variable;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import java.util.Iterator;

import static org.elasticsearch.painless.WriterConstants.ITERATOR_HASNEXT;
import static org.elasticsearch.painless.WriterConstants.ITERATOR_NEXT;
import static org.elasticsearch.painless.WriterConstants.ITERATOR_TYPE;

/**
 * Represents a for-each loop for iterables.
 */
public class ForEachSubIterableNode extends LoopNode {

    /* ---- begin tree structure ---- */

    @Override
    public ForEachSubIterableNode setConditionNode(ExpressionNode conditionNode) {
        super.setConditionNode(conditionNode);
        return this;
    }

    @Override
    public ForEachSubIterableNode setBlockNode(BlockNode blockNode) {
        super.setBlockNode(blockNode);
        return this;
    }

    /* ---- end tree structure, begin node data ---- */

    protected Class<?> variableType;
    protected String variableName;
    protected PainlessCast cast;
    protected Class<?> iteratorType;
    protected String iteratorName;
    protected PainlessMethod method;

    public ForEachSubIterableNode setVariableType(Class<?> variableType) {
        this.variableType = variableType;
        return this;
    }

    public Class<?> getVariableType() {
        return variableType;
    }

    public ForEachSubIterableNode setVariableName(String variableName) {
        this.variableName = variableName;
        return this;
    }

    public String getVariableName() {
        return variableName;
    }
    
    public ForEachSubIterableNode setCast(PainlessCast cast) {
        this.cast = cast;
        return this;
    }

    public PainlessCast getCast() {
        return cast;
    }

    public ForEachSubIterableNode setIteratorType(Class<?> iteratorType) {
        this.iteratorType = iteratorType;
        return this;
    }

    public Class<?> getIteratorType() {
        return iteratorType;
    }

    public ForEachSubIterableNode setIteratorName(String iteratorName) {
        this.iteratorName = iteratorName;
        return this;
    }

    public String getIteratorName() {
        return iteratorName;
    }

    public ForEachSubIterableNode setMethod(PainlessMethod method) {
        this.method = method;
        return this;
    }

    public PainlessMethod getMethod() {
        return method;
    }

    @Override
    public ForEachSubIterableNode setContinuous(boolean isContinuous) {
        super.setContinuous(isContinuous);
        return this;
    }

    @Override
    public ForEachSubIterableNode setLocation(Location location) {
        super.setLocation(location);
        return this;
    }

    /* ---- end node data ---- */

    public ForEachSubIterableNode() {
        // do nothing
    }

    @Override
    protected void write(ClassWriter classWriter, MethodWriter methodWriter, Globals globals, ScopeTable scopeTable) {
        methodWriter.writeStatementOffset(location);

        Variable variable = scopeTable.defineVariable(variableType, variableName);
        Variable iterator = scopeTable.defineVariable(iteratorType, iteratorName);

        conditionNode.write(classWriter, methodWriter, globals, scopeTable);

        if (method == null) {
            org.objectweb.asm.Type methodType = org.objectweb.asm.Type
                    .getMethodType(org.objectweb.asm.Type.getType(Iterator.class), org.objectweb.asm.Type.getType(Object.class));
            methodWriter.invokeDefCall("iterator", methodType, DefBootstrap.ITERATOR);
        } else {
            methodWriter.invokeMethodCall(method);
        }

        methodWriter.visitVarInsn(iterator.getAsmType().getOpcode(Opcodes.ISTORE), iterator.getSlot());

        Label begin = new Label();
        Label end = new Label();

        methodWriter.mark(begin);

        methodWriter.visitVarInsn(iterator.getAsmType().getOpcode(Opcodes.ILOAD), iterator.getSlot());
        methodWriter.invokeInterface(ITERATOR_TYPE, ITERATOR_HASNEXT);
        methodWriter.ifZCmp(MethodWriter.EQ, end);

        methodWriter.visitVarInsn(iterator.getAsmType().getOpcode(Opcodes.ILOAD), iterator.getSlot());
        methodWriter.invokeInterface(ITERATOR_TYPE, ITERATOR_NEXT);
        methodWriter.writeCast(cast);
        methodWriter.visitVarInsn(variable.getAsmType().getOpcode(Opcodes.ISTORE), variable.getSlot());

        Variable loop = scopeTable.getVariable("#loop");

        if (loop != null) {
            methodWriter.writeLoopCounter(loop.getSlot(), blockNode.getStatementCount(), location);
        }

        blockNode.continueLabel = begin;
        blockNode.breakLabel = end;
        blockNode.write(classWriter, methodWriter, globals, scopeTable);

        methodWriter.goTo(begin);
        methodWriter.mark(end);
    }
}
