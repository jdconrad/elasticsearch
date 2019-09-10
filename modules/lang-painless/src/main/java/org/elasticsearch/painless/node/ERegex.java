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

import org.elasticsearch.painless.CompilerSettings;
import org.elasticsearch.painless.Globals;
import org.elasticsearch.painless.Locals;
import org.elasticsearch.painless.Location;
import org.elasticsearch.painless.MethodWriter;
import org.elasticsearch.painless.WriterConstants;
import org.elasticsearch.painless.builder.SymbolTable;
import org.objectweb.asm.Opcodes;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static org.elasticsearch.painless.WriterConstants.CLASS_TYPE;

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
    void analyze(SymbolTable table) {
        if (false == table.settings().areRegexesEnabled()) {
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

        name = "regexAt$" + location.getOffset();
        actual = Pattern.class;
    }

    @Override
    void write(MethodWriter writer, Globals globals) {
        globals.visitor.visitField(
                Opcodes.ACC_FINAL | Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC,
                name,
                MethodWriter.getType(Pattern.class).getDescriptor(),
                null,
                null).visitEnd();

        globals.clinit.push(pattern);
        globals.clinit.push(flags);
        globals.clinit.invokeStatic(org.objectweb.asm.Type.getType(Pattern.class), WriterConstants.PATTERN_COMPILE);
        globals.clinit.putStatic(CLASS_TYPE, name, org.objectweb.asm.Type.getType(Pattern.class));

        writer.writeDebugInfo(location);

        writer.getStatic(WriterConstants.CLASS_TYPE, name, org.objectweb.asm.Type.getType(Pattern.class));
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
