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

package org.elasticsearch.painless.node;

import org.elasticsearch.painless.Location;
import org.elasticsearch.painless.ir.ClassNode;
import org.elasticsearch.painless.ir.FunctionNode;
import org.elasticsearch.painless.ir.IRNode;
import org.elasticsearch.painless.phase.DefaultIRTreeBuilderPhase;
import org.elasticsearch.painless.phase.DefaultSemanticAnalysisPhase;
import org.elasticsearch.painless.phase.DefaultSemanticHeaderPhase;
import org.elasticsearch.painless.phase.UserTreeVisitor;
import org.elasticsearch.painless.symbol.ScriptScope;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * The root of all Painless trees.  Contains a series of statements.
 */
public class SClass extends ANode {

    private final List<SFunction> functionNodes;

    public SClass(int identifier, Location location, List<SFunction> functionNodes) {
        super(identifier, location);

        this.functionNodes = Collections.unmodifiableList(Objects.requireNonNull(functionNodes));
    }

    public List<SFunction> getFunctionNodes() {
        return functionNodes;
    }

    @Override
    public <Input, Output> Output visit(UserTreeVisitor<Input, Output> userTreeVisitor, Input input) {
        return userTreeVisitor.visitClass(this, input);
    }

    public static void visitDefaultSemanticHeader(DefaultSemanticHeaderPhase visitor, SClass userClassNode, ScriptScope scriptScope) {
        for (SFunction userFunctionNode : userClassNode.getFunctionNodes()) {
            visitor.visit(userFunctionNode, scriptScope);
        }
    }

    public static void visitDefaultSemanticAnalysis(DefaultSemanticAnalysisPhase visitor, SClass userClassNode, ScriptScope scriptScope) {
        for (SFunction userFunctionNode : userClassNode.getFunctionNodes()) {
            visitor.visitFunction(userFunctionNode, scriptScope);
        }
    }

    public static IRNode visitDefaultIRTreeBuild(DefaultIRTreeBuilderPhase visitor, SClass userClassNode, ScriptScope scriptScope) {
        ClassNode irClassNode = new ClassNode();
        scriptScope.setIRClassNode(irClassNode);
        visitor.injectBootstrapMethod(scriptScope);

        for (SFunction userFunctionNode : userClassNode.getFunctionNodes()) {
            irClassNode.addFunctionNode((FunctionNode)visitor.visit(userFunctionNode, scriptScope));
        }

        irClassNode.setLocation(irClassNode.getLocation());
        irClassNode.setScriptScope(scriptScope);

        return irClassNode;
    }
}
