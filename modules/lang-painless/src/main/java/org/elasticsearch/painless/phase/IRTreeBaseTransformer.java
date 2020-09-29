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

public class IRTreeBaseTransformer<Scope> implements IRTreeTransformer<Scope> {

    @Override
    public IRNode transformClass(ClassNode irClassNode, Scope scope) {
        irClassNode.transformChildren(this, scope);
        return irClassNode;
    }

    @Override
    public IRNode transformFunction(FunctionNode irFunctionNode, Scope scope) {
        irFunctionNode.transformChildren(this, scope);
        return irFunctionNode;
    }

    @Override
    public IRNode transformField(FieldNode irFieldNode, Scope scope) {
        irFieldNode.transformChildren(this, scope);
        return irFieldNode;
    }

    @Override
    public IRNode transformBlock(BlockNode irBlockNode, Scope scope) {
        irBlockNode.transformChildren(this, scope);
        return irBlockNode;
    }

    @Override
    public IRNode transformIf(IfNode irIfNode, Scope scope) {
        irIfNode.transformChildren(this, scope);
        return irIfNode;
    }

    @Override
    public IRNode transformIfElse(IfElseNode irIfElseNode, Scope scope) {
        irIfElseNode.transformChildren(this, scope);
        return irIfElseNode;
    }

    @Override
    public IRNode transformWhileLoop(WhileLoopNode irWhileLoopNode, Scope scope) {
        irWhileLoopNode.transformChildren(this, scope);
        return irWhileLoopNode;
    }

    @Override
    public IRNode transformDoWhileLoop(DoWhileLoopNode irDoWhileLoopNode, Scope scope) {
        irDoWhileLoopNode.transformChildren(this, scope);
        return irDoWhileLoopNode;
    }

    @Override
    public IRNode transformForLoop(ForLoopNode irForLoopNode, Scope scope) {
        irForLoopNode.transformChildren(this, scope);
        return irForLoopNode;
    }

    @Override
    public IRNode transformForEachLoop(ForEachLoopNode irForEachLoopNode, Scope scope) {
        irForEachLoopNode.transformChildren(this, scope);
        return irForEachLoopNode;
    }

    @Override
    public IRNode transformForEachSubArrayLoop(ForEachSubArrayNode irForEachSubArrayNode, Scope scope) {
        irForEachSubArrayNode.transformChildren(this, scope);
        return irForEachSubArrayNode;
    }

    @Override
    public IRNode transformForEachSubIterableLoop(ForEachSubIterableNode irForEachSubIterableNode, Scope scope) {
        irForEachSubIterableNode.transformChildren(this, scope);
        return irForEachSubIterableNode;
    }

    @Override
    public IRNode transformDeclarationBlock(DeclarationBlockNode irDeclarationBlockNode, Scope scope) {
        irDeclarationBlockNode.transformChildren(this, scope);
        return irDeclarationBlockNode;
    }

    @Override
    public IRNode transformDeclaration(DeclarationNode irDeclarationNode, Scope scope) {
        irDeclarationNode.transformChildren(this, scope);
        return irDeclarationNode;
    }

    @Override
    public IRNode transformReturn(ReturnNode irReturnNode, Scope scope) {
        irReturnNode.transformChildren(this, scope);
        return irReturnNode;
    }

    @Override
    public IRNode transformStatementExpression(StatementExpressionNode irStatementExpressionNode, Scope scope) {
        irStatementExpressionNode.transformChildren(this, scope);
        return irStatementExpressionNode;
    }

    @Override
    public IRNode transformTry(TryNode irTryNode, Scope scope) {
        irTryNode.transformChildren(this, scope);
        return irTryNode;
    }

    @Override
    public IRNode transformCatch(CatchNode irCatchNode, Scope scope) {
        irCatchNode.transformChildren(this, scope);
        return irCatchNode;
    }

    @Override
    public IRNode transformThrow(ThrowNode irThrowNode, Scope scope) {
        irThrowNode.transformChildren(this, scope);
        return irThrowNode;
    }

    @Override
    public IRNode transformContinue(ContinueNode irContinueNode, Scope scope) {
        irContinueNode.transformChildren(this, scope);
        return irContinueNode;
    }

    @Override
    public IRNode transformBreak(BreakNode irBreakNode, Scope scope) {
        irBreakNode.transformChildren(this, scope);
        return irBreakNode;
    }

    @Override
    public IRNode transformBinaryImpl(BinaryImplNode irBinaryImplNode, Scope scope) {
        irBinaryImplNode.transformChildren(this, scope);
        return irBinaryImplNode;
    }

    @Override
    public IRNode transformUnaryMath(UnaryMathNode irUnaryMathNode, Scope scope) {
        irUnaryMathNode.transformChildren(this, scope);
        return irUnaryMathNode;
    }

    @Override
    public IRNode transformBinaryMath(BinaryMathNode irBinaryMathNode, Scope scope) {
        irBinaryMathNode.transformChildren(this, scope);
        return irBinaryMathNode;
    }

