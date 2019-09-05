/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.painless.builder;

import org.elasticsearch.painless.Location;
import org.elasticsearch.painless.Operation;
import org.elasticsearch.painless.node.ANode;
import org.elasticsearch.painless.node.DParameter;
import org.elasticsearch.painless.node.DParameters;
import org.elasticsearch.painless.node.DTypeClass;
import org.elasticsearch.painless.node.DTypeString;
import org.elasticsearch.painless.node.EAssignment;
import org.elasticsearch.painless.node.EBinary;
import org.elasticsearch.painless.node.EBool;
import org.elasticsearch.painless.node.EBoolean;
import org.elasticsearch.painless.node.ECallLocal;
import org.elasticsearch.painless.node.ECapturingFunctionRef;
import org.elasticsearch.painless.node.EComp;
import org.elasticsearch.painless.node.EConditional;
import org.elasticsearch.painless.node.EDecimal;
import org.elasticsearch.painless.node.EDirectCallInvoke;
import org.elasticsearch.painless.node.EDirectFieldAccess;
import org.elasticsearch.painless.node.EElvis;
import org.elasticsearch.painless.node.EExplicit;
import org.elasticsearch.painless.node.EFunctionRef;
import org.elasticsearch.painless.node.EInstanceof;
import org.elasticsearch.painless.node.ELambda;
import org.elasticsearch.painless.node.EListInit;
import org.elasticsearch.painless.node.EMapInit;
import org.elasticsearch.painless.node.ENewArray;
import org.elasticsearch.painless.node.ENewArrayFunctionRef;
import org.elasticsearch.painless.node.ENewObj;
import org.elasticsearch.painless.node.ENull;
import org.elasticsearch.painless.node.ENumeric;
import org.elasticsearch.painless.node.ERegex;
import org.elasticsearch.painless.node.EStatic;
import org.elasticsearch.painless.node.EString;
import org.elasticsearch.painless.node.EThis;
import org.elasticsearch.painless.node.EUnary;
import org.elasticsearch.painless.node.EUsed;
import org.elasticsearch.painless.node.EVariable;
import org.elasticsearch.painless.node.PBrace;
import org.elasticsearch.painless.node.PCallInvoke;
import org.elasticsearch.painless.node.PField;
import org.elasticsearch.painless.node.SBlock;
import org.elasticsearch.painless.node.SBreak;
import org.elasticsearch.painless.node.SCatch;
import org.elasticsearch.painless.node.SContinue;
import org.elasticsearch.painless.node.SDeclBlock;
import org.elasticsearch.painless.node.SDeclaration;
import org.elasticsearch.painless.node.SDo;
import org.elasticsearch.painless.node.SEach;
import org.elasticsearch.painless.node.SExpression;
import org.elasticsearch.painless.node.SFor;
import org.elasticsearch.painless.node.SFunction;
import org.elasticsearch.painless.node.SIf;
import org.elasticsearch.painless.node.SIfElse;
import org.elasticsearch.painless.node.SReturn;
import org.elasticsearch.painless.node.SSource;
import org.elasticsearch.painless.node.SThrow;
import org.elasticsearch.painless.node.STry;
import org.elasticsearch.painless.node.SWhile;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;
import org.objectweb.asm.util.Printer;

import java.util.HashMap;
import java.util.Map;

public class ASTBuilder {

    protected ANode root = null;
    protected ANode current = null;

    protected final Map<String, ANode> saves = new HashMap<>();

    public ASTBuilder() {

    }

    public ASTBuilder root() {
        current = root;

        return this;
    }

    public ASTBuilder save(String name) {
        if (current == null) {
            throw new IllegalArgumentException("invalid tree structure");
        }

        saves.put(name, current);

        return this;
    }

    public ASTBuilder load(String name) {
        current = saves.get(name);

        if (current == null) {
            throw new IllegalArgumentException("invalid tree structure");
        }

        return this;
    }

    protected ASTBuilder visitChild(ANode child) {
        if (current == null) {
            if (root != null) {
                throw new IllegalArgumentException("invalid tree structure");
            }

            root = child;
        } else {
            child.parent = current;
            current.children.add(child);
        }

        current = child;

        return this;
    }

    public ASTBuilder endVisit() {
        current = current.parent;

        return this;
    }

    public ANode endBuild() {
        if (current != null) {
            throw new IllegalArgumentException("invalid tree structure");
        }

        return root;
    }

    public ASTBuilder visitNode(ANode node) {
        visitChild(node);

        return this;
    }

    public ASTBuilder visitEmpty() {
        current.children.add(null);

        return this;
    }

    public ASTBuilder visitSource(Location location, String scriptName, String sourceText, Class<?> baseClass, Printer debugStream) {
        return visitChild(new SSource(location, scriptName, sourceText, baseClass, debugStream));
    }

    public ASTBuilder visitFunction(Location location, String name, boolean auto, boolean statik, boolean synthetic) {
        return visitChild(new SFunction(location, name, auto, statik, synthetic));
    }

    public ASTBuilder visitBlock(Location location) {
        return visitChild(new SBlock(location));
    }

    public ASTBuilder visitIf(Location location) {
        return visitChild(new SIf(location));
    }

    public ASTBuilder visitIfElse(Location location) {
        return visitChild(new SIfElse(location));
    }

    public ASTBuilder visitFor(Location location) {
        return visitChild(new SFor(location));
    }

    public ASTBuilder visitEach(Location location) {
        return visitChild(new SEach(location));
    }

    public ASTBuilder visitWhile(Location location) {
        return visitChild(new SWhile(location));
    }

    public ASTBuilder visitDo(Location location) {
        return visitChild(new SDo(location));
    }

