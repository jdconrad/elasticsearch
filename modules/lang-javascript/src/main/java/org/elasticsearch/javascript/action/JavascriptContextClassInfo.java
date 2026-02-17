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
import org.elasticsearch.javascript.lookup.JavascriptClass;
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

public class JavascriptContextClassInfo implements Writeable, ToXContentObject {

    public static final ParseField NAME = new ParseField("name");
    public static final ParseField IMPORTED = new ParseField("imported");
    public static final ParseField CONSTRUCTORS = new ParseField("constructors");
    public static final ParseField STATIC_METHODS = new ParseField("static_methods");
    public static final ParseField METHODS = new ParseField("methods");
    public static final ParseField STATIC_FIELDS = new ParseField("static_fields");
    public static final ParseField FIELDS = new ParseField("fields");

    @SuppressWarnings("unchecked")
    private static final ConstructingObjectParser<JavascriptContextClassInfo, Void> PARSER = new ConstructingObjectParser<>(
        JavascriptContextClassInfo.class.getCanonicalName(),
        (v) -> new JavascriptContextClassInfo(
            (String) v[0],
            (boolean) v[1],
            (List<JavascriptContextConstructorInfo>) v[2],
            (List<JavascriptContextMethodInfo>) v[3],
            (List<JavascriptContextMethodInfo>) v[4],
            (List<JavascriptContextFieldInfo>) v[5],
            (List<JavascriptContextFieldInfo>) v[6]
        )
    );

    static {
        PARSER.declareString(ConstructingObjectParser.constructorArg(), NAME);
        PARSER.declareBoolean(ConstructingObjectParser.constructorArg(), IMPORTED);
        PARSER.declareObjectArray(
            ConstructingObjectParser.constructorArg(),
            (p, c) -> JavascriptContextConstructorInfo.fromXContent(p),
            CONSTRUCTORS
        );
        PARSER.declareObjectArray(
            ConstructingObjectParser.constructorArg(),
            (p, c) -> JavascriptContextMethodInfo.fromXContent(p),
            STATIC_METHODS
        );
        PARSER.declareObjectArray(ConstructingObjectParser.constructorArg(), (p, c) -> JavascriptContextMethodInfo.fromXContent(p), METHODS);
        PARSER.declareObjectArray(
            ConstructingObjectParser.constructorArg(),
            (p, c) -> JavascriptContextFieldInfo.fromXContent(p),
            STATIC_FIELDS
        );
        PARSER.declareObjectArray(ConstructingObjectParser.constructorArg(), (p, c) -> JavascriptContextFieldInfo.fromXContent(p), FIELDS);
    }

    private final String name;
    private final boolean imported;
    private final List<JavascriptContextConstructorInfo> constructors;
    private final List<JavascriptContextMethodInfo> staticMethods;
    private final List<JavascriptContextMethodInfo> methods;
    private final List<JavascriptContextFieldInfo> staticFields;
    private final List<JavascriptContextFieldInfo> fields;

    public JavascriptContextClassInfo(Class<?> javaClass, boolean imported, JavascriptClass javascriptClass) {
        this(
            javaClass.getName(),
            imported,
            javascriptClass.constructors.values().stream().map(JavascriptContextConstructorInfo::new).collect(Collectors.toList()),
            javascriptClass.staticMethods.values().stream().map(JavascriptContextMethodInfo::new).collect(Collectors.toList()),
            javascriptClass.methods.values().stream().map(JavascriptContextMethodInfo::new).collect(Collectors.toList()),
            javascriptClass.staticFields.values().stream().map(JavascriptContextFieldInfo::new).collect(Collectors.toList()),
            javascriptClass.fields.values().stream().map(JavascriptContextFieldInfo::new).collect(Collectors.toList())
        );
    }

