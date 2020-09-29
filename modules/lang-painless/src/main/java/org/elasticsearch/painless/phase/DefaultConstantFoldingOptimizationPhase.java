/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.painless.phase;

import org.elasticsearch.painless.AnalyzerCaster;
import org.elasticsearch.painless.Operation;
import org.elasticsearch.painless.ir.BinaryMathNode;
import org.elasticsearch.painless.ir.BooleanNode;
import org.elasticsearch.painless.ir.CastNode;
import org.elasticsearch.painless.ir.ComparisonNode;
import org.elasticsearch.painless.ir.ConstantNode;
import org.elasticsearch.painless.ir.ExpressionNode;
import org.elasticsearch.painless.ir.IRNode;
import org.elasticsearch.painless.ir.NullNode;
import org.elasticsearch.painless.ir.StringConcatenationNode;
import org.elasticsearch.painless.ir.UnaryMathNode;
import org.elasticsearch.painless.lookup.PainlessLookupUtility;
import org.elasticsearch.painless.symbol.IRDecorations.IRDCast;
import org.elasticsearch.painless.symbol.IRDecorations.IRDComparisonType;
import org.elasticsearch.painless.symbol.IRDecorations.IRDConstant;
import org.elasticsearch.painless.symbol.IRDecorations.IRDExpressionType;
import org.elasticsearch.painless.symbol.IRDecorations.IRDOperation;

/**
 * This optimization pass will perform the specified operation on two leafs nodes if they are both
 * constants. The additional overrides for visiting ir nodes in this class are required whenever
 * there is a child node that is an expression. The structure of the tree does not have a way
 * for a child node to introspect into its parent node, so to replace itself the parent node
 * must pass the child node's particular set method as method reference.
 */
public class DefaultConstantFoldingOptimizationPhase extends IRTreeBaseTransformer<Void> {

    @Override
    public IRNode transformUnaryMath(UnaryMathNode irUnaryMathNode, Void scope) {
        irUnaryMathNode.transformChildren(this, scope);
        IRNode irTransformNode = irUnaryMathNode;

        if (irUnaryMathNode.getChildNode() instanceof ConstantNode) {
            ConstantNode irConstantNode = (ConstantNode)irUnaryMathNode.getChildNode();
            Operation operation = irUnaryMathNode.getDecorationValue(IRDOperation.class);
            Class<?> type = irUnaryMathNode.getDecorationValue(IRDExpressionType.class);

            if (operation == Operation.SUB) {
                if (type == int.class) {
                    irConstantNode.attachDecoration(new IRDConstant(-(int)irConstantNode.getDecorationValue(IRDConstant.class)));
                } else if (type == long.class) {
                    irConstantNode.attachDecoration(new IRDConstant(-(long)irConstantNode.getDecorationValue(IRDConstant.class)));
                } else if (type == float.class) {
                    irConstantNode.attachDecoration(new IRDConstant(-(float)irConstantNode.getDecorationValue(IRDConstant.class)));
                } else if (type == double.class) {
                    irConstantNode.attachDecoration(new IRDConstant(-(double)irConstantNode.getDecorationValue(IRDConstant.class)));
                } else {
                    throw irUnaryMathNode.getLocation().createError(new IllegalStateException("constant folding error: " +
                            "unexpected type [" + PainlessLookupUtility.typeToCanonicalTypeName(type) + "] for " +
                            "unary operation [" + operation.symbol + "] on " +
                            "constant [" + irConstantNode.getDecorationString(IRDConstant.class) + "]"));
                }

                irTransformNode = irConstantNode;
            } else if (operation == Operation.BWNOT) {
                if (type == int.class) {
                    irConstantNode.attachDecoration(new IRDConstant(~(int)irConstantNode.getDecorationValue(IRDConstant.class)));
                } else if (type == long.class) {
                    irConstantNode.attachDecoration(new IRDConstant(~(long)irConstantNode.getDecorationValue(IRDConstant.class)));
                } else {
                    throw irUnaryMathNode.getLocation().createError(new IllegalStateException("constant folding error: " +
                            "unexpected type [" + PainlessLookupUtility.typeToCanonicalTypeName(type) + "] for " +
                            "unary operation [" + operation.symbol + "] on " +
                            "constant [" + irConstantNode.getDecorationString(IRDConstant.class) + "]"));
                }

                irTransformNode = irConstantNode;
            } else if (operation == Operation.NOT) {
                if (type == boolean.class) {
                    irConstantNode.attachDecoration(new IRDConstant(!(boolean)irConstantNode.getDecorationValue(IRDConstant.class)));
                } else {
                    throw irUnaryMathNode.getLocation().createError(new IllegalStateException("constant folding error: " +
                            "unexpected type [" + PainlessLookupUtility.typeToCanonicalTypeName(type) + "] for " +
                            "unary operation [" + operation.symbol + "] on " +
                            "constant [" + irConstantNode.getDecorationString(IRDConstant.class) + "]"));
                }

                irTransformNode = irConstantNode;
            } else if (operation == Operation.ADD) {
                irTransformNode = irConstantNode;
            }
        }

        return irTransformNode;
    }

