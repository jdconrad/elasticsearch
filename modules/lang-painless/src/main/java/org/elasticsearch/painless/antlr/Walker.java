/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */

package org.elasticsearch.painless.antlr;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DiagnosticErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.elasticsearch.painless.CompilerSettings;
import org.elasticsearch.painless.antlr.PainlessParser.SourceContext;
import org.elasticsearch.painless.lookup.PainlessLookup;
import org.elasticsearch.painless.node.SClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Converts the ANTLR tree to a Painless tree.
 */
public final class Walker {

    public static SClass buildPainlessTree(String sourceName, String sourceText, CompilerSettings settings, PainlessLookup painlessLookup) {
        return new Walker(sourceName, sourceText, settings, painlessLookup).source;
    }

    private final CompilerSettings settings;
    private final String sourceName;

    private final SClass source;

    private Walker(String sourceName, String sourceText, CompilerSettings settings, PainlessLookup painlessLookup) {
        this.source = null;
        this.settings = settings;
        this.sourceName = sourceName;

        buildAntlrTree(sourceText, painlessLookup);
    }

    private static class Tracker {

        private static class WalkState {

            private final List<? extends Token> tokens;

            private WalkState(List<? extends Token> tokens) {
                this.tokens = Collections.unmodifiableList(tokens);
            }

            private int current = 0;
        }

        private static class FunctionMachine {

            private static class FunctionData {
                private String returnType = "";
                private String functionName = "";
                private final List<String> parameterTypes = new ArrayList<>();
                private final List<String> parameterNames = new ArrayList<>();

                private int bodyStartToken = -1;
                private int bodyEndToken = -1;

                @Override
                public String toString() {
                    return "FunctionState{" +
                            "returnType='" + returnType + '\'' +
                            ", functionName='" + functionName + '\'' +
                            ", parameterTypes=" + parameterTypes +
                            ", parameterNames=" + parameterNames +
                            ", bodyStartToken=" + bodyStartToken +
                            ", bodyEndToken=" + bodyEndToken +
                            '}';
                }
            }

            private static class FunctionState {

                private final WalkState ws;

                private FunctionState(WalkState ws) {
                    this.ws = ws;
                }

                private int target = 0;

                private String returnType;
                private String functionName;
                private String parameterType;
                private FunctionData functionData;

                private final List<FunctionData> functions = new ArrayList<>();

                private int brackets;
            }

            private static final List<Function<FunctionState, Integer>> fstates;

            static {
                fstates = new ArrayList<>();
                // 0 - possible start of function
                fstates.add(fs -> {
                    Token token = fs.ws.tokens.get(fs.ws.current);
                    if (token.getType() == PainlessLexer.ATYPE || token.getType() == PainlessLexer.TYPE) {
                        // VALID: possible return type for function
                        fs.returnType = token.getText();
                        return 1;
                    }
                    // VALID: not a function
                    return 0;
                });
                // 1 - possible function name
                fstates.add(fs -> {
                    Token token = fs.ws.tokens.get(fs.ws.current);
                    if (token.getType() == PainlessLexer.ID) {
                        // VALID: possible function name
                        fs.functionName = token.getText();
                        return 2;
                    }
                    // VALID: not a function
                    return 0;
                });
                // 2 - start of parameters
                fstates.add(fs -> {
                    Token token = fs.ws.tokens.get(fs.ws.current);
                    if (token.getType() == PainlessLexer.LP) {
                        // VALID: found a function, record return type and function name
                        fs.functionData = new FunctionData();
                        fs.functionData.returnType = fs.returnType;
                        fs.functionData.functionName = fs.functionName;
                        fs.functions.add(fs.functionData);
                        return 3;
                    }
                    // VALID: not a function
                    return 0;
                });
                // 3 - start of a parameter or end of parameters
                fstates.add(fs -> {
                    Token token = fs.ws.tokens.get(fs.ws.current);
                    if (token.getType() == PainlessLexer.ATYPE || token.getType() == PainlessLexer.TYPE) {
                        // VALID: found a parameter type
                        fs.parameterType = token.getText();
                        return 6;
                    } else if (token.getType() == PainlessLexer.RP) {
                        // VALID: end of function header
                        return 4;
                    } else if (token.getType() == PainlessLexer.LBRACK) {
                        // ERROR (process): missing right parenthesis, but found start of function body
                        fs.brackets = 1;
                        fs.functionData.bodyStartToken = fs.ws.current + 1;
                        return 5;
                    }
                    // ERROR (ignore): unexpected token, keep looking for a sentinel
                    return 3;
                });
                // 4 - start of function body
                fstates.add(fs -> {
                    Token token = fs.ws.tokens.get(fs.ws.current);
                    if (token.getType() == PainlessLexer.LBRACK) {
                        // VALID: found start of function body
                        fs.brackets = 1;
                        fs.functionData.bodyStartToken = fs.ws.current + 1;
                        return 5;
                    }
                    // ERROR (ignore): unexpected token, keep looking for a sentinel
                    return 4;
                });
                // 5 - possible end of function body
                fstates.add(fs -> {
                    Token token = fs.ws.tokens.get(fs.ws.current);
                    if (token.getType() == PainlessLexer.LBRACK) {
                        // VALID: increase scope
                        ++fs.brackets;
                    } else if (token.getType() == PainlessLexer.RBRACK) {
                        // VALID: decrease scope
                        --fs.brackets;
                        if (fs.brackets == 0) {
                            // VALID: end of function body
                            fs.functionData.bodyEndToken = fs.ws.current - 1;
                            return 0;
                        }
                    }
                    // VALID: keep looking for end of function body
                    return 5;
                });
                // 6 - parameter name
                fstates.add(fs -> {
                    Token token = fs.ws.tokens.get(fs.ws.current);
                    if (token.getType() == PainlessLexer.ID) {
                        // VALID: found a parameter name, record parameter type and name
                        fs.functionData.parameterTypes.add(fs.parameterType);
                        fs.functionData.parameterNames.add(token.getText());
                        return 7;
                    } else if (token.getType() == PainlessLexer.RP) {
                        // ERROR (process): missing parameter name, but found end of function header
                        return 4;
                    } else if (token.getType() == PainlessLexer.LBRACK) {
                        // ERROR (process): missing parameter name, but found start of function body
                        return 5;
                    }
                    // ERROR (ignore): unexpected token, keep looking for a sentinel
                    return 6;
                });
                // 7 - start of another parameter or end of parameters
                fstates.add(fs -> {
                    Token token = fs.ws.tokens.get(fs.ws.current);
                    if (token.getType() == PainlessLexer.COMMA) {
                        // VALID: found comma, look for another parameter
                        return 8;
                    } else if (token.getType() == PainlessLexer.RP) {
                        // VALID: end of function header
                        return 4;
                    } else if (token.getType() == PainlessLexer.LBRACK) {
                        // ERROR (process): missing comma or right parenthesis, but found start of function body
                        return 5;
                    }
                    // ERROR (ignore): unexpected token, keep looking for a sentinel
                    return 7;
                });
                // 8 - start of another parameter
                fstates.add(fs -> {
                    Token token = fs.ws.tokens.get(fs.ws.current);
                    if (token.getType() == PainlessLexer.ATYPE || token.getType() == PainlessLexer.TYPE) {
                        // VALID: found a parameter type
                        fs.parameterType = token.getText();
                        return 6;
                    } else if (token.getType() == PainlessLexer.RP) {
                        // ERROR (process): missing parameter type, but found end of function header
                        return 4;
                    } else if (token.getType() == PainlessLexer.LBRACK) {
                        // ERROR (process): missing parameter type, but found start of function body
                        return 5;
                    }
                    // ERROR (ignore): unexpected token, keep looking for a sentinel
                    return 8;
                });
            }

            private static void walk(FunctionState fs) {
                WalkState ws = fs.ws;

                while (ws.current < ws.tokens.size()) {
                    Function<FunctionState, Integer> state = fstates.get(fs.target);
                    fs.target = state.apply(fs);
                    ++ws.current;
                }
            }

            private FunctionMachine() {

            }
        }

        private static class LambdaMachine {

            private static class LambdaData {
                private final List<String> parameterTypes = new ArrayList<>();
                private final List<String> parameterNames = new ArrayList<>();

                private int headerStartToken = -1;
                private int headerEndToken = -1;

                @Override
                public String toString() {
                    return "LambdaData{" +
                            "parameterTypes=" + parameterTypes +
                            ", parameterNames=" + parameterNames +
                            ", headerStartToken=" + headerStartToken +
                            ", headerEndToken=" + headerEndToken +
                            '}';
                }
            }

            private static class LambdaState {

                private final WalkState ws;

                private LambdaState(WalkState ws) {
                    this.ws = ws;
                }

                private int target = 0;

                private LambdaData lambdaData;

                private final List<LambdaMachine.LambdaData> lambdas = new ArrayList<>();
            }

            private static final List<Function<LambdaMachine.LambdaState, Integer>> lstates;

