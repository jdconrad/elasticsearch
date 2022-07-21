/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */

package org.elasticsearch.painless.node;

import org.elasticsearch.painless.Location;
import org.elasticsearch.painless.Operation;
import org.elasticsearch.painless.phase.UserTreeVisitor;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Represents a binary math expression.
 */
public class EBinary extends AExpression {

    public static class Builder implements Supplier<EBinary> {

        protected int identifier;
        protected Location location;
        protected Operation operation;
        protected AExpression left;
        protected AExpression right;

        public Builder withIdentifier(int identifier) {
            this.identifier = identifier;
            return this;
        }

        public Builder withIdentifier(Supplier<Integer> identifier) {
            this.identifier = identifier.get();
            return this;
        }

        public Builder withLocation(Location location) {
            this.location = location;
            return this;
        }

        public Builder withLocation(Supplier<Location> location) {
            this.location = location.get();
            return this;
        }

        public Builder withOperation(Operation operation) {
            this.operation = operation;
            return this;
        }

        public Builder withOperation(Supplier<Operation> operation) {
            this.operation = operation.get();
            return this;
        }

        public Builder withLeft(AExpression left) {
            this.left = left;
            return this;
        }

        public Builder withLeft(Supplier<AExpression> left) {
            this.left = left.get();
            return this;
        }

        public Builder withRight(AExpression expression) {
            this.right = expression;
            return this;
        }

        public Builder withRight(Supplier<AExpression> right) {
            this.right = right.get();
            return this;
        }

        public EBinary build() {
            return get();
        }

        @Override
        public EBinary get() {
            return new EBinary(identifier, location, operation, left, right);
        }
    }

    public static Builder builder(Consumer<Builder> consumer) {
        Builder builder = new Builder();
        consumer.accept(builder);
        return builder;
    }

    private final Operation operation;
    private final AExpression leftNode;
    private final AExpression rightNode;

    protected EBinary(int identifier, Location location, Operation operation, AExpression leftNode, AExpression rightNode) {
        super(identifier, location);

        this.operation = Objects.requireNonNull(operation);
        this.leftNode = Objects.requireNonNull(leftNode);
        this.rightNode = Objects.requireNonNull(rightNode);
    }

    public Operation getOperation() {
        return operation;
    }

    public AExpression getLeftNode() {
        return leftNode;
    }

    public AExpression getRightNode() {
        return rightNode;
    }

    @Override
    public <Scope> void visit(UserTreeVisitor<Scope> userTreeVisitor, Scope scope) {
        userTreeVisitor.visitBinary(this, scope);
    }

    @Override
    public <Scope> void visitChildren(UserTreeVisitor<Scope> userTreeVisitor, Scope scope) {
        leftNode.visit(userTreeVisitor, scope);
        rightNode.visit(userTreeVisitor, scope);
    }
}