    public JavascriptContextClassInfo(
        String name,
        boolean imported,
        List<JavascriptContextConstructorInfo> constructors,
        List<JavascriptContextMethodInfo> staticMethods,
        List<JavascriptContextMethodInfo> methods,
        List<JavascriptContextFieldInfo> staticFields,
        List<JavascriptContextFieldInfo> fields
    ) {

        this.name = Objects.requireNonNull(name);
        this.imported = imported;
        constructors = new ArrayList<>(Objects.requireNonNull(constructors));
        constructors.sort(Comparator.comparing(JavascriptContextConstructorInfo::getSortValue));
        this.constructors = Collections.unmodifiableList(constructors);
        staticMethods = new ArrayList<>(Objects.requireNonNull(staticMethods));
        staticMethods.sort(Comparator.comparing(JavascriptContextMethodInfo::getSortValue));
        this.staticMethods = Collections.unmodifiableList(staticMethods);
        methods = new ArrayList<>(Objects.requireNonNull(methods));
        methods.sort(Comparator.comparing(JavascriptContextMethodInfo::getSortValue));
        this.methods = Collections.unmodifiableList(methods);
        staticFields = new ArrayList<>(Objects.requireNonNull(staticFields));
        staticFields.sort(Comparator.comparing(JavascriptContextFieldInfo::getSortValue));
        this.staticFields = Collections.unmodifiableList(staticFields);
        fields = new ArrayList<>(Objects.requireNonNull(fields));
        fields.sort(Comparator.comparing(JavascriptContextFieldInfo::getSortValue));
        this.fields = Collections.unmodifiableList(fields);
    }

    public JavascriptContextClassInfo(StreamInput in) throws IOException {
        name = in.readString();
        imported = in.readBoolean();
        constructors = in.readCollectionAsImmutableList(JavascriptContextConstructorInfo::new);
        staticMethods = in.readCollectionAsImmutableList(JavascriptContextMethodInfo::new);
        methods = in.readCollectionAsImmutableList(JavascriptContextMethodInfo::new);
        staticFields = in.readCollectionAsImmutableList(JavascriptContextFieldInfo::new);
        fields = in.readCollectionAsImmutableList(JavascriptContextFieldInfo::new);
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        out.writeString(name);
        out.writeBoolean(imported);
        out.writeCollection(constructors);
        out.writeCollection(staticMethods);
        out.writeCollection(methods);
        out.writeCollection(staticFields);
        out.writeCollection(fields);
    }

    public static JavascriptContextClassInfo fromXContent(XContentParser parser) {
        return PARSER.apply(parser, null);
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject();
        builder.field(NAME.getPreferredName(), name);
        builder.field(IMPORTED.getPreferredName(), imported);
        builder.field(CONSTRUCTORS.getPreferredName(), constructors);
        builder.field(STATIC_METHODS.getPreferredName(), staticMethods);
        builder.field(METHODS.getPreferredName(), methods);
        builder.field(STATIC_FIELDS.getPreferredName(), staticFields);
        builder.field(FIELDS.getPreferredName(), fields);
        builder.endObject();

        return builder;
    }

    public String getSortValue() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JavascriptContextClassInfo that = (JavascriptContextClassInfo) o;
        return imported == that.imported
            && Objects.equals(name, that.name)
            && Objects.equals(constructors, that.constructors)
            && Objects.equals(staticMethods, that.staticMethods)
            && Objects.equals(methods, that.methods)
            && Objects.equals(staticFields, that.staticFields)
            && Objects.equals(fields, that.fields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, imported, constructors, staticMethods, methods, staticFields, fields);
    }

    @Override
    public String toString() {
        return "JavascriptContextClassInfo{"
            + "name='"
            + name
            + '\''
            + ", imported="
            + imported
            + ", constructors="
            + constructors
            + ", staticMethods="
            + staticMethods
            + ", methods="
            + methods
            + ", staticFields="
            + staticFields
            + ", fields="
            + fields
            + '}';
    }

    public String getName() {
        return name;
    }

    public boolean isImported() {
        return imported;
    }

    public List<JavascriptContextConstructorInfo> getConstructors() {
        return constructors;
    }

    public List<JavascriptContextMethodInfo> getStaticMethods() {
        return staticMethods;
    }

    public List<JavascriptContextMethodInfo> getMethods() {
        return methods;
    }

    public List<JavascriptContextFieldInfo> getStaticFields() {
        return staticFields;
    }

    public List<JavascriptContextFieldInfo> getFields() {
        return fields;
    }
}
