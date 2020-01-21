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

public class LoadMapSubShortcutNode extends LoadNode {

    /* ---- begin tree structure ---- */

    ExpressionNode indexNode;

    public LoadMapSubShortcutNode setIndexNode(ExpressionNode indexNode) {
        this.indexNode = indexNode;
        return this;
    }

    public ExpressionNode getIndexNode() {
        return indexNode;
    }

    @Override
    public LoadMapSubShortcutNode setTypeNode(TypeNode typeNode) {
        super.setTypeNode(typeNode);
        return this;
    }

    /* ---- end tree structure, begin node data ---- */

    protected PainlessMethod getter;

    public LoadMapSubShortcutNode setGetter(PainlessMethod getter) {
        this.getter = getter;
        return this;
    }

    public PainlessMethod getGetter() {
        return getter;
    }

    @Override
    public LoadMapSubShortcutNode setCompoundOperation(boolean isCompoundOperation) {
        this.isCompoundOperation = isCompoundOperation;
        return this;
    }

    @Override
    public LoadMapSubShortcutNode setReadFrom(boolean isReadFrom) {
        this.isReadFrom = isReadFrom;
        return this;
    }

    @Override
    public LoadMapSubShortcutNode setLocation(Location location) {
        super.setLocation(location);
        return this;
    }

    /* ---- end node data ---- */

    public LoadMapSubShortcutNode() {
        // do nothing
    }

    @Override
    protected void write(ClassWriter classWriter, MethodWriter methodWriter, ScopeTable scopeTable) {
        if (isCompoundOperation()) {
            methodWriter.dup2();
        } else {
            indexNode.write(classWriter, methodWriter, scopeTable);
        }

        methodWriter.writeDebugInfo(location);
        methodWriter.invokeMethodCall(getter);

        if (getter.returnType != getter.javaMethod.getReturnType()) {
            methodWriter.checkCast(MethodWriter.getType(getter.returnType));
        }

        if (isReadFrom()) {
            methodWriter.writeDup(MethodWriter.getType(getType()).getSize(), 2);
        }
    }
}
