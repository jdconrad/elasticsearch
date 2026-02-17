/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.javascript.action;

import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.io.stream.Writeable;
import org.elasticsearch.javascript.lookup.JavascriptClassBinding;
import org.elasticsearch.javascript.lookup.JavascriptInstanceBinding;
import org.elasticsearch.javascript.lookup.JavascriptLookup;
import org.elasticsearch.javascript.lookup.JavascriptMethod;
import org.elasticsearch.script.ScriptContext;
import org.elasticsearch.xcontent.ConstructingObjectParser;
import org.elasticsearch.xcontent.ParseField;
import org.elasticsearch.xcontent.ToXContentObject;
import org.elasticsearch.xcontent.XContentBuilder;
import org.elasticsearch.xcontent.XContentParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class JavascriptContextInfo implements Writeable, ToXContentObject {

    public static final ParseField NAME = new ParseField("name");
    public static final ParseField CLASSES = new ParseField("classes");
    public static final ParseField IMPORTED_METHODS = new ParseField("imported_methods");
    public static final ParseField CLASS_BINDINGS = new ParseField("class_bindings");
    public static final ParseField INSTANCE_BINDINGS = new ParseField("instance_bindings");

    @SuppressWarnings("unchecked")
    private static final ConstructingObjectParser<JavascriptContextInfo, Void> PARSER = new ConstructingObjectParser<>(
        JavascriptContextInfo.class.getCanonicalName(),
        (v) -> new JavascriptContextInfo(
            (String) v[0],
            (List<JavascriptContextClassInfo>) v[1],
            (List<JavascriptContextMethodInfo>) v[2],
            (List<JavascriptContextClassBindingInfo>) v[3],
            (List<JavascriptContextInstanceBindingInfo>) v[4]
        )
    );

    static {
        PARSER.declareString(ConstructingObjectParser.constructorArg(), NAME);
        PARSER.declareObjectArray(ConstructingObjectParser.constructorArg(), (p, c) -> JavascriptContextClassInfo.fromXContent(p), CLASSES);
        PARSER.declareObjectArray(
            ConstructingObjectParser.constructorArg(),
            (p, c) -> JavascriptContextMethodInfo.fromXContent(p),
            IMPORTED_METHODS
        );
        PARSER.declareObjectArray(
            ConstructingObjectParser.constructorArg(),
            (p, c) -> JavascriptContextClassBindingInfo.fromXContent(p),
            CLASS_BINDINGS
        );
        PARSER.declareObjectArray(
            ConstructingObjectParser.constructorArg(),
            (p, c) -> JavascriptContextInstanceBindingInfo.fromXContent(p),
            INSTANCE_BINDINGS
        );
    }

    private final String name;
    private final List<JavascriptContextClassInfo> classes;
    private final List<JavascriptContextMethodInfo> importedMethods;
    private final List<JavascriptContextClassBindingInfo> classBindings;
    private final List<JavascriptContextInstanceBindingInfo> instanceBindings;

    public JavascriptContextInfo(ScriptContext<?> scriptContext, JavascriptLookup javascriptLookup) {
        this(
            scriptContext.name,
            javascriptLookup.getClasses()
                .stream()
                .map(
                    javaClass -> new JavascriptContextClassInfo(
                        javaClass,
                        javaClass == javascriptLookup.canonicalTypeNameToType(
                            javaClass.getName().substring(javaClass.getName().lastIndexOf('.') + 1).replace('$', '.')
                        ),
                        javascriptLookup.lookupJavascriptClass(javaClass)
                    )
                )
                .collect(Collectors.toList()),
            javascriptLookup.getImportedJavascriptMethodsKeys().stream().map(importedJavascriptMethodKey -> {
                String[] split = importedJavascriptMethodKey.split("/");
                String importedJavascriptMethodName = split[0];
                int importedJavascriptMethodArity = Integer.parseInt(split[1]);
                JavascriptMethod importedJavascriptMethod = javascriptLookup.lookupImportedJavascriptMethod(
                    importedJavascriptMethodName,
                    importedJavascriptMethodArity
                );
                return new JavascriptContextMethodInfo(importedJavascriptMethod);
            }).collect(Collectors.toList()),
            javascriptLookup.getJavascriptClassBindingsKeys().stream().map(javascriptClassBindingKey -> {
                String[] split = javascriptClassBindingKey.split("/");
                String javascriptClassBindingName = split[0];
                int javascriptClassBindingArity = Integer.parseInt(split[1]);
                JavascriptClassBinding javascriptClassBinding = javascriptLookup.lookupJavascriptClassBinding(
                    javascriptClassBindingName,
                    javascriptClassBindingArity
                );
                return new JavascriptContextClassBindingInfo(javascriptClassBinding);
            }).collect(Collectors.toList()),
            javascriptLookup.getJavascriptInstanceBindingsKeys().stream().map(javascriptInstanceBindingKey -> {
                String[] split = javascriptInstanceBindingKey.split("/");
                String javascriptInstanceBindingName = split[0];
                int javascriptInstanceBindingArity = Integer.parseInt(split[1]);
                JavascriptInstanceBinding javascriptInstanceBinding = javascriptLookup.lookupJavascriptInstanceBinding(
                    javascriptInstanceBindingName,
                    javascriptInstanceBindingArity
                );
                return new JavascriptContextInstanceBindingInfo(javascriptInstanceBinding);
            }).collect(Collectors.toList())
        );
    }

    public JavascriptContextInfo(
        String name,
        List<JavascriptContextClassInfo> classes,
        List<JavascriptContextMethodInfo> importedMethods,
        List<JavascriptContextClassBindingInfo> classBindings,
        List<JavascriptContextInstanceBindingInfo> instanceBindings
    ) {
        this.name = Objects.requireNonNull(name);
        classes = new ArrayList<>(Objects.requireNonNull(classes));
        classes.sort(Comparator.comparing(JavascriptContextClassInfo::getSortValue));
        this.classes = Collections.unmodifiableList(classes);
        importedMethods = new ArrayList<>(Objects.requireNonNull(importedMethods));
        importedMethods.sort(Comparator.comparing(JavascriptContextMethodInfo::getSortValue));
        this.importedMethods = Collections.unmodifiableList(importedMethods);
        classBindings = new ArrayList<>(Objects.requireNonNull(classBindings));
        classBindings.sort(Comparator.comparing(JavascriptContextClassBindingInfo::getSortValue));
        this.classBindings = Collections.unmodifiableList(classBindings);
        instanceBindings = new ArrayList<>(Objects.requireNonNull(instanceBindings));
        instanceBindings.sort(Comparator.comparing(JavascriptContextInstanceBindingInfo::getSortValue));
        this.instanceBindings = Collections.unmodifiableList(instanceBindings);
    }

    public JavascriptContextInfo(StreamInput in) throws IOException {
        name = in.readString();
        classes = in.readCollectionAsImmutableList(JavascriptContextClassInfo::new);
        importedMethods = in.readCollectionAsImmutableList(JavascriptContextMethodInfo::new);
        classBindings = in.readCollectionAsImmutableList(JavascriptContextClassBindingInfo::new);
        instanceBindings = in.readCollectionAsImmutableList(JavascriptContextInstanceBindingInfo::new);
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        out.writeString(name);
        out.writeCollection(classes);
        out.writeCollection(importedMethods);
        out.writeCollection(classBindings);
        out.writeCollection(instanceBindings);
    }

    public static JavascriptContextInfo fromXContent(XContentParser parser) {
        return PARSER.apply(parser, null);
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject();
        builder.field(NAME.getPreferredName(), name);
        builder.field(CLASSES.getPreferredName(), classes);
        builder.field(IMPORTED_METHODS.getPreferredName(), importedMethods);
        builder.field(CLASS_BINDINGS.getPreferredName(), classBindings);
        builder.field(INSTANCE_BINDINGS.getPreferredName(), instanceBindings);
        builder.endObject();

        return builder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JavascriptContextInfo that = (JavascriptContextInfo) o;
        return Objects.equals(name, that.name)
            && Objects.equals(classes, that.classes)
            && Objects.equals(importedMethods, that.importedMethods)
            && Objects.equals(classBindings, that.classBindings)
            && Objects.equals(instanceBindings, that.instanceBindings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, classes, importedMethods, classBindings, instanceBindings);
    }

    @Override
    public String toString() {
        return "JavascriptContextInfo{"
            + "name='"
            + name
            + '\''
            + ", classes="
            + classes
            + ", importedMethods="
            + importedMethods
            + ", classBindings="
            + classBindings
            + ", instanceBindings="
            + instanceBindings
            + '}';
    }

    public String getName() {
        return name;
    }

    public List<JavascriptContextClassInfo> getClasses() {
        return classes;
    }

    public List<JavascriptContextMethodInfo> getImportedMethods() {
        return importedMethods;
    }

    public List<JavascriptContextClassBindingInfo> getClassBindings() {
        return classBindings;
    }

    public List<JavascriptContextInstanceBindingInfo> getInstanceBindings() {
        return instanceBindings;
    }
}
