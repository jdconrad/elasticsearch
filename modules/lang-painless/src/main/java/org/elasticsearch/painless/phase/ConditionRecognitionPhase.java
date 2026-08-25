/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.painless.phase;

import org.elasticsearch.logging.LogManager;
import org.elasticsearch.logging.Logger;
import org.elasticsearch.painless.ConditionProgram;
import org.elasticsearch.painless.Operation;
import org.elasticsearch.painless.ir.BinaryImplNode;
import org.elasticsearch.painless.ir.BlockNode;
import org.elasticsearch.painless.ir.BooleanNode;
import org.elasticsearch.painless.ir.ClassNode;
import org.elasticsearch.painless.ir.ComparisonNode;
import org.elasticsearch.painless.ir.ConstantNode;
import org.elasticsearch.painless.ir.DeclarationNode;
import org.elasticsearch.painless.ir.ExpressionNode;
import org.elasticsearch.painless.ir.FunctionNode;
import org.elasticsearch.painless.ir.InstanceofNode;
import org.elasticsearch.painless.ir.InvokeCallNode;
import org.elasticsearch.painless.ir.LoadDotDefNode;
import org.elasticsearch.painless.ir.LoadMapShortcutNode;
import org.elasticsearch.painless.ir.LoadVariableNode;
import org.elasticsearch.painless.ir.NullNode;
import org.elasticsearch.painless.ir.NullSafeSubNode;
import org.elasticsearch.painless.ir.ReturnNode;
import org.elasticsearch.painless.ir.StatementNode;
import org.elasticsearch.painless.ir.TryNode;
import org.elasticsearch.painless.ir.UnaryMathNode;
import org.elasticsearch.painless.lookup.PainlessLookup;
import org.elasticsearch.painless.lookup.def;
import org.elasticsearch.painless.symbol.IRDecorations.IRDComparisonType;
import org.elasticsearch.painless.symbol.IRDecorations.IRDConstant;
import org.elasticsearch.painless.symbol.IRDecorations.IRDInstanceType;
import org.elasticsearch.painless.symbol.IRDecorations.IRDName;
import org.elasticsearch.painless.symbol.IRDecorations.IRDOperation;
import org.elasticsearch.painless.symbol.IRDecorations.IRDValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Recognises a boolean condition that can be represented as data instead of as a generated class,
 * producing a {@link ConditionProgram}, or {@code null} when the condition needs normal compilation.
 * <p>
 * Ingest pipelines allow a Painless {@code if} on every processor, and integration packages use that as
 * the idiom for "only run this when the field is present". Conditions therefore vastly outnumber real
 * {@code script} processors -- roughly twelve to one on a large installation -- and each currently costs
 * a pair of generated classes under its own classloader. The cost is per-condition, not per-byte, so
 * tens of thousands of 48-byte null checks dominate the script engine's metaspace footprint.
 * <p>
 * This runs late, after the optimisation phases, so that constants have already been folded and
 * {@code ['a','b'].contains(x)} has already been rewritten by {@link DefaultConstantListOptimizationPhase}
 * into a constant {@link Set}. Matching post-folding means every literal value this phase reads was
 * computed by the compiler rather than re-derived here, which is the same reason the evaluator delegates
 * to {@code Def.lookupGetter} and {@code DefMath.lookupBinary} rather than reimplementing them.
 * <p>
 * It must run <em>before</em> {@link DefaultStaticConstantExtractionPhase}, which moves non-primitive
 * constants (including that folded {@link Set}) out to static fields on the generated class and replaces
 * the node with a field load.
 * <p>
 * Anything unrecognised returns {@code null} and is compiled exactly as before, so a gap here costs only
 * a generated class, never correctness.
 */
public class ConditionRecognitionPhase {

    private static final Logger logger = LogManager.getLogger(ConditionRecognitionPhase.class);

    /** Generated implementations of the {@link org.elasticsearch.painless.PainlessScript} accessors. */
    private static final Set<String> SYNTHETIC_FUNCTIONS = Set.of("getName", "getSource", "getStatements");

    /** Prefix of the {@link org.elasticsearch.painless.Location} used for compiler-injected statements. */
    private static final String INTERNAL_LOCATION = "$internal$";

