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
import org.elasticsearch.painless.ir.ClassNode;
import org.elasticsearch.painless.ir.FieldNode;
import org.elasticsearch.painless.ir.TypeNode;

/**
 * Represents a member field for its parent class (internal only).
 */
public class SField extends ANode {

    protected final int modifiers;
    protected final String name;
    protected final Class<?> type;

    /**
     * Standard constructor.
     * @param location original location in the source
     * @param modifiers java modifiers for the field
     * @param name name of the field
     * @param type type of the field
     */
    public SField(Location location, int modifiers, String name, Class<?> type) {
        super(location);

        this.modifiers = modifiers;
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    // TODO: what do we do here as far as analysis goes?
    public FieldNode write(ClassNode classNode) {
        return new FieldNode()
                .setTypeNode(new TypeNode()
                        .setLocation(location)
                        .setType(type)
                )
                .setLocation(location)
                .setModifiers(modifiers)
                .setName(name);
    }

    @Override
    public String toString() {
        return singleLineToString(name, type);
    }
}
