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

package org.elasticsearch.painless;

import org.elasticsearch.painless.ir.CastNode;
import org.elasticsearch.painless.ir.ClassNode;
import org.elasticsearch.painless.ir.ConstantNode;
import org.elasticsearch.painless.ir.ExpressionNode;
import org.elasticsearch.painless.ir.StatementNode;
import org.elasticsearch.painless.ir.TypeNode;
import org.elasticsearch.painless.lookup.PainlessCast;
import org.elasticsearch.painless.node.AExpression;
import org.elasticsearch.painless.node.ENumeric;

public class IRBuilderVisitor extends BuilderVisitor<StatementNode, ExpressionNode> {

    protected ClassNode classNode = new ClassNode();

    public IRBuilderVisitor() {
        // do nothing
    }

    @Override
    public void visitNumericBuilder(
            ENumeric numericBuilder,
            AExpression.Input input,
            AExpression.Output<ExpressionNode> output,
            Object constant) {

        output.setData(new ConstantNode()
                .setTypeNode(new TypeNode()
                        .setLocation(numericBuilder.getLocation())
                        .setType(output.getOriginalType())
                )
                .setLocation(numericBuilder.getLocation())
                .setConstant(constant)
        );
    }

    protected void insertCastNode(AExpression.Output<ExpressionNode> output, Location location) {
        PainlessCast painlessCast = output.getPainlessCast();

        if (painlessCast != null) {
            output.setData(new CastNode()
                    .setTypeNode(new TypeNode()
                            .setLocation(location)
                            .setType(painlessCast.targetType)
                    )
                    .setChildNode(output.getData())
                    .setLocation(location)
                    .setCast(painlessCast)
            );
        }
    }
}
