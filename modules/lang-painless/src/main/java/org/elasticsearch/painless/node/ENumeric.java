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
import org.elasticsearch.painless.symbol.Decorations.TargetType;
import org.elasticsearch.painless.symbol.Decorations.ValueType;
import org.elasticsearch.painless.symbol.Decorations.Write;
import org.elasticsearch.painless.symbol.ScriptScope;
import org.elasticsearch.painless.symbol.SemanticScope;

import java.util.Objects;

/**
 * Represents a non-decimal numeric constant.
 */
public class ENumeric extends AExpression {

    private final String numeric;
    private final int radix;

    public ENumeric(int identifier, Location location, String numeric, int radix) {
        super(identifier, location);

        this.numeric = Objects.requireNonNull(numeric);
        this.radix = radix;
    }

    public String getNumeric() {
        return numeric;
    }

    public int getRadix() {
        return radix;
    }

    public <Input, Output> Output visit(UserTreeVisitor<Input, Output> userTreeVisitor, Input input) {
        return userTreeVisitor.visitNumeric(this, input);
    }

    public static void visitDefaultSemanticAnalysis(
            DefaultSemanticAnalysisPhase visitor, ENumeric userNumericNode, SemanticScope semanticScope) {

        String numeric = userNumericNode.getNumeric();

        if (semanticScope.getCondition(userNumericNode, Negate.class)) {
            numeric = "-" + numeric;
        }

        if (semanticScope.getCondition(userNumericNode, Write.class)) {
            throw userNumericNode.createError(new IllegalArgumentException(
                    "invalid assignment: cannot assign a value to numeric constant [" + numeric + "]"));
        }

        if (semanticScope.getCondition(userNumericNode, Read.class) == false) {
            throw userNumericNode.createError(new IllegalArgumentException(
                    "not a statement: numeric constant [" + numeric + "] not used"));
        }

        int radix = userNumericNode.getRadix();
        Class<?> valueType;
        Object constant;

        if (numeric.endsWith("d") || numeric.endsWith("D")) {
            if (radix != 10) {
                throw userNumericNode.createError(new IllegalStateException("Illegal tree structure."));
            }

            try {
                constant = Double.parseDouble(numeric.substring(0, numeric.length() - 1));
                valueType = double.class;
            } catch (NumberFormatException exception) {
                throw userNumericNode.createError(new IllegalArgumentException("Invalid double constant [" + numeric + "]."));
            }
        } else if (numeric.endsWith("f") || numeric.endsWith("F")) {
            if (radix != 10) {
                throw userNumericNode.createError(new IllegalStateException("Illegal tree structure."));
            }

            try {
                constant = Float.parseFloat(numeric.substring(0, numeric.length() - 1));
                valueType = float.class;
            } catch (NumberFormatException exception) {
                throw userNumericNode.createError(new IllegalArgumentException("Invalid float constant [" + numeric + "]."));
            }
        } else if (numeric.endsWith("l") || numeric.endsWith("L")) {
            try {
                constant = Long.parseLong(numeric.substring(0, numeric.length() - 1), radix);
                valueType = long.class;
            } catch (NumberFormatException exception) {
                throw userNumericNode.createError(new IllegalArgumentException("Invalid long constant [" + numeric + "]."));
            }
        } else {
            try {
                TargetType targetType = semanticScope.getDecoration(userNumericNode, TargetType.class);
                Class<?> sort = targetType == null ? int.class : targetType.getTargetType();
                int integer = Integer.parseInt(numeric, radix);

                if (sort == byte.class && integer >= Byte.MIN_VALUE && integer <= Byte.MAX_VALUE) {
                    constant = (byte)integer;
                    valueType = byte.class;
                } else if (sort == char.class && integer >= Character.MIN_VALUE && integer <= Character.MAX_VALUE) {
                    constant = (char)integer;
                    valueType = char.class;
                } else if (sort == short.class && integer >= Short.MIN_VALUE && integer <= Short.MAX_VALUE) {
                    constant = (short)integer;
                    valueType = short.class;
                } else {
                    constant = integer;
                    valueType = int.class;
                }
            } catch (NumberFormatException exception) {
                try {
                    // Check if we can parse as a long. If so then hint that the user might prefer that.
                    Long.parseLong(numeric, radix);
                    throw userNumericNode.createError(new IllegalArgumentException(
                            "Invalid int constant [" + numeric + "]. If you want a long constant then change it to [" + numeric + "L]."));
                } catch (NumberFormatException longNoGood) {
                    // Ignored
                }
                throw userNumericNode.createError(new IllegalArgumentException("Invalid int constant [" + numeric + "]."));
            }
        }

        semanticScope.putDecoration(userNumericNode, new ValueType(valueType));
        semanticScope.putDecoration(userNumericNode, new StandardConstant(constant));
    }

    public static IRNode visitDefaultIRTreeBuild(DefaultIRTreeBuilderPhase visitor, ENumeric userNumericNode, ScriptScope scriptScope) {
        ConstantNode irConstantNode = new ConstantNode();
        irConstantNode.setLocation(userNumericNode.getLocation());
        irConstantNode.setExpressionType(scriptScope.getDecoration(userNumericNode, ValueType.class).getValueType());
        irConstantNode.setConstant(scriptScope.getDecoration(userNumericNode, StandardConstant.class).getStandardConstant());

        return irConstantNode;
    }
}
