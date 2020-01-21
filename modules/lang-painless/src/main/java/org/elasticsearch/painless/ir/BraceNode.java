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

public class BraceNode extends PrefixNode {

    /* ---- begin tree structure ---- */

    @Override
    public BraceNode setPrefixNode(ExpressionNode prefixNode) {
        this.prefixNode = prefixNode;
        return this;
    }

    @Override
    public BraceNode setChildNode(ExpressionNode childNode) {
        super.setChildNode(childNode);
        return this;
    }

    @Override
    public BraceNode setTypeNode(TypeNode typeNode) {
        super.setTypeNode(typeNode);
        return this;
    }

    /* ---- end tree structure, begin node data ---- */

    @Override
    public BraceNode setLocation(Location location) {
        super.setLocation(location);
        return this;
    }

    /* ---- end node data ---- */

    public BraceNode() {
        // do nothing
    }

    @Override
    protected void write(ClassWriter classWriter, MethodWriter methodWriter, ScopeTable scopeTable) {
        methodWriter.writeDebugInfo(location);
        prefixNode.write(classWriter, methodWriter, scopeTable);
        childNode.write(classWriter, methodWriter, scopeTable);
    }
}
