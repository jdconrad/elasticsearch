/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.javascript;

import java.util.ArrayList;

/**
 * JavaScript-style array: {@code new Array(n)} creates a list of length {@code n} with null elements,
 * so {@code .length} and index access match JS semantics. Whitelisted as "Array" with constructor (int) only.
 */
public final class JsArray extends ArrayList<Object> {

    /**
     * Create an array of the given length with null elements (JavaScript {@code new Array(length)} semantics).
     */
    public JsArray(int length) {
        super(length);
        for (int i = 0; i < length; i++) {
            add(null);
        }
    }

    /**
     * Length of the array (for script {@code x.length}); same as {@link #size()}.
     */
    public int length() {
        return size();
    }
}
