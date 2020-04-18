/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.painless.symbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SemanticDecorator {

    public static class Write {
        private Write() {
            // do nothing
        }
    }

    private final ArrayList<Map<Class<?>, Object>> decorations;
    private final ArrayList<Set<Class<?>>> conditions;

    public SemanticDecorator(int nodeCount) {
        decorations = new ArrayList<>(nodeCount);
        conditions = new ArrayList<>(nodeCount);

        for (int i = 0; i < nodeCount; ++i) {
            decorations.add(new HashMap<>());
            conditions.add(new HashSet<>());
        }
    }

    public Object add(int identifier, Object decoration) {
        return decorations.get(identifier).put(decoration.getClass(), decoration);
    }

    public Object remove(int identifier, Class<?> type) {
        return decorations.get(identifier).remove(type);
    }

    public Object get(int identifier, Class<?> type) {
        return decorations.get(identifier).get(type);
    }

    public boolean set(int identifier, Class<?> type) {
        return conditions.get(identifier).add(type);
    }

    public boolean delete(int identifier, Class<?> type) {
        return conditions.get(identifier).remove(type);
    }

    public boolean exists(int identifier, Class<?> type) {
        return conditions.get(identifier).contains(type);
    }
}
