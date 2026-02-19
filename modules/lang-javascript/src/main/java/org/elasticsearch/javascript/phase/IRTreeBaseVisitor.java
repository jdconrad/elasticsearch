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
import org.elasticsearch.javascript.ir.BlockNode;
import org.elasticsearch.javascript.ir.BooleanNode;
import org.elasticsearch.javascript.ir.BreakNode;
import org.elasticsearch.javascript.ir.CastNode;
import org.elasticsearch.javascript.ir.CatchNode;
import org.elasticsearch.javascript.ir.ClassNode;
import org.elasticsearch.javascript.ir.ComparisonNode;
import org.elasticsearch.javascript.ir.ConditionalNode;
import org.elasticsearch.javascript.ir.ConstantNode;
import org.elasticsearch.javascript.ir.ContinueNode;
import org.elasticsearch.javascript.ir.DeclarationBlockNode;
import org.elasticsearch.javascript.ir.DeclarationNode;
import org.elasticsearch.javascript.ir.DoWhileLoopNode;
import org.elasticsearch.javascript.ir.DupNode;
import org.elasticsearch.javascript.ir.ElvisNode;
import org.elasticsearch.javascript.ir.FieldNode;
import org.elasticsearch.javascript.ir.FlipArrayIndexNode;
import org.elasticsearch.javascript.ir.FlipCollectionIndexNode;
import org.elasticsearch.javascript.ir.FlipDefIndexNode;
import org.elasticsearch.javascript.ir.ForEachLoopNode;
import org.elasticsearch.javascript.ir.ForEachSubArrayNode;
import org.elasticsearch.javascript.ir.ForEachSubIterableNode;
import org.elasticsearch.javascript.ir.ForLoopNode;
import org.elasticsearch.javascript.ir.FunctionNode;
import org.elasticsearch.javascript.ir.IfElseNode;
import org.elasticsearch.javascript.ir.IfNode;
import org.elasticsearch.javascript.ir.InstanceofNode;
import org.elasticsearch.javascript.ir.InvokeCallDefNode;
import org.elasticsearch.javascript.ir.InvokeCallMemberNode;
import org.elasticsearch.javascript.ir.InvokeCallNode;
import org.elasticsearch.javascript.ir.ListInitializationNode;
import org.elasticsearch.javascript.ir.LoadBraceDefNode;
import org.elasticsearch.javascript.ir.LoadBraceNode;
import org.elasticsearch.javascript.ir.LoadDotArrayLengthNode;
import org.elasticsearch.javascript.ir.LoadDotDefNode;
import org.elasticsearch.javascript.ir.LoadDotNode;
import org.elasticsearch.javascript.ir.LoadDotShortcutNode;
import org.elasticsearch.javascript.ir.LoadFieldMemberNode;
import org.elasticsearch.javascript.ir.LoadListShortcutNode;
import org.elasticsearch.javascript.ir.LoadMapShortcutNode;
import org.elasticsearch.javascript.ir.LoadVariableNode;
import org.elasticsearch.javascript.ir.MapInitializationNode;
import org.elasticsearch.javascript.ir.NewArrayNode;
import org.elasticsearch.javascript.ir.NewObjectNode;
import org.elasticsearch.javascript.ir.NullNode;
import org.elasticsearch.javascript.ir.NullSafeSubNode;
import org.elasticsearch.javascript.ir.ReturnNode;
import org.elasticsearch.javascript.ir.RuntimeCallableNode;
import org.elasticsearch.javascript.ir.StatementExpressionNode;
import org.elasticsearch.javascript.ir.StaticNode;
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
import org.elasticsearch.javascript.ir.TryNode;
import org.elasticsearch.javascript.ir.TypedCaptureReferenceNode;
import org.elasticsearch.javascript.ir.TypedInterfaceReferenceNode;
import org.elasticsearch.javascript.ir.UnaryMathNode;
import org.elasticsearch.javascript.ir.WhileLoopNode;

