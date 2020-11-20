/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenSource;

public class CursorToken implements Token {

    protected Token token;
    protected boolean isCursor;

    CursorToken(Token token, boolean isCursor) {
        this.token = token;
        this.isCursor = isCursor;
    }

    public boolean isCursor() {
        return isCursor;
    }

    @Override
    public String getText() {
        return token.getText();
    }

    @Override
    public int getType() {
        return token.getType();
    }

    @Override
    public int getLine() {
        return token.getLine();
    }

    @Override
    public int getCharPositionInLine() {
        return token.getCharPositionInLine();
    }

    @Override
    public int getChannel() {
        return token.getChannel();
    }

    @Override
    public int getTokenIndex() {
        return token.getTokenIndex();
    }

    @Override
    public int getStartIndex() {
        return token.getStartIndex();
    }

    @Override
    public int getStopIndex() {
        return token.getStopIndex();
    }

    @Override
    public TokenSource getTokenSource() {
        return token.getTokenSource();
    }

    @Override
    public CharStream getInputStream() {
        return token.getInputStream();
    }
}
