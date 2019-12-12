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

import java.lang.reflect.Modifier;
import java.util.Objects;

/**
 * Represents a Painless type as a {@link Class}. This may still
 * require resolution to ensure the type in the {@link PainlessLookup}.
 */
public class DResolvedMethod extends DMethod {

    protected final PainlessMethod painlessMethod;

    /**
     * If set to {@code true} ensures the {@link PainlessMethod} is in the {@link PainlessLookup}.
     * If set to {@code false} assumes the {@link PainlessMethod} is valid.
     */
    protected final boolean checkInLookup;

    public DResolvedMethod(Location location, PainlessMethod painlessMethod) {
        this(location, painlessMethod, true);
    }

    public DResolvedMethod(Location location, PainlessMethod painlessMethod, boolean checkInLookup) {
        super(location);
        this.painlessMethod = Objects.requireNonNull(painlessMethod);
        this.checkInLookup = checkInLookup;
    }

    /**
     * If {@link #checkInLookup} is {@code true} checks if the {@link PainlessMethod} is in the
     * {@link PainlessLookup}, otherwise returns {@code this}.
     * @throws IllegalArgumentException if checking the {@link PainlessMethod} is in the
     * {@link PainlessLookup} and the type cannot be resolved from the {@link PainlessLookup}
     * @return a {@link DResolvedType} where the resolved Painless type is retrievable
     */
    @Override
    public DResolvedMethod resolveMethod(PainlessLookup painlessLookup, Class<?> targetClass, boolean isStatic) {
        if (checkInLookup == false) {
            return this;
        }

        if (painlessMethod.targetClass != targetClass || Modifier.isStatic(painlessMethod.javaMethod.getModifiers()) != isStatic) {
            throw location.createError(new IllegalArgumentException(
                    "cannot resolve method [" + PainlessLookupUtility.buildPainlessMethodKey(
                            painlessMethod.javaMethod.getName(), painlessMethod.typeParameters.size()) + "]"));
        }

        if (painlessLookup.lookupPainlessMethod(painlessMethod.targetClass, Modifier.isStatic(painlessMethod.javaMethod.getModifiers()),
                painlessMethod.javaMethod.getName(), painlessMethod.typeParameters.size()) == null) {
            throw location.createError(new IllegalArgumentException(
                    "cannot resolve method [" + PainlessLookupUtility.buildPainlessMethodKey(
                            painlessMethod.javaMethod.getName(), painlessMethod.typeParameters.size()) + "]"));
        }

        return new DResolvedMethod(location, painlessMethod, false);
    }

    public PainlessMethod getMethod() {
        return painlessMethod;
    }

    @Override
    public String toString() {
        return "(DResolvedType [" + PainlessLookupUtility.buildPainlessMethodKey(
                painlessMethod.javaMethod.getName(), painlessMethod.typeParameters.size()) + "])";
    }
}
