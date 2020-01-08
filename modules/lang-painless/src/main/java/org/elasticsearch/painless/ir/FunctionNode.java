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
import org.elasticsearch.painless.symbol.ScopeTable.Variable;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FunctionNode extends IRNode {

    /* ---- begin tree structure ---- */

    protected BlockNode blockNode;

    public FunctionNode setBlockNode(BlockNode blockNode) {
        this.blockNode = blockNode;
        return this;
    }

    public BlockNode getBlockNode() {
        return blockNode;
    }

    /* ---- end tree structure, begin node data ---- */

    protected String name;
    Class<?> returnType;
    List<Class<?>> typeParameters = new ArrayList<>();
    List<String> parameterNames = new ArrayList<>();
    protected boolean isStatic;
    protected boolean hasVarArgs;
    protected boolean isSynthetic;
    protected int maxLoopCounter;

    public FunctionNode setName(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return name;
    }

    public FunctionNode setReturnType(Class<?> returnType) {
        this.returnType = returnType;
        return this;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public FunctionNode addTypeParameter(Class<?> typeParameter) {
        typeParameters.add(typeParameter);
        return this;
    }

    public FunctionNode addTypeParameters(Class<?>... typeParameters) {
        this.typeParameters.addAll(Arrays.asList(typeParameters));
        return this;
    }

    public FunctionNode addTypeParameters(List<Class<?>> typeParameters) {
        this.typeParameters.addAll(typeParameters);
        return this;
    }

    public FunctionNode setTypeParameter(int index, Class<?> typeParameter) {
        typeParameters.set(index, typeParameter);
        return this;
    }

    public Class<?> getTypeParameter(int index) {
        return typeParameters.get(index);
    }

    public FunctionNode removeTypeParameter(Class<?> typeParameter) {
        typeParameters.remove(typeParameter);
        return this;
    }

    public FunctionNode removeTypeParameter(int index) {
        typeParameters.remove(index);
        return this;
    }

    public int getTypeParametersSize() {
        return typeParameters.size();
    }

    public List<Class<?>> getTypeParameters() {
        return typeParameters;
    }

    public FunctionNode clearTypeParameters() {
        typeParameters.clear();
        return this;
    }

    public FunctionNode addParameterName(String parameterName) {
        parameterNames.add(parameterName);
        return this;
    }

    public FunctionNode addParameterNames(String... parameterNames) {
        this.parameterNames.addAll(Arrays.asList(parameterNames));
        return this;
    }

    public FunctionNode addParameterNames(List<String> parameterNames) {
        this.parameterNames.addAll(parameterNames);
        return this;
    }

    public FunctionNode setParameterName(int index, String parameterName) {
        parameterNames.set(index, parameterName);
        return this;
    }

    public String getParameterName(int index) {
        return parameterNames.get(index);
    }

    public FunctionNode removeParameterName(String parameterName) {
        parameterNames.remove(parameterName);
        return this;
    }

    public FunctionNode removeParameterName(int index) {
        parameterNames.remove(index);
        return this;
    }

    public int getParameterNamesSize() {
        return parameterNames.size();
    }

    public List<String> getParameterNames() {
        return parameterNames;
    }

    public FunctionNode clearParameterNames() {
        parameterNames.clear();
        return this;
    }

    public FunctionNode setStatic(boolean isStatic) {
        this.isStatic = isStatic;
        return this;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public FunctionNode setVarArgs(boolean hasVarArgs) {
        this.hasVarArgs = hasVarArgs;
        return this;
    }

    public boolean hasVarArgs() {
        return hasVarArgs;
    }

    public FunctionNode setSynthetic(boolean isSythetic) {
        this.isSynthetic = isSythetic;
        return this;
    }

    public boolean isSynthetic() {
        return isSynthetic;
    }

    public FunctionNode setMaxLoopCounter(int maxLoopCounter) {
        this.maxLoopCounter = maxLoopCounter;
        return this;
    }

    public int getMaxLoopCounter() {
        return maxLoopCounter;
    }

    @Override
    public FunctionNode setLocation(Location location) {
        super.setLocation(location);
        return this;
    }

    /* ---- end node data ---- */

    public FunctionNode() {
        // do nothing
    }

    @Override
    protected void write(ClassWriter classWriter, MethodWriter methodWriter, Globals globals, ScopeTable scopeTable) {
        int access = Opcodes.ACC_PUBLIC;

        if (isStatic) {
            access |= Opcodes.ACC_STATIC;
        } else {
            scopeTable.defineVariable(Object.class, "#this");
        }

        if (hasVarArgs) {
            access |= Opcodes.ACC_VARARGS;
        }

        if (isSynthetic) {
            access |= Opcodes.ACC_SYNTHETIC;
        }

        Type asmReturnType = MethodWriter.getType(returnType);
        Type[] asmParameterTypes = new Type[typeParameters.size()];

        for (int index = 0; index < asmParameterTypes.length; ++index) {
            Class<?> type = typeParameters.get(index);
            String name = parameterNames.get(index);
            scopeTable.defineVariable(type, name);
            asmParameterTypes[index] = MethodWriter.getType(typeParameters.get(index));
        }

        Method method = new Method(name, asmReturnType, asmParameterTypes);

        methodWriter = classWriter.newMethodWriter(access, method);
        methodWriter.visitCode();

        if (maxLoopCounter > 0) {
            // if there is infinite loop protection, we do this once:
            // int #loop = settings.getMaxLoopCounter()

            Variable loop = scopeTable.defineVariable(int.class, "#loop");

            methodWriter.push(maxLoopCounter);
            methodWriter.visitVarInsn(Opcodes.ISTORE, loop.getSlot());
        }

        blockNode.write(classWriter, methodWriter, globals, scopeTable.newScope());

        methodWriter.endMethod();
    }
}
