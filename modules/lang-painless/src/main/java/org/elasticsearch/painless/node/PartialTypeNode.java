/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.painless.node;

import org.elasticsearch.painless.Location;

/**
 * A temporary node used only during semantic analysis to carry a partial canonical
 * type name (e.g., {@code "java.util"} when resolving {@code java.util.List}).
 *
 * <p>This node is never present in the tree after semantic analysis completes.
 * Any remaining {@code PartialTypeNode} at the end of analysis represents an
 * unresolvable symbol and should be reported as an error.
 */
public final class PartialTypeNode extends ExpressionNode {

    private final String partialCanonicalTypeName;

    public PartialTypeNode(Location location, String partialCanonicalTypeName) {
        super(location, null);
        this.partialCanonicalTypeName = partialCanonicalTypeName;
    }

    public String getPartialCanonicalTypeName() {
        return partialCanonicalTypeName;
    }

    public PartialTypeNode withPartialCanonicalTypeName(String partialCanonicalTypeName) {
        return new PartialTypeNode(getLocation(), partialCanonicalTypeName);
    }
}
