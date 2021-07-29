/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */

package org.elasticsearch.painless.phase;

import org.elasticsearch.painless.AnalyzerCaster;
import org.elasticsearch.painless.Location;
import org.elasticsearch.painless.Operation;
import org.elasticsearch.painless.ir.BinaryImplNode;
import org.elasticsearch.painless.ir.BinaryMathNode;
import org.elasticsearch.painless.ir.BinaryRegexNode;
import org.elasticsearch.painless.ir.BooleanNode;
import org.elasticsearch.painless.ir.CastNode;
import org.elasticsearch.painless.ir.ComparisonNode;
import org.elasticsearch.painless.ir.ConditionalNode;
import org.elasticsearch.painless.ir.ConstantNode;
import org.elasticsearch.painless.ir.DeclarationNode;
import org.elasticsearch.painless.ir.DoWhileLoopNode;
import org.elasticsearch.painless.ir.DupNode;
import org.elasticsearch.painless.ir.ElvisNode;
import org.elasticsearch.painless.ir.ExpressionNode;
import org.elasticsearch.painless.ir.FlipArrayIndexNode;
import org.elasticsearch.painless.ir.FlipCollectionIndexNode;
import org.elasticsearch.painless.ir.FlipDefIndexNode;
import org.elasticsearch.painless.ir.ForEachSubArrayNode;
import org.elasticsearch.painless.ir.ForEachSubIterableNode;
import org.elasticsearch.painless.ir.ForLoopNode;
import org.elasticsearch.painless.ir.IfElseNode;
import org.elasticsearch.painless.ir.IfNode;
import org.elasticsearch.painless.ir.InstanceofNode;
import org.elasticsearch.painless.ir.InvokeCallDefNode;
import org.elasticsearch.painless.ir.InvokeCallMemberNode;
import org.elasticsearch.painless.ir.InvokeCallNode;
import org.elasticsearch.painless.ir.ListInitializationNode;
import org.elasticsearch.painless.ir.MapInitializationNode;
import org.elasticsearch.painless.ir.NewArrayNode;
import org.elasticsearch.painless.ir.NewObjectNode;
import org.elasticsearch.painless.ir.NullNode;
import org.elasticsearch.painless.ir.NullSafeSubNode;
import org.elasticsearch.painless.ir.ReturnNode;
import org.elasticsearch.painless.ir.StatementExpressionNode;
import org.elasticsearch.painless.ir.StoreBraceDefNode;
import org.elasticsearch.painless.ir.StoreBraceNode;
import org.elasticsearch.painless.ir.StoreDotDefNode;
import org.elasticsearch.painless.ir.StoreDotNode;
import org.elasticsearch.painless.ir.StoreDotShortcutNode;
import org.elasticsearch.painless.ir.StoreFieldMemberNode;
import org.elasticsearch.painless.ir.StoreListShortcutNode;
import org.elasticsearch.painless.ir.StoreMapShortcutNode;
import org.elasticsearch.painless.ir.StoreVariableNode;
import org.elasticsearch.painless.ir.StringConcatenationNode;
import org.elasticsearch.painless.ir.ThrowNode;
import org.elasticsearch.painless.ir.UnaryMathNode;
import org.elasticsearch.painless.ir.WhileLoopNode;
import org.elasticsearch.painless.lookup.PainlessInstanceBinding;
import org.elasticsearch.painless.lookup.PainlessLookupUtility;
import org.elasticsearch.painless.lookup.PainlessMethod;
import org.elasticsearch.painless.spi.annotation.CompileTimeOnlyAnnotation;
import org.elasticsearch.painless.symbol.IRDecorations.IRDCast;
import org.elasticsearch.painless.symbol.IRDecorations.IRDComparisonType;
import org.elasticsearch.painless.symbol.IRDecorations.IRDInstanceBinding;
import org.elasticsearch.painless.symbol.IRDecorations.IRDMethod;
import org.elasticsearch.painless.symbol.IRDecorations.IRDOperation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;

/**
 * This optimization pass will perform the specified operation on two leafs nodes if they are both
 * constants. The additional overrides for visiting ir nodes in this class are required whenever
 * there is a child node that is an expression. The structure of the tree does not have a way
 * for a child node to introspect into its parent node, so to replace itself the parent node
 * must pass the child node's particular set method as method reference.
 */
public class DefaultConstantFoldingOptimizationPhase extends IRTreeBaseVisitor<Consumer<ExpressionNode>> {

    @Override
    public void visitIf(IfNode irIfNode, Consumer<ExpressionNode> scope) {
        irIfNode.getConditionNode().visit(this, irIfNode::setConditionNode);
        irIfNode.getBlockNode().visit(this, null);
    }

    @Override
    public void visitIfElse(IfElseNode irIfElseNode, Consumer<ExpressionNode> scope) {
        irIfElseNode.getConditionNode().visit(this, irIfElseNode::setConditionNode);
        irIfElseNode.getBlockNode().visit(this, null);
        irIfElseNode.getElseBlockNode().visit(this, null);
    }

    @Override
    public void visitWhileLoop(WhileLoopNode irWhileLoopNode, Consumer<ExpressionNode> scope) {
        if (irWhileLoopNode.getConditionNode() != null) {
            irWhileLoopNode.getConditionNode().visit(this, irWhileLoopNode::setConditionNode);
        }

        if (irWhileLoopNode.getBlockNode() != null) {
            irWhileLoopNode.getBlockNode().visit(this, null);
        }
    }

    @Override
    public void visitDoWhileLoop(DoWhileLoopNode irDoWhileLoopNode, Consumer<ExpressionNode> scope) {
        irDoWhileLoopNode.getBlockNode().visit(this, null);

        if (irDoWhileLoopNode.getConditionNode() != null) {
            irDoWhileLoopNode.getConditionNode().visit(this, irDoWhileLoopNode::setConditionNode);
        }
    }