    private final PainlessLookup painlessLookup;
    private final List<ConditionProgram.Path> paths = new ArrayList<>();
    private final List<Object> constants = new ArrayList<>();

    private ConditionRecognitionPhase(PainlessLookup painlessLookup) {
        this.painlessLookup = painlessLookup;
    }

    /** Thrown internally to abandon recognition; never escapes {@link #recognize}. */
    private static final class Unsupported extends RuntimeException {
        Unsupported(String reason) {
            super(reason, null, false, false);
        }
    }

    private static Unsupported unsupported(String reason) {
        return new Unsupported(reason);
    }

    /**
     * @return a program equivalent to {@code irClassNode}, or {@code null} if it must be compiled
     */
    public static ConditionProgram recognize(ClassNode irClassNode, PainlessLookup painlessLookup) {
        try {
            ExpressionNode irExpressionNode = singleReturnExpression(irClassNode);

            if (irExpressionNode == null) {
                return null;
            }

            ConditionRecognitionPhase phase = new ConditionRecognitionPhase(painlessLookup);
            List<ConditionProgram.Check> checks = new ArrayList<>();
            Boolean all = phase.flatten(irExpressionNode, checks, null);

            return new ConditionProgram(
                phase.paths.toArray(new ConditionProgram.Path[0]),
                phase.constants.toArray(),
                checks.toArray(new ConditionProgram.Check[0]),
                all == null || all,
                painlessLookup
            );
        } catch (Unsupported unsupported) {
            logger.trace(() -> "condition not recognised: " + unsupported.getMessage());
            return null;
        }
    }

    /** The body must be exactly {@code return <expression>;}: no locals, no control flow, no functions. */
    private static ExpressionNode singleReturnExpression(ClassNode irClassNode) {
        FunctionNode irExecuteNode = null;

        for (FunctionNode irFunctionNode : irClassNode.getFunctionsNodes()) {
            String name = irFunctionNode.getDecorationValue(IRDName.class);

            // Generated support functions carry no user code: the PainlessScript accessors, plus
            // $-prefixed helpers such as $bootstrapDef, which every def navigation pulls in. Anything
            // else is a user-defined function, which means this is not a simple condition.
            if (name != null && (name.startsWith("$") || SYNTHETIC_FUNCTIONS.contains(name))) {
                continue;
            }

            if ("execute".equals(name) == false) {
                return null;
            }

            if (irExecuteNode != null) {
                return null;
            }

            irExecuteNode = irFunctionNode;
        }

        if (irExecuteNode == null) {
            return null;
        }

        BlockNode irBlockNode = irExecuteNode.getBlockNode();

        if (irBlockNode == null || irBlockNode.getStatementsNodes().size() != 1) {
            return null;
        }

        StatementNode irStatementNode = irBlockNode.getStatementsNodes().get(0);

        /*
         * PainlessUserTreeToIRTreePhase#injectSandboxExceptions wraps every execute body in a try that
         * converts throwables into a ScriptException. The user's code is the block inside it; a recognised
         * condition reproduces that conversion itself, in ProgramConditionalFactory.
         */
        if (irStatementNode instanceof TryNode irTryNode) {
            BlockNode irTryBlockNode = irTryNode.getBlockNode();

            if (irTryBlockNode == null) {
                return null;
            }

            irStatementNode = null;

            for (StatementNode irBodyNode : irTryBlockNode.getStatementsNodes()) {
                /*
                 * injectGetsDeclarations prepends a local per used getter, so `ctx` in the source is really
                 * `def ctx = getCtx()`. Those carry an internal location that user code cannot produce.
                 */
                if (irBodyNode instanceof DeclarationNode && irBodyNode.getLocation().getSourceName().startsWith(INTERNAL_LOCATION)) {
                    continue;
                }

                if (irStatementNode != null) {
                    return null;
                }

                irStatementNode = irBodyNode;
            }

            if (irStatementNode == null) {
                return null;
            }
        }

        return irStatementNode instanceof ReturnNode irReturnNode ? irReturnNode.getExpressionNode() : null;
    }

