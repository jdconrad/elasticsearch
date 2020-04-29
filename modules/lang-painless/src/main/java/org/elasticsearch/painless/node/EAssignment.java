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


import org.elasticsearch.painless.AnalyzerCaster;
import org.elasticsearch.painless.DefBootstrap;
import org.elasticsearch.painless.Location;
import org.elasticsearch.painless.MethodWriter;
import org.elasticsearch.painless.Operation;
import org.elasticsearch.painless.ir.BinaryMathNode;
import org.elasticsearch.painless.ir.CastNode;
import org.elasticsearch.painless.ir.DupNode;
import org.elasticsearch.painless.ir.ExpressionNode;
import org.elasticsearch.painless.ir.IRNode;
import org.elasticsearch.painless.ir.StoreNode;
import org.elasticsearch.painless.lookup.PainlessCast;
import org.elasticsearch.painless.lookup.PainlessLookupUtility;
import org.elasticsearch.painless.lookup.def;
import org.elasticsearch.painless.phase.DefaultIRTreeBuilderPhase;
import org.elasticsearch.painless.phase.DefaultSemanticAnalysisPhase;
import org.elasticsearch.painless.phase.UserTreeVisitor;
import org.elasticsearch.painless.symbol.Decorations;
import org.elasticsearch.painless.symbol.Decorations.AccessDepth;
import org.elasticsearch.painless.symbol.Decorations.Compound;
import org.elasticsearch.painless.symbol.Decorations.CompoundType;
import org.elasticsearch.painless.symbol.Decorations.Concatenate;
import org.elasticsearch.painless.symbol.Decorations.DefOptimized;
import org.elasticsearch.painless.symbol.Decorations.DowncastPainlessCast;
import org.elasticsearch.painless.symbol.Decorations.Explicit;
import org.elasticsearch.painless.symbol.Decorations.Read;
import org.elasticsearch.painless.symbol.Decorations.TargetType;
import org.elasticsearch.painless.symbol.Decorations.UpcastPainlessCast;
import org.elasticsearch.painless.symbol.Decorations.ValueType;
import org.elasticsearch.painless.symbol.Decorations.Write;
import org.elasticsearch.painless.symbol.ScriptScope;
import org.elasticsearch.painless.symbol.SemanticScope;

import java.util.Objects;

/**
 * Represents an assignment with the lhs and rhs as child nodes.
 */
public class EAssignment extends AExpression {

    private final AExpression leftNode;
    private final AExpression rightNode;
    private final boolean postIfRead;
    private final Operation operation;

    public EAssignment(int identifier, Location location,
            AExpression leftNode, AExpression rightNode, boolean postIfRead, Operation operation) {

        super(identifier, location);

        this.leftNode = Objects.requireNonNull(leftNode);
        this.rightNode = Objects.requireNonNull(rightNode);
        this.postIfRead = postIfRead;
        this.operation = operation;
    }

    public AExpression getLeftNode() {
        return leftNode;
    }

    public AExpression getRightNode() {
        return rightNode;
    }

    public boolean postIfRead() {
        return postIfRead;
    }

    public Operation getOperation() {
        return operation;
    }

    @Override
    public <Input, Output> Output visit(UserTreeVisitor<Input, Output> userTreeVisitor, Input input) {
        return userTreeVisitor.visitAssignment(this, input);
    }

