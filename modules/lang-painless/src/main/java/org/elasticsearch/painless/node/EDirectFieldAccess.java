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

import org.elasticsearch.painless.Globals;
import org.elasticsearch.painless.Location;
import org.elasticsearch.painless.MethodWriter;
import org.elasticsearch.painless.builder.SymbolTable;
import org.elasticsearch.painless.lookup.$this;
import org.elasticsearch.painless.lookup.def;
import org.objectweb.asm.Type;

import static org.elasticsearch.painless.WriterConstants.CLASS_TYPE;

public class EDirectFieldAccess extends AExpression {

    private final Type type;
    private final String name;
    private final boolean isStatic;

    public EDirectFieldAccess(Location location, Type type, String name, boolean isStatic) {
        super(location);

        this.type = type;
        this.name = name;
        this.isStatic = isStatic;
    }

    @Override
    void analyze(SymbolTable table) {
        AExpression prefix = (AExpression)children.get(0);

        prefix.analyze(table);
        prefix.expected = prefix.actual;
        children.set(0, prefix.cast(table));

        Class<?> fieldType = table.lookup().canonicalTypeNameToType(type.getClassName().replace('$', '.'));

        if (fieldType == null) {
            try {
                fieldType = Class.forName(type.getInternalName().replace('/', '.'));
            } catch (ClassNotFoundException cnfe) {
                throw new IllegalStateException(cnfe);
            }
        }

        if (fieldType == Object.class) {
            fieldType = def.class;
        }

        actual = fieldType;
    }

    @Override
    void write(MethodWriter writer, Globals globals) {
        writer.writeDebugInfo(location);

        AExpression prefix = (AExpression)children.get(0);
        Type owner = prefix.actual == $this.class ? CLASS_TYPE : Type.getType(prefix.actual);

        if (isStatic) {
            writer.getStatic(owner, name, type);
        } else {
            writer.getField(owner, name, type);
        }
    }

    @Override
    public String toString() {
        return null;
    }
}
