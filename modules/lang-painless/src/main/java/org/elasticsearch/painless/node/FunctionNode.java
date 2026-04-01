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
 */
public final class FunctionNode extends Node {

    private final BlockNode blockNode;
    private final String name;
    private final Class<?> returnType;
    private final List<Class<?>> typeParameters;
    private final List<String> parameterNames;
    private final int modifiers;
    private final boolean isStatic;
    private final boolean isSynthetic;
    private final boolean isVarArgs;
    private final boolean isInstanceCapture;
    private final int maxLoopCounter;

    public FunctionNode(Location location, BlockNode blockNode, String name, Class<?> returnType,
                        List<Class<?>> typeParameters, List<String> parameterNames, int modifiers,
                        boolean isStatic, boolean isSynthetic, boolean isVarArgs,
                        boolean isInstanceCapture, int maxLoopCounter) {
        super(location);
        this.blockNode = blockNode;
        this.name = name;
        this.returnType = returnType;
        this.typeParameters = typeParameters == null ? null : List.copyOf(typeParameters);
        this.parameterNames = parameterNames == null ? null : List.copyOf(parameterNames);
        this.modifiers = modifiers;
        this.isStatic = isStatic;
        this.isSynthetic = isSynthetic;
        this.isVarArgs = isVarArgs;
        this.isInstanceCapture = isInstanceCapture;
        this.maxLoopCounter = maxLoopCounter;
    }

    public BlockNode getBlockNode() { return blockNode; }
    public String getName() { return name; }
    public Class<?> getReturnType() { return returnType; }
    public List<Class<?>> getTypeParameters() { return typeParameters; }
    public List<String> getParameterNames() { return parameterNames; }
    public int getModifiers() { return modifiers; }
    public boolean isStatic() { return isStatic; }
    public boolean isSynthetic() { return isSynthetic; }
    public boolean isVarArgs() { return isVarArgs; }
    public boolean isInstanceCapture() { return isInstanceCapture; }
    public int getMaxLoopCounter() { return maxLoopCounter; }

    public FunctionNode withBlockNode(BlockNode blockNode) {
        return new FunctionNode(getLocation(), blockNode, name, returnType, typeParameters,
            parameterNames, modifiers, isStatic, isSynthetic, isVarArgs, isInstanceCapture, maxLoopCounter);
    }
}