public class IRTreeBaseVisitor<Scope> implements IRTreeVisitor<Scope> {

    @Override
    public void visitClass(ClassNode irClassNode, Scope scope) {
        irClassNode.visitChildren(this, scope);
    }

    @Override
    public void visitFunction(FunctionNode irFunctionNode, Scope scope) {
        irFunctionNode.visitChildren(this, scope);
    }

    @Override
    public void visitField(FieldNode irFieldNode, Scope scope) {
        irFieldNode.visitChildren(this, scope);
    }

    @Override
    public void visitBlock(BlockNode irBlockNode, Scope scope) {
        irBlockNode.visitChildren(this, scope);
    }

    @Override
    public void visitIf(IfNode irIfNode, Scope scope) {
        irIfNode.visitChildren(this, scope);
    }

    @Override
    public void visitIfElse(IfElseNode irIfElseNode, Scope scope) {
        irIfElseNode.visitChildren(this, scope);
    }

    @Override
    public void visitWhileLoop(WhileLoopNode irWhileLoopNode, Scope scope) {
        irWhileLoopNode.visitChildren(this, scope);
    }

    @Override
    public void visitDoWhileLoop(DoWhileLoopNode irDoWhileLoopNode, Scope scope) {
        irDoWhileLoopNode.visitChildren(this, scope);
    }

    @Override
    public void visitForLoop(ForLoopNode irForLoopNode, Scope scope) {
        irForLoopNode.visitChildren(this, scope);
    }

    @Override
    public void visitForEachLoop(ForEachLoopNode irForEachLoopNode, Scope scope) {
        irForEachLoopNode.visitChildren(this, scope);
    }

    @Override
    public void visitForEachSubArrayLoop(ForEachSubArrayNode irForEachSubArrayNode, Scope scope) {
        irForEachSubArrayNode.visitChildren(this, scope);
    }

    @Override
    public void visitForEachSubIterableLoop(ForEachSubIterableNode irForEachSubIterableNode, Scope scope) {
        irForEachSubIterableNode.visitChildren(this, scope);
    }

    @Override
    public void visitDeclarationBlock(DeclarationBlockNode irDeclarationBlockNode, Scope scope) {
        irDeclarationBlockNode.visitChildren(this, scope);
    }

    @Override
    public void visitDeclaration(DeclarationNode irDeclarationNode, Scope scope) {
        irDeclarationNode.visitChildren(this, scope);
    }

    @Override
    public void visitReturn(ReturnNode irReturnNode, Scope scope) {
        irReturnNode.visitChildren(this, scope);
    }

    @Override
    public void visitStatementExpression(StatementExpressionNode irStatementExpressionNode, Scope scope) {
        irStatementExpressionNode.visitChildren(this, scope);
    }

    @Override
    public void visitTry(TryNode irTryNode, Scope scope) {
        irTryNode.visitChildren(this, scope);
    }

    @Override
    public void visitCatch(CatchNode irCatchNode, Scope scope) {
        irCatchNode.visitChildren(this, scope);
    }

    @Override
    public void visitThrow(ThrowNode irThrowNode, Scope scope) {
        irThrowNode.visitChildren(this, scope);
    }

    @Override
    public void visitContinue(ContinueNode irContinueNode, Scope scope) {
        irContinueNode.visitChildren(this, scope);
    }

    @Override
    public void visitBreak(BreakNode irBreakNode, Scope scope) {
        irBreakNode.visitChildren(this, scope);
    }

    @Override
    public void visitBinaryImpl(BinaryImplNode irBinaryImplNode, Scope scope) {
        irBinaryImplNode.visitChildren(this, scope);
    }

    @Override
    public void visitUnaryMath(UnaryMathNode irUnaryMathNode, Scope scope) {
        irUnaryMathNode.visitChildren(this, scope);
    }

    @Override
    public void visitBinaryMath(BinaryMathNode irBinaryMathNode, Scope scope) {
        irBinaryMathNode.visitChildren(this, scope);
    }

