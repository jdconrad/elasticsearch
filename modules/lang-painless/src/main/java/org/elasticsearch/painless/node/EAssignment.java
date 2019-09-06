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
import org.elasticsearch.painless.builder.SymbolTable;
import org.elasticsearch.painless.lookup.PainlessCast;
import org.elasticsearch.painless.lookup.def;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents an assignment with the lhs and rhs as child nodes.
 */
public final class EAssignment extends AExpression {

    private final boolean pre;
    private final boolean post;
    private Operation operation;

    private boolean cat = false;
    private Class<?> promote = null;
    private Class<?> shiftDistance; // for shifts, the RHS is promoted independently
    private PainlessCast there = null;
    private PainlessCast back = null;

    public EAssignment(Location location, boolean pre, boolean post, Operation operation) {
        super(location);

        this.pre = pre;
        this.post = post;
        this.operation = operation;
    }

    @Override
    void storeSettings(CompilerSettings settings) {
        children.get(0).storeSettings(settings);

        if (children.get(1) != null) {
            children.get(1).storeSettings(settings);
        }
    }

    public static void enter(ANode node, SymbolTable table, Map<String, Object> data) {
        EAssignment assignment = (EAssignment)node;
        AExpression lhs = (AExpression)assignment.children.get(0);

        if (lhs instanceof AStoreable) {
            lhs.read = assignment.read;
            ((AStoreable)lhs).write = true;
        } else {
            throw assignment.createError(new IllegalArgumentException("left-hand side of an assignment must be assignable"));
        }
    }

    public static void before(ANode node, ANode child, int index, SymbolTable table, Map<String, Object> data) {
        if (index == 1) {
            EAssignment assignment = (EAssignment)node;
            Location location = assignment.location;
            Operation operation = assignment.operation;
            AStoreable lhs = (AStoreable) assignment.children.get(0);
            AExpression rhs = (AExpression)assignment.children.get(1);

            if (assignment.pre && assignment.post) {
                throw assignment.createError(new IllegalStateException("illegal tree structure"));
            } else if (assignment.pre || assignment.post) {
                if (rhs != null) {
                    throw assignment.createError(new IllegalStateException("illegal tree structure"));
                }

                if (operation == Operation.INCR) {
                    if (lhs.actual == double.class) {
                        assignment.replace(1, new EConstant(location, 1D));
                    } else if (lhs.actual == float.class) {
                        assignment.replace(1, new EConstant(location, 1F));
                    } else if (lhs.actual == long.class) {
                        assignment.replace(1, new EConstant(location, 1L));
                    } else {
                        assignment.replace(1, new EConstant(location, 1));
                    }

                    assignment.operation = Operation.ADD;
                } else if (operation == Operation.DECR) {
                    if (lhs.actual == double.class) {
                        assignment.replace(1, new EConstant(location, 1D));
                    } else if (lhs.actual == float.class) {
                        assignment.replace(1, new EConstant(location, 1F));
                    } else if (lhs.actual == long.class) {
                        assignment.replace(1, new EConstant(location, 1L));
                    } else {
                        assignment.replace(1, new EConstant(location, 1));
                    }

                    assignment.operation = Operation.SUB;
                } else {
                    throw assignment.createError(new IllegalStateException("illegal tree structure"));
                }
            }

            if (operation == null && lhs.isDefOptimized() == false) {
                rhs.expected = lhs.actual;
            }
        }
    }

