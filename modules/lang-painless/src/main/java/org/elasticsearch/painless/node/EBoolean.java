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
import org.elasticsearch.painless.ir.ConstantNode;
import org.elasticsearch.painless.ir.IRNode;
import org.elasticsearch.painless.phase.DefaultIRTreeBuilderPhase;
import org.elasticsearch.painless.phase.DefaultSemanticAnalysisPhase;
import org.elasticsearch.painless.phase.UserTreeVisitor;
import org.elasticsearch.painless.symbol.Decorations.Read;
import org.elasticsearch.painless.symbol.Decorations.StandardConstant;
import org.elasticsearch.painless.symbol.Decorations.ValueType;
import org.elasticsearch.painless.symbol.Decorations.Write;
import org.elasticsearch.painless.symbol.ScriptScope;
import org.elasticsearch.painless.symbol.SemanticScope;

/**
 * Represents a boolean constant.
 */
public class EBoolean extends AExpression {

    private final boolean bool;

    public EBoolean(int identifier, Location location, boolean bool) {
        super(identifier, location);

        this.bool = bool;
    }

    public boolean getBool() {
        return bool;
    }

    public <Input, Output> Output visit(UserTreeVisitor<Input, Output> userTreeVisitor, Input input) {
        return userTreeVisitor.visitBoolean(this, input);
    }

    public static void visitDefaultSemanticAnalysis(
            DefaultSemanticAnalysisPhase visitor, EBoolean userBooleanNode, SemanticScope semanticScope) {

        boolean bool = userBooleanNode.getBool();

        if (semanticScope.getCondition(userBooleanNode, Write.class)) {
            throw userBooleanNode.createError(new IllegalArgumentException(
                    "invalid assignment: cannot assign a value to boolean constant [" + bool + "]"));
        }

        if (semanticScope.getCondition(userBooleanNode, Read.class) == false) {
            throw userBooleanNode.createError(new IllegalArgumentException("not a statement: boolean constant [" + bool + "] not used"));
        }

        semanticScope.putDecoration(userBooleanNode, new ValueType(boolean.class));
        semanticScope.putDecoration(userBooleanNode, new StandardConstant(bool));
    }

    public static IRNode visitDefaultIRTreeBuild(DefaultIRTreeBuilderPhase visitor, EBoolean userBooleanNode, ScriptScope scriptScope) {
        ConstantNode irConstantNode = new ConstantNode();
        irConstantNode.setLocation(userBooleanNode.getLocation());
        irConstantNode.setExpressionType(scriptScope.getDecoration(userBooleanNode, ValueType.class).getValueType());
        irConstantNode.setConstant(scriptScope.getDecoration(userBooleanNode, StandardConstant.class).getStandardConstant());

        return irConstantNode;
    }
}