    public static void visitDefaultSemanticAnalysis(
            DefaultSemanticAnalysisPhase visitor, EAssignment userAssignmentNode, SemanticScope semanticScope) {

        AExpression userLeftNode = userAssignmentNode.getLeftNode();
        semanticScope.replicateCondition(userAssignmentNode, userLeftNode, Read.class);
        semanticScope.setCondition(userLeftNode, Write.class);
        visitor.checkedVisit(userLeftNode, semanticScope);
        Class<?> leftValueType = semanticScope.getDecoration(userLeftNode, Decorations.ValueType.class).getValueType();

        AExpression userRightNode = userAssignmentNode.getRightNode();
        semanticScope.setCondition(userRightNode, Read.class);

        Operation operation = userAssignmentNode.getOperation();

        if (operation != null) {
            visitor.checkedVisit(userRightNode, semanticScope);
            Class<?> rightValueType = semanticScope.getDecoration(userRightNode, ValueType.class).getValueType();

            Class<?> compoundType;
            Class<?> shiftType = null;
            boolean isShift = false;

            if (operation == Operation.MUL) {
                compoundType = AnalyzerCaster.promoteNumeric(leftValueType, rightValueType, true);
            } else if (operation == Operation.DIV) {
                compoundType = AnalyzerCaster.promoteNumeric(leftValueType, rightValueType, true);
            } else if (operation == Operation.REM) {
                compoundType = AnalyzerCaster.promoteNumeric(leftValueType, rightValueType, true);
            } else if (operation == Operation.ADD) {
                compoundType = AnalyzerCaster.promoteAdd(leftValueType, rightValueType);
            } else if (operation == Operation.SUB) {
                compoundType = AnalyzerCaster.promoteNumeric(leftValueType, rightValueType, true);
            } else if (operation == Operation.LSH) {
                compoundType = AnalyzerCaster.promoteNumeric(leftValueType, false);
                shiftType = AnalyzerCaster.promoteNumeric(rightValueType, false);
                isShift = true;
            } else if (operation == Operation.RSH) {
                compoundType = AnalyzerCaster.promoteNumeric(leftValueType, false);
                shiftType = AnalyzerCaster.promoteNumeric(rightValueType, false);
                isShift = true;
            } else if (operation == Operation.USH) {
                compoundType = AnalyzerCaster.promoteNumeric(leftValueType, false);
                shiftType = AnalyzerCaster.promoteNumeric(rightValueType, false);
                isShift = true;
            } else if (operation == Operation.BWAND) {
                compoundType = AnalyzerCaster.promoteXor(leftValueType, rightValueType);
            } else if (operation == Operation.XOR) {
                compoundType = AnalyzerCaster.promoteXor(leftValueType, rightValueType);
            } else if (operation == Operation.BWOR) {
                compoundType = AnalyzerCaster.promoteXor(leftValueType, rightValueType);
            } else {
                throw userAssignmentNode.createError(new IllegalStateException("illegal tree structure"));
            }

            if (compoundType == null || (isShift && shiftType == null)) {
                throw userAssignmentNode.createError(new ClassCastException("invalid compound assignment: " +
                        "cannot apply [" + operation.symbol + "=] to types [" + leftValueType + "] and [" + rightValueType + "]"));
            }

            boolean cat = operation == Operation.ADD && compoundType == String.class;

            if (cat && userRightNode instanceof EBinary &&
                    ((EBinary)userRightNode).getOperation() == Operation.ADD && rightValueType == String.class) {
                semanticScope.setCondition(userRightNode, Concatenate.class);
            }

            if (isShift) {
                if (compoundType == def.class) {
                    // shifts are promoted independently, but for the def type, we need object.
                    semanticScope.putDecoration(userRightNode, new TargetType(def.class));
                } else if (shiftType == long.class) {
                    semanticScope.putDecoration(userRightNode, new TargetType(int.class));
                    semanticScope.setCondition(userRightNode, Explicit.class);
                } else {
                    semanticScope.putDecoration(userRightNode, new TargetType(shiftType));
                }
            } else {
                semanticScope.putDecoration(userRightNode, new TargetType(compoundType));
            }

            visitor.decorateWithCast(userRightNode, semanticScope);

            Location location = userAssignmentNode.getLocation();
            PainlessCast upcast = AnalyzerCaster.getLegalCast(location, leftValueType, compoundType, false, false);
            PainlessCast downcast = AnalyzerCaster.getLegalCast(location, compoundType, leftValueType, true, false);

            semanticScope.putDecoration(userAssignmentNode, new CompoundType(compoundType));

            if (cat) {
                semanticScope.setCondition(userAssignmentNode, Concatenate.class);
            }

            if (upcast != null) {
                semanticScope.putDecoration(userAssignmentNode, new UpcastPainlessCast(upcast));
            }

            if (downcast != null) {
                semanticScope.putDecoration(userAssignmentNode, new DowncastPainlessCast(downcast));
            }
        // if the lhs node is a def optimized node we update the actual type to remove the need for a cast
        } else if (semanticScope.getCondition(userLeftNode, DefOptimized.class)) {
            visitor.checkedVisit(userRightNode, semanticScope);
            Class<?> rightValueType = semanticScope.getDecoration(userRightNode, ValueType.class).getValueType();

            if (rightValueType == void.class) {
                throw userAssignmentNode.createError(new IllegalArgumentException(
                        "invalid assignment: cannot assign type [" + PainlessLookupUtility.typeToCanonicalTypeName(void.class) + "]"));
            }

            semanticScope.putDecoration(userLeftNode, new ValueType(rightValueType));
            leftValueType = rightValueType;
        // Otherwise, we must adapt the rhs type to the lhs type with a cast.
        } else {
            semanticScope.putDecoration(userRightNode, new TargetType(leftValueType));
            visitor.checkedVisit(userRightNode, semanticScope);
            visitor.decorateWithCast(userRightNode, semanticScope);
        }

        semanticScope.putDecoration(userAssignmentNode,
                new ValueType(semanticScope.getCondition(userAssignmentNode, Read.class) ? leftValueType : void.class));
    }