    @Override
    public void visitForLoop(ForLoopNode irForLoopNode, Consumer<ExpressionNode> scope) {
        if (irForLoopNode.getInitializerNode() != null) {
            irForLoopNode.getInitializerNode().visit(this, irForLoopNode::setInitialzerNode);
        }

        if (irForLoopNode.getConditionNode() != null) {
            irForLoopNode.getConditionNode().visit(this, irForLoopNode::setConditionNode);
        }

        if (irForLoopNode.getAfterthoughtNode() != null) {
            irForLoopNode.getAfterthoughtNode().visit(this, irForLoopNode::setAfterthoughtNode);
        }

        if (irForLoopNode.getBlockNode() != null) {
            irForLoopNode.getBlockNode().visit(this, null);
        }
    }

    @Override
    public void visitForEachSubArrayLoop(ForEachSubArrayNode irForEachSubArrayNode, Consumer<ExpressionNode> scope) {
        irForEachSubArrayNode.getConditionNode().visit(this, irForEachSubArrayNode::setConditionNode);
        irForEachSubArrayNode.getBlockNode().visit(this, null);
    }

    @Override
    public void visitForEachSubIterableLoop(ForEachSubIterableNode irForEachSubIterableNode, Consumer<ExpressionNode> scope) {
        irForEachSubIterableNode.getConditionNode().visit(this, irForEachSubIterableNode::setConditionNode);
        irForEachSubIterableNode.getBlockNode().visit(this, null);
    }

    @Override
    public void visitDeclaration(DeclarationNode irDeclarationNode, Consumer<ExpressionNode> scope) {
        if (irDeclarationNode.getExpressionNode() != null) {
            irDeclarationNode.getExpressionNode().visit(this, irDeclarationNode::setExpressionNode);
        }
    }

    @Override
    public void visitReturn(ReturnNode irReturnNode, Consumer<ExpressionNode> scope) {
        if (irReturnNode.getExpressionNode() != null) {
            irReturnNode.getExpressionNode().visit(this, irReturnNode::setExpressionNode);
        }
    }

    @Override
    public void visitStatementExpression(StatementExpressionNode irStatementExpressionNode, Consumer<ExpressionNode> scope) {
        irStatementExpressionNode.getExpressionNode().visit(this, irStatementExpressionNode::setExpressionNode);
    }

    @Override
    public void visitThrow(ThrowNode irThrowNode, Consumer<ExpressionNode> scope) {
        irThrowNode.getExpressionNode().visit(this, irThrowNode::setExpressionNode);
    }

    @Override
    public void visitBinaryImpl(BinaryImplNode irBinaryImplNode, Consumer<ExpressionNode> scope) {
        irBinaryImplNode.getLeftNode().visit(this, irBinaryImplNode::setLeftNode);
        irBinaryImplNode.getRightNode().visit(this, irBinaryImplNode::setRightNode);
    }

    @Override
    public void visitUnaryMath(UnaryMathNode irUnaryMathNode, Consumer<ExpressionNode> scope) {
        irUnaryMathNode.getChildNode().visit(this, irUnaryMathNode::setChildNode);

        if (irUnaryMathNode.getChildNode() instanceof ConstantNode) {
            Object constantValue = ((ConstantNode)irUnaryMathNode.getChildNode()).getConstantValue();
            Location location = irUnaryMathNode.getLocation();
            Operation operation = irUnaryMathNode.getDecorationValue(IRDOperation.class);
            Class<?> type = irUnaryMathNode.getExpressionType();
            ConstantNode irConstantNode;

            if (operation == Operation.SUB) {
                if (type == int.class) {
                    irConstantNode = new ConstantNode(location, type, -(int)constantValue);
                } else if (type == long.class) {
                    irConstantNode = new ConstantNode(location, type, -(long)constantValue);
                } else if (type == float.class) {
                    irConstantNode = new ConstantNode(location, type, -(float)constantValue);
                } else if (type == double.class) {
                    irConstantNode = new ConstantNode(location, type, -(double)constantValue);
                } else {
                    throw irUnaryMathNode.getLocation().createError(new IllegalStateException("constant folding error: " +
                            "unexpected type [" + PainlessLookupUtility.typeToCanonicalTypeName(type) + "] for " +
                            "unary operation [" + operation.symbol + "] on " +
                            "constant [" + constantValue + "]"));
                }

                scope.accept(irConstantNode);
            } else if (operation == Operation.BWNOT) {
                if (type == int.class) {
                    irConstantNode = new ConstantNode(location, type, ~(int)constantValue);
                } else if (type == long.class) {
                    irConstantNode = new ConstantNode(location, type, ~(long)constantValue);
                } else {
                    throw irUnaryMathNode.getLocation().createError(new IllegalStateException("constant folding error: " +
                            "unexpected type [" + PainlessLookupUtility.typeToCanonicalTypeName(type) + "] for " +
                            "unary operation [" + operation.symbol + "] on " +
                            "constant [" + constantValue + "]"));
                }

                scope.accept(irConstantNode);
            } else if (operation == Operation.NOT) {
                if (type == boolean.class) {
                    irConstantNode = new ConstantNode(location, type, ((boolean)constantValue) == false);
                } else {
                    throw irUnaryMathNode.getLocation().createError(new IllegalStateException("constant folding error: " +
                            "unexpected type [" + PainlessLookupUtility.typeToCanonicalTypeName(type) + "] for " +
                            "unary operation [" + operation.symbol + "] on " +
                            "constant [" + constantValue + "]"));
                }

                scope.accept(irConstantNode);
            } else if (operation == Operation.ADD) {
                scope.accept(irUnaryMathNode.getChildNode());
            }
        }
    }

