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
import org.elasticsearch.painless.lookup.PainlessMethod;

import java.util.Objects;

/**
 * Represents an abstract Painless method. {@link DMethod} nodes must be
 * resolved using {@link #resolveMethod(PainlessLookup, Class, boolean)} to
 * retrieve the actual {@link PainlessMethod}. {@link DMethod} exists as a
 * base class so consumers may have either a {@link DUnresolvedMethod}
 * representing an abstract Painless method or a {@link DResolvedMethod}
 * representing a {@link PainlessMethod} as the Painless AST is constructed.
 * This allows {@link PainlessMethod}s already resolved at the time of Painless
 * AST construction to not be forced to convert back to a method name and method
 * arity and then re-resolved.
 */
public abstract class DMethod {

    protected final Location location;

    public DMethod(Location location) {
        this.location = Objects.requireNonNull(location);
    }

    public abstract DResolvedMethod resolveMethod(PainlessLookup painlessLookup, Class<?> targetClass, boolean isStatic);
}