    public static IRNode visitDefaultIRTreeBuild(
            DefaultIRTreeBuilderPhase visitor, EAssignment userAssignmentNode, ScriptScope scriptScope) {

        boolean read = scriptScope.getCondition(userAssignmentNode, Read.class);
        Class<?> compoundType = scriptScope.hasDecoration(userAssignmentNode, CompoundType.class) ?
                scriptScope.getDecoration(userAssignmentNode, CompoundType.class).getCompoundType() : null;

        StoreNode storeNode;
        ExpressionNode valueNode = visitor.injectCast(userAssignmentNode.getRightNode(), scriptScope);

        if (compoundType != null) {
            scriptScope.setCondition(userAssignmentNode.getLeftNode(), Compound.class);
            storeNode = (StoreNode)visitor.visit(userAssignmentNode.getLeftNode(), scriptScope);
            BinaryMathNode irBinaryMathNode = (BinaryMathNode)storeNode.getChildNode();

            PainlessCast downcast = scriptScope.hasDecoration(userAssignmentNode, DowncastPainlessCast.class) ?
                    scriptScope.getDecoration(userAssignmentNode, DowncastPainlessCast.class).getDowncastPainlessCast() : null;

            if (downcast == null) {
                irBinaryMathNode.setExpressionType(storeNode.getStoreType());
            } else {
                CastNode irCastNode = new CastNode();
                irCastNode.setLocation(irBinaryMathNode.getLocation());
                irCastNode.setExpressionType(downcast.targetType);
                irCastNode.setCast(downcast);
                irCastNode.setChildNode(irBinaryMathNode);
                storeNode.setChildNode(irCastNode);
            }

            if (read) {
                int accessDepth = scriptScope.getDecoration(userAssignmentNode, AccessDepth.class).getAccessDepth();

                DupNode irDupNode = new DupNode();

                if (userAssignmentNode.postIfRead()) {
                    ExpressionNode irLoadNode = irBinaryMathNode.getLeftNode();
                    irDupNode.setLocation(irLoadNode.getLocation());
                    irDupNode.setExpressionType(irLoadNode.getExpressionType());
                    irDupNode.setSize(MethodWriter.getType(irLoadNode.getExpressionType()).getSize());
                    irDupNode.setDepth(accessDepth);
                    irDupNode.setChildNode(irLoadNode);
                    irBinaryMathNode.setLeftNode(irDupNode);
                } else {
                    irDupNode.setLocation(storeNode.getLocation());
                    irDupNode.setExpressionType(storeNode.getStoreType());
                    irDupNode.setSize(MethodWriter.getType(storeNode.getExpressionType()).getSize());
                    irDupNode.setDepth(accessDepth);
                    irDupNode.setChildNode(storeNode.getChildNode());
                    storeNode.setChildNode(irDupNode);
                }
            }

            PainlessCast upcast = scriptScope.hasDecoration(userAssignmentNode, UpcastPainlessCast.class) ?
                    scriptScope.getDecoration(userAssignmentNode, UpcastPainlessCast.class).getUpcastPainlessCast() : null;

            if (upcast != null) {
                ExpressionNode irLoadNode = irBinaryMathNode.getLeftNode();
                CastNode irCastNode = new CastNode();
                irCastNode.setLocation(irLoadNode.getLocation());
                irCastNode.setExpressionType(upcast.targetType);
                irCastNode.setCast(upcast);
                irCastNode.setChildNode(irLoadNode);
                irBinaryMathNode.setLeftNode(irCastNode);
            }

            irBinaryMathNode.setExpressionType(compoundType);
            irBinaryMathNode.setBinaryType(compoundType);
            irBinaryMathNode.setOperation(userAssignmentNode.getOperation());
            irBinaryMathNode.setFlags(DefBootstrap.OPERATOR_COMPOUND_ASSIGNMENT);
            irBinaryMathNode.setRightNode(valueNode);
        } else {
            storeNode = (StoreNode)visitor.visit(userAssignmentNode.getLeftNode(), scriptScope);

            if (read) {
                int accessDepth = scriptScope.getDecoration(userAssignmentNode.getLeftNode(), AccessDepth.class).getAccessDepth();

                DupNode dupNode = new DupNode();
                dupNode.setLocation(valueNode.getLocation());
                dupNode.setExpressionType(valueNode.getExpressionType());
                dupNode.setSize(MethodWriter.getType(valueNode.getExpressionType()).getSize());
                dupNode.setDepth(accessDepth);
                dupNode.setChildNode(valueNode);

                valueNode = dupNode;
            }

            storeNode.setChildNode(valueNode);
        }

        return storeNode;
    }
}
