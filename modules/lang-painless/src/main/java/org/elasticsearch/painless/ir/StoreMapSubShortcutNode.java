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
import org.elasticsearch.painless.lookup.PainlessMethod;
import org.elasticsearch.painless.symbol.ScopeTable;

public class StoreMapSubShortcutNode extends StoreNode {

    /* ---- begin tree structure ---- */

    ExpressionNode indexNode;

    public StoreMapSubShortcutNode setIndexNode(ExpressionNode indexNode) {
        this.indexNode = indexNode;
        return this;
    }

    public ExpressionNode getIndexNode() {
        return indexNode;
    }

    @Override
    public StoreMapSubShortcutNode setStoreNode(ExpressionNode storeNode) {
        this.storeNode = storeNode;
        return this;
    }

    @Override
    public StoreMapSubShortcutNode setTypeNode(TypeNode typeNode) {
        super.setTypeNode(typeNode);
        return this;
    }

    /* ---- end tree structure, begin node data ---- */

    protected PainlessMethod setter;

    public StoreMapSubShortcutNode setSetter(PainlessMethod setter) {
        this.setter = setter;
        return this;
    }

    public PainlessMethod getSetter() {
        return setter;
    }

    @Override
    public StoreMapSubShortcutNode setReadFrom(boolean isReadFrom) {
        this.isReadFrom = isReadFrom;
        return this;
    }

    @Override
    public StoreMapSubShortcutNode setLocation(Location location) {
        super.setLocation(location);
        return this;
    }

    /* ---- end node data ---- */

    public StoreMapSubShortcutNode() {
        // do nothing
    }

    @Override
    protected void write(ClassWriter classWriter, MethodWriter methodWriter, ScopeTable scopeTable) {
        indexNode.write(classWriter, methodWriter, scopeTable);

        if (isReadFrom()) {
            methodWriter.writeDup(MethodWriter.getType(getType()).getSize(), 2);
        }

        methodWriter.writeDebugInfo(location);
        methodWriter.invokeMethodCall(setter);
        methodWriter.writePop(MethodWriter.getType(setter.returnType).getSize());
    }
}
