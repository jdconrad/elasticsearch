/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.javascript;

import org.elasticsearch.javascript.action.JavascriptContextClassBindingInfo;
import org.elasticsearch.javascript.action.JavascriptContextClassInfo;
import org.elasticsearch.javascript.action.JavascriptContextConstructorInfo;
import org.elasticsearch.javascript.action.JavascriptContextFieldInfo;
import org.elasticsearch.javascript.action.JavascriptContextInfo;
import org.elasticsearch.javascript.action.JavascriptContextInstanceBindingInfo;
import org.elasticsearch.javascript.action.JavascriptContextMethodInfo;
import org.elasticsearch.xcontent.ParseField;
import org.elasticsearch.xcontent.ToXContentObject;
import org.elasticsearch.xcontent.XContentBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JavascriptInfoJson {
    public static class Context implements ToXContentObject {
        private final String name;
        private final List<Class> classes;
        private final List<Method> importedMethods;
        private final List<JavascriptContextClassBindingInfo> classBindings;
        private final List<JavascriptContextInstanceBindingInfo> instanceBindings;

        public Context(
            JavascriptContextInfo info,
            Set<JavascriptContextClassInfo> commonClassInfos,
            Map<String, String> javaNamesToDisplayNames,
            JavadocExtractor extractor
        ) throws IOException {
            this.name = info.getName();
            List<JavascriptContextClassInfo> classInfos = ContextGeneratorCommon.excludeCommonClassInfos(commonClassInfos, info.getClasses());
            classInfos = ContextGeneratorCommon.sortClassInfos(classInfos);
            this.classes = Class.fromInfos(classInfos, javaNamesToDisplayNames, extractor);
            // TODO(stu): should we use extractor for these imported methods?
            this.importedMethods = Method.fromInfos(info.getImportedMethods(), javaNamesToDisplayNames);
            this.classBindings = info.getClassBindings();
            this.instanceBindings = info.getInstanceBindings();
        }

        public Context(
            JavascriptContextInfo info,
            Set<JavascriptContextClassInfo> commonClassInfos,
            Map<String, String> javaNamesToDisplayNames
        ) {
            this.name = info.getName();
            List<JavascriptContextClassInfo> classInfos = ContextGeneratorCommon.excludeCommonClassInfos(commonClassInfos, info.getClasses());
            classInfos = ContextGeneratorCommon.sortClassInfos(classInfos);
            this.classes = Class.fromInfos(classInfos, javaNamesToDisplayNames);
            this.importedMethods = Method.fromInfos(info.getImportedMethods(), javaNamesToDisplayNames);
            this.classBindings = info.getClassBindings();
            this.instanceBindings = info.getInstanceBindings();
        }

        @Override
        public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
            builder.startObject();
            builder.field(JavascriptContextInfo.NAME.getPreferredName(), name);
            builder.field(JavascriptContextInfo.CLASSES.getPreferredName(), classes);
            builder.field(JavascriptContextInfo.IMPORTED_METHODS.getPreferredName(), importedMethods);
            builder.field(JavascriptContextInfo.CLASS_BINDINGS.getPreferredName(), classBindings);
            builder.field(JavascriptContextInfo.INSTANCE_BINDINGS.getPreferredName(), instanceBindings);
            builder.endObject();

            return builder;
        }

        public String getName() {
            return name;
        }
    }

    public static class Class implements ToXContentObject {
        private final String name;
        private final boolean imported;
        private final List<Constructor> constructors;
        private final List<Method> staticMethods;
        private final List<Method> methods;
        private final List<Field> staticFields;
        private final List<Field> fields;

        private Class(
            String name,
            boolean imported,
            List<Constructor> constructors,
            List<Method> staticMethods,
            List<Method> methods,
            List<Field> staticFields,
            List<Field> fields
        ) {
            this.name = name;
            this.imported = imported;
            this.constructors = constructors;
            this.staticMethods = staticMethods;
            this.methods = methods;
            this.staticFields = staticFields;
            this.fields = fields;
        }

        public static List<Class> fromInfos(
            List<JavascriptContextClassInfo> infos,
            Map<String, String> javaNamesToDisplayNames,
            JavadocExtractor extractor
        ) throws IOException {
            List<Class> classes = new ArrayList<>(infos.size());
            for (JavascriptContextClassInfo info : infos) {
                JavadocExtractor.ParsedJavaClass parsedClass = extractor.parseClass(info.getName());
                Class cls = new Class(
                    javaNamesToDisplayNames.get(info.getName()),
                    info.isImported(),
                    Constructor.fromInfos(info.getConstructors(), javaNamesToDisplayNames, parsedClass, extractor, info.getName()),
                    Method.fromInfos(info.getStaticMethods(), javaNamesToDisplayNames, parsedClass, extractor, info.getName()),
                    Method.fromInfos(info.getMethods(), javaNamesToDisplayNames, parsedClass, extractor, info.getName()),
                    Field.fromInfos(info.getStaticFields(), javaNamesToDisplayNames, parsedClass),
                    Field.fromInfos(info.getFields(), javaNamesToDisplayNames, parsedClass)
                );
                classes.add(cls);
            }
            return classes;
        }

        public static List<Class> fromInfos(List<JavascriptContextClassInfo> infos, Map<String, String> javaNamesToDisplayNames) {
            List<Class> classes = new ArrayList<>(infos.size());
            for (JavascriptContextClassInfo info : infos) {
                classes.add(
                    new Class(
                        javaNamesToDisplayNames.get(info.getName()),
                        info.isImported(),
                        Constructor.fromInfos(info.getConstructors(), javaNamesToDisplayNames),
                        Method.fromInfos(info.getStaticMethods(), javaNamesToDisplayNames),
                        Method.fromInfos(info.getMethods(), javaNamesToDisplayNames),
                        Field.fromInfos(info.getStaticFields(), javaNamesToDisplayNames),
                        Field.fromInfos(info.getFields(), javaNamesToDisplayNames)
                    )
                );
            }
            return classes;
        }

        @Override
        public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
            builder.startObject();
            builder.field(JavascriptContextClassInfo.NAME.getPreferredName(), name);
            builder.field(JavascriptContextClassInfo.IMPORTED.getPreferredName(), imported);
            builder.field(JavascriptContextClassInfo.CONSTRUCTORS.getPreferredName(), constructors);
            builder.field(JavascriptContextClassInfo.STATIC_METHODS.getPreferredName(), staticMethods);
            builder.field(JavascriptContextClassInfo.METHODS.getPreferredName(), methods);
            builder.field(JavascriptContextClassInfo.STATIC_FIELDS.getPreferredName(), staticFields);
            builder.field(JavascriptContextClassInfo.FIELDS.getPreferredName(), fields);
            builder.endObject();

            return builder;
        }
    }

    private static List<String> toDisplayParameterTypes(List<String> rawParameterTypes, Map<String, String> javaNamesToDisplayNames) {
        List<String> displayParameterTypes = new ArrayList<>(rawParameterTypes.size());
        for (String rawParameterType : rawParameterTypes) {
            displayParameterTypes.add(ContextGeneratorCommon.getType(javaNamesToDisplayNames, rawParameterType));
        }
        return displayParameterTypes;
    }

    public static class Method implements ToXContentObject {
        private final String declaring;
        private final String name;
        private final String rtn;
        private final JavadocExtractor.ParsedJavadoc javadoc;
        private final List<String> parameters;
        private final List<String> parameterNames;
        public static final ParseField PARAMETER_NAMES = new ParseField("parameter_names");
        public static final ParseField JAVADOC = new ParseField("javadoc");

        private Method(
            String declaring,
            String name,
            String rtn,
            JavadocExtractor.ParsedJavadoc javadoc,
            List<String> parameters,
            List<String> parameterNames
        ) {
            this.declaring = declaring;
            this.name = name;
            this.rtn = rtn;
            this.javadoc = javadoc;
            this.parameters = parameters;
            this.parameterNames = parameterNames;
        }

        public static List<Method> fromInfos(List<JavascriptContextMethodInfo> infos, Map<String, String> javaNamesToDisplayNames) {
            List<Method> methods = new ArrayList<>(infos.size());
            for (JavascriptContextMethodInfo info : infos) {
                String returnType = ContextGeneratorCommon.getType(javaNamesToDisplayNames, info.getRtn());
                List<String> parameterTypes = toDisplayParameterTypes(info.getParameters(), javaNamesToDisplayNames);
                methods.add(new Method(info.getDeclaring(), info.getName(), returnType, null, parameterTypes, null));
            }
            return methods;
        }

        public static List<Method> fromInfos(
            List<JavascriptContextMethodInfo> infos,
            Map<String, String> javaNamesToDisplayNames,
            JavadocExtractor.ParsedJavaClass parsed,
            JavadocExtractor extractor,
            String className
        ) throws IOException {
            List<Method> methods = new ArrayList<>(infos.size());
            for (JavascriptContextMethodInfo info : infos) {
                JavadocExtractor.ParsedJavadoc javadoc = null;
                List<String> parameterNames = null;

                String name = info.getName();
                List<String> parameterTypes = toDisplayParameterTypes(info.getParameters(), javaNamesToDisplayNames);

                JavadocExtractor.ParsedMethod parsedMethod = parsed.getMethod(name, parameterTypes);
                if ((parsedMethod == null || parsedMethod.isEmpty()) && className.equals(info.getDeclaring()) == false) {
                    JavadocExtractor.ParsedJavaClass parsedDeclared = extractor.parseClass(info.getDeclaring());
                    parsedMethod = parsedDeclared.getMethod(name, parameterTypes);
                    if (parsedMethod == null) {
                        parsedMethod = parsedDeclared.getAugmentedMethod(name, javaNamesToDisplayNames.get(className), parameterTypes);
                    }
                }
                if (parsedMethod != null) {
                    javadoc = parsedMethod.javadoc();
                    parameterNames = parsedMethod.parameterNames();
                }

                methods.add(
                    new Method(
                        info.getDeclaring(),
                        name,
                        ContextGeneratorCommon.getType(javaNamesToDisplayNames, info.getRtn()),
                        javadoc,
                        parameterTypes,
                        parameterNames
                    )
                );
            }
            return methods;
        }

        @Override
        public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
            builder.startObject();
            builder.field(JavascriptContextMethodInfo.DECLARING.getPreferredName(), declaring);
            builder.field(JavascriptContextMethodInfo.NAME.getPreferredName(), name);
            builder.field(JavascriptContextMethodInfo.RTN.getPreferredName(), rtn);
            if (javadoc != null && javadoc.isEmpty() == false) {
                builder.field(JAVADOC.getPreferredName(), javadoc);
            }
            builder.field(JavascriptContextMethodInfo.PARAMETERS.getPreferredName(), parameters);
            if (parameterNames != null && parameterNames.size() > 0) {
                builder.field(PARAMETER_NAMES.getPreferredName(), parameterNames);
            }
            builder.endObject();

            return builder;
        }
    }

    public static class Constructor implements ToXContentObject {
        private final String declaring;
        private final List<String> parameters;
        private final List<String> parameterNames;
        private final JavadocExtractor.ParsedJavadoc javadoc;

        public static final ParseField JAVADOC = new ParseField("javadoc");
        public static final ParseField PARAMETER_NAMES = new ParseField("parameter_names");

        private Constructor(
            String declaring,
            List<String> parameters,
            List<String> parameterNames,
            JavadocExtractor.ParsedJavadoc javadoc
        ) {
            this.declaring = declaring;
            this.parameters = parameters;
            this.parameterNames = parameterNames;
            this.javadoc = javadoc;
        }

        public static List<Constructor> fromInfos(List<JavascriptContextConstructorInfo> infos, Map<String, String> javaNamesToDisplayNames) {
            List<Constructor> constructors = new ArrayList<>(infos.size());
            for (JavascriptContextConstructorInfo info : infos) {
                List<String> parameterTypes = toDisplayParameterTypes(info.getParameters(), javaNamesToDisplayNames);
                constructors.add(new Constructor(info.getDeclaring(), parameterTypes, null, null));
            }
            return constructors;
        }

        private static List<Constructor> fromInfos(
            List<JavascriptContextConstructorInfo> infos,
            Map<String, String> javaNamesToDisplayNames,
            JavadocExtractor.ParsedJavaClass parsed,
            JavadocExtractor extractor,
            String className
        ) throws IOException {
            List<Constructor> constructors = new ArrayList<>(infos.size());
            for (JavascriptContextConstructorInfo info : infos) {
                List<String> parameterTypes = toDisplayParameterTypes(info.getParameters(), javaNamesToDisplayNames);
                List<String> parameterNames = null;
                JavadocExtractor.ParsedJavadoc javadoc = null;

                JavadocExtractor.ParsedMethod parsedMethod = parsed.getConstructor(parameterTypes);
                if ((parsedMethod == null || parsedMethod.isEmpty()) && className.equals(info.getDeclaring()) == false) {
                    parsedMethod = extractor.parseClass(info.getDeclaring()).getConstructor(parameterTypes);
                }
                if (parsedMethod != null) {
                    parameterNames = parsedMethod.parameterNames();
                    javadoc = parsedMethod.javadoc();
                }

                constructors.add(new Constructor(info.getDeclaring(), parameterTypes, parameterNames, javadoc));
            }
            return constructors;
        }

        @Override
        public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
            builder.startObject();
            builder.field(JavascriptContextConstructorInfo.DECLARING.getPreferredName(), declaring);
            builder.field(JavascriptContextConstructorInfo.PARAMETERS.getPreferredName(), parameters);
            if (parameterNames != null && parameterNames.size() > 0) {
                builder.field(PARAMETER_NAMES.getPreferredName(), parameterNames);
            }
            if (javadoc != null && javadoc.isEmpty() == false) {
                builder.field(JAVADOC.getPreferredName(), javadoc);
            }
            builder.endObject();

            return builder;
        }
    }

    public static class Field implements ToXContentObject {
        private final String declaring;
        private final String name;
        private final String type;
        private final String javadoc;

        public static final ParseField JAVADOC = new ParseField("javadoc");

        private Field(String declaring, String name, String type, String javadoc) {
            this.declaring = declaring;
            this.name = name;
            this.type = type;
            this.javadoc = javadoc;
        }

        public static List<Field> fromInfos(List<JavascriptContextFieldInfo> infos, Map<String, String> javaNamesToDisplayNames) {
            List<Field> fields = new ArrayList<>(infos.size());
            for (JavascriptContextFieldInfo info : infos) {
                String type = ContextGeneratorCommon.getType(javaNamesToDisplayNames, info.getType());
                fields.add(new Field(info.getDeclaring(), info.getName(), type, null));
            }
            return fields;
        }

        public static List<Field> fromInfos(
            List<JavascriptContextFieldInfo> infos,
            Map<String, String> javaNamesToDisplayNames,
            JavadocExtractor.ParsedJavaClass pj
        ) {
            List<Field> fields = new ArrayList<>(infos.size());
            for (JavascriptContextFieldInfo info : infos) {
                String name = info.getName();
                String type = ContextGeneratorCommon.getType(javaNamesToDisplayNames, info.getType());
                fields.add(new Field(info.getDeclaring(), name, type, pj.getField(name)));
            }
            return fields;
        }

        @Override
        public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
            builder.startObject();
            builder.field(JavascriptContextFieldInfo.DECLARING.getPreferredName(), declaring);
            builder.field(JavascriptContextFieldInfo.NAME.getPreferredName(), name);
            builder.field(JavascriptContextFieldInfo.TYPE.getPreferredName(), type);
            if (javadoc != null && "".equals(javadoc) == false) {
                builder.field(JAVADOC.getPreferredName(), javadoc);
            }
            builder.endObject();

            return builder;
        }
    }
}
