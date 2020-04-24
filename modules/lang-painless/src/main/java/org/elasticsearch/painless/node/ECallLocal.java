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

package org.elasticsearch.painless.node;

import org.elasticsearch.painless.Location;
import org.elasticsearch.painless.ir.FieldNode;
import org.elasticsearch.painless.ir.IRNode;
import org.elasticsearch.painless.ir.MemberCallNode;
import org.elasticsearch.painless.lookup.PainlessClassBinding;
import org.elasticsearch.painless.lookup.PainlessInstanceBinding;
import org.elasticsearch.painless.lookup.PainlessMethod;
import org.elasticsearch.painless.phase.DefaultIRTreeBuilderPhase;
import org.elasticsearch.painless.phase.DefaultSemanticAnalysisPhase;
import org.elasticsearch.painless.phase.UserTreeVisitor;
import org.elasticsearch.painless.spi.annotation.NonDeterministicAnnotation;
import org.elasticsearch.painless.symbol.Decorations.Internal;
import org.elasticsearch.painless.symbol.Decorations.Read;
import org.elasticsearch.painless.symbol.Decorations.StandardConstant;
import org.elasticsearch.painless.symbol.Decorations.StandardLocalFunction;
import org.elasticsearch.painless.symbol.Decorations.StandardPainlessClassBinding;
import org.elasticsearch.painless.symbol.Decorations.StandardPainlessInstanceBinding;
import org.elasticsearch.painless.symbol.Decorations.StandardPainlessMethod;
import org.elasticsearch.painless.symbol.Decorations.TargetType;
import org.elasticsearch.painless.symbol.Decorations.ValueType;
import org.elasticsearch.painless.symbol.Decorations.Write;
import org.elasticsearch.painless.symbol.FunctionTable;
import org.elasticsearch.painless.symbol.ScriptScope;
import org.elasticsearch.painless.symbol.SemanticScope;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a user-defined call.
 */
public class ECallLocal extends AExpression {

    private final String methodName;
    private final List<AExpression> argumentNodes;

    public ECallLocal(int identifier, Location location, String methodName, List<AExpression> argumentNodes) {
        super(identifier, location);

        this.methodName = Objects.requireNonNull(methodName);
        this.argumentNodes = Collections.unmodifiableList(Objects.requireNonNull(argumentNodes));
    }

    public String getMethodName() {
        return methodName;
    }

    public List<AExpression> getArgumentNodes() {
        return argumentNodes;
    }

    @Override
    public <Input, Output> Output visit(UserTreeVisitor<Input, Output> userTreeVisitor, Input input) {
        return userTreeVisitor.visitCallLocal(this, input);
    }

    public static void visitDefaultSemanticAnalysis(
            DefaultSemanticAnalysisPhase visitor, ECallLocal userCallLocalNode, SemanticScope semanticScope) {

        String methodName = userCallLocalNode.getMethodName();
        List<AExpression> userArgumentNodes = userCallLocalNode.getArgumentNodes();
        int userArgumentsSize = userArgumentNodes.size();

        if (semanticScope.getCondition(userCallLocalNode, Write.class)) {
            throw userCallLocalNode.createError(new IllegalArgumentException(
                    "invalid assignment: cannot assign a value to function call [" + methodName + "/" + userArgumentsSize + "]"));
        }

        ScriptScope scriptScope = semanticScope.getScriptScope();

        FunctionTable.LocalFunction localFunction = null;
        PainlessMethod importedMethod = null;
        PainlessClassBinding classBinding = null;
        int classBindingOffset = 0;
        PainlessInstanceBinding instanceBinding = null;

        Class<?> valueType;

        localFunction = scriptScope.getFunctionTable().getFunction(methodName, userArgumentsSize);

        // user cannot call internal functions, reset to null if an internal function is found
        if (localFunction != null && localFunction.isInternal()) {
            localFunction = null;
        }

        if (localFunction == null) {
            importedMethod = scriptScope.getPainlessLookup().lookupImportedPainlessMethod(methodName, userArgumentsSize);

            if (importedMethod == null) {
                classBinding = scriptScope.getPainlessLookup().lookupPainlessClassBinding(methodName, userArgumentsSize);

                // check to see if this class binding requires an implicit this reference
                if (classBinding != null && classBinding.typeParameters.isEmpty() == false &&
                        classBinding.typeParameters.get(0) == scriptScope.getScriptClassInfo().getBaseClass()) {
                    classBinding = null;
                }

                if (classBinding == null) {
                    // This extra check looks for a possible match where the class binding requires an implicit this
                    // reference.  This is a temporary solution to allow the class binding access to data from the
                    // base script class without need for a user to add additional arguments.  A long term solution
                    // will likely involve adding a class instance binding where any instance can have a class binding
                    // as part of its API.  However, the situation at run-time is difficult and will modifications that
                    // are a substantial change if even possible to do.
                    classBinding = scriptScope.getPainlessLookup().lookupPainlessClassBinding(methodName, userArgumentsSize + 1);

                    if (classBinding != null) {
                        if (classBinding.typeParameters.isEmpty() == false &&
                                classBinding.typeParameters.get(0) == scriptScope.getScriptClassInfo().getBaseClass()) {
                            classBindingOffset = 1;
                        } else {
                            classBinding = null;
                        }
                    }

                    if (classBinding == null) {
                        instanceBinding = scriptScope.getPainlessLookup().lookupPainlessInstanceBinding(methodName, userArgumentsSize);

                        if (instanceBinding == null) {
                            throw userCallLocalNode.createError(new IllegalArgumentException(
                                    "Unknown call [" + methodName + "] with [" + userArgumentNodes + "] arguments."));
                        }
                    }
                }
            }
        }

        List<Class<?>> typeParameters;

        if (localFunction != null) {
            semanticScope.putDecoration(userCallLocalNode, new StandardLocalFunction(localFunction));

            typeParameters = new ArrayList<>(localFunction.getTypeParameters());
            valueType = localFunction.getReturnType();
        } else if (importedMethod != null) {
            semanticScope.putDecoration(userCallLocalNode, new StandardPainlessMethod(importedMethod));

            scriptScope.markNonDeterministic(importedMethod.annotations.containsKey(NonDeterministicAnnotation.class));
            typeParameters = new ArrayList<>(importedMethod.typeParameters);
            valueType = importedMethod.returnType;
        } else if (classBinding != null) {
            semanticScope.putDecoration(userCallLocalNode, new StandardPainlessClassBinding(classBinding));
            semanticScope.putDecoration(userCallLocalNode, new StandardConstant(classBindingOffset));

            scriptScope.markNonDeterministic(classBinding.annotations.containsKey(NonDeterministicAnnotation.class));
            typeParameters = new ArrayList<>(classBinding.typeParameters);
            valueType = classBinding.returnType;
        } else if (instanceBinding != null) {
            semanticScope.putDecoration(userCallLocalNode, new StandardPainlessInstanceBinding(instanceBinding));

            typeParameters = new ArrayList<>(instanceBinding.typeParameters);
            valueType = instanceBinding.returnType;
        } else {
            throw new IllegalStateException("Illegal tree structure.");
        }
        // if the class binding is using an implicit this reference then the arguments counted must
        // be incremented by 1 as the this reference will not be part of the arguments passed into
        // the class binding call
        for (int argument = 0; argument < userArgumentsSize; ++argument) {
            AExpression userArgumentNode = userArgumentNodes.get(argument);

            semanticScope.setCondition(userArgumentNode, Read.class);
            semanticScope.putDecoration(userArgumentNode, new TargetType(typeParameters.get(argument + classBindingOffset)));
            semanticScope.setCondition(userArgumentNode, Internal.class);
            visitor.checkedVisit(userArgumentNode, semanticScope);
            visitor.decorateWithCast(userArgumentNode, semanticScope);
        }

        semanticScope.putDecoration(userCallLocalNode, new ValueType(valueType));
    }