    @Override
    public void visitBinaryMath(BinaryMathNode irBinaryMathNode, Consumer<ExpressionNode> scope) {
        irBinaryMathNode.getLeftNode().visit(this, irBinaryMathNode::setLeftNode);
        irBinaryMathNode.getRightNode().visit(this, irBinaryMathNode::setRightNode);

        if (irBinaryMathNode.getLeftNode() instanceof ConstantNode && irBinaryMathNode.getRightNode() instanceof ConstantNode) {
            ConstantNode irLeftConstantNode = (ConstantNode)irBinaryMathNode.getLeftNode();
            ConstantNode irRightConstantNode = (ConstantNode)irBinaryMathNode.getRightNode();
            Object leftConstantValue = irLeftConstantNode.getConstantValue();
            Object rightConstantValue = irRightConstantNode.getConstantValue();
            Location location = irBinaryMathNode.getLocation();
            Operation operation = irBinaryMathNode.getDecorationValue(IRDOperation.class);
            Class<?> type = irBinaryMathNode.getExpressionType();
            ConstantNode irConstantNode;

            if (operation == Operation.MUL) {
                if (type == int.class) {
                    irConstantNode = new ConstantNode(location, type, (int)leftConstantValue * (int)rightConstantValue);
                } else if (type == long.class) {
                    irConstantNode = new ConstantNode(location, type, (long)leftConstantValue * (long)rightConstantValue);
                } else if (type == float.class) {
                    irConstantNode = new ConstantNode(location, type, (float)leftConstantValue * (float)rightConstantValue);
                } else if (type == double.class) {
                    irConstantNode = new ConstantNode(location, type, (double)leftConstantValue * (double)rightConstantValue);
                } else {
                    throw location.createError(new IllegalStateException("constant folding error: " +
                            "unexpected type [" + PainlessLookupUtility.typeToCanonicalTypeName(type) + "] for " +
                            "binary operation [" + operation.symbol + "] on " +
                            "constants [" + leftConstantValue + "] " +
                            "and [" + rightConstantValue + "]"));
                }

                scope.accept(irConstantNode);
            } else if (operation == Operation.DIV) {
                try {
                    if (type == int.class) {
                        irConstantNode = new ConstantNode(location, type, (int)leftConstantValue / (int)rightConstantValue);
                    } else if (type == long.class) {
                        irConstantNode = new ConstantNode(location, type, (long)leftConstantValue / (long)rightConstantValue);
                    } else if (type == float.class) {
                        irConstantNode = new ConstantNode(location, type, (float)leftConstantValue / (float)rightConstantValue);
                    } else if (type == double.class) {
                        irConstantNode = new ConstantNode(location, type, (double)leftConstantValue / (double)rightConstantValue);
                    } else {
                        throw location.createError(new IllegalStateException("constant folding error: " +
                                "unexpected type [" + PainlessLookupUtility.typeToCanonicalTypeName(type) + "] for " +
                                "binary operation [" + operation.symbol + "] on " +
                                "constants [" + leftConstantValue + "] " +
                                "and [" + rightConstantValue + "]"));
                    }
                } catch (ArithmeticException ae) {
                    throw irBinaryMathNode.getLocation().createError(ae);
                }

                scope.accept(irConstantNode);
            } else if (operation == Operation.REM) {
                try {
                    if (type == int.class) {
                        irConstantNode = new ConstantNode(location, type, (int)leftConstantValue % (int)rightConstantValue);
                    } else if (type == long.class) {
                        irConstantNode = new ConstantNode(location, type, (long)leftConstantValue % (long)rightConstantValue);
                    } else if (type == float.class) {
                        irConstantNode = new ConstantNode(location, type, (float)leftConstantValue % (float)rightConstantValue);
                    } else if (type == double.class) {
                        irConstantNode = new ConstantNode(location, type, (double)leftConstantValue % (double)rightConstantValue);
                    } else {
                        throw location.createError(new IllegalStateException("constant folding error: " +
                                "unexpected type [" + PainlessLookupUtility.typeToCanonicalTypeName(type) + "] for " +
                                "binary operation [" + operation.symbol + "] on " +
                                "constants [" + leftConstantValue + "] " +
                                "and [" + rightConstantValue + "]"));
                    }
                } catch (ArithmeticException ae) {
                    throw location.createError(ae);
                }

                scope.accept(irConstantNode);
            } else if (operation == Operation.ADD) {
                if (type == int.class) {
                    irConstantNode = new ConstantNode(location, type, (int)leftConstantValue + (int)rightConstantValue);
                } else if (type == long.class) {
                    irConstantNode = new ConstantNode(location, type, (long)leftConstantValue + (long)rightConstantValue);
                } else if (type == float.class) {
                    irConstantNode = new ConstantNode(location, type, (float)leftConstantValue + (float)rightConstantValue);
                } else if (type == double.class) {
                    irConstantNode = new ConstantNode(location, type, (double)leftConstantValue + (double)rightConstantValue);
                } else {
                    throw location.createError(new IllegalStateException("constant folding error: " +
                            "unexpected type [" + PainlessLookupUtility.typeToCanonicalTypeName(type) + "] for " +
                            "binary operation [" + operation.symbol + "] on " +
                            "constants [" + leftConstantValue + "] " +
                            "and [" + rightConstantValue + "]"));
                }

                scope.accept(irConstantNode);
            } else if (operation == Operation.SUB) {
                if (type == int.class) {
                    irConstantNode = new ConstantNode(location, type, (int)leftConstantValue - (int)rightConstantValue);
                } else if (type == long.class) {
                    irConstantNode = new ConstantNode(location, type, (long)leftConstantValue - (long)rightConstantValue);
                } else if (type == float.class) {
                    irConstantNode = new ConstantNode(location, type, (float)leftConstantValue - (float)rightConstantValue);
                } else if (type == double.class) {
                    irConstantNode = new ConstantNode(location, type, (double)leftConstantValue - (double)rightConstantValue);
                } else {
                    throw location.createError(new IllegalStateException("constant folding error: " +
                            "unexpected type [" + PainlessLookupUtility.typeToCanonicalTypeName(type) + "] for " +
                            "binary operation [" + operation.symbol + "] on " +
                            "constants [" + leftConstantValue + "] " +
                            "and [" + rightConstantValue + "]"));
                }

                scope.accept(irConstantNode);
            } else if (operation == Operation.LSH) {
                if (type == int.class) {
                    irConstantNode = new ConstantNode(location, type, (int)leftConstantValue << (int)rightConstantValue);
                } else if (type == long.class) {
                    irConstantNode = new ConstantNode(location, type, (long)leftConstantValue << (int)rightConstantValue);
                } else {
                    throw location.createError(new IllegalStateException("constant folding error: " +
                            "unexpected type [" + PainlessLookupUtility.typeToCanonicalTypeName(type) + "] for " +
                            "binary operation [" + operation.symbol + "] on " +
                            "constants [" + leftConstantValue + "] " +
                            "and [" + rightConstantValue + "]"));
                }

                scope.accept(irConstantNode);
            } else if (operation == Operation.RSH) {
                if (type == int.class) {
                    irConstantNode = new ConstantNode(location, type, (int)leftConstantValue >> (int)rightConstantValue);
                } else if (type == long.class) {
                    irConstantNode = new ConstantNode(location, type, (long)leftConstantValue >> (int)rightConstantValue);
                } else {
                    throw location.createError(new IllegalStateException("constant folding error: " +
                            "unexpected type [" + PainlessLookupUtility.typeToCanonicalTypeName(type) + "] for " +
                            "binary operation [" + operation.symbol + "] on " +
                            "constants [" + leftConstantValue + "] " +
                            "and [" + rightConstantValue + "]"));
                }

                scope.accept(irConstantNode);
            } else if (operation == Operation.USH) {
                if (type == int.class) {
                    irConstantNode = new ConstantNode(location, type, (int)leftConstantValue >>> (int)rightConstantValue);
                } else if (type == long.class) {
                    irConstantNode = new ConstantNode(location, type, (long)leftConstantValue >>> (int)rightConstantValue);
                } else {
                    throw location.createError(new IllegalStateException("constant folding error: " +
                            "unexpected type [" + PainlessLookupUtility.typeToCanonicalTypeName(type) + "] for " +
                            "binary operation [" + operation.symbol + "] on " +
                            "constants [" + leftConstantValue + "] " +
                            "and [" + rightConstantValue + "]"));
                }

                scope.accept(irConstantNode);
            } else if (operation == Operation.BWAND) {
                if (type == int.class) {
                    irConstantNode = new ConstantNode(location, type, (int)leftConstantValue & (int)rightConstantValue);
                } else if (type == long.class) {
                    irConstantNode = new ConstantNode(location, type, (long)leftConstantValue & (long)rightConstantValue);
                } else {
                    throw location.createError(new IllegalStateException("constant folding error: " +
                            "unexpected type [" + PainlessLookupUtility.typeToCanonicalTypeName(type) + "] for " +
                            "binary operation [" + operation.symbol + "] on " +
                            "constants [" + leftConstantValue + "] " +
                            "and [" + rightConstantValue + "]"));
                }

                scope.accept(irConstantNode);
            } else if (operation == Operation.XOR) {
                if (type == boolean.class) {
                    irConstantNode = new ConstantNode(location, type, (boolean)leftConstantValue ^ (boolean)rightConstantValue);
                } else if (type == int.class) {
                    irConstantNode = new ConstantNode(location, type, (int)leftConstantValue ^ (int)rightConstantValue);
                } else if (type == long.class) {
                    irConstantNode = new ConstantNode(location, type, (long)leftConstantValue ^ (long)rightConstantValue);
                } else {
                    throw location.createError(new IllegalStateException("constant folding error: " +
                            "unexpected type [" + PainlessLookupUtility.typeToCanonicalTypeName(type) + "] for " +
                            "binary operation [" + operation.symbol + "] on " +
                            "constants [" + leftConstantValue + "] " +
                            "and [" + rightConstantValue + "]"));
                }

                scope.accept(irConstantNode);
            } else if (operation == Operation.BWOR) {
                if (type == int.class) {
                    irConstantNode = new ConstantNode(location, type, (int)leftConstantValue | (int)rightConstantValue);
                } else if (type == long.class) {
                    irConstantNode = new ConstantNode(location, type, (long)leftConstantValue | (long)rightConstantValue);
                } else {
                    throw location.createError(new IllegalStateException("constant folding error: " +
                            "unexpected type [" + PainlessLookupUtility.typeToCanonicalTypeName(type) + "] for " +
                            "binary operation [" + operation.symbol + "] on " +
                            "constants [" + leftConstantValue + "] " +
                            "and [" + rightConstantValue + "]"));
                }

                scope.accept(irConstantNode);
            }
        }
    }

