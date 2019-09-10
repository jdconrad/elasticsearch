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

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a binary math expression.
 */
public final class EBinary extends AExpression {

    final Operation operation;
    final boolean compound;

    private Class<?> promote = null;            // promoted type
    private Class<?> shiftDistance = null;      // for shifts, the rhs is promoted independently
    boolean cat = false;
    private boolean originallyExplicit = false; // record whether there was originally an explicit cast

    public EBinary(Location location, Operation operation, boolean compound) {
        super(location);

        this.operation = Objects.requireNonNull(operation);
        this.compound = compound;
    }

    @Override
    void analyze(SymbolTable table) {
        originallyExplicit = explicit;

        if (operation == Operation.MUL) {
            analyzeMul(table);
        } else if (operation == Operation.DIV) {
            analyzeDiv(table);
        } else if (operation == Operation.REM) {
            analyzeRem(table);
        } else if (operation == Operation.ADD) {
            analyzeAdd(table);
        } else if (operation == Operation.SUB) {
            analyzeSub(table);
        } else if (operation == Operation.FIND) {
            analyzeRegexOp(table);
        } else if (operation == Operation.MATCH) {
            analyzeRegexOp(table);
        } else if (operation == Operation.LSH) {
            analyzeLSH(table);
        } else if (operation == Operation.RSH) {
            analyzeRSH(table);
        } else if (operation == Operation.USH) {
            analyzeUSH(table);
        } else if (operation == Operation.BWAND) {
            analyzeBWAnd(table);
        } else if (operation == Operation.XOR) {
            analyzeXor(table);
        } else if (operation == Operation.BWOR) {
            analyzeBWOr(table);
        } else {
            throw createError(new IllegalStateException("Illegal tree structure."));
        }
    }

    private void analyzeMul(SymbolTable table) {
        AExpression left = (AExpression)children.get(0);
        AExpression right = (AExpression)children.get(1);

        left.analyze(table);
        right.analyze(table);

        promote = AnalyzerCaster.promoteNumeric(left.actual, right.actual, true);

        if (promote == null) {
            throw createError(new ClassCastException("Cannot apply multiply [*] to types " +
                    "[" + PainlessLookupUtility.typeToCanonicalTypeName(left.actual) + "] and " +
                    "[" + PainlessLookupUtility.typeToCanonicalTypeName(right.actual) + "]."));
        }

        actual = promote;

        if (promote == def.class) {
            left.expected = left.actual;
            right.expected = right.actual;
            if (expected != null) {
                actual = expected;
            }
        } else {
            left.expected = promote;
            right.expected = promote;
        }

        children.set(0, left.cast(table));
        children.set(1, right.cast(table));
    }

    private void analyzeDiv(SymbolTable table) {
        AExpression left = (AExpression)children.get(0);
        AExpression right = (AExpression)children.get(1);

        left.analyze(table);
        right.analyze(table);

        promote = AnalyzerCaster.promoteNumeric(left.actual, right.actual, true);

        if (promote == null) {
            throw createError(new ClassCastException("Cannot apply divide [/] to types " +
                    "[" + PainlessLookupUtility.typeToCanonicalTypeName(left.actual) + "] and " +
                    "[" + PainlessLookupUtility.typeToCanonicalTypeName(right.actual) + "]."));
        }

        actual = promote;

        if (promote == def.class) {
            left.expected = left.actual;
            right.expected = right.actual;

            if (expected != null) {
                actual = expected;
            }
        } else {
            left.expected = promote;
            right.expected = promote;
        }

        children.set(0, left.cast(table));
        children.set(1, right.cast(table));
    }

    private void analyzeRem(SymbolTable table) {
        AExpression left = (AExpression)children.get(0);
        AExpression right = (AExpression)children.get(1);

        left.analyze(table);
        right.analyze(table);

        promote = AnalyzerCaster.promoteNumeric(left.actual, right.actual, true);

        if (promote == null) {
            throw createError(new ClassCastException("Cannot apply remainder [%] to types " +
                    "[" + PainlessLookupUtility.typeToCanonicalTypeName(left.actual) + "] and " +
                    "[" + PainlessLookupUtility.typeToCanonicalTypeName(right.actual) + "]."));
        }

        actual = promote;

        if (promote == def.class) {
            left.expected = left.actual;
            right.expected = right.actual;

            if (expected != null) {
                actual = expected;
            }
        } else {
            left.expected = promote;
            right.expected = promote;
        }

        children.set(0, left.cast(table));
        children.set(1, right.cast(table));
    }

