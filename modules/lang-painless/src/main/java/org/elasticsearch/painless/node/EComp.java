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
import org.elasticsearch.painless.CompilerSettings;
import org.elasticsearch.painless.DefBootstrap;
import org.elasticsearch.painless.Globals;
import org.elasticsearch.painless.Location;
import org.elasticsearch.painless.MethodWriter;
import org.elasticsearch.painless.Operation;
import org.elasticsearch.painless.builder.SymbolTable;
import org.elasticsearch.painless.lookup.PainlessLookupUtility;
import org.elasticsearch.painless.lookup.def;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;

import java.util.Map;
import java.util.Objects;

import static org.elasticsearch.painless.WriterConstants.EQUALS;
import static org.elasticsearch.painless.WriterConstants.OBJECTS_TYPE;

/**
 * Represents a comparison expression.
 */
public final class EComp extends AExpression {

    private final Operation operation;

    private Class<?> promote;

    public EComp(Location location, Operation operation) {
        super(location);

        this.operation = Objects.requireNonNull(operation);
    }

    @Override
    void storeSettings(CompilerSettings settings) {
        children.get(0).storeSettings(settings);
        children.get(1).storeSettings(settings);
    }

    public static void exit(ANode node, SymbolTable table, Map<String, Object> data) {
        EComp comp = (EComp)node;
        Operation operation = comp.operation;
        AExpression lhs = (AExpression)comp.children.get(0);
        AExpression rhs = (AExpression)comp.children.get(1);

        switch (operation) {
            case EQ:
            case EQR:
            case NE:
            case NER:
                comp.promote = AnalyzerCaster.promoteEquality(lhs.actual, rhs.actual);
                break;
            case GT:
            case GTE:
            case LT:
            case LTE:
                comp.promote = AnalyzerCaster.promoteNumeric(lhs.actual, rhs.actual, true);
                break;
            default:
                throw comp.createError(new IllegalStateException("illegal tree structure"));
        }

        if (comp.promote == null) {
            throw comp.createError(new ClassCastException(
                    "illegal operation [" + operation.symbol + "] for types " +
                    "[" + PainlessLookupUtility.typeToCanonicalTypeName(lhs.actual) + "] and " +
                    "[" + PainlessLookupUtility.typeToCanonicalTypeName(rhs.actual) + "]"));
        }

        if (comp.promote == def.class) {
            lhs.expected = lhs.actual;
            rhs.expected = rhs.actual;
        } else {
            lhs.expected = comp.promote;
            rhs.expected = comp.promote;
        }

        comp.children.set(0, lhs.cast());
        comp.children.set(1, rhs.cast());

        comp.actual = boolean.class;
    }

    @Override
    void write(MethodWriter writer, Globals globals) {
        AExpression left = (AExpression)children.get(0);
        AExpression right = (AExpression)children.get(1);

        writer.writeDebugInfo(location);

        left.write(writer, globals);

        if (!right.isNull) {
            right.write(writer, globals);
        }

        Label jump = new Label();
        Label end = new Label();

        boolean eq = (operation == Operation.EQ || operation == Operation.EQR);
        boolean ne = (operation == Operation.NE || operation == Operation.NER);
        boolean lt  = operation == Operation.LT;
        boolean lte = operation == Operation.LTE;
        boolean gt  = operation == Operation.GT;
        boolean gte = operation == Operation.GTE;

        boolean writejump = true;

        Type type = MethodWriter.getType(promote);

        if (promote == void.class || promote == byte.class || promote == short.class || promote == char.class) {
            throw createError(new IllegalStateException("Illegal tree structure."));
        } else if (promote == boolean.class) {
            if (eq) writer.ifCmp(type, MethodWriter.EQ, jump);
            else if (ne) writer.ifCmp(type, MethodWriter.NE, jump);
            else {
                throw createError(new IllegalStateException("Illegal tree structure."));
            }
        } else if (promote == int.class || promote == long.class || promote == float.class || promote == double.class) {
            if (eq) writer.ifCmp(type, MethodWriter.EQ, jump);
            else if (ne) writer.ifCmp(type, MethodWriter.NE, jump);
            else if (lt) writer.ifCmp(type, MethodWriter.LT, jump);
            else if (lte) writer.ifCmp(type, MethodWriter.LE, jump);
            else if (gt) writer.ifCmp(type, MethodWriter.GT, jump);
            else if (gte) writer.ifCmp(type, MethodWriter.GE, jump);
            else {
                throw createError(new IllegalStateException("Illegal tree structure."));
            }

        } else if (promote == def.class) {
            Type booleanType = Type.getType(boolean.class);
            Type descriptor = Type.getMethodType(booleanType, MethodWriter.getType(left.actual), MethodWriter.getType(right.actual));

            if (eq) {
                if (right.isNull) {
                    writer.ifNull(jump);
                } else if (!left.isNull && operation == Operation.EQ) {
                    writer.invokeDefCall("eq", descriptor, DefBootstrap.BINARY_OPERATOR, DefBootstrap.OPERATOR_ALLOWS_NULL);
                    writejump = false;
                } else {
                    writer.ifCmp(type, MethodWriter.EQ, jump);
                }
            } else if (ne) {
                if (right.isNull) {
                    writer.ifNonNull(jump);
                } else if (!left.isNull && operation == Operation.NE) {
                    writer.invokeDefCall("eq", descriptor, DefBootstrap.BINARY_OPERATOR, DefBootstrap.OPERATOR_ALLOWS_NULL);
                    writer.ifZCmp(MethodWriter.EQ, jump);
                } else {
                    writer.ifCmp(type, MethodWriter.NE, jump);
                }
            } else if (lt) {
                writer.invokeDefCall("lt", descriptor, DefBootstrap.BINARY_OPERATOR, 0);
                writejump = false;
            } else if (lte) {
                writer.invokeDefCall("lte", descriptor, DefBootstrap.BINARY_OPERATOR, 0);
                writejump = false;
            } else if (gt) {
                writer.invokeDefCall("gt", descriptor, DefBootstrap.BINARY_OPERATOR, 0);
                writejump = false;
            } else if (gte) {
                writer.invokeDefCall("gte", descriptor, DefBootstrap.BINARY_OPERATOR, 0);
                writejump = false;
            } else {
                throw createError(new IllegalStateException("Illegal tree structure."));
            }
        } else {
            if (eq) {
                if (right.isNull) {
                    writer.ifNull(jump);
                } else if (operation == Operation.EQ) {
                    writer.invokeStatic(OBJECTS_TYPE, EQUALS);
                    writejump = false;
                } else {
                    writer.ifCmp(type, MethodWriter.EQ, jump);
                }
            } else if (ne) {
                if (right.isNull) {
                    writer.ifNonNull(jump);
                } else if (operation == Operation.NE) {
                    writer.invokeStatic(OBJECTS_TYPE, EQUALS);
                    writer.ifZCmp(MethodWriter.EQ, jump);
                } else {
                    writer.ifCmp(type, MethodWriter.NE, jump);
                }
            } else {
                throw createError(new IllegalStateException("Illegal tree structure."));
            }
        }

        if (writejump) {
            writer.push(false);
            writer.goTo(end);
            writer.mark(jump);
            writer.push(true);
            writer.mark(end);
        }
    }

    @Override
    public String toString() {
        return singleLineToString(children.get(0), operation.symbol, children.get(1));
    }
}