    @Override
    public void visitBinaryRegex(BinaryRegexNode irBinaryRegexNode, Consumer<ExpressionNode> scope) {
        irBinaryRegexNode.getLeftNode().visit(this, irBinaryRegexNode::setLeftNode);
        irBinaryRegexNode.getRightNode().visit(this, irBinaryRegexNode::setRightNode);
    }

    @Override
    public void visitStringConcatenation(StringConcatenationNode irStringConcatenationNode, Consumer<ExpressionNode> scope) {
        irStringConcatenationNode.getArgumentNodes().get(0).visit(this, (e) -> irStringConcatenationNode.getArgumentNodes().set(0, e));

        int i = 0;

        while (i < irStringConcatenationNode.getArgumentNodes().size() - 1) {
            ExpressionNode irLeftNode = irStringConcatenationNode.getArgumentNodes().get(i);
            ExpressionNode irRightNode = irStringConcatenationNode.getArgumentNodes().get(i + 1);

            int j = i;
            irRightNode.visit(this, (e) -> irStringConcatenationNode.getArgumentNodes().set(j + 1, e));

            if (irLeftNode instanceof ConstantNode && irRightNode instanceof ConstantNode) {
                ConstantNode irConstantNode = new ConstantNode(irLeftNode.getLocation(), String.class,
                        "" + ((ConstantNode)irLeftNode).getConstantValue() + ((ConstantNode)irRightNode).getConstantValue());
                irStringConcatenationNode.getArgumentNodes().set(i, irConstantNode);
                irStringConcatenationNode.getArgumentNodes().remove(i + 1);
            } else if (irLeftNode instanceof NullNode && irRightNode instanceof ConstantNode) {
                ConstantNode irConstantNode = new ConstantNode(irLeftNode.getLocation(), String.class,
                        "" + null + ((ConstantNode)irRightNode).getConstantValue());
                irStringConcatenationNode.getArgumentNodes().set(i, irConstantNode);
                irStringConcatenationNode.getArgumentNodes().remove(i + 1);
            } else if (irLeftNode instanceof ConstantNode && irRightNode instanceof NullNode) {
                ConstantNode irConstantNode = new ConstantNode(irLeftNode.getLocation(), String.class,
                        "" + ((ConstantNode)irLeftNode).getConstantValue() + null);
                irStringConcatenationNode.getArgumentNodes().set(i, irConstantNode);
                irStringConcatenationNode.getArgumentNodes().remove(i + 1);
            } else if (irLeftNode instanceof NullNode && irRightNode instanceof NullNode) {
                ConstantNode irConstantNode = new ConstantNode(irLeftNode.getLocation(), String.class, "" + null + null);
                irStringConcatenationNode.getArgumentNodes().set(i, irConstantNode);
                irStringConcatenationNode.getArgumentNodes().remove(i + 1);
            } else {
                i++;
            }
        }

        if (irStringConcatenationNode.getArgumentNodes().size() == 1) {
            ExpressionNode irArgumentNode = irStringConcatenationNode.getArgumentNodes().get(0);

            if (irArgumentNode instanceof ConstantNode) {
                scope.accept(irArgumentNode);
            }
        }
    }

