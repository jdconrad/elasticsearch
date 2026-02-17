/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.javascript.api;

import org.elasticsearch.javascript.JavascriptExplainError;

/**
 * Utility methods for debugging javascript scripts that are accessible to javascript scripts.
 */
public class Debug {
    private Debug() {}

    /**
     * Throw an {@link Error} that "explains" an object.
     */
    public static void explain(Object objectToExplain) throws JavascriptExplainError {
        throw new JavascriptExplainError(objectToExplain);
    }
}
