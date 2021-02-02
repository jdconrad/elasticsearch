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
package org.elasticsearch.qa.custom_logging;

import org.elasticsearch.common.logging.JsonLogLine;
import org.elasticsearch.common.logging.JsonLogsIntegTestCase;
import org.elasticsearch.common.xcontent.ObjectParser;
import org.hamcrest.Matcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.AccessController;
import java.security.PrivilegedAction;

import static org.hamcrest.Matchers.is;

/**
 * Test to verify ES JSON log format. Used in ES v7. Some users might decide to keep that format.
 */
public class ESJsonLogsConfigIT extends JsonLogsIntegTestCase {
    @Override
    protected Matcher<String> nodeNameMatcher() {
        return is("integTest-0");
    }

    @Override
    protected BufferedReader openReader(Path logFile) {
        assumeFalse("Skipping test because it is being run against an external cluster.",
            logFile.getFileName().toString().equals("--external--"));

        return AccessController.doPrivileged((PrivilegedAction<BufferedReader>) () -> {
            try {
                String temp = Files.readString(logFile);

                return Files.newBufferedReader(logFile, StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    protected String getLogFileName() {
        return System.getProperty("tests.jsonLogfile");
    }

    @Override
    protected ObjectParser<JsonLogLine, Void> getParser() {
        return JsonLogLine.ES_LOG_LINE;
    }
}
