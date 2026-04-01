/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.painless.antlr;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DiagnosticErrorListener;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.elasticsearch.painless.CompilerSettings;
import org.elasticsearch.painless.Location;
import org.elasticsearch.painless.Operation;
import org.elasticsearch.painless.antlr.PainlessParser.AddsubContext;
import org.elasticsearch.painless.antlr.PainlessParser.AfterthoughtContext;
import org.elasticsearch.painless.antlr.PainlessParser.ArgumentContext;
import org.elasticsearch.painless.antlr.PainlessParser.ArgumentsContext;
import org.elasticsearch.painless.antlr.PainlessParser.AssignmentContext;
import org.elasticsearch.painless.antlr.PainlessParser.BinaryContext;
import org.elasticsearch.painless.antlr.PainlessParser.BlockContext;
import org.elasticsearch.painless.antlr.PainlessParser.BoolContext;
import org.elasticsearch.painless.antlr.PainlessParser.BraceaccessContext;
import org.elasticsearch.painless.antlr.PainlessParser.BreakContext;
import org.elasticsearch.painless.antlr.PainlessParser.CallinvokeContext;
import org.elasticsearch.painless.antlr.PainlessParser.CalllocalContext;
import org.elasticsearch.painless.antlr.PainlessParser.CastContext;
import org.elasticsearch.painless.antlr.PainlessParser.ClassfuncrefContext;
import org.elasticsearch.painless.antlr.PainlessParser.CompContext;
import org.elasticsearch.painless.antlr.PainlessParser.ConditionalContext;
import org.elasticsearch.painless.antlr.PainlessParser.ConstructorfuncrefContext;
import org.elasticsearch.painless.antlr.PainlessParser.ContinueContext;
import org.elasticsearch.painless.antlr.PainlessParser.DeclContext;
import org.elasticsearch.painless.antlr.PainlessParser.DeclarationContext;
import org.elasticsearch.painless.antlr.PainlessParser.DecltypeContext;
import org.elasticsearch.painless.antlr.PainlessParser.DeclvarContext;
import org.elasticsearch.painless.antlr.PainlessParser.DoContext;
import org.elasticsearch.painless.antlr.PainlessParser.DynamicContext;
import org.elasticsearch.painless.antlr.PainlessParser.EachContext;
import org.elasticsearch.painless.antlr.PainlessParser.ElvisContext;
import org.elasticsearch.painless.antlr.PainlessParser.EmptyContext;
import org.elasticsearch.painless.antlr.PainlessParser.ExprContext;
import org.elasticsearch.painless.antlr.PainlessParser.ExpressionContext;
import org.elasticsearch.painless.antlr.PainlessParser.FalseContext;
import org.elasticsearch.painless.antlr.PainlessParser.FieldaccessContext;
import org.elasticsearch.painless.antlr.PainlessParser.ForContext;
import org.elasticsearch.painless.antlr.PainlessParser.FunctionContext;
import org.elasticsearch.painless.antlr.PainlessParser.IfContext;
import org.elasticsearch.painless.antlr.PainlessParser.IneachContext;
import org.elasticsearch.painless.antlr.PainlessParser.InitializerContext;
import org.elasticsearch.painless.antlr.PainlessParser.InstanceofContext;
import org.elasticsearch.painless.antlr.PainlessParser.LambdaContext;
import org.elasticsearch.painless.antlr.PainlessParser.LamtypeContext;
import org.elasticsearch.painless.antlr.PainlessParser.ListinitContext;
import org.elasticsearch.painless.antlr.PainlessParser.ListinitializerContext;
import org.elasticsearch.painless.antlr.PainlessParser.LocalfuncrefContext;
import org.elasticsearch.painless.antlr.PainlessParser.MapinitContext;
import org.elasticsearch.painless.antlr.PainlessParser.MapinitializerContext;
import org.elasticsearch.painless.antlr.PainlessParser.MaptokenContext;
import org.elasticsearch.painless.antlr.PainlessParser.NewarrayContext;
import org.elasticsearch.painless.antlr.PainlessParser.NewinitializedarrayContext;
import org.elasticsearch.painless.antlr.PainlessParser.NewobjectContext;
import org.elasticsearch.painless.antlr.PainlessParser.NewstandardarrayContext;
import org.elasticsearch.painless.antlr.PainlessParser.NonconditionalContext;
import org.elasticsearch.painless.antlr.PainlessParser.NotContext;
import org.elasticsearch.painless.antlr.PainlessParser.NotaddsubContext;
import org.elasticsearch.painless.antlr.PainlessParser.NullContext;
import org.elasticsearch.painless.antlr.PainlessParser.NumericContext;
import org.elasticsearch.painless.antlr.PainlessParser.ParametersContext;
import org.elasticsearch.painless.antlr.PainlessParser.PostContext;
import org.elasticsearch.painless.antlr.PainlessParser.PostdotContext;
import org.elasticsearch.painless.antlr.PainlessParser.PostfixContext;
import org.elasticsearch.painless.antlr.PainlessParser.PreContext;
import org.elasticsearch.painless.antlr.PainlessParser.PrecedenceContext;
import org.elasticsearch.painless.antlr.PainlessParser.ReadContext;
import org.elasticsearch.painless.antlr.PainlessParser.RegexContext;
import org.elasticsearch.painless.antlr.PainlessParser.ReturnContext;
import org.elasticsearch.painless.antlr.PainlessParser.SingleContext;
import org.elasticsearch.painless.antlr.PainlessParser.SourceContext;
import org.elasticsearch.painless.antlr.PainlessParser.StatementContext;
import org.elasticsearch.painless.antlr.PainlessParser.StringContext;
import org.elasticsearch.painless.antlr.PainlessParser.ThrowContext;
import org.elasticsearch.painless.antlr.PainlessParser.TrailerContext;
import org.elasticsearch.painless.antlr.PainlessParser.TrapContext;
import org.elasticsearch.painless.antlr.PainlessParser.TrueContext;
import org.elasticsearch.painless.antlr.PainlessParser.TryContext;
import org.elasticsearch.painless.antlr.PainlessParser.TypeContext;
import org.elasticsearch.painless.antlr.PainlessParser.VariableContext;
import org.elasticsearch.painless.antlr.PainlessParser.WhileContext;
import org.elasticsearch.painless.node.AssignmentNode;
import org.elasticsearch.painless.node.BlockNode;
import org.elasticsearch.painless.node.BinaryMathNode;
import org.elasticsearch.painless.node.BooleanNode;
import org.elasticsearch.painless.node.BraceAccessNode;
import org.elasticsearch.painless.node.BreakNode;
import org.elasticsearch.painless.node.CatchNode;
import org.elasticsearch.painless.node.ClassNode;
import org.elasticsearch.painless.node.ComparisonNode;
import org.elasticsearch.painless.node.ConditionalNode;
import org.elasticsearch.painless.node.ConstantNode;
import org.elasticsearch.painless.node.ContinueNode;
import org.elasticsearch.painless.node.DecimalNode;
import org.elasticsearch.painless.node.DeclarationBlockNode;
import org.elasticsearch.painless.node.DeclarationNode;
import org.elasticsearch.painless.node.DoWhileLoopNode;
import org.elasticsearch.painless.node.DotAccessNode;
import org.elasticsearch.painless.node.ElvisNode;
import org.elasticsearch.painless.node.ExplicitCastNode;
import org.elasticsearch.painless.node.ExpressionNode;
import org.elasticsearch.painless.node.ForEachSourceNode;
import org.elasticsearch.painless.node.ForLoopNode;
import org.elasticsearch.painless.node.FunctionNode;
import org.elasticsearch.painless.node.FunctionReferenceNode;
import org.elasticsearch.painless.node.IfElseNode;
import org.elasticsearch.painless.node.IfNode;
import org.elasticsearch.painless.node.LambdaNode;
import org.elasticsearch.painless.node.ListInitializationNode;
import org.elasticsearch.painless.node.LocalFunctionCallNode;
import org.elasticsearch.painless.node.MapInitializationNode;
import org.elasticsearch.painless.node.MethodCallNode;
import org.elasticsearch.painless.node.NewArrayFunctionReferenceNode;
import org.elasticsearch.painless.node.NewArrayNode;
import org.elasticsearch.painless.node.NewObjectNode;
import org.elasticsearch.painless.node.Node;
import org.elasticsearch.painless.node.NullNode;
import org.elasticsearch.painless.node.NumericNode;
import org.elasticsearch.painless.node.RegexNode;
import org.elasticsearch.painless.node.ReturnNode;
import org.elasticsearch.painless.node.SourceInstanceofNode;
import org.elasticsearch.painless.node.StatementExpressionNode;
import org.elasticsearch.painless.node.StatementNode;
import org.elasticsearch.painless.node.ThrowNode;
import org.elasticsearch.painless.node.TryNode;
import org.elasticsearch.painless.node.UnaryMathNode;
import org.elasticsearch.painless.node.VariableNode;
import org.elasticsearch.painless.node.WhileLoopNode;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Converts the ANTLR tree to a unified Painless node tree. All nodes produced here
 * carry only parse-time information; semantic fields (resolved types, method references,
 * etc.) are null and are filled in by later compiler phases.
 */
