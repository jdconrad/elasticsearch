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
import org.elasticsearch.painless.Location;
import org.elasticsearch.painless.MethodWriter;
import org.elasticsearch.painless.lookup.PainlessCast;
import org.elasticsearch.painless.symbol.ScopeTable;
import org.elasticsearch.painless.symbol.ScopeTable.Variable;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

public class ForEachSubArrayNode extends LoopNode {

    /* ---- being tree structure ---- */

    protected TypeNode indexedTypeNode;

    public ForEachSubArrayNode setIndexedTypeNode(TypeNode indexedTypeNode) {
        this.indexedTypeNode = indexedTypeNode;
        return this;
    }

    public TypeNode getIndexedTypeNode() {
        return indexedTypeNode;
    }

    public Class<?> getIndexedType() {
        return indexedTypeNode.getType();
    }

    public String getIndexedCanonicalTypeName() {
        return indexedTypeNode.getCanonicalTypeName();
    }

    @Override
    public ForEachSubArrayNode setConditionNode(ExpressionNode conditionNode) {
        super.setConditionNode(conditionNode);
        return this;
    }

    @Override
    public ForEachSubArrayNode setBlockNode(BlockNode blockNode) {
        super.setBlockNode(blockNode);
        return this;
    }

    /* ---- begin node data ---- */

    protected Class<?> variableType;
    protected String variableName;
    protected PainlessCast cast;
    protected Class<?> arrayType;
    protected String arrayName;
    protected Class<?> indexType;
    protected String indexName;

    public ForEachSubArrayNode setVariableType(Class<?> variableType) {
        this.variableType = variableType;
        return this;
    }

    public Class<?> getVariableType() {
        return variableType;
    }
    
    public ForEachSubArrayNode setVariableName(String variableName) {
        this.variableName = variableName;
        return this;
    }

    public String getVariableName() {
        return variableName;
    }

    public ForEachSubArrayNode setCast(PainlessCast cast) {
        this.cast = cast;
        return this;
    }

    public PainlessCast getCast() {
        return cast;
    }

    public ForEachSubArrayNode setArrayType(Class<?> arrayType) {
        this.arrayType = arrayType;
        return this;
    }

    public Class<?> getArrayType() {
        return arrayType;
    }

    public ForEachSubArrayNode setArrayName(String arrayName) {
        this.arrayName = arrayName;
        return this;
    }

    public String getArrayName() {
        return arrayName;
    }

    public ForEachSubArrayNode setIndexType(Class<?> indexType) {
        this.indexType = indexType;
        return this;
    }

    public Class<?> getIndexType() {
        return indexType;
    }

    public ForEachSubArrayNode setIndexName(String indexName) {
        this.indexName = indexName;
        return this;
    }

    public String getIndexName() {
        return indexName;
    }
    
    @Override
    public ForEachSubArrayNode setContinuous(boolean isContinuous) {
        super.setContinuous(isContinuous);
        return this;
    }

    @Override
    public ForEachSubArrayNode setLocation(Location location) {
        super.setLocation(location);
        return this;
    }

    /* ---- end node data ---- */

    public ForEachSubArrayNode() {
        // do nothing
    }

    @Override
    protected void write(ClassWriter classWriter, MethodWriter methodWriter, ScopeTable scopeTable) {
        methodWriter.writeStatementOffset(location);

        Variable variable = scopeTable.defineVariable(variableType, variableName);
        Variable array = scopeTable.defineVariable(arrayType, arrayName);
        Variable index = scopeTable.defineVariable(indexType, indexName);

        conditionNode.write(classWriter, methodWriter, scopeTable);
        methodWriter.visitVarInsn(array.getAsmType().getOpcode(Opcodes.ISTORE), array.getSlot());
        methodWriter.push(-1);
        methodWriter.visitVarInsn(index.getAsmType().getOpcode(Opcodes.ISTORE), index.getSlot());

        Label begin = new Label();
        Label end = new Label();

        methodWriter.mark(begin);

        methodWriter.visitIincInsn(index.getSlot(), 1);
        methodWriter.visitVarInsn(index.getAsmType().getOpcode(Opcodes.ILOAD), index.getSlot());
        methodWriter.visitVarInsn(array.getAsmType().getOpcode(Opcodes.ILOAD), array.getSlot());
        methodWriter.arrayLength();
        methodWriter.ifICmp(MethodWriter.GE, end);

        methodWriter.visitVarInsn(array.getAsmType().getOpcode(Opcodes.ILOAD), array.getSlot());
        methodWriter.visitVarInsn(index.getAsmType().getOpcode(Opcodes.ILOAD), index.getSlot());
        methodWriter.arrayLoad(MethodWriter.getType(getIndexedType()));
        methodWriter.writeCast(cast);
        methodWriter.visitVarInsn(variable.getAsmType().getOpcode(Opcodes.ISTORE), variable.getSlot());

        Variable loop = scopeTable.getVariable("#loop");

        if (loop != null) {
            methodWriter.writeLoopCounter(loop.getSlot(), blockNode.getStatementCount(), location);
        }

        blockNode.continueLabel = begin;
        blockNode.breakLabel = end;
        blockNode.write(classWriter, methodWriter, scopeTable);

        methodWriter.goTo(begin);
        methodWriter.mark(end);
    }
}
