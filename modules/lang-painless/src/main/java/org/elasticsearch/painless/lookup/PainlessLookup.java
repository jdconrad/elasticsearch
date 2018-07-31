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

package org.elasticsearch.painless.lookup;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.elasticsearch.painless.lookup.PainlessLookupUtility.buildPainlessMethodKey;
import static org.elasticsearch.painless.lookup.PainlessLookupUtility.typeToCanonicalTypeName;

public final class PainlessLookup {

    private final Map<String, Class<?>> canonicalClassNamesToClasses;
    private final Map<Class<?>, PainlessClass> classesToPainlessClasses;

    private final List<Class<?>> sortedClassesByCanonicalClassName;

    PainlessLookup(Map<String, Class<?>> canonicalClassNamesToClasses, Map<Class<?>, PainlessClass> classesToPainlessClasses) {
        Objects.requireNonNull(canonicalClassNamesToClasses);
        Objects.requireNonNull(classesToPainlessClasses);

        this.canonicalClassNamesToClasses = Collections.unmodifiableMap(canonicalClassNamesToClasses);
        this.classesToPainlessClasses = Collections.unmodifiableMap(classesToPainlessClasses);

        this.sortedClassesByCanonicalClassName = Collections.unmodifiableList(
                classesToPainlessClasses.keySet().stream().sorted(
                        Comparator.comparing(Class::getCanonicalName)
                ).collect(Collectors.toList())
        );
    }

    public List<Class<?>> getSortedClassesByCanonicalClassName() {
        return sortedClassesByCanonicalClassName;
    }

    public boolean isValidCanonicalClassName(String canonicalClassName) {
        Objects.requireNonNull(canonicalClassName);

        return canonicalClassNamesToClasses.containsKey(canonicalClassName);
    }

    public Class<?> canonicalTypeNameToType(String painlessType) {
        Objects.requireNonNull(painlessType);

        return PainlessLookupUtility.canonicalTypeNameToType(painlessType, canonicalClassNamesToClasses);
    }

    public PainlessMethod lookupPainlessMethod(Class<?> targetClass, boolean isStatic, String methodName, int methodArity) {
        Objects.requireNonNull(targetClass);
        Objects.requireNonNull(methodName);

        if (targetClass.isPrimitive()) {
            targetClass = PainlessLookupUtility.typeToBoxedType(targetClass);
        }

        PainlessClass targetPainlessClass = classesToPainlessClasses.get(targetClass);
        String painlessMethodKey = buildPainlessMethodKey(methodName, methodArity);

        if (targetPainlessClass == null) {
            throw new IllegalArgumentException(
                    "target class [" + typeToCanonicalTypeName(targetClass) + "] not found for method [" + painlessMethodKey + "]");
        }

        PainlessMethod painlessMethod = isStatic ?
                targetPainlessClass.staticMethods.get(painlessMethodKey) :
                targetPainlessClass.methods.get(painlessMethodKey);

        if (painlessMethod == null) {
            throw new IllegalArgumentException(
                    "method [" + typeToCanonicalTypeName(targetClass) + ", " + painlessMethodKey + "] not found");
        }

        return painlessMethod;
    }

    public PainlessClass getPainlessStructFromJavaClass(Class<?> clazz) {
        return classesToPainlessClasses.get(clazz);
    }
}
