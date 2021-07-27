/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */

package org.elasticsearch.painless.ir;

import org.elasticsearch.painless.Location;
import org.elasticsearch.painless.phase.IRTreeVisitor;

public class BinaryRegexNode extends BinaryNode {

    /* ---- begin visitor ---- */

    @Override
    public <Scope> void visit(IRTreeVisitor<Scope> irTreeVisitor, Scope scope) {
        irTreeVisitor.visitBinaryRegex(this, scope);
    }

    @Override
    public <Scope> void visitChildren(IRTreeVisitor<Scope> irTreeVisitor, Scope scope) {
        getLeftNode().visit(irTreeVisitor, scope);
        getRightNode().visit(irTreeVisitor, scope);
    }

    /* ---- end visitor, begin node data ---- */

    private final int regexLimit;

    public int getRegexLimit() {
        return regexLimit;
    }

    /* ---- end node data ---- */

    public BinaryRegexNode(Location location, Class<?> expressionType, int regexLimit) {
        super(location, expressionType);

        this.regexLimit = regexLimit;
    }

}
