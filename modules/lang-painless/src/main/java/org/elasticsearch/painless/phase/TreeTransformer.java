/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.painless.phase;

import org.elasticsearch.painless.node.ClassNode;
import org.elasticsearch.painless.node.ExpressionNode;
import org.elasticsearch.painless.node.FieldNode;
import org.elasticsearch.painless.node.FunctionNode;
import org.elasticsearch.painless.node.StatementNode;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class for all compiler phases. Phases register handlers for specific node types
 * via the protected {@code on()} overloads in their constructors. Unregistered nodes
 * pass through automatically — their children are transformed recursively via the
 * {@link NodeTraversals} registry, and the node is rebuilt only if a child changed.
 *
 * <h2>Handler registration</h2>
 * Each {@code on()} overload accepts a category-specific handler interface
 * ({@link ExpressionHandler}, {@link StatementHandler}, etc.), so the compiler enforces
 * that an expression handler returns an {@link ExpressionNode}, a statement handler
 * returns a {@link StatementNode}, and so on. No casting is required in handler bodies.
 * The distinct functional interface types also allow {@code on()} to be overloaded per
 * category without erasure clashes.
 *
 * <h2>Consumer extension</h2>
 * The public {@code withHandler()} overloads allow consumers to inject handlers for
 * new node types, or to replace built-in handlers, on any phase instance. Last
 * registration wins.
 *
 * <h2>Thread safety</h2>
 * Handlers are registered during construction and must not be modified concurrently.
 * After construction, {@code transform()} is safe to call from a single thread per
 * compilation (phases are instantiated per-compile via the phase factory in the
 * Compiler).
 */
public class TreeTransformer {

    private final NodeTraversals nodeTraversals;

    private final Map<Class<?>, ExpressionHandler<ExpressionNode>> exprHandlers  = new HashMap<>();
    private final Map<Class<?>, StatementHandler<StatementNode>>   stmtHandlers  = new HashMap<>();
    private final Map<Class<?>, ClassHandler<ClassNode>>           classHandlers = new HashMap<>();
    private final Map<Class<?>, FunctionHandler<FunctionNode>>     funcHandlers  = new HashMap<>();
    private final Map<Class<?>, FieldHandler<FieldNode>>           fieldHandlers = new HashMap<>();

    public TreeTransformer(NodeTraversals nodeTraversals) {
        this.nodeTraversals = nodeTraversals;
    }

    // -------------------------------------------------------------------------
    // Protected on() — for phase authors registering default handlers.
    // Distinct handler interface types allow overloading without erasure clashes.
    // -------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    protected <T extends ExpressionNode> void on(Class<T> type, ExpressionHandler<T> handler) {
        exprHandlers.put(type, node -> handler.apply((T) node));
    }

    @SuppressWarnings("unchecked")
    protected <T extends StatementNode> void on(Class<T> type, StatementHandler<T> handler) {
        stmtHandlers.put(type, node -> handler.apply((T) node));
    }

    @SuppressWarnings("unchecked")
    protected <T extends ClassNode> void on(Class<T> type, ClassHandler<T> handler) {
        classHandlers.put(type, node -> handler.apply((T) node));
    }

    @SuppressWarnings("unchecked")
    protected <T extends FunctionNode> void on(Class<T> type, FunctionHandler<T> handler) {
        funcHandlers.put(type, node -> handler.apply((T) node));
    }

    @SuppressWarnings("unchecked")
    protected <T extends FieldNode> void on(Class<T> type, FieldHandler<T> handler) {
        fieldHandlers.put(type, node -> handler.apply((T) node));
    }

    // -------------------------------------------------------------------------
    // Public withHandler() — for consumers adding or overriding handlers.
    // -------------------------------------------------------------------------

    public <T extends ExpressionNode> TreeTransformer withHandler(Class<T> type, ExpressionHandler<T> handler) {
        on(type, handler);
        return this;
    }

    public <T extends StatementNode> TreeTransformer withHandler(Class<T> type, StatementHandler<T> handler) {
        on(type, handler);
        return this;
    }

    public <T extends ClassNode> TreeTransformer withHandler(Class<T> type, ClassHandler<T> handler) {
        on(type, handler);
        return this;
    }

    public <T extends FunctionNode> TreeTransformer withHandler(Class<T> type, FunctionHandler<T> handler) {
        on(type, handler);
        return this;
    }

    public <T extends FieldNode> TreeTransformer withHandler(Class<T> type, FieldHandler<T> handler) {
        on(type, handler);
        return this;
    }

    // -------------------------------------------------------------------------
    // Typed transform() — no casting in handler bodies or at call sites.
    // Casts in the fallback are isolated to this class.
    // -------------------------------------------------------------------------

    public ExpressionNode transform(ExpressionNode node) {
        var handler = exprHandlers.get(node.getClass());
        if (handler != null) return handler.apply(node);
        return (ExpressionNode) nodeTraversals.traverse(node, this);
    }

    public StatementNode transform(StatementNode node) {
        var handler = stmtHandlers.get(node.getClass());
        if (handler != null) return handler.apply(node);
        return (StatementNode) nodeTraversals.traverse(node, this);
    }

    public ClassNode transform(ClassNode node) {
        var handler = classHandlers.get(node.getClass());
        if (handler != null) return handler.apply(node);
        return (ClassNode) nodeTraversals.traverse(node, this);
    }

    public FunctionNode transform(FunctionNode node) {
        var handler = funcHandlers.get(node.getClass());
        if (handler != null) return handler.apply(node);
        return (FunctionNode) nodeTraversals.traverse(node, this);
    }

    public FieldNode transform(FieldNode node) {
        var handler = fieldHandlers.get(node.getClass());
        if (handler != null) return handler.apply(node);
        return (FieldNode) nodeTraversals.traverse(node, this);
    }

    // -------------------------------------------------------------------------
    // Convenience transformChildren() — delegate directly to NodeTraversals.
    // -------------------------------------------------------------------------

    protected ExpressionNode transformChildren(ExpressionNode node) {
        return (ExpressionNode) nodeTraversals.traverse(node, this);
    }

    protected StatementNode transformChildren(StatementNode node) {
        return (StatementNode) nodeTraversals.traverse(node, this);
    }

    protected ClassNode transformChildren(ClassNode node) {
        return (ClassNode) nodeTraversals.traverse(node, this);
    }

    protected FunctionNode transformChildren(FunctionNode node) {
        return (FunctionNode) nodeTraversals.traverse(node, this);
    }

    protected FieldNode transformChildren(FieldNode node) {
        return (FieldNode) nodeTraversals.traverse(node, this);
    }
}