            static {
                lstates = new ArrayList<>();

                // 0
                lstates.add(ls -> {
                    Token token = ls.ws.tokens.get(ls.ws.current);
                    if (token.getType() == PainlessLexer.ARROW) {
                        ls.lambdaData = new LambdaData();
                        ls.lambdas.add(ls.lambdaData);
                        ls.lambdaData.headerStartToken = ls.ws.current;
                        ls.lambdaData.headerEndToken = ls.ws.current;
                        return 1;
                    }
                    return 0;
                });
                // 1
                lstates.add(ls -> {
                    Token token = ls.ws.tokens.get(ls.ws.current);
                    if (token.getType() == PainlessLexer.ID) {
                        ls.lambdaData.headerStartToken = ls.ws.current;
                        ls.lambdaData.parameterTypes.add("def");
                        ls.lambdaData.parameterNames.add(token.getText());
                    } else if (token.getType() == PainlessLexer.ARROW) {
                        ls.lambdaData = new LambdaData();
                        ls.lambdas.add(ls.lambdaData);
                        ls.lambdaData.headerStartToken = ls.ws.current;
                        ls.lambdaData.headerEndToken = ls.ws.current;
                        return 1;
                    } else if (token.getType() == PainlessLexer.RP) {
                        return 2;
                    }
                    return 0;
                });
                // 2
                lstates.add(ls -> {
                    Token token = ls.ws.tokens.get(ls.ws.current);
                    if (token.getType() == PainlessLexer.LP) {
                        ls.lambdaData.headerStartToken = ls.ws.current;
                        return 0;
                    } else if (token.getType() == PainlessLexer.ID) {
                        ls.lambdaData.headerStartToken = ls.ws.current;
                        ls.lambdaData.parameterTypes.add("def");
                        ls.lambdaData.parameterNames.add(token.getText());
                        return 3;
                    } else if (token.getType() == PainlessLexer.ARROW) {
                        ls.lambdaData = new LambdaData();
                        ls.lambdas.add(ls.lambdaData);
                        ls.lambdaData.headerStartToken = ls.ws.current;
                        ls.lambdaData.headerEndToken = ls.ws.current;
                        return 1;
                    }
                    return 0;
                });
                // 3
                lstates.add(ls -> {
                    Token token = ls.ws.tokens.get(ls.ws.current);
                    if (token.getType() == PainlessLexer.LP) {
                        ls.lambdaData.headerStartToken = ls.ws.current;
                        return 0;
                    } else if (token.getType() == PainlessLexer.ATYPE || token.getType() == PainlessLexer.TYPE) {
                        ls.lambdaData.headerStartToken = ls.ws.current;
                        ls.lambdaData.parameterTypes.set(ls.lambdaData.parameterTypes.size() - 1, token.getText());
                        return 4;
                    } else if (token.getType() == PainlessLexer.COMMA) {
                        return 2;
                    } else if (token.getType() == PainlessLexer.ARROW) {
                        ls.lambdaData = new LambdaData();
                        ls.lambdas.add(ls.lambdaData);
                        ls.lambdaData.headerStartToken = ls.ws.current;
                        ls.lambdaData.headerEndToken = ls.ws.current;
                        return 1;
                    }
                    return 0;
                });
                // 4
                lstates.add(ls -> {
                    Token token = ls.ws.tokens.get(ls.ws.current);
                    if (token.getType() == PainlessLexer.LP) {
                        ls.lambdaData.headerStartToken = ls.ws.current;
                        return 0;
                    } if (token.getType() == PainlessLexer.COMMA) {
                        return 2;
                    } else if (token.getType() == PainlessLexer.ARROW) {
                        ls.lambdaData = new LambdaData();
                        ls.lambdas.add(ls.lambdaData);
                        ls.lambdaData.headerStartToken = ls.ws.current;
                        ls.lambdaData.headerEndToken = ls.ws.current;
                        return 1;
                    }
                    return 0;
                });
            }

            private static void walk(LambdaMachine.LambdaState ls) {
                WalkState ws = ls.ws;
                ws.current = ws.tokens.size() - 1;

                while (ws.current >= 0) {
                    Function<LambdaMachine.LambdaState, Integer> state = lstates.get(ls.target);
                    ls.target = state.apply(ls);
                    --ws.current;
                }
            }

            private LambdaMachine() {

            }
        }

        private static class BlockMachine {

            private static class BlockState {

                private static class BlockScope {

                    private final BlockScope parent;

                    private int type;
                    private int sentinel;
                    private boolean pop = false;
                    private int parens = 0;
                    private int braces = 0;

                    private final Map<String, String> variables = new HashMap<>();
                    private int decltarget = 0;
                    private String decltype = null;
                    private int declparens = 0;
                    private int declbraces = 0;

                    private BlockScope(BlockScope parent, int type, int sentinel) {
                        this.parent = parent;
                        this.type = type;
                        this.sentinel = sentinel;
                    }

                    public String toString() {
                        StringBuilder builder = new StringBuilder();
                        builder.append("[");
                        builder.append(type == -1 ? "EOF" : PainlessLexer.ruleNames[type - 1]);
                        builder.append(" : ");
                        builder.append(sentinel == -1 ? "EOF" : PainlessLexer.ruleNames[sentinel - 1]);
                        builder.append(" : ");
                        builder.append(variables);
                        builder.append("]");

                        if (parent != null) {
                            builder.append(" ");
                            builder.append(parent.toString());
                        }

                        return builder.toString();
                    }
                }

                //private final PainlessLookup lookup;
                private final WalkState ws;
                private final Map<Integer, LambdaMachine.LambdaData> mld;

                private BlockScope scope = new BlockScope(null, PainlessLexer.EOF, PainlessLexer.EOF);

                private BlockState(/*PainlessLookup lookup, */WalkState ws, Map<Integer, LambdaMachine.LambdaData> mld) {
                    //this.lookup = Objects.requireNonNull(lookup);
                    this.ws = ws;
                    this.mld = mld;
                }
            }

            private static final List<Function<BlockState, Integer>> declstates;

            static {
                declstates = new ArrayList<>();

                // 0
                declstates.add(bs -> {
                    Token token = bs.ws.tokens.get(bs.ws.current);
                    if (token.getType() == PainlessLexer.ATYPE || token.getType() == PainlessLexer.TYPE) {
                        bs.scope.decltype = token.getText();
                        return 1;
                    } else if (token.getType() == PainlessLexer.IN) {
                        Token prev = bs.ws.current > 0 ? bs.ws.tokens.get(bs.ws.current - 1) : null;

                        if (prev != null && prev.getType() == PainlessLexer.ID) {
                            bs.scope.variables.put(prev.getText(), "def");
                        }
                    }
                    return 0;
                });
                // 1
                declstates.add(bs -> {
                    Token token = bs.ws.tokens.get(bs.ws.current);
                    if (token.getType() == PainlessLexer.ATYPE || token.getType() == PainlessLexer.TYPE) {
                        bs.scope.decltype = token.getText();
                        return 1;
                    } else if (token.getType() == PainlessLexer.ID) {
                        bs.scope.variables.put(token.getText(), bs.scope.decltype);
                        return 2;
                    }
                    return 0;
                });
                // 2
                declstates.add(bs -> {
                    Token token = bs.ws.tokens.get(bs.ws.current);
                    if (token.getType() == PainlessLexer.COMMA) {
                        return 1;
                    } else if (token.getType() == PainlessLexer.ASSIGN) {
                        return 3;
                    } else if (token.getType() == PainlessLexer.ATYPE || token.getType() == PainlessLexer.TYPE) {
                        bs.scope.decltype = token.getText();
                        return 1;
                    }
                    return 0;
                });
                // 3
                declstates.add(bs -> {
                    Token token = bs.ws.tokens.get(bs.ws.current);
                    if (token.getType() == PainlessLexer.COMMA && bs.scope.declparens == 0 && bs.scope.declbraces == 0) {
                        return 1;
                    } else if (token.getType() == PainlessLexer.LP) {
                        ++bs.scope.declparens;
                        return 3;
                    } else if (token.getType() == PainlessLexer.RP) {
                        --bs.scope.declparens;
                        return 3;
                    } else if (token.getType() == PainlessLexer.LBRACE) {
                        ++bs.scope.declbraces;
                        return 3;
                    } else if (token.getType() == PainlessLexer.RBRACE) {
                        --bs.scope.declbraces;
                    } else if (token.getType() == PainlessLexer.SEMICOLON) {
                        return 0;
                    }
                    return 3;
                });
            }

