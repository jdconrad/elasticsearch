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

public class UserTreeBaseVisitor<Scope> implements UserTreeVisitor<Scope> {

    @Override
    public void visitClass(SClass userClassNode, Scope scope) {
        userClassNode.visitChildren(this, scope);
    }

    @Override
    public void visitFunction(SFunction userFunctionNode, Scope scope) {
        userFunctionNode.visitChildren(this, scope);
    }

    @Override
    public void visitBlock(SBlock userBlockNode, Scope scope) {
        userBlockNode.visitChildren(this, scope);
    }

    @Override
    public void visitIf(SIf userIfNode, Scope scope) {
        userIfNode.visitChildren(this, scope);
    }

    @Override
    public void visitIfElse(SIfElse userIfElseNode, Scope scope) {
        userIfElseNode.visitChildren(this, scope);
    }

    @Override
    public void visitWhile(SWhile userWhileNode, Scope scope) {
        userWhileNode.visitChildren(this, scope);
    }

    @Override
    public void visitDo(SDo userDoNode, Scope scope) {
        userDoNode.visitChildren(this, scope);
    }

    @Override
    public void visitFor(SFor userForNode, Scope scope) {
        userForNode.visitChildren(this, scope);
    }

    @Override
    public void visitEach(SEach userEachNode, Scope scope) {
        userEachNode.visitChildren(this, scope);
    }

    @Override
    public void visitDeclBlock(SDeclBlock userDeclBlockNode, Scope scope) {
        userDeclBlockNode.visitChildren(this, scope);
    }

    @Override
    public void visitDeclaration(SDeclaration userDeclarationNode, Scope scope) {
        userDeclarationNode.visitChildren(this, scope);
    }

    @Override
    public void visitReturn(SReturn userReturnNode, Scope scope) {
        userReturnNode.visitChildren(this, scope);
    }

    @Override
    public void visitExpression(SExpression userExpressionNode, Scope scope) {
        userExpressionNode.visitChildren(this, scope);
    }

    @Override
    public void visitTry(STry userTryNode, Scope scope) {
        userTryNode.visitChildren(this, scope);
    }

    @Override
    public void visitCatch(SCatch userCatchNode, Scope scope) {
        userCatchNode.visitChildren(this, scope);
    }

    @Override
    public void visitThrow(SThrow userThrowNode, Scope scope) {
        userThrowNode.visitChildren(this, scope);
    }

    @Override
    public void visitContinue(SContinue userContinueNode, Scope scope) {
        userContinueNode.visitChildren(this, scope);
    }

    @Override
    public void visitBreak(SBreak userBreakNode, Scope scope) {
        userBreakNode.visitChildren(this, scope);
    }

    @Override
    public void visitAssignment(EAssignment userAssignmentNode, Scope scope) {
        userAssignmentNode.visitChildren(this, scope);
    }

    @Override
    public void visitUnary(EUnary userUnaryNode, Scope scope) {
        userUnaryNode.visitChildren(this, scope);
    }

    @Override
    public void visitBinary(EBinary userBinaryNode, Scope scope) {
        userBinaryNode.visitChildren(this, scope);
    }

    @Override
    public void visitBooleanComp(EBooleanComp userBooleanCompNode, Scope scope) {
        userBooleanCompNode.visitChildren(this, scope);
    }

    @Override
    public void visitComp(EComp userCompNode, Scope scope) {
        userCompNode.visitChildren(this, scope);
    }

    @Override
    public void visitExplicit(EExplicit userExplicitNode, Scope scope) {
        userExplicitNode.visitChildren(this, scope);
    }

    @Override
    public void visitInstanceof(EInstanceof userInstanceofNode, Scope scope) {
        userInstanceofNode.visitChildren(this, scope);
    }

    @Override
    public void visitConditional(EConditional userConditionalNode, Scope scope) {
        userConditionalNode.visitChildren(this, scope);
    }

    @Override
    public void visitElvis(EElvis userElvisNode, Scope scope) {
        userElvisNode.visitChildren(this, scope);
    }

    @Override
    public void visitListInit(EListInit userListInitNode, Scope scope) {
        userListInitNode.visitChildren(this, scope);
    }

    @Override
    public void visitMapInit(EMapInit userMapInitNode, Scope scope) {
        userMapInitNode.visitChildren(this, scope);
    }

    @Override
    public void visitNewArray(ENewArray userNewArrayNode, Scope scope) {
        userNewArrayNode.visitChildren(this, scope);
    }

    @Override
    public void visitNewObj(ENewObj userNewObjNode, Scope scope) {
        userNewObjNode.visitChildren(this, scope);
    }

    @Override
    public void visitCallLocal(ECallLocal userCallLocalNode, Scope scope) {
        userCallLocalNode.visitChildren(this, scope);
    }

    @Override
    public void visitBooleanConstant(EBooleanConstant userBooleanConstantNode, Scope scope) {
        userBooleanConstantNode.visitChildren(this, scope);
    }

    @Override
    public void visitNumeric(ENumeric userNumericNode, Scope scope) {
        userNumericNode.visitChildren(this, scope);
    }

    @Override
    public void visitDecimal(EDecimal userDecimalNode, Scope scope) {
        userDecimalNode.visitChildren(this, scope);
    }

    @Override
    public void visitString(EString userStringNode, Scope scope) {
        userStringNode.visitChildren(this, scope);
    }

    @Override
    public void visitNull(ENull userNullNode, Scope scope) {
        userNullNode.visitChildren(this, scope);
    }

    @Override
    public void visitRegex(ERegex userRegexNode, Scope scope) {
        userRegexNode.visitChildren(this, scope);
    }

    @Override
    public void visitLambda(ELambda userLambdaNode, Scope scope) {
        userLambdaNode.visitChildren(this, scope);
    }

    @Override
    public void visitFunctionRef(EFunctionRef userFunctionRefNode, Scope scope) {
        userFunctionRefNode.visitChildren(this, scope);
    }

    @Override
    public void visitNewArrayFunctionRef(ENewArrayFunctionRef userNewArrayFunctionRefNode, Scope scope) {
        userNewArrayFunctionRefNode.visitChildren(this, scope);
    }

    @Override
    public void visitSymbol(ESymbol userSymbolNode, Scope scope) {
        userSymbolNode.visitChildren(this, scope);
    }

    @Override
    public void visitDot(EDot userDotNode, Scope scope) {
        userDotNode.visitChildren(this, scope);
    }

    @Override
    public void visitBrace(EBrace userBraceNode, Scope scope) {
        userBraceNode.visitChildren(this, scope);
    }

    @Override
    public void visitCall(ECall userCallNode, Scope scope) {
        userCallNode.visitChildren(this, scope);
    }
}
