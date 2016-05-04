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
import org.elasticsearch.painless.Definition.Sort;
import org.elasticsearch.painless.Definition.Type;
import org.elasticsearch.painless.tree.analyzer.Caster;
import org.elasticsearch.painless.tree.analyzer.Operation;
import org.elasticsearch.painless.tree.analyzer.Variables;
import org.objectweb.asm.commons.GeneratorAdapter;

public class EUnary extends AExpression {
    protected Operation operation;
    protected AExpression child;

    public EUnary(final String location, final Operation operation, final AExpression child) {
        super(location);

        this.operation = operation;
        this.child = child;
    }

    @Override
    protected void analyze(final CompilerSettings settings, final Definition definition, final Variables variables) {
        if (operation == Operation.NOT) {
            analyzeNot(settings, definition, variables);
        } else if (operation == Operation.BWNOT) {
            analyzeBWNot(settings, definition, variables);
        } else if (operation == Operation.ADD) {
            analyzerAdd(settings, definition, variables);
        } else if (operation == Operation.SUB) {
            analyzerSub(settings, definition, variables);
        } else {
            throw new IllegalStateException(error("Illegal tree structure."));
        }
    }

    protected void analyzeNot(final CompilerSettings settings, final Definition definition, final Variables variables) {
        child.expected = definition.booleanType;
        child.analyze(settings, definition, variables);
        child = child.cast(definition);

        if (child.constant != null) {
            constant = !(boolean)constant;
        }

        actual = definition.booleanType;
    }

    protected void analyzeBWNot(final CompilerSettings settings, final Definition definition, final Variables variables) {
        child.analyze(settings, definition, variables);

        final Type promote = Caster.promoteNumeric(definition, child.actual, false, true);

        if (promote == null) {
            throw new ClassCastException(error("Cannot apply not [~] to type [" + child.actual.name + "]."));
        }

        child.expected = promote;
        child = child.cast(definition);

        if (child.constant != null) {
            final Sort sort = promote.sort;

            if (sort == Sort.INT) {
                constant = ~(int)child.constant;
            } else if (sort == Sort.LONG) {
                constant = ~(long)child.constant;
            } else {
                throw new IllegalStateException(error("Illegal tree structure."));
            }
        }

        actual = promote;
        typesafe = child.typesafe;
    }

    protected void analyzerAdd(final CompilerSettings settings, final Definition definition, final Variables variables) {
        child.analyze(settings, definition, variables);

        final Type promote = Caster.promoteNumeric(definition, child.actual, true, true);

        if (promote == null) {
            throw new ClassCastException(error("Cannot apply positive [+] to type [" + child.actual.name + "]."));
        }

        child.expected = promote;
        child = child.cast(definition);

        if (child.constant != null) {
            final Sort sort = promote.sort;

            if (sort == Sort.INT) {
                constant = +(int)child.constant;
            } else if (sort == Sort.LONG) {
                constant = +(long)child.constant;
            } else if (sort == Sort.FLOAT) {
                constant = +(float)child.constant;
            } else if (sort == Sort.DOUBLE) {
                constant = +(double)child.constant;
            } else {
                throw new IllegalStateException(error("Illegal tree structure."));
            }
        }

        actual = promote;
        typesafe = child.typesafe;
    }

    protected void analyzerSub(final CompilerSettings settings, final Definition definition, final Variables variables) {
        child.analyze(settings, definition, variables);

        final Type promote = Caster.promoteNumeric(definition, child.actual, true, true);

        if (promote == null) {
            throw new ClassCastException(error("Cannot apply negative [-] to type [" + child.actual.name + "]."));
        }

        child.expected = promote;
        child = child.cast(definition);

        if (child.constant != null) {
            final Sort sort = promote.sort;

            if (sort == Sort.INT) {
                constant = -(int)child.constant;
            } else if (sort == Sort.LONG) {
                constant = -(long)child.constant;
            } else if (sort == Sort.FLOAT) {
                constant = -(float)child.constant;
            } else if (sort == Sort.DOUBLE) {
                constant = -(double)child.constant;
            } else {
                throw new IllegalStateException(error("Illegal tree structure."));
            }
        }

        actual = promote;
        typesafe = child.typesafe;
    }

    @Override
    protected void write(final GeneratorAdapter adapter) {

    }
}
