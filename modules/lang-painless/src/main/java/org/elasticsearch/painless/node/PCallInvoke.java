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
import org.elasticsearch.painless.ir.CallNode;
import org.elasticsearch.painless.ir.TypeNode;
import org.elasticsearch.painless.lookup.PainlessMethod;
import org.elasticsearch.painless.lookup.def;
import org.elasticsearch.painless.spi.annotation.NonDeterministicAnnotation;
import org.elasticsearch.painless.symbol.ScriptRoot;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.elasticsearch.painless.lookup.PainlessLookupUtility.typeToCanonicalTypeName;

/**
 * Represents a method call and defers to a child subnode.
 */
public class PCallInvoke extends AExpression {

    protected final String name;
    protected final boolean nullSafe;
    protected final List<AExpression> arguments;

    public PCallInvoke(Location location, AExpression prefix, String name, boolean nullSafe, List<AExpression> arguments) {
        super(location, prefix);

        this.name = Objects.requireNonNull(name);
        this.nullSafe = nullSafe;
        this.arguments = Collections.unmodifiableList(Objects.requireNonNull(arguments));
    }

    @Override
    protected Output<E> analyze(BuilderVisitor<S, E> builderVisitor, ScriptRoot scriptRoot, Scope scope, Input input) {
        Output output = new Output();

        Input prefixInput = new Input();
        Output prefixOutput = prefix.analyze(builderVisitor, scriptRoot, scope, prefixInput);
        prefixInput.expected = prefixOutput.actual;
        prefix.cast(prefixInput, prefixOutput);

        AExpression sub;

        if (prefixOutput.actual == def.class) {
            sub = new PSubDefCall(location, name, arguments);
        } else {
            PainlessMethod method = scriptRoot.getPainlessLookup().lookupPainlessMethod(
                    prefixOutput.actual, prefix instanceof EStatic, name, arguments.size());

            if (method == null) {
                throw createError(new IllegalArgumentException(
                        "method [" + typeToCanonicalTypeName(prefixOutput.actual) + ", " + name + "/" + arguments.size() + "] not found"));
            }

            scriptRoot.markNonDeterministic(method.annotations.containsKey(NonDeterministicAnnotation.class));

            sub = new PSubCallInvoke(location, method, prefixOutput.actual, arguments);
        }

        if (nullSafe) {
            sub = new PSubNullSafeCallInvoke(location, sub);
        }

        Input subInput = new Input();
        subInput.expected = input.expected;
        subInput.explicit = input.explicit;
        Output subOutput = sub.analyze(builderVisitor, scriptRoot, scope, subInput);
        output.actual = subOutput.actual;

        output.statement = true;

        output.expressionNode = new CallNode()
                .setTypeNode(new TypeNode()
                        .setLocation(location)
                        .setType(output.actual)
                )
                .setChildNode(subOutput.expressionNode)
                .setPrefixNode(prefix.cast(prefixOutput))
                .setLocation(location);

        return output;
    }

    @Override
    public String toString() {
        return singleLineToStringWithOptionalArgs(arguments, prefix, name);
    }
}