    @Override
    public IRNode transformStringConcatenation(StringConcatenationNode irStringConcatenationNode, Scope scope) {
        irStringConcatenationNode.transformChildren(this, scope);
        return irStringConcatenationNode;
    }

    @Override
    public IRNode transformBoolean(BooleanNode irBooleanNode, Scope scope) {
        irBooleanNode.transformChildren(this, scope);
        return irBooleanNode;
    }

    @Override
    public IRNode transformComparison(ComparisonNode irComparisonNode, Scope scope) {
        irComparisonNode.transformChildren(this, scope);
        return irComparisonNode;
    }

    @Override
    public IRNode transformCast(CastNode irCastNode, Scope scope) {
        irCastNode.transformChildren(this, scope);
        return irCastNode;
    }

    @Override
    public IRNode transformInstanceof(InstanceofNode irInstanceofNode, Scope scope) {
        irInstanceofNode.transformChildren(this, scope);
        return irInstanceofNode;
    }

    @Override
    public IRNode transformConditional(ConditionalNode irConditionalNode, Scope scope) {
        irConditionalNode.transformChildren(this, scope);
        return irConditionalNode;
    }

    @Override
    public IRNode transformElvis(ElvisNode irElvisNode, Scope scope) {
        irElvisNode.transformChildren(this, scope);
        return irElvisNode;
    }

    @Override
    public IRNode transformListInitialization(ListInitializationNode irListInitializationNode, Scope scope) {
        irListInitializationNode.transformChildren(this, scope);
        return irListInitializationNode;
    }

    @Override
    public IRNode transformMapInitialization(MapInitializationNode irMapInitializationNode, Scope scope) {
        irMapInitializationNode.transformChildren(this, scope);
        return irMapInitializationNode;
    }

    @Override
    public IRNode transformNewArray(NewArrayNode irNewArrayNode, Scope scope) {
        irNewArrayNode.transformChildren(this, scope);
        return irNewArrayNode;
    }

    @Override
    public IRNode transformNewObject(NewObjectNode irNewObjectNode, Scope scope) {
        irNewObjectNode.transformChildren(this, scope);
        return irNewObjectNode;
    }

    @Override
    public IRNode transformConstant(ConstantNode irConstantNode, Scope scope) {
        irConstantNode.transformChildren(this, scope);
        return irConstantNode;
    }

    @Override
    public IRNode transformNull(NullNode irNullNode, Scope scope) {
        irNullNode.transformChildren(this, scope);
        return irNullNode;
    }

    @Override
    public IRNode transformDefInterfaceReference(DefInterfaceReferenceNode irDefInterfaceReferenceNode, Scope scope) {
        irDefInterfaceReferenceNode.transformChildren(this, scope);
        return irDefInterfaceReferenceNode;
    }

    @Override
    public IRNode transformTypedInterfaceReference(TypedInterfaceReferenceNode irTypedInterfaceReferenceNode, Scope scope) {
        irTypedInterfaceReferenceNode.transformChildren(this, scope);
        return irTypedInterfaceReferenceNode;
    }

    @Override
    public IRNode transformTypedCaptureReference(TypedCaptureReferenceNode irTypedCaptureReferenceNode, Scope scope) {
        irTypedCaptureReferenceNode.transformChildren(this, scope);
        return irTypedCaptureReferenceNode;
    }

    @Override
    public IRNode transformStatic(StaticNode irStaticNode, Scope scope) {
        irStaticNode.transformChildren(this, scope);
        return irStaticNode;
    }

    @Override
    public IRNode transformLoadVariable(LoadVariableNode irLoadVariableNode, Scope scope) {
        irLoadVariableNode.transformChildren(this, scope);
        return irLoadVariableNode;
    }

    @Override
    public IRNode transformNullSafeSub(NullSafeSubNode irNullSafeSubNode, Scope scope) {
        irNullSafeSubNode.transformChildren(this, scope);
        return irNullSafeSubNode;
    }

    @Override
    public IRNode transformLoadDotArrayLengthNode(LoadDotArrayLengthNode irLoadDotArrayLengthNode, Scope scope) {
        irLoadDotArrayLengthNode.transformChildren(this, scope);
        return irLoadDotArrayLengthNode;
    }

    @Override
    public IRNode transformLoadDotDef(LoadDotDefNode irLoadDotDefNode, Scope scope) {
        irLoadDotDefNode.transformChildren(this, scope);
        return irLoadDotDefNode;
    }

    @Override
    public IRNode transformLoadDot(LoadDotNode irLoadDotNode, Scope scope) {
        irLoadDotNode.transformChildren(this, scope);
        return irLoadDotNode;
    }

    @Override
    public IRNode transformLoadDotShortcut(LoadDotShortcutNode irDotSubShortcutNode, Scope scope) {
        irDotSubShortcutNode.transformChildren(this, scope);
        return irDotSubShortcutNode;
    }