    @Override
    public IRNode transformBinaryMath(BinaryMathNode irBinaryMathNode, Void scope) {
        irBinaryMathNode.transformChildren(this, scope);
        IRNode irTransformNode = irBinaryMathNode;

        if (irBinaryMathNode.getLeftNode() instanceof ConstantNode && irBinaryMathNode.getRightNode() instanceof ConstantNode) {
            ConstantNode irLeftConstantNode = (ConstantNode)irBinaryMathNode.getLeftNode();
            ConstantNode irRightConstantNode = (ConstantNode)irBinaryMathNode.getRightNode();
            Operation operation = irBinaryMathNode.getDecorationValue(IRDOperation.class);
            Class<?> type = irBinaryMathNode.getDecorationValue(IRDExpressionType.class);

            if (operation == Operation.MUL) {
                if (type == int.class) {
                    irLeftConstantNode.attachDecoration(new IRDConstant(
                            (int)irLeftConstantNode.getDecorationValue(IRDConstant.class) *
                            (int)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                } else if (type == long.class) {
                    irLeftConstantNode.attachDecoration(new IRDConstant(
                            (long)irLeftConstantNode.getDecorationValue(IRDConstant.class) *
                            (long)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                } else if (type == float.class) {
                    irLeftConstantNode.attachDecoration(new IRDConstant(
                            (float)irLeftConstantNode.getDecorationValue(IRDConstant.class) *
                            (float)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                } else if (type == double.class) {
                    irLeftConstantNode.attachDecoration(new IRDConstant(
                            (double)irLeftConstantNode.getDecorationValue(IRDConstant.class) *
                            (double)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                } else {
                    throw irBinaryMathNode.getLocation().createError(new IllegalStateException("constant folding error: " +
                            "unexpected type [" + PainlessLookupUtility.typeToCanonicalTypeName(type) + "] for " +
                            "binary operation [" + operation.symbol + "] on " +
                            "constants [" + irLeftConstantNode.getDecorationString(IRDConstant.class) + "] " +
                            "and [" + irRightConstantNode.getDecorationString(IRDConstant.class) + "]"));
                }

                irTransformNode = irLeftConstantNode;
            } else if (operation == Operation.DIV) {
                try {
                    if (type == int.class) {
                        irLeftConstantNode.attachDecoration(new IRDConstant(
                                (int)irLeftConstantNode.getDecorationValue(IRDConstant.class) /
                                (int)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                    } else if (type == long.class) {
                        irLeftConstantNode.attachDecoration(new IRDConstant(
                                (long)irLeftConstantNode.getDecorationValue(IRDConstant.class) /
                                (long)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                    } else if (type == float.class) {
                        irLeftConstantNode.attachDecoration(new IRDConstant(
                                (float)irLeftConstantNode.getDecorationValue(IRDConstant.class) /
                                (float)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                    } else if (type == double.class) {
                        irLeftConstantNode.attachDecoration(new IRDConstant(
                                (double)irLeftConstantNode.getDecorationValue(IRDConstant.class) /
                                (double)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                    } else {
                        throw irBinaryMathNode.getLocation().createError(new IllegalStateException("constant folding error: " +
                                "unexpected type [" + PainlessLookupUtility.typeToCanonicalTypeName(type) + "] for " +
                                "binary operation [" + operation.symbol + "] on " +
                                "constants [" + irLeftConstantNode.getDecorationString(IRDConstant.class) + "] " +
                                "and [" + irRightConstantNode.getDecorationString(IRDConstant.class) + "]"));
                    }
                } catch (ArithmeticException ae) {
                    throw irBinaryMathNode.getLocation().createError(ae);
                }

                irTransformNode = irLeftConstantNode;
            } else if (operation == Operation.REM) {
                try {
                    if (type == int.class) {
                        irLeftConstantNode.attachDecoration(new IRDConstant(
                                (int)irLeftConstantNode.getDecorationValue(IRDConstant.class) %
                                (int)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                    } else if (type == long.class) {
                        irLeftConstantNode.attachDecoration(new IRDConstant(
                                (long)irLeftConstantNode.getDecorationValue(IRDConstant.class) %
                                (long)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                    } else if (type == float.class) {
                        irLeftConstantNode.attachDecoration(new IRDConstant(
                                (float)irLeftConstantNode.getDecorationValue(IRDConstant.class) %
                                (float)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                    } else if (type == double.class) {
                        irLeftConstantNode.attachDecoration(new IRDConstant(
                                (double)irLeftConstantNode.getDecorationValue(IRDConstant.class) %
                                (double)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                    } else {
                        throw irBinaryMathNode.getLocation().createError(new IllegalStateException("constant folding error: " +
                                "unexpected type [" + PainlessLookupUtility.typeToCanonicalTypeName(type) + "] for " +
                                "binary operation [" + operation.symbol + "] on " +
                                "constants [" + irLeftConstantNode.getDecorationString(IRDConstant.class) + "] " +
                                "and [" + irRightConstantNode.getDecorationString(IRDConstant.class) + "]"));
                    }
                } catch (ArithmeticException ae) {
                    throw irBinaryMathNode.getLocation().createError(ae);
                }

                irTransformNode = irLeftConstantNode;
            } else if (operation == Operation.ADD) {
                if (type == int.class) {
                    irLeftConstantNode.attachDecoration(new IRDConstant(
                            (int)irLeftConstantNode.getDecorationValue(IRDConstant.class) +
                            (int)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                } else if (type == long.class) {
                    irLeftConstantNode.attachDecoration(new IRDConstant(
                            (long)irLeftConstantNode.getDecorationValue(IRDConstant.class) +
                            (long)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                } else if (type == float.class) {
                    irLeftConstantNode.attachDecoration(new IRDConstant(
                            (float)irLeftConstantNode.getDecorationValue(IRDConstant.class) +
                            (float)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                } else if (type == double.class) {
                    irLeftConstantNode.attachDecoration(new IRDConstant(
                            (double)irLeftConstantNode.getDecorationValue(IRDConstant.class) +
                            (double)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                } else {
                    throw irBinaryMathNode.getLocation().createError(new IllegalStateException("constant folding error: " +
                            "unexpected type [" + PainlessLookupUtility.typeToCanonicalTypeName(type) + "] for " +
                            "binary operation [" + operation.symbol + "] on " +
                            "constants [" + irLeftConstantNode.getDecorationString(IRDConstant.class) + "] " +
                            "and [" + irRightConstantNode.getDecorationString(IRDConstant.class) + "]"));
                }

                irTransformNode = irLeftConstantNode;
            } else if (operation == Operation.SUB) {
                if (type == int.class) {
                    irLeftConstantNode.attachDecoration(new IRDConstant(
                            (int)irLeftConstantNode.getDecorationValue(IRDConstant.class) -
                            (int)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                } else if (type == long.class) {
                    irLeftConstantNode.attachDecoration(new IRDConstant(
                            (long)irLeftConstantNode.getDecorationValue(IRDConstant.class) -
                            (long)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                } else if (type == float.class) {
                    irLeftConstantNode.attachDecoration(new IRDConstant(
                            (float)irLeftConstantNode.getDecorationValue(IRDConstant.class) -
                            (float)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                } else if (type == double.class) {
                    irLeftConstantNode.attachDecoration(new IRDConstant(
                            (double)irLeftConstantNode.getDecorationValue(IRDConstant.class) -
                            (double)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                } else {
                    throw irBinaryMathNode.getLocation().createError(new IllegalStateException("constant folding error: " +
                            "unexpected type [" + PainlessLookupUtility.typeToCanonicalTypeName(type) + "] for " +
                            "binary operation [" + operation.symbol + "] on " +
                            "constants [" + irLeftConstantNode.getDecorationString(IRDConstant.class) + "] " +
                            "and [" + irRightConstantNode.getDecorationString(IRDConstant.class) + "]"));
                }

                irTransformNode = irLeftConstantNode;
            } else if (operation == Operation.LSH) {
                if (type == int.class) {
                    irLeftConstantNode.attachDecoration(new IRDConstant(
                            (int)irLeftConstantNode.getDecorationValue(IRDConstant.class) <<
                            (int)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                } else if (type == long.class) {
                    irLeftConstantNode.attachDecoration(new IRDConstant(
                            (long)irLeftConstantNode.getDecorationValue(IRDConstant.class) <<
                            (int)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                } else {
                    throw irBinaryMathNode.getLocation().createError(new IllegalStateException("constant folding error: " +
                            "unexpected type [" + PainlessLookupUtility.typeToCanonicalTypeName(type) + "] for " +
                            "binary operation [" + operation.symbol + "] on " +
                            "constants [" + irLeftConstantNode.getDecorationString(IRDConstant.class) + "] " +
                            "and [" + irRightConstantNode.getDecorationString(IRDConstant.class) + "]"));
                }

                irTransformNode = irLeftConstantNode;
            } else if (operation == Operation.RSH) {
                if (type == int.class) {
                    irLeftConstantNode.attachDecoration(new IRDConstant(
                            (int)irLeftConstantNode.getDecorationValue(IRDConstant.class) >>
                            (int)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                } else if (type == long.class) {
                    irLeftConstantNode.attachDecoration(new IRDConstant(
                            (long)irLeftConstantNode.getDecorationValue(IRDConstant.class) >>
                            (int)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                } else {
                    throw irBinaryMathNode.getLocation().createError(new IllegalStateException("constant folding error: " +
                            "unexpected type [" + PainlessLookupUtility.typeToCanonicalTypeName(type) + "] for " +
                            "binary operation [" + operation.symbol + "] on " +
                            "constants [" + irLeftConstantNode.getDecorationString(IRDConstant.class) + "] " +
                            "and [" + irRightConstantNode.getDecorationString(IRDConstant.class) + "]"));
                }

                irTransformNode = irLeftConstantNode;
            } else if (operation == Operation.USH) {
                if (type == int.class) {
                    irLeftConstantNode.attachDecoration(new IRDConstant(
                            (int)irLeftConstantNode.getDecorationValue(IRDConstant.class) >>>
                            (int)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                } else if (type == long.class) {
                    irLeftConstantNode.attachDecoration(new IRDConstant(
                            (long)irLeftConstantNode.getDecorationValue(IRDConstant.class) >>>
                            (int)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                } else {
                    throw irBinaryMathNode.getLocation().createError(new IllegalStateException("constant folding error: " +
                            "unexpected type [" + PainlessLookupUtility.typeToCanonicalTypeName(type) + "] for " +
                            "binary operation [" + operation.symbol + "] on " +
                            "constants [" + irLeftConstantNode.getDecorationString(IRDConstant.class) + "] and " +
                            "[" + irRightConstantNode.getDecorationString(IRDConstant.class) + "]"));
                }

                irTransformNode = irLeftConstantNode;
            } else if (operation == Operation.BWAND) {
                if (type == int.class) {
                    irLeftConstantNode.attachDecoration(new IRDConstant(
                            (int)irLeftConstantNode.getDecorationValue(IRDConstant.class) &
                            (int)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                } else if (type == long.class) {
                    irLeftConstantNode.attachDecoration(new IRDConstant(
                            (long)irLeftConstantNode.getDecorationValue(IRDConstant.class) &
                            (long)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                } else {
                    throw irBinaryMathNode.getLocation().createError(new IllegalStateException("constant folding error: " +
                            "unexpected type [" + PainlessLookupUtility.typeToCanonicalTypeName(type) + "] for " +
                            "binary operation [" + operation.symbol + "] on " +
                            "constants [" + irLeftConstantNode.getDecorationString(IRDConstant.class) + "] " +
                            "and [" + irRightConstantNode.getDecorationString(IRDConstant.class) + "]"));
                }

                irTransformNode = irLeftConstantNode;
            } else if (operation == Operation.XOR) {
                if (type == boolean.class) {
                    irLeftConstantNode.attachDecoration(new IRDConstant(
                            (boolean)irLeftConstantNode.getDecorationValue(IRDConstant.class) ^
                            (boolean)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                } else if (type == int.class) {
                    irLeftConstantNode.attachDecoration(new IRDConstant(
                            (int)irLeftConstantNode.getDecorationValue(IRDConstant.class) ^
                            (int)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                } else if (type == long.class) {
                    irLeftConstantNode.attachDecoration(new IRDConstant(
                            (long)irLeftConstantNode.getDecorationValue(IRDConstant.class) ^
                            (long)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                } else {
                    throw irBinaryMathNode.getLocation().createError(new IllegalStateException("constant folding error: " +
                            "unexpected type [" + PainlessLookupUtility.typeToCanonicalTypeName(type) + "] for " +
                            "binary operation [" + operation.symbol + "] on " +
                            "constants [" + irLeftConstantNode.getDecorationString(IRDConstant.class) + "] and " +
                            "[" + irRightConstantNode.getDecorationString(IRDConstant.class) + "]"));
                }

                irTransformNode = irLeftConstantNode;
            } else if (operation == Operation.BWOR) {
                if (type == int.class) {
                    irLeftConstantNode.attachDecoration(new IRDConstant(
                            (int)irLeftConstantNode.getDecorationValue(IRDConstant.class) |
                            (int)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                } else if (type == long.class) {
                    irLeftConstantNode.attachDecoration(new IRDConstant(
                            (long)irLeftConstantNode.getDecorationValue(IRDConstant.class) |
                            (long)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                } else {
                    throw irBinaryMathNode.getLocation().createError(new IllegalStateException("constant folding error: " +
                            "unexpected type [" + PainlessLookupUtility.typeToCanonicalTypeName(type) + "] for " +
                            "binary operation [" + operation.symbol + "] on " +
                            "constants [" + irLeftConstantNode.getDecorationString(IRDConstant.class) + "] " +
                            "and [" + irRightConstantNode.getDecorationString(IRDConstant.class) + "]"));
                }

                irTransformNode = irLeftConstantNode;
            }
        }

        return irTransformNode;
    }

    @Override
    public IRNode transformStringConcatenation(StringConcatenationNode irStringConcatenationNode, Void scope) {
        irStringConcatenationNode.transformChildren(this, scope);

        int i = 0;

        while (i < irStringConcatenationNode.getArgumentNodes().size() - 1) {
            ExpressionNode irLeftNode = irStringConcatenationNode.getArgumentNodes().get(i);
            ExpressionNode irRightNode = irStringConcatenationNode.getArgumentNodes().get(i + 1);

            if (irLeftNode instanceof ConstantNode && irRightNode instanceof ConstantNode) {
                ConstantNode irConstantNode = (ConstantNode)irLeftNode;
                irConstantNode.attachDecoration(new IRDConstant(
                        "" + irConstantNode.getDecorationValue(IRDConstant.class) + irRightNode.getDecorationValue(IRDConstant.class)));
                irConstantNode.attachDecoration(new IRDExpressionType(String.class));
                irStringConcatenationNode.getArgumentNodes().remove(i + 1);
            } else if (irLeftNode instanceof NullNode && irRightNode instanceof ConstantNode) {
                ConstantNode irConstantNode = (ConstantNode)irRightNode;
                irConstantNode.attachDecoration(new IRDConstant("" + null + irRightNode.getDecorationValue(IRDConstant.class)));
                irConstantNode.attachDecoration(new IRDExpressionType(String.class));
                irStringConcatenationNode.getArgumentNodes().remove(i);
            } else if (irLeftNode instanceof ConstantNode && irRightNode instanceof NullNode) {
                ConstantNode irConstantNode = (ConstantNode)irLeftNode;
                irConstantNode.attachDecoration(new IRDConstant("" + irLeftNode.getDecorationValue(IRDConstant.class) + null));
                irConstantNode.attachDecoration(new IRDExpressionType(String.class));
                irStringConcatenationNode.getArgumentNodes().remove(i + 1);
            } else if (irLeftNode instanceof NullNode && irRightNode instanceof NullNode) {
                ConstantNode irConstantNode = new ConstantNode(irLeftNode.getLocation());
                irConstantNode.attachDecoration(new IRDConstant("" + null + null));
                irConstantNode.attachDecoration(new IRDExpressionType(String.class));
                irStringConcatenationNode.getArgumentNodes().set(i, irConstantNode);
                irStringConcatenationNode.getArgumentNodes().remove(i + 1);
            } else {
                i++;
            }
        }

        IRNode irTransformNode = irStringConcatenationNode;

        if (irStringConcatenationNode.getArgumentNodes().size() == 1) {
            ExpressionNode irArgumentNode = irStringConcatenationNode.getArgumentNodes().get(0);

            if (irArgumentNode instanceof ConstantNode) {
                irTransformNode = irArgumentNode;
            }
        }

        return irTransformNode;
    }

    @Override
    public IRNode transformBoolean(BooleanNode irBooleanNode, Void scope) {
        irBooleanNode.transformChildren(this, scope);
        IRNode irTransformNode = irBooleanNode;

        if (irBooleanNode.getLeftNode() instanceof ConstantNode && irBooleanNode.getRightNode() instanceof ConstantNode) {
            ConstantNode irLeftConstantNode = (ConstantNode)irBooleanNode.getLeftNode();
            ConstantNode irRightConstantNode = (ConstantNode)irBooleanNode.getRightNode();
            Operation operation = irBooleanNode.getDecorationValue(IRDOperation.class);
            Class<?> type = irBooleanNode.getDecorationValue(IRDExpressionType.class);

            if (operation == Operation.AND) {
                if (type == boolean.class) {
                    irLeftConstantNode.attachDecoration(new IRDConstant(
                            (boolean)irLeftConstantNode.getDecorationValue(IRDConstant.class) &&
                            (boolean)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                } else {
                    throw irBooleanNode.getLocation().createError(new IllegalStateException("constant folding error: " +
                            "unexpected type [" + PainlessLookupUtility.typeToCanonicalTypeName(type) + "] for " +
                            "binary operation [" + operation.symbol + "] on " +
                            "constants [" + irLeftConstantNode.getDecorationString(IRDConstant.class) + "] " +
                            "and [" + irRightConstantNode.getDecorationString(IRDConstant.class) + "]"));
                }

                irTransformNode = irLeftConstantNode;
            } else if (operation == Operation.OR) {
                if (type == boolean.class) {
                    irLeftConstantNode.attachDecoration(new IRDConstant(
                            (boolean)irLeftConstantNode.getDecorationValue(IRDConstant.class) ||
                            (boolean)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                } else {
                    throw irBooleanNode.getLocation().createError(new IllegalStateException("constant folding error: " +
                            "unexpected type [" + PainlessLookupUtility.typeToCanonicalTypeName(type) + "] for " +
                            "boolean operation [" + operation.symbol + "] on " +
                            "constants [" + irLeftConstantNode.getDecorationString(IRDConstant.class) + "] " +
                            "and [" + irRightConstantNode.getDecorationString(IRDConstant.class) + "]"));
                }

                irTransformNode = irLeftConstantNode;
            }
        }

        return irTransformNode;
    }

    @Override
    public IRNode transformComparison(ComparisonNode irComparisonNode, Void scope) {
        irComparisonNode.transformChildren(this, scope);
        IRNode irTransformNode = irComparisonNode;

        if ((irComparisonNode.getLeftNode() instanceof ConstantNode || irComparisonNode.getLeftNode() instanceof NullNode)
                && (irComparisonNode.getRightNode() instanceof ConstantNode || irComparisonNode.getRightNode() instanceof NullNode)) {

            ConstantNode irLeftConstantNode =
                    irComparisonNode.getLeftNode() instanceof NullNode ? null : (ConstantNode)irComparisonNode.getLeftNode();
            ConstantNode irRightConstantNode =
                    irComparisonNode.getRightNode() instanceof NullNode ? null : (ConstantNode)irComparisonNode.getRightNode();
            Operation operation = irComparisonNode.getDecorationValue(IRDOperation.class);
            Class<?> type = irComparisonNode.getDecorationValue(IRDComparisonType.class);

            if (operation == Operation.EQ || operation == Operation.EQR) {
                if (type == boolean.class) {
                    irLeftConstantNode.attachDecoration(new IRDConstant(
                            (boolean)irLeftConstantNode.getDecorationValue(IRDConstant.class) ==
                            (boolean)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                } else if (type == int.class) {
                    irLeftConstantNode.attachDecoration(new IRDConstant(
                            (int)irLeftConstantNode.getDecorationValue(IRDConstant.class) ==
                            (int)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                } else if (type == long.class) {
                    irLeftConstantNode.attachDecoration(new IRDConstant(
                            (long)irLeftConstantNode.getDecorationValue(IRDConstant.class) ==
                            (long)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                } else if (type == float.class) {
                    irLeftConstantNode.attachDecoration(new IRDConstant(
                            (float)irLeftConstantNode.getDecorationValue(IRDConstant.class) ==
                            (float)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                } else if (type == double.class) {
                    irLeftConstantNode.attachDecoration(new IRDConstant(
                            (double)irLeftConstantNode.getDecorationValue(IRDConstant.class) ==
                            (double)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                } else if (irLeftConstantNode == null && irRightConstantNode == null) {
                    irLeftConstantNode = new ConstantNode(irComparisonNode.getLeftNode().getLocation());
                    irLeftConstantNode.attachDecoration(new IRDConstant(true));
                } else if (irLeftConstantNode == null || irRightConstantNode == null) {
                    irLeftConstantNode = new ConstantNode(irComparisonNode.getLeftNode().getLocation());
                    irLeftConstantNode.attachDecoration(new IRDConstant(false));
                } else {
                    if (operation == Operation.EQ) {
                        irLeftConstantNode.attachDecoration(new IRDConstant(
                                irLeftConstantNode.getDecorationValue(IRDConstant.class).equals(
                                        irRightConstantNode.getDecorationValue(IRDConstant.class))));
                    } else {
                        irLeftConstantNode.attachDecoration(new IRDConstant(
                                irLeftConstantNode.getDecorationValue(IRDConstant.class) ==
                                irRightConstantNode.getDecorationValue(IRDConstant.class)));
                    }
                }

                irLeftConstantNode.attachDecoration(new IRDExpressionType(boolean.class));
                irTransformNode = irLeftConstantNode;
            } else if (operation == Operation.NE || operation == Operation.NER) {
                if (type == boolean.class) {
                    irLeftConstantNode.attachDecoration(new IRDConstant(
                            (boolean)irLeftConstantNode.getDecorationValue(IRDConstant.class) !=
                            (boolean)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                } else if (type == int.class) {
                    irLeftConstantNode.attachDecoration(new IRDConstant(
                            (int)irLeftConstantNode.getDecorationValue(IRDConstant.class) !=
                            (int)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                } else if (type == long.class) {
                    irLeftConstantNode.attachDecoration(new IRDConstant(
                            (long)irLeftConstantNode.getDecorationValue(IRDConstant.class) !=
                            (long)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                } else if (type == float.class) {
                    irLeftConstantNode.attachDecoration(new IRDConstant(
                            (float)irLeftConstantNode.getDecorationValue(IRDConstant.class) !=
                            (float)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                } else if (type == double.class) {
                    irLeftConstantNode.attachDecoration(new IRDConstant(
                            (double)irLeftConstantNode.getDecorationValue(IRDConstant.class) !=
                            (double)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                } else if (irLeftConstantNode == null && irRightConstantNode == null) {
                    irLeftConstantNode = new ConstantNode(irComparisonNode.getLeftNode().getLocation());
                    irLeftConstantNode.attachDecoration(new IRDConstant(false));
                } else if (irLeftConstantNode == null || irRightConstantNode == null) {
                    irLeftConstantNode = new ConstantNode(irComparisonNode.getLeftNode().getLocation());
                    irLeftConstantNode.attachDecoration(new IRDConstant(true));
                } else {
                    if (operation == Operation.NE) {
                        irLeftConstantNode.attachDecoration(new IRDConstant(
                                irLeftConstantNode.getDecorationValue(IRDConstant.class).equals(
                                        irRightConstantNode.getDecorationValue(IRDConstant.class)) == false));
                    } else {
                        irLeftConstantNode.attachDecoration(new IRDConstant(
                                irLeftConstantNode.getDecorationValue(IRDConstant.class) !=
                                irRightConstantNode.getDecorationValue(IRDConstant.class)));
                    }
                }

                irLeftConstantNode.attachDecoration(new IRDExpressionType(boolean.class));
                irTransformNode = irLeftConstantNode;
            } else if (irLeftConstantNode != null && irRightConstantNode != null) {
                if (operation == Operation.GT) {
                    if (type == int.class) {
                        irLeftConstantNode.attachDecoration(new IRDConstant(
                                (int)irLeftConstantNode.getDecorationValue(IRDConstant.class) >
                                (int)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                    } else if (type == long.class) {
                        irLeftConstantNode.attachDecoration(new IRDConstant(
                                (long)irLeftConstantNode.getDecorationValue(IRDConstant.class) >
                                (long)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                    } else if (type == float.class) {
                        irLeftConstantNode.attachDecoration(new IRDConstant(
                                (float)irLeftConstantNode.getDecorationValue(IRDConstant.class) >
                                (float)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                    } else if (type == double.class) {
                        irLeftConstantNode.attachDecoration(new IRDConstant(
                                (double)irLeftConstantNode.getDecorationValue(IRDConstant.class) >
                                (double)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                    } else {
                        throw irComparisonNode.getLocation().createError(new IllegalStateException("constant folding error: " +
                                "unexpected type [" + PainlessLookupUtility.typeToCanonicalTypeName(type) + "] for " +
                                "comparison operation [" + operation.symbol + "] on " +
                                "constants [" + irLeftConstantNode.getDecorationString(IRDConstant.class) + "] " +
                                "and [" + irRightConstantNode.getDecorationString(IRDConstant.class) + "]"));
                    }

                    irLeftConstantNode.attachDecoration(new IRDExpressionType(boolean.class));
                    irTransformNode = irLeftConstantNode;
                } else if (operation == Operation.GTE) {
                    if (type == int.class) {
                        irLeftConstantNode.attachDecoration(new IRDConstant(
                                (int)irLeftConstantNode.getDecorationValue(IRDConstant.class) >=
                                (int)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                    } else if (type == long.class) {
                        irLeftConstantNode.attachDecoration(new IRDConstant(
                                (long)irLeftConstantNode.getDecorationValue(IRDConstant.class) >=
                                (long)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                    } else if (type == float.class) {
                        irLeftConstantNode.attachDecoration(new IRDConstant(
                                (float)irLeftConstantNode.getDecorationValue(IRDConstant.class) >=
                                (float)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                    } else if (type == double.class) {
                        irLeftConstantNode.attachDecoration(new IRDConstant(
                                (double)irLeftConstantNode.getDecorationValue(IRDConstant.class) >=
                                (double)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                    } else {
                        throw irComparisonNode.getLocation().createError(new IllegalStateException("constant folding error: " +
                                "unexpected type [" + PainlessLookupUtility.typeToCanonicalTypeName(type) + "] for " +
                                "comparison operation [" + operation.symbol + "] on " +
                                "constants [" + irLeftConstantNode.getDecorationString(IRDConstant.class) + "] " +
                                "and [" + irRightConstantNode.getDecorationString(IRDConstant.class) + "]"));
                    }

                    irLeftConstantNode.attachDecoration(new IRDExpressionType(boolean.class));
                    irTransformNode = irLeftConstantNode;
                } else if (operation == Operation.LT) {
                    if (type == int.class) {
                        irLeftConstantNode.attachDecoration(new IRDConstant(
                                (int)irLeftConstantNode.getDecorationValue(IRDConstant.class) <
                                (int)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                    } else if (type == long.class) {
                        irLeftConstantNode.attachDecoration(new IRDConstant(
                                (long)irLeftConstantNode.getDecorationValue(IRDConstant.class) <
                                (long)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                    } else if (type == float.class) {
                        irLeftConstantNode.attachDecoration(new IRDConstant(
                                (float)irLeftConstantNode.getDecorationValue(IRDConstant.class) <
                                (float)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                    } else if (type == double.class) {
                        irLeftConstantNode.attachDecoration(new IRDConstant(
                                (double)irLeftConstantNode.getDecorationValue(IRDConstant.class) <
                                (double)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                    } else {
                        throw irComparisonNode.getLocation().createError(new IllegalStateException("constant folding error: " +
                                "unexpected type [" + PainlessLookupUtility.typeToCanonicalTypeName(type) + "] for " +
                                "comparison operation [" + operation.symbol + "] on " +
                                "constants [" + irLeftConstantNode.getDecorationString(IRDConstant.class) + "] " +
                                "and [" + irRightConstantNode.getDecorationString(IRDConstant.class) + "]"));
                    }

                    irLeftConstantNode.attachDecoration(new IRDExpressionType(boolean.class));
                    irTransformNode = irLeftConstantNode;
                } else if (operation == Operation.LTE) {
                    if (type == int.class) {
                        irLeftConstantNode.attachDecoration(new IRDConstant(
                                (int)irLeftConstantNode.getDecorationValue(IRDConstant.class) <=
                                (int)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                    } else if (type == long.class) {
                        irLeftConstantNode.attachDecoration(new IRDConstant(
                                (long)irLeftConstantNode.getDecorationValue(IRDConstant.class) <=
                                (long)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                    } else if (type == float.class) {
                        irLeftConstantNode.attachDecoration(new IRDConstant(
                                (float)irLeftConstantNode.getDecorationValue(IRDConstant.class) <=
                                (float)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                    } else if (type == double.class) {
                        irLeftConstantNode.attachDecoration(new IRDConstant(
                                (double)irLeftConstantNode.getDecorationValue(IRDConstant.class) <=
                                (double)irRightConstantNode.getDecorationValue(IRDConstant.class)));
                    } else {
                        throw irComparisonNode.getLocation().createError(new IllegalStateException("constant folding error: " +
                                "unexpected type [" + PainlessLookupUtility.typeToCanonicalTypeName(type) + "] for " +
                                "comparison operation [" + operation.symbol + "] on " +
                                "constants [" + irLeftConstantNode.getDecorationString(IRDConstant.class) + "] " +
                                "and [" + irRightConstantNode.getDecorationString(IRDConstant.class) + "]"));
                    }

                    irLeftConstantNode.attachDecoration(new IRDExpressionType(boolean.class));
                    irTransformNode = irLeftConstantNode;
                }
            }
        }

        return irTransformNode;
    }

    @Override
    public IRNode transformCast(CastNode irCastNode, Void scope) {
        irCastNode.transformChildren(this, scope);
        IRNode irTransformNode = irCastNode;

        if (irCastNode.getChildNode() instanceof ConstantNode &&
                PainlessLookupUtility.isConstantType(irCastNode.getDecorationValue(IRDExpressionType.class))) {
            ConstantNode irConstantNode = (ConstantNode)irCastNode.getChildNode();
            irConstantNode.attachDecoration(new IRDConstant(AnalyzerCaster.constCast(irCastNode.getLocation(),
                    irConstantNode.getDecorationValue(IRDConstant.class), irCastNode.getDecorationValue(IRDCast.class))));
            irConstantNode.attachDecoration(irCastNode.getDecoration(IRDExpressionType.class));
            irTransformNode = irConstantNode;
        }

        return irTransformNode;
    }
}
