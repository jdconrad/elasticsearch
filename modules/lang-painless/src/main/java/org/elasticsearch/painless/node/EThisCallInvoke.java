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
import org.elasticsearch.painless.lookup.def;
import org.objectweb.asm.commons.Method;

import java.util.Set;

import static org.elasticsearch.painless.WriterConstants.CLASS_TYPE;

// TODO: modify this to search for the method on the local class
public class EThisCallInvoke extends AExpression {

    public final String name;

    // TODO: remove these later
    private final Class<?> type;
    private final Method method;

    public EThisCallInvoke(Location location, String name, Class<?> type, Method method) {
        super(location);

        this.name = name;

        this.type = type;
        this.method = method;
    }

    @Override
    void storeSettings(CompilerSettings settings) {
        for (ANode child : children) {
            child.storeSettings(settings);
        }
    }

    @Override
    void extractVariables(Set<String> variables) {

    }

    @Override
    void analyze(Locals locals) {
        for (int argument = 0; argument < children.size(); ++argument) {
            AExpression expression = (AExpression)children.get(argument);

            Class<?> parameterType =
                    locals.getPainlessLookup().canonicalTypeNameToType(method.getArgumentTypes()[argument].getInternalName());

            if (parameterType == Object.class) {
                parameterType = def.class;
            }

            expression.expected = parameterType;
            expression.internal = true;
            expression.analyze(locals);
            children.set(argument, expression.cast(locals));
        }

        statement = true;
        actual = type;
    }

    @Override
    void write(MethodWriter writer, Globals globals) {
        writer.writeDebugInfo(location);

        writer.loadThis();

        for (ANode argument : children) {
            argument.write(writer, globals);
        }

        writer.invokeVirtual(CLASS_TYPE, method);
    }

    @Override
    public String toString() {
        return null;
    }
}
