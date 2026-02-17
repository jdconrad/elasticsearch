// ANTLR GENERATED CODE: DO NOT EDIT
package org.elasticsearch.javascript.antlr;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
class JavascriptParser extends Parser {
  static { RuntimeMetaData.checkVersion("4.13.1", RuntimeMetaData.VERSION); }

  protected static final DFA[] _decisionToDFA;
  protected static final PredictionContextCache _sharedContextCache =
    new PredictionContextCache();
  public static final int
    WS=1, COMMENT=2, LBRACK=3, RBRACK=4, LBRACE=5, RBRACE=6, LP=7, RP=8, DOLLAR=9, 
    DOT=10, NSDOT=11, COMMA=12, SEMICOLON=13, IF=14, IN=15, ELSE=16, WHILE=17, 
    DO=18, FOR=19, CONTINUE=20, BREAK=21, RETURN=22, NEW=23, TRY=24, CATCH=25, 
    THROW=26, THIS=27, INSTANCEOF=28, BOOLNOT=29, BWNOT=30, MUL=31, DIV=32, 
    REM=33, ADD=34, SUB=35, LSH=36, RSH=37, USH=38, LT=39, LTE=40, GT=41, 
    GTE=42, EQ=43, EQR=44, NE=45, NER=46, BWAND=47, XOR=48, BWOR=49, BOOLAND=50, 
    BOOLOR=51, COND=52, COLON=53, ELVIS=54, REF=55, ARROW=56, FIND=57, MATCH=58, 
    INCR=59, DECR=60, ASSIGN=61, AADD=62, ASUB=63, AMUL=64, ADIV=65, AREM=66, 
    AAND=67, AXOR=68, AOR=69, ALSH=70, ARSH=71, AUSH=72, OCTAL=73, HEX=74, 
    INTEGER=75, DECIMAL=76, STRING=77, REGEX=78, TRUE=79, FALSE=80, NULL=81, 
    PRIMITIVE=82, DEF=83, ID=84, DOTINTEGER=85, DOTID=86;
  public static final int
    RULE_source = 0, RULE_function = 1, RULE_parameters = 2, RULE_statement = 3, 
    RULE_rstatement = 4, RULE_dstatement = 5, RULE_trailer = 6, RULE_block = 7, 
    RULE_empty = 8, RULE_initializer = 9, RULE_afterthought = 10, RULE_declaration = 11, 
    RULE_decltype = 12, RULE_type = 13, RULE_declvar = 14, RULE_trap = 15, 
    RULE_noncondexpression = 16, RULE_expression = 17, RULE_unary = 18, RULE_unarynotaddsub = 19, 
    RULE_castexpression = 20, RULE_primordefcasttype = 21, RULE_refcasttype = 22, 
    RULE_chain = 23, RULE_primary = 24, RULE_postfix = 25, RULE_postdot = 26, 
    RULE_callinvoke = 27, RULE_fieldaccess = 28, RULE_braceaccess = 29, RULE_arrayinitializer = 30, 
    RULE_listinitializer = 31, RULE_mapinitializer = 32, RULE_maptoken = 33, 
    RULE_arguments = 34, RULE_argument = 35, RULE_lambda = 36, RULE_lamtype = 37, 
    RULE_funcref = 38;
  private static String[] makeRuleNames() {
    return new String[] {
      "source", "function", "parameters", "statement", "rstatement", "dstatement", 
      "trailer", "block", "empty", "initializer", "afterthought", "declaration", 
      "decltype", "type", "declvar", "trap", "noncondexpression", "expression", 
      "unary", "unarynotaddsub", "castexpression", "primordefcasttype", "refcasttype", 
      "chain", "primary", "postfix", "postdot", "callinvoke", "fieldaccess", 
      "braceaccess", "arrayinitializer", "listinitializer", "mapinitializer", 
      "maptoken", "arguments", "argument", "lambda", "lamtype", "funcref"
    };
  }
  public static final String[] ruleNames = makeRuleNames();

  private static String[] makeLiteralNames() {
    return new String[] {
      null, null, null, "'{'", "'}'", "'['", "']'", "'('", "')'", "'$'", "'.'", 
      "'?.'", "','", "';'", "'if'", "'in'", "'else'", "'while'", "'do'", "'for'", 
      "'continue'", "'break'", "'return'", "'new'", "'try'", "'catch'", "'throw'", 
      "'this'", "'instanceof'", "'!'", "'~'", "'*'", "'/'", "'%'", "'+'", "'-'", 
      "'<<'", "'>>'", "'>>>'", "'<'", "'<='", "'>'", "'>='", "'=='", "'==='", 
      "'!='", "'!=='", "'&'", "'^'", "'|'", "'&&'", "'||'", "'?'", "':'", "'?:'", 
      "'::'", "'->'", "'=~'", "'==~'", "'++'", "'--'", "'='", "'+='", "'-='", 
      "'*='", "'/='", "'%='", "'&='", "'^='", "'|='", "'<<='", "'>>='", "'>>>='", 
      null, null, null, null, null, null, "'true'", "'false'", "'null'", null, 
      "'def'"
    };
  }
  private static final String[] _LITERAL_NAMES = makeLiteralNames();
  private static String[] makeSymbolicNames() {
    return new String[] {
      null, "WS", "COMMENT", "LBRACK", "RBRACK", "LBRACE", "RBRACE", "LP", 
      "RP", "DOLLAR", "DOT", "NSDOT", "COMMA", "SEMICOLON", "IF", "IN", "ELSE", 
      "WHILE", "DO", "FOR", "CONTINUE", "BREAK", "RETURN", "NEW", "TRY", "CATCH", 
      "THROW", "THIS", "INSTANCEOF", "BOOLNOT", "BWNOT", "MUL", "DIV", "REM", 
      "ADD", "SUB", "LSH", "RSH", "USH", "LT", "LTE", "GT", "GTE", "EQ", "EQR", 
      "NE", "NER", "BWAND", "XOR", "BWOR", "BOOLAND", "BOOLOR", "COND", "COLON", 
      "ELVIS", "REF", "ARROW", "FIND", "MATCH", "INCR", "DECR", "ASSIGN", "AADD", 
      "ASUB", "AMUL", "ADIV", "AREM", "AAND", "AXOR", "AOR", "ALSH", "ARSH", 
      "AUSH", "OCTAL", "HEX", "INTEGER", "DECIMAL", "STRING", "REGEX", "TRUE", 
      "FALSE", "NULL", "PRIMITIVE", "DEF", "ID", "DOTINTEGER", "DOTID"
    };
  }
  private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
  public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

  /**
   * @deprecated Use {@link #VOCABULARY} instead.
   */
  @Deprecated
  public static final String[] tokenNames;
  static {
    tokenNames = new String[_SYMBOLIC_NAMES.length];
    for (int i = 0; i < tokenNames.length; i++) {
      tokenNames[i] = VOCABULARY.getLiteralName(i);
      if (tokenNames[i] == null) {
        tokenNames[i] = VOCABULARY.getSymbolicName(i);
      }

      if (tokenNames[i] == null) {
        tokenNames[i] = "<INVALID>";
      }
    }
  }

  @Override
  @Deprecated
  public String[] getTokenNames() {
    return tokenNames;
  }

  @Override

  public Vocabulary getVocabulary() {
    return VOCABULARY;
  }

  @Override
  public String getGrammarFileName() { return "JavascriptParser.g4"; }

  @Override
  public String[] getRuleNames() { return ruleNames; }

  @Override
  public String getSerializedATN() { return _serializedATN; }

  @Override
  public ATN getATN() { return _ATN; }


      private boolean isLambdaAhead() {
          int la1 = _input.LA(1);

          if (la1 == LP) {
              int index = 1;
              int depth = 0;

              while (true) {
                  int token = _input.LA(index++);
                  if (token == org.antlr.v4.runtime.Token.EOF) {
                      return false;
                  }
                  if (token == LP) {
                      depth++;
                  } else if (token == RP) {
                      depth--;
                      if (depth == 0) {
                          return _input.LA(index) == ARROW;
                      }
                  }
              }
          }

          if (la1 == ID && _input.LA(2) == ARROW) {
              return true;
          }

          int afterType = skipDeclType(1);
          if (afterType == -1) {
              return false;
          }

          return _input.LA(afterType) == ID && _input.LA(afterType + 1) == ARROW;
      }

      private boolean isFunctionRefAhead() {
          if (_input.LA(1) == THIS) {
              return _input.LA(2) == REF && _input.LA(3) == ID;
          }

          int afterType = skipDeclType(1);
          if (afterType == -1) {
              return false;
          }

          if (_input.LA(afterType) != REF) {
              return false;
          }

          int target = _input.LA(afterType + 1);
          return target == ID || target == NEW;
      }

      private int skipDeclType(int index) {
          int token = _input.LA(index);
          if (token == DEF || token == PRIMITIVE) {
              index++;
          } else if (token == ID) {
              index++;

              while (_input.LA(index) == DOT) {
                  if (_input.LA(index + 1) != DOTID) {
                      return -1;
                  }
                  index += 2;
              }
          } else {
              return -1;
          }

          while (_input.LA(index) == LBRACE && _input.LA(index + 1) == RBRACE) {
              index += 2;
          }

          return index;
      }

  public JavascriptParser(TokenStream input) {
    super(input);
    _interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
  }

