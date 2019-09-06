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
import org.elasticsearch.painless.DefBootstrap;
import org.elasticsearch.painless.FunctionRef;
import org.elasticsearch.painless.Globals;
import org.elasticsearch.painless.Location;
import org.elasticsearch.painless.MethodWriter;
import org.elasticsearch.painless.builder.ScopeTable;
import org.elasticsearch.painless.builder.SymbolTable;
import org.elasticsearch.painless.lookup.PainlessLookupUtility;
import org.elasticsearch.painless.lookup.def;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.Map;
import java.util.Objects;

/**
 * Represents a capturing function reference.
 */
public final class ECapturingFunctionRef extends AExpression implements ILambda {
    private final String variable;
    private final String call;

    private FunctionRef ref;
    private ScopeTable.Variable captured;
    private String defPointer;

    public ECapturingFunctionRef(Location location, String variable, String call) {
        super(location);

        this.variable = Objects.requireNonNull(variable);
        this.call = Objects.requireNonNull(call);
    }

    @Override
    void storeSettings(CompilerSettings settings) {
        // Do nothing.
    }

    public static void exit(ANode node, SymbolTable table, Map<String, Object> data) {
        ECapturingFunctionRef cfr = (ECapturingFunctionRef)node;

        cfr.captured = table.scopeTable.getNodeScope(cfr).getVariable(cfr.variable);
        if (cfr.expected == null) {
            if (cfr.captured.getType() == def.class) {
                // dynamic implementation
                cfr.defPointer = "D" + cfr.variable + "." + cfr.call + ",1";
            } else {
                // typed implementation
                cfr.defPointer = "S" + PainlessLookupUtility.typeToCanonicalTypeName(cfr.captured.getType()) + "." + cfr.call + ",1";
            }
            cfr.actual = String.class;
        } else {
            cfr.defPointer = null;
            // static case
            if (cfr.captured.getType() != def.class) {
                cfr.ref = FunctionRef.create(table.painlessLookup, table.functionTable, cfr.location,
                        cfr.expected, PainlessLookupUtility.typeToCanonicalTypeName(cfr.captured.getType()), cfr.call, 1);
            }
            cfr.actual = cfr.expected;
        }
    }

    @Override
    void write(MethodWriter writer, Globals globals) {
        writer.writeDebugInfo(location);
        if (defPointer != null) {
            // dynamic interface: push captured parameter on stack
            // TODO: don't do this: its just to cutover :)
            writer.push((String)null);
            writer.visitVarInsn(MethodWriter.getType(captured.getType()).getOpcode(Opcodes.ILOAD), captured.getSlot());
        } else if (ref == null) {
            // typed interface, dynamic implementation
            writer.visitVarInsn(MethodWriter.getType(captured.getType()).getOpcode(Opcodes.ILOAD), captured.getSlot());
            Type methodType = Type.getMethodType(MethodWriter.getType(expected), MethodWriter.getType(captured.getType()));
            writer.invokeDefCall(call, methodType, DefBootstrap.REFERENCE, PainlessLookupUtility.typeToCanonicalTypeName(expected));
        } else {
            // typed interface, typed implementation
            writer.visitVarInsn(MethodWriter.getType(captured.getType()).getOpcode(Opcodes.ILOAD), captured.getSlot());
            writer.invokeLambdaCall(ref);
        }
    }

    @Override
    public String getPointer() {
        return defPointer;
    }

    @Override
    public Type[] getCaptures() {
        return new Type[] { MethodWriter.getType(captured.getType()) };
    }

    @Override
    public String toString() {
        return singleLineToString(variable, call);
    }
}
