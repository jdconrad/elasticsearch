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
import org.elasticsearch.painless.symbol.Decorations.Negate;
import org.elasticsearch.painless.symbol.Decorations.Read;
import org.elasticsearch.painless.symbol.Decorations.StandardConstant;
import org.elasticsearch.painless.symbol.Decorations.ValueType;
import org.elasticsearch.painless.symbol.Decorations.Write;
import org.elasticsearch.painless.symbol.ScriptScope;
import org.elasticsearch.painless.symbol.SemanticScope;

import java.util.Objects;

/**
 * Represents a decimal constant.
 */
public class EDecimal extends AExpression {

    private final String decimal;

    public EDecimal(int identifier, Location location, String decimal) {
        super(identifier, location);

        this.decimal = Objects.requireNonNull(decimal);
    }

    public String getDecimal() {
        return decimal;
    }

    public <Input, Output> Output visit(UserTreeVisitor<Input, Output> userTreeVisitor, Input input) {
        return userTreeVisitor.visitDecimal(this, input);
    }

    public static void visitDefaultSemanticAnalysis(
            DefaultSemanticAnalysisPhase visitor, EDecimal userDecimalNode, SemanticScope semanticScope) {

        String decimal = userDecimalNode.getDecimal();

        if (semanticScope.getCondition(userDecimalNode, Negate.class)) {
            decimal = "-" + decimal;
        }

        if (semanticScope.getCondition(userDecimalNode, Write.class)) {
            throw userDecimalNode.createError(new IllegalArgumentException(
                    "invalid assignment: cannot assign a value to decimal constant [" + decimal + "]"));
        }

        if (semanticScope.getCondition(userDecimalNode, Read.class) == false) {
            throw userDecimalNode.createError(new IllegalArgumentException("not a statement: decimal constant [" + decimal + "] not used"));
        }

        Class<?> valueType;
        Object constant;

        if (decimal.endsWith("f") || decimal.endsWith("F")) {
            try {
                constant = Float.parseFloat(decimal.substring(0, decimal.length() - 1));
                valueType = float.class;
            } catch (NumberFormatException exception) {
                throw userDecimalNode.createError(new IllegalArgumentException("Invalid float constant [" + decimal + "]."));
            }
        } else {
            String toParse = decimal;
            if (toParse.endsWith("d") || decimal.endsWith("D")) {
                toParse = toParse.substring(0, decimal.length() - 1);
            }
            try {
                constant = Double.parseDouble(toParse);
                valueType = double.class;
            } catch (NumberFormatException exception) {
                throw userDecimalNode.createError(new IllegalArgumentException("Invalid double constant [" + decimal + "]."));
            }
        }

        semanticScope.putDecoration(userDecimalNode, new ValueType(valueType));
        semanticScope.putDecoration(userDecimalNode, new StandardConstant(constant));
    }

    public static IRNode visitDefaultIRTreeBuild(DefaultIRTreeBuilderPhase visitor, EDecimal userDecimalNode, ScriptScope scriptScope) {
        ConstantNode irConstantNode = new ConstantNode();
        irConstantNode.setLocation(userDecimalNode.getLocation());
        irConstantNode.setExpressionType(scriptScope.getDecoration(userDecimalNode, ValueType.class).getValueType());
        irConstantNode.setConstant(scriptScope.getDecoration(userDecimalNode, StandardConstant.class).getStandardConstant());

        return irConstantNode;
    }
}
