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
import org.elasticsearch.painless.lookup.$this;
import org.elasticsearch.painless.lookup.def;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

import java.util.Map;

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
    void storeSettings(CompilerSettings settings) {
        for (ANode child : children) {
            child.storeSettings(settings);
        }
    }

    public static void before(ANode node, ANode child, int index, SymbolTable table, Map<String, Object> data) {
        if (index > 0) {
            EDirectCallInvoke invoke = (EDirectCallInvoke)node;
            AExpression expression = (AExpression)node.children.get(index);

            Type type = invoke.method.getArgumentTypes()[index - 1];
            Class<?> parameterType = table.painlessLookup.canonicalTypeNameToType(type.getClassName().replace('$', '.'));

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
        }
    }

    public static void after(ANode node, ANode child, int index, SymbolTable table, Map<String, Object> data) {
        if (index == 0) {
            AExpression prefix = (AExpression)node.children.get(0);
            prefix.expected = prefix.actual;
            node.children.set(0, prefix.cast());
        } else {
            AExpression expression = (AExpression)node.children.get(index);
            node.children.set(index, expression.cast());
        }
    }

    public static void exit(ANode node, SymbolTable table, Map<String, Object> data) {
        EDirectCallInvoke invoke = (EDirectCallInvoke)node;

        Class<?> returnType = table.painlessLookup.canonicalTypeNameToType(invoke.method.getReturnType().getClassName().replace('$', '.'));

        if (returnType == null) {
            try {
                returnType = Class.forName(invoke.method.getReturnType().getInternalName().replace('/', '.'));
            } catch (ClassNotFoundException cnfe) {
                throw new IllegalStateException(cnfe);
            }
        }

        if (returnType == Object.class) {
            returnType = def.class;
        }

        invoke.actual = returnType;
        invoke.statement = true;
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
        return null;
    }
}