    public ASTBuilder visitContinue(Location location) {
        return visitChild(new SContinue(location));
    }

    public ASTBuilder visitBreak(Location location) {
        return visitChild(new SBreak(location));
    }

    public ASTBuilder visitDeclBlock(Location location) {
        return visitChild(new SDeclBlock(location));
    }

    public ASTBuilder visitDeclaration(Location location, String name, boolean initialize) {
        return visitChild(new SDeclaration(location, name, initialize));
    }

    public ASTBuilder visitReturn(Location location) {
        return visitChild(new SReturn(location));
    }

    public ASTBuilder visitThrow(Location location) {
        return visitChild(new SThrow(location));
    }

    public ASTBuilder visitTry(Location location) {
        return visitChild(new STry(location));
    }

    public ASTBuilder visitCatch(Location location) {
        return visitChild(new SCatch(location));
    }

    public ASTBuilder visitExpression(Location location) {
        return visitChild(new SExpression(location));
    }

    public ASTBuilder visitExplicit(Location location) {
        return visitChild(new EExplicit(location));
    }

    public ASTBuilder visitInstanceof(Location location) {
        return visitChild(new EInstanceof(location));
    }

    public ASTBuilder visitAssignment(Location location, boolean pre, boolean post, Operation operation) {
        return visitChild(new EAssignment(location, pre, post, operation));
    }

    public ASTBuilder visitBinary(Location location, Operation operation) {
        return visitChild(new EBinary(location, operation));
    }

    public ASTBuilder visitUnary(Location location, Operation operation) {
        return visitChild(new EUnary(location, operation));
    }

    public ASTBuilder visitBool(Location location, Operation operation) {
        return visitChild(new EBool(location, operation));
    }

    public ASTBuilder visitComp(Location location, Operation operation) {
        return visitChild(new EComp(location, operation));
    }

    public ASTBuilder visitConditional(Location location) {
        return visitChild(new EConditional(location));
    }

    public ASTBuilder visitElvis(Location location) {
        return visitChild(new EElvis(location));
    }

    public ASTBuilder visitNewObj(Location location) {
        return visitChild(new ENewObj(location));
    }

    public ASTBuilder visitNewArray(Location location, boolean initialize) {
        return visitChild(new ENewArray(location, initialize));
    }

    public ASTBuilder visitMapInit(Location location) {
        return visitChild(new EMapInit(location));
    }

    public ASTBuilder visitListInit(Location location) {
        return visitChild(new EListInit(location));
    }

    public ASTBuilder visitLambda(Location location) {
        return visitChild(new ELambda(location));
    }

    public ASTBuilder visitFunctionRef(Location location, String type, String call) {
        return visitChild(new EFunctionRef(location, type, call));
    }

    public ASTBuilder visitCapturingFunctionRef(Location location, String variable, String call) {
        return visitChild(new ECapturingFunctionRef(location, variable, call));
    }

    public ASTBuilder visitNewArrayFunctionRef(Location location, String type) {
        return visitChild(new ENewArrayFunctionRef(location, type));
    }

    public ASTBuilder visitCallLocal(Location location, String name) {
        return visitChild(new ECallLocal(location, name));
    }

    public ASTBuilder visitNull(Location location) {
        return visitChild(new ENull(location));
    }

    public ASTBuilder visitBoolean(Location location, boolean value) {
        return visitChild(new EBoolean(location, value));
    }

    public ASTBuilder visitDecimal(Location location, String value) {
        return visitChild(new EDecimal(location, value));
    }

    public ASTBuilder visitNumeric(Location location, String value, int radix) {
        return visitChild(new ENumeric(location, value, radix));
    }

    public ASTBuilder visitString(Location location, String value) {
        return visitChild(new EString(location, value));
    }

    public ASTBuilder visitRegex(Location location, String pattern, String flagsString) {
        return visitChild(new ERegex(location, pattern, flagsString));
    }

    public ASTBuilder visitStatic(Location location) {
        return visitChild(new EStatic(location));
    }

    public ASTBuilder visitVariable(Location location, String name) {
        return visitChild(new EVariable(location, name));
    }

    public ASTBuilder visitBrace(Location location) {
        return visitChild(new PBrace(location));
    }

    public ASTBuilder visitCallInvoke(Location location, String name, boolean nullSafe) {
        return visitChild(new PCallInvoke(location, name, nullSafe));
    }

    public ASTBuilder visitField(Location location, String value, boolean nullSafe) {
        return visitChild(new PField(location, nullSafe, value));
    }

    public ASTBuilder visitParameters(Location location) {
        return visitChild(new DParameters(location));
    }

    public ASTBuilder visitParameter(Location location, String name) {
        return visitChild(new DParameter(location, name));
    }

    public ASTBuilder visitTypeString(Location location, String string) {
        return visitChild(new DTypeString(location, string));
    }

    public ASTBuilder visitTypeClass(Location location, Class<?> type) {
        return visitChild(new DTypeClass(location, type));
    }

    public ASTBuilder visitThis(Location location) {
        return visitChild(new EThis(location));
    }

    public ASTBuilder visitDirectCallInvoke(Location location, Method method, boolean isInterface, boolean isStatic) {
        return visitChild(new EDirectCallInvoke(location, method, isInterface, isStatic));
    }

    public ASTBuilder visitDirectFieldAccess(Location location, Type type, String name, boolean isStatic) {
        return visitChild(new EDirectFieldAccess(location, type, name, isStatic));
    }

    public ASTBuilder visitUsed(Location location, String key, String name) {
        return visitChild(new EUsed(location, key, name));
    }
}
