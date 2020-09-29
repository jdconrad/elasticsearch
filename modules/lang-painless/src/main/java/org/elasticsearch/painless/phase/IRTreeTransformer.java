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

import org.elasticsearch.painless.ir.BinaryImplNode;
import org.elasticsearch.painless.ir.BinaryMathNode;
import org.elasticsearch.painless.ir.BlockNode;
import org.elasticsearch.painless.ir.BooleanNode;
import org.elasticsearch.painless.ir.BreakNode;
import org.elasticsearch.painless.ir.CastNode;
import org.elasticsearch.painless.ir.CatchNode;
import org.elasticsearch.painless.ir.ClassNode;
import org.elasticsearch.painless.ir.ComparisonNode;
import org.elasticsearch.painless.ir.ConditionalNode;
import org.elasticsearch.painless.ir.ConstantNode;
import org.elasticsearch.painless.ir.ContinueNode;
import org.elasticsearch.painless.ir.DeclarationBlockNode;
import org.elasticsearch.painless.ir.DeclarationNode;
import org.elasticsearch.painless.ir.DefInterfaceReferenceNode;
import org.elasticsearch.painless.ir.DoWhileLoopNode;
import org.elasticsearch.painless.ir.DupNode;
import org.elasticsearch.painless.ir.ElvisNode;
import org.elasticsearch.painless.ir.FieldNode;
import org.elasticsearch.painless.ir.FlipArrayIndexNode;
import org.elasticsearch.painless.ir.FlipCollectionIndexNode;
import org.elasticsearch.painless.ir.FlipDefIndexNode;
import org.elasticsearch.painless.ir.ForEachLoopNode;
import org.elasticsearch.painless.ir.ForEachSubArrayNode;
import org.elasticsearch.painless.ir.ForEachSubIterableNode;
import org.elasticsearch.painless.ir.ForLoopNode;
import org.elasticsearch.painless.ir.FunctionNode;
import org.elasticsearch.painless.ir.IRNode;
import org.elasticsearch.painless.ir.IfElseNode;
import org.elasticsearch.painless.ir.IfNode;
import org.elasticsearch.painless.ir.InstanceofNode;
import org.elasticsearch.painless.ir.InvokeCallDefNode;
import org.elasticsearch.painless.ir.InvokeCallMemberNode;
import org.elasticsearch.painless.ir.InvokeCallNode;
import org.elasticsearch.painless.ir.ListInitializationNode;
import org.elasticsearch.painless.ir.LoadBraceDefNode;
import org.elasticsearch.painless.ir.LoadBraceNode;
import org.elasticsearch.painless.ir.LoadDotArrayLengthNode;
import org.elasticsearch.painless.ir.LoadDotDefNode;
import org.elasticsearch.painless.ir.LoadDotNode;
import org.elasticsearch.painless.ir.LoadDotShortcutNode;
import org.elasticsearch.painless.ir.LoadFieldMemberNode;
import org.elasticsearch.painless.ir.LoadListShortcutNode;
import org.elasticsearch.painless.ir.LoadMapShortcutNode;
import org.elasticsearch.painless.ir.LoadVariableNode;
import org.elasticsearch.painless.ir.MapInitializationNode;
import org.elasticsearch.painless.ir.NewArrayNode;
import org.elasticsearch.painless.ir.NewObjectNode;
import org.elasticsearch.painless.ir.NullNode;
import org.elasticsearch.painless.ir.NullSafeSubNode;
import org.elasticsearch.painless.ir.ReturnNode;
import org.elasticsearch.painless.ir.StatementExpressionNode;
import org.elasticsearch.painless.ir.StaticNode;
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
import org.elasticsearch.painless.ir.TryNode;
import org.elasticsearch.painless.ir.TypedCaptureReferenceNode;
import org.elasticsearch.painless.ir.TypedInterfaceReferenceNode;
import org.elasticsearch.painless.ir.UnaryMathNode;
import org.elasticsearch.painless.ir.WhileLoopNode;

public interface IRTreeTransformer<Scope> {

    IRNode transformClass(ClassNode irClassNode, Scope scope);
    IRNode transformFunction(FunctionNode irFunctionNode, Scope scope);
    IRNode transformField(FieldNode irFieldNode, Scope scope);

    IRNode transformBlock(BlockNode irBlockNode, Scope scope);
    IRNode transformIf(IfNode irIfNode, Scope scope);
    IRNode transformIfElse(IfElseNode irIfElseNode, Scope scope);
    IRNode transformWhileLoop(WhileLoopNode irWhileLoopNode, Scope scope);
    IRNode transformDoWhileLoop(DoWhileLoopNode irDoWhileLoopNode, Scope scope);
    IRNode transformForLoop(ForLoopNode irForLoopNode, Scope scope);
    IRNode transformForEachLoop(ForEachLoopNode irForEachLoopNode, Scope scope);
    IRNode transformForEachSubArrayLoop(ForEachSubArrayNode irForEachSubArrayNode, Scope scope);
    IRNode transformForEachSubIterableLoop(ForEachSubIterableNode irForEachSubIterableNode, Scope scope);
    IRNode transformDeclarationBlock(DeclarationBlockNode irDeclarationBlockNode, Scope scope);
    IRNode transformDeclaration(DeclarationNode irDeclarationNode, Scope scope);
    IRNode transformReturn(ReturnNode irReturnNode, Scope scope);
    IRNode transformStatementExpression(StatementExpressionNode irStatementExpressionNode, Scope scope);
    IRNode transformTry(TryNode irTryNode, Scope scope);
    IRNode transformCatch(CatchNode irCatchNode, Scope scope);
    IRNode transformThrow(ThrowNode irThrowNode, Scope scope);
    IRNode transformContinue(ContinueNode irContinueNode, Scope scope);
    IRNode transformBreak(BreakNode irBreakNode, Scope scope);

