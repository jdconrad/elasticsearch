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

package org.elasticsearch.painless;

import org.objectweb.asm.ClassVisitor;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Program-wide globals (initializers, synthetic methods, etc)
 */
public class Globals {
    public static int counter = 0;
    public final ClassVisitor visitor;
    public final MethodWriter clinit;
    private final BitSet statements;

    public final Map<String, Object> statics;
    
    /** Create a new Globals from the set of statement boundaries */
    public Globals(ClassVisitor visitor, MethodWriter clinit, BitSet statements) {
        this.visitor = visitor;
        this.clinit = clinit;
        this.statements = statements;

        statics = new HashMap<>();
        statics.put("$STATEMENTS", statements);
    }

    /** Returns the set of statement boundaries */
    public BitSet getStatements() {
        return statements;
    }
}
