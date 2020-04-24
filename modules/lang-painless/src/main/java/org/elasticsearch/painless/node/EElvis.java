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
import org.elasticsearch.painless.Location;
import org.elasticsearch.painless.ir.ElvisNode;
import org.elasticsearch.painless.ir.IRNode;
import org.elasticsearch.painless.phase.DefaultIRTreeBuilderPhase;
import org.elasticsearch.painless.phase.DefaultSemanticAnalysisPhase;
import org.elasticsearch.painless.phase.UserTreeVisitor;
import org.elasticsearch.painless.symbol.Decorations.Explicit;
import org.elasticsearch.painless.symbol.Decorations.Internal;
import org.elasticsearch.painless.symbol.Decorations.Read;
import org.elasticsearch.painless.symbol.Decorations.TargetType;
import org.elasticsearch.painless.symbol.Decorations.ValueType;
import org.elasticsearch.painless.symbol.Decorations.Write;
import org.elasticsearch.painless.symbol.ScriptScope;
import org.elasticsearch.painless.symbol.SemanticScope;

import static java.util.Objects.requireNonNull;

/**
 * The Elvis operator ({@code ?:}), a null coalescing operator. Binary operator that evaluates the first expression and return it if it is
 * non null. If the first expression is null then it evaluates the second expression and returns it.
 */
public class EElvis extends AExpression {

    private final AExpression leftNode;
    private final AExpression rightNode;

    public EElvis(int identifier, Location location, AExpression leftNode, AExpression rightNode) {
        super(identifier, location);

        this.leftNode = requireNonNull(leftNode);
        this.rightNode = requireNonNull(rightNode);
    }

    public AExpression getLeftNode() {
        return leftNode;
    }

    public AExpression getRightNode() {
        return rightNode;
    }

    @Override
    public <Input, Output> Output visit(UserTreeVisitor<Input, Output> userTreeVisitor, Input input) {
        return userTreeVisitor.visitElvis(this, input);
    }

    public static void visitDefaultSemanticAnalysis(
            DefaultSemanticAnalysisPhase visitor, EElvis userElvisNode, SemanticScope semanticScope) {

        if (semanticScope.getCondition(userElvisNode, Write.class)) {
            throw userElvisNode.createError(new IllegalArgumentException(
                    "invalid assignment: cannot assign a value to elvis operation [?:]"));
        }

        if (semanticScope.getCondition(userElvisNode, Read.class) == false) {
            throw userElvisNode.createError(new IllegalArgumentException("not a statement: result not used from elvis operation [?:]"));
        }

        TargetType targetType = semanticScope.getDecoration(userElvisNode, TargetType.class);

        if (targetType != null && targetType.getTargetType().isPrimitive()) {
            throw userElvisNode.createError(new IllegalArgumentException("Elvis operator cannot return primitives"));
        }

        AExpression userLeftNode = userElvisNode.getLeftNode();
        semanticScope.setCondition(userLeftNode, Read.class);
        semanticScope.copyDecoration(userElvisNode, userLeftNode, TargetType.class);
        semanticScope.replicateCondition(userElvisNode, userLeftNode, Explicit.class);
        semanticScope.replicateCondition(userElvisNode, userLeftNode, Internal.class);
        visitor.checkedVisit(userLeftNode, semanticScope);
        Class<?> leftValueType = semanticScope.getDecoration(userLeftNode, ValueType.class).getValueType();

        AExpression userRightNode = userElvisNode.getRightNode();
        semanticScope.setCondition(userRightNode, Read.class);
        semanticScope.copyDecoration(userElvisNode, userRightNode, TargetType.class);
        semanticScope.replicateCondition(userElvisNode, userRightNode, Explicit.class);
        semanticScope.replicateCondition(userElvisNode, userRightNode, Internal.class);
        visitor.checkedVisit(userRightNode, semanticScope);
        Class<?> rightValueType = semanticScope.getDecoration(userRightNode, ValueType.class).getValueType();

        if (userLeftNode instanceof ENull) {
            throw userElvisNode.createError(new IllegalArgumentException("Extraneous elvis operator. LHS is null."));
        }
        if (userLeftNode instanceof EBoolean ||
                userLeftNode instanceof ENumeric ||
                userLeftNode instanceof EDecimal ||
                userLeftNode instanceof EString) {
            throw userElvisNode.createError(new IllegalArgumentException("Extraneous elvis operator. LHS is a constant."));
        }
        if (leftValueType.isPrimitive()) {
            throw userElvisNode.createError(new IllegalArgumentException("Extraneous elvis operator. LHS is a primitive."));
        }
        if (userRightNode instanceof ENull) {
            throw userElvisNode.createError(new IllegalArgumentException("Extraneous elvis operator. RHS is null."));
        }

        Class<?> valueType;

        if (targetType == null) {
            Class<?> promote = AnalyzerCaster.promoteConditional(leftValueType, rightValueType);

            semanticScope.putDecoration(userLeftNode, new TargetType(promote));
            semanticScope.putDecoration(userRightNode, new TargetType(promote));
            valueType = promote;
        } else {
            valueType = targetType.getTargetType();
        }

        visitor.decorateWithCast(userLeftNode, semanticScope);
        visitor.decorateWithCast(userRightNode, semanticScope);

        semanticScope.putDecoration(userElvisNode, new ValueType(valueType));
    }

    public static IRNode visitDefaultIRTreeBuild(DefaultIRTreeBuilderPhase visitor, EElvis userElvisNode, ScriptScope scriptScope) {
        ElvisNode irElvisNode = new ElvisNode();
        irElvisNode.setLocation(userElvisNode.getLocation());
        irElvisNode.setExpressionType(scriptScope.getDecoration(userElvisNode, ValueType.class).getValueType());
        irElvisNode.setLeftNode(visitor.injectCast(userElvisNode.getLeftNode(), scriptScope));
        irElvisNode.setRightNode(visitor.injectCast(userElvisNode.getRightNode(), scriptScope));

        return irElvisNode;
    }
}