    /**
     * Flattens a uniformly {@code &&}-joined or {@code ||}-joined expression into a list of checks.
     * Mixed operators are rejected because their precedence is not expressible in a flat list.
     *
     * @param all the operator seen so far, or {@code null} if none yet
     * @return the operator for the whole expression
     */
    private Boolean flatten(ExpressionNode irExpressionNode, List<ConditionProgram.Check> checks, Boolean all) {
        if (irExpressionNode instanceof BooleanNode irBooleanNode) {
            Operation operation = irBooleanNode.getDecorationValue(IRDOperation.class);

            if (operation != Operation.AND && operation != Operation.OR) {
                throw unsupported("boolean operator other than && or ||");
            }

            boolean and = operation == Operation.AND;

            if (all != null && all != and) {
                throw unsupported("mixed && and ||");
            }

            return flatten(irBooleanNode.getRightNode(), checks, flatten(irBooleanNode.getLeftNode(), checks, and));
        }

        checks.add(check(irExpressionNode));

        return all;
    }

    private ConditionProgram.Check check(ExpressionNode irExpressionNode) {
        /*
         * A leading `!` negates the check rather than changing its shape, so it folds into the Check's
         * negate flag. Repeated negation cancels.
         */
        boolean negate = false;

        while (irExpressionNode instanceof UnaryMathNode irUnaryMathNode
            && irUnaryMathNode.getDecorationValue(IRDOperation.class) == Operation.NOT) {
            negate = negate == false;
            irExpressionNode = irUnaryMathNode.getChildNode();
        }

        if (negate) {
            ConditionProgram.Check check = check(irExpressionNode);

            return new ConditionProgram.Check(
                check.leftKind(),
                check.leftIndex(),
                check.op(),
                check.rightKind(),
                check.rightIndex(),
                check.negate() == false
            );
        }

        if (irExpressionNode instanceof ComparisonNode irComparisonNode) {
            return comparison(irComparisonNode);
        }

        if (irExpressionNode instanceof InstanceofNode irInstanceofNode) {
            return instanceOf(irInstanceofNode);
        }

        if (irExpressionNode instanceof BinaryImplNode irBinaryImplNode) {
            ConditionProgram.Check stringEquals = constantStringEquals(irBinaryImplNode);

            return stringEquals != null ? stringEquals : constantSetContains(irBinaryImplNode);
        }

        throw unsupported("check: unhandled node " + irExpressionNode.getClass().getSimpleName());
    }

    private ConditionProgram.Check comparison(ComparisonNode irComparisonNode) {
        Operation operation = irComparisonNode.getDecorationValue(IRDOperation.class);

        // EQR/NER are reference equality and are deliberately not folded into EQ/NE here
        byte op = switch (operation) {
            case EQ -> ConditionProgram.EQ;
            case NE -> ConditionProgram.NE;
            case LT -> ConditionProgram.LT;
            case LTE -> ConditionProgram.LTE;
            case GT -> ConditionProgram.GT;
            case GTE -> ConditionProgram.GTE;
            default -> throw unsupported("comparison op " + operation);
        };

        // Only def-typed comparisons are recognised. A primitive- or String-typed comparison compiles to
        // direct bytecode that is cheaper than anything done here, and follows different promotion rules.
        Class<?> comparisonType = irComparisonNode.getDecorationValue(IRDComparisonType.class);

        if (comparisonType != def.class && comparisonType != Object.class) {
            throw unsupported("comparison type " + comparisonType);
        }

        /*
         * A comparison against the null literal is emitted by code generation as a bare ifNull/ifNonNull
         * rather than a def call, so it gets its own op here and needs no bootstrap.
         */
        boolean leftIsNull = irComparisonNode.getLeftNode() instanceof NullNode;
        boolean rightIsNull = irComparisonNode.getRightNode() instanceof NullNode;

        if ((op == ConditionProgram.EQ || op == ConditionProgram.NE) && (leftIsNull || rightIsNull)) {
            if (leftIsNull && rightIsNull) {
                throw unsupported("null compared to null");
            }

            Operand value = operand(leftIsNull ? irComparisonNode.getRightNode() : irComparisonNode.getLeftNode());

            return new ConditionProgram.Check(
                value.kind(),
                value.index(),
                op == ConditionProgram.EQ ? ConditionProgram.IS_NULL : ConditionProgram.IS_NOT_NULL,
                ConditionProgram.OPERAND_CONSTANT,
                0,
                false
            );
        }

        Operand left = operand(irComparisonNode.getLeftNode());
        Operand right = operand(irComparisonNode.getRightNode());

        return new ConditionProgram.Check(left.kind(), left.index(), op, right.kind(), right.index(), false);
    }

