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

public class EConstant extends AExpression {
    public EConstant(final String location, final Object constant) {
        super(location);

        this.constant = constant;
    }

    @Override
    protected void analyze(final CompilerSettings settings, final Definition definition, final Variables variables) {
        if (constant instanceof Double) {
            actual = definition.doubleType;
        } else if (constant instanceof Float) {
            actual = definition.floatType;
        } else if (constant instanceof Long) {
            actual = definition.longType;
        } else if (constant instanceof Integer) {
            actual = definition.intType;
        } else if (constant instanceof Character) {
            actual = definition.charType;
        } else if (constant instanceof Short) {
            actual = definition.shortType;
        } else if (constant instanceof Byte) {
            actual = definition.byteType;
        } else if (constant instanceof Boolean) {
            actual = definition.booleanType;
        } else {
            throw new IllegalStateException(error("Illegal tree structure."));
        }
    }

    @Override
    protected void write(final GeneratorAdapter adapter) {

    }
}
