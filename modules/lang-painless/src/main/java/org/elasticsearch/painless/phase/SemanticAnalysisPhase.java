/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.painless.phase;

import org.elasticsearch.core.Strings;
import org.elasticsearch.painless.AnalyzerCaster;
import org.elasticsearch.painless.Location;
import org.elasticsearch.painless.Operation;
import org.elasticsearch.painless.lookup.PainlessCast;
import org.elasticsearch.painless.lookup.PainlessClass;
import org.elasticsearch.painless.lookup.PainlessClassBinding;
import org.elasticsearch.painless.lookup.PainlessConstructor;
import org.elasticsearch.painless.lookup.PainlessField;
import org.elasticsearch.painless.lookup.PainlessInstanceBinding;
import org.elasticsearch.painless.lookup.PainlessLookup;
import org.elasticsearch.painless.lookup.PainlessLookupUtility;
import org.elasticsearch.painless.lookup.PainlessMethod;
import org.elasticsearch.painless.lookup.def;
import org.elasticsearch.painless.node.AssignmentNode;
import org.elasticsearch.painless.node.BinaryImplNode;
import org.elasticsearch.painless.node.BinaryMathNode;
import org.elasticsearch.painless.node.BlockNode;
import org.elasticsearch.painless.node.BooleanNode;
import org.elasticsearch.painless.node.BraceAccessNode;
import org.elasticsearch.painless.node.CastNode;
import org.elasticsearch.painless.node.CatchNode;
import org.elasticsearch.painless.node.ClassNode;
import org.elasticsearch.painless.node.ComparisonNode;
import org.elasticsearch.painless.node.ConditionalNode;
import org.elasticsearch.painless.node.ConstantNode;
import org.elasticsearch.painless.node.DeclarationBlockNode;
import org.elasticsearch.painless.node.DeclarationNode;
import org.elasticsearch.painless.node.DefInterfaceReferenceNode;
import org.elasticsearch.painless.node.DoWhileLoopNode;
import org.elasticsearch.painless.node.DotAccessNode;
import org.elasticsearch.painless.node.DupNode;
import org.elasticsearch.painless.node.ElvisNode;
import org.elasticsearch.painless.node.ExplicitCastNode;
import org.elasticsearch.painless.node.ExpressionNode;
import org.elasticsearch.painless.node.FlipArrayIndexNode;
import org.elasticsearch.painless.node.FlipCollectionIndexNode;
import org.elasticsearch.painless.node.FlipDefIndexNode;
import org.elasticsearch.painless.node.ForEachLoopNode;
import org.elasticsearch.painless.node.DecimalNode;
import org.elasticsearch.painless.node.ForEachSourceNode;
import org.elasticsearch.painless.node.ForEachSubArrayNode;
import org.elasticsearch.painless.node.ForEachSubIterableNode;
import org.elasticsearch.painless.node.ForLoopNode;
import org.elasticsearch.painless.node.FunctionNode;
import org.elasticsearch.painless.node.FunctionReferenceNode;
import org.elasticsearch.painless.node.IfElseNode;
import org.elasticsearch.painless.node.IfNode;
import org.elasticsearch.painless.node.InstanceofNode;
import org.elasticsearch.painless.node.InvokeCallDefNode;
import org.elasticsearch.painless.node.InvokeCallMemberNode;
import org.elasticsearch.painless.node.InvokeCallNode;
import org.elasticsearch.painless.node.LambdaNode;
import org.elasticsearch.painless.node.ListInitializationNode;
import org.elasticsearch.painless.node.LoadBraceDefNode;
import org.elasticsearch.painless.node.LoadBraceNode;
import org.elasticsearch.painless.node.LoadDotArrayLengthNode;
import org.elasticsearch.painless.node.LoadDotDefNode;
import org.elasticsearch.painless.node.LoadDotNode;
import org.elasticsearch.painless.node.LoadDotShortcutNode;
import org.elasticsearch.painless.node.LoadListShortcutNode;
import org.elasticsearch.painless.node.LoadMapShortcutNode;
import org.elasticsearch.painless.node.LoadVariableNode;
import org.elasticsearch.painless.node.LocalFunctionCallNode;
import org.elasticsearch.painless.node.MapInitializationNode;
import org.elasticsearch.painless.node.MethodCallNode;
import org.elasticsearch.painless.node.NewArrayFunctionReferenceNode;
import org.elasticsearch.painless.node.NewArrayNode;
import org.elasticsearch.painless.node.NewObjectNode;
import org.elasticsearch.painless.node.Node;
import org.elasticsearch.painless.node.NullNode;
import org.elasticsearch.painless.node.NullSafeSubNode;
import org.elasticsearch.painless.node.NumericNode;
import org.elasticsearch.painless.node.PartialTypeNode;
import org.elasticsearch.painless.node.RegexNode;
import org.elasticsearch.painless.node.ReturnNode;
import org.elasticsearch.painless.node.SourceInstanceofNode;
import org.elasticsearch.painless.node.StatementExpressionNode;
import org.elasticsearch.painless.node.StatementNode;
import org.elasticsearch.painless.node.StaticNode;
import org.elasticsearch.painless.node.StoreBraceDefNode;
import org.elasticsearch.painless.node.StoreBraceNode;
import org.elasticsearch.painless.node.StoreDotDefNode;
import org.elasticsearch.painless.node.StoreDotNode;
import org.elasticsearch.painless.node.StoreDotShortcutNode;
import org.elasticsearch.painless.node.StoreListShortcutNode;
import org.elasticsearch.painless.node.StoreMapShortcutNode;
import org.elasticsearch.painless.node.StoreVariableNode;
import org.elasticsearch.painless.node.StringConcatenationNode;
import org.elasticsearch.painless.node.ThrowNode;
import org.elasticsearch.painless.node.TryNode;
import org.elasticsearch.painless.node.TypedCaptureReferenceNode;
import org.elasticsearch.painless.node.TypedInterfaceReferenceNode;
import org.elasticsearch.painless.node.UnaryMathNode;
import org.elasticsearch.painless.node.VariableNode;
import org.elasticsearch.painless.node.WhileLoopNode;
import org.elasticsearch.painless.spi.annotation.DynamicTypeAnnotation;
import org.elasticsearch.painless.spi.annotation.NonDeterministicAnnotation;
import org.elasticsearch.painless.symbol.FunctionTable;
import org.elasticsearch.painless.symbol.FunctionTable.LocalFunction;
import org.elasticsearch.painless.symbol.ScriptScope;
import org.elasticsearch.painless.symbol.SemanticScope;
import org.elasticsearch.painless.symbol.SemanticScope.FunctionScope;
import org.elasticsearch.painless.symbol.SemanticScope.LambdaScope;

