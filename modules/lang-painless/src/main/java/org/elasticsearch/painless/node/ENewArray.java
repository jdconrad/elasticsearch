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
import org.elasticsearch.painless.Location;
import org.elasticsearch.painless.MethodWriter;
import org.elasticsearch.painless.builder.SymbolTable;
import org.elasticsearch.painless.lookup.def;

import java.util.Map;

/**
 * Represents an array instantiation.
 */
public final class ENewArray extends AExpression {

    private final boolean initialize;

    public ENewArray(Location location, boolean initialize) {
        super(location);

        this.initialize = initialize;
    }

    @Override
    void storeSettings(CompilerSettings settings) {
        for (ANode argument : children) {
            if (argument instanceof AData) {
                continue;
            }

            argument.storeSettings(settings);
        }
    }

    public static void enter(ANode node, SymbolTable table, Map<String, Object> data) {
        ENewArray array = (ENewArray) node;

        if (!array.read) {
             throw array.createError(new IllegalArgumentException("a newly initialized array must be read from"));
        }

        array.actual = ((DTypeClass)array.children.get(0)).type;
    }

    public static void before(ANode node, ANode child, int index, SymbolTable table, Map<String, Object> data) {
        if (index > 0) {
            ENewArray array = (ENewArray)node;
            AExpression expression = (AExpression) child;

            expression.expected = array.initialize ? array.actual.getComponentType() : int.class;
            expression.internal = true;
        }
    }

    public static void after(ANode node, ANode child, int index, SymbolTable table, Map<String, Object> data) {
        if (index > 0) {
            AExpression expression = (AExpression)child;
            node.children.set(index, expression.cast());
        }
    }

    @Override
    void write(MethodWriter writer, Globals globals) {
        writer.writeDebugInfo(location);

        if (initialize) {
            writer.push(children.size() - 1);
            writer.newArray(MethodWriter.getType(actual.getComponentType()));

            for (int index = 1; index < children.size(); ++index) {
                ANode argument = children.get(index);

                writer.dup();
                writer.push(index - 1);
                argument.write(writer, globals);
                writer.arrayStore(MethodWriter.getType(actual.getComponentType()));
            }
        } else {
            for (int index = 1; index < children.size(); ++index) {
                children.get(index).write(writer, globals);
            }

            if (children.size() > 2) {
                writer.visitMultiANewArrayInsn(MethodWriter.getType(actual).getDescriptor(), children.size() - 1);
            } else {
                writer.newArray(MethodWriter.getType(actual.getComponentType()));
            }
        }
    }

    @Override
    public String toString() {
        return null;
    }
}
