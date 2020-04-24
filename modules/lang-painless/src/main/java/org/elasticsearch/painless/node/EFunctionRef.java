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

import org.elasticsearch.painless.FunctionRef;
import org.elasticsearch.painless.Location;
import org.elasticsearch.painless.ir.DefInterfaceReferenceNode;
import org.elasticsearch.painless.ir.IRNode;
import org.elasticsearch.painless.ir.ReferenceNode;
import org.elasticsearch.painless.ir.TypedCaptureReferenceNode;
import org.elasticsearch.painless.ir.TypedInterfaceReferenceNode;
import org.elasticsearch.painless.lookup.def;
import org.elasticsearch.painless.phase.DefaultIRTreeBuilderPhase;
import org.elasticsearch.painless.phase.DefaultSemanticAnalysisPhase;
import org.elasticsearch.painless.phase.UserTreeVisitor;
import org.elasticsearch.painless.symbol.Decorations.CapturesDecoration;
import org.elasticsearch.painless.symbol.Decorations.EncodingDecoration;
import org.elasticsearch.painless.symbol.Decorations.Read;
import org.elasticsearch.painless.symbol.Decorations.ReferenceDecoration;
import org.elasticsearch.painless.symbol.Decorations.TargetType;
import org.elasticsearch.painless.symbol.Decorations.ValueType;
import org.elasticsearch.painless.symbol.Decorations.Write;
import org.elasticsearch.painless.symbol.ScriptScope;
import org.elasticsearch.painless.symbol.SemanticScope;

import java.util.Collections;
import java.util.Objects;

/**
 * Represents a function reference.
 */
public class EFunctionRef extends AExpression {

    private final String symbol;
    private final String methodName;

    public EFunctionRef(int identifier, Location location, String symbol, String methodName) {
        super(identifier, location);

        this.symbol = Objects.requireNonNull(symbol);
        this.methodName = Objects.requireNonNull(methodName);
    }

    public String getSymbol() {
        return symbol;
    }

    public String getMethodName() {
        return methodName;
    }

    @Override
    public <Input, Output> Output visit(UserTreeVisitor<Input, Output> userTreeVisitor, Input input) {
        return userTreeVisitor.visitFunctionRef(this, input);
    }

    public static void visitDefaultSemanticAnalysis(
            DefaultSemanticAnalysisPhase visitor, EFunctionRef userFunctionRefNode, SemanticScope semanticScope) {

        ScriptScope scriptScope = semanticScope.getScriptScope();

        Location location = userFunctionRefNode.getLocation();
        String symbol = userFunctionRefNode.getSymbol();
        String methodName = userFunctionRefNode.getMethodName();
        boolean read = semanticScope.getCondition(userFunctionRefNode, Read.class);

        Class<?> type = scriptScope.getPainlessLookup().canonicalTypeNameToType(symbol);
        TargetType targetType = semanticScope.getDecoration(userFunctionRefNode, TargetType.class);
        Class<?> valueType;

        if (symbol.equals("this") || type != null)  {
            if (semanticScope.getCondition(userFunctionRefNode, Write.class)) {
                throw userFunctionRefNode.createError(new IllegalArgumentException(
                        "invalid assignment: cannot assign a value to function reference [" + symbol + ":" + methodName + "]"));
            }

            if (read == false) {
                throw userFunctionRefNode.createError(new IllegalArgumentException(
                        "not a statement: function reference [" + symbol + ":" + methodName + "] not used"));
            }

            if (targetType == null) {
                valueType = String.class;
                String defReferenceEncoding = "S" + symbol + "." + methodName + ",0";
                semanticScope.putDecoration(userFunctionRefNode, new EncodingDecoration(defReferenceEncoding));
            } else {
                FunctionRef ref = FunctionRef.create(scriptScope.getPainlessLookup(), scriptScope.getFunctionTable(),
                        location, targetType.getTargetType(), symbol, methodName, 0);
                valueType = targetType.getTargetType();
                semanticScope.putDecoration(userFunctionRefNode, new ReferenceDecoration(ref));
            }
        } else {
            if (semanticScope.getCondition(userFunctionRefNode, Write.class)) {
                throw userFunctionRefNode.createError(new IllegalArgumentException(
                        "invalid assignment: cannot assign a value to capturing function reference [" + symbol + ":"  + methodName + "]"));
            }

            if (read == false) {
                throw userFunctionRefNode.createError(new IllegalArgumentException(
                        "not a statement: capturing function reference [" + symbol + ":"  + methodName + "] not used"));
            }

            SemanticScope.Variable captured = semanticScope.getVariable(location, symbol);
            semanticScope.putDecoration(userFunctionRefNode, new CapturesDecoration(Collections.singletonList(captured)));
            if (targetType == null) {
                String defReferenceEncoding;
                if (captured.getType() == def.class) {
                    // dynamic implementation
                    defReferenceEncoding = "D" + symbol + "." + methodName + ",1";
                } else {
                    // typed implementation
                    defReferenceEncoding = "S" + captured.getCanonicalTypeName() + "." + methodName + ",1";
                }
                valueType = String.class;
                semanticScope.putDecoration(userFunctionRefNode, new EncodingDecoration(defReferenceEncoding));
            } else {
                valueType = targetType.getTargetType();
                // static case
                if (captured.getType() != def.class) {
                    FunctionRef ref = FunctionRef.create(scriptScope.getPainlessLookup(), scriptScope.getFunctionTable(), location,
                            targetType.getTargetType(), captured.getCanonicalTypeName(), methodName, 1);
                    semanticScope.putDecoration(userFunctionRefNode, new ReferenceDecoration(ref));
                }
            }
        }

        semanticScope.putDecoration(userFunctionRefNode, new ValueType(valueType));
    }

    public static IRNode visitDefaultIRTreeBuild(
            DefaultIRTreeBuilderPhase visitor, EFunctionRef userFunctionRefNode, ScriptScope scriptScope) {

        ReferenceNode irReferenceNode;

        TargetType targetType = scriptScope.getDecoration(userFunctionRefNode, TargetType.class);
        CapturesDecoration capturesDecoration = scriptScope.getDecoration(userFunctionRefNode, CapturesDecoration.class);

        if (targetType == null) {
            DefInterfaceReferenceNode defInterfaceReferenceNode = new DefInterfaceReferenceNode();
            defInterfaceReferenceNode.setDefReferenceEncoding(
                    scriptScope.getDecoration(userFunctionRefNode, EncodingDecoration.class).getEncoding());
            irReferenceNode = defInterfaceReferenceNode;
        } else if (capturesDecoration != null && capturesDecoration.getCaptures().get(0).getType() == def.class) {
            TypedCaptureReferenceNode typedCaptureReferenceNode = new TypedCaptureReferenceNode();
            typedCaptureReferenceNode.setMethodName(userFunctionRefNode.getMethodName());
            irReferenceNode = typedCaptureReferenceNode;
        } else {
            TypedInterfaceReferenceNode typedInterfaceReferenceNode = new TypedInterfaceReferenceNode();
            typedInterfaceReferenceNode.setReference(
                    scriptScope.getDecoration(userFunctionRefNode, ReferenceDecoration.class).getReference());
            irReferenceNode = typedInterfaceReferenceNode;
        }

        irReferenceNode.setLocation(userFunctionRefNode.getLocation());
        irReferenceNode.setExpressionType(scriptScope.getDecoration(userFunctionRefNode, ValueType.class).getValueType());

        if (capturesDecoration != null) {
            irReferenceNode.addCapture(capturesDecoration.getCaptures().get(0).getName());
        }

        return irReferenceNode;
    }
}
