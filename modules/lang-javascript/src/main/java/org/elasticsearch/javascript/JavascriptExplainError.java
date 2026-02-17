/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.javascript;

import org.elasticsearch.javascript.api.Debug;
import org.elasticsearch.javascript.lookup.JavascriptClass;
import org.elasticsearch.javascript.lookup.JavascriptLookup;
import org.elasticsearch.javascript.lookup.JavascriptLookupUtility;
import org.elasticsearch.script.ScriptException;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static java.util.Collections.singletonList;

/**
 * Thrown by {@link Debug#explain(Object)} to explain an object. Subclass of {@linkplain Error} so it cannot be caught by javascript
 * scripts.
 */
public class JavascriptExplainError extends Error {
    private final Object objectToExplain;

    public JavascriptExplainError(Object objectToExplain) {
        this.objectToExplain = objectToExplain;
    }

    Object getObjectToExplain() {
        return objectToExplain;
    }

    /**
     * Headers to be added to the {@link ScriptException} for structured rendering.
     */
    public Map<String, List<String>> getHeaders(JavascriptLookup javascriptLookup) {
        Map<String, List<String>> headers = new TreeMap<>();
        String toString = "null";
        String javaClassName = null;
        String javascriptClassName = null;
        if (objectToExplain != null) {
            toString = objectToExplain.toString();
            javaClassName = objectToExplain.getClass().getName();
            JavascriptClass struct = javascriptLookup.lookupJavascriptClass(objectToExplain.getClass());
            if (struct != null) {
                javascriptClassName = JavascriptLookupUtility.typeToCanonicalTypeName(objectToExplain.getClass());
            }
        }

        headers.put("es.to_string", singletonList(toString));
        if (javascriptClassName != null) {
            headers.put("es.javascript_class", singletonList(javascriptClassName));
        }
        if (javaClassName != null) {
            headers.put("es.java_class", singletonList(javaClassName));
        }
        return headers;
    }
}
