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
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

public class BraceSubNode extends UnaryNode {

    /* ---- begin tree structure ---- */

    @Override
    public BraceSubNode setChildNode(ExpressionNode childNode) {
        super.setChildNode(childNode);
        return this;
    }

    @Override
    public BraceSubNode setTypeNode(TypeNode typeNode) {
        super.setTypeNode(typeNode);
        return this;
    }

    /* ---- end tree structure, begin node data ---- */

    @Override
    public BraceSubNode setLocation(Location location) {
        super.setLocation(location);
        return this;
    }

    /* ---- end node data ---- */

    public BraceSubNode() {
        // do nothing
    }

    @Override
    protected void write(ClassWriter classWriter, MethodWriter methodWriter, Globals globals) {
        setup(classWriter, methodWriter, globals);
        load(classWriter, methodWriter, globals);
    }

    @Override
    protected int accessElementCount() {
        return 2;
    }

    @Override
    protected void setup(ClassWriter classWriter, MethodWriter methodWriter, Globals globals) {
        childNode.write(classWriter, methodWriter, globals);

        Label noFlip = new Label();
        methodWriter.dup();
        methodWriter.ifZCmp(Opcodes.IFGE, noFlip);
        methodWriter.swap();
        methodWriter.dupX1();
        methodWriter.arrayLength();
        methodWriter.visitInsn(Opcodes.IADD);
        methodWriter.mark(noFlip);
    }

    @Override
    protected void load(ClassWriter classWriter, MethodWriter methodWriter, Globals globals) {
        methodWriter.writeDebugInfo(location);
        methodWriter.arrayLoad(MethodWriter.getType(getType()));
    }

    @Override
    protected void store(ClassWriter classWriter, MethodWriter methodWriter, Globals globals) {
        methodWriter.writeDebugInfo(location);
        methodWriter.arrayStore(MethodWriter.getType(getType()));
    }
}