  @SuppressWarnings("CheckReturnValue")
  public static class SourceContext extends ParserRuleContext {
    public TerminalNode EOF() { return getToken(JavascriptParser.EOF, 0); }
    public List<FunctionContext> function() {
      return getRuleContexts(FunctionContext.class);
    }
    public FunctionContext function(int i) {
      return getRuleContext(FunctionContext.class,i);
    }
    public List<StatementContext> statement() {
      return getRuleContexts(StatementContext.class);
    }
    public StatementContext statement(int i) {
      return getRuleContext(StatementContext.class,i);
    }
    public SourceContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_source; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitSource(this);
      else return visitor.visitChildren(this);
    }
  }

  public final SourceContext source() throws RecognitionException {
    SourceContext _localctx = new SourceContext(_ctx, getState());
    enterRule(_localctx, 0, RULE_source);
    try {
      int _alt;
      enterOuterAlt(_localctx, 1);
      {
      setState(81);
      _errHandler.sync(this);
      _alt = getInterpreter().adaptivePredict(_input,0,_ctx);
      while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
        if ( _alt==1 ) {
          {
          {
          setState(78);
          function();
          }
          } 
        }
        setState(83);
        _errHandler.sync(this);
        _alt = getInterpreter().adaptivePredict(_input,0,_ctx);
      }
      setState(87);
      _errHandler.sync(this);
      _alt = getInterpreter().adaptivePredict(_input,1,_ctx);
      while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
        if ( _alt==1 ) {
          {
          {
          setState(84);
          statement();
          }
          } 
        }
        setState(89);
        _errHandler.sync(this);
        _alt = getInterpreter().adaptivePredict(_input,1,_ctx);
      }
      setState(90);
      match(EOF);
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class FunctionContext extends ParserRuleContext {
    public DecltypeContext decltype() {
      return getRuleContext(DecltypeContext.class,0);
    }
    public TerminalNode ID() { return getToken(JavascriptParser.ID, 0); }
    public ParametersContext parameters() {
      return getRuleContext(ParametersContext.class,0);
    }
    public BlockContext block() {
      return getRuleContext(BlockContext.class,0);
    }
    public FunctionContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_function; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitFunction(this);
      else return visitor.visitChildren(this);
    }
  }

  public final FunctionContext function() throws RecognitionException {
    FunctionContext _localctx = new FunctionContext(_ctx, getState());
    enterRule(_localctx, 2, RULE_function);
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(92);
      decltype();
      setState(93);
      match(ID);
      setState(94);
      parameters();
      setState(95);
      block();
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class ParametersContext extends ParserRuleContext {
    public TerminalNode LP() { return getToken(JavascriptParser.LP, 0); }
    public TerminalNode RP() { return getToken(JavascriptParser.RP, 0); }
    public List<DecltypeContext> decltype() {
      return getRuleContexts(DecltypeContext.class);
    }
    public DecltypeContext decltype(int i) {
      return getRuleContext(DecltypeContext.class,i);
    }
    public List<TerminalNode> ID() { return getTokens(JavascriptParser.ID); }
    public TerminalNode ID(int i) {
      return getToken(JavascriptParser.ID, i);
    }
    public List<TerminalNode> COMMA() { return getTokens(JavascriptParser.COMMA); }
    public TerminalNode COMMA(int i) {
      return getToken(JavascriptParser.COMMA, i);
    }
    public ParametersContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_parameters; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitParameters(this);
      else return visitor.visitChildren(this);
    }
  }

  public final ParametersContext parameters() throws RecognitionException {
    ParametersContext _localctx = new ParametersContext(_ctx, getState());
    enterRule(_localctx, 4, RULE_parameters);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(97);
      match(LP);
      setState(109);
      _errHandler.sync(this);
      _la = _input.LA(1);
      if (((((_la - 82)) & ~0x3f) == 0 && ((1L << (_la - 82)) & 7L) != 0)) {
        {
        setState(98);
        decltype();
        setState(99);
        match(ID);
        setState(106);
        _errHandler.sync(this);
        _la = _input.LA(1);
        while (_la==COMMA) {
          {
          {
          setState(100);
          match(COMMA);
          setState(101);
          decltype();
          setState(102);
          match(ID);
          }
          }
          setState(108);
          _errHandler.sync(this);
          _la = _input.LA(1);
        }
        }
      }

      setState(111);
      match(RP);
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class StatementContext extends ParserRuleContext {
    public RstatementContext rstatement() {
      return getRuleContext(RstatementContext.class,0);
    }
    public DstatementContext dstatement() {
      return getRuleContext(DstatementContext.class,0);
    }
    public TerminalNode SEMICOLON() { return getToken(JavascriptParser.SEMICOLON, 0); }
    public TerminalNode EOF() { return getToken(JavascriptParser.EOF, 0); }
    public StatementContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_statement; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitStatement(this);
      else return visitor.visitChildren(this);
    }
  }

  public final StatementContext statement() throws RecognitionException {
    StatementContext _localctx = new StatementContext(_ctx, getState());
    enterRule(_localctx, 6, RULE_statement);
    int _la;
    try {
      setState(117);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
      case 1:
        enterOuterAlt(_localctx, 1);
        {
        setState(113);
        rstatement();
        }
        break;
      case 2:
        enterOuterAlt(_localctx, 2);
        {
        setState(114);
        dstatement();
        setState(115);
        _la = _input.LA(1);
        if ( !(_la==EOF || _la==SEMICOLON) ) {
        _errHandler.recoverInline(this);
        }
        else {
          if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
          _errHandler.reportMatch(this);
          consume();
        }
        }
        break;
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class RstatementContext extends ParserRuleContext {
    public RstatementContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_rstatement; }
   
    public RstatementContext() { }
    public void copyFrom(RstatementContext ctx) {
      super.copyFrom(ctx);
    }
  }
  @SuppressWarnings("CheckReturnValue")
  public static class ForContext extends RstatementContext {
    public TerminalNode FOR() { return getToken(JavascriptParser.FOR, 0); }
    public TerminalNode LP() { return getToken(JavascriptParser.LP, 0); }
    public List<TerminalNode> SEMICOLON() { return getTokens(JavascriptParser.SEMICOLON); }
    public TerminalNode SEMICOLON(int i) {
      return getToken(JavascriptParser.SEMICOLON, i);
    }
    public TerminalNode RP() { return getToken(JavascriptParser.RP, 0); }
    public TrailerContext trailer() {
      return getRuleContext(TrailerContext.class,0);
    }
    public EmptyContext empty() {
      return getRuleContext(EmptyContext.class,0);
    }
    public InitializerContext initializer() {
      return getRuleContext(InitializerContext.class,0);
    }
    public ExpressionContext expression() {
      return getRuleContext(ExpressionContext.class,0);
    }
    public AfterthoughtContext afterthought() {
      return getRuleContext(AfterthoughtContext.class,0);
    }
    public ForContext(RstatementContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitFor(this);
      else return visitor.visitChildren(this);
    }
  }
  @SuppressWarnings("CheckReturnValue")
  public static class TryContext extends RstatementContext {
    public TerminalNode TRY() { return getToken(JavascriptParser.TRY, 0); }
    public BlockContext block() {
      return getRuleContext(BlockContext.class,0);
    }
    public List<TrapContext> trap() {
      return getRuleContexts(TrapContext.class);
    }
    public TrapContext trap(int i) {
      return getRuleContext(TrapContext.class,i);
    }
    public TryContext(RstatementContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitTry(this);
      else return visitor.visitChildren(this);
    }
  }
  @SuppressWarnings("CheckReturnValue")
  public static class WhileContext extends RstatementContext {
    public TerminalNode WHILE() { return getToken(JavascriptParser.WHILE, 0); }
    public TerminalNode LP() { return getToken(JavascriptParser.LP, 0); }
    public ExpressionContext expression() {
      return getRuleContext(ExpressionContext.class,0);
    }
    public TerminalNode RP() { return getToken(JavascriptParser.RP, 0); }
    public TrailerContext trailer() {
      return getRuleContext(TrailerContext.class,0);
    }
    public EmptyContext empty() {
      return getRuleContext(EmptyContext.class,0);
    }
    public WhileContext(RstatementContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitWhile(this);
      else return visitor.visitChildren(this);
    }
  }
  @SuppressWarnings("CheckReturnValue")
  public static class IneachContext extends RstatementContext {
    public TerminalNode FOR() { return getToken(JavascriptParser.FOR, 0); }
    public TerminalNode LP() { return getToken(JavascriptParser.LP, 0); }
    public TerminalNode ID() { return getToken(JavascriptParser.ID, 0); }
    public TerminalNode IN() { return getToken(JavascriptParser.IN, 0); }
    public ExpressionContext expression() {
      return getRuleContext(ExpressionContext.class,0);
    }
    public TerminalNode RP() { return getToken(JavascriptParser.RP, 0); }
    public TrailerContext trailer() {
      return getRuleContext(TrailerContext.class,0);
    }
    public IneachContext(RstatementContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitIneach(this);
      else return visitor.visitChildren(this);
    }
  }
  @SuppressWarnings("CheckReturnValue")
  public static class IfContext extends RstatementContext {
    public TerminalNode IF() { return getToken(JavascriptParser.IF, 0); }
    public TerminalNode LP() { return getToken(JavascriptParser.LP, 0); }
    public ExpressionContext expression() {
      return getRuleContext(ExpressionContext.class,0);
    }
    public TerminalNode RP() { return getToken(JavascriptParser.RP, 0); }
    public List<TrailerContext> trailer() {
      return getRuleContexts(TrailerContext.class);
    }
    public TrailerContext trailer(int i) {
      return getRuleContext(TrailerContext.class,i);
    }
    public TerminalNode ELSE() { return getToken(JavascriptParser.ELSE, 0); }
    public IfContext(RstatementContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitIf(this);
      else return visitor.visitChildren(this);
    }
  }
  @SuppressWarnings("CheckReturnValue")
  public static class EachContext extends RstatementContext {
    public TerminalNode FOR() { return getToken(JavascriptParser.FOR, 0); }
    public TerminalNode LP() { return getToken(JavascriptParser.LP, 0); }
    public DecltypeContext decltype() {
      return getRuleContext(DecltypeContext.class,0);
    }
    public TerminalNode ID() { return getToken(JavascriptParser.ID, 0); }
    public TerminalNode COLON() { return getToken(JavascriptParser.COLON, 0); }
    public ExpressionContext expression() {
      return getRuleContext(ExpressionContext.class,0);
    }
    public TerminalNode RP() { return getToken(JavascriptParser.RP, 0); }
    public TrailerContext trailer() {
      return getRuleContext(TrailerContext.class,0);
    }
    public EachContext(RstatementContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitEach(this);
      else return visitor.visitChildren(this);
    }
  }

  public final RstatementContext rstatement() throws RecognitionException {
    RstatementContext _localctx = new RstatementContext(_ctx, getState());
    enterRule(_localctx, 8, RULE_rstatement);
    try {
      int _alt;
      setState(179);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
      case 1:
        _localctx = new IfContext(_localctx);
        enterOuterAlt(_localctx, 1);
        {
        setState(119);
        match(IF);
        setState(120);
        match(LP);
        setState(121);
        expression();
        setState(122);
        match(RP);
        setState(123);
        trailer();
        setState(127);
        _errHandler.sync(this);
        switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
        case 1:
          {
          setState(124);
          match(ELSE);
          setState(125);
          trailer();
          }
          break;
        case 2:
          {
          setState(126);
          if (!( _input.LA(1) != ELSE )) throw new FailedPredicateException(this, " _input.LA(1) != ELSE ");
          }
          break;
        }
        }
        break;
      case 2:
        _localctx = new WhileContext(_localctx);
        enterOuterAlt(_localctx, 2);
        {
        setState(129);
        match(WHILE);
        setState(130);
        match(LP);
        setState(131);
        expression();
        setState(132);
        match(RP);
        setState(135);
        _errHandler.sync(this);
        switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
        case 1:
          {
          setState(133);
          trailer();
          }
          break;
        case 2:
          {
          setState(134);
          empty();
          }
          break;
        }
        }
        break;
      case 3:
        _localctx = new ForContext(_localctx);
        enterOuterAlt(_localctx, 3);
        {
        setState(137);
        match(FOR);
        setState(138);
        match(LP);
        setState(140);
        _errHandler.sync(this);
        switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
        case 1:
          {
          setState(139);
          initializer();
          }
          break;
        }
        setState(142);
        match(SEMICOLON);
        setState(144);
        _errHandler.sync(this);
        switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
        case 1:
          {
          setState(143);
          expression();
          }
          break;
        }
        setState(146);
        match(SEMICOLON);
        setState(148);
        _errHandler.sync(this);
        switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
        case 1:
          {
          setState(147);
          afterthought();
          }
          break;
        }
        setState(150);
        match(RP);
        setState(153);
        _errHandler.sync(this);
        switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
        case 1:
          {
          setState(151);
          trailer();
          }
          break;
        case 2:
          {
          setState(152);
          empty();
          }
          break;
        }
        }
        break;
      case 4:
        _localctx = new EachContext(_localctx);
        enterOuterAlt(_localctx, 4);
        {
        setState(155);
        match(FOR);
        setState(156);
        match(LP);
        setState(157);
        decltype();
        setState(158);
        match(ID);
        setState(159);
        match(COLON);
        setState(160);
        expression();
        setState(161);
        match(RP);
        setState(162);
        trailer();
        }
        break;
      case 5:
        _localctx = new IneachContext(_localctx);
        enterOuterAlt(_localctx, 5);
        {
        setState(164);
        match(FOR);
        setState(165);
        match(LP);
        setState(166);
        match(ID);
        setState(167);
        match(IN);
        setState(168);
        expression();
        setState(169);
        match(RP);
        setState(170);
        trailer();
        }
        break;
      case 6:
        _localctx = new TryContext(_localctx);
        enterOuterAlt(_localctx, 6);
        {
        setState(172);
        match(TRY);
        setState(173);
        block();
        setState(175); 
        _errHandler.sync(this);
        _alt = 1;
        do {
          switch (_alt) {
          case 1:
            {
            {
            setState(174);
            trap();
            }
            }
            break;
          default:
            throw new NoViableAltException(this);
          }
          setState(177); 
          _errHandler.sync(this);
          _alt = getInterpreter().adaptivePredict(_input,11,_ctx);
        } while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
        }
        break;
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class DstatementContext extends ParserRuleContext {
    public DstatementContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_dstatement; }
   
    public DstatementContext() { }
    public void copyFrom(DstatementContext ctx) {
      super.copyFrom(ctx);
    }
  }
  @SuppressWarnings("CheckReturnValue")
  public static class DeclContext extends DstatementContext {
    public DeclarationContext declaration() {
      return getRuleContext(DeclarationContext.class,0);
    }
    public DeclContext(DstatementContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitDecl(this);
      else return visitor.visitChildren(this);
    }
  }
  @SuppressWarnings("CheckReturnValue")
  public static class BreakContext extends DstatementContext {
    public TerminalNode BREAK() { return getToken(JavascriptParser.BREAK, 0); }
    public BreakContext(DstatementContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitBreak(this);
      else return visitor.visitChildren(this);
    }
  }
  @SuppressWarnings("CheckReturnValue")
  public static class ThrowContext extends DstatementContext {
    public TerminalNode THROW() { return getToken(JavascriptParser.THROW, 0); }
    public ExpressionContext expression() {
      return getRuleContext(ExpressionContext.class,0);
    }
    public ThrowContext(DstatementContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitThrow(this);
      else return visitor.visitChildren(this);
    }
  }
  @SuppressWarnings("CheckReturnValue")
  public static class ContinueContext extends DstatementContext {
    public TerminalNode CONTINUE() { return getToken(JavascriptParser.CONTINUE, 0); }
    public ContinueContext(DstatementContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitContinue(this);
      else return visitor.visitChildren(this);
    }
  }
  @SuppressWarnings("CheckReturnValue")
  public static class ExprContext extends DstatementContext {
    public ExpressionContext expression() {
      return getRuleContext(ExpressionContext.class,0);
    }
    public ExprContext(DstatementContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitExpr(this);
      else return visitor.visitChildren(this);
    }
  }
  @SuppressWarnings("CheckReturnValue")
  public static class DoContext extends DstatementContext {
    public TerminalNode DO() { return getToken(JavascriptParser.DO, 0); }
    public BlockContext block() {
      return getRuleContext(BlockContext.class,0);
    }
    public TerminalNode WHILE() { return getToken(JavascriptParser.WHILE, 0); }
    public TerminalNode LP() { return getToken(JavascriptParser.LP, 0); }
    public ExpressionContext expression() {
      return getRuleContext(ExpressionContext.class,0);
    }
    public TerminalNode RP() { return getToken(JavascriptParser.RP, 0); }
    public DoContext(DstatementContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitDo(this);
      else return visitor.visitChildren(this);
    }
  }
  @SuppressWarnings("CheckReturnValue")
  public static class ReturnContext extends DstatementContext {
    public TerminalNode RETURN() { return getToken(JavascriptParser.RETURN, 0); }
    public ExpressionContext expression() {
      return getRuleContext(ExpressionContext.class,0);
    }
    public ReturnContext(DstatementContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitReturn(this);
      else return visitor.visitChildren(this);
    }
  }

  public final DstatementContext dstatement() throws RecognitionException {
    DstatementContext _localctx = new DstatementContext(_ctx, getState());
    enterRule(_localctx, 10, RULE_dstatement);
    try {
      setState(198);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
      case 1:
        _localctx = new DoContext(_localctx);
        enterOuterAlt(_localctx, 1);
        {
        setState(181);
        match(DO);
        setState(182);
        block();
        setState(183);
        match(WHILE);
        setState(184);
        match(LP);
        setState(185);
        expression();
        setState(186);
        match(RP);
        }
        break;
      case 2:
        _localctx = new DeclContext(_localctx);
        enterOuterAlt(_localctx, 2);
        {
        setState(188);
        declaration();
        }
        break;
      case 3:
        _localctx = new ContinueContext(_localctx);
        enterOuterAlt(_localctx, 3);
        {
        setState(189);
        match(CONTINUE);
        }
        break;
      case 4:
        _localctx = new BreakContext(_localctx);
        enterOuterAlt(_localctx, 4);
        {
        setState(190);
        match(BREAK);
        }
        break;
      case 5:
        _localctx = new ReturnContext(_localctx);
        enterOuterAlt(_localctx, 5);
        {
        setState(191);
        match(RETURN);
        setState(193);
        _errHandler.sync(this);
        switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
        case 1:
          {
          setState(192);
          expression();
          }
          break;
        }
        }
        break;
      case 6:
        _localctx = new ThrowContext(_localctx);
        enterOuterAlt(_localctx, 6);
        {
        setState(195);
        match(THROW);
        setState(196);
        expression();
        }
        break;
      case 7:
        _localctx = new ExprContext(_localctx);
        enterOuterAlt(_localctx, 7);
        {
        setState(197);
        expression();
        }
        break;
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class TrailerContext extends ParserRuleContext {
    public BlockContext block() {
      return getRuleContext(BlockContext.class,0);
    }
    public StatementContext statement() {
      return getRuleContext(StatementContext.class,0);
    }
    public TrailerContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_trailer; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitTrailer(this);
      else return visitor.visitChildren(this);
    }
  }

  public final TrailerContext trailer() throws RecognitionException {
    TrailerContext _localctx = new TrailerContext(_ctx, getState());
    enterRule(_localctx, 12, RULE_trailer);
    try {
      setState(202);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,15,_ctx) ) {
      case 1:
        enterOuterAlt(_localctx, 1);
        {
        setState(200);
        block();
        }
        break;
      case 2:
        enterOuterAlt(_localctx, 2);
        {
        setState(201);
        statement();
        }
        break;
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class BlockContext extends ParserRuleContext {
    public TerminalNode LBRACK() { return getToken(JavascriptParser.LBRACK, 0); }
    public TerminalNode RBRACK() { return getToken(JavascriptParser.RBRACK, 0); }
    public List<StatementContext> statement() {
      return getRuleContexts(StatementContext.class);
    }
    public StatementContext statement(int i) {
      return getRuleContext(StatementContext.class,i);
    }
    public DstatementContext dstatement() {
      return getRuleContext(DstatementContext.class,0);
    }
    public BlockContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_block; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitBlock(this);
      else return visitor.visitChildren(this);
    }
  }

  public final BlockContext block() throws RecognitionException {
    BlockContext _localctx = new BlockContext(_ctx, getState());
    enterRule(_localctx, 14, RULE_block);
    try {
      int _alt;
      enterOuterAlt(_localctx, 1);
      {
      setState(204);
      match(LBRACK);
      setState(208);
      _errHandler.sync(this);
      _alt = getInterpreter().adaptivePredict(_input,16,_ctx);
      while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
        if ( _alt==1 ) {
          {
          {
          setState(205);
          statement();
          }
          } 
        }
        setState(210);
        _errHandler.sync(this);
        _alt = getInterpreter().adaptivePredict(_input,16,_ctx);
      }
      setState(212);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,17,_ctx) ) {
      case 1:
        {
        setState(211);
        dstatement();
        }
        break;
      }
      setState(214);
      match(RBRACK);
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class EmptyContext extends ParserRuleContext {
    public TerminalNode SEMICOLON() { return getToken(JavascriptParser.SEMICOLON, 0); }
    public EmptyContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_empty; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitEmpty(this);
      else return visitor.visitChildren(this);
    }
  }

  public final EmptyContext empty() throws RecognitionException {
    EmptyContext _localctx = new EmptyContext(_ctx, getState());
    enterRule(_localctx, 16, RULE_empty);
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(216);
      match(SEMICOLON);
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class InitializerContext extends ParserRuleContext {
    public DeclarationContext declaration() {
      return getRuleContext(DeclarationContext.class,0);
    }
    public ExpressionContext expression() {
      return getRuleContext(ExpressionContext.class,0);
    }
    public InitializerContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_initializer; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitInitializer(this);
      else return visitor.visitChildren(this);
    }
  }

  public final InitializerContext initializer() throws RecognitionException {
    InitializerContext _localctx = new InitializerContext(_ctx, getState());
    enterRule(_localctx, 18, RULE_initializer);
    try {
      setState(220);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,18,_ctx) ) {
      case 1:
        enterOuterAlt(_localctx, 1);
        {
        setState(218);
        declaration();
        }
        break;
      case 2:
        enterOuterAlt(_localctx, 2);
        {
        setState(219);
        expression();
        }
        break;
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class AfterthoughtContext extends ParserRuleContext {
    public ExpressionContext expression() {
      return getRuleContext(ExpressionContext.class,0);
    }
    public AfterthoughtContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_afterthought; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitAfterthought(this);
      else return visitor.visitChildren(this);
    }
  }

  public final AfterthoughtContext afterthought() throws RecognitionException {
    AfterthoughtContext _localctx = new AfterthoughtContext(_ctx, getState());
    enterRule(_localctx, 20, RULE_afterthought);
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(222);
      expression();
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class DeclarationContext extends ParserRuleContext {
    public DecltypeContext decltype() {
      return getRuleContext(DecltypeContext.class,0);
    }
    public List<DeclvarContext> declvar() {
      return getRuleContexts(DeclvarContext.class);
    }
    public DeclvarContext declvar(int i) {
      return getRuleContext(DeclvarContext.class,i);
    }
    public List<TerminalNode> COMMA() { return getTokens(JavascriptParser.COMMA); }
    public TerminalNode COMMA(int i) {
      return getToken(JavascriptParser.COMMA, i);
    }
    public DeclarationContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_declaration; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitDeclaration(this);
      else return visitor.visitChildren(this);
    }
  }

  public final DeclarationContext declaration() throws RecognitionException {
    DeclarationContext _localctx = new DeclarationContext(_ctx, getState());
    enterRule(_localctx, 22, RULE_declaration);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(224);
      decltype();
      setState(225);
      declvar();
      setState(230);
      _errHandler.sync(this);
      _la = _input.LA(1);
      while (_la==COMMA) {
        {
        {
        setState(226);
        match(COMMA);
        setState(227);
        declvar();
        }
        }
        setState(232);
        _errHandler.sync(this);
        _la = _input.LA(1);
      }
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class DecltypeContext extends ParserRuleContext {
    public TypeContext type() {
      return getRuleContext(TypeContext.class,0);
    }
    public List<TerminalNode> LBRACE() { return getTokens(JavascriptParser.LBRACE); }
    public TerminalNode LBRACE(int i) {
      return getToken(JavascriptParser.LBRACE, i);
    }
    public List<TerminalNode> RBRACE() { return getTokens(JavascriptParser.RBRACE); }
    public TerminalNode RBRACE(int i) {
      return getToken(JavascriptParser.RBRACE, i);
    }
    public DecltypeContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_decltype; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitDecltype(this);
      else return visitor.visitChildren(this);
    }
  }

  public final DecltypeContext decltype() throws RecognitionException {
    DecltypeContext _localctx = new DecltypeContext(_ctx, getState());
    enterRule(_localctx, 24, RULE_decltype);
    try {
      int _alt;
      enterOuterAlt(_localctx, 1);
      {
      setState(233);
      type();
      setState(238);
      _errHandler.sync(this);
      _alt = getInterpreter().adaptivePredict(_input,20,_ctx);
      while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
        if ( _alt==1 ) {
          {
          {
          setState(234);
          match(LBRACE);
          setState(235);
          match(RBRACE);
          }
          } 
        }
        setState(240);
        _errHandler.sync(this);
        _alt = getInterpreter().adaptivePredict(_input,20,_ctx);
      }
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class TypeContext extends ParserRuleContext {
    public TerminalNode DEF() { return getToken(JavascriptParser.DEF, 0); }
    public TerminalNode PRIMITIVE() { return getToken(JavascriptParser.PRIMITIVE, 0); }
    public TerminalNode ID() { return getToken(JavascriptParser.ID, 0); }
    public List<TerminalNode> DOT() { return getTokens(JavascriptParser.DOT); }
    public TerminalNode DOT(int i) {
      return getToken(JavascriptParser.DOT, i);
    }
    public List<TerminalNode> DOTID() { return getTokens(JavascriptParser.DOTID); }
    public TerminalNode DOTID(int i) {
      return getToken(JavascriptParser.DOTID, i);
    }
    public TypeContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_type; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitType(this);
      else return visitor.visitChildren(this);
    }
  }

  public final TypeContext type() throws RecognitionException {
    TypeContext _localctx = new TypeContext(_ctx, getState());
    enterRule(_localctx, 26, RULE_type);
    try {
      int _alt;
      setState(251);
      _errHandler.sync(this);
      switch (_input.LA(1)) {
      case DEF:
        enterOuterAlt(_localctx, 1);
        {
        setState(241);
        match(DEF);
        }
        break;
      case PRIMITIVE:
        enterOuterAlt(_localctx, 2);
        {
        setState(242);
        match(PRIMITIVE);
        }
        break;
      case ID:
        enterOuterAlt(_localctx, 3);
        {
        setState(243);
        match(ID);
        setState(248);
        _errHandler.sync(this);
        _alt = getInterpreter().adaptivePredict(_input,21,_ctx);
        while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
          if ( _alt==1 ) {
            {
            {
            setState(244);
            match(DOT);
            setState(245);
            match(DOTID);
            }
            } 
          }
          setState(250);
          _errHandler.sync(this);
          _alt = getInterpreter().adaptivePredict(_input,21,_ctx);
        }
        }
        break;
      default:
        throw new NoViableAltException(this);
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class DeclvarContext extends ParserRuleContext {
    public TerminalNode ID() { return getToken(JavascriptParser.ID, 0); }
    public TerminalNode ASSIGN() { return getToken(JavascriptParser.ASSIGN, 0); }
    public ExpressionContext expression() {
      return getRuleContext(ExpressionContext.class,0);
    }
    public DeclvarContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_declvar; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitDeclvar(this);
      else return visitor.visitChildren(this);
    }
  }

  public final DeclvarContext declvar() throws RecognitionException {
    DeclvarContext _localctx = new DeclvarContext(_ctx, getState());
    enterRule(_localctx, 28, RULE_declvar);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(253);
      match(ID);
      setState(256);
      _errHandler.sync(this);
      _la = _input.LA(1);
      if (_la==ASSIGN) {
        {
        setState(254);
        match(ASSIGN);
        setState(255);
        expression();
        }
      }

      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class TrapContext extends ParserRuleContext {
    public TerminalNode CATCH() { return getToken(JavascriptParser.CATCH, 0); }
    public TerminalNode LP() { return getToken(JavascriptParser.LP, 0); }
    public TypeContext type() {
      return getRuleContext(TypeContext.class,0);
    }
    public TerminalNode ID() { return getToken(JavascriptParser.ID, 0); }
    public TerminalNode RP() { return getToken(JavascriptParser.RP, 0); }
    public BlockContext block() {
      return getRuleContext(BlockContext.class,0);
    }
    public TrapContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_trap; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitTrap(this);
      else return visitor.visitChildren(this);
    }
  }

  public final TrapContext trap() throws RecognitionException {
    TrapContext _localctx = new TrapContext(_ctx, getState());
    enterRule(_localctx, 30, RULE_trap);
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(258);
      match(CATCH);
      setState(259);
      match(LP);
      setState(260);
      type();
      setState(261);
      match(ID);
      setState(262);
      match(RP);
      setState(263);
      block();
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class NoncondexpressionContext extends ParserRuleContext {
    public NoncondexpressionContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_noncondexpression; }
   
    public NoncondexpressionContext() { }
    public void copyFrom(NoncondexpressionContext ctx) {
      super.copyFrom(ctx);
    }
  }
  @SuppressWarnings("CheckReturnValue")
  public static class SingleContext extends NoncondexpressionContext {
    public UnaryContext unary() {
      return getRuleContext(UnaryContext.class,0);
    }
    public SingleContext(NoncondexpressionContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitSingle(this);
      else return visitor.visitChildren(this);
    }
  }
  @SuppressWarnings("CheckReturnValue")
  public static class CompContext extends NoncondexpressionContext {
    public List<NoncondexpressionContext> noncondexpression() {
      return getRuleContexts(NoncondexpressionContext.class);
    }
    public NoncondexpressionContext noncondexpression(int i) {
      return getRuleContext(NoncondexpressionContext.class,i);
    }
    public TerminalNode LT() { return getToken(JavascriptParser.LT, 0); }
    public TerminalNode LTE() { return getToken(JavascriptParser.LTE, 0); }
    public TerminalNode GT() { return getToken(JavascriptParser.GT, 0); }
    public TerminalNode GTE() { return getToken(JavascriptParser.GTE, 0); }
    public TerminalNode EQ() { return getToken(JavascriptParser.EQ, 0); }
    public TerminalNode EQR() { return getToken(JavascriptParser.EQR, 0); }
    public TerminalNode NE() { return getToken(JavascriptParser.NE, 0); }
    public TerminalNode NER() { return getToken(JavascriptParser.NER, 0); }
    public CompContext(NoncondexpressionContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitComp(this);
      else return visitor.visitChildren(this);
    }
  }
  @SuppressWarnings("CheckReturnValue")
  public static class BoolContext extends NoncondexpressionContext {
    public List<NoncondexpressionContext> noncondexpression() {
      return getRuleContexts(NoncondexpressionContext.class);
    }
    public NoncondexpressionContext noncondexpression(int i) {
      return getRuleContext(NoncondexpressionContext.class,i);
    }
    public TerminalNode BOOLAND() { return getToken(JavascriptParser.BOOLAND, 0); }
    public TerminalNode BOOLOR() { return getToken(JavascriptParser.BOOLOR, 0); }
    public BoolContext(NoncondexpressionContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitBool(this);
      else return visitor.visitChildren(this);
    }
  }
  @SuppressWarnings("CheckReturnValue")
  public static class BinaryContext extends NoncondexpressionContext {
    public List<NoncondexpressionContext> noncondexpression() {
      return getRuleContexts(NoncondexpressionContext.class);
    }
    public NoncondexpressionContext noncondexpression(int i) {
      return getRuleContext(NoncondexpressionContext.class,i);
    }
    public TerminalNode MUL() { return getToken(JavascriptParser.MUL, 0); }
    public TerminalNode DIV() { return getToken(JavascriptParser.DIV, 0); }
    public TerminalNode REM() { return getToken(JavascriptParser.REM, 0); }
    public TerminalNode ADD() { return getToken(JavascriptParser.ADD, 0); }
    public TerminalNode SUB() { return getToken(JavascriptParser.SUB, 0); }
    public TerminalNode FIND() { return getToken(JavascriptParser.FIND, 0); }
    public TerminalNode MATCH() { return getToken(JavascriptParser.MATCH, 0); }
    public TerminalNode LSH() { return getToken(JavascriptParser.LSH, 0); }
    public TerminalNode RSH() { return getToken(JavascriptParser.RSH, 0); }
    public TerminalNode USH() { return getToken(JavascriptParser.USH, 0); }
    public TerminalNode BWAND() { return getToken(JavascriptParser.BWAND, 0); }
    public TerminalNode XOR() { return getToken(JavascriptParser.XOR, 0); }
    public TerminalNode BWOR() { return getToken(JavascriptParser.BWOR, 0); }
    public BinaryContext(NoncondexpressionContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitBinary(this);
      else return visitor.visitChildren(this);
    }
  }
  @SuppressWarnings("CheckReturnValue")
  public static class ElvisContext extends NoncondexpressionContext {
    public List<NoncondexpressionContext> noncondexpression() {
      return getRuleContexts(NoncondexpressionContext.class);
    }
    public NoncondexpressionContext noncondexpression(int i) {
      return getRuleContext(NoncondexpressionContext.class,i);
    }
    public TerminalNode ELVIS() { return getToken(JavascriptParser.ELVIS, 0); }
    public ElvisContext(NoncondexpressionContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitElvis(this);
      else return visitor.visitChildren(this);
    }
  }
  @SuppressWarnings("CheckReturnValue")
  public static class InstanceofContext extends NoncondexpressionContext {
    public NoncondexpressionContext noncondexpression() {
      return getRuleContext(NoncondexpressionContext.class,0);
    }
    public TerminalNode INSTANCEOF() { return getToken(JavascriptParser.INSTANCEOF, 0); }
    public DecltypeContext decltype() {
      return getRuleContext(DecltypeContext.class,0);
    }
    public InstanceofContext(NoncondexpressionContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitInstanceof(this);
      else return visitor.visitChildren(this);
    }
  }

  public final NoncondexpressionContext noncondexpression() throws RecognitionException {
    return noncondexpression(0);
  }

  private NoncondexpressionContext noncondexpression(int _p) throws RecognitionException {
    ParserRuleContext _parentctx = _ctx;
    int _parentState = getState();
    NoncondexpressionContext _localctx = new NoncondexpressionContext(_ctx, _parentState);
    NoncondexpressionContext _prevctx = _localctx;
    int _startState = 32;
    enterRecursionRule(_localctx, 32, RULE_noncondexpression, _p);
    int _la;
    try {
      int _alt;
      enterOuterAlt(_localctx, 1);
      {
      {
      _localctx = new SingleContext(_localctx);
      _ctx = _localctx;
      _prevctx = _localctx;

      setState(266);
      unary();
      }
      _ctx.stop = _input.LT(-1);
      setState(309);
      _errHandler.sync(this);
      _alt = getInterpreter().adaptivePredict(_input,25,_ctx);
      while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
        if ( _alt==1 ) {
          if ( _parseListeners!=null ) triggerExitRuleEvent();
          _prevctx = _localctx;
          {
          setState(307);
          _errHandler.sync(this);
          switch ( getInterpreter().adaptivePredict(_input,24,_ctx) ) {
          case 1:
            {
            _localctx = new BinaryContext(new NoncondexpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_noncondexpression);
            setState(268);
            if (!(precpred(_ctx, 13))) throw new FailedPredicateException(this, "precpred(_ctx, 13)");
            setState(269);
            _la = _input.LA(1);
            if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 15032385536L) != 0)) ) {
            _errHandler.recoverInline(this);
            }
            else {
              if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
              _errHandler.reportMatch(this);
              consume();
            }
            setState(270);
            noncondexpression(14);
            }
            break;
          case 2:
            {
            _localctx = new BinaryContext(new NoncondexpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_noncondexpression);
            setState(271);
            if (!(precpred(_ctx, 12))) throw new FailedPredicateException(this, "precpred(_ctx, 12)");
            setState(272);
            _la = _input.LA(1);
            if ( !(_la==ADD || _la==SUB) ) {
            _errHandler.recoverInline(this);
            }
            else {
              if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
              _errHandler.reportMatch(this);
              consume();
            }
            setState(273);
            noncondexpression(13);
            }
            break;
          case 3:
            {
            _localctx = new BinaryContext(new NoncondexpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_noncondexpression);
            setState(274);
            if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
            setState(275);
            _la = _input.LA(1);
            if ( !(_la==FIND || _la==MATCH) ) {
            _errHandler.recoverInline(this);
            }
            else {
              if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
              _errHandler.reportMatch(this);
              consume();
            }
            setState(276);
            noncondexpression(12);
            }
            break;
          case 4:
            {
            _localctx = new BinaryContext(new NoncondexpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_noncondexpression);
            setState(277);
            if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
            setState(278);
            _la = _input.LA(1);
            if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 481036337152L) != 0)) ) {
            _errHandler.recoverInline(this);
            }
            else {
              if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
              _errHandler.reportMatch(this);
              consume();
            }
            setState(279);
            noncondexpression(11);
            }
            break;
          case 5:
            {
            _localctx = new CompContext(new NoncondexpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_noncondexpression);
            setState(280);
            if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
            setState(281);
            _la = _input.LA(1);
            if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 8246337208320L) != 0)) ) {
            _errHandler.recoverInline(this);
            }
            else {
              if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
              _errHandler.reportMatch(this);
              consume();
            }
            setState(282);
            noncondexpression(10);
            }
            break;
          case 6:
            {
            _localctx = new CompContext(new NoncondexpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_noncondexpression);
            setState(283);
            if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
            setState(284);
            _la = _input.LA(1);
            if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 131941395333120L) != 0)) ) {
            _errHandler.recoverInline(this);
            }
            else {
              if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
              _errHandler.reportMatch(this);
              consume();
            }
            setState(285);
            noncondexpression(8);
            }
            break;
          case 7:
            {
            _localctx = new BinaryContext(new NoncondexpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_noncondexpression);
            setState(286);
            if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
            setState(287);
            match(BWAND);
            setState(288);
            noncondexpression(7);
            }
            break;
          case 8:
            {
            _localctx = new BinaryContext(new NoncondexpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_noncondexpression);
            setState(289);
            if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
            setState(290);
            match(XOR);
            setState(291);
            noncondexpression(6);
            }
            break;
          case 9:
            {
            _localctx = new BinaryContext(new NoncondexpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_noncondexpression);
            setState(292);
            if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
            setState(293);
            match(BWOR);
            setState(294);
            noncondexpression(5);
            }
            break;
          case 10:
            {
            _localctx = new BoolContext(new NoncondexpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_noncondexpression);
            setState(295);
            if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
            setState(296);
            match(BOOLAND);
            setState(297);
            noncondexpression(4);
            }
            break;
          case 11:
            {
            _localctx = new BoolContext(new NoncondexpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_noncondexpression);
            setState(298);
            if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
            setState(299);
            match(BOOLOR);
            setState(300);
            noncondexpression(3);
            }
            break;
          case 12:
            {
            _localctx = new ElvisContext(new NoncondexpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_noncondexpression);
            setState(301);
            if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
            setState(302);
            match(ELVIS);
            setState(303);
            noncondexpression(1);
            }
            break;
          case 13:
            {
            _localctx = new InstanceofContext(new NoncondexpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_noncondexpression);
            setState(304);
            if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
            setState(305);
            match(INSTANCEOF);
            setState(306);
            decltype();
            }
            break;
          }
          } 
        }
        setState(311);
        _errHandler.sync(this);
        _alt = getInterpreter().adaptivePredict(_input,25,_ctx);
      }
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      unrollRecursionContexts(_parentctx);
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class ExpressionContext extends ParserRuleContext {
    public ExpressionContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_expression; }
   
    public ExpressionContext() { }
    public void copyFrom(ExpressionContext ctx) {
      super.copyFrom(ctx);
    }
  }
  @SuppressWarnings("CheckReturnValue")
  public static class ConditionalContext extends ExpressionContext {
    public NoncondexpressionContext noncondexpression() {
      return getRuleContext(NoncondexpressionContext.class,0);
    }
    public TerminalNode COND() { return getToken(JavascriptParser.COND, 0); }
    public List<ExpressionContext> expression() {
      return getRuleContexts(ExpressionContext.class);
    }
    public ExpressionContext expression(int i) {
      return getRuleContext(ExpressionContext.class,i);
    }
    public TerminalNode COLON() { return getToken(JavascriptParser.COLON, 0); }
    public ConditionalContext(ExpressionContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitConditional(this);
      else return visitor.visitChildren(this);
    }
  }
  @SuppressWarnings("CheckReturnValue")
  public static class AssignmentContext extends ExpressionContext {
    public NoncondexpressionContext noncondexpression() {
      return getRuleContext(NoncondexpressionContext.class,0);
    }
    public ExpressionContext expression() {
      return getRuleContext(ExpressionContext.class,0);
    }
    public TerminalNode ASSIGN() { return getToken(JavascriptParser.ASSIGN, 0); }
    public TerminalNode AADD() { return getToken(JavascriptParser.AADD, 0); }
    public TerminalNode ASUB() { return getToken(JavascriptParser.ASUB, 0); }
    public TerminalNode AMUL() { return getToken(JavascriptParser.AMUL, 0); }
    public TerminalNode ADIV() { return getToken(JavascriptParser.ADIV, 0); }
    public TerminalNode AREM() { return getToken(JavascriptParser.AREM, 0); }
    public TerminalNode AAND() { return getToken(JavascriptParser.AAND, 0); }
    public TerminalNode AXOR() { return getToken(JavascriptParser.AXOR, 0); }
    public TerminalNode AOR() { return getToken(JavascriptParser.AOR, 0); }
    public TerminalNode ALSH() { return getToken(JavascriptParser.ALSH, 0); }
    public TerminalNode ARSH() { return getToken(JavascriptParser.ARSH, 0); }
    public TerminalNode AUSH() { return getToken(JavascriptParser.AUSH, 0); }
    public AssignmentContext(ExpressionContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitAssignment(this);
      else return visitor.visitChildren(this);
    }
  }
  @SuppressWarnings("CheckReturnValue")
  public static class LambdavalContext extends ExpressionContext {
    public LambdaContext lambda() {
      return getRuleContext(LambdaContext.class,0);
    }
    public LambdavalContext(ExpressionContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitLambdaval(this);
      else return visitor.visitChildren(this);
    }
  }
  @SuppressWarnings("CheckReturnValue")
  public static class FuncrefvalContext extends ExpressionContext {
    public FuncrefContext funcref() {
      return getRuleContext(FuncrefContext.class,0);
    }
    public FuncrefvalContext(ExpressionContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitFuncrefval(this);
      else return visitor.visitChildren(this);
    }
  }
  @SuppressWarnings("CheckReturnValue")
  public static class NonconditionalContext extends ExpressionContext {
    public NoncondexpressionContext noncondexpression() {
      return getRuleContext(NoncondexpressionContext.class,0);
    }
    public NonconditionalContext(ExpressionContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitNonconditional(this);
      else return visitor.visitChildren(this);
    }
  }

  public final ExpressionContext expression() throws RecognitionException {
    ExpressionContext _localctx = new ExpressionContext(_ctx, getState());
    enterRule(_localctx, 34, RULE_expression);
    int _la;
    try {
      setState(327);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,26,_ctx) ) {
      case 1:
        _localctx = new LambdavalContext(_localctx);
        enterOuterAlt(_localctx, 1);
        {
        setState(312);
        if (!(isLambdaAhead())) throw new FailedPredicateException(this, "isLambdaAhead()");
        setState(313);
        lambda();
        }
        break;
      case 2:
        _localctx = new FuncrefvalContext(_localctx);
        enterOuterAlt(_localctx, 2);
        {
        setState(314);
        if (!(isFunctionRefAhead())) throw new FailedPredicateException(this, "isFunctionRefAhead()");
        setState(315);
        funcref();
        }
        break;
      case 3:
        _localctx = new NonconditionalContext(_localctx);
        enterOuterAlt(_localctx, 3);
        {
        setState(316);
        noncondexpression(0);
        }
        break;
      case 4:
        _localctx = new ConditionalContext(_localctx);
        enterOuterAlt(_localctx, 4);
        {
        setState(317);
        noncondexpression(0);
        setState(318);
        match(COND);
        setState(319);
        expression();
        setState(320);
        match(COLON);
        setState(321);
        expression();
        }
        break;
      case 5:
        _localctx = new AssignmentContext(_localctx);
        enterOuterAlt(_localctx, 5);
        {
        setState(323);
        noncondexpression(0);
        setState(324);
        _la = _input.LA(1);
        if ( !(((((_la - 61)) & ~0x3f) == 0 && ((1L << (_la - 61)) & 4095L) != 0)) ) {
        _errHandler.recoverInline(this);
        }
        else {
          if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
          _errHandler.reportMatch(this);
          consume();
        }
        setState(325);
        expression();
        }
        break;
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class UnaryContext extends ParserRuleContext {
    public UnaryContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_unary; }
   
    public UnaryContext() { }
    public void copyFrom(UnaryContext ctx) {
      super.copyFrom(ctx);
    }
  }
  @SuppressWarnings("CheckReturnValue")
  public static class NotaddsubContext extends UnaryContext {
    public UnarynotaddsubContext unarynotaddsub() {
      return getRuleContext(UnarynotaddsubContext.class,0);
    }
    public NotaddsubContext(UnaryContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitNotaddsub(this);
      else return visitor.visitChildren(this);
    }
  }
  @SuppressWarnings("CheckReturnValue")
  public static class PreContext extends UnaryContext {
    public ChainContext chain() {
      return getRuleContext(ChainContext.class,0);
    }
    public TerminalNode INCR() { return getToken(JavascriptParser.INCR, 0); }
    public TerminalNode DECR() { return getToken(JavascriptParser.DECR, 0); }
    public PreContext(UnaryContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitPre(this);
      else return visitor.visitChildren(this);
    }
  }
  @SuppressWarnings("CheckReturnValue")
  public static class AddsubContext extends UnaryContext {
    public UnaryContext unary() {
      return getRuleContext(UnaryContext.class,0);
    }
    public TerminalNode ADD() { return getToken(JavascriptParser.ADD, 0); }
    public TerminalNode SUB() { return getToken(JavascriptParser.SUB, 0); }
    public AddsubContext(UnaryContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitAddsub(this);
      else return visitor.visitChildren(this);
    }
  }

  public final UnaryContext unary() throws RecognitionException {
    UnaryContext _localctx = new UnaryContext(_ctx, getState());
    enterRule(_localctx, 36, RULE_unary);
    int _la;
    try {
      setState(334);
      _errHandler.sync(this);
      switch (_input.LA(1)) {
      case INCR:
      case DECR:
        _localctx = new PreContext(_localctx);
        enterOuterAlt(_localctx, 1);
        {
        setState(329);
        _la = _input.LA(1);
        if ( !(_la==INCR || _la==DECR) ) {
        _errHandler.recoverInline(this);
        }
        else {
          if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
          _errHandler.reportMatch(this);
          consume();
        }
        setState(330);
        chain();
        }
        break;
      case ADD:
      case SUB:
        _localctx = new AddsubContext(_localctx);
        enterOuterAlt(_localctx, 2);
        {
        setState(331);
        _la = _input.LA(1);
        if ( !(_la==ADD || _la==SUB) ) {
        _errHandler.recoverInline(this);
        }
        else {
          if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
          _errHandler.reportMatch(this);
          consume();
        }
        setState(332);
        unary();
        }
        break;
      case LBRACE:
      case LP:
      case DOLLAR:
      case NEW:
      case BOOLNOT:
      case BWNOT:
      case OCTAL:
      case HEX:
      case INTEGER:
      case DECIMAL:
      case STRING:
      case REGEX:
      case TRUE:
      case FALSE:
      case NULL:
      case ID:
        _localctx = new NotaddsubContext(_localctx);
        enterOuterAlt(_localctx, 3);
        {
        setState(333);
        unarynotaddsub();
        }
        break;
      default:
        throw new NoViableAltException(this);
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class UnarynotaddsubContext extends ParserRuleContext {
    public UnarynotaddsubContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_unarynotaddsub; }
   
    public UnarynotaddsubContext() { }
    public void copyFrom(UnarynotaddsubContext ctx) {
      super.copyFrom(ctx);
    }
  }
  @SuppressWarnings("CheckReturnValue")
  public static class CastContext extends UnarynotaddsubContext {
    public CastexpressionContext castexpression() {
      return getRuleContext(CastexpressionContext.class,0);
    }
    public CastContext(UnarynotaddsubContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitCast(this);
      else return visitor.visitChildren(this);
    }
  }
  @SuppressWarnings("CheckReturnValue")
  public static class NotContext extends UnarynotaddsubContext {
    public UnaryContext unary() {
      return getRuleContext(UnaryContext.class,0);
    }
    public TerminalNode BOOLNOT() { return getToken(JavascriptParser.BOOLNOT, 0); }
    public TerminalNode BWNOT() { return getToken(JavascriptParser.BWNOT, 0); }
    public NotContext(UnarynotaddsubContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitNot(this);
      else return visitor.visitChildren(this);
    }
  }
  @SuppressWarnings("CheckReturnValue")
  public static class ReadContext extends UnarynotaddsubContext {
    public ChainContext chain() {
      return getRuleContext(ChainContext.class,0);
    }
    public ReadContext(UnarynotaddsubContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitRead(this);
      else return visitor.visitChildren(this);
    }
  }
  @SuppressWarnings("CheckReturnValue")
  public static class PostContext extends UnarynotaddsubContext {
    public ChainContext chain() {
      return getRuleContext(ChainContext.class,0);
    }
    public TerminalNode INCR() { return getToken(JavascriptParser.INCR, 0); }
    public TerminalNode DECR() { return getToken(JavascriptParser.DECR, 0); }
    public PostContext(UnarynotaddsubContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitPost(this);
      else return visitor.visitChildren(this);
    }
  }

  public final UnarynotaddsubContext unarynotaddsub() throws RecognitionException {
    UnarynotaddsubContext _localctx = new UnarynotaddsubContext(_ctx, getState());
    enterRule(_localctx, 38, RULE_unarynotaddsub);
    int _la;
    try {
      setState(343);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,28,_ctx) ) {
      case 1:
        _localctx = new ReadContext(_localctx);
        enterOuterAlt(_localctx, 1);
        {
        setState(336);
        chain();
        }
        break;
      case 2:
        _localctx = new PostContext(_localctx);
        enterOuterAlt(_localctx, 2);
        {
        setState(337);
        chain();
        setState(338);
        _la = _input.LA(1);
        if ( !(_la==INCR || _la==DECR) ) {
        _errHandler.recoverInline(this);
        }
        else {
          if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
          _errHandler.reportMatch(this);
          consume();
        }
        }
        break;
      case 3:
        _localctx = new NotContext(_localctx);
        enterOuterAlt(_localctx, 3);
        {
        setState(340);
        _la = _input.LA(1);
        if ( !(_la==BOOLNOT || _la==BWNOT) ) {
        _errHandler.recoverInline(this);
        }
        else {
          if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
          _errHandler.reportMatch(this);
          consume();
        }
        setState(341);
        unary();
        }
        break;
      case 4:
        _localctx = new CastContext(_localctx);
        enterOuterAlt(_localctx, 4);
        {
        setState(342);
        castexpression();
        }
        break;
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class CastexpressionContext extends ParserRuleContext {
    public CastexpressionContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_castexpression; }
   
    public CastexpressionContext() { }
    public void copyFrom(CastexpressionContext ctx) {
      super.copyFrom(ctx);
    }
  }
  @SuppressWarnings("CheckReturnValue")
  public static class RefcastContext extends CastexpressionContext {
    public TerminalNode LP() { return getToken(JavascriptParser.LP, 0); }
    public RefcasttypeContext refcasttype() {
      return getRuleContext(RefcasttypeContext.class,0);
    }
    public TerminalNode RP() { return getToken(JavascriptParser.RP, 0); }
    public UnarynotaddsubContext unarynotaddsub() {
      return getRuleContext(UnarynotaddsubContext.class,0);
    }
    public RefcastContext(CastexpressionContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitRefcast(this);
      else return visitor.visitChildren(this);
    }
  }
  @SuppressWarnings("CheckReturnValue")
  public static class PrimordefcastContext extends CastexpressionContext {
    public TerminalNode LP() { return getToken(JavascriptParser.LP, 0); }
    public PrimordefcasttypeContext primordefcasttype() {
      return getRuleContext(PrimordefcasttypeContext.class,0);
    }
    public TerminalNode RP() { return getToken(JavascriptParser.RP, 0); }
    public UnaryContext unary() {
      return getRuleContext(UnaryContext.class,0);
    }
    public PrimordefcastContext(CastexpressionContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitPrimordefcast(this);
      else return visitor.visitChildren(this);
    }
  }

  public final CastexpressionContext castexpression() throws RecognitionException {
    CastexpressionContext _localctx = new CastexpressionContext(_ctx, getState());
    enterRule(_localctx, 40, RULE_castexpression);
    try {
      setState(355);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,29,_ctx) ) {
      case 1:
        _localctx = new PrimordefcastContext(_localctx);
        enterOuterAlt(_localctx, 1);
        {
        setState(345);
        match(LP);
        setState(346);
        primordefcasttype();
        setState(347);
        match(RP);
        setState(348);
        unary();
        }
        break;
      case 2:
        _localctx = new RefcastContext(_localctx);
        enterOuterAlt(_localctx, 2);
        {
        setState(350);
        match(LP);
        setState(351);
        refcasttype();
        setState(352);
        match(RP);
        setState(353);
        unarynotaddsub();
        }
        break;
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class PrimordefcasttypeContext extends ParserRuleContext {
    public TerminalNode DEF() { return getToken(JavascriptParser.DEF, 0); }
    public TerminalNode PRIMITIVE() { return getToken(JavascriptParser.PRIMITIVE, 0); }
    public PrimordefcasttypeContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_primordefcasttype; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitPrimordefcasttype(this);
      else return visitor.visitChildren(this);
    }
  }

  public final PrimordefcasttypeContext primordefcasttype() throws RecognitionException {
    PrimordefcasttypeContext _localctx = new PrimordefcasttypeContext(_ctx, getState());
    enterRule(_localctx, 42, RULE_primordefcasttype);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(357);
      _la = _input.LA(1);
      if ( !(_la==PRIMITIVE || _la==DEF) ) {
      _errHandler.recoverInline(this);
      }
      else {
        if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
        _errHandler.reportMatch(this);
        consume();
      }
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class RefcasttypeContext extends ParserRuleContext {
    public TerminalNode DEF() { return getToken(JavascriptParser.DEF, 0); }
    public List<TerminalNode> LBRACE() { return getTokens(JavascriptParser.LBRACE); }
    public TerminalNode LBRACE(int i) {
      return getToken(JavascriptParser.LBRACE, i);
    }
    public List<TerminalNode> RBRACE() { return getTokens(JavascriptParser.RBRACE); }
    public TerminalNode RBRACE(int i) {
      return getToken(JavascriptParser.RBRACE, i);
    }
    public TerminalNode PRIMITIVE() { return getToken(JavascriptParser.PRIMITIVE, 0); }
    public TerminalNode ID() { return getToken(JavascriptParser.ID, 0); }
    public List<TerminalNode> DOT() { return getTokens(JavascriptParser.DOT); }
    public TerminalNode DOT(int i) {
      return getToken(JavascriptParser.DOT, i);
    }
    public List<TerminalNode> DOTID() { return getTokens(JavascriptParser.DOTID); }
    public TerminalNode DOTID(int i) {
      return getToken(JavascriptParser.DOTID, i);
    }
    public RefcasttypeContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_refcasttype; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitRefcasttype(this);
      else return visitor.visitChildren(this);
    }
  }

  public final RefcasttypeContext refcasttype() throws RecognitionException {
    RefcasttypeContext _localctx = new RefcasttypeContext(_ctx, getState());
    enterRule(_localctx, 44, RULE_refcasttype);
    int _la;
    try {
      setState(388);
      _errHandler.sync(this);
      switch (_input.LA(1)) {
      case DEF:
        enterOuterAlt(_localctx, 1);
        {
        setState(359);
        match(DEF);
        setState(362); 
        _errHandler.sync(this);
        _la = _input.LA(1);
        do {
          {
          {
          setState(360);
          match(LBRACE);
          setState(361);
          match(RBRACE);
          }
          }
          setState(364); 
          _errHandler.sync(this);
          _la = _input.LA(1);
        } while ( _la==LBRACE );
        }
        break;
      case PRIMITIVE:
        enterOuterAlt(_localctx, 2);
        {
        setState(366);
        match(PRIMITIVE);
        setState(369); 
        _errHandler.sync(this);
        _la = _input.LA(1);
        do {
          {
          {
          setState(367);
          match(LBRACE);
          setState(368);
          match(RBRACE);
          }
          }
          setState(371); 
          _errHandler.sync(this);
          _la = _input.LA(1);
        } while ( _la==LBRACE );
        }
        break;
      case ID:
        enterOuterAlt(_localctx, 3);
        {
        setState(373);
        match(ID);
        setState(378);
        _errHandler.sync(this);
        _la = _input.LA(1);
        while (_la==DOT) {
          {
          {
          setState(374);
          match(DOT);
          setState(375);
          match(DOTID);
          }
          }
          setState(380);
          _errHandler.sync(this);
          _la = _input.LA(1);
        }
        setState(385);
        _errHandler.sync(this);
        _la = _input.LA(1);
        while (_la==LBRACE) {
          {
          {
          setState(381);
          match(LBRACE);
          setState(382);
          match(RBRACE);
          }
          }
          setState(387);
          _errHandler.sync(this);
          _la = _input.LA(1);
        }
        }
        break;
      default:
        throw new NoViableAltException(this);
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class ChainContext extends ParserRuleContext {
    public ChainContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_chain; }
   
    public ChainContext() { }
    public void copyFrom(ChainContext ctx) {
      super.copyFrom(ctx);
    }
  }
  @SuppressWarnings("CheckReturnValue")
  public static class DynamicContext extends ChainContext {
    public PrimaryContext primary() {
      return getRuleContext(PrimaryContext.class,0);
    }
    public List<PostfixContext> postfix() {
      return getRuleContexts(PostfixContext.class);
    }
    public PostfixContext postfix(int i) {
      return getRuleContext(PostfixContext.class,i);
    }
    public DynamicContext(ChainContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitDynamic(this);
      else return visitor.visitChildren(this);
    }
  }
  @SuppressWarnings("CheckReturnValue")
  public static class NewarrayContext extends ChainContext {
    public ArrayinitializerContext arrayinitializer() {
      return getRuleContext(ArrayinitializerContext.class,0);
    }
    public NewarrayContext(ChainContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitNewarray(this);
      else return visitor.visitChildren(this);
    }
  }

  public final ChainContext chain() throws RecognitionException {
    ChainContext _localctx = new ChainContext(_ctx, getState());
    enterRule(_localctx, 46, RULE_chain);
    try {
      int _alt;
      setState(398);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,36,_ctx) ) {
      case 1:
        _localctx = new DynamicContext(_localctx);
        enterOuterAlt(_localctx, 1);
        {
        setState(390);
        primary();
        setState(394);
        _errHandler.sync(this);
        _alt = getInterpreter().adaptivePredict(_input,35,_ctx);
        while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
          if ( _alt==1 ) {
            {
            {
            setState(391);
            postfix();
            }
            } 
          }
          setState(396);
          _errHandler.sync(this);
          _alt = getInterpreter().adaptivePredict(_input,35,_ctx);
        }
        }
        break;
      case 2:
        _localctx = new NewarrayContext(_localctx);
        enterOuterAlt(_localctx, 2);
        {
        setState(397);
        arrayinitializer();
        }
        break;
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class PrimaryContext extends ParserRuleContext {
    public PrimaryContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_primary; }
   
    public PrimaryContext() { }
    public void copyFrom(PrimaryContext ctx) {
      super.copyFrom(ctx);
    }
  }
  @SuppressWarnings("CheckReturnValue")
  public static class ListinitContext extends PrimaryContext {
    public ListinitializerContext listinitializer() {
      return getRuleContext(ListinitializerContext.class,0);
    }
    public ListinitContext(PrimaryContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitListinit(this);
      else return visitor.visitChildren(this);
    }
  }
  @SuppressWarnings("CheckReturnValue")
  public static class RegexContext extends PrimaryContext {
    public TerminalNode REGEX() { return getToken(JavascriptParser.REGEX, 0); }
    public RegexContext(PrimaryContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitRegex(this);
      else return visitor.visitChildren(this);
    }
  }
  @SuppressWarnings("CheckReturnValue")
  public static class NullContext extends PrimaryContext {
    public TerminalNode NULL() { return getToken(JavascriptParser.NULL, 0); }
    public NullContext(PrimaryContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitNull(this);
      else return visitor.visitChildren(this);
    }
  }
  @SuppressWarnings("CheckReturnValue")
  public static class StringContext extends PrimaryContext {
    public TerminalNode STRING() { return getToken(JavascriptParser.STRING, 0); }
    public StringContext(PrimaryContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitString(this);
      else return visitor.visitChildren(this);
    }
  }
  @SuppressWarnings("CheckReturnValue")
  public static class MapinitContext extends PrimaryContext {
    public MapinitializerContext mapinitializer() {
      return getRuleContext(MapinitializerContext.class,0);
    }
    public MapinitContext(PrimaryContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitMapinit(this);
      else return visitor.visitChildren(this);
    }
  }
  @SuppressWarnings("CheckReturnValue")
  public static class CalllocalContext extends PrimaryContext {
    public ArgumentsContext arguments() {
      return getRuleContext(ArgumentsContext.class,0);
    }
    public TerminalNode ID() { return getToken(JavascriptParser.ID, 0); }
    public TerminalNode DOLLAR() { return getToken(JavascriptParser.DOLLAR, 0); }
    public CalllocalContext(PrimaryContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitCalllocal(this);
      else return visitor.visitChildren(this);
    }
  }
  @SuppressWarnings("CheckReturnValue")
  public static class TrueContext extends PrimaryContext {
    public TerminalNode TRUE() { return getToken(JavascriptParser.TRUE, 0); }
    public TrueContext(PrimaryContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitTrue(this);
      else return visitor.visitChildren(this);
    }
  }
  @SuppressWarnings("CheckReturnValue")
  public static class FalseContext extends PrimaryContext {
    public TerminalNode FALSE() { return getToken(JavascriptParser.FALSE, 0); }
    public FalseContext(PrimaryContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitFalse(this);
      else return visitor.visitChildren(this);
    }
  }
  @SuppressWarnings("CheckReturnValue")
  public static class VariableContext extends PrimaryContext {
    public TerminalNode ID() { return getToken(JavascriptParser.ID, 0); }
    public VariableContext(PrimaryContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitVariable(this);
      else return visitor.visitChildren(this);
    }
  }
  @SuppressWarnings("CheckReturnValue")
  public static class NumericContext extends PrimaryContext {
    public TerminalNode OCTAL() { return getToken(JavascriptParser.OCTAL, 0); }
    public TerminalNode HEX() { return getToken(JavascriptParser.HEX, 0); }
    public TerminalNode INTEGER() { return getToken(JavascriptParser.INTEGER, 0); }
    public TerminalNode DECIMAL() { return getToken(JavascriptParser.DECIMAL, 0); }
    public NumericContext(PrimaryContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitNumeric(this);
      else return visitor.visitChildren(this);
    }
  }
  @SuppressWarnings("CheckReturnValue")
  public static class NewobjectContext extends PrimaryContext {
    public TerminalNode NEW() { return getToken(JavascriptParser.NEW, 0); }
    public TypeContext type() {
      return getRuleContext(TypeContext.class,0);
    }
    public ArgumentsContext arguments() {
      return getRuleContext(ArgumentsContext.class,0);
    }
    public NewobjectContext(PrimaryContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitNewobject(this);
      else return visitor.visitChildren(this);
    }
  }
  @SuppressWarnings("CheckReturnValue")
  public static class PrecedenceContext extends PrimaryContext {
    public TerminalNode LP() { return getToken(JavascriptParser.LP, 0); }
    public ExpressionContext expression() {
      return getRuleContext(ExpressionContext.class,0);
    }
    public TerminalNode RP() { return getToken(JavascriptParser.RP, 0); }
    public PrecedenceContext(PrimaryContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitPrecedence(this);
      else return visitor.visitChildren(this);
    }
  }

  public final PrimaryContext primary() throws RecognitionException {
    PrimaryContext _localctx = new PrimaryContext(_ctx, getState());
    enterRule(_localctx, 48, RULE_primary);
    int _la;
    try {
      setState(419);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,37,_ctx) ) {
      case 1:
        _localctx = new PrecedenceContext(_localctx);
        enterOuterAlt(_localctx, 1);
        {
        setState(400);
        match(LP);
        setState(401);
        expression();
        setState(402);
        match(RP);
        }
        break;
      case 2:
        _localctx = new NumericContext(_localctx);
        enterOuterAlt(_localctx, 2);
        {
        setState(404);
        _la = _input.LA(1);
        if ( !(((((_la - 73)) & ~0x3f) == 0 && ((1L << (_la - 73)) & 15L) != 0)) ) {
        _errHandler.recoverInline(this);
        }
        else {
          if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
          _errHandler.reportMatch(this);
          consume();
        }
        }
        break;
      case 3:
        _localctx = new TrueContext(_localctx);
        enterOuterAlt(_localctx, 3);
        {
        setState(405);
        match(TRUE);
        }
        break;
      case 4:
        _localctx = new FalseContext(_localctx);
        enterOuterAlt(_localctx, 4);
        {
        setState(406);
        match(FALSE);
        }
        break;
      case 5:
        _localctx = new NullContext(_localctx);
        enterOuterAlt(_localctx, 5);
        {
        setState(407);
        match(NULL);
        }
        break;
      case 6:
        _localctx = new StringContext(_localctx);
        enterOuterAlt(_localctx, 6);
        {
        setState(408);
        match(STRING);
        }
        break;
      case 7:
        _localctx = new RegexContext(_localctx);
        enterOuterAlt(_localctx, 7);
        {
        setState(409);
        match(REGEX);
        }
        break;
      case 8:
        _localctx = new ListinitContext(_localctx);
        enterOuterAlt(_localctx, 8);
        {
        setState(410);
        listinitializer();
        }
        break;
      case 9:
        _localctx = new MapinitContext(_localctx);
        enterOuterAlt(_localctx, 9);
        {
        setState(411);
        mapinitializer();
        }
        break;
      case 10:
        _localctx = new VariableContext(_localctx);
        enterOuterAlt(_localctx, 10);
        {
        setState(412);
        match(ID);
        }
        break;
      case 11:
        _localctx = new CalllocalContext(_localctx);
        enterOuterAlt(_localctx, 11);
        {
        setState(413);
        _la = _input.LA(1);
        if ( !(_la==DOLLAR || _la==ID) ) {
        _errHandler.recoverInline(this);
        }
        else {
          if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
          _errHandler.reportMatch(this);
          consume();
        }
        setState(414);
        arguments();
        }
        break;
      case 12:
        _localctx = new NewobjectContext(_localctx);
        enterOuterAlt(_localctx, 12);
        {
        setState(415);
        match(NEW);
        setState(416);
        type();
        setState(417);
        arguments();
        }
        break;
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class PostfixContext extends ParserRuleContext {
    public CallinvokeContext callinvoke() {
      return getRuleContext(CallinvokeContext.class,0);
    }
    public FieldaccessContext fieldaccess() {
      return getRuleContext(FieldaccessContext.class,0);
    }
    public BraceaccessContext braceaccess() {
      return getRuleContext(BraceaccessContext.class,0);
    }
    public PostfixContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_postfix; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitPostfix(this);
      else return visitor.visitChildren(this);
    }
  }

  public final PostfixContext postfix() throws RecognitionException {
    PostfixContext _localctx = new PostfixContext(_ctx, getState());
    enterRule(_localctx, 50, RULE_postfix);
    try {
      setState(424);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,38,_ctx) ) {
      case 1:
        enterOuterAlt(_localctx, 1);
        {
        setState(421);
        callinvoke();
        }
        break;
      case 2:
        enterOuterAlt(_localctx, 2);
        {
        setState(422);
        fieldaccess();
        }
        break;
      case 3:
        enterOuterAlt(_localctx, 3);
        {
        setState(423);
        braceaccess();
        }
        break;
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class PostdotContext extends ParserRuleContext {
    public CallinvokeContext callinvoke() {
      return getRuleContext(CallinvokeContext.class,0);
    }
    public FieldaccessContext fieldaccess() {
      return getRuleContext(FieldaccessContext.class,0);
    }
    public PostdotContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_postdot; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitPostdot(this);
      else return visitor.visitChildren(this);
    }
  }

  public final PostdotContext postdot() throws RecognitionException {
    PostdotContext _localctx = new PostdotContext(_ctx, getState());
    enterRule(_localctx, 52, RULE_postdot);
    try {
      setState(428);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,39,_ctx) ) {
      case 1:
        enterOuterAlt(_localctx, 1);
        {
        setState(426);
        callinvoke();
        }
        break;
      case 2:
        enterOuterAlt(_localctx, 2);
        {
        setState(427);
        fieldaccess();
        }
        break;
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class CallinvokeContext extends ParserRuleContext {
    public TerminalNode DOTID() { return getToken(JavascriptParser.DOTID, 0); }
    public ArgumentsContext arguments() {
      return getRuleContext(ArgumentsContext.class,0);
    }
    public TerminalNode DOT() { return getToken(JavascriptParser.DOT, 0); }
    public TerminalNode NSDOT() { return getToken(JavascriptParser.NSDOT, 0); }
    public CallinvokeContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_callinvoke; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitCallinvoke(this);
      else return visitor.visitChildren(this);
    }
  }

  public final CallinvokeContext callinvoke() throws RecognitionException {
    CallinvokeContext _localctx = new CallinvokeContext(_ctx, getState());
    enterRule(_localctx, 54, RULE_callinvoke);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(430);
      _la = _input.LA(1);
      if ( !(_la==DOT || _la==NSDOT) ) {
      _errHandler.recoverInline(this);
      }
      else {
        if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
        _errHandler.reportMatch(this);
        consume();
      }
      setState(431);
      match(DOTID);
      setState(432);
      arguments();
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class FieldaccessContext extends ParserRuleContext {
    public TerminalNode DOT() { return getToken(JavascriptParser.DOT, 0); }
    public TerminalNode NSDOT() { return getToken(JavascriptParser.NSDOT, 0); }
    public TerminalNode DOTID() { return getToken(JavascriptParser.DOTID, 0); }
    public TerminalNode DOTINTEGER() { return getToken(JavascriptParser.DOTINTEGER, 0); }
    public FieldaccessContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_fieldaccess; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitFieldaccess(this);
      else return visitor.visitChildren(this);
    }
  }

  public final FieldaccessContext fieldaccess() throws RecognitionException {
    FieldaccessContext _localctx = new FieldaccessContext(_ctx, getState());
    enterRule(_localctx, 56, RULE_fieldaccess);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(434);
      _la = _input.LA(1);
      if ( !(_la==DOT || _la==NSDOT) ) {
      _errHandler.recoverInline(this);
      }
      else {
        if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
        _errHandler.reportMatch(this);
        consume();
      }
      setState(435);
      _la = _input.LA(1);
      if ( !(_la==DOTINTEGER || _la==DOTID) ) {
      _errHandler.recoverInline(this);
      }
      else {
        if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
        _errHandler.reportMatch(this);
        consume();
      }
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class BraceaccessContext extends ParserRuleContext {
    public TerminalNode LBRACE() { return getToken(JavascriptParser.LBRACE, 0); }
    public ExpressionContext expression() {
      return getRuleContext(ExpressionContext.class,0);
    }
    public TerminalNode RBRACE() { return getToken(JavascriptParser.RBRACE, 0); }
    public BraceaccessContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_braceaccess; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitBraceaccess(this);
      else return visitor.visitChildren(this);
    }
  }

  public final BraceaccessContext braceaccess() throws RecognitionException {
    BraceaccessContext _localctx = new BraceaccessContext(_ctx, getState());
    enterRule(_localctx, 58, RULE_braceaccess);
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(437);
      match(LBRACE);
      setState(438);
      expression();
      setState(439);
      match(RBRACE);
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class ArrayinitializerContext extends ParserRuleContext {
    public ArrayinitializerContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_arrayinitializer; }
   
    public ArrayinitializerContext() { }
    public void copyFrom(ArrayinitializerContext ctx) {
      super.copyFrom(ctx);
    }
  }
  @SuppressWarnings("CheckReturnValue")
  public static class NewstandardarrayContext extends ArrayinitializerContext {
    public TerminalNode NEW() { return getToken(JavascriptParser.NEW, 0); }
    public TypeContext type() {
      return getRuleContext(TypeContext.class,0);
    }
    public List<TerminalNode> LBRACE() { return getTokens(JavascriptParser.LBRACE); }
    public TerminalNode LBRACE(int i) {
      return getToken(JavascriptParser.LBRACE, i);
    }
    public List<ExpressionContext> expression() {
      return getRuleContexts(ExpressionContext.class);
    }
    public ExpressionContext expression(int i) {
      return getRuleContext(ExpressionContext.class,i);
    }
    public List<TerminalNode> RBRACE() { return getTokens(JavascriptParser.RBRACE); }
    public TerminalNode RBRACE(int i) {
      return getToken(JavascriptParser.RBRACE, i);
    }
    public PostdotContext postdot() {
      return getRuleContext(PostdotContext.class,0);
    }
    public List<PostfixContext> postfix() {
      return getRuleContexts(PostfixContext.class);
    }
    public PostfixContext postfix(int i) {
      return getRuleContext(PostfixContext.class,i);
    }
    public NewstandardarrayContext(ArrayinitializerContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitNewstandardarray(this);
      else return visitor.visitChildren(this);
    }
  }
  @SuppressWarnings("CheckReturnValue")
  public static class NewinitializedarrayContext extends ArrayinitializerContext {
    public TerminalNode NEW() { return getToken(JavascriptParser.NEW, 0); }
    public TypeContext type() {
      return getRuleContext(TypeContext.class,0);
    }
    public TerminalNode LBRACE() { return getToken(JavascriptParser.LBRACE, 0); }
    public TerminalNode RBRACE() { return getToken(JavascriptParser.RBRACE, 0); }
    public TerminalNode LBRACK() { return getToken(JavascriptParser.LBRACK, 0); }
    public TerminalNode RBRACK() { return getToken(JavascriptParser.RBRACK, 0); }
    public List<ExpressionContext> expression() {
      return getRuleContexts(ExpressionContext.class);
    }
    public ExpressionContext expression(int i) {
      return getRuleContext(ExpressionContext.class,i);
    }
    public List<PostfixContext> postfix() {
      return getRuleContexts(PostfixContext.class);
    }
    public PostfixContext postfix(int i) {
      return getRuleContext(PostfixContext.class,i);
    }
    public List<TerminalNode> COMMA() { return getTokens(JavascriptParser.COMMA); }
    public TerminalNode COMMA(int i) {
      return getToken(JavascriptParser.COMMA, i);
    }
    public NewinitializedarrayContext(ArrayinitializerContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitNewinitializedarray(this);
      else return visitor.visitChildren(this);
    }
  }

  public final ArrayinitializerContext arrayinitializer() throws RecognitionException {
    ArrayinitializerContext _localctx = new ArrayinitializerContext(_ctx, getState());
    enterRule(_localctx, 60, RULE_arrayinitializer);
    int _la;
    try {
      int _alt;
      setState(482);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,46,_ctx) ) {
      case 1:
        _localctx = new NewstandardarrayContext(_localctx);
        enterOuterAlt(_localctx, 1);
        {
        setState(441);
        match(NEW);
        setState(442);
        type();
        setState(447); 
        _errHandler.sync(this);
        _alt = 1;
        do {
          switch (_alt) {
          case 1:
            {
            {
            setState(443);
            match(LBRACE);
            setState(444);
            expression();
            setState(445);
            match(RBRACE);
            }
            }
            break;
          default:
            throw new NoViableAltException(this);
          }
          setState(449); 
          _errHandler.sync(this);
          _alt = getInterpreter().adaptivePredict(_input,40,_ctx);
        } while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
        setState(458);
        _errHandler.sync(this);
        switch ( getInterpreter().adaptivePredict(_input,42,_ctx) ) {
        case 1:
          {
          setState(451);
          postdot();
          setState(455);
          _errHandler.sync(this);
          _alt = getInterpreter().adaptivePredict(_input,41,_ctx);
          while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
            if ( _alt==1 ) {
              {
              {
              setState(452);
              postfix();
              }
              } 
            }
            setState(457);
            _errHandler.sync(this);
            _alt = getInterpreter().adaptivePredict(_input,41,_ctx);
          }
          }
          break;
        }
        }
        break;
      case 2:
        _localctx = new NewinitializedarrayContext(_localctx);
        enterOuterAlt(_localctx, 2);
        {
        setState(460);
        match(NEW);
        setState(461);
        type();
        setState(462);
        match(LBRACE);
        setState(463);
        match(RBRACE);
        setState(464);
        match(LBRACK);
        setState(473);
        _errHandler.sync(this);
        switch ( getInterpreter().adaptivePredict(_input,44,_ctx) ) {
        case 1:
          {
          setState(465);
          expression();
          setState(470);
          _errHandler.sync(this);
          _la = _input.LA(1);
          while (_la==COMMA) {
            {
            {
            setState(466);
            match(COMMA);
            setState(467);
            expression();
            }
            }
            setState(472);
            _errHandler.sync(this);
            _la = _input.LA(1);
          }
          }
          break;
        }
        setState(475);
        match(RBRACK);
        setState(479);
        _errHandler.sync(this);
        _alt = getInterpreter().adaptivePredict(_input,45,_ctx);
        while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
          if ( _alt==1 ) {
            {
            {
            setState(476);
            postfix();
            }
            } 
          }
          setState(481);
          _errHandler.sync(this);
          _alt = getInterpreter().adaptivePredict(_input,45,_ctx);
        }
        }
        break;
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class ListinitializerContext extends ParserRuleContext {
    public TerminalNode LBRACE() { return getToken(JavascriptParser.LBRACE, 0); }
    public List<ExpressionContext> expression() {
      return getRuleContexts(ExpressionContext.class);
    }
    public ExpressionContext expression(int i) {
      return getRuleContext(ExpressionContext.class,i);
    }
    public TerminalNode RBRACE() { return getToken(JavascriptParser.RBRACE, 0); }
    public List<TerminalNode> COMMA() { return getTokens(JavascriptParser.COMMA); }
    public TerminalNode COMMA(int i) {
      return getToken(JavascriptParser.COMMA, i);
    }
    public ListinitializerContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_listinitializer; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitListinitializer(this);
      else return visitor.visitChildren(this);
    }
  }

  public final ListinitializerContext listinitializer() throws RecognitionException {
    ListinitializerContext _localctx = new ListinitializerContext(_ctx, getState());
    enterRule(_localctx, 62, RULE_listinitializer);
    int _la;
    try {
      setState(497);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,48,_ctx) ) {
      case 1:
        enterOuterAlt(_localctx, 1);
        {
        setState(484);
        match(LBRACE);
        setState(485);
        expression();
        setState(490);
        _errHandler.sync(this);
        _la = _input.LA(1);
        while (_la==COMMA) {
          {
          {
          setState(486);
          match(COMMA);
          setState(487);
          expression();
          }
          }
          setState(492);
          _errHandler.sync(this);
          _la = _input.LA(1);
        }
        setState(493);
        match(RBRACE);
        }
        break;
      case 2:
        enterOuterAlt(_localctx, 2);
        {
        setState(495);
        match(LBRACE);
        setState(496);
        match(RBRACE);
        }
        break;
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class MapinitializerContext extends ParserRuleContext {
    public TerminalNode LBRACE() { return getToken(JavascriptParser.LBRACE, 0); }
    public List<MaptokenContext> maptoken() {
      return getRuleContexts(MaptokenContext.class);
    }
    public MaptokenContext maptoken(int i) {
      return getRuleContext(MaptokenContext.class,i);
    }
    public TerminalNode RBRACE() { return getToken(JavascriptParser.RBRACE, 0); }
    public List<TerminalNode> COMMA() { return getTokens(JavascriptParser.COMMA); }
    public TerminalNode COMMA(int i) {
      return getToken(JavascriptParser.COMMA, i);
    }
    public TerminalNode COLON() { return getToken(JavascriptParser.COLON, 0); }
    public MapinitializerContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_mapinitializer; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitMapinitializer(this);
      else return visitor.visitChildren(this);
    }
  }

  public final MapinitializerContext mapinitializer() throws RecognitionException {
    MapinitializerContext _localctx = new MapinitializerContext(_ctx, getState());
    enterRule(_localctx, 64, RULE_mapinitializer);
    int _la;
    try {
      setState(513);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,50,_ctx) ) {
      case 1:
        enterOuterAlt(_localctx, 1);
        {
        setState(499);
        match(LBRACE);
        setState(500);
        maptoken();
        setState(505);
        _errHandler.sync(this);
        _la = _input.LA(1);
        while (_la==COMMA) {
          {
          {
          setState(501);
          match(COMMA);
          setState(502);
          maptoken();
          }
          }
          setState(507);
          _errHandler.sync(this);
          _la = _input.LA(1);
        }
        setState(508);
        match(RBRACE);
        }
        break;
      case 2:
        enterOuterAlt(_localctx, 2);
        {
        setState(510);
        match(LBRACE);
        setState(511);
        match(COLON);
        setState(512);
        match(RBRACE);
        }
        break;
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class MaptokenContext extends ParserRuleContext {
    public List<ExpressionContext> expression() {
      return getRuleContexts(ExpressionContext.class);
    }
    public ExpressionContext expression(int i) {
      return getRuleContext(ExpressionContext.class,i);
    }
    public TerminalNode COLON() { return getToken(JavascriptParser.COLON, 0); }
    public MaptokenContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_maptoken; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitMaptoken(this);
      else return visitor.visitChildren(this);
    }
  }

  public final MaptokenContext maptoken() throws RecognitionException {
    MaptokenContext _localctx = new MaptokenContext(_ctx, getState());
    enterRule(_localctx, 66, RULE_maptoken);
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(515);
      expression();
      setState(516);
      match(COLON);
      setState(517);
      expression();
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class ArgumentsContext extends ParserRuleContext {
    public TerminalNode LP() { return getToken(JavascriptParser.LP, 0); }
    public TerminalNode RP() { return getToken(JavascriptParser.RP, 0); }
    public List<ArgumentContext> argument() {
      return getRuleContexts(ArgumentContext.class);
    }
    public ArgumentContext argument(int i) {
      return getRuleContext(ArgumentContext.class,i);
    }
    public List<TerminalNode> COMMA() { return getTokens(JavascriptParser.COMMA); }
    public TerminalNode COMMA(int i) {
      return getToken(JavascriptParser.COMMA, i);
    }
    public ArgumentsContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_arguments; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitArguments(this);
      else return visitor.visitChildren(this);
    }
  }

  public final ArgumentsContext arguments() throws RecognitionException {
    ArgumentsContext _localctx = new ArgumentsContext(_ctx, getState());
    enterRule(_localctx, 68, RULE_arguments);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
      {
      setState(519);
      match(LP);
      setState(528);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,52,_ctx) ) {
      case 1:
        {
        setState(520);
        argument();
        setState(525);
        _errHandler.sync(this);
        _la = _input.LA(1);
        while (_la==COMMA) {
          {
          {
          setState(521);
          match(COMMA);
          setState(522);
          argument();
          }
          }
          setState(527);
          _errHandler.sync(this);
          _la = _input.LA(1);
        }
        }
        break;
      }
      setState(530);
      match(RP);
      }
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class ArgumentContext extends ParserRuleContext {
    public ExpressionContext expression() {
      return getRuleContext(ExpressionContext.class,0);
    }
    public ArgumentContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_argument; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitArgument(this);
      else return visitor.visitChildren(this);
    }
  }

  public final ArgumentContext argument() throws RecognitionException {
    ArgumentContext _localctx = new ArgumentContext(_ctx, getState());
    enterRule(_localctx, 70, RULE_argument);
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(532);
      expression();
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class LambdaContext extends ParserRuleContext {
    public TerminalNode ARROW() { return getToken(JavascriptParser.ARROW, 0); }
    public List<LamtypeContext> lamtype() {
      return getRuleContexts(LamtypeContext.class);
    }
    public LamtypeContext lamtype(int i) {
      return getRuleContext(LamtypeContext.class,i);
    }
    public TerminalNode LP() { return getToken(JavascriptParser.LP, 0); }
    public TerminalNode RP() { return getToken(JavascriptParser.RP, 0); }
    public BlockContext block() {
      return getRuleContext(BlockContext.class,0);
    }
    public ExpressionContext expression() {
      return getRuleContext(ExpressionContext.class,0);
    }
    public List<TerminalNode> COMMA() { return getTokens(JavascriptParser.COMMA); }
    public TerminalNode COMMA(int i) {
      return getToken(JavascriptParser.COMMA, i);
    }
    public LambdaContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_lambda; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitLambda(this);
      else return visitor.visitChildren(this);
    }
  }

  public final LambdaContext lambda() throws RecognitionException {
    LambdaContext _localctx = new LambdaContext(_ctx, getState());
    enterRule(_localctx, 72, RULE_lambda);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(547);
      _errHandler.sync(this);
      switch (_input.LA(1)) {
      case PRIMITIVE:
      case DEF:
      case ID:
        {
        setState(534);
        lamtype();
        }
        break;
      case LP:
        {
        setState(535);
        match(LP);
        setState(544);
        _errHandler.sync(this);
        _la = _input.LA(1);
        if (((((_la - 82)) & ~0x3f) == 0 && ((1L << (_la - 82)) & 7L) != 0)) {
          {
          setState(536);
          lamtype();
          setState(541);
          _errHandler.sync(this);
          _la = _input.LA(1);
          while (_la==COMMA) {
            {
            {
            setState(537);
            match(COMMA);
            setState(538);
            lamtype();
            }
            }
            setState(543);
            _errHandler.sync(this);
            _la = _input.LA(1);
          }
          }
        }

        setState(546);
        match(RP);
        }
        break;
      default:
        throw new NoViableAltException(this);
      }
      setState(549);
      match(ARROW);
      setState(552);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,56,_ctx) ) {
      case 1:
        {
        setState(550);
        block();
        }
        break;
      case 2:
        {
        setState(551);
        expression();
        }
        break;
      }
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class LamtypeContext extends ParserRuleContext {
    public TerminalNode ID() { return getToken(JavascriptParser.ID, 0); }
    public DecltypeContext decltype() {
      return getRuleContext(DecltypeContext.class,0);
    }
    public LamtypeContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_lamtype; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitLamtype(this);
      else return visitor.visitChildren(this);
    }
  }

  public final LamtypeContext lamtype() throws RecognitionException {
    LamtypeContext _localctx = new LamtypeContext(_ctx, getState());
    enterRule(_localctx, 74, RULE_lamtype);
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(555);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,57,_ctx) ) {
      case 1:
        {
        setState(554);
        decltype();
        }
        break;
      }
      setState(557);
      match(ID);
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class FuncrefContext extends ParserRuleContext {
    public FuncrefContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_funcref; }
   
    public FuncrefContext() { }
    public void copyFrom(FuncrefContext ctx) {
      super.copyFrom(ctx);
    }
  }
  @SuppressWarnings("CheckReturnValue")
  public static class ClassfuncrefContext extends FuncrefContext {
    public DecltypeContext decltype() {
      return getRuleContext(DecltypeContext.class,0);
    }
    public TerminalNode REF() { return getToken(JavascriptParser.REF, 0); }
    public TerminalNode ID() { return getToken(JavascriptParser.ID, 0); }
    public ClassfuncrefContext(FuncrefContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitClassfuncref(this);
      else return visitor.visitChildren(this);
    }
  }
  @SuppressWarnings("CheckReturnValue")
  public static class ConstructorfuncrefContext extends FuncrefContext {
    public DecltypeContext decltype() {
      return getRuleContext(DecltypeContext.class,0);
    }
    public TerminalNode REF() { return getToken(JavascriptParser.REF, 0); }
    public TerminalNode NEW() { return getToken(JavascriptParser.NEW, 0); }
    public ConstructorfuncrefContext(FuncrefContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitConstructorfuncref(this);
      else return visitor.visitChildren(this);
    }
  }
  @SuppressWarnings("CheckReturnValue")
  public static class LocalfuncrefContext extends FuncrefContext {
    public TerminalNode THIS() { return getToken(JavascriptParser.THIS, 0); }
    public TerminalNode REF() { return getToken(JavascriptParser.REF, 0); }
    public TerminalNode ID() { return getToken(JavascriptParser.ID, 0); }
    public LocalfuncrefContext(FuncrefContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof JavascriptParserVisitor ) return ((JavascriptParserVisitor<? extends T>)visitor).visitLocalfuncref(this);
      else return visitor.visitChildren(this);
    }
  }

  public final FuncrefContext funcref() throws RecognitionException {
    FuncrefContext _localctx = new FuncrefContext(_ctx, getState());
    enterRule(_localctx, 76, RULE_funcref);
    try {
      setState(570);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,58,_ctx) ) {
      case 1:
        _localctx = new ClassfuncrefContext(_localctx);
        enterOuterAlt(_localctx, 1);
        {
        setState(559);
        decltype();
        setState(560);
        match(REF);
        setState(561);
        match(ID);
        }
        break;
      case 2:
        _localctx = new ConstructorfuncrefContext(_localctx);
        enterOuterAlt(_localctx, 2);
        {
        setState(563);
        decltype();
        setState(564);
        match(REF);
        setState(565);
        match(NEW);
        }
        break;
      case 3:
        _localctx = new LocalfuncrefContext(_localctx);
        enterOuterAlt(_localctx, 3);
        {
        setState(567);
        match(THIS);
        setState(568);
        match(REF);
        setState(569);
        match(ID);
        }
        break;
      }
    }
    catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
    switch (ruleIndex) {
    case 4:
      return rstatement_sempred((RstatementContext)_localctx, predIndex);
    case 16:
      return noncondexpression_sempred((NoncondexpressionContext)_localctx, predIndex);
    case 17:
      return expression_sempred((ExpressionContext)_localctx, predIndex);
    }
    return true;
  }
  private boolean rstatement_sempred(RstatementContext _localctx, int predIndex) {
    switch (predIndex) {
    case 0:
      return  _input.LA(1) != ELSE ;
    }
    return true;
  }
  private boolean noncondexpression_sempred(NoncondexpressionContext _localctx, int predIndex) {
    switch (predIndex) {
    case 1:
      return precpred(_ctx, 13);
    case 2:
      return precpred(_ctx, 12);
    case 3:
      return precpred(_ctx, 11);
    case 4:
      return precpred(_ctx, 10);
    case 5:
      return precpred(_ctx, 9);
    case 6:
      return precpred(_ctx, 7);
    case 7:
      return precpred(_ctx, 6);
    case 8:
      return precpred(_ctx, 5);
    case 9:
      return precpred(_ctx, 4);
    case 10:
      return precpred(_ctx, 3);
    case 11:
      return precpred(_ctx, 2);
    case 12:
      return precpred(_ctx, 1);
    case 13:
      return precpred(_ctx, 8);
    }
    return true;
  }
  private boolean expression_sempred(ExpressionContext _localctx, int predIndex) {
    switch (predIndex) {
    case 14:
      return isLambdaAhead();
    case 15:
      return isFunctionRefAhead();
    }
    return true;
  }

  public static final String _serializedATN =
    "\u0004\u0001V\u023d\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
    "\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
    "\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
    "\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
    "\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
    "\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"+
    "\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"+
    "\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018"+
    "\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007\u001b"+
    "\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002\u001e\u0007\u001e"+
    "\u0002\u001f\u0007\u001f\u0002 \u0007 \u0002!\u0007!\u0002\"\u0007\"\u0002"+
    "#\u0007#\u0002$\u0007$\u0002%\u0007%\u0002&\u0007&\u0001\u0000\u0005\u0000"+
    "P\b\u0000\n\u0000\f\u0000S\t\u0000\u0001\u0000\u0005\u0000V\b\u0000\n"+
    "\u0000\f\u0000Y\t\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001"+
    "\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002"+
    "\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0005\u0002i\b\u0002"+
    "\n\u0002\f\u0002l\t\u0002\u0003\u0002n\b\u0002\u0001\u0002\u0001\u0002"+
    "\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0003\u0003v\b\u0003"+
    "\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004"+
    "\u0001\u0004\u0001\u0004\u0003\u0004\u0080\b\u0004\u0001\u0004\u0001\u0004"+
    "\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0003\u0004\u0088\b\u0004"+
    "\u0001\u0004\u0001\u0004\u0001\u0004\u0003\u0004\u008d\b\u0004\u0001\u0004"+
    "\u0001\u0004\u0003\u0004\u0091\b\u0004\u0001\u0004\u0001\u0004\u0003\u0004"+
    "\u0095\b\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0003\u0004\u009a\b"+
    "\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001"+
    "\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001"+
    "\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001"+
    "\u0004\u0001\u0004\u0001\u0004\u0004\u0004\u00b0\b\u0004\u000b\u0004\f"+
    "\u0004\u00b1\u0003\u0004\u00b4\b\u0004\u0001\u0005\u0001\u0005\u0001\u0005"+
    "\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005"+
    "\u0001\u0005\u0001\u0005\u0001\u0005\u0003\u0005\u00c2\b\u0005\u0001\u0005"+
    "\u0001\u0005\u0001\u0005\u0003\u0005\u00c7\b\u0005\u0001\u0006\u0001\u0006"+
    "\u0003\u0006\u00cb\b\u0006\u0001\u0007\u0001\u0007\u0005\u0007\u00cf\b"+
    "\u0007\n\u0007\f\u0007\u00d2\t\u0007\u0001\u0007\u0003\u0007\u00d5\b\u0007"+
    "\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001\t\u0001\t\u0003\t\u00dd"+
    "\b\t\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0005"+
    "\u000b\u00e5\b\u000b\n\u000b\f\u000b\u00e8\t\u000b\u0001\f\u0001\f\u0001"+
    "\f\u0005\f\u00ed\b\f\n\f\f\f\u00f0\t\f\u0001\r\u0001\r\u0001\r\u0001\r"+
    "\u0001\r\u0005\r\u00f7\b\r\n\r\f\r\u00fa\t\r\u0003\r\u00fc\b\r\u0001\u000e"+
    "\u0001\u000e\u0001\u000e\u0003\u000e\u0101\b\u000e\u0001\u000f\u0001\u000f"+
    "\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u0010"+
    "\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010"+
    "\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010"+
    "\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010"+
    "\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010"+
    "\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010"+
    "\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010"+
    "\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0005\u0010"+
    "\u0134\b\u0010\n\u0010\f\u0010\u0137\t\u0010\u0001\u0011\u0001\u0011\u0001"+
    "\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001"+
    "\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001"+
    "\u0011\u0003\u0011\u0148\b\u0011\u0001\u0012\u0001\u0012\u0001\u0012\u0001"+
    "\u0012\u0001\u0012\u0003\u0012\u014f\b\u0012\u0001\u0013\u0001\u0013\u0001"+
    "\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0003\u0013\u0158"+
    "\b\u0013\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001"+
    "\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0003\u0014\u0164"+
    "\b\u0014\u0001\u0015\u0001\u0015\u0001\u0016\u0001\u0016\u0001\u0016\u0004"+
    "\u0016\u016b\b\u0016\u000b\u0016\f\u0016\u016c\u0001\u0016\u0001\u0016"+
    "\u0001\u0016\u0004\u0016\u0172\b\u0016\u000b\u0016\f\u0016\u0173\u0001"+
    "\u0016\u0001\u0016\u0001\u0016\u0005\u0016\u0179\b\u0016\n\u0016\f\u0016"+
    "\u017c\t\u0016\u0001\u0016\u0001\u0016\u0005\u0016\u0180\b\u0016\n\u0016"+
    "\f\u0016\u0183\t\u0016\u0003\u0016\u0185\b\u0016\u0001\u0017\u0001\u0017"+
    "\u0005\u0017\u0189\b\u0017\n\u0017\f\u0017\u018c\t\u0017\u0001\u0017\u0003"+
    "\u0017\u018f\b\u0017\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001"+
    "\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001"+
    "\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001"+
    "\u0018\u0001\u0018\u0001\u0018\u0003\u0018\u01a4\b\u0018\u0001\u0019\u0001"+
    "\u0019\u0001\u0019\u0003\u0019\u01a9\b\u0019\u0001\u001a\u0001\u001a\u0003"+
    "\u001a\u01ad\b\u001a\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001"+
    "\u001c\u0001\u001c\u0001\u001c\u0001\u001d\u0001\u001d\u0001\u001d\u0001"+
    "\u001d\u0001\u001e\u0001\u001e\u0001\u001e\u0001\u001e\u0001\u001e\u0001"+
    "\u001e\u0004\u001e\u01c0\b\u001e\u000b\u001e\f\u001e\u01c1\u0001\u001e"+
    "\u0001\u001e\u0005\u001e\u01c6\b\u001e\n\u001e\f\u001e\u01c9\t\u001e\u0003"+
    "\u001e\u01cb\b\u001e\u0001\u001e\u0001\u001e\u0001\u001e\u0001\u001e\u0001"+
    "\u001e\u0001\u001e\u0001\u001e\u0001\u001e\u0005\u001e\u01d5\b\u001e\n"+
    "\u001e\f\u001e\u01d8\t\u001e\u0003\u001e\u01da\b\u001e\u0001\u001e\u0001"+
    "\u001e\u0005\u001e\u01de\b\u001e\n\u001e\f\u001e\u01e1\t\u001e\u0003\u001e"+
    "\u01e3\b\u001e\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0005\u001f"+
    "\u01e9\b\u001f\n\u001f\f\u001f\u01ec\t\u001f\u0001\u001f\u0001\u001f\u0001"+
    "\u001f\u0001\u001f\u0003\u001f\u01f2\b\u001f\u0001 \u0001 \u0001 \u0001"+
    " \u0005 \u01f8\b \n \f \u01fb\t \u0001 \u0001 \u0001 \u0001 \u0001 \u0003"+
    " \u0202\b \u0001!\u0001!\u0001!\u0001!\u0001\"\u0001\"\u0001\"\u0001\""+
    "\u0005\"\u020c\b\"\n\"\f\"\u020f\t\"\u0003\"\u0211\b\"\u0001\"\u0001\""+
    "\u0001#\u0001#\u0001$\u0001$\u0001$\u0001$\u0001$\u0005$\u021c\b$\n$\f"+
    "$\u021f\t$\u0003$\u0221\b$\u0001$\u0003$\u0224\b$\u0001$\u0001$\u0001"+
    "$\u0003$\u0229\b$\u0001%\u0003%\u022c\b%\u0001%\u0001%\u0001&\u0001&\u0001"+
    "&\u0001&\u0001&\u0001&\u0001&\u0001&\u0001&\u0001&\u0001&\u0003&\u023b"+
    "\b&\u0001&\u0000\u0001 \'\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012"+
    "\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*,.02468:<>@BDFHJL\u0000\u000f"+
    "\u0001\u0001\r\r\u0001\u0000\u001f!\u0001\u0000\"#\u0001\u00009:\u0001"+
    "\u0000$&\u0001\u0000\'*\u0001\u0000+.\u0001\u0000=H\u0001\u0000;<\u0001"+
    "\u0000\u001d\u001e\u0001\u0000RS\u0001\u0000IL\u0002\u0000\t\tTT\u0001"+
    "\u0000\n\u000b\u0001\u0000UV\u0278\u0000Q\u0001\u0000\u0000\u0000\u0002"+
    "\\\u0001\u0000\u0000\u0000\u0004a\u0001\u0000\u0000\u0000\u0006u\u0001"+
    "\u0000\u0000\u0000\b\u00b3\u0001\u0000\u0000\u0000\n\u00c6\u0001\u0000"+
    "\u0000\u0000\f\u00ca\u0001\u0000\u0000\u0000\u000e\u00cc\u0001\u0000\u0000"+
    "\u0000\u0010\u00d8\u0001\u0000\u0000\u0000\u0012\u00dc\u0001\u0000\u0000"+
    "\u0000\u0014\u00de\u0001\u0000\u0000\u0000\u0016\u00e0\u0001\u0000\u0000"+
    "\u0000\u0018\u00e9\u0001\u0000\u0000\u0000\u001a\u00fb\u0001\u0000\u0000"+
    "\u0000\u001c\u00fd\u0001\u0000\u0000\u0000\u001e\u0102\u0001\u0000\u0000"+
    "\u0000 \u0109\u0001\u0000\u0000\u0000\"\u0147\u0001\u0000\u0000\u0000"+
    "$\u014e\u0001\u0000\u0000\u0000&\u0157\u0001\u0000\u0000\u0000(\u0163"+
    "\u0001\u0000\u0000\u0000*\u0165\u0001\u0000\u0000\u0000,\u0184\u0001\u0000"+
    "\u0000\u0000.\u018e\u0001\u0000\u0000\u00000\u01a3\u0001\u0000\u0000\u0000"+
    "2\u01a8\u0001\u0000\u0000\u00004\u01ac\u0001\u0000\u0000\u00006\u01ae"+
    "\u0001\u0000\u0000\u00008\u01b2\u0001\u0000\u0000\u0000:\u01b5\u0001\u0000"+
    "\u0000\u0000<\u01e2\u0001\u0000\u0000\u0000>\u01f1\u0001\u0000\u0000\u0000"+
    "@\u0201\u0001\u0000\u0000\u0000B\u0203\u0001\u0000\u0000\u0000D\u0207"+
    "\u0001\u0000\u0000\u0000F\u0214\u0001\u0000\u0000\u0000H\u0223\u0001\u0000"+
    "\u0000\u0000J\u022b\u0001\u0000\u0000\u0000L\u023a\u0001\u0000\u0000\u0000"+
    "NP\u0003\u0002\u0001\u0000ON\u0001\u0000\u0000\u0000PS\u0001\u0000\u0000"+
    "\u0000QO\u0001\u0000\u0000\u0000QR\u0001\u0000\u0000\u0000RW\u0001\u0000"+
    "\u0000\u0000SQ\u0001\u0000\u0000\u0000TV\u0003\u0006\u0003\u0000UT\u0001"+
    "\u0000\u0000\u0000VY\u0001\u0000\u0000\u0000WU\u0001\u0000\u0000\u0000"+
    "WX\u0001\u0000\u0000\u0000XZ\u0001\u0000\u0000\u0000YW\u0001\u0000\u0000"+
    "\u0000Z[\u0005\u0000\u0000\u0001[\u0001\u0001\u0000\u0000\u0000\\]\u0003"+
    "\u0018\f\u0000]^\u0005T\u0000\u0000^_\u0003\u0004\u0002\u0000_`\u0003"+
    "\u000e\u0007\u0000`\u0003\u0001\u0000\u0000\u0000am\u0005\u0007\u0000"+
    "\u0000bc\u0003\u0018\f\u0000cj\u0005T\u0000\u0000de\u0005\f\u0000\u0000"+
    "ef\u0003\u0018\f\u0000fg\u0005T\u0000\u0000gi\u0001\u0000\u0000\u0000"+
    "hd\u0001\u0000\u0000\u0000il\u0001\u0000\u0000\u0000jh\u0001\u0000\u0000"+
    "\u0000jk\u0001\u0000\u0000\u0000kn\u0001\u0000\u0000\u0000lj\u0001\u0000"+
    "\u0000\u0000mb\u0001\u0000\u0000\u0000mn\u0001\u0000\u0000\u0000no\u0001"+
    "\u0000\u0000\u0000op\u0005\b\u0000\u0000p\u0005\u0001\u0000\u0000\u0000"+
    "qv\u0003\b\u0004\u0000rs\u0003\n\u0005\u0000st\u0007\u0000\u0000\u0000"+
    "tv\u0001\u0000\u0000\u0000uq\u0001\u0000\u0000\u0000ur\u0001\u0000\u0000"+
    "\u0000v\u0007\u0001\u0000\u0000\u0000wx\u0005\u000e\u0000\u0000xy\u0005"+
    "\u0007\u0000\u0000yz\u0003\"\u0011\u0000z{\u0005\b\u0000\u0000{\u007f"+
    "\u0003\f\u0006\u0000|}\u0005\u0010\u0000\u0000}\u0080\u0003\f\u0006\u0000"+
    "~\u0080\u0004\u0004\u0000\u0000\u007f|\u0001\u0000\u0000\u0000\u007f~"+
    "\u0001\u0000\u0000\u0000\u0080\u00b4\u0001\u0000\u0000\u0000\u0081\u0082"+
    "\u0005\u0011\u0000\u0000\u0082\u0083\u0005\u0007\u0000\u0000\u0083\u0084"+
    "\u0003\"\u0011\u0000\u0084\u0087\u0005\b\u0000\u0000\u0085\u0088\u0003"+
    "\f\u0006\u0000\u0086\u0088\u0003\u0010\b\u0000\u0087\u0085\u0001\u0000"+
    "\u0000\u0000\u0087\u0086\u0001\u0000\u0000\u0000\u0088\u00b4\u0001\u0000"+
    "\u0000\u0000\u0089\u008a\u0005\u0013\u0000\u0000\u008a\u008c\u0005\u0007"+
    "\u0000\u0000\u008b\u008d\u0003\u0012\t\u0000\u008c\u008b\u0001\u0000\u0000"+
    "\u0000\u008c\u008d\u0001\u0000\u0000\u0000\u008d\u008e\u0001\u0000\u0000"+
    "\u0000\u008e\u0090\u0005\r\u0000\u0000\u008f\u0091\u0003\"\u0011\u0000"+
    "\u0090\u008f\u0001\u0000\u0000\u0000\u0090\u0091\u0001\u0000\u0000\u0000"+
    "\u0091\u0092\u0001\u0000\u0000\u0000\u0092\u0094\u0005\r\u0000\u0000\u0093"+
    "\u0095\u0003\u0014\n\u0000\u0094\u0093\u0001\u0000\u0000\u0000\u0094\u0095"+
    "\u0001\u0000\u0000\u0000\u0095\u0096\u0001\u0000\u0000\u0000\u0096\u0099"+
    "\u0005\b\u0000\u0000\u0097\u009a\u0003\f\u0006\u0000\u0098\u009a\u0003"+
    "\u0010\b\u0000\u0099\u0097\u0001\u0000\u0000\u0000\u0099\u0098\u0001\u0000"+
    "\u0000\u0000\u009a\u00b4\u0001\u0000\u0000\u0000\u009b\u009c\u0005\u0013"+
    "\u0000\u0000\u009c\u009d\u0005\u0007\u0000\u0000\u009d\u009e\u0003\u0018"+
    "\f\u0000\u009e\u009f\u0005T\u0000\u0000\u009f\u00a0\u00055\u0000\u0000"+
    "\u00a0\u00a1\u0003\"\u0011\u0000\u00a1\u00a2\u0005\b\u0000\u0000\u00a2"+
    "\u00a3\u0003\f\u0006\u0000\u00a3\u00b4\u0001\u0000\u0000\u0000\u00a4\u00a5"+
    "\u0005\u0013\u0000\u0000\u00a5\u00a6\u0005\u0007\u0000\u0000\u00a6\u00a7"+
    "\u0005T\u0000\u0000\u00a7\u00a8\u0005\u000f\u0000\u0000\u00a8\u00a9\u0003"+
    "\"\u0011\u0000\u00a9\u00aa\u0005\b\u0000\u0000\u00aa\u00ab\u0003\f\u0006"+
    "\u0000\u00ab\u00b4\u0001\u0000\u0000\u0000\u00ac\u00ad\u0005\u0018\u0000"+
    "\u0000\u00ad\u00af\u0003\u000e\u0007\u0000\u00ae\u00b0\u0003\u001e\u000f"+
    "\u0000\u00af\u00ae\u0001\u0000\u0000\u0000\u00b0\u00b1\u0001\u0000\u0000"+
    "\u0000\u00b1\u00af\u0001\u0000\u0000\u0000\u00b1\u00b2\u0001\u0000\u0000"+
    "\u0000\u00b2\u00b4\u0001\u0000\u0000\u0000\u00b3w\u0001\u0000\u0000\u0000"+
    "\u00b3\u0081\u0001\u0000\u0000\u0000\u00b3\u0089\u0001\u0000\u0000\u0000"+
    "\u00b3\u009b\u0001\u0000\u0000\u0000\u00b3\u00a4\u0001\u0000\u0000\u0000"+
    "\u00b3\u00ac\u0001\u0000\u0000\u0000\u00b4\t\u0001\u0000\u0000\u0000\u00b5"+
    "\u00b6\u0005\u0012\u0000\u0000\u00b6\u00b7\u0003\u000e\u0007\u0000\u00b7"+
    "\u00b8\u0005\u0011\u0000\u0000\u00b8\u00b9\u0005\u0007\u0000\u0000\u00b9"+
    "\u00ba\u0003\"\u0011\u0000\u00ba\u00bb\u0005\b\u0000\u0000\u00bb\u00c7"+
    "\u0001\u0000\u0000\u0000\u00bc\u00c7\u0003\u0016\u000b\u0000\u00bd\u00c7"+
    "\u0005\u0014\u0000\u0000\u00be\u00c7\u0005\u0015\u0000\u0000\u00bf\u00c1"+
    "\u0005\u0016\u0000\u0000\u00c0\u00c2\u0003\"\u0011\u0000\u00c1\u00c0\u0001"+
    "\u0000\u0000\u0000\u00c1\u00c2\u0001\u0000\u0000\u0000\u00c2\u00c7\u0001"+
    "\u0000\u0000\u0000\u00c3\u00c4\u0005\u001a\u0000\u0000\u00c4\u00c7\u0003"+
    "\"\u0011\u0000\u00c5\u00c7\u0003\"\u0011\u0000\u00c6\u00b5\u0001\u0000"+
    "\u0000\u0000\u00c6\u00bc\u0001\u0000\u0000\u0000\u00c6\u00bd\u0001\u0000"+
    "\u0000\u0000\u00c6\u00be\u0001\u0000\u0000\u0000\u00c6\u00bf\u0001\u0000"+
    "\u0000\u0000\u00c6\u00c3\u0001\u0000\u0000\u0000\u00c6\u00c5\u0001\u0000"+
    "\u0000\u0000\u00c7\u000b\u0001\u0000\u0000\u0000\u00c8\u00cb\u0003\u000e"+
    "\u0007\u0000\u00c9\u00cb\u0003\u0006\u0003\u0000\u00ca\u00c8\u0001\u0000"+
    "\u0000\u0000\u00ca\u00c9\u0001\u0000\u0000\u0000\u00cb\r\u0001\u0000\u0000"+
    "\u0000\u00cc\u00d0\u0005\u0003\u0000\u0000\u00cd\u00cf\u0003\u0006\u0003"+
    "\u0000\u00ce\u00cd\u0001\u0000\u0000\u0000\u00cf\u00d2\u0001\u0000\u0000"+
    "\u0000\u00d0\u00ce\u0001\u0000\u0000\u0000\u00d0\u00d1\u0001\u0000\u0000"+
    "\u0000\u00d1\u00d4\u0001\u0000\u0000\u0000\u00d2\u00d0\u0001\u0000\u0000"+
    "\u0000\u00d3\u00d5\u0003\n\u0005\u0000\u00d4\u00d3\u0001\u0000\u0000\u0000"+
    "\u00d4\u00d5\u0001\u0000\u0000\u0000\u00d5\u00d6\u0001\u0000\u0000\u0000"+
    "\u00d6\u00d7\u0005\u0004\u0000\u0000\u00d7\u000f\u0001\u0000\u0000\u0000"+
    "\u00d8\u00d9\u0005\r\u0000\u0000\u00d9\u0011\u0001\u0000\u0000\u0000\u00da"+
    "\u00dd\u0003\u0016\u000b\u0000\u00db\u00dd\u0003\"\u0011\u0000\u00dc\u00da"+
    "\u0001\u0000\u0000\u0000\u00dc\u00db\u0001\u0000\u0000\u0000\u00dd\u0013"+
    "\u0001\u0000\u0000\u0000\u00de\u00df\u0003\"\u0011\u0000\u00df\u0015\u0001"+
    "\u0000\u0000\u0000\u00e0\u00e1\u0003\u0018\f\u0000\u00e1\u00e6\u0003\u001c"+
    "\u000e\u0000\u00e2\u00e3\u0005\f\u0000\u0000\u00e3\u00e5\u0003\u001c\u000e"+
    "\u0000\u00e4\u00e2\u0001\u0000\u0000\u0000\u00e5\u00e8\u0001\u0000\u0000"+
    "\u0000\u00e6\u00e4\u0001\u0000\u0000\u0000\u00e6\u00e7\u0001\u0000\u0000"+
    "\u0000\u00e7\u0017\u0001\u0000\u0000\u0000\u00e8\u00e6\u0001\u0000\u0000"+
    "\u0000\u00e9\u00ee\u0003\u001a\r\u0000\u00ea\u00eb\u0005\u0005\u0000\u0000"+
    "\u00eb\u00ed\u0005\u0006\u0000\u0000\u00ec\u00ea\u0001\u0000\u0000\u0000"+
    "\u00ed\u00f0\u0001\u0000\u0000\u0000\u00ee\u00ec\u0001\u0000\u0000\u0000"+
    "\u00ee\u00ef\u0001\u0000\u0000\u0000\u00ef\u0019\u0001\u0000\u0000\u0000"+
    "\u00f0\u00ee\u0001\u0000\u0000\u0000\u00f1\u00fc\u0005S\u0000\u0000\u00f2"+
    "\u00fc\u0005R\u0000\u0000\u00f3\u00f8\u0005T\u0000\u0000\u00f4\u00f5\u0005"+
    "\n\u0000\u0000\u00f5\u00f7\u0005V\u0000\u0000\u00f6\u00f4\u0001\u0000"+
    "\u0000\u0000\u00f7\u00fa\u0001\u0000\u0000\u0000\u00f8\u00f6\u0001\u0000"+
    "\u0000\u0000\u00f8\u00f9\u0001\u0000\u0000\u0000\u00f9\u00fc\u0001\u0000"+
    "\u0000\u0000\u00fa\u00f8\u0001\u0000\u0000\u0000\u00fb\u00f1\u0001\u0000"+
    "\u0000\u0000\u00fb\u00f2\u0001\u0000\u0000\u0000\u00fb\u00f3\u0001\u0000"+
    "\u0000\u0000\u00fc\u001b\u0001\u0000\u0000\u0000\u00fd\u0100\u0005T\u0000"+
    "\u0000\u00fe\u00ff\u0005=\u0000\u0000\u00ff\u0101\u0003\"\u0011\u0000"+
    "\u0100\u00fe\u0001\u0000\u0000\u0000\u0100\u0101\u0001\u0000\u0000\u0000"+
    "\u0101\u001d\u0001\u0000\u0000\u0000\u0102\u0103\u0005\u0019\u0000\u0000"+
    "\u0103\u0104\u0005\u0007\u0000\u0000\u0104\u0105\u0003\u001a\r\u0000\u0105"+
    "\u0106\u0005T\u0000\u0000\u0106\u0107\u0005\b\u0000\u0000\u0107\u0108"+
    "\u0003\u000e\u0007\u0000\u0108\u001f\u0001\u0000\u0000\u0000\u0109\u010a"+
    "\u0006\u0010\uffff\uffff\u0000\u010a\u010b\u0003$\u0012\u0000\u010b\u0135"+
    "\u0001\u0000\u0000\u0000\u010c\u010d\n\r\u0000\u0000\u010d\u010e\u0007"+
    "\u0001\u0000\u0000\u010e\u0134\u0003 \u0010\u000e\u010f\u0110\n\f\u0000"+
    "\u0000\u0110\u0111\u0007\u0002\u0000\u0000\u0111\u0134\u0003 \u0010\r"+
    "\u0112\u0113\n\u000b\u0000\u0000\u0113\u0114\u0007\u0003\u0000\u0000\u0114"+
    "\u0134\u0003 \u0010\f\u0115\u0116\n\n\u0000\u0000\u0116\u0117\u0007\u0004"+
    "\u0000\u0000\u0117\u0134\u0003 \u0010\u000b\u0118\u0119\n\t\u0000\u0000"+
    "\u0119\u011a\u0007\u0005\u0000\u0000\u011a\u0134\u0003 \u0010\n\u011b"+
    "\u011c\n\u0007\u0000\u0000\u011c\u011d\u0007\u0006\u0000\u0000\u011d\u0134"+
    "\u0003 \u0010\b\u011e\u011f\n\u0006\u0000\u0000\u011f\u0120\u0005/\u0000"+
    "\u0000\u0120\u0134\u0003 \u0010\u0007\u0121\u0122\n\u0005\u0000\u0000"+
    "\u0122\u0123\u00050\u0000\u0000\u0123\u0134\u0003 \u0010\u0006\u0124\u0125"+
    "\n\u0004\u0000\u0000\u0125\u0126\u00051\u0000\u0000\u0126\u0134\u0003"+
    " \u0010\u0005\u0127\u0128\n\u0003\u0000\u0000\u0128\u0129\u00052\u0000"+
    "\u0000\u0129\u0134\u0003 \u0010\u0004\u012a\u012b\n\u0002\u0000\u0000"+
    "\u012b\u012c\u00053\u0000\u0000\u012c\u0134\u0003 \u0010\u0003\u012d\u012e"+
    "\n\u0001\u0000\u0000\u012e\u012f\u00056\u0000\u0000\u012f\u0134\u0003"+
    " \u0010\u0001\u0130\u0131\n\b\u0000\u0000\u0131\u0132\u0005\u001c\u0000"+
    "\u0000\u0132\u0134\u0003\u0018\f\u0000\u0133\u010c\u0001\u0000\u0000\u0000"+
    "\u0133\u010f\u0001\u0000\u0000\u0000\u0133\u0112\u0001\u0000\u0000\u0000"+
    "\u0133\u0115\u0001\u0000\u0000\u0000\u0133\u0118\u0001\u0000\u0000\u0000"+
    "\u0133\u011b\u0001\u0000\u0000\u0000\u0133\u011e\u0001\u0000\u0000\u0000"+
    "\u0133\u0121\u0001\u0000\u0000\u0000\u0133\u0124\u0001\u0000\u0000\u0000"+
    "\u0133\u0127\u0001\u0000\u0000\u0000\u0133\u012a\u0001\u0000\u0000\u0000"+
    "\u0133\u012d\u0001\u0000\u0000\u0000\u0133\u0130\u0001\u0000\u0000\u0000"+
    "\u0134\u0137\u0001\u0000\u0000\u0000\u0135\u0133\u0001\u0000\u0000\u0000"+
    "\u0135\u0136\u0001\u0000\u0000\u0000\u0136!\u0001\u0000\u0000\u0000\u0137"+
    "\u0135\u0001\u0000\u0000\u0000\u0138\u0139\u0004\u0011\u000e\u0000\u0139"+
    "\u0148\u0003H$\u0000\u013a\u013b\u0004\u0011\u000f\u0000\u013b\u0148\u0003"+
    "L&\u0000\u013c\u0148\u0003 \u0010\u0000\u013d\u013e\u0003 \u0010\u0000"+
    "\u013e\u013f\u00054\u0000\u0000\u013f\u0140\u0003\"\u0011\u0000\u0140"+
    "\u0141\u00055\u0000\u0000\u0141\u0142\u0003\"\u0011\u0000\u0142\u0148"+
    "\u0001\u0000\u0000\u0000\u0143\u0144\u0003 \u0010\u0000\u0144\u0145\u0007"+
    "\u0007\u0000\u0000\u0145\u0146\u0003\"\u0011\u0000\u0146\u0148\u0001\u0000"+
    "\u0000\u0000\u0147\u0138\u0001\u0000\u0000\u0000\u0147\u013a\u0001\u0000"+
    "\u0000\u0000\u0147\u013c\u0001\u0000\u0000\u0000\u0147\u013d\u0001\u0000"+
    "\u0000\u0000\u0147\u0143\u0001\u0000\u0000\u0000\u0148#\u0001\u0000\u0000"+
    "\u0000\u0149\u014a\u0007\b\u0000\u0000\u014a\u014f\u0003.\u0017\u0000"+
    "\u014b\u014c\u0007\u0002\u0000\u0000\u014c\u014f\u0003$\u0012\u0000\u014d"+
    "\u014f\u0003&\u0013\u0000\u014e\u0149\u0001\u0000\u0000\u0000\u014e\u014b"+
    "\u0001\u0000\u0000\u0000\u014e\u014d\u0001\u0000\u0000\u0000\u014f%\u0001"+
    "\u0000\u0000\u0000\u0150\u0158\u0003.\u0017\u0000\u0151\u0152\u0003.\u0017"+
    "\u0000\u0152\u0153\u0007\b\u0000\u0000\u0153\u0158\u0001\u0000\u0000\u0000"+
    "\u0154\u0155\u0007\t\u0000\u0000\u0155\u0158\u0003$\u0012\u0000\u0156"+
    "\u0158\u0003(\u0014\u0000\u0157\u0150\u0001\u0000\u0000\u0000\u0157\u0151"+
    "\u0001\u0000\u0000\u0000\u0157\u0154\u0001\u0000\u0000\u0000\u0157\u0156"+
    "\u0001\u0000\u0000\u0000\u0158\'\u0001\u0000\u0000\u0000\u0159\u015a\u0005"+
    "\u0007\u0000\u0000\u015a\u015b\u0003*\u0015\u0000\u015b\u015c\u0005\b"+
    "\u0000\u0000\u015c\u015d\u0003$\u0012\u0000\u015d\u0164\u0001\u0000\u0000"+
    "\u0000\u015e\u015f\u0005\u0007\u0000\u0000\u015f\u0160\u0003,\u0016\u0000"+
    "\u0160\u0161\u0005\b\u0000\u0000\u0161\u0162\u0003&\u0013\u0000\u0162"+
    "\u0164\u0001\u0000\u0000\u0000\u0163\u0159\u0001\u0000\u0000\u0000\u0163"+
    "\u015e\u0001\u0000\u0000\u0000\u0164)\u0001\u0000\u0000\u0000\u0165\u0166"+
    "\u0007\n\u0000\u0000\u0166+\u0001\u0000\u0000\u0000\u0167\u016a\u0005"+
    "S\u0000\u0000\u0168\u0169\u0005\u0005\u0000\u0000\u0169\u016b\u0005\u0006"+
    "\u0000\u0000\u016a\u0168\u0001\u0000\u0000\u0000\u016b\u016c\u0001\u0000"+
    "\u0000\u0000\u016c\u016a\u0001\u0000\u0000\u0000\u016c\u016d\u0001\u0000"+
    "\u0000\u0000\u016d\u0185\u0001\u0000\u0000\u0000\u016e\u0171\u0005R\u0000"+
    "\u0000\u016f\u0170\u0005\u0005\u0000\u0000\u0170\u0172\u0005\u0006\u0000"+
    "\u0000\u0171\u016f\u0001\u0000\u0000\u0000\u0172\u0173\u0001\u0000\u0000"+
    "\u0000\u0173\u0171\u0001\u0000\u0000\u0000\u0173\u0174\u0001\u0000\u0000"+
    "\u0000\u0174\u0185\u0001\u0000\u0000\u0000\u0175\u017a\u0005T\u0000\u0000"+
    "\u0176\u0177\u0005\n\u0000\u0000\u0177\u0179\u0005V\u0000\u0000\u0178"+
    "\u0176\u0001\u0000\u0000\u0000\u0179\u017c\u0001\u0000\u0000\u0000\u017a"+
    "\u0178\u0001\u0000\u0000\u0000\u017a\u017b\u0001\u0000\u0000\u0000\u017b"+
    "\u0181\u0001\u0000\u0000\u0000\u017c\u017a\u0001\u0000\u0000\u0000\u017d"+
    "\u017e\u0005\u0005\u0000\u0000\u017e\u0180\u0005\u0006\u0000\u0000\u017f"+
    "\u017d\u0001\u0000\u0000\u0000\u0180\u0183\u0001\u0000\u0000\u0000\u0181"+
    "\u017f\u0001\u0000\u0000\u0000\u0181\u0182\u0001\u0000\u0000\u0000\u0182"+
    "\u0185\u0001\u0000\u0000\u0000\u0183\u0181\u0001\u0000\u0000\u0000\u0184"+
    "\u0167\u0001\u0000\u0000\u0000\u0184\u016e\u0001\u0000\u0000\u0000\u0184"+
    "\u0175\u0001\u0000\u0000\u0000\u0185-\u0001\u0000\u0000\u0000\u0186\u018a"+
    "\u00030\u0018\u0000\u0187\u0189\u00032\u0019\u0000\u0188\u0187\u0001\u0000"+
    "\u0000\u0000\u0189\u018c\u0001\u0000\u0000\u0000\u018a\u0188\u0001\u0000"+
    "\u0000\u0000\u018a\u018b\u0001\u0000\u0000\u0000\u018b\u018f\u0001\u0000"+
    "\u0000\u0000\u018c\u018a\u0001\u0000\u0000\u0000\u018d\u018f\u0003<\u001e"+
    "\u0000\u018e\u0186\u0001\u0000\u0000\u0000\u018e\u018d\u0001\u0000\u0000"+
    "\u0000\u018f/\u0001\u0000\u0000\u0000\u0190\u0191\u0005\u0007\u0000\u0000"+
    "\u0191\u0192\u0003\"\u0011\u0000\u0192\u0193\u0005\b\u0000\u0000\u0193"+
    "\u01a4\u0001\u0000\u0000\u0000\u0194\u01a4\u0007\u000b\u0000\u0000\u0195"+
    "\u01a4\u0005O\u0000\u0000\u0196\u01a4\u0005P\u0000\u0000\u0197\u01a4\u0005"+
    "Q\u0000\u0000\u0198\u01a4\u0005M\u0000\u0000\u0199\u01a4\u0005N\u0000"+
    "\u0000\u019a\u01a4\u0003>\u001f\u0000\u019b\u01a4\u0003@ \u0000\u019c"+
    "\u01a4\u0005T\u0000\u0000\u019d\u019e\u0007\f\u0000\u0000\u019e\u01a4"+
    "\u0003D\"\u0000\u019f\u01a0\u0005\u0017\u0000\u0000\u01a0\u01a1\u0003"+
    "\u001a\r\u0000\u01a1\u01a2\u0003D\"\u0000\u01a2\u01a4\u0001\u0000\u0000"+
    "\u0000\u01a3\u0190\u0001\u0000\u0000\u0000\u01a3\u0194\u0001\u0000\u0000"+
    "\u0000\u01a3\u0195\u0001\u0000\u0000\u0000\u01a3\u0196\u0001\u0000\u0000"+
    "\u0000\u01a3\u0197\u0001\u0000\u0000\u0000\u01a3\u0198\u0001\u0000\u0000"+
    "\u0000\u01a3\u0199\u0001\u0000\u0000\u0000\u01a3\u019a\u0001\u0000\u0000"+
    "\u0000\u01a3\u019b\u0001\u0000\u0000\u0000\u01a3\u019c\u0001\u0000\u0000"+
    "\u0000\u01a3\u019d\u0001\u0000\u0000\u0000\u01a3\u019f\u0001\u0000\u0000"+
    "\u0000\u01a41\u0001\u0000\u0000\u0000\u01a5\u01a9\u00036\u001b\u0000\u01a6"+
    "\u01a9\u00038\u001c\u0000\u01a7\u01a9\u0003:\u001d\u0000\u01a8\u01a5\u0001"+
    "\u0000\u0000\u0000\u01a8\u01a6\u0001\u0000\u0000\u0000\u01a8\u01a7\u0001"+
    "\u0000\u0000\u0000\u01a93\u0001\u0000\u0000\u0000\u01aa\u01ad\u00036\u001b"+
    "\u0000\u01ab\u01ad\u00038\u001c\u0000\u01ac\u01aa\u0001\u0000\u0000\u0000"+
    "\u01ac\u01ab\u0001\u0000\u0000\u0000\u01ad5\u0001\u0000\u0000\u0000\u01ae"+
    "\u01af\u0007\r\u0000\u0000\u01af\u01b0\u0005V\u0000\u0000\u01b0\u01b1"+
    "\u0003D\"\u0000\u01b17\u0001\u0000\u0000\u0000\u01b2\u01b3\u0007\r\u0000"+
    "\u0000\u01b3\u01b4\u0007\u000e\u0000\u0000\u01b49\u0001\u0000\u0000\u0000"+
    "\u01b5\u01b6\u0005\u0005\u0000\u0000\u01b6\u01b7\u0003\"\u0011\u0000\u01b7"+
    "\u01b8\u0005\u0006\u0000\u0000\u01b8;\u0001\u0000\u0000\u0000\u01b9\u01ba"+
    "\u0005\u0017\u0000\u0000\u01ba\u01bf\u0003\u001a\r\u0000\u01bb\u01bc\u0005"+
    "\u0005\u0000\u0000\u01bc\u01bd\u0003\"\u0011\u0000\u01bd\u01be\u0005\u0006"+
    "\u0000\u0000\u01be\u01c0\u0001\u0000\u0000\u0000\u01bf\u01bb\u0001\u0000"+
    "\u0000\u0000\u01c0\u01c1\u0001\u0000\u0000\u0000\u01c1\u01bf\u0001\u0000"+
    "\u0000\u0000\u01c1\u01c2\u0001\u0000\u0000\u0000\u01c2\u01ca\u0001\u0000"+
    "\u0000\u0000\u01c3\u01c7\u00034\u001a\u0000\u01c4\u01c6\u00032\u0019\u0000"+
    "\u01c5\u01c4\u0001\u0000\u0000\u0000\u01c6\u01c9\u0001\u0000\u0000\u0000"+
    "\u01c7\u01c5\u0001\u0000\u0000\u0000\u01c7\u01c8\u0001\u0000\u0000\u0000"+
    "\u01c8\u01cb\u0001\u0000\u0000\u0000\u01c9\u01c7\u0001\u0000\u0000\u0000"+
    "\u01ca\u01c3\u0001\u0000\u0000\u0000\u01ca\u01cb\u0001\u0000\u0000\u0000"+
    "\u01cb\u01e3\u0001\u0000\u0000\u0000\u01cc\u01cd\u0005\u0017\u0000\u0000"+
    "\u01cd\u01ce\u0003\u001a\r\u0000\u01ce\u01cf\u0005\u0005\u0000\u0000\u01cf"+
    "\u01d0\u0005\u0006\u0000\u0000\u01d0\u01d9\u0005\u0003\u0000\u0000\u01d1"+
    "\u01d6\u0003\"\u0011\u0000\u01d2\u01d3\u0005\f\u0000\u0000\u01d3\u01d5"+
    "\u0003\"\u0011\u0000\u01d4\u01d2\u0001\u0000\u0000\u0000\u01d5\u01d8\u0001"+
    "\u0000\u0000\u0000\u01d6\u01d4\u0001\u0000\u0000\u0000\u01d6\u01d7\u0001"+
    "\u0000\u0000\u0000\u01d7\u01da\u0001\u0000\u0000\u0000\u01d8\u01d6\u0001"+
    "\u0000\u0000\u0000\u01d9\u01d1\u0001\u0000\u0000\u0000\u01d9\u01da\u0001"+
    "\u0000\u0000\u0000\u01da\u01db\u0001\u0000\u0000\u0000\u01db\u01df\u0005"+
    "\u0004\u0000\u0000\u01dc\u01de\u00032\u0019\u0000\u01dd\u01dc\u0001\u0000"+
    "\u0000\u0000\u01de\u01e1\u0001\u0000\u0000\u0000\u01df\u01dd\u0001\u0000"+
    "\u0000\u0000\u01df\u01e0\u0001\u0000\u0000\u0000\u01e0\u01e3\u0001\u0000"+
    "\u0000\u0000\u01e1\u01df\u0001\u0000\u0000\u0000\u01e2\u01b9\u0001\u0000"+
    "\u0000\u0000\u01e2\u01cc\u0001\u0000\u0000\u0000\u01e3=\u0001\u0000\u0000"+
    "\u0000\u01e4\u01e5\u0005\u0005\u0000\u0000\u01e5\u01ea\u0003\"\u0011\u0000"+
    "\u01e6\u01e7\u0005\f\u0000\u0000\u01e7\u01e9\u0003\"\u0011\u0000\u01e8"+
    "\u01e6\u0001\u0000\u0000\u0000\u01e9\u01ec\u0001\u0000\u0000\u0000\u01ea"+
    "\u01e8\u0001\u0000\u0000\u0000\u01ea\u01eb\u0001\u0000\u0000\u0000\u01eb"+
    "\u01ed\u0001\u0000\u0000\u0000\u01ec\u01ea\u0001\u0000\u0000\u0000\u01ed"+
    "\u01ee\u0005\u0006\u0000\u0000\u01ee\u01f2\u0001\u0000\u0000\u0000\u01ef"+
    "\u01f0\u0005\u0005\u0000\u0000\u01f0\u01f2\u0005\u0006\u0000\u0000\u01f1"+
    "\u01e4\u0001\u0000\u0000\u0000\u01f1\u01ef\u0001\u0000\u0000\u0000\u01f2"+
    "?\u0001\u0000\u0000\u0000\u01f3\u01f4\u0005\u0005\u0000\u0000\u01f4\u01f9"+
    "\u0003B!\u0000\u01f5\u01f6\u0005\f\u0000\u0000\u01f6\u01f8\u0003B!\u0000"+
    "\u01f7\u01f5\u0001\u0000\u0000\u0000\u01f8\u01fb\u0001\u0000\u0000\u0000"+
    "\u01f9\u01f7\u0001\u0000\u0000\u0000\u01f9\u01fa\u0001\u0000\u0000\u0000"+
    "\u01fa\u01fc\u0001\u0000\u0000\u0000\u01fb\u01f9\u0001\u0000\u0000\u0000"+
    "\u01fc\u01fd\u0005\u0006\u0000\u0000\u01fd\u0202\u0001\u0000\u0000\u0000"+
    "\u01fe\u01ff\u0005\u0005\u0000\u0000\u01ff\u0200\u00055\u0000\u0000\u0200"+
    "\u0202\u0005\u0006\u0000\u0000\u0201\u01f3\u0001\u0000\u0000\u0000\u0201"+
    "\u01fe\u0001\u0000\u0000\u0000\u0202A\u0001\u0000\u0000\u0000\u0203\u0204"+
    "\u0003\"\u0011\u0000\u0204\u0205\u00055\u0000\u0000\u0205\u0206\u0003"+
    "\"\u0011\u0000\u0206C\u0001\u0000\u0000\u0000\u0207\u0210\u0005\u0007"+
    "\u0000\u0000\u0208\u020d\u0003F#\u0000\u0209\u020a\u0005\f\u0000\u0000"+
    "\u020a\u020c\u0003F#\u0000\u020b\u0209\u0001\u0000\u0000\u0000\u020c\u020f"+
    "\u0001\u0000\u0000\u0000\u020d\u020b\u0001\u0000\u0000\u0000\u020d\u020e"+
    "\u0001\u0000\u0000\u0000\u020e\u0211\u0001\u0000\u0000\u0000\u020f\u020d"+
    "\u0001\u0000\u0000\u0000\u0210\u0208\u0001\u0000\u0000\u0000\u0210\u0211"+
    "\u0001\u0000\u0000\u0000\u0211\u0212\u0001\u0000\u0000\u0000\u0212\u0213"+
    "\u0005\b\u0000\u0000\u0213E\u0001\u0000\u0000\u0000\u0214\u0215\u0003"+
    "\"\u0011\u0000\u0215G\u0001\u0000\u0000\u0000\u0216\u0224\u0003J%\u0000"+
    "\u0217\u0220\u0005\u0007\u0000\u0000\u0218\u021d\u0003J%\u0000\u0219\u021a"+
    "\u0005\f\u0000\u0000\u021a\u021c\u0003J%\u0000\u021b\u0219\u0001\u0000"+
    "\u0000\u0000\u021c\u021f\u0001\u0000\u0000\u0000\u021d\u021b\u0001\u0000"+
    "\u0000\u0000\u021d\u021e\u0001\u0000\u0000\u0000\u021e\u0221\u0001\u0000"+
    "\u0000\u0000\u021f\u021d\u0001\u0000\u0000\u0000\u0220\u0218\u0001\u0000"+
    "\u0000\u0000\u0220\u0221\u0001\u0000\u0000\u0000\u0221\u0222\u0001\u0000"+
    "\u0000\u0000\u0222\u0224\u0005\b\u0000\u0000\u0223\u0216\u0001\u0000\u0000"+
    "\u0000\u0223\u0217\u0001\u0000\u0000\u0000\u0224\u0225\u0001\u0000\u0000"+
    "\u0000\u0225\u0228\u00058\u0000\u0000\u0226\u0229\u0003\u000e\u0007\u0000"+
    "\u0227\u0229\u0003\"\u0011\u0000\u0228\u0226\u0001\u0000\u0000\u0000\u0228"+
    "\u0227\u0001\u0000\u0000\u0000\u0229I\u0001\u0000\u0000\u0000\u022a\u022c"+
    "\u0003\u0018\f\u0000\u022b\u022a\u0001\u0000\u0000\u0000\u022b\u022c\u0001"+
    "\u0000\u0000\u0000\u022c\u022d\u0001\u0000\u0000\u0000\u022d\u022e\u0005"+
    "T\u0000\u0000\u022eK\u0001\u0000\u0000\u0000\u022f\u0230\u0003\u0018\f"+
    "\u0000\u0230\u0231\u00057\u0000\u0000\u0231\u0232\u0005T\u0000\u0000\u0232"+
    "\u023b\u0001\u0000\u0000\u0000\u0233\u0234\u0003\u0018\f\u0000\u0234\u0235"+
    "\u00057\u0000\u0000\u0235\u0236\u0005\u0017\u0000\u0000\u0236\u023b\u0001"+
    "\u0000\u0000\u0000\u0237\u0238\u0005\u001b\u0000\u0000\u0238\u0239\u0005"+
    "7\u0000\u0000\u0239\u023b\u0005T\u0000\u0000\u023a\u022f\u0001\u0000\u0000"+
    "\u0000\u023a\u0233\u0001\u0000\u0000\u0000\u023a\u0237\u0001\u0000\u0000"+
    "\u0000\u023bM\u0001\u0000\u0000\u0000;QWjmu\u007f\u0087\u008c\u0090\u0094"+
    "\u0099\u00b1\u00b3\u00c1\u00c6\u00ca\u00d0\u00d4\u00dc\u00e6\u00ee\u00f8"+
    "\u00fb\u0100\u0133\u0135\u0147\u014e\u0157\u0163\u016c\u0173\u017a\u0181"+
    "\u0184\u018a\u018e\u01a3\u01a8\u01ac\u01c1\u01c7\u01ca\u01d6\u01d9\u01df"+
    "\u01e2\u01ea\u01f1\u01f9\u0201\u020d\u0210\u021d\u0220\u0223\u0228\u022b"+
    "\u023a";
  public static final ATN _ATN =
    new ATNDeserializer().deserialize(_serializedATN.toCharArray());
  static {
    _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
    for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
      _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
    }
  }
}
