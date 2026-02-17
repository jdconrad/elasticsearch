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
import org.elasticsearch.javascript.ir.DefInterfaceReferenceNode;
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

public interface IRTreeVisitor<Scope> {

    void visitClass(ClassNode irClassNode, Scope scope);

    void visitFunction(FunctionNode irFunctionNode, Scope scope);

    void visitField(FieldNode irFieldNode, Scope scope);

    void visitBlock(BlockNode irBlockNode, Scope scope);

    void visitIf(IfNode irIfNode, Scope scope);

    void visitIfElse(IfElseNode irIfElseNode, Scope scope);

    void visitWhileLoop(WhileLoopNode irWhileLoopNode, Scope scope);

    void visitDoWhileLoop(DoWhileLoopNode irDoWhileLoopNode, Scope scope);

    void visitForLoop(ForLoopNode irForLoopNode, Scope scope);

    void visitForEachLoop(ForEachLoopNode irForEachLoopNode, Scope scope);

    void visitForEachSubArrayLoop(ForEachSubArrayNode irForEachSubArrayNode, Scope scope);

    void visitForEachSubIterableLoop(ForEachSubIterableNode irForEachSubIterableNode, Scope scope);

    void visitDeclarationBlock(DeclarationBlockNode irDeclarationBlockNode, Scope scope);

    void visitDeclaration(DeclarationNode irDeclarationNode, Scope scope);

    void visitReturn(ReturnNode irReturnNode, Scope scope);

    void visitStatementExpression(StatementExpressionNode irStatementExpressionNode, Scope scope);

    void visitTry(TryNode irTryNode, Scope scope);

    void visitCatch(CatchNode irCatchNode, Scope scope);

    void visitThrow(ThrowNode irThrowNode, Scope scope);

    void visitContinue(ContinueNode irContinueNode, Scope scope);

    void visitBreak(BreakNode irBreakNode, Scope scope);

    void visitBinaryImpl(BinaryImplNode irBinaryImplNode, Scope scope);

    void visitUnaryMath(UnaryMathNode irUnaryMathNode, Scope scope);

    void visitBinaryMath(BinaryMathNode irBinaryMathNode, Scope scope);

    void visitStringConcatenation(StringConcatenationNode irStringConcatenationNode, Scope scope);

    void visitBoolean(BooleanNode irBooleanNode, Scope scope);

    void visitComparison(ComparisonNode irComparisonNode, Scope scope);

    void visitCast(CastNode irCastNode, Scope scope);

    void visitInstanceof(InstanceofNode irInstanceofNode, Scope scope);

    void visitConditional(ConditionalNode irConditionalNode, Scope scope);

    void visitElvis(ElvisNode irElvisNode, Scope scope);

    void visitListInitialization(ListInitializationNode irListInitializationNode, Scope scope);

    void visitMapInitialization(MapInitializationNode irMapInitializationNode, Scope scope);

    void visitNewArray(NewArrayNode irNewArrayNode, Scope scope);

    void visitNewObject(NewObjectNode irNewObjectNode, Scope scope);

    void visitConstant(ConstantNode irConstantNode, Scope scope);

    void visitNull(NullNode irNullNode, Scope scope);

    void visitDefInterfaceReference(DefInterfaceReferenceNode irDefInterfaceReferenceNode, Scope scope);

    void visitTypedInterfaceReference(TypedInterfaceReferenceNode irTypedInterfaceReferenceNode, Scope scope);

    void visitTypedCaptureReference(TypedCaptureReferenceNode irTypedCaptureReferenceNode, Scope scope);

    void visitStatic(StaticNode irStaticNode, Scope scope);

    void visitLoadVariable(LoadVariableNode irLoadVariableNode, Scope scope);

    void visitNullSafeSub(NullSafeSubNode irNullSafeSubNode, Scope scope);

    void visitLoadDotArrayLengthNode(LoadDotArrayLengthNode irLoadDotArrayLengthNode, Scope scope);

    void visitLoadDotDef(LoadDotDefNode irLoadDotDefNode, Scope scope);

    void visitLoadDot(LoadDotNode irLoadDotNode, Scope scope);

    void visitLoadDotShortcut(LoadDotShortcutNode irDotSubShortcutNode, Scope scope);

    void visitLoadListShortcut(LoadListShortcutNode irLoadListShortcutNode, Scope scope);

    void visitLoadMapShortcut(LoadMapShortcutNode irLoadMapShortcutNode, Scope scope);

    void visitLoadFieldMember(LoadFieldMemberNode irLoadFieldMemberNode, Scope scope);

    void visitLoadBraceDef(LoadBraceDefNode irLoadBraceDefNode, Scope scope);

    void visitLoadBrace(LoadBraceNode irLoadBraceNode, Scope scope);

    void visitStoreVariable(StoreVariableNode irStoreVariableNode, Scope scope);

    void visitStoreDotDef(StoreDotDefNode irStoreDotDefNode, Scope scope);

    void visitStoreDot(StoreDotNode irStoreDotNode, Scope scope);

    void visitStoreDotShortcut(StoreDotShortcutNode irDotSubShortcutNode, Scope scope);

    void visitStoreListShortcut(StoreListShortcutNode irStoreListShortcutNode, Scope scope);

    void visitStoreMapShortcut(StoreMapShortcutNode irStoreMapShortcutNode, Scope scope);

    void visitStoreFieldMember(StoreFieldMemberNode irStoreFieldMemberNode, Scope scope);

    void visitStoreBraceDef(StoreBraceDefNode irStoreBraceDefNode, Scope scope);

    void visitStoreBrace(StoreBraceNode irStoreBraceNode, Scope scope);

    void visitInvokeCallDef(InvokeCallDefNode irInvokeCallDefNode, Scope scope);

    void visitInvokeCall(InvokeCallNode irInvokeCallNode, Scope scope);

    void visitInvokeCallMember(InvokeCallMemberNode irInvokeCallMemberNode, Scope scope);

    void visitFlipArrayIndex(FlipArrayIndexNode irFlipArrayIndexNode, Scope scope);

    void visitFlipCollectionIndex(FlipCollectionIndexNode irFlipCollectionIndexNode, Scope scope);

    void visitFlipDefIndex(FlipDefIndexNode irFlipDefIndexNode, Scope scope);

    void visitDup(DupNode irDupNode, Scope scope);
}
