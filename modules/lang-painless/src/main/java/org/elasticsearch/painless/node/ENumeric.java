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
import org.elasticsearch.painless.builder.SymbolTable;

import java.util.Map;
import java.util.Objects;

/**
 * Represents a non-decimal numeric constant.
 */
public final class ENumeric extends AExpression {

    private final String value;
    private int radix;

    public ENumeric(Location location, String value, int radix) {
        super(location);

        this.value = Objects.requireNonNull(value);
        this.radix = radix;
    }

    @Override
    void storeSettings(CompilerSettings settings) {
        // do nothing
    }

    public static void enter(ANode node, SymbolTable table, Map<String, Object> data) {
        ENumeric numeric = (ENumeric)node;

        if (!numeric.read) {
            throw numeric.createError(new IllegalArgumentException("must read from constant [" + numeric.value + "]"));
        }

        if (numeric.value.endsWith("d") || numeric.value.endsWith("D")) {
            if (numeric.radix != 10) {
                throw numeric.createError(new IllegalStateException("illegal tree structure"));
            }

            try {
                numeric.constant = Double.parseDouble(numeric.value.substring(0, numeric.value.length() - 1));
            } catch (NumberFormatException exception) {
                throw numeric.createError(new IllegalArgumentException("invalid double constant [" + numeric.value + "]"));
            }
        } else if (numeric.value.endsWith("f") || numeric.value.endsWith("F")) {
            if (numeric.radix != 10) {
                throw numeric.createError(new IllegalStateException("illegal tree structure"));
            }

            try {
                numeric.constant = Float.parseFloat(numeric.value.substring(0, numeric.value.length() - 1));
            } catch (NumberFormatException exception) {
                throw numeric.createError(new IllegalArgumentException("invalid float constant [" + numeric.value + "]."));
            }
        } else if (numeric.value.endsWith("l") || numeric.value.endsWith("L")) {
            try {
                numeric.constant = Long.parseLong(numeric.value.substring(0, numeric.value.length() - 1), numeric.radix);
            } catch (NumberFormatException exception) {
                throw numeric.createError(new IllegalArgumentException("invalid long constant [" + numeric.value + "]."));
            }
        } else {
            try {
                Class<?> sort = numeric.expected == null ? int.class : numeric.expected;
                int integer = Integer.parseInt(numeric.value, numeric.radix);

                if (sort == byte.class && integer >= Byte.MIN_VALUE && integer <= Byte.MAX_VALUE) {
                    numeric.constant = (byte)integer;
                } else if (sort == char.class && integer >= Character.MIN_VALUE && integer <= Character.MAX_VALUE) {
                    numeric.constant = (char)integer;
                } else if (sort == short.class && integer >= Short.MIN_VALUE && integer <= Short.MAX_VALUE) {
                    numeric.constant = (short)integer;
                } else {
                    numeric.constant = integer;
                }
            } catch (NumberFormatException exception) {
                throw numeric.createError(new IllegalArgumentException("invalid int constant [" + numeric.value + "]."));
            }
        }

        numeric.replace(new EConstant(numeric.location, numeric.constant));
    }

    @Override
    void write(MethodWriter writer, Globals globals) {
        throw createError(new IllegalStateException("illegal tree structure"));
    }

    @Override
    public String toString() {
        if (radix != 10) {
            return singleLineToString(value, radix);
        }
        return singleLineToString(value);
    }
}
