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
import org.elasticsearch.painless.Globals;
import org.elasticsearch.painless.Location;
import org.elasticsearch.painless.MethodWriter;
import org.elasticsearch.painless.symbol.ScopeTable;
import org.elasticsearch.painless.symbol.ScopeTable.Variable;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

public class DoWhileLoopNode extends LoopNode {

    /* ---- begin tree structure ---- */

    @Override
    public DoWhileLoopNode setConditionNode(ExpressionNode conditionNode) {
        super.setConditionNode(conditionNode);
        return this;
    }

    @Override
    public DoWhileLoopNode setBlockNode(BlockNode blockNode) {
        super.setBlockNode(blockNode);
        return this;
    }

    /* ---- end tree structure, begin node data ---- */

    @Override
    public DoWhileLoopNode setContinuous(boolean isContinuous) {
        super.setContinuous(isContinuous);
        return this;
    }

    @Override
    public DoWhileLoopNode setLocation(Location location) {
        super.setLocation(location);
        return this;
    }

    /* ---- end node data ---- */

    public DoWhileLoopNode() {
        // do nothing
    }

    @Override
    protected void write(ClassWriter classWriter, MethodWriter methodWriter, Globals globals, ScopeTable scopeTable) {
        methodWriter.writeStatementOffset(location);

        scopeTable = scopeTable.newScope();

        Label start = new Label();
        Label begin = new Label();
        Label end = new Label();

        methodWriter.mark(start);

        blockNode.continueLabel = begin;
        blockNode.breakLabel = end;
        blockNode.write(classWriter, methodWriter, globals, scopeTable);

        methodWriter.mark(begin);

        if (!isContinuous) {
            conditionNode.write(classWriter, methodWriter, globals, scopeTable);
            methodWriter.ifZCmp(Opcodes.IFEQ, end);
        }

        Variable loop = scopeTable.getVariable("#loop");

        if (loop != null) {
            methodWriter.writeLoopCounter(loop.getSlot(), Math.max(1, blockNode.getStatementCount()), location);
        }

        methodWriter.goTo(start);
        methodWriter.mark(end);
    }
}
