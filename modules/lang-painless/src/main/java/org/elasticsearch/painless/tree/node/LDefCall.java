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

package org.elasticsearch.painless.tree.node;

import org.elasticsearch.painless.CompilerSettings;
import org.elasticsearch.painless.Definition;
import org.elasticsearch.painless.tree.analyzer.Variables;
import org.objectweb.asm.commons.GeneratorAdapter;

import java.util.Collections;
import java.util.List;

import static org.elasticsearch.painless.tree.writer.Constants.CLASS_TYPE;
import static org.elasticsearch.painless.tree.writer.Constants.DEFINITION_TYPE;
import static org.elasticsearch.painless.tree.writer.Constants.DEF_METHOD_CALL;

public class LDefCall extends ALink {
    protected final String name;
    protected final List<AExpression> arguments;

    public LDefCall(final String location, final String name, final List<AExpression> arguments) {
        super(location);

        this.name = name;
        this.arguments = Collections.unmodifiableList(arguments);
    }

    @Override
    protected ALink analyze(final CompilerSettings settings, final Definition definition, final Variables variables) {
        for (int argument = 0; argument < arguments.size(); ++argument) {
            final AExpression expression = arguments.get(argument);

            expression.expected = definition.objectType;
            expression.analyze(settings, definition, variables);
            arguments.set(argument, expression.cast(definition));
        }

        statement = true;
        after = definition.defType;

        return this;
    }

    @Override
    protected void write(final CompilerSettings settings, final Definition definition, final GeneratorAdapter adapter) {
        adapter.push(name);
        adapter.loadThis();
        adapter.getField(CLASS_TYPE, "definition", DEFINITION_TYPE);

        adapter.push(arguments.size());
        adapter.newArray(definition.defType.type);

        for (int argument = 0; argument < arguments.size(); ++argument) {
            adapter.dup();
            adapter.push(argument);
            arguments.get(argument).write(settings, definition, adapter);
            adapter.arrayStore(definition.defType.type);
        }

        adapter.push(arguments.size());
        adapter.newArray(definition.booleanType.type);

        for (int argument = 0; argument < arguments.size(); ++argument) {
            adapter.dup();
            adapter.push(argument);
            adapter.push(arguments.get(argument).typesafe);
            adapter.arrayStore(definition.booleanType.type);
        }

        adapter.invokeStatic(definition.defobjType.type, DEF_METHOD_CALL);
    }
}
