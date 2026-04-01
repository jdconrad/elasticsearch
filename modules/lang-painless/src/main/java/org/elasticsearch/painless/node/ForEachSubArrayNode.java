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

public final class ForEachSubArrayNode extends StatementNode {

    private final ExpressionNode iterableNode;
    private final BlockNode blockNode;
    private final Class<?> arrayType;
    private final String arrayName;
    private final Class<?> indexType;
    private final String indexName;

    public ForEachSubArrayNode(Location location, ExpressionNode iterableNode, BlockNode blockNode,
                               Class<?> arrayType, String arrayName, Class<?> indexType, String indexName) {
        super(location);
        this.iterableNode = iterableNode;
        this.blockNode = blockNode;
        this.arrayType = arrayType;
        this.arrayName = arrayName;
        this.indexType = indexType;
        this.indexName = indexName;
    }

    public ExpressionNode getIterableNode() { return iterableNode; }
    public BlockNode getBlockNode() { return blockNode; }
    public Class<?> getArrayType() { return arrayType; }
    public String getArrayName() { return arrayName; }
    public Class<?> getIndexType() { return indexType; }
    public String getIndexName() { return indexName; }

    public ForEachSubArrayNode withIterableNode(ExpressionNode iterableNode) {
        return new ForEachSubArrayNode(getLocation(), iterableNode, blockNode, arrayType, arrayName, indexType, indexName);
    }

    public ForEachSubArrayNode withBlockNode(BlockNode blockNode) {
        return new ForEachSubArrayNode(getLocation(), iterableNode, blockNode, arrayType, arrayName, indexType, indexName);
    }
}
