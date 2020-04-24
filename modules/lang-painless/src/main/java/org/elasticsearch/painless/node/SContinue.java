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
import org.elasticsearch.painless.ir.ContinueNode;
import org.elasticsearch.painless.ir.IRNode;
import org.elasticsearch.painless.phase.DefaultIRTreeBuilderPhase;
import org.elasticsearch.painless.phase.DefaultSemanticAnalysisPhase;
import org.elasticsearch.painless.phase.UserTreeVisitor;
import org.elasticsearch.painless.symbol.Decorations.AllEscape;
import org.elasticsearch.painless.symbol.Decorations.AnyContinue;
import org.elasticsearch.painless.symbol.Decorations.InLoop;
import org.elasticsearch.painless.symbol.Decorations.LastLoop;
import org.elasticsearch.painless.symbol.ScriptScope;
import org.elasticsearch.painless.symbol.SemanticScope;

/**
 * Represents a continue statement.
 */
public class SContinue extends AStatement {

    public SContinue(int identifier, Location location) {
        super(identifier, location);
    }

    @Override
    public <Input, Output> Output visit(UserTreeVisitor<Input, Output> userTreeVisitor, Input input) {
        return userTreeVisitor.visitContinue(this, input);
    }

    public static void visitDefaultSemanticAnalysis(
            DefaultSemanticAnalysisPhase visitor, SContinue userContinueNode, SemanticScope semanticScope) {

        if (semanticScope.getCondition(userContinueNode, InLoop.class) == false) {
            throw userContinueNode.createError(new IllegalArgumentException("invalid continue statement: not inside loop"));
        }

        if (semanticScope.getCondition(userContinueNode, LastLoop.class)) {
            throw userContinueNode.createError(new IllegalArgumentException("extraneous continue statement"));
        }

        semanticScope.setCondition(userContinueNode, AllEscape.class);
        semanticScope.setCondition(userContinueNode, AnyContinue.class);
    }

    public static IRNode visitDefaultIRTreeBuild(DefaultIRTreeBuilderPhase visitor, SContinue userContinueNode, ScriptScope scriptScope) {
        ContinueNode irContinueNode = new ContinueNode();
        irContinueNode.setLocation(userContinueNode.getLocation());

        return irContinueNode;
    }
}
