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
import org.elasticsearch.painless.lookup.PainlessConstructor;
import org.elasticsearch.painless.lookup.PainlessMethod;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MapInitializationNode extends ExpressionNode {

    protected final Location location;
    protected final PainlessConstructor constructor;
    protected final PainlessMethod method;

    public MapInitializationNode(Location location, PainlessConstructor constructor, PainlessMethod method) {
        this.location = Objects.requireNonNull(location);
        this.constructor = Objects.requireNonNull(constructor);
        this.method = Objects.requireNonNull(method);
    }

    protected final List<ExpressionNode> keyNodes = new ArrayList<>();
    protected final List<ExpressionNode> valueNodes = new ArrayList<>();

    public void addArgumentNode(ExpressionNode keyNode, ExpressionNode valueNode) {
        keyNodes.add(keyNode);
        valueNodes.add(valueNode);
    }

    public void setArgumentNode(int index, ExpressionNode keyNode, ExpressionNode valueNode) {
        keyNodes.set(index, keyNode);
        valueNodes.set(index, valueNode);
    }

    public ExpressionNode getKeyNode(int index) {
        return keyNodes.get(index);
    }

    public ExpressionNode getValueNode(int index) {
        return keyNodes.get(index);
    }

    public ExpressionNode[] getArgumentNode(int index) {
        return new ExpressionNode[] {
                keyNodes.get(index),
                valueNodes.get(index)
        };
    }

    public void removeArgumentNode(int index) {
        keyNodes.remove(index);
        valueNodes.remove(index);
    }

    public int getArgumentsSize() {
        return keyNodes.size();
    }

    public List<ExpressionNode> getKeyNodes() {
        return keyNodes;
    }

    public List<ExpressionNode> getValueNodes() {
        return valueNodes;
    }

    public void clearArgumentNodes() {
        keyNodes.clear();
        valueNodes.clear();
    }

    @Override
    public void write(ClassWriter classWriter, MethodWriter methodWriter, Globals globals) {
        methodWriter.writeDebugInfo(location);

        methodWriter.newInstance(MethodWriter.getType(getType()));
        methodWriter.dup();
        methodWriter.invokeConstructor(
                    Type.getType(constructor.javaConstructor.getDeclaringClass()), Method.getMethod(constructor.javaConstructor));

        for (int index = 0; index < getArgumentsSize(); ++index) {
            methodWriter.dup();
            getKeyNode(index).write(classWriter, methodWriter, globals);
            getValueNode(index).write(classWriter, methodWriter, globals);
            methodWriter.invokeMethodCall(method);
            methodWriter.pop();
        }
    }
}