    private void analyzeAdd(SymbolTable table) {
        AExpression left = (AExpression)children.get(0);
        AExpression right = (AExpression)children.get(1);

        left.analyze(table);
        right.analyze(table);

        promote = AnalyzerCaster.promoteAdd(left.actual, right.actual);

        if (promote == null) {
            throw createError(new ClassCastException("Cannot apply add [+] to types " +
                    "[" + PainlessLookupUtility.typeToCanonicalTypeName(left.actual) + "] and " +
                    "[" + PainlessLookupUtility.typeToCanonicalTypeName(right.actual) + "]."));
        }

        actual = promote;

        if (promote == String.class) {
            left.expected = left.actual;

            if (left instanceof EBinary && ((EBinary)left).operation == Operation.ADD && left.actual == String.class) {
                ((EBinary)left).cat = true;
            }

            right.expected = right.actual;

            if (right instanceof EBinary && ((EBinary)right).operation == Operation.ADD && right.actual == String.class) {
                ((EBinary)right).cat = true;
            }
        } else if (promote == def.class) {
            left.expected = left.actual;
            right.expected = right.actual;

            if (expected != null) {
                actual = expected;
            }
        } else {
            left.expected = promote;
            right.expected = promote;
        }

        children.set(0, left.cast(table));
        children.set(1, right.cast(table));
    }

    private void analyzeSub(SymbolTable table) {
        AExpression left = (AExpression)children.get(0);
        AExpression right = (AExpression)children.get(1);

        left.analyze(table);
        right.analyze(table);

        promote = AnalyzerCaster.promoteNumeric(left.actual, right.actual, true);

        if (promote == null) {
            throw createError(new ClassCastException("Cannot apply subtract [-] to types " +
                    "[" + PainlessLookupUtility.typeToCanonicalTypeName(left.actual) + "] and " +
                    "[" + PainlessLookupUtility.typeToCanonicalTypeName(right.actual) + "]."));
        }

        actual = promote;

        if (promote == def.class) {
            left.expected = left.actual;
            right.expected = right.actual;

            if (expected != null) {
                actual = expected;
            }
        } else {
            left.expected = promote;
            right.expected = promote;
        }

        children.set(0, left.cast(table));
        children.set(1, right.cast(table));
    }

    private void analyzeRegexOp(SymbolTable table) {
        AExpression left = (AExpression)children.get(0);
        AExpression right = (AExpression)children.get(1);

        left.analyze(table);
        right.analyze(table);

        left.expected = String.class;
        right.expected = Pattern.class;

        children.set(0, left.cast(table));
        children.set(1, right.cast(table));

        promote = boolean.class;
        actual = boolean.class;
    }

    private void analyzeLSH(SymbolTable table) {
        AExpression left = (AExpression)children.get(0);
        AExpression right = (AExpression)children.get(1);

        left.analyze(table);
        right.analyze(table);

        Class<?> lhspromote = AnalyzerCaster.promoteNumeric(left.actual, false);
        Class<?> rhspromote = AnalyzerCaster.promoteNumeric(right.actual, false);

        if (lhspromote == null || rhspromote == null) {
            throw createError(new ClassCastException("Cannot apply left shift [<<] to types " +
                    "[" + PainlessLookupUtility.typeToCanonicalTypeName(left.actual) + "] and " +
                    "[" + PainlessLookupUtility.typeToCanonicalTypeName(right.actual) + "]."));
        }

        actual = promote = lhspromote;
        shiftDistance = rhspromote;

        if (lhspromote == def.class || rhspromote == def.class) {
            left.expected = left.actual;
            right.expected = right.actual;

            if (expected != null) {
                actual = expected;
            }
        } else {
            left.expected = lhspromote;

            if (rhspromote == long.class) {
                right.expected = int.class;
                right.explicit = true;
            } else {
                right.expected = rhspromote;
            }
        }

        children.set(0, left.cast(table));
        children.set(1, right.cast(table));
    }

    private void analyzeRSH(SymbolTable table) {
        AExpression left = (AExpression)children.get(0);
        AExpression right = (AExpression)children.get(1);

        left.analyze(table);
        right.analyze(table);

        Class<?> lhspromote = AnalyzerCaster.promoteNumeric(left.actual, false);
        Class<?> rhspromote = AnalyzerCaster.promoteNumeric(right.actual, false);

        if (lhspromote == null || rhspromote == null) {
            throw createError(new ClassCastException("Cannot apply right shift [>>] to types " +
                    "[" + PainlessLookupUtility.typeToCanonicalTypeName(left.actual) + "] and " +
                    "[" + PainlessLookupUtility.typeToCanonicalTypeName(right.actual) + "]."));
        }

        actual = promote = lhspromote;
        shiftDistance = rhspromote;

        if (lhspromote == def.class || rhspromote == def.class) {
            left.expected = left.actual;
            right.expected = right.actual;

            if (expected != null) {
                actual = expected;
            }
        } else {
            left.expected = lhspromote;

            if (rhspromote == long.class) {
                right.expected = int.class;
                right.explicit = true;
            } else {
                right.expected = rhspromote;
            }
        }

        children.set(0, left.cast(table));
        children.set(1, right.cast(table));
    }

