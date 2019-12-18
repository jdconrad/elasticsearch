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
import org.elasticsearch.painless.MethodWriter;

public class BraceNode extends PrefixNode {

    public BraceNode() {
        // do nothing
    }

    @Override
    protected void write(ClassWriter classWriter, MethodWriter methodWriter, Globals globals) {
        prefixNode.write(classWriter, methodWriter, globals);
        childNode.write(classWriter, methodWriter, globals);
    }

    @Override
    protected int accessElementCount() {
        return childNode.accessElementCount();
    }

    @Override
    protected void setup(ClassWriter classWriter, MethodWriter methodWriter, Globals globals) {
        prefixNode.write(classWriter, methodWriter, globals);
        childNode.setup(classWriter, methodWriter, globals);
    }

    @Override
    protected void load(ClassWriter classWriter, MethodWriter methodWriter, Globals globals) {
        childNode.load(classWriter, methodWriter, globals);
    }

    @Override
    protected void store(ClassWriter classWriter, MethodWriter methodWriter, Globals globals) {
        childNode.store(classWriter, methodWriter, globals);
    }
}
