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

public class SymbolTable {

    public static class LocalFunction {

        public static String buildKey(String name, int arity) {
            return name + "/" + arity;
        }

        public final String name;
        public final Class<?> returnType;
        public final List<Class<?>> typeParameters;

        public final MethodType methodType;

        public LocalFunction(String name, Class<?> returnType, List<Class<?>> typeParameters) {
            this.name = name;
            this.returnType = returnType;
            this.typeParameters = typeParameters;

            this.methodType = MethodType.methodType(PainlessLookupUtility.typeToJavaType(returnType), typeParameters);
        }
    }

    protected Map<String, LocalFunction> localFunctions = new HashMap<>();

    public LocalFunction addLocalFunction(String name, Class<?> returnType, List<Class<?>> typeParameters) {
        return localFunctions.put(
                LocalFunction.buildKey(name, typeParameters.size()),
                new LocalFunction(name, returnType, typeParameters));
    }

    public LocalFunction getLocalFunction(String key) {
        return localFunctions.get(key);
    }
}
