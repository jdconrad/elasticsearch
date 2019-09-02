/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
import org.elasticsearch.painless.ScriptClassInfo;
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
import org.elasticsearch.painless.antlr.PainlessParser.CapturingfuncrefContext;
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
import org.elasticsearch.painless.antlr.PainlessParser.NullContext;
import org.elasticsearch.painless.antlr.PainlessParser.NumericContext;
import org.elasticsearch.painless.antlr.PainlessParser.OperatorContext;
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
import org.elasticsearch.painless.antlr.PainlessParser.StaticContext;
import org.elasticsearch.painless.antlr.PainlessParser.StringContext;
import org.elasticsearch.painless.antlr.PainlessParser.ThrowContext;
import org.elasticsearch.painless.antlr.PainlessParser.TrailerContext;
import org.elasticsearch.painless.antlr.PainlessParser.TrapContext;
import org.elasticsearch.painless.antlr.PainlessParser.TrueContext;
import org.elasticsearch.painless.antlr.PainlessParser.TryContext;
import org.elasticsearch.painless.antlr.PainlessParser.VariableContext;
import org.elasticsearch.painless.antlr.PainlessParser.WhileContext;
import org.elasticsearch.painless.builder.ASTBuilder;
import org.elasticsearch.painless.lookup.PainlessLookup;
import org.elasticsearch.painless.node.SSource;
import org.objectweb.asm.util.Printer;

import java.util.List;
import java.util.Stack;

/**
 * Converts the ANTLR tree to a Painless tree.
 */
public final class Walker extends PainlessParserBaseVisitor<Void> {

    public static SSource buildPainlessTree(ScriptClassInfo mainMethod, String sourceName,
                                            String sourceText, CompilerSettings settings, PainlessLookup painlessLookup,
                                            Printer debugStream) {
        return new Walker(mainMethod, sourceName, sourceText, settings, painlessLookup, debugStream).source;
    }

    private final ScriptClassInfo scriptClassInfo;
    private final CompilerSettings settings;
    private final Printer debugStream;
    private final String sourceName;
    private final String sourceText;
    private final PainlessLookup painlessLookup;

    private final ASTBuilder builder;
    private SSource source;

    private Walker(ScriptClassInfo scriptClassInfo, String sourceName, String sourceText,
                   CompilerSettings settings, PainlessLookup painlessLookup, Printer debugStream) {
        this.scriptClassInfo = scriptClassInfo;
        this.debugStream = debugStream;
        this.settings = settings;
        this.sourceName = Location.computeSourceName(sourceName);
        this.sourceText = sourceText;
        this.painlessLookup = painlessLookup;
        builder = new ASTBuilder();
        visit(buildAntlrTree(sourceText));
        source = (SSource)builder.endBuild();
    }

