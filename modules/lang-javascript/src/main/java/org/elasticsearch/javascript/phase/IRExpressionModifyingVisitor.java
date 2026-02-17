/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.javascript.phase;

import org.elasticsearch.javascript.ir.BinaryImplNode;
import org.elasticsearch.javascript.ir.BinaryMathNode;
import org.elasticsearch.javascript.ir.BooleanNode;
import org.elasticsearch.javascript.ir.CastNode;
import org.elasticsearch.javascript.ir.ComparisonNode;
import org.elasticsearch.javascript.ir.ConditionalNode;
import org.elasticsearch.javascript.ir.DeclarationNode;
import org.elasticsearch.javascript.ir.DoWhileLoopNode;
import org.elasticsearch.javascript.ir.DupNode;
import org.elasticsearch.javascript.ir.ElvisNode;
import org.elasticsearch.javascript.ir.ExpressionNode;
import org.elasticsearch.javascript.ir.FlipArrayIndexNode;
import org.elasticsearch.javascript.ir.FlipCollectionIndexNode;
import org.elasticsearch.javascript.ir.FlipDefIndexNode;
import org.elasticsearch.javascript.ir.ForEachSubArrayNode;
import org.elasticsearch.javascript.ir.ForEachSubIterableNode;
import org.elasticsearch.javascript.ir.ForLoopNode;
import org.elasticsearch.javascript.ir.IfElseNode;
import org.elasticsearch.javascript.ir.IfNode;
import org.elasticsearch.javascript.ir.InstanceofNode;
import org.elasticsearch.javascript.ir.InvokeCallDefNode;
import org.elasticsearch.javascript.ir.InvokeCallMemberNode;
import org.elasticsearch.javascript.ir.InvokeCallNode;
import org.elasticsearch.javascript.ir.ListInitializationNode;
import org.elasticsearch.javascript.ir.MapInitializationNode;
import org.elasticsearch.javascript.ir.NewArrayNode;
import org.elasticsearch.javascript.ir.NewObjectNode;
import org.elasticsearch.javascript.ir.NullSafeSubNode;
import org.elasticsearch.javascript.ir.ReturnNode;
import org.elasticsearch.javascript.ir.StatementExpressionNode;
import org.elasticsearch.javascript.ir.StoreBraceDefNode;
import org.elasticsearch.javascript.ir.StoreBraceNode;
import org.elasticsearch.javascript.ir.StoreDotDefNode;
import org.elasticsearch.javascript.ir.StoreDotNode;
import org.elasticsearch.javascript.ir.StoreDotShortcutNode;
import org.elasticsearch.javascript.ir.StoreFieldMemberNode;
import org.elasticsearch.javascript.ir.StoreListShortcutNode;
import org.elasticsearch.javascript.ir.StoreMapShortcutNode;
import org.elasticsearch.javascript.ir.StoreVariableNode;
import org.elasticsearch.javascript.ir.StringConcatenationNode;
import org.elasticsearch.javascript.ir.ThrowNode;
import org.elasticsearch.javascript.ir.UnaryMathNode;
import org.elasticsearch.javascript.ir.WhileLoopNode;

import java.util.List;
import java.util.function.Consumer;

public class IRExpressionModifyingVisitor extends IRTreeBaseVisitor<Consumer<ExpressionNode>> {

    private void visitList(List<ExpressionNode> nodes) {
        for (int i = 0; i < nodes.size(); i++) {
            int ii = i;
            nodes.get(i).visit(this, e -> nodes.set(ii, e));
        }
    }

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
            irForLoopNode.getInitializerNode().visit(this, irForLoopNode::setInitializerNode);
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
    }

    @Override
    public void visitBinaryMath(BinaryMathNode irBinaryMathNode, Consumer<ExpressionNode> scope) {
        irBinaryMathNode.getLeftNode().visit(this, irBinaryMathNode::setLeftNode);
        irBinaryMathNode.getRightNode().visit(this, irBinaryMathNode::setRightNode);
    }

    @Override
    public void visitStringConcatenation(StringConcatenationNode irStringConcatenationNode, Consumer<ExpressionNode> scope) {
        visitList(irStringConcatenationNode.getArgumentNodes());
    }

    @Override
    public void visitBoolean(BooleanNode irBooleanNode, Consumer<ExpressionNode> scope) {
        irBooleanNode.getLeftNode().visit(this, irBooleanNode::setLeftNode);
        irBooleanNode.getRightNode().visit(this, irBooleanNode::setRightNode);
    }

    @Override
    public void visitComparison(ComparisonNode irComparisonNode, Consumer<ExpressionNode> scope) {
        irComparisonNode.getLeftNode().visit(this, irComparisonNode::setLeftNode);
        irComparisonNode.getRightNode().visit(this, irComparisonNode::setRightNode);
    }

    @Override
    public void visitCast(CastNode irCastNode, Consumer<ExpressionNode> scope) {
        irCastNode.getChildNode().visit(this, irCastNode::setChildNode);
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
        visitList(irListInitializationNode.getArgumentNodes());
    }

    @Override
    public void visitMapInitialization(MapInitializationNode irMapInitializationNode, Consumer<ExpressionNode> scope) {
        visitList(irMapInitializationNode.getKeyNodes());
        visitList(irMapInitializationNode.getValueNodes());
    }

    @Override
    public void visitNewArray(NewArrayNode irNewArrayNode, Consumer<ExpressionNode> scope) {
        visitList(irNewArrayNode.getArgumentNodes());
    }

    @Override
    public void visitNewObject(NewObjectNode irNewObjectNode, Consumer<ExpressionNode> scope) {
        visitList(irNewObjectNode.getArgumentNodes());
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
        visitList(irInvokeCallDefNode.getArgumentNodes());
    }

    @Override
    public void visitInvokeCall(InvokeCallNode irInvokeCallNode, Consumer<ExpressionNode> scope) {
        visitList(irInvokeCallNode.getArgumentNodes());
    }

    @Override
    public void visitInvokeCallMember(InvokeCallMemberNode irInvokeCallMemberNode, Consumer<ExpressionNode> scope) {
        visitList(irInvokeCallMemberNode.getArgumentNodes());
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
