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

package org.elasticsearch.painless.node;

import org.elasticsearch.painless.Location;
import org.elasticsearch.painless.Scope;
import org.elasticsearch.painless.ir.BlockNode;
import org.elasticsearch.painless.ir.CallNode;
import org.elasticsearch.painless.ir.CallSubNode;
import org.elasticsearch.painless.ir.ClassNode;
import org.elasticsearch.painless.ir.ConstantNode;
import org.elasticsearch.painless.ir.FieldNode;
import org.elasticsearch.painless.ir.StatementExpressionNode;
import org.elasticsearch.painless.ir.StaticNode;
import org.elasticsearch.painless.ir.TypeNode;
import org.elasticsearch.painless.ir.UnboundFieldLoadNode;
import org.elasticsearch.painless.ir.UnboundFieldStoreNode;
import org.elasticsearch.painless.lookup.PainlessMethod;
import org.elasticsearch.painless.symbol.ScriptRoot;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Represents a regex constant. All regexes are constants.
 */
public final class ERegex extends AExpression {

    private final String pattern;
    private final int flags;
    private String name;

    public ERegex(Location location, String pattern, String flagsString) {
        super(location);

        this.pattern = pattern;

        int flags = 0;

        for (int c = 0; c < flagsString.length(); c++) {
            flags |= flagForChar(flagsString.charAt(c));
        }

        this.flags = flags;
    }

    @Override
    void analyze(ScriptRoot scriptRoot, Scope scope) {
        if (scriptRoot.getCompilerSettings().areRegexesEnabled() == false) {
            throw createError(new IllegalStateException("Regexes are disabled. Set [script.painless.regex.enabled] to [true] "
                    + "in elasticsearch.yaml to allow them. Be careful though, regexes break out of Painless's protection against deep "
                    + "recursion and long loops."));
        }

        if (!read) {
            throw createError(new IllegalArgumentException("Regex constant may only be read [" + pattern + "]."));
        }

        try {
            Pattern.compile(pattern, flags);
        } catch (PatternSyntaxException e) {
            throw new Location(location.getSourceName(), location.getOffset() + 1 + e.getIndex()).createError(
                    new IllegalArgumentException("Error compiling regex: " + e.getDescription()));
        }

        name = scriptRoot.getNextSyntheticName("regex");
        actual = Pattern.class;
    }

    @Override
    UnboundFieldLoadNode write(ClassNode classNode) {
        classNode.addFieldNode(new FieldNode()
                .setTypeNode(new TypeNode()
                        .setLocation(location)
                        .setType(Pattern.class)
                )
                .setLocation(location)
                .setModifiers(Modifier.FINAL | Modifier.STATIC | Modifier.PRIVATE)
                .setName(name)
        );

        try {
            BlockNode blockNode = classNode.getClinitNode().getBlockNode();
            blockNode.addStatementNode(new StatementExpressionNode()
                    .setExpressionNode(new UnboundFieldStoreNode()
                            .setChildNode(new CallNode()
                                    .setTypeNode(new TypeNode()
                                            .setLocation(location)
                                            .setType(Pattern.class)
                                    )
                                    .setPrefixNode(new StaticNode()
                                            .setTypeNode(new TypeNode()
                                                    .setLocation(location)
                                                    .setType(Pattern.class)
                                            )
                                            .setLocation(location)
                                    )
                                    .setChildNode(new CallSubNode()
                                            .setTypeNode(new TypeNode()
                                                    .setLocation(location)
                                                    .setType(Pattern.class)
                                            )
                                            .addArgumentNode(new ConstantNode()
                                                    .setTypeNode(new TypeNode()
                                                            .setLocation(location)
                                                            .setType(String.class)
                                                    )
                                                    .setLocation(location)
                                                    .setConstant(pattern)
                                            )
                                            .addArgumentNode(new ConstantNode()
                                                    .setTypeNode(new TypeNode()
                                                            .setLocation(location)
                                                            .setType(int.class)
                                                    )
                                                    .setLocation(location)
                                                    .setConstant(flags)
                                            )
                                            .setLocation(location)
                                            .setMethod(new PainlessMethod(
                                                            Pattern.class.getMethod("compile", String.class, int.class),
                                                            Pattern.class,
                                                            Pattern.class,
                                                            Arrays.asList(String.class, int.class),
                                                            null,
                                                            null,
                                                            null
                                                    )
                                            )
                                            .setBox(Pattern.class)
                                    )
                                    .setLocation(location)
                            )
                            .setTypeNode(new TypeNode()
                                    .setLocation(location)
                                    .setType(Pattern.class)
                            )
                            .setLocation(location)
                            .setName(name)
                            .setStatic(true)
                    )
                    .setLocation(location)
                    .setNoop(true)
                    .setMethodEscape(false)
            );
        } catch (Exception exception) {
            throw createError(new IllegalStateException("could not generate regex constant [" + pattern + "/" + flags +"] in clinit"));
        }

        return new UnboundFieldLoadNode()
                .setTypeNode(new TypeNode()
                        .setLocation(location)
                        .setType(Pattern.class)
                )
                .setLocation(location)
                .setName(name)
                .setStatic(true);
    }

    private int flagForChar(char c) {
        switch (c) {
            case 'c': return Pattern.CANON_EQ;
            case 'i': return Pattern.CASE_INSENSITIVE;
            case 'l': return Pattern.LITERAL;
            case 'm': return Pattern.MULTILINE;
            case 's': return Pattern.DOTALL;
            case 'U': return Pattern.UNICODE_CHARACTER_CLASS;
            case 'u': return Pattern.UNICODE_CASE;
            case 'x': return Pattern.COMMENTS;
            default:
                throw new IllegalArgumentException("Unknown flag [" + c + "]");
        }
    }

    @Override
    public String toString() {
        StringBuilder f = new StringBuilder();
        if ((flags & Pattern.CANON_EQ) != 0)                f.append('c');
        if ((flags & Pattern.CASE_INSENSITIVE) != 0)        f.append('i');
        if ((flags & Pattern.LITERAL) != 0)                 f.append('l');
        if ((flags & Pattern.MULTILINE) != 0)               f.append('m');
        if ((flags & Pattern.DOTALL) != 0)                  f.append('s');
        if ((flags & Pattern.UNICODE_CHARACTER_CLASS) != 0) f.append('U');
        if ((flags & Pattern.UNICODE_CASE) != 0)            f.append('u');
        if ((flags & Pattern.COMMENTS) != 0)                f.append('x');

        String p = "/" + pattern + "/";
        if (f.length() == 0) {
            return singleLineToString(p);
        }
        return singleLineToString(p, f);
    }
}
