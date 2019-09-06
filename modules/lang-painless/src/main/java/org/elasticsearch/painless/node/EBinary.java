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
import org.elasticsearch.painless.Locals;
import org.elasticsearch.painless.Location;
import org.elasticsearch.painless.MethodWriter;
import org.elasticsearch.painless.Operation;
import org.elasticsearch.painless.WriterConstants;
import org.elasticsearch.painless.builder.SymbolTable;
import org.elasticsearch.painless.lookup.PainlessLookupUtility;
import org.elasticsearch.painless.lookup.def;

import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a binary math expression.
 */
public final class EBinary extends AExpression {

    final Operation operation;

    private Class<?> promote = null;            // promoted type
    private Class<?> shiftDistance = null;      // for shifts, the rhs is promoted independently
    boolean cat = false;
    private boolean originallyExplicit = false; // record whether there was originally an explicit cast

    public EBinary(Location location, Operation operation) {
        super(location);

        this.operation = Objects.requireNonNull(operation);
    }

    @Override
    void storeSettings(CompilerSettings settings) {
        children.get(0).storeSettings(settings);
        children.get(1).storeSettings(settings);
    }

    public static void enter(ANode node, SymbolTable table, Map<String, Object> data) {
        EBinary binary = (EBinary)node;
        Operation operation = binary.operation;

        binary.originallyExplicit = binary.explicit;

        if (operation == Operation.FIND || operation == operation.MATCH) {
            AExpression lhs = (AExpression)binary.children.get(0);
            AExpression rhs = (AExpression)binary.children.get(0);

            lhs.expected = String.class;
            rhs.expected = Pattern.class;
        }
    }

    public static void exit(ANode node, SymbolTable table, Map<String, Object> data) {
        EBinary binary = (EBinary) node;
        Operation operation = binary.operation;
        AExpression lhs = (AExpression) binary.children.get(0);
        AExpression rhs = (AExpression) binary.children.get(0);

        boolean shift = false;

        switch (operation) {
            case MUL:
            case DIV:
            case REM:
            case SUB:
                binary.promote = AnalyzerCaster.promoteNumeric(lhs.actual, rhs.actual, true);
                break;
            case ADD:
                binary.promote = AnalyzerCaster.promoteAdd(lhs.actual, rhs.actual);
                break;
            case LSH:
            case RSH:
            case USH:
                binary.promote = AnalyzerCaster.promoteNumeric(lhs.actual, false);
                binary.shiftDistance = AnalyzerCaster.promoteNumeric(rhs.actual, false);
                shift = true;
                break;
            case BWAND:
            case BWOR:
                binary.promote = AnalyzerCaster.promoteNumeric(lhs.actual, rhs.actual, false);
                break;
            case XOR:
                binary.promote = AnalyzerCaster.promoteXor(lhs.actual, rhs.actual);
                break;
            case FIND:
            case MATCH:
                binary.promote = boolean.class;
                break;
            default:
                throw binary.createError(new IllegalStateException("illegal tree structure"));
        }

        if (binary.promote == null || (shift && binary.shiftDistance == null)) {
            throw binary.createError(new ClassCastException(
                    "illegal operation [" + operation.symbol + "] for types " +
                    "[" + PainlessLookupUtility.typeToCanonicalTypeName(lhs.actual) + "] and " +
                    "[" + PainlessLookupUtility.typeToCanonicalTypeName(rhs.actual) + "]"));
        }

        binary.actual = binary.promote;

        if (operation != Operation.FIND && operation != Operation.MATCH) {
            if (operation == Operation.ADD && binary.promote == String.class) {
                lhs.expected = lhs.actual;

                if (lhs instanceof EBinary && ((EBinary) lhs).operation == Operation.ADD && lhs.actual == String.class) {
                    ((EBinary) lhs).cat = true;
                }

                rhs.expected = rhs.actual;

                if (rhs instanceof EBinary && ((EBinary) rhs).operation == Operation.ADD && rhs.actual == String.class) {
                    ((EBinary) rhs).cat = true;
                }
            } else if (binary.promote == def.class) {
                lhs.expected = lhs.actual;
                rhs.expected = rhs.actual;

                if (binary.expected != null) {
                    binary.actual = binary.expected;
                }
            } else {
                lhs.expected = binary.promote;
                rhs.expected = binary.promote;
            }
        }

        binary.children.set(0, lhs.cast());
        binary.children.set(1, rhs.cast());
    }

    @Override
    void write(MethodWriter writer, Globals globals) {
        AExpression left = (AExpression)children.get(0);
        AExpression right = (AExpression)children.get(1);

        writer.writeDebugInfo(location);

        if (promote == String.class && operation == Operation.ADD) {
            if (!cat) {
                writer.writeNewStrings();
            }

            left.write(writer, globals);

            if (!(left instanceof EBinary) || !((EBinary)left).cat) {
                writer.writeAppendStrings(left.actual);
            }

            right.write(writer, globals);

            if (!(right instanceof EBinary) || !((EBinary)right).cat) {
                writer.writeAppendStrings(right.actual);
            }

            if (!cat) {
                writer.writeToStrings();
            }
        } else if (operation == Operation.FIND || operation == Operation.MATCH) {
            right.write(writer, globals);
            left.write(writer, globals);
            writer.invokeVirtual(org.objectweb.asm.Type.getType(Pattern.class), WriterConstants.PATTERN_MATCHER);

            if (operation == Operation.FIND) {
                writer.invokeVirtual(org.objectweb.asm.Type.getType(Matcher.class), WriterConstants.MATCHER_FIND);
            } else if (operation == Operation.MATCH) {
                writer.invokeVirtual(org.objectweb.asm.Type.getType(Matcher.class), WriterConstants.MATCHER_MATCHES);
            } else {
                throw new IllegalStateException("Illegal tree structure.");
            }
        } else {
            left.write(writer, globals);
            right.write(writer, globals);

            if (promote == def.class || (shiftDistance != null && shiftDistance == def.class)) {
                // def calls adopt the wanted return value. if there was a narrowing cast,
                // we need to flag that so that its done at runtime.
                int flags = 0;
                if (originallyExplicit) {
                    flags |= DefBootstrap.OPERATOR_EXPLICIT_CAST;
                }
                writer.writeDynamicBinaryInstruction(location, actual, left.actual, right.actual, operation, flags);
            } else {
                writer.writeBinaryInstruction(location, actual, operation);
            }
        }
    }

    @Override
    public String toString() {
        return singleLineToString(children.get(0), operation.symbol, children.get(1));
    }
}