    private SourceContext buildAntlrTree(String source) {
        ANTLRInputStream stream = new ANTLRInputStream(source);
        PainlessLexer lexer = new EnhancedPainlessLexer(stream, sourceName, painlessLookup);
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

    private void setupPicky(PainlessParser parser) {
        // Diagnostic listener invokes syntaxError on other listeners for ambiguity issues,
        parser.addErrorListener(new DiagnosticErrorListener(true));
        // a second listener to fail the test when the above happens.
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(final Recognizer<?,?> recognizer, final Object offendingSymbol, final int line,
                                    final int charPositionInLine, final String msg, final RecognitionException e) {
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

    private Location location(TerminalNode ctx) {
        return new Location(sourceName, ctx.getSymbol().getStartIndex());
    }

    @Override
    public Void visitSource(SourceContext ctx) {
        builder.visitSource(scriptClassInfo, sourceName, sourceText, debugStream, location(ctx));

        for (FunctionContext function : ctx.function()) {
            visit(function);
        }

        for (StatementContext statement : ctx.statement()) {
            visit(statement);
        }

        builder.endVisit();

        return null;
    }

    @Override
    public Void visitFunction(FunctionContext ctx) {
        String rtnType = ctx.decltype().getText();
        String name = ctx.ID().getText();

        builder.visitFunction(location(ctx), name, false)
                .visitTypeString(location(ctx), rtnType).endVisit();
        visit(ctx.parameters());
        visit(ctx.block());
        builder.endVisit();

        return null;
    }

    @Override
    public Void visitParameters(ParametersContext ctx) {
        if (ctx.decltype().size() != ctx.ID().size()) {
            throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
        }

        builder.visitParameters(location(ctx));

        for (int parameterIndex = 0; parameterIndex < ctx.decltype().size(); ++parameterIndex) {
            DecltypeContext type = ctx.decltype(parameterIndex);
            TerminalNode name = ctx.ID(parameterIndex);

            builder.visitParameter(location(ctx), name.getText())
                    .visitTypeString(location(type), type.getText()).endVisit()
                    .endVisit();
        }

        builder.endVisit();

        return null;
    }

    @Override
    public Void visitStatement(StatementContext ctx) {
        if (ctx.rstatement() != null) {
            visit(ctx.rstatement());
        } else if (ctx.dstatement() != null) {
            visit(ctx.dstatement());
        } else {
            throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
        }

        return null;
    }

    @Override
    public Void visitIf(IfContext ctx) {
        if (ctx.trailer().size() > 1) {
            builder.visitIfElse(location(ctx));
        } else {
            builder.visitIf(location(ctx));
        }

        visit(ctx.expression());
        visit(ctx.trailer(0));

        if (ctx.trailer().size() > 1) {
            visit(ctx.trailer(1));
        }

        builder.endVisit();

        return null;
    }

    @Override
    public Void visitWhile(WhileContext ctx) {
        builder.visitWhile(location(ctx));

        visit(ctx.expression());

        if (ctx.trailer() != null) {
            visit(ctx.trailer());
        } else if (ctx.empty() != null) {
            builder.visitEmpty();
        } else {
            throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
        }

        builder.endVisit();

        return null;
    }

    @Override
    public Void visitDo(DoContext ctx) {
        builder.visitDo(location(ctx));

        visit(ctx.expression());
        visit(ctx.block());

        builder.endVisit();

        return null;
    }

    @Override
    public Void visitFor(ForContext ctx) {
        builder.visitFor(location(ctx));

        if (ctx.initializer() == null) {
            builder.visitEmpty();
        } else {
            visit(ctx.initializer());
        }

        if (ctx.expression() == null) {
            builder.visitEmpty();
        } else {
            visit(ctx.expression());
        }

        if (ctx.afterthought() == null) {
            builder.visitEmpty();
        } else {
            visit(ctx.afterthought());
        }

        if (ctx.trailer() != null) {
            visit(ctx.trailer());
        } else if (ctx.empty() != null) {
            builder.visitEmpty();
        } else {
            throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
        }

        builder.endVisit();

        return null;
    }

    @Override
    public Void visitEach(EachContext ctx) {
        String type = ctx.decltype().getText();
        String name = ctx.ID().getText();

        builder.visitEach(location(ctx))
                .visitDeclaration(location(ctx.ID()), name, false)
                        .visitTypeString(location(ctx.decltype()), type).endVisit().visitEmpty()
                .endVisit();

        visit(ctx.expression());
        visit(ctx.trailer());

        builder.endVisit();

        return null;
    }

    @Override
    public Void visitIneach(IneachContext ctx) {
        String name = ctx.ID().getText();

        builder.visitEach(location(ctx))
                .visitDeclaration(location(ctx.ID()), name, false)
                        .visitTypeString(location(ctx), "def").endVisit().visitEmpty()
                .endVisit();

        visit(ctx.expression());
        visit(ctx.trailer());

        builder.endVisit();

        return null;
    }

    @Override
    public Void visitDecl(DeclContext ctx) {
        visit(ctx.declaration());

        return null;
    }

    @Override
    public Void visitContinue(ContinueContext ctx) {
        builder.visitContinue(location(ctx)).endVisit();

        return null;
    }

    @Override
    public Void visitBreak(BreakContext ctx) {
        builder.visitBreak(location(ctx)).endVisit();

        return null;
    }

    @Override
    public Void visitReturn(ReturnContext ctx) {
        builder.visitReturn(location(ctx));

        if (ctx.expression() == null) {
            builder.visitEmpty();
        } else {
            visit(ctx.expression());
        }

        builder.endVisit();

        return null;
    }

    @Override
    public Void visitTry(TryContext ctx) {
        builder.visitTry(location(ctx));

        visit(ctx.block());

        for (TrapContext trap : ctx.trap()) {
            visit(trap);
        }

        builder.endVisit();

        return null;
    }

    @Override
    public Void visitThrow(ThrowContext ctx) {
        builder.visitThrow(location(ctx));
        visit(ctx.expression());
        builder.endVisit();

        return null;
    }

    @Override
    public Void visitExpr(ExprContext ctx) {
        builder.visitExpression(location(ctx));
        visit(ctx.expression());
        builder.endVisit();

        return null;
    }

    @Override
    public Void visitTrailer(TrailerContext ctx) {
        if (ctx.block() != null) {
            visit(ctx.block());
        } else if (ctx.statement() != null) {
            builder.visitBlock(location(ctx));
            visit(ctx.statement());
            builder.endVisit();
        } else {
            throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
        }

        return null;
    }

    @Override
    public Void visitBlock(BlockContext ctx) {
        if (ctx.statement().isEmpty() && ctx.dstatement() == null) {
            builder.visitEmpty();
        } else {
            builder.visitBlock(location(ctx));

            for (StatementContext statement : ctx.statement()) {
                visit(statement);
            }

            if (ctx.dstatement() != null) {
                visit(ctx.dstatement());
            }

            builder.endVisit();
        }

        return null;
    }

    @Override
    public Void visitEmpty(EmptyContext ctx) {
        throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
    }

    @Override
    public Void visitInitializer(InitializerContext ctx) {
        if (ctx.declaration() != null) {
            visit(ctx.declaration());
        } else if (ctx.expression() != null) {
            visit(ctx.expression());
        } else {
            throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
        }

        return null;
    }

    @Override
    public Void visitAfterthought(AfterthoughtContext ctx) {
        visit(ctx.expression());

        return null;
    }

    @Override
    public Void visitDeclaration(DeclarationContext ctx) {
        builder.visitDeclBlock(location(ctx));

        String type = ctx.decltype().getText();

        for (DeclvarContext declvar : ctx.declvar()) {
            String name = declvar.ID().getText();

            builder.visitDeclaration(location(declvar), name, true)
                    .visitTypeString(location(ctx.decltype()), type).endVisit();

            if (declvar.expression() == null) {
                builder.visitEmpty();
            } else {
                visit(declvar.expression());
            }

            builder.endVisit();
        }

        builder.endVisit();

        return null;
    }

    @Override
    public Void visitDecltype(DecltypeContext ctx) {
        throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
    }

    @Override
    public Void visitDeclvar(DeclvarContext ctx) {
        throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
    }

    @Override
    public Void visitTrap(TrapContext ctx) {
        String type = ctx.TYPE().getText();
        String name = ctx.ID().getText();

        builder.visitCatch(location(ctx))
                .visitDeclaration(location(ctx.ID()), name, false)
                        .visitTypeString(location(ctx.TYPE()), type).endVisit().visitEmpty()
                .endVisit();

        visit(ctx.block());

        builder.endVisit();

        return null;
    }

    @Override
    public Void visitSingle(SingleContext ctx) {
        visit(ctx.unary());

        return null;
    }

    @Override
    public Void visitBinary(BinaryContext ctx) {
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

        builder.visitBinary(location(ctx), operation);

        visit(ctx.expression(0));
        visit(ctx.expression(1));

        builder.endVisit();

        return null;
    }

    @Override
    public Void visitComp(CompContext ctx) {
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

        builder.visitComp(location(ctx), operation);

        visit(ctx.expression(0));
        visit(ctx.expression(1));

        builder.endVisit();

        return null;
    }

    @Override
    public Void visitInstanceof(InstanceofContext ctx) {
        String type = ctx.decltype().getText();

        builder.visitInstanceof(location(ctx))
                .visitTypeString(location(ctx.decltype()), type).endVisit();

        visit(ctx.expression());

        builder.endVisit();

        return null;
    }

    @Override
    public Void visitBool(BoolContext ctx) {
        final Operation operation;

        if (ctx.BOOLAND() != null) {
            operation = Operation.AND;
        } else if (ctx.BOOLOR() != null) {
            operation = Operation.OR;
        } else {
            throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
        }

        builder.visitBool(location(ctx), operation);

        visit(ctx.expression(0));
        visit(ctx.expression(1));

        builder.endVisit();

        return null;
    }

    @Override
    public Void visitConditional(ConditionalContext ctx) {
        builder.visitConditional(location(ctx));

        visit(ctx.expression(0));
        visit(ctx.expression(1));
        visit(ctx.expression(2));

        builder.endVisit();

        return null;
    }

    @Override
    public Void visitElvis(ElvisContext ctx) {
        builder.visitElvis(location(ctx));

        visit(ctx.expression(0));
        visit(ctx.expression(1));

        builder.endVisit();

        return null;
    }

    @Override
    public Void visitAssignment(AssignmentContext ctx) {
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

        builder.visitAssignment(location(ctx), false, false, operation);

        visit(ctx.expression(0));
        visit(ctx.expression(1));

        builder.endVisit();

        return null;
    }

    @Override
    public Void visitPre(PreContext ctx) {
        final Operation operation;

        if (ctx.INCR() != null) {
            operation = Operation.INCR;
        } else if (ctx.DECR() != null) {
            operation = Operation.DECR;
        } else {
            throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
        }

        builder.visitAssignment(location(ctx), true, false, operation);

        visit(ctx.chain());
        builder.visitEmpty();

        builder.endVisit();

        return null;
    }

    @Override
    public Void visitPost(PostContext ctx) {
        final Operation operation;

        if (ctx.INCR() != null) {
            operation = Operation.INCR;
        } else if (ctx.DECR() != null) {
            operation = Operation.DECR;
        } else {
            throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
        }

        builder.visitAssignment(location(ctx), false, true, operation);

        visit(ctx.chain());
        builder.visitEmpty();

        builder.endVisit();

        return null;
    }

    @Override
    public Void visitRead(ReadContext ctx) {
        visit(ctx.chain());

        return null;
    }

    @Override
    public Void visitOperator(OperatorContext ctx) {
        final Operation operation;

        if (ctx.BOOLNOT() != null) {
            operation = Operation.NOT;
        } else if (ctx.BWNOT() != null) {
            operation = Operation.BWNOT;
        } else if (ctx.ADD() != null) {
            operation = Operation.ADD;
        } else if (ctx.SUB() != null) {
            if (ctx.unary() instanceof ReadContext && ((ReadContext)ctx.unary()).chain() instanceof DynamicContext &&
                ((DynamicContext)((ReadContext)ctx.unary()).chain()).primary() instanceof NumericContext &&
                ((DynamicContext)((ReadContext)ctx.unary()).chain()).postfix().isEmpty()) {

                visit(ctx.unary());

                return null;
            }

            operation = Operation.SUB;
        } else {
            throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
        }

        builder.visitUnary(location(ctx), operation);

        visit(ctx.unary());

        builder.endVisit();

        return null;
    }

    @Override
    public Void visitCast(CastContext ctx) {
        String type = ctx.decltype().getText();

        builder.visitExplicit(location(ctx))
                .visitTypeString(location(ctx.decltype()), type).endVisit();

        visit(ctx.unary());

        builder.endVisit();

        return null;
    }

    @Override
    public Void visitDynamic(DynamicContext ctx) {
        buildPrefixChain(ctx, null, ctx.postfix());

        return null;
    }

    @Override
    public Void visitStatic(StaticContext ctx) {
        buildPrefixChain(ctx, ctx.postdot(), ctx.postfix());

        return null;
    }

    private void visitPrefixStatic(StaticContext ctx) {
        String type = ctx.decltype().getText();

        builder.visitStatic(location(ctx), type).endVisit();
    }

    @Override
    public Void visitNewarray(NewarrayContext ctx) {
        visit(ctx.arrayinitializer());

        return null;
    }

    @Override
    public Void visitPrecedence(PrecedenceContext ctx) {
        visit(ctx.expression());

        return null;
    }

    @Override
    public Void visitNumeric(NumericContext ctx) {
        final boolean negate = ((DynamicContext)ctx.parent).postfix().isEmpty() &&
            ctx.parent.parent.parent instanceof OperatorContext &&
            ((OperatorContext)ctx.parent.parent.parent).SUB() != null;

        if (ctx.DECIMAL() != null) {
            builder.visitDecimal(location(ctx), (negate ? "-" : "") + ctx.DECIMAL().getText()).endVisit();
        } else if (ctx.HEX() != null) {
            builder.visitNumeric(location(ctx), (negate ? "-" : "") + ctx.HEX().getText().substring(2), 16).endVisit();
        } else if (ctx.INTEGER() != null) {
            builder.visitNumeric(location(ctx), (negate ? "-" : "") + ctx.INTEGER().getText(), 10).endVisit();
        } else if (ctx.OCTAL() != null) {
            builder.visitNumeric(location(ctx), (negate ? "-" : "") + ctx.OCTAL().getText().substring(1), 8).endVisit();
        } else {
            throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
        }

        return null;
    }

    @Override
    public Void visitTrue(TrueContext ctx) {
        builder.visitBoolean(location(ctx), true).endVisit();

        return null;
    }

    @Override
    public Void visitFalse(FalseContext ctx) {
        builder.visitBoolean(location(ctx), false).endVisit();

        return null;
    }

    @Override
    public Void visitNull(NullContext ctx) {
        builder.visitNull(location(ctx)).endVisit();

        return null;
    }

    @Override
    public Void visitString(StringContext ctx) {
        StringBuilder string = new StringBuilder(ctx.STRING().getText());

        // Strip the leading and trailing quotes and replace the escape sequences with their literal equivalents
        int src = 1;
        int dest = 0;
        int end = string.length() - 1;
        assert string.charAt(0) == '"' || string.charAt(0) == '\'' : "expected string to start with a quote but was [" + string + "]";
        assert string.charAt(end) == '"' || string.charAt(end) == '\'' : "expected string to end with a quote was [" + string + "]";
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

        builder.visitString(location(ctx), string.toString()).endVisit();

        return null;
    }

    @Override
    public Void visitRegex(RegexContext ctx) {
        String text = ctx.REGEX().getText();
        int lastSlash = text.lastIndexOf('/');
        String pattern = text.substring(1, lastSlash);
        String flags = text.substring(lastSlash + 1);

        builder.visitRegex(location(ctx), pattern, flags).endVisit();

        return null;
    }

    @Override
    public Void visitListinit(ListinitContext ctx) {
        visit(ctx.listinitializer());

        return null;
    }

    @Override
    public Void visitMapinit(MapinitContext ctx) {
        visit(ctx.mapinitializer());

        return null;
    }

    @Override
    public Void visitVariable(VariableContext ctx) {
        String name = ctx.ID().getText();

        builder.visitVariable(location(ctx), name).endVisit();

        return null;
    }

    @Override
    public Void visitCalllocal(CalllocalContext ctx) {
        String name = ctx.ID().getText();

        builder.visitCallLocal(location(ctx), name);

        visit(ctx.arguments());

        builder.endVisit();

        return null;
    }

    @Override
    public Void visitNewobject(NewobjectContext ctx) {
        String type = ctx.TYPE().getText();

        builder.visitNewObj(location(ctx))
                .visitTypeString(location(ctx.TYPE()), type).endVisit();

        visit(ctx.arguments());

        builder.endVisit();

        return null;
    }

    private void buildPrefixChain(ParserRuleContext prefix, PostdotContext postdot, List<PostfixContext> postfixes) {
        Stack<ParserRuleContext> prefixes = new Stack<>();

        prefixes.push(prefix);

        if (postdot != null) {
            prefixes.push(postdot);
        }

        for (PostfixContext postfix : postfixes) {
            prefixes.push(postfix);
        }

        visitPrefix(prefixes);
    }

    @Override
    public Void visitPostfix(PostfixContext ctx) {
        throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
    }

    private void visitPrefix(Stack<ParserRuleContext> prefixes) {
        if (prefixes.isEmpty()) {
            throw new IllegalStateException("illegal tree structure");
        }

        ParserRuleContext prefix = prefixes.pop();

        if (prefix instanceof DynamicContext) {
            DynamicContext ctx = (DynamicContext)prefix;

            visit(ctx.primary());
        } else if (prefix instanceof StaticContext) {
            StaticContext ctx = (StaticContext)prefix;

            visitPrefixStatic(ctx);
        } else if (prefix instanceof NewstandardarrayContext) {
            NewstandardarrayContext ctx = (NewstandardarrayContext)prefix;

            visitPrefixNewstandardArray(ctx);
        } else if (prefix instanceof NewinitializedarrayContext) {
            NewinitializedarrayContext ctx = (NewinitializedarrayContext)prefix;

            visitPrefixNewinitializedarray(ctx);
        } else if (prefix instanceof PostdotContext) {
            PostdotContext ctx = (PostdotContext)prefix;

            visitPostdot(ctx, prefixes);
        } else if (prefix instanceof PostfixContext) {
            PostfixContext ctx = (PostfixContext)prefix;

            if (ctx.callinvoke() != null) {
                visitCallinvoke(ctx.callinvoke(), prefixes);
            } else if (ctx.fieldaccess() != null) {
                visitFieldaccess(ctx.fieldaccess(), prefixes);
            } else if (ctx.braceaccess() != null) {
                visitBraceaccess(ctx.braceaccess(), prefixes);
            } else {
                throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
            }
        } else {
            throw location(prefix).createError(new IllegalStateException("illegal tree structure"));
        }
    }

    @Override
    public Void visitPostdot(PostdotContext ctx) {
        throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
    }

    private void visitPostdot(PostdotContext ctx, Stack<ParserRuleContext> prefixes) {
        if (ctx.callinvoke() != null) {
            visitCallinvoke(ctx.callinvoke(), prefixes);
        } else if (ctx.fieldaccess() != null) {
            visitFieldaccess(ctx.fieldaccess(), prefixes);
        } else {
            throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
        }
    }

    @Override
    public Void visitCallinvoke(CallinvokeContext ctx) {
        throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
    }

    private void visitCallinvoke(CallinvokeContext ctx, Stack<ParserRuleContext> prefixes) {
        String name = ctx.DOTID().getText();

        builder.visitCallInvoke(location(ctx), name, ctx.NSDOT() != null);

        visitPrefix(prefixes);
        visit(ctx.arguments());

        builder.endVisit();
    }

    @Override
    public Void visitFieldaccess(FieldaccessContext ctx) {
        throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
    }

    private void visitFieldaccess(FieldaccessContext ctx, Stack<ParserRuleContext> prefixes) {
        final String value;

        if (ctx.DOTID() != null) {
            value = ctx.DOTID().getText();
        } else if (ctx.DOTINTEGER() != null) {
            value = ctx.DOTINTEGER().getText();
        } else {
            throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
        }

        builder.visitField(location(ctx), value, ctx.NSDOT() != null);

        visitPrefix(prefixes);

        builder.endVisit();
    }

    @Override
    public Void visitBraceaccess(BraceaccessContext ctx) {
        throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
    }

    private void visitBraceaccess(BraceaccessContext ctx, Stack<ParserRuleContext> prefixes) {
        builder.visitBrace(location(ctx));

        visitPrefix(prefixes);
        visit(ctx.expression());

        builder.endVisit();
    }

    @Override
    public Void visitNewstandardarray(NewstandardarrayContext ctx) {
        buildPrefixChain(ctx, ctx.postdot(), ctx.postfix());

        return null;
    }

    private void visitPrefixNewstandardArray(NewstandardarrayContext ctx) {
        StringBuilder type = new StringBuilder(ctx.TYPE().getText());

        for (int dimensions = 0; dimensions < ctx.expression().size(); ++dimensions) {
            type.append("[]");
        }

        builder.visitNewArray(location(ctx), false)
                .visitTypeString(location(ctx.TYPE()), type.toString()).endVisit();

        for (ExpressionContext expression : ctx.expression()) {
            visit(expression);
        }

        builder.endVisit();
    }

    @Override
    public Void visitNewinitializedarray(NewinitializedarrayContext ctx) {
        buildPrefixChain(ctx, null, ctx.postfix());

        return null;
    }

    private void visitPrefixNewinitializedarray(NewinitializedarrayContext ctx) {
        String type = ctx.TYPE().getText() + "[]";

        builder.visitNewArray(location(ctx), true)
                .visitTypeString(location(ctx.TYPE()), type).endVisit();

        for (ExpressionContext expression : ctx.expression()) {
            visit(expression);
        }

        builder.endVisit();
    }

    @Override
    public Void visitListinitializer(ListinitializerContext ctx) {
        builder.visitListInit(location(ctx));

        for (ExpressionContext expression : ctx.expression()) {
            visit(expression);
        }

        builder.endVisit();

        return null;
    }

    @Override
    public Void visitMapinitializer(MapinitializerContext ctx) {
        builder.visitMapInit(location(ctx));

        for (MaptokenContext maptoken : ctx.maptoken()) {
            visit(maptoken.expression(0));
            visit(maptoken.expression(1));
        }

        builder.endVisit();

        return null;
    }

    @Override
    public Void visitMaptoken(MaptokenContext ctx) {
        throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
    }

    @Override
    public Void visitArguments(ArgumentsContext ctx) {
        for (ArgumentContext argument : ctx.argument()) {
            visit(argument);
        }

        return null;
    }

    @Override
    public Void visitArgument(ArgumentContext ctx) {
        if (ctx.expression() != null) {
            visit(ctx.expression());
        } else if (ctx.lambda() != null) {
            visit(ctx.lambda());
        } else if (ctx.funcref() != null) {
            visit(ctx.funcref());
        } else {
            throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
        }

        return null;
    }

    @Override
    public Void visitLambda(LambdaContext ctx) {
        builder.visitLambda(location(ctx)).visitParameters(location(ctx));

        for (LamtypeContext lamtype : ctx.lamtype()) {
            visit(lamtype);
        }

        builder.endVisit();

        if (ctx.expression() != null) {
            builder.visitBlock(location(ctx)).visitReturn(location(ctx));
            visit(ctx.expression());
            builder.endVisit().endVisit();
        } else {
            visit(ctx.block());
        }

        builder.endVisit();

        return null;
    }

    @Override
    public Void visitLamtype(LamtypeContext ctx) {
        builder.visitParameter(location(ctx.ID()), ctx.ID().getText());

        if (ctx.decltype() == null) {
            builder.visitEmpty();
        } else {
            builder.visitTypeString(location(ctx.decltype()), ctx.decltype().getText()).endVisit();
        }

        builder.endVisit();

        return null;
    }

    @Override
    public Void visitClassfuncref(ClassfuncrefContext ctx) {
        builder.visitFunctionRef(location(ctx), ctx.TYPE().getText(), ctx.ID().getText()).endVisit();

        return null;
    }

    @Override
    public Void visitConstructorfuncref(ConstructorfuncrefContext ctx) {
        if (ctx.decltype().LBRACE().isEmpty()) {
            builder.visitFunctionRef(location(ctx), ctx.decltype().getText(), ctx.NEW().getText()).endVisit();
        } else {
            builder.visitNewArrayFunctionRef(location(ctx), ctx.decltype().getText()).endVisit();
        }

        return null;
    }

    @Override
    public Void visitCapturingfuncref(CapturingfuncrefContext ctx) {
        builder.visitCapturingFunctionRef(location(ctx), ctx.ID(0).getText(), ctx.ID(1).getText()).endVisit();

        return null;
    }

    @Override
    public Void visitLocalfuncref(LocalfuncrefContext ctx) {
        builder.visitFunctionRef(location(ctx), ctx.THIS().getText(), ctx.ID().getText()).endVisit();

        return null;
    }
}
