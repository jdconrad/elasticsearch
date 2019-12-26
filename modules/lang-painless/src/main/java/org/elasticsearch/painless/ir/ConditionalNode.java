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
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

public class ConditionalNode extends BinaryNode {

    /* ---- begin tree structure ---- */

    protected ExpressionNode conditionNode;

    public ConditionalNode setConditionNode(ExpressionNode conditionNode) {
        this.conditionNode = conditionNode;
        return this;
    }

    public ExpressionNode getConditionNode() {
        return conditionNode;
    }

    @Override
    public ConditionalNode setLeftNode(ExpressionNode leftNode) {
        super.setLeftNode(leftNode);
        return this;
    }

    @Override
    public ConditionalNode setRightNode(ExpressionNode rightNode) {
        super.setRightNode(rightNode);
        return this;
    }

    @Override
    public ConditionalNode setTypeNode(TypeNode typeNode) {
        super.setTypeNode(typeNode);
        return this;
    }

    /* ---- end tree structure, begin node data ---- */

    @Override
    public ConditionalNode setLocation(Location location) {
        super.setLocation(location);
        return this;
    }

    /* ---- end node data ---- */

    public ConditionalNode() {
        // do nothing
    }

    @Override
    protected void write(ClassWriter classWriter, MethodWriter methodWriter, Globals globals, ScopeTable scopeTable) {
        methodWriter.writeDebugInfo(location);

        Label fals = new Label();
        Label end = new Label();

        conditionNode.write(classWriter, methodWriter, globals, scopeTable);
        methodWriter.ifZCmp(Opcodes.IFEQ, fals);

        leftNode.write(classWriter, methodWriter, globals, scopeTable);
        methodWriter.goTo(end);
        methodWriter.mark(fals);
        rightNode.write(classWriter, methodWriter, globals, scopeTable);
        methodWriter.mark(end);
    }
}
