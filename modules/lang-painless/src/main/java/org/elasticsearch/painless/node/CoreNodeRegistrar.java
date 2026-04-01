/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.painless.node;

import org.elasticsearch.painless.phase.NodeTraversals;
import org.elasticsearch.painless.phase.TreeTransformer;

import java.util.ArrayList;
import java.util.List;

/**
 * Registers traversal functions for all built-in node types into a {@link NodeTraversals}
 * registry. Traversal functions rebuild nodes with transformed children, returning the
 * original node unchanged when no children change (structural sharing).
 *
 * <p>Leaf nodes (no children) always return {@code node} unchanged. Structural nodes
 * compare transformed children with the originals via reference equality; only if at
 * least one child changed is a new node built via the appropriate {@code withXxx} method.
 */
public final class CoreNodeRegistrar {

    /**
     * Register traversal functions for every built-in node type into {@code traversals}
     * and return it. Existing registrations for the same type are replaced.
     */
    public static NodeTraversals register(NodeTraversals traversals) {
        // ---- Structural nodes -----------------------------------------------

        traversals.register(ClassNode.class, (node, t) -> {
            var fieldNodes     = transformFieldList(node.getFieldNodes(), t);
            var functionNodes  = transformFunctionList(node.getFunctionNodes(), t);
            var clinitBlock    = node.getClinitBlockNode() != null
                ? (BlockNode) t.transform(node.getClinitBlockNode())
                : null;
            if (fieldNodes == node.getFieldNodes()
                && functionNodes == node.getFunctionNodes()
                && clinitBlock == node.getClinitBlockNode()) {
                return node;
            }
            return new ClassNode(node.getLocation(), fieldNodes, functionNodes, clinitBlock, node.getScriptScope());
        });

        traversals.register(FieldNode.class, (node, t) -> node); // leaf

        traversals.register(FunctionNode.class, (node, t) -> {
            var block = (BlockNode) t.transform(node.getBlockNode());
            if (block == node.getBlockNode()) return node;
            return node.withBlockNode(block);
        });

        // ---- Statement nodes ------------------------------------------------

        traversals.register(BlockNode.class, (node, t) -> {
            var stmts = transformStmtList(node.getStatementNodes(), t);
            if (stmts == node.getStatementNodes()) return node;
            return node.withStatementNodes(stmts);
        });

        traversals.register(BreakNode.class,    (node, t) -> node); // leaf
        traversals.register(ContinueNode.class, (node, t) -> node); // leaf

        traversals.register(CatchNode.class, (node, t) -> {
            var block = (BlockNode) t.transform(node.getBlockNode());
            if (block == node.getBlockNode()) return node;
            return node.withBlockNode(block);
        });

        traversals.register(DeclarationBlockNode.class, (node, t) -> {
            @SuppressWarnings("unchecked")
            var decls = (List<DeclarationNode>) (List<?>) transformStmtList(node.getDeclarationNodes(), t);
            if (decls == node.getDeclarationNodes()) return node;
            return node.withDeclarationNodes(decls);
        });

        traversals.register(DeclarationNode.class, (node, t) -> {
            if (node.getExpressionNode() == null) return node;
            var expr = t.transform(node.getExpressionNode());
            if (expr == node.getExpressionNode()) return node;
            return node.withExpressionNode(expr);
        });

        traversals.register(DoWhileLoopNode.class, (node, t) -> {
            var cond  = t.transform(node.getConditionNode());
            var block = (BlockNode) t.transform(node.getBlockNode());
            if (cond == node.getConditionNode() && block == node.getBlockNode()) return node;
            return node.withConditionNode(cond).withBlockNode(block);
        });

        traversals.register(ForEachLoopNode.class, (node, t) -> {
            var cond = (StatementNode) t.transform(node.getConditionNode());
            if (cond == node.getConditionNode()) return node;
            return node.withConditionNode(cond);
        });

        traversals.register(ForEachSubArrayNode.class, (node, t) -> {
            var iterable = t.transform(node.getIterableNode());
            var block    = (BlockNode) t.transform(node.getBlockNode());
            if (iterable == node.getIterableNode() && block == node.getBlockNode()) return node;
            return node.withIterableNode(iterable).withBlockNode(block);
        });

        traversals.register(ForEachSubIterableNode.class, (node, t) -> {
            var iterable = t.transform(node.getIterableNode());
            var block    = (BlockNode) t.transform(node.getBlockNode());
            if (iterable == node.getIterableNode() && block == node.getBlockNode()) return node;
            return node.withIterableNode(iterable).withBlockNode(block);
        });

        traversals.register(ForLoopNode.class, (node, t) -> {
            Node init = null;
            if (node.getInitializerNode() instanceof DeclarationBlockNode decl) {
                init = t.transform(decl);
            } else if (node.getInitializerNode() instanceof ExpressionNode expr) {
                init = t.transform(expr);
            }
            var cond         = node.getConditionNode() != null ? t.transform(node.getConditionNode()) : null;
            var afterthought = node.getAfterthoughtNode() != null ? t.transform(node.getAfterthoughtNode()) : null;
            var block        = node.getBlockNode() != null ? (BlockNode) t.transform(node.getBlockNode()) : null;
            if (init == node.getInitializerNode()
                && cond == node.getConditionNode()
                && afterthought == node.getAfterthoughtNode()
                && block == node.getBlockNode()) {
                return node;
            }
            return new ForLoopNode(node.getLocation(), init, cond, afterthought, block, node.isContinuous());
        });

        traversals.register(IfNode.class, (node, t) -> {
            var cond  = t.transform(node.getConditionNode());
            var block = (BlockNode) t.transform(node.getBlockNode());
            if (cond == node.getConditionNode() && block == node.getBlockNode()) return node;
            return node.withConditionNode(cond).withBlockNode(block);
        });

        traversals.register(IfElseNode.class, (node, t) -> {
            var cond    = t.transform(node.getConditionNode());
            var ifBlock = (BlockNode) t.transform(node.getIfBlockNode());
            var elBlock = (BlockNode) t.transform(node.getElseBlockNode());
            if (cond == node.getConditionNode()
                && ifBlock == node.getIfBlockNode()
                && elBlock == node.getElseBlockNode()) {
                return node;
            }
            return node.withConditionNode(cond).withIfBlockNode(ifBlock).withElseBlockNode(elBlock);
        });

        traversals.register(ReturnNode.class, (node, t) -> {
            if (node.getExpressionNode() == null) return node;
            var expr = t.transform(node.getExpressionNode());
            if (expr == node.getExpressionNode()) return node;
            return node.withExpressionNode(expr);
        });

        traversals.register(StatementExpressionNode.class, (node, t) -> {
            var expr = t.transform(node.getExpressionNode());
            if (expr == node.getExpressionNode()) return node;
            return node.withExpressionNode(expr);
        });

        traversals.register(ThrowNode.class, (node, t) -> {
            var expr = t.transform(node.getExpressionNode());
            if (expr == node.getExpressionNode()) return node;
            return node.withExpressionNode(expr);
        });

        traversals.register(TryNode.class, (node, t) -> {
            var block  = (BlockNode) t.transform(node.getBlockNode());
            @SuppressWarnings("unchecked")
            var catches = (List<CatchNode>) (List<?>) transformStmtList(node.getCatchNodes(), t);
            if (block == node.getBlockNode() && catches == node.getCatchNodes()) return node;
            return node.withBlockNode(block).withCatchNodes(catches);
        });

        traversals.register(WhileLoopNode.class, (node, t) -> {
            var cond  = t.transform(node.getConditionNode());
            var block = (BlockNode) t.transform(node.getBlockNode());
            if (cond == node.getConditionNode() && block == node.getBlockNode()) return node;
            return node.withConditionNode(cond).withBlockNode(block);
        });

        // ---- Binary expression nodes ----------------------------------------

        traversals.register(BinaryImplNode.class, (node, t) -> {
            var left  = t.transform(node.getLeftNode());
            var right = t.transform(node.getRightNode());
            if (left == node.getLeftNode() && right == node.getRightNode()) return node;
            return node.withLeftNode(left).withRightNode(right);
        });

        traversals.register(BinaryMathNode.class, (node, t) -> {
            var left  = t.transform(node.getLeftNode());
            var right = t.transform(node.getRightNode());
            if (left == node.getLeftNode() && right == node.getRightNode()) return node;
            return node.withLeftNode(left).withRightNode(right);
        });

        traversals.register(BooleanNode.class, (node, t) -> {
            var left  = t.transform(node.getLeftNode());
            var right = t.transform(node.getRightNode());
            if (left == node.getLeftNode() && right == node.getRightNode()) return node;
            return node.withLeftNode(left).withRightNode(right);
        });

        traversals.register(ComparisonNode.class, (node, t) -> {
            var left  = t.transform(node.getLeftNode());
            var right = t.transform(node.getRightNode());
            if (left == node.getLeftNode() && right == node.getRightNode()) return node;
            return node.withLeftNode(left).withRightNode(right);
        });

        traversals.register(ConditionalNode.class, (node, t) -> {
            var cond  = t.transform(node.getConditionNode());
            var left  = t.transform(node.getLeftNode());
            var right = t.transform(node.getRightNode());
            if (cond == node.getConditionNode()
                && left == node.getLeftNode()
                && right == node.getRightNode()) {
                return node;
            }
            return node.withConditionNode(cond).withLeftNode(left).withRightNode(right);
        });

        traversals.register(ElvisNode.class, (node, t) -> {
            var left  = t.transform(node.getLeftNode());
            var right = t.transform(node.getRightNode());
            if (left == node.getLeftNode() && right == node.getRightNode()) return node;
            return node.withLeftNode(left).withRightNode(right);
        });

        // ---- Unary expression nodes -----------------------------------------

        traversals.register(CastNode.class, (node, t) -> {
            var child = t.transform(node.getChildNode());
            if (child == node.getChildNode()) return node;
            return node.withChildNode(child);
        });

        traversals.register(DupNode.class, (node, t) -> {
            var child = t.transform(node.getChildNode());
            if (child == node.getChildNode()) return node;
            return node.withChildNode(child);
        });

        traversals.register(FlipArrayIndexNode.class, (node, t) -> {
            var child = t.transform(node.getChildNode());
            if (child == node.getChildNode()) return node;
            return node.withChildNode(child);
        });

        traversals.register(FlipCollectionIndexNode.class, (node, t) -> {
            var child = t.transform(node.getChildNode());
            if (child == node.getChildNode()) return node;
            return node.withChildNode(child);
        });

        traversals.register(FlipDefIndexNode.class, (node, t) -> {
            var child = t.transform(node.getChildNode());
            if (child == node.getChildNode()) return node;
            return node.withChildNode(child);
        });

        traversals.register(InstanceofNode.class, (node, t) -> {
            var child = t.transform(node.getChildNode());
            if (child == node.getChildNode()) return node;
            return node.withChildNode(child);
        });

        traversals.register(NullSafeSubNode.class, (node, t) -> {
            var child = t.transform(node.getChildNode());
            if (child == node.getChildNode()) return node;
            return node.withChildNode(child);
        });

        traversals.register(StoreBraceDefNode.class, (node, t) -> {
            var child = t.transform(node.getChildNode());
            if (child == node.getChildNode()) return node;
            return node.withChildNode(child);
        });

        traversals.register(StoreBraceNode.class, (node, t) -> {
            var child = t.transform(node.getChildNode());
            if (child == node.getChildNode()) return node;
            return node.withChildNode(child);
        });

        traversals.register(StoreDotDefNode.class, (node, t) -> {
            var child = t.transform(node.getChildNode());
            if (child == node.getChildNode()) return node;
            return node.withChildNode(child);
        });

        traversals.register(StoreDotNode.class, (node, t) -> {
            var child = t.transform(node.getChildNode());
            if (child == node.getChildNode()) return node;
            return node.withChildNode(child);
        });

        traversals.register(StoreDotShortcutNode.class, (node, t) -> {
            var child = t.transform(node.getChildNode());
            if (child == node.getChildNode()) return node;
            return node.withChildNode(child);
        });

        traversals.register(StoreFieldMemberNode.class, (node, t) -> {
            var child = t.transform(node.getChildNode());
            if (child == node.getChildNode()) return node;
            return node.withChildNode(child);
        });

        traversals.register(StoreListShortcutNode.class, (node, t) -> {
            var child = t.transform(node.getChildNode());
            if (child == node.getChildNode()) return node;
            return node.withChildNode(child);
        });

        traversals.register(StoreMapShortcutNode.class, (node, t) -> {
            var child = t.transform(node.getChildNode());
            if (child == node.getChildNode()) return node;
            return node.withChildNode(child);
        });

        traversals.register(StoreVariableNode.class, (node, t) -> {
            var child = t.transform(node.getChildNode());
            if (child == node.getChildNode()) return node;
            return node.withChildNode(child);
        });

        traversals.register(UnaryMathNode.class, (node, t) -> {
            var child = t.transform(node.getChildNode());
            if (child == node.getChildNode()) return node;
            return node.withChildNode(child);
        });

        // ---- Arguments expression nodes -------------------------------------

        traversals.register(InvokeCallDefNode.class, (node, t) -> {
            var args = transformExprList(node.getArgumentNodes(), t);
            if (args == node.getArgumentNodes()) return node;
            return node.withArgumentNodes(args);
        });

        traversals.register(InvokeCallMemberNode.class, (node, t) -> {
            var args = transformExprList(node.getArgumentNodes(), t);
            if (args == node.getArgumentNodes()) return node;
            return node.withArgumentNodes(args);
        });

        traversals.register(InvokeCallNode.class, (node, t) -> {
            var args = transformExprList(node.getArgumentNodes(), t);
            if (args == node.getArgumentNodes()) return node;
            return node.withArgumentNodes(args);
        });

        traversals.register(ListInitializationNode.class, (node, t) -> {
            var args = transformExprList(node.getArgumentNodes(), t);
            if (args == node.getArgumentNodes()) return node;
            return node.withArgumentNodes(args);
        });

        traversals.register(MapInitializationNode.class, (node, t) -> {
            var keys   = transformExprList(node.getKeyNodes(), t);
            var values = transformExprList(node.getValueNodes(), t);
            if (keys == node.getKeyNodes() && values == node.getValueNodes()) return node;
            return node.withKeyNodes(keys).withValueNodes(values);
        });

        traversals.register(NewArrayNode.class, (node, t) -> {
            var args = transformExprList(node.getArgumentNodes(), t);
            if (args == node.getArgumentNodes()) return node;
            return node.withArgumentNodes(args);
        });

        traversals.register(NewObjectNode.class, (node, t) -> {
            var args = transformExprList(node.getArgumentNodes(), t);
            if (args == node.getArgumentNodes()) return node;
            return node.withArgumentNodes(args);
        });

        traversals.register(StringConcatenationNode.class, (node, t) -> {
            var args = transformExprList(node.getArgumentNodes(), t);
            if (args == node.getArgumentNodes()) return node;
            return node.withArgumentNodes(args);
        });

        // ---- Source-level expression nodes (produced by Walker, lowered by later phases) --

        traversals.register(AssignmentNode.class, (node, t) -> {
            var left  = t.transform(node.getLeftNode());
            var right = t.transform(node.getRightNode());
            if (left == node.getLeftNode() && right == node.getRightNode()) return node;
            return node.withLeftNode(left).withRightNode(right);
        });

        traversals.register(BraceAccessNode.class, (node, t) -> {
            var prefix = t.transform(node.getPrefixNode());
            var index  = t.transform(node.getIndexNode());
            if (prefix == node.getPrefixNode() && index == node.getIndexNode()) return node;
            return node.withPrefixNode(prefix).withIndexNode(index);
        });

        traversals.register(DecimalNode.class,                (node, t) -> node); // leaf
        traversals.register(NumericNode.class,                (node, t) -> node); // leaf
        traversals.register(RegexNode.class,                  (node, t) -> node); // leaf
        traversals.register(VariableNode.class,               (node, t) -> node); // leaf
        traversals.register(FunctionReferenceNode.class,      (node, t) -> node); // leaf
        traversals.register(NewArrayFunctionReferenceNode.class, (node, t) -> node); // leaf

        traversals.register(DotAccessNode.class, (node, t) -> {
            var prefix = t.transform(node.getPrefixNode());
            if (prefix == node.getPrefixNode()) return node;
            return node.withPrefixNode(prefix);
        });

        traversals.register(ExplicitCastNode.class, (node, t) -> {
            var child = t.transform(node.getChildNode());
            if (child == node.getChildNode()) return node;
            return node.withChildNode(child);
        });

        traversals.register(SourceInstanceofNode.class, (node, t) -> {
            var expr = t.transform(node.getExpressionNode());
            if (expr == node.getExpressionNode()) return node;
            return node.withExpressionNode(expr);
        });

        traversals.register(LambdaNode.class, (node, t) -> {
            var block = (BlockNode) t.transform(node.getBlockNode());
            if (block == node.getBlockNode()) return node;
            return node.withBlockNode(block);
        });

        traversals.register(MethodCallNode.class, (node, t) -> {
            var prefix = t.transform(node.getPrefixNode());
            var args   = transformExprList(node.getArgumentNodes(), t);
            if (prefix == node.getPrefixNode() && args == node.getArgumentNodes()) return node;
            return node.withPrefixNode(prefix).withArgumentNodes(args);
        });

        traversals.register(LocalFunctionCallNode.class, (node, t) -> {
            var args = transformExprList(node.getArgumentNodes(), t);
            if (args == node.getArgumentNodes()) return node;
            return node.withArgumentNodes(args);
        });

        // ---- Source-level statement nodes -----------------------------------

        traversals.register(ForEachSourceNode.class, (node, t) -> {
            var iterable = t.transform(node.getIterableNode());
            var block    = node.getBlockNode() != null ? (BlockNode) t.transform(node.getBlockNode()) : null;
            if (iterable == node.getIterableNode() && block == node.getBlockNode()) return node;
            return node.withIterableNode(iterable).withBlockNode(block);
        });

        // ---- Leaf expression nodes ------------------------------------------

        traversals.register(ConstantNode.class,              (node, t) -> node);
        traversals.register(DefInterfaceReferenceNode.class,  (node, t) -> node);
        traversals.register(LoadBraceDefNode.class,           (node, t) -> node);
        traversals.register(LoadBraceNode.class,              (node, t) -> node);
        traversals.register(LoadDotArrayLengthNode.class,     (node, t) -> node);
        traversals.register(LoadDotDefNode.class,             (node, t) -> node);
        traversals.register(LoadDotNode.class,                (node, t) -> node);
        traversals.register(LoadDotShortcutNode.class,        (node, t) -> node);
        traversals.register(LoadFieldMemberNode.class,        (node, t) -> node);
        traversals.register(LoadListShortcutNode.class,       (node, t) -> node);
        traversals.register(LoadMapShortcutNode.class,        (node, t) -> node);
        traversals.register(LoadVariableNode.class,           (node, t) -> node);
        traversals.register(NullNode.class,                   (node, t) -> node);
        traversals.register(StaticNode.class,                 (node, t) -> node);
        traversals.register(TypedCaptureReferenceNode.class,  (node, t) -> node);
        traversals.register(TypedInterfaceReferenceNode.class, (node, t) -> node);

        return traversals;
    }

