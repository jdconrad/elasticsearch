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
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

import static org.elasticsearch.painless.WriterConstants.CLASS_TYPE;

public class EDirectCallInvoke extends AExpression {

    private final Method method;
    private final boolean isInterface;
    private final boolean isStatic;

    public EDirectCallInvoke(Location location, Method method, boolean isInterface, boolean isStatic) {
        super(location);

        this.method = method;
        this.isInterface = isInterface;
        this.isStatic = isStatic;
    }

    @Override
    void analyze(SymbolTable table) {
        AExpression prefix = (AExpression)children.get(0);

        prefix.analyze(table);
        prefix.expected = prefix.actual;
        children.set(0, prefix.cast(table));

        for (int argument = 1; argument < children.size(); ++argument) {
            AExpression expression = (AExpression)children.get(argument);

            Type type = method.getArgumentTypes()[argument - 1];
            Class<?> parameterType = table.lookup().canonicalTypeNameToType(type.getClassName().replace('$', '.'));

            if (parameterType == null) {
                try {
                    parameterType = Class.forName(type.getInternalName().replace('/', '.'));
                } catch (ClassNotFoundException cnfe) {
                    throw new IllegalStateException(cnfe);
                }
            }

            if (parameterType == Object.class) {
                parameterType = def.class;
            }

            expression.expected = parameterType;
            expression.internal = true;
            expression.analyze(table);
            children.set(argument, expression.cast(table));
        }

        statement = true;

        Class<?> returnType = table.lookup().canonicalTypeNameToType(method.getReturnType().getClassName().replace('$', '.'));

        if (returnType == null) {
            try {
                returnType = Class.forName(method.getReturnType().getInternalName().replace('/', '.'));
            } catch (ClassNotFoundException cnfe) {
                throw new IllegalStateException(cnfe);
            }
        }

        if (returnType == Object.class) {
            returnType = def.class;
        }

        actual = returnType;
    }

    @Override
    void write(MethodWriter writer, Globals globals) {
        writer.writeDebugInfo(location);

        for (ANode child : children) {
            child.write(writer, globals);
        }

        AExpression prefix = (AExpression)children.get(0);
        Type owner = prefix.actual == $this.class ? CLASS_TYPE : Type.getType(prefix.actual);

       if (isStatic) {
            writer.visitMethodInsn(Opcodes.INVOKESTATIC, owner.getInternalName(), method.getName(), method.getDescriptor(), isInterface);
       } else {
           if (isInterface) {
               writer.invokeInterface(owner, method);
           } else {
               writer.invokeVirtual(owner, method);
           }
       }
   }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder
                .append(getClass().getSimpleName())
                .append(" [")
                .append(method.getName())
                .append("] [")
                .append(method.getDescriptor())
                .append("]");

        for (ANode child : children) {
            builder.append(" (")
                    .append(child)
                    .append(")");
        }

        return builder.toString();
    }
}
