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
import org.elasticsearch.painless.MethodWriter;
import org.elasticsearch.painless.symbol.ScopeTable;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.List;

import static org.elasticsearch.painless.symbol.ScopeTable.Variable;

public class AccessDefCallNode extends ArgumentsNode {

    /* ---- begin node data ---- */

    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /* ---- end node data ---- */

    @Override
    protected void write(ClassWriter classWriter, MethodWriter methodWriter, ScopeTable scopeTable) {
        methodWriter.writeDebugInfo(location);

        StringBuilder defCallRecipe = new StringBuilder();
        List<Object> boostrapArguments = new ArrayList<>();
        List<Class<?>> typeParameters = new ArrayList<>();
        int capturedCount = 0;

        typeParameters.add(Object.class);

        for (int i = 0; i < getArgumentNodes().size(); ++i) {
            ExpressionNode argumentNode = getArgumentNodes().get(i);
            argumentNode.write(classWriter, methodWriter, scopeTable);

            typeParameters.add(argumentNode.getExpressionType());

            if (argumentNode instanceof DefInterfaceReferenceNode) {
                DefInterfaceReferenceNode defInterfaceReferenceNode = (DefInterfaceReferenceNode)argumentNode;
                boostrapArguments.add(defInterfaceReferenceNode.getDefReferenceEncoding());

                char encoding = (char)(i + capturedCount);
                defCallRecipe.append(encoding);
                capturedCount += defInterfaceReferenceNode.getCaptures().size();

                for (String capturedName : defInterfaceReferenceNode.getCaptures()) {
                    Variable capturedVariable = scopeTable.getVariable(capturedName);
                    typeParameters.add(capturedVariable.getType());
                }
            }
        }

        Type[] asmParameterTypes = new Type[typeParameters.size()];

        for (int index = 0; index < asmParameterTypes.length; ++index) {
            asmParameterTypes[index] = MethodWriter.getType(typeParameters.get(index));
        }

        Type methodType = Type.getMethodType(MethodWriter.getType(getExpressionType()), asmParameterTypes);

        boostrapArguments.add(0, defCallRecipe.toString());
        methodWriter.invokeDefCall(name, methodType, DefBootstrap.METHOD_CALL, boostrapArguments.toArray());
    }
}
