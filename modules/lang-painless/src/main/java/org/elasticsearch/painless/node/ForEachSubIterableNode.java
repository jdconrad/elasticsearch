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
import org.elasticsearch.painless.lookup.PainlessMethod;

public final class ForEachSubIterableNode extends StatementNode {

    private final ExpressionNode iterableNode;
    private final BlockNode blockNode;
    private final Class<?> iterableType;
    private final String iterableName;
    private final Class<?> indexType;
    private final String indexName;
    private final PainlessMethod method;

    public ForEachSubIterableNode(Location location, ExpressionNode iterableNode, BlockNode blockNode,
                                  Class<?> iterableType, String iterableName, Class<?> indexType,
                                  String indexName, PainlessMethod method) {
        super(location);
        this.iterableNode = iterableNode;
        this.blockNode = blockNode;
        this.iterableType = iterableType;
        this.iterableName = iterableName;
        this.indexType = indexType;
        this.indexName = indexName;
        this.method = method;
    }

    public ExpressionNode getIterableNode() { return iterableNode; }
    public BlockNode getBlockNode() { return blockNode; }
    public Class<?> getIterableType() { return iterableType; }
    public String getIterableName() { return iterableName; }
    public Class<?> getIndexType() { return indexType; }
    public String getIndexName() { return indexName; }
    public PainlessMethod getMethod() { return method; }

    public ForEachSubIterableNode withIterableNode(ExpressionNode iterableNode) {
        return new ForEachSubIterableNode(getLocation(), iterableNode, blockNode, iterableType,
            iterableName, indexType, indexName, method);
    }

    public ForEachSubIterableNode withBlockNode(BlockNode blockNode) {
        return new ForEachSubIterableNode(getLocation(), iterableNode, blockNode, iterableType,
            iterableName, indexType, indexName, method);
    }
}
