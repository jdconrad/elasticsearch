/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.painless.node;

import org.elasticsearch.painless.Location;

import java.util.List;

/**
 * Represents a function (method) in a compiled Painless script.
 *
 * <p>Before semantic analysis, {@code returnTypeName} and {@code typeParameterNames} carry
 * the string type names as written in source; {@code returnType} and {@code typeParameters}
 * are null. The SemanticHeaderPhase resolves the names and fills in the {@code Class<?>}
 * fields.
 */
public final class FunctionNode extends Node {

    private final BlockNode blockNode;
    private final String name;
    // resolved by SemanticHeaderPhase; null before that phase
    private final Class<?> returnType;
    private final List<Class<?>> typeParameters;
    // string names from source; set by Walker, used by SemanticHeaderPhase
    private final String returnTypeName;
    private final List<String> typeParameterNames;
    private final List<String> parameterNames;
    private final int modifiers;
    private final boolean isStatic;
    private final boolean isSynthetic;
    private final boolean isVarArgs;
    private final boolean isInstanceCapture;
    private final boolean isAutoReturnEnabled;
    private final boolean isInternal;
    private final int maxLoopCounter;

    public FunctionNode(Location location, BlockNode blockNode, String name, Class<?> returnType,
                        List<Class<?>> typeParameters, String returnTypeName,
                        List<String> typeParameterNames, List<String> parameterNames,
                        int modifiers, boolean isStatic, boolean isSynthetic, boolean isVarArgs,
                        boolean isInstanceCapture, boolean isAutoReturnEnabled, boolean isInternal,
                        int maxLoopCounter) {
        super(location);
        this.blockNode = blockNode;
        this.name = name;
        this.returnType = returnType;
        this.typeParameters = typeParameters == null ? null : List.copyOf(typeParameters);
        this.returnTypeName = returnTypeName;
        this.typeParameterNames = typeParameterNames == null ? null : List.copyOf(typeParameterNames);
        this.parameterNames = parameterNames == null ? null : List.copyOf(parameterNames);
        this.modifiers = modifiers;
        this.isStatic = isStatic;
        this.isSynthetic = isSynthetic;
        this.isVarArgs = isVarArgs;
        this.isInstanceCapture = isInstanceCapture;
        this.isAutoReturnEnabled = isAutoReturnEnabled;
        this.isInternal = isInternal;
        this.maxLoopCounter = maxLoopCounter;
    }

    public BlockNode getBlockNode() { return blockNode; }
    public String getName() { return name; }
    public Class<?> getReturnType() { return returnType; }
    public List<Class<?>> getTypeParameters() { return typeParameters; }
    public String getReturnTypeName() { return returnTypeName; }
    public List<String> getTypeParameterNames() { return typeParameterNames; }
    public List<String> getParameterNames() { return parameterNames; }
    public int getModifiers() { return modifiers; }
    public boolean isStatic() { return isStatic; }
    public boolean isSynthetic() { return isSynthetic; }
    public boolean isVarArgs() { return isVarArgs; }
    public boolean isInstanceCapture() { return isInstanceCapture; }
    public boolean isAutoReturnEnabled() { return isAutoReturnEnabled; }
    public boolean isInternal() { return isInternal; }
    public int getMaxLoopCounter() { return maxLoopCounter; }

    public FunctionNode withBlockNode(BlockNode blockNode) {
        return new FunctionNode(getLocation(), blockNode, name, returnType, typeParameters,
            returnTypeName, typeParameterNames, parameterNames, modifiers, isStatic, isSynthetic,
            isVarArgs, isInstanceCapture, isAutoReturnEnabled, isInternal, maxLoopCounter);
    }

    public FunctionNode withReturnType(Class<?> returnType) {
        return new FunctionNode(getLocation(), blockNode, name, returnType, typeParameters,
            returnTypeName, typeParameterNames, parameterNames, modifiers, isStatic, isSynthetic,
            isVarArgs, isInstanceCapture, isAutoReturnEnabled, isInternal, maxLoopCounter);
    }

    public FunctionNode withTypeParameters(List<Class<?>> typeParameters) {
        return new FunctionNode(getLocation(), blockNode, name, returnType, typeParameters,
            returnTypeName, typeParameterNames, parameterNames, modifiers, isStatic, isSynthetic,
            isVarArgs, isInstanceCapture, isAutoReturnEnabled, isInternal, maxLoopCounter);
    }
}
