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
import org.elasticsearch.painless.Scope;
import org.elasticsearch.painless.ir.NewArrayNode;
import org.elasticsearch.painless.ir.TypeNode;
import org.elasticsearch.painless.symbol.ScriptRoot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents an array instantiation.
 */
public class ENewArray extends AExpression {

    protected final String type;
    protected final List<AExpression> arguments;
    protected final boolean initialize;

    public ENewArray(Location location, String type, List<AExpression> arguments, boolean initialize) {
        super(location);

        this.type = Objects.requireNonNull(type);
        this.arguments = Collections.unmodifiableList(Objects.requireNonNull(arguments));
        this.initialize = initialize;
    }

    @Override
    protected Output<E> analyze(BuilderVisitor<S, E> builderVisitor, ScriptRoot scriptRoot, Scope scope, Input input) {
        Output output = new Output();

        if (input.read == false) {
             throw createError(new IllegalArgumentException("A newly created array must be read from."));
        }

        Class<?> clazz = scriptRoot.getPainlessLookup().canonicalTypeNameToType(this.type);

        if (clazz == null) {
            throw createError(new IllegalArgumentException("Not a type [" + this.type + "]."));
        }

        List<Output> argumentOutputs = new ArrayList<>();

        for (int argument = 0; argument < arguments.size(); ++argument) {
            AExpression expression = arguments.get(argument);

            Input expressionInput = new Input();
            expressionInput.expected = initialize ? clazz.getComponentType() : int.class;
            expressionInput.internal = true;
            Output expressionOutput = expression.analyze(builderVisitor, scriptRoot, scope, expressionInput);
            expression.cast(expressionInput, expressionOutput);
            argumentOutputs.add(expressionOutput);
        }

        output.actual = clazz;

        NewArrayNode newArrayNode = new NewArrayNode()
                .setTypeNode(new TypeNode()
                        .setLocation(location)
                        .setType(output.actual)
                )
                .setLocation(location)
                .setInitialize(initialize);

        for (int argument = 0; argument < arguments.size(); ++ argument) {
            newArrayNode.addArgumentNode(arguments.get(argument).cast(argumentOutputs.get(argument)));
        }

        output.expressionNode = newArrayNode;

        return output;
    }

    @Override
    public String toString() {
        return singleLineToStringWithOptionalArgs(arguments, type, initialize ? "init" : "dims");
    }
}
