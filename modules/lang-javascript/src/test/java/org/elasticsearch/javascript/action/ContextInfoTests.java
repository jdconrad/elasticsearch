/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.javascript.action;

import org.elasticsearch.common.io.stream.Writeable;
import org.elasticsearch.test.AbstractXContentSerializingTestCase;
import org.elasticsearch.xcontent.XContentParser;

import java.util.ArrayList;
import java.util.List;

public class ContextInfoTests extends AbstractXContentSerializingTestCase<JavascriptContextInfo> {

    @Override
    protected JavascriptContextInfo doParseInstance(XContentParser parser) {
        return JavascriptContextInfo.fromXContent(parser);
    }

    @Override
    protected JavascriptContextInfo createTestInstance() {
        int classesSize = randomIntBetween(20, 100);
        List<JavascriptContextClassInfo> classes = new ArrayList<>();

        for (int clazz = 0; clazz < classesSize; ++clazz) {
            int constructorsSize = randomInt(4);
            List<JavascriptContextConstructorInfo> constructors = new ArrayList<>(constructorsSize);
            for (int constructor = 0; constructor < constructorsSize; ++constructor) {
                int parameterSize = randomInt(12);
                List<String> parameters = new ArrayList<>(parameterSize);
                for (int parameter = 0; parameter < parameterSize; ++parameter) {
                    parameters.add(randomAlphaOfLengthBetween(1, 20));
                }
                constructors.add(new JavascriptContextConstructorInfo(randomAlphaOfLength(randomIntBetween(4, 10)), parameters));
            }
            ;

            int staticMethodsSize = randomInt(4);
            List<JavascriptContextMethodInfo> staticMethods = new ArrayList<>(staticMethodsSize);
            for (int staticMethod = 0; staticMethod < staticMethodsSize; ++staticMethod) {
                int parameterSize = randomInt(12);
                List<String> parameters = new ArrayList<>(parameterSize);
                for (int parameter = 0; parameter < parameterSize; ++parameter) {
                    parameters.add(randomAlphaOfLengthBetween(1, 20));
                }
                staticMethods.add(
                    new JavascriptContextMethodInfo(
                        randomAlphaOfLength(randomIntBetween(4, 10)),
                        randomAlphaOfLength(randomIntBetween(4, 10)),
                        randomAlphaOfLength(randomIntBetween(4, 10)),
                        parameters
                    )
                );
            }

            int methodsSize = randomInt(10);
            List<JavascriptContextMethodInfo> methods = new ArrayList<>(methodsSize);
            for (int method = 0; method < methodsSize; ++method) {
                int parameterSize = randomInt(12);
                List<String> parameters = new ArrayList<>(parameterSize);
                for (int parameter = 0; parameter < parameterSize; ++parameter) {
                    parameters.add(randomAlphaOfLengthBetween(1, 20));
                }
                methods.add(
                    new JavascriptContextMethodInfo(
                        randomAlphaOfLength(randomIntBetween(4, 10)),
                        randomAlphaOfLength(randomIntBetween(4, 10)),
                        randomAlphaOfLength(randomIntBetween(4, 10)),
                        parameters
                    )
                );
            }

            int staticFieldsSize = randomInt(10);
            List<JavascriptContextFieldInfo> staticFields = new ArrayList<>();
            for (int staticField = 0; staticField < staticFieldsSize; ++staticField) {
                staticFields.add(
                    new JavascriptContextFieldInfo(
                        randomAlphaOfLength(randomIntBetween(4, 10)),
                        randomAlphaOfLength(randomIntBetween(4, 10)),
                        randomAlphaOfLength(randomIntBetween(4, 10))
                    )
                );
            }

            int fieldsSize = randomInt(4);
            List<JavascriptContextFieldInfo> fields = new ArrayList<>();
            for (int field = 0; field < fieldsSize; ++field) {
                fields.add(
                    new JavascriptContextFieldInfo(
                        randomAlphaOfLength(randomIntBetween(4, 10)),
                        randomAlphaOfLength(randomIntBetween(4, 10)),
                        randomAlphaOfLength(randomIntBetween(4, 10))
                    )
                );
            }

            classes.add(
                new JavascriptContextClassInfo(
                    randomAlphaOfLength(randomIntBetween(3, 200)),
                    randomBoolean(),
                    constructors,
                    staticMethods,
                    methods,
                    fields,
                    staticFields
                )
            );
        }

        int importedMethodsSize = randomInt(4);
        List<JavascriptContextMethodInfo> importedMethods = new ArrayList<>(importedMethodsSize);
        for (int importedMethod = 0; importedMethod < importedMethodsSize; ++importedMethod) {
            int parameterSize = randomInt(12);
            List<String> parameters = new ArrayList<>(parameterSize);
            for (int parameter = 0; parameter < parameterSize; ++parameter) {
                parameters.add(randomAlphaOfLengthBetween(1, 20));
            }
            importedMethods.add(
                new JavascriptContextMethodInfo(
                    randomAlphaOfLength(randomIntBetween(4, 10)),
                    randomAlphaOfLength(randomIntBetween(4, 10)),
                    randomAlphaOfLength(randomIntBetween(4, 10)),
                    parameters
                )
            );
        }

        int classBindingsSize = randomInt(3);
        List<JavascriptContextClassBindingInfo> classBindings = new ArrayList<>(classBindingsSize);
        for (int classBinding = 0; classBinding < classBindingsSize; ++classBinding) {
            int parameterSize = randomIntBetween(2, 5);
            int readOnly = randomIntBetween(1, parameterSize - 1);
            List<String> parameters = new ArrayList<>(parameterSize);
            for (int parameter = 0; parameter < parameterSize; ++parameter) {
                parameters.add(randomAlphaOfLengthBetween(1, 20));
            }
            classBindings.add(
                new JavascriptContextClassBindingInfo(
                    randomAlphaOfLength(randomIntBetween(4, 10)),
                    randomAlphaOfLength(randomIntBetween(4, 10)),
                    randomAlphaOfLength(randomIntBetween(4, 10)),
                    readOnly,
                    parameters
                )
            );
        }

        int instanceBindingsSize = randomInt(3);
        List<JavascriptContextInstanceBindingInfo> instanceBindings = new ArrayList<>(classBindingsSize);
        for (int instanceBinding = 0; instanceBinding < instanceBindingsSize; ++instanceBinding) {
            int parameterSize = randomInt(12);
            List<String> parameters = new ArrayList<>(parameterSize);
            for (int parameter = 0; parameter < parameterSize; ++parameter) {
                parameters.add(randomAlphaOfLengthBetween(1, 20));
            }
            instanceBindings.add(
                new JavascriptContextInstanceBindingInfo(
                    randomAlphaOfLength(randomIntBetween(4, 10)),
                    randomAlphaOfLength(randomIntBetween(4, 10)),
                    randomAlphaOfLength(randomIntBetween(4, 10)),
                    parameters
                )
            );
        }

        return new JavascriptContextInfo(randomAlphaOfLength(20), classes, importedMethods, classBindings, instanceBindings);
    }

    @Override
    protected JavascriptContextInfo mutateInstance(JavascriptContextInfo instance) {
        return null;// TODO implement https://github.com/elastic/elasticsearch/issues/25929
    }

    @Override
    protected Writeable.Reader<JavascriptContextInfo> instanceReader() {
        return JavascriptContextInfo::new;
    }
}
