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
import org.elasticsearch.painless.Location;
import org.elasticsearch.painless.MethodWriter;
import org.elasticsearch.painless.symbol.ScopeTable;
import org.objectweb.asm.Type;

public class StoreBraceSubDefNode extends StoreNode {

    /* ---- begin tree structure ---- */

    ExpressionNode indexNode;

    public StoreBraceSubDefNode setIndexNode(ExpressionNode indexNode) {
        this.indexNode = indexNode;
        return this;
    }

    public ExpressionNode getIndexNode() {
        return indexNode;
    }

    @Override
    public StoreBraceSubDefNode setStoreNode(ExpressionNode storeNode) {
        this.storeNode = storeNode;
        return this;
    }

    @Override
    public StoreBraceSubDefNode setTypeNode(TypeNode typeNode) {
        super.setTypeNode(typeNode);
        return this;
    }

    /* ---- end tree structure, begin node data ---- */

    @Override
    public StoreBraceSubDefNode setReadFrom(boolean isReadFrom) {
        this.isReadFrom = isReadFrom;
        return this;
    }

    @Override
    public StoreBraceSubDefNode setLocation(Location location) {
        super.setLocation(location);
        return this;
    }

    /* ---- end node data ---- */

    public StoreBraceSubDefNode() {
        // do nothing
    }

    @Override
    protected void write(ClassWriter classWriter, MethodWriter methodWriter, ScopeTable scopeTable) {
        methodWriter.dup();
        indexNode.write(classWriter, methodWriter, scopeTable);
        Type indexMethodType = Type.getMethodType(
                MethodWriter.getType(indexNode.getType()), Type.getType(Object.class), MethodWriter.getType(indexNode.getType()));
        methodWriter.invokeDefCall("normalizeIndex", indexMethodType, DefBootstrap.INDEX_NORMALIZE);

        if (isReadFrom()) {
            methodWriter.writeDup(MethodWriter.getType(getType()).getSize(), 2);
        }

        methodWriter.writeDebugInfo(location);
        Type storeMethodType = Type.getMethodType(Type.getType(void.class), Type.getType(Object.class),
                MethodWriter.getType(indexNode.getType()), MethodWriter.getType(getType()));
        methodWriter.invokeDefCall("arrayStore", storeMethodType, DefBootstrap.ARRAY_STORE);
    }
}