import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Combined semantic analysis and lowering phase for the Painless compiler.
 *
 * <p>This phase operates on the new immutable {@code org.elasticsearch.painless.node.*} AST
 * produced by the Walker, performing type resolution, scope management, and tree lowering in
 * a single pass. It replaces the four old phases:
 * {@code DefaultSemanticAnalysisPhase}, {@code DefaultUserTreeToIRTreePhase},
 * {@code PainlessSemanticAnalysisPhase}, and {@code PainlessUserTreeToIRTreePhase}.
 *
 * <p>Since {@link TreeTransformer} handlers take no context parameter, semantic scope is
 * threaded through the traversal via a {@code Deque<SemanticScope>} instance field.
 * Control-flow escape information is returned inline via node fields (e.g.
 * {@link BlockNode#isAllEscape()}) rather than an external decoration map.
 */
public class SemanticAnalysisPhase extends TreeTransformer {

    private final ScriptScope scriptScope;

    /** Stack of active semantic scopes; top of deque is the innermost scope. */
    private final Deque<SemanticScope> scopeStack = new ArrayDeque<>();

    public SemanticAnalysisPhase(NodeTraversals nodeTraversals, ScriptScope scriptScope) {
        super(nodeTraversals);
        this.scriptScope = scriptScope;

        on(ClassNode.class,      this::visitClass);
        on(FunctionNode.class,   this::visitFunction);
        on(BlockNode.class,      this::visitBlock);

        // statements
        on(IfNode.class,              this::visitIf);
        on(IfElseNode.class,          this::visitIfElse);
        on(WhileLoopNode.class,       this::visitWhile);
        on(DoWhileLoopNode.class,     this::visitDo);
        on(ForLoopNode.class,         this::visitFor);
        on(ForEachSourceNode.class,   this::visitForEach);
        on(DeclarationBlockNode.class,this::visitDeclarationBlock);
        on(DeclarationNode.class,     this::visitDeclaration);
        on(ReturnNode.class,          this::visitReturn);
        on(StatementExpressionNode.class, this::visitStatementExpression);
        on(TryNode.class,             this::visitTry);
        on(CatchNode.class,           this::visitCatch);
        on(ThrowNode.class,           this::visitThrow);

        // expressions that require semantic resolution / lowering
        on(AssignmentNode.class,           this::visitAssignment);
        on(VariableNode.class,             this::visitVariable);
        on(DotAccessNode.class,            this::visitDotAccess);
        on(BraceAccessNode.class,          this::visitBraceAccess);
        on(MethodCallNode.class,           this::visitMethodCall);
        on(LocalFunctionCallNode.class,    this::visitLocalFunctionCall);
        on(ExplicitCastNode.class,         this::visitExplicitCast);
        on(SourceInstanceofNode.class,     this::visitSourceInstanceof);
        on(LambdaNode.class,               this::visitLambda);
        on(FunctionReferenceNode.class,    this::visitFunctionReference);
        on(NewArrayFunctionReferenceNode.class, this::visitNewArrayFunctionReference);
        on(BinaryMathNode.class,           this::visitBinaryMath);
        on(ComparisonNode.class,           this::visitComparison);
        on(BooleanNode.class,              this::visitBoolean);
        on(ElvisNode.class,                this::visitElvis);
        on(ConditionalNode.class,          this::visitConditional);
        on(UnaryMathNode.class,            this::visitUnaryMath);
        on(ConstantNode.class,             this::visitConstant);
        on(NumericNode.class,              this::visitNumeric);
        on(DecimalNode.class,              this::visitDecimal);
        on(NullNode.class,                 this::visitNull);
        on(RegexNode.class,                this::visitRegex);
        on(NewObjectNode.class,            this::visitNewObject);
        on(NewArrayNode.class,             this::visitNewArray);
        on(ListInitializationNode.class,   this::visitListInitialization);
        on(MapInitializationNode.class,    this::visitMapInitialization);
    }

    // =========================================================================
    // Scope helpers
    // =========================================================================

    private SemanticScope currentScope() {
        SemanticScope scope = scopeStack.peek();
        if (scope == null) {
            throw new IllegalStateException("semantic scope stack is empty");
        }
        return scope;
    }

    private void pushScope(SemanticScope scope) {
        scopeStack.push(scope);
    }

    private SemanticScope popScope() {
        return scopeStack.pop();
    }

    // =========================================================================
    // injectCast — wrap an expression in a CastNode when a cast is required
    // =========================================================================

    /**
     * Returns {@code expr} unchanged if no cast is needed, or wrapped in a
     * {@link CastNode} when {@code AnalyzerCaster} identifies a required cast.
     * {@code targetType} is the expected type at the use site; pass {@code null}
     * to skip cast injection.
     */
    private ExpressionNode injectCast(ExpressionNode expr, Class<?> fromType, Class<?> targetType, boolean explicit) {
        if (targetType == null || fromType == targetType) {
            return expr;
        }
        PainlessCast cast = AnalyzerCaster.getLegalCast(expr.getLocation(), fromType, targetType, explicit, true);
        if (cast == null) {
            return expr;
        }
        return new CastNode(expr.getLocation(), expr, cast, targetType);
    }

    // =========================================================================
    // buildLoadStore — mirrors DefaultUserTreeToIRTreePhase.buildLoadStore
    // Assembles compound load/store nodes from prefix, index, load, and store pieces.
    // =========================================================================

    /**
     * Builds the load/store sub-tree for a variable, dot, or brace access.
     *
     * @param accessDepth how many stack slots the prefix occupies (0 = variable, 1 = dot/shortcut, 2 = brace/map/list)
     * @param location    source location for synthetic nodes
     * @param isNullSafe  true when using the null-safe operator {@code ?.}
     * @param prefix      the prefix expression (null for plain variable access)
     * @param index       the index expression for brace/map/list accesses (null otherwise)
     * @param loadNode    the leaf load node (null for write-only)
     * @param storeNode   the leaf store node (null for read-only); for compound assignments this is non-null
     */
    private static ExpressionNode buildLoadStore(
        int accessDepth,
        Location location,
        boolean isNullSafe,
        ExpressionNode prefix,
        ExpressionNode index,
        ExpressionNode loadNode,
        ExpressionNode storeNode
    ) {
        // start from the leaf — for compound the load node is the "read" side
        ExpressionNode expr = loadNode != null ? loadNode : storeNode;

        if (prefix != null) {
            if (index != null) {
                // brace / map / list: combine prefix + index into a BinaryImplNode
                BinaryImplNode binaryImpl = new BinaryImplNode(location, null, null, null, void.class);
                if (isNullSafe) {
                    binaryImpl = binaryImpl.withExpressionType(expr.getExpressionType())
                        .withLeftNode(index)
                        .withRightNode(expr);
                    expr = binaryImpl;
                } else {
                    binaryImpl = binaryImpl.withLeftNode(prefix).withRightNode(index);
                    prefix = binaryImpl;
                }
            }

            if (loadNode != null && storeNode != null) {
                // compound assignment: dup the prefix so it can be used for both load and store
                DupNode dup = new DupNode(location, prefix, accessDepth, 0, void.class);
                prefix = dup;
            }

            BinaryImplNode outer = new BinaryImplNode(location, null, null, null, expr.getExpressionType());
            if (isNullSafe) {
                NullSafeSubNode nullSafe = new NullSafeSubNode(location, expr, expr.getExpressionType());
                outer = outer.withLeftNode(prefix).withRightNode(nullSafe);
            } else {
                outer = outer.withLeftNode(prefix).withRightNode(expr);
            }
            expr = outer;
        }

        if (loadNode != null && storeNode != null) {
            // compound assignment: the store node wraps the combined load expression
            if (storeNode instanceof StoreVariableNode svn) {
                expr = svn.withChildNode(expr);
            } else if (storeNode instanceof StoreDotNode sdn) {
                expr = sdn.withChildNode(expr);
            } else if (storeNode instanceof StoreDotDefNode sddn) {
                expr = sddn.withChildNode(expr);
            } else if (storeNode instanceof StoreDotShortcutNode sdsn) {
                expr = sdsn.withChildNode(expr);
            } else if (storeNode instanceof StoreMapShortcutNode smsn) {
                expr = smsn.withChildNode(expr);
            } else if (storeNode instanceof StoreListShortcutNode slsn) {
                expr = slsn.withChildNode(expr);
            } else if (storeNode instanceof StoreBraceNode sbn) {
                expr = sbn.withChildNode(expr);
            } else if (storeNode instanceof StoreBraceDefNode sbdn) {
                expr = sbdn.withChildNode(expr);
            } else {
                throw new IllegalStateException("unknown store node type: " + storeNode.getClass());
            }
        }

        return expr;
    }

    // =========================================================================
    // Class / Function
    // =========================================================================

    private ClassNode visitClass(ClassNode node) {
        List<FunctionNode> loweredFunctions = new ArrayList<>();
        for (FunctionNode fn : node.getFunctionNodes()) {
            loweredFunctions.add(transform(fn));
        }
        return node.withFunctionNodes(loweredFunctions);
    }

    private FunctionNode visitFunction(FunctionNode node) {
        String functionName = node.getName();
        FunctionTable functionTable = scriptScope.getFunctionTable();
        int arity = node.getParameterNames() != null ? node.getParameterNames().size() : 0;
        LocalFunction localFunction = functionTable.getFunction(functionName, arity);

        Class<?> returnType = node.getReturnType();
        List<Class<?>> typeParameters = node.getTypeParameters();
        List<String> parameterNames = node.getParameterNames();

        FunctionScope functionScope = SemanticScope.newFunctionScope(scriptScope, returnType);

        if ("execute".equals(functionName)) {
            // Inject execute parameters from ScriptClassInfo
            var scriptClassInfo = scriptScope.getScriptClassInfo();
            var executeArgs = scriptClassInfo.getExecuteArguments();
            for (int i = 0; i < executeArgs.size(); i++) {
                var arg = executeArgs.get(i);
                functionScope.defineVariable(node.getLocation(), arg.clazz(), arg.name(), true);
            }
            // Define getter variables
            var getMethods = scriptClassInfo.getGetMethods();
            var getReturns = scriptClassInfo.getGetReturns();
            for (int i = 0; i < getMethods.size(); i++) {
                org.objectweb.asm.commons.Method getMethod = getMethods.get(i);
                String varName = getMethod.getName().substring(3);
                varName = Character.toLowerCase(varName.charAt(0)) + varName.substring(1);
                Class<?> varType = getReturns.get(i);
                if (functionScope.isVariableDefined(varName) == false) {
                    functionScope.defineVariable(node.getLocation(), varType, varName, false);
                }
            }
        } else {
            // Define parameters for user functions
            if (parameterNames != null && typeParameters != null) {
                for (int i = 0; i < parameterNames.size(); i++) {
                    functionScope.defineVariable(node.getLocation(), typeParameters.get(i), parameterNames.get(i), false);
                }
            }
        }

        pushScope(functionScope);
        BlockNode loweredBlock;
        try {
            loweredBlock = visitBlock(node.getBlockNode());
        } finally {
            popScope();
        }

        // if execute, record used variables for needs* methods
        if ("execute".equals(functionName)) {
            scriptScope.setUsedVariables(functionScope.getUsedVariables());
        }

        // auto-return: if the function didn't naturally escape, add a return at the end
        if (localFunction != null && loweredBlock.isAllEscape() == false && node.isAutoReturnEnabled()) {
            ExpressionNode autoReturnExpr = null;
            if (returnType != void.class) {
                autoReturnExpr = buildDefaultConstant(node.getLocation(), returnType);
            }
            ReturnNode autoReturn = new ReturnNode(node.getLocation(), autoReturnExpr);
            List<StatementNode> stmts = new ArrayList<>(loweredBlock.getStatementNodes());
            stmts.add(autoReturn);
            loweredBlock = new BlockNode(loweredBlock.getLocation(), stmts, true);
        }

        return node.withBlockNode(loweredBlock);
    }

    /** Build a default (zero/false/null) constant for auto-return purposes. */
    private static ExpressionNode buildDefaultConstant(Location location, Class<?> type) {
        if (type == boolean.class) {
            return new ConstantNode(location, false, null, boolean.class);
        } else if (type == byte.class) {
            return new ConstantNode(location, (byte) 0, null, byte.class);
        } else if (type == short.class) {
            return new ConstantNode(location, (short) 0, null, short.class);
        } else if (type == char.class) {
            return new ConstantNode(location, (char) 0, null, char.class);
        } else if (type == int.class) {
            return new ConstantNode(location, 0, null, int.class);
        } else if (type == long.class) {
            return new ConstantNode(location, 0L, null, long.class);
        } else if (type == float.class) {
            return new ConstantNode(location, 0.0f, null, float.class);
        } else if (type == double.class) {
            return new ConstantNode(location, 0.0d, null, double.class);
        } else {
            return new NullNode(location, type);
        }
    }

    // =========================================================================
    // Block
    // =========================================================================

    private BlockNode visitBlock(BlockNode node) {
        SemanticScope outerScope = currentScope();
        SemanticScope blockScope = outerScope.newLocalScope();
        pushScope(blockScope);
        try {
            return visitBlockInScope(node);
        } finally {
            popScope();
        }
    }

    private BlockNode visitBlockInScope(BlockNode node) {
        List<StatementNode> stmts = node.getStatementNodes();
        List<StatementNode> lowered = new ArrayList<>(stmts.size());
        boolean allEscape = false;

        for (int i = 0; i < stmts.size(); i++) {
            StatementNode stmt = stmts.get(i);
            StatementNode loweredStmt = transform(stmt);
            lowered.add(loweredStmt);

            if (statementEscapes(loweredStmt)) {
                allEscape = true;
                // remaining statements are unreachable — could warn, but we just drop them
                break;
            }
        }

        return new BlockNode(node.getLocation(), lowered, allEscape);
    }

    /** Returns true when a statement unconditionally escapes (return/throw/break/continue or a known-escape block). */
    private static boolean statementEscapes(StatementNode stmt) {
        return stmt instanceof ReturnNode
            || stmt instanceof ThrowNode
            || (stmt instanceof BlockNode bn && bn.isAllEscape())
            || (stmt instanceof IfElseNode ien && ien.isAllEscape())
            || (stmt instanceof TryNode tn && tn.isAllEscape())
            || (stmt instanceof WhileLoopNode wln && wln.isContinuous())
            || (stmt instanceof DoWhileLoopNode dwln && dwln.isContinuous());
    }

    // =========================================================================
    // Statements
    // =========================================================================

    private StatementNode visitIf(IfNode node) {
        ExpressionNode condition = transform(node.getConditionNode());
        condition = injectCast(condition, condition.getExpressionType(), boolean.class, false);

        BlockNode block = visitBlock(node.getBlockNode());
        return new IfNode(node.getLocation(), condition, block, block.isAllEscape());
    }

    private StatementNode visitIfElse(IfElseNode node) {
        ExpressionNode condition = transform(node.getConditionNode());
        condition = injectCast(condition, condition.getExpressionType(), boolean.class, false);

        BlockNode ifBlock   = visitBlock(node.getIfBlockNode());
        BlockNode elseBlock = visitBlock(node.getElseBlockNode());
        boolean allEscape = ifBlock.isAllEscape() && elseBlock.isAllEscape();
        return new IfElseNode(node.getLocation(), condition, ifBlock, elseBlock, allEscape);
    }

    private StatementNode visitWhile(WhileLoopNode node) {
        SemanticScope loopScope = currentScope().newLocalScope();
        pushScope(loopScope);
        try {
            ExpressionNode condition = null;
            boolean continuous = false;
            if (node.getConditionNode() != null) {
                condition = transform(node.getConditionNode());
                condition = injectCast(condition, condition.getExpressionType(), boolean.class, false);
                // detect literal `true`
                if (condition instanceof ConstantNode cn && Boolean.TRUE.equals(cn.getConstant())) {
                    continuous = true;
                }
            } else {
                continuous = true;
            }

            BlockNode block = node.getBlockNode() != null ? visitBlock(node.getBlockNode()) : null;
            return new WhileLoopNode(node.getLocation(), condition, block, continuous);
        } finally {
            popScope();
        }
    }

    private StatementNode visitDo(DoWhileLoopNode node) {
        SemanticScope loopScope = currentScope().newLocalScope();
        pushScope(loopScope);
        try {
            BlockNode block = visitBlock(node.getBlockNode());

            ExpressionNode condition = transform(node.getConditionNode());
            condition = injectCast(condition, condition.getExpressionType(), boolean.class, false);

            boolean continuous = condition instanceof ConstantNode cn && Boolean.TRUE.equals(cn.getConstant());
            return new DoWhileLoopNode(node.getLocation(), condition, block, continuous);
        } finally {
            popScope();
        }
    }

    private StatementNode visitFor(ForLoopNode node) {
        SemanticScope loopScope = currentScope().newLocalScope();
        pushScope(loopScope);
        try {
            Node initializer = null;
            if (node.getInitializerNode() != null) {
                if (node.getInitializerNode() instanceof DeclarationBlockNode dbn) {
                    initializer = visitDeclarationBlock(dbn);
                } else if (node.getInitializerNode() instanceof ExpressionNode en) {
                    initializer = transform(en);
                } else {
                    throw new IllegalStateException(
                        "unexpected for-loop initializer type: " + node.getInitializerNode().getClass());
                }
            }

            ExpressionNode condition = null;
            boolean continuous = false;
            if (node.getConditionNode() != null) {
                condition = transform(node.getConditionNode());
                condition = injectCast(condition, condition.getExpressionType(), boolean.class, false);
                if (condition instanceof ConstantNode cn && Boolean.TRUE.equals(cn.getConstant())) {
                    continuous = true;
                }
            } else {
                continuous = true;
            }

            ExpressionNode afterthought = null;
            if (node.getAfterthoughtNode() != null) {
                afterthought = transform(node.getAfterthoughtNode());
            }

            BlockNode block = node.getBlockNode() != null ? visitBlock(node.getBlockNode()) : null;
            return new ForLoopNode(node.getLocation(), initializer, condition, afterthought, block, continuous);
        } finally {
            popScope();
        }
    }

    private StatementNode visitForEach(ForEachSourceNode node) {
        Location location = node.getLocation();

        // Resolve the declared variable type
        String canonicalTypeName = node.getCanonicalTypeName();
        Class<?> varType;
        if ("def".equals(canonicalTypeName)) {
            varType = def.class;
        } else {
            varType = scriptScope.getPainlessLookup().canonicalTypeNameToType(canonicalTypeName);
            if (varType == null) {
                throw node.createError(new IllegalArgumentException(
                    "invalid foreach loop: type [" + canonicalTypeName + "] not found"));
            }
        }

        String variableName = node.getVariableName();

        // Transform the iterable expression (in the outer scope)
        ExpressionNode iterable = transform(node.getIterableNode());
        Class<?> iterableType = iterable.getExpressionType();

        // Create a new scope for the loop body and variable
        SemanticScope loopScope = currentScope().newLocalScope();
        pushScope(loopScope);
        try {
            if (iterableType.isArray()) {
                // Array foreach: for (Type var : array) { ... }
                String arrayName  = "#array"  + location.getOffset();
                String indexName  = "#index"  + location.getOffset();

                loopScope.defineVariable(location, iterableType, arrayName, true);
                loopScope.defineVariable(location, int.class,    indexName, true);
                loopScope.defineVariable(location, varType,      variableName, false);

                BlockNode block = visitBlock(node.getBlockNode());

                ForEachSubArrayNode subArray = new ForEachSubArrayNode(
                    location, iterable, block,
                    iterableType, arrayName, int.class, indexName);

                return new ForEachLoopNode(location, subArray);

            } else if (iterableType == def.class || Iterable.class.isAssignableFrom(iterableType)) {
                // Iterable foreach: for (Type var : iterable) { ... }
                PainlessMethod iteratorMethod = null;
                if (iterableType != def.class) {
                    iteratorMethod = scriptScope.getPainlessLookup()
                        .lookupPainlessMethod(iterableType, false, "iterator", 0);
                    if (iteratorMethod == null) {
                        throw node.createError(new IllegalArgumentException(
                            "invalid foreach loop: method ["
                                + PainlessLookupUtility.typeToCanonicalTypeName(iterableType)
                                + ", iterator/0] not found"));
                    }
                }

                String iterableName = "#itr" + location.getOffset();

                loopScope.defineVariable(location, java.util.Iterator.class, iterableName, true);
                loopScope.defineVariable(location, varType,                  variableName, false);

                BlockNode block = visitBlock(node.getBlockNode());

                ForEachSubIterableNode subIterable = new ForEachSubIterableNode(
                    location, iterable, block,
                    iterableType, iterableName, varType, variableName, iteratorMethod);

                return new ForEachLoopNode(location, subIterable);

            } else {
                throw node.createError(new IllegalArgumentException(
                    "invalid foreach loop: cannot iterate over type ["
                        + PainlessLookupUtility.typeToCanonicalTypeName(iterableType) + "]"));
            }
        } finally {
            popScope();
        }
    }

    // ForEachSubArrayNode and ForEachSubIterableNode are produced exclusively by visitForEach above.
    // They are never seen as top-level source nodes.

    private DeclarationBlockNode visitDeclarationBlock(DeclarationBlockNode node) {
        List<DeclarationNode> lowered = new ArrayList<>();
        for (DeclarationNode decl : node.getDeclarationNodes()) {
            lowered.add((DeclarationNode) transform(decl));
        }
        return new DeclarationBlockNode(node.getLocation(), lowered);
    }

    private StatementNode visitDeclaration(DeclarationNode node) {
        SemanticScope scope = currentScope();
        String typeName = node.getDeclarationTypeName();
        Class<?> declType = scriptScope.getPainlessLookup().canonicalTypeNameToType(typeName);

        if (declType == null) {
            throw node.createError(new IllegalArgumentException(
                "invalid declaration: unknown type [" + typeName + "]"));
        }

        ExpressionNode initializer = null;
        if (node.getExpressionNode() != null) {
            ExpressionNode rawInit = transform(node.getExpressionNode());
            initializer = injectCast(rawInit, rawInit.getExpressionType(), declType, false);
        }

        scope.defineVariable(node.getLocation(), declType, node.getName(), false);
        return new DeclarationNode(node.getLocation(), initializer, declType, typeName, node.getName());
    }

    private StatementNode visitReturn(ReturnNode node) {
        SemanticScope scope = currentScope();
        Class<?> returnType = scope.getReturnType();

        ExpressionNode expr = null;
        if (node.getExpressionNode() != null) {
            ExpressionNode raw = transform(node.getExpressionNode());
            expr = injectCast(raw, raw.getExpressionType(), returnType, false);
        } else if (returnType != void.class) {
            throw node.createError(new IllegalArgumentException(
                "invalid return: must return a value of type ["
                    + PainlessLookupUtility.typeToCanonicalTypeName(returnType) + "]"));
        }
        return new ReturnNode(node.getLocation(), expr);
    }

    private StatementNode visitStatementExpression(StatementExpressionNode node) {
        ExpressionNode expr = transform(node.getExpressionNode());
        return new StatementExpressionNode(node.getLocation(), expr);
    }

    private StatementNode visitTry(TryNode node) {
        BlockNode block = visitBlock(node.getBlockNode());

        List<CatchNode> catches = new ArrayList<>();
        boolean allEscape = block.isAllEscape();
        for (CatchNode catchNode : node.getCatchNodes()) {
            CatchNode loweredCatch = (CatchNode) transform(catchNode);
            catches.add(loweredCatch);
            if (loweredCatch.getBlockNode().isAllEscape() == false) {
                allEscape = false;
            }
        }
        return new TryNode(node.getLocation(), block, catches, allEscape);
    }

    private StatementNode visitCatch(CatchNode node) {
        SemanticScope catchScope = currentScope().newLocalScope();
        pushScope(catchScope);
        try {
            String typeName = node.getExceptionTypeName();
            Class<?> exceptionType = scriptScope.getPainlessLookup().canonicalTypeNameToType(typeName);
            if (exceptionType == null) {
                throw node.createError(new IllegalArgumentException(
                    "invalid catch: unknown type [" + typeName + "]"));
            }
            catchScope.defineVariable(node.getLocation(), exceptionType, node.getName(), true);
            BlockNode block = visitBlockInScope(node.getBlockNode());
            return new CatchNode(node.getLocation(), block, exceptionType, typeName, node.getName());
        } finally {
            popScope();
        }
    }

    private StatementNode visitThrow(ThrowNode node) {
        ExpressionNode expr = transform(node.getExpressionNode());
        return new ThrowNode(node.getLocation(), expr);
    }

    // =========================================================================
    // Expressions — Assignment
    // =========================================================================

    private ExpressionNode visitAssignment(AssignmentNode node) {
        // Lower the LHS in "write" context (and read if postIfRead or compound)
        ExpressionNode rawLeft = node.getLeftNode();
        Operation operation = node.getOperation();
        boolean read = node.getExpressionType() != void.class;
        boolean compound = operation != null;

        // First, analyse the LHS to get its value type — transform it in write context
        AccessInfo lhsInfo = analyzeLhs(rawLeft, read || compound, true, compound);
        Class<?> leftType = lhsInfo.valueType();

        // Analyse the RHS
        ExpressionNode rawRight = node.getRightNode();
        ExpressionNode rhsExpr;
        Class<?> finalType;

        if (compound) {
            // Compound assignment: analyse RHS freely then promote
            rhsExpr = transform(rawRight);
            Class<?> rightType = rhsExpr.getExpressionType();

            Class<?> compoundType;
            Class<?> shiftType = null;
            boolean isShift = false;
            boolean isConcatenation = false;

            if (operation == Operation.MUL) {
                compoundType = AnalyzerCaster.promoteNumeric(leftType, rightType, true);
            } else if (operation == Operation.DIV) {
                compoundType = AnalyzerCaster.promoteNumeric(leftType, rightType, true);
            } else if (operation == Operation.REM) {
                compoundType = AnalyzerCaster.promoteNumeric(leftType, rightType, true);
            } else if (operation == Operation.ADD) {
                compoundType = AnalyzerCaster.promoteAdd(leftType, rightType);
                isConcatenation = compoundType == String.class;
            } else if (operation == Operation.SUB) {
                compoundType = AnalyzerCaster.promoteNumeric(leftType, rightType, true);
            } else if (operation == Operation.LSH || operation == Operation.RSH || operation == Operation.USH) {
                compoundType = AnalyzerCaster.promoteNumeric(leftType, false);
                shiftType    = AnalyzerCaster.promoteNumeric(rightType, false);
                isShift      = true;
            } else if (operation == Operation.BWAND || operation == Operation.XOR || operation == Operation.BWOR) {
                compoundType = AnalyzerCaster.promoteXor(leftType, rightType);
            } else {
                throw node.createError(new IllegalStateException("illegal tree structure"));
            }

            if (compoundType == null || (isShift && shiftType == null)) {
                throw node.createError(new IllegalArgumentException(
                    Strings.format("invalid compound assignment: cannot apply [%s=] to types [%s] and [%s]",
                        operation.symbol,
                        PainlessLookupUtility.typeToCanonicalTypeName(leftType),
                        PainlessLookupUtility.typeToCanonicalTypeName(rightType))));
            }

            // Cast RHS to compound type
            Class<?> rhsTarget;
            if (isConcatenation) {
                rhsTarget = rightType;
            } else if (isShift) {
                if (compoundType == def.class) {
                    rhsTarget = def.class;
                } else if (shiftType == long.class) {
                    rhsTarget = int.class;
                } else {
                    rhsTarget = shiftType;
                }
            } else {
                rhsTarget = compoundType;
            }
            rhsExpr = injectCast(rhsExpr, rightType, rhsTarget, isShift && shiftType == long.class);

            // upcast left to compound type for the binary operation
            PainlessCast upcast   = AnalyzerCaster.getLegalCast(node.getLocation(), leftType, compoundType, false, false);
            // downcast compound result back to left type for storage
            PainlessCast downcast = AnalyzerCaster.getLegalCast(node.getLocation(), compoundType, leftType, true, false);

            // Build the compound operation node
            ExpressionNode loadExpr = lhsInfo.loadNode();
            if (upcast != null) {
                loadExpr = new CastNode(node.getLocation(), loadExpr, upcast, compoundType);
            }

            ExpressionNode opResult;
            if (isConcatenation) {
                opResult = buildStringConcatenation(node.getLocation(), loadExpr, rhsExpr, compoundType);
            } else {
                opResult = new BinaryMathNode(node.getLocation(), loadExpr, rhsExpr, operation,
                    compoundType, null, compoundType == def.class, compoundType);
            }

            if (downcast != null) {
                opResult = new CastNode(node.getLocation(), opResult, downcast, leftType);
            }

            ExpressionNode storeExpr = lhsInfo.buildStore(opResult);
            finalType = read ? leftType : void.class;
            if (read && node.isPostIfRead() == false) {
                // pre: value after operation is returned — wrap in cast if needed
                if (storeExpr.getExpressionType() != finalType) {
                    PainlessCast adjustCast = AnalyzerCaster.getLegalCast(
                        node.getLocation(), storeExpr.getExpressionType(), finalType, false, true);
                    if (adjustCast != null) {
                        return new CastNode(node.getLocation(), storeExpr, adjustCast, finalType);
                    }
                }
                return storeExpr;
            }
            return storeExpr;
        } else if (lhsInfo.isDefOptimized()) {
            // def-optimized: RHS type determines the store type
            rhsExpr = transform(rawRight);
            Class<?> rightType = rhsExpr.getExpressionType();
            if (rightType == void.class) {
                throw node.createError(new IllegalArgumentException(
                    "invalid assignment: cannot assign type [void]"));
            }
            finalType = read ? rightType : void.class;
            ExpressionNode storeExpr = lhsInfo.buildStoreWithType(rhsExpr, rightType, finalType);
            return storeExpr;
        } else {
            // Simple assignment
            rhsExpr = transform(rawRight);
            rhsExpr = injectCast(rhsExpr, rhsExpr.getExpressionType(), leftType, false);
            finalType = read ? leftType : void.class;
            return lhsInfo.buildStore(rhsExpr);
        }
    }

    // =========================================================================
    // LHS analysis — returns AccessInfo encapsulating load/store node builders
    // =========================================================================

    /**
     * Analyses a left-hand-side expression node and returns an {@link AccessInfo}
     * that encapsulates the resolved type, load node (for compound assignments), and
     * a factory for building the store expression.
     */
    private AccessInfo analyzeLhs(ExpressionNode lhs, boolean read, boolean write, boolean compound) {
        if (lhs instanceof VariableNode vn) {
            return analyzeVariableLhs(vn, read, write, compound);
        } else if (lhs instanceof DotAccessNode dan) {
            return analyzeDotLhs(dan, read, write, compound);
        } else if (lhs instanceof BraceAccessNode ban) {
            return analyzeBraceLhs(ban, read, write, compound);
        } else {
            throw lhs.createError(new IllegalArgumentException(
                "invalid assignment: left-hand side is not assignable"));
        }
    }

    private AccessInfo analyzeVariableLhs(VariableNode node, boolean read, boolean write, boolean compound) {
        SemanticScope scope = currentScope();
        String name = node.getName();

        if (scope.isVariableDefined(name) == false) {
            throw node.createError(new IllegalArgumentException(
                "variable [" + name + "] is not defined"));
        }
        var variable = scope.getVariable(node.getLocation(), name);
        Class<?> valueType = variable.type();

        ExpressionNode loadNode = null;
        if (read || compound) {
            loadNode = new LoadVariableNode(node.getLocation(), name, valueType);
        }

        final ExpressionNode capturedLoad = loadNode;
        final Class<?> capturedType = valueType;

        return new AccessInfo(
            valueType,
            false,
            capturedLoad,
            (value) -> {
                Class<?> storeType = read ? capturedType : void.class;
                StoreVariableNode store = new StoreVariableNode(node.getLocation(), value, name, storeType);
                if (compound) {
                    return buildLoadStore(0, node.getLocation(), false, null, null, capturedLoad, store);
                }
                return store;
            },
            (value, vType, exprType) -> {
                StoreVariableNode store = new StoreVariableNode(node.getLocation(), value, name, exprType);
                return store;
            }
        );
    }

    private AccessInfo analyzeDotLhs(DotAccessNode node, boolean read, boolean write, boolean compound) {
        ExpressionNode prefix = transform(node.getPrefixNode());
        Class<?> prefixType = prefix.getExpressionType();

        String fieldName = node.getFieldName();

        if (prefix instanceof PartialTypeNode ptn) {
            throw node.createError(new IllegalArgumentException(
                "cannot resolve symbol [" + ptn.getPartialCanonicalTypeName() + "]"));
        }

        // Check if this is a static type reference (StaticNode)
        if (prefix instanceof StaticNode staticNode) {
            Class<?> staticType = staticNode.getExpressionType();
            // Try nested type
            String nestedName = PainlessLookupUtility.typeToCanonicalTypeName(staticType) + "." + fieldName;
            Class<?> nestedType = scriptScope.getPainlessLookup().canonicalTypeNameToType(nestedName);
            if (nestedType != null) {
                if (write) {
                    throw node.createError(new IllegalArgumentException(
                        "invalid assignment: cannot write to static type [" + nestedName + "]"));
                }
                return AccessInfo.staticType(new StaticNode(node.getLocation(), nestedType));
            }
        }

        // Determine whether prefix is a value or static
        boolean isStatic = prefix instanceof StaticNode;
        Class<?> prefixValueType = isStatic ? prefix.getExpressionType() : prefixType;
        boolean prefixIsValue = !isStatic;

        if (isStatic) {
            // static field / sub-type access
            return analyzeStaticDotLhs(node, prefix, prefixValueType, fieldName, read, write, compound);
        }

        // value dot access
        if (prefixType.isArray()) {
            if ("length".equals(fieldName)) {
                if (write) {
                    throw node.createError(new IllegalArgumentException(
                        "invalid assignment: cannot assign a value to read-only field [length] for an array."));
                }
                LoadDotArrayLengthNode loadLen = new LoadDotArrayLengthNode(node.getLocation(), int.class);
                BinaryImplNode result = new BinaryImplNode(node.getLocation(), prefix, loadLen, null, int.class);
                return AccessInfo.readOnly(result, int.class);
            } else {
                throw node.createError(new IllegalArgumentException(
                    "Field [" + fieldName + "] does not exist for type ["
                        + PainlessLookupUtility.typeToCanonicalTypeName(prefixType) + "]."));
            }
        }

        if (prefixType == def.class) {
            Class<?> valueType = def.class;  // dynamic
            boolean isNullSafe = node.isNullSafe();

            ExpressionNode loadNode = compound || read
                ? new LoadDotDefNode(node.getLocation(), fieldName, valueType) : null;
            final ExpressionNode capturedPrefix = prefix;
            final ExpressionNode capturedLoad = loadNode;

            return new AccessInfo(
                valueType,
                true,  // def-optimized
                capturedLoad,
                (value) -> {
                    Class<?> storeExprType = read ? valueType : void.class;
                    StoreDotDefNode store = new StoreDotDefNode(node.getLocation(), value, fieldName, valueType, storeExprType);
                    ExpressionNode result = buildLoadStore(1, node.getLocation(), isNullSafe,
                        capturedPrefix, null, capturedLoad, store);
                    return result;
                },
                (value, vType, exprType) -> {
                    StoreDotDefNode store = new StoreDotDefNode(node.getLocation(), value, fieldName, vType, exprType);
                    return buildLoadStore(1, node.getLocation(), isNullSafe, capturedPrefix, null, capturedLoad, store);
                }
            );
        }

        // Concrete type
        PainlessLookup lookup = scriptScope.getPainlessLookup();
        PainlessField field = lookup.lookupPainlessField(prefixType, false, fieldName);

        if (field != null) {
            if (write && Modifier.isFinal(field.javaField().getModifiers())) {
                throw node.createError(new IllegalArgumentException(
                    "invalid assignment: cannot assign a value to read-only field [" + field.javaField().getName() + "]"));
            }
            Class<?> valueType = field.typeParameter();
            boolean isNullSafe = node.isNullSafe();
            final ExpressionNode capturedPrefix = prefix;

            ExpressionNode loadNode = compound || read
                ? new LoadDotNode(node.getLocation(), field, valueType) : null;
            final ExpressionNode capturedLoad = loadNode;

            return new AccessInfo(
                valueType,
                false,
                capturedLoad,
                (value) -> {
                    Class<?> storeExprType = read ? valueType : void.class;
                    StoreDotNode store = new StoreDotNode(node.getLocation(), value, field, storeExprType);
                    return buildLoadStore(1, node.getLocation(), isNullSafe, capturedPrefix, null, capturedLoad, store);
                },
                (value, vType, exprType) -> {
                    StoreDotNode store = new StoreDotNode(node.getLocation(), value, field, exprType);
                    return buildLoadStore(1, node.getLocation(), isNullSafe, capturedPrefix, null, capturedLoad, store);
                }
            );
        }

        // Shortcut (getter/setter)
        PainlessMethod getter = lookup.lookupPainlessMethod(prefixType, false,
            "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1), 0);
        if (getter == null) {
            getter = lookup.lookupPainlessMethod(prefixType, false,
                "is" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1), 0);
        }
        PainlessMethod setter = lookup.lookupPainlessMethod(prefixType, false,
            "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1), 1);

        if (getter != null || setter != null) {
            validateShortcut(node, fieldName, PainlessLookupUtility.typeToCanonicalTypeName(prefixType),
                getter, setter, read, write);
            Class<?> valueType = setter != null ? setter.typeParameters().get(0) : getter.returnType();
            boolean isNullSafe = node.isNullSafe();
            final ExpressionNode capturedPrefix = prefix;
            final PainlessMethod capturedGetter = getter;
            final PainlessMethod capturedSetter = setter;

            ExpressionNode loadNode = (compound || read) && capturedGetter != null
                ? new LoadDotShortcutNode(node.getLocation(), capturedGetter, valueType) : null;
            final ExpressionNode capturedLoad = loadNode;

            return new AccessInfo(
                valueType,
                false,
                capturedLoad,
                (value) -> {
                    Class<?> storeExprType = read ? valueType : void.class;
                    StoreDotShortcutNode store = new StoreDotShortcutNode(node.getLocation(), value, capturedSetter, storeExprType);
                    return buildLoadStore(1, node.getLocation(), isNullSafe, capturedPrefix, null, capturedLoad, store);
                },
                (value, vType, exprType) -> {
                    StoreDotShortcutNode store = new StoreDotShortcutNode(node.getLocation(), value, capturedSetter, exprType);
                    return buildLoadStore(1, node.getLocation(), isNullSafe, capturedPrefix, null, capturedLoad, store);
                }
            );
        }

        // Map shortcut
        if (Map.class.isAssignableFrom(prefixType)) {
            return buildMapDotLhs(node, prefix, prefixType, fieldName, read, write, compound);
        }

        // List shortcut
        if (List.class.isAssignableFrom(prefixType)) {
            return buildListDotLhs(node, prefix, prefixType, fieldName, read, write, compound);
        }

        throw node.createError(new IllegalArgumentException(
            "field [" + PainlessLookupUtility.typeToCanonicalTypeName(prefixType) + ", " + fieldName + "] not found"));
    }

    private AccessInfo analyzeStaticDotLhs(DotAccessNode node, ExpressionNode prefix,
                                             Class<?> staticType, String fieldName,
                                             boolean read, boolean write, boolean compound) {
        PainlessLookup lookup = scriptScope.getPainlessLookup();
        PainlessField field = lookup.lookupPainlessField(staticType, true, fieldName);

        if (field != null) {
            if (write && Modifier.isFinal(field.javaField().getModifiers())) {
                throw node.createError(new IllegalArgumentException(
                    "invalid assignment: cannot assign a value to read-only field [" + field.javaField().getName() + "]"));
            }
            Class<?> valueType = field.typeParameter();
            final ExpressionNode capturedPrefix = prefix;

            ExpressionNode loadNode = compound || read
                ? new LoadDotNode(node.getLocation(), field, valueType) : null;
            final ExpressionNode capturedLoad = loadNode;

            return new AccessInfo(
                valueType,
                false,
                capturedLoad,
                (value) -> {
                    Class<?> storeExprType = read ? valueType : void.class;
                    StoreDotNode store = new StoreDotNode(node.getLocation(), value, field, storeExprType);
                    return buildLoadStore(1, node.getLocation(), false, capturedPrefix, null, capturedLoad, store);
                },
                (value, vType, exprType) -> {
                    StoreDotNode store = new StoreDotNode(node.getLocation(), value, field, exprType);
                    return buildLoadStore(1, node.getLocation(), false, capturedPrefix, null, capturedLoad, store);
                }
            );
        }

        throw node.createError(new IllegalArgumentException(
            "static field [" + PainlessLookupUtility.typeToCanonicalTypeName(staticType) + ", " + fieldName + "] not found"));
    }

    private AccessInfo buildMapDotLhs(DotAccessNode node, ExpressionNode prefix,
                                       Class<?> prefixType, String fieldName,
                                       boolean read, boolean write, boolean compound) {
        PainlessLookup lookup = scriptScope.getPainlessLookup();
        String prefixName = PainlessLookupUtility.typeToCanonicalTypeName(prefixType);

        PainlessMethod getter = lookup.lookupPainlessMethod(prefixType, false, "get", 1);
        PainlessMethod setter = lookup.lookupPainlessMethod(prefixType, false, "put", 2);

        validateMapShortcut(node, prefixName, getter, setter, read, write);
        Class<?> valueType = setter != null ? setter.typeParameters().get(1) : getter.returnType();

        ConstantNode keyNode = new ConstantNode(node.getLocation(), fieldName, null, String.class);
        final ExpressionNode capturedPrefix = prefix;
        final PainlessMethod capturedGetter = getter;
        final PainlessMethod capturedSetter = setter;

        ExpressionNode loadNode = (compound || read) && capturedGetter != null
            ? new LoadMapShortcutNode(node.getLocation(), capturedGetter, valueType) : null;
        final ExpressionNode capturedLoad = loadNode;

        return new AccessInfo(
            valueType,
            false,
            capturedLoad,
            (value) -> {
                Class<?> storeExprType = read ? valueType : void.class;
                StoreMapShortcutNode store = new StoreMapShortcutNode(node.getLocation(), value, capturedSetter, storeExprType);
                return buildLoadStore(2, node.getLocation(), false, capturedPrefix, keyNode, capturedLoad, store);
            },
            (value, vType, exprType) -> {
                StoreMapShortcutNode store = new StoreMapShortcutNode(node.getLocation(), value, capturedSetter, exprType);
                return buildLoadStore(2, node.getLocation(), false, capturedPrefix, keyNode, capturedLoad, store);
            }
        );
    }

    private AccessInfo buildListDotLhs(DotAccessNode node, ExpressionNode prefix,
                                        Class<?> prefixType, String fieldName,
                                        boolean read, boolean write, boolean compound) {
        PainlessLookup lookup = scriptScope.getPainlessLookup();
        String prefixName = PainlessLookupUtility.typeToCanonicalTypeName(prefixType);

        int listIndex;
        try {
            listIndex = Integer.parseInt(fieldName);
        } catch (NumberFormatException nfe) {
            throw node.createError(new IllegalArgumentException("invalid list index [" + fieldName + "]", nfe));
        }

        PainlessMethod getter = lookup.lookupPainlessMethod(prefixType, false, "get", 1);
        PainlessMethod setter = lookup.lookupPainlessMethod(prefixType, false, "set", 2);

        validateListShortcut(node, prefixName, getter, setter, read, write);
        Class<?> valueType = setter != null ? setter.typeParameters().get(1) : getter.returnType();

        ConstantNode keyNode = new ConstantNode(node.getLocation(), listIndex, null, int.class);
        final ExpressionNode capturedPrefix = prefix;
        final PainlessMethod capturedGetter = getter;
        final PainlessMethod capturedSetter = setter;

        ExpressionNode loadNode = (compound || read) && capturedGetter != null
            ? new LoadListShortcutNode(node.getLocation(), capturedGetter, valueType) : null;
        final ExpressionNode capturedLoad = loadNode;

        return new AccessInfo(
            valueType,
            false,
            capturedLoad,
            (value) -> {
                Class<?> storeExprType = read ? valueType : void.class;
                StoreListShortcutNode store = new StoreListShortcutNode(node.getLocation(), value, capturedSetter, storeExprType);
                FlipCollectionIndexNode flipped = new FlipCollectionIndexNode(keyNode.getLocation(), keyNode, int.class);
                return buildLoadStore(2, node.getLocation(), false, capturedPrefix, flipped, capturedLoad, store);
            },
            (value, vType, exprType) -> {
                StoreListShortcutNode store = new StoreListShortcutNode(node.getLocation(), value, capturedSetter, exprType);
                FlipCollectionIndexNode flipped = new FlipCollectionIndexNode(keyNode.getLocation(), keyNode, int.class);
                return buildLoadStore(2, node.getLocation(), false, capturedPrefix, flipped, capturedLoad, store);
            }
        );
    }

    private AccessInfo analyzeBraceLhs(BraceAccessNode node, boolean read, boolean write, boolean compound) {
        ExpressionNode prefix = transform(node.getPrefixNode());
        Class<?> prefixType = prefix.getExpressionType();

        if (prefixType.isArray()) {
            ExpressionNode indexRaw = transform(node.getIndexNode());
            ExpressionNode indexExpr = injectCast(indexRaw, indexRaw.getExpressionType(), int.class, false);
            FlipArrayIndexNode flipped = new FlipArrayIndexNode(indexExpr.getLocation(), indexExpr, int.class);
            Class<?> valueType = prefixType.getComponentType();

            final ExpressionNode capturedPrefix = prefix;
            ExpressionNode loadNode = compound || read ? new LoadBraceNode(node.getLocation(), valueType) : null;
            final ExpressionNode capturedLoad = loadNode;

            return new AccessInfo(
                valueType,
                false,
                capturedLoad,
                (value) -> {
                    Class<?> storeExprType = read ? valueType : void.class;
                    StoreBraceNode store = new StoreBraceNode(node.getLocation(), value, valueType, storeExprType);
                    return buildLoadStore(2, node.getLocation(), false, capturedPrefix, flipped, capturedLoad, store);
                },
                (value, vType, exprType) -> {
                    StoreBraceNode store = new StoreBraceNode(node.getLocation(), value, vType, exprType);
                    return buildLoadStore(2, node.getLocation(), false, capturedPrefix, flipped, capturedLoad, store);
                }
            );
        }

        if (prefixType == def.class) {
            ExpressionNode indexRaw = transform(node.getIndexNode());
            Class<?> indexType = indexRaw.getExpressionType();
            FlipDefIndexNode flipped = new FlipDefIndexNode(indexRaw.getLocation(), indexRaw, indexType);
            Class<?> valueType = def.class;

            final ExpressionNode capturedPrefix = prefix;
            ExpressionNode loadNode = compound || read ? new LoadBraceDefNode(node.getLocation(), indexType, valueType) : null;
            final ExpressionNode capturedLoad = loadNode;

            return new AccessInfo(
                valueType,
                true,
                capturedLoad,
                (value) -> {
                    Class<?> storeExprType = read ? valueType : void.class;
                    StoreBraceDefNode store = new StoreBraceDefNode(node.getLocation(), value, indexType, valueType, storeExprType);
                    return buildLoadStore(2, node.getLocation(), false, capturedPrefix, flipped, capturedLoad, store);
                },
                (value, vType, exprType) -> {
                    StoreBraceDefNode store = new StoreBraceDefNode(node.getLocation(), value, indexType, vType, exprType);
                    return buildLoadStore(2, node.getLocation(), false, capturedPrefix, flipped, capturedLoad, store);
                }
            );
        }

        // Map
        if (Map.class.isAssignableFrom(prefixType)) {
            return buildMapBraceLhs(node, prefix, prefixType, read, write, compound);
        }

        // List
        if (List.class.isAssignableFrom(prefixType)) {
            return buildListBraceLhs(node, prefix, prefixType, read, write, compound);
        }

        throw node.createError(new IllegalArgumentException(
            "Illegal array access on type ["
                + PainlessLookupUtility.typeToCanonicalTypeName(prefixType) + "]."));
    }

    private AccessInfo buildMapBraceLhs(BraceAccessNode node, ExpressionNode prefix,
                                         Class<?> prefixType, boolean read, boolean write, boolean compound) {
        PainlessLookup lookup = scriptScope.getPainlessLookup();
        String prefixName = PainlessLookupUtility.typeToCanonicalTypeName(prefixType);

        PainlessMethod getter = lookup.lookupPainlessMethod(prefixType, false, "get", 1);
        PainlessMethod setter = lookup.lookupPainlessMethod(prefixType, false, "put", 2);

        validateMapShortcut(node, prefixName, getter, setter, read, write);
        Class<?> keyType = setter != null ? setter.typeParameters().get(0) : getter.typeParameters().get(0);
        Class<?> valueType = setter != null ? setter.typeParameters().get(1) : getter.returnType();

        ExpressionNode indexRaw = transform(node.getIndexNode());
        ExpressionNode indexExpr = injectCast(indexRaw, indexRaw.getExpressionType(), keyType, false);
        final ExpressionNode capturedPrefix = prefix;
        final PainlessMethod capturedGetter = getter;
        final PainlessMethod capturedSetter = setter;

        ExpressionNode loadNode = (compound || read) && capturedGetter != null
            ? new LoadMapShortcutNode(node.getLocation(), capturedGetter, valueType) : null;
        final ExpressionNode capturedLoad = loadNode;

        return new AccessInfo(
            valueType,
            false,
            capturedLoad,
            (value) -> {
                Class<?> storeExprType = read ? valueType : void.class;
                StoreMapShortcutNode store = new StoreMapShortcutNode(node.getLocation(), value, capturedSetter, storeExprType);
                return buildLoadStore(2, node.getLocation(), false, capturedPrefix, indexExpr, capturedLoad, store);
            },
            (value, vType, exprType) -> {
                StoreMapShortcutNode store = new StoreMapShortcutNode(node.getLocation(), value, capturedSetter, exprType);
                return buildLoadStore(2, node.getLocation(), false, capturedPrefix, indexExpr, capturedLoad, store);
            }
        );
    }

    private AccessInfo buildListBraceLhs(BraceAccessNode node, ExpressionNode prefix,
                                          Class<?> prefixType, boolean read, boolean write, boolean compound) {
        PainlessLookup lookup = scriptScope.getPainlessLookup();
        String prefixName = PainlessLookupUtility.typeToCanonicalTypeName(prefixType);

        PainlessMethod getter = lookup.lookupPainlessMethod(prefixType, false, "get", 1);
        PainlessMethod setter = lookup.lookupPainlessMethod(prefixType, false, "set", 2);

        validateListShortcut(node, prefixName, getter, setter, read, write);
        Class<?> valueType = setter != null ? setter.typeParameters().get(1) : getter.returnType();

        ExpressionNode indexRaw = transform(node.getIndexNode());
        ExpressionNode indexExpr = injectCast(indexRaw, indexRaw.getExpressionType(), int.class, false);
        FlipCollectionIndexNode flipped = new FlipCollectionIndexNode(indexExpr.getLocation(), indexExpr, int.class);
        final ExpressionNode capturedPrefix = prefix;
        final PainlessMethod capturedGetter = getter;
        final PainlessMethod capturedSetter = setter;

        ExpressionNode loadNode = (compound || read) && capturedGetter != null
            ? new LoadListShortcutNode(node.getLocation(), capturedGetter, valueType) : null;
        final ExpressionNode capturedLoad = loadNode;

        return new AccessInfo(
            valueType,
            false,
            capturedLoad,
            (value) -> {
                Class<?> storeExprType = read ? valueType : void.class;
                StoreListShortcutNode store = new StoreListShortcutNode(node.getLocation(), value, capturedSetter, storeExprType);
                return buildLoadStore(2, node.getLocation(), false, capturedPrefix, flipped, capturedLoad, store);
            },
            (value, vType, exprType) -> {
                StoreListShortcutNode store = new StoreListShortcutNode(node.getLocation(), value, capturedSetter, exprType);
                return buildLoadStore(2, node.getLocation(), false, capturedPrefix, flipped, capturedLoad, store);
            }
        );
    }

    // =========================================================================
    // Shortcut validation helpers
    // =========================================================================

    private static void validateShortcut(DotAccessNode node, String fieldName, String prefixName,
                                          PainlessMethod getter, PainlessMethod setter,
                                          boolean read, boolean write) {
        if (getter != null && (getter.returnType() == void.class || getter.typeParameters().isEmpty() == false)) {
            throw node.createError(new IllegalArgumentException(
                "Illegal get shortcut on field [" + fieldName + "] for type [" + prefixName + "]."));
        }
        if (setter != null && (setter.returnType() != void.class || setter.typeParameters().size() != 1)) {
            throw node.createError(new IllegalArgumentException(
                "Illegal set shortcut on field [" + fieldName + "] for type [" + prefixName + "]."));
        }
        if (getter != null && setter != null && setter.typeParameters().get(0) != getter.returnType()) {
            throw node.createError(new IllegalArgumentException("Shortcut argument types must match."));
        }
        if ((read == false || getter != null) && (write == false || setter != null)) {
            return;
        }
        throw node.createError(new IllegalArgumentException(
            "Illegal shortcut on field [" + fieldName + "] for type [" + prefixName + "]."));
    }

    private static void validateMapShortcut(org.elasticsearch.painless.node.Node node, String prefixName,
                                             PainlessMethod getter, PainlessMethod setter,
                                             boolean read, boolean write) {
        if (getter != null && (getter.returnType() == void.class || getter.typeParameters().size() != 1)) {
            throw node.createError(new IllegalArgumentException(
                "Illegal map get shortcut for type [" + prefixName + "]."));
        }
        if (setter != null && setter.typeParameters().size() != 2) {
            throw node.createError(new IllegalArgumentException(
                "Illegal map set shortcut for type [" + prefixName + "]."));
        }
        if (getter != null && setter != null
            && (getter.typeParameters().get(0).equals(setter.typeParameters().get(0)) == false
                || getter.returnType().equals(setter.typeParameters().get(1)) == false)) {
            throw node.createError(new IllegalArgumentException("Shortcut argument types must match."));
        }
        if ((read == false || getter != null) && (write == false || setter != null)) {
            return;
        }
        throw node.createError(new IllegalArgumentException(
            "Illegal map shortcut for type [" + prefixName + "]."));
    }

    private static void validateListShortcut(org.elasticsearch.painless.node.Node node, String prefixName,
                                              PainlessMethod getter, PainlessMethod setter,
                                              boolean read, boolean write) {
        if (getter != null && (getter.returnType() == void.class
            || getter.typeParameters().size() != 1
            || getter.typeParameters().get(0) != int.class)) {
            throw node.createError(new IllegalArgumentException(
                "Illegal list get shortcut for type [" + prefixName + "]."));
        }
        if (setter != null && (setter.typeParameters().size() != 2 || setter.typeParameters().get(0) != int.class)) {
            throw node.createError(new IllegalArgumentException(
                "Illegal list set shortcut for type [" + prefixName + "]."));
        }
        if (getter != null && setter != null
            && (getter.typeParameters().get(0).equals(setter.typeParameters().get(0)) == false
                || getter.returnType().equals(setter.typeParameters().get(1)) == false)) {
            throw node.createError(new IllegalArgumentException("Shortcut argument types must match."));
        }
        if ((read == false || getter != null) && (write == false || setter != null)) {
            return;
        }
        throw node.createError(new IllegalArgumentException(
            "Illegal list shortcut for type [" + prefixName + "]."));
    }

    // =========================================================================
    // AccessInfo — captures resolved type + store-node builder
    // =========================================================================

    @FunctionalInterface
    private interface StoreBuilder {
        ExpressionNode build(ExpressionNode value);
    }

    @FunctionalInterface
    private interface TypedStoreBuilder {
        ExpressionNode build(ExpressionNode value, Class<?> valueType, Class<?> expressionType);
    }

    private record AccessInfo(
        Class<?> valueType,
        boolean isDefOptimized,
        ExpressionNode loadNode,
        StoreBuilder storeBuilder,
        TypedStoreBuilder typedStoreBuilder
    ) {
        ExpressionNode buildStore(ExpressionNode value) {
            return storeBuilder.build(value);
        }

        ExpressionNode buildStoreWithType(ExpressionNode value, Class<?> vType, Class<?> exprType) {
            return typedStoreBuilder.build(value, vType, exprType);
        }

        static AccessInfo readOnly(ExpressionNode readExpr, Class<?> type) {
            return new AccessInfo(type, false, readExpr,
                (v) -> { throw new IllegalStateException("read-only access"); },
                (v, vt, et) -> { throw new IllegalStateException("read-only access"); });
        }

        static AccessInfo staticType(ExpressionNode staticExpr) {
            return readOnly(staticExpr, staticExpr.getExpressionType());
        }
    }

    // =========================================================================
    // Variable (read)
    // =========================================================================

    private ExpressionNode visitVariable(VariableNode node) {
        SemanticScope scope = currentScope();
        String name = node.getName();

        // Check for static type
        Class<?> staticType = scriptScope.getPainlessLookup().canonicalTypeNameToType(name);
        if (staticType != null) {
            return new StaticNode(node.getLocation(), staticType);
        }

        if (scope.isVariableDefined(name)) {
            var variable = scope.getVariable(node.getLocation(), name);
            return new LoadVariableNode(node.getLocation(), name, variable.type());
        }

        // Partial type name — will be completed by a subsequent DotAccessNode
        return new PartialTypeNode(node.getLocation(), name);
    }

    // =========================================================================
    // DotAccess (read)
    // =========================================================================

    private ExpressionNode visitDotAccess(DotAccessNode node) {
        ExpressionNode prefix = transform(node.getPrefixNode());
        String fieldName = node.getFieldName();

        // PartialTypeNode chain — try to resolve a fully qualified type name
        if (prefix instanceof PartialTypeNode ptn) {
            String fullName = ptn.getPartialCanonicalTypeName() + "." + fieldName;
            Class<?> type = scriptScope.getPainlessLookup().canonicalTypeNameToType(fullName);
            if (type != null) {
                return new StaticNode(node.getLocation(), type);
            }
            return new PartialTypeNode(node.getLocation(), fullName);
        }

        // StaticNode — nested type or static field
        if (prefix instanceof StaticNode staticNode) {
            Class<?> ownerType = staticNode.getExpressionType();
            // Check nested type
            String nestedName = PainlessLookupUtility.typeToCanonicalTypeName(ownerType) + "." + fieldName;
            Class<?> nestedType = scriptScope.getPainlessLookup().canonicalTypeNameToType(nestedName);
            if (nestedType != null) {
                return new StaticNode(node.getLocation(), nestedType);
            }
            // Static field
            PainlessField field = scriptScope.getPainlessLookup().lookupPainlessField(ownerType, true, fieldName);
            if (field != null) {
                LoadDotNode loadField = new LoadDotNode(node.getLocation(), field, field.typeParameter());
                if (node.isNullSafe()) {
                    throw node.createError(new IllegalArgumentException(
                        "invalid assignment: cannot use null safe on static field [" + fieldName + "]"));
                }
                return new BinaryImplNode(node.getLocation(), prefix, loadField, null, field.typeParameter());
            }
            // Static method shortcut (getter)
            PainlessMethod getter = scriptScope.getPainlessLookup()
                .lookupPainlessMethod(ownerType, true,
                    "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1), 0);
            if (getter == null) {
                getter = scriptScope.getPainlessLookup()
                    .lookupPainlessMethod(ownerType, true,
                        "is" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1), 0);
            }
            if (getter != null) {
                LoadDotShortcutNode shortcut = new LoadDotShortcutNode(node.getLocation(), getter, getter.returnType());
                return new BinaryImplNode(node.getLocation(), prefix, shortcut, null, getter.returnType());
            }
            throw node.createError(new IllegalArgumentException(
                "field [" + PainlessLookupUtility.typeToCanonicalTypeName(ownerType) + ", " + fieldName + "] not found"));
        }

        // Value types
        Class<?> prefixType = prefix.getExpressionType();

        if (prefixType.isArray() && "length".equals(fieldName)) {
            LoadDotArrayLengthNode lengthNode = new LoadDotArrayLengthNode(node.getLocation(), int.class);
            if (node.isNullSafe()) {
                NullSafeSubNode safe = new NullSafeSubNode(node.getLocation(), lengthNode, int.class);
                return new BinaryImplNode(node.getLocation(), prefix, safe, null, int.class);
            }
            return new BinaryImplNode(node.getLocation(), prefix, lengthNode, null, int.class);
        }

        if (prefixType.isArray()) {
            throw node.createError(new IllegalArgumentException(
                "Field [" + fieldName + "] does not exist for type ["
                    + PainlessLookupUtility.typeToCanonicalTypeName(prefixType) + "]."));
        }

        if (prefixType == def.class) {
            LoadDotDefNode loadDef = new LoadDotDefNode(node.getLocation(), fieldName, def.class);
            if (node.isNullSafe()) {
                NullSafeSubNode safe = new NullSafeSubNode(node.getLocation(), loadDef, def.class);
                return new BinaryImplNode(node.getLocation(), prefix, safe, null, def.class);
            }
            return new BinaryImplNode(node.getLocation(), prefix, loadDef, null, def.class);
        }

        // Concrete type — field, shortcut, map, list
        PainlessLookup lookup = scriptScope.getPainlessLookup();
        PainlessField field = lookup.lookupPainlessField(prefixType, false, fieldName);

        if (field != null) {
            LoadDotNode loadField = new LoadDotNode(node.getLocation(), field, field.typeParameter());
            ExpressionNode inner = node.isNullSafe()
                ? new NullSafeSubNode(node.getLocation(), loadField, field.typeParameter())
                : loadField;
            return new BinaryImplNode(node.getLocation(), prefix, inner, null, field.typeParameter());
        }

        // Shortcut getter
        PainlessMethod getter = lookup.lookupPainlessMethod(prefixType, false,
            "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1), 0);
        if (getter == null) {
            getter = lookup.lookupPainlessMethod(prefixType, false,
                "is" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1), 0);
        }
        if (getter != null) {
            validateShortcutReadOnly(node, fieldName, PainlessLookupUtility.typeToCanonicalTypeName(prefixType), getter);
            LoadDotShortcutNode shortcut = new LoadDotShortcutNode(node.getLocation(), getter, getter.returnType());
            ExpressionNode inner = node.isNullSafe()
                ? new NullSafeSubNode(node.getLocation(), shortcut, getter.returnType())
                : shortcut;
            return new BinaryImplNode(node.getLocation(), prefix, inner, null, getter.returnType());
        }

        // Map shortcut (string key → map.get(key))
        if (Map.class.isAssignableFrom(prefixType)) {
            PainlessMethod mapGetter = lookup.lookupPainlessMethod(prefixType, false, "get", 1);
            if (mapGetter != null) {
                ConstantNode keyNode = new ConstantNode(node.getLocation(), fieldName, null, String.class);
                LoadMapShortcutNode loadMap = new LoadMapShortcutNode(node.getLocation(), mapGetter, mapGetter.returnType());
                BinaryImplNode indexAccess = new BinaryImplNode(node.getLocation(), prefix, keyNode, null, void.class);
                ExpressionNode inner = node.isNullSafe()
                    ? new NullSafeSubNode(node.getLocation(), loadMap, mapGetter.returnType())
                    : loadMap;
                return new BinaryImplNode(node.getLocation(), indexAccess, inner, null, mapGetter.returnType());
            }
        }

        // List shortcut (integer index)
        if (List.class.isAssignableFrom(prefixType)) {
            int listIndex;
            try {
                listIndex = Integer.parseInt(fieldName);
            } catch (NumberFormatException nfe) {
                throw node.createError(new IllegalArgumentException("invalid list index [" + fieldName + "]", nfe));
            }
            PainlessMethod listGetter = lookup.lookupPainlessMethod(prefixType, false, "get", 1);
            if (listGetter != null) {
                ConstantNode keyNode = new ConstantNode(node.getLocation(), listIndex, null, int.class);
                FlipCollectionIndexNode flipped = new FlipCollectionIndexNode(keyNode.getLocation(), keyNode, int.class);
                LoadListShortcutNode loadList = new LoadListShortcutNode(node.getLocation(), listGetter, listGetter.returnType());
                BinaryImplNode indexAccess = new BinaryImplNode(node.getLocation(), prefix, flipped, null, void.class);
                ExpressionNode inner = node.isNullSafe()
                    ? new NullSafeSubNode(node.getLocation(), loadList, listGetter.returnType())
                    : loadList;
                return new BinaryImplNode(node.getLocation(), indexAccess, inner, null, listGetter.returnType());
            }
        }

        throw node.createError(new IllegalArgumentException(
            "field [" + PainlessLookupUtility.typeToCanonicalTypeName(prefixType) + ", " + fieldName + "] not found"));
    }

    private static void validateShortcutReadOnly(org.elasticsearch.painless.node.Node node, String fieldName,
                                                  String prefixName, PainlessMethod getter) {
        if (getter.returnType() == void.class || getter.typeParameters().isEmpty() == false) {
            throw node.createError(new IllegalArgumentException(
                "Illegal get shortcut on field [" + fieldName + "] for type [" + prefixName + "]."));
        }
    }

    // =========================================================================
    // BraceAccess (read)
    // =========================================================================

    private ExpressionNode visitBraceAccess(BraceAccessNode node) {
        ExpressionNode prefix = transform(node.getPrefixNode());
        Class<?> prefixType = prefix.getExpressionType();

        if (prefixType.isArray()) {
            ExpressionNode indexRaw = transform(node.getIndexNode());
            ExpressionNode indexExpr = injectCast(indexRaw, indexRaw.getExpressionType(), int.class, false);
            FlipArrayIndexNode flipped = new FlipArrayIndexNode(indexExpr.getLocation(), indexExpr, int.class);
            Class<?> elemType = prefixType.getComponentType();
            LoadBraceNode load = new LoadBraceNode(node.getLocation(), elemType);
            BinaryImplNode indexAccess = new BinaryImplNode(node.getLocation(), prefix, flipped, null, void.class);
            return new BinaryImplNode(node.getLocation(), indexAccess, load, null, elemType);
        }

        if (prefixType == def.class) {
            ExpressionNode indexRaw = transform(node.getIndexNode());
            Class<?> indexType = indexRaw.getExpressionType();
            FlipDefIndexNode flipped = new FlipDefIndexNode(indexRaw.getLocation(), indexRaw, indexType);
            LoadBraceDefNode load = new LoadBraceDefNode(node.getLocation(), indexType, def.class);
            BinaryImplNode indexAccess = new BinaryImplNode(node.getLocation(), prefix, flipped, null, void.class);
            return new BinaryImplNode(node.getLocation(), indexAccess, load, null, def.class);
        }

        PainlessLookup lookup = scriptScope.getPainlessLookup();

        if (Map.class.isAssignableFrom(prefixType)) {
            String prefixName = PainlessLookupUtility.typeToCanonicalTypeName(prefixType);
            PainlessMethod getter = lookup.lookupPainlessMethod(prefixType, false, "get", 1);
            PainlessMethod setter = lookup.lookupPainlessMethod(prefixType, false, "put", 2);
            validateMapShortcut(node, prefixName, getter, setter, true, false);

            Class<?> keyType = getter.typeParameters().get(0);
            ExpressionNode indexRaw = transform(node.getIndexNode());
            ExpressionNode indexExpr = injectCast(indexRaw, indexRaw.getExpressionType(), keyType, false);
            LoadMapShortcutNode load = new LoadMapShortcutNode(node.getLocation(), getter, getter.returnType());
            BinaryImplNode indexAccess = new BinaryImplNode(node.getLocation(), prefix, indexExpr, null, void.class);
            return new BinaryImplNode(node.getLocation(), indexAccess, load, null, getter.returnType());
        }

        if (List.class.isAssignableFrom(prefixType)) {
            String prefixName = PainlessLookupUtility.typeToCanonicalTypeName(prefixType);
            PainlessMethod getter = lookup.lookupPainlessMethod(prefixType, false, "get", 1);
            PainlessMethod setter = lookup.lookupPainlessMethod(prefixType, false, "set", 2);
            validateListShortcut(node, prefixName, getter, setter, true, false);

            ExpressionNode indexRaw = transform(node.getIndexNode());
            ExpressionNode indexExpr = injectCast(indexRaw, indexRaw.getExpressionType(), int.class, false);
            FlipCollectionIndexNode flipped = new FlipCollectionIndexNode(indexExpr.getLocation(), indexExpr, int.class);
            LoadListShortcutNode load = new LoadListShortcutNode(node.getLocation(), getter, getter.returnType());
            BinaryImplNode indexAccess = new BinaryImplNode(node.getLocation(), prefix, flipped, null, void.class);
            return new BinaryImplNode(node.getLocation(), indexAccess, load, null, getter.returnType());
        }

        throw node.createError(new IllegalArgumentException(
            "Illegal array access on type ["
                + PainlessLookupUtility.typeToCanonicalTypeName(prefixType) + "]."));
    }

    // =========================================================================
    // MethodCall
    // =========================================================================

    private ExpressionNode visitMethodCall(MethodCallNode node) {
        ExpressionNode prefix = transform(node.getPrefixNode());
        String methodName = node.getMethodName();
        int argCount = node.getArgumentNodes().size();

        if (prefix instanceof PartialTypeNode ptn) {
            throw node.createError(new IllegalArgumentException(
                "cannot resolve symbol [" + ptn.getPartialCanonicalTypeName() + "]"));
        }

        boolean isStatic = prefix instanceof StaticNode;
        Class<?> prefixType = prefix.getExpressionType();
        PainlessLookup lookup = scriptScope.getPainlessLookup();

        boolean dynamic = false;
        PainlessMethod method = null;

        if (isStatic) {
            method = lookup.lookupPainlessMethod(prefixType, true, methodName, argCount);
            if (method == null) {
                throw node.createError(new IllegalArgumentException(
                    Strings.format("static method [%s, %s/%d] not found",
                        PainlessLookupUtility.typeToCanonicalTypeName(prefixType), methodName, argCount)));
            }
        } else if (prefixType == def.class) {
            dynamic = true;
        } else {
            method = lookup.lookupPainlessMethod(prefixType, false, methodName, argCount);
            if (method == null) {
                PainlessClass pc = lookup.lookupPainlessClass(prefixType);
                dynamic = pc != null
                    && pc.annotations.containsKey(DynamicTypeAnnotation.class)
                    && lookup.lookupPainlessSubClassesMethod(prefixType, methodName, argCount) != null;
                if (dynamic == false) {
                    throw node.createError(new IllegalArgumentException(
                        Strings.format("member method [%s, %s/%d] not found",
                            PainlessLookupUtility.typeToCanonicalTypeName(prefixType), methodName, argCount)));
                }
            }
        }

        ExpressionNode callExpr;

        if (dynamic) {
            List<ExpressionNode> args = new ArrayList<>();
            for (ExpressionNode argRaw : node.getArgumentNodes()) {
                args.add(transform(argRaw));
            }
            callExpr = new InvokeCallDefNode(node.getLocation(), args, methodName, def.class);
        } else {
            Objects.requireNonNull(method);
            scriptScope.markNonDeterministic(method.annotations().containsKey(NonDeterministicAnnotation.class));

            // inject constant injections
            Object[] injections = PainlessLookupUtility.buildInjections(method, scriptScope.getCompilerSettings().asMap());
            Class<?>[] paramTypes = method.javaMethod().getParameterTypes();
            int augOffset = method.javaMethod().getDeclaringClass() == method.targetClass() ? 0 : 1;

            List<ExpressionNode> args = new ArrayList<>();
            for (int i = 0; i < injections.length; i++) {
                Object injection = injections[i];
                Class<?> paramType = paramTypes[i + augOffset];
                if (paramType != PainlessLookupUtility.typeToUnboxedType(injection.getClass())) {
                    throw new IllegalStateException("illegal tree structure");
                }
                args.add(new ConstantNode(node.getLocation(), injection, null, paramType));
            }

            List<ExpressionNode> userArgs = node.getArgumentNodes();
            for (int i = 0; i < userArgs.size(); i++) {
                ExpressionNode rawArg = transform(userArgs.get(i));
                Class<?> targetType = method.typeParameters().get(i);
                args.add(injectCast(rawArg, rawArg.getExpressionType(), targetType, false));
            }

            Class<?> boxType = isStatic ? prefixType : prefixType;
            callExpr = new InvokeCallNode(node.getLocation(), args, method, boxType, method.returnType());
        }

        if (node.isNullSafe() && callExpr.getExpressionType().isPrimitive()) {
            throw node.createError(new IllegalArgumentException(
                "Result of null safe operator must be nullable"));
        }

        ExpressionNode wrappedCallExpr = callExpr;
        if (node.isNullSafe()) {
            wrappedCallExpr = new NullSafeSubNode(node.getLocation(), callExpr, callExpr.getExpressionType());
        }

        return new BinaryImplNode(node.getLocation(), prefix, wrappedCallExpr, null, wrappedCallExpr.getExpressionType());
    }

    // =========================================================================
    // LocalFunctionCall ($-shortcut and regular local calls)
    // =========================================================================

    private ExpressionNode visitLocalFunctionCall(LocalFunctionCallNode node) {
        String methodName = node.getMethodName();

        // Special $ shortcut: $(param0, param1) → param0.get(param1)
        if ("$".equals(methodName)) {
            return visitDollarShortcut(node);
        }

        List<ExpressionNode> rawArgs = node.getArgumentNodes();
        int argCount = rawArgs.size();
        FunctionTable functionTable = scriptScope.getFunctionTable();

        // Try local user function first
        LocalFunction localFunction = functionTable.getFunction(methodName, argCount);

        if (localFunction != null) {
            List<ExpressionNode> args = new ArrayList<>();
            List<Class<?>> paramTypes = localFunction.getTypeParameters();
            for (int i = 0; i < rawArgs.size(); i++) {
                ExpressionNode rawArg = transform(rawArgs.get(i));
                args.add(injectCast(rawArg, rawArg.getExpressionType(), paramTypes.get(i), false));
            }
            return new InvokeCallMemberNode(node.getLocation(), methodName, args,
                localFunction, null, null, null, null, localFunction.getReturnType());
        }

        // Try imported method
        PainlessMethod importedMethod = scriptScope.getPainlessLookup()
            .lookupImportedPainlessMethod(methodName, argCount);

        if (importedMethod != null) {
            scriptScope.markNonDeterministic(
                importedMethod.annotations().containsKey(NonDeterministicAnnotation.class));

            Object[] injections = PainlessLookupUtility.buildInjections(importedMethod,
                scriptScope.getCompilerSettings().asMap());
            Class<?>[] paramTypes = importedMethod.javaMethod().getParameterTypes();
            int augOffset = importedMethod.javaMethod().getDeclaringClass() == importedMethod.targetClass() ? 0 : 1;

            List<ExpressionNode> args = new ArrayList<>();
            for (int i = 0; i < injections.length; i++) {
                Object injection = injections[i];
                Class<?> paramType = paramTypes[i + augOffset];
                args.add(new ConstantNode(node.getLocation(), injection, null, paramType));
            }
            List<Class<?>> methodParamTypes = importedMethod.typeParameters();
            for (int i = 0; i < rawArgs.size(); i++) {
                ExpressionNode rawArg = transform(rawArgs.get(i));
                args.add(injectCast(rawArg, rawArg.getExpressionType(), methodParamTypes.get(i), false));
            }
            return new InvokeCallMemberNode(node.getLocation(), methodName, args,
                null, null, importedMethod, null, null, importedMethod.returnType());
        }

        // Try class binding
        PainlessClassBinding classBinding = scriptScope.getPainlessLookup()
            .lookupPainlessClassBinding(methodName, argCount);

        if (classBinding != null) {
            List<ExpressionNode> args = new ArrayList<>();
            for (int i = 0; i < rawArgs.size(); i++) {
                ExpressionNode rawArg = transform(rawArgs.get(i));
                args.add(injectCast(rawArg, rawArg.getExpressionType(),
                    classBinding.typeParameters().get(i), false));
            }
            return new InvokeCallMemberNode(node.getLocation(), methodName, args,
                null, null, null, classBinding, null, classBinding.returnType());
        }

        // Try instance binding
        PainlessInstanceBinding instanceBinding = scriptScope.getPainlessLookup()
            .lookupPainlessInstanceBinding(methodName, argCount);

        if (instanceBinding != null) {
            List<ExpressionNode> args = new ArrayList<>();
            for (int i = 0; i < rawArgs.size(); i++) {
                ExpressionNode rawArg = transform(rawArgs.get(i));
                args.add(injectCast(rawArg, rawArg.getExpressionType(),
                    instanceBinding.typeParameters().get(i), false));
            }
            return new InvokeCallMemberNode(node.getLocation(), methodName, args,
                null, null, null, null, instanceBinding, instanceBinding.returnType());
        }

        throw node.createError(new IllegalArgumentException(
            Strings.format("unknown call [%s/%d]", methodName, argCount)));
    }

    /**
     * Lowers the {@code $} shortcut: {@code $(arg0, arg1)} becomes
     * {@code BinaryImplNode(InvokeCallMemberNode(thisMethod, [arg0]), InvokeCallDefNode("get", [arg1]))}.
     *
     * <p>The {@code thisMethod} is the {@code field(String)} method on the script's base class,
     * resolved via the Painless whitelist. The second argument is passed to a dynamic {@code get}
     * call on the returned object.
     */
    private ExpressionNode visitDollarShortcut(LocalFunctionCallNode node) {
        List<ExpressionNode> rawArgs = node.getArgumentNodes();
        if (rawArgs.size() != 2) {
            throw node.createError(new IllegalArgumentException(
                "$ shortcut requires exactly 2 arguments"));
        }

        ExpressionNode arg0 = transform(rawArgs.get(0));
        ExpressionNode arg1 = transform(rawArgs.get(1));

        // Look up the "field" method on the script base class (used by the $ shortcut)
        Class<?> baseClass = scriptScope.getScriptClassInfo().getBaseClass();
        PainlessMethod thisMethod = scriptScope.getPainlessLookup()
            .lookupPainlessMethod(baseClass, false, "field", 1);

        if (thisMethod == null) {
            throw node.createError(new IllegalArgumentException(
                "invalid shortcut [$] for [field]; ensure [field] exists in this context"));
        }

        // Mark that this lambda/function uses an instance method so "this" is captured
        currentScope().setUsesInstanceMethod();

        List<ExpressionNode> thisArgs = Collections.singletonList(
            injectCast(arg0, arg0.getExpressionType(), thisMethod.typeParameters().get(0), false));
        InvokeCallMemberNode callMember = new InvokeCallMemberNode(
            node.getLocation(), "$", thisArgs,
            null, thisMethod, null, null, null, thisMethod.returnType());

        List<ExpressionNode> getArgs = Collections.singletonList(arg1);
        InvokeCallDefNode callDef = new InvokeCallDefNode(
            node.getLocation(), getArgs, "get", def.class);

        return new BinaryImplNode(node.getLocation(), callMember, callDef, null, def.class);
    }

    // =========================================================================
    // ExplicitCast
    // =========================================================================

    private ExpressionNode visitExplicitCast(ExplicitCastNode node) {
        String typeName = node.getCanonicalTypeName();
        Class<?> targetType = scriptScope.getPainlessLookup().canonicalTypeNameToType(typeName);
        if (targetType == null) {
            throw node.createError(new IllegalArgumentException(
                "invalid cast: unknown type [" + typeName + "]"));
        }

        ExpressionNode child = transform(node.getChildNode());
        Class<?> childType = child.getExpressionType();

        if (childType == targetType) {
            return child;
        }

        PainlessCast cast = AnalyzerCaster.getLegalCast(node.getLocation(), childType, targetType, true, false);
        if (cast == null) {
            // void or same type — just return the child
            return child;
        }
        return new CastNode(node.getLocation(), child, cast, targetType);
    }

    // =========================================================================
    // SourceInstanceof → InstanceofNode
    // =========================================================================

    private ExpressionNode visitSourceInstanceof(SourceInstanceofNode node) {
        String typeName = node.getCanonicalTypeName();
        Class<?> instanceType = scriptScope.getPainlessLookup().canonicalTypeNameToType(typeName);
        if (instanceType == null) {
            throw node.createError(new IllegalArgumentException(
                "invalid instanceof: unknown type [" + typeName + "]"));
        }

        ExpressionNode child = transform(node.getExpressionNode());
        return new InstanceofNode(node.getLocation(), child, instanceType, boolean.class);
    }

    // =========================================================================
    // BinaryMath
    // =========================================================================

    private ExpressionNode visitBinaryMath(BinaryMathNode node) {
        Operation operation = node.getOperation();
        ExpressionNode left  = transform(node.getLeftNode());
        ExpressionNode right = transform(node.getRightNode());
        Class<?> leftType  = left.getExpressionType();
        Class<?> rightType = right.getExpressionType();

        Class<?> promotedType;
        Class<?> shiftType = null;
        boolean isShift = false;

        if (operation == Operation.MUL || operation == Operation.DIV || operation == Operation.REM
            || operation == Operation.SUB) {
            promotedType = AnalyzerCaster.promoteNumeric(leftType, rightType, true);
        } else if (operation == Operation.ADD) {
            promotedType = AnalyzerCaster.promoteAdd(leftType, rightType);
        } else if (operation == Operation.LSH || operation == Operation.RSH || operation == Operation.USH) {
            promotedType = AnalyzerCaster.promoteNumeric(leftType, false);
            shiftType    = AnalyzerCaster.promoteNumeric(rightType, false);
            isShift      = true;
        } else if (operation == Operation.BWAND || operation == Operation.XOR || operation == Operation.BWOR) {
            promotedType = AnalyzerCaster.promoteXor(leftType, rightType);
        } else {
            throw node.createError(new IllegalStateException("illegal tree structure"));
        }

        if (promotedType == null || (isShift && shiftType == null)) {
            throw node.createError(new IllegalArgumentException(
                Strings.format("cannot apply [%s] to types [%s] and [%s]",
                    operation.symbol,
                    PainlessLookupUtility.typeToCanonicalTypeName(leftType),
                    PainlessLookupUtility.typeToCanonicalTypeName(rightType))));
        }

        // String concatenation
        if (promotedType == String.class) {
            return buildStringConcatenation(node.getLocation(), left, right, String.class);
        }

        // Cast both sides to promoted type
        left  = injectCast(left,  leftType,  promotedType, false);
        Class<?> rightTarget;
        if (isShift) {
            if (promotedType == def.class) {
                rightTarget = def.class;
            } else if (shiftType == long.class) {
                rightTarget = int.class;
            } else {
                rightTarget = shiftType;
            }
        } else {
            rightTarget = promotedType;
        }
        right = injectCast(right, rightType, rightTarget, isShift && Objects.equals(shiftType, long.class));

        boolean isDefType = promotedType == def.class;
        return new BinaryMathNode(node.getLocation(), left, right, operation, promotedType, shiftType, isDefType, promotedType);
    }

    private static ExpressionNode buildStringConcatenation(Location location,
                                                             ExpressionNode left, ExpressionNode right,
                                                             Class<?> type) {
        // Flatten nested string concatenations into a StringConcatenationNode
        List<ExpressionNode> parts = new ArrayList<>();
        collectConcatParts(left, parts);
        collectConcatParts(right, parts);
        return new StringConcatenationNode(location, parts, type);
    }

    private static void collectConcatParts(ExpressionNode node, List<ExpressionNode> parts) {
        if (node instanceof StringConcatenationNode scn) {
            parts.addAll(scn.getArgumentNodes());
        } else {
            parts.add(node);
        }
    }

    // =========================================================================
    // Comparison
    // =========================================================================

    private ExpressionNode visitComparison(ComparisonNode node) {
        Operation operation = node.getOperation();
        ExpressionNode left  = transform(node.getLeftNode());
        ExpressionNode right = transform(node.getRightNode());
        Class<?> leftType  = left.getExpressionType();
        Class<?> rightType = right.getExpressionType();

        Class<?> promotedType;

        if (operation == Operation.EQ || operation == Operation.NE
            || operation == Operation.EQR || operation == Operation.NER) {
            promotedType = AnalyzerCaster.promoteEquality(leftType, rightType);
        } else if (operation == Operation.GT || operation == Operation.GTE
            || operation == Operation.LT || operation == Operation.LTE) {
            promotedType = AnalyzerCaster.promoteNumeric(leftType, rightType, true);
        } else {
            throw node.createError(new IllegalStateException("illegal tree structure"));
        }

        if (promotedType == null) {
            throw node.createError(new IllegalArgumentException(
                Strings.format("cannot apply [%s] to types [%s] and [%s]",
                    operation.symbol,
                    PainlessLookupUtility.typeToCanonicalTypeName(leftType),
                    PainlessLookupUtility.typeToCanonicalTypeName(rightType))));
        }

        // For reference comparisons (===, !==) and def comparisons, skip cast injection
        if (operation != Operation.EQR && operation != Operation.NER && promotedType != def.class) {
            left  = injectCast(left,  leftType,  promotedType, false);
            right = injectCast(right, rightType, promotedType, false);
        }
        return new ComparisonNode(node.getLocation(), left, right, operation, promotedType, boolean.class);
    }

    // =========================================================================
    // Boolean
    // =========================================================================

    private ExpressionNode visitBoolean(BooleanNode node) {
        ExpressionNode left  = transform(node.getLeftNode());
        ExpressionNode right = transform(node.getRightNode());
        left  = injectCast(left,  left.getExpressionType(),  boolean.class, false);
        right = injectCast(right, right.getExpressionType(), boolean.class, false);
        return new BooleanNode(node.getLocation(), left, right, node.getOperation(), boolean.class);
    }

    // =========================================================================
    // Elvis
    // =========================================================================

    private ExpressionNode visitElvis(ElvisNode node) {
        ExpressionNode left  = transform(node.getLeftNode());
        ExpressionNode right = transform(node.getRightNode());

        Class<?> leftType  = left.getExpressionType();
        Class<?> rightType = right.getExpressionType();

        if (leftType.isPrimitive()) {
            throw node.createError(new IllegalArgumentException(
                "Elvis operator requires a nullable left-hand side, got ["
                    + PainlessLookupUtility.typeToCanonicalTypeName(leftType) + "]"));
        }

        Class<?> promotedType = AnalyzerCaster.promoteConditional(leftType, rightType);
        if (promotedType == null) {
            throw node.createError(new IllegalArgumentException(
                Strings.format("cannot apply ?: to types [%s] and [%s]",
                    PainlessLookupUtility.typeToCanonicalTypeName(leftType),
                    PainlessLookupUtility.typeToCanonicalTypeName(rightType))));
        }

        right = injectCast(right, rightType, promotedType, false);
        return new ElvisNode(node.getLocation(), left, right, promotedType);
    }

    // =========================================================================
    // Conditional
    // =========================================================================

    private ExpressionNode visitConditional(ConditionalNode node) {
        ExpressionNode condition = transform(node.getConditionNode());
        condition = injectCast(condition, condition.getExpressionType(), boolean.class, false);

        ExpressionNode left  = transform(node.getLeftNode());
        ExpressionNode right = transform(node.getRightNode());
        Class<?> leftType  = left.getExpressionType();
        Class<?> rightType = right.getExpressionType();

        Class<?> promotedType = AnalyzerCaster.promoteConditional(leftType, rightType);
        if (promotedType == null) {
            throw node.createError(new IllegalArgumentException(
                Strings.format("cannot determine common type for conditional: [%s] and [%s]",
                    PainlessLookupUtility.typeToCanonicalTypeName(leftType),
                    PainlessLookupUtility.typeToCanonicalTypeName(rightType))));
        }

        left  = injectCast(left,  leftType,  promotedType, false);
        right = injectCast(right, rightType, promotedType, false);
        return new ConditionalNode(node.getLocation(), condition, left, right, promotedType);
    }

    // =========================================================================
    // UnaryMath
    // =========================================================================

    private ExpressionNode visitUnaryMath(UnaryMathNode node) {
        Operation operation = node.getOperation();

        // For unary minus/plus applied directly to a numeric/decimal literal, fold the sign into the
        // literal parse. This mirrors the old Negate condition mechanism from DefaultSemanticAnalysisPhase.
        if ((operation == Operation.SUB || operation == Operation.ADD)
            && node.getChildNode() instanceof NumericNode numericNode) {
            String raw = (operation == Operation.SUB ? "-" : "") + numericNode.getNumeric();
            NumericNode signed = new NumericNode(numericNode.getLocation(), raw, numericNode.getRadix());
            return transform(signed);
        }
        if ((operation == Operation.SUB || operation == Operation.ADD)
            && node.getChildNode() instanceof DecimalNode decimalNode) {
            String raw = (operation == Operation.SUB ? "-" : "") + decimalNode.getDecimal();
            DecimalNode signed = new DecimalNode(decimalNode.getLocation(), raw);
            return transform(signed);
        }

        ExpressionNode child = transform(node.getChildNode());
        Class<?> childType = child.getExpressionType();

        Class<?> unaryType;
        if (operation == Operation.NOT) {
            unaryType = boolean.class;
        } else if (operation == Operation.BWNOT) {
            unaryType = AnalyzerCaster.promoteNumeric(childType, false);
        } else if (operation == Operation.ADD || operation == Operation.SUB) {
            unaryType = AnalyzerCaster.promoteNumeric(childType, true);
        } else {
            throw node.createError(new IllegalStateException("illegal tree structure"));
        }

        if (unaryType == null) {
            throw node.createError(new IllegalArgumentException(
                Strings.format("cannot apply [%s] to type [%s]",
                    operation.symbol,
                    PainlessLookupUtility.typeToCanonicalTypeName(childType))));
        }

        child = injectCast(child, childType, unaryType, false);
        boolean isDefType = unaryType == def.class;
        return new UnaryMathNode(node.getLocation(), child, operation, unaryType, node.getFlags(), isDefType, unaryType);
    }

    // =========================================================================
    // Constant / Null / Regex
    // =========================================================================

    private ExpressionNode visitConstant(ConstantNode node) {
        // Already typed by Walker
        return node;
    }

    /**
     * Parse a source-level integer/long literal and lower it to a {@link ConstantNode}.
     * The {@code numeric} string may optionally begin with {@code -} (from folded unary minus).
     */
    private ExpressionNode visitNumeric(NumericNode node) {
        String numeric = node.getNumeric();
        int radix = node.getRadix();

        Class<?> valueType;
        Object constant;

        if (radix != 16 && (numeric.endsWith("d") || numeric.endsWith("D"))) {
            try {
                constant = Double.parseDouble(numeric.substring(0, numeric.length() - 1));
                valueType = double.class;
            } catch (NumberFormatException e) {
                throw node.createError(new IllegalArgumentException("Invalid double constant [" + numeric + "]."));
            }
        } else if (radix != 16 && (numeric.endsWith("f") || numeric.endsWith("F"))) {
            try {
                constant = Float.parseFloat(numeric.substring(0, numeric.length() - 1));
                valueType = float.class;
            } catch (NumberFormatException e) {
                throw node.createError(new IllegalArgumentException("Invalid float constant [" + numeric + "]."));
            }
        } else if (numeric.endsWith("l") || numeric.endsWith("L")) {
            try {
                constant = Long.parseLong(numeric.substring(0, numeric.length() - 1), radix);
                valueType = long.class;
            } catch (NumberFormatException e) {
                throw node.createError(new IllegalArgumentException("Invalid long constant [" + numeric + "]."));
            }
        } else {
            try {
                int integer = Integer.parseInt(numeric, radix);
                constant = integer;
                valueType = int.class;
            } catch (NumberFormatException e) {
                try {
                    Long.parseLong(numeric, radix);
                    throw node.createError(new IllegalArgumentException(
                        "Invalid int constant [" + numeric + "]. If you want a long constant then change it to ["
                            + numeric + "L]."));
                } catch (NumberFormatException ignored) {}
                throw node.createError(new IllegalArgumentException("Invalid int constant [" + numeric + "]."));
            }
        }

        return new ConstantNode(node.getLocation(), constant, null, valueType);
    }

    /**
     * Parse a source-level floating-point literal and lower it to a {@link ConstantNode}.
     * The {@code decimal} string may optionally begin with {@code -} (from folded unary minus).
     */
    private ExpressionNode visitDecimal(DecimalNode node) {
        String decimal = node.getDecimal();

        Class<?> valueType;
        Object constant;

        if (decimal.endsWith("f") || decimal.endsWith("F")) {
            try {
                constant = Float.parseFloat(decimal.substring(0, decimal.length() - 1));
                valueType = float.class;
            } catch (NumberFormatException e) {
                throw node.createError(new IllegalArgumentException("Invalid float constant [" + decimal + "]."));
            }
        } else {
            String toParse = decimal;
            if (toParse.endsWith("d") || toParse.endsWith("D")) {
                toParse = toParse.substring(0, toParse.length() - 1);
            }
            try {
                constant = Double.parseDouble(toParse);
                valueType = double.class;
            } catch (NumberFormatException e) {
                throw node.createError(new IllegalArgumentException("Invalid double constant [" + decimal + "]."));
            }
        }

        return new ConstantNode(node.getLocation(), constant, null, valueType);
    }

    private ExpressionNode visitNull(NullNode node) {
        return node;
    }

    private ExpressionNode visitRegex(RegexNode node) {
        // Already fully formed by Walker
        return node;
    }

    // =========================================================================
    // NewObject
    // =========================================================================

    private ExpressionNode visitNewObject(NewObjectNode node) {
        String typeName = node.getCanonicalTypeName();
        Class<?> type = scriptScope.getPainlessLookup().canonicalTypeNameToType(typeName);
        if (type == null) {
            throw node.createError(new IllegalArgumentException(
                "invalid new object: unknown type [" + typeName + "]"));
        }

        int argCount = node.getArgumentNodes().size();
        PainlessConstructor constructor = scriptScope.getPainlessLookup()
            .lookupPainlessConstructor(type, argCount);
        if (constructor == null) {
            throw node.createError(new IllegalArgumentException(
                Strings.format("constructor [%s, <%d>] not found",
                    typeName, argCount)));
        }

        List<ExpressionNode> args = new ArrayList<>();
        for (int i = 0; i < node.getArgumentNodes().size(); i++) {
            ExpressionNode rawArg = transform(node.getArgumentNodes().get(i));
            args.add(injectCast(rawArg, rawArg.getExpressionType(), constructor.typeParameters().get(i), false));
        }

        return new NewObjectNode(node.getLocation(), typeName, args, constructor, type);
    }

    // =========================================================================
    // NewArray
    // =========================================================================

    private ExpressionNode visitNewArray(NewArrayNode node) {
        String typeName = node.getCanonicalTypeName();
        Class<?> elementType = scriptScope.getPainlessLookup().canonicalTypeNameToType(typeName);
        if (elementType == null) {
            throw node.createError(new IllegalArgumentException(
                "invalid new array: unknown type [" + typeName + "]"));
        }

        Class<?> arrayType = node.isInitialize()
            ? java.lang.reflect.Array.newInstance(elementType, 0).getClass()
            : java.lang.reflect.Array.newInstance(elementType, 0).getClass();

        List<ExpressionNode> args = new ArrayList<>();
        for (ExpressionNode rawArg : node.getArgumentNodes()) {
            ExpressionNode arg = transform(rawArg);
            if (node.isInitialize()) {
                arg = injectCast(arg, arg.getExpressionType(), elementType, false);
            } else {
                arg = injectCast(arg, arg.getExpressionType(), int.class, false);
            }
            args.add(arg);
        }

        return new NewArrayNode(node.getLocation(), typeName, args, node.isInitialize(), arrayType);
    }

    // =========================================================================
    // ListInitialization / MapInitialization
    // =========================================================================

    private ExpressionNode visitListInitialization(ListInitializationNode node) {
        PainlessConstructor constructor = scriptScope.getPainlessLookup()
            .lookupPainlessConstructor(ArrayList.class, 0);
        if (constructor == null) {
            throw node.createError(new IllegalStateException("ArrayList() constructor not found"));
        }
        PainlessMethod addMethod = scriptScope.getPainlessLookup()
            .lookupPainlessMethod(ArrayList.class, false, "add", 1);
        if (addMethod == null) {
            throw node.createError(new IllegalStateException("ArrayList.add() not found"));
        }

        List<ExpressionNode> args = new ArrayList<>();
        for (ExpressionNode rawArg : node.getArgumentNodes()) {
            ExpressionNode arg = transform(rawArg);
            args.add(injectCast(arg, arg.getExpressionType(), addMethod.typeParameters().get(0), false));
        }

        return new ListInitializationNode(node.getLocation(), args, constructor, addMethod, ArrayList.class);
    }

    private ExpressionNode visitMapInitialization(MapInitializationNode node) {
        PainlessConstructor constructor = scriptScope.getPainlessLookup()
            .lookupPainlessConstructor(java.util.HashMap.class, 0);
        if (constructor == null) {
            throw node.createError(new IllegalStateException("HashMap() constructor not found"));
        }
        PainlessMethod putMethod = scriptScope.getPainlessLookup()
            .lookupPainlessMethod(java.util.HashMap.class, false, "put", 2);
        if (putMethod == null) {
            throw node.createError(new IllegalStateException("HashMap.put() not found"));
        }

        List<ExpressionNode> keys   = new ArrayList<>();
        List<ExpressionNode> values = new ArrayList<>();

        for (int i = 0; i < node.getKeyNodes().size(); i++) {
            ExpressionNode rawKey = transform(node.getKeyNodes().get(i));
            ExpressionNode rawVal = transform(node.getValueNodes().get(i));
            keys.add(injectCast(rawKey,   rawKey.getExpressionType(),   putMethod.typeParameters().get(0), false));
            values.add(injectCast(rawVal, rawVal.getExpressionType(), putMethod.typeParameters().get(1), false));
        }

        return new MapInitializationNode(node.getLocation(), keys, values, constructor, putMethod, java.util.HashMap.class);
    }

    // =========================================================================
    // Lambda
    // =========================================================================

    /**
     * Visits a lambda node. Performs scope management and parameter definition for any
     * explicitly-typed parameters, then recurses into the body. The full resolution of
     * the functional interface type (and thus the generated inner-class synthesis) requires
     * the target type from the enclosing context and is deferred: this visitor returns the
     * {@link LambdaNode} with the body block lowered, leaving the expression type as
     * {@code def.class}. A downstream phase that has target-type context is expected to
     * replace this node with a {@link TypedInterfaceReferenceNode} or
     * {@link DefInterfaceReferenceNode}.
     */
    private ExpressionNode visitLambda(LambdaNode node) {
        SemanticScope outerScope = currentScope();
        // Lambda return type is not yet known (depends on target functional-interface context).
        // Use def.class as a placeholder; a downstream phase resolves the actual type.
        LambdaScope lambdaScope = outerScope.newLambdaScope(def.class);

        List<String> typeNames  = node.getTypeNameParameters();
        List<String> paramNames = node.getParameterNames();

        for (int i = 0; i < paramNames.size(); i++) {
            Class<?> paramType = def.class;
            if (typeNames != null && i < typeNames.size() && typeNames.get(i) != null) {
                paramType = scriptScope.getPainlessLookup().canonicalTypeNameToType(typeNames.get(i));
                if (paramType == null) {
                    throw node.createError(new IllegalArgumentException(
                        "invalid lambda parameter type [" + typeNames.get(i) + "]"));
                }
            }
            lambdaScope.defineVariable(node.getLocation(), paramType, paramNames.get(i), true);
        }

        pushScope(lambdaScope);
        BlockNode body;
        try {
            body = visitBlock(node.getBlockNode());
        } finally {
            popScope();
        }

        return new LambdaNode(node.getLocation(), typeNames, paramNames, body, def.class);
    }

    // =========================================================================
    // FunctionReference
    // =========================================================================

    /**
     * Visits a function-reference node ({@code Type::method} or {@code variable::method}).
     * Validates that the referenced symbol is known (either a type or a variable in scope),
     * then returns the node with the expression type set to {@code def.class}. Full
     * resolution into a {@link TypedInterfaceReferenceNode} or
     * {@link TypedCaptureReferenceNode} requires the target functional-interface type from
     * the enclosing context and is deferred to a downstream phase.
     */
    private ExpressionNode visitFunctionReference(FunctionReferenceNode node) {
        String symbol    = node.getSymbol();
        String methodName = node.getMethodName();

        // Validate symbol: must be a known type or a variable in scope
        boolean isType = scriptScope.getPainlessLookup().canonicalTypeNameToType(symbol) != null;
        boolean isVar  = isType == false && currentScope().isVariableDefined(symbol);
        boolean isThis = "this".equals(symbol);

        if (isType == false && isVar == false && isThis == false) {
            throw node.createError(new IllegalArgumentException(
                "cannot resolve function reference [" + symbol + "::" + methodName + "]"));
        }

        // Return with def.class; target-type resolution happens in the caller's context
        return new FunctionReferenceNode(node.getLocation(), symbol, methodName, def.class);
    }

    // =========================================================================
    // NewArrayFunctionReference
    // =========================================================================

    private ExpressionNode visitNewArrayFunctionReference(NewArrayFunctionReferenceNode node) {
        String typeName = node.getCanonicalTypeName();
        Class<?> type = scriptScope.getPainlessLookup().canonicalTypeNameToType(typeName);
        if (type == null) {
            throw node.createError(new IllegalArgumentException(
                "invalid new-array function reference: unknown type [" + typeName + "]"));
        }
        Class<?> arrayType;
        try {
            arrayType = java.lang.reflect.Array.newInstance(type, 0).getClass();
        } catch (Exception e) {
            throw node.createError(new IllegalArgumentException(
                "invalid new-array function reference: cannot create array of type [" + typeName + "]", e));
        }
        return new NewArrayFunctionReferenceNode(node.getLocation(), typeName, arrayType);
    }
}