    private void analyzeUSH(SymbolTable table) {
        AExpression left = (AExpression)children.get(0);
        AExpression right = (AExpression)children.get(1);

        left.analyze(table);
        right.analyze(table);

        Class<?> lhspromote = AnalyzerCaster.promoteNumeric(left.actual, false);
        Class<?> rhspromote = AnalyzerCaster.promoteNumeric(right.actual, false);

        actual = promote = lhspromote;
        shiftDistance = rhspromote;

        if (lhspromote == null || rhspromote == null) {
            throw createError(new ClassCastException("Cannot apply unsigned shift [>>>] to types " +
                    "[" + PainlessLookupUtility.typeToCanonicalTypeName(left.actual) + "] and " +
                    "[" + PainlessLookupUtility.typeToCanonicalTypeName(right.actual) + "]."));
        }

        if (lhspromote == def.class || rhspromote == def.class) {
            left.expected = left.actual;
            right.expected = right.actual;

            if (expected != null) {
                actual = expected;
            }
        } else {
            left.expected = lhspromote;

            if (rhspromote == long.class) {
                right.expected = int.class;
                right.explicit = true;
            } else {
                right.expected = rhspromote;
            }
        }

        children.set(0, left.cast(table));
        children.set(1, right.cast(table));
    }

    private void analyzeBWAnd(SymbolTable table) {
        AExpression left = (AExpression)children.get(0);
        AExpression right = (AExpression)children.get(1);

        left.analyze(table);
        right.analyze(table);

        if (compound) {
            promote = AnalyzerCaster.promoteXor(left.actual, right.actual);
        } else {
            promote = AnalyzerCaster.promoteNumeric(left.actual, right.actual, false);
        }

        if (promote == null) {
            throw createError(new ClassCastException("Cannot apply and [&] to types " +
                    "[" + PainlessLookupUtility.typeToCanonicalTypeName(left.actual) + "] and " +
                    "[" + PainlessLookupUtility.typeToCanonicalTypeName(right.actual) + "]."));
        }

        actual = promote;

        if (promote == def.class) {
            left.expected = left.actual;
            right.expected = right.actual;

            if (expected != null) {
                actual = expected;
            }
        } else {
            left.expected = promote;
            right.expected = promote;
        }

        children.set(0, left.cast(table));
        children.set(1, right.cast(table));
    }

    private void analyzeXor(SymbolTable table) {
        AExpression left = (AExpression)children.get(0);
        AExpression right = (AExpression)children.get(1);

        left.analyze(table);
        right.analyze(table);

        promote = AnalyzerCaster.promoteXor(left.actual, right.actual);

        if (promote == null) {
            throw createError(new ClassCastException("Cannot apply xor [^] to types " +
                    "[" + PainlessLookupUtility.typeToCanonicalTypeName(left.actual) + "] and " +
                    "[" + PainlessLookupUtility.typeToCanonicalTypeName(right.actual) + "]."));
        }

        actual = promote;

        if (promote == def.class) {
            left.expected = left.actual;
            right.expected = right.actual;
            if (expected != null) {
                actual = expected;
            }
        } else {
            left.expected = promote;
            right.expected = promote;
        }

        children.set(0, left.cast(table));
        children.set(1, right.cast(table));
    }

    private void analyzeBWOr(SymbolTable table) {
        AExpression left = (AExpression)children.get(0);
        AExpression right = (AExpression)children.get(1);

        left.analyze(table);
        right.analyze(table);

        if (compound) {
            promote = AnalyzerCaster.promoteXor(left.actual, right.actual);
        } else {
            promote = AnalyzerCaster.promoteNumeric(left.actual, right.actual, false);
        }

        if (promote == null) {
            throw createError(new ClassCastException("Cannot apply or [|] to types " +
                    "[" + PainlessLookupUtility.typeToCanonicalTypeName(left.actual) + "] and " +
                    "[" + PainlessLookupUtility.typeToCanonicalTypeName(right.actual) + "]."));
        }

        actual = promote;

        if (promote == def.class) {
            left.expected = left.actual;
            right.expected = right.actual;
            if (expected != null) {
                actual = expected;
            }
        } else {
            left.expected = promote;
            right.expected = promote;
        }

        children.set(0, left.cast(table));
        children.set(1, right.cast(table));
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
                if (compound) {
                    flags |= DefBootstrap.OPERATOR_COMPOUND_ASSIGNMENT;
                } else if (originallyExplicit) {
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
