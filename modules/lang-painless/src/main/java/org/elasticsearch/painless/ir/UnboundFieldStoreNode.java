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

import static org.elasticsearch.painless.WriterConstants.CLASS_TYPE;

public class UnboundFieldStoreNode extends UnaryNode {

    /* ---- begin tree structure ---- */

    public UnboundFieldStoreNode setChildNode(ExpressionNode childNode) {
        super.setChildNode(childNode);
        return this;
    }

    @Override
    public UnboundFieldStoreNode setTypeNode(TypeNode typeNode) {
        super.setTypeNode(typeNode);
        return this;
    }

    /* ---- end tree structure, begin node data ---- */

    protected String name;
    protected boolean isStatic;

    public UnboundFieldStoreNode setName(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return name;
    }

    public UnboundFieldStoreNode setStatic(boolean isStatic) {
        this.isStatic = isStatic;
        return this;
    }

    public boolean isStatic() {
        return isStatic;
    }

    @Override
    public UnboundFieldStoreNode setLocation(Location location) {
        super.setLocation(location);
        return this;
    }

    /* ---- end node data ---- */

    public UnboundFieldStoreNode() {
        // do nothing
    }

    @Override
    public void write(ClassWriter classWriter, MethodWriter methodWriter, Globals globals, ScopeTable scopeTable) {
        if (isStatic == false) {
            methodWriter.loadThis();
        }

        childNode.write(classWriter, methodWriter, globals, scopeTable);

        methodWriter.writeDebugInfo(location);

        if (isStatic) {
            methodWriter.putStatic(CLASS_TYPE, name, MethodWriter.getType(getType()));
        } else {
            methodWriter.putField(CLASS_TYPE, name, MethodWriter.getType(getType()));
        }
    }
}
