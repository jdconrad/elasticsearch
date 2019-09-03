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
import org.elasticsearch.painless.Globals;
import org.elasticsearch.painless.Locals;
import org.elasticsearch.painless.Locals.Variable;
import org.elasticsearch.painless.Location;
import org.elasticsearch.painless.MethodWriter;
import org.elasticsearch.painless.lookup.PainlessLookupUtility;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import java.util.Set;

/**
 * Represents a catch block as part of a try-catch block.
 */
public final class SCatch extends AStatement {

    private Variable variable = null;

    Label begin = null;
    Label end = null;
    Label exception = null;

    public SCatch(Location location) {
        super(location);
    }

    @Override
    void storeSettings(CompilerSettings settings) {
        children.get(1).storeSettings(settings);

        if (children.get(2) != null) {
            children.get(2).storeSettings(settings);
        }
    }

    @Override
    void extractVariables(Set<String> variables) {
        if (children.get(1) != null) {
            children.get(1).extractVariables(variables);
        }
    }

    @Override
    void analyze(Locals locals) {
        Class<?> exception = ((DTypeClass)children.get(0)).type;
        SDeclaration declaration = (SDeclaration)children.get(1);
        declaration.analyze(locals);

        variable = locals.getVariable(location, declaration.name);

        if (!exception.isAssignableFrom(variable.clazz)) {
            throw createError(new ClassCastException("base exception type " +
                    "[" + PainlessLookupUtility.typeToCanonicalTypeName(exception) + "], but found " +
                    "[" + PainlessLookupUtility.typeToCanonicalTypeName(variable.clazz) + "]"));
        }

        SBlock block = (SBlock)children.get(2);

        if (block != null) {
            block.lastSource = lastSource;
            block.inLoop = inLoop;
            block.lastLoop = lastLoop;

            block.analyze(locals);

            methodEscape = block.methodEscape;
            loopEscape = block.loopEscape;
            allEscape = block.allEscape;
            anyContinue = block.anyContinue;
            anyBreak = block.anyBreak;
            statementCount = block.statementCount;
        }
    }

    @Override
    void write(MethodWriter writer, Globals globals) {
        SBlock block = (SBlock)children.get(2);

        writer.writeStatementOffset(location);

        Label jump = new Label();

        writer.mark(jump);
        writer.visitVarInsn(MethodWriter.getType(variable.clazz).getOpcode(Opcodes.ISTORE), variable.getSlot());

        if (block != null) {
            block.continu = continu;
            block.brake = brake;
            block.write(writer, globals);
        }

        writer.visitTryCatchBlock(begin, end, jump, MethodWriter.getType(variable.clazz).getInternalName());

        if (exception != null && (block == null || !block.allEscape)) {
            writer.goTo(exception);
        }
    }

    @Override
    public String toString() {
        return null;
    }
}
