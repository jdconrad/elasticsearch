/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

parser grammar JavascriptParser;

options { tokenVocab=JavascriptLexer; }

@members {
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
}

source
    : function* statement* EOF
    ;

function
    : decltype ID parameters block
    ;

parameters
    : LP ( decltype ID ( COMMA decltype ID )* )? RP
    ;

statement
    : rstatement
    | dstatement ( SEMICOLON | EOF )
    ;

// Note we use a predicate on the if/else case here to prevent the
// "dangling-else" ambiguity by forcing the 'else' token to be consumed
// as soon as one is found.  See (https://en.wikipedia.org/wiki/Dangling_else).
rstatement
    : IF LP expression RP trailer ( ELSE trailer | { _input.LA(1) != ELSE }? )                 # if
    | WHILE LP expression RP ( trailer | empty )                                               # while
    | FOR LP initializer? SEMICOLON expression? SEMICOLON afterthought? RP ( trailer | empty ) # for
    | FOR LP decltype ID COLON expression RP trailer                                           # each
    | FOR LP ID IN expression RP trailer                                                       # ineach
    | TRY block trap+                                                                          # try
    ;

dstatement
    : DO block WHILE LP expression RP # do
    | declaration                     # decl
    | CONTINUE                        # continue
    | BREAK                           # break
    | RETURN expression?              # return
    | THROW expression                # throw
    | expression                      # expr
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
    : decltype declvar (COMMA declvar)*
    ;

decltype
    : type (LBRACE RBRACE)*
    ;

type
    : DEF
    | PRIMITIVE
    | ID (DOT DOTID)*
    ;

declvar
    : ID ( ASSIGN expression )?
    ;

trap
    : CATCH LP type ID RP block
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
    : {isLambdaAhead()}? lambda                                                  # lambdaval
    | {isFunctionRefAhead()}? funcref                                            # funcrefval
    |               noncondexpression                                            # nonconditional
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
    | ID                                  # variable
    | (ID | DOLLAR) arguments             # calllocal
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
    ;

lambda
    : ( lamtype | LP ( lamtype ( COMMA lamtype )* )? RP ) ARROW ( block | expression )
    ;

lamtype
    : decltype? ID
    ;

funcref
    : decltype REF ID  # classfuncref
    | decltype REF NEW # constructorfuncref
    | THIS REF ID      # localfuncref
    ;
