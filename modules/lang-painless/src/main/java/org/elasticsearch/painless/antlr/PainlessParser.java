// ANTLR GENERATED CODE: DO NOT EDIT
package org.elasticsearch.painless.antlr;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
class PainlessParser extends Parser {
  static { RuntimeMetaData.checkVersion("4.5.3", RuntimeMetaData.VERSION); }

  protected static final DFA[] _decisionToDFA;
  protected static final PredictionContextCache _sharedContextCache =
    new PredictionContextCache();
  public static final int
    WS=1, COMMENT=2, LBRACK=3, RBRACK=4, LBRACE=5, RBRACE=6, LP=7, RP=8, DOT=9, 
    NSDOT=10, COMMA=11, SEMICOLON=12, IF=13, IN=14, ELSE=15, WHILE=16, DO=17, 
    FOR=18, CONTINUE=19, BREAK=20, RETURN=21, NEW=22, TRY=23, CATCH=24, THROW=25, 
    THIS=26, INSTANCEOF=27, BOOLNOT=28, BWNOT=29, MUL=30, DIV=31, REM=32, 
    ADD=33, SUB=34, LSH=35, RSH=36, USH=37, LT=38, LTE=39, GT=40, GTE=41, 
    EQ=42, EQR=43, NE=44, NER=45, BWAND=46, XOR=47, BWOR=48, BOOLAND=49, BOOLOR=50, 
    COND=51, COLON=52, ELVIS=53, REF=54, ARROW=55, FIND=56, MATCH=57, INCR=58, 
    DECR=59, ASSIGN=60, AADD=61, ASUB=62, AMUL=63, ADIV=64, AREM=65, AAND=66, 
    AXOR=67, AOR=68, ALSH=69, ARSH=70, AUSH=71, OCTAL=72, HEX=73, INTEGER=74, 
    DECIMAL=75, STRING=76, REGEX=77, TRUE=78, FALSE=79, NULL=80, PRIMITIVE=81, 
    DEF=82, ID=83, DOTINTEGER=84, DOTID=85, DOTINGORE=86;
  public static final int
    RULE_source = 0, RULE_function = 1, RULE_parameters = 2, RULE_statement = 3, 
    RULE_rstatement = 4, RULE_dstatement = 5, RULE_trailer = 6, RULE_block = 7, 
    RULE_empty = 8, RULE_initializer = 9, RULE_afterthought = 10, RULE_declaration = 11, 
    RULE_decltype = 12, RULE_type = 13, RULE_declvar = 14, RULE_trap = 15, 
    RULE_noncondexpression = 16, RULE_expression = 17, RULE_unary = 18, RULE_unarynotaddsub = 19, 
    RULE_castexpression = 20, RULE_primordefcasttype = 21, RULE_refcasttype = 22, 
    RULE_chain = 23, RULE_primary = 24, RULE_postfix = 25, RULE_postdot = 26, 
    RULE_callinvoke = 27, RULE_fieldaccess = 28, RULE_dotaccess = 29, RULE_braceaccess = 30, 
    RULE_arrayinitializer = 31, RULE_listinitializer = 32, RULE_mapinitializer = 33, 
    RULE_maptoken = 34, RULE_arguments = 35, RULE_argument = 36, RULE_lambda = 37, 
    RULE_lamtype = 38, RULE_funcref = 39;
  public static final String[] ruleNames = {
    "source", "function", "parameters", "statement", "rstatement", "dstatement", 
    "trailer", "block", "empty", "initializer", "afterthought", "declaration", 
    "decltype", "type", "declvar", "trap", "noncondexpression", "expression", 
    "unary", "unarynotaddsub", "castexpression", "primordefcasttype", "refcasttype", 
    "chain", "primary", "postfix", "postdot", "callinvoke", "fieldaccess", 
    "dotaccess", "braceaccess", "arrayinitializer", "listinitializer", "mapinitializer", 
    "maptoken", "arguments", "argument", "lambda", "lamtype", "funcref"
  };

  private static final String[] _LITERAL_NAMES = {
    null, null, null, "'{'", "'}'", "'['", "']'", "'('", "')'", "'.'", "'?.'", 
    "','", "';'", "'if'", "'in'", "'else'", "'while'", "'do'", "'for'", "'continue'", 
    "'break'", "'return'", "'new'", "'try'", "'catch'", "'throw'", "'this'", 
    "'instanceof'", "'!'", "'~'", "'*'", "'/'", "'%'", "'+'", "'-'", "'<<'", 
    "'>>'", "'>>>'", "'<'", "'<='", "'>'", "'>='", "'=='", "'==='", "'!='", 
    "'!=='", "'&'", "'^'", "'|'", "'&&'", "'||'", "'?'", "':'", "'?:'", "'::'", 
    "'->'", "'=~'", "'==~'", "'++'", "'--'", "'='", "'+='", "'-='", "'*='", 
    "'/='", "'%='", "'&='", "'^='", "'|='", "'<<='", "'>>='", "'>>>='", null, 
    null, null, null, null, null, "'true'", "'false'", "'null'", null, "'def'"
  };
  private static final String[] _SYMBOLIC_NAMES = {
    null, "WS", "COMMENT", "LBRACK", "RBRACK", "LBRACE", "RBRACE", "LP", "RP", 
    "DOT", "NSDOT", "COMMA", "SEMICOLON", "IF", "IN", "ELSE", "WHILE", "DO", 
    "FOR", "CONTINUE", "BREAK", "RETURN", "NEW", "TRY", "CATCH", "THROW", 
    "THIS", "INSTANCEOF", "BOOLNOT", "BWNOT", "MUL", "DIV", "REM", "ADD", 
    "SUB", "LSH", "RSH", "USH", "LT", "LTE", "GT", "GTE", "EQ", "EQR", "NE", 
    "NER", "BWAND", "XOR", "BWOR", "BOOLAND", "BOOLOR", "COND", "COLON", "ELVIS", 
    "REF", "ARROW", "FIND", "MATCH", "INCR", "DECR", "ASSIGN", "AADD", "ASUB", 
    "AMUL", "ADIV", "AREM", "AAND", "AXOR", "AOR", "ALSH", "ARSH", "AUSH", 
    "OCTAL", "HEX", "INTEGER", "DECIMAL", "STRING", "REGEX", "TRUE", "FALSE", 
    "NULL", "PRIMITIVE", "DEF", "ID", "DOTINTEGER", "DOTID", "DOTINGORE"
  };
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
  public String getGrammarFileName() { return "PainlessParser.g4"; }

  @Override
  public String[] getRuleNames() { return ruleNames; }

  @Override
  public String getSerializedATN() { return _serializedATN; }

  @Override
  public ATN getATN() { return _ATN; }

