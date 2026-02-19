/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.javascript.spi.annotation;

import org.elasticsearch.javascript.spi.annotation.AliasAnnotation.AliasType;

import java.util.Map;

/**
 * Parser for the <pre>@alias[class="Inner"]</pre> and <pre>@alias[method="foo"]</pre> annotations.
 * See {@link AliasAnnotation} for details.
 */
public class AliasAnnotationParser implements WhitelistAnnotationParser {
    public static final AliasAnnotationParser INSTANCE = new AliasAnnotationParser();

    private AliasAnnotationParser() {}

    @Override
    public Object parse(Map<String, String> arguments) {
        if (arguments.size() != 1) {
            throw new IllegalArgumentException("[@alias] requires one alias");
        }
        AliasAnnotation annotation = null;
        for (Map.Entry<String, String> entry : arguments.entrySet()) {
            AliasType aliasType;
            if ("class".equals(entry.getKey())) {
                aliasType = AliasType.CLASS;
            } else if ("method".equals(entry.getKey())) {
                aliasType = AliasType.METHOD;
            } else {
                throw new IllegalArgumentException("[@alias] only supports class or method aliases");
            }
            String alias = entry.getValue();
            if (alias == null || alias.isBlank()) {
                throw new IllegalArgumentException("[@alias] must be non-empty");
            }
            annotation = new AliasAnnotation(aliasType, alias);
        }
        return annotation;
    }
}
