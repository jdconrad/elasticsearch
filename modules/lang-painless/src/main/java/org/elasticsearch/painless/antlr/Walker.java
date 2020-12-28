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
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.atn.Transition;
import org.antlr.v4.runtime.misc.IntervalSet;
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
import org.elasticsearch.painless.lookup.PainlessLookup;
import org.elasticsearch.painless.node.AExpression;
import org.elasticsearch.painless.node.ANode;
import org.elasticsearch.painless.node.AStatement;
import org.elasticsearch.painless.node.EAssignment;
import org.elasticsearch.painless.node.EBinary;
import org.elasticsearch.painless.node.EBooleanComp;
import org.elasticsearch.painless.node.EBooleanConstant;
import org.elasticsearch.painless.node.EBrace;
import org.elasticsearch.painless.node.ECall;
import org.elasticsearch.painless.node.ECallLocal;
import org.elasticsearch.painless.node.EComp;
import org.elasticsearch.painless.node.EConditional;
import org.elasticsearch.painless.node.EDecimal;
import org.elasticsearch.painless.node.EDot;
import org.elasticsearch.painless.node.EElvis;
import org.elasticsearch.painless.node.EExplicit;
import org.elasticsearch.painless.node.EFunctionRef;
import org.elasticsearch.painless.node.EInstanceof;
import org.elasticsearch.painless.node.ELambda;
import org.elasticsearch.painless.node.EListInit;
import org.elasticsearch.painless.node.EMapInit;
import org.elasticsearch.painless.node.ENewArray;
import org.elasticsearch.painless.node.ENewArrayFunctionRef;
import org.elasticsearch.painless.node.ENewObj;
import org.elasticsearch.painless.node.ENull;
import org.elasticsearch.painless.node.ENumeric;
import org.elasticsearch.painless.node.ERegex;
import org.elasticsearch.painless.node.EString;
import org.elasticsearch.painless.node.ESymbol;
import org.elasticsearch.painless.node.EUnary;
import org.elasticsearch.painless.node.SBlock;
import org.elasticsearch.painless.node.SBreak;
import org.elasticsearch.painless.node.SCatch;
import org.elasticsearch.painless.node.SClass;
import org.elasticsearch.painless.node.SContinue;
import org.elasticsearch.painless.node.SDeclBlock;
import org.elasticsearch.painless.node.SDeclaration;
import org.elasticsearch.painless.node.SDo;
import org.elasticsearch.painless.node.SEach;
import org.elasticsearch.painless.node.SExpression;
import org.elasticsearch.painless.node.SFor;
import org.elasticsearch.painless.node.SFunction;
import org.elasticsearch.painless.node.SIf;
import org.elasticsearch.painless.node.SIfElse;
import org.elasticsearch.painless.node.SReturn;
import org.elasticsearch.painless.node.SThrow;
import org.elasticsearch.painless.node.STry;
import org.elasticsearch.painless.node.SWhile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

/**
 * Converts the ANTLR tree to a Painless tree.
 */
public final class Walker extends PainlessParserBaseVisitor<ANode> {

    public static SClass buildPainlessTree(String sourceName, String sourceText, CompilerSettings settings, PainlessLookup painlessLookup) {
        return new Walker(sourceName, sourceText, settings, painlessLookup).source;
    }

    private final CompilerSettings settings;
    private final String sourceName;

    private int identifier;

    private final SClass source;

    private Walker(String sourceName, String sourceText, CompilerSettings settings, PainlessLookup painlessLookup) {
        this.settings = settings;
        this.sourceName = sourceName;

        this.identifier = 0;

        this.source = (SClass)visit(buildAntlrTree(sourceText, painlessLookup));
    }

    private int nextIdentifier() {
        return identifier++;
    }

    private static class Tracker {

        private static class Machine {

            private static class Edge {

                private final int match;
                private final int target;

                private Edge(int match, int target) {
                    this.match = match;
                    this.target = target;
                }
            }

            private final List<List<Edge>> states;
            private final int end;

            private Machine(List<List<Edge>> states) {
                this.states = Collections.unmodifiableList(states);
                this.end = states.size();
            }

            private int match(List<? extends Token> tokens, int current) {
                List<Edge> edges = states.get(0);

                while (current < tokens.size()) {
                    int type = tokens.get(current++).getType();
                    int target = -1;

                    for (Edge edge : edges) {
                        if (type == edge.match) {
                            target = edge.target;
                            break;
                        }
                    }

                    if (target == end) {
                        return current;
                    } else if (target == -1) {
                        return -1;
                    }

                    edges = states.get(target);
                }

                return -1;
            }
        }

        private static final Machine declaration;

        static {
            List<List<Machine.Edge>> states = new ArrayList<>();

            List<Machine.Edge> edges = new ArrayList<>();
            edges.add(new Machine.Edge(PainlessLexer.ID, 1));
            edges.add(new Machine.Edge(PainlessLexer.DEF, 3));
            edges.add(new Machine.Edge(PainlessLexer.PRIMITIVE, 3));
            states.add(Collections.unmodifiableList(edges));

            edges = new ArrayList<>();
            edges.add(new Machine.Edge(PainlessLexer.DOT, 2));
            edges.add(new Machine.Edge(PainlessLexer.LBRACE, 4));
            edges.add(new Machine.Edge(PainlessLexer.ID, 6));
            states.add(Collections.unmodifiableList(edges));

            edges = new ArrayList<>();
            edges.add(new Machine.Edge(PainlessLexer.DOTID, 1));
            states.add(Collections.unmodifiableList(edges));

            edges = new ArrayList<>();
            edges.add(new Machine.Edge(PainlessLexer.LBRACE, 4));
            edges.add(new Machine.Edge(PainlessLexer.ID, 6));
            states.add(Collections.unmodifiableList(edges));

            edges = new ArrayList<>();
            edges.add(new Machine.Edge(PainlessLexer.RBRACE, 5));
            states.add(Collections.unmodifiableList(edges));

            edges = new ArrayList<>();
            edges.add(new Machine.Edge(PainlessLexer.LBRACE, 4));
            edges.add(new Machine.Edge(PainlessLexer.ID, 6));
            states.add(Collections.unmodifiableList(edges));

            declaration = new Machine(states);
        }

        private static List<String> track(List<? extends Token> tokens) {
            return new Tracker(tokens).track();
        }

        private final List<? extends Token> tokens;
        private final List<String> suggestions;

        private Tracker(List<? extends Token> tokens) {
            this.tokens = Collections.unmodifiableList(tokens);
            this.suggestions = new ArrayList<>();
        }

        private List<String> track() {
            int current = 0;

            while (current < tokens.size()) {
                int type = tokens.get(current).getType();

                if (type == PainlessLexer.ID || type == PainlessLexer.DEF || type == PainlessLexer.PRIMITIVE) {
                    int matched = declaration.match(tokens, current);

                    if (matched == -1) {
                        ++current;
                    } else {
                        StringBuilder suggestion = new StringBuilder();

                        while (current < matched) {
                            Token token = tokens.get(current++);
                            suggestion.append(token.getText());

                            if (current == matched - 1) {
                                suggestion.append(" ");
                            }
                        }

                        suggestions.add(suggestion.toString());
                    }
                } else {
                    ++current;
                }
            }

            if (true) throw new RuntimeException(suggestions.toString());
                    //tokens.stream().map(t -> PainlessLexer.ruleNames[t.getType()] + ":" + t.getText()).collect(Collectors.toList())
                    //        .toString()
            //);

            return suggestions;
        }
    }

