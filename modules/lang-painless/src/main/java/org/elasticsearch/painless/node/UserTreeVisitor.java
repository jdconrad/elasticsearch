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

package org.elasticsearch.painless.node;

public interface UserTreeVisitor<I, O> {

    O visitClass(SClass classNode, I input);
    O visitFunction(SFunction functionNode, I input);

    O visitReturn(SReturn returnNode, I input);
    O visitDeclBlock(SDeclBlock declBlockNode, I input);
    O visitDeclaration(SDeclaration declarationNode, I input);
    O visitIf(SIf ifNode, I input);
    O visitIfElse(SIfElse ifElseNode, I input);
    O visitFor(SFor forNode, I input);
    O visitForEach(SEach forEachNode, I Input);
    O visitWhile(SWhile whileNode, I Input);
    O visitDoWhile(SDo doWhileNode, I input);
    O visitContinue(SContinue continueNode, I input);
    O visitBreak(SBreak breakNode, I input);
    O visitThrow(SThrow throwNode, I input);
    O visitTry(STry tryNode, I input);
    O visitCatch(SCatch catchNode, I input);
    O visitStatementExpression(SExpression statementExpressionNode, I input);
    O visitBlock(SBlock blockNode, I input);

    O visitPrecedence(EPrecedence precedenceNode, I input);
    O visitAssignment(EAssignment assignmentNode, I input);
    O visitUnaryMathNode(EUnary unaryMathNode, I input);
    O visitBinaryMath(EBinary binaryMathNode, I input);
    O visitBinaryComparison(EComp binaryComparisonNode, I input);
    O visitBooleanComparison(EBool booleanComparisonNode, I input);
    O visitConditional(EConditional conditionalNode, I input);
    O visitElvis(EElvis elvisNode, I input);
    O visitExplicitCast(EExplicit explicitCastNode, I input);
    O visitInstanceof(EInstanceof instanceofNode, I input);

    O visitVariableNode(EVariable variableNode, I input);
    O visitStaticCallInvoke(ECallLocal staticCallInvokeNode, I input);
    O visitMemberCallInvoke(ECall memberCallInvokeNode, I input);
    O visitBraceAccess(EBrace braceAccessNode, I input);
    O visitDotAccess(EDot dotAccessNode, I input);

    O visitListInitialization(EListInit listInitializationNode, I input);
    O visitMapInitialization(EMapInit mapInitializationNode, I input);
    O visitNewArray(ENewArray newArrayNode, I input);
    O visitNewObject(ENewObj newObjectNode, I input);

    O visitConstant(EConstant constantNode, I input);
    O visitBooleanConstant(EBoolean booleanConstantNode, I input);
    O visitNumericConstant(ENumeric numericConstantNode, I input);
    O visitDecimalConstant(EDecimal decimalConstantNode, I input);
    O visitStringConstant(EString stringConstant, I input);
    O visitNullConstant(ENull nullConstantNode, I input);
    O visitRegularExpression(ERegex regularExpressionNode, I input);

    O visitReference(EFunctionRef referenceNode, I input);
    O visitNewArrayReference(ENewArrayFunctionRef newArrayReferenceNode, I input);
    O visitLambda(ELambda lambdaNode, I input);
}