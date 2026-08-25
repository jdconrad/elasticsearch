/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.painless;

import org.elasticsearch.painless.lookup.PainlessLookup;
import org.elasticsearch.painless.symbol.FunctionTable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Map;

/**
 * A boolean ingest condition represented as data rather than as a generated class.
 * <p>
 * Ingest pipelines put a Painless {@code if} on any processor, and integration packages use that as the
 * idiom for "only run this when the field is present". The result is that conditions vastly outnumber
 * real {@code script} processors -- on a large installation, tens of thousands of them, each currently
 * compiled into its own pair of classes under its own {@link Compiler.Loader}. The bytecode for a
 * 48-byte condition costs several KB of metaspace, and the cost is per-condition, not per-byte.
 * <p>
 * A condition that fits this shape -- a single boolean expression made of ctx/params navigation,
 * null/instanceof/relational comparisons against constants, and one level of {@code &&} or {@code ||} --
 * is instead stored as three arrays and evaluated by this class. One class serves every such condition.
 * Anything that does not fit is compiled exactly as before; see
 * {@link org.elasticsearch.painless.phase.ConditionRecognitionPhase}.
 * <p>
 * Semantics are inherited rather than reimplemented: field access goes through
 * {@link Def#lookupGetter} and comparison through {@link DefMath#lookupBinary}, which are the same
 * entry points {@link DefBootstrap} binds at each generated call site.
 */
public final class ConditionProgram {

    // comparison ops
    public static final byte EQ = 0;
    public static final byte NE = 1;
    public static final byte LT = 2;
    public static final byte LTE = 3;
    public static final byte GT = 4;
    public static final byte GTE = 5;
    public static final byte INSTANCEOF = 6;
    public static final byte TRUTHY = 7;
    /** {@code x == null}; code generation emits a bare ifNull for this rather than a def call. */
    public static final byte IS_NULL = 9;
    /** {@code x != null}; likewise a bare ifNonNull. */
    public static final byte IS_NOT_NULL = 10;
    /** {@code ['a','b'].contains(x)}, after the constant list optimisation has folded the list to a Set. */
    public static final byte CONTAINED_BY = 8;
    /**
     * {@code x == "literal"}, which {@link org.elasticsearch.painless.phase.DefaultEqualityMethodOptimizationPhase}
     * has already rewritten into a direct {@code String.equals} call with the constant as the receiver.
     */
    public static final byte CONSTANT_STRING_EQUALS = 11;

    // operand kinds
    public static final byte OPERAND_PATH = 0;
    public static final byte OPERAND_CONSTANT = 1;
    /** An allowlisted method call on another operand, such as {@code ctx.tags.contains('x')}. */
    public static final byte OPERAND_CALL = 2;

    /**
     * One step of a {@code ctx.a.b} navigation.
     * <p>
     * {@code nullSafe} records that the source wrote {@code ?.} at this step. Painless's {@code ?.} makes
     * only <i>this</i> access yield null; the chain continues, so a following plain {@code .} on the null
     * result still throws. {@code ctx.a?.b.c} with {@code a} absent throws rather than returning null.
     */
    public record Segment(String name, boolean nullSafe) {}

    /** A ctx or params navigation path, with a one-entry inline cache per segment. */
    public static final class Path {
        private final Segment[] segments;
        private final boolean fromParams;

        /**
         * Resolved getter for the last-seen receiver class, mirroring {@link DefBootstrap}'s monomorphic
         * inline cache. Written as an immutable pair so a race can only cost a recompute, never tear.
         */
        private volatile CachedGetter[] cache;

        private record CachedGetter(Class<?> receiver, MethodHandle getter) {}

        public Path(Segment[] segments, boolean fromParams) {
            this.segments = segments;
            this.fromParams = fromParams;
            this.cache = new CachedGetter[segments.length];
        }

        public Object load(Map<String, Object> ctx, Map<String, Object> params, PainlessLookup lookup) throws Throwable {
            Object value = fromParams ? params : ctx;
            CachedGetter[] local = cache;

            for (int i = 0; i < segments.length; i++) {
                Segment segment = segments[i];

                if (value == null) {
                    if (segment.nullSafe()) {
                        continue; // this access yields null; the rest of the chain still runs
                    }
                    throw new NullPointerException("cannot access method/field [" + segment.name() + "] from a null def reference");
                }

                Class<?> receiver = value.getClass();
                CachedGetter cached = local[i];
                if (cached == null || cached.receiver() != receiver) {
                    cached = new CachedGetter(receiver, Def.lookupGetter(lookup, receiver, segment.name()));
                    local[i] = cached;
                }
                value = cached.getter().invoke(value);
            }

            return value;
        }
    }

    /** A single comparison. {@code right} is unused for {@link #TRUTHY}. */
    public record Check(byte leftKind, int leftIndex, byte op, byte rightKind, int rightIndex, boolean negate) {}

    /**
     * A method call whose receiver and arguments are themselves operands.
     * <p>
     * {@code handle} is bound by {@link org.elasticsearch.painless.phase.ConditionRecognitionPhase}: for a
     * statically resolved call it is the {@code PainlessMethod}'s own handle, and for a def call it is the
     * dynamic invoker of a {@link DefBootstrap#METHOD_CALL} site built with the same name and method type
     * code generation would have emitted. Either way the dispatch is the one the compiled script performs.
     */
    public record Call(byte receiverKind, int receiverIndex, MethodHandle handle, byte[] argKinds, int[] argIndexes) {}

