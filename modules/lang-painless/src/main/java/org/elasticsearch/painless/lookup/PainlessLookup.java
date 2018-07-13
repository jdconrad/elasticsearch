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

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import static org.elasticsearch.painless.lookup.PainlessLookupUtility.CONSTRUCTOR_ANY_NAME;
import static org.elasticsearch.painless.lookup.PainlessLookupUtility.anyTypeToPainlessTypeName;
import static org.elasticsearch.painless.lookup.PainlessLookupUtility.buildPainlessMethodKey;

public final class PainlessLookup {

    private final Map<String, Class<?>> painlessClassNamesToJavaClasses;
    private final Map<Class<?>, PainlessClass> javaClassesToPainlessClasses;

    PainlessLookup(Map<String, Class<?>> painlessClassNamesToJavaClasses, Map<Class<?>, PainlessClass> javaClassesToPainlessClasses) {
        Objects.requireNonNull(painlessClassNamesToJavaClasses);
        Objects.requireNonNull(javaClassesToPainlessClasses);

        this.painlessClassNamesToJavaClasses = Collections.unmodifiableMap(painlessClassNamesToJavaClasses);
        this.javaClassesToPainlessClasses = Collections.unmodifiableMap(javaClassesToPainlessClasses);
    }

    public Collection<PainlessClass> getPainlessClasses() {
        return javaClassesToPainlessClasses.values();
    }

    public PainlessClass getPainlessClassFromJavaClass(Class<?> javaClass) {
        return javaClassesToPainlessClasses.get(javaClass);
    }

    public Class<?> painlessTypeNameToPainlessType(String painlessTypeName) {
        return PainlessLookupUtility.painlessTypeNameToPainlessType(painlessTypeName, painlessClassNamesToJavaClasses);
    }

    public void validatePainlessType(Class<?> painlessType) {
        PainlessLookupUtility.validatePainlessType(painlessType, javaClassesToPainlessClasses.keySet());
    }

    public boolean isSimplePainlessType(String painlessType) {
        return painlessClassNamesToJavaClasses.containsKey(painlessType);
    }

    public PainlessMethod lookupMethod(Class<?> painlessType, String methodName, int methodArity) {
        String painlessMethodKey = buildPainlessMethodKey(methodName, methodArity);

        if (painlessType.isArray()) {
            throw new IllegalArgumentException("painless method [" + painlessMethodKey + "] " +
                    "cannot be called on a painless type array  [" + anyTypeToPainlessTypeName(painlessType) + "]");
        }

        PainlessClass painlessClass = javaClassesToPainlessClasses.get(painlessType);

        if (painlessClass == null) {
            throw new IllegalArgumentException("painless class [" + anyTypeToPainlessTypeName(painlessType) + "] not found");
        }

        PainlessMethod painlessMethod = painlessClass.methods.get(painlessMethodKey);

        if (painlessMethod == null) {
            throw new IllegalArgumentException("painless method [" + painlessMethodKey + "] not found " +
                    "for painless class [" + anyTypeToPainlessTypeName(painlessType) + "]");
        }

        return painlessMethod;
    }
}
