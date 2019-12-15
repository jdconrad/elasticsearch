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

package org.elasticsearch.painless.ir;

import org.elasticsearch.painless.ClassWriter;
import org.elasticsearch.painless.DefBootstrap;
import org.elasticsearch.painless.FunctionRef;
import org.elasticsearch.painless.Globals;
import org.elasticsearch.painless.Locals.Variable;
import org.elasticsearch.painless.Location;
import org.elasticsearch.painless.MethodWriter;
import org.elasticsearch.painless.lookup.PainlessLookupUtility;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.Objects;

public class ECapturingFunctionRef extends ExpressionNode {

    protected final String name;
    protected final FunctionRef functionRef;
    protected final Variable captured;
    protected final String pointer;

    public ECapturingFunctionRef(Location location, FunctionRef functionRef, ) {
        super(location);

        this.variable = Objects.requireNonNull(variable);
        this.call = Objects.requireNonNull(call);
    }

    @Override
    public void write(ClassWriter classWriter, MethodWriter methodWriter, Globals globals) {
        methodWriter.writeDebugInfo(location);
        if (defPointer != null) {
            // dynamic interface: push placeholder onto stack
            methodWriter.push((String)null);
            methodWriter.visitVarInsn(MethodWriter.getType(captured.clazz).getOpcode(Opcodes.ILOAD), captured.getSlot());
        } else if (ref == null) {
            // typed interface, dynamic implementation
            methodWriter.visitVarInsn(MethodWriter.getType(captured.clazz).getOpcode(Opcodes.ILOAD), captured.getSlot());
            Type methodType = Type.getMethodType(MethodWriter.getType(expected), MethodWriter.getType(captured.clazz));
            methodWriter.invokeDefCall(call, methodType, DefBootstrap.REFERENCE, PainlessLookupUtility.typeToCanonicalTypeName(expected));
        } else {
            // typed interface, typed implementation
            methodWriter.visitVarInsn(MethodWriter.getType(captured.clazz).getOpcode(Opcodes.ILOAD), captured.getSlot());
            methodWriter.invokeLambdaCall(ref);
        }
    }
}