    @Override
    public void visitBoolean(BooleanNode irBooleanNode, Consumer<ExpressionNode> scope) {
        irBooleanNode.getLeftNode().visit(this, irBooleanNode::setLeftNode);
        irBooleanNode.getRightNode().visit(this, irBooleanNode::setRightNode);

        if (irBooleanNode.getLeftNode() instanceof ConstantNode && irBooleanNode.getRightNode() instanceof ConstantNode) {
            ConstantNode irLeftConstantNode = (ConstantNode)irBooleanNode.getLeftNode();
            ConstantNode irRightConstantNode = (ConstantNode)irBooleanNode.getRightNode();
            Location location = irBooleanNode.getLocation();
            Operation operation = irBooleanNode.getDecorationValue(IRDOperation.class);
            Class<?> type = irBooleanNode.getExpressionType();
            ConstantNode irConstantNode;

            if (operation == Operation.AND) {
                if (type == boolean.class) {
                    irConstantNode = new ConstantNode(location, type,
                            (boolean)irLeftConstantNode.getConstantValue() && (boolean)irRightConstantNode.getConstantValue());
                } else {
                    throw irBooleanNode.getLocation().createError(new IllegalStateException("constant folding error: " +
                            "unexpected type [" + PainlessLookupUtility.typeToCanonicalTypeName(type) + "] for " +
                            "binary operation [" + operation.symbol + "] on " +
                            "constants [" + irLeftConstantNode.getConstantValue() + "] " +
                            "and [" + irRightConstantNode.getConstantValue() + "]"));
                }

                scope.accept(irConstantNode);
            } else if (operation == Operation.OR) {
                if (type == boolean.class) {
                    irConstantNode = new ConstantNode(location, type,
                            (boolean)irLeftConstantNode.getConstantValue() || (boolean)irRightConstantNode.getConstantValue());
                } else {
                    throw irBooleanNode.getLocation().createError(new IllegalStateException("constant folding error: " +
                            "unexpected type [" + PainlessLookupUtility.typeToCanonicalTypeName(type) + "] for " +
                            "binary operation [" + operation.symbol + "] on " +
                            "constants [" + irLeftConstantNode.getConstantValue() + "] " +
                            "and [" + irRightConstantNode.getConstantValue() + "]"));
                }

                scope.accept(irConstantNode);
            }
        }
    }

