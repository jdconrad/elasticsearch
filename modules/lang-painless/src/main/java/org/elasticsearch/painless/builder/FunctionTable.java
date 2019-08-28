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

package org.elasticsearch.painless.builder;

import org.elasticsearch.painless.lookup.PainlessLookupUtility;

import java.lang.invoke.MethodType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FunctionTable {

    public static class LocalFunction {

        public final String name;
        public final Class<?> returnType;
        public final List<Class<?>> typeParameters;
        public final List<String> parameterNames;

        public final MethodType methodType;
        public final org.objectweb.asm.commons.Method asmMethod;

        public LocalFunction(String name, Class<?> returnType, List<Class<?>> typeParameters, List<String> parameterNames) {
            this.name = name;
            this.returnType = returnType;
            this.typeParameters = typeParameters;
            this.parameterNames = parameterNames;

            Class<?> javaReturnType = PainlessLookupUtility.typeToJavaType(returnType);
            Class<?>[] javaTypeParameters = typeParameters.stream().map(PainlessLookupUtility::typeToJavaType).toArray(Class<?>[]::new);

            this.methodType = MethodType.methodType(javaReturnType, typeParameters);
            this.asmMethod = new org.objectweb.asm.commons.Method(name,
                    MethodType.methodType(javaReturnType, javaTypeParameters).toMethodDescriptorString());
        }
    }

    public static String buildKey(String name, int arity) {
        return name + "/" + arity;
    }

    protected final Map<String, LocalFunction> localFunctions = new HashMap<>();

    public LocalFunction add(String name, Class<?> returnType, List<Class<?>> typeParameters, List<String> parameterNames) {
        LocalFunction localFunction = new LocalFunction(name, returnType, typeParameters, parameterNames);
        localFunctions.put(buildKey(name, typeParameters.size()), localFunction);
        return localFunction;
    }

    public LocalFunction get(String key) {
        return localFunctions.get(key);
    }
}