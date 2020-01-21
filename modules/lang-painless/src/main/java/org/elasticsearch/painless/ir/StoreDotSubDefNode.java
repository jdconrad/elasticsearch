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

public class StoreDotSubDefNode extends StoreNode {

    /* ---- begin tree structure ---- */

    @Override
    public StoreDotSubDefNode setTypeNode(TypeNode typeNode) {
        super.setTypeNode(typeNode);
        return this;
    }

    @Override
    public StoreDotSubDefNode setStoreNode(ExpressionNode storeNode) {
        this.storeNode = storeNode;
        return this;
    }

    /* ---- end tree structure, begin node data ---- */

    protected String value;

    public StoreDotSubDefNode setValue(String value) {
        this.value = value;
        return this;
    }

    public String getValue() {
        return value;
    }

    @Override
    public StoreDotSubDefNode setReadFrom(boolean isReadFrom) {
        super.setReadFrom(isReadFrom);
        return this;
    }

    @Override
    public StoreDotSubDefNode setLocation(Location location) {
        super.setLocation(location);
        return this;
    }

    /* ---- end node data ---- */

    public StoreDotSubDefNode() {
        // do nothing
    }

    @Override
    protected void write(ClassWriter classWriter, MethodWriter methodWriter, ScopeTable scopeTable) {
        if (isReadFrom()) {
            methodWriter.writeDup(MethodWriter.getType(getType()).getSize(), 1);
        }

        methodWriter.writeDebugInfo(location);

        org.objectweb.asm.Type methodType = org.objectweb.asm.Type.getMethodType(
                org.objectweb.asm.Type.getType(void.class), org.objectweb.asm.Type.getType(Object.class), MethodWriter.getType(getType()));
        methodWriter.invokeDefCall(value, methodType, DefBootstrap.STORE);
    }
}