    @Override
    public void visitComparison(ComparisonNode irComparisonNode, Consumer<ExpressionNode> scope) {
        irComparisonNode.getLeftNode().visit(this, irComparisonNode::setLeftNode);
        irComparisonNode.getRightNode().visit(this, irComparisonNode::setRightNode);

        if ((irComparisonNode.getLeftNode() instanceof ConstantNode || irComparisonNode.getLeftNode() instanceof NullNode)
                && (irComparisonNode.getRightNode() instanceof ConstantNode || irComparisonNode.getRightNode() instanceof NullNode)) {

            ConstantNode irLeftConstantNode =
                    irComparisonNode.getLeftNode() instanceof NullNode ? null : (ConstantNode)irComparisonNode.getLeftNode();
            ConstantNode irRightConstantNode =
                    irComparisonNode.getRightNode() instanceof NullNode ? null : (ConstantNode)irComparisonNode.getRightNode();
            Object leftConstantValue = irLeftConstantNode == null ? null : irLeftConstantNode.getConstantValue();
            Object rightConstantValue = irRightConstantNode == null ? null : irRightConstantNode.getConstantValue();
            Location location = irComparisonNode.getLocation();
            Operation operation = irComparisonNode.getDecorationValue(IRDOperation.class);
            Class<?> type = irComparisonNode.getDecorationValue(IRDComparisonType.class);
            ConstantNode irConstantNode;

            if (operation == Operation.EQ || operation == Operation.EQR) {
                if (irLeftConstantNode == null && irRightConstantNode == null) {
                    irConstantNode = new ConstantNode(irComparisonNode.getLeftNode().getLocation(), boolean.class, true);
                } else if (irLeftConstantNode == null || irRightConstantNode == null) {
                    irConstantNode = new ConstantNode(irComparisonNode.getLeftNode().getLocation(), boolean.class, false);
                } else if (type == boolean.class) {
                    irConstantNode = new ConstantNode(irComparisonNode.getLeftNode().getLocation(), boolean.class,
                            (boolean)leftConstantValue == (boolean)rightConstantValue);
                } else if (type == int.class) {
                    irConstantNode = new ConstantNode(irComparisonNode.getLeftNode().getLocation(), boolean.class,
                            (int)leftConstantValue == (int)rightConstantValue);
                } else if (type == long.class) {
                    irConstantNode = new ConstantNode(irComparisonNode.getLeftNode().getLocation(), boolean.class,
                            (long)leftConstantValue == (long)rightConstantValue);
                } else if (type == float.class) {
                    irConstantNode = new ConstantNode(irComparisonNode.getLeftNode().getLocation(), boolean.class,
                            (float)leftConstantValue == (float)rightConstantValue);
                } else if (type == double.class) {
                    irConstantNode = new ConstantNode(irComparisonNode.getLeftNode().getLocation(), boolean.class,
                            (double)leftConstantValue == (double)rightConstantValue);
                } else {
                    if (operation == Operation.EQ) {
                        irConstantNode = new ConstantNode(irComparisonNode.getLeftNode().getLocation(), boolean.class,
                                leftConstantValue.equals(rightConstantValue));
                    } else {
                        irConstantNode = new ConstantNode(irComparisonNode.getLeftNode().getLocation(), boolean.class,
                                leftConstantValue == rightConstantValue);
                    }
                }

                scope.accept(irConstantNode);
            } else if (operation == Operation.NE || operation == Operation.NER) {
                if (irLeftConstantNode == null && irRightConstantNode == null) {
                    irConstantNode = new ConstantNode(irComparisonNode.getLeftNode().getLocation(), boolean.class, false);
                } else if (irLeftConstantNode == null || irRightConstantNode == null) {
                    irConstantNode = new ConstantNode(irComparisonNode.getLeftNode().getLocation(), boolean.class, true);
                } else if (type == boolean.class) {
                    irConstantNode = new ConstantNode(irComparisonNode.getLeftNode().getLocation(), boolean.class,
                            (boolean)leftConstantValue != (boolean)rightConstantValue);
                } else if (type == int.class) {
                    irConstantNode = new ConstantNode(irComparisonNode.getLeftNode().getLocation(), boolean.class,
                            (int)leftConstantValue != (int)rightConstantValue);
                } else if (type == long.class) {
                    irConstantNode = new ConstantNode(irComparisonNode.getLeftNode().getLocation(), boolean.class,
                            (long)leftConstantValue != (long)rightConstantValue);
                } else if (type == float.class) {
                    irConstantNode = new ConstantNode(irComparisonNode.getLeftNode().getLocation(), boolean.class,
                            (float)leftConstantValue != (float)rightConstantValue);
                } else if (type == double.class) {
                    irConstantNode = new ConstantNode(irComparisonNode.getLeftNode().getLocation(), boolean.class,
                            (double)leftConstantValue != (double)rightConstantValue);
                } else {
                    if (operation == Operation.NE) {
                        irConstantNode = new ConstantNode(irComparisonNode.getLeftNode().getLocation(), boolean.class,
                                leftConstantValue.equals(rightConstantValue) == false);
                    } else {
                        irConstantNode = new ConstantNode(irComparisonNode.getLeftNode().getLocation(), boolean.class,
                                leftConstantValue != rightConstantValue);
                    }
                }

                scope.accept(irConstantNode);
            } else if (irLeftConstantNode != null && irRightConstantNode != null) {
                if (operation == Operation.GT) {
                    if (type == int.class) {
                        irConstantNode = new ConstantNode(irComparisonNode.getLeftNode().getLocation(), boolean.class,
                                (int)leftConstantValue > (int)rightConstantValue);
                    } else if (type == long.class) {
                        irConstantNode = new ConstantNode(irComparisonNode.getLeftNode().getLocation(), boolean.class,
                                (long)leftConstantValue > (long)rightConstantValue);
                    } else if (type == float.class) {
                        irConstantNode = new ConstantNode(irComparisonNode.getLeftNode().getLocation(), boolean.class,
                                (float)leftConstantValue > (float)rightConstantValue);
                    } else if (type == double.class) {
                        irConstantNode = new ConstantNode(irComparisonNode.getLeftNode().getLocation(), boolean.class,
                                (double)leftConstantValue > (double)rightConstantValue);
                    } else {
                        throw irComparisonNode.getLocation().createError(new IllegalStateException("constant folding error: " +
                                "unexpected type [" + PainlessLookupUtility.typeToCanonicalTypeName(type) + "] for " +
                                "comparison operation [" + operation.symbol + "] on " +
                                "constants [" + leftConstantValue + "] " +
                                "and [" + rightConstantValue + "]"));
                    }

                    scope.accept(irConstantNode);
                } else if (operation == Operation.GTE) {
                    if (type == int.class) {
                        irConstantNode = new ConstantNode(irComparisonNode.getLeftNode().getLocation(), boolean.class,
                                (int)leftConstantValue >= (int)rightConstantValue);
                    } else if (type == long.class) {
                        irConstantNode = new ConstantNode(irComparisonNode.getLeftNode().getLocation(), boolean.class,
                                (long)leftConstantValue >= (long)rightConstantValue);
                    } else if (type == float.class) {
                        irConstantNode = new ConstantNode(irComparisonNode.getLeftNode().getLocation(), boolean.class,
                                (float)leftConstantValue >= (float)rightConstantValue);
                    } else if (type == double.class) {
                        irConstantNode = new ConstantNode(irComparisonNode.getLeftNode().getLocation(), boolean.class,
                                (double)leftConstantValue >= (double)rightConstantValue);
                    } else {
                        throw irComparisonNode.getLocation().createError(new IllegalStateException("constant folding error: " +
                                "unexpected type [" + PainlessLookupUtility.typeToCanonicalTypeName(type) + "] for " +
                                "comparison operation [" + operation.symbol + "] on " +
                                "constants [" + leftConstantValue + "] " +
                                "and [" + rightConstantValue + "]"));
                    }

                    scope.accept(irConstantNode);
                } else if (operation == Operation.LT) {
                    if (type == int.class) {
                        irConstantNode = new ConstantNode(irComparisonNode.getLeftNode().getLocation(), boolean.class,
                                (int)leftConstantValue < (int)rightConstantValue);
                    } else if (type == long.class) {
                        irConstantNode = new ConstantNode(irComparisonNode.getLeftNode().getLocation(), boolean.class,
                                (long)leftConstantValue < (long)rightConstantValue);
                    } else if (type == float.class) {
                        irConstantNode = new ConstantNode(irComparisonNode.getLeftNode().getLocation(), boolean.class,
                                (float)leftConstantValue < (float)rightConstantValue);
                    } else if (type == double.class) {
                        irConstantNode = new ConstantNode(irComparisonNode.getLeftNode().getLocation(), boolean.class,
                                (double)leftConstantValue < (double)rightConstantValue);
                    } else {
                        throw irComparisonNode.getLocation().createError(new IllegalStateException("constant folding error: " +
                                "unexpected type [" + PainlessLookupUtility.typeToCanonicalTypeName(type) + "] for " +
                                "comparison operation [" + operation.symbol + "] on " +
                                "constants [" + leftConstantValue + "] " +
                                "and [" + rightConstantValue + "]"));
                    }

                    scope.accept(irConstantNode);
                } else if (operation == Operation.LTE) {
                    if (type == int.class) {
                        irConstantNode = new ConstantNode(irComparisonNode.getLeftNode().getLocation(), boolean.class,
                                (int)leftConstantValue <= (int)rightConstantValue);
                    } else if (type == long.class) {
                        irConstantNode = new ConstantNode(irComparisonNode.getLeftNode().getLocation(), boolean.class,
                                (long)leftConstantValue <= (long)rightConstantValue);
                    } else if (type == float.class) {
                        irConstantNode = new ConstantNode(irComparisonNode.getLeftNode().getLocation(), boolean.class,
                                (float)leftConstantValue <= (float)rightConstantValue);
                    } else if (type == double.class) {
                        irConstantNode = new ConstantNode(irComparisonNode.getLeftNode().getLocation(), boolean.class,
                                (double)leftConstantValue <= (double)rightConstantValue);
                    } else {
                        throw irComparisonNode.getLocation().createError(new IllegalStateException("constant folding error: " +
                                "unexpected type [" + PainlessLookupUtility.typeToCanonicalTypeName(type) + "] for " +
                                "comparison operation [" + operation.symbol + "] on " +
                                "constants [" + leftConstantValue + "] " +
                                "and [" + rightConstantValue + "]"));
                    }

                    scope.accept(irConstantNode);
                }
            }
        }
    }