            private static void scope(BlockState bs, StringBuilder builder) {
                WalkState ws = bs.ws;

                int token = ws.tokens.get(ws.current).getType();
                int prev = ws.current > 0 ? ws.tokens.get(ws.current - 1).getType() : PainlessLexer.EOF;

                if (bs.scope.pop) {
                    if (token == PainlessLexer.CATCH && (bs.scope.type == PainlessLexer.TRY || bs.scope.type == PainlessLexer.CATCH)) {
                        bs.scope = bs.scope.parent;
                    } else if (token == PainlessLexer.ELSE) {
                        while (bs.scope.type != PainlessLexer.IF && bs.scope.sentinel == PainlessLexer.SEMICOLON) {
                            bs.scope = bs.scope.parent;
                        }

                        if (bs.scope.type == PainlessLexer.IF) {
                            bs.scope = bs.scope.parent;
                        }
                    } else {
                        bs.scope = bs.scope.parent;

                        while (bs.scope.sentinel == PainlessLexer.SEMICOLON) {
                            bs.scope = bs.scope.parent;
                        }
                    }
                }

                LambdaMachine.LambdaData ld = bs.mld.get(ws.current);

                if (ld != null) {
                    bs.scope = new BlockState.BlockScope(bs.scope, PainlessLexer.ARROW, PainlessLexer.EOF);

                    for (int param = 0; param < ld.parameterTypes.size(); ++param) {
                        bs.scope.variables.put(ld.parameterNames.get(param), ld.parameterTypes.get(param));
                    }

                    ws.current = ld.headerEndToken;
                    token = PainlessLexer.ARROW;
                } else if (token == PainlessLexer.WHILE || token == PainlessLexer.IF || token == PainlessLexer.ELSE) {
                    if (prev == PainlessLexer.ELSE && token == PainlessLexer.IF) {
                        bs.scope.type = PainlessLexer.IF;
                    } else {
                        bs.scope = new BlockState.BlockScope(bs.scope, token, PainlessLexer.SEMICOLON);
                    }
                } else if (token == PainlessLexer.FOR) {
                    bs.scope = new BlockState.BlockScope(bs.scope, token, PainlessLexer.RP);
                } else if (token == PainlessLexer.DO || token == PainlessLexer.TRY || token == PainlessLexer.CATCH) {
                    bs.scope = new BlockState.BlockScope(bs.scope, token, PainlessLexer.RBRACK);
                } else if (token == PainlessLexer.LBRACK) {
                    if (bs.scope.sentinel == PainlessLexer.SEMICOLON || bs.scope.sentinel == PainlessLexer.RP) {
                        bs.scope.sentinel = PainlessLexer.RBRACK;
                    }
                } else if (token == PainlessLexer.LP) {
                    ++bs.scope.parens;
                } else if (token == PainlessLexer.RP) {
                    bs.scope.parens = Math.max(0, bs.scope.parens - 1);

                    if (bs.scope.sentinel == PainlessLexer.RP && bs.scope.parens == 0) {
                        bs.scope.sentinel = PainlessLexer.SEMICOLON;
                    }
                } else if (token == PainlessLexer.LBRACE) {
                    ++bs.scope.braces;
                } else if (token == PainlessLexer.RBRACE) {
                    bs.scope.braces = Math.max(0, bs.scope.braces - 1);
                }

                if (bs.scope.type == PainlessLexer.ARROW) {
                    if (token == PainlessLexer.COMMA || token == PainlessLexer.RP && bs.scope.parens == 0 && bs.scope.braces == 0) {
                        bs.scope = bs.scope.parent;
                    }
                } else if (token == bs.scope.sentinel) {
                    if (bs.scope.type == PainlessLexer.DO) {
                        bs.scope.type = PainlessLexer.WHILE;
                        bs.scope.sentinel = PainlessLexer.SEMICOLON;
                    } else {
                        bs.scope.pop = true;
                    }
                }
            }

            private static void walk(BlockState bs, StringBuilder builder) {
                WalkState ws = bs.ws;

                // DEBUG
                String previous = "[EOF : EOF]";
                // END DEBUG

                while (ws.current < ws.tokens.size()) {
                    scope(bs, builder);

                    // DEBUG
                    String str = bs.scope.toString();
                    if (str.equals(previous) == false) {
                        int token = ws.tokens.get(ws.current).getType();
                        builder.append(token == -1 ? "EOF" : PainlessLexer.ruleNames[token - 1]);
                        builder.append(" : ");
                        builder.append(bs.scope);
                        builder.append("\n");
                        previous = str;
                    }
                    // END DEBUG

                    Function<BlockState, Integer> declstate = declstates.get(bs.scope.decltarget);
                    bs.scope.decltarget = declstate.apply(bs);
                    ++ws.current;
                }
            }

            private BlockMachine() {

            }
        }

        private static List<String> track(List<? extends Token> tokens) {
            return new Tracker(tokens).track();
        }

        private final List<? extends Token> tokens;

        private Tracker(List<? extends Token> tokens) {
            this.tokens = Collections.unmodifiableList(tokens);
        }

        private List<String> track() {
            //FunctionState fs = new FunctionState(tokens);
            //FunctionMachine.walk(fs);

            LambdaMachine.LambdaState ls = new LambdaMachine.LambdaState(new WalkState(tokens));
            LambdaMachine.walk(ls);

            Map<Integer, LambdaMachine.LambdaData> mld = new HashMap<>();

            for (LambdaMachine.LambdaData ld : ls.lambdas) {
                mld.put(ld.headerStartToken, ld);
            }

            StringBuilder builder = new StringBuilder();
            BlockMachine.BlockState bws = new BlockMachine.BlockState(new WalkState(tokens), mld);
            BlockMachine.walk(bws, builder);

            //for (FunctionMachine.FunctionState functionState = ws.functions) {

            //}

            if (true) throw new RuntimeException("\n\n" + builder);
            //if (true) throw new RuntimeException("\n\n" + builder.toString());
                    //tokens.stream().map(t -> PainlessLexer.ruleNames[t.getType()] + ":" + t.getText()).collect(Collectors.toList())
                    //        .toString()
            //);

            return null;
        }
    }

