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
import org.elasticsearch.painless.FunctionRef;
import org.elasticsearch.painless.Globals;
import org.elasticsearch.painless.Locals;
import org.elasticsearch.painless.Location;
import org.elasticsearch.painless.MethodWriter;
import org.objectweb.asm.Type;

import java.util.Objects;
import java.util.Set;

/**
 * Represents a function reference.
 */
public final class ENewArrayFunctionRef extends AExpression implements ILambda {
    private final String type;

    private CompilerSettings settings;

    private FunctionRef ref;
    private String defPointer;

    public ENewArrayFunctionRef(Location location, String type) {
        super(location);

        this.type = Objects.requireNonNull(type);
    }

    @Override
    void storeSettings(CompilerSettings settings) {
        this.settings = settings;
    }

    @Override
    void extractVariables(Set<String> variables) {
        // do nothing
    }

    @Override
    void analyze(Locals locals) {
        Class<?> type = locals.getPainlessLookup().canonicalTypeNameToType(this.type);

        if (type == null) {
            throw createError(new IllegalArgumentException("cannot resolve symbol [" + this.type + "]"));
        }

        SFunction function = new SFunction(location, locals.getNextSyntheticName(), true);
        function.children.add(new DTypeClass(location, type));
        DParameters parameters = new DParameters(location);
        DParameter parameter = new DParameter(location, "size");
        parameter.children.add(new DTypeClass(location, int.class));
        parameters.children.add(parameter);
        function.children.add(parameters);
        EVariable size = new EVariable(location, "size");
        ENewArray array = new ENewArray(location, false);
        array.children.add(new DTypeClass(location, type));
        array.children.add(size);
        SReturn rtn = new SReturn(location);
        rtn.children.add(array);
        SBlock block = new SBlock(location);
        block.children.add(rtn);
        function.children.add(block);
        children.add(function);
        function.storeSettings(settings);
        function.generateSignature();
        //function.extractVariables(null);
        function.analyze(Locals.newLambdaScope(locals.getProgramScope(), function.name, ((DTypeClass)function.children.get(0)).type,
                (DParameters)function.children.get(1), 0, settings.getMaxLoopCounter()));
        children.set(0, function);

        if (expected == null) {
            ref = null;
            actual = String.class;
            defPointer = "Sthis." + function.name + ",0";
        } else {
            defPointer = null;
            ref = FunctionRef.create(locals.getPainlessLookup(), locals.getMethods(), location, expected, "this", function.name, 0);
            actual = expected;
        }
    }

    @Override
    void write(MethodWriter writer, Globals globals) {
        if (ref != null) {
            writer.writeDebugInfo(location);
            writer.invokeLambdaCall(ref);
        } else {
            // push a null instruction as a placeholder for future lambda instructions
            writer.push((String)null);
        }

        SFunction function = (SFunction)children.get(0);
        function.write(globals.visitor, globals);
    }

    @Override
    public String getPointer() {
        return defPointer;
    }

    @Override
    public Type[] getCaptures() {
        return new Type[0]; // no captures
    }

    @Override
    public String toString() {
        return singleLineToString(type + "[]", "new");
    }
}
