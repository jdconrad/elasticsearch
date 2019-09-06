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
import org.elasticsearch.painless.builder.ScopeTable;
import org.elasticsearch.painless.builder.SymbolTable;
import org.objectweb.asm.Type;

import java.util.Map;
import java.util.Objects;

/**
 * Represents a function reference.
 */
public final class ENewArrayFunctionRef extends AExpression implements ILambda {
    private final String type;

    private FunctionRef ref;
    private String defPointer;

    public ENewArrayFunctionRef(Location location, String type) {
        super(location);

        this.type = Objects.requireNonNull(type);
    }

    @Override
    void storeSettings(CompilerSettings settings) {

    }

    public static void enter(ANode node, SymbolTable table, Map<String, Object> data) {
        ENewArrayFunctionRef nafr = (ENewArrayFunctionRef)node;
        Class<?> type = table.painlessLookup.canonicalTypeNameToType(nafr.type);

        if (type == null) {
            throw nafr.createError(new IllegalArgumentException("cannot resolve symbol [" + nafr.type + "]"));
        }

        SFunction function = new SFunction(nafr.location, table.functionTable.getNextSyntheticName(), true, false, true, true);
        function.children.add(new DTypeClass(nafr.location, type));
        SDeclBlock parameters = new SDeclBlock(nafr.location);
        SDeclaration parameter = new SDeclaration(nafr.location, "size", false);
        parameter.children.add(new DTypeClass(nafr.location, int.class));
        parameters.children.add(parameter);
        function.children.add(parameters);
        function.children.add(null);
        EVariable size = new EVariable(nafr.location, "size");
        ENewArray array = new ENewArray(nafr.location, false);
        array.children.add(new DTypeClass(nafr.location, type));
        array.children.add(size);
        SReturn rtn = new SReturn(nafr.location);
        rtn.children.add(array);
        SBlock block = new SBlock(nafr.location);
        block.children.add(rtn);
        function.children.add(block);
        nafr.children.add(function);
        function.storeSettings(table.compilerSettings);
        ScopeTable.FunctionScope functionScope = table.scopeTable.newFunctionScope(function);
        functionScope.addVariable("size", true);
        functionScope.updateVariable("size", int.class);

        if (nafr.expected == null) {
            nafr.ref = null;
            nafr.actual = String.class;
            nafr.defPointer = "Sthis." + function.name + ",0";
        } else {
            nafr.defPointer = null;
            nafr.ref =
                    FunctionRef.create(table.painlessLookup, table.functionTable, nafr.location, nafr.expected, "this", function.name, 0);
            nafr.actual = nafr.expected;
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
