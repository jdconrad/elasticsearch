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
import org.elasticsearch.javascript.lookup.JavascriptField;
import org.elasticsearch.javascript.lookup.JavascriptLookupUtility;
import org.elasticsearch.xcontent.ConstructingObjectParser;
import org.elasticsearch.xcontent.ParseField;
import org.elasticsearch.xcontent.ToXContentObject;
import org.elasticsearch.xcontent.XContentBuilder;
import org.elasticsearch.xcontent.XContentParser;

import java.io.IOException;
import java.util.Objects;

public class JavascriptContextFieldInfo implements Writeable, ToXContentObject {

    public static final ParseField DECLARING = new ParseField("declaring");
    public static final ParseField NAME = new ParseField("name");
    public static final ParseField TYPE = new ParseField("type");

    private static final ConstructingObjectParser<JavascriptContextFieldInfo, Void> PARSER = new ConstructingObjectParser<>(
        JavascriptContextFieldInfo.class.getCanonicalName(),
        (v) -> new JavascriptContextFieldInfo((String) v[0], (String) v[1], (String) v[2])
    );

    static {
        PARSER.declareString(ConstructingObjectParser.constructorArg(), DECLARING);
        PARSER.declareString(ConstructingObjectParser.constructorArg(), NAME);
        PARSER.declareString(ConstructingObjectParser.constructorArg(), TYPE);
    }

    private final String declaring;
    private final String name;
    private final String type;

    public JavascriptContextFieldInfo(JavascriptField javascriptField) {
        this(
            javascriptField.javaField().getDeclaringClass().getName(),
            javascriptField.javaField().getName(),
            JavascriptContextTypeInfo.getType(javascriptField.typeParameter().getName())
        );
    }

    public JavascriptContextFieldInfo(String declaring, String name, String type) {
        this.declaring = Objects.requireNonNull(declaring);
        this.name = Objects.requireNonNull(name);
        this.type = Objects.requireNonNull(type);
    }

    public JavascriptContextFieldInfo(StreamInput in) throws IOException {
        declaring = in.readString();
        name = in.readString();
        type = in.readString();
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        out.writeString(declaring);
        out.writeString(name);
        out.writeString(type);
    }

    public static JavascriptContextFieldInfo fromXContent(XContentParser parser) {
        return PARSER.apply(parser, null);
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject();
        builder.field(DECLARING.getPreferredName(), declaring);
        builder.field(NAME.getPreferredName(), name);
        builder.field(TYPE.getPreferredName(), type);
        builder.endObject();

        return builder;
    }

    public String getSortValue() {
        return JavascriptLookupUtility.buildJavascriptFieldKey(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JavascriptContextFieldInfo that = (JavascriptContextFieldInfo) o;
        return Objects.equals(declaring, that.declaring) && Objects.equals(name, that.name) && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(declaring, name, type);
    }

    @Override
    public String toString() {
        return "JavascriptContextFieldInfo{" + "declaring='" + declaring + '\'' + ", name='" + name + '\'' + ", type='" + type + '\'' + '}';
    }

    public String getDeclaring() {
        return declaring;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
