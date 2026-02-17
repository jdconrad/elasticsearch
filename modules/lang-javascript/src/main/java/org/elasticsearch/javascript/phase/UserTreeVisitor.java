/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.javascript.phase;

import org.elasticsearch.javascript.node.EAssignment;
import org.elasticsearch.javascript.node.EBinary;
import org.elasticsearch.javascript.node.EBooleanComp;
import org.elasticsearch.javascript.node.EBooleanConstant;
import org.elasticsearch.javascript.node.EBrace;
import org.elasticsearch.javascript.node.ECall;
import org.elasticsearch.javascript.node.ECallLocal;
import org.elasticsearch.javascript.node.EComp;
import org.elasticsearch.javascript.node.EConditional;
import org.elasticsearch.javascript.node.EDecimal;
import org.elasticsearch.javascript.node.EDot;
import org.elasticsearch.javascript.node.EElvis;
import org.elasticsearch.javascript.node.EExplicit;
import org.elasticsearch.javascript.node.EFunctionRef;
import org.elasticsearch.javascript.node.EInstanceof;
import org.elasticsearch.javascript.node.ELambda;
import org.elasticsearch.javascript.node.EListInit;
import org.elasticsearch.javascript.node.EMapInit;
import org.elasticsearch.javascript.node.ENewArray;
import org.elasticsearch.javascript.node.ENewArrayFunctionRef;
import org.elasticsearch.javascript.node.ENewObj;
import org.elasticsearch.javascript.node.ENull;
import org.elasticsearch.javascript.node.ENumeric;
import org.elasticsearch.javascript.node.ERegex;
import org.elasticsearch.javascript.node.EString;
import org.elasticsearch.javascript.node.ESymbol;
import org.elasticsearch.javascript.node.EUnary;
import org.elasticsearch.javascript.node.SBlock;
import org.elasticsearch.javascript.node.SBreak;
import org.elasticsearch.javascript.node.SCatch;
import org.elasticsearch.javascript.node.SClass;
import org.elasticsearch.javascript.node.SContinue;
import org.elasticsearch.javascript.node.SDeclBlock;
import org.elasticsearch.javascript.node.SDeclaration;
import org.elasticsearch.javascript.node.SDo;
import org.elasticsearch.javascript.node.SEach;
import org.elasticsearch.javascript.node.SExpression;
import org.elasticsearch.javascript.node.SFor;
import org.elasticsearch.javascript.node.SFunction;
import org.elasticsearch.javascript.node.SIf;
import org.elasticsearch.javascript.node.SIfElse;
import org.elasticsearch.javascript.node.SReturn;
import org.elasticsearch.javascript.node.SThrow;
import org.elasticsearch.javascript.node.STry;
import org.elasticsearch.javascript.node.SWhile;

public interface UserTreeVisitor<Scope> {

    void visitClass(SClass userClassNode, Scope scope);

    void visitFunction(SFunction userFunctionNode, Scope scope);

    void visitBlock(SBlock userBlockNode, Scope scope);

    void visitIf(SIf userIfNode, Scope scope);

    void visitIfElse(SIfElse userIfElseNode, Scope scope);

    void visitWhile(SWhile userWhileNode, Scope scope);

    void visitDo(SDo userDoNode, Scope scope);

    void visitFor(SFor userForNode, Scope scope);

    void visitEach(SEach userEachNode, Scope scope);

    void visitDeclBlock(SDeclBlock userDeclBlockNode, Scope scope);

    void visitDeclaration(SDeclaration userDeclarationNode, Scope scope);

    void visitReturn(SReturn userReturnNode, Scope scope);

    void visitExpression(SExpression userExpressionNode, Scope scope);

    void visitTry(STry userTryNode, Scope scope);

    void visitCatch(SCatch userCatchNode, Scope scope);

    void visitThrow(SThrow userThrowNode, Scope scope);

    void visitContinue(SContinue userContinueNode, Scope scope);

    void visitBreak(SBreak userBreakNode, Scope scope);

    void visitAssignment(EAssignment userAssignmentNode, Scope scope);

    void visitUnary(EUnary userUnaryNode, Scope scope);

    void visitBinary(EBinary userBinaryNode, Scope scope);

    void visitBooleanComp(EBooleanComp userBooleanCompNode, Scope scope);

    void visitComp(EComp userCompNode, Scope scope);

    void visitExplicit(EExplicit userExplicitNode, Scope scope);

    void visitInstanceof(EInstanceof userInstanceofNode, Scope scope);

    void visitConditional(EConditional userConditionalNode, Scope scope);

    void visitElvis(EElvis userElvisNode, Scope scope);

    void visitListInit(EListInit userListInitNode, Scope scope);

    void visitMapInit(EMapInit userMapInitNode, Scope scope);

    void visitNewArray(ENewArray userNewArrayNode, Scope scope);

    void visitNewObj(ENewObj userNewObjectNode, Scope scope);

    void visitCallLocal(ECallLocal userCallLocalNode, Scope scope);

    void visitBooleanConstant(EBooleanConstant userBooleanConstantNode, Scope scope);

    void visitNumeric(ENumeric userNumericNode, Scope scope);

    void visitDecimal(EDecimal userDecimalNode, Scope scope);

    void visitString(EString userStringNode, Scope scope);

    void visitNull(ENull userNullNode, Scope scope);

    void visitRegex(ERegex userRegexNode, Scope scope);

    void visitLambda(ELambda userLambdaNode, Scope scope);

    void visitFunctionRef(EFunctionRef userFunctionRefNode, Scope scope);

    void visitNewArrayFunctionRef(ENewArrayFunctionRef userNewArrayFunctionRefNode, Scope scope);

    void visitSymbol(ESymbol userSymbolNode, Scope scope);

    void visitDot(EDot userDotNode, Scope scope);

    void visitBrace(EBrace userBraceNode, Scope scope);

    void visitCall(ECall userCallNode, Scope scope);
}