    @Override
    public void visitStringConcatenation(StringConcatenationNode irStringConcatenationNode, Scope scope) {
        irStringConcatenationNode.visitChildren(this, scope);
    }

    @Override
    public void visitBoolean(BooleanNode irBooleanNode, Scope scope) {
        irBooleanNode.visitChildren(this, scope);
    }

    @Override
    public void visitComparison(ComparisonNode irComparisonNode, Scope scope) {
        irComparisonNode.visitChildren(this, scope);
    }

    @Override
    public void visitCast(CastNode irCastNode, Scope scope) {
        irCastNode.visitChildren(this, scope);
    }

    @Override
    public void visitInstanceof(InstanceofNode irInstanceofNode, Scope scope) {
        irInstanceofNode.visitChildren(this, scope);
    }

    @Override
    public void visitConditional(ConditionalNode irConditionalNode, Scope scope) {
        irConditionalNode.visitChildren(this, scope);
    }

    @Override
    public void visitElvis(ElvisNode irElvisNode, Scope scope) {
        irElvisNode.visitChildren(this, scope);
    }

    @Override
    public void visitListInitialization(ListInitializationNode irListInitializationNode, Scope scope) {
        irListInitializationNode.visitChildren(this, scope);
    }

    @Override
    public void visitMapInitialization(MapInitializationNode irMapInitializationNode, Scope scope) {
        irMapInitializationNode.visitChildren(this, scope);
    }

    @Override
    public void visitNewArray(NewArrayNode irNewArrayNode, Scope scope) {
        irNewArrayNode.visitChildren(this, scope);
    }

    @Override
    public void visitNewObject(NewObjectNode irNewObjectNode, Scope scope) {
        irNewObjectNode.visitChildren(this, scope);
    }

    @Override
    public void visitConstant(ConstantNode irConstantNode, Scope scope) {
        irConstantNode.visitChildren(this, scope);
    }

    @Override
    public void visitNull(NullNode irNullNode, Scope scope) {
        irNullNode.visitChildren(this, scope);
    }

    @Override
    public void visitRuntimeCallable(RuntimeCallableNode irRuntimeCallableNode, Scope scope) {
        irRuntimeCallableNode.visitChildren(this, scope);
    }

    @Override
    public void visitTypedInterfaceReference(TypedInterfaceReferenceNode irTypedInterfaceReferenceNode, Scope scope) {
        irTypedInterfaceReferenceNode.visitChildren(this, scope);
    }

    @Override
    public void visitTypedCaptureReference(TypedCaptureReferenceNode irTypedCaptureReferenceNode, Scope scope) {
        irTypedCaptureReferenceNode.visitChildren(this, scope);
    }

    @Override
    public void visitStatic(StaticNode irStaticNode, Scope scope) {
        irStaticNode.visitChildren(this, scope);
    }

    @Override
    public void visitLoadVariable(LoadVariableNode irLoadVariableNode, Scope scope) {
        irLoadVariableNode.visitChildren(this, scope);
    }

    @Override
    public void visitNullSafeSub(NullSafeSubNode irNullSafeSubNode, Scope scope) {
        irNullSafeSubNode.visitChildren(this, scope);
    }

    @Override
    public void visitLoadDotArrayLengthNode(LoadDotArrayLengthNode irLoadDotArrayLengthNode, Scope scope) {
        irLoadDotArrayLengthNode.visitChildren(this, scope);
    }

    @Override
    public void visitLoadDotDef(LoadDotDefNode irLoadDotDefNode, Scope scope) {
        irLoadDotDefNode.visitChildren(this, scope);
    }

    @Override
    public void visitLoadDot(LoadDotNode irLoadDotNode, Scope scope) {
        irLoadDotNode.visitChildren(this, scope);
    }

    @Override
    public void visitLoadDotShortcut(LoadDotShortcutNode irDotSubShortcutNode, Scope scope) {
        irDotSubShortcutNode.visitChildren(this, scope);
    }

