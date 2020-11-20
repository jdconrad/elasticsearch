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

parser grammar PainlessParser;

options { tokenVocab=PainlessLexer; }

@header {
import java.util.HashMap;
import java.util.Map;
}

@members {
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
}

source
    : function* { pushScope(); } statement* EOF { popScope(); }
    ;

function
    : { pushScope(); } decltype ID parameters { pushScope(); } block { popScope(); popScope(); }
    ;

parameters
    : LP ( parameter ( COMMA parameter )* )? RP
    ;

parameter
    : decltype ID { addVariable($ID.text, $decltype.text); }
    ;

statement
    : rstatement
    | dstatement ( SEMICOLON | EOF )
    ;

// Note we use a predicate on the if/else case here to prevent the
// "dangling-else" ambiguity by forcing the 'else' token to be consumed
// as soon as one is found.  See (https://en.wikipedia.org/wiki/Dangling_else).
rstatement
    : IF LP expression RP { pushScope(); } trailer { popScope(); }
        ( ELSE { pushScope(); } trailer { popScope(); } | { _input.LA(1) != ELSE }? )                                           # if
    | WHILE { pushScope(); } LP expression RP ( trailer | empty ) { popScope(); }                                               # while
    | FOR { pushScope(); } LP initializer? SEMICOLON expression? SEMICOLON afterthought? RP ( trailer | empty ) { popScope(); } # for
    | FOR { pushScope(); } LP decltype ID COLON expression RP trailer { popScope(); }                                           # each
    | FOR { pushScope(); } LP ID IN expression RP trailer { popScope(); }                                                       # ineach
    | TRY { pushScope(); } block { popScope(); } trap+                                                                          # try
    ;

dstatement
    : DO { pushScope(); } block WHILE LP expression RP { popScope(); } # do
    | declaration                                                      # decl
    | CONTINUE                                                         # continue
    | BREAK                                                            # break
    | RETURN expression?                                               # return
    | THROW expression                                                 # throw
    | expression                                                       # expr
    ;

trailer
    : block
    | statement
    ;

block
    : LBRACK statement* dstatement? RBRACK
    ;

empty
    : SEMICOLON
    ;

initializer
    : declaration
    | expression
    ;

afterthought
    : expression
    ;

declaration
    : decltype declvar[$decltype.text] (COMMA declvar[$decltype.text])*
    ;

decltype
    : type (LBRACE RBRACE)*
    ;

type
    : DEF
    | PRIMITIVE
    | ID (DOT DOTID)*
    ;

declvar[String typename]
    : ID ( ASSIGN expression )? { addVariable($ID.text, $typename); }
    ;

trap
    : CATCH { pushScope(); } LP type ID RP block { popScope(); }
    ;

noncondexpression
    :               unary                                                       # single
    |               noncondexpression ( MUL | DIV | REM ) noncondexpression     # binary
    |               noncondexpression ( ADD | SUB ) noncondexpression           # binary
    |               noncondexpression ( FIND | MATCH ) noncondexpression        # binary
    |               noncondexpression ( LSH | RSH | USH ) noncondexpression     # binary
    |               noncondexpression ( LT | LTE | GT | GTE ) noncondexpression # comp
    |               noncondexpression INSTANCEOF decltype                       # instanceof
    |               noncondexpression ( EQ | EQR | NE | NER ) noncondexpression # comp
    |               noncondexpression BWAND noncondexpression                   # binary
    |               noncondexpression XOR noncondexpression                     # binary
    |               noncondexpression BWOR noncondexpression                    # binary
    |               noncondexpression BOOLAND noncondexpression                 # bool
    |               noncondexpression BOOLOR noncondexpression                  # bool
    | <assoc=right> noncondexpression ELVIS noncondexpression                   # elvis
    ;

expression
    :               noncondexpression                                            # nonconditional
    | <assoc=right> noncondexpression COND expression COLON expression           # conditional
    | <assoc=right> noncondexpression ( ASSIGN | AADD | ASUB | AMUL |
                                        ADIV   | AREM | AAND | AXOR |
                                        AOR    | ALSH | ARSH | AUSH ) expression # assignment
    ;

unary
    : ( INCR | DECR ) chain # pre
    | ( ADD | SUB ) unary   # addsub
    | unarynotaddsub        # notaddsub
    ;

unarynotaddsub
    : chain                     # read
    | chain (INCR | DECR )      # post
    | ( BOOLNOT | BWNOT ) unary # not
    | castexpression            # cast
    ;

castexpression
    : LP primordefcasttype RP unary    # primordefcast
    | LP refcasttype RP unarynotaddsub # refcast
    ;

primordefcasttype
    : DEF
    | PRIMITIVE
    ;

refcasttype
    : DEF (LBRACE RBRACE)+
    | PRIMITIVE (LBRACE RBRACE)+
    | ID (DOT DOTID)* (LBRACE RBRACE)*
    ;

chain
    : primary postfix* # dynamic
    | arrayinitializer # newarray
    ;

primary
    : LP expression RP                    # precedence
    | ( OCTAL | HEX | INTEGER | DECIMAL ) # numeric
    | TRUE                                # true
    | FALSE                               # false
    | NULL                                # null
    | STRING                              # string
    | REGEX                               # regex
    | listinitializer                     # listinit
    | mapinitializer                      # mapinit
    | ID
    {
    CursorToken next = (CursorToken)_input.LA(1);
    if (next.isCursor() && _input.LA(1) == PainlessLexer.DOT) {
        cursorid = $ID.text;
        cursortype = scope.getVariable(cursorid);
    }
    }                                     # variable
    | ID arguments                        # calllocal
    | NEW type arguments                  # newobject
    ;

postfix
    : callinvoke
    | fieldaccess
    | braceaccess
    ;

postdot
    : callinvoke
    | fieldaccess
    ;

callinvoke
    : ( DOT | NSDOT ) DOTID arguments
    ;

fieldaccess
    : ( DOT | NSDOT ) ( DOTID | DOTINTEGER )
    ;

braceaccess
    : LBRACE expression RBRACE
    ;

arrayinitializer
    : NEW type ( LBRACE expression RBRACE )+ ( postdot postfix* )?                        # newstandardarray
    | NEW type LBRACE RBRACE LBRACK ( expression ( COMMA expression )* )? RBRACK postfix* # newinitializedarray
    ;

listinitializer
    : LBRACE expression ( COMMA expression)* RBRACE
    | LBRACE RBRACE
    ;

mapinitializer
    : LBRACE maptoken ( COMMA maptoken )* RBRACE
    | LBRACE COLON RBRACE
    ;

maptoken
    : expression COLON expression
    ;

arguments
    : ( LP ( argument ( COMMA argument )* )? RP )
    ;

argument
    : expression
    | lambda
    | funcref
    ;

lambda
    : { pushScope(); } ( lamtype | LP ( lamtype ( COMMA lamtype )* )? RP ) ARROW ( block | expression ) { popScope(); }
    ;

lamtype
    : decltype? ID { String typename = $decltype.text; addVariable($ID.text, typename == null ? "def" : typename); }
    ;

funcref
    : decltype REF ID  # classfuncref
    | decltype REF NEW # constructorfuncref
    | THIS REF ID      # localfuncref
    ;
