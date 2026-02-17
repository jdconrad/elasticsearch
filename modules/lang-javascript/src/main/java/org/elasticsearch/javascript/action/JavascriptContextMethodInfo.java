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
import org.elasticsearch.javascript.lookup.JavascriptLookupUtility;
import org.elasticsearch.javascript.lookup.JavascriptMethod;
import org.elasticsearch.xcontent.ConstructingObjectParser;
import org.elasticsearch.xcontent.ParseField;
import org.elasticsearch.xcontent.ToXContent;
import org.elasticsearch.xcontent.ToXContentObject;
import org.elasticsearch.xcontent.XContentBuilder;
import org.elasticsearch.xcontent.XContentParser;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class JavascriptContextMethodInfo implements Writeable, ToXContentObject {

    public static final ParseField DECLARING = new ParseField("declaring");
    public static final ParseField NAME = new ParseField("name");
    public static final ParseField RTN = new ParseField("return");
    public static final ParseField PARAMETERS = new ParseField("parameters");

    @SuppressWarnings("unchecked")
    private static final ConstructingObjectParser<JavascriptContextMethodInfo, Void> PARSER = new ConstructingObjectParser<>(
        JavascriptContextMethodInfo.class.getCanonicalName(),
        (v) -> new JavascriptContextMethodInfo((String) v[0], (String) v[1], (String) v[2], (List<String>) v[3])
    );

    static {
        PARSER.declareString(ConstructingObjectParser.constructorArg(), DECLARING);
        PARSER.declareString(ConstructingObjectParser.constructorArg(), NAME);
        PARSER.declareString(ConstructingObjectParser.constructorArg(), RTN);
        PARSER.declareStringArray(ConstructingObjectParser.constructorArg(), PARAMETERS);
    }

    private final String declaring;
    private final String name;
    private final String rtn;
    private final List<String> parameters;

    public JavascriptContextMethodInfo(JavascriptMethod javascriptMethod) {
        this(
            javascriptMethod.javaMethod().getDeclaringClass().getName(),
            javascriptMethod.javaMethod().getName(),
            JavascriptContextTypeInfo.getType(javascriptMethod.returnType().getName()),
            javascriptMethod.typeParameters().stream().map(c -> JavascriptContextTypeInfo.getType(c.getName())).collect(Collectors.toList())
        );
    }

    public JavascriptContextMethodInfo(String declaring, String name, String rtn, List<String> parameters) {
        this.declaring = Objects.requireNonNull(declaring);
        this.name = Objects.requireNonNull(name);
        this.rtn = Objects.requireNonNull(rtn);
        this.parameters = Collections.unmodifiableList(Objects.requireNonNull(parameters));
    }

    public JavascriptContextMethodInfo(StreamInput in) throws IOException {
        declaring = in.readString();
        name = in.readString();
        rtn = in.readString();
        parameters = in.readCollectionAsImmutableList(StreamInput::readString);
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        out.writeString(declaring);
        out.writeString(name);
        out.writeString(rtn);
        out.writeStringCollection(parameters);
    }

    public static JavascriptContextMethodInfo fromXContent(XContentParser parser) {
        return PARSER.apply(parser, null);
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, ToXContent.Params params) throws IOException {
        builder.startObject();
        builder.field(DECLARING.getPreferredName(), declaring);
        builder.field(NAME.getPreferredName(), name);
        builder.field(RTN.getPreferredName(), rtn);
        builder.field(PARAMETERS.getPreferredName(), parameters);
        builder.endObject();

        return builder;
    }

    public String getSortValue() {
        return JavascriptLookupUtility.buildJavascriptMethodKey(name, parameters.size());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JavascriptContextMethodInfo that = (JavascriptContextMethodInfo) o;
        return Objects.equals(declaring, that.declaring)
            && Objects.equals(name, that.name)
            && Objects.equals(rtn, that.rtn)
            && Objects.equals(parameters, that.parameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(declaring, name, rtn, parameters);
    }

    @Override
    public String toString() {
        return "JavascriptContextMethodInfo{"
            + "declaring='"
            + declaring
            + '\''
            + ", name='"
            + name
            + '\''
            + ", rtn='"
            + rtn
            + '\''
            + ", parameters="
            + parameters
            + '}';
    }

    public String getDeclaring() {
        return declaring;
    }

    public String getName() {
        return name;
    }

    public String getRtn() {
        return rtn;
    }

    public List<String> getParameters() {
        return parameters;
    }
}
