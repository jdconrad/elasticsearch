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

import org.elasticsearch.painless.lookup.PainlessCast;
import org.elasticsearch.painless.node.AExpression;
import org.elasticsearch.painless.node.AStoreable;
import org.elasticsearch.painless.node.EAssignment;
import org.elasticsearch.painless.node.ENumeric;

public abstract class BuilderVisitor<S, E> {

    public void visitAssignmentBuilder(
            EAssignment assignmentBuilder,
            AExpression.Input input,
            AExpression.Output<E> output,
            AExpression leftBuilder,
            AStoreable.Input leftInput,
            AExpression.Output<E> leftOutput,
            AExpression rightBuilder,
            AExpression.Input rightInput,
            AExpression.Output<E> rightOutput,
            Class<?> promotionType,
            Class<?> shiftType,
            PainlessCast castThere,
            PainlessCast castBack,
            boolean doConcatenation) {

        // do nothing
    }

    public void visitNumericBuilder(
            ENumeric numericBuilder,
            AExpression.Input input,
            AExpression.Output<E> output,
            Object constant) {

        // do nothing
    }


}