    @Override
    public void visitCast(CastNode irCastNode, Consumer<ExpressionNode> scope) {
        irCastNode.getChildNode().visit(this, irCastNode::setChildNode);

        if (irCastNode.getChildNode() instanceof ConstantNode &&
                PainlessLookupUtility.isConstantType(irCastNode.getExpressionType())) {
            ConstantNode irConstantNode = (ConstantNode)irCastNode.getChildNode();
            Object constantValue = irConstantNode.getConstantValue();
            constantValue = AnalyzerCaster.constCast(irCastNode.getLocation(), constantValue, irCastNode.getDecorationValue(IRDCast.class));
            irConstantNode = new ConstantNode(irConstantNode.getLocation(), irCastNode.getExpressionType(), constantValue);
            scope.accept(irConstantNode);
        }
    }

    @Override
    public void visitInstanceof(InstanceofNode irInstanceofNode, Consumer<ExpressionNode> scope) {
        irInstanceofNode.getChildNode().visit(this, irInstanceofNode::setChildNode);
    }

    @Override
    public void visitConditional(ConditionalNode irConditionalNode, Consumer<ExpressionNode> scope) {
        irConditionalNode.getConditionNode().visit(this, irConditionalNode::setConditionNode);
        irConditionalNode.getLeftNode().visit(this, irConditionalNode::setLeftNode);
        irConditionalNode.getRightNode().visit(this, irConditionalNode::setRightNode);
    }

    @Override
    public void visitElvis(ElvisNode irElvisNode, Consumer<ExpressionNode> scope) {
        irElvisNode.getLeftNode().visit(this, irElvisNode::setLeftNode);
        irElvisNode.getRightNode().visit(this, irElvisNode::setRightNode);
    }

    @Override
    public void visitListInitialization(ListInitializationNode irListInitializationNode, Consumer<ExpressionNode> scope) {
        for (int i = 0; i < irListInitializationNode.getArgumentNodes().size(); i++) {
            int j = i;
            irListInitializationNode.getArgumentNodes().get(i).visit(this, (e) -> irListInitializationNode.getArgumentNodes().set(j, e));
        }
    }

    @Override
    public void visitMapInitialization(MapInitializationNode irMapInitializationNode, Consumer<ExpressionNode> scope) {
        for (int i = 0; i < irMapInitializationNode.getKeyNodes().size(); i++) {
            int j = i;
            irMapInitializationNode.getKeyNode(i).visit(this, (e) -> irMapInitializationNode.getKeyNodes().set(j, e));
        }

        for (int i = 0; i < irMapInitializationNode.getValueNodes().size(); i++) {
            int j = i;
            irMapInitializationNode.getValueNode(i).visit(this, (e) -> irMapInitializationNode.getValueNodes().set(j, e));
        }
    }

    @Override
    public void visitNewArray(NewArrayNode irNewArrayNode, Consumer<ExpressionNode> scope) {
        for (int i = 0; i < irNewArrayNode.getArgumentNodes().size(); i++) {
            int j = i;
            irNewArrayNode.getArgumentNodes().get(i).visit(this, (e) -> irNewArrayNode.getArgumentNodes().set(j, e));
        }
    }

    @Override
    public void visitNewObject(NewObjectNode irNewObjectNode, Consumer<ExpressionNode> scope) {
        for (int i = 0; i < irNewObjectNode.getArgumentNodes().size(); i++) {
            int j = i;
            irNewObjectNode.getArgumentNodes().get(i).visit(this, (e) -> irNewObjectNode.getArgumentNodes().set(j, e));
        }
    }

    @Override
    public void visitNullSafeSub(NullSafeSubNode irNullSafeSubNode, Consumer<ExpressionNode> scope) {
        irNullSafeSubNode.getChildNode().visit(this, irNullSafeSubNode::setChildNode);
    }

    @Override
    public void visitStoreVariable(StoreVariableNode irStoreVariableNode, Consumer<ExpressionNode> scope) {
        irStoreVariableNode.getChildNode().visit(this, irStoreVariableNode::setChildNode);
    }

    @Override
    public void visitStoreDotDef(StoreDotDefNode irStoreDotDefNode, Consumer<ExpressionNode> scope) {
        irStoreDotDefNode.getChildNode().visit(this, irStoreDotDefNode::setChildNode);
    }

    @Override
    public void visitStoreDot(StoreDotNode irStoreDotNode, Consumer<ExpressionNode> scope) {
        irStoreDotNode.getChildNode().visit(this, irStoreDotNode::setChildNode);
    }