    @Override
    public void visitLoadListShortcut(LoadListShortcutNode irLoadListShortcutNode, Scope scope) {
        irLoadListShortcutNode.visitChildren(this, scope);
    }

    @Override
    public void visitLoadMapShortcut(LoadMapShortcutNode irLoadMapShortcutNode, Scope scope) {
        irLoadMapShortcutNode.visitChildren(this, scope);
    }

    @Override
    public void visitLoadFieldMember(LoadFieldMemberNode irLoadFieldMemberNode, Scope scope) {
        irLoadFieldMemberNode.visitChildren(this, scope);
    }

    @Override
    public void visitLoadBraceDef(LoadBraceDefNode irLoadBraceDefNode, Scope scope) {
        irLoadBraceDefNode.visitChildren(this, scope);
    }

    @Override
    public void visitLoadBrace(LoadBraceNode irLoadBraceNode, Scope scope) {
        irLoadBraceNode.visitChildren(this, scope);
    }

    @Override
    public void visitStoreVariable(StoreVariableNode irStoreVariableNode, Scope scope) {
        irStoreVariableNode.visitChildren(this, scope);
    }

    @Override
    public void visitStoreDotDef(StoreDotDefNode irStoreDotDefNode, Scope scope) {
        irStoreDotDefNode.visitChildren(this, scope);
    }

    @Override
    public void visitStoreDot(StoreDotNode irStoreDotNode, Scope scope) {
        irStoreDotNode.visitChildren(this, scope);
    }

    @Override
    public void visitStoreDotShortcut(StoreDotShortcutNode irDotSubShortcutNode, Scope scope) {
        irDotSubShortcutNode.visitChildren(this, scope);
    }

    @Override
    public void visitStoreListShortcut(StoreListShortcutNode irStoreListShortcutNode, Scope scope) {
        irStoreListShortcutNode.visitChildren(this, scope);
    }

    @Override
    public void visitStoreMapShortcut(StoreMapShortcutNode irStoreMapShortcutNode, Scope scope) {
        irStoreMapShortcutNode.visitChildren(this, scope);
    }

    @Override
    public void visitStoreFieldMember(StoreFieldMemberNode irStoreFieldMemberNode, Scope scope) {
        irStoreFieldMemberNode.visitChildren(this, scope);
    }

    @Override
    public void visitStoreBraceDef(StoreBraceDefNode irStoreBraceDefNode, Scope scope) {
        irStoreBraceDefNode.visitChildren(this, scope);
    }

    @Override
    public void visitStoreBrace(StoreBraceNode irStoreBraceNode, Scope scope) {
        irStoreBraceNode.visitChildren(this, scope);
    }

    @Override
    public void visitInvokeCallDef(InvokeCallDefNode irInvokeCallDefNode, Scope scope) {
        irInvokeCallDefNode.visitChildren(this, scope);
    }

    @Override
    public void visitInvokeCall(InvokeCallNode irInvokeCallNode, Scope scope) {
        irInvokeCallNode.visitChildren(this, scope);
    }

    @Override
    public void visitInvokeCallMember(InvokeCallMemberNode irInvokeCallMemberNode, Scope scope) {
        irInvokeCallMemberNode.visitChildren(this, scope);
    }

    @Override
    public void visitFlipArrayIndex(FlipArrayIndexNode irFlipArrayIndexNode, Scope scope) {
        irFlipArrayIndexNode.visitChildren(this, scope);
    }

    @Override
    public void visitFlipCollectionIndex(FlipCollectionIndexNode irFlipCollectionIndexNode, Scope scope) {
        irFlipCollectionIndexNode.visitChildren(this, scope);
    }

    @Override
    public void visitFlipDefIndex(FlipDefIndexNode irFlipDefIndexNode, Scope scope) {
        irFlipDefIndexNode.visitChildren(this, scope);
    }

    @Override
    public void visitDup(DupNode irDupNode, Scope scope) {
        irDupNode.visitChildren(this, scope);
    }
}
