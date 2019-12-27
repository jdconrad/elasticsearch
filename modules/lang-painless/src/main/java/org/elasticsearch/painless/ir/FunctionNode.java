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
import org.elasticsearch.painless.symbol.FunctionTable.LocalFunction;
import org.elasticsearch.painless.symbol.ScopeTable;
import org.elasticsearch.painless.symbol.ScopeTable.Variable;
import org.elasticsearch.painless.symbol.ScriptRoot;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.elasticsearch.painless.WriterConstants.BASE_INTERFACE_TYPE;
import static org.elasticsearch.painless.WriterConstants.BOOTSTRAP_METHOD_ERROR_TYPE;
import static org.elasticsearch.painless.WriterConstants.CLASS_TYPE;
import static org.elasticsearch.painless.WriterConstants.COLLECTIONS_TYPE;
import static org.elasticsearch.painless.WriterConstants.CONVERT_TO_SCRIPT_EXCEPTION_METHOD;
import static org.elasticsearch.painless.WriterConstants.DEFINITION_TYPE;
import static org.elasticsearch.painless.WriterConstants.EMPTY_MAP_METHOD;
import static org.elasticsearch.painless.WriterConstants.EXCEPTION_TYPE;
import static org.elasticsearch.painless.WriterConstants.OUT_OF_MEMORY_ERROR_TYPE;
import static org.elasticsearch.painless.WriterConstants.PAINLESS_ERROR_TYPE;
import static org.elasticsearch.painless.WriterConstants.PAINLESS_EXPLAIN_ERROR_GET_HEADERS_METHOD;
import static org.elasticsearch.painless.WriterConstants.PAINLESS_EXPLAIN_ERROR_TYPE;
import static org.elasticsearch.painless.WriterConstants.STACK_OVERFLOW_ERROR_TYPE;

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

    protected ScriptRoot scriptRoot;
    protected String name;
    Class<?> returnType;
    List<Class<?>> typeParameters = new ArrayList<>();
    List<String> parameterNames = new ArrayList<>();
    protected boolean isStatic;
    protected boolean isSynthetic;
    protected boolean doAutoReturn;
    protected boolean doesMethodEscape;
    protected int maxLoopCounter;

    public FunctionNode setScriptRoot(ScriptRoot scriptRoot) {
        this.scriptRoot = scriptRoot;
        return this;
    }

    public ScriptRoot getScriptRoot() {
        return scriptRoot;
    }

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

    public FunctionNode setSynthetic(boolean isSythetic) {
        this.isSynthetic = isSythetic;
        return this;
    }

    public boolean isSynthetic() {
        return isSynthetic;
    }

    public FunctionNode setAutoReturn(boolean doAutoReturn) {
        this.doAutoReturn = doAutoReturn;
        return this;
    }

    public boolean doAutoReturn() {
        return doAutoReturn;
    }

    public FunctionNode setMethodEscape(boolean doesMethodEscape) {
        this.doesMethodEscape = doesMethodEscape;
        return this;
    }

    public boolean doesMethodEscape() {
        return doesMethodEscape;
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

        // TODO: do not specialize for execute
        Label startTry = new Label();
        Label endTry = new Label();
        Label startExplainCatch = new Label();
        Label startOtherCatch = new Label();
        Label endCatch = new Label();

        if ("execute".equals(name)) {
            methodWriter.mark(startTry);

            int statementIndex = 0;

            while (statementIndex < blockNode.getStatementsNodes().size()) {
                StatementNode statementNode = blockNode.getStatementNode(statementIndex);

                if (statementNode instanceof DeclarationNode) {
                    DeclarationNode declarationNode = (DeclarationNode) statementNode;
                    boolean isRemoved = false;

                    for (int getIndex = 0; getIndex < scriptRoot.getScriptClassInfo().getGetMethods().size(); ++getIndex) {
                        Class<?> returnType = scriptRoot.getScriptClassInfo().getGetReturns().get(getIndex);
                        Method getMethod = scriptRoot.getScriptClassInfo().getGetMethods().get(getIndex);
                        String name = getMethod.getName().substring(3);
                        name = Character.toLowerCase(name.charAt(0)) + name.substring(1);

                        if (name.equals(declarationNode.getName())) {
                            if (scriptRoot.getUsedVariables().contains(name)) {
                                declarationNode.setExpressionNode(new UnboundCallNode()
                                        .setTypeNode(new TypeNode()
                                                .setLocation(declarationNode.getLocation())
                                                .setType(declarationNode.getDeclarationType())
                                        )
                                        .setLocation(declarationNode.getLocation())
                                        .setLocalFunction(new LocalFunction(
                                                getMethod.getName(), returnType, Collections.emptyList(), true, false
                                        ))
                                );
                            } else {
                                blockNode.removeStatementNode(statementIndex);
                                isRemoved = true;
                            }

                            break;
                        }
                    }

                    if (isRemoved == false) {
                        ++statementIndex;
                    }
                } else {
                    ++statementIndex;
                }
            }
        }
        // TODO: end

        if (maxLoopCounter > 0) {
            // if there is infinite loop protection, we do this once:
            // int #loop = settings.getMaxLoopCounter()

            Variable loop = scopeTable.defineVariable(int.class, "#loop");

            methodWriter.push(maxLoopCounter);
            methodWriter.visitVarInsn(Opcodes.ISTORE, loop.getSlot());
        }

        blockNode.write(classWriter, methodWriter, globals, scopeTable);

        if (doesMethodEscape == false) {
            if (returnType == void.class) {
                methodWriter.returnValue();
            } else if (doAutoReturn) {
                if (returnType == boolean.class) {
                    methodWriter.push(false);
                } else if (returnType == byte.class || returnType == char.class || returnType == short.class || returnType == int.class) {
                    methodWriter.push(0);
                } else if (returnType == long.class) {
                    methodWriter.push(0L);
                } else if (returnType == float.class) {
                    methodWriter.push(0f);
                } else if (returnType == double.class) {
                    methodWriter.push(0d);
                } else {
                    methodWriter.visitInsn(Opcodes.ACONST_NULL);
                }

                methodWriter.returnValue();
            } else {
                throw getLocation().createError(new IllegalStateException("not all paths provide a return value " +
                        "for function [" + name + "] with [" + typeParameters.size() + "] parameters"));
            }
        }

        // TODO: do not specialize for execute
        if ("execute".equals(name)) {
            methodWriter.mark(endTry);
            methodWriter.goTo(endCatch);
            // This looks like:
            // } catch (PainlessExplainError e) {
            //   throw this.convertToScriptException(e, e.getHeaders($DEFINITION))
            // }
            methodWriter.visitTryCatchBlock(startTry, endTry, startExplainCatch, PAINLESS_EXPLAIN_ERROR_TYPE.getInternalName());
            methodWriter.mark(startExplainCatch);
            methodWriter.loadThis();
            methodWriter.swap();
            methodWriter.dup();
            methodWriter.getStatic(CLASS_TYPE, "$DEFINITION", DEFINITION_TYPE);
            methodWriter.invokeVirtual(PAINLESS_EXPLAIN_ERROR_TYPE, PAINLESS_EXPLAIN_ERROR_GET_HEADERS_METHOD);
            methodWriter.invokeInterface(BASE_INTERFACE_TYPE, CONVERT_TO_SCRIPT_EXCEPTION_METHOD);
            methodWriter.throwException();
            // This looks like:
            // } catch (PainlessError | BootstrapMethodError | OutOfMemoryError | StackOverflowError | Exception e) {
            //   throw this.convertToScriptException(e, e.getHeaders())
            // }
            // We *think* it is ok to catch OutOfMemoryError and StackOverflowError because Painless is stateless
            methodWriter.visitTryCatchBlock(startTry, endTry, startOtherCatch, PAINLESS_ERROR_TYPE.getInternalName());
            methodWriter.visitTryCatchBlock(startTry, endTry, startOtherCatch, BOOTSTRAP_METHOD_ERROR_TYPE.getInternalName());
            methodWriter.visitTryCatchBlock(startTry, endTry, startOtherCatch, OUT_OF_MEMORY_ERROR_TYPE.getInternalName());
            methodWriter.visitTryCatchBlock(startTry, endTry, startOtherCatch, STACK_OVERFLOW_ERROR_TYPE.getInternalName());
            methodWriter.visitTryCatchBlock(startTry, endTry, startOtherCatch, EXCEPTION_TYPE.getInternalName());
            methodWriter.mark(startOtherCatch);
            methodWriter.loadThis();
            methodWriter.swap();
            methodWriter.invokeStatic(COLLECTIONS_TYPE, EMPTY_MAP_METHOD);
            methodWriter.invokeInterface(BASE_INTERFACE_TYPE, CONVERT_TO_SCRIPT_EXCEPTION_METHOD);
            methodWriter.throwException();
            methodWriter.mark(endCatch);
        }
        // TODO: end

        methodWriter.endMethod();
    }
}
