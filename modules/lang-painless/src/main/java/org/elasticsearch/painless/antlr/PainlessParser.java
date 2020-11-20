// ANTLR GENERATED CODE: DO NOT EDIT
package org.elasticsearch.painless.antlr;

import java.util.HashMap;
import java.util.Map;

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
    DEF=82, ID=83, DOTINTEGER=84, DOTID=85;
  public static final int
    RULE_source = 0, RULE_function = 1, RULE_parameters = 2, RULE_parameter = 3,
    RULE_statement = 4, RULE_rstatement = 5, RULE_dstatement = 6, RULE_trailer = 7,
    RULE_block = 8, RULE_empty = 9, RULE_initializer = 10, RULE_afterthought = 11,
    RULE_declaration = 12, RULE_decltype = 13, RULE_type = 14, RULE_declvar = 15,
    RULE_trap = 16, RULE_noncondexpression = 17, RULE_expression = 18, RULE_unary = 19,
    RULE_unarynotaddsub = 20, RULE_castexpression = 21, RULE_primordefcasttype = 22,
    RULE_refcasttype = 23, RULE_chain = 24, RULE_primary = 25, RULE_postfix = 26,
    RULE_postdot = 27, RULE_callinvoke = 28, RULE_fieldaccess = 29, RULE_braceaccess = 30,
    RULE_arrayinitializer = 31, RULE_listinitializer = 32, RULE_mapinitializer = 33,
    RULE_maptoken = 34, RULE_arguments = 35, RULE_argument = 36, RULE_lambda = 37,
    RULE_lamtype = 38, RULE_funcref = 39;
  public static final String[] ruleNames = {
    "source", "function", "parameters", "parameter", "statement", "rstatement",
    "dstatement", "trailer", "block", "empty", "initializer", "afterthought",
    "declaration", "decltype", "type", "declvar", "trap", "noncondexpression",
    "expression", "unary", "unarynotaddsub", "castexpression", "primordefcasttype",
    "refcasttype", "chain", "primary", "postfix", "postdot", "callinvoke",
    "fieldaccess", "braceaccess", "arrayinitializer", "listinitializer", "mapinitializer",
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
    "NULL", "PRIMITIVE", "DEF", "ID", "DOTINTEGER", "DOTID"
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


  public static class Scope {
      public final Scope parent;
      public final Map<String, String> variables;

      public Scope(Scope parent) {
          this.parent = null;
          this.variables = new HashMap<>();
      }

      public void addVariable(String id, String type) {
          variables.put(id, type);
      }

      public String getVariable(String id) {
          String type = variables.get(id);

          if (type == null && parent != null) {
              return parent.getVariable(id);
          }

          return type;
      }
  }

  public void pushScope() {
      this.scope = new Scope(this.scope.parent);
  }

  public void popScope() {
      this.scope = this.scope.parent;
  }

  public void addVariable(String id, String type) {
      this.scope.addVariable(id, type);
  }

  public String getVariable(String id) {
      return this.scope.getVariable(id);
  }

  public Scope scope = new Scope(null);
  public String cursorid = null;
  public String cursortype = null;

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
       pushScope();
      setState(90);
      _errHandler.sync(this);
      _la = _input.LA(1);
      while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LBRACE) | (1L << LP) | (1L << IF) | (1L << WHILE) | (1L << DO) | (1L << FOR) | (1L << CONTINUE) | (1L << BREAK) | (1L << RETURN) | (1L << NEW) | (1L << TRY) | (1L << THROW) | (1L << BOOLNOT) | (1L << BWNOT) | (1L << ADD) | (1L << SUB) | (1L << INCR) | (1L << DECR))) != 0) || ((((_la - 72)) & ~0x3f) == 0 && ((1L << (_la - 72)) & ((1L << (OCTAL - 72)) | (1L << (HEX - 72)) | (1L << (INTEGER - 72)) | (1L << (DECIMAL - 72)) | (1L << (STRING - 72)) | (1L << (REGEX - 72)) | (1L << (TRUE - 72)) | (1L << (FALSE - 72)) | (1L << (NULL - 72)) | (1L << (PRIMITIVE - 72)) | (1L << (DEF - 72)) | (1L << (ID - 72)))) != 0)) {
        {
        {
        setState(87);
        statement();
        }
        }
        setState(92);
        _errHandler.sync(this);
        _la = _input.LA(1);
      }
      setState(93);
      match(EOF);
       popScope();
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
       pushScope();
      setState(97);
      decltype();
      setState(98);
      match(ID);
      setState(99);
      parameters();
       pushScope();
      setState(101);
      block();
       popScope(); popScope();
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
    public List<ParameterContext> parameter() {
      return getRuleContexts(ParameterContext.class);
    }
    public ParameterContext parameter(int i) {
      return getRuleContext(ParameterContext.class,i);
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
      setState(104);
      match(LP);
      setState(113);
      _la = _input.LA(1);
      if (((((_la - 81)) & ~0x3f) == 0 && ((1L << (_la - 81)) & ((1L << (PRIMITIVE - 81)) | (1L << (DEF - 81)) | (1L << (ID - 81)))) != 0)) {
        {
        setState(105);
        parameter();
        setState(110);
        _errHandler.sync(this);
        _la = _input.LA(1);
        while (_la==COMMA) {
          {
          {
          setState(106);
          match(COMMA);
          setState(107);
          parameter();
          }
          }
          setState(112);
          _errHandler.sync(this);
          _la = _input.LA(1);
        }
        }
      }

      setState(115);
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

  public static class ParameterContext extends ParserRuleContext {
    public DecltypeContext decltype;
    public Token ID;
    public DecltypeContext decltype() {
      return getRuleContext(DecltypeContext.class,0);
    }
    public TerminalNode ID() { return getToken(PainlessParser.ID, 0); }
    public ParameterContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_parameter; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitParameter(this);
      else return visitor.visitChildren(this);
    }
  }

  public final ParameterContext parameter() throws RecognitionException {
    ParameterContext _localctx = new ParameterContext(_ctx, getState());
    enterRule(_localctx, 6, RULE_parameter);
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(117);
      ((ParameterContext)_localctx).decltype = decltype();
      setState(118);
      ((ParameterContext)_localctx).ID = match(ID);
       addVariable((((ParameterContext)_localctx).ID!=null?((ParameterContext)_localctx).ID.getText():null), (((ParameterContext)_localctx).decltype!=null?_input.getText(((ParameterContext)_localctx).decltype.start,((ParameterContext)_localctx).decltype.stop):null));
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
    enterRule(_localctx, 8, RULE_statement);
    int _la;
    try {
      setState(125);
      switch (_input.LA(1)) {
      case IF:
      case WHILE:
      case FOR:
      case TRY:
        enterOuterAlt(_localctx, 1);
        {
        setState(121);
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
        setState(122);
        dstatement();
        setState(123);
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
    enterRule(_localctx, 10, RULE_rstatement);
    int _la;
    try {
      int _alt;
      setState(204);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
      case 1:
        _localctx = new IfContext(_localctx);
        enterOuterAlt(_localctx, 1);
        {
        setState(127);
        match(IF);
        setState(128);
        match(LP);
        setState(129);
        expression();
        setState(130);
        match(RP);
         pushScope();
        setState(132);
        trailer();
         popScope();
        setState(140);
        _errHandler.sync(this);
        switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
        case 1:
          {
          setState(134);
          match(ELSE);
           pushScope();
          setState(136);
          trailer();
           popScope();
          }
          break;
        case 2:
          {
          setState(139);
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
        setState(142);
        match(WHILE);
         pushScope();
        setState(144);
        match(LP);
        setState(145);
        expression();
        setState(146);
        match(RP);
        setState(149);
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
          setState(147);
          trailer();
          }
          break;
        case SEMICOLON:
          {
          setState(148);
          empty();
          }
          break;
        default:
          throw new NoViableAltException(this);
        }
         popScope();
        }
        break;
      case 3:
        _localctx = new ForContext(_localctx);
        enterOuterAlt(_localctx, 3);
        {
        setState(153);
        match(FOR);
         pushScope();
        setState(155);
        match(LP);
        setState(157);
        _la = _input.LA(1);
        if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LBRACE) | (1L << LP) | (1L << NEW) | (1L << BOOLNOT) | (1L << BWNOT) | (1L << ADD) | (1L << SUB) | (1L << INCR) | (1L << DECR))) != 0) || ((((_la - 72)) & ~0x3f) == 0 && ((1L << (_la - 72)) & ((1L << (OCTAL - 72)) | (1L << (HEX - 72)) | (1L << (INTEGER - 72)) | (1L << (DECIMAL - 72)) | (1L << (STRING - 72)) | (1L << (REGEX - 72)) | (1L << (TRUE - 72)) | (1L << (FALSE - 72)) | (1L << (NULL - 72)) | (1L << (PRIMITIVE - 72)) | (1L << (DEF - 72)) | (1L << (ID - 72)))) != 0)) {
          {
          setState(156);
          initializer();
          }
        }

        setState(159);
        match(SEMICOLON);
        setState(161);
        _la = _input.LA(1);
        if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LBRACE) | (1L << LP) | (1L << NEW) | (1L << BOOLNOT) | (1L << BWNOT) | (1L << ADD) | (1L << SUB) | (1L << INCR) | (1L << DECR))) != 0) || ((((_la - 72)) & ~0x3f) == 0 && ((1L << (_la - 72)) & ((1L << (OCTAL - 72)) | (1L << (HEX - 72)) | (1L << (INTEGER - 72)) | (1L << (DECIMAL - 72)) | (1L << (STRING - 72)) | (1L << (REGEX - 72)) | (1L << (TRUE - 72)) | (1L << (FALSE - 72)) | (1L << (NULL - 72)) | (1L << (ID - 72)))) != 0)) {
          {
          setState(160);
          expression();
          }
        }

        setState(163);
        match(SEMICOLON);
        setState(165);
        _la = _input.LA(1);
        if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LBRACE) | (1L << LP) | (1L << NEW) | (1L << BOOLNOT) | (1L << BWNOT) | (1L << ADD) | (1L << SUB) | (1L << INCR) | (1L << DECR))) != 0) || ((((_la - 72)) & ~0x3f) == 0 && ((1L << (_la - 72)) & ((1L << (OCTAL - 72)) | (1L << (HEX - 72)) | (1L << (INTEGER - 72)) | (1L << (DECIMAL - 72)) | (1L << (STRING - 72)) | (1L << (REGEX - 72)) | (1L << (TRUE - 72)) | (1L << (FALSE - 72)) | (1L << (NULL - 72)) | (1L << (ID - 72)))) != 0)) {
          {
          setState(164);
          afterthought();
          }
        }

        setState(167);
        match(RP);
        setState(170);
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
          setState(168);
          trailer();
          }
          break;
        case SEMICOLON:
          {
          setState(169);
          empty();
          }
          break;
        default:
          throw new NoViableAltException(this);
        }
         popScope();
        }
        break;
      case 4:
        _localctx = new EachContext(_localctx);
        enterOuterAlt(_localctx, 4);
        {
        setState(174);
        match(FOR);
         pushScope();
        setState(176);
        match(LP);
        setState(177);
        decltype();
        setState(178);
        match(ID);
        setState(179);
        match(COLON);
        setState(180);
        expression();
        setState(181);
        match(RP);
        setState(182);
        trailer();
         popScope();
        }
        break;
      case 5:
        _localctx = new IneachContext(_localctx);
        enterOuterAlt(_localctx, 5);
        {
        setState(185);
        match(FOR);
         pushScope();
        setState(187);
        match(LP);
        setState(188);
        match(ID);
        setState(189);
        match(IN);
        setState(190);
        expression();
        setState(191);
        match(RP);
        setState(192);
        trailer();
         popScope();
        }
        break;
      case 6:
        _localctx = new TryContext(_localctx);
        enterOuterAlt(_localctx, 6);
        {
        setState(195);
        match(TRY);
         pushScope();
        setState(197);
        block();
         popScope();
        setState(200);
        _errHandler.sync(this);
        _alt = 1;
        do {
          switch (_alt) {
          case 1:
            {
            {
            setState(199);
            trap();
            }
            }
            break;
          default:
            throw new NoViableAltException(this);
          }
          setState(202);
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
    enterRule(_localctx, 12, RULE_dstatement);
    int _la;
    try {
      setState(225);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
      case 1:
        _localctx = new DoContext(_localctx);
        enterOuterAlt(_localctx, 1);
        {
        setState(206);
        match(DO);
         pushScope();
        setState(208);
        block();
        setState(209);
        match(WHILE);
        setState(210);
        match(LP);
        setState(211);
        expression();
        setState(212);
        match(RP);
         popScope();
        }
        break;
      case 2:
        _localctx = new DeclContext(_localctx);
        enterOuterAlt(_localctx, 2);
        {
        setState(215);
        declaration();
        }
        break;
      case 3:
        _localctx = new ContinueContext(_localctx);
        enterOuterAlt(_localctx, 3);
        {
        setState(216);
        match(CONTINUE);
        }
        break;
      case 4:
        _localctx = new BreakContext(_localctx);
        enterOuterAlt(_localctx, 4);
        {
        setState(217);
        match(BREAK);
        }
        break;
      case 5:
        _localctx = new ReturnContext(_localctx);
        enterOuterAlt(_localctx, 5);
        {
        setState(218);
        match(RETURN);
        setState(220);
        _la = _input.LA(1);
        if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LBRACE) | (1L << LP) | (1L << NEW) | (1L << BOOLNOT) | (1L << BWNOT) | (1L << ADD) | (1L << SUB) | (1L << INCR) | (1L << DECR))) != 0) || ((((_la - 72)) & ~0x3f) == 0 && ((1L << (_la - 72)) & ((1L << (OCTAL - 72)) | (1L << (HEX - 72)) | (1L << (INTEGER - 72)) | (1L << (DECIMAL - 72)) | (1L << (STRING - 72)) | (1L << (REGEX - 72)) | (1L << (TRUE - 72)) | (1L << (FALSE - 72)) | (1L << (NULL - 72)) | (1L << (ID - 72)))) != 0)) {
          {
          setState(219);
          expression();
          }
        }

        }
        break;
      case 6:
        _localctx = new ThrowContext(_localctx);
        enterOuterAlt(_localctx, 6);
        {
        setState(222);
        match(THROW);
        setState(223);
        expression();
        }
        break;
      case 7:
        _localctx = new ExprContext(_localctx);
        enterOuterAlt(_localctx, 7);
        {
        setState(224);
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
    enterRule(_localctx, 14, RULE_trailer);
    try {
      setState(229);
      switch (_input.LA(1)) {
      case LBRACK:
        enterOuterAlt(_localctx, 1);
        {
        setState(227);
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
        setState(228);
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
    enterRule(_localctx, 16, RULE_block);
    int _la;
    try {
      int _alt;
      enterOuterAlt(_localctx, 1);
      {
      setState(231);
      match(LBRACK);
      setState(235);
      _errHandler.sync(this);
      _alt = getInterpreter().adaptivePredict(_input,16,_ctx);
      while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
        if ( _alt==1 ) {
          {
          {
          setState(232);
          statement();
          }
          }
        }
        setState(237);
        _errHandler.sync(this);
        _alt = getInterpreter().adaptivePredict(_input,16,_ctx);
      }
      setState(239);
      _la = _input.LA(1);
      if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LBRACE) | (1L << LP) | (1L << DO) | (1L << CONTINUE) | (1L << BREAK) | (1L << RETURN) | (1L << NEW) | (1L << THROW) | (1L << BOOLNOT) | (1L << BWNOT) | (1L << ADD) | (1L << SUB) | (1L << INCR) | (1L << DECR))) != 0) || ((((_la - 72)) & ~0x3f) == 0 && ((1L << (_la - 72)) & ((1L << (OCTAL - 72)) | (1L << (HEX - 72)) | (1L << (INTEGER - 72)) | (1L << (DECIMAL - 72)) | (1L << (STRING - 72)) | (1L << (REGEX - 72)) | (1L << (TRUE - 72)) | (1L << (FALSE - 72)) | (1L << (NULL - 72)) | (1L << (PRIMITIVE - 72)) | (1L << (DEF - 72)) | (1L << (ID - 72)))) != 0)) {
        {
        setState(238);
        dstatement();
        }
      }

      setState(241);
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
    enterRule(_localctx, 18, RULE_empty);
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(243);
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
    enterRule(_localctx, 20, RULE_initializer);
    try {
      setState(247);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,18,_ctx) ) {
      case 1:
        enterOuterAlt(_localctx, 1);
        {
        setState(245);
        declaration();
        }
        break;
      case 2:
        enterOuterAlt(_localctx, 2);
        {
        setState(246);
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
    enterRule(_localctx, 22, RULE_afterthought);
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(249);
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
    public DecltypeContext decltype;
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
    enterRule(_localctx, 24, RULE_declaration);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(251);
      ((DeclarationContext)_localctx).decltype = decltype();
      setState(252);
      declvar((((DeclarationContext)_localctx).decltype!=null?_input.getText(((DeclarationContext)_localctx).decltype.start,((DeclarationContext)_localctx).decltype.stop):null));
      setState(257);
      _errHandler.sync(this);
      _la = _input.LA(1);
      while (_la==COMMA) {
        {
        {
        setState(253);
        match(COMMA);
        setState(254);
        declvar((((DeclarationContext)_localctx).decltype!=null?_input.getText(((DeclarationContext)_localctx).decltype.start,((DeclarationContext)_localctx).decltype.stop):null));
        }
        }
        setState(259);
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
    enterRule(_localctx, 26, RULE_decltype);
    try {
      int _alt;
      enterOuterAlt(_localctx, 1);
      {
      setState(260);
      type();
      setState(265);
      _errHandler.sync(this);
      _alt = getInterpreter().adaptivePredict(_input,20,_ctx);
      while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
        if ( _alt==1 ) {
          {
          {
          setState(261);
          match(LBRACE);
          setState(262);
          match(RBRACE);
          }
          }
        }
        setState(267);
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
    enterRule(_localctx, 28, RULE_type);
    try {
      int _alt;
      setState(278);
      switch (_input.LA(1)) {
      case DEF:
        enterOuterAlt(_localctx, 1);
        {
        setState(268);
        match(DEF);
        }
        break;
      case PRIMITIVE:
        enterOuterAlt(_localctx, 2);
        {
        setState(269);
        match(PRIMITIVE);
        }
        break;
      case ID:
        enterOuterAlt(_localctx, 3);
        {
        setState(270);
        match(ID);
        setState(275);
        _errHandler.sync(this);
        _alt = getInterpreter().adaptivePredict(_input,21,_ctx);
        while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
          if ( _alt==1 ) {
            {
            {
            setState(271);
            match(DOT);
            setState(272);
            match(DOTID);
            }
            }
          }
          setState(277);
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
    public String typename;
    public Token ID;
    public TerminalNode ID() { return getToken(PainlessParser.ID, 0); }
    public TerminalNode ASSIGN() { return getToken(PainlessParser.ASSIGN, 0); }
    public ExpressionContext expression() {
      return getRuleContext(ExpressionContext.class,0);
    }
    public DeclvarContext(ParserRuleContext parent, int invokingState) { super(parent, invokingState); }
    public DeclvarContext(ParserRuleContext parent, int invokingState, String typename) {
      super(parent, invokingState);
      this.typename = typename;
    }
    @Override public int getRuleIndex() { return RULE_declvar; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitDeclvar(this);
      else return visitor.visitChildren(this);
    }
  }

  public final DeclvarContext declvar(String typename) throws RecognitionException {
    DeclvarContext _localctx = new DeclvarContext(_ctx, getState(), typename);
    enterRule(_localctx, 30, RULE_declvar);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(280);
      ((DeclvarContext)_localctx).ID = match(ID);
      setState(283);
      _la = _input.LA(1);
      if (_la==ASSIGN) {
        {
        setState(281);
        match(ASSIGN);
        setState(282);
        expression();
        }
      }

       addVariable((((DeclvarContext)_localctx).ID!=null?((DeclvarContext)_localctx).ID.getText():null), _localctx.typename);
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
    enterRule(_localctx, 32, RULE_trap);
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(287);
      match(CATCH);
       pushScope();
      setState(289);
      match(LP);
      setState(290);
      type();
      setState(291);
      match(ID);
      setState(292);
      match(RP);
      setState(293);
      block();
       popScope();
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
    int _startState = 34;
    enterRecursionRule(_localctx, 34, RULE_noncondexpression, _p);
    int _la;
    try {
      int _alt;
      enterOuterAlt(_localctx, 1);
      {
      {
      _localctx = new SingleContext(_localctx);
      _ctx = _localctx;
      _prevctx = _localctx;

      setState(297);
      unary();
      }
      _ctx.stop = _input.LT(-1);
      setState(340);
      _errHandler.sync(this);
      _alt = getInterpreter().adaptivePredict(_input,25,_ctx);
      while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
        if ( _alt==1 ) {
          if ( _parseListeners!=null ) triggerExitRuleEvent();
          _prevctx = _localctx;
          {
          setState(338);
          _errHandler.sync(this);
          switch ( getInterpreter().adaptivePredict(_input,24,_ctx) ) {
          case 1:
            {
            _localctx = new BinaryContext(new NoncondexpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_noncondexpression);
            setState(299);
            if (!(precpred(_ctx, 13))) throw new FailedPredicateException(this, "precpred(_ctx, 13)");
            setState(300);
            _la = _input.LA(1);
            if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << MUL) | (1L << DIV) | (1L << REM))) != 0)) ) {
            _errHandler.recoverInline(this);
            } else {
              consume();
            }
            setState(301);
            noncondexpression(14);
            }
            break;
          case 2:
            {
            _localctx = new BinaryContext(new NoncondexpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_noncondexpression);
            setState(302);
            if (!(precpred(_ctx, 12))) throw new FailedPredicateException(this, "precpred(_ctx, 12)");
            setState(303);
            _la = _input.LA(1);
            if ( !(_la==ADD || _la==SUB) ) {
            _errHandler.recoverInline(this);
            } else {
              consume();
            }
            setState(304);
            noncondexpression(13);
            }
            break;
          case 3:
            {
            _localctx = new BinaryContext(new NoncondexpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_noncondexpression);
            setState(305);
            if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
            setState(306);
            _la = _input.LA(1);
            if ( !(_la==FIND || _la==MATCH) ) {
            _errHandler.recoverInline(this);
            } else {
              consume();
            }
            setState(307);
            noncondexpression(12);
            }
            break;
          case 4:
            {
            _localctx = new BinaryContext(new NoncondexpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_noncondexpression);
            setState(308);
            if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
            setState(309);
            _la = _input.LA(1);
            if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LSH) | (1L << RSH) | (1L << USH))) != 0)) ) {
            _errHandler.recoverInline(this);
            } else {
              consume();
            }
            setState(310);
            noncondexpression(11);
            }
            break;
          case 5:
            {
            _localctx = new CompContext(new NoncondexpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_noncondexpression);
            setState(311);
            if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
            setState(312);
            _la = _input.LA(1);
            if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LT) | (1L << LTE) | (1L << GT) | (1L << GTE))) != 0)) ) {
            _errHandler.recoverInline(this);
            } else {
              consume();
            }
            setState(313);
            noncondexpression(10);
            }
            break;
          case 6:
            {
            _localctx = new CompContext(new NoncondexpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_noncondexpression);
            setState(314);
            if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
            setState(315);
            _la = _input.LA(1);
            if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << EQ) | (1L << EQR) | (1L << NE) | (1L << NER))) != 0)) ) {
            _errHandler.recoverInline(this);
            } else {
              consume();
            }
            setState(316);
            noncondexpression(8);
            }
            break;
          case 7:
            {
            _localctx = new BinaryContext(new NoncondexpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_noncondexpression);
            setState(317);
            if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
            setState(318);
            match(BWAND);
            setState(319);
            noncondexpression(7);
            }
            break;
          case 8:
            {
            _localctx = new BinaryContext(new NoncondexpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_noncondexpression);
            setState(320);
            if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
            setState(321);
            match(XOR);
            setState(322);
            noncondexpression(6);
            }
            break;
          case 9:
            {
            _localctx = new BinaryContext(new NoncondexpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_noncondexpression);
            setState(323);
            if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
            setState(324);
            match(BWOR);
            setState(325);
            noncondexpression(5);
            }
            break;
          case 10:
            {
            _localctx = new BoolContext(new NoncondexpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_noncondexpression);
            setState(326);
            if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
            setState(327);
            match(BOOLAND);
            setState(328);
            noncondexpression(4);
            }
            break;
          case 11:
            {
            _localctx = new BoolContext(new NoncondexpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_noncondexpression);
            setState(329);
            if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
            setState(330);
            match(BOOLOR);
            setState(331);
            noncondexpression(3);
            }
            break;
          case 12:
            {
            _localctx = new ElvisContext(new NoncondexpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_noncondexpression);
            setState(332);
            if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
            setState(333);
            match(ELVIS);
            setState(334);
            noncondexpression(1);
            }
            break;
          case 13:
            {
            _localctx = new InstanceofContext(new NoncondexpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_noncondexpression);
            setState(335);
            if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
            setState(336);
            match(INSTANCEOF);
            setState(337);
            decltype();
            }
            break;
          }
          }
        }
        setState(342);
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
    enterRule(_localctx, 36, RULE_expression);
    int _la;
    try {
      setState(354);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,26,_ctx) ) {
      case 1:
        _localctx = new NonconditionalContext(_localctx);
        enterOuterAlt(_localctx, 1);
        {
        setState(343);
        noncondexpression(0);
        }
        break;
      case 2:
        _localctx = new ConditionalContext(_localctx);
        enterOuterAlt(_localctx, 2);
        {
        setState(344);
        noncondexpression(0);
        setState(345);
        match(COND);
        setState(346);
        expression();
        setState(347);
        match(COLON);
        setState(348);
        expression();
        }
        break;
      case 3:
        _localctx = new AssignmentContext(_localctx);
        enterOuterAlt(_localctx, 3);
        {
        setState(350);
        noncondexpression(0);
        setState(351);
        _la = _input.LA(1);
        if ( !(((((_la - 60)) & ~0x3f) == 0 && ((1L << (_la - 60)) & ((1L << (ASSIGN - 60)) | (1L << (AADD - 60)) | (1L << (ASUB - 60)) | (1L << (AMUL - 60)) | (1L << (ADIV - 60)) | (1L << (AREM - 60)) | (1L << (AAND - 60)) | (1L << (AXOR - 60)) | (1L << (AOR - 60)) | (1L << (ALSH - 60)) | (1L << (ARSH - 60)) | (1L << (AUSH - 60)))) != 0)) ) {
        _errHandler.recoverInline(this);
        } else {
          consume();
        }
        setState(352);
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
    enterRule(_localctx, 38, RULE_unary);
    int _la;
    try {
      setState(361);
      switch (_input.LA(1)) {
      case INCR:
      case DECR:
        _localctx = new PreContext(_localctx);
        enterOuterAlt(_localctx, 1);
        {
        setState(356);
        _la = _input.LA(1);
        if ( !(_la==INCR || _la==DECR) ) {
        _errHandler.recoverInline(this);
        } else {
          consume();
        }
        setState(357);
        chain();
        }
        break;
      case ADD:
      case SUB:
        _localctx = new AddsubContext(_localctx);
        enterOuterAlt(_localctx, 2);
        {
        setState(358);
        _la = _input.LA(1);
        if ( !(_la==ADD || _la==SUB) ) {
        _errHandler.recoverInline(this);
        } else {
          consume();
        }
        setState(359);
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
        setState(360);
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
    enterRule(_localctx, 40, RULE_unarynotaddsub);
    int _la;
    try {
      setState(370);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,28,_ctx) ) {
      case 1:
        _localctx = new ReadContext(_localctx);
        enterOuterAlt(_localctx, 1);
        {
        setState(363);
        chain();
        }
        break;
      case 2:
        _localctx = new PostContext(_localctx);
        enterOuterAlt(_localctx, 2);
        {
        setState(364);
        chain();
        setState(365);
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
        setState(367);
        _la = _input.LA(1);
        if ( !(_la==BOOLNOT || _la==BWNOT) ) {
        _errHandler.recoverInline(this);
        } else {
          consume();
        }
        setState(368);
        unary();
        }
        break;
      case 4:
        _localctx = new CastContext(_localctx);
        enterOuterAlt(_localctx, 4);
        {
        setState(369);
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
    enterRule(_localctx, 42, RULE_castexpression);
    try {
      setState(382);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,29,_ctx) ) {
      case 1:
        _localctx = new PrimordefcastContext(_localctx);
        enterOuterAlt(_localctx, 1);
        {
        setState(372);
        match(LP);
        setState(373);
        primordefcasttype();
        setState(374);
        match(RP);
        setState(375);
        unary();
        }
        break;
      case 2:
        _localctx = new RefcastContext(_localctx);
        enterOuterAlt(_localctx, 2);
        {
        setState(377);
        match(LP);
        setState(378);
        refcasttype();
        setState(379);
        match(RP);
        setState(380);
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
    enterRule(_localctx, 44, RULE_primordefcasttype);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(384);
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
    enterRule(_localctx, 46, RULE_refcasttype);
    int _la;
    try {
      setState(415);
      switch (_input.LA(1)) {
      case DEF:
        enterOuterAlt(_localctx, 1);
        {
        setState(386);
        match(DEF);
        setState(389);
        _errHandler.sync(this);
        _la = _input.LA(1);
        do {
          {
          {
          setState(387);
          match(LBRACE);
          setState(388);
          match(RBRACE);
          }
          }
          setState(391);
          _errHandler.sync(this);
          _la = _input.LA(1);
        } while ( _la==LBRACE );
        }
        break;
      case PRIMITIVE:
        enterOuterAlt(_localctx, 2);
        {
        setState(393);
        match(PRIMITIVE);
        setState(396);
        _errHandler.sync(this);
        _la = _input.LA(1);
        do {
          {
          {
          setState(394);
          match(LBRACE);
          setState(395);
          match(RBRACE);
          }
          }
          setState(398);
          _errHandler.sync(this);
          _la = _input.LA(1);
        } while ( _la==LBRACE );
        }
        break;
      case ID:
        enterOuterAlt(_localctx, 3);
        {
        setState(400);
        match(ID);
        setState(405);
        _errHandler.sync(this);
        _la = _input.LA(1);
        while (_la==DOT) {
          {
          {
          setState(401);
          match(DOT);
          setState(402);
          match(DOTID);
          }
          }
          setState(407);
          _errHandler.sync(this);
          _la = _input.LA(1);
        }
        setState(412);
        _errHandler.sync(this);
        _la = _input.LA(1);
        while (_la==LBRACE) {
          {
          {
          setState(408);
          match(LBRACE);
          setState(409);
          match(RBRACE);
          }
          }
          setState(414);
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
      Token s = _input.get(_input.index());
    ChainContext _localctx = new ChainContext(_ctx, getState());
    enterRule(_localctx, 48, RULE_chain);
    try {
      int _alt;
      setState(425);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,36,_ctx) ) {
      case 1:
        _localctx = new DynamicContext(_localctx);
        enterOuterAlt(_localctx, 1);
        {
        setState(417);
        primary();
        setState(421);
        _errHandler.sync(this);
        _alt = getInterpreter().adaptivePredict(_input,35,_ctx);
        while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
          if ( _alt==1 ) {
            {
            {
            setState(418);
            postfix();
            }
            }
          }
          setState(423);
          _errHandler.sync(this);
          _alt = getInterpreter().adaptivePredict(_input,35,_ctx);
        }
        }
        break;
      case 2:
        _localctx = new NewarrayContext(_localctx);
        enterOuterAlt(_localctx, 2);
        {
        setState(424);
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
    public Token ID;
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
    enterRule(_localctx, 50, RULE_primary);
    int _la;
    try {
      setState(447);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,37,_ctx) ) {
      case 1:
        _localctx = new PrecedenceContext(_localctx);
        enterOuterAlt(_localctx, 1);
        {
        setState(427);
        match(LP);
        setState(428);
        expression();
        setState(429);
        match(RP);
        }
        break;
      case 2:
        _localctx = new NumericContext(_localctx);
        enterOuterAlt(_localctx, 2);
        {
        setState(431);
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
        setState(432);
        match(TRUE);
        }
        break;
      case 4:
        _localctx = new FalseContext(_localctx);
        enterOuterAlt(_localctx, 4);
        {
        setState(433);
        match(FALSE);
        }
        break;
      case 5:
        _localctx = new NullContext(_localctx);
        enterOuterAlt(_localctx, 5);
        {
        setState(434);
        match(NULL);
        }
        break;
      case 6:
        _localctx = new StringContext(_localctx);
        enterOuterAlt(_localctx, 6);
        {
        setState(435);
        match(STRING);
        }
        break;
      case 7:
        _localctx = new RegexContext(_localctx);
        enterOuterAlt(_localctx, 7);
        {
        setState(436);
        match(REGEX);
        }
        break;
      case 8:
        _localctx = new ListinitContext(_localctx);
        enterOuterAlt(_localctx, 8);
        {
        setState(437);
        listinitializer();
        }
        break;
      case 9:
        _localctx = new MapinitContext(_localctx);
        enterOuterAlt(_localctx, 9);
        {
        setState(438);
        mapinitializer();
        }
        break;
      case 10:
        _localctx = new VariableContext(_localctx);
        enterOuterAlt(_localctx, 10);
        {
        setState(439);
        ((VariableContext)_localctx).ID = match(ID);

            CursorToken next = (CursorToken)_input.get(_input.index());
            if (next != null && next.isCursor() && _input.LA(1) == PainlessLexer.DOT) {
                cursorid = (((VariableContext)_localctx).ID!=null?((VariableContext)_localctx).ID.getText():null);
                cursortype = scope.getVariable(cursorid);
            }

        }
        break;
      case 11:
        _localctx = new CalllocalContext(_localctx);
        enterOuterAlt(_localctx, 11);
        {
        setState(441);
        match(ID);
        setState(442);
        arguments();
        }
        break;
      case 12:
        _localctx = new NewobjectContext(_localctx);
        enterOuterAlt(_localctx, 12);
        {
        setState(443);
        match(NEW);
        setState(444);
        type();
        setState(445);
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
    enterRule(_localctx, 52, RULE_postfix);
    try {
      setState(452);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,38,_ctx) ) {
      case 1:
        enterOuterAlt(_localctx, 1);
        {
        setState(449);
        callinvoke();
        }
        break;
      case 2:
        enterOuterAlt(_localctx, 2);
        {
        setState(450);
        fieldaccess();
        }
        break;
      case 3:
        enterOuterAlt(_localctx, 3);
        {
        setState(451);
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
      if ( visitor instanceof PainlessParserVisitor ) return ((PainlessParserVisitor<? extends T>)visitor).visitPostdot(this);
      else return visitor.visitChildren(this);
    }
  }

  public final PostdotContext postdot() throws RecognitionException {
    PostdotContext _localctx = new PostdotContext(_ctx, getState());
    enterRule(_localctx, 54, RULE_postdot);
    try {
      setState(456);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,39,_ctx) ) {
      case 1:
        enterOuterAlt(_localctx, 1);
        {
        setState(454);
        callinvoke();
        }
        break;
      case 2:
        enterOuterAlt(_localctx, 2);
        {
        setState(455);
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
    enterRule(_localctx, 56, RULE_callinvoke);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(458);
      _la = _input.LA(1);
      if ( !(_la==DOT || _la==NSDOT) ) {
      _errHandler.recoverInline(this);
      } else {
        consume();
      }
      setState(459);
      match(DOTID);
      setState(460);
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
    enterRule(_localctx, 58, RULE_fieldaccess);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(462);
      _la = _input.LA(1);
      if ( !(_la==DOT || _la==NSDOT) ) {
      _errHandler.recoverInline(this);
      } else {
        consume();
      }
      setState(463);
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
      setState(465);
      match(LBRACE);
      setState(466);
      expression();
      setState(467);
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
      setState(510);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,46,_ctx) ) {
      case 1:
        _localctx = new NewstandardarrayContext(_localctx);
        enterOuterAlt(_localctx, 1);
        {
        setState(469);
        match(NEW);
        setState(470);
        type();
        setState(475);
        _errHandler.sync(this);
        _alt = 1;
        do {
          switch (_alt) {
          case 1:
            {
            {
            setState(471);
            match(LBRACE);
            setState(472);
            expression();
            setState(473);
            match(RBRACE);
            }
            }
            break;
          default:
            throw new NoViableAltException(this);
          }
          setState(477);
          _errHandler.sync(this);
          _alt = getInterpreter().adaptivePredict(_input,40,_ctx);
        } while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
        setState(486);
        _errHandler.sync(this);
        switch ( getInterpreter().adaptivePredict(_input,42,_ctx) ) {
        case 1:
          {
          setState(479);
          postdot();
          setState(483);
          _errHandler.sync(this);
          _alt = getInterpreter().adaptivePredict(_input,41,_ctx);
          while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
            if ( _alt==1 ) {
              {
              {
              setState(480);
              postfix();
              }
              }
            }
            setState(485);
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
        setState(488);
        match(NEW);
        setState(489);
        type();
        setState(490);
        match(LBRACE);
        setState(491);
        match(RBRACE);
        setState(492);
        match(LBRACK);
        setState(501);
        _la = _input.LA(1);
        if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LBRACE) | (1L << LP) | (1L << NEW) | (1L << BOOLNOT) | (1L << BWNOT) | (1L << ADD) | (1L << SUB) | (1L << INCR) | (1L << DECR))) != 0) || ((((_la - 72)) & ~0x3f) == 0 && ((1L << (_la - 72)) & ((1L << (OCTAL - 72)) | (1L << (HEX - 72)) | (1L << (INTEGER - 72)) | (1L << (DECIMAL - 72)) | (1L << (STRING - 72)) | (1L << (REGEX - 72)) | (1L << (TRUE - 72)) | (1L << (FALSE - 72)) | (1L << (NULL - 72)) | (1L << (ID - 72)))) != 0)) {
          {
          setState(493);
          expression();
          setState(498);
          _errHandler.sync(this);
          _la = _input.LA(1);
          while (_la==COMMA) {
            {
            {
            setState(494);
            match(COMMA);
            setState(495);
            expression();
            }
            }
            setState(500);
            _errHandler.sync(this);
            _la = _input.LA(1);
          }
          }
        }

        setState(503);
        match(RBRACK);
        setState(507);
        _errHandler.sync(this);
        _alt = getInterpreter().adaptivePredict(_input,45,_ctx);
        while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
          if ( _alt==1 ) {
            {
            {
            setState(504);
            postfix();
            }
            }
          }
          setState(509);
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
      setState(525);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,48,_ctx) ) {
      case 1:
        enterOuterAlt(_localctx, 1);
        {
        setState(512);
        match(LBRACE);
        setState(513);
        expression();
        setState(518);
        _errHandler.sync(this);
        _la = _input.LA(1);
        while (_la==COMMA) {
          {
          {
          setState(514);
          match(COMMA);
          setState(515);
          expression();
          }
          }
          setState(520);
          _errHandler.sync(this);
          _la = _input.LA(1);
        }
        setState(521);
        match(RBRACE);
        }
        break;
      case 2:
        enterOuterAlt(_localctx, 2);
        {
        setState(523);
        match(LBRACE);
        setState(524);
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
      setState(541);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,50,_ctx) ) {
      case 1:
        enterOuterAlt(_localctx, 1);
        {
        setState(527);
        match(LBRACE);
        setState(528);
        maptoken();
        setState(533);
        _errHandler.sync(this);
        _la = _input.LA(1);
        while (_la==COMMA) {
          {
          {
          setState(529);
          match(COMMA);
          setState(530);
          maptoken();
          }
          }
          setState(535);
          _errHandler.sync(this);
          _la = _input.LA(1);
        }
        setState(536);
        match(RBRACE);
        }
        break;
      case 2:
        enterOuterAlt(_localctx, 2);
        {
        setState(538);
        match(LBRACE);
        setState(539);
        match(COLON);
        setState(540);
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
      setState(543);
      expression();
      setState(544);
      match(COLON);
      setState(545);
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
      setState(547);
      match(LP);
      setState(556);
      _la = _input.LA(1);
      if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LBRACE) | (1L << LP) | (1L << NEW) | (1L << THIS) | (1L << BOOLNOT) | (1L << BWNOT) | (1L << ADD) | (1L << SUB) | (1L << INCR) | (1L << DECR))) != 0) || ((((_la - 72)) & ~0x3f) == 0 && ((1L << (_la - 72)) & ((1L << (OCTAL - 72)) | (1L << (HEX - 72)) | (1L << (INTEGER - 72)) | (1L << (DECIMAL - 72)) | (1L << (STRING - 72)) | (1L << (REGEX - 72)) | (1L << (TRUE - 72)) | (1L << (FALSE - 72)) | (1L << (NULL - 72)) | (1L << (PRIMITIVE - 72)) | (1L << (DEF - 72)) | (1L << (ID - 72)))) != 0)) {
        {
        setState(548);
        argument();
        setState(553);
        _errHandler.sync(this);
        _la = _input.LA(1);
        while (_la==COMMA) {
          {
          {
          setState(549);
          match(COMMA);
          setState(550);
          argument();
          }
          }
          setState(555);
          _errHandler.sync(this);
          _la = _input.LA(1);
        }
        }
      }

      setState(558);
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
      setState(563);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,53,_ctx) ) {
      case 1:
        enterOuterAlt(_localctx, 1);
        {
        setState(560);
        expression();
        }
        break;
      case 2:
        enterOuterAlt(_localctx, 2);
        {
        setState(561);
        lambda();
        }
        break;
      case 3:
        enterOuterAlt(_localctx, 3);
        {
        setState(562);
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
       pushScope();
      setState(579);
      switch (_input.LA(1)) {
      case PRIMITIVE:
      case DEF:
      case ID:
        {
        setState(566);
        lamtype();
        }
        break;
      case LP:
        {
        setState(567);
        match(LP);
        setState(576);
        _la = _input.LA(1);
        if (((((_la - 81)) & ~0x3f) == 0 && ((1L << (_la - 81)) & ((1L << (PRIMITIVE - 81)) | (1L << (DEF - 81)) | (1L << (ID - 81)))) != 0)) {
          {
          setState(568);
          lamtype();
          setState(573);
          _errHandler.sync(this);
          _la = _input.LA(1);
          while (_la==COMMA) {
            {
            {
            setState(569);
            match(COMMA);
            setState(570);
            lamtype();
            }
            }
            setState(575);
            _errHandler.sync(this);
            _la = _input.LA(1);
          }
          }
        }

        setState(578);
        match(RP);
        }
        break;
      default:
        throw new NoViableAltException(this);
      }
      setState(581);
      match(ARROW);
      setState(584);
      switch (_input.LA(1)) {
      case LBRACK:
        {
        setState(582);
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
        setState(583);
        expression();
        }
        break;
      default:
        throw new NoViableAltException(this);
      }
       popScope();
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
    public DecltypeContext decltype;
    public Token ID;
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
      setState(589);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,58,_ctx) ) {
      case 1:
        {
        setState(588);
        ((LamtypeContext)_localctx).decltype = decltype();
        }
        break;
      }
      setState(591);
      ((LamtypeContext)_localctx).ID = match(ID);
       String typename = (((LamtypeContext)_localctx).decltype!=null?_input.getText(((LamtypeContext)_localctx).decltype.start,((LamtypeContext)_localctx).decltype.stop):null); addVariable((((LamtypeContext)_localctx).ID!=null?((LamtypeContext)_localctx).ID.getText():null), typename == null ? "def" : typename);
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
      setState(605);
      _errHandler.sync(this);
      switch ( getInterpreter().adaptivePredict(_input,59,_ctx) ) {
      case 1:
        _localctx = new ClassfuncrefContext(_localctx);
        enterOuterAlt(_localctx, 1);
        {
        setState(594);
        decltype();
        setState(595);
        match(REF);
        setState(596);
        match(ID);
        }
        break;
      case 2:
        _localctx = new ConstructorfuncrefContext(_localctx);
        enterOuterAlt(_localctx, 2);
        {
        setState(598);
        decltype();
        setState(599);
        match(REF);
        setState(600);
        match(NEW);
        }
        break;
      case 3:
        _localctx = new LocalfuncrefContext(_localctx);
        enterOuterAlt(_localctx, 3);
        {
        setState(602);
        match(THIS);
        setState(603);
        match(REF);
        setState(604);
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
    case 5:
      return rstatement_sempred((RstatementContext)_localctx, predIndex);
    case 17:
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
    "\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3W\u0262\4\2\t\2\4"+
    "\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
    "\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
    "\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
    "\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
    "\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\3\2\7\2T\n\2\f"+
    "\2\16\2W\13\2\3\2\3\2\7\2[\n\2\f\2\16\2^\13\2\3\2\3\2\3\2\3\3\3\3\3\3"+
    "\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\7\4o\n\4\f\4\16\4r\13\4\5\4t\n\4"+
    "\3\4\3\4\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\5\6\u0080\n\6\3\7\3\7\3\7\3\7"+
    "\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\5\7\u008f\n\7\3\7\3\7\3\7\3\7\3\7"+
    "\3\7\3\7\5\7\u0098\n\7\3\7\3\7\3\7\3\7\3\7\3\7\5\7\u00a0\n\7\3\7\3\7\5"+
    "\7\u00a4\n\7\3\7\3\7\5\7\u00a8\n\7\3\7\3\7\3\7\5\7\u00ad\n\7\3\7\3\7\3"+
    "\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7"+
    "\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\6\7\u00cb\n\7\r\7\16\7\u00cc\5\7\u00cf"+
    "\n\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\5\b\u00df"+
    "\n\b\3\b\3\b\3\b\5\b\u00e4\n\b\3\t\3\t\5\t\u00e8\n\t\3\n\3\n\7\n\u00ec"+
    "\n\n\f\n\16\n\u00ef\13\n\3\n\5\n\u00f2\n\n\3\n\3\n\3\13\3\13\3\f\3\f\5"+
    "\f\u00fa\n\f\3\r\3\r\3\16\3\16\3\16\3\16\7\16\u0102\n\16\f\16\16\16\u0105"+
    "\13\16\3\17\3\17\3\17\7\17\u010a\n\17\f\17\16\17\u010d\13\17\3\20\3\20"+
    "\3\20\3\20\3\20\7\20\u0114\n\20\f\20\16\20\u0117\13\20\5\20\u0119\n\20"+
    "\3\21\3\21\3\21\5\21\u011e\n\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\22"+
    "\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23"+
    "\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23"+
    "\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23"+
    "\3\23\3\23\3\23\7\23\u0155\n\23\f\23\16\23\u0158\13\23\3\24\3\24\3\24"+
    "\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\5\24\u0165\n\24\3\25\3\25\3\25"+
    "\3\25\3\25\5\25\u016c\n\25\3\26\3\26\3\26\3\26\3\26\3\26\3\26\5\26\u0175"+
    "\n\26\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\5\27\u0181\n\27"+
    "\3\30\3\30\3\31\3\31\3\31\6\31\u0188\n\31\r\31\16\31\u0189\3\31\3\31\3"+
    "\31\6\31\u018f\n\31\r\31\16\31\u0190\3\31\3\31\3\31\7\31\u0196\n\31\f"+
    "\31\16\31\u0199\13\31\3\31\3\31\7\31\u019d\n\31\f\31\16\31\u01a0\13\31"+
    "\5\31\u01a2\n\31\3\32\3\32\7\32\u01a6\n\32\f\32\16\32\u01a9\13\32\3\32"+
    "\5\32\u01ac\n\32\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33"+
    "\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\5\33\u01c2\n\33\3\34\3\34"+
    "\3\34\5\34\u01c7\n\34\3\35\3\35\5\35\u01cb\n\35\3\36\3\36\3\36\3\36\3"+
    "\37\3\37\3\37\3 \3 \3 \3 \3!\3!\3!\3!\3!\3!\6!\u01de\n!\r!\16!\u01df\3"+
    "!\3!\7!\u01e4\n!\f!\16!\u01e7\13!\5!\u01e9\n!\3!\3!\3!\3!\3!\3!\3!\3!"+
    "\7!\u01f3\n!\f!\16!\u01f6\13!\5!\u01f8\n!\3!\3!\7!\u01fc\n!\f!\16!\u01ff"+
    "\13!\5!\u0201\n!\3\"\3\"\3\"\3\"\7\"\u0207\n\"\f\"\16\"\u020a\13\"\3\""+
    "\3\"\3\"\3\"\5\"\u0210\n\"\3#\3#\3#\3#\7#\u0216\n#\f#\16#\u0219\13#\3"+
    "#\3#\3#\3#\3#\5#\u0220\n#\3$\3$\3$\3$\3%\3%\3%\3%\7%\u022a\n%\f%\16%\u022d"+
    "\13%\5%\u022f\n%\3%\3%\3&\3&\3&\5&\u0236\n&\3\'\3\'\3\'\3\'\3\'\3\'\7"+
    "\'\u023e\n\'\f\'\16\'\u0241\13\'\5\'\u0243\n\'\3\'\5\'\u0246\n\'\3\'\3"+
    "\'\3\'\5\'\u024b\n\'\3\'\3\'\3(\5(\u0250\n(\3(\3(\3(\3)\3)\3)\3)\3)\3"+
    ")\3)\3)\3)\3)\3)\5)\u0260\n)\3)\2\3$*\2\4\6\b\n\f\16\20\22\24\26\30\32"+
    "\34\36 \"$&(*,.\60\62\64\668:<>@BDFHJLNP\2\20\3\3\16\16\3\2 \"\3\2#$\3"+
    "\2:;\3\2%\'\3\2(+\3\2,/\3\2>I\3\2<=\3\2\36\37\3\2ST\3\2JM\3\2\13\f\3\2"+
    "VW\u029c\2U\3\2\2\2\4b\3\2\2\2\6j\3\2\2\2\bw\3\2\2\2\n\177\3\2\2\2\f\u00ce"+
    "\3\2\2\2\16\u00e3\3\2\2\2\20\u00e7\3\2\2\2\22\u00e9\3\2\2\2\24\u00f5\3"+
    "\2\2\2\26\u00f9\3\2\2\2\30\u00fb\3\2\2\2\32\u00fd\3\2\2\2\34\u0106\3\2"+
    "\2\2\36\u0118\3\2\2\2 \u011a\3\2\2\2\"\u0121\3\2\2\2$\u012a\3\2\2\2&\u0164"+
    "\3\2\2\2(\u016b\3\2\2\2*\u0174\3\2\2\2,\u0180\3\2\2\2.\u0182\3\2\2\2\60"+
    "\u01a1\3\2\2\2\62\u01ab\3\2\2\2\64\u01c1\3\2\2\2\66\u01c6\3\2\2\28\u01ca"+
    "\3\2\2\2:\u01cc\3\2\2\2<\u01d0\3\2\2\2>\u01d3\3\2\2\2@\u0200\3\2\2\2B"+
    "\u020f\3\2\2\2D\u021f\3\2\2\2F\u0221\3\2\2\2H\u0225\3\2\2\2J\u0235\3\2"+
    "\2\2L\u0237\3\2\2\2N\u024f\3\2\2\2P\u025f\3\2\2\2RT\5\4\3\2SR\3\2\2\2"+
    "TW\3\2\2\2US\3\2\2\2UV\3\2\2\2VX\3\2\2\2WU\3\2\2\2X\\\b\2\1\2Y[\5\n\6"+
    "\2ZY\3\2\2\2[^\3\2\2\2\\Z\3\2\2\2\\]\3\2\2\2]_\3\2\2\2^\\\3\2\2\2_`\7"+
    "\2\2\3`a\b\2\1\2a\3\3\2\2\2bc\b\3\1\2cd\5\34\17\2de\7U\2\2ef\5\6\4\2f"+
    "g\b\3\1\2gh\5\22\n\2hi\b\3\1\2i\5\3\2\2\2js\7\t\2\2kp\5\b\5\2lm\7\r\2"+
    "\2mo\5\b\5\2nl\3\2\2\2or\3\2\2\2pn\3\2\2\2pq\3\2\2\2qt\3\2\2\2rp\3\2\2"+
    "\2sk\3\2\2\2st\3\2\2\2tu\3\2\2\2uv\7\n\2\2v\7\3\2\2\2wx\5\34\17\2xy\7"+
    "U\2\2yz\b\5\1\2z\t\3\2\2\2{\u0080\5\f\7\2|}\5\16\b\2}~\t\2\2\2~\u0080"+
    "\3\2\2\2\177{\3\2\2\2\177|\3\2\2\2\u0080\13\3\2\2\2\u0081\u0082\7\17\2"+
    "\2\u0082\u0083\7\t\2\2\u0083\u0084\5&\24\2\u0084\u0085\7\n\2\2\u0085\u0086"+
    "\b\7\1\2\u0086\u0087\5\20\t\2\u0087\u008e\b\7\1\2\u0088\u0089\7\21\2\2"+
    "\u0089\u008a\b\7\1\2\u008a\u008b\5\20\t\2\u008b\u008c\b\7\1\2\u008c\u008f"+
    "\3\2\2\2\u008d\u008f\6\7\2\2\u008e\u0088\3\2\2\2\u008e\u008d\3\2\2\2\u008f"+
    "\u00cf\3\2\2\2\u0090\u0091\7\22\2\2\u0091\u0092\b\7\1\2\u0092\u0093\7"+
    "\t\2\2\u0093\u0094\5&\24\2\u0094\u0097\7\n\2\2\u0095\u0098\5\20\t\2\u0096"+
    "\u0098\5\24\13\2\u0097\u0095\3\2\2\2\u0097\u0096\3\2\2\2\u0098\u0099\3"+
    "\2\2\2\u0099\u009a\b\7\1\2\u009a\u00cf\3\2\2\2\u009b\u009c\7\24\2\2\u009c"+
    "\u009d\b\7\1\2\u009d\u009f\7\t\2\2\u009e\u00a0\5\26\f\2\u009f\u009e\3"+
    "\2\2\2\u009f\u00a0\3\2\2\2\u00a0\u00a1\3\2\2\2\u00a1\u00a3\7\16\2\2\u00a2"+
    "\u00a4\5&\24\2\u00a3\u00a2\3\2\2\2\u00a3\u00a4\3\2\2\2\u00a4\u00a5\3\2"+
    "\2\2\u00a5\u00a7\7\16\2\2\u00a6\u00a8\5\30\r\2\u00a7\u00a6\3\2\2\2\u00a7"+
    "\u00a8\3\2\2\2\u00a8\u00a9\3\2\2\2\u00a9\u00ac\7\n\2\2\u00aa\u00ad\5\20"+
    "\t\2\u00ab\u00ad\5\24\13\2\u00ac\u00aa\3\2\2\2\u00ac\u00ab\3\2\2\2\u00ad"+
    "\u00ae\3\2\2\2\u00ae\u00af\b\7\1\2\u00af\u00cf\3\2\2\2\u00b0\u00b1\7\24"+
    "\2\2\u00b1\u00b2\b\7\1\2\u00b2\u00b3\7\t\2\2\u00b3\u00b4\5\34\17\2\u00b4"+
    "\u00b5\7U\2\2\u00b5\u00b6\7\66\2\2\u00b6\u00b7\5&\24\2\u00b7\u00b8\7\n"+
    "\2\2\u00b8\u00b9\5\20\t\2\u00b9\u00ba\b\7\1\2\u00ba\u00cf\3\2\2\2\u00bb"+
    "\u00bc\7\24\2\2\u00bc\u00bd\b\7\1\2\u00bd\u00be\7\t\2\2\u00be\u00bf\7"+
    "U\2\2\u00bf\u00c0\7\20\2\2\u00c0\u00c1\5&\24\2\u00c1\u00c2\7\n\2\2\u00c2"+
    "\u00c3\5\20\t\2\u00c3\u00c4\b\7\1\2\u00c4\u00cf\3\2\2\2\u00c5\u00c6\7"+
    "\31\2\2\u00c6\u00c7\b\7\1\2\u00c7\u00c8\5\22\n\2\u00c8\u00ca\b\7\1\2\u00c9"+
    "\u00cb\5\"\22\2\u00ca\u00c9\3\2\2\2\u00cb\u00cc\3\2\2\2\u00cc\u00ca\3"+
    "\2\2\2\u00cc\u00cd\3\2\2\2\u00cd\u00cf\3\2\2\2\u00ce\u0081\3\2\2\2\u00ce"+
    "\u0090\3\2\2\2\u00ce\u009b\3\2\2\2\u00ce\u00b0\3\2\2\2\u00ce\u00bb\3\2"+
    "\2\2\u00ce\u00c5\3\2\2\2\u00cf\r\3\2\2\2\u00d0\u00d1\7\23\2\2\u00d1\u00d2"+
    "\b\b\1\2\u00d2\u00d3\5\22\n\2\u00d3\u00d4\7\22\2\2\u00d4\u00d5\7\t\2\2"+
    "\u00d5\u00d6\5&\24\2\u00d6\u00d7\7\n\2\2\u00d7\u00d8\b\b\1\2\u00d8\u00e4"+
    "\3\2\2\2\u00d9\u00e4\5\32\16\2\u00da\u00e4\7\25\2\2\u00db\u00e4\7\26\2"+
    "\2\u00dc\u00de\7\27\2\2\u00dd\u00df\5&\24\2\u00de\u00dd\3\2\2\2\u00de"+
    "\u00df\3\2\2\2\u00df\u00e4\3\2\2\2\u00e0\u00e1\7\33\2\2\u00e1\u00e4\5"+
    "&\24\2\u00e2\u00e4\5&\24\2\u00e3\u00d0\3\2\2\2\u00e3\u00d9\3\2\2\2\u00e3"+
    "\u00da\3\2\2\2\u00e3\u00db\3\2\2\2\u00e3\u00dc\3\2\2\2\u00e3\u00e0\3\2"+
    "\2\2\u00e3\u00e2\3\2\2\2\u00e4\17\3\2\2\2\u00e5\u00e8\5\22\n\2\u00e6\u00e8"+
    "\5\n\6\2\u00e7\u00e5\3\2\2\2\u00e7\u00e6\3\2\2\2\u00e8\21\3\2\2\2\u00e9"+
    "\u00ed\7\5\2\2\u00ea\u00ec\5\n\6\2\u00eb\u00ea\3\2\2\2\u00ec\u00ef\3\2"+
    "\2\2\u00ed\u00eb\3\2\2\2\u00ed\u00ee\3\2\2\2\u00ee\u00f1\3\2\2\2\u00ef"+
    "\u00ed\3\2\2\2\u00f0\u00f2\5\16\b\2\u00f1\u00f0\3\2\2\2\u00f1\u00f2\3"+
    "\2\2\2\u00f2\u00f3\3\2\2\2\u00f3\u00f4\7\6\2\2\u00f4\23\3\2\2\2\u00f5"+
    "\u00f6\7\16\2\2\u00f6\25\3\2\2\2\u00f7\u00fa\5\32\16\2\u00f8\u00fa\5&"+
    "\24\2\u00f9\u00f7\3\2\2\2\u00f9\u00f8\3\2\2\2\u00fa\27\3\2\2\2\u00fb\u00fc"+
    "\5&\24\2\u00fc\31\3\2\2\2\u00fd\u00fe\5\34\17\2\u00fe\u0103\5 \21\2\u00ff"+
    "\u0100\7\r\2\2\u0100\u0102\5 \21\2\u0101\u00ff\3\2\2\2\u0102\u0105\3\2"+
    "\2\2\u0103\u0101\3\2\2\2\u0103\u0104\3\2\2\2\u0104\33\3\2\2\2\u0105\u0103"+
    "\3\2\2\2\u0106\u010b\5\36\20\2\u0107\u0108\7\7\2\2\u0108\u010a\7\b\2\2"+
    "\u0109\u0107\3\2\2\2\u010a\u010d\3\2\2\2\u010b\u0109\3\2\2\2\u010b\u010c"+
    "\3\2\2\2\u010c\35\3\2\2\2\u010d\u010b\3\2\2\2\u010e\u0119\7T\2\2\u010f"+
    "\u0119\7S\2\2\u0110\u0115\7U\2\2\u0111\u0112\7\13\2\2\u0112\u0114\7W\2"+
    "\2\u0113\u0111\3\2\2\2\u0114\u0117\3\2\2\2\u0115\u0113\3\2\2\2\u0115\u0116"+
    "\3\2\2\2\u0116\u0119\3\2\2\2\u0117\u0115\3\2\2\2\u0118\u010e\3\2\2\2\u0118"+
    "\u010f\3\2\2\2\u0118\u0110\3\2\2\2\u0119\37\3\2\2\2\u011a\u011d\7U\2\2"+
    "\u011b\u011c\7>\2\2\u011c\u011e\5&\24\2\u011d\u011b\3\2\2\2\u011d\u011e"+
    "\3\2\2\2\u011e\u011f\3\2\2\2\u011f\u0120\b\21\1\2\u0120!\3\2\2\2\u0121"+
    "\u0122\7\32\2\2\u0122\u0123\b\22\1\2\u0123\u0124\7\t\2\2\u0124\u0125\5"+
    "\36\20\2\u0125\u0126\7U\2\2\u0126\u0127\7\n\2\2\u0127\u0128\5\22\n\2\u0128"+
    "\u0129\b\22\1\2\u0129#\3\2\2\2\u012a\u012b\b\23\1\2\u012b\u012c\5(\25"+
    "\2\u012c\u0156\3\2\2\2\u012d\u012e\f\17\2\2\u012e\u012f\t\3\2\2\u012f"+
    "\u0155\5$\23\20\u0130\u0131\f\16\2\2\u0131\u0132\t\4\2\2\u0132\u0155\5"+
    "$\23\17\u0133\u0134\f\r\2\2\u0134\u0135\t\5\2\2\u0135\u0155\5$\23\16\u0136"+
    "\u0137\f\f\2\2\u0137\u0138\t\6\2\2\u0138\u0155\5$\23\r\u0139\u013a\f\13"+
    "\2\2\u013a\u013b\t\7\2\2\u013b\u0155\5$\23\f\u013c\u013d\f\t\2\2\u013d"+
    "\u013e\t\b\2\2\u013e\u0155\5$\23\n\u013f\u0140\f\b\2\2\u0140\u0141\7\60"+
    "\2\2\u0141\u0155\5$\23\t\u0142\u0143\f\7\2\2\u0143\u0144\7\61\2\2\u0144"+
    "\u0155\5$\23\b\u0145\u0146\f\6\2\2\u0146\u0147\7\62\2\2\u0147\u0155\5"+
    "$\23\7\u0148\u0149\f\5\2\2\u0149\u014a\7\63\2\2\u014a\u0155\5$\23\6\u014b"+
    "\u014c\f\4\2\2\u014c\u014d\7\64\2\2\u014d\u0155\5$\23\5\u014e\u014f\f"+
    "\3\2\2\u014f\u0150\7\67\2\2\u0150\u0155\5$\23\3\u0151\u0152\f\n\2\2\u0152"+
    "\u0153\7\35\2\2\u0153\u0155\5\34\17\2\u0154\u012d\3\2\2\2\u0154\u0130"+
    "\3\2\2\2\u0154\u0133\3\2\2\2\u0154\u0136\3\2\2\2\u0154\u0139\3\2\2\2\u0154"+
    "\u013c\3\2\2\2\u0154\u013f\3\2\2\2\u0154\u0142\3\2\2\2\u0154\u0145\3\2"+
    "\2\2\u0154\u0148\3\2\2\2\u0154\u014b\3\2\2\2\u0154\u014e\3\2\2\2\u0154"+
    "\u0151\3\2\2\2\u0155\u0158\3\2\2\2\u0156\u0154\3\2\2\2\u0156\u0157\3\2"+
    "\2\2\u0157%\3\2\2\2\u0158\u0156\3\2\2\2\u0159\u0165\5$\23\2\u015a\u015b"+
    "\5$\23\2\u015b\u015c\7\65\2\2\u015c\u015d\5&\24\2\u015d\u015e\7\66\2\2"+
    "\u015e\u015f\5&\24\2\u015f\u0165\3\2\2\2\u0160\u0161\5$\23\2\u0161\u0162"+
    "\t\t\2\2\u0162\u0163\5&\24\2\u0163\u0165\3\2\2\2\u0164\u0159\3\2\2\2\u0164"+
    "\u015a\3\2\2\2\u0164\u0160\3\2\2\2\u0165\'\3\2\2\2\u0166\u0167\t\n\2\2"+
    "\u0167\u016c\5\62\32\2\u0168\u0169\t\4\2\2\u0169\u016c\5(\25\2\u016a\u016c"+
    "\5*\26\2\u016b\u0166\3\2\2\2\u016b\u0168\3\2\2\2\u016b\u016a\3\2\2\2\u016c"+
    ")\3\2\2\2\u016d\u0175\5\62\32\2\u016e\u016f\5\62\32\2\u016f\u0170\t\n"+
    "\2\2\u0170\u0175\3\2\2\2\u0171\u0172\t\13\2\2\u0172\u0175\5(\25\2\u0173"+
    "\u0175\5,\27\2\u0174\u016d\3\2\2\2\u0174\u016e\3\2\2\2\u0174\u0171\3\2"+
    "\2\2\u0174\u0173\3\2\2\2\u0175+\3\2\2\2\u0176\u0177\7\t\2\2\u0177\u0178"+
    "\5.\30\2\u0178\u0179\7\n\2\2\u0179\u017a\5(\25\2\u017a\u0181\3\2\2\2\u017b"+
    "\u017c\7\t\2\2\u017c\u017d\5\60\31\2\u017d\u017e\7\n\2\2\u017e\u017f\5"+
    "*\26\2\u017f\u0181\3\2\2\2\u0180\u0176\3\2\2\2\u0180\u017b\3\2\2\2\u0181"+
    "-\3\2\2\2\u0182\u0183\t\f\2\2\u0183/\3\2\2\2\u0184\u0187\7T\2\2\u0185"+
    "\u0186\7\7\2\2\u0186\u0188\7\b\2\2\u0187\u0185\3\2\2\2\u0188\u0189\3\2"+
    "\2\2\u0189\u0187\3\2\2\2\u0189\u018a\3\2\2\2\u018a\u01a2\3\2\2\2\u018b"+
    "\u018e\7S\2\2\u018c\u018d\7\7\2\2\u018d\u018f\7\b\2\2\u018e\u018c\3\2"+
    "\2\2\u018f\u0190\3\2\2\2\u0190\u018e\3\2\2\2\u0190\u0191\3\2\2\2\u0191"+
    "\u01a2\3\2\2\2\u0192\u0197\7U\2\2\u0193\u0194\7\13\2\2\u0194\u0196\7W"+
    "\2\2\u0195\u0193\3\2\2\2\u0196\u0199\3\2\2\2\u0197\u0195\3\2\2\2\u0197"+
    "\u0198\3\2\2\2\u0198\u019e\3\2\2\2\u0199\u0197\3\2\2\2\u019a\u019b\7\7"+
    "\2\2\u019b\u019d\7\b\2\2\u019c\u019a\3\2\2\2\u019d\u01a0\3\2\2\2\u019e"+
    "\u019c\3\2\2\2\u019e\u019f\3\2\2\2\u019f\u01a2\3\2\2\2\u01a0\u019e\3\2"+
    "\2\2\u01a1\u0184\3\2\2\2\u01a1\u018b\3\2\2\2\u01a1\u0192\3\2\2\2\u01a2"+
    "\61\3\2\2\2\u01a3\u01a7\5\64\33\2\u01a4\u01a6\5\66\34\2\u01a5\u01a4\3"+
    "\2\2\2\u01a6\u01a9\3\2\2\2\u01a7\u01a5\3\2\2\2\u01a7\u01a8\3\2\2\2\u01a8"+
    "\u01ac\3\2\2\2\u01a9\u01a7\3\2\2\2\u01aa\u01ac\5@!\2\u01ab\u01a3\3\2\2"+
    "\2\u01ab\u01aa\3\2\2\2\u01ac\63\3\2\2\2\u01ad\u01ae\7\t\2\2\u01ae\u01af"+
    "\5&\24\2\u01af\u01b0\7\n\2\2\u01b0\u01c2\3\2\2\2\u01b1\u01c2\t\r\2\2\u01b2"+
    "\u01c2\7P\2\2\u01b3\u01c2\7Q\2\2\u01b4\u01c2\7R\2\2\u01b5\u01c2\7N\2\2"+
    "\u01b6\u01c2\7O\2\2\u01b7\u01c2\5B\"\2\u01b8\u01c2\5D#\2\u01b9\u01ba\7"+
    "U\2\2\u01ba\u01c2\b\33\1\2\u01bb\u01bc\7U\2\2\u01bc\u01c2\5H%\2\u01bd"+
    "\u01be\7\30\2\2\u01be\u01bf\5\36\20\2\u01bf\u01c0\5H%\2\u01c0\u01c2\3"+
    "\2\2\2\u01c1\u01ad\3\2\2\2\u01c1\u01b1\3\2\2\2\u01c1\u01b2\3\2\2\2\u01c1"+
    "\u01b3\3\2\2\2\u01c1\u01b4\3\2\2\2\u01c1\u01b5\3\2\2\2\u01c1\u01b6\3\2"+
    "\2\2\u01c1\u01b7\3\2\2\2\u01c1\u01b8\3\2\2\2\u01c1\u01b9\3\2\2\2\u01c1"+
    "\u01bb\3\2\2\2\u01c1\u01bd\3\2\2\2\u01c2\65\3\2\2\2\u01c3\u01c7\5:\36"+
    "\2\u01c4\u01c7\5<\37\2\u01c5\u01c7\5> \2\u01c6\u01c3\3\2\2\2\u01c6\u01c4"+
    "\3\2\2\2\u01c6\u01c5\3\2\2\2\u01c7\67\3\2\2\2\u01c8\u01cb\5:\36\2\u01c9"+
    "\u01cb\5<\37\2\u01ca\u01c8\3\2\2\2\u01ca\u01c9\3\2\2\2\u01cb9\3\2\2\2"+
    "\u01cc\u01cd\t\16\2\2\u01cd\u01ce\7W\2\2\u01ce\u01cf\5H%\2\u01cf;\3\2"+
    "\2\2\u01d0\u01d1\t\16\2\2\u01d1\u01d2\t\17\2\2\u01d2=\3\2\2\2\u01d3\u01d4"+
    "\7\7\2\2\u01d4\u01d5\5&\24\2\u01d5\u01d6\7\b\2\2\u01d6?\3\2\2\2\u01d7"+
    "\u01d8\7\30\2\2\u01d8\u01dd\5\36\20\2\u01d9\u01da\7\7\2\2\u01da\u01db"+
    "\5&\24\2\u01db\u01dc\7\b\2\2\u01dc\u01de\3\2\2\2\u01dd\u01d9\3\2\2\2\u01de"+
    "\u01df\3\2\2\2\u01df\u01dd\3\2\2\2\u01df\u01e0\3\2\2\2\u01e0\u01e8\3\2"+
    "\2\2\u01e1\u01e5\58\35\2\u01e2\u01e4\5\66\34\2\u01e3\u01e2\3\2\2\2\u01e4"+
    "\u01e7\3\2\2\2\u01e5\u01e3\3\2\2\2\u01e5\u01e6\3\2\2\2\u01e6\u01e9\3\2"+
    "\2\2\u01e7\u01e5\3\2\2\2\u01e8\u01e1\3\2\2\2\u01e8\u01e9\3\2\2\2\u01e9"+
    "\u0201\3\2\2\2\u01ea\u01eb\7\30\2\2\u01eb\u01ec\5\36\20\2\u01ec\u01ed"+
    "\7\7\2\2\u01ed\u01ee\7\b\2\2\u01ee\u01f7\7\5\2\2\u01ef\u01f4\5&\24\2\u01f0"+
    "\u01f1\7\r\2\2\u01f1\u01f3\5&\24\2\u01f2\u01f0\3\2\2\2\u01f3\u01f6\3\2"+
    "\2\2\u01f4\u01f2\3\2\2\2\u01f4\u01f5\3\2\2\2\u01f5\u01f8\3\2\2\2\u01f6"+
    "\u01f4\3\2\2\2\u01f7\u01ef\3\2\2\2\u01f7\u01f8\3\2\2\2\u01f8\u01f9\3\2"+
    "\2\2\u01f9\u01fd\7\6\2\2\u01fa\u01fc\5\66\34\2\u01fb\u01fa\3\2\2\2\u01fc"+
    "\u01ff\3\2\2\2\u01fd\u01fb\3\2\2\2\u01fd\u01fe\3\2\2\2\u01fe\u0201\3\2"+
    "\2\2\u01ff\u01fd\3\2\2\2\u0200\u01d7\3\2\2\2\u0200\u01ea\3\2\2\2\u0201"+
    "A\3\2\2\2\u0202\u0203\7\7\2\2\u0203\u0208\5&\24\2\u0204\u0205\7\r\2\2"+
    "\u0205\u0207\5&\24\2\u0206\u0204\3\2\2\2\u0207\u020a\3\2\2\2\u0208\u0206"+
    "\3\2\2\2\u0208\u0209\3\2\2\2\u0209\u020b\3\2\2\2\u020a\u0208\3\2\2\2\u020b"+
    "\u020c\7\b\2\2\u020c\u0210\3\2\2\2\u020d\u020e\7\7\2\2\u020e\u0210\7\b"+
    "\2\2\u020f\u0202\3\2\2\2\u020f\u020d\3\2\2\2\u0210C\3\2\2\2\u0211\u0212"+
    "\7\7\2\2\u0212\u0217\5F$\2\u0213\u0214\7\r\2\2\u0214\u0216\5F$\2\u0215"+
    "\u0213\3\2\2\2\u0216\u0219\3\2\2\2\u0217\u0215\3\2\2\2\u0217\u0218\3\2"+
    "\2\2\u0218\u021a\3\2\2\2\u0219\u0217\3\2\2\2\u021a\u021b\7\b\2\2\u021b"+
    "\u0220\3\2\2\2\u021c\u021d\7\7\2\2\u021d\u021e\7\66\2\2\u021e\u0220\7"+
    "\b\2\2\u021f\u0211\3\2\2\2\u021f\u021c\3\2\2\2\u0220E\3\2\2\2\u0221\u0222"+
    "\5&\24\2\u0222\u0223\7\66\2\2\u0223\u0224\5&\24\2\u0224G\3\2\2\2\u0225"+
    "\u022e\7\t\2\2\u0226\u022b\5J&\2\u0227\u0228\7\r\2\2\u0228\u022a\5J&\2"+
    "\u0229\u0227\3\2\2\2\u022a\u022d\3\2\2\2\u022b\u0229\3\2\2\2\u022b\u022c"+
    "\3\2\2\2\u022c\u022f\3\2\2\2\u022d\u022b\3\2\2\2\u022e\u0226\3\2\2\2\u022e"+
    "\u022f\3\2\2\2\u022f\u0230\3\2\2\2\u0230\u0231\7\n\2\2\u0231I\3\2\2\2"+
    "\u0232\u0236\5&\24\2\u0233\u0236\5L\'\2\u0234\u0236\5P)\2\u0235\u0232"+
    "\3\2\2\2\u0235\u0233\3\2\2\2\u0235\u0234\3\2\2\2\u0236K\3\2\2\2\u0237"+
    "\u0245\b\'\1\2\u0238\u0246\5N(\2\u0239\u0242\7\t\2\2\u023a\u023f\5N(\2"+
    "\u023b\u023c\7\r\2\2\u023c\u023e\5N(\2\u023d\u023b\3\2\2\2\u023e\u0241"+
    "\3\2\2\2\u023f\u023d\3\2\2\2\u023f\u0240\3\2\2\2\u0240\u0243\3\2\2\2\u0241"+
    "\u023f\3\2\2\2\u0242\u023a\3\2\2\2\u0242\u0243\3\2\2\2\u0243\u0244\3\2"+
    "\2\2\u0244\u0246\7\n\2\2\u0245\u0238\3\2\2\2\u0245\u0239\3\2\2\2\u0246"+
    "\u0247\3\2\2\2\u0247\u024a\79\2\2\u0248\u024b\5\22\n\2\u0249\u024b\5&"+
    "\24\2\u024a\u0248\3\2\2\2\u024a\u0249\3\2\2\2\u024b\u024c\3\2\2\2\u024c"+
    "\u024d\b\'\1\2\u024dM\3\2\2\2\u024e\u0250\5\34\17\2\u024f\u024e\3\2\2"+
    "\2\u024f\u0250\3\2\2\2\u0250\u0251\3\2\2\2\u0251\u0252\7U\2\2\u0252\u0253"+
    "\b(\1\2\u0253O\3\2\2\2\u0254\u0255\5\34\17\2\u0255\u0256\78\2\2\u0256"+
    "\u0257\7U\2\2\u0257\u0260\3\2\2\2\u0258\u0259\5\34\17\2\u0259\u025a\7"+
    "8\2\2\u025a\u025b\7\30\2\2\u025b\u0260\3\2\2\2\u025c\u025d\7\34\2\2\u025d"+
    "\u025e\78\2\2\u025e\u0260\7U\2\2\u025f\u0254\3\2\2\2\u025f\u0258\3\2\2"+
    "\2\u025f\u025c\3\2\2\2\u0260Q\3\2\2\2>U\\ps\177\u008e\u0097\u009f\u00a3"+
    "\u00a7\u00ac\u00cc\u00ce\u00de\u00e3\u00e7\u00ed\u00f1\u00f9\u0103\u010b"+
    "\u0115\u0118\u011d\u0154\u0156\u0164\u016b\u0174\u0180\u0189\u0190\u0197"+
    "\u019e\u01a1\u01a7\u01ab\u01c1\u01c6\u01ca\u01df\u01e5\u01e8\u01f4\u01f7"+
    "\u01fd\u0200\u0208\u020f\u0217\u021f\u022b\u022e\u0235\u023f\u0242\u0245"+
    "\u024a\u024f\u025f";
  public static final ATN _ATN =
    new ATNDeserializer().deserialize(_serializedATN.toCharArray());
  static {
    _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
    for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
      _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
    }
  }
}
