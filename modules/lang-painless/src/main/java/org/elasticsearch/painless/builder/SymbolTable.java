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

import org.elasticsearch.painless.CompilerSettings;
import org.elasticsearch.painless.lookup.PainlessLookup;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SymbolTable {

    protected final CompilerSettings compilerSettings;
    protected final PainlessLookup painlessLookup;

    protected final Class<?> baseClass;
    protected final List<Class<?>> baseInterfaces;

    protected final FunctionTable functionTable = new FunctionTable();
    protected final ScopeTable scopeTable = new ScopeTable();

    protected int syntheticCounter = 0;

    public SymbolTable(CompilerSettings compilerSettings, PainlessLookup painlessLookup,
            Class<?> baseClass, List<Class<?>> baseInterfaces) {

        this.compilerSettings = Objects.requireNonNull(compilerSettings);
        this.painlessLookup = Objects.requireNonNull(painlessLookup);

        this.baseClass = Objects.requireNonNull(baseClass);
        this.baseInterfaces = Collections.unmodifiableList(Objects.requireNonNull(baseInterfaces));
    }

    public CompilerSettings settings() {
        return compilerSettings;
    }

    public PainlessLookup lookup() {
        return painlessLookup;
    }

    public Class<?> baseClass() {
        return baseClass;
    }

    public List<Class<?>> baseInterfaces() {
        return baseInterfaces;
    }

    public FunctionTable functions() {
        return functionTable;
    }

    public ScopeTable scopes() {
        return scopeTable;
    }

    public String nextSyntheticName(String syntheticPrefix) {
        return syntheticPrefix + "$" + syntheticCounter++;
    }
}