    private SourceContext buildAntlrTree(String source, PainlessLookup painlessLookup) {
        ANTLRInputStream stream = new ANTLRInputStream(source);
        PainlessLexer lexer = new EnhancedPainlessLexer(stream, painlessLookup);

        if (true) throw new RuntimeException(
            lexer.getAllTokens().stream().map(t -> PainlessLexer.ruleNames[t.getType() - 1] + ":" + t.getText()).collect(Collectors.toList())
                .toString()
        );

        ATN atn = PainlessParser._ATN;
        StringBuilder builder = new StringBuilder("\n");
        for (ATNState state : atn.states) {
            builder.append(state.stateNumber);
            builder.append(" ");
            builder.append(PainlessParser.ruleNames[state.ruleIndex]);
            builder.append(" ");
            builder.append(ATNState.serializationNames.get(state.getStateType()));
            builder.append("\n");
            IntervalSet is = atn.nextTokens(state);
            builder.append("    [");
            for (int token : is.toList()) {
                String symbolName = PainlessLexer.VOCABULARY.getSymbolicName(token);
                if (symbolName != null) {
                    builder.append(" ");
                    builder.append(symbolName);
                }
            }
            builder.append(" ]\n");
            builder.append("    [");
            for (Transition transition : state.getTransitions()) {
                builder.append(" [ ");
                builder.append(transition.target.toString());
                if (transition.label() != null) {
                    builder.append(" : ");
                    for (int token : transition.label().toList()) {
                        builder.append(PainlessLexer.VOCABULARY.getSymbolicName(token));
                        builder.append(" ");
                    }
                    builder.append("]");
                } else {
                    builder.append(" ]");
                }
                builder.append(" ");
            }
            builder.append("]\n");
        }

        if (true) throw new IllegalStateException(builder.toString());

        List<String> suggestions = Tracker.track(lexer.getAllTokens());
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

    private Location location(TerminalNode tn) {
        return new Location(sourceName, tn.getSymbol().getStartIndex());
    }

    /*@Override
    public ANode visitSource(SourceContext ctx) {
        List<SFunction> functions = new ArrayList<>();

        for (FunctionContext function : ctx.function()) {
            functions.add((SFunction)visit(function));
        }

        // handle the code to generate the execute method here
        // because the statements come loose from the grammar as
        // part of the overall class
        List<AStatement> statements = new ArrayList<>();

        for (StatementContext statement : ctx.statement()) {
            statements.add((AStatement)visit(statement));
        }

        // generate the execute method from the collected statements and parameters
        SFunction execute = new SFunction(nextIdentifier(), location(ctx), "<internal>", "execute", emptyList(), emptyList(),
                new SBlock(nextIdentifier(), location(ctx), statements), false, false, false, false);
        functions.add(execute);

        return new SClass(nextIdentifier(), location(ctx), functions);
    }

    @Override
    public ANode visitFunction(FunctionContext ctx) {
        String rtnType = ctx.decltype().getText();
        String name = ctx.ID().getText();
        List<String> paramTypes = new ArrayList<>();
        List<String> paramNames = new ArrayList<>();
        List<AStatement> statements = new ArrayList<>();

        for (DecltypeContext decltype : ctx.parameters().decltype()) {
            paramTypes.add(decltype.getText());
        }

        for (TerminalNode id : ctx.parameters().ID()) {
            paramNames.add(id.getText());
        }

        for (StatementContext statement : ctx.block().statement()) {
            statements.add((AStatement)visit(statement));
        }

        if (ctx.block().dstatement() != null) {
            statements.add((AStatement)visit(ctx.block().dstatement()));
        }

        return new SFunction(nextIdentifier(), location(ctx),
                rtnType, name, paramTypes, paramNames, new SBlock(nextIdentifier(), location(ctx), statements), false, true, false, false);
    }

    @Override
    public ANode visitParameters(ParametersContext ctx) {
        throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
    }

    @Override
    public ANode visitStatement(StatementContext ctx) {
        if (ctx.rstatement() != null) {
            return visit(ctx.rstatement());
        } else if (ctx.dstatement() != null) {
            return visit(ctx.dstatement());
        } else {
            throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
        }
    }

    @Override
    public ANode visitIf(IfContext ctx) {
        AExpression expression = (AExpression)visit(ctx.expression());
        SBlock ifblock = (SBlock)visit(ctx.trailer(0));

        if (ctx.trailer().size() > 1) {
            SBlock elseblock = (SBlock)visit(ctx.trailer(1));

            return new SIfElse(nextIdentifier(), location(ctx), expression, ifblock, elseblock);
        } else {
            return new SIf(nextIdentifier(), location(ctx), expression, ifblock);
        }
    }

    @Override
    public ANode visitWhile(WhileContext ctx) {
        AExpression expression = (AExpression)visit(ctx.expression());

        if (ctx.trailer() != null) {
            SBlock block = (SBlock)visit(ctx.trailer());

            return new SWhile(nextIdentifier(), location(ctx), expression, block);
        } else if (ctx.empty() != null) {
            return new SWhile(nextIdentifier(), location(ctx), expression, null);
        } else {
            throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
        }
    }

    @Override
    public ANode visitDo(DoContext ctx) {
        AExpression expression = (AExpression)visit(ctx.expression());
        SBlock block = (SBlock)visit(ctx.block());

        return new SDo(nextIdentifier(), location(ctx), expression, block);
    }

    @Override
    public ANode visitFor(ForContext ctx) {
        ANode initializer = ctx.initializer() == null ? null : visit(ctx.initializer());
        AExpression expression = ctx.expression() == null ? null : (AExpression)visit(ctx.expression());
        AExpression afterthought = ctx.afterthought() == null ? null : (AExpression)visit(ctx.afterthought());

        if (ctx.trailer() != null) {
            SBlock block = (SBlock)visit(ctx.trailer());

            return new SFor(nextIdentifier(), location(ctx), initializer, expression, afterthought, block);
        } else if (ctx.empty() != null) {
            return new SFor(nextIdentifier(), location(ctx), initializer, expression, afterthought, null);
        } else {
            throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
        }
    }

    @Override
    public ANode visitEach(EachContext ctx) {
        String type = ctx.decltype().getText();
        String name = ctx.ID().getText();
        AExpression expression = (AExpression)visit(ctx.expression());
        SBlock block = (SBlock)visit(ctx.trailer());

        return new SEach(nextIdentifier(), location(ctx), type, name, expression, block);
    }

    @Override
    public ANode visitIneach(IneachContext ctx) {
        String name = ctx.ID().getText();
        AExpression expression = (AExpression)visit(ctx.expression());
        SBlock block = (SBlock)visit(ctx.trailer());

        return new SEach(nextIdentifier(), location(ctx), "def", name, expression, block);
    }

    @Override
    public ANode visitDecl(DeclContext ctx) {
        return visit(ctx.declaration());
    }

    @Override
    public ANode visitContinue(ContinueContext ctx) {
        return new SContinue(nextIdentifier(), location(ctx));
    }

    @Override
    public ANode visitBreak(BreakContext ctx) {
        return new SBreak(nextIdentifier(), location(ctx));
    }

    @Override
    public ANode visitReturn(ReturnContext ctx) {
        AExpression expression = null;

        if (ctx.expression() != null) {
            expression = (AExpression) visit(ctx.expression());
        }

        return new SReturn(nextIdentifier(), location(ctx), expression);
    }

    @Override
    public ANode visitTry(TryContext ctx) {
        SBlock block = (SBlock)visit(ctx.block());
        List<SCatch> catches = new ArrayList<>();

        for (TrapContext trap : ctx.trap()) {
            catches.add((SCatch)visit(trap));
        }

        return new STry(nextIdentifier(), location(ctx), block, catches);
    }

    @Override
    public ANode visitThrow(ThrowContext ctx) {
        AExpression expression = (AExpression)visit(ctx.expression());

        return new SThrow(nextIdentifier(), location(ctx), expression);
    }

    @Override
    public ANode visitExpr(ExprContext ctx) {
        AExpression expression = (AExpression)visit(ctx.expression());

        return new SExpression(nextIdentifier(), location(ctx), expression);
    }

    @Override
    public ANode visitTrailer(TrailerContext ctx) {
        if (ctx.block() != null) {
            return visit(ctx.block());
        } else if (ctx.statement() != null) {
            List<AStatement> statements = new ArrayList<>();
            statements.add((AStatement)visit(ctx.statement()));

            return new SBlock(nextIdentifier(), location(ctx), statements);
        } else {
            throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
        }
    }

    @Override
    public ANode visitBlock(BlockContext ctx) {
        if (ctx.statement().isEmpty() && ctx.dstatement() == null) {
            return null;
        } else {
            List<AStatement> statements = new ArrayList<>();

            for (StatementContext statement : ctx.statement()) {
                statements.add((AStatement)visit(statement));
            }

            if (ctx.dstatement() != null) {
                statements.add((AStatement)visit(ctx.dstatement()));
            }

            return new SBlock(nextIdentifier(), location(ctx), statements);
        }
    }

    @Override
    public ANode visitEmpty(EmptyContext ctx) {
        throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
    }

    @Override
    public ANode visitInitializer(InitializerContext ctx) {
        if (ctx.declaration() != null) {
            return visit(ctx.declaration());
        } else if (ctx.expression() != null) {
            return visit(ctx.expression());
        } else {
            throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
        }
    }

    @Override
    public ANode visitAfterthought(AfterthoughtContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public ANode visitDeclaration(DeclarationContext ctx) {
        String type = ctx.decltype().getText();
        List<SDeclaration> declarations = new ArrayList<>();

        for (DeclvarContext declvar : ctx.declvar()) {
            String name = declvar.ID().getText();
            AExpression expression = declvar.expression() == null ? null : (AExpression)visit(declvar.expression());
            declarations.add(new SDeclaration(nextIdentifier(), location(declvar), type, name, expression));
        }

        return new SDeclBlock(nextIdentifier(), location(ctx), declarations);
    }

    @Override
    public ANode visitDecltype(DecltypeContext ctx) {
        throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
    }

    @Override
    public ANode visitType(TypeContext ctx) {
        throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
    }

    @Override
    public ANode visitDeclvar(DeclvarContext ctx) {
        throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
    }

    @Override
    public ANode visitTrap(TrapContext ctx) {
        String type = ctx.type().getText();
        String name = ctx.ID().getText();
        SBlock block = (SBlock)visit(ctx.block());

        return new SCatch(nextIdentifier(), location(ctx), Exception.class, type, name, block);
    }

    @Override
    public ANode visitSingle(SingleContext ctx) {
        return visit(ctx.unary());
    }

    @Override
    public ANode visitBinary(BinaryContext ctx) {
        AExpression left = (AExpression)visit(ctx.noncondexpression(0));
        AExpression right = (AExpression)visit(ctx.noncondexpression(1));
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

        return new EBinary(nextIdentifier(), location(ctx), left, right, operation);
    }

    @Override
    public ANode visitComp(CompContext ctx) {
        AExpression left = (AExpression)visit(ctx.noncondexpression(0));
        AExpression right = (AExpression)visit(ctx.noncondexpression(1));
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

        return new EComp(nextIdentifier(), location(ctx), left, right, operation);
    }

    @Override
    public ANode visitInstanceof(InstanceofContext ctx) {
        AExpression expr = (AExpression)visit(ctx.noncondexpression());
        String type = ctx.decltype().getText();

        return new EInstanceof(nextIdentifier(), location(ctx), expr, type);
    }

    @Override
    public ANode visitBool(BoolContext ctx) {
        AExpression left = (AExpression)visit(ctx.noncondexpression(0));
        AExpression right = (AExpression)visit(ctx.noncondexpression(1));
        final Operation operation;

        if (ctx.BOOLAND() != null) {
            operation = Operation.AND;
        } else if (ctx.BOOLOR() != null) {
            operation = Operation.OR;
        } else {
            throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
        }

        return new EBooleanComp(nextIdentifier(), location(ctx), left, right, operation);
    }

    @Override
    public ANode visitElvis(ElvisContext ctx) {
        AExpression left = (AExpression)visit(ctx.noncondexpression(0));
        AExpression right = (AExpression)visit(ctx.noncondexpression(1));

        return new EElvis(nextIdentifier(), location(ctx), left, right);
    }

    @Override
    public ANode visitNonconditional(NonconditionalContext ctx) {
        return visit(ctx.noncondexpression());
    }

    @Override
    public ANode visitConditional(ConditionalContext ctx) {
        AExpression condition = (AExpression)visit(ctx.noncondexpression());
        AExpression left = (AExpression)visit(ctx.expression(0));
        AExpression right = (AExpression)visit(ctx.expression(1));

        return new EConditional(nextIdentifier(), location(ctx), condition, left, right);
    }

    @Override
    public ANode visitAssignment(AssignmentContext ctx) {
        AExpression lhs = (AExpression)visit(ctx.noncondexpression());
        AExpression rhs = (AExpression)visit(ctx.expression());

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

        return new EAssignment(nextIdentifier(), location(ctx), lhs, rhs, false, operation);
    }

    @Override
    public ANode visitPre(PreContext ctx) {
        AExpression expression = (AExpression)visit(ctx.chain());

        final Operation operation;

        if (ctx.INCR() != null) {
            operation = Operation.ADD;
        } else if (ctx.DECR() != null) {
            operation = Operation.SUB;
        } else {
            throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
        }

        return new EAssignment(nextIdentifier(), location(ctx), expression,
                new ENumeric(nextIdentifier(), location(ctx), "1", 10), false, operation);
    }

    @Override
    public ANode visitAddsub(AddsubContext ctx) {
        AExpression expression = (AExpression)visit(ctx.unary());

        final Operation operation;

        if (ctx.ADD() != null) {
            operation = Operation.ADD;
        } else if (ctx.SUB() != null) {
            operation = Operation.SUB;
        } else {
            throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
        }

        return new EUnary(nextIdentifier(), location(ctx), expression, operation);
    }

    @Override
    public ANode visitNotaddsub(NotaddsubContext ctx) {
        return visit(ctx.unarynotaddsub());
    }

    @Override
    public ANode visitRead(ReadContext ctx) {
        return visit(ctx.chain());
    }

    @Override
    public ANode visitPost(PostContext ctx) {
        AExpression expression = (AExpression)visit(ctx.chain());

        final Operation operation;

        if (ctx.INCR() != null) {
            operation = Operation.ADD;
        } else if (ctx.DECR() != null) {
            operation = Operation.SUB;
        } else {
            throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
        }

        return new EAssignment(nextIdentifier(), location(ctx), expression,
                new ENumeric(nextIdentifier(), location(ctx), "1", 10), true, operation);
    }

    @Override
    public ANode visitNot(NotContext ctx) {
        AExpression expression = (AExpression)visit(ctx.unary());

        final Operation operation;

        if (ctx.BOOLNOT() != null) {
            operation = Operation.NOT;
        } else if (ctx.BWNOT() != null) {
            operation = Operation.BWNOT;
        } else {
            throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
        }

        return new EUnary(nextIdentifier(), location(ctx), expression, operation);
    }

    @Override
    public ANode visitCast(CastContext ctx) {
        return visit(ctx.castexpression());
    }

    @Override
    public ANode visitPrimordefcast(PainlessParser.PrimordefcastContext ctx) {
        String type = ctx.primordefcasttype().getText();
        AExpression child = (AExpression)visit(ctx.unary());

        return new EExplicit(nextIdentifier(), location(ctx), type, child);
    }

    @Override
    public ANode visitRefcast(PainlessParser.RefcastContext ctx) {
        String type = ctx.refcasttype().getText();
        AExpression child = (AExpression)visit(ctx.unarynotaddsub());

        return new EExplicit(nextIdentifier(), location(ctx), type, child);
    }

    @Override
    public ANode visitPrimordefcasttype(PainlessParser.PrimordefcasttypeContext ctx) {
        throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
    }

    @Override
    public ANode visitRefcasttype(PainlessParser.RefcasttypeContext ctx) {
        throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
    }

    @Override
    public ANode visitDynamic(DynamicContext ctx) {
        AExpression primary = (AExpression)visit(ctx.primary());

        return buildPostfixChain(primary, null, ctx.postfix());
    }

    @Override
    public ANode visitNewarray(NewarrayContext ctx) {
        return visit(ctx.arrayinitializer());
    }

    @Override
    public ANode visitPrecedence(PrecedenceContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public ANode visitNumeric(NumericContext ctx) {
        if (ctx.DECIMAL() != null) {
            return new EDecimal(nextIdentifier(), location(ctx), ctx.DECIMAL().getText());
        } else if (ctx.HEX() != null) {
            return new ENumeric(nextIdentifier(), location(ctx), ctx.HEX().getText().substring(2), 16);
        } else if (ctx.INTEGER() != null) {
            return new ENumeric(nextIdentifier(), location(ctx), ctx.INTEGER().getText(), 10);
        } else if (ctx.OCTAL() != null) {
            return new ENumeric(nextIdentifier(), location(ctx), ctx.OCTAL().getText().substring(1), 8);
        } else {
            throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
        }
    }

    @Override
    public ANode visitTrue(TrueContext ctx) {
        return new EBooleanConstant(nextIdentifier(), location(ctx), true);
    }

    @Override
    public ANode visitFalse(FalseContext ctx) {
        return new EBooleanConstant(nextIdentifier(), location(ctx), false);
    }

    @Override
    public ANode visitNull(NullContext ctx) {
        return new ENull(nextIdentifier(), location(ctx));
    }

    @Override
    public ANode visitString(StringContext ctx) {
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

        return new EString(nextIdentifier(), location(ctx), string.toString());
    }

    @Override
    public ANode visitRegex(RegexContext ctx) {
        String text = ctx.REGEX().getText();
        int lastSlash = text.lastIndexOf('/');
        String pattern = text.substring(1, lastSlash);
        String flags = text.substring(lastSlash + 1);

        return new ERegex(nextIdentifier(), location(ctx), pattern, flags);
    }

    @Override
    public ANode visitListinit(ListinitContext ctx) {
        return visit(ctx.listinitializer());
    }

    @Override
    public ANode visitMapinit(MapinitContext ctx) {
        return visit(ctx.mapinitializer());
    }

    @Override
    public ANode visitVariable(VariableContext ctx) {
        String name = ctx.ID().getText();

        return new ESymbol(nextIdentifier(), location(ctx), name);
    }

    @Override
    public ANode visitCalllocal(CalllocalContext ctx) {
        String name = ctx.ID().getText();
        List<AExpression> arguments = collectArguments(ctx.arguments());

        return new ECallLocal(nextIdentifier(), location(ctx), name, arguments);
    }

    @Override
    public ANode visitNewobject(NewobjectContext ctx) {
        String type = ctx.type().getText();
        List<AExpression> arguments = collectArguments(ctx.arguments());

        return new ENewObj(nextIdentifier(), location(ctx), type, arguments);
    }

    private AExpression buildPostfixChain(AExpression primary, PostdotContext postdot, List<PostfixContext> postfixes) {
        AExpression prefix = primary;

        if (postdot != null) {
            prefix = visitPostdot(postdot, prefix);
        }

        for (PostfixContext postfix : postfixes) {
            prefix = visitPostfix(postfix, prefix);
        }

        return prefix;
    }

    @Override
    public ANode visitPostfix(PostfixContext ctx) {
        throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
    }

    public AExpression visitPostfix(PostfixContext ctx, AExpression prefix) {
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
    public ANode visitPostdot(PostdotContext ctx) {
        throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
    }

    public AExpression visitPostdot(PostdotContext ctx, AExpression prefix) {
        if (ctx.callinvoke() != null) {
            return visitCallinvoke(ctx.callinvoke(), prefix);
        } else if (ctx.fieldaccess() != null) {
            return visitFieldaccess(ctx.fieldaccess(), prefix);
        } else {
            throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
        }
    }

    @Override
    public ANode visitCallinvoke(CallinvokeContext ctx) {
        throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
    }

    public AExpression visitCallinvoke(CallinvokeContext ctx, AExpression prefix) {
        String name = ctx.DOTID().getText();
        List<AExpression> arguments = collectArguments(ctx.arguments());

        return new ECall(nextIdentifier(), location(ctx), prefix, name, arguments, ctx.NSDOT() != null);
    }

    @Override
    public ANode visitFieldaccess(FieldaccessContext ctx) {
        throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
    }

    public AExpression visitFieldaccess(FieldaccessContext ctx, AExpression prefix) {
        final String value;

        if (ctx.DOTID() != null) {
            value = ctx.DOTID().getText();
        } else if (ctx.DOTINTEGER() != null) {
            value = ctx.DOTINTEGER().getText();
        } else {
            throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
        }

        return new EDot(nextIdentifier(), location(ctx), prefix, value, ctx.NSDOT() != null);
    }

    @Override
    public ANode visitBraceaccess(BraceaccessContext ctx) {
        throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
    }

    public AExpression visitBraceaccess(BraceaccessContext ctx, AExpression prefix) {
        AExpression expression = (AExpression)visit(ctx.expression());

        return new EBrace(nextIdentifier(), location(ctx), prefix, expression);
    }

    @Override
    public ANode visitNewstandardarray(NewstandardarrayContext ctx) {
        StringBuilder type = new StringBuilder(ctx.type().getText());
        List<AExpression> expressions = new ArrayList<>();

        for (ExpressionContext expression : ctx.expression()) {
            type.append("[]");
            expressions.add((AExpression)visit(expression));
        }

        return buildPostfixChain(
                new ENewArray(nextIdentifier(), location(ctx), type.toString(), expressions, false), ctx.postdot(), ctx.postfix());
    }

    @Override
    public ANode visitNewinitializedarray(NewinitializedarrayContext ctx) {
        String type = ctx.type().getText() + "[]";
        List<AExpression> expressions = new ArrayList<>();

        for (ExpressionContext expression : ctx.expression()) {
            expressions.add((AExpression)visit(expression));
        }

        return buildPostfixChain(new ENewArray(nextIdentifier(), location(ctx), type, expressions, true), null, ctx.postfix());
    }

    @Override
    public ANode visitListinitializer(ListinitializerContext ctx) {
        List<AExpression> values = new ArrayList<>();

        for (ExpressionContext expression : ctx.expression()) {
            values.add((AExpression)visit(expression));
        }

        return new EListInit(nextIdentifier(), location(ctx), values);
    }

    @Override
    public ANode visitMapinitializer(MapinitializerContext ctx) {
        List<AExpression> keys = new ArrayList<>();
        List<AExpression> values = new ArrayList<>();

        for (MaptokenContext maptoken : ctx.maptoken()) {
            keys.add((AExpression)visit(maptoken.expression(0)));
            values.add((AExpression)visit(maptoken.expression(1)));
        }

        return new EMapInit(nextIdentifier(), location(ctx), keys, values);
    }

    @Override
    public ANode visitMaptoken(MaptokenContext ctx) {
        throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
    }

    @Override
    public ANode visitArguments(ArgumentsContext ctx) {
        throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
    }

    private List<AExpression> collectArguments(ArgumentsContext ctx) {
        List<AExpression> arguments = new ArrayList<>();

        for (ArgumentContext argument : ctx.argument()) {
            arguments.add((AExpression)visit(argument));
        }

        return arguments;
    }

    @Override
    public ANode visitArgument(ArgumentContext ctx) {
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
    public ANode visitLambda(LambdaContext ctx) {
        List<String> paramTypes = new ArrayList<>();
        List<String> paramNames = new ArrayList<>();
        SBlock block;

        for (LamtypeContext lamtype : ctx.lamtype()) {
            if (lamtype.decltype() == null) {
                paramTypes.add(null);
            } else {
                paramTypes.add(lamtype.decltype().getText());
            }

            paramNames.add(lamtype.ID().getText());
        }

        if (ctx.expression() != null) {
            // single expression
            AExpression expression = (AExpression)visit(ctx.expression());
            block = new SBlock(nextIdentifier(), location(ctx),
                    Collections.singletonList(new SReturn(nextIdentifier(), location(ctx), expression)));
        } else {
            block = (SBlock)visit(ctx.block());
        }

        return new ELambda(nextIdentifier(), location(ctx), paramTypes, paramNames, block);
    }

    @Override
    public ANode visitLamtype(LamtypeContext ctx) {
        throw location(ctx).createError(new IllegalStateException("illegal tree structure"));
    }

    @Override
    public ANode visitClassfuncref(ClassfuncrefContext ctx) {
        return new EFunctionRef(nextIdentifier(), location(ctx), ctx.decltype().getText(), ctx.ID().getText());
    }

    @Override
    public ANode visitConstructorfuncref(ConstructorfuncrefContext ctx) {
        return ctx.decltype().LBRACE().isEmpty() ?
                new EFunctionRef(nextIdentifier(), location(ctx), ctx.decltype().getText(), ctx.NEW().getText()) :
                new ENewArrayFunctionRef(nextIdentifier(), location(ctx), ctx.decltype().getText());
    }

    @Override
    public ANode visitLocalfuncref(LocalfuncrefContext ctx) {
        return new EFunctionRef(nextIdentifier(), location(ctx), ctx.THIS().getText(), ctx.ID().getText());
    }*/
}

/*
0 source RULE_START
    [ EOF LBRACE LP IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT
        ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL PRIMITIVE DEF ID ]
    [ [ 81 ] ]
1 source RULE_STOP
    [ ]
    []
2 function RULE_START
    [ PRIMITIVE DEF ID ]
    [ [ 92 ] ]
3 function RULE_STOP
    [ ]
    [ [ 80 ] ]
4 parameters RULE_START
    [ LP ]
    [ [ 97 ] ]
5 parameters RULE_STOP
    [ ]
    [ [ 95 ] ]
6 statement RULE_START
    [ LBRACE LP IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX
        INTEGER DECIMAL STRING REGEX TRUE FALSE NULL PRIMITIVE DEF ID ]
    [ [ 117 ] ]
7 statement RULE_STOP
    [ ]
    [ [ 86 ]  [ 203 ]  [ 207 ] ]
8 rstatement RULE_START
    [ IF WHILE FOR TRY ]
    [ [ 179 ] ]
9 rstatement RULE_STOP
    [ ]
    [ [ 118 ] ]
10 dstatement RULE_START
    [ LBRACE LP DO CONTINUE BREAK RETURN NEW THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL
        STRING REGEX TRUE FALSE NULL PRIMITIVE DEF ID ]
    [ [ 198 ] ]
11 dstatement RULE_STOP
    [ ]
    [ [ 115 ]  [ 213 ] ]
12 trailer RULE_START
    [ LBRACK LBRACE LP IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL
        HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL PRIMITIVE DEF ID ]
    [ [ 202 ] ]
13 trailer RULE_STOP
    [ ]
    [ [ 127 ]  [ 128 ]  [ 136 ]  [ 154 ]  [ 163 ]  [ 171 ] ]
14 block RULE_START
    [ LBRACK ]
    [ [ 204 ] ]
15 block RULE_STOP
    [ ]
    [ [ 96 ]  [ 175 ]  [ 183 ]  [ 203 ]  [ 264 ]  [ 552 ] ]
16 empty RULE_START
    [ SEMICOLON ]
    [ [ 216 ] ]
17 empty RULE_STOP
    [ ]
    [ [ 136 ]  [ 154 ] ]
18 initializer RULE_START
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL PRIMITIVE DEF ID ]
    [ [ 220 ] ]
19 initializer RULE_STOP
    [ ]
    [ [ 141 ] ]
20 afterthought RULE_START
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 222 ] ]
21 afterthought RULE_STOP
    [ ]
    [ [ 149 ] ]
22 declaration RULE_START
    [ PRIMITIVE DEF ID ]
    [ [ 224 ] ]
23 declaration RULE_STOP
    [ ]
    [ [ 199 ]  [ 221 ] ]
24 decltype RULE_START
    [ PRIMITIVE DEF ID ]
    [ [ 233 ] ]
25 decltype RULE_STOP
    [ ]
    [ [ 93 ]  [ 99 ]  [ 102 ]  [ 158 ]  [ 225 ]  [ 308 ]  [ 555 ]  [ 559 ]  [ 563 ] ]
26 type RULE_START
    [ PRIMITIVE DEF ID ]
    [ [ 251 ] ]
27 type RULE_STOP
    [ ]
    [ [ 238 ]  [ 261 ]  [ 413 ]  [ 443 ]  [ 458 ] ]
28 declvar RULE_START
    [ ID ]
    [ [ 253 ] ]
29 declvar RULE_STOP
    [ ]
    [ [ 230 ]  [ 229 ] ]
30 trap RULE_START
    [ CATCH ]
    [ [ 258 ] ]
31 trap RULE_STOP
    [ ]
    [ [ 176 ] ]
32 noncondexpression RULE_START
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 265 ] ]
33 noncondexpression RULE_STOP
    [ ]
    [ [ 308 ]  [ 308 ]  [ 308 ]  [ 308 ]  [ 308 ]  [ 308 ]  [ 308 ]  [ 308 ]  [ 308 ]  [ 308 ]  [ 308 ]  [ 308 ]  [ 324 ]  [ 314 ]  [ 320 ] ]
34 expression RULE_START
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 323 ] ]
35 expression RULE_STOP
    [ ]
    [ [ 122 ]  [ 132 ]  [ 145 ]  [ 161 ]  [ 169 ]  [ 186 ]  [ 194 ]  [ 199 ]  [ 199 ]
      [ 221 ]  [ 223 ]  [ 257 ]  [ 316 ]  [ 318 ]  [ 322 ]  [ 398 ]  [ 435 ]  [ 441 ]
      [ 466 ]  [ 465 ]  [ 486 ]  [ 485 ]  [ 512 ]  [ 514 ]  [ 532 ]  [ 552 ] ]
36 unary RULE_START
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 330 ] ]
37 unary RULE_STOP
    [ ]
    [ [ 267 ]  [ 331 ]  [ 340 ]  [ 345 ] ]
38 unarynotaddsub RULE_START
    [ LBRACE LP NEW BOOLNOT BWNOT OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 339 ] ]
39 unarynotaddsub RULE_STOP
    [ ]
    [ [ 331 ]  [ 350 ] ]
40 castexpression RULE_START
    [ LP ]
    [ [ 351 ] ]
41 castexpression RULE_STOP
    [ ]
    [ [ 340 ] ]
42 primordefcasttype RULE_START
    [ PRIMITIVE DEF ]
    [ [ 353 ] ]
43 primordefcasttype RULE_STOP
    [ ]
    [ [ 343 ] ]
44 refcasttype RULE_START
    [ PRIMITIVE DEF ID ]
    [ [ 384 ] ]
45 refcasttype RULE_STOP
    [ ]
    [ [ 348 ] ]
46 chain RULE_START
    [ LBRACE LP NEW OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 394 ] ]
47 chain RULE_STOP
    [ ]
    [ [ 331 ]  [ 340 ]  [ 334 ] ]
48 primary RULE_START
    [ LBRACE LP NEW OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 415 ] ]
49 primary RULE_STOP
    [ ]
    [ [ 390 ] ]
50 postfix RULE_START
    [ LBRACE DOT NSDOT ]
    [ [ 420 ] ]
51 postfix RULE_STOP
    [ ]
    [ [ 389 ]  [ 450 ]  [ 474 ] ]
52 postdot RULE_START
    [ DOT NSDOT ]
    [ [ 424 ] ]
53 postdot RULE_STOP
    [ ]
    [ [ 451 ] ]
54 callinvoke RULE_START
    [ DOT NSDOT ]
    [ [ 426 ] ]
55 callinvoke RULE_STOP
    [ ]
    [ [ 421 ]  [ 425 ] ]
56 fieldaccess RULE_START
    [ DOT NSDOT ]
    [ [ 430 ] ]
57 fieldaccess RULE_STOP
    [ ]
    [ [ 421 ]  [ 425 ] ]
58 braceaccess RULE_START
    [ LBRACE ]
    [ [ 433 ] ]
59 braceaccess RULE_STOP
    [ ]
    [ [ 421 ] ]
60 arrayinitializer RULE_START
    [ NEW ]
    [ [ 478 ] ]
61 arrayinitializer RULE_STOP
    [ ]
    [ [ 395 ] ]
62 listinitializer RULE_START
    [ LBRACE ]
    [ [ 493 ] ]
63 listinitializer RULE_STOP
    [ ]
    [ [ 416 ] ]
64 mapinitializer RULE_START
    [ LBRACE ]
    [ [ 509 ] ]
65 mapinitializer RULE_STOP
    [ ]
    [ [ 416 ] ]
66 maptoken RULE_START
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 511 ] ]
67 maptoken RULE_STOP
    [ ]
    [ [ 501 ]  [ 500 ] ]
68 arguments RULE_START
    [ LP ]
    [ [ 515 ] ]
69 arguments RULE_STOP
    [ ]
    [ [ 416 ]  [ 414 ]  [ 429 ] ]
70 argument RULE_START
    [ LBRACE LP NEW THIS BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL PRIMITIVE DEF ID ]
    [ [ 531 ] ]
71 argument RULE_STOP
    [ ]
    [ [ 521 ]  [ 520 ] ]
72 lambda RULE_START
    [ LP PRIMITIVE DEF ID ]
    [ [ 546 ] ]
73 lambda RULE_STOP
    [ ]
    [ [ 532 ] ]
74 lamtype RULE_START
    [ PRIMITIVE DEF ID ]
    [ [ 554 ] ]
75 lamtype RULE_STOP
    [ ]
    [ [ 547 ]  [ 540 ]  [ 539 ] ]
76 funcref RULE_START
    [ THIS PRIMITIVE DEF ID ]
    [ [ 569 ] ]
77 funcref RULE_STOP
    [ ]
    [ [ 532 ] ]
78 source BASIC
    [ PRIMITIVE DEF ID ]
    [ [ 2 ] ]
79 source STAR_BLOCK_START
    [ PRIMITIVE DEF ID ]
    [ [ 78 ] ]
80 source BLOCK_END
    [ EOF LBRACE LP IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL PRIMITIVE DEF ID ]
    [ [ 83 ] ]
81 source STAR_LOOP_ENTRY
    [ EOF LBRACE LP IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL PRIMITIVE DEF ID ]
    [ [ 79 ]  [ 82 ] ]
82 source LOOP_END
    [ EOF LBRACE LP IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL PRIMITIVE DEF ID ]
    [ [ 87 ] ]
83 source STAR_LOOP_BACK
    [ EOF LBRACE LP IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL PRIMITIVE DEF ID ]
    [ [ 81 ] ]
84 source BASIC
    [ LBRACE LP IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL PRIMITIVE DEF ID ]
    [ [ 6 ] ]
85 source STAR_BLOCK_START
    [ LBRACE LP IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL PRIMITIVE DEF ID ]
    [ [ 84 ] ]
86 source BLOCK_END
    [ EOF LBRACE LP IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL PRIMITIVE DEF ID ]
    [ [ 89 ] ]
87 source STAR_LOOP_ENTRY
    [ EOF LBRACE LP IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL PRIMITIVE DEF ID ]
    [ [ 85 ]  [ 88 ] ]
88 source LOOP_END
    [ EOF ]
    [ [ 90 ] ]
89 source STAR_LOOP_BACK
    [ EOF LBRACE LP IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL PRIMITIVE DEF ID ]
    [ [ 87 ] ]
90 source BASIC
    [ EOF ]
    [ [ 91 : EOF ] ]
91 source BASIC
    [ ]
    [ [ 1 ] ]
92 function BASIC
    [ PRIMITIVE DEF ID ]
    [ [ 24 ] ]
93 function BASIC
    [ ID ]
    [ [ 94 : ID ] ]
94 function BASIC
    [ LP ]
    [ [ 4 ] ]
95 function BASIC
    [ LBRACK ]
    [ [ 14 ] ]
96 function BASIC
    [ ]
    [ [ 3 ] ]
97 parameters BASIC
    [ LP ]
    [ [ 109 : LP ] ]
98 parameters BASIC
    [ PRIMITIVE DEF ID ]
    [ [ 24 ] ]
99 parameters BASIC
    [ ID ]
    [ [ 106 : ID ] ]
100 parameters BASIC
    [ COMMA ]
    [ [ 101 : COMMA ] ]
101 parameters BASIC
    [ PRIMITIVE DEF ID ]
    [ [ 24 ] ]
102 parameters BASIC
    [ ID ]
    [ [ 103 : ID ] ]
103 parameters BASIC
    [ RP COMMA ]
    [ [ 105 ] ]
104 parameters STAR_BLOCK_START
    [ COMMA ]
    [ [ 100 ] ]
105 parameters BLOCK_END
    [ RP COMMA ]
    [ [ 108 ] ]
106 parameters STAR_LOOP_ENTRY
    [ RP COMMA ]
    [ [ 104 ]  [ 107 ] ]
107 parameters LOOP_END
    [ RP ]
    [ [ 110 ] ]
108 parameters STAR_LOOP_BACK
    [ RP COMMA ]
    [ [ 106 ] ]
109 parameters BLOCK_START
    [ RP PRIMITIVE DEF ID ]
    [ [ 98 ]  [ 110 ] ]
110 parameters BLOCK_END
    [ RP ]
    [ [ 111 ] ]
111 parameters BASIC
    [ RP ]
    [ [ 112 : RP ] ]
112 parameters BASIC
    [ ]
    [ [ 5 ] ]
113 statement BASIC
    [ IF WHILE FOR TRY ]
    [ [ 8 ] ]
114 statement BASIC
    [ LBRACE LP DO CONTINUE BREAK RETURN NEW THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL PRIMITIVE DEF ID ]
    [ [ 10 ] ]
115 statement BASIC
    [ EOF SEMICOLON ]
    [ [ 116 : EOF SEMICOLON ] ]
116 statement BASIC
    [ ]
    [ [ 118 ] ]
117 statement BLOCK_START
    [ LBRACE LP IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL PRIMITIVE DEF ID ]
    [ [ 113 ]  [ 114 ] ]
118 statement BLOCK_END
    [ ]
    [ [ 7 ] ]
119 rstatement BASIC
    [ IF ]
    [ [ 120 : IF ] ]
120 rstatement BASIC
    [ LP ]
    [ [ 121 : LP ] ]
121 rstatement BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 34 ] ]
122 rstatement BASIC
    [ RP ]
    [ [ 123 : RP ] ]
123 rstatement BASIC
    [ LBRACK LBRACE LP IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL PRIMITIVE DEF ID ]
    [ [ 12 ] ]
124 rstatement BASIC
    [ ELSE ]
    [ [ 125 : ELSE ] ]
125 rstatement BASIC
    [ LBRACK LBRACE LP IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL PRIMITIVE DEF ID ]
    [ [ 12 ] ]
126 rstatement BASIC
    [ ]
    [ [ 128 ] ]
127 rstatement BLOCK_START
    [ ELSE ]
    [ [ 124 ]  [ 126 ] ]
128 rstatement BLOCK_END
    [ ]
    [ [ 180 ] ]
129 rstatement BASIC
    [ WHILE ]
    [ [ 130 : WHILE ] ]
130 rstatement BASIC
    [ LP ]
    [ [ 131 : LP ] ]
131 rstatement BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 34 ] ]
132 rstatement BASIC
    [ RP ]
    [ [ 135 : RP ] ]
133 rstatement BASIC
    [ LBRACK LBRACE LP IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL PRIMITIVE DEF ID ]
    [ [ 12 ] ]
134 rstatement BASIC
    [ SEMICOLON ]
    [ [ 16 ] ]
135 rstatement BLOCK_START
    [ LBRACK LBRACE LP SEMICOLON IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL PRIMITIVE DEF ID ]
    [ [ 133 ]  [ 134 ] ]
136 rstatement BLOCK_END
    [ ]
    [ [ 180 ] ]
137 rstatement BASIC
    [ FOR ]
    [ [ 138 : FOR ] ]
138 rstatement BASIC
    [ LP ]
    [ [ 140 : LP ] ]
139 rstatement BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL PRIMITIVE DEF ID ]
    [ [ 18 ] ]
140 rstatement BLOCK_START
    [ LBRACE LP SEMICOLON NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL PRIMITIVE DEF ID ]
    [ [ 139 ]  [ 141 ] ]
141 rstatement BLOCK_END
    [ SEMICOLON ]
    [ [ 142 ] ]
142 rstatement BASIC
    [ SEMICOLON ]
    [ [ 144 : SEMICOLON ] ]
143 rstatement BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 34 ] ]
144 rstatement BLOCK_START
    [ LBRACE LP SEMICOLON NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 143 ]  [ 145 ] ]
145 rstatement BLOCK_END
    [ SEMICOLON ]
    [ [ 146 ] ]
146 rstatement BASIC
    [ SEMICOLON ]
    [ [ 148 : SEMICOLON ] ]
147 rstatement BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 20 ] ]
148 rstatement BLOCK_START
    [ LBRACE LP RP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 147 ]  [ 149 ] ]
149 rstatement BLOCK_END
    [ RP ]
    [ [ 150 ] ]
150 rstatement BASIC
    [ RP ]
    [ [ 153 : RP ] ]
151 rstatement BASIC
    [ LBRACK LBRACE LP IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL PRIMITIVE DEF ID ]
    [ [ 12 ] ]
152 rstatement BASIC
    [ SEMICOLON ]
    [ [ 16 ] ]
153 rstatement BLOCK_START
    [ LBRACK LBRACE LP SEMICOLON IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL PRIMITIVE DEF ID ]
    [ [ 151 ]  [ 152 ] ]
154 rstatement BLOCK_END
    [ ]
    [ [ 180 ] ]
155 rstatement BASIC
    [ FOR ]
    [ [ 156 : FOR ] ]
156 rstatement BASIC
    [ LP ]
    [ [ 157 : LP ] ]
157 rstatement BASIC
    [ PRIMITIVE DEF ID ]
    [ [ 24 ] ]
158 rstatement BASIC
    [ ID ]
    [ [ 159 : ID ] ]
159 rstatement BASIC
    [ COLON ]
    [ [ 160 : COLON ] ]
160 rstatement BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 34 ] ]
161 rstatement BASIC
    [ RP ]
    [ [ 162 : RP ] ]
162 rstatement BASIC
    [ LBRACK LBRACE LP IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL PRIMITIVE DEF ID ]
    [ [ 12 ] ]
163 rstatement BASIC
    [ ]
    [ [ 180 ] ]
164 rstatement BASIC
    [ FOR ]
    [ [ 165 : FOR ] ]
165 rstatement BASIC
    [ LP ]
    [ [ 166 : LP ] ]
166 rstatement BASIC
    [ ID ]
    [ [ 167 : ID ] ]
167 rstatement BASIC
    [ IN ]
    [ [ 168 : IN ] ]
168 rstatement BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 34 ] ]
169 rstatement BASIC
    [ RP ]
    [ [ 170 : RP ] ]
170 rstatement BASIC
    [ LBRACK LBRACE LP IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL PRIMITIVE DEF ID ]
    [ [ 12 ] ]
171 rstatement BASIC
    [ ]
    [ [ 180 ] ]
172 rstatement BASIC
    [ TRY ]
    [ [ 173 : TRY ] ]
173 rstatement BASIC
    [ LBRACK ]
    [ [ 14 ] ]
174 rstatement BASIC
    [ CATCH ]
    [ [ 30 ] ]
175 rstatement PLUS_BLOCK_START
    [ CATCH ]
    [ [ 174 ] ]
176 rstatement BLOCK_END
    [ CATCH ]
    [ [ 177 ] ]
177 rstatement PLUS_LOOP_BACK
    [ CATCH ]
    [ [ 175 ]  [ 178 ] ]
178 rstatement LOOP_END
    [ ]
    [ [ 180 ] ]
179 rstatement BLOCK_START
    [ IF WHILE FOR TRY ]
    [ [ 119 ]  [ 129 ]  [ 137 ]  [ 155 ]  [ 164 ]  [ 172 ] ]
180 rstatement BLOCK_END
    [ ]
    [ [ 9 ] ]
181 dstatement BASIC
    [ DO ]
    [ [ 182 : DO ] ]
182 dstatement BASIC
    [ LBRACK ]
    [ [ 14 ] ]
183 dstatement BASIC
    [ WHILE ]
    [ [ 184 : WHILE ] ]
184 dstatement BASIC
    [ LP ]
    [ [ 185 : LP ] ]
185 dstatement BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 34 ] ]
186 dstatement BASIC
    [ RP ]
    [ [ 187 : RP ] ]
187 dstatement BASIC
    [ ]
    [ [ 199 ] ]
188 dstatement BASIC
    [ PRIMITIVE DEF ID ]
    [ [ 22 ] ]
189 dstatement BASIC
    [ CONTINUE ]
    [ [ 199 : CONTINUE ] ]
190 dstatement BASIC
    [ BREAK ]
    [ [ 199 : BREAK ] ]
191 dstatement BASIC
    [ RETURN ]
    [ [ 193 : RETURN ] ]
192 dstatement BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 34 ] ]
193 dstatement BLOCK_START
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 192 ]  [ 194 ] ]
194 dstatement BLOCK_END
    [ ]
    [ [ 199 ] ]
195 dstatement BASIC
    [ THROW ]
    [ [ 196 : THROW ] ]
196 dstatement BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 34 ] ]
197 dstatement BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 34 ] ]
198 dstatement BLOCK_START
    [ LBRACE LP DO CONTINUE BREAK RETURN NEW THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL PRIMITIVE DEF ID ]
    [ [ 181 ]  [ 188 ]  [ 189 ]  [ 190 ]  [ 191 ]  [ 195 ]  [ 197 ] ]
199 dstatement BLOCK_END
    [ ]
    [ [ 11 ] ]
200 trailer BASIC
    [ LBRACK ]
    [ [ 14 ] ]
201 trailer BASIC
    [ LBRACE LP IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL PRIMITIVE DEF ID ]
    [ [ 6 ] ]
202 trailer BLOCK_START
    [ LBRACK LBRACE LP IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL PRIMITIVE DEF ID ]
    [ [ 200 ]  [ 201 ] ]
203 trailer BLOCK_END
    [ ]
    [ [ 13 ] ]
204 block BASIC
    [ LBRACK ]
    [ [ 208 : LBRACK ] ]
205 block BASIC
    [ LBRACE LP IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL PRIMITIVE DEF ID ]
    [ [ 6 ] ]
206 block STAR_BLOCK_START
    [ LBRACE LP IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL PRIMITIVE DEF ID ]
    [ [ 205 ] ]
207 block BLOCK_END
    [ RBRACK LBRACE LP IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL PRIMITIVE DEF ID ]
    [ [ 210 ] ]
208 block STAR_LOOP_ENTRY
    [ RBRACK LBRACE LP IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL PRIMITIVE DEF ID ]
    [ [ 206 ]  [ 209 ] ]
209 block LOOP_END
    [ RBRACK LBRACE LP DO CONTINUE BREAK RETURN NEW THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL PRIMITIVE DEF ID ]
    [ [ 212 ] ]
210 block STAR_LOOP_BACK
    [ RBRACK LBRACE LP IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL PRIMITIVE DEF ID ]
    [ [ 208 ] ]
211 block BASIC
    [ LBRACE LP DO CONTINUE BREAK RETURN NEW THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL PRIMITIVE DEF ID ]
    [ [ 10 ] ]
212 block BLOCK_START
    [ RBRACK LBRACE LP DO CONTINUE BREAK RETURN NEW THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL PRIMITIVE DEF ID ]
    [ [ 211 ]  [ 213 ] ]
213 block BLOCK_END
    [ RBRACK ]
    [ [ 214 ] ]
214 block BASIC
    [ RBRACK ]
    [ [ 215 : RBRACK ] ]
215 block BASIC
    [ ]
    [ [ 15 ] ]
216 empty BASIC
    [ SEMICOLON ]
    [ [ 217 : SEMICOLON ] ]
217 empty BASIC
    [ ]
    [ [ 17 ] ]
218 initializer BASIC
    [ PRIMITIVE DEF ID ]
    [ [ 22 ] ]
219 initializer BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 34 ] ]
220 initializer BLOCK_START
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL PRIMITIVE DEF ID ]
    [ [ 218 ]  [ 219 ] ]
221 initializer BLOCK_END
    [ ]
    [ [ 19 ] ]
222 afterthought BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 34 ] ]
223 afterthought BASIC
    [ ]
    [ [ 21 ] ]
224 declaration BASIC
    [ PRIMITIVE DEF ID ]
    [ [ 24 ] ]
225 declaration BASIC
    [ ID ]
    [ [ 28 ] ]
226 declaration BASIC
    [ COMMA ]
    [ [ 227 : COMMA ] ]
227 declaration BASIC
    [ ID ]
    [ [ 28 ] ]
228 declaration STAR_BLOCK_START
    [ COMMA ]
    [ [ 226 ] ]
229 declaration BLOCK_END
    [ COMMA ]
    [ [ 232 ] ]
230 declaration STAR_LOOP_ENTRY
    [ COMMA ]
    [ [ 228 ]  [ 231 ] ]
231 declaration LOOP_END
    [ ]
    [ [ 23 ] ]
232 declaration STAR_LOOP_BACK
    [ COMMA ]
    [ [ 230 ] ]
233 decltype BASIC
    [ PRIMITIVE DEF ID ]
    [ [ 26 ] ]
234 decltype BASIC
    [ LBRACE ]
    [ [ 235 : LBRACE ] ]
235 decltype BASIC
    [ RBRACE ]
    [ [ 237 : RBRACE ] ]
236 decltype STAR_BLOCK_START
    [ LBRACE ]
    [ [ 234 ] ]
237 decltype BLOCK_END
    [ LBRACE ]
    [ [ 240 ] ]
238 decltype STAR_LOOP_ENTRY
    [ LBRACE ]
    [ [ 236 ]  [ 239 ] ]
239 decltype LOOP_END
    [ ]
    [ [ 25 ] ]
240 decltype STAR_LOOP_BACK
    [ LBRACE ]
    [ [ 238 ] ]
241 type BASIC
    [ DEF ]
    [ [ 252 : DEF ] ]
242 type BASIC
    [ PRIMITIVE ]
    [ [ 252 : PRIMITIVE ] ]
243 type BASIC
    [ ID ]
    [ [ 248 : ID ] ]
244 type BASIC
    [ DOT ]
    [ [ 245 : DOT ] ]
245 type BASIC
    [ DOTID ]
    [ [ 247 : DOTID ] ]
246 type STAR_BLOCK_START
    [ DOT ]
    [ [ 244 ] ]
247 type BLOCK_END
    [ DOT ]
    [ [ 250 ] ]
248 type STAR_LOOP_ENTRY
    [ DOT ]
    [ [ 246 ]  [ 249 ] ]
249 type LOOP_END
    [ ]
    [ [ 252 ] ]
250 type STAR_LOOP_BACK
    [ DOT ]
    [ [ 248 ] ]
251 type BLOCK_START
    [ PRIMITIVE DEF ID ]
    [ [ 241 ]  [ 242 ]  [ 243 ] ]
252 type BLOCK_END
    [ ]
    [ [ 27 ] ]
253 declvar BASIC
    [ ID ]
    [ [ 256 : ID ] ]
254 declvar BASIC
    [ ASSIGN ]
    [ [ 255 : ASSIGN ] ]
255 declvar BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 34 ] ]
256 declvar BLOCK_START
    [ ASSIGN ]
    [ [ 254 ]  [ 257 ] ]
257 declvar BLOCK_END
    [ ]
    [ [ 29 ] ]
258 trap BASIC
    [ CATCH ]
    [ [ 259 : CATCH ] ]
259 trap BASIC
    [ LP ]
    [ [ 260 : LP ] ]
260 trap BASIC
    [ PRIMITIVE DEF ID ]
    [ [ 26 ] ]
261 trap BASIC
    [ ID ]
    [ [ 262 : ID ] ]
262 trap BASIC
    [ RP ]
    [ [ 263 : RP ] ]
263 trap BASIC
    [ LBRACK ]
    [ [ 14 ] ]
264 trap BASIC
    [ ]
    [ [ 31 ] ]
265 noncondexpression BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 266 ] ]
266 noncondexpression BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 36 ] ]
267 noncondexpression BASIC
    [ INSTANCEOF MUL DIV REM ADD SUB LSH RSH USH LT LTE GT GTE EQ EQR NE NER BWAND XOR BWOR BOOLAND BOOLOR ELVIS FIND MATCH ]
    [ [ 309 ] ]
268 noncondexpression BASIC
    [ MUL DIV REM ]
    [ [ 269 ] ]
269 noncondexpression BASIC
    [ MUL DIV REM ]
    [ [ 270 : MUL DIV REM ] ]
270 noncondexpression BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 32 ] ]
271 noncondexpression BASIC
    [ ADD SUB ]
    [ [ 272 ] ]
272 noncondexpression BASIC
    [ ADD SUB ]
    [ [ 273 : ADD SUB ] ]
273 noncondexpression BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 32 ] ]
274 noncondexpression BASIC
    [ FIND MATCH ]
    [ [ 275 ] ]
275 noncondexpression BASIC
    [ FIND MATCH ]
    [ [ 276 : FIND MATCH ] ]
276 noncondexpression BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 32 ] ]
277 noncondexpression BASIC
    [ LSH RSH USH ]
    [ [ 278 ] ]
278 noncondexpression BASIC
    [ LSH RSH USH ]
    [ [ 279 : LSH RSH USH ] ]
279 noncondexpression BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 32 ] ]
280 noncondexpression BASIC
    [ LT LTE GT GTE ]
    [ [ 281 ] ]
281 noncondexpression BASIC
    [ LT LTE GT GTE ]
    [ [ 282 : LT LTE GT GTE ] ]
282 noncondexpression BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 32 ] ]
283 noncondexpression BASIC
    [ EQ EQR NE NER ]
    [ [ 284 ] ]
284 noncondexpression BASIC
    [ EQ EQR NE NER ]
    [ [ 285 : EQ EQR NE NER ] ]
285 noncondexpression BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 32 ] ]
286 noncondexpression BASIC
    [ BWAND ]
    [ [ 287 ] ]
287 noncondexpression BASIC
    [ BWAND ]
    [ [ 288 : BWAND ] ]
288 noncondexpression BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 32 ] ]
289 noncondexpression BASIC
    [ XOR ]
    [ [ 290 ] ]
290 noncondexpression BASIC
    [ XOR ]
    [ [ 291 : XOR ] ]
291 noncondexpression BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 32 ] ]
292 noncondexpression BASIC
    [ BWOR ]
    [ [ 293 ] ]
293 noncondexpression BASIC
    [ BWOR ]
    [ [ 294 : BWOR ] ]
294 noncondexpression BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 32 ] ]
295 noncondexpression BASIC
    [ BOOLAND ]
    [ [ 296 ] ]
296 noncondexpression BASIC
    [ BOOLAND ]
    [ [ 297 : BOOLAND ] ]
297 noncondexpression BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 32 ] ]
298 noncondexpression BASIC
    [ BOOLOR ]
    [ [ 299 ] ]
299 noncondexpression BASIC
    [ BOOLOR ]
    [ [ 300 : BOOLOR ] ]
300 noncondexpression BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 32 ] ]
301 noncondexpression BASIC
    [ ELVIS ]
    [ [ 302 ] ]
302 noncondexpression BASIC
    [ ELVIS ]
    [ [ 303 : ELVIS ] ]
303 noncondexpression BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 32 ] ]
304 noncondexpression BASIC
    [ INSTANCEOF ]
    [ [ 305 ] ]
305 noncondexpression BASIC
    [ INSTANCEOF ]
    [ [ 306 : INSTANCEOF ] ]
306 noncondexpression BASIC
    [ PRIMITIVE DEF ID ]
    [ [ 24 ] ]
307 noncondexpression STAR_BLOCK_START
    [ INSTANCEOF MUL DIV REM ADD SUB LSH RSH USH LT LTE GT GTE EQ EQR NE NER BWAND XOR BWOR BOOLAND BOOLOR ELVIS FIND MATCH ]
    [ [ 268 ]  [ 271 ]  [ 274 ]  [ 277 ]  [ 280 ]  [ 283 ]  [ 286 ]  [ 289 ]  [ 292 ]  [ 295 ]  [ 298 ]  [ 301 ]  [ 304 ] ]
308 noncondexpression BLOCK_END
    [ INSTANCEOF MUL DIV REM ADD SUB LSH RSH USH LT LTE GT GTE EQ EQR NE NER BWAND XOR BWOR BOOLAND BOOLOR ELVIS FIND MATCH ]
    [ [ 311 ] ]
309 noncondexpression STAR_LOOP_ENTRY
    [ INSTANCEOF MUL DIV REM ADD SUB LSH RSH USH LT LTE GT GTE EQ EQR NE NER BWAND XOR BWOR BOOLAND BOOLOR ELVIS FIND MATCH ]
    [ [ 307 ]  [ 310 ] ]
310 noncondexpression LOOP_END
    [ ]
    [ [ 33 ] ]
311 noncondexpression STAR_LOOP_BACK
    [ INSTANCEOF MUL DIV REM ADD SUB LSH RSH USH LT LTE GT GTE EQ EQR NE NER BWAND XOR BWOR BOOLAND BOOLOR ELVIS FIND MATCH ]
    [ [ 309 ] ]
312 expression BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 32 ] ]
313 expression BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 32 ] ]
314 expression BASIC
    [ COND ]
    [ [ 315 : COND ] ]
315 expression BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 34 ] ]
316 expression BASIC
    [ COLON ]
    [ [ 317 : COLON ] ]
317 expression BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 34 ] ]
318 expression BASIC
    [ ]
    [ [ 324 ] ]
319 expression BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 32 ] ]
320 expression BASIC
    [ ASSIGN AADD ASUB AMUL ADIV AREM AAND AXOR AOR ALSH ARSH AUSH ]
    [ [ 321 : ASSIGN AADD ASUB AMUL ADIV AREM AAND AXOR AOR ALSH ARSH AUSH ] ]
321 expression BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 34 ] ]
322 expression BASIC
    [ ]
    [ [ 324 ] ]
323 expression BLOCK_START
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 312 ]  [ 313 ]  [ 319 ] ]
324 expression BLOCK_END
    [ ]
    [ [ 35 ] ]
325 unary BASIC
    [ INCR DECR ]
    [ [ 326 : INCR DECR ] ]
326 unary BASIC
    [ LBRACE LP NEW OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 46 ] ]
327 unary BASIC
    [ ADD SUB ]
    [ [ 328 : ADD SUB ] ]
328 unary BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 36 ] ]
329 unary BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 38 ] ]
330 unary BLOCK_START
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 325 ]  [ 327 ]  [ 329 ] ]
331 unary BLOCK_END
    [ ]
    [ [ 37 ] ]
332 unarynotaddsub BASIC
    [ LBRACE LP NEW OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 46 ] ]
333 unarynotaddsub BASIC
    [ LBRACE LP NEW OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 46 ] ]
334 unarynotaddsub BASIC
    [ INCR DECR ]
    [ [ 335 : INCR DECR ] ]
335 unarynotaddsub BASIC
    [ ]
    [ [ 340 ] ]
336 unarynotaddsub BASIC
    [ BOOLNOT BWNOT ]
    [ [ 337 : BOOLNOT BWNOT ] ]
337 unarynotaddsub BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 36 ] ]
338 unarynotaddsub BASIC
    [ LP ]
    [ [ 40 ] ]
339 unarynotaddsub BLOCK_START
    [ LBRACE LP NEW BOOLNOT BWNOT OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 332 ]  [ 333 ]  [ 336 ]  [ 338 ] ]
340 unarynotaddsub BLOCK_END
    [ ]
    [ [ 39 ] ]
341 castexpression BASIC
    [ LP ]
    [ [ 342 : LP ] ]
342 castexpression BASIC
    [ PRIMITIVE DEF ]
    [ [ 42 ] ]
343 castexpression BASIC
    [ RP ]
    [ [ 344 : RP ] ]
344 castexpression BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 36 ] ]
345 castexpression BASIC
    [ ]
    [ [ 352 ] ]
346 castexpression BASIC
    [ LP ]
    [ [ 347 : LP ] ]
347 castexpression BASIC
    [ PRIMITIVE DEF ID ]
    [ [ 44 ] ]
348 castexpression BASIC
    [ RP ]
    [ [ 349 : RP ] ]
349 castexpression BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 38 ] ]
350 castexpression BASIC
    [ ]
    [ [ 352 ] ]
351 castexpression BLOCK_START
    [ LP ]
    [ [ 341 ]  [ 346 ] ]
352 castexpression BLOCK_END
    [ ]
    [ [ 41 ] ]
353 primordefcasttype BASIC
    [ PRIMITIVE DEF ]
    [ [ 354 : PRIMITIVE DEF ] ]
354 primordefcasttype BASIC
    [ ]
    [ [ 43 ] ]
355 refcasttype BASIC
    [ DEF ]
    [ [ 358 : DEF ] ]
356 refcasttype BASIC
    [ LBRACE ]
    [ [ 357 : LBRACE ] ]
357 refcasttype BASIC
    [ RBRACE ]
    [ [ 359 : RBRACE ] ]
358 refcasttype PLUS_BLOCK_START
    [ LBRACE ]
    [ [ 356 ] ]
359 refcasttype BLOCK_END
    [ LBRACE ]
    [ [ 360 ] ]
360 refcasttype PLUS_LOOP_BACK
    [ LBRACE ]
    [ [ 358 ]  [ 361 ] ]
361 refcasttype LOOP_END
    [ ]
    [ [ 385 ] ]
362 refcasttype BASIC
    [ PRIMITIVE ]
    [ [ 365 : PRIMITIVE ] ]
363 refcasttype BASIC
    [ LBRACE ]
    [ [ 364 : LBRACE ] ]
364 refcasttype BASIC
    [ RBRACE ]
    [ [ 366 : RBRACE ] ]
365 refcasttype PLUS_BLOCK_START
    [ LBRACE ]
    [ [ 363 ] ]
366 refcasttype BLOCK_END
    [ LBRACE ]
    [ [ 367 ] ]
367 refcasttype PLUS_LOOP_BACK
    [ LBRACE ]
    [ [ 365 ]  [ 368 ] ]
368 refcasttype LOOP_END
    [ ]
    [ [ 385 ] ]
369 refcasttype BASIC
    [ ID ]
    [ [ 374 : ID ] ]
370 refcasttype BASIC
    [ DOT ]
    [ [ 371 : DOT ] ]
371 refcasttype BASIC
    [ DOTID ]
    [ [ 373 : DOTID ] ]
372 refcasttype STAR_BLOCK_START
    [ DOT ]
    [ [ 370 ] ]
373 refcasttype BLOCK_END
    [ LBRACE DOT ]
    [ [ 376 ] ]
374 refcasttype STAR_LOOP_ENTRY
    [ LBRACE DOT ]
    [ [ 372 ]  [ 375 ] ]
375 refcasttype LOOP_END
    [ LBRACE ]
    [ [ 381 ] ]
376 refcasttype STAR_LOOP_BACK
    [ LBRACE DOT ]
    [ [ 374 ] ]
377 refcasttype BASIC
    [ LBRACE ]
    [ [ 378 : LBRACE ] ]
378 refcasttype BASIC
    [ RBRACE ]
    [ [ 380 : RBRACE ] ]
379 refcasttype STAR_BLOCK_START
    [ LBRACE ]
    [ [ 377 ] ]
380 refcasttype BLOCK_END
    [ LBRACE ]
    [ [ 383 ] ]
381 refcasttype STAR_LOOP_ENTRY
    [ LBRACE ]
    [ [ 379 ]  [ 382 ] ]
382 refcasttype LOOP_END
    [ ]
    [ [ 385 ] ]
383 refcasttype STAR_LOOP_BACK
    [ LBRACE ]
    [ [ 381 ] ]
384 refcasttype BLOCK_START
    [ PRIMITIVE DEF ID ]
    [ [ 355 ]  [ 362 ]  [ 369 ] ]
385 refcasttype BLOCK_END
    [ ]
    [ [ 45 ] ]
386 chain BASIC
    [ LBRACE LP NEW OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 48 ] ]
387 chain BASIC
    [ LBRACE DOT NSDOT ]
    [ [ 50 ] ]
388 chain STAR_BLOCK_START
    [ LBRACE DOT NSDOT ]
    [ [ 387 ] ]
389 chain BLOCK_END
    [ LBRACE DOT NSDOT ]
    [ [ 392 ] ]
390 chain STAR_LOOP_ENTRY
    [ LBRACE DOT NSDOT ]
    [ [ 388 ]  [ 391 ] ]
391 chain LOOP_END
    [ ]
    [ [ 395 ] ]
392 chain STAR_LOOP_BACK
    [ LBRACE DOT NSDOT ]
    [ [ 390 ] ]
393 chain BASIC
    [ NEW ]
    [ [ 60 ] ]
394 chain BLOCK_START
    [ LBRACE LP NEW OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 386 ]  [ 393 ] ]
395 chain BLOCK_END
    [ ]
    [ [ 47 ] ]
396 primary BASIC
    [ LP ]
    [ [ 397 : LP ] ]
397 primary BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 34 ] ]
398 primary BASIC
    [ RP ]
    [ [ 399 : RP ] ]
399 primary BASIC
    [ ]
    [ [ 416 ] ]
400 primary BASIC
    [ OCTAL HEX INTEGER DECIMAL ]
    [ [ 416 : OCTAL HEX INTEGER DECIMAL ] ]
401 primary BASIC
    [ TRUE ]
    [ [ 416 : TRUE ] ]
402 primary BASIC
    [ FALSE ]
    [ [ 416 : FALSE ] ]
403 primary BASIC
    [ NULL ]
    [ [ 416 : NULL ] ]
404 primary BASIC
    [ STRING ]
    [ [ 416 : STRING ] ]
405 primary BASIC
    [ REGEX ]
    [ [ 416 : REGEX ] ]
406 primary BASIC
    [ LBRACE ]
    [ [ 62 ] ]
407 primary BASIC
    [ LBRACE ]
    [ [ 64 ] ]
408 primary BASIC
    [ ID ]
    [ [ 416 : ID ] ]
409 primary BASIC
    [ ID ]
    [ [ 410 : ID ] ]
410 primary BASIC
    [ LP ]
    [ [ 68 ] ]
411 primary BASIC
    [ NEW ]
    [ [ 412 : NEW ] ]
412 primary BASIC
    [ PRIMITIVE DEF ID ]
    [ [ 26 ] ]
413 primary BASIC
    [ LP ]
    [ [ 68 ] ]
414 primary BASIC
    [ ]
    [ [ 416 ] ]
415 primary BLOCK_START
    [ LBRACE LP NEW OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 396 ]  [ 400 ]  [ 401 ]  [ 402 ]  [ 403 ]  [ 404 ]  [ 405 ]  [ 406 ]  [ 407 ]  [ 408 ]  [ 409 ]  [ 411 ] ]
416 primary BLOCK_END
    [ ]
    [ [ 49 ] ]
417 postfix BASIC
    [ DOT NSDOT ]
    [ [ 54 ] ]
418 postfix BASIC
    [ DOT NSDOT ]
    [ [ 56 ] ]
419 postfix BASIC
    [ LBRACE ]
    [ [ 58 ] ]
420 postfix BLOCK_START
    [ LBRACE DOT NSDOT ]
    [ [ 417 ]  [ 418 ]  [ 419 ] ]
421 postfix BLOCK_END
    [ ]
    [ [ 51 ] ]
422 postdot BASIC
    [ DOT NSDOT ]
    [ [ 54 ] ]
423 postdot BASIC
    [ DOT NSDOT ]
    [ [ 56 ] ]
424 postdot BLOCK_START
    [ DOT NSDOT ]
    [ [ 422 ]  [ 423 ] ]
425 postdot BLOCK_END
    [ ]
    [ [ 53 ] ]
426 callinvoke BASIC
    [ DOT NSDOT ]
    [ [ 427 : DOT NSDOT ] ]
427 callinvoke BASIC
    [ DOTID ]
    [ [ 428 : DOTID ] ]
428 callinvoke BASIC
    [ LP ]
    [ [ 68 ] ]
429 callinvoke BASIC
    [ ]
    [ [ 55 ] ]
430 fieldaccess BASIC
    [ DOT NSDOT ]
    [ [ 431 : DOT NSDOT ] ]
431 fieldaccess BASIC
    [ DOTINTEGER DOTID ]
    [ [ 432 : DOTINTEGER DOTID ] ]
432 fieldaccess BASIC
    [ ]
    [ [ 57 ] ]
433 braceaccess BASIC
    [ LBRACE ]
    [ [ 434 : LBRACE ] ]
434 braceaccess BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 34 ] ]
435 braceaccess BASIC
    [ RBRACE ]
    [ [ 436 : RBRACE ] ]
436 braceaccess BASIC
    [ ]
    [ [ 59 ] ]
437 arrayinitializer BASIC
    [ NEW ]
    [ [ 438 : NEW ] ]
438 arrayinitializer BASIC
    [ PRIMITIVE DEF ID ]
    [ [ 26 ] ]
439 arrayinitializer BASIC
    [ LBRACE ]
    [ [ 440 : LBRACE ] ]
440 arrayinitializer BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 34 ] ]
441 arrayinitializer BASIC
    [ RBRACE ]
    [ [ 442 : RBRACE ] ]
442 arrayinitializer BASIC
    [ LBRACE DOT NSDOT ]
    [ [ 444 ] ]
443 arrayinitializer PLUS_BLOCK_START
    [ LBRACE ]
    [ [ 439 ] ]
444 arrayinitializer BLOCK_END
    [ LBRACE DOT NSDOT ]
    [ [ 445 ] ]
445 arrayinitializer PLUS_LOOP_BACK
    [ LBRACE DOT NSDOT ]
    [ [ 443 ]  [ 446 ] ]
446 arrayinitializer LOOP_END
    [ DOT NSDOT ]
    [ [ 454 ] ]
447 arrayinitializer BASIC
    [ DOT NSDOT ]
    [ [ 52 ] ]
448 arrayinitializer BASIC
    [ LBRACE DOT NSDOT ]
    [ [ 50 ] ]
449 arrayinitializer STAR_BLOCK_START
    [ LBRACE DOT NSDOT ]
    [ [ 448 ] ]
450 arrayinitializer BLOCK_END
    [ LBRACE DOT NSDOT ]
    [ [ 453 ] ]
451 arrayinitializer STAR_LOOP_ENTRY
    [ LBRACE DOT NSDOT ]
    [ [ 449 ]  [ 452 ] ]
452 arrayinitializer LOOP_END
    [ ]
    [ [ 455 ] ]
453 arrayinitializer STAR_LOOP_BACK
    [ LBRACE DOT NSDOT ]
    [ [ 451 ] ]
454 arrayinitializer BLOCK_START
    [ DOT NSDOT ]
    [ [ 447 ]  [ 455 ] ]
455 arrayinitializer BLOCK_END
    [ ]
    [ [ 479 ] ]
456 arrayinitializer BASIC
    [ NEW ]
    [ [ 457 : NEW ] ]
457 arrayinitializer BASIC
    [ PRIMITIVE DEF ID ]
    [ [ 26 ] ]
458 arrayinitializer BASIC
    [ LBRACE ]
    [ [ 459 : LBRACE ] ]
459 arrayinitializer BASIC
    [ RBRACE ]
    [ [ 460 : RBRACE ] ]
460 arrayinitializer BASIC
    [ LBRACK ]
    [ [ 469 : LBRACK ] ]
461 arrayinitializer BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 34 ] ]
462 arrayinitializer BASIC
    [ COMMA ]
    [ [ 463 : COMMA ] ]
463 arrayinitializer BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 34 ] ]
464 arrayinitializer STAR_BLOCK_START
    [ COMMA ]
    [ [ 462 ] ]
465 arrayinitializer BLOCK_END
    [ RBRACK COMMA ]
    [ [ 468 ] ]
466 arrayinitializer STAR_LOOP_ENTRY
    [ RBRACK COMMA ]
    [ [ 464 ]  [ 467 ] ]
467 arrayinitializer LOOP_END
    [ RBRACK ]
    [ [ 470 ] ]
468 arrayinitializer STAR_LOOP_BACK
    [ RBRACK COMMA ]
    [ [ 466 ] ]
469 arrayinitializer BLOCK_START
    [ RBRACK LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 461 ]  [ 470 ] ]
470 arrayinitializer BLOCK_END
    [ RBRACK ]
    [ [ 471 ] ]
471 arrayinitializer BASIC
    [ RBRACK ]
    [ [ 475 : RBRACK ] ]
472 arrayinitializer BASIC
    [ LBRACE DOT NSDOT ]
    [ [ 50 ] ]
473 arrayinitializer STAR_BLOCK_START
    [ LBRACE DOT NSDOT ]
    [ [ 472 ] ]
474 arrayinitializer BLOCK_END
    [ LBRACE DOT NSDOT ]
    [ [ 477 ] ]
475 arrayinitializer STAR_LOOP_ENTRY
    [ LBRACE DOT NSDOT ]
    [ [ 473 ]  [ 476 ] ]
476 arrayinitializer LOOP_END
    [ ]
    [ [ 479 ] ]
477 arrayinitializer STAR_LOOP_BACK
    [ LBRACE DOT NSDOT ]
    [ [ 475 ] ]
478 arrayinitializer BLOCK_START
    [ NEW ]
    [ [ 437 ]  [ 456 ] ]
479 arrayinitializer BLOCK_END
    [ ]
    [ [ 61 ] ]
480 listinitializer BASIC
    [ LBRACE ]
    [ [ 481 : LBRACE ] ]
481 listinitializer BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 34 ] ]
482 listinitializer BASIC
    [ COMMA ]
    [ [ 483 : COMMA ] ]
483 listinitializer BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 34 ] ]
484 listinitializer STAR_BLOCK_START
    [ COMMA ]
    [ [ 482 ] ]
485 listinitializer BLOCK_END
    [ RBRACE COMMA ]
    [ [ 488 ] ]
486 listinitializer STAR_LOOP_ENTRY
    [ RBRACE COMMA ]
    [ [ 484 ]  [ 487 ] ]
487 listinitializer LOOP_END
    [ RBRACE ]
    [ [ 489 ] ]
488 listinitializer STAR_LOOP_BACK
    [ RBRACE COMMA ]
    [ [ 486 ] ]
489 listinitializer BASIC
    [ RBRACE ]
    [ [ 490 : RBRACE ] ]
490 listinitializer BASIC
    [ ]
    [ [ 494 ] ]
491 listinitializer BASIC
    [ LBRACE ]
    [ [ 492 : LBRACE ] ]
492 listinitializer BASIC
    [ RBRACE ]
    [ [ 494 : RBRACE ] ]
493 listinitializer BLOCK_START
    [ LBRACE ]
    [ [ 480 ]  [ 491 ] ]
494 listinitializer BLOCK_END
    [ ]
    [ [ 63 ] ]
495 mapinitializer BASIC
    [ LBRACE ]
    [ [ 496 : LBRACE ] ]
496 mapinitializer BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 66 ] ]
497 mapinitializer BASIC
    [ COMMA ]
    [ [ 498 : COMMA ] ]
498 mapinitializer BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 66 ] ]
499 mapinitializer STAR_BLOCK_START
    [ COMMA ]
    [ [ 497 ] ]
500 mapinitializer BLOCK_END
    [ RBRACE COMMA ]
    [ [ 503 ] ]
501 mapinitializer STAR_LOOP_ENTRY
    [ RBRACE COMMA ]
    [ [ 499 ]  [ 502 ] ]
502 mapinitializer LOOP_END
    [ RBRACE ]
    [ [ 504 ] ]
503 mapinitializer STAR_LOOP_BACK
    [ RBRACE COMMA ]
    [ [ 501 ] ]
504 mapinitializer BASIC
    [ RBRACE ]
    [ [ 505 : RBRACE ] ]
505 mapinitializer BASIC
    [ ]
    [ [ 510 ] ]
506 mapinitializer BASIC
    [ LBRACE ]
    [ [ 507 : LBRACE ] ]
507 mapinitializer BASIC
    [ COLON ]
    [ [ 508 : COLON ] ]
508 mapinitializer BASIC
    [ RBRACE ]
    [ [ 510 : RBRACE ] ]
509 mapinitializer BLOCK_START
    [ LBRACE ]
    [ [ 495 ]  [ 506 ] ]
510 mapinitializer BLOCK_END
    [ ]
    [ [ 65 ] ]
511 maptoken BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 34 ] ]
512 maptoken BASIC
    [ COLON ]
    [ [ 513 : COLON ] ]
513 maptoken BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 34 ] ]
514 maptoken BASIC
    [ ]
    [ [ 67 ] ]
515 arguments BASIC
    [ LP ]
    [ [ 524 : LP ] ]
516 arguments BASIC
    [ LBRACE LP NEW THIS BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL PRIMITIVE DEF ID ]
    [ [ 70 ] ]
517 arguments BASIC
    [ COMMA ]
    [ [ 518 : COMMA ] ]
518 arguments BASIC
    [ LBRACE LP NEW THIS BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL PRIMITIVE DEF ID ]
    [ [ 70 ] ]
519 arguments STAR_BLOCK_START
    [ COMMA ]
    [ [ 517 ] ]
520 arguments BLOCK_END
    [ RP COMMA ]
    [ [ 523 ] ]
521 arguments STAR_LOOP_ENTRY
    [ RP COMMA ]
    [ [ 519 ]  [ 522 ] ]
522 arguments LOOP_END
    [ RP ]
    [ [ 525 ] ]
523 arguments STAR_LOOP_BACK
    [ RP COMMA ]
    [ [ 521 ] ]
524 arguments BLOCK_START
    [ LBRACE LP RP NEW THIS BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL PRIMITIVE DEF ID ]
    [ [ 516 ]  [ 525 ] ]
525 arguments BLOCK_END
    [ RP ]
    [ [ 526 ] ]
526 arguments BASIC
    [ RP ]
    [ [ 527 : RP ] ]
527 arguments BASIC
    [ ]
    [ [ 69 ] ]
528 argument BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 34 ] ]
529 argument BASIC
    [ LP PRIMITIVE DEF ID ]
    [ [ 72 ] ]
530 argument BASIC
    [ THIS PRIMITIVE DEF ID ]
    [ [ 76 ] ]
531 argument BLOCK_START
    [ LBRACE LP NEW THIS BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL PRIMITIVE DEF ID ]
    [ [ 528 ]  [ 529 ]  [ 530 ] ]
532 argument BLOCK_END
    [ ]
    [ [ 71 ] ]
533 lambda BASIC
    [ PRIMITIVE DEF ID ]
    [ [ 74 ] ]
534 lambda BASIC
    [ LP ]
    [ [ 543 : LP ] ]
535 lambda BASIC
    [ PRIMITIVE DEF ID ]
    [ [ 74 ] ]
536 lambda BASIC
    [ COMMA ]
    [ [ 537 : COMMA ] ]
537 lambda BASIC
    [ PRIMITIVE DEF ID ]
    [ [ 74 ] ]
538 lambda STAR_BLOCK_START
    [ COMMA ]
    [ [ 536 ] ]
539 lambda BLOCK_END
    [ RP COMMA ]
    [ [ 542 ] ]
540 lambda STAR_LOOP_ENTRY
    [ RP COMMA ]
    [ [ 538 ]  [ 541 ] ]
541 lambda LOOP_END
    [ RP ]
    [ [ 544 ] ]
542 lambda STAR_LOOP_BACK
    [ RP COMMA ]
    [ [ 540 ] ]
543 lambda BLOCK_START
    [ RP PRIMITIVE DEF ID ]
    [ [ 535 ]  [ 544 ] ]
544 lambda BLOCK_END
    [ RP ]
    [ [ 545 ] ]
545 lambda BASIC
    [ RP ]
    [ [ 547 : RP ] ]
546 lambda BLOCK_START
    [ LP PRIMITIVE DEF ID ]
    [ [ 533 ]  [ 534 ] ]
547 lambda BLOCK_END
    [ ARROW ]
    [ [ 548 ] ]
548 lambda BASIC
    [ ARROW ]
    [ [ 551 : ARROW ] ]
549 lambda BASIC
    [ LBRACK ]
    [ [ 14 ] ]
550 lambda BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 34 ] ]
551 lambda BLOCK_START
    [ LBRACK LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 549 ]  [ 550 ] ]
552 lambda BLOCK_END
    [ ]
    [ [ 73 ] ]
553 lamtype BASIC
    [ PRIMITIVE DEF ID ]
    [ [ 24 ] ]
554 lamtype BLOCK_START
    [ PRIMITIVE DEF ID ]
    [ [ 553 ]  [ 555 ] ]
555 lamtype BLOCK_END
    [ ID ]
    [ [ 556 ] ]
556 lamtype BASIC
    [ ID ]
    [ [ 557 : ID ] ]
557 lamtype BASIC
    [ ]
    [ [ 75 ] ]
558 funcref BASIC
    [ PRIMITIVE DEF ID ]
    [ [ 24 ] ]
559 funcref BASIC
    [ REF ]
    [ [ 560 : REF ] ]
560 funcref BASIC
    [ ID ]
    [ [ 561 : ID ] ]
561 funcref BASIC
    [ ]
    [ [ 570 ] ]
562 funcref BASIC
    [ PRIMITIVE DEF ID ]
    [ [ 24 ] ]
563 funcref BASIC
    [ REF ]
    [ [ 564 : REF ] ]
564 funcref BASIC
    [ NEW ]
    [ [ 565 : NEW ] ]
565 funcref BASIC
    [ ]
    [ [ 570 ] ]
566 funcref BASIC
    [ THIS ]
    [ [ 567 : THIS ] ]
567 funcref BASIC
    [ REF ]
    [ [ 568 : REF ] ]
568 funcref BASIC
    [ ID ]
    [ [ 570 : ID ] ]
569 funcref BLOCK_START
    [ THIS PRIMITIVE DEF ID ]
    [ [ 558 ]  [ 562 ]  [ 566 ] ]
570 funcref BLOCK_END
    [ ]
    [ [ 77 ] ]
571 funcref BASIC
    [ ]
    []
*/