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

import org.elasticsearch.painless.CompilerSettings;
import org.elasticsearch.painless.Definition;
import org.elasticsearch.painless.Variables;
import org.objectweb.asm.commons.GeneratorAdapter;

public class EDecimal extends AExpression {
    protected final String value;

    public EDecimal(final String location, final String value) {
        super(location);

        this.value = value;
    }

    @Override
    protected void analyze(final CompilerSettings settings, final Definition definition, final Variables variables) {
        if (value.endsWith("f") || value.endsWith("F")) {
            try {
                constant = Float.parseFloat(value.substring(0, value.length() - 1));
                actual = definition.floatType;
            } catch (final NumberFormatException exception) {
                throw new IllegalArgumentException(error("Invalid float constant [" + value + "]."));
            }
        } else {
            try {
                constant = Double.parseDouble(value);
                actual = definition.doubleType;
            } catch (final NumberFormatException exception) {
                throw new IllegalArgumentException(error("Invalid double constant [" + value + "]."));
            }
        }
    }

    @Override
    protected void write(final CompilerSettings settings, final Definition definition, final GeneratorAdapter adapter) {
        throw new IllegalArgumentException(error("Illegal tree structure."));
    }
}
