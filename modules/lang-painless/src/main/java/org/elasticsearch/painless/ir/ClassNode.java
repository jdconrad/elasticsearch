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
import org.elasticsearch.painless.ScriptClassInfo;
import org.elasticsearch.painless.symbol.ScopeTable;
import org.elasticsearch.painless.symbol.ScriptRoot;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.util.Printer;

import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;

import static org.elasticsearch.painless.WriterConstants.BASE_INTERFACE_TYPE;
import static org.elasticsearch.painless.WriterConstants.CLASS_TYPE;

public class ClassNode extends IRNode {

    /* ---- begin tree structure ---- */

    protected final List<FieldNode> fieldNodes = new ArrayList<>();
    protected final List<FunctionNode> functionNodes = new ArrayList<>();
    protected final FunctionNode clinitNode = new FunctionNode()
            .setBlockNode(new BlockNode()
                    .setLocation(new Location("internal$clinit$blocknode", 0))
                    .setAllEscape(true)
                    .setStatementCount(1)
            )
            .setLocation(new Location("internal$clinit", 0))
            .setName("<clinit>")
            .setReturnType(void.class)
            .setStatic(true)
            .setVarArgs(false)
            .setSynthetic(false)
            .setMaxLoopCounter(0);

    public ClassNode addFieldNode(FieldNode fieldNode) {
        fieldNodes.add(fieldNode);
        return this;
    }

    public ClassNode addFieldNodes(Collection<FieldNode> fieldNodes) {
        this.fieldNodes.addAll(fieldNodes);
        return this;
    }

    public ClassNode setFieldNode(int index, FieldNode fieldNode) {
        fieldNodes.set(index, fieldNode);
        return this;
    }

    public FieldNode getFieldNode(int index) {
        return fieldNodes.get(index);
    }

    public ClassNode removeFieldNode(FieldNode fieldNode) {
        fieldNodes.remove(fieldNode);
        return this;
    }

    public ClassNode removeFieldNode(int index) {
        fieldNodes.remove(index);
        return this;
    }

    public int getFieldsSize() {
        return fieldNodes.size();
    }

    public List<FieldNode> getFieldsNodes() {
        return fieldNodes;
    }

    public ClassNode clearFieldNodes() {
        fieldNodes.clear();
        return this;
    }
    
    public ClassNode addFunctionNode(FunctionNode functionNode) {
        functionNodes.add(functionNode);
        return this;
    }

    public ClassNode addFunctionNode(Collection<FunctionNode> functionNodes) {
        this.functionNodes.addAll(functionNodes);
        return this;
    }

    public ClassNode setFunctionNode(int index, FunctionNode functionNode) {
        functionNodes.set(index, functionNode);
        return this;
    }

    public FunctionNode getFunctionNode(int index) {
        return functionNodes.get(index);
    }

    public ClassNode removeFunctionNode(FunctionNode functionNode) {
        functionNodes.remove(functionNode);
        return this;
    }

    public ClassNode removeFunctionNode(int index) {
        functionNodes.remove(index);
        return this;
    }

    public int getFunctionsSize() {
        return functionNodes.size();
    }

    public List<FunctionNode> getFunctionsNodes() {
        return functionNodes;
    }

    public ClassNode clearFunctionNodes() {
        functionNodes.clear();
        return this;
    }

    public FunctionNode getClinitNode() {
        return clinitNode;
    }

    /* ---- end tree structure, begin node data ---- */

    protected ScriptClassInfo scriptClassInfo;
    protected String name;
    protected String sourceText;
    protected Printer debugStream;
    protected ScriptRoot scriptRoot;

    public ClassNode setScriptClassInfo(ScriptClassInfo scriptClassInfo) {
        this.scriptClassInfo = scriptClassInfo;
        return this;
    }

    public ScriptClassInfo getScriptClassInfo() {
        return scriptClassInfo;
    }

    public ClassNode setName(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return name;
    }

    public ClassNode setSourceText(String sourceText) {
        this.sourceText = sourceText;
        return this;
    }