    public static void exit(ANode node, SymbolTable table, Map<String, Object> data) {
        EAssignment assignment = (EAssignment)node;
        Location location = assignment.location;
        Operation operation = assignment.operation;
        AStoreable lhs = (AStoreable) assignment.children.get(0);
        AExpression rhs = (AExpression)assignment.children.get(1);

        if (operation == null) {
            if (lhs.isDefOptimized()) {
                if (rhs.actual == void.class) {
                    throw assignment.createError(new IllegalArgumentException("right-hand side of an assignment cannot be [void]"));
                }

                rhs.expected = rhs.actual;
                lhs.updateActual(rhs.actual);
            }

            assignment.children.set(1, rhs.cast());
        } else {
            boolean shift = false;

            if (operation == Operation.MUL) {
                assignment.promote = AnalyzerCaster.promoteNumeric(lhs.actual, rhs.actual, true);
            } else if (operation == Operation.DIV) {
                assignment.promote = AnalyzerCaster.promoteNumeric(lhs.actual, rhs.actual, true);
            } else if (operation == Operation.REM) {
                assignment.promote = AnalyzerCaster.promoteNumeric(lhs.actual, rhs.actual, true);
            } else if (operation == Operation.ADD) {
                assignment.promote = AnalyzerCaster.promoteAdd(lhs.actual, rhs.actual);
            } else if (operation == Operation.SUB) {
                assignment.promote = AnalyzerCaster.promoteNumeric(lhs.actual, rhs.actual, true);
            } else if (operation == Operation.LSH) {
                assignment.promote = AnalyzerCaster.promoteNumeric(lhs.actual, false);
                assignment.shiftDistance = AnalyzerCaster.promoteNumeric(rhs.actual, false);
                shift = true;
            } else if (operation == Operation.RSH) {
                assignment.promote = AnalyzerCaster.promoteNumeric(lhs.actual, false);
                assignment.shiftDistance = AnalyzerCaster.promoteNumeric(rhs.actual, false);
                shift = true;
            } else if (operation == Operation.USH) {
                assignment.promote = AnalyzerCaster.promoteNumeric(lhs.actual, false);
                assignment.shiftDistance = AnalyzerCaster.promoteNumeric(rhs.actual, false);
                shift = true;
            } else if (operation == Operation.BWAND) {
                assignment.promote = AnalyzerCaster.promoteXor(lhs.actual, rhs.actual);
            } else if (operation == Operation.XOR) {
                assignment.promote = AnalyzerCaster.promoteXor(lhs.actual, rhs.actual);
            } else if (operation == Operation.BWOR) {
                assignment.promote = AnalyzerCaster.promoteXor(lhs.actual, rhs.actual);
            } else {
                throw assignment.createError(new IllegalStateException("illegal tree structure"));
            }

            if (assignment.promote == null || (shift && assignment.shiftDistance == null)) {
                throw assignment.createError(new ClassCastException("illegal compound assignment " +
                        "[" + operation.symbol + "=] for types [" + lhs.actual + "] and [" + rhs.actual + "]"));
            }

            assignment.cat = operation == Operation.ADD && assignment.promote == String.class;

            if (assignment.cat) {
                if (rhs instanceof EBinary && ((EBinary)rhs).operation == Operation.ADD && rhs.actual == String.class) {
                    ((EBinary)rhs).cat = true;
                }

                rhs.expected = rhs.actual;
            } else if (shift) {
                if (assignment.promote == def.class) {
                    // shifts are promoted independently, but for the def type, we need object.
                    rhs.expected = assignment.promote;
                } else if (assignment.shiftDistance == long.class) {
                    rhs.expected = int.class;
                    rhs.explicit = true;
                } else {
                    rhs.expected = assignment.shiftDistance;
                }
            } else {
                rhs.expected = assignment.promote;
            }

            assignment.children.set(1, rhs.cast());

            assignment.there = AnalyzerCaster.getLegalCast(location, lhs.actual, assignment.promote, false, false);
            assignment.back = AnalyzerCaster.getLegalCast(location, assignment.promote, lhs.actual, true, false);
        }

        assignment.statement = true;
        assignment.actual = assignment.read ? lhs.actual : void.class;
    }