public final class Walker extends PainlessParserBaseVisitor<Node> {

    public static ClassNode buildPainlessTree(String sourceName, String sourceText,
                                              CompilerSettings settings) {
        return new Walker(sourceName, sourceText, settings).classNode;
    }

    private final CompilerSettings settings;
    private final String sourceName;

    private final ClassNode classNode;

    private Walker(String sourceName, String sourceText, CompilerSettings settings) {
        this.settings = settings;
        this.sourceName = sourceName;

        this.classNode = (ClassNode) visit(buildAntlrTree(sourceText));
    }

    private SourceContext buildAntlrTree(String sourceString) {
        ANTLRInputStream stream = new ANTLRInputStream(sourceString);
        PainlessLexer lexer = new EnhancedPainlessLexer(stream, sourceName);
        PainlessParser parser = new PainlessParser(new CommonTokenStream(lexer));
        ParserErrorStrategy strategy = new ParserErrorStrategy(sourceName);

        lexer.removeErrorListeners();
        parser.removeErrorListeners();

        if (settings.isPicky()) {
            setupPicky(parser);
        }

        parser.setErrorHandler(strategy);

        return parser.source();
    }

    private static void setupPicky(PainlessParser parser) {
        // Diagnostic listener invokes syntaxError on other listeners for ambiguity issues,
        parser.addErrorListener(new DiagnosticErrorListener(true));
        // a second listener to fail the test when the above happens.
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(
                final Recognizer<?, ?> recognizer,
                final Object offendingSymbol,
                final int line,
                final int charPositionInLine,
                final String msg,
                final RecognitionException e
            ) {
                throw new AssertionError("line: " + line + ", offset: " + charPositionInLine +
                    ", symbol:" + offendingSymbol + " " + msg);
            }
        });

        // Enable exact ambiguity detection (costly). we enable exact since its the default for
        // DiagnosticErrorListener, life is too short to think about what 'inexact ambiguity' might mean.
        parser.getInterpreter().setPredictionMode(PredictionMode.LL_EXACT_AMBIG_DETECTION);
    }

    private Location location(ParserRuleContext ctx) {
        return new Location(sourceName, ctx.getStart().getStartIndex());
    }

    @Override
    public Node visitSource(SourceContext ctx) {
        List<FunctionNode> functions = new ArrayList<>();

        for (FunctionContext function : ctx.function()) {
            functions.add((FunctionNode) visit(function));
        }

        // handle the code to generate the execute method here
        // because the statements come loose from the grammar as
        // part of the overall class
        List<StatementNode> statements = new ArrayList<>();

        for (StatementContext statement : ctx.statement()) {
            statements.add((StatementNode) visit(statement));
        }

        // generate the execute method from the collected statements and parameters
        FunctionNode execute = new FunctionNode(
            location(ctx),
            new BlockNode(location(ctx), statements, false),
            "execute",
            null,        // returnType — resolved by SemanticHeaderPhase
            null,        // typeParameters — resolved by SemanticHeaderPhase
            "<internal>",
            List.of(),
            List.of(),
            Modifier.PUBLIC,
            false,       // isStatic
            false,       // isSynthetic
            false,       // isVarArgs
            false,       // isInstanceCapture
            false,       // isAutoReturnEnabled
            false,       // isInternal
            settings.getMaxLoopCounter()
        );
        functions.add(execute);

        // scriptScope is null here; the Compiler injects it via ClassNode.withScriptScope()
        return new ClassNode(location(ctx), List.of(), functions, null, null);
    }

    @Override
    public Node visitFunction(FunctionContext ctx) {
        String rtnType = ctx.decltype().getText();
        String name = ctx.ID().getText();

        List<String> paramTypes = ctx.parameters().decltype().stream().map(DecltypeContext::getText).toList();
        List<String> paramNames = ctx.parameters().ID().stream().map(TerminalNode::getText).toList();

        List<StatementNode> statements = new ArrayList<>();
        for (StatementContext statement : ctx.block().statement()) {
            statements.add((StatementNode) visit(statement));
        }

        if (ctx.block().dstatement() != null) {
            statements.add((StatementNode) visit(ctx.block().dstatement()));
        }

        return new FunctionNode(
            location(ctx),
            new BlockNode(location(ctx), statements, false),
            name,
            null,        // returnType — resolved by SemanticHeaderPhase
            null,        // typeParameters — resolved by SemanticHeaderPhase
            rtnType,
            paramTypes,
            paramNames,
            Modifier.PUBLIC,
            false,       // isStatic
            false,       // isSynthetic
            false,       // isVarArgs
            false,       // isInstanceCapture
            false,       // isAutoReturnEnabled
            false,       // isInternal
            settings.getMaxLoopCounter()
        );
    }

    @Override
    public Node visitParameters(ParametersContext ctx) {
        throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
    }

    @Override
    public Node visitStatement(StatementContext ctx) {
        if (ctx.rstatement() != null) {
            return visit(ctx.rstatement());
        } else if (ctx.dstatement() != null) {
            return visit(ctx.dstatement());
        } else {
            throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
        }
    }

    @Override
    public Node visitIf(IfContext ctx) {
        ExpressionNode expression = (ExpressionNode) visit(ctx.expression());
        BlockNode ifblock = (BlockNode) visit(ctx.trailer(0));

        if (ctx.trailer().size() > 1) {
            BlockNode elseblock = (BlockNode) visit(ctx.trailer(1));
            return new IfElseNode(location(ctx), expression, ifblock, elseblock, false);
        } else {
            return new IfNode(location(ctx), expression, ifblock, false);
        }
    }

    @Override
    public Node visitWhile(WhileContext ctx) {
        ExpressionNode expression = (ExpressionNode) visit(ctx.expression());

        if (ctx.trailer() != null) {
            BlockNode block = (BlockNode) visit(ctx.trailer());
            return new WhileLoopNode(location(ctx), expression, block, false);
        } else if (ctx.empty() != null) {
            return new WhileLoopNode(location(ctx), expression, null, false);
        } else {
            throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
        }
    }

    @Override
    public Node visitDo(DoContext ctx) {
        ExpressionNode expression = (ExpressionNode) visit(ctx.expression());
        BlockNode block = (BlockNode) visit(ctx.block());
        return new DoWhileLoopNode(location(ctx), expression, block, false);
    }

    @Override
    public Node visitFor(ForContext ctx) {
        Node initializer = ctx.initializer() == null ? null : visit(ctx.initializer());
        ExpressionNode expression = ctx.expression() == null ? null : (ExpressionNode) visit(ctx.expression());
        ExpressionNode afterthought = ctx.afterthought() == null ? null : (ExpressionNode) visit(ctx.afterthought());

        if (ctx.trailer() != null) {
            BlockNode block = (BlockNode) visit(ctx.trailer());
            return new ForLoopNode(location(ctx), initializer, expression, afterthought, block, false);
        } else if (ctx.empty() != null) {
            return new ForLoopNode(location(ctx), initializer, expression, afterthought, null, false);
        } else {
            throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
        }
    }

    @Override
    public Node visitEach(EachContext ctx) {
        String type = ctx.decltype().getText();
        String name = ctx.ID().getText();
        ExpressionNode expression = (ExpressionNode) visit(ctx.expression());
        BlockNode block = (BlockNode) visit(ctx.trailer());
        return new ForEachSourceNode(location(ctx), type, name, expression, block);
    }

    @Override
    public Node visitIneach(IneachContext ctx) {
        String name = ctx.ID().getText();
        ExpressionNode expression = (ExpressionNode) visit(ctx.expression());
        BlockNode block = (BlockNode) visit(ctx.trailer());
        return new ForEachSourceNode(location(ctx), "def", name, expression, block);
    }

    @Override
    public Node visitDecl(DeclContext ctx) {
        return visit(ctx.declaration());
    }

    @Override
    public Node visitContinue(ContinueContext ctx) {
        return new ContinueNode(location(ctx));
    }

    @Override
    public Node visitBreak(BreakContext ctx) {
        return new BreakNode(location(ctx));
    }

    @Override
    public Node visitReturn(ReturnContext ctx) {
        ExpressionNode expression = null;

        if (ctx.expression() != null) {
            expression = (ExpressionNode) visit(ctx.expression());
        }

        return new ReturnNode(location(ctx), expression);
    }

    @Override
    public Node visitTry(TryContext ctx) {
        BlockNode block = (BlockNode) visit(ctx.block());
        List<CatchNode> catches = new ArrayList<>();

        for (TrapContext trap : ctx.trap()) {
            catches.add((CatchNode) visit(trap));
        }

        return new TryNode(location(ctx), block, catches, false);
    }

    @Override
    public Node visitThrow(ThrowContext ctx) {
        ExpressionNode expression = (ExpressionNode) visit(ctx.expression());
        return new ThrowNode(location(ctx), expression);
    }

    @Override
    public Node visitExpr(ExprContext ctx) {
        ExpressionNode expression = (ExpressionNode) visit(ctx.expression());
        return new StatementExpressionNode(location(ctx), expression);
    }

    @Override
    public Node visitTrailer(TrailerContext ctx) {
        if (ctx.block() != null) {
            return visit(ctx.block());
        } else if (ctx.statement() != null) {
            List<StatementNode> statements = new ArrayList<>();
            statements.add((StatementNode) visit(ctx.statement()));
            return new BlockNode(location(ctx), statements, false);
        } else {
            throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
        }
    }

    @Override
    public Node visitBlock(BlockContext ctx) {
        if (ctx.statement().isEmpty() && ctx.dstatement() == null) {
            return null;
        } else {
            List<StatementNode> statements = new ArrayList<>();

            for (StatementContext statement : ctx.statement()) {
                statements.add((StatementNode) visit(statement));
            }

            if (ctx.dstatement() != null) {
                statements.add((StatementNode) visit(ctx.dstatement()));
            }

            return new BlockNode(location(ctx), statements, false);
        }
    }

    @Override
    public Node visitEmpty(EmptyContext ctx) {
        throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
    }

    @Override
    public Node visitInitializer(InitializerContext ctx) {
        if (ctx.declaration() != null) {
            return visit(ctx.declaration());
        } else if (ctx.expression() != null) {
            return visit(ctx.expression());
        } else {
            throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
        }
    }

    @Override
    public Node visitAfterthought(AfterthoughtContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public Node visitDeclaration(DeclarationContext ctx) {
        String type = ctx.decltype().getText();
        List<DeclarationNode> declarations = new ArrayList<>();

        for (DeclvarContext declvar : ctx.declvar()) {
            String name = declvar.ID().getText();
            ExpressionNode expression = declvar.expression() == null
                ? null : (ExpressionNode) visit(declvar.expression());
            declarations.add(new DeclarationNode(location(declvar), expression, null, type, name));
        }

        return new DeclarationBlockNode(location(ctx), declarations);
    }

    @Override
    public Node visitDecltype(DecltypeContext ctx) {
        throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
    }

    @Override
    public Node visitType(TypeContext ctx) {
        throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
    }

    @Override
    public Node visitDeclvar(DeclvarContext ctx) {
        throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
    }

    @Override
    public Node visitTrap(TrapContext ctx) {
        String type = ctx.type().getText();
        String name = ctx.ID().getText();
        BlockNode block = (BlockNode) visit(ctx.block());
        return new CatchNode(location(ctx), block, null, type, name);
    }

    @Override
    public Node visitSingle(SingleContext ctx) {
        return visit(ctx.unary());
    }

    @Override
    public Node visitBinary(BinaryContext ctx) {
        ExpressionNode left = (ExpressionNode) visit(ctx.noncondexpression(0));
        ExpressionNode right = (ExpressionNode) visit(ctx.noncondexpression(1));
        final Operation operation;

        if (ctx.MUL() != null) {
            operation = Operation.MUL;
        } else if (ctx.DIV() != null) {
            operation = Operation.DIV;
        } else if (ctx.REM() != null) {
            operation = Operation.REM;
        } else if (ctx.ADD() != null) {
            operation = Operation.ADD;
        } else if (ctx.SUB() != null) {
            operation = Operation.SUB;
        } else if (ctx.FIND() != null) {
            operation = Operation.FIND;
        } else if (ctx.MATCH() != null) {
            operation = Operation.MATCH;
        } else if (ctx.LSH() != null) {
            operation = Operation.LSH;
        } else if (ctx.RSH() != null) {
            operation = Operation.RSH;
        } else if (ctx.USH() != null) {
            operation = Operation.USH;
        } else if (ctx.BWAND() != null) {
            operation = Operation.BWAND;
        } else if (ctx.XOR() != null) {
            operation = Operation.XOR;
        } else if (ctx.BWOR() != null) {
            operation = Operation.BWOR;
        } else {
            throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
        }

        return new BinaryMathNode(location(ctx), left, right, operation, null, null, false, null);
    }

    @Override
    public Node visitComp(CompContext ctx) {
        ExpressionNode left = (ExpressionNode) visit(ctx.noncondexpression(0));
        ExpressionNode right = (ExpressionNode) visit(ctx.noncondexpression(1));
        final Operation operation;

        if (ctx.LT() != null) {
            operation = Operation.LT;
        } else if (ctx.LTE() != null) {
            operation = Operation.LTE;
        } else if (ctx.GT() != null) {
            operation = Operation.GT;
        } else if (ctx.GTE() != null) {
            operation = Operation.GTE;
        } else if (ctx.EQ() != null) {
            operation = Operation.EQ;
        } else if (ctx.EQR() != null) {
            operation = Operation.EQR;
        } else if (ctx.NE() != null) {
            operation = Operation.NE;
        } else if (ctx.NER() != null) {
            operation = Operation.NER;
        } else {
            throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
        }

        return new ComparisonNode(location(ctx), left, right, operation, null, null);
    }

    @Override
    public Node visitInstanceof(InstanceofContext ctx) {
        ExpressionNode expr = (ExpressionNode) visit(ctx.noncondexpression());
        String type = ctx.decltype().getText();
        return new SourceInstanceofNode(location(ctx), expr, type, null);
    }

    @Override
    public Node visitBool(BoolContext ctx) {
        ExpressionNode left = (ExpressionNode) visit(ctx.noncondexpression(0));
        ExpressionNode right = (ExpressionNode) visit(ctx.noncondexpression(1));
        final Operation operation;

        if (ctx.BOOLAND() != null) {
            operation = Operation.AND;
        } else if (ctx.BOOLOR() != null) {
            operation = Operation.OR;
        } else {
            throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
        }

        return new BooleanNode(location(ctx), left, right, operation, null);
    }

    @Override
    public Node visitElvis(ElvisContext ctx) {
        ExpressionNode left = (ExpressionNode) visit(ctx.noncondexpression(0));
        ExpressionNode right = (ExpressionNode) visit(ctx.noncondexpression(1));
        return new ElvisNode(location(ctx), left, right, null);
    }

    @Override
    public Node visitNonconditional(NonconditionalContext ctx) {
        return visit(ctx.noncondexpression());
    }

    @Override
    public Node visitConditional(ConditionalContext ctx) {
        ExpressionNode condition = (ExpressionNode) visit(ctx.noncondexpression());
        ExpressionNode left = (ExpressionNode) visit(ctx.expression(0));
        ExpressionNode right = (ExpressionNode) visit(ctx.expression(1));
        return new ConditionalNode(location(ctx), condition, left, right, null);
    }

    @Override
    public Node visitAssignment(AssignmentContext ctx) {
        ExpressionNode lhs = (ExpressionNode) visit(ctx.noncondexpression());
        ExpressionNode rhs = (ExpressionNode) visit(ctx.expression());

        final Operation operation;

        if (ctx.ASSIGN() != null) {
            operation = null;
        } else if (ctx.AMUL() != null) {
            operation = Operation.MUL;
        } else if (ctx.ADIV() != null) {
            operation = Operation.DIV;
        } else if (ctx.AREM() != null) {
            operation = Operation.REM;
        } else if (ctx.AADD() != null) {
            operation = Operation.ADD;
        } else if (ctx.ASUB() != null) {
            operation = Operation.SUB;
        } else if (ctx.ALSH() != null) {
            operation = Operation.LSH;
        } else if (ctx.ARSH() != null) {
            operation = Operation.RSH;
        } else if (ctx.AUSH() != null) {
            operation = Operation.USH;
        } else if (ctx.AAND() != null) {
            operation = Operation.BWAND;
        } else if (ctx.AXOR() != null) {
            operation = Operation.XOR;
        } else if (ctx.AOR() != null) {
            operation = Operation.BWOR;
        } else {
            throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
        }

        return new AssignmentNode(location(ctx), lhs, rhs, false, operation, null);
    }

    @Override
    public Node visitPre(PreContext ctx) {
        ExpressionNode expression = (ExpressionNode) visit(ctx.chain());

        final Operation operation;

        if (ctx.INCR() != null) {
            operation = Operation.ADD;
        } else if (ctx.DECR() != null) {
            operation = Operation.SUB;
        } else {
            throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
        }

        return new AssignmentNode(
            location(ctx),
            expression,
            new NumericNode(location(ctx), "1", 10),
            false,
            operation,
            null
        );
    }

    @Override
    public Node visitAddsub(AddsubContext ctx) {
        ExpressionNode expression = (ExpressionNode) visit(ctx.unary());

        final Operation operation;

        if (ctx.ADD() != null) {
            operation = Operation.ADD;
        } else if (ctx.SUB() != null) {
            operation = Operation.SUB;
        } else {
            throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
        }

        return new UnaryMathNode(location(ctx), expression, operation, null, 0, false, null);
    }

    @Override
    public Node visitNotaddsub(NotaddsubContext ctx) {
        return visit(ctx.unarynotaddsub());
    }

    @Override
    public Node visitRead(ReadContext ctx) {
        return visit(ctx.chain());
    }

    @Override
    public Node visitPost(PostContext ctx) {
        ExpressionNode expression = (ExpressionNode) visit(ctx.chain());

        final Operation operation;

        if (ctx.INCR() != null) {
            operation = Operation.ADD;
        } else if (ctx.DECR() != null) {
            operation = Operation.SUB;
        } else {
            throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
        }

        return new AssignmentNode(
            location(ctx),
            expression,
            new NumericNode(location(ctx), "1", 10),
            true,
            operation,
            null
        );
    }

    @Override
    public Node visitNot(NotContext ctx) {
        ExpressionNode expression = (ExpressionNode) visit(ctx.unary());

        final Operation operation;

        if (ctx.BOOLNOT() != null) {
            operation = Operation.NOT;
        } else if (ctx.BWNOT() != null) {
            operation = Operation.BWNOT;
        } else {
            throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
        }

        return new UnaryMathNode(location(ctx), expression, operation, null, 0, false, null);
    }

    @Override
    public Node visitCast(CastContext ctx) {
        return visit(ctx.castexpression());
    }

    @Override
    public Node visitPrimordefcast(PainlessParser.PrimordefcastContext ctx) {
        String type = ctx.primordefcasttype().getText();
        ExpressionNode child = (ExpressionNode) visit(ctx.unary());
        return new ExplicitCastNode(location(ctx), type, child, null);
    }

    @Override
    public Node visitRefcast(PainlessParser.RefcastContext ctx) {
        String type = ctx.refcasttype().getText();
        ExpressionNode child = (ExpressionNode) visit(ctx.unarynotaddsub());
        return new ExplicitCastNode(location(ctx), type, child, null);
    }

    @Override
    public Node visitPrimordefcasttype(PainlessParser.PrimordefcasttypeContext ctx) {
        throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
    }

    @Override
    public Node visitRefcasttype(PainlessParser.RefcasttypeContext ctx) {
        throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
    }

    @Override
    public Node visitDynamic(DynamicContext ctx) {
        ExpressionNode primary = (ExpressionNode) visit(ctx.primary());
        return buildPostfixChain(primary, null, ctx.postfix());
    }

    @Override
    public Node visitNewarray(NewarrayContext ctx) {
        return visit(ctx.arrayinitializer());
    }

    @Override
    public Node visitPrecedence(PrecedenceContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public Node visitNumeric(NumericContext ctx) {
        if (ctx.DECIMAL() != null) {
            return new DecimalNode(location(ctx), ctx.DECIMAL().getText());
        } else if (ctx.HEX() != null) {
            return new NumericNode(location(ctx), ctx.HEX().getText().substring(2), 16);
        } else if (ctx.INTEGER() != null) {
            return new NumericNode(location(ctx), ctx.INTEGER().getText(), 10);
        } else if (ctx.OCTAL() != null) {
            return new NumericNode(location(ctx), ctx.OCTAL().getText().substring(1), 8);
        } else {
            throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
        }
    }

    @Override
    public Node visitTrue(TrueContext ctx) {
        return new ConstantNode(location(ctx), Boolean.TRUE, null, null);
    }

    @Override
    public Node visitFalse(FalseContext ctx) {
        return new ConstantNode(location(ctx), Boolean.FALSE, null, null);
    }

    @Override
    public Node visitNull(NullContext ctx) {
        return new NullNode(location(ctx), null);
    }

    @Override
    public Node visitString(StringContext ctx) {
        StringBuilder string = new StringBuilder(ctx.STRING().getText());

        // Strip the leading and trailing quotes and replace the escape sequences with their literal equivalents
        int src = 1;
        int dest = 0;
        int end = string.length() - 1;
        assert string.charAt(0) == '"' || string.charAt(0) == '\'' :
            "expected string to start with a quote but was [" + string + "]";
        assert string.charAt(end) == '"' || string.charAt(end) == '\'' :
            "expected string to end with a quote was [" + string + "]";
        while (src < end) {
            char current = string.charAt(src);
            if (current == '\\') {
                src++;
                current = string.charAt(src);
            }
            string.setCharAt(dest, current);
            src++;
            dest++;
        }
        string.setLength(dest);

        return new ConstantNode(location(ctx), string.toString(), null, null);
    }

    @Override
    public Node visitRegex(RegexContext ctx) {
        String text = ctx.REGEX().getText();
        int lastSlash = text.lastIndexOf('/');
        String pattern = text.substring(1, lastSlash);
        String flags = text.substring(lastSlash + 1);
        return new RegexNode(location(ctx), pattern, flags, null);
    }

    @Override
    public Node visitListinit(ListinitContext ctx) {
        return visit(ctx.listinitializer());
    }

    @Override
    public Node visitMapinit(MapinitContext ctx) {
        return visit(ctx.mapinitializer());
    }

    @Override
    public Node visitVariable(VariableContext ctx) {
        String name = ctx.ID().getText();
        return new VariableNode(location(ctx), name, null);
    }

    @Override
    public Node visitCalllocal(CalllocalContext ctx) {
        String name = ctx.ID() == null ? ctx.DOLLAR().getText() : ctx.ID().getText();
        List<ExpressionNode> arguments = collectArguments(ctx.arguments());
        return new LocalFunctionCallNode(location(ctx), name, arguments, null);
    }

    @Override
    public Node visitNewobject(NewobjectContext ctx) {
        String type = ctx.type().getText();
        List<ExpressionNode> arguments = collectArguments(ctx.arguments());
        return new NewObjectNode(location(ctx), type, arguments, null, null);
    }

    private ExpressionNode buildPostfixChain(ExpressionNode primary, PostdotContext postdot,
                                             List<PostfixContext> postfixes) {
        ExpressionNode prefix = primary;

        if (postdot != null) {
            prefix = visitPostdot(postdot, prefix);
        }

        for (PostfixContext postfix : postfixes) {
            prefix = visitPostfix(postfix, prefix);
        }

        return prefix;
    }

    @Override
    public Node visitPostfix(PostfixContext ctx) {
        throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
    }

    public ExpressionNode visitPostfix(PostfixContext ctx, ExpressionNode prefix) {
        if (ctx.callinvoke() != null) {
            return visitCallinvoke(ctx.callinvoke(), prefix);
        } else if (ctx.fieldaccess() != null) {
            return visitFieldaccess(ctx.fieldaccess(), prefix);
        } else if (ctx.braceaccess() != null) {
            return visitBraceaccess(ctx.braceaccess(), prefix);
        } else {
            throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
        }
    }

    @Override
    public Node visitPostdot(PostdotContext ctx) {
        throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
    }

    public ExpressionNode visitPostdot(PostdotContext ctx, ExpressionNode prefix) {
        if (ctx.callinvoke() != null) {
            return visitCallinvoke(ctx.callinvoke(), prefix);
        } else if (ctx.fieldaccess() != null) {
            return visitFieldaccess(ctx.fieldaccess(), prefix);
        } else {
            throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
        }
    }

    @Override
    public Node visitCallinvoke(CallinvokeContext ctx) {
        throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
    }

    public ExpressionNode visitCallinvoke(CallinvokeContext ctx, ExpressionNode prefix) {
        String name = ctx.DOTID().getText();
        List<ExpressionNode> arguments = collectArguments(ctx.arguments());
        return new MethodCallNode(location(ctx), prefix, name, arguments, ctx.NSDOT() != null, null);
    }

    @Override
    public Node visitFieldaccess(FieldaccessContext ctx) {
        throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
    }

    public ExpressionNode visitFieldaccess(FieldaccessContext ctx, ExpressionNode prefix) {
        final String value;

        if (ctx.DOTID() != null) {
            value = ctx.DOTID().getText();
        } else if (ctx.DOTINTEGER() != null) {
            value = ctx.DOTINTEGER().getText();
        } else {
            throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
        }

        return new DotAccessNode(location(ctx), prefix, value, ctx.NSDOT() != null, null);
    }

    @Override
    public Node visitBraceaccess(BraceaccessContext ctx) {
        throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
    }

    public ExpressionNode visitBraceaccess(BraceaccessContext ctx, ExpressionNode prefix) {
        ExpressionNode expression = (ExpressionNode) visit(ctx.expression());
        return new BraceAccessNode(location(ctx), prefix, expression, null);
    }

    @Override
    public Node visitNewstandardarray(NewstandardarrayContext ctx) {
        StringBuilder type = new StringBuilder(ctx.type().getText());
        List<ExpressionNode> expressions = new ArrayList<>();

        for (ExpressionContext expression : ctx.expression()) {
            type.append("[]");
            expressions.add((ExpressionNode) visit(expression));
        }

        return buildPostfixChain(
            new NewArrayNode(location(ctx), type.toString(), expressions, false, null),
            ctx.postdot(),
            ctx.postfix()
        );
    }

    @Override
    public Node visitNewinitializedarray(NewinitializedarrayContext ctx) {
        String type = ctx.type().getText() + "[]";
        List<ExpressionNode> expressions = new ArrayList<>();

        for (ExpressionContext expression : ctx.expression()) {
            expressions.add((ExpressionNode) visit(expression));
        }

        return buildPostfixChain(
            new NewArrayNode(location(ctx), type, expressions, true, null),
            null,
            ctx.postfix()
        );
    }

    @Override
    public Node visitListinitializer(ListinitializerContext ctx) {
        List<ExpressionNode> values = new ArrayList<>();

        for (ExpressionContext expression : ctx.expression()) {
            values.add((ExpressionNode) visit(expression));
        }

        return new ListInitializationNode(location(ctx), values, null, null, null);
    }

    @Override
    public Node visitMapinitializer(MapinitializerContext ctx) {
        List<ExpressionNode> keys = new ArrayList<>();
        List<ExpressionNode> values = new ArrayList<>();

        for (MaptokenContext maptoken : ctx.maptoken()) {
            keys.add((ExpressionNode) visit(maptoken.expression(0)));
            values.add((ExpressionNode) visit(maptoken.expression(1)));
        }

        return new MapInitializationNode(location(ctx), keys, values, null, null, null);
    }

    @Override
    public Node visitMaptoken(MaptokenContext ctx) {
        throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
    }

    @Override
    public Node visitArguments(ArgumentsContext ctx) {
        throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
    }

    private List<ExpressionNode> collectArguments(ArgumentsContext ctx) {
        List<ExpressionNode> arguments = new ArrayList<>(ctx.argument().size());

        for (ArgumentContext argument : ctx.argument()) {
            arguments.add((ExpressionNode) visit(argument));
        }

        return arguments;
    }

    @Override
    public Node visitArgument(ArgumentContext ctx) {
        if (ctx.expression() != null) {
            return visit(ctx.expression());
        } else if (ctx.lambda() != null) {
            return visit(ctx.lambda());
        } else if (ctx.funcref() != null) {
            return visit(ctx.funcref());
        } else {
            throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
        }
    }

    @Override
    public Node visitLambda(LambdaContext ctx) {
        List<String> paramTypes = new ArrayList<>(ctx.lamtype().size());
        List<String> paramNames = new ArrayList<>(ctx.lamtype().size());
        BlockNode block;

        for (LamtypeContext lamtype : ctx.lamtype()) {
            if (lamtype.decltype() == null) {
                paramTypes.add(null);
            } else {
                paramTypes.add(lamtype.decltype().getText());
            }

            paramNames.add(lamtype.ID().getText());
        }

        if (ctx.expression() != null) {
            // single expression — wrap in a return statement
            ExpressionNode expression = (ExpressionNode) visit(ctx.expression());
            block = new BlockNode(
                location(ctx),
                Collections.singletonList(new ReturnNode(location(ctx), expression)),
                false
            );
        } else {
            block = (BlockNode) visit(ctx.block());
        }

        return new LambdaNode(location(ctx), paramTypes, paramNames, block, null);
    }

    @Override
    public Node visitLamtype(LamtypeContext ctx) {
        throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
    }

    @Override
    public Node visitClassfuncref(ClassfuncrefContext ctx) {
        return new FunctionReferenceNode(location(ctx), ctx.decltype().getText(), ctx.ID().getText(), null);
    }

    @Override
    public Node visitConstructorfuncref(ConstructorfuncrefContext ctx) {
        return ctx.decltype().LBRACE().isEmpty()
            ? new FunctionReferenceNode(location(ctx), ctx.decltype().getText(), ctx.NEW().getText(), null)
            : new NewArrayFunctionReferenceNode(location(ctx), ctx.decltype().getText(), null);
    }

    @Override
    public Node visitLocalfuncref(LocalfuncrefContext ctx) {
        return new FunctionReferenceNode(location(ctx), ctx.THIS().getText(), ctx.ID().getText(), null);
    }
}