    // ---- Private list-traversal helpers ------------------------------------

    /**
     * Transform each element of an expression list. Returns the original list unchanged
     * (same reference) if no element changed.
     */
    private static List<ExpressionNode> transformExprList(List<ExpressionNode> list, TreeTransformer t) {
        List<ExpressionNode> result = null;
        for (int i = 0; i < list.size(); i++) {
            ExpressionNode original    = list.get(i);
            ExpressionNode transformed = t.transform(original);
            if (transformed != original && result == null) {
                result = new ArrayList<>(list.subList(0, i));
            }
            if (result != null) {
                result.add(transformed);
            }
        }
        return result != null ? result : list;
    }

    /**
     * Transform each element of a statement list. Returns the original list unchanged
     * (same reference) if no element changed.
     */
    @SuppressWarnings("unchecked")
    private static <T extends StatementNode> List<T> transformStmtList(List<T> list, TreeTransformer t) {
        List<T> result = null;
        for (int i = 0; i < list.size(); i++) {
            T original    = list.get(i);
            T transformed = (T) t.transform(original);
            if (transformed != original && result == null) {
                result = new ArrayList<>(list.subList(0, i));
            }
            if (result != null) {
                result.add(transformed);
            }
        }
        return result != null ? result : list;
    }

    /**
     * Transform each element of a field node list. Returns the original list unchanged
     * (same reference) if no element changed.
     */
    private static List<FieldNode> transformFieldList(List<FieldNode> list, TreeTransformer t) {
        List<FieldNode> result = null;
        for (int i = 0; i < list.size(); i++) {
            FieldNode original    = list.get(i);
            FieldNode transformed = t.transform(original);
            if (transformed != original && result == null) {
                result = new ArrayList<>(list.subList(0, i));
            }
            if (result != null) {
                result.add(transformed);
            }
        }
        return result != null ? result : list;
    }

    /**
     * Transform each element of a function node list. Returns the original list unchanged
     * (same reference) if no element changed.
     */
    private static List<FunctionNode> transformFunctionList(List<FunctionNode> list, TreeTransformer t) {
        List<FunctionNode> result = null;
        for (int i = 0; i < list.size(); i++) {
            FunctionNode original    = list.get(i);
            FunctionNode transformed = t.transform(original);
            if (transformed != original && result == null) {
                result = new ArrayList<>(list.subList(0, i));
            }
            if (result != null) {
                result.add(transformed);
            }
        }
        return result != null ? result : list;
    }

    private CoreNodeRegistrar() {}
}
