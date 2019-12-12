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

import org.elasticsearch.painless.Location;
import org.elasticsearch.painless.lookup.PainlessLookup;
import org.elasticsearch.painless.lookup.PainlessLookupUtility;
import org.elasticsearch.painless.lookup.PainlessMethod;

import java.util.Objects;

/**
 * Represents a canonical Painless type name as a {@link String}
 * that requires resolution.
 */
public class DUnresolvedMethod extends DMethod {

    protected final String methodName;
    protected final int methodArity;

    public DUnresolvedMethod(Location location, String methodName, int methodArity) {
        super(location);
        this.methodName = Objects.requireNonNull(methodName);
        this.methodArity = methodArity;
    }

    /**
     * Resolves the {@link #methodName} and {@link #methodArity} to a {@link PainlessMethod}.
     * @throws IllegalArgumentException if the {@link PainlessMethod} cannot be resolved from
     * the {@link PainlessLookup}
     * @return a {@link DResolvedMethod} where the resolved {@link PainlessMethod} is retrievable
     */
    @Override
    public DResolvedMethod resolveMethod(PainlessLookup painlessLookup, Class<?> targetClass, boolean isStatic) {
        PainlessMethod method = painlessLookup.lookupPainlessMethod(targetClass, isStatic, methodName, methodArity);

        if (type == null) {
            throw location.createError(new IllegalArgumentException("cannot resolve type [" + typeName + "]"));
        }

        return new DResolvedType(location, type);
    }

    @Override
    public String toString() {
        return "(DUnresolvedType [" + PainlessLookupUtility.buildPainlessMethodKey(methodName, methodArity) + "])";
    }
}