    /**
     * The boolean structure of the condition. A flat {@code a && b && c} is a single {@link All}, while a
     * mixed expression keeps its nesting, which a flat list of checks cannot express. Both short-circuit.
     */
    public sealed interface Node permits Leaf, All, Any {}

    /** A single {@link Check}, by index into the check table. */
    public record Leaf(int check) implements Node {}

    /** Conjunction. */
    public record All(Node[] children) implements Node {}

    /** Disjunction. */
    public record Any(Node[] children) implements Node {}

    private final Path[] paths;
    private final Object[] constants;
    private final Call[] calls;
    private final Check[] checks;
    private final Node root;
    private final PainlessLookup lookup;

    /**
     * One handle per comparison check, or null for checks that need none. Built through
     * {@link DefBootstrap}, the same bootstrap the generated code binds at each comparison call site, so
     * promotion, null handling and argument conversion behave identically -- calling {@link DefMath}
     * directly is a level too low and throws on operands the real call site would have converted. Each is
     * a monomorphic inline cache, exactly as it would be in bytecode.
     */
    private final MethodHandle[] comparators;

    public ConditionProgram(Path[] paths, Object[] constants, Call[] calls, Check[] checks, Node root, PainlessLookup lookup) {
        this.paths = paths;
        this.constants = constants;
        this.calls = calls;
        this.checks = checks;
        this.root = root;
        this.lookup = lookup;
        this.comparators = new MethodHandle[checks.length];

        for (int i = 0; i < checks.length; i++) {
            this.comparators[i] = comparator(checks[i].op(), lookup);
        }
    }

    private static MethodHandle comparator(byte op, PainlessLookup lookup) {
        String name = switch (op) {
            case EQ, NE -> "eq";
            case LT -> "lt";
            case LTE -> "lte";
            case GT -> "gt";
            case GTE -> "gte";
            default -> null;
        };

        if (name == null) {
            return null;
        }

        // equality allows null operands; the relational operators do not, matching code generation
        int flags = "eq".equals(name) ? DefBootstrap.OPERATOR_ALLOWS_NULL : 0;

        return DefBootstrap.bootstrap(
            lookup,
            new FunctionTable(),
            Map.of(),
            MethodHandles.lookup(),
            name,
            MethodType.methodType(boolean.class, Object.class, Object.class),
            0,
            DefBootstrap.BINARY_OPERATOR,
            flags
        ).dynamicInvoker();
    }

    public boolean execute(Map<String, Object> ctx, Map<String, Object> params) throws Throwable {
        return evaluate(root, ctx, params);
    }

    private boolean evaluate(Node node, Map<String, Object> ctx, Map<String, Object> params) throws Throwable {
        return switch (node) {
            case Leaf leaf -> test(checks[leaf.check()], leaf.check(), ctx, params);
            case All all -> {
                for (Node child : all.children()) {
                    if (evaluate(child, ctx, params) == false) {
                        yield false; // short-circuits
                    }
                }
                yield true;
            }
            case Any any -> {
                for (Node child : any.children()) {
                    if (evaluate(child, ctx, params)) {
                        yield true; // short-circuits
                    }
                }
                yield false;
            }
        };
    }

    private boolean test(Check check, int index, Map<String, Object> ctx, Map<String, Object> params) throws Throwable {
        Object left = operand(check.leftKind(), check.leftIndex(), ctx, params);
        boolean result;

        switch (check.op()) {
            case IS_NULL -> result = left == null;
            case IS_NOT_NULL -> result = left != null;
            case TRUTHY -> result = (Boolean) left;
            case INSTANCEOF -> result = ((Class<?>) constants[check.rightIndex()]).isInstance(left);
            case CONSTANT_STRING_EQUALS -> result = constants[check.rightIndex()].equals(left);
            case CONTAINED_BY ->
                // matches HashSet.contains, which the generated code calls once the list has been folded
                result = ((java.util.Set<?>) constants[check.rightIndex()]).contains(left);
            default -> {
                Object right = operand(check.rightKind(), check.rightIndex(), ctx, params);
                boolean equalOrOrdered = (boolean) comparators[index].invokeExact(left, right);
                result = equalOrOrdered ^ (check.op() == NE);
            }
        }

        return result ^ check.negate();
    }

    private Object operand(byte kind, int index, Map<String, Object> ctx, Map<String, Object> params) throws Throwable {
        return switch (kind) {
            case OPERAND_PATH -> paths[index].load(ctx, params, lookup);
            case OPERAND_CONSTANT -> constants[index];
            case OPERAND_CALL -> call(calls[index], ctx, params);
            default -> throw new IllegalStateException("unexpected operand kind [" + kind + "]");
        };
    }

    private Object call(Call call, Map<String, Object> ctx, Map<String, Object> params) throws Throwable {
        Object receiver = operand(call.receiverKind(), call.receiverIndex(), ctx, params);

        if (receiver == null) {
            // the compiled call would dereference null here, and reports it the same way
            throw new NullPointerException("cannot call method on a null def reference");
        }

        byte[] argKinds = call.argKinds();
        Object[] arguments = new Object[argKinds.length + 1];
        arguments[0] = receiver;

        for (int i = 0; i < argKinds.length; i++) {
            arguments[i + 1] = operand(argKinds[i], call.argIndexes()[i], ctx, params);
        }

        return call.handle().invokeWithArguments(arguments);
    }
}