    public static IRNode visitDefaultIRTreeBuild(DefaultIRTreeBuilderPhase visitor, ECallLocal userCallLocalNode, ScriptScope scriptScope) {
        MemberCallNode irMemberCallNode = new MemberCallNode();

        if (scriptScope.hasDecoration(userCallLocalNode, StandardLocalFunction.class)) {
            irMemberCallNode.setLocalFunction(scriptScope.getDecoration(userCallLocalNode, StandardLocalFunction.class).getLocalFunction());
        } else if (scriptScope.hasDecoration(userCallLocalNode, StandardPainlessMethod.class)) {
            irMemberCallNode.setImportedMethod(
                    scriptScope.getDecoration(userCallLocalNode, StandardPainlessMethod.class).getStandardPainlessMethod());
        } else if (scriptScope.hasDecoration(userCallLocalNode, StandardPainlessClassBinding.class)) {
            PainlessClassBinding painlessClassBinding =
                    scriptScope.getDecoration(userCallLocalNode, StandardPainlessClassBinding.class).getPainlessClassBinding();
            String bindingName = scriptScope.getNextSyntheticName("class_binding");

            FieldNode irFieldNode = new FieldNode();
            irFieldNode.setLocation(userCallLocalNode.getLocation());
            irFieldNode.setModifiers(Modifier.PRIVATE);
            irFieldNode.setFieldType(painlessClassBinding.javaConstructor.getDeclaringClass());
            irFieldNode.setName(bindingName);
            scriptScope.getIRClassNode().addFieldNode(irFieldNode);

            irMemberCallNode.setClassBinding(painlessClassBinding);
            irMemberCallNode.setClassBindingOffset(
                    (int)scriptScope.getDecoration(userCallLocalNode, StandardConstant.class).getStandardConstant());
            irMemberCallNode.setBindingName(bindingName);
        } else if (scriptScope.hasDecoration(userCallLocalNode, StandardPainlessInstanceBinding.class)) {
            PainlessInstanceBinding painlessInstanceBinding =
                    scriptScope.getDecoration(userCallLocalNode, StandardPainlessInstanceBinding.class).getPainlessInstanceBinding();
            String bindingName = scriptScope.getNextSyntheticName("instance_binding");

            FieldNode irFieldNode = new FieldNode();
            irFieldNode.setLocation(userCallLocalNode.getLocation());
            irFieldNode.setModifiers(Modifier.PUBLIC | Modifier.STATIC);
            irFieldNode.setFieldType(painlessInstanceBinding.targetInstance.getClass());
            irFieldNode.setName(bindingName);
            scriptScope.getIRClassNode().addFieldNode(irFieldNode);

            irMemberCallNode.setInstanceBinding(painlessInstanceBinding);
            irMemberCallNode.setBindingName(bindingName);

            scriptScope.addStaticConstant(bindingName, painlessInstanceBinding.targetInstance);
        } else {
            throw userCallLocalNode.createError(new IllegalStateException("illegal tree structure"));
        }

        for (AExpression userArgumentNode : userCallLocalNode.getArgumentNodes()) {
            irMemberCallNode.addArgumentNode(visitor.injectCast(userArgumentNode, scriptScope));
        }

        irMemberCallNode.setLocation(userCallLocalNode.getLocation());
        irMemberCallNode.setExpressionType(scriptScope.getDecoration(userCallLocalNode, ValueType.class).getValueType());

        return irMemberCallNode;
    }
}