    private SourceContext buildAntlrTree(String source, PainlessLookup painlessLookup) {
        ANTLRInputStream stream = new ANTLRInputStream(source);
        PainlessLexer lexer = new EnhancedPainlessLexer(stream, painlessLookup);

//        if (true) throw new RuntimeException(
//            lexer.getAllTokens().stream().map(t -> PainlessLexer.ruleNames[t.getType() - 1] + ":" +
//                    t.getText()).collect(Collectors.toList()).toString()
//        );

//        ATN atn = PainlessParser._ATN;
//        StringBuilder builder = new StringBuilder("\n");
//        for (ATNState state : atn.states) {
//            builder.append(state.stateNumber);
//            builder.append(" ");
//            builder.append(PainlessParser.ruleNames[state.ruleIndex]);
//            builder.append(" ");
//            builder.append(ATNState.serializationNames.get(state.getStateType()));
//            builder.append("\n");
//            IntervalSet is = atn.nextTokens(state);
//            builder.append("    [");
//            for (int token : is.toList()) {
//                String symbolName = PainlessLexer.VOCABULARY.getSymbolicName(token);
//                if (symbolName != null) {
//                    builder.append(" ");
//                    builder.append(symbolName);
//                }
//            }
//            builder.append(" ]\n");
//            builder.append("    [");
//            for (Transition transition : state.getTransitions()) {
//                builder.append(" [ ");
//                builder.append(transition.target.toString());
//                if (transition.label() != null) {
//                    builder.append(" : ");
//                    for (int token : transition.label().toList()) {
//                        builder.append(PainlessLexer.VOCABULARY.getSymbolicName(token));
//                        builder.append(" ");
//                    }
//                    builder.append("]");
//                } else {
//                    builder.append(" ]");
//                }
//                builder.append(" ");
//            }
//            builder.append("]\n");
//        }
//
//        if (true) throw new IllegalStateException(builder.toString());

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
}

/*
0 source RULE_START
    [ EOF LBRACE LP IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT
      BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 71 ] ]
1 source RULE_STOP
    [ ]
    []
2 function RULE_START
    [ ATYPE TYPE ]
    [ [ 82 ] ]
3 function RULE_STOP
    [ ]
    [ [ 70 ] ]
4 parameters RULE_START
    [ LP ]
    [ [ 87 ] ]
5 parameters RULE_STOP
    [ ]
    [ [ 85 ] ]
6 statement RULE_START
    [ LBRACE LP IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL
      HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 107 ] ]
7 statement RULE_STOP
    [ ]
    [ [ 76 ]  [ 193 ]  [ 197 ] ]
8 rstatement RULE_START
    [ IF WHILE FOR TRY ]
    [ [ 169 ] ]
9 rstatement RULE_STOP
    [ ]
    [ [ 108 ] ]
10 dstatement RULE_START
    [ LBRACE LP DO CONTINUE BREAK RETURN NEW THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX
      INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 188 ] ]
11 dstatement RULE_STOP
    [ ]
    [ [ 105 ]  [ 203 ] ]
12 trailer RULE_START
    [ LBRACK LBRACE LP IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT
      ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 192 ] ]
13 trailer RULE_STOP
    [ ]
    [ [ 117 ]  [ 118 ]  [ 126 ]  [ 144 ]  [ 153 ]  [ 161 ] ]
14 block RULE_START
    [ LBRACK ]
    [ [ 194 ] ]
15 block RULE_STOP
    [ ]
    [ [ 86 ]  [ 165 ]  [ 173 ]  [ 193 ]  [ 236 ]  [ 485 ] ]
16 empty RULE_START
    [ SEMICOLON ]
    [ [ 206 ] ]
17 empty RULE_STOP
    [ ]
    [ [ 126 ]  [ 144 ] ]
18 initializer RULE_START
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 210 ] ]
19 initializer RULE_STOP
    [ ]
    [ [ 131 ] ]
20 afterthought RULE_START
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 212 ] ]
21 afterthought RULE_STOP
    [ ]
    [ [ 139 ] ]
22 declaration RULE_START
    [ ATYPE TYPE ]
    [ [ 214 ] ]
23 declaration RULE_STOP
    [ ]
    [ [ 189 ]  [ 211 ] ]
24 decltype RULE_START
    [ ATYPE TYPE ]
    [ [ 223 ] ]
25 decltype RULE_STOP
    [ ]
    [ [ 83 ]  [ 89 ]  [ 92 ]  [ 148 ]  [ 215 ]  [ 280 ]  [ 307 ]  [ 320 ]  [ 488 ]  [ 492 ]  [ 496 ] ]
26 declvar RULE_START
    [ ID ]
    [ [ 225 ] ]
27 declvar RULE_STOP
    [ ]
    [ [ 220 ]  [ 219 ] ]
28 trap RULE_START
    [ CATCH ]
    [ [ 230 ] ]
29 trap RULE_STOP
    [ ]
    [ [ 166 ] ]
30 noncondexpression RULE_START
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 237 ] ]
31 noncondexpression RULE_STOP
    [ ]
    [ [ 280 ]  [ 280 ]  [ 280 ]  [ 280 ]  [ 280 ]  [ 280 ]  [ 280 ]  [ 280 ]  [ 280 ]
      [ 280 ]  [ 280 ]  [ 280 ]  [ 296 ]  [ 286 ]  [ 292 ] ]
32 expression RULE_START
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 295 ] ]
33 expression RULE_STOP
    [ ]
    [ [ 112 ]  [ 122 ]  [ 135 ]  [ 151 ]  [ 159 ]  [ 176 ]  [ 184 ]  [ 189 ]  [ 189 ]  [ 211 ]  [ 213 ]  [ 229 ]
      [ 288 ]  [ 290 ]  [ 294 ]  [ 332 ]  [ 368 ]  [ 374 ]  [ 399 ]  [ 398 ]  [ 419 ]  [ 418 ]  [ 445 ]  [ 447 ]  [ 465 ]  [ 485 ] ]
34 unary RULE_START
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 310 ] ]
35 unary RULE_STOP
    [ ]
    [ [ 239 ]  [ 311 ]  [ 309 ] ]
36 chain RULE_START
    [ LBRACE LP NEW OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 328 ] ]
37 chain RULE_STOP
    [ ]
    [ [ 311 ]  [ 300 ]  [ 311 ] ]
38 primary RULE_START
    [ LBRACE LP NEW OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 348 ] ]
39 primary RULE_STOP
    [ ]
    [ [ 316 ] ]
40 postfix RULE_START
    [ LBRACE DOT NSDOT ]
    [ [ 353 ] ]
41 postfix RULE_STOP
    [ ]
    [ [ 315 ]  [ 323 ]  [ 383 ]  [ 407 ] ]
42 postdot RULE_START
    [ DOT NSDOT ]
    [ [ 357 ] ]
43 postdot RULE_STOP
    [ ]
    [ [ 324 ]  [ 384 ] ]
44 callinvoke RULE_START
    [ DOT NSDOT ]
    [ [ 359 ] ]
45 callinvoke RULE_STOP
    [ ]
    [ [ 354 ]  [ 358 ] ]
46 fieldaccess RULE_START
    [ DOT NSDOT ]
    [ [ 363 ] ]
47 fieldaccess RULE_STOP
    [ ]
    [ [ 354 ]  [ 358 ] ]
48 braceaccess RULE_START
    [ LBRACE ]
    [ [ 366 ] ]
49 braceaccess RULE_STOP
    [ ]
    [ [ 354 ] ]
50 arrayinitializer RULE_START
    [ NEW ]
    [ [ 411 ] ]
51 arrayinitializer RULE_STOP
    [ ]
    [ [ 329 ] ]
52 listinitializer RULE_START
    [ LBRACE ]
    [ [ 426 ] ]
53 listinitializer RULE_STOP
    [ ]
    [ [ 349 ] ]
54 mapinitializer RULE_START
    [ LBRACE ]
    [ [ 442 ] ]
55 mapinitializer RULE_STOP
    [ ]
    [ [ 349 ] ]
56 maptoken RULE_START
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 444 ] ]
57 maptoken RULE_STOP
    [ ]
    [ [ 434 ]  [ 433 ] ]
58 arguments RULE_START
    [ LP ]
    [ [ 448 ] ]
59 arguments RULE_STOP
    [ ]
    [ [ 349 ]  [ 349 ]  [ 362 ] ]
60 argument RULE_START
    [ LBRACE LP NEW THIS BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 464 ] ]
61 argument RULE_STOP
    [ ]
    [ [ 454 ]  [ 453 ] ]
62 lambda RULE_START
    [ LP ATYPE TYPE ID ]
    [ [ 479 ] ]
63 lambda RULE_STOP
    [ ]
    [ [ 465 ] ]
64 lamtype RULE_START
    [ ATYPE TYPE ID ]
    [ [ 487 ] ]
65 lamtype RULE_STOP
    [ ]
    [ [ 480 ]  [ 473 ]  [ 472 ] ]
66 funcref RULE_START
    [ THIS ATYPE TYPE ]
    [ [ 502 ] ]
67 funcref RULE_STOP
    [ ]
    [ [ 465 ] ]
68 source BASIC
    [ ATYPE TYPE ]
    [ [ 2 ] ]
69 source STAR_BLOCK_START
    [ ATYPE TYPE ]
    [ [ 68 ] ]
70 source BLOCK_END
    [ EOF LBRACE LP IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER
      DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 73 ] ]
71 source STAR_LOOP_ENTRY
    [ EOF LBRACE LP IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER
      DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 69 ]  [ 72 ] ]
72 source LOOP_END
    [ EOF LBRACE LP IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER
      DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 77 ] ]
73 source STAR_LOOP_BACK
    [ EOF LBRACE LP IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER
      DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 71 ] ]
74 source BASIC
    [ LBRACE LP IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER
      DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 6 ] ]
75 source STAR_BLOCK_START
    [ LBRACE LP IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER
      DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 74 ] ]
76 source BLOCK_END
    [ EOF LBRACE LP IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER
      DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 79 ] ]
77 source STAR_LOOP_ENTRY
    [ EOF LBRACE LP IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER
      DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 75 ]  [ 78 ] ]
78 source LOOP_END
    [ EOF ]
    [ [ 80 ] ]
79 source STAR_LOOP_BACK
    [ EOF LBRACE LP IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER
      DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 77 ] ]
80 source BASIC
    [ EOF ]
    [ [ 81 : EOF ] ]
81 source BASIC
    [ ]
    [ [ 1 ] ]
82 function BASIC
    [ ATYPE TYPE ]
    [ [ 24 ] ]
83 function BASIC
    [ ID ]
    [ [ 84 : ID ] ]
84 function BASIC
    [ LP ]
    [ [ 4 ] ]
85 function BASIC
    [ LBRACK ]
    [ [ 14 ] ]
86 function BASIC
    [ ]
    [ [ 3 ] ]
87 parameters BASIC
    [ LP ]
    [ [ 99 : LP ] ]
88 parameters BASIC
    [ ATYPE TYPE ]
    [ [ 24 ] ]
89 parameters BASIC
    [ ID ]
    [ [ 96 : ID ] ]
90 parameters BASIC
    [ COMMA ]
    [ [ 91 : COMMA ] ]
91 parameters BASIC
    [ ATYPE TYPE ]
    [ [ 24 ] ]
92 parameters BASIC
    [ ID ]
    [ [ 93 : ID ] ]
93 parameters BASIC
    [ RP COMMA ]
    [ [ 95 ] ]
94 parameters STAR_BLOCK_START
    [ COMMA ]
    [ [ 90 ] ]
95 parameters BLOCK_END
    [ RP COMMA ]
    [ [ 98 ] ]
96 parameters STAR_LOOP_ENTRY
    [ RP COMMA ]
    [ [ 94 ]  [ 97 ] ]
97 parameters LOOP_END
    [ RP ]
    [ [ 100 ] ]
98 parameters STAR_LOOP_BACK
    [ RP COMMA ]
    [ [ 96 ] ]
99 parameters BLOCK_START
    [ RP ATYPE TYPE ]
    [ [ 88 ]  [ 100 ] ]
100 parameters BLOCK_END
    [ RP ]
    [ [ 101 ] ]
101 parameters BASIC
    [ RP ]
    [ [ 102 : RP ] ]
102 parameters BASIC
    [ ]
    [ [ 5 ] ]
103 statement BASIC
    [ IF WHILE FOR TRY ]
    [ [ 8 ] ]
104 statement BASIC
    [ LBRACE LP DO CONTINUE BREAK RETURN NEW THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL
      STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 10 ] ]
105 statement BASIC
    [ EOF SEMICOLON ]
    [ [ 106 : EOF SEMICOLON ] ]
106 statement BASIC
    [ ]
    [ [ 108 ] ]
107 statement BLOCK_START
    [ LBRACE LP IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX
      INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 103 ]  [ 104 ] ]
108 statement BLOCK_END
    [ ]
    [ [ 7 ] ]
109 rstatement BASIC
    [ IF ]
    [ [ 110 : IF ] ]
110 rstatement BASIC
    [ LP ]
    [ [ 111 : LP ] ]
111 rstatement BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 32 ] ]
112 rstatement BASIC
    [ RP ]
    [ [ 113 : RP ] ]
113 rstatement BASIC
    [ LBRACK LBRACE LP IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX
      INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 12 ] ]
114 rstatement BASIC
    [ ELSE ]
    [ [ 115 : ELSE ] ]
115 rstatement BASIC
    [ LBRACK LBRACE LP IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX
      INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 12 ] ]
116 rstatement BASIC
    [ ]
    [ [ 118 ] ]
117 rstatement BLOCK_START
    [ ELSE ]
    [ [ 114 ]  [ 116 ] ]
118 rstatement BLOCK_END
    [ ]
    [ [ 170 ] ]
119 rstatement BASIC
    [ WHILE ]
    [ [ 120 : WHILE ] ]
120 rstatement BASIC
    [ LP ]
    [ [ 121 : LP ] ]
121 rstatement BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 32 ] ]
122 rstatement BASIC
    [ RP ]
    [ [ 125 : RP ] ]
123 rstatement BASIC
    [ LBRACK LBRACE LP IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER
      DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 12 ] ]
124 rstatement BASIC
    [ SEMICOLON ]
    [ [ 16 ] ]
125 rstatement BLOCK_START
    [ LBRACK LBRACE LP SEMICOLON IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL
      HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 123 ]  [ 124 ] ]
126 rstatement BLOCK_END
    [ ]
    [ [ 170 ] ]
127 rstatement BASIC
    [ FOR ]
    [ [ 128 : FOR ] ]
128 rstatement BASIC
    [ LP ]
    [ [ 130 : LP ] ]
129 rstatement BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 18 ] ]
130 rstatement BLOCK_START
    [ LBRACE LP SEMICOLON NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 129 ]  [ 131 ] ]
131 rstatement BLOCK_END
    [ SEMICOLON ]
    [ [ 132 ] ]
132 rstatement BASIC
    [ SEMICOLON ]
    [ [ 134 : SEMICOLON ] ]
133 rstatement BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 32 ] ]
134 rstatement BLOCK_START
    [ LBRACE LP SEMICOLON NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 133 ]  [ 135 ] ]
135 rstatement BLOCK_END
    [ SEMICOLON ]
    [ [ 136 ] ]
136 rstatement BASIC
    [ SEMICOLON ]
    [ [ 138 : SEMICOLON ] ]
137 rstatement BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 20 ] ]
138 rstatement BLOCK_START
    [ LBRACE LP RP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 137 ]  [ 139 ] ]
139 rstatement BLOCK_END
    [ RP ]
    [ [ 140 ] ]
140 rstatement BASIC
    [ RP ]
    [ [ 143 : RP ] ]
141 rstatement BASIC
    [ LBRACK LBRACE LP IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX
      INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 12 ] ]
142 rstatement BASIC
    [ SEMICOLON ]
    [ [ 16 ] ]
143 rstatement BLOCK_START
    [ LBRACK LBRACE LP SEMICOLON IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL
      HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 141 ]  [ 142 ] ]
144 rstatement BLOCK_END
    [ ]
    [ [ 170 ] ]
145 rstatement BASIC
    [ FOR ]
    [ [ 146 : FOR ] ]
146 rstatement BASIC
    [ LP ]
    [ [ 147 : LP ] ]
147 rstatement BASIC
    [ ATYPE TYPE ]
    [ [ 24 ] ]
148 rstatement BASIC
    [ ID ]
    [ [ 149 : ID ] ]
149 rstatement BASIC
    [ COLON ]
    [ [ 150 : COLON ] ]
150 rstatement BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 32 ] ]
151 rstatement BASIC
    [ RP ]
    [ [ 152 : RP ] ]
152 rstatement BASIC
    [ LBRACK LBRACE LP IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL
      HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 12 ] ]
153 rstatement BASIC
    [ ]
    [ [ 170 ] ]
154 rstatement BASIC
    [ FOR ]
    [ [ 155 : FOR ] ]
155 rstatement BASIC
    [ LP ]
    [ [ 156 : LP ] ]
156 rstatement BASIC
    [ ID ]
    [ [ 157 : ID ] ]
157 rstatement BASIC
    [ IN ]
    [ [ 158 : IN ] ]
158 rstatement BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 32 ] ]
159 rstatement BASIC
    [ RP ]
    [ [ 160 : RP ] ]
160 rstatement BASIC
    [ LBRACK LBRACE LP IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX
      INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 12 ] ]
161 rstatement BASIC
    [ ]
    [ [ 170 ] ]
162 rstatement BASIC
    [ TRY ]
    [ [ 163 : TRY ] ]
163 rstatement BASIC
    [ LBRACK ]
    [ [ 14 ] ]
164 rstatement BASIC
    [ CATCH ]
    [ [ 28 ] ]
165 rstatement PLUS_BLOCK_START
    [ CATCH ]
    [ [ 164 ] ]
166 rstatement BLOCK_END
    [ CATCH ]
    [ [ 167 ] ]
167 rstatement PLUS_LOOP_BACK
    [ CATCH ]
    [ [ 165 ]  [ 168 ] ]
168 rstatement LOOP_END
    [ ]
    [ [ 170 ] ]
169 rstatement BLOCK_START
    [ IF WHILE FOR TRY ]
    [ [ 109 ]  [ 119 ]  [ 127 ]  [ 145 ]  [ 154 ]  [ 162 ] ]
170 rstatement BLOCK_END
    [ ]
    [ [ 9 ] ]
171 dstatement BASIC
    [ DO ]
    [ [ 172 : DO ] ]
172 dstatement BASIC
    [ LBRACK ]
    [ [ 14 ] ]
173 dstatement BASIC
    [ WHILE ]
    [ [ 174 : WHILE ] ]
174 dstatement BASIC
    [ LP ]
    [ [ 175 : LP ] ]
175 dstatement BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 32 ] ]
176 dstatement BASIC
    [ RP ]
    [ [ 177 : RP ] ]
177 dstatement BASIC
    [ ]
    [ [ 189 ] ]
178 dstatement BASIC
    [ ATYPE TYPE ]
    [ [ 22 ] ]
179 dstatement BASIC
    [ CONTINUE ]
    [ [ 189 : CONTINUE ] ]
180 dstatement BASIC
    [ BREAK ]
    [ [ 189 : BREAK ] ]
181 dstatement BASIC
    [ RETURN ]
    [ [ 183 : RETURN ] ]
182 dstatement BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 32 ] ]
183 dstatement BLOCK_START
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 182 ]  [ 184 ] ]
184 dstatement BLOCK_END
    [ ]
    [ [ 189 ] ]
185 dstatement BASIC
    [ THROW ]
    [ [ 186 : THROW ] ]
186 dstatement BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 32 ] ]
187 dstatement BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 32 ] ]
188 dstatement BLOCK_START
    [ LBRACE LP DO CONTINUE BREAK RETURN NEW THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX
      TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 171 ]  [ 178 ]  [ 179 ]  [ 180 ]  [ 181 ]  [ 185 ]  [ 187 ] ]
189 dstatement BLOCK_END
    [ ]
    [ [ 11 ] ]
190 trailer BASIC
    [ LBRACK ]
    [ [ 14 ] ]
191 trailer BASIC
    [ LBRACE LP IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER
      DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 6 ] ]
192 trailer BLOCK_START
    [ LBRACK LBRACE LP IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX
      INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 190 ]  [ 191 ] ]
193 trailer BLOCK_END
    [ ]
    [ [ 13 ] ]
194 block BASIC
    [ LBRACK ]
    [ [ 198 : LBRACK ] ]
195 block BASIC
    [ LBRACE LP IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER
      DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 6 ] ]
196 block STAR_BLOCK_START
    [ LBRACE LP IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL
      STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 195 ] ]
197 block BLOCK_END
    [ RBRACK LBRACE LP IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX
      INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 200 ] ]
198 block STAR_LOOP_ENTRY
    [ RBRACK LBRACE LP IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX
      INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 196 ]  [ 199 ] ]
199 block LOOP_END
    [ RBRACK LBRACE LP DO CONTINUE BREAK RETURN NEW THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL
      STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 202 ] ]
200 block STAR_LOOP_BACK
    [ RBRACK LBRACE LP IF WHILE DO FOR CONTINUE BREAK RETURN NEW TRY THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX
      INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 198 ] ]
201 block BASIC
    [ LBRACE LP DO CONTINUE BREAK RETURN NEW THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX
      TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 10 ] ]
202 block BLOCK_START
    [ RBRACK LBRACE LP DO CONTINUE BREAK RETURN NEW THROW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL
      STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 201 ]  [ 203 ] ]
203 block BLOCK_END
    [ RBRACK ]
    [ [ 204 ] ]
204 block BASIC
    [ RBRACK ]
    [ [ 205 : RBRACK ] ]
205 block BASIC
    [ ]
    [ [ 15 ] ]
206 empty BASIC
    [ SEMICOLON ]
    [ [ 207 : SEMICOLON ] ]
207 empty BASIC
    [ ]
    [ [ 17 ] ]
208 initializer BASIC
    [ ATYPE TYPE ]
    [ [ 22 ] ]
209 initializer BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 32 ] ]
210 initializer BLOCK_START
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 208 ]  [ 209 ] ]
211 initializer BLOCK_END
    [ ]
    [ [ 19 ] ]
212 afterthought BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 32 ] ]
213 afterthought BASIC
    [ ]
    [ [ 21 ] ]
214 declaration BASIC
    [ ATYPE TYPE ]
    [ [ 24 ] ]
215 declaration BASIC
    [ ID ]
    [ [ 26 ] ]
216 declaration BASIC
    [ COMMA ]
    [ [ 217 : COMMA ] ]
217 declaration BASIC
    [ ID ]
    [ [ 26 ] ]
218 declaration STAR_BLOCK_START
    [ COMMA ]
    [ [ 216 ] ]
219 declaration BLOCK_END
    [ COMMA ]
    [ [ 222 ] ]
220 declaration STAR_LOOP_ENTRY
    [ COMMA ]
    [ [ 218 ]  [ 221 ] ]
221 declaration LOOP_END
    [ ]
    [ [ 23 ] ]
222 declaration STAR_LOOP_BACK
    [ COMMA ]
    [ [ 220 ] ]
223 decltype BASIC
    [ ATYPE TYPE ]
    [ [ 224 : ATYPE TYPE ] ]
224 decltype BASIC
    [ ]
    [ [ 25 ] ]
225 declvar BASIC
    [ ID ]
    [ [ 228 : ID ] ]
226 declvar BASIC
    [ ASSIGN ]
    [ [ 227 : ASSIGN ] ]
227 declvar BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 32 ] ]
228 declvar BLOCK_START
    [ ASSIGN ]
    [ [ 226 ]  [ 229 ] ]
229 declvar BLOCK_END
    [ ]
    [ [ 27 ] ]
230 trap BASIC
    [ CATCH ]
    [ [ 231 : CATCH ] ]
231 trap BASIC
    [ LP ]
    [ [ 232 : LP ] ]
232 trap BASIC
    [ TYPE ]
    [ [ 233 : TYPE ] ]
233 trap BASIC
    [ ID ]
    [ [ 234 : ID ] ]
234 trap BASIC
    [ RP ]
    [ [ 235 : RP ] ]
235 trap BASIC
    [ LBRACK ]
    [ [ 14 ] ]
236 trap BASIC
    [ ]
    [ [ 29 ] ]
237 noncondexpression BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 238 ] ]
238 noncondexpression BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 34 ] ]
239 noncondexpression BASIC
    [ INSTANCEOF MUL DIV REM ADD SUB LSH RSH USH LT LTE GT GTE EQ EQR NE NER BWAND XOR BWOR BOOLAND BOOLOR ELVIS FIND MATCH ]
    [ [ 281 ] ]
240 noncondexpression BASIC
    [ MUL DIV REM ]
    [ [ 241 ] ]
241 noncondexpression BASIC
    [ MUL DIV REM ]
    [ [ 242 : MUL DIV REM ] ]
242 noncondexpression BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 30 ] ]
243 noncondexpression BASIC
    [ ADD SUB ]
    [ [ 244 ] ]
244 noncondexpression BASIC
    [ ADD SUB ]
    [ [ 245 : ADD SUB ] ]
245 noncondexpression BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 30 ] ]
246 noncondexpression BASIC
    [ FIND MATCH ]
    [ [ 247 ] ]
247 noncondexpression BASIC
    [ FIND MATCH ]
    [ [ 248 : FIND MATCH ] ]
248 noncondexpression BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 30 ] ]
249 noncondexpression BASIC
    [ LSH RSH USH ]
    [ [ 250 ] ]
250 noncondexpression BASIC
    [ LSH RSH USH ]
    [ [ 251 : LSH RSH USH ] ]
251 noncondexpression BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 30 ] ]
252 noncondexpression BASIC
    [ LT LTE GT GTE ]
    [ [ 253 ] ]
253 noncondexpression BASIC
    [ LT LTE GT GTE ]
    [ [ 254 : LT LTE GT GTE ] ]
254 noncondexpression BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 30 ] ]
255 noncondexpression BASIC
    [ EQ EQR NE NER ]
    [ [ 256 ] ]
256 noncondexpression BASIC
    [ EQ EQR NE NER ]
    [ [ 257 : EQ EQR NE NER ] ]
257 noncondexpression BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 30 ] ]
258 noncondexpression BASIC
    [ BWAND ]
    [ [ 259 ] ]
259 noncondexpression BASIC
    [ BWAND ]
    [ [ 260 : BWAND ] ]
260 noncondexpression BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 30 ] ]
261 noncondexpression BASIC
    [ XOR ]
    [ [ 262 ] ]
262 noncondexpression BASIC
    [ XOR ]
    [ [ 263 : XOR ] ]
263 noncondexpression BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 30 ] ]
264 noncondexpression BASIC
    [ BWOR ]
    [ [ 265 ] ]
265 noncondexpression BASIC
    [ BWOR ]
    [ [ 266 : BWOR ] ]
266 noncondexpression BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 30 ] ]
267 noncondexpression BASIC
    [ BOOLAND ]
    [ [ 268 ] ]
268 noncondexpression BASIC
    [ BOOLAND ]
    [ [ 269 : BOOLAND ] ]
269 noncondexpression BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 30 ] ]
270 noncondexpression BASIC
    [ BOOLOR ]
    [ [ 271 ] ]
271 noncondexpression BASIC
    [ BOOLOR ]
    [ [ 272 : BOOLOR ] ]
272 noncondexpression BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 30 ] ]
273 noncondexpression BASIC
    [ ELVIS ]
    [ [ 274 ] ]
274 noncondexpression BASIC
    [ ELVIS ]
    [ [ 275 : ELVIS ] ]
275 noncondexpression BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 30 ] ]
276 noncondexpression BASIC
    [ INSTANCEOF ]
    [ [ 277 ] ]
277 noncondexpression BASIC
    [ INSTANCEOF ]
    [ [ 278 : INSTANCEOF ] ]
278 noncondexpression BASIC
    [ ATYPE TYPE ]
    [ [ 24 ] ]
279 noncondexpression STAR_BLOCK_START
    [ INSTANCEOF MUL DIV REM ADD SUB LSH RSH USH LT LTE GT GTE EQ EQR NE NER BWAND XOR BWOR BOOLAND BOOLOR ELVIS FIND MATCH ]
    [ [ 240 ]  [ 243 ]  [ 246 ]  [ 249 ]  [ 252 ]  [ 255 ]  [ 258 ]  [ 261 ]  [ 264 ]  [ 267 ]  [ 270 ]  [ 273 ]  [ 276 ] ]
280 noncondexpression BLOCK_END
    [ INSTANCEOF MUL DIV REM ADD SUB LSH RSH USH LT LTE GT GTE EQ EQR NE NER BWAND XOR BWOR BOOLAND BOOLOR ELVIS FIND MATCH ]
    [ [ 283 ] ]
281 noncondexpression STAR_LOOP_ENTRY
    [ INSTANCEOF MUL DIV REM ADD SUB LSH RSH USH LT LTE GT GTE EQ EQR NE NER BWAND XOR BWOR BOOLAND BOOLOR ELVIS FIND MATCH ]
    [ [ 279 ]  [ 282 ] ]
282 noncondexpression LOOP_END
    [ ]
    [ [ 31 ] ]
283 noncondexpression STAR_LOOP_BACK
    [ INSTANCEOF MUL DIV REM ADD SUB LSH RSH USH LT LTE GT GTE EQ EQR NE NER BWAND XOR BWOR BOOLAND BOOLOR ELVIS FIND MATCH ]
    [ [ 281 ] ]
284 expression BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 30 ] ]
285 expression BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 30 ] ]
286 expression BASIC
    [ COND ]
    [ [ 287 : COND ] ]
287 expression BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 32 ] ]
288 expression BASIC
    [ COLON ]
    [ [ 289 : COLON ] ]
289 expression BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 32 ] ]
290 expression BASIC
    [ ]
    [ [ 296 ] ]
291 expression BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 30 ] ]
292 expression BASIC
    [ ASSIGN AADD ASUB AMUL ADIV AREM AAND AXOR AOR ALSH ARSH AUSH ]
    [ [ 293 : ASSIGN AADD ASUB AMUL ADIV AREM AAND AXOR AOR ALSH ARSH AUSH ] ]
293 expression BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 32 ] ]
294 expression BASIC
    [ ]
    [ [ 296 ] ]
295 expression BLOCK_START
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 284 ]  [ 285 ]  [ 291 ] ]
296 expression BLOCK_END
    [ ]
    [ [ 33 ] ]
297 unary BASIC
    [ INCR DECR ]
    [ [ 298 : INCR DECR ] ]
298 unary BASIC
    [ LBRACE LP NEW OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 36 ] ]
299 unary BASIC
    [ LBRACE LP NEW OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 36 ] ]
300 unary BASIC
    [ INCR DECR ]
    [ [ 301 : INCR DECR ] ]
301 unary BASIC
    [ ]
    [ [ 311 ] ]
302 unary BASIC
    [ LBRACE LP NEW OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 36 ] ]
303 unary BASIC
    [ BOOLNOT BWNOT ADD SUB ]
    [ [ 304 : BOOLNOT BWNOT ADD SUB ] ]
304 unary BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 34 ] ]
305 unary BASIC
    [ LP ]
    [ [ 306 : LP ] ]
306 unary BASIC
    [ ATYPE TYPE ]
    [ [ 24 ] ]
307 unary BASIC
    [ RP ]
    [ [ 308 : RP ] ]
308 unary BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 34 ] ]
309 unary BASIC
    [ ]
    [ [ 311 ] ]
310 unary BLOCK_START
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 297 ]  [ 299 ]  [ 302 ]  [ 303 ]  [ 305 ] ]
311 unary BLOCK_END
    [ ]
    [ [ 35 ] ]
312 chain BASIC
    [ LBRACE LP NEW OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 38 ] ]
313 chain BASIC
    [ LBRACE DOT NSDOT ]
    [ [ 40 ] ]
314 chain STAR_BLOCK_START
    [ LBRACE DOT NSDOT ]
    [ [ 313 ] ]
315 chain BLOCK_END
    [ LBRACE DOT NSDOT ]
    [ [ 318 ] ]
316 chain STAR_LOOP_ENTRY
    [ LBRACE DOT NSDOT ]
    [ [ 314 ]  [ 317 ] ]
317 chain LOOP_END
    [ ]
    [ [ 329 ] ]
318 chain STAR_LOOP_BACK
    [ LBRACE DOT NSDOT ]
    [ [ 316 ] ]
319 chain BASIC
    [ ATYPE TYPE ]
    [ [ 24 ] ]
320 chain BASIC
    [ DOT NSDOT ]
    [ [ 42 ] ]
321 chain BASIC
    [ LBRACE DOT NSDOT ]
    [ [ 40 ] ]
322 chain STAR_BLOCK_START
    [ LBRACE DOT NSDOT ]
    [ [ 321 ] ]
323 chain BLOCK_END
    [ LBRACE DOT NSDOT ]
    [ [ 326 ] ]
324 chain STAR_LOOP_ENTRY
    [ LBRACE DOT NSDOT ]
    [ [ 322 ]  [ 325 ] ]
325 chain LOOP_END
    [ ]
    [ [ 329 ] ]
326 chain STAR_LOOP_BACK
    [ LBRACE DOT NSDOT ]
    [ [ 324 ] ]
327 chain BASIC
    [ NEW ]
    [ [ 50 ] ]
328 chain BLOCK_START
    [ LBRACE LP NEW OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 312 ]  [ 319 ]  [ 327 ] ]
329 chain BLOCK_END
    [ ]
    [ [ 37 ] ]
330 primary BASIC
    [ LP ]
    [ [ 331 : LP ] ]
331 primary BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 32 ] ]
332 primary BASIC
    [ RP ]
    [ [ 333 : RP ] ]
333 primary BASIC
    [ ]
    [ [ 349 ] ]
334 primary BASIC
    [ OCTAL HEX INTEGER DECIMAL ]
    [ [ 349 : OCTAL HEX INTEGER DECIMAL ] ]
335 primary BASIC
    [ TRUE ]
    [ [ 349 : TRUE ] ]
336 primary BASIC
    [ FALSE ]
    [ [ 349 : FALSE ] ]
337 primary BASIC
    [ NULL ]
    [ [ 349 : NULL ] ]
338 primary BASIC
    [ STRING ]
    [ [ 349 : STRING ] ]
339 primary BASIC
    [ REGEX ]
    [ [ 349 : REGEX ] ]
340 primary BASIC
    [ LBRACE ]
    [ [ 52 ] ]
341 primary BASIC
    [ LBRACE ]
    [ [ 54 ] ]
342 primary BASIC
    [ ID ]
    [ [ 349 : ID ] ]
343 primary BASIC
    [ ID ]
    [ [ 344 : ID ] ]
344 primary BASIC
    [ LP ]
    [ [ 58 ] ]
345 primary BASIC
    [ NEW ]
    [ [ 346 : NEW ] ]
346 primary BASIC
    [ TYPE ]
    [ [ 347 : TYPE ] ]
347 primary BASIC
    [ LP ]
    [ [ 58 ] ]
348 primary BLOCK_START
    [ LBRACE LP NEW OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ID ]
    [ [ 330 ]  [ 334 ]  [ 335 ]  [ 336 ]  [ 337 ]  [ 338 ]  [ 339 ]  [ 340 ]  [ 341 ]  [ 342 ]  [ 343 ]  [ 345 ] ]
349 primary BLOCK_END
    [ ]
    [ [ 39 ] ]
350 postfix BASIC
    [ DOT NSDOT ]
    [ [ 44 ] ]
351 postfix BASIC
    [ DOT NSDOT ]
    [ [ 46 ] ]
352 postfix BASIC
    [ LBRACE ]
    [ [ 48 ] ]
353 postfix BLOCK_START
    [ LBRACE DOT NSDOT ]
    [ [ 350 ]  [ 351 ]  [ 352 ] ]
354 postfix BLOCK_END
    [ ]
    [ [ 41 ] ]
355 postdot BASIC
    [ DOT NSDOT ]
    [ [ 44 ] ]
356 postdot BASIC
    [ DOT NSDOT ]
    [ [ 46 ] ]
357 postdot BLOCK_START
    [ DOT NSDOT ]
    [ [ 355 ]  [ 356 ] ]
358 postdot BLOCK_END
    [ ]
    [ [ 43 ] ]
359 callinvoke BASIC
    [ DOT NSDOT ]
    [ [ 360 : DOT NSDOT ] ]
360 callinvoke BASIC
    [ DOTID ]
    [ [ 361 : DOTID ] ]
361 callinvoke BASIC
    [ LP ]
    [ [ 58 ] ]
362 callinvoke BASIC
    [ ]
    [ [ 45 ] ]
363 fieldaccess BASIC
    [ DOT NSDOT ]
    [ [ 364 : DOT NSDOT ] ]
364 fieldaccess BASIC
    [ DOTINTEGER DOTID ]
    [ [ 365 : DOTINTEGER DOTID ] ]
365 fieldaccess BASIC
    [ ]
    [ [ 47 ] ]
366 braceaccess BASIC
    [ LBRACE ]
    [ [ 367 : LBRACE ] ]
367 braceaccess BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 32 ] ]
368 braceaccess BASIC
    [ RBRACE ]
    [ [ 369 : RBRACE ] ]
369 braceaccess BASIC
    [ ]
    [ [ 49 ] ]
370 arrayinitializer BASIC
    [ NEW ]
    [ [ 371 : NEW ] ]
371 arrayinitializer BASIC
    [ TYPE ]
    [ [ 376 : TYPE ] ]
372 arrayinitializer BASIC
    [ LBRACE ]
    [ [ 373 : LBRACE ] ]
373 arrayinitializer BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 32 ] ]
374 arrayinitializer BASIC
    [ RBRACE ]
    [ [ 375 : RBRACE ] ]
375 arrayinitializer BASIC
    [ LBRACE DOT NSDOT ]
    [ [ 377 ] ]
376 arrayinitializer PLUS_BLOCK_START
    [ LBRACE ]
    [ [ 372 ] ]
377 arrayinitializer BLOCK_END
    [ LBRACE DOT NSDOT ]
    [ [ 378 ] ]
378 arrayinitializer PLUS_LOOP_BACK
    [ LBRACE DOT NSDOT ]
    [ [ 376 ]  [ 379 ] ]
379 arrayinitializer LOOP_END
    [ DOT NSDOT ]
    [ [ 387 ] ]
380 arrayinitializer BASIC
    [ DOT NSDOT ]
    [ [ 42 ] ]
381 arrayinitializer BASIC
    [ LBRACE DOT NSDOT ]
    [ [ 40 ] ]
382 arrayinitializer STAR_BLOCK_START
    [ LBRACE DOT NSDOT ]
    [ [ 381 ] ]
383 arrayinitializer BLOCK_END
    [ LBRACE DOT NSDOT ]
    [ [ 386 ] ]
384 arrayinitializer STAR_LOOP_ENTRY
    [ LBRACE DOT NSDOT ]
    [ [ 382 ]  [ 385 ] ]
385 arrayinitializer LOOP_END
    [ ]
    [ [ 388 ] ]
386 arrayinitializer STAR_LOOP_BACK
    [ LBRACE DOT NSDOT ]
    [ [ 384 ] ]
387 arrayinitializer BLOCK_START
    [ DOT NSDOT ]
    [ [ 380 ]  [ 388 ] ]
388 arrayinitializer BLOCK_END
    [ ]
    [ [ 412 ] ]
389 arrayinitializer BASIC
    [ NEW ]
    [ [ 390 : NEW ] ]
390 arrayinitializer BASIC
    [ TYPE ]
    [ [ 391 : TYPE ] ]
391 arrayinitializer BASIC
    [ LBRACE ]
    [ [ 392 : LBRACE ] ]
392 arrayinitializer BASIC
    [ RBRACE ]
    [ [ 393 : RBRACE ] ]
393 arrayinitializer BASIC
    [ LBRACK ]
    [ [ 402 : LBRACK ] ]
394 arrayinitializer BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 32 ] ]
395 arrayinitializer BASIC
    [ COMMA ]
    [ [ 396 : COMMA ] ]
396 arrayinitializer BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 32 ] ]
397 arrayinitializer STAR_BLOCK_START
    [ COMMA ]
    [ [ 395 ] ]
398 arrayinitializer BLOCK_END
    [ RBRACK COMMA ]
    [ [ 401 ] ]
399 arrayinitializer STAR_LOOP_ENTRY
    [ RBRACK COMMA ]
    [ [ 397 ]  [ 400 ] ]
400 arrayinitializer LOOP_END
    [ RBRACK ]
    [ [ 403 ] ]
401 arrayinitializer STAR_LOOP_BACK
    [ RBRACK COMMA ]
    [ [ 399 ] ]
402 arrayinitializer BLOCK_START
    [ RBRACK LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 394 ]  [ 403 ] ]
403 arrayinitializer BLOCK_END
    [ RBRACK ]
    [ [ 404 ] ]
404 arrayinitializer BASIC
    [ RBRACK ]
    [ [ 408 : RBRACK ] ]
405 arrayinitializer BASIC
    [ LBRACE DOT NSDOT ]
    [ [ 40 ] ]
406 arrayinitializer STAR_BLOCK_START
    [ LBRACE DOT NSDOT ]
    [ [ 405 ] ]
407 arrayinitializer BLOCK_END
    [ LBRACE DOT NSDOT ]
    [ [ 410 ] ]
408 arrayinitializer STAR_LOOP_ENTRY
    [ LBRACE DOT NSDOT ]
    [ [ 406 ]  [ 409 ] ]
409 arrayinitializer LOOP_END
    [ ]
    [ [ 412 ] ]
410 arrayinitializer STAR_LOOP_BACK
    [ LBRACE DOT NSDOT ]
    [ [ 408 ] ]
411 arrayinitializer BLOCK_START
    [ NEW ]
    [ [ 370 ]  [ 389 ] ]
412 arrayinitializer BLOCK_END
    [ ]
    [ [ 51 ] ]
413 listinitializer BASIC
    [ LBRACE ]
    [ [ 414 : LBRACE ] ]
414 listinitializer BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 32 ] ]
415 listinitializer BASIC
    [ COMMA ]
    [ [ 416 : COMMA ] ]
416 listinitializer BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 32 ] ]
417 listinitializer STAR_BLOCK_START
    [ COMMA ]
    [ [ 415 ] ]
418 listinitializer BLOCK_END
    [ RBRACE COMMA ]
    [ [ 421 ] ]
419 listinitializer STAR_LOOP_ENTRY
    [ RBRACE COMMA ]
    [ [ 417 ]  [ 420 ] ]
420 listinitializer LOOP_END
    [ RBRACE ]
    [ [ 422 ] ]
421 listinitializer STAR_LOOP_BACK
    [ RBRACE COMMA ]
    [ [ 419 ] ]
422 listinitializer BASIC
    [ RBRACE ]
    [ [ 423 : RBRACE ] ]
423 listinitializer BASIC
    [ ]
    [ [ 427 ] ]
424 listinitializer BASIC
    [ LBRACE ]
    [ [ 425 : LBRACE ] ]
425 listinitializer BASIC
    [ RBRACE ]
    [ [ 427 : RBRACE ] ]
426 listinitializer BLOCK_START
    [ LBRACE ]
    [ [ 413 ]  [ 424 ] ]
427 listinitializer BLOCK_END
    [ ]
    [ [ 53 ] ]
428 mapinitializer BASIC
    [ LBRACE ]
    [ [ 429 : LBRACE ] ]
429 mapinitializer BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 56 ] ]
430 mapinitializer BASIC
    [ COMMA ]
    [ [ 431 : COMMA ] ]
431 mapinitializer BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 56 ] ]
432 mapinitializer STAR_BLOCK_START
    [ COMMA ]
    [ [ 430 ] ]
433 mapinitializer BLOCK_END
    [ RBRACE COMMA ]
    [ [ 436 ] ]
434 mapinitializer STAR_LOOP_ENTRY
    [ RBRACE COMMA ]
    [ [ 432 ]  [ 435 ] ]
435 mapinitializer LOOP_END
    [ RBRACE ]
    [ [ 437 ] ]
436 mapinitializer STAR_LOOP_BACK
    [ RBRACE COMMA ]
    [ [ 434 ] ]
437 mapinitializer BASIC
    [ RBRACE ]
    [ [ 438 : RBRACE ] ]
438 mapinitializer BASIC
    [ ]
    [ [ 443 ] ]
439 mapinitializer BASIC
    [ LBRACE ]
    [ [ 440 : LBRACE ] ]
440 mapinitializer BASIC
    [ COLON ]
    [ [ 441 : COLON ] ]
441 mapinitializer BASIC
    [ RBRACE ]
    [ [ 443 : RBRACE ] ]
442 mapinitializer BLOCK_START
    [ LBRACE ]
    [ [ 428 ]  [ 439 ] ]
443 mapinitializer BLOCK_END
    [ ]
    [ [ 55 ] ]
444 maptoken BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 32 ] ]
445 maptoken BASIC
    [ COLON ]
    [ [ 446 : COLON ] ]
446 maptoken BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 32 ] ]
447 maptoken BASIC
    [ ]
    [ [ 57 ] ]
448 arguments BASIC
    [ LP ]
    [ [ 457 : LP ] ]
449 arguments BASIC
    [ LBRACE LP NEW THIS BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 60 ] ]
450 arguments BASIC
    [ COMMA ]
    [ [ 451 : COMMA ] ]
451 arguments BASIC
    [ LBRACE LP NEW THIS BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 60 ] ]
452 arguments STAR_BLOCK_START
    [ COMMA ]
    [ [ 450 ] ]
453 arguments BLOCK_END
    [ RP COMMA ]
    [ [ 456 ] ]
454 arguments STAR_LOOP_ENTRY
    [ RP COMMA ]
    [ [ 452 ]  [ 455 ] ]
455 arguments LOOP_END
    [ RP ]
    [ [ 458 ] ]
456 arguments STAR_LOOP_BACK
    [ RP COMMA ]
    [ [ 454 ] ]
457 arguments BLOCK_START
    [ LBRACE LP RP NEW THIS BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 449 ]  [ 458 ] ]
458 arguments BLOCK_END
    [ RP ]
    [ [ 459 ] ]
459 arguments BASIC
    [ RP ]
    [ [ 460 : RP ] ]
460 arguments BASIC
    [ ]
    [ [ 59 ] ]
461 argument BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 32 ] ]
462 argument BASIC
    [ LP ATYPE TYPE ID ]
    [ [ 62 ] ]
463 argument BASIC
    [ THIS ATYPE TYPE ]
    [ [ 66 ] ]
464 argument BLOCK_START
    [ LBRACE LP NEW THIS BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 461 ]  [ 462 ]  [ 463 ] ]
465 argument BLOCK_END
    [ ]
    [ [ 61 ] ]
466 lambda BASIC
    [ ATYPE TYPE ID ]
    [ [ 64 ] ]
467 lambda BASIC
    [ LP ]
    [ [ 476 : LP ] ]
468 lambda BASIC
    [ ATYPE TYPE ID ]
    [ [ 64 ] ]
469 lambda BASIC
    [ COMMA ]
    [ [ 470 : COMMA ] ]
470 lambda BASIC
    [ ATYPE TYPE ID ]
    [ [ 64 ] ]
471 lambda STAR_BLOCK_START
    [ COMMA ]
    [ [ 469 ] ]
472 lambda BLOCK_END
    [ RP COMMA ]
    [ [ 475 ] ]
473 lambda STAR_LOOP_ENTRY
    [ RP COMMA ]
    [ [ 471 ]  [ 474 ] ]
474 lambda LOOP_END
    [ RP ]
    [ [ 477 ] ]
475 lambda STAR_LOOP_BACK
    [ RP COMMA ]
    [ [ 473 ] ]
476 lambda BLOCK_START
    [ RP ATYPE TYPE ID ]
    [ [ 468 ]  [ 477 ] ]
477 lambda BLOCK_END
    [ RP ]
    [ [ 478 ] ]
478 lambda BASIC
    [ RP ]
    [ [ 480 : RP ] ]
479 lambda BLOCK_START
    [ LP ATYPE TYPE ID ]
    [ [ 466 ]  [ 467 ] ]
480 lambda BLOCK_END
    [ ARROW ]
    [ [ 481 ] ]
481 lambda BASIC
    [ ARROW ]
    [ [ 484 : ARROW ] ]
482 lambda BASIC
    [ LBRACK ]
    [ [ 14 ] ]
483 lambda BASIC
    [ LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 32 ] ]
484 lambda BLOCK_START
    [ LBRACK LBRACE LP NEW BOOLNOT BWNOT ADD SUB INCR DECR OCTAL HEX INTEGER DECIMAL STRING REGEX TRUE FALSE NULL ATYPE TYPE ID ]
    [ [ 482 ]  [ 483 ] ]
485 lambda BLOCK_END
    [ ]
    [ [ 63 ] ]
486 lamtype BASIC
    [ ATYPE TYPE ]
    [ [ 24 ] ]
487 lamtype BLOCK_START
    [ ATYPE TYPE ID ]
    [ [ 486 ]  [ 488 ] ]
488 lamtype BLOCK_END
    [ ID ]
    [ [ 489 ] ]
489 lamtype BASIC
    [ ID ]
    [ [ 490 : ID ] ]
490 lamtype BASIC
    [ ]
    [ [ 65 ] ]
491 funcref BASIC
    [ ATYPE TYPE ]
    [ [ 24 ] ]
492 funcref BASIC
    [ REF ]
    [ [ 493 : REF ] ]
493 funcref BASIC
    [ ID ]
    [ [ 494 : ID ] ]
494 funcref BASIC
    [ ]
    [ [ 503 ] ]
495 funcref BASIC
    [ ATYPE TYPE ]
    [ [ 24 ] ]
496 funcref BASIC
    [ REF ]
    [ [ 497 : REF ] ]
497 funcref BASIC
    [ NEW ]
    [ [ 498 : NEW ] ]
498 funcref BASIC
    [ ]
    [ [ 503 ] ]
499 funcref BASIC
    [ THIS ]
    [ [ 500 : THIS ] ]
500 funcref BASIC
    [ REF ]
    [ [ 501 : REF ] ]
501 funcref BASIC
    [ ID ]
    [ [ 503 : ID ] ]
502 funcref BLOCK_START
    [ THIS ATYPE TYPE ]
    [ [ 491 ]  [ 495 ]  [ 499 ] ]
503 funcref BLOCK_END
    [ ]
    [ [ 67 ] ]
504 funcref BASIC
    [ ]
    []
*/