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
import org.elasticsearch.painless.symbol.ScriptScope;
import org.objectweb.asm.util.Printer;

import java.util.List;

/**
 * The root node of the compiler tree, representing a compiled Painless script class.
 * {@code bytes} and {@code debugStream} are mutable outputs written by the codegen phase.
 */
public final class ClassNode extends Node {

    private final List<FieldNode> fieldNodes;
    private final List<FunctionNode> functionNodes;
    private final BlockNode clinitBlockNode;
    private final ScriptScope scriptScope;

    // mutable outputs written by the codegen phase
    private Printer debugStream;
    private byte[] bytes;

    public ClassNode(Location location, List<FieldNode> fieldNodes, List<FunctionNode> functionNodes,
                     BlockNode clinitBlockNode, ScriptScope scriptScope) {
        super(location);
        this.fieldNodes = List.copyOf(fieldNodes);
        this.functionNodes = List.copyOf(functionNodes);
        this.clinitBlockNode = clinitBlockNode;
        this.scriptScope = scriptScope;
    }

    public List<FieldNode> getFieldNodes() {
        return fieldNodes;
    }

    public List<FunctionNode> getFunctionNodes() {
        return functionNodes;
    }

    public BlockNode getClinitBlockNode() {
        return clinitBlockNode;
    }

    public ScriptScope getScriptScope() {
        return scriptScope;
    }

    public Printer getDebugStream() {
        return debugStream;
    }

    public void setDebugStream(Printer debugStream) {
        this.debugStream = debugStream;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public ClassNode withClinitBlockNode(BlockNode clinitBlockNode) {
        return new ClassNode(getLocation(), fieldNodes, functionNodes, clinitBlockNode, scriptScope);
    }

    public ClassNode withFunctionNodes(List<FunctionNode> functionNodes) {
        return new ClassNode(getLocation(), fieldNodes, functionNodes, clinitBlockNode, scriptScope);
    }

    public ClassNode withFieldNodes(List<FieldNode> fieldNodes) {
        return new ClassNode(getLocation(), fieldNodes, functionNodes, clinitBlockNode, scriptScope);
    }

    public ClassNode withScriptScope(ScriptScope scriptScope) {
        return new ClassNode(getLocation(), fieldNodes, functionNodes, clinitBlockNode, scriptScope);
    }
}
