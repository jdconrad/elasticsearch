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
import org.elasticsearch.painless.ir.NewObjectNode;
import org.elasticsearch.painless.ir.TypeNode;
import org.elasticsearch.painless.lookup.PainlessConstructor;
import org.elasticsearch.painless.lookup.PainlessLookupUtility;
import org.elasticsearch.painless.spi.annotation.NonDeterministicAnnotation;
import org.elasticsearch.painless.symbol.ScriptRoot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.elasticsearch.painless.lookup.PainlessLookupUtility.typeToCanonicalTypeName;

/**
 * Represents and object instantiation.
 */
public class ENewObj extends AExpression {

    protected final String type;
    protected final List<AExpression> arguments;

    public ENewObj(Location location, String type, List<AExpression> arguments) {
        super(location);

        this.type = Objects.requireNonNull(type);
        this.arguments = Collections.unmodifiableList(Objects.requireNonNull(arguments));
    }

    @Override
    protected Output<E> analyze(BuilderVisitor<S, E> builderVisitor, ScriptRoot scriptRoot, Scope scope, Input input) {
        Output output = new Output();

        output.actual = scriptRoot.getPainlessLookup().canonicalTypeNameToType(this.type);

        if (output.actual == null) {
            throw createError(new IllegalArgumentException("Not a type [" + this.type + "]."));
        }

        PainlessConstructor constructor = scriptRoot.getPainlessLookup().lookupPainlessConstructor(output.actual, arguments.size());

        if (constructor == null) {
            throw createError(new IllegalArgumentException(
                    "constructor [" + typeToCanonicalTypeName(output.actual) + ", <init>/" + arguments.size() + "] not found"));
        }

        scriptRoot.markNonDeterministic(constructor.annotations.containsKey(NonDeterministicAnnotation.class));

        Class<?>[] types = new Class<?>[constructor.typeParameters.size()];
        constructor.typeParameters.toArray(types);

        if (constructor.typeParameters.size() != arguments.size()) {
            throw createError(new IllegalArgumentException(
                    "When calling constructor on type [" + PainlessLookupUtility.typeToCanonicalTypeName(output.actual) + "] " +
                    "expected [" + constructor.typeParameters.size() + "] arguments, but found [" + arguments.size() + "]."));
        }

        List<Output> argumentOutputs = new ArrayList<>();

        for (int argument = 0; argument < arguments.size(); ++argument) {
            AExpression expression = arguments.get(argument);

            Input expressionInput = new Input();
            expressionInput.expected = types[argument];
            expressionInput.internal = true;
            Output expressionOutput = expression.analyze(builderVisitor, scriptRoot, scope, expressionInput);
            expression.cast(expressionInput, expressionOutput);
            argumentOutputs.add(expressionOutput);
        }

        output.statement = true;

        NewObjectNode newObjectNode = new NewObjectNode()
                .setTypeNode(new TypeNode()
                        .setLocation(location)
                        .setType(output.actual)
                )
                .setLocation(location)
                .setRead(input.read)
                .setConstructor(constructor);

        for (int argument = 0; argument < arguments.size(); ++ argument) {
            newObjectNode.addArgumentNode(arguments.get(argument).cast(argumentOutputs.get(argument)));
        }

        output.expressionNode = newObjectNode;

        return output;
    }

    @Override
    public String toString() {
        return singleLineToStringWithOptionalArgs(arguments, type);
    }
}
