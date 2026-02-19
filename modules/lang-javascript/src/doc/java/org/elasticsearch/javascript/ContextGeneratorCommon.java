/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.javascript;

import org.elasticsearch.core.SuppressForbidden;
import org.elasticsearch.javascript.action.JavascriptContextClassBindingInfo;
import org.elasticsearch.javascript.action.JavascriptContextClassInfo;
import org.elasticsearch.javascript.action.JavascriptContextInfo;
import org.elasticsearch.javascript.action.JavascriptContextInstanceBindingInfo;
import org.elasticsearch.javascript.action.JavascriptContextMethodInfo;
import org.elasticsearch.xcontent.XContentParser;
import org.elasticsearch.xcontent.XContentParserConfiguration;
import org.elasticsearch.xcontent.json.JsonXContent;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ContextGeneratorCommon {
    @SuppressForbidden(reason = "retrieving data from an internal API not exposed as part of the REST client")
    @SuppressWarnings("unchecked")
    public static List<JavascriptContextInfo> getContextInfos() throws IOException {
        URLConnection getContextNames = new URL("http://" + System.getProperty("cluster.uri") + "/_scripts/javascript/_context")
            .openConnection();
        List<String> contextNames;
        try (
            XContentParser parser = JsonXContent.jsonXContent.createParser(
                XContentParserConfiguration.EMPTY,
                getContextNames.getInputStream()
            )
        ) {
            parser.nextToken();
            parser.nextToken();
            contextNames = (List<String>) (Object) parser.list();
        }
        ((HttpURLConnection) getContextNames).disconnect();

        List<JavascriptContextInfo> contextInfos = new ArrayList<>();

        for (String contextName : contextNames) {
            URLConnection getContextInfo = new URL(
                "http://" + System.getProperty("cluster.uri") + "/_scripts/javascript/_context?context=" + contextName
            ).openConnection();
            try (var parser = JsonXContent.jsonXContent.createParser(XContentParserConfiguration.EMPTY, getContextInfo.getInputStream())) {
                contextInfos.add(JavascriptContextInfo.fromXContent(parser));
                ((HttpURLConnection) getContextInfo).disconnect();
            }
        }

        contextInfos.sort(Comparator.comparing(JavascriptContextInfo::getName));

        return contextInfos;
    }

    public static String getType(Map<String, String> javaNamesToDisplayNames, String javaType) {
        if (javaType.endsWith("[]") == false) {
            return javaNamesToDisplayNames.getOrDefault(javaType, javaType);
        }
        int bracePosition = javaType.indexOf('[');
        String braces = javaType.substring(bracePosition);
        String type = javaType.substring(0, bracePosition);
        if (javaNamesToDisplayNames.containsKey(type)) {
            return javaNamesToDisplayNames.get(type) + braces;
        }
        return javaType;
    }

    private static Map<String, String> getDisplayNames(Collection<JavascriptContextInfo> contextInfos) {
        Map<String, String> javaNamesToDisplayNames = new HashMap<>();

        for (JavascriptContextInfo contextInfo : contextInfos) {
            for (JavascriptContextClassInfo classInfo : contextInfo.getClasses()) {
                String className = classInfo.getName();
                if (javaNamesToDisplayNames.containsKey(className) == false) {
                    if (classInfo.isImported()) {
                        javaNamesToDisplayNames.put(className, className.substring(className.lastIndexOf('.') + 1).replace('$', '.'));
                    } else {
                        javaNamesToDisplayNames.put(className, className.replace('$', '.'));
                    }
                }
            }
        }
        return javaNamesToDisplayNames;
    }

    public static List<JavascriptContextClassInfo> sortClassInfos(Collection<JavascriptContextClassInfo> unsortedClassInfos) {
        List<JavascriptContextClassInfo> classInfos = new ArrayList<>(unsortedClassInfos);
        classInfos.removeIf(ContextGeneratorCommon::isExcludedClassInfo);
        return sortFilteredClassInfos(classInfos);
    }

    static boolean isExcludedClassInfo(JavascriptContextClassInfo v) {
        return "void".equals(v.getName())
            || "boolean".equals(v.getName())
            || "byte".equals(v.getName())
            || "short".equals(v.getName())
            || "char".equals(v.getName())
            || "int".equals(v.getName())
            || "long".equals(v.getName())
            || "float".equals(v.getName())
            || "double".equals(v.getName())
            || "org.elasticsearch.javascript.lookup.def".equals(v.getName())
            || isInternalClass(v.getName());
    }

    static List<JavascriptContextClassInfo> sortFilteredClassInfos(List<JavascriptContextClassInfo> classInfos) {
        classInfos.sort((c1, c2) -> {
            String n1 = c1.getName();
            String n2 = c2.getName();
            boolean i1 = c1.isImported();
            boolean i2 = c2.isImported();

            String p1 = n1.substring(0, n1.lastIndexOf('.'));
            String p2 = n2.substring(0, n2.lastIndexOf('.'));

            int compare = p1.compareTo(p2);

            if (compare == 0) {
                if (i1 && i2) {
                    compare = n1.substring(n1.lastIndexOf('.') + 1).compareTo(n2.substring(n2.lastIndexOf('.') + 1));
                } else if (i1 == false && i2 == false) {
                    compare = n1.compareTo(n2);
                } else {
                    compare = Boolean.compare(i1, i2) * -1;
                }
            }

            return compare;
        });

        return classInfos;
    }

    private static boolean isInternalClass(String javaName) {
        return javaName.equals("org.elasticsearch.script.ScoreScript")
            || javaName.equals("org.elasticsearch.xpack.sql.expression.function.scalar.geo.GeoShape")
            || javaName.equals("org.elasticsearch.xpack.sql.expression.function.scalar.whitelist.InternalSqlScriptUtils")
            || javaName.equals("org.elasticsearch.xpack.sql.expression.literal.IntervalDayTime")
            || javaName.equals("org.elasticsearch.xpack.sql.expression.literal.IntervalYearMonth")
            || javaName.equals("org.elasticsearch.xpack.eql.expression.function.scalar.whitelist.InternalEqlScriptUtils")
            || javaName.equals("org.elasticsearch.xpack.ql.expression.function.scalar.InternalQlScriptUtils")
            || javaName.equals("org.elasticsearch.xpack.ql.expression.function.scalar.whitelist.InternalQlScriptUtils")
            || javaName.equals("org.elasticsearch.script.ScoreScript$ExplanationHolder");
    }

    public static List<JavascriptContextClassInfo> excludeCommonClassInfos(
        Set<JavascriptContextClassInfo> exclude,
        List<JavascriptContextClassInfo> classInfos
    ) {
        List<JavascriptContextClassInfo> uniqueClassInfos = new ArrayList<>(classInfos);
        uniqueClassInfos.removeIf(exclude::contains);
        return uniqueClassInfos;
    }

    public static class JavascriptInfos {
        public final Set<JavascriptContextMethodInfo> importedMethods;
        public final Set<JavascriptContextClassBindingInfo> classBindings;
        public final Set<JavascriptContextInstanceBindingInfo> instanceBindings;

        public final List<JavascriptInfoJson.Class> common;
        public final List<JavascriptInfoJson.Context> contexts;

        public final Map<String, String> javaNamesToDisplayNames;
        public final Map<String, String> javaNamesToJavadoc;
        public final Map<String, List<String>> javaNamesToArgs;

        public JavascriptInfos(List<JavascriptContextInfo> contextInfos) {
            javaNamesToDisplayNames = getDisplayNames(contextInfos);

            javaNamesToJavadoc = new HashMap<>();
            javaNamesToArgs = new HashMap<>();

            Set<JavascriptContextClassInfo> commonClassInfos = getCommon(contextInfos, JavascriptContextInfo::getClasses);
            common = JavascriptInfoJson.Class.fromInfos(sortClassInfos(commonClassInfos), javaNamesToDisplayNames);

            importedMethods = getCommon(contextInfos, JavascriptContextInfo::getImportedMethods);

            classBindings = getCommon(contextInfos, JavascriptContextInfo::getClassBindings);

            instanceBindings = getCommon(contextInfos, JavascriptContextInfo::getInstanceBindings);

            contexts = contextInfos.stream()
                .map(ctx -> new JavascriptInfoJson.Context(ctx, commonClassInfos, javaNamesToDisplayNames))
                .collect(Collectors.toList());
        }

        public JavascriptInfos(List<JavascriptContextInfo> contextInfos, JavadocExtractor extractor) throws IOException {
            javaNamesToDisplayNames = getDisplayNames(contextInfos);

            javaNamesToJavadoc = new HashMap<>();
            javaNamesToArgs = new HashMap<>();

            Set<JavascriptContextClassInfo> commonClassInfos = getCommon(contextInfos, JavascriptContextInfo::getClasses);
            common = JavascriptInfoJson.Class.fromInfos(sortClassInfos(commonClassInfos), javaNamesToDisplayNames, extractor);

            importedMethods = getCommon(contextInfos, JavascriptContextInfo::getImportedMethods);

            classBindings = getCommon(contextInfos, JavascriptContextInfo::getClassBindings);

            instanceBindings = getCommon(contextInfos, JavascriptContextInfo::getInstanceBindings);

            contexts = new ArrayList<>(contextInfos.size());
            for (JavascriptContextInfo contextInfo : contextInfos) {
                contexts.add(new JavascriptInfoJson.Context(contextInfo, commonClassInfos, javaNamesToDisplayNames, extractor));
            }
        }

        private static <T> Set<T> getCommon(
            List<JavascriptContextInfo> javascriptContexts,
            Function<JavascriptContextInfo, List<T>> getter
        ) {
            Map<T, Integer> infoCounts = new HashMap<>();
            for (JavascriptContextInfo contextInfo : javascriptContexts) {
                for (T info : getter.apply(contextInfo)) {
                    infoCounts.merge(info, 1, Integer::sum);
                }
            }
            return infoCounts.entrySet()
                .stream()
                .filter(e -> e.getValue() == javascriptContexts.size())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
        }
    }
}
