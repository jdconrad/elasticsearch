/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.javascript.spi;

import org.elasticsearch.script.ScriptContext;

import java.util.Map;

/**
 * Generic "test" context used by the javascript execute REST API
 * for testing javascript scripts.
 */
public abstract class JavascriptTestScript {
    private final Map<String, Object> params;

    public JavascriptTestScript(Map<String, Object> params) {
        this.params = params;
    }

    /** Return the parameters for this script. */
    public Map<String, Object> getParams() {
        return params;
    }

    public abstract Object execute();

    public interface Factory {
        JavascriptTestScript newInstance(Map<String, Object> params);
    }

    public static final String[] PARAMETERS = {};
    public static final ScriptContext<Factory> CONTEXT = new ScriptContext<>("javascript_test", Factory.class);
}
