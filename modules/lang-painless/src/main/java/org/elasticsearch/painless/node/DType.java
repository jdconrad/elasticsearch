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

import org.elasticsearch.painless.ClassWriter;
import org.elasticsearch.painless.CompilerSettings;
import org.elasticsearch.painless.Globals;
import org.elasticsearch.painless.Locals;
import org.elasticsearch.painless.Location;
import org.elasticsearch.painless.MethodWriter;
import org.elasticsearch.painless.ScriptRoot;

import java.util.Set;

public abstract class DType extends ANode {

    public DType(Location location) {
        super(location);
    }

    void storeSettings(CompilerSettings settings) {
        throw createError(new IllegalStateException("unexpected node"));
    }

    void extractVariables(Set<String> variables) {
        throw createError(new IllegalStateException("unexpected node"));
    }

    void analyze(ScriptRoot scriptRoot, Locals locals) {
        throw createError(new IllegalStateException("unexpected node"));
    }

    void write(ClassWriter classWriter, MethodWriter methodWriter, Globals globals) {
        throw createError(new IllegalStateException("unexpected node"));
    }

    @Override
    public abstract String toString();

    public abstract Class<?> getType();
}