  public PainlessParser(TokenStream input) {
    super(input);
    _interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
  }
  public static class SourceContext extends ParserRuleContext {
    public TerminalNode EOF() { return getToken(PainlessParser.EOF, 0); }
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
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitSource(this);
      else return visitor.visitChildren(this);
    }
  }

  public final SourceContext source() throws RecognitionException {
    SourceContext _localctx = new SourceContext(_ctx, getState());
    enterRule(_localctx, 0, RULE_source);
    int _la;
    try {
      int _alt;
      enterOuterAlt(_localctx, 1);
      {
      setState(83);
      _errHandler.sync(this);
      _alt = getInterpreter().adaptivePredict(_input,0,_ctx);
      while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
        if ( _alt==1 ) {
          {
          {
          setState(80);
          function();
          }
          } 
        }
        setState(85);
        _errHandler.sync(this);
        _alt = getInterpreter().adaptivePredict(_input,0,_ctx);
      }
      setState(89);
      _errHandler.sync(this);
      _la = _input.LA(1);
      while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LBRACE) | (1L << LP) | (1L << IF) | (1L << WHILE) | (1L << DO) | (1L << FOR) | (1L << CONTINUE) | (1L << BREAK) | (1L << RETURN) | (1L << NEW) | (1L << TRY) | (1L << THROW) | (1L << BOOLNOT) | (1L << BWNOT) | (1L << ADD) | (1L << SUB) | (1L << INCR) | (1L << DECR))) != 0) || ((((_la - 72)) & ~0x3f) == 0 && ((1L << (_la - 72)) & ((1L << (OCTAL - 72)) | (1L << (HEX - 72)) | (1L << (INTEGER - 72)) | (1L << (DECIMAL - 72)) | (1L << (STRING - 72)) | (1L << (REGEX - 72)) | (1L << (TRUE - 72)) | (1L << (FALSE - 72)) | (1L << (NULL - 72)) | (1L << (PRIMITIVE - 72)) | (1L << (DEF - 72)) | (1L << (ID - 72)))) != 0)) {
        {
        {
        setState(86);
        statement();
        }
        }
        setState(91);
        _errHandler.sync(this);
        _la = _input.LA(1);
      }
      setState(92);
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

  public static class FunctionContext extends ParserRuleContext {
    public DecltypeContext decltype() {
      return getRuleContext(DecltypeContext.class,0);
    }
    public TerminalNode ID() { return getToken(PainlessParser.ID, 0); }
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
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitFunction(this);
      else return visitor.visitChildren(this);
    }
  }

  public final FunctionContext function() throws RecognitionException {
    FunctionContext _localctx = new FunctionContext(_ctx, getState());
    enterRule(_localctx, 2, RULE_function);
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(94);
      decltype();
      setState(95);
      match(ID);
      setState(96);
      parameters();
      setState(97);
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

  public static class ParametersContext extends ParserRuleContext {
    public TerminalNode LP() { return getToken(PainlessParser.LP, 0); }
    public TerminalNode RP() { return getToken(PainlessParser.RP, 0); }
    public List<DecltypeContext> decltype() {
      return getRuleContexts(DecltypeContext.class);
    }
    public DecltypeContext decltype(int i) {
      return getRuleContext(DecltypeContext.class,i);
    }
    public List<TerminalNode> ID() { return getTokens(PainlessParser.ID); }
    public TerminalNode ID(int i) {
      return getToken(PainlessParser.ID, i);
    }
    public List<TerminalNode> COMMA() { return getTokens(PainlessParser.COMMA); }
    public TerminalNode COMMA(int i) {
      return getToken(PainlessParser.COMMA, i);
    }
    public ParametersContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_parameters; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitParameters(this);
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
      setState(99);
      match(LP);
      setState(111);
      _la = _input.LA(1);
      if (((((_la - 81)) & ~0x3f) == 0 && ((1L << (_la - 81)) & ((1L << (PRIMITIVE - 81)) | (1L << (DEF - 81)) | (1L << (ID - 81)))) != 0)) {
        {
        setState(100);
        decltype();
        setState(101);
        match(ID);
        setState(108);
        _errHandler.sync(this);
        _la = _input.LA(1);
        while (_la==COMMA) {
          {
          {
          setState(102);
          match(COMMA);
          setState(103);
          decltype();
          setState(104);
          match(ID);
          }
          }
          setState(110);
          _errHandler.sync(this);
          _la = _input.LA(1);
        }
        }
      }

      setState(113);
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

  public static class StatementContext extends ParserRuleContext {
    public RstatementContext rstatement() {
      return getRuleContext(RstatementContext.class,0);
    }
    public DstatementContext dstatement() {
      return getRuleContext(DstatementContext.class,0);
    }
    public TerminalNode SEMICOLON() { return getToken(PainlessParser.SEMICOLON, 0); }
    public TerminalNode EOF() { return getToken(PainlessParser.EOF, 0); }
    public StatementContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_statement; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitStatement(this);
      else return visitor.visitChildren(this);
    }
  }

  public final StatementContext statement() throws RecognitionException {
    StatementContext _localctx = new StatementContext(_ctx, getState());
    enterRule(_localctx, 6, RULE_statement);
    int _la;
    try {
      setState(119);
      switch (_input.LA(1)) {
      case IF:
      case WHILE:
      case FOR:
      case TRY:
        enterOuterAlt(_localctx, 1);
        {
        setState(115);
        rstatement();
        }
        break;
      case LBRACE:
      case LP:
      case DO:
      case CONTINUE:
      case BREAK:
      case RETURN:
      case NEW:
      case THROW:
      case BOOLNOT:
      case BWNOT:
      case ADD:
      case SUB:
      case INCR:
      case DECR:
      case OCTAL:
      case HEX:
      case INTEGER:
      case DECIMAL:
      case STRING:
      case REGEX:
      case TRUE:
      case FALSE:
      case NULL:
      case PRIMITIVE:
      case DEF:
      case ID:
        enterOuterAlt(_localctx, 2);
        {
        setState(116);
        dstatement();
        setState(117);
        _la = _input.LA(1);
        if ( !(_la==EOF || _la==SEMICOLON) ) {
        _errHandler.recoverInline(this);
        } else {
          consume();
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
  public static class ForContext extends RstatementContext {
    public TerminalNode FOR() { return getToken(PainlessParser.FOR, 0); }
    public TerminalNode LP() { return getToken(PainlessParser.LP, 0); }
    public List<TerminalNode> SEMICOLON() { return getTokens(PainlessParser.SEMICOLON); }
    public TerminalNode SEMICOLON(int i) {
      return getToken(PainlessParser.SEMICOLON, i);
    }
    public TerminalNode RP() { return getToken(PainlessParser.RP, 0); }
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
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitFor(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class TryContext extends RstatementContext {
    public TerminalNode TRY() { return getToken(PainlessParser.TRY, 0); }
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
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitTry(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class WhileContext extends RstatementContext {
    public TerminalNode WHILE() { return getToken(PainlessParser.WHILE, 0); }
    public TerminalNode LP() { return getToken(PainlessParser.LP, 0); }
    public ExpressionContext expression() {
      return getRuleContext(ExpressionContext.class,0);
    }
    public TerminalNode RP() { return getToken(PainlessParser.RP, 0); }
    public TrailerContext trailer() {
      return getRuleContext(TrailerContext.class,0);
    }
    public EmptyContext empty() {
      return getRuleContext(EmptyContext.class,0);
    }
    public WhileContext(RstatementContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitWhile(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class IneachContext extends RstatementContext {
    public TerminalNode FOR() { return getToken(PainlessParser.FOR, 0); }
    public TerminalNode LP() { return getToken(PainlessParser.LP, 0); }
    public TerminalNode ID() { return getToken(PainlessParser.ID, 0); }
    public TerminalNode IN() { return getToken(PainlessParser.IN, 0); }
    public ExpressionContext expression() {
      return getRuleContext(ExpressionContext.class,0);
    }
    public TerminalNode RP() { return getToken(PainlessParser.RP, 0); }
    public TrailerContext trailer() {
      return getRuleContext(TrailerContext.class,0);
    }
    public IneachContext(RstatementContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitIneach(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class IfContext extends RstatementContext {
    public TerminalNode IF() { return getToken(PainlessParser.IF, 0); }
    public TerminalNode LP() { return getToken(PainlessParser.LP, 0); }
    public ExpressionContext expression() {
      return getRuleContext(ExpressionContext.class,0);
    }
    public TerminalNode RP() { return getToken(PainlessParser.RP, 0); }
    public List<TrailerContext> trailer() {
      return getRuleContexts(TrailerContext.class);
    }
    public TrailerContext trailer(int i) {
      return getRuleContext(TrailerContext.class,i);
    }
    public TerminalNode ELSE() { return getToken(PainlessParser.ELSE, 0); }
    public IfContext(RstatementContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitIf(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class EachContext extends RstatementContext {
    public TerminalNode FOR() { return getToken(PainlessParser.FOR, 0); }
    public TerminalNode LP() { return getToken(PainlessParser.LP, 0); }
    public DecltypeContext decltype() {
      return getRuleContext(DecltypeContext.class,0);
    }
    public TerminalNode ID() { return getToken(PainlessParser.ID, 0); }
    public TerminalNode COLON() { return getToken(PainlessParser.COLON, 0); }
    public ExpressionContext expression() {
      return getRuleContext(ExpressionContext.class,0);
    }
    public TerminalNode RP() { return getToken(PainlessParser.RP, 0); }
    public TrailerContext trailer() {
      return getRuleContext(TrailerContext.class,0);
    }
    public EachContext(RstatementContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitEach(this);
      else return visitor.visitChildren(this);
    }
  }

  public final RstatementContext rstatement() throws RecognitionException {
    RstatementContext _localctx = new RstatementContext(_ctx, getState());
    enterRule(_localctx, 8, RULE_rstatement);
    int _la;
    try {
      int _alt;
      setState(181);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
      case 1:
        _localctx = new IfContext(_localctx);
        enterOuterAlt(_localctx, 1);
        {
        setState(121);
        match(IF);
        setState(122);
        match(LP);
        setState(123);
        expression();
        setState(124);
        match(RP);
        setState(125);
        trailer();
        setState(129);
        _errHandler.sync(this);
        switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
        case 1:
          {
          setState(126);
          match(ELSE);
          setState(127);
          trailer();
          }
          break;
        case 2:
          {
          setState(128);
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
        setState(131);
        match(WHILE);
        setState(132);
        match(LP);
        setState(133);
        expression();
        setState(134);
        match(RP);
        setState(137);
        switch (_input.LA(1)) {
        case LBRACK:
        case LBRACE:
        case LP:
        case IF:
        case WHILE:
        case DO:
        case FOR:
        case CONTINUE:
        case BREAK:
        case RETURN:
        case NEW:
        case TRY:
        case THROW:
        case BOOLNOT:
        case BWNOT:
        case ADD:
        case SUB:
        case INCR:
        case DECR:
        case OCTAL:
        case HEX:
        case INTEGER:
        case DECIMAL:
        case STRING:
        case REGEX:
        case TRUE:
        case FALSE:
        case NULL:
        case PRIMITIVE:
        case DEF:
        case ID:
          {
          setState(135);
          trailer();
          }
          break;
        case SEMICOLON:
          {
          setState(136);
          empty();
          }
          break;
        default:
          throw new NoViableAltException(this);
        }
        }
        break;
      case 3:
        _localctx = new ForContext(_localctx);
        enterOuterAlt(_localctx, 3);
        {
        setState(139);
        match(FOR);
        setState(140);
        match(LP);
        setState(142);
        _la = _input.LA(1);
        if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LBRACE) | (1L << LP) | (1L << NEW) | (1L << BOOLNOT) | (1L << BWNOT) | (1L << ADD) | (1L << SUB) | (1L << INCR) | (1L << DECR))) != 0) || ((((_la - 72)) & ~0x3f) == 0 && ((1L << (_la - 72)) & ((1L << (OCTAL - 72)) | (1L << (HEX - 72)) | (1L << (INTEGER - 72)) | (1L << (DECIMAL - 72)) | (1L << (STRING - 72)) | (1L << (REGEX - 72)) | (1L << (TRUE - 72)) | (1L << (FALSE - 72)) | (1L << (NULL - 72)) | (1L << (PRIMITIVE - 72)) | (1L << (DEF - 72)) | (1L << (ID - 72)))) != 0)) {
          {
          setState(141);
          initializer();
          }
        }

        setState(144);
        match(SEMICOLON);
        setState(146);
        _la = _input.LA(1);
        if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LBRACE) | (1L << LP) | (1L << NEW) | (1L << BOOLNOT) | (1L << BWNOT) | (1L << ADD) | (1L << SUB) | (1L << INCR) | (1L << DECR))) != 0) || ((((_la - 72)) & ~0x3f) == 0 && ((1L << (_la - 72)) & ((1L << (OCTAL - 72)) | (1L << (HEX - 72)) | (1L << (INTEGER - 72)) | (1L << (DECIMAL - 72)) | (1L << (STRING - 72)) | (1L << (REGEX - 72)) | (1L << (TRUE - 72)) | (1L << (FALSE - 72)) | (1L << (NULL - 72)) | (1L << (ID - 72)))) != 0)) {
          {
          setState(145);
          expression();
          }
        }

        setState(148);
        match(SEMICOLON);
        setState(150);
        _la = _input.LA(1);
        if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LBRACE) | (1L << LP) | (1L << NEW) | (1L << BOOLNOT) | (1L << BWNOT) | (1L << ADD) | (1L << SUB) | (1L << INCR) | (1L << DECR))) != 0) || ((((_la - 72)) & ~0x3f) == 0 && ((1L << (_la - 72)) & ((1L << (OCTAL - 72)) | (1L << (HEX - 72)) | (1L << (INTEGER - 72)) | (1L << (DECIMAL - 72)) | (1L << (STRING - 72)) | (1L << (REGEX - 72)) | (1L << (TRUE - 72)) | (1L << (FALSE - 72)) | (1L << (NULL - 72)) | (1L << (ID - 72)))) != 0)) {
          {
          setState(149);
          afterthought();
          }
        }

        setState(152);
        match(RP);
        setState(155);
        switch (_input.LA(1)) {
        case LBRACK:
        case LBRACE:
        case LP:
        case IF:
        case WHILE:
        case DO:
        case FOR:
        case CONTINUE:
        case BREAK:
        case RETURN:
        case NEW:
        case TRY:
        case THROW:
        case BOOLNOT:
        case BWNOT:
        case ADD:
        case SUB:
        case INCR:
        case DECR:
        case OCTAL:
        case HEX:
        case INTEGER:
        case DECIMAL:
        case STRING:
        case REGEX:
        case TRUE:
        case FALSE:
        case NULL:
        case PRIMITIVE:
        case DEF:
        case ID:
          {
          setState(153);
          trailer();
          }
          break;
        case SEMICOLON:
          {
          setState(154);
          empty();
          }
          break;
        default:
          throw new NoViableAltException(this);
        }
        }
        break;
      case 4:
        _localctx = new EachContext(_localctx);
        enterOuterAlt(_localctx, 4);
        {
        setState(157);
        match(FOR);
        setState(158);
        match(LP);
        setState(159);
        decltype();
        setState(160);
        match(ID);
        setState(161);
        match(COLON);
        setState(162);
        expression();
        setState(163);
        match(RP);
        setState(164);
        trailer();
        }
        break;
      case 5:
        _localctx = new IneachContext(_localctx);
        enterOuterAlt(_localctx, 5);
        {
        setState(166);
        match(FOR);
        setState(167);
        match(LP);
        setState(168);
        match(ID);
        setState(169);
        match(IN);
        setState(170);
        expression();
        setState(171);
        match(RP);
        setState(172);
        trailer();
        }
        break;
      case 6:
        _localctx = new TryContext(_localctx);
        enterOuterAlt(_localctx, 6);
        {
        setState(174);
        match(TRY);
        setState(175);
        block();
        setState(177); 
        _errHandler.sync(this);
        _alt = 1;
        do {
          switch (_alt) {
          case 1:
            {
            {
            setState(176);
            trap();
            }
            }
            break;
          default:
            throw new NoViableAltException(this);
          }
          setState(179); 
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
  public static class DeclContext extends DstatementContext {
    public DeclarationContext declaration() {
      return getRuleContext(DeclarationContext.class,0);
    }
    public DeclContext(DstatementContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitDecl(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class BreakContext extends DstatementContext {
    public TerminalNode BREAK() { return getToken(PainlessParser.BREAK, 0); }
    public BreakContext(DstatementContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitBreak(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class ThrowContext extends DstatementContext {
    public TerminalNode THROW() { return getToken(PainlessParser.THROW, 0); }
    public ExpressionContext expression() {
      return getRuleContext(ExpressionContext.class,0);
    }
    public ThrowContext(DstatementContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitThrow(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class ContinueContext extends DstatementContext {
    public TerminalNode CONTINUE() { return getToken(PainlessParser.CONTINUE, 0); }
    public ContinueContext(DstatementContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitContinue(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class ExprContext extends DstatementContext {
    public ExpressionContext expression() {
      return getRuleContext(ExpressionContext.class,0);
    }
    public ExprContext(DstatementContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitExpr(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class DoContext extends DstatementContext {
    public TerminalNode DO() { return getToken(PainlessParser.DO, 0); }
    public BlockContext block() {
      return getRuleContext(BlockContext.class,0);
    }
    public TerminalNode WHILE() { return getToken(PainlessParser.WHILE, 0); }
    public TerminalNode LP() { return getToken(PainlessParser.LP, 0); }
    public ExpressionContext expression() {
      return getRuleContext(ExpressionContext.class,0);
    }
    public TerminalNode RP() { return getToken(PainlessParser.RP, 0); }
    public DoContext(DstatementContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitDo(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class ReturnContext extends DstatementContext {
    public TerminalNode RETURN() { return getToken(PainlessParser.RETURN, 0); }
    public ExpressionContext expression() {
      return getRuleContext(ExpressionContext.class,0);
    }
    public ReturnContext(DstatementContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitReturn(this);
      else return visitor.visitChildren(this);
    }
  }

  public final DstatementContext dstatement() throws RecognitionException {
    DstatementContext _localctx = new DstatementContext(_ctx, getState());
    enterRule(_localctx, 10, RULE_dstatement);
    int _la;
    try {
      setState(200);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
      case 1:
        _localctx = new DoContext(_localctx);
        enterOuterAlt(_localctx, 1);
        {
        setState(183);
        match(DO);
        setState(184);
        block();
        setState(185);
        match(WHILE);
        setState(186);
        match(LP);
        setState(187);
        expression();
        setState(188);
        match(RP);
        }
        break;
      case 2:
        _localctx = new DeclContext(_localctx);
        enterOuterAlt(_localctx, 2);
        {
        setState(190);
        declaration();
        }
        break;
      case 3:
        _localctx = new ContinueContext(_localctx);
        enterOuterAlt(_localctx, 3);
        {
        setState(191);
        match(CONTINUE);
        }
        break;
      case 4:
        _localctx = new BreakContext(_localctx);
        enterOuterAlt(_localctx, 4);
        {
        setState(192);
        match(BREAK);
        }
        break;
      case 5:
        _localctx = new ReturnContext(_localctx);
        enterOuterAlt(_localctx, 5);
        {
        setState(193);
        match(RETURN);
        setState(195);
        _la = _input.LA(1);
        if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LBRACE) | (1L << LP) | (1L << NEW) | (1L << BOOLNOT) | (1L << BWNOT) | (1L << ADD) | (1L << SUB) | (1L << INCR) | (1L << DECR))) != 0) || ((((_la - 72)) & ~0x3f) == 0 && ((1L << (_la - 72)) & ((1L << (OCTAL - 72)) | (1L << (HEX - 72)) | (1L << (INTEGER - 72)) | (1L << (DECIMAL - 72)) | (1L << (STRING - 72)) | (1L << (REGEX - 72)) | (1L << (TRUE - 72)) | (1L << (FALSE - 72)) | (1L << (NULL - 72)) | (1L << (ID - 72)))) != 0)) {
          {
          setState(194);
          expression();
          }
        }

        }
        break;
      case 6:
        _localctx = new ThrowContext(_localctx);
        enterOuterAlt(_localctx, 6);
        {
        setState(197);
        match(THROW);
        setState(198);
        expression();
        }
        break;
      case 7:
        _localctx = new ExprContext(_localctx);
        enterOuterAlt(_localctx, 7);
        {
        setState(199);
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
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitTrailer(this);
      else return visitor.visitChildren(this);
    }
  }

  public final TrailerContext trailer() throws RecognitionException {
    TrailerContext _localctx = new TrailerContext(_ctx, getState());
    enterRule(_localctx, 12, RULE_trailer);
    try {
      setState(204);
      switch (_input.LA(1)) {
      case LBRACK:
        enterOuterAlt(_localctx, 1);
        {
        setState(202);
        block();
        }
        break;
      case LBRACE:
      case LP:
      case IF:
      case WHILE:
      case DO:
      case FOR:
      case CONTINUE:
      case BREAK:
      case RETURN:
      case NEW:
      case TRY:
      case THROW:
      case BOOLNOT:
      case BWNOT:
      case ADD:
      case SUB:
      case INCR:
      case DECR:
      case OCTAL:
      case HEX:
      case INTEGER:
      case DECIMAL:
      case STRING:
      case REGEX:
      case TRUE:
      case FALSE:
      case NULL:
      case PRIMITIVE:
      case DEF:
      case ID:
        enterOuterAlt(_localctx, 2);
        {
        setState(203);
        statement();
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

  public static class BlockContext extends ParserRuleContext {
    public TerminalNode LBRACK() { return getToken(PainlessParser.LBRACK, 0); }
    public TerminalNode RBRACK() { return getToken(PainlessParser.RBRACK, 0); }
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
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitBlock(this);
      else return visitor.visitChildren(this);
    }
  }

  public final BlockContext block() throws RecognitionException {
    BlockContext _localctx = new BlockContext(_ctx, getState());
    enterRule(_localctx, 14, RULE_block);
    int _la;
    try {
      int _alt;
      enterOuterAlt(_localctx, 1);
      {
      setState(206);
      match(LBRACK);
      setState(210);
      _errHandler.sync(this);
      _alt = getInterpreter().adaptivePredict(_input,16,_ctx);
      while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
        if ( _alt==1 ) {
          {
          {
          setState(207);
          statement();
          }
          } 
        }
        setState(212);
        _errHandler.sync(this);
        _alt = getInterpreter().adaptivePredict(_input,16,_ctx);
      }
      setState(214);
      _la = _input.LA(1);
      if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LBRACE) | (1L << LP) | (1L << DO) | (1L << CONTINUE) | (1L << BREAK) | (1L << RETURN) | (1L << NEW) | (1L << THROW) | (1L << BOOLNOT) | (1L << BWNOT) | (1L << ADD) | (1L << SUB) | (1L << INCR) | (1L << DECR))) != 0) || ((((_la - 72)) & ~0x3f) == 0 && ((1L << (_la - 72)) & ((1L << (OCTAL - 72)) | (1L << (HEX - 72)) | (1L << (INTEGER - 72)) | (1L << (DECIMAL - 72)) | (1L << (STRING - 72)) | (1L << (REGEX - 72)) | (1L << (TRUE - 72)) | (1L << (FALSE - 72)) | (1L << (NULL - 72)) | (1L << (PRIMITIVE - 72)) | (1L << (DEF - 72)) | (1L << (ID - 72)))) != 0)) {
        {
        setState(213);
        dstatement();
        }
      }

      setState(216);
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

  public static class EmptyContext extends ParserRuleContext {
    public TerminalNode SEMICOLON() { return getToken(PainlessParser.SEMICOLON, 0); }
    public EmptyContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_empty; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitEmpty(this);
      else return visitor.visitChildren(this);
    }
  }

  public final EmptyContext empty() throws RecognitionException {
    EmptyContext _localctx = new EmptyContext(_ctx, getState());
    enterRule(_localctx, 16, RULE_empty);
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(218);
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
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitInitializer(this);
      else return visitor.visitChildren(this);
    }
  }

  public final InitializerContext initializer() throws RecognitionException {
    InitializerContext _localctx = new InitializerContext(_ctx, getState());
    enterRule(_localctx, 18, RULE_initializer);
    try {
      setState(222);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,18,_ctx) ) {
      case 1:
        enterOuterAlt(_localctx, 1);
        {
        setState(220);
        declaration();
        }
        break;
      case 2:
        enterOuterAlt(_localctx, 2);
        {
        setState(221);
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
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitAfterthought(this);
      else return visitor.visitChildren(this);
    }
  }

  public final AfterthoughtContext afterthought() throws RecognitionException {
    AfterthoughtContext _localctx = new AfterthoughtContext(_ctx, getState());
    enterRule(_localctx, 20, RULE_afterthought);
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(224);
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
    public List<TerminalNode> COMMA() { return getTokens(PainlessParser.COMMA); }
    public TerminalNode COMMA(int i) {
      return getToken(PainlessParser.COMMA, i);
    }
    public DeclarationContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_declaration; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitDeclaration(this);
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
      setState(226);
      decltype();
      setState(227);
      declvar();
      setState(232);
      _errHandler.sync(this);
      _la = _input.LA(1);
      while (_la==COMMA) {
        {
        {
        setState(228);
        match(COMMA);
        setState(229);
        declvar();
        }
        }
        setState(234);
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

  public static class DecltypeContext extends ParserRuleContext {
    public TypeContext type() {
      return getRuleContext(TypeContext.class,0);
    }
    public List<TerminalNode> LBRACE() { return getTokens(PainlessParser.LBRACE); }
    public TerminalNode LBRACE(int i) {
      return getToken(PainlessParser.LBRACE, i);
    }
    public List<TerminalNode> RBRACE() { return getTokens(PainlessParser.RBRACE); }
    public TerminalNode RBRACE(int i) {
      return getToken(PainlessParser.RBRACE, i);
    }
    public DecltypeContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_decltype; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitDecltype(this);
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
      setState(235);
      type();
      setState(240);
      _errHandler.sync(this);
      _alt = getInterpreter().adaptivePredict(_input,20,_ctx);
      while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
        if ( _alt==1 ) {
          {
          {
          setState(236);
          match(LBRACE);
          setState(237);
          match(RBRACE);
          }
          } 
        }
        setState(242);
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

  public static class TypeContext extends ParserRuleContext {
    public TerminalNode DEF() { return getToken(PainlessParser.DEF, 0); }
    public TerminalNode PRIMITIVE() { return getToken(PainlessParser.PRIMITIVE, 0); }
    public TerminalNode ID() { return getToken(PainlessParser.ID, 0); }
    public List<TerminalNode> DOT() { return getTokens(PainlessParser.DOT); }
    public TerminalNode DOT(int i) {
      return getToken(PainlessParser.DOT, i);
    }
    public List<TerminalNode> DOTID() { return getTokens(PainlessParser.DOTID); }
    public TerminalNode DOTID(int i) {
      return getToken(PainlessParser.DOTID, i);
    }
    public TypeContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_type; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitType(this);
      else return visitor.visitChildren(this);
    }
  }

  public final TypeContext type() throws RecognitionException {
    TypeContext _localctx = new TypeContext(_ctx, getState());
    enterRule(_localctx, 26, RULE_type);
    try {
      int _alt;
      setState(253);
      switch (_input.LA(1)) {
      case DEF:
        enterOuterAlt(_localctx, 1);
        {
        setState(243);
        match(DEF);
        }
        break;
      case PRIMITIVE:
        enterOuterAlt(_localctx, 2);
        {
        setState(244);
        match(PRIMITIVE);
        }
        break;
      case ID:
        enterOuterAlt(_localctx, 3);
        {
        setState(245);
        match(ID);
        setState(250);
        _errHandler.sync(this);
        _alt = getInterpreter().adaptivePredict(_input,21,_ctx);
        while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
          if ( _alt==1 ) {
            {
            {
            setState(246);
            match(DOT);
            setState(247);
            match(DOTID);
            }
            } 
          }
          setState(252);
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

  public static class DeclvarContext extends ParserRuleContext {
    public TerminalNode ID() { return getToken(PainlessParser.ID, 0); }
    public TerminalNode ASSIGN() { return getToken(PainlessParser.ASSIGN, 0); }
    public ExpressionContext expression() {
      return getRuleContext(ExpressionContext.class,0);
    }
    public DeclvarContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_declvar; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitDeclvar(this);
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
      setState(255);
      match(ID);
      setState(258);
      _la = _input.LA(1);
      if (_la==ASSIGN) {
        {
        setState(256);
        match(ASSIGN);
        setState(257);
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

  public static class TrapContext extends ParserRuleContext {
    public TerminalNode CATCH() { return getToken(PainlessParser.CATCH, 0); }
    public TerminalNode LP() { return getToken(PainlessParser.LP, 0); }
    public TypeContext type() {
      return getRuleContext(TypeContext.class,0);
    }
    public TerminalNode ID() { return getToken(PainlessParser.ID, 0); }
    public TerminalNode RP() { return getToken(PainlessParser.RP, 0); }
    public BlockContext block() {
      return getRuleContext(BlockContext.class,0);
    }
    public TrapContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_trap; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitTrap(this);
      else return visitor.visitChildren(this);
    }
  }

  public final TrapContext trap() throws RecognitionException {
    TrapContext _localctx = new TrapContext(_ctx, getState());
    enterRule(_localctx, 30, RULE_trap);
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(260);
      match(CATCH);
      setState(261);
      match(LP);
      setState(262);
      type();
      setState(263);
      match(ID);
      setState(264);
      match(RP);
      setState(265);
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
  public static class SingleContext extends NoncondexpressionContext {
    public UnaryContext unary() {
      return getRuleContext(UnaryContext.class,0);
    }
    public SingleContext(NoncondexpressionContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitSingle(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class CompContext extends NoncondexpressionContext {
    public List<NoncondexpressionContext> noncondexpression() {
      return getRuleContexts(NoncondexpressionContext.class);
    }
    public NoncondexpressionContext noncondexpression(int i) {
      return getRuleContext(NoncondexpressionContext.class,i);
    }
    public TerminalNode LT() { return getToken(PainlessParser.LT, 0); }
    public TerminalNode LTE() { return getToken(PainlessParser.LTE, 0); }
    public TerminalNode GT() { return getToken(PainlessParser.GT, 0); }
    public TerminalNode GTE() { return getToken(PainlessParser.GTE, 0); }
    public TerminalNode EQ() { return getToken(PainlessParser.EQ, 0); }
    public TerminalNode EQR() { return getToken(PainlessParser.EQR, 0); }
    public TerminalNode NE() { return getToken(PainlessParser.NE, 0); }
    public TerminalNode NER() { return getToken(PainlessParser.NER, 0); }
    public CompContext(NoncondexpressionContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitComp(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class BoolContext extends NoncondexpressionContext {
    public List<NoncondexpressionContext> noncondexpression() {
      return getRuleContexts(NoncondexpressionContext.class);
    }
    public NoncondexpressionContext noncondexpression(int i) {
      return getRuleContext(NoncondexpressionContext.class,i);
    }
    public TerminalNode BOOLAND() { return getToken(PainlessParser.BOOLAND, 0); }
    public TerminalNode BOOLOR() { return getToken(PainlessParser.BOOLOR, 0); }
    public BoolContext(NoncondexpressionContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitBool(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class BinaryContext extends NoncondexpressionContext {
    public List<NoncondexpressionContext> noncondexpression() {
      return getRuleContexts(NoncondexpressionContext.class);
    }
    public NoncondexpressionContext noncondexpression(int i) {
      return getRuleContext(NoncondexpressionContext.class,i);
    }
    public TerminalNode MUL() { return getToken(PainlessParser.MUL, 0); }
    public TerminalNode DIV() { return getToken(PainlessParser.DIV, 0); }
    public TerminalNode REM() { return getToken(PainlessParser.REM, 0); }
    public TerminalNode ADD() { return getToken(PainlessParser.ADD, 0); }
    public TerminalNode SUB() { return getToken(PainlessParser.SUB, 0); }
    public TerminalNode FIND() { return getToken(PainlessParser.FIND, 0); }
    public TerminalNode MATCH() { return getToken(PainlessParser.MATCH, 0); }
    public TerminalNode LSH() { return getToken(PainlessParser.LSH, 0); }
    public TerminalNode RSH() { return getToken(PainlessParser.RSH, 0); }
    public TerminalNode USH() { return getToken(PainlessParser.USH, 0); }
    public TerminalNode BWAND() { return getToken(PainlessParser.BWAND, 0); }
    public TerminalNode XOR() { return getToken(PainlessParser.XOR, 0); }
    public TerminalNode BWOR() { return getToken(PainlessParser.BWOR, 0); }
    public BinaryContext(NoncondexpressionContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitBinary(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class ElvisContext extends NoncondexpressionContext {
    public List<NoncondexpressionContext> noncondexpression() {
      return getRuleContexts(NoncondexpressionContext.class);
    }
    public NoncondexpressionContext noncondexpression(int i) {
      return getRuleContext(NoncondexpressionContext.class,i);
    }
    public TerminalNode ELVIS() { return getToken(PainlessParser.ELVIS, 0); }
    public ElvisContext(NoncondexpressionContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitElvis(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class InstanceofContext extends NoncondexpressionContext {
    public NoncondexpressionContext noncondexpression() {
      return getRuleContext(NoncondexpressionContext.class,0);
    }
    public TerminalNode INSTANCEOF() { return getToken(PainlessParser.INSTANCEOF, 0); }
    public DecltypeContext decltype() {
      return getRuleContext(DecltypeContext.class,0);
    }
    public InstanceofContext(NoncondexpressionContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitInstanceof(this);
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

      setState(268);
      unary();
      }
      _ctx.stop = _input.LT(-1);
      setState(311);
      _errHandler.sync(this);
      _alt = getInterpreter().adaptivePredict(_input,25,_ctx);
      while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
        if ( _alt==1 ) {
          if ( _parseListeners!=null ) triggerExitRuleEvent();
          _prevctx = _localctx;
          {
          setState(309);
          _errHandler.sync(this);
          switch ( getInterpreter().adaptivePredict(_input,24,_ctx) ) {
          case 1:
            {
            _localctx = new BinaryContext(new NoncondexpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_noncondexpression);
            setState(270);
            if (!(precpred(_ctx, 13))) throw new FailedPredicateException(this, "precpred(_ctx, 13)");
            setState(271);
            _la = _input.LA(1);
            if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << MUL) | (1L << DIV) | (1L << REM))) != 0)) ) {
            _errHandler.recoverInline(this);
            } else {
              consume();
            }
            setState(272);
            noncondexpression(14);
            }
            break;
          case 2:
            {
            _localctx = new BinaryContext(new NoncondexpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_noncondexpression);
            setState(273);
            if (!(precpred(_ctx, 12))) throw new FailedPredicateException(this, "precpred(_ctx, 12)");
            setState(274);
            _la = _input.LA(1);
            if ( !(_la==ADD || _la==SUB) ) {
            _errHandler.recoverInline(this);
            } else {
              consume();
            }
            setState(275);
            noncondexpression(13);
            }
            break;
          case 3:
            {
            _localctx = new BinaryContext(new NoncondexpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_noncondexpression);
            setState(276);
            if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
            setState(277);
            _la = _input.LA(1);
            if ( !(_la==FIND || _la==MATCH) ) {
            _errHandler.recoverInline(this);
            } else {
              consume();
            }
            setState(278);
            noncondexpression(12);
            }
            break;
          case 4:
            {
            _localctx = new BinaryContext(new NoncondexpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_noncondexpression);
            setState(279);
            if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
            setState(280);
            _la = _input.LA(1);
            if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LSH) | (1L << RSH) | (1L << USH))) != 0)) ) {
            _errHandler.recoverInline(this);
            } else {
              consume();
            }
            setState(281);
            noncondexpression(11);
            }
            break;
          case 5:
            {
            _localctx = new CompContext(new NoncondexpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_noncondexpression);
            setState(282);
            if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
            setState(283);
            _la = _input.LA(1);
            if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LT) | (1L << LTE) | (1L << GT) | (1L << GTE))) != 0)) ) {
            _errHandler.recoverInline(this);
            } else {
              consume();
            }
            setState(284);
            noncondexpression(10);
            }
            break;
          case 6:
            {
            _localctx = new CompContext(new NoncondexpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_noncondexpression);
            setState(285);
            if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
            setState(286);
            _la = _input.LA(1);
            if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << EQ) | (1L << EQR) | (1L << NE) | (1L << NER))) != 0)) ) {
            _errHandler.recoverInline(this);
            } else {
              consume();
            }
            setState(287);
            noncondexpression(8);
            }
            break;
          case 7:
            {
            _localctx = new BinaryContext(new NoncondexpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_noncondexpression);
            setState(288);
            if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
            setState(289);
            match(BWAND);
            setState(290);
            noncondexpression(7);
            }
            break;
          case 8:
            {
            _localctx = new BinaryContext(new NoncondexpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_noncondexpression);
            setState(291);
            if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
            setState(292);
            match(XOR);
            setState(293);
            noncondexpression(6);
            }
            break;
          case 9:
            {
            _localctx = new BinaryContext(new NoncondexpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_noncondexpression);
            setState(294);
            if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
            setState(295);
            match(BWOR);
            setState(296);
            noncondexpression(5);
            }
            break;
          case 10:
            {
            _localctx = new BoolContext(new NoncondexpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_noncondexpression);
            setState(297);
            if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
            setState(298);
            match(BOOLAND);
            setState(299);
            noncondexpression(4);
            }
            break;
          case 11:
            {
            _localctx = new BoolContext(new NoncondexpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_noncondexpression);
            setState(300);
            if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
            setState(301);
            match(BOOLOR);
            setState(302);
            noncondexpression(3);
            }
            break;
          case 12:
            {
            _localctx = new ElvisContext(new NoncondexpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_noncondexpression);
            setState(303);
            if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
            setState(304);
            match(ELVIS);
            setState(305);
            noncondexpression(1);
            }
            break;
          case 13:
            {
            _localctx = new InstanceofContext(new NoncondexpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_noncondexpression);
            setState(306);
            if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
            setState(307);
            match(INSTANCEOF);
            setState(308);
            decltype();
            }
            break;
          }
          } 
        }
        setState(313);
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
  public static class ConditionalContext extends ExpressionContext {
    public NoncondexpressionContext noncondexpression() {
      return getRuleContext(NoncondexpressionContext.class,0);
    }
    public TerminalNode COND() { return getToken(PainlessParser.COND, 0); }
    public List<ExpressionContext> expression() {
      return getRuleContexts(ExpressionContext.class);
    }
    public ExpressionContext expression(int i) {
      return getRuleContext(ExpressionContext.class,i);
    }
    public TerminalNode COLON() { return getToken(PainlessParser.COLON, 0); }
    public ConditionalContext(ExpressionContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitConditional(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class AssignmentContext extends ExpressionContext {
    public NoncondexpressionContext noncondexpression() {
      return getRuleContext(NoncondexpressionContext.class,0);
    }
    public ExpressionContext expression() {
      return getRuleContext(ExpressionContext.class,0);
    }
    public TerminalNode ASSIGN() { return getToken(PainlessParser.ASSIGN, 0); }
    public TerminalNode AADD() { return getToken(PainlessParser.AADD, 0); }
    public TerminalNode ASUB() { return getToken(PainlessParser.ASUB, 0); }
    public TerminalNode AMUL() { return getToken(PainlessParser.AMUL, 0); }
    public TerminalNode ADIV() { return getToken(PainlessParser.ADIV, 0); }
    public TerminalNode AREM() { return getToken(PainlessParser.AREM, 0); }
    public TerminalNode AAND() { return getToken(PainlessParser.AAND, 0); }
    public TerminalNode AXOR() { return getToken(PainlessParser.AXOR, 0); }
    public TerminalNode AOR() { return getToken(PainlessParser.AOR, 0); }
    public TerminalNode ALSH() { return getToken(PainlessParser.ALSH, 0); }
    public TerminalNode ARSH() { return getToken(PainlessParser.ARSH, 0); }
    public TerminalNode AUSH() { return getToken(PainlessParser.AUSH, 0); }
    public AssignmentContext(ExpressionContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitAssignment(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class NonconditionalContext extends ExpressionContext {
    public NoncondexpressionContext noncondexpression() {
      return getRuleContext(NoncondexpressionContext.class,0);
    }
    public NonconditionalContext(ExpressionContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitNonconditional(this);
      else return visitor.visitChildren(this);
    }
  }

  public final ExpressionContext expression() throws RecognitionException {
    ExpressionContext _localctx = new ExpressionContext(_ctx, getState());
    enterRule(_localctx, 34, RULE_expression);
    int _la;
    try {
      setState(325);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,26,_ctx) ) {
      case 1:
        _localctx = new NonconditionalContext(_localctx);
        enterOuterAlt(_localctx, 1);
        {
        setState(314);
        noncondexpression(0);
        }
        break;
      case 2:
        _localctx = new ConditionalContext(_localctx);
        enterOuterAlt(_localctx, 2);
        {
        setState(315);
        noncondexpression(0);
        setState(316);
        match(COND);
        setState(317);
        expression();
        setState(318);
        match(COLON);
        setState(319);
        expression();
        }
        break;
      case 3:
        _localctx = new AssignmentContext(_localctx);
        enterOuterAlt(_localctx, 3);
        {
        setState(321);
        noncondexpression(0);
        setState(322);
        _la = _input.LA(1);
        if ( !(((((_la - 60)) & ~0x3f) == 0 && ((1L << (_la - 60)) & ((1L << (ASSIGN - 60)) | (1L << (AADD - 60)) | (1L << (ASUB - 60)) | (1L << (AMUL - 60)) | (1L << (ADIV - 60)) | (1L << (AREM - 60)) | (1L << (AAND - 60)) | (1L << (AXOR - 60)) | (1L << (AOR - 60)) | (1L << (ALSH - 60)) | (1L << (ARSH - 60)) | (1L << (AUSH - 60)))) != 0)) ) {
        _errHandler.recoverInline(this);
        } else {
          consume();
        }
        setState(323);
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
  public static class NotaddsubContext extends UnaryContext {
    public UnarynotaddsubContext unarynotaddsub() {
      return getRuleContext(UnarynotaddsubContext.class,0);
    }
    public NotaddsubContext(UnaryContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitNotaddsub(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class PreContext extends UnaryContext {
    public ChainContext chain() {
      return getRuleContext(ChainContext.class,0);
    }
    public TerminalNode INCR() { return getToken(PainlessParser.INCR, 0); }
    public TerminalNode DECR() { return getToken(PainlessParser.DECR, 0); }
    public PreContext(UnaryContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitPre(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class AddsubContext extends UnaryContext {
    public UnaryContext unary() {
      return getRuleContext(UnaryContext.class,0);
    }
    public TerminalNode ADD() { return getToken(PainlessParser.ADD, 0); }
    public TerminalNode SUB() { return getToken(PainlessParser.SUB, 0); }
    public AddsubContext(UnaryContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitAddsub(this);
      else return visitor.visitChildren(this);
    }
  }

  public final UnaryContext unary() throws RecognitionException {
    UnaryContext _localctx = new UnaryContext(_ctx, getState());
    enterRule(_localctx, 36, RULE_unary);
    int _la;
    try {
      setState(332);
      switch (_input.LA(1)) {
      case INCR:
      case DECR:
        _localctx = new PreContext(_localctx);
        enterOuterAlt(_localctx, 1);
        {
        setState(327);
        _la = _input.LA(1);
        if ( !(_la==INCR || _la==DECR) ) {
        _errHandler.recoverInline(this);
        } else {
          consume();
        }
        setState(328);
        chain();
        }
        break;
      case ADD:
      case SUB:
        _localctx = new AddsubContext(_localctx);
        enterOuterAlt(_localctx, 2);
        {
        setState(329);
        _la = _input.LA(1);
        if ( !(_la==ADD || _la==SUB) ) {
        _errHandler.recoverInline(this);
        } else {
          consume();
        }
        setState(330);
        unary();
        }
        break;
      case LBRACE:
      case LP:
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
        setState(331);
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
  public static class CastContext extends UnarynotaddsubContext {
    public CastexpressionContext castexpression() {
      return getRuleContext(CastexpressionContext.class,0);
    }
    public CastContext(UnarynotaddsubContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitCast(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class NotContext extends UnarynotaddsubContext {
    public UnaryContext unary() {
      return getRuleContext(UnaryContext.class,0);
    }
    public TerminalNode BOOLNOT() { return getToken(PainlessParser.BOOLNOT, 0); }
    public TerminalNode BWNOT() { return getToken(PainlessParser.BWNOT, 0); }
    public NotContext(UnarynotaddsubContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitNot(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class ReadContext extends UnarynotaddsubContext {
    public ChainContext chain() {
      return getRuleContext(ChainContext.class,0);
    }
    public ReadContext(UnarynotaddsubContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitRead(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class PostContext extends UnarynotaddsubContext {
    public ChainContext chain() {
      return getRuleContext(ChainContext.class,0);
    }
    public TerminalNode INCR() { return getToken(PainlessParser.INCR, 0); }
    public TerminalNode DECR() { return getToken(PainlessParser.DECR, 0); }
    public PostContext(UnarynotaddsubContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitPost(this);
      else return visitor.visitChildren(this);
    }
  }

  public final UnarynotaddsubContext unarynotaddsub() throws RecognitionException {
    UnarynotaddsubContext _localctx = new UnarynotaddsubContext(_ctx, getState());
    enterRule(_localctx, 38, RULE_unarynotaddsub);
    int _la;
    try {
      setState(341);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,28,_ctx) ) {
      case 1:
        _localctx = new ReadContext(_localctx);
        enterOuterAlt(_localctx, 1);
        {
        setState(334);
        chain();
        }
        break;
      case 2:
        _localctx = new PostContext(_localctx);
        enterOuterAlt(_localctx, 2);
        {
        setState(335);
        chain();
        setState(336);
        _la = _input.LA(1);
        if ( !(_la==INCR || _la==DECR) ) {
        _errHandler.recoverInline(this);
        } else {
          consume();
        }
        }
        break;
      case 3:
        _localctx = new NotContext(_localctx);
        enterOuterAlt(_localctx, 3);
        {
        setState(338);
        _la = _input.LA(1);
        if ( !(_la==BOOLNOT || _la==BWNOT) ) {
        _errHandler.recoverInline(this);
        } else {
          consume();
        }
        setState(339);
        unary();
        }
        break;
      case 4:
        _localctx = new CastContext(_localctx);
        enterOuterAlt(_localctx, 4);
        {
        setState(340);
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
  public static class RefcastContext extends CastexpressionContext {
    public TerminalNode LP() { return getToken(PainlessParser.LP, 0); }
    public RefcasttypeContext refcasttype() {
      return getRuleContext(RefcasttypeContext.class,0);
    }
    public TerminalNode RP() { return getToken(PainlessParser.RP, 0); }
    public UnarynotaddsubContext unarynotaddsub() {
      return getRuleContext(UnarynotaddsubContext.class,0);
    }
    public RefcastContext(CastexpressionContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitRefcast(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class PrimordefcastContext extends CastexpressionContext {
    public TerminalNode LP() { return getToken(PainlessParser.LP, 0); }
    public PrimordefcasttypeContext primordefcasttype() {
      return getRuleContext(PrimordefcasttypeContext.class,0);
    }
    public TerminalNode RP() { return getToken(PainlessParser.RP, 0); }
    public UnaryContext unary() {
      return getRuleContext(UnaryContext.class,0);
    }
    public PrimordefcastContext(CastexpressionContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitPrimordefcast(this);
      else return visitor.visitChildren(this);
    }
  }

  public final CastexpressionContext castexpression() throws RecognitionException {
    CastexpressionContext _localctx = new CastexpressionContext(_ctx, getState());
    enterRule(_localctx, 40, RULE_castexpression);
    try {
      setState(353);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,29,_ctx) ) {
      case 1:
        _localctx = new PrimordefcastContext(_localctx);
        enterOuterAlt(_localctx, 1);
        {
        setState(343);
        match(LP);
        setState(344);
        primordefcasttype();
        setState(345);
        match(RP);
        setState(346);
        unary();
        }
        break;
      case 2:
        _localctx = new RefcastContext(_localctx);
        enterOuterAlt(_localctx, 2);
        {
        setState(348);
        match(LP);
        setState(349);
        refcasttype();
        setState(350);
        match(RP);
        setState(351);
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

  public static class PrimordefcasttypeContext extends ParserRuleContext {
    public TerminalNode DEF() { return getToken(PainlessParser.DEF, 0); }
    public TerminalNode PRIMITIVE() { return getToken(PainlessParser.PRIMITIVE, 0); }
    public PrimordefcasttypeContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_primordefcasttype; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitPrimordefcasttype(this);
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
      setState(355);
      _la = _input.LA(1);
      if ( !(_la==PRIMITIVE || _la==DEF) ) {
      _errHandler.recoverInline(this);
      } else {
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

  public static class RefcasttypeContext extends ParserRuleContext {
    public TerminalNode DEF() { return getToken(PainlessParser.DEF, 0); }
    public List<TerminalNode> LBRACE() { return getTokens(PainlessParser.LBRACE); }
    public TerminalNode LBRACE(int i) {
      return getToken(PainlessParser.LBRACE, i);
    }
    public List<TerminalNode> RBRACE() { return getTokens(PainlessParser.RBRACE); }
    public TerminalNode RBRACE(int i) {
      return getToken(PainlessParser.RBRACE, i);
    }
    public TerminalNode PRIMITIVE() { return getToken(PainlessParser.PRIMITIVE, 0); }
    public TerminalNode ID() { return getToken(PainlessParser.ID, 0); }
    public List<TerminalNode> DOT() { return getTokens(PainlessParser.DOT); }
    public TerminalNode DOT(int i) {
      return getToken(PainlessParser.DOT, i);
    }
    public List<TerminalNode> DOTID() { return getTokens(PainlessParser.DOTID); }
    public TerminalNode DOTID(int i) {
      return getToken(PainlessParser.DOTID, i);
    }
    public RefcasttypeContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_refcasttype; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitRefcasttype(this);
      else return visitor.visitChildren(this);
    }
  }

  public final RefcasttypeContext refcasttype() throws RecognitionException {
    RefcasttypeContext _localctx = new RefcasttypeContext(_ctx, getState());
    enterRule(_localctx, 44, RULE_refcasttype);
    int _la;
    try {
      setState(386);
      switch (_input.LA(1)) {
      case DEF:
        enterOuterAlt(_localctx, 1);
        {
        setState(357);
        match(DEF);
        setState(360); 
        _errHandler.sync(this);
        _la = _input.LA(1);
        do {
          {
          {
          setState(358);
          match(LBRACE);
          setState(359);
          match(RBRACE);
          }
          }
          setState(362); 
          _errHandler.sync(this);
          _la = _input.LA(1);
        } while ( _la==LBRACE );
        }
        break;
      case PRIMITIVE:
        enterOuterAlt(_localctx, 2);
        {
        setState(364);
        match(PRIMITIVE);
        setState(367); 
        _errHandler.sync(this);
        _la = _input.LA(1);
        do {
          {
          {
          setState(365);
          match(LBRACE);
          setState(366);
          match(RBRACE);
          }
          }
          setState(369); 
          _errHandler.sync(this);
          _la = _input.LA(1);
        } while ( _la==LBRACE );
        }
        break;
      case ID:
        enterOuterAlt(_localctx, 3);
        {
        setState(371);
        match(ID);
        setState(376);
        _errHandler.sync(this);
        _la = _input.LA(1);
        while (_la==DOT) {
          {
          {
          setState(372);
          match(DOT);
          setState(373);
          match(DOTID);
          }
          }
          setState(378);
          _errHandler.sync(this);
          _la = _input.LA(1);
        }
        setState(383);
        _errHandler.sync(this);
        _la = _input.LA(1);
        while (_la==LBRACE) {
          {
          {
          setState(379);
          match(LBRACE);
          setState(380);
          match(RBRACE);
          }
          }
          setState(385);
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
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitDynamic(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class NewarrayContext extends ChainContext {
    public ArrayinitializerContext arrayinitializer() {
      return getRuleContext(ArrayinitializerContext.class,0);
    }
    public NewarrayContext(ChainContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitNewarray(this);
      else return visitor.visitChildren(this);
    }
  }

  public final ChainContext chain() throws RecognitionException {
    ChainContext _localctx = new ChainContext(_ctx, getState());
    enterRule(_localctx, 46, RULE_chain);
    try {
      int _alt;
      setState(396);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,36,_ctx) ) {
      case 1:
        _localctx = new DynamicContext(_localctx);
        enterOuterAlt(_localctx, 1);
        {
        setState(388);
        primary();
        setState(392);
        _errHandler.sync(this);
        _alt = getInterpreter().adaptivePredict(_input,35,_ctx);
        while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
          if ( _alt==1 ) {
            {
            {
            setState(389);
            postfix();
            }
            } 
          }
          setState(394);
          _errHandler.sync(this);
          _alt = getInterpreter().adaptivePredict(_input,35,_ctx);
        }
        }
        break;
      case 2:
        _localctx = new NewarrayContext(_localctx);
        enterOuterAlt(_localctx, 2);
        {
        setState(395);
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
  public static class ListinitContext extends PrimaryContext {
    public ListinitializerContext listinitializer() {
      return getRuleContext(ListinitializerContext.class,0);
    }
    public ListinitContext(PrimaryContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitListinit(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class RegexContext extends PrimaryContext {
    public TerminalNode REGEX() { return getToken(PainlessParser.REGEX, 0); }
    public RegexContext(PrimaryContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitRegex(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class NullContext extends PrimaryContext {
    public TerminalNode NULL() { return getToken(PainlessParser.NULL, 0); }
    public NullContext(PrimaryContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitNull(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class StringContext extends PrimaryContext {
    public TerminalNode STRING() { return getToken(PainlessParser.STRING, 0); }
    public StringContext(PrimaryContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitString(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class MapinitContext extends PrimaryContext {
    public MapinitializerContext mapinitializer() {
      return getRuleContext(MapinitializerContext.class,0);
    }
    public MapinitContext(PrimaryContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitMapinit(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class CalllocalContext extends PrimaryContext {
    public TerminalNode ID() { return getToken(PainlessParser.ID, 0); }
    public ArgumentsContext arguments() {
      return getRuleContext(ArgumentsContext.class,0);
    }
    public CalllocalContext(PrimaryContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitCalllocal(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class TrueContext extends PrimaryContext {
    public TerminalNode TRUE() { return getToken(PainlessParser.TRUE, 0); }
    public TrueContext(PrimaryContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitTrue(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class FalseContext extends PrimaryContext {
    public TerminalNode FALSE() { return getToken(PainlessParser.FALSE, 0); }
    public FalseContext(PrimaryContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitFalse(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class VariableContext extends PrimaryContext {
    public TerminalNode ID() { return getToken(PainlessParser.ID, 0); }
    public VariableContext(PrimaryContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitVariable(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class NumericContext extends PrimaryContext {
    public TerminalNode OCTAL() { return getToken(PainlessParser.OCTAL, 0); }
    public TerminalNode HEX() { return getToken(PainlessParser.HEX, 0); }
    public TerminalNode INTEGER() { return getToken(PainlessParser.INTEGER, 0); }
    public TerminalNode DECIMAL() { return getToken(PainlessParser.DECIMAL, 0); }
    public NumericContext(PrimaryContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitNumeric(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class NewobjectContext extends PrimaryContext {
    public TerminalNode NEW() { return getToken(PainlessParser.NEW, 0); }
    public TypeContext type() {
      return getRuleContext(TypeContext.class,0);
    }
    public ArgumentsContext arguments() {
      return getRuleContext(ArgumentsContext.class,0);
    }
    public NewobjectContext(PrimaryContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitNewobject(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class PrecedenceContext extends PrimaryContext {
    public TerminalNode LP() { return getToken(PainlessParser.LP, 0); }
    public ExpressionContext expression() {
      return getRuleContext(ExpressionContext.class,0);
    }
    public TerminalNode RP() { return getToken(PainlessParser.RP, 0); }
    public PrecedenceContext(PrimaryContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitPrecedence(this);
      else return visitor.visitChildren(this);
    }
  }

  public final PrimaryContext primary() throws RecognitionException {
    PrimaryContext _localctx = new PrimaryContext(_ctx, getState());
    enterRule(_localctx, 48, RULE_primary);
    int _la;
    try {
      setState(417);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,37,_ctx) ) {
      case 1:
        _localctx = new PrecedenceContext(_localctx);
        enterOuterAlt(_localctx, 1);
        {
        setState(398);
        match(LP);
        setState(399);
        expression();
        setState(400);
        match(RP);
        }
        break;
      case 2:
        _localctx = new NumericContext(_localctx);
        enterOuterAlt(_localctx, 2);
        {
        setState(402);
        _la = _input.LA(1);
        if ( !(((((_la - 72)) & ~0x3f) == 0 && ((1L << (_la - 72)) & ((1L << (OCTAL - 72)) | (1L << (HEX - 72)) | (1L << (INTEGER - 72)) | (1L << (DECIMAL - 72)))) != 0)) ) {
        _errHandler.recoverInline(this);
        } else {
          consume();
        }
        }
        break;
      case 3:
        _localctx = new TrueContext(_localctx);
        enterOuterAlt(_localctx, 3);
        {
        setState(403);
        match(TRUE);
        }
        break;
      case 4:
        _localctx = new FalseContext(_localctx);
        enterOuterAlt(_localctx, 4);
        {
        setState(404);
        match(FALSE);
        }
        break;
      case 5:
        _localctx = new NullContext(_localctx);
        enterOuterAlt(_localctx, 5);
        {
        setState(405);
        match(NULL);
        }
        break;
      case 6:
        _localctx = new StringContext(_localctx);
        enterOuterAlt(_localctx, 6);
        {
        setState(406);
        match(STRING);
        }
        break;
      case 7:
        _localctx = new RegexContext(_localctx);
        enterOuterAlt(_localctx, 7);
        {
        setState(407);
        match(REGEX);
        }
        break;
      case 8:
        _localctx = new ListinitContext(_localctx);
        enterOuterAlt(_localctx, 8);
        {
        setState(408);
        listinitializer();
        }
        break;
      case 9:
        _localctx = new MapinitContext(_localctx);
        enterOuterAlt(_localctx, 9);
        {
        setState(409);
        mapinitializer();
        }
        break;
      case 10:
        _localctx = new VariableContext(_localctx);
        enterOuterAlt(_localctx, 10);
        {
        setState(410);
        match(ID);
        }
        break;
      case 11:
        _localctx = new CalllocalContext(_localctx);
        enterOuterAlt(_localctx, 11);
        {
        setState(411);
        match(ID);
        setState(412);
        arguments();
        }
        break;
      case 12:
        _localctx = new NewobjectContext(_localctx);
        enterOuterAlt(_localctx, 12);
        {
        setState(413);
        match(NEW);
        setState(414);
        type();
        setState(415);
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
    public DotaccessContext dotaccess() {
      return getRuleContext(DotaccessContext.class,0);
    }
    public PostfixContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_postfix; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitPostfix(this);
      else return visitor.visitChildren(this);
    }
  }

  public final PostfixContext postfix() throws RecognitionException {
    PostfixContext _localctx = new PostfixContext(_ctx, getState());
    enterRule(_localctx, 50, RULE_postfix);
    try {
      setState(423);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,38,_ctx) ) {
      case 1:
        enterOuterAlt(_localctx, 1);
        {
        setState(419);
        callinvoke();
        }
        break;
      case 2:
        enterOuterAlt(_localctx, 2);
        {
        setState(420);
        fieldaccess();
        }
        break;
      case 3:
        enterOuterAlt(_localctx, 3);
        {
        setState(421);
        braceaccess();
        }
        break;
      case 4:
        enterOuterAlt(_localctx, 4);
        {
        setState(422);
        dotaccess();
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

  public static class PostdotContext extends ParserRuleContext {
    public CallinvokeContext callinvoke() {
      return getRuleContext(CallinvokeContext.class,0);
    }
    public FieldaccessContext fieldaccess() {
      return getRuleContext(FieldaccessContext.class,0);
    }
    public DotaccessContext dotaccess() {
      return getRuleContext(DotaccessContext.class,0);
    }
    public PostdotContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_postdot; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitPostdot(this);
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
        setState(425);
        callinvoke();
        }
        break;
      case 2:
        enterOuterAlt(_localctx, 2);
        {
        setState(426);
        fieldaccess();
        }
        break;
      case 3:
        enterOuterAlt(_localctx, 3);
        {
        setState(427);
        dotaccess();
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

  public static class CallinvokeContext extends ParserRuleContext {
    public TerminalNode DOTID() { return getToken(PainlessParser.DOTID, 0); }
    public ArgumentsContext arguments() {
      return getRuleContext(ArgumentsContext.class,0);
    }
    public TerminalNode DOT() { return getToken(PainlessParser.DOT, 0); }
    public TerminalNode NSDOT() { return getToken(PainlessParser.NSDOT, 0); }
    public CallinvokeContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_callinvoke; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitCallinvoke(this);
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
      } else {
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

  public static class FieldaccessContext extends ParserRuleContext {
    public TerminalNode DOT() { return getToken(PainlessParser.DOT, 0); }
    public TerminalNode NSDOT() { return getToken(PainlessParser.NSDOT, 0); }
    public TerminalNode DOTID() { return getToken(PainlessParser.DOTID, 0); }
    public TerminalNode DOTINTEGER() { return getToken(PainlessParser.DOTINTEGER, 0); }
    public FieldaccessContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_fieldaccess; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitFieldaccess(this);
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
      } else {
        consume();
      }
      setState(435);
      _la = _input.LA(1);
      if ( !(_la==DOTINTEGER || _la==DOTID) ) {
      _errHandler.recoverInline(this);
      } else {
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

  public static class DotaccessContext extends ParserRuleContext {
    public TerminalNode DOT() { return getToken(PainlessParser.DOT, 0); }
    public TerminalNode NSDOT() { return getToken(PainlessParser.NSDOT, 0); }
    public DotaccessContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_dotaccess; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitDotaccess(this);
      else return visitor.visitChildren(this);
    }
  }

  public final DotaccessContext dotaccess() throws RecognitionException {
    DotaccessContext _localctx = new DotaccessContext(_ctx, getState());
    enterRule(_localctx, 58, RULE_dotaccess);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(437);
      _la = _input.LA(1);
      if ( !(_la==DOT || _la==NSDOT) ) {
      _errHandler.recoverInline(this);
      } else {
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

  public static class BraceaccessContext extends ParserRuleContext {
    public TerminalNode LBRACE() { return getToken(PainlessParser.LBRACE, 0); }
    public ExpressionContext expression() {
      return getRuleContext(ExpressionContext.class,0);
    }
    public TerminalNode RBRACE() { return getToken(PainlessParser.RBRACE, 0); }
    public BraceaccessContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_braceaccess; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitBraceaccess(this);
      else return visitor.visitChildren(this);
    }
  }

  public final BraceaccessContext braceaccess() throws RecognitionException {
    BraceaccessContext _localctx = new BraceaccessContext(_ctx, getState());
    enterRule(_localctx, 60, RULE_braceaccess);
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(439);
      match(LBRACE);
      setState(440);
      expression();
      setState(441);
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
  public static class NewstandardarrayContext extends ArrayinitializerContext {
    public TerminalNode NEW() { return getToken(PainlessParser.NEW, 0); }
    public TypeContext type() {
      return getRuleContext(TypeContext.class,0);
    }
    public List<TerminalNode> LBRACE() { return getTokens(PainlessParser.LBRACE); }
    public TerminalNode LBRACE(int i) {
      return getToken(PainlessParser.LBRACE, i);
    }
    public List<ExpressionContext> expression() {
      return getRuleContexts(ExpressionContext.class);
    }
    public ExpressionContext expression(int i) {
      return getRuleContext(ExpressionContext.class,i);
    }
    public List<TerminalNode> RBRACE() { return getTokens(PainlessParser.RBRACE); }
    public TerminalNode RBRACE(int i) {
      return getToken(PainlessParser.RBRACE, i);
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
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitNewstandardarray(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class NewinitializedarrayContext extends ArrayinitializerContext {
    public TerminalNode NEW() { return getToken(PainlessParser.NEW, 0); }
    public TypeContext type() {
      return getRuleContext(TypeContext.class,0);
    }
    public TerminalNode LBRACE() { return getToken(PainlessParser.LBRACE, 0); }
    public TerminalNode RBRACE() { return getToken(PainlessParser.RBRACE, 0); }
    public TerminalNode LBRACK() { return getToken(PainlessParser.LBRACK, 0); }
    public TerminalNode RBRACK() { return getToken(PainlessParser.RBRACK, 0); }
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
    public List<TerminalNode> COMMA() { return getTokens(PainlessParser.COMMA); }
    public TerminalNode COMMA(int i) {
      return getToken(PainlessParser.COMMA, i);
    }
    public NewinitializedarrayContext(ArrayinitializerContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitNewinitializedarray(this);
      else return visitor.visitChildren(this);
    }
  }

  public final ArrayinitializerContext arrayinitializer() throws RecognitionException {
    ArrayinitializerContext _localctx = new ArrayinitializerContext(_ctx, getState());
    enterRule(_localctx, 62, RULE_arrayinitializer);
    int _la;
    try {
      int _alt;
      setState(484);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,46,_ctx) ) {
      case 1:
        _localctx = new NewstandardarrayContext(_localctx);
        enterOuterAlt(_localctx, 1);
        {
        setState(443);
        match(NEW);
        setState(444);
        type();
        setState(449); 
        _errHandler.sync(this);
        _alt = 1;
        do {
          switch (_alt) {
          case 1:
            {
            {
            setState(445);
            match(LBRACE);
            setState(446);
            expression();
            setState(447);
            match(RBRACE);
            }
            }
            break;
          default:
            throw new NoViableAltException(this);
          }
          setState(451); 
          _errHandler.sync(this);
          _alt = getInterpreter().adaptivePredict(_input,40,_ctx);
        } while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
        setState(460);
        _errHandler.sync(this);
        switch ( getInterpreter().adaptivePredict(_input,42,_ctx) ) {
        case 1:
          {
          setState(453);
          postdot();
          setState(457);
          _errHandler.sync(this);
          _alt = getInterpreter().adaptivePredict(_input,41,_ctx);
          while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
            if ( _alt==1 ) {
              {
              {
              setState(454);
              postfix();
              }
              } 
            }
            setState(459);
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
        setState(462);
        match(NEW);
        setState(463);
        type();
        setState(464);
        match(LBRACE);
        setState(465);
        match(RBRACE);
        setState(466);
        match(LBRACK);
        setState(475);
        _la = _input.LA(1);
        if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LBRACE) | (1L << LP) | (1L << NEW) | (1L << BOOLNOT) | (1L << BWNOT) | (1L << ADD) | (1L << SUB) | (1L << INCR) | (1L << DECR))) != 0) || ((((_la - 72)) & ~0x3f) == 0 && ((1L << (_la - 72)) & ((1L << (OCTAL - 72)) | (1L << (HEX - 72)) | (1L << (INTEGER - 72)) | (1L << (DECIMAL - 72)) | (1L << (STRING - 72)) | (1L << (REGEX - 72)) | (1L << (TRUE - 72)) | (1L << (FALSE - 72)) | (1L << (NULL - 72)) | (1L << (ID - 72)))) != 0)) {
          {
          setState(467);
          expression();
          setState(472);
          _errHandler.sync(this);
          _la = _input.LA(1);
          while (_la==COMMA) {
            {
            {
            setState(468);
            match(COMMA);
            setState(469);
            expression();
            }
            }
            setState(474);
            _errHandler.sync(this);
            _la = _input.LA(1);
          }
          }
        }

        setState(477);
        match(RBRACK);
        setState(481);
        _errHandler.sync(this);
        _alt = getInterpreter().adaptivePredict(_input,45,_ctx);
        while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
          if ( _alt==1 ) {
            {
            {
            setState(478);
            postfix();
            }
            } 
          }
          setState(483);
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

  public static class ListinitializerContext extends ParserRuleContext {
    public TerminalNode LBRACE() { return getToken(PainlessParser.LBRACE, 0); }
    public List<ExpressionContext> expression() {
      return getRuleContexts(ExpressionContext.class);
    }
    public ExpressionContext expression(int i) {
      return getRuleContext(ExpressionContext.class,i);
    }
    public TerminalNode RBRACE() { return getToken(PainlessParser.RBRACE, 0); }
    public List<TerminalNode> COMMA() { return getTokens(PainlessParser.COMMA); }
    public TerminalNode COMMA(int i) {
      return getToken(PainlessParser.COMMA, i);
    }
    public ListinitializerContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_listinitializer; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitListinitializer(this);
      else return visitor.visitChildren(this);
    }
  }

  public final ListinitializerContext listinitializer() throws RecognitionException {
    ListinitializerContext _localctx = new ListinitializerContext(_ctx, getState());
    enterRule(_localctx, 64, RULE_listinitializer);
    int _la;
    try {
      setState(499);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,48,_ctx) ) {
      case 1:
        enterOuterAlt(_localctx, 1);
        {
        setState(486);
        match(LBRACE);
        setState(487);
        expression();
        setState(492);
        _errHandler.sync(this);
        _la = _input.LA(1);
        while (_la==COMMA) {
          {
          {
          setState(488);
          match(COMMA);
          setState(489);
          expression();
          }
          }
          setState(494);
          _errHandler.sync(this);
          _la = _input.LA(1);
        }
        setState(495);
        match(RBRACE);
        }
        break;
      case 2:
        enterOuterAlt(_localctx, 2);
        {
        setState(497);
        match(LBRACE);
        setState(498);
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

  public static class MapinitializerContext extends ParserRuleContext {
    public TerminalNode LBRACE() { return getToken(PainlessParser.LBRACE, 0); }
    public List<MaptokenContext> maptoken() {
      return getRuleContexts(MaptokenContext.class);
    }
    public MaptokenContext maptoken(int i) {
      return getRuleContext(MaptokenContext.class,i);
    }
    public TerminalNode RBRACE() { return getToken(PainlessParser.RBRACE, 0); }
    public List<TerminalNode> COMMA() { return getTokens(PainlessParser.COMMA); }
    public TerminalNode COMMA(int i) {
      return getToken(PainlessParser.COMMA, i);
    }
    public TerminalNode COLON() { return getToken(PainlessParser.COLON, 0); }
    public MapinitializerContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_mapinitializer; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitMapinitializer(this);
      else return visitor.visitChildren(this);
    }
  }

  public final MapinitializerContext mapinitializer() throws RecognitionException {
    MapinitializerContext _localctx = new MapinitializerContext(_ctx, getState());
    enterRule(_localctx, 66, RULE_mapinitializer);
    int _la;
    try {
      setState(515);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,50,_ctx) ) {
      case 1:
        enterOuterAlt(_localctx, 1);
        {
        setState(501);
        match(LBRACE);
        setState(502);
        maptoken();
        setState(507);
        _errHandler.sync(this);
        _la = _input.LA(1);
        while (_la==COMMA) {
          {
          {
          setState(503);
          match(COMMA);
          setState(504);
          maptoken();
          }
          }
          setState(509);
          _errHandler.sync(this);
          _la = _input.LA(1);
        }
        setState(510);
        match(RBRACE);
        }
        break;
      case 2:
        enterOuterAlt(_localctx, 2);
        {
        setState(512);
        match(LBRACE);
        setState(513);
        match(COLON);
        setState(514);
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

  public static class MaptokenContext extends ParserRuleContext {
    public List<ExpressionContext> expression() {
      return getRuleContexts(ExpressionContext.class);
    }
    public ExpressionContext expression(int i) {
      return getRuleContext(ExpressionContext.class,i);
    }
    public TerminalNode COLON() { return getToken(PainlessParser.COLON, 0); }
    public MaptokenContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_maptoken; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitMaptoken(this);
      else return visitor.visitChildren(this);
    }
  }

  public final MaptokenContext maptoken() throws RecognitionException {
    MaptokenContext _localctx = new MaptokenContext(_ctx, getState());
    enterRule(_localctx, 68, RULE_maptoken);
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(517);
      expression();
      setState(518);
      match(COLON);
      setState(519);
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

  public static class ArgumentsContext extends ParserRuleContext {
    public TerminalNode LP() { return getToken(PainlessParser.LP, 0); }
    public TerminalNode RP() { return getToken(PainlessParser.RP, 0); }
    public List<ArgumentContext> argument() {
      return getRuleContexts(ArgumentContext.class);
    }
    public ArgumentContext argument(int i) {
      return getRuleContext(ArgumentContext.class,i);
    }
    public List<TerminalNode> COMMA() { return getTokens(PainlessParser.COMMA); }
    public TerminalNode COMMA(int i) {
      return getToken(PainlessParser.COMMA, i);
    }
    public ArgumentsContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_arguments; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitArguments(this);
      else return visitor.visitChildren(this);
    }
  }

  public final ArgumentsContext arguments() throws RecognitionException {
    ArgumentsContext _localctx = new ArgumentsContext(_ctx, getState());
    enterRule(_localctx, 70, RULE_arguments);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
      {
      setState(521);
      match(LP);
      setState(530);
      _la = _input.LA(1);
      if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LBRACE) | (1L << LP) | (1L << NEW) | (1L << THIS) | (1L << BOOLNOT) | (1L << BWNOT) | (1L << ADD) | (1L << SUB) | (1L << INCR) | (1L << DECR))) != 0) || ((((_la - 72)) & ~0x3f) == 0 && ((1L << (_la - 72)) & ((1L << (OCTAL - 72)) | (1L << (HEX - 72)) | (1L << (INTEGER - 72)) | (1L << (DECIMAL - 72)) | (1L << (STRING - 72)) | (1L << (REGEX - 72)) | (1L << (TRUE - 72)) | (1L << (FALSE - 72)) | (1L << (NULL - 72)) | (1L << (PRIMITIVE - 72)) | (1L << (DEF - 72)) | (1L << (ID - 72)))) != 0)) {
        {
        setState(522);
        argument();
        setState(527);
        _errHandler.sync(this);
        _la = _input.LA(1);
        while (_la==COMMA) {
          {
          {
          setState(523);
          match(COMMA);
          setState(524);
          argument();
          }
          }
          setState(529);
          _errHandler.sync(this);
          _la = _input.LA(1);
        }
        }
      }

      setState(532);
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

  public static class ArgumentContext extends ParserRuleContext {
    public ExpressionContext expression() {
      return getRuleContext(ExpressionContext.class,0);
    }
    public LambdaContext lambda() {
      return getRuleContext(LambdaContext.class,0);
    }
    public FuncrefContext funcref() {
      return getRuleContext(FuncrefContext.class,0);
    }
    public ArgumentContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_argument; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitArgument(this);
      else return visitor.visitChildren(this);
    }
  }

  public final ArgumentContext argument() throws RecognitionException {
    ArgumentContext _localctx = new ArgumentContext(_ctx, getState());
    enterRule(_localctx, 72, RULE_argument);
    try {
      setState(537);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,53,_ctx) ) {
      case 1:
        enterOuterAlt(_localctx, 1);
        {
        setState(534);
        expression();
        }
        break;
      case 2:
        enterOuterAlt(_localctx, 2);
        {
        setState(535);
        lambda();
        }
        break;
      case 3:
        enterOuterAlt(_localctx, 3);
        {
        setState(536);
        funcref();
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

  public static class LambdaContext extends ParserRuleContext {
    public TerminalNode ARROW() { return getToken(PainlessParser.ARROW, 0); }
    public List<LamtypeContext> lamtype() {
      return getRuleContexts(LamtypeContext.class);
    }
    public LamtypeContext lamtype(int i) {
      return getRuleContext(LamtypeContext.class,i);
    }
    public TerminalNode LP() { return getToken(PainlessParser.LP, 0); }
    public TerminalNode RP() { return getToken(PainlessParser.RP, 0); }
    public BlockContext block() {
      return getRuleContext(BlockContext.class,0);
    }
    public ExpressionContext expression() {
      return getRuleContext(ExpressionContext.class,0);
    }
    public List<TerminalNode> COMMA() { return getTokens(PainlessParser.COMMA); }
    public TerminalNode COMMA(int i) {
      return getToken(PainlessParser.COMMA, i);
    }
    public LambdaContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_lambda; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitLambda(this);
      else return visitor.visitChildren(this);
    }
  }

  public final LambdaContext lambda() throws RecognitionException {
    LambdaContext _localctx = new LambdaContext(_ctx, getState());
    enterRule(_localctx, 74, RULE_lambda);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(552);
      switch (_input.LA(1)) {
      case PRIMITIVE:
      case DEF:
      case ID:
        {
        setState(539);
        lamtype();
        }
        break;
      case LP:
        {
        setState(540);
        match(LP);
        setState(549);
        _la = _input.LA(1);
        if (((((_la - 81)) & ~0x3f) == 0 && ((1L << (_la - 81)) & ((1L << (PRIMITIVE - 81)) | (1L << (DEF - 81)) | (1L << (ID - 81)))) != 0)) {
          {
          setState(541);
          lamtype();
          setState(546);
          _errHandler.sync(this);
          _la = _input.LA(1);
          while (_la==COMMA) {
            {
            {
            setState(542);
            match(COMMA);
            setState(543);
            lamtype();
            }
            }
            setState(548);
            _errHandler.sync(this);
            _la = _input.LA(1);
          }
          }
        }

        setState(551);
        match(RP);
        }
        break;
      default:
        throw new NoViableAltException(this);
      }
      setState(554);
      match(ARROW);
      setState(557);
      switch (_input.LA(1)) {
      case LBRACK:
        {
        setState(555);
        block();
        }
        break;
      case LBRACE:
      case LP:
      case NEW:
      case BOOLNOT:
      case BWNOT:
      case ADD:
      case SUB:
      case INCR:
      case DECR:
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
        {
        setState(556);
        expression();
        }
        break;
      default:
        throw new NoViableAltException(this);
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

  public static class LamtypeContext extends ParserRuleContext {
    public TerminalNode ID() { return getToken(PainlessParser.ID, 0); }
    public DecltypeContext decltype() {
      return getRuleContext(DecltypeContext.class,0);
    }
    public LamtypeContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_lamtype; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitLamtype(this);
      else return visitor.visitChildren(this);
    }
  }

  public final LamtypeContext lamtype() throws RecognitionException {
    LamtypeContext _localctx = new LamtypeContext(_ctx, getState());
    enterRule(_localctx, 76, RULE_lamtype);
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(560);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,58,_ctx) ) {
      case 1:
        {
        setState(559);
        decltype();
        }
        break;
      }
      setState(562);
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
  public static class ClassfuncrefContext extends FuncrefContext {
    public DecltypeContext decltype() {
      return getRuleContext(DecltypeContext.class,0);
    }
    public TerminalNode REF() { return getToken(PainlessParser.REF, 0); }
    public TerminalNode ID() { return getToken(PainlessParser.ID, 0); }
    public ClassfuncrefContext(FuncrefContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitClassfuncref(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class ConstructorfuncrefContext extends FuncrefContext {
    public DecltypeContext decltype() {
      return getRuleContext(DecltypeContext.class,0);
    }
    public TerminalNode REF() { return getToken(PainlessParser.REF, 0); }
    public TerminalNode NEW() { return getToken(PainlessParser.NEW, 0); }
    public ConstructorfuncrefContext(FuncrefContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitConstructorfuncref(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class LocalfuncrefContext extends FuncrefContext {
    public TerminalNode THIS() { return getToken(PainlessParser.THIS, 0); }
    public TerminalNode REF() { return getToken(PainlessParser.REF, 0); }
    public TerminalNode ID() { return getToken(PainlessParser.ID, 0); }
    public LocalfuncrefContext(FuncrefContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitLocalfuncref(this);
      else return visitor.visitChildren(this);
    }
  }

  public final FuncrefContext funcref() throws RecognitionException {
    FuncrefContext _localctx = new FuncrefContext(_ctx, getState());
    enterRule(_localctx, 78, RULE_funcref);
    try {
      setState(575);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,59,_ctx) ) {
      case 1:
        _localctx = new ClassfuncrefContext(_localctx);
        enterOuterAlt(_localctx, 1);
        {
        setState(564);
        decltype();
        setState(565);
        match(REF);
        setState(566);
        match(ID);
        }
        break;
      case 2:
        _localctx = new ConstructorfuncrefContext(_localctx);
        enterOuterAlt(_localctx, 2);
        {
        setState(568);
        decltype();
        setState(569);
        match(REF);
        setState(570);
        match(NEW);
        }
        break;
      case 3:
        _localctx = new LocalfuncrefContext(_localctx);
        enterOuterAlt(_localctx, 3);
        {
        setState(572);
        match(THIS);
        setState(573);
        match(REF);
        setState(574);
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

  public static final String _serializedATN =
    "\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3X\u0244\4\2\t\2\4"+
    "\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
    "\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
    "\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
    "\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
    "\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\3\2\7\2T\n\2\f"+
    "\2\16\2W\13\2\3\2\7\2Z\n\2\f\2\16\2]\13\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3"+
    "\3\4\3\4\3\4\3\4\3\4\3\4\3\4\7\4m\n\4\f\4\16\4p\13\4\5\4r\n\4\3\4\3\4"+
    "\3\5\3\5\3\5\3\5\5\5z\n\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\5\6\u0084\n"+
    "\6\3\6\3\6\3\6\3\6\3\6\3\6\5\6\u008c\n\6\3\6\3\6\3\6\5\6\u0091\n\6\3\6"+
    "\3\6\5\6\u0095\n\6\3\6\3\6\5\6\u0099\n\6\3\6\3\6\3\6\5\6\u009e\n\6\3\6"+
    "\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3"+
    "\6\3\6\6\6\u00b4\n\6\r\6\16\6\u00b5\5\6\u00b8\n\6\3\7\3\7\3\7\3\7\3\7"+
    "\3\7\3\7\3\7\3\7\3\7\3\7\3\7\5\7\u00c6\n\7\3\7\3\7\3\7\5\7\u00cb\n\7\3"+
    "\b\3\b\5\b\u00cf\n\b\3\t\3\t\7\t\u00d3\n\t\f\t\16\t\u00d6\13\t\3\t\5\t"+
    "\u00d9\n\t\3\t\3\t\3\n\3\n\3\13\3\13\5\13\u00e1\n\13\3\f\3\f\3\r\3\r\3"+
    "\r\3\r\7\r\u00e9\n\r\f\r\16\r\u00ec\13\r\3\16\3\16\3\16\7\16\u00f1\n\16"+
    "\f\16\16\16\u00f4\13\16\3\17\3\17\3\17\3\17\3\17\7\17\u00fb\n\17\f\17"+
    "\16\17\u00fe\13\17\5\17\u0100\n\17\3\20\3\20\3\20\5\20\u0105\n\20\3\21"+
    "\3\21\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22"+
    "\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22"+
    "\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22"+
    "\3\22\3\22\3\22\3\22\3\22\3\22\7\22\u0138\n\22\f\22\16\22\u013b\13\22"+
    "\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\5\23\u0148\n\23"+
    "\3\24\3\24\3\24\3\24\3\24\5\24\u014f\n\24\3\25\3\25\3\25\3\25\3\25\3\25"+
    "\3\25\5\25\u0158\n\25\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26"+
    "\5\26\u0164\n\26\3\27\3\27\3\30\3\30\3\30\6\30\u016b\n\30\r\30\16\30\u016c"+
    "\3\30\3\30\3\30\6\30\u0172\n\30\r\30\16\30\u0173\3\30\3\30\3\30\7\30\u0179"+
    "\n\30\f\30\16\30\u017c\13\30\3\30\3\30\7\30\u0180\n\30\f\30\16\30\u0183"+
    "\13\30\5\30\u0185\n\30\3\31\3\31\7\31\u0189\n\31\f\31\16\31\u018c\13\31"+
    "\3\31\5\31\u018f\n\31\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32"+
    "\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\5\32\u01a4\n\32\3\33\3\33"+
    "\3\33\3\33\5\33\u01aa\n\33\3\34\3\34\3\34\5\34\u01af\n\34\3\35\3\35\3"+
    "\35\3\35\3\36\3\36\3\36\3\37\3\37\3 \3 \3 \3 \3!\3!\3!\3!\3!\3!\6!\u01c4"+
    "\n!\r!\16!\u01c5\3!\3!\7!\u01ca\n!\f!\16!\u01cd\13!\5!\u01cf\n!\3!\3!"+
    "\3!\3!\3!\3!\3!\3!\7!\u01d9\n!\f!\16!\u01dc\13!\5!\u01de\n!\3!\3!\7!\u01e2"+
    "\n!\f!\16!\u01e5\13!\5!\u01e7\n!\3\"\3\"\3\"\3\"\7\"\u01ed\n\"\f\"\16"+
    "\"\u01f0\13\"\3\"\3\"\3\"\3\"\5\"\u01f6\n\"\3#\3#\3#\3#\7#\u01fc\n#\f"+
    "#\16#\u01ff\13#\3#\3#\3#\3#\3#\5#\u0206\n#\3$\3$\3$\3$\3%\3%\3%\3%\7%"+
    "\u0210\n%\f%\16%\u0213\13%\5%\u0215\n%\3%\3%\3&\3&\3&\5&\u021c\n&\3\'"+
    "\3\'\3\'\3\'\3\'\7\'\u0223\n\'\f\'\16\'\u0226\13\'\5\'\u0228\n\'\3\'\5"+
    "\'\u022b\n\'\3\'\3\'\3\'\5\'\u0230\n\'\3(\5(\u0233\n(\3(\3(\3)\3)\3)\3"+
    ")\3)\3)\3)\3)\3)\3)\3)\5)\u0242\n)\3)\2\3\"*\2\4\6\b\n\f\16\20\22\24\26"+
    "\30\32\34\36 \"$&(*,.\60\62\64\668:<>@BDFHJLNP\2\20\3\3\16\16\3\2 \"\3"+
    "\2#$\3\2:;\3\2%\'\3\2(+\3\2,/\3\2>I\3\2<=\3\2\36\37\3\2ST\3\2JM\3\2\13"+
    "\f\3\2VW\u0280\2U\3\2\2\2\4`\3\2\2\2\6e\3\2\2\2\by\3\2\2\2\n\u00b7\3\2"+
    "\2\2\f\u00ca\3\2\2\2\16\u00ce\3\2\2\2\20\u00d0\3\2\2\2\22\u00dc\3\2\2"+
    "\2\24\u00e0\3\2\2\2\26\u00e2\3\2\2\2\30\u00e4\3\2\2\2\32\u00ed\3\2\2\2"+
    "\34\u00ff\3\2\2\2\36\u0101\3\2\2\2 \u0106\3\2\2\2\"\u010d\3\2\2\2$\u0147"+
    "\3\2\2\2&\u014e\3\2\2\2(\u0157\3\2\2\2*\u0163\3\2\2\2,\u0165\3\2\2\2."+
    "\u0184\3\2\2\2\60\u018e\3\2\2\2\62\u01a3\3\2\2\2\64\u01a9\3\2\2\2\66\u01ae"+
    "\3\2\2\28\u01b0\3\2\2\2:\u01b4\3\2\2\2<\u01b7\3\2\2\2>\u01b9\3\2\2\2@"+
    "\u01e6\3\2\2\2B\u01f5\3\2\2\2D\u0205\3\2\2\2F\u0207\3\2\2\2H\u020b\3\2"+
    "\2\2J\u021b\3\2\2\2L\u022a\3\2\2\2N\u0232\3\2\2\2P\u0241\3\2\2\2RT\5\4"+
    "\3\2SR\3\2\2\2TW\3\2\2\2US\3\2\2\2UV\3\2\2\2V[\3\2\2\2WU\3\2\2\2XZ\5\b"+
    "\5\2YX\3\2\2\2Z]\3\2\2\2[Y\3\2\2\2[\\\3\2\2\2\\^\3\2\2\2][\3\2\2\2^_\7"+
    "\2\2\3_\3\3\2\2\2`a\5\32\16\2ab\7U\2\2bc\5\6\4\2cd\5\20\t\2d\5\3\2\2\2"+
    "eq\7\t\2\2fg\5\32\16\2gn\7U\2\2hi\7\r\2\2ij\5\32\16\2jk\7U\2\2km\3\2\2"+
    "\2lh\3\2\2\2mp\3\2\2\2nl\3\2\2\2no\3\2\2\2or\3\2\2\2pn\3\2\2\2qf\3\2\2"+
    "\2qr\3\2\2\2rs\3\2\2\2st\7\n\2\2t\7\3\2\2\2uz\5\n\6\2vw\5\f\7\2wx\t\2"+
    "\2\2xz\3\2\2\2yu\3\2\2\2yv\3\2\2\2z\t\3\2\2\2{|\7\17\2\2|}\7\t\2\2}~\5"+
    "$\23\2~\177\7\n\2\2\177\u0083\5\16\b\2\u0080\u0081\7\21\2\2\u0081\u0084"+
    "\5\16\b\2\u0082\u0084\6\6\2\2\u0083\u0080\3\2\2\2\u0083\u0082\3\2\2\2"+
    "\u0084\u00b8\3\2\2\2\u0085\u0086\7\22\2\2\u0086\u0087\7\t\2\2\u0087\u0088"+
    "\5$\23\2\u0088\u008b\7\n\2\2\u0089\u008c\5\16\b\2\u008a\u008c\5\22\n\2"+
    "\u008b\u0089\3\2\2\2\u008b\u008a\3\2\2\2\u008c\u00b8\3\2\2\2\u008d\u008e"+
    "\7\24\2\2\u008e\u0090\7\t\2\2\u008f\u0091\5\24\13\2\u0090\u008f\3\2\2"+
    "\2\u0090\u0091\3\2\2\2\u0091\u0092\3\2\2\2\u0092\u0094\7\16\2\2\u0093"+
    "\u0095\5$\23\2\u0094\u0093\3\2\2\2\u0094\u0095\3\2\2\2\u0095\u0096\3\2"+
    "\2\2\u0096\u0098\7\16\2\2\u0097\u0099\5\26\f\2\u0098\u0097\3\2\2\2\u0098"+
    "\u0099\3\2\2\2\u0099\u009a\3\2\2\2\u009a\u009d\7\n\2\2\u009b\u009e\5\16"+
    "\b\2\u009c\u009e\5\22\n\2\u009d\u009b\3\2\2\2\u009d\u009c\3\2\2\2\u009e"+
    "\u00b8\3\2\2\2\u009f\u00a0\7\24\2\2\u00a0\u00a1\7\t\2\2\u00a1\u00a2\5"+
    "\32\16\2\u00a2\u00a3\7U\2\2\u00a3\u00a4\7\66\2\2\u00a4\u00a5\5$\23\2\u00a5"+
    "\u00a6\7\n\2\2\u00a6\u00a7\5\16\b\2\u00a7\u00b8\3\2\2\2\u00a8\u00a9\7"+
    "\24\2\2\u00a9\u00aa\7\t\2\2\u00aa\u00ab\7U\2\2\u00ab\u00ac\7\20\2\2\u00ac"+
    "\u00ad\5$\23\2\u00ad\u00ae\7\n\2\2\u00ae\u00af\5\16\b\2\u00af\u00b8\3"+
    "\2\2\2\u00b0\u00b1\7\31\2\2\u00b1\u00b3\5\20\t\2\u00b2\u00b4\5 \21\2\u00b3"+
    "\u00b2\3\2\2\2\u00b4\u00b5\3\2\2\2\u00b5\u00b3\3\2\2\2\u00b5\u00b6\3\2"+
    "\2\2\u00b6\u00b8\3\2\2\2\u00b7{\3\2\2\2\u00b7\u0085\3\2\2\2\u00b7\u008d"+
    "\3\2\2\2\u00b7\u009f\3\2\2\2\u00b7\u00a8\3\2\2\2\u00b7\u00b0\3\2\2\2\u00b8"+
    "\13\3\2\2\2\u00b9\u00ba\7\23\2\2\u00ba\u00bb\5\20\t\2\u00bb\u00bc\7\22"+
    "\2\2\u00bc\u00bd\7\t\2\2\u00bd\u00be\5$\23\2\u00be\u00bf\7\n\2\2\u00bf"+
    "\u00cb\3\2\2\2\u00c0\u00cb\5\30\r\2\u00c1\u00cb\7\25\2\2\u00c2\u00cb\7"+
    "\26\2\2\u00c3\u00c5\7\27\2\2\u00c4\u00c6\5$\23\2\u00c5\u00c4\3\2\2\2\u00c5"+
    "\u00c6\3\2\2\2\u00c6\u00cb\3\2\2\2\u00c7\u00c8\7\33\2\2\u00c8\u00cb\5"+
    "$\23\2\u00c9\u00cb\5$\23\2\u00ca\u00b9\3\2\2\2\u00ca\u00c0\3\2\2\2\u00ca"+
    "\u00c1\3\2\2\2\u00ca\u00c2\3\2\2\2\u00ca\u00c3\3\2\2\2\u00ca\u00c7\3\2"+
    "\2\2\u00ca\u00c9\3\2\2\2\u00cb\r\3\2\2\2\u00cc\u00cf\5\20\t\2\u00cd\u00cf"+
    "\5\b\5\2\u00ce\u00cc\3\2\2\2\u00ce\u00cd\3\2\2\2\u00cf\17\3\2\2\2\u00d0"+
    "\u00d4\7\5\2\2\u00d1\u00d3\5\b\5\2\u00d2\u00d1\3\2\2\2\u00d3\u00d6\3\2"+
    "\2\2\u00d4\u00d2\3\2\2\2\u00d4\u00d5\3\2\2\2\u00d5\u00d8\3\2\2\2\u00d6"+
    "\u00d4\3\2\2\2\u00d7\u00d9\5\f\7\2\u00d8\u00d7\3\2\2\2\u00d8\u00d9\3\2"+
    "\2\2\u00d9\u00da\3\2\2\2\u00da\u00db\7\6\2\2\u00db\21\3\2\2\2\u00dc\u00dd"+
    "\7\16\2\2\u00dd\23\3\2\2\2\u00de\u00e1\5\30\r\2\u00df\u00e1\5$\23\2\u00e0"+
    "\u00de\3\2\2\2\u00e0\u00df\3\2\2\2\u00e1\25\3\2\2\2\u00e2\u00e3\5$\23"+
    "\2\u00e3\27\3\2\2\2\u00e4\u00e5\5\32\16\2\u00e5\u00ea\5\36\20\2\u00e6"+
    "\u00e7\7\r\2\2\u00e7\u00e9\5\36\20\2\u00e8\u00e6\3\2\2\2\u00e9\u00ec\3"+
    "\2\2\2\u00ea\u00e8\3\2\2\2\u00ea\u00eb\3\2\2\2\u00eb\31\3\2\2\2\u00ec"+
    "\u00ea\3\2\2\2\u00ed\u00f2\5\34\17\2\u00ee\u00ef\7\7\2\2\u00ef\u00f1\7"+
    "\b\2\2\u00f0\u00ee\3\2\2\2\u00f1\u00f4\3\2\2\2\u00f2\u00f0\3\2\2\2\u00f2"+
    "\u00f3\3\2\2\2\u00f3\33\3\2\2\2\u00f4\u00f2\3\2\2\2\u00f5\u0100\7T\2\2"+
    "\u00f6\u0100\7S\2\2\u00f7\u00fc\7U\2\2\u00f8\u00f9\7\13\2\2\u00f9\u00fb"+
    "\7W\2\2\u00fa\u00f8\3\2\2\2\u00fb\u00fe\3\2\2\2\u00fc\u00fa\3\2\2\2\u00fc"+
    "\u00fd\3\2\2\2\u00fd\u0100\3\2\2\2\u00fe\u00fc\3\2\2\2\u00ff\u00f5\3\2"+
    "\2\2\u00ff\u00f6\3\2\2\2\u00ff\u00f7\3\2\2\2\u0100\35\3\2\2\2\u0101\u0104"+
    "\7U\2\2\u0102\u0103\7>\2\2\u0103\u0105\5$\23\2\u0104\u0102\3\2\2\2\u0104"+
    "\u0105\3\2\2\2\u0105\37\3\2\2\2\u0106\u0107\7\32\2\2\u0107\u0108\7\t\2"+
    "\2\u0108\u0109\5\34\17\2\u0109\u010a\7U\2\2\u010a\u010b\7\n\2\2\u010b"+
    "\u010c\5\20\t\2\u010c!\3\2\2\2\u010d\u010e\b\22\1\2\u010e\u010f\5&\24"+
    "\2\u010f\u0139\3\2\2\2\u0110\u0111\f\17\2\2\u0111\u0112\t\3\2\2\u0112"+
    "\u0138\5\"\22\20\u0113\u0114\f\16\2\2\u0114\u0115\t\4\2\2\u0115\u0138"+
    "\5\"\22\17\u0116\u0117\f\r\2\2\u0117\u0118\t\5\2\2\u0118\u0138\5\"\22"+
    "\16\u0119\u011a\f\f\2\2\u011a\u011b\t\6\2\2\u011b\u0138\5\"\22\r\u011c"+
    "\u011d\f\13\2\2\u011d\u011e\t\7\2\2\u011e\u0138\5\"\22\f\u011f\u0120\f"+
    "\t\2\2\u0120\u0121\t\b\2\2\u0121\u0138\5\"\22\n\u0122\u0123\f\b\2\2\u0123"+
    "\u0124\7\60\2\2\u0124\u0138\5\"\22\t\u0125\u0126\f\7\2\2\u0126\u0127\7"+
    "\61\2\2\u0127\u0138\5\"\22\b\u0128\u0129\f\6\2\2\u0129\u012a\7\62\2\2"+
    "\u012a\u0138\5\"\22\7\u012b\u012c\f\5\2\2\u012c\u012d\7\63\2\2\u012d\u0138"+
    "\5\"\22\6\u012e\u012f\f\4\2\2\u012f\u0130\7\64\2\2\u0130\u0138\5\"\22"+
    "\5\u0131\u0132\f\3\2\2\u0132\u0133\7\67\2\2\u0133\u0138\5\"\22\3\u0134"+
    "\u0135\f\n\2\2\u0135\u0136\7\35\2\2\u0136\u0138\5\32\16\2\u0137\u0110"+
    "\3\2\2\2\u0137\u0113\3\2\2\2\u0137\u0116\3\2\2\2\u0137\u0119\3\2\2\2\u0137"+
    "\u011c\3\2\2\2\u0137\u011f\3\2\2\2\u0137\u0122\3\2\2\2\u0137\u0125\3\2"+
    "\2\2\u0137\u0128\3\2\2\2\u0137\u012b\3\2\2\2\u0137\u012e\3\2\2\2\u0137"+
    "\u0131\3\2\2\2\u0137\u0134\3\2\2\2\u0138\u013b\3\2\2\2\u0139\u0137\3\2"+
    "\2\2\u0139\u013a\3\2\2\2\u013a#\3\2\2\2\u013b\u0139\3\2\2\2\u013c\u0148"+
    "\5\"\22\2\u013d\u013e\5\"\22\2\u013e\u013f\7\65\2\2\u013f\u0140\5$\23"+
    "\2\u0140\u0141\7\66\2\2\u0141\u0142\5$\23\2\u0142\u0148\3\2\2\2\u0143"+
    "\u0144\5\"\22\2\u0144\u0145\t\t\2\2\u0145\u0146\5$\23\2\u0146\u0148\3"+
    "\2\2\2\u0147\u013c\3\2\2\2\u0147\u013d\3\2\2\2\u0147\u0143\3\2\2\2\u0148"+
    "%\3\2\2\2\u0149\u014a\t\n\2\2\u014a\u014f\5\60\31\2\u014b\u014c\t\4\2"+
    "\2\u014c\u014f\5&\24\2\u014d\u014f\5(\25\2\u014e\u0149\3\2\2\2\u014e\u014b"+
    "\3\2\2\2\u014e\u014d\3\2\2\2\u014f\'\3\2\2\2\u0150\u0158\5\60\31\2\u0151"+
    "\u0152\5\60\31\2\u0152\u0153\t\n\2\2\u0153\u0158\3\2\2\2\u0154\u0155\t"+
    "\13\2\2\u0155\u0158\5&\24\2\u0156\u0158\5*\26\2\u0157\u0150\3\2\2\2\u0157"+
    "\u0151\3\2\2\2\u0157\u0154\3\2\2\2\u0157\u0156\3\2\2\2\u0158)\3\2\2\2"+
    "\u0159\u015a\7\t\2\2\u015a\u015b\5,\27\2\u015b\u015c\7\n\2\2\u015c\u015d"+
    "\5&\24\2\u015d\u0164\3\2\2\2\u015e\u015f\7\t\2\2\u015f\u0160\5.\30\2\u0160"+
    "\u0161\7\n\2\2\u0161\u0162\5(\25\2\u0162\u0164\3\2\2\2\u0163\u0159\3\2"+
    "\2\2\u0163\u015e\3\2\2\2\u0164+\3\2\2\2\u0165\u0166\t\f\2\2\u0166-\3\2"+
    "\2\2\u0167\u016a\7T\2\2\u0168\u0169\7\7\2\2\u0169\u016b\7\b\2\2\u016a"+
    "\u0168\3\2\2\2\u016b\u016c\3\2\2\2\u016c\u016a\3\2\2\2\u016c\u016d\3\2"+
    "\2\2\u016d\u0185\3\2\2\2\u016e\u0171\7S\2\2\u016f\u0170\7\7\2\2\u0170"+
    "\u0172\7\b\2\2\u0171\u016f\3\2\2\2\u0172\u0173\3\2\2\2\u0173\u0171\3\2"+
    "\2\2\u0173\u0174\3\2\2\2\u0174\u0185\3\2\2\2\u0175\u017a\7U\2\2\u0176"+
    "\u0177\7\13\2\2\u0177\u0179\7W\2\2\u0178\u0176\3\2\2\2\u0179\u017c\3\2"+
    "\2\2\u017a\u0178\3\2\2\2\u017a\u017b\3\2\2\2\u017b\u0181\3\2\2\2\u017c"+
    "\u017a\3\2\2\2\u017d\u017e\7\7\2\2\u017e\u0180\7\b\2\2\u017f\u017d\3\2"+
    "\2\2\u0180\u0183\3\2\2\2\u0181\u017f\3\2\2\2\u0181\u0182\3\2\2\2\u0182"+
    "\u0185\3\2\2\2\u0183\u0181\3\2\2\2\u0184\u0167\3\2\2\2\u0184\u016e\3\2"+
    "\2\2\u0184\u0175\3\2\2\2\u0185/\3\2\2\2\u0186\u018a\5\62\32\2\u0187\u0189"+
    "\5\64\33\2\u0188\u0187\3\2\2\2\u0189\u018c\3\2\2\2\u018a\u0188\3\2\2\2"+
    "\u018a\u018b\3\2\2\2\u018b\u018f\3\2\2\2\u018c\u018a\3\2\2\2\u018d\u018f"+
    "\5@!\2\u018e\u0186\3\2\2\2\u018e\u018d\3\2\2\2\u018f\61\3\2\2\2\u0190"+
    "\u0191\7\t\2\2\u0191\u0192\5$\23\2\u0192\u0193\7\n\2\2\u0193\u01a4\3\2"+
    "\2\2\u0194\u01a4\t\r\2\2\u0195\u01a4\7P\2\2\u0196\u01a4\7Q\2\2\u0197\u01a4"+
    "\7R\2\2\u0198\u01a4\7N\2\2\u0199\u01a4\7O\2\2\u019a\u01a4\5B\"\2\u019b"+
    "\u01a4\5D#\2\u019c\u01a4\7U\2\2\u019d\u019e\7U\2\2\u019e\u01a4\5H%\2\u019f"+
    "\u01a0\7\30\2\2\u01a0\u01a1\5\34\17\2\u01a1\u01a2\5H%\2\u01a2\u01a4\3"+
    "\2\2\2\u01a3\u0190\3\2\2\2\u01a3\u0194\3\2\2\2\u01a3\u0195\3\2\2\2\u01a3"+
    "\u0196\3\2\2\2\u01a3\u0197\3\2\2\2\u01a3\u0198\3\2\2\2\u01a3\u0199\3\2"+
    "\2\2\u01a3\u019a\3\2\2\2\u01a3\u019b\3\2\2\2\u01a3\u019c\3\2\2\2\u01a3"+
    "\u019d\3\2\2\2\u01a3\u019f\3\2\2\2\u01a4\63\3\2\2\2\u01a5\u01aa\58\35"+
    "\2\u01a6\u01aa\5:\36\2\u01a7\u01aa\5> \2\u01a8\u01aa\5<\37\2\u01a9\u01a5"+
    "\3\2\2\2\u01a9\u01a6\3\2\2\2\u01a9\u01a7\3\2\2\2\u01a9\u01a8\3\2\2\2\u01aa"+
    "\65\3\2\2\2\u01ab\u01af\58\35\2\u01ac\u01af\5:\36\2\u01ad\u01af\5<\37"+
    "\2\u01ae\u01ab\3\2\2\2\u01ae\u01ac\3\2\2\2\u01ae\u01ad\3\2\2\2\u01af\67"+
    "\3\2\2\2\u01b0\u01b1\t\16\2\2\u01b1\u01b2\7W\2\2\u01b2\u01b3\5H%\2\u01b3"+
    "9\3\2\2\2\u01b4\u01b5\t\16\2\2\u01b5\u01b6\t\17\2\2\u01b6;\3\2\2\2\u01b7"+
    "\u01b8\t\16\2\2\u01b8=\3\2\2\2\u01b9\u01ba\7\7\2\2\u01ba\u01bb\5$\23\2"+
    "\u01bb\u01bc\7\b\2\2\u01bc?\3\2\2\2\u01bd\u01be\7\30\2\2\u01be\u01c3\5"+
    "\34\17\2\u01bf\u01c0\7\7\2\2\u01c0\u01c1\5$\23\2\u01c1\u01c2\7\b\2\2\u01c2"+
    "\u01c4\3\2\2\2\u01c3\u01bf\3\2\2\2\u01c4\u01c5\3\2\2\2\u01c5\u01c3\3\2"+
    "\2\2\u01c5\u01c6\3\2\2\2\u01c6\u01ce\3\2\2\2\u01c7\u01cb\5\66\34\2\u01c8"+
    "\u01ca\5\64\33\2\u01c9\u01c8\3\2\2\2\u01ca\u01cd\3\2\2\2\u01cb\u01c9\3"+
    "\2\2\2\u01cb\u01cc\3\2\2\2\u01cc\u01cf\3\2\2\2\u01cd\u01cb\3\2\2\2\u01ce"+
    "\u01c7\3\2\2\2\u01ce\u01cf\3\2\2\2\u01cf\u01e7\3\2\2\2\u01d0\u01d1\7\30"+
    "\2\2\u01d1\u01d2\5\34\17\2\u01d2\u01d3\7\7\2\2\u01d3\u01d4\7\b\2\2\u01d4"+
    "\u01dd\7\5\2\2\u01d5\u01da\5$\23\2\u01d6\u01d7\7\r\2\2\u01d7\u01d9\5$"+
    "\23\2\u01d8\u01d6\3\2\2\2\u01d9\u01dc\3\2\2\2\u01da\u01d8\3\2\2\2\u01da"+
    "\u01db\3\2\2\2\u01db\u01de\3\2\2\2\u01dc\u01da\3\2\2\2\u01dd\u01d5\3\2"+
    "\2\2\u01dd\u01de\3\2\2\2\u01de\u01df\3\2\2\2\u01df\u01e3\7\6\2\2\u01e0"+
    "\u01e2\5\64\33\2\u01e1\u01e0\3\2\2\2\u01e2\u01e5\3\2\2\2\u01e3\u01e1\3"+
    "\2\2\2\u01e3\u01e4\3\2\2\2\u01e4\u01e7\3\2\2\2\u01e5\u01e3\3\2\2\2\u01e6"+
    "\u01bd\3\2\2\2\u01e6\u01d0\3\2\2\2\u01e7A\3\2\2\2\u01e8\u01e9\7\7\2\2"+
    "\u01e9\u01ee\5$\23\2\u01ea\u01eb\7\r\2\2\u01eb\u01ed\5$\23\2\u01ec\u01ea"+
    "\3\2\2\2\u01ed\u01f0\3\2\2\2\u01ee\u01ec\3\2\2\2\u01ee\u01ef\3\2\2\2\u01ef"+
    "\u01f1\3\2\2\2\u01f0\u01ee\3\2\2\2\u01f1\u01f2\7\b\2\2\u01f2\u01f6\3\2"+
    "\2\2\u01f3\u01f4\7\7\2\2\u01f4\u01f6\7\b\2\2\u01f5\u01e8\3\2\2\2\u01f5"+
    "\u01f3\3\2\2\2\u01f6C\3\2\2\2\u01f7\u01f8\7\7\2\2\u01f8\u01fd\5F$\2\u01f9"+
    "\u01fa\7\r\2\2\u01fa\u01fc\5F$\2\u01fb\u01f9\3\2\2\2\u01fc\u01ff\3\2\2"+
    "\2\u01fd\u01fb\3\2\2\2\u01fd\u01fe\3\2\2\2\u01fe\u0200\3\2\2\2\u01ff\u01fd"+
    "\3\2\2\2\u0200\u0201\7\b\2\2\u0201\u0206\3\2\2\2\u0202\u0203\7\7\2\2\u0203"+
    "\u0204\7\66\2\2\u0204\u0206\7\b\2\2\u0205\u01f7\3\2\2\2\u0205\u0202\3"+
    "\2\2\2\u0206E\3\2\2\2\u0207\u0208\5$\23\2\u0208\u0209\7\66\2\2\u0209\u020a"+
    "\5$\23\2\u020aG\3\2\2\2\u020b\u0214\7\t\2\2\u020c\u0211\5J&\2\u020d\u020e"+
    "\7\r\2\2\u020e\u0210\5J&\2\u020f\u020d\3\2\2\2\u0210\u0213\3\2\2\2\u0211"+
    "\u020f\3\2\2\2\u0211\u0212\3\2\2\2\u0212\u0215\3\2\2\2\u0213\u0211\3\2"+
    "\2\2\u0214\u020c\3\2\2\2\u0214\u0215\3\2\2\2\u0215\u0216\3\2\2\2\u0216"+
    "\u0217\7\n\2\2\u0217I\3\2\2\2\u0218\u021c\5$\23\2\u0219\u021c\5L\'\2\u021a"+
    "\u021c\5P)\2\u021b\u0218\3\2\2\2\u021b\u0219\3\2\2\2\u021b\u021a\3\2\2"+
    "\2\u021cK\3\2\2\2\u021d\u022b\5N(\2\u021e\u0227\7\t\2\2\u021f\u0224\5"+
    "N(\2\u0220\u0221\7\r\2\2\u0221\u0223\5N(\2\u0222\u0220\3\2\2\2\u0223\u0226"+
    "\3\2\2\2\u0224\u0222\3\2\2\2\u0224\u0225\3\2\2\2\u0225\u0228\3\2\2\2\u0226"+
    "\u0224\3\2\2\2\u0227\u021f\3\2\2\2\u0227\u0228\3\2\2\2\u0228\u0229\3\2"+
    "\2\2\u0229\u022b\7\n\2\2\u022a\u021d\3\2\2\2\u022a\u021e\3\2\2\2\u022b"+
    "\u022c\3\2\2\2\u022c\u022f\79\2\2\u022d\u0230\5\20\t\2\u022e\u0230\5$"+
    "\23\2\u022f\u022d\3\2\2\2\u022f\u022e\3\2\2\2\u0230M\3\2\2\2\u0231\u0233"+
    "\5\32\16\2\u0232\u0231\3\2\2\2\u0232\u0233\3\2\2\2\u0233\u0234\3\2\2\2"+
    "\u0234\u0235\7U\2\2\u0235O\3\2\2\2\u0236\u0237\5\32\16\2\u0237\u0238\7"+
    "8\2\2\u0238\u0239\7U\2\2\u0239\u0242\3\2\2\2\u023a\u023b\5\32\16\2\u023b"+
    "\u023c\78\2\2\u023c\u023d\7\30\2\2\u023d\u0242\3\2\2\2\u023e\u023f\7\34"+
    "\2\2\u023f\u0240\78\2\2\u0240\u0242\7U\2\2\u0241\u0236\3\2\2\2\u0241\u023a"+
    "\3\2\2\2\u0241\u023e\3\2\2\2\u0242Q\3\2\2\2>U[nqy\u0083\u008b\u0090\u0094"+
    "\u0098\u009d\u00b5\u00b7\u00c5\u00ca\u00ce\u00d4\u00d8\u00e0\u00ea\u00f2"+
    "\u00fc\u00ff\u0104\u0137\u0139\u0147\u014e\u0157\u0163\u016c\u0173\u017a"+
    "\u0181\u0184\u018a\u018e\u01a3\u01a9\u01ae\u01c5\u01cb\u01ce\u01da\u01dd"+
    "\u01e3\u01e6\u01ee\u01f5\u01fd\u0205\u0211\u0214\u021b\u0224\u0227\u022a"+
    "\u022f\u0232\u0241";
  public static final ATN _ATN =
    new ATNDeserializer().deserialize(_serializedATN.toCharArray());
  static {
    _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
    for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
      _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
    }
  }
}