    IRNode transformBinaryImpl(BinaryImplNode irBinaryImplNode, Scope scope);
    IRNode transformUnaryMath(UnaryMathNode irUnaryMathNode, Scope scope);
    IRNode transformBinaryMath(BinaryMathNode irBinaryMathNode, Scope scope);
    IRNode transformStringConcatenation(StringConcatenationNode irStringConcatenationNode, Scope scope);
    IRNode transformBoolean(BooleanNode irBooleanNode, Scope scope);
    IRNode transformComparison(ComparisonNode irComparisonNode, Scope scope);
    IRNode transformCast(CastNode irCastNode, Scope scope);
    IRNode transformInstanceof(InstanceofNode irInstanceofNode, Scope scope);
    IRNode transformConditional(ConditionalNode irConditionalNode, Scope scope);
    IRNode transformElvis(ElvisNode irElvisNode, Scope scope);
    IRNode transformListInitialization(ListInitializationNode irListInitializationNode, Scope scope);
    IRNode transformMapInitialization(MapInitializationNode irMapInitializationNode, Scope scope);
    IRNode transformNewArray(NewArrayNode irNewArrayNode, Scope scope);
    IRNode transformNewObject(NewObjectNode irNewObjectNode, Scope scope);
    IRNode transformConstant(ConstantNode irConstantNode, Scope scope);
    IRNode transformNull(NullNode irNullNode, Scope scope);
    IRNode transformDefInterfaceReference(DefInterfaceReferenceNode irDefInterfaceReferenceNode, Scope scope);
    IRNode transformTypedInterfaceReference(TypedInterfaceReferenceNode irTypedInterfaceReferenceNode, Scope scope);
    IRNode transformTypedCaptureReference(TypedCaptureReferenceNode irTypedCaptureReferenceNode, Scope scope);
    IRNode transformStatic(StaticNode irStaticNode, Scope scope);
    IRNode transformLoadVariable(LoadVariableNode irLoadVariableNode, Scope scope);
    IRNode transformNullSafeSub(NullSafeSubNode irNullSafeSubNode, Scope scope);
    IRNode transformLoadDotArrayLengthNode(LoadDotArrayLengthNode irLoadDotArrayLengthNode, Scope scope);
    IRNode transformLoadDotDef(LoadDotDefNode irLoadDotDefNode, Scope scope);
    IRNode transformLoadDot(LoadDotNode irLoadDotNode, Scope scope);
    IRNode transformLoadDotShortcut(LoadDotShortcutNode irDotSubShortcutNode, Scope scope);
    IRNode transformLoadListShortcut(LoadListShortcutNode irLoadListShortcutNode, Scope scope);
    IRNode transformLoadMapShortcut(LoadMapShortcutNode irLoadMapShortcutNode, Scope scope);
    IRNode transformLoadFieldMember(LoadFieldMemberNode irLoadFieldMemberNode, Scope scope);
    IRNode transformLoadBraceDef(LoadBraceDefNode irLoadBraceDefNode, Scope scope);
    IRNode transformLoadBrace(LoadBraceNode irLoadBraceNode, Scope scope);
    IRNode transformStoreVariable(StoreVariableNode irStoreVariableNode, Scope scope);
    IRNode transformStoreDotDef(StoreDotDefNode irStoreDotDefNode, Scope scope);
    IRNode transformStoreDot(StoreDotNode irStoreDotNode, Scope scope);
    IRNode transformStoreDotShortcut(StoreDotShortcutNode irDotSubShortcutNode, Scope scope);
    IRNode transformStoreListShortcut(StoreListShortcutNode irStoreListShortcutNode, Scope scope);
    IRNode transformStoreMapShortcut(StoreMapShortcutNode irStoreMapShortcutNode, Scope scope);
    IRNode transformStoreFieldMember(StoreFieldMemberNode irStoreFieldMemberNode, Scope scope);
    IRNode transformStoreBraceDef(StoreBraceDefNode irStoreBraceDefNode, Scope scope);
    IRNode transformStoreBrace(StoreBraceNode irStoreBraceNode, Scope scope);
    IRNode transformInvokeCallDef(InvokeCallDefNode irInvokeCallDefNode, Scope scope);
    IRNode transformInvokeCall(InvokeCallNode irInvokeCallNode, Scope scope);
    IRNode transformInvokeCallMember(InvokeCallMemberNode irInvokeCallMemberNode, Scope scope);
    IRNode transformFlipArrayIndex(FlipArrayIndexNode irFlipArrayIndexNode, Scope scope);
    IRNode transformFlipCollectionIndex(FlipCollectionIndexNode irFlipCollectionIndexNode, Scope scope);
    IRNode transformFlipDefIndex(FlipDefIndexNode irFlipDefIndexNode, Scope scope);
    IRNode transformDup(DupNode irDupNode, Scope scope);
}
