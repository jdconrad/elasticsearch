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
import org.elasticsearch.painless.Location;
import org.elasticsearch.painless.MethodWriter;
import org.elasticsearch.painless.lookup.PainlessField;
import org.elasticsearch.painless.symbol.ScopeTable;
import org.objectweb.asm.Type;

public class StoreDotSubNode extends StoreNode {

    /* ---- begin tree structure ---- */

    @Override
    public StoreDotSubNode setStoreNode(ExpressionNode storeNode) {
        this.storeNode = storeNode;
        return this;
    }

    @Override
    public StoreDotSubNode setTypeNode(TypeNode typeNode) {
        super.setTypeNode(typeNode);
        return this;
    }

    /* ---- begin node data ---- */

    protected PainlessField field;

    public StoreDotSubNode setField(PainlessField field) {
        this.field = field;
        return this;
    }

    public PainlessField getField() {
        return field;
    }

    @Override
    public StoreDotSubNode setReadFrom(boolean isReadFrom) {
        super.setReadFrom(isReadFrom);
        return this;
    }

    @Override
    public StoreDotSubNode setLocation(Location location) {
        super.setLocation(location);
        return this;
    }

    /* ---- end node data ---- */
    
    public StoreDotSubNode() {
        // do nothing
    }

    @Override
    protected void write(ClassWriter classWriter, MethodWriter methodWriter, ScopeTable scopeTable) {
        if (isReadFrom()) {
            methodWriter.writeDup(MethodWriter.getType(getType()).getSize(), 1);
        }

        methodWriter.writeDebugInfo(location);

        if (java.lang.reflect.Modifier.isStatic(field.javaField.getModifiers())) {
            methodWriter.putStatic(Type.getType(
                    field.javaField.getDeclaringClass()), field.javaField.getName(), MethodWriter.getType(field.typeParameter));
        } else {
            methodWriter.putField(Type.getType(
                    field.javaField.getDeclaringClass()), field.javaField.getName(), MethodWriter.getType(field.typeParameter));
        }
    }
}
