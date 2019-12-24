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

import org.elasticsearch.painless.AnalyzerCaster;
import org.elasticsearch.painless.Locals;
import org.elasticsearch.painless.Location;
import org.elasticsearch.painless.Operation;
import org.elasticsearch.painless.ir.TypeNode;
import org.elasticsearch.painless.ir.UnaryMathNode;
import org.elasticsearch.painless.ir.UnaryNode;
import org.elasticsearch.painless.lookup.PainlessLookupUtility;
import org.elasticsearch.painless.lookup.def;
import org.elasticsearch.painless.symbol.ScriptRoot;

import java.util.Objects;
import java.util.Set;

/**
 * Represents a unary math expression.
 */
public final class EUnary extends AExpression {

    private final Operation operation;
    private AExpression child;

    private Class<?> promote;
    private boolean originallyExplicit = false; // record whether there was originally an explicit cast

    public EUnary(Location location, Operation operation, AExpression child) {
        super(location);

        this.operation = Objects.requireNonNull(operation);
        this.child = Objects.requireNonNull(child);
    }

    @Override
    void extractVariables(Set<String> variables) {
        child.extractVariables(variables);
    }

    @Override
    void analyze(ScriptRoot scriptRoot, Locals locals) {
        originallyExplicit = explicit;

        if (operation == Operation.NOT) {
            analyzeNot(scriptRoot, locals);
        } else if (operation == Operation.BWNOT) {
            analyzeBWNot(scriptRoot, locals);
        } else if (operation == Operation.ADD) {
            analyzerAdd(scriptRoot, locals);
        } else if (operation == Operation.SUB) {
            analyzerSub(scriptRoot, locals);
        } else {
            throw createError(new IllegalStateException("Illegal tree structure."));
        }
    }

    void analyzeNot(ScriptRoot scriptRoot, Locals variables) {
        child.expected = boolean.class;
        child.analyze(scriptRoot, variables);
        child = child.cast(scriptRoot, variables);

        if (child.constant != null) {
            constant = !(boolean)child.constant;
        }

        actual = boolean.class;
    }

    void analyzeBWNot(ScriptRoot scriptRoot, Locals variables) {
        child.analyze(scriptRoot, variables);

        promote = AnalyzerCaster.promoteNumeric(child.actual, false);

        if (promote == null) {
            throw createError(new ClassCastException("Cannot apply not [~] to type " +
                    "[" + PainlessLookupUtility.typeToCanonicalTypeName(child.actual) + "]."));
        }

        child.expected = promote;
        child = child.cast(scriptRoot, variables);

        if (child.constant != null) {
            if (promote == int.class) {
                constant = ~(int)child.constant;
            } else if (promote == long.class) {
                constant = ~(long)child.constant;
            } else {
                throw createError(new IllegalStateException("Illegal tree structure."));
            }
        }

        if (promote == def.class && expected != null) {
            actual = expected;
        } else {
            actual = promote;
        }
    }

    void analyzerAdd(ScriptRoot scriptRoot, Locals variables) {
        child.analyze(scriptRoot, variables);

        promote = AnalyzerCaster.promoteNumeric(child.actual, true);

        if (promote == null) {
            throw createError(new ClassCastException("Cannot apply positive [+] to type " +
                    "[" + PainlessLookupUtility.typeToJavaType(child.actual) + "]."));
        }

        child.expected = promote;
        child = child.cast(scriptRoot, variables);

        if (child.constant != null) {
            if (promote == int.class) {
                constant = +(int)child.constant;
            } else if (promote == long.class) {
                constant = +(long)child.constant;
            } else if (promote == float.class) {
                constant = +(float)child.constant;
            } else if (promote == double.class) {
                constant = +(double)child.constant;
            } else {
                throw createError(new IllegalStateException("Illegal tree structure."));
            }
        }

        if (promote == def.class && expected != null) {
            actual = expected;
        } else {
            actual = promote;
        }
    }

    void analyzerSub(ScriptRoot scriptRoot, Locals variables) {
        child.analyze(scriptRoot, variables);

        promote = AnalyzerCaster.promoteNumeric(child.actual, true);

        if (promote == null) {
            throw createError(new ClassCastException("Cannot apply negative [-] to type " +
                    "[" + PainlessLookupUtility.typeToJavaType(child.actual) + "]."));
        }

        child.expected = promote;
        child = child.cast(scriptRoot, variables);

        if (child.constant != null) {
            if (promote == int.class) {
                constant = -(int)child.constant;
            } else if (promote == long.class) {
                constant = -(long)child.constant;
            } else if (promote == float.class) {
                constant = -(float)child.constant;
            } else if (promote == double.class) {
                constant = -(double)child.constant;
            } else {
                throw createError(new IllegalStateException("Illegal tree structure."));
            }
        }

        if (promote == def.class && expected != null) {
            actual = expected;
        } else {
            actual = promote;
        }
    }

    @Override
    UnaryNode write() {
        return new UnaryMathNode()
                .setTypeNode(new TypeNode()
                        .setLocation(location)
                        .setType(actual)
                )
                .setChildNode(child.write())
                .setUnaryTypeNode(new TypeNode()
                        .setLocation(location)
                        .setType(promote)
                )
                .setLocation(location)
                .setOperation(operation);
    }

    @Override
    public String toString() {
        return singleLineToString(operation.symbol, child);
    }
}
