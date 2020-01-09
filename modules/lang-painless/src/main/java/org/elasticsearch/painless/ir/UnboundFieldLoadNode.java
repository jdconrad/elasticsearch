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

import static org.elasticsearch.painless.WriterConstants.CLASS_TYPE;

public class UnboundFieldLoadNode extends ExpressionNode {

    /* ---- begin tree structure ---- */

    @Override
    public UnboundFieldLoadNode setTypeNode(TypeNode typeNode) {
        super.setTypeNode(typeNode);
        return this;
    }

    /* ---- end tree structure, begin node data ---- */

    protected String name;
    protected boolean isStatic;

    public UnboundFieldLoadNode setName(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return name;
    }

    public UnboundFieldLoadNode setStatic(boolean isStatic) {
        this.isStatic = isStatic;
        return this;
    }

    public boolean isStatic() {
        return isStatic;
    }
    
    @Override
    public UnboundFieldLoadNode setLocation(Location location) {
        super.setLocation(location);
        return this;
    }

    /* ---- end node data ---- */

    public UnboundFieldLoadNode() {
        // do nothing
    }

    @Override
    public void write(ClassWriter classWriter, MethodWriter methodWriter, ScopeTable scopeTable) {
        methodWriter.writeDebugInfo(location);

        if (isStatic) {
            methodWriter.getStatic(CLASS_TYPE, name, MethodWriter.getType(getType()));
        } else {
            methodWriter.loadThis();
            methodWriter.getField(CLASS_TYPE, name, MethodWriter.getType(getType()));
        }
    }
}
