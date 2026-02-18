/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.javascript.antlr;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DiagnosticErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.dfa.DFA;
import org.elasticsearch.javascript.CompilerSettings;
import org.elasticsearch.javascript.Location;
import org.elasticsearch.javascript.Operation;
import org.elasticsearch.javascript.antlr.JavascriptParser.AdditiveExpressionContext;
import org.elasticsearch.javascript.antlr.JavascriptParser.ArgumentContext;
import org.elasticsearch.javascript.antlr.JavascriptParser.ArgumentsContext;
import org.elasticsearch.javascript.antlr.JavascriptParser.ArgumentsExpressionContext;
import org.elasticsearch.javascript.antlr.JavascriptParser.AssignmentExpressionContext;
import org.elasticsearch.javascript.antlr.JavascriptParser.AssignmentOperatorExpressionContext;
import org.elasticsearch.javascript.antlr.JavascriptParser.BitAndExpressionContext;
import org.elasticsearch.javascript.antlr.JavascriptParser.BitOrExpressionContext;
import org.elasticsearch.javascript.antlr.JavascriptParser.BitShiftExpressionContext;
import org.elasticsearch.javascript.antlr.JavascriptParser.BitXOrExpressionContext;
import org.elasticsearch.javascript.antlr.JavascriptParser.BlockContext;
import org.elasticsearch.javascript.antlr.JavascriptParser.BreakStatementContext;
import org.elasticsearch.javascript.antlr.JavascriptParser.CatchProductionContext;
import org.elasticsearch.javascript.antlr.JavascriptParser.ContinueStatementContext;
import org.elasticsearch.javascript.antlr.JavascriptParser.DoStatementContext;
import org.elasticsearch.javascript.antlr.JavascriptParser.EqualityExpressionContext;
import org.elasticsearch.javascript.antlr.JavascriptParser.ExpressionSequenceContext;
import org.elasticsearch.javascript.antlr.JavascriptParser.ExpressionStatementContext;
import org.elasticsearch.javascript.antlr.JavascriptParser.ForInStatementContext;
import org.elasticsearch.javascript.antlr.JavascriptParser.ForOfStatementContext;
import org.elasticsearch.javascript.antlr.JavascriptParser.ForStatementContext;
import org.elasticsearch.javascript.antlr.JavascriptParser.IfStatementContext;
import org.elasticsearch.javascript.antlr.JavascriptParser.InstanceofExpressionContext;
import org.elasticsearch.javascript.antlr.JavascriptParser.LiteralContext;
import org.elasticsearch.javascript.antlr.JavascriptParser.LiteralExpressionContext;
import org.elasticsearch.javascript.antlr.JavascriptParser.LogicalAndExpressionContext;
import org.elasticsearch.javascript.antlr.JavascriptParser.LogicalOrExpressionContext;
import org.elasticsearch.javascript.antlr.JavascriptParser.MemberDotExpressionContext;
import org.elasticsearch.javascript.antlr.JavascriptParser.MemberIndexExpressionContext;
import org.elasticsearch.javascript.antlr.JavascriptParser.MultiplicativeExpressionContext;
import org.elasticsearch.javascript.antlr.JavascriptParser.NewExpressionContext;
import org.elasticsearch.javascript.antlr.JavascriptParser.NotExpressionContext;
import org.elasticsearch.javascript.antlr.JavascriptParser.PostDecreaseExpressionContext;
import org.elasticsearch.javascript.antlr.JavascriptParser.PostIncrementExpressionContext;
import org.elasticsearch.javascript.antlr.JavascriptParser.PreDecreaseExpressionContext;
import org.elasticsearch.javascript.antlr.JavascriptParser.PreIncrementExpressionContext;
import org.elasticsearch.javascript.antlr.JavascriptParser.ProgramContext;
import org.elasticsearch.javascript.antlr.JavascriptParser.RelationalExpressionContext;
import org.elasticsearch.javascript.antlr.JavascriptParser.ReturnStatementContext;
import org.elasticsearch.javascript.antlr.JavascriptParser.SourceElementContext;
import org.elasticsearch.javascript.antlr.JavascriptParser.SourceElementsContext;
import org.elasticsearch.javascript.antlr.JavascriptParser.StatementContext;
import org.elasticsearch.javascript.antlr.JavascriptParser.StatementListContext;
import org.elasticsearch.javascript.antlr.JavascriptParser.TernaryExpressionContext;
import org.elasticsearch.javascript.antlr.JavascriptParser.ThrowStatementContext;
import org.elasticsearch.javascript.antlr.JavascriptParser.TryStatementContext;
import org.elasticsearch.javascript.antlr.JavascriptParser.UnaryMinusExpressionContext;
import org.elasticsearch.javascript.antlr.JavascriptParser.UnaryPlusExpressionContext;
import org.elasticsearch.javascript.antlr.JavascriptParser.VariableDeclarationContext;
import org.elasticsearch.javascript.antlr.JavascriptParser.VariableDeclarationListContext;
import org.elasticsearch.javascript.antlr.JavascriptParser.VariableStatementContext;
import org.elasticsearch.javascript.antlr.JavascriptParser.WhileStatementContext;
import org.elasticsearch.javascript.node.AExpression;
import org.elasticsearch.javascript.node.ANode;
import org.elasticsearch.javascript.node.AStatement;
import org.elasticsearch.javascript.node.EAssignment;
import org.elasticsearch.javascript.node.EBinary;
import org.elasticsearch.javascript.node.EBooleanComp;
import org.elasticsearch.javascript.node.EBooleanConstant;
import org.elasticsearch.javascript.node.EBrace;
import org.elasticsearch.javascript.node.ECall;
import org.elasticsearch.javascript.node.ECallLocal;
import org.elasticsearch.javascript.node.EComp;
import org.elasticsearch.javascript.node.EConditional;
import org.elasticsearch.javascript.node.EDecimal;
import org.elasticsearch.javascript.node.EDot;
import org.elasticsearch.javascript.node.EInstanceof;
import org.elasticsearch.javascript.node.EListInit;
import org.elasticsearch.javascript.node.ENewObj;
import org.elasticsearch.javascript.node.ENull;
import org.elasticsearch.javascript.node.ENumeric;
import org.elasticsearch.javascript.node.ERegex;
import org.elasticsearch.javascript.node.EString;
import org.elasticsearch.javascript.node.ESymbol;
import org.elasticsearch.javascript.node.EUnary;
import org.elasticsearch.javascript.node.SBlock;
import org.elasticsearch.javascript.node.SBreak;
import org.elasticsearch.javascript.node.SCatch;
import org.elasticsearch.javascript.node.SClass;
import org.elasticsearch.javascript.node.SContinue;
import org.elasticsearch.javascript.node.SDeclBlock;
import org.elasticsearch.javascript.node.SDeclaration;
import org.elasticsearch.javascript.node.SDo;
import org.elasticsearch.javascript.node.SEach;
import org.elasticsearch.javascript.node.SExpression;
import org.elasticsearch.javascript.node.SFor;
import org.elasticsearch.javascript.node.SFunction;
import org.elasticsearch.javascript.node.SIf;
import org.elasticsearch.javascript.node.SIfElse;
import org.elasticsearch.javascript.node.SReturn;
import org.elasticsearch.javascript.node.SThrow;
import org.elasticsearch.javascript.node.STry;
import org.elasticsearch.javascript.node.SWhile;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * Converts the JavaScript ANTLR parse tree (from {@link JavascriptParser#program()}) into the
 * JavaScript IR tree (rooted at {@link org.elasticsearch.javascript.node.SClass}).
 * Implements the same logical structure as the Painless Walker but using the JavaScript grammar
 * context types. Constructs with no JavaScript equivalent are left as TODO.
 */
public final class Walker extends JavascriptParserBaseVisitor<ANode> {

    public static SClass buildJavascriptTree(String sourceName, String sourceText, CompilerSettings settings) {
        return new Walker(sourceName, sourceText, settings).source;
    }

    private final CompilerSettings settings;
    private final String sourceName;
    private int identifier;
    private final SClass source;

    private Walker(String sourceName, String sourceText, CompilerSettings settings) {
        this.settings = settings;
        this.sourceName = sourceName;
        this.identifier = 0;
        this.source = (SClass) visit(buildAntlrTree(sourceText));
    }

    private int nextIdentifier() {
        return identifier++;
    }

    private ProgramContext buildAntlrTree(String sourceString) {
        ANTLRInputStream stream = new ANTLRInputStream(sourceString);
        JavascriptLexer lexer = new EnhancedJavascriptLexer(stream, sourceName);
        JavascriptParser parser = new JavascriptParser(new CommonTokenStream(lexer));
        ParserErrorStrategy strategy = new ParserErrorStrategy(sourceName);

        lexer.removeErrorListeners();
        parser.removeErrorListeners();

        if (settings.isPicky()) {
            setupPicky(parser);
        }

        parser.setErrorHandler(strategy);
        return parser.program();
    }

    private static void setupPicky(JavascriptParser parser) {
        // Suppress diagnostic reports for full-context/context-sensitivity so they are not treated as syntax errors.
        // The parser still uses full context to resolve; we only avoid failing the build on these diagnostics.
        parser.addErrorListener(new DiagnosticErrorListener(true) {
            @Override
            public void reportAttemptingFullContext(
                Parser recognizer,
                DFA dfa,
                int startIndex,
                int stopIndex,
                BitSet conflictingAlts,
                ATNConfigSet configs
            ) {}

            @Override
            public void reportContextSensitivity(
                Parser recognizer,
                DFA dfa,
                int startIndex,
                int stopIndex,
                int prediction,
                ATNConfigSet configs
            ) {}

            @Override
            public void reportAmbiguity(
                Parser recognizer,
                DFA dfa,
                int startIndex,
                int stopIndex,
                boolean exact,
                BitSet ambigAlts,
                ATNConfigSet configs
            ) {}
        });
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(
                Recognizer<?, ?> recognizer,
                Object offendingSymbol,
                int line,
                int charPositionInLine,
                String msg,
                RecognitionException e
            ) {
                throw new AssertionError(
                    "line: " + line + ", offset: " + charPositionInLine + ", symbol:" + offendingSymbol + " " + msg
                );
            }
        });
        parser.getInterpreter().setPredictionMode(PredictionMode.LL_EXACT_AMBIG_DETECTION);
    }

    private Location location(ParserRuleContext ctx) {
        return new Location(sourceName, ctx.getStart().getStartIndex());
    }

    /** Returns the first expression in a comma-separated expression sequence (e.g. condition, return value). */
    private AExpression firstExpression(ExpressionSequenceContext ctx) {
        if (ctx == null || ctx.singleExpression().isEmpty()) {
            return null;
        }
        return (AExpression) visit(ctx.singleExpression(0));
    }

    private List<AExpression> collectArguments(ArgumentsContext ctx) {
        if (ctx == null) {
            return List.of();
        }
        List<AExpression> args = new ArrayList<>();
        for (ArgumentContext arg : ctx.argument()) {
            if (arg.singleExpression() != null) {
                args.add((AExpression) visit(arg.singleExpression()));
            } else if (arg.identifier() != null) {
                args.add(new ESymbol(nextIdentifier(), location(arg), arg.identifier().getText()));
            }
        }
        return args;
    }

    // -------------------------------------------------------------------------
    // Program and top-level
    // -------------------------------------------------------------------------

    @Override
    public ANode visitProgram(ProgramContext ctx) {
        List<AStatement> statements = new ArrayList<>();
        SourceElementsContext sourceElements = ctx.sourceElements();
        if (sourceElements != null) {
            for (SourceElementContext el : sourceElements.sourceElement()) {
                ANode st = visit(el.statement());
                if (st != null) {
                    statements.add((AStatement) st);
                }
            }
        }
        SFunction execute = new SFunction(
            nextIdentifier(),
            location(ctx),
            "<internal>",
            "execute",
            List.of(),
            List.of(),
            new SBlock(nextIdentifier(), location(ctx), statements),
            false,
            false,
            false,
            false
        );
        return new SClass(nextIdentifier(), location(ctx), List.of(execute));
    }

    @Override
    public ANode visitSourceElement(SourceElementContext ctx) {
        return visit(ctx.statement());
    }

    @Override
    public ANode visitStatement(StatementContext ctx) {
        if (ctx.block() != null) return visit(ctx.block());
        if (ctx.variableStatement() != null) return visit(ctx.variableStatement());
        if (ctx.expressionStatement() != null) return visit(ctx.expressionStatement());
        if (ctx.ifStatement() != null) return visit(ctx.ifStatement());
        if (ctx.iterationStatement() != null) return visit(ctx.iterationStatement());
        if (ctx.returnStatement() != null) return visit(ctx.returnStatement());
        if (ctx.throwStatement() != null) return visit(ctx.throwStatement());
        if (ctx.tryStatement() != null) return visit(ctx.tryStatement());
        if (ctx.continueStatement() != null) return visit(ctx.continueStatement());
        if (ctx.breakStatement() != null) return visit(ctx.breakStatement());
        // TODO: importStatement, exportStatement, classDeclaration, functionDeclaration,
        // switchStatement, withStatement, labelledStatement, yieldStatement, debuggerStatement, emptyStatement_
        return visitChildren(ctx);
    }

    @Override
    public ANode visitBlock(BlockContext ctx) {
        StatementListContext list = ctx.statementList();
        if (list == null || list.statement().isEmpty()) {
            return new SBlock(nextIdentifier(), location(ctx), List.of());
        }
        List<AStatement> statements = new ArrayList<>();
        for (StatementContext s : list.statement()) {
            ANode n = visit(s);
            if (n != null) statements.add((AStatement) n);
        }
        return new SBlock(nextIdentifier(), location(ctx), statements);
    }

    @Override
    public ANode visitIfStatement(IfStatementContext ctx) {
        AExpression condition = firstExpression(ctx.expressionSequence());
        AStatement thenSt = (AStatement) visit(ctx.statement(0));
        SBlock ifBlock = thenSt instanceof SBlock ? (SBlock) thenSt : new SBlock(nextIdentifier(), location(ctx), List.of(thenSt));
        if (ctx.statement().size() > 1) {
            AStatement elseSt = (AStatement) visit(ctx.statement(1));
            SBlock elseBlock = elseSt instanceof SBlock ? (SBlock) elseSt : new SBlock(nextIdentifier(), location(ctx), List.of(elseSt));
            return new SIfElse(nextIdentifier(), location(ctx), condition, ifBlock, elseBlock);
        }
        return new SIf(nextIdentifier(), location(ctx), condition, ifBlock);
    }

    @Override
    public ANode visitDoStatement(DoStatementContext ctx) {
        AExpression condition = firstExpression(ctx.expressionSequence());
        SBlock block = (SBlock) visit(ctx.statement());
        return new SDo(nextIdentifier(), location(ctx), condition, block);
    }

    @Override
    public ANode visitWhileStatement(WhileStatementContext ctx) {
        AExpression condition = firstExpression(ctx.expressionSequence());
        SBlock block = (SBlock) visit(ctx.statement());
        return new SWhile(nextIdentifier(), location(ctx), condition, block);
    }

    @Override
    public ANode visitForStatement(ForStatementContext ctx) {
        List<ExpressionSequenceContext> seqs = ctx.expressionSequence();
        ANode initializer = null;
        AExpression condition = null;
        AExpression afterthought = null;
        if (ctx.variableDeclarationList() != null) {
            initializer = visit(ctx.variableDeclarationList());
            if (seqs != null && !seqs.isEmpty()) {
                condition = seqs.size() > 0 ? firstExpression(seqs.get(0)) : null;
                afterthought = seqs.size() > 1 ? firstExpression(seqs.get(1)) : null;
            }
        } else {
            if (seqs != null && !seqs.isEmpty()) {
                initializer = seqs.size() > 0 ? firstExpression(seqs.get(0)) : null;
                condition = seqs.size() > 1 ? firstExpression(seqs.get(1)) : null;
                afterthought = seqs.size() > 2 ? firstExpression(seqs.get(2)) : null;
            }
        }
        SBlock block = (SBlock) visit(ctx.statement());
        return new SFor(nextIdentifier(), location(ctx), initializer, condition, afterthought, block);
    }

    @Override
    public ANode visitForInStatement(ForInStatementContext ctx) {
        String name;
        if (ctx.singleVariableDeclaration() != null) {
            // singleVariableDeclaration has assignable; get identifier text
            if (ctx.singleVariableDeclaration().variableDeclaration().assignable().identifier() != null) {
                name = ctx.singleVariableDeclaration().variableDeclaration().assignable().identifier().getText();
            } else {
                name = ctx.singleVariableDeclaration().getText();
            }
        } else {
            // singleExpression as binding (e.g. bare identifier)
            AExpression binding = (AExpression) visit(ctx.singleExpression());
            name = binding instanceof ESymbol ? ((ESymbol) binding).getSymbol() : "item";
        }
        AExpression expression = firstExpression(ctx.expressionSequence());
        SBlock block = (SBlock) visit(ctx.statement());
        return new SEach(nextIdentifier(), location(ctx), "def", name, expression, block);
    }

    @Override
    public ANode visitForOfStatement(ForOfStatementContext ctx) {
        String name;
        if (ctx.singleVariableDeclaration() != null) {
            if (ctx.singleVariableDeclaration().variableDeclaration().assignable().identifier() != null) {
                name = ctx.singleVariableDeclaration().variableDeclaration().assignable().identifier().getText();
            } else {
                name = ctx.singleVariableDeclaration().getText();
            }
        } else {
            AExpression binding = (AExpression) visit(ctx.singleExpression());
            name = binding instanceof ESymbol ? ((ESymbol) binding).getSymbol() : "item";
        }
        AExpression expression = firstExpression(ctx.expressionSequence());
        SBlock block = (SBlock) visit(ctx.statement());
        return new SEach(nextIdentifier(), location(ctx), "def", name, expression, block);
    }

    @Override
    public ANode visitReturnStatement(ReturnStatementContext ctx) {
        AExpression expression = firstExpression(ctx.expressionSequence());
        return new SReturn(nextIdentifier(), location(ctx), expression);
    }

    @Override
    public ANode visitThrowStatement(ThrowStatementContext ctx) {
        AExpression expression = firstExpression(ctx.expressionSequence());
        return new SThrow(nextIdentifier(), location(ctx), expression);
    }

    @Override
    public ANode visitTryStatement(TryStatementContext ctx) {
        SBlock block = (SBlock) visit(ctx.block());
        List<SCatch> catches = new ArrayList<>();
        if (ctx.catchProduction() != null) {
            catches.add((SCatch) visit(ctx.catchProduction()));
        }
        // TODO: finallyProduction - no SFinally in current node set
        return new STry(nextIdentifier(), location(ctx), block, catches);
    }

    @Override
    public ANode visitCatchProduction(CatchProductionContext ctx) {
        String type = "Error";
        String name = "e";
        if (ctx.assignable() != null && ctx.assignable().identifier() != null) {
            name = ctx.assignable().identifier().getText();
        }
        SBlock block = (SBlock) visit(ctx.block());
        return new SCatch(nextIdentifier(), location(ctx), Exception.class, type, name, block);
    }

    @Override
    public ANode visitExpressionStatement(ExpressionStatementContext ctx) {
        AExpression expression = firstExpression(ctx.expressionSequence());
        return new SExpression(nextIdentifier(), location(ctx), expression);
    }

    @Override
    public ANode visitVariableStatement(VariableStatementContext ctx) {
        VariableDeclarationListContext list = ctx.variableDeclarationList();
        String modifier = list.varModifier().getText();
        List<SDeclaration> declarations = new ArrayList<>();
        for (VariableDeclarationContext vd : list.variableDeclaration()) {
            String name = vd.assignable().identifier() != null ? vd.assignable().identifier().getText() : vd.getText();
            AExpression init = vd.singleExpression() != null ? (AExpression) visit(vd.singleExpression()) : null;
            declarations.add(new SDeclaration(nextIdentifier(), location(vd), modifier, name, init));
        }
        return new SDeclBlock(nextIdentifier(), location(ctx), declarations);
    }

    @Override
    public ANode visitContinueStatement(ContinueStatementContext ctx) {
        return new SContinue(nextIdentifier(), location(ctx));
    }

    @Override
    public ANode visitBreakStatement(BreakStatementContext ctx) {
        return new SBreak(nextIdentifier(), location(ctx));
    }

    // -------------------------------------------------------------------------
    // Expressions: singleExpression alternatives
    // -------------------------------------------------------------------------

    @Override
    public ANode visitTernaryExpression(TernaryExpressionContext ctx) {
        AExpression condition = (AExpression) visit(ctx.singleExpression(0));
        AExpression left = (AExpression) visit(ctx.singleExpression(1));
        AExpression right = (AExpression) visit(ctx.singleExpression(2));
        return new EConditional(nextIdentifier(), location(ctx), condition, left, right);
    }

    @Override
    public ANode visitLogicalAndExpression(LogicalAndExpressionContext ctx) {
        AExpression left = (AExpression) visit(ctx.singleExpression(0));
        AExpression right = (AExpression) visit(ctx.singleExpression(1));
        return new EBooleanComp(nextIdentifier(), location(ctx), left, right, Operation.AND);
    }

    @Override
    public ANode visitLogicalOrExpression(LogicalOrExpressionContext ctx) {
        AExpression left = (AExpression) visit(ctx.singleExpression(0));
        AExpression right = (AExpression) visit(ctx.singleExpression(1));
        return new EBooleanComp(nextIdentifier(), location(ctx), left, right, Operation.OR);
    }

    @Override
    public ANode visitAssignmentExpression(AssignmentExpressionContext ctx) {
        AExpression lhs = (AExpression) visit(ctx.singleExpression(0));
        AExpression rhs = (AExpression) visit(ctx.singleExpression(1));
        return new EAssignment(nextIdentifier(), location(ctx), lhs, rhs, false, null);
    }

    @Override
    public ANode visitAssignmentOperatorExpression(AssignmentOperatorExpressionContext ctx) {
        AExpression lhs = (AExpression) visit(ctx.singleExpression(0));
        AExpression rhs = (AExpression) visit(ctx.singleExpression(1));
        String op = ctx.assignmentOperator().getText();
        Operation operation = mapAssignmentOperator(op);
        return new EAssignment(nextIdentifier(), location(ctx), lhs, rhs, false, operation);
    }

    private static Operation mapAssignmentOperator(String op) {
        return switch (op) {
            case "*=" -> Operation.MUL;
            case "/=" -> Operation.DIV;
            case "%=" -> Operation.REM;
            case "+=" -> Operation.ADD;
            case "-=" -> Operation.SUB;
            case "<<=" -> Operation.LSH;
            case ">>=" -> Operation.RSH;
            case ">>>=" -> Operation.USH;
            case "&=" -> Operation.BWAND;
            case "^=" -> Operation.XOR;
            case "|=" -> Operation.BWOR;
            default -> null;
        };
    }

    @Override
    public ANode visitAdditiveExpression(AdditiveExpressionContext ctx) {
        AExpression left = (AExpression) visit(ctx.singleExpression(0));
        AExpression right = (AExpression) visit(ctx.singleExpression(1));
        Operation op = ctx.Plus() != null ? Operation.ADD : Operation.SUB;
        return new EBinary(nextIdentifier(), location(ctx), left, right, op);
    }

    @Override
    public ANode visitMultiplicativeExpression(MultiplicativeExpressionContext ctx) {
        AExpression left = (AExpression) visit(ctx.singleExpression(0));
        AExpression right = (AExpression) visit(ctx.singleExpression(1));
        Operation op = ctx.Multiply() != null ? Operation.MUL
            : ctx.Divide() != null ? Operation.DIV
            : Operation.REM;
        return new EBinary(nextIdentifier(), location(ctx), left, right, op);
    }

    @Override
    public ANode visitEqualityExpression(EqualityExpressionContext ctx) {
        AExpression left = (AExpression) visit(ctx.singleExpression(0));
        AExpression right = (AExpression) visit(ctx.singleExpression(1));
        String text = ctx.getChild(1).getText();
        Operation op = "==".equals(text) ? Operation.EQ : "===".equals(text) ? Operation.EQR : "!=".equals(text) ? Operation.NE : Operation.NER;
        return new EComp(nextIdentifier(), location(ctx), left, right, op);
    }

    @Override
    public ANode visitRelationalExpression(RelationalExpressionContext ctx) {
        AExpression left = (AExpression) visit(ctx.singleExpression(0));
        AExpression right = (AExpression) visit(ctx.singleExpression(1));
        String text = ctx.getChild(1).getText();
        Operation op = "<".equals(text) ? Operation.LT : "<=".equals(text) ? Operation.LTE : ">".equals(text) ? Operation.GT : Operation.GTE;
        return new EComp(nextIdentifier(), location(ctx), left, right, op);
    }

    @Override
    public ANode visitBitShiftExpression(BitShiftExpressionContext ctx) {
        AExpression left = (AExpression) visit(ctx.singleExpression(0));
        AExpression right = (AExpression) visit(ctx.singleExpression(1));
        String text = ctx.getChild(1).getText();
        Operation op = "<<".equals(text) ? Operation.LSH : ">>>".equals(text) ? Operation.USH : Operation.RSH;
        return new EBinary(nextIdentifier(), location(ctx), left, right, op);
    }

    @Override
    public ANode visitBitAndExpression(BitAndExpressionContext ctx) {
        AExpression left = (AExpression) visit(ctx.singleExpression(0));
        AExpression right = (AExpression) visit(ctx.singleExpression(1));
        return new EBinary(nextIdentifier(), location(ctx), left, right, Operation.BWAND);
    }

    @Override
    public ANode visitBitXOrExpression(BitXOrExpressionContext ctx) {
        AExpression left = (AExpression) visit(ctx.singleExpression(0));
        AExpression right = (AExpression) visit(ctx.singleExpression(1));
        return new EBinary(nextIdentifier(), location(ctx), left, right, Operation.XOR);
    }

    @Override
    public ANode visitBitOrExpression(BitOrExpressionContext ctx) {
        AExpression left = (AExpression) visit(ctx.singleExpression(0));
        AExpression right = (AExpression) visit(ctx.singleExpression(1));
        return new EBinary(nextIdentifier(), location(ctx), left, right, Operation.BWOR);
    }

    @Override
    public ANode visitArgumentsExpression(ArgumentsExpressionContext ctx) {
        AExpression callee = (AExpression) visit(ctx.singleExpression());
        List<AExpression> args = collectArguments(ctx.arguments());
        if (callee instanceof ESymbol) {
            return new ECallLocal(nextIdentifier(), location(ctx), ((ESymbol) callee).getSymbol(), args);
        }
        if (callee instanceof EDot) {
            return new ECall(
                nextIdentifier(),
                location(ctx),
                ((EDot) callee).getPrefixNode(),
                ((EDot) callee).getIndex(),
                args,
                ((EDot) callee).isNullSafe()
            );
        }
        return new ECall(nextIdentifier(), location(ctx), callee, "", args, false);
    }

    @Override
    public ANode visitMemberDotExpression(MemberDotExpressionContext ctx) {
        AExpression prefix = (AExpression) visit(ctx.singleExpression());
        String name = ctx.identifierName().getText();
        boolean nullSafe = ctx.QuestionMark() != null;
        return new EDot(nextIdentifier(), location(ctx), prefix, name, nullSafe);
    }

    @Override
    public ANode visitMemberIndexExpression(MemberIndexExpressionContext ctx) {
        AExpression prefix = (AExpression) visit(ctx.singleExpression());
        AExpression index = firstExpression(ctx.expressionSequence());
        return new EBrace(nextIdentifier(), location(ctx), prefix, index);
    }

    @Override
    public ANode visitNewExpression(NewExpressionContext ctx) {
        String type = ctx.identifier() != null ? ctx.identifier().getText() : ctx.singleExpression().getText();
        List<AExpression> args = ctx.arguments() != null ? collectArguments(ctx.arguments()) : List.of();
        return new ENewObj(nextIdentifier(), location(ctx), type, args);
    }

    @Override
    public ANode visitLiteralExpression(LiteralExpressionContext ctx) {
        return visit(ctx.literal());
    }

    @Override
    public ANode visitLiteral(LiteralContext ctx) {
        if (ctx.NullLiteral() != null) return new ENull(nextIdentifier(), location(ctx));
        if (ctx.BooleanLiteral() != null) {
            boolean value = "true".equals(ctx.BooleanLiteral().getText());
            return new EBooleanConstant(nextIdentifier(), location(ctx), value);
        }
        if (ctx.StringLiteral() != null) {
            String raw = ctx.StringLiteral().getText();
            String unescaped = unescapeString(raw.substring(1, raw.length() - 1), raw.charAt(0));
            return new EString(nextIdentifier(), location(ctx), unescaped);
        }
        if (ctx.RegularExpressionLiteral() != null) {
            String text = ctx.RegularExpressionLiteral().getText();
            int lastSlash = text.lastIndexOf('/');
            return new ERegex(nextIdentifier(), location(ctx), text.substring(1, lastSlash), text.substring(lastSlash + 1));
        }
        if (ctx.numericLiteral() != null) {
            String text = ctx.numericLiteral().getText();
            if (text.contains(".") || text.toLowerCase().contains("e")) {
                return new EDecimal(nextIdentifier(), location(ctx), text);
            }
            int radix = 10;
            if (text.startsWith("0x") || text.startsWith("0X")) { text = text.substring(2); radix = 16; }
            else if (text.startsWith("0o") || text.startsWith("0O")) { text = text.substring(2); radix = 8; }
            else if (text.startsWith("0b") || text.startsWith("0B")) { text = text.substring(2); radix = 2; }
            return new ENumeric(nextIdentifier(), location(ctx), text.replaceAll("[lL]$", ""), radix);
        }
        // TODO: templateStringLiteral, bigintLiteral
        return visitChildren(ctx);
    }

    private static String unescapeString(String s, char quote) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < s.length()) {
                char next = s.charAt(++i);
                sb.append(switch (next) {
                    case 'n' -> '\n';
                    case 'r' -> '\r';
                    case 't' -> '\t';
                    case '\\' -> '\\';
                    case '\'', '"' -> next;
                    default -> next;
                });
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    @Override
    public ANode visitInstanceofExpression(InstanceofExpressionContext ctx) {
        AExpression left = (AExpression) visit(ctx.singleExpression(0));
        String type = ctx.singleExpression(1).getText();
        return new EInstanceof(nextIdentifier(), location(ctx), left, type);
    }

    @Override
    public ANode visitNotExpression(NotExpressionContext ctx) {
        AExpression expression = (AExpression) visit(ctx.singleExpression());
        return new EUnary(nextIdentifier(), location(ctx), expression, Operation.NOT);
    }

    @Override
    public ANode visitPreIncrementExpression(PreIncrementExpressionContext ctx) {
        AExpression expression = (AExpression) visit(ctx.singleExpression());
        return new EAssignment(
            nextIdentifier(),
            location(ctx),
            expression,
            new ENumeric(nextIdentifier(), location(ctx), "1", 10),
            false,
            Operation.ADD
        );
    }

    @Override
    public ANode visitPreDecreaseExpression(PreDecreaseExpressionContext ctx) {
        AExpression expression = (AExpression) visit(ctx.singleExpression());
        return new EAssignment(
            nextIdentifier(),
            location(ctx),
            expression,
            new ENumeric(nextIdentifier(), location(ctx), "1", 10),
            false,
            Operation.SUB
        );
    }

    @Override
    public ANode visitPostIncrementExpression(PostIncrementExpressionContext ctx) {
        AExpression expression = (AExpression) visit(ctx.singleExpression());
        return new EAssignment(
            nextIdentifier(),
            location(ctx),
            expression,
            new ENumeric(nextIdentifier(), location(ctx), "1", 10),
            true,
            Operation.ADD
        );
    }

    @Override
    public ANode visitPostDecreaseExpression(PostDecreaseExpressionContext ctx) {
        AExpression expression = (AExpression) visit(ctx.singleExpression());
        return new EAssignment(
            nextIdentifier(),
            location(ctx),
            expression,
            new ENumeric(nextIdentifier(), location(ctx), "1", 10),
            true,
            Operation.SUB
        );
    }

    @Override
    public ANode visitUnaryPlusExpression(UnaryPlusExpressionContext ctx) {
        AExpression expression = (AExpression) visit(ctx.singleExpression());
        return new EUnary(nextIdentifier(), location(ctx), expression, Operation.ADD);
    }

    @Override
    public ANode visitUnaryMinusExpression(UnaryMinusExpressionContext ctx) {
        AExpression expression = (AExpression) visit(ctx.singleExpression());
        return new EUnary(nextIdentifier(), location(ctx), expression, Operation.SUB);
    }

    @Override
    public ANode visitParenthesizedExpression(org.elasticsearch.javascript.antlr.JavascriptParser.ParenthesizedExpressionContext ctx) {
        return visit(ctx.expressionSequence());
    }

    @Override
    public ANode visitExpressionSequence(ExpressionSequenceContext ctx) {
        return ctx.singleExpression().isEmpty() ? null : visit(ctx.singleExpression(0));
    }

    @Override
    public ANode visitIdentifierExpression(org.elasticsearch.javascript.antlr.JavascriptParser.IdentifierExpressionContext ctx) {
        String name = ctx.identifier().getText();
        return new ESymbol(nextIdentifier(), location(ctx), name);
    }

    @Override
    public ANode visitArrayLiteralExpression(org.elasticsearch.javascript.antlr.JavascriptParser.ArrayLiteralExpressionContext ctx) {
        org.elasticsearch.javascript.antlr.JavascriptParser.ArrayLiteralContext al = ctx.arrayLiteral();
        if (al.elementList() == null || al.elementList().arrayElement() == null) {
            return new EListInit(nextIdentifier(), location(ctx), List.of());
        }
        List<AExpression> values = new ArrayList<>();
        for (org.elasticsearch.javascript.antlr.JavascriptParser.ArrayElementContext ae : al.elementList().arrayElement()) {
            if (ae.singleExpression() != null) {
                values.add((AExpression) visit(ae.singleExpression()));
            }
        }
        return new EListInit(nextIdentifier(), location(ctx), values);
    }

    // TODO: PowerExpression, CoalesceExpression, OptionalChainExpression, TemplateStringExpression,
    // ThisExpression, SuperExpression, ClassExpression, FunctionExpression, ObjectLiteralExpression,
    // MetaExpression, YieldExpression, AwaitExpression, DeleteExpression, TypeofExpression,
    // VoidExpression, ImportExpression, BitNotExpression, NamedFunction, AnonymousFunctionDecl, ArrowFunction
}