    private ConditionProgram.Check instanceOf(InstanceofNode irInstanceofNode) {
        Class<?> instanceType = irInstanceofNode.getDecorationValue(IRDInstanceType.class);

        // a def instance type is always true and is already special cased during code generation
        if (instanceType == null || instanceType == def.class || instanceType.isPrimitive()) {
            throw unsupported("instanceof type not usable");
        }

        Operand child = operand(irInstanceofNode.getChildNode());

        if (child.kind() != ConditionProgram.OPERAND_PATH) {
            throw unsupported("instanceof operand is not a ctx path");
        }

        return new ConditionProgram.Check(
            child.kind(),
            child.index(),
            ConditionProgram.INSTANCEOF,
            ConditionProgram.OPERAND_CONSTANT,
            constant(instanceType),
            false
        );
    }

    /**
     * Recognises {@code x == "literal"} in the form {@link DefaultEqualityMethodOptimizationPhase} leaves it:
     * the constant hoisted to the receiver and a direct {@code String.equals} call taking the value. That
     * phase runs immediately before this one, so a string comparison never arrives as a ComparisonNode.
     * {@code !=} arrives wrapped in a NOT, which {@link #check} has already folded into the negate flag.
     */
    private ConditionProgram.Check constantStringEquals(BinaryImplNode irBinaryImplNode) {
        if (irBinaryImplNode.getLeftNode() instanceof ConstantNode irConstantNode
            && irBinaryImplNode.getRightNode() instanceof InvokeCallNode irInvokeCallNode
            && irConstantNode.getDecorationValue(IRDConstant.class) instanceof String constant
            && irInvokeCallNode.getMethod() != null
            // painless resolves this to the inherited Object.equals, so match on name and arity; the
            // receiver being a constant String is what makes String.equals semantics the right ones
            && "equals".equals(irInvokeCallNode.getMethod().javaMethod().getName())
            && irInvokeCallNode.getArgumentNodes().size() == 1) {

            Operand value = operand(irInvokeCallNode.getArgumentNodes().get(0));

            return new ConditionProgram.Check(
                value.kind(),
                value.index(),
                ConditionProgram.CONSTANT_STRING_EQUALS,
                ConditionProgram.OPERAND_CONSTANT,
                constant(constant),
                false
            );
        }

        return null;
    }

    /**
     * Recognises {@code ['a','b'].contains(x)} in the form {@link DefaultConstantListOptimizationPhase}
     * leaves it: a constant {@link Set} on the left and a {@code HashSet.contains} call on the right.
     */
    private ConditionProgram.Check constantSetContains(BinaryImplNode irBinaryImplNode) {
        if (irBinaryImplNode.getLeftNode() instanceof ConstantNode irConstantNode
            && irBinaryImplNode.getRightNode() instanceof InvokeCallNode irInvokeCallNode
            && irConstantNode.getDecorationValue(IRDConstant.class) instanceof Set<?> constantSet
            && "contains".equals(irInvokeCallNode.getMethod().javaMethod().getName())
            && irInvokeCallNode.getArgumentNodes().size() == 1) {

            Operand argument = operand(irInvokeCallNode.getArgumentNodes().get(0));

            if (argument.kind() != ConditionProgram.OPERAND_PATH) {
                throw unsupported("contains argument is not a ctx path");
            }

            return new ConditionProgram.Check(
                argument.kind(),
                argument.index(),
                ConditionProgram.CONTAINED_BY,
                ConditionProgram.OPERAND_CONSTANT,
                constant(constantSet),
                false
            );
        }

        throw unsupported("not a constant-set contains");
    }

    private record Operand(byte kind, int index) {}

    private Operand operand(ExpressionNode irExpressionNode) {
        if (irExpressionNode instanceof NullNode) {
            return new Operand(ConditionProgram.OPERAND_CONSTANT, constant(null));
        }

        if (irExpressionNode instanceof ConstantNode irConstantNode) {
            return new Operand(ConditionProgram.OPERAND_CONSTANT, constant(irConstantNode.getDecorationValue(IRDConstant.class)));
        }

        List<ConditionProgram.Segment> segments = new ArrayList<>();
        boolean fromParams = navigate(irExpressionNode, segments);
        Collections.reverse(segments);
        paths.add(new ConditionProgram.Path(segments.toArray(new ConditionProgram.Segment[0]), fromParams));

        return new Operand(ConditionProgram.OPERAND_PATH, paths.size() - 1);
    }

    /**
     * Walks a {@code ctx.a.b} chain, which the IR nests as
     * {@code BinaryImpl(BinaryImpl(LoadVariable(ctx), LoadDotDef(a)), LoadDotDef(b))}, collecting segments
     * innermost-first for the caller to reverse.
     *
     * @return true when the chain is rooted at {@code params} rather than {@code ctx}
     */
    private boolean navigate(ExpressionNode irExpressionNode, List<ConditionProgram.Segment> segments) {
        if (irExpressionNode instanceof LoadVariableNode irLoadVariableNode) {
            String name = irLoadVariableNode.getDecorationValue(IRDName.class);

            if ("ctx".equals(name)) {
                return false;
            }

            if ("params".equals(name)) {
                return true;
            }

            throw unsupported("root variable " + name); // some other local
        }

        if (irExpressionNode instanceof BinaryImplNode irBinaryImplNode) {
            ExpressionNode irRightNode = irBinaryImplNode.getRightNode();
            ExpressionNode irPrefixNode = irBinaryImplNode.getLeftNode();
            boolean nullSafe = false;

            if (irRightNode instanceof NullSafeSubNode irNullSafeSubNode) {
                nullSafe = true;
                irRightNode = irNullSafeSubNode.getChildNode();
            }

            /*
             * A dynamic access, when the receiver is def. DefaultUserTreeToIRTreePhase#buildLoadStore
             * leaves this as BinaryImpl(prefix, LoadDotDef) with the field name on the load node.
             */
            if (irRightNode instanceof LoadDotDefNode irLoadDotDefNode) {
                segments.add(new ConditionProgram.Segment(irLoadDotDefNode.getDecorationValue(IRDValue.class), nullSafe));

                return navigate(irPrefixNode, segments);
            }

            /*
             * A static map access, used when the receiver's declared type is Map -- which is the case for
             * the first hop off ctx, since IngestConditionalScript#getCtx returns Map<String, Object>.
             * buildLoadStore pairs the key with the prefix, so the key is one level in:
             *     not null safe: BinaryImpl(BinaryImpl(prefix, key), LoadMapShortcut)
             *     null safe:     BinaryImpl(prefix, NullSafeSub(BinaryImpl(key, LoadMapShortcut)))
             */
            if (nullSafe && irRightNode instanceof BinaryImplNode irKeyedNode) {
                if (irKeyedNode.getRightNode() instanceof LoadMapShortcutNode) {
                    segments.add(new ConditionProgram.Segment(constantKey(irKeyedNode.getLeftNode()), true));

                    return navigate(irPrefixNode, segments);
                }
            }

            if (irRightNode instanceof LoadMapShortcutNode) {
                if (irPrefixNode instanceof BinaryImplNode irKeyedNode) {
                    segments.add(new ConditionProgram.Segment(constantKey(irKeyedNode.getRightNode()), false));

                    return navigate(irKeyedNode.getLeftNode(), segments);
                }

                throw unsupported("map shortcut without a constant key");
            }
        }

        throw unsupported("navigate: unhandled node " + irExpressionNode.getClass().getSimpleName());
    }

    /** A map key must be a literal; a computed key such as {@code ctx[ctx.a]} is not representable. */
    private String constantKey(ExpressionNode irExpressionNode) {
        if (irExpressionNode instanceof ConstantNode irConstantNode
            && irConstantNode.getDecorationValue(IRDConstant.class) instanceof String key) {
            return key;
        }

        throw unsupported("non-constant map key");
    }

    private int constant(Object value) {
        constants.add(value);

        return constants.size() - 1;
    }
}