    @Override
    public IRNode transformLoadListShortcut(LoadListShortcutNode irLoadListShortcutNode, Scope scope) {
        irLoadListShortcutNode.transformChildren(this, scope);
        return irLoadListShortcutNode;
    }

    @Override
    public IRNode transformLoadMapShortcut(LoadMapShortcutNode irLoadMapShortcutNode, Scope scope) {
        irLoadMapShortcutNode.transformChildren(this, scope);
        return irLoadMapShortcutNode;
    }

    @Override
    public IRNode transformLoadFieldMember(LoadFieldMemberNode irLoadFieldMemberNode, Scope scope) {
        irLoadFieldMemberNode.transformChildren(this, scope);
        return irLoadFieldMemberNode;
    }

    @Override
    public IRNode transformLoadBraceDef(LoadBraceDefNode irLoadBraceDefNode, Scope scope) {
        irLoadBraceDefNode.transformChildren(this, scope);
        return irLoadBraceDefNode;
    }

    @Override
    public IRNode transformLoadBrace(LoadBraceNode irLoadBraceNode, Scope scope) {
        irLoadBraceNode.transformChildren(this, scope);
        return irLoadBraceNode;
    }

    @Override
    public IRNode transformStoreVariable(StoreVariableNode irStoreVariableNode, Scope scope) {
        irStoreVariableNode.transformChildren(this, scope);
        return irStoreVariableNode;
    }

    @Override
    public IRNode transformStoreDotDef(StoreDotDefNode irStoreDotDefNode, Scope scope) {
        irStoreDotDefNode.transformChildren(this, scope);
        return irStoreDotDefNode;
    }

    @Override
    public IRNode transformStoreDot(StoreDotNode irStoreDotNode, Scope scope) {
        irStoreDotNode.transformChildren(this, scope);
        return irStoreDotNode;
    }

    @Override
    public IRNode transformStoreDotShortcut(StoreDotShortcutNode irDotSubShortcutNode, Scope scope) {
        irDotSubShortcutNode.transformChildren(this, scope);
        return irDotSubShortcutNode;
    }

    @Override
    public IRNode transformStoreListShortcut(StoreListShortcutNode irStoreListShortcutNode, Scope scope) {
        irStoreListShortcutNode.transformChildren(this, scope);
        return irStoreListShortcutNode;
    }

    @Override
    public IRNode transformStoreMapShortcut(StoreMapShortcutNode irStoreMapShortcutNode, Scope scope) {
        irStoreMapShortcutNode.transformChildren(this, scope);
        return irStoreMapShortcutNode;
    }

    @Override
    public IRNode transformStoreFieldMember(StoreFieldMemberNode irStoreFieldMemberNode, Scope scope) {
        irStoreFieldMemberNode.transformChildren(this, scope);
        return irStoreFieldMemberNode;
    }

    @Override
    public IRNode transformStoreBraceDef(StoreBraceDefNode irStoreBraceDefNode, Scope scope) {
        irStoreBraceDefNode.transformChildren(this, scope);
        return irStoreBraceDefNode;
    }

    @Override
    public IRNode transformStoreBrace(StoreBraceNode irStoreBraceNode, Scope scope) {
        irStoreBraceNode.transformChildren(this, scope);
        return irStoreBraceNode;
    }

    @Override
    public IRNode transformInvokeCallDef(InvokeCallDefNode irInvokeCallDefNode, Scope scope) {
        irInvokeCallDefNode.transformChildren(this, scope);
        return irInvokeCallDefNode;
    }

    @Override
    public IRNode transformInvokeCall(InvokeCallNode irInvokeCallNode, Scope scope) {
        irInvokeCallNode.transformChildren(this, scope);
        return irInvokeCallNode;
    }

    @Override
    public IRNode transformInvokeCallMember(InvokeCallMemberNode irInvokeCallMemberNode, Scope scope) {
        irInvokeCallMemberNode.transformChildren(this, scope);
        return irInvokeCallMemberNode;
    }

    @Override
    public IRNode transformFlipArrayIndex(FlipArrayIndexNode irFlipArrayIndexNode, Scope scope) {
        irFlipArrayIndexNode.transformChildren(this, scope);
        return irFlipArrayIndexNode;
    }

    @Override
    public IRNode transformFlipCollectionIndex(FlipCollectionIndexNode irFlipCollectionIndexNode, Scope scope) {
        irFlipCollectionIndexNode.transformChildren(this, scope);
        return irFlipCollectionIndexNode;
    }

    @Override
    public IRNode transformFlipDefIndex(FlipDefIndexNode irFlipDefIndexNode, Scope scope) {
        irFlipDefIndexNode.transformChildren(this, scope);
        return irFlipDefIndexNode;
    }

    @Override
    public IRNode transformDup(DupNode irDupNode, Scope scope) {
        irDupNode.transformChildren(this, scope);
        return irDupNode;
    }
}
