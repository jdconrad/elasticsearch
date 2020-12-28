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

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.LexerNoViableAltException;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;
import org.elasticsearch.painless.Location;
import org.elasticsearch.painless.lookup.PainlessLookup;

/**
 * A lexer that is customized for painless. It:
 * <ul>
 * <li>Overrides the default error behavior to fail on the first error.
 * <li>Stores the last token in case we need to do lookbehind for semicolon insertion and regex vs division detection.
 * <li>Implements the regex vs division detection.
 * <li>Insert semicolons where they'd improve the language's readability. Rather than hack this into the parser and create a ton of
 * ambiguity we hack them here where we can use heuristics to do it quickly.
 * <li>Enhances the error message when a string contains invalid escape sequences to include a list of valid escape sequences.
 * </ul>
 */
final class EnhancedPainlessLexer extends PainlessLexer {
    private Token current = null;
    private final PainlessLookup painlessLookup;

    EnhancedPainlessLexer(CharStream charStream, PainlessLookup painlessLookup) {
        super(charStream);
        this.painlessLookup = painlessLookup;
    }

    @Override
    public Token nextToken() {
        current = super.nextToken();
        return current;
    }

    @Override
    public void recover(final LexerNoViableAltException lnvae) {
        if (this._mode != PainlessLexer.DEFAULT_MODE) {
            this._mode = DEFAULT_MODE;
        } else {
            throw new IllegalStateException("unexpected token [" + lnvae.getOffendingToken().getText() + "]", lnvae);
        }
    }

    @Override
    protected boolean isSlashRegex() {
        Token lastToken = current;
        if (lastToken == null) {
            return true;
        }
        switch (lastToken.getType()) {
        case PainlessLexer.RBRACE:
        case PainlessLexer.RP:
        case PainlessLexer.OCTAL:
        case PainlessLexer.HEX:
        case PainlessLexer.INTEGER:
        case PainlessLexer.DECIMAL:
        case PainlessLexer.ID:
        case PainlessLexer.DOTINTEGER:
        case PainlessLexer.DOTID:
            return false;
        default:
            return true;
        }
    }

    @Override
    protected boolean isType(String text) {
        return painlessLookup.isValidCanonicalClassName(text);
    }
}