    public String getSourceText() {
        return sourceText;
    }

    public ClassNode setDebugStream(Printer debugStream) {
        this.debugStream = debugStream;
        return this;
    }

    public Printer getDebugStream() {
        return debugStream;
    }

    public ClassNode setScriptRoot(ScriptRoot scriptRoot) {
        this.scriptRoot = scriptRoot;
        return this;
    }

    public ScriptRoot getScriptRoot() {
        return scriptRoot;
    }

    @Override
    public ClassNode setLocation(Location location) {
        super.setLocation(location);
        return this;
    }

    /* ---- end node data ---- */

    protected Globals globals;

    public ClassNode() {
        // do nothing
    }

    public byte[] write() {
        globals = new Globals(new BitSet(sourceText.length()));
        scriptRoot.addStaticConstant("$STATEMENTS", globals.getStatements());

        // Create the ClassWriter.

        int classFrames = org.objectweb.asm.ClassWriter.COMPUTE_FRAMES | org.objectweb.asm.ClassWriter.COMPUTE_MAXS;
        int classAccess = Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER | Opcodes.ACC_FINAL;
        String interfaceBase = BASE_INTERFACE_TYPE.getInternalName();
        String className = CLASS_TYPE.getInternalName();
        String[] classInterfaces = new String[] { interfaceBase };

        ClassWriter classWriter = new ClassWriter(scriptRoot.getCompilerSettings(), globals.getStatements(), debugStream,
                scriptClassInfo.getBaseClass(), classFrames, classAccess, className, classInterfaces);
        ClassVisitor classVisitor = classWriter.getClassVisitor();
        classVisitor.visitSource(Location.computeSourceName(name), null);

        org.objectweb.asm.commons.Method init;

        if (scriptClassInfo.getBaseClass().getConstructors().length == 0) {
            init = new org.objectweb.asm.commons.Method("<init>", MethodType.methodType(void.class).toMethodDescriptorString());
        } else {
            init = new org.objectweb.asm.commons.Method("<init>", MethodType.methodType(void.class,
                scriptClassInfo.getBaseClass().getConstructors()[0].getParameterTypes()).toMethodDescriptorString());
        }

        // Write the constructor:
        MethodWriter constructor = classWriter.newMethodWriter(Opcodes.ACC_PUBLIC, init);
        constructor.visitCode();
        constructor.loadThis();
        constructor.loadArgs();
        constructor.invokeConstructor(Type.getType(scriptClassInfo.getBaseClass()), init);
        constructor.returnValue();
        constructor.endMethod();

        if (clinitNode.getBlockNode().getStatementsNodes().isEmpty() == false) {
            clinitNode.getBlockNode().addStatementNode(new ReturnNode()
                    .setLocation(new Location("internal$clinit$return", 0))
            );
            clinitNode.write(classWriter, null, globals, new ScopeTable());
        }

        // Write all fields:
        for (FieldNode fieldNode : fieldNodes) {
            fieldNode.write(classWriter, null, null, null);
        }

        // Write all functions:
        for (FunctionNode functionNode : functionNodes) {
            functionNode.write(classWriter, null, globals, new ScopeTable());
        }

        // Write the constants
        /*if (false == globals.getConstantInitializers().isEmpty()) {
            Collection<Constant> inits = globals.getConstantInitializers().values();

            // Initialize the constants in a static initializerNode
            final MethodWriter clinit = new MethodWriter(Opcodes.ACC_STATIC,
                    WriterConstants.CLINIT, classVisitor, globals.getStatements(), scriptRoot.getCompilerSettings());
            clinit.visitCode();
            for (Constant constant : inits) {
                constant.initializer.accept(clinit);
                clinit.putStatic(CLASS_TYPE, constant.name, constant.type);
            }
            clinit.returnValue();
            clinit.endMethod();
        }*/

        // End writing the class and store the generated bytes.

        classVisitor.visitEnd();
        return classWriter.getClassBytes();
    }
}