    @Override
    public void visitStoreDotShortcut(StoreDotShortcutNode irDotSubShortcutNode, Consumer<ExpressionNode> scope) {
        irDotSubShortcutNode.getChildNode().visit(this, irDotSubShortcutNode::setChildNode);
    }

    @Override
    public void visitStoreListShortcut(StoreListShortcutNode irStoreListShortcutNode, Consumer<ExpressionNode> scope) {
        irStoreListShortcutNode.getChildNode().visit(this, irStoreListShortcutNode::setChildNode);
    }

    @Override
    public void visitStoreMapShortcut(StoreMapShortcutNode irStoreMapShortcutNode, Consumer<ExpressionNode> scope) {
        irStoreMapShortcutNode.getChildNode().visit(this, irStoreMapShortcutNode::setChildNode);
    }

    @Override
    public void visitStoreFieldMember(StoreFieldMemberNode irStoreFieldMemberNode, Consumer<ExpressionNode> scope) {
        irStoreFieldMemberNode.getChildNode().visit(this, irStoreFieldMemberNode::setChildNode);
    }

    @Override
    public void visitStoreBraceDef(StoreBraceDefNode irStoreBraceDefNode, Consumer<ExpressionNode> scope) {
        irStoreBraceDefNode.getChildNode().visit(this, irStoreBraceDefNode::setChildNode);
    }

    @Override
    public void visitStoreBrace(StoreBraceNode irStoreBraceNode, Consumer<ExpressionNode> scope) {
        irStoreBraceNode.getChildNode().visit(this, irStoreBraceNode::setChildNode);
    }

    @Override
    public void visitInvokeCallDef(InvokeCallDefNode irInvokeCallDefNode, Consumer<ExpressionNode> scope) {
        for (int i = 0; i < irInvokeCallDefNode.getArgumentNodes().size(); i++) {
            int j = i;
            irInvokeCallDefNode.getArgumentNodes().get(i).visit(this, (e) -> irInvokeCallDefNode.getArgumentNodes().set(j, e));
        }
    }

    @Override
    public void visitInvokeCall(InvokeCallNode irInvokeCallNode, Consumer<ExpressionNode> scope) {
        for (int i = 0; i < irInvokeCallNode.getArgumentNodes().size(); i++) {
            int j = i;
            irInvokeCallNode.getArgumentNodes().get(i).visit(this, (e) -> irInvokeCallNode.getArgumentNodes().set(j, e));
        }
    }

    @Override
    public void visitInvokeCallMember(InvokeCallMemberNode irInvokeCallMemberNode, Consumer<ExpressionNode> scope) {
        for (int i = 0; i < irInvokeCallMemberNode.getArgumentNodes().size(); i++) {
            int j = i;
            irInvokeCallMemberNode.getArgumentNodes().get(i).visit(this, (e) -> irInvokeCallMemberNode.getArgumentNodes().set(j, e));
        }
        PainlessMethod method = irInvokeCallMemberNode.getDecorationValue(IRDMethod.class);
        if (method != null && method.annotations.containsKey(CompileTimeOnlyAnnotation.class)) {
            replaceCallWithConstant(irInvokeCallMemberNode, scope, method.javaMethod, null);
            return;
        }
        PainlessInstanceBinding instanceBinding = irInvokeCallMemberNode.getDecorationValue(IRDInstanceBinding.class);
        if (instanceBinding != null && instanceBinding.annotations.containsKey(CompileTimeOnlyAnnotation.class)) {
            replaceCallWithConstant(irInvokeCallMemberNode, scope, instanceBinding.javaMethod, instanceBinding.targetInstance);
            return;
        }
    }

    private void replaceCallWithConstant(
        InvokeCallMemberNode irInvokeCallMemberNode,
        Consumer<ExpressionNode> scope,
        Method javaMethod,
        Object receiver
    ) {
        Object[] args = new Object[irInvokeCallMemberNode.getArgumentNodes().size()];
        for (int i = 0; i < irInvokeCallMemberNode.getArgumentNodes().size(); i++) {
            ExpressionNode argNode = irInvokeCallMemberNode.getArgumentNodes().get(i);
            if (argNode instanceof ConstantNode == false) {
                // TODO find a better string to output
                throw irInvokeCallMemberNode.getLocation()
                    .createError(
                        new IllegalArgumentException(
                            "all arguments to [" + javaMethod.getName() + "] must be constant but the [" + (i + 1) + "] argument isn't"
                        )
                    );
            }
            args[i] = ((ConstantNode)argNode).getConstantValue();
        }
        Object result;
        try {
            result = javaMethod.invoke(receiver, args);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw irInvokeCallMemberNode.getLocation()
                .createError(new IllegalArgumentException("error invoking [" + irInvokeCallMemberNode + "] at compile time", e));
        } catch (InvocationTargetException e) {
            throw irInvokeCallMemberNode.getLocation()
                .createError(new IllegalArgumentException("error invoking [" + irInvokeCallMemberNode + "] at compile time", e.getCause()));
        }
        ConstantNode replacement = new ConstantNode(
                irInvokeCallMemberNode.getLocation(), irInvokeCallMemberNode.getExpressionType(), result);
        scope.accept(replacement);
    }

    @Override
    public void visitFlipArrayIndex(FlipArrayIndexNode irFlipArrayIndexNode, Consumer<ExpressionNode> scope) {
        irFlipArrayIndexNode.getChildNode().visit(this, irFlipArrayIndexNode::setChildNode);
    }

    @Override
    public void visitFlipCollectionIndex(FlipCollectionIndexNode irFlipCollectionIndexNode, Consumer<ExpressionNode> scope) {
        irFlipCollectionIndexNode.getChildNode().visit(this, irFlipCollectionIndexNode::setChildNode);
    }

    @Override
    public void visitFlipDefIndex(FlipDefIndexNode irFlipDefIndexNode, Consumer<ExpressionNode> scope) {
        irFlipDefIndexNode.getChildNode().visit(this, irFlipDefIndexNode::setChildNode);
    }

    @Override
    public void visitDup(DupNode irDupNode, Consumer<ExpressionNode> scope) {
        irDupNode.getChildNode().visit(this, irDupNode::setChildNode);
    }
}
