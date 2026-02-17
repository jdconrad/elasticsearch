/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

module org.elasticsearch.javascript {
    requires org.elasticsearch.base;
    requires org.elasticsearch.geo;
    requires org.elasticsearch.javascript.spi;
    requires org.elasticsearch.server;
    requires org.elasticsearch.xcontent;

    requires org.antlr.antlr4.runtime;
    requires org.apache.lucene.core;
    requires org.objectweb.asm;
    requires org.objectweb.asm.commons;
    requires org.objectweb.asm.util;

    exports org.elasticsearch.javascript;
    exports org.elasticsearch.javascript.api;
    exports org.elasticsearch.javascript.action;

    opens org.elasticsearch.javascript to org.elasticsearch.javascript.spi;  // whitelist access
    opens org.elasticsearch.javascript.action to org.elasticsearch.server; // guice
}