    /**
     * Handles writing byte code for variable/method chains for all given possibilities
     * including String concatenation, compound assignment, regular assignment, and simple
     * reads.  Includes proper duplication for chained assignments and assignments that are
     * also read from.
     */
    @Override
    void write(MethodWriter writer, Globals globals) {
        writer.writeDebugInfo(location);

        // For the case where the assignment represents a String concatenation
        // we must, depending on the Java version, write a StringBuilder or
        // track types going onto the stack.  This must be done before the
        // lhs is read because we need the StringBuilder to be placed on the
        // stack ahead of any potential concatenation arguments.
        int catElementStackSize = 0;

        if (cat) {
            catElementStackSize = writer.writeNewStrings();
        }

        // Cast the lhs to a storeable to perform the necessary operations to store the rhs.
        AStoreable lhs = (AStoreable)children.get(0);
        AExpression rhs = (AExpression)children.get(1);
        lhs.setup(writer, globals); // call the setup method on the lhs to prepare for a load/store operation

        if (cat) {
            // Handle the case where we are doing a compound assignment
            // representing a String concatenation.

            writer.writeDup(lhs.accessElementCount(), catElementStackSize); // dup the top element and insert it
                                                                            // before concat helper on stack
            lhs.load(writer, globals);                                      // read the current lhs's value
            writer.writeAppendStrings(lhs.actual);  // append the lhs's value using the StringBuilder

            rhs.write(writer, globals); // write the bytecode for the rhs

            if (!(rhs instanceof EBinary) || !((EBinary)rhs).cat) {            // check to see if the rhs has already done a concatenation
                writer.writeAppendStrings(rhs.actual); // append the rhs's value since it's hasn't already
            }

            writer.writeToStrings(); // put the value for string concat onto the stack
            writer.writeCast(back);  // if necessary, cast the String to the lhs actual type

            if (lhs.read) {
                writer.writeDup(MethodWriter.getType(lhs.actual).getSize(), lhs.accessElementCount()); // if this lhs is also read
                                                                                                       // from dup the value onto the stack
            }

            lhs.store(writer, globals); // store the lhs's value from the stack in its respective variable/field/array
        } else if (operation != null) {
            // Handle the case where we are doing a compound assignment that
            // does not represent a String concatenation.

            writer.writeDup(lhs.accessElementCount(), 0); // if necessary, dup the previous lhs's value
                                                          // to be both loaded from and stored to
            lhs.load(writer, globals);                    // load the current lhs's value

            if (lhs.read && post) {
                writer.writeDup(MethodWriter.getType(lhs.actual).getSize(), lhs.accessElementCount()); // dup the value if the lhs is also
                                                                                 // read from and is a post increment
            }

            writer.writeCast(there);    // if necessary cast the current lhs's value
                                        // to the promotion type between the lhs and rhs types
            rhs.write(writer, globals); // write the bytecode for the rhs

        // XXX: fix these types, but first we need def compound assignment tests.
        // its tricky here as there are possibly explicit casts, too.
        // write the operation instruction for compound assignment
            if (promote == def.class) {
                writer.writeDynamicBinaryInstruction(
                    location, promote, def.class, def.class, operation, DefBootstrap.OPERATOR_COMPOUND_ASSIGNMENT);
            } else {
                writer.writeBinaryInstruction(location, promote, operation);
            }

            writer.writeCast(back); // if necessary cast the promotion type value back to the lhs's type

            if (lhs.read && !post) {
                writer.writeDup(MethodWriter.getType(lhs.actual).getSize(), lhs.accessElementCount()); // dup the value if the lhs is also
                                                                                                       // read from and is not a post
                                                                                                       // increment
            }

            lhs.store(writer, globals); // store the lhs's value from the stack in its respective variable/field/array
        } else {
            // Handle the case for a simple write.

            rhs.write(writer, globals); // write the bytecode for the rhs rhs

            if (lhs.read) {
                writer.writeDup(MethodWriter.getType(lhs.actual).getSize(), lhs.accessElementCount()); // dup the value if the lhs
                                                                                                       // is also read from
            }

            lhs.store(writer, globals); // store the lhs's value from the stack in its respective variable/field/array
        }
    }

    @Override
    public String toString() {
        List<Object> subs = new ArrayList<>();
        subs.add(children.get(0));
        if (children.get(1) != null) {
            // Make sure "=" is in the symbol so this is easy to read at a glance
            subs.add(operation == null ? "=" : operation.symbol + "=");
            subs.add(children.get(1));
            return singleLineToString(subs);
        }
        subs.add(operation.symbol);
        if (pre) {
            subs.add("pre");
        }
        if (post) {
            subs.add("post");
        }
        return singleLineToString(subs);
    }
}
