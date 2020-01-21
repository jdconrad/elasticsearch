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
import org.elasticsearch.painless.symbol.ScopeTable;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

public class LoadBraceSubNode extends LoadNode {

    /* ---- begin tree structure ---- */

    ExpressionNode indexNode;

    public LoadBraceSubNode setIndexNode(ExpressionNode indexNode) {
        this.indexNode = indexNode;
        return this;
    }

    public ExpressionNode getIndexNode() {
        return indexNode;
    }

    @Override
    public LoadBraceSubNode setTypeNode(TypeNode typeNode) {
        super.setTypeNode(typeNode);
        return this;
    }

    /* ---- end tree structure, begin node data ---- */

    @Override
    public LoadBraceSubNode setCompoundOperation(boolean isCompoundOperation) {
        this.isCompoundOperation = isCompoundOperation;
        return this;
    }

    @Override
    public LoadBraceSubNode setReadFrom(boolean isReadFrom) {
        this.isReadFrom = isReadFrom;
        return this;
    }

    @Override
    public LoadBraceSubNode setLocation(Location location) {
        super.setLocation(location);
        return this;
    }

    /* ---- end node data ---- */

    public LoadBraceSubNode() {
        // do nothing
    }

    @Override
    protected void write(ClassWriter classWriter, MethodWriter methodWriter, ScopeTable scopeTable) {
        if (isCompoundOperation()) {
            methodWriter.dup2();
        } else {
            indexNode.write(classWriter, methodWriter, scopeTable);

            Label noFlip = new Label();
            methodWriter.dup();
            methodWriter.ifZCmp(Opcodes.IFGE, noFlip);
            methodWriter.swap();
            methodWriter.dupX1();
            methodWriter.arrayLength();
            methodWriter.visitInsn(Opcodes.IADD);
            methodWriter.mark(noFlip);
        }

        methodWriter.writeDebugInfo(location);
        methodWriter.arrayLoad(MethodWriter.getType(getType()));

        if (isReadFrom()) {
            methodWriter.writeDup(MethodWriter.getType(getType()).getSize(), accessElementCount());
        }
    }
}
