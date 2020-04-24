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
import org.elasticsearch.painless.ir.IRNode;
import org.elasticsearch.painless.ir.ThrowNode;
import org.elasticsearch.painless.phase.DefaultIRTreeBuilderPhase;
import org.elasticsearch.painless.phase.DefaultSemanticAnalysisPhase;
import org.elasticsearch.painless.phase.UserTreeVisitor;
import org.elasticsearch.painless.symbol.Decorations.AllEscape;
import org.elasticsearch.painless.symbol.Decorations.LoopEscape;
import org.elasticsearch.painless.symbol.Decorations.MethodEscape;
import org.elasticsearch.painless.symbol.Decorations.Read;
import org.elasticsearch.painless.symbol.Decorations.TargetType;
import org.elasticsearch.painless.symbol.ScriptScope;
import org.elasticsearch.painless.symbol.SemanticScope;

import java.util.Objects;

/**
 * Represents a throw statement.
 */
public class SThrow extends AStatement {

    private final AExpression expressionNode;

    public SThrow(int identifier, Location location, AExpression expressionNode) {
        super(identifier, location);

        this.expressionNode = Objects.requireNonNull(expressionNode);
    }

    public AExpression getExpressionNode() {
        return expressionNode;
    }

    @Override
    public <Input, Output> Output visit(UserTreeVisitor<Input, Output> userTreeVisitor, Input input) {
        return userTreeVisitor.visitThrow(this, input);
    }

    public static void visitDefaultSemanticAnalysis(
            DefaultSemanticAnalysisPhase visitor, SThrow userThrowNode, SemanticScope semanticScope) {

        AExpression userExpressionNode = userThrowNode.getExpressionNode();

        semanticScope.setCondition(userExpressionNode, Read.class);
        semanticScope.putDecoration(userExpressionNode, new TargetType(Exception.class));
        visitor.checkedVisit(userExpressionNode, semanticScope);
        visitor.decorateWithCast(userExpressionNode, semanticScope);

        semanticScope.setCondition(userThrowNode, MethodEscape.class);
        semanticScope.setCondition(userThrowNode, LoopEscape.class);
        semanticScope.setCondition(userThrowNode, AllEscape.class);
    }

    public static IRNode visitDefaultIRTreeBuild(DefaultIRTreeBuilderPhase visitor, SThrow userThrowNode, ScriptScope scriptScope) {
        ThrowNode irThrowNode = new ThrowNode();
        irThrowNode.setExpressionNode(visitor.injectCast(userThrowNode.getExpressionNode(), scriptScope));
        irThrowNode.setLocation(userThrowNode.getLocation());

        return irThrowNode;
    }
}
