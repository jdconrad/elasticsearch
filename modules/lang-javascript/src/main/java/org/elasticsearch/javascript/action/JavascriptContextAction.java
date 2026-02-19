/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.javascript.action;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.ActionRequestValidationException;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.action.ActionType;
import org.elasticsearch.action.LegacyActionRequest;
import org.elasticsearch.action.support.ActionFilters;
import org.elasticsearch.action.support.HandledTransportAction;
import org.elasticsearch.client.internal.node.NodeClient;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.io.stream.Writeable;
import org.elasticsearch.common.util.concurrent.EsExecutors;
import org.elasticsearch.injection.guice.Inject;
import org.elasticsearch.javascript.JavascriptScriptEngine;
import org.elasticsearch.javascript.lookup.JavascriptLookup;
import org.elasticsearch.rest.BaseRestHandler;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.action.RestToXContentListener;
import org.elasticsearch.script.ScriptContext;
import org.elasticsearch.tasks.Task;
import org.elasticsearch.transport.TransportService;
import org.elasticsearch.xcontent.ParseField;
import org.elasticsearch.xcontent.ToXContentObject;
import org.elasticsearch.xcontent.XContentBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.elasticsearch.rest.RestRequest.Method.GET;

/**
 * Internal REST API for querying context information about Javascript whitelists.
 * Commands include the following:
 * <ul>
 *     <li> GET /_scripts/javascript/_context -- retrieves a list of contexts </li>
 *     <li> GET /_scripts/javascript/_context?context=%name% --
 *     retrieves all available information about the API for this specific context</li>
 * </ul>
 */
public class JavascriptContextAction {

    public static final ActionType<Response> INSTANCE = new ActionType<>("cluster:admin/scripts/javascript/context");

    private static final String SCRIPT_CONTEXT_NAME_PARAM = "context";

    private JavascriptContextAction() {/* no instances */}

    public static class Request extends LegacyActionRequest {

        private String scriptContextName;

        public Request() {
            scriptContextName = null;
        }

        public Request(StreamInput in) throws IOException {
            super(in);
            scriptContextName = in.readString();
        }

        public void setScriptContextName(String scriptContextName) {
            this.scriptContextName = scriptContextName;
        }

        public String getScriptContextName() {
            return scriptContextName;
        }

        @Override
        public ActionRequestValidationException validate() {
            return null;
        }

        @Override
        public void writeTo(StreamOutput out) throws IOException {
            super.writeTo(out);
            out.writeString(scriptContextName);
        }
    }

    public static class Response extends ActionResponse implements ToXContentObject {

        public static final ParseField CONTEXTS = new ParseField("contexts");

        private final List<String> scriptContextNames;
        private final JavascriptContextInfo javascriptContextInfo;

        public Response(List<String> scriptContextNames, JavascriptContextInfo javascriptContextInfo) {
            Objects.requireNonNull(scriptContextNames);
            scriptContextNames = new ArrayList<>(scriptContextNames);
            scriptContextNames.sort(String::compareTo);
            this.scriptContextNames = Collections.unmodifiableList(scriptContextNames);
            this.javascriptContextInfo = javascriptContextInfo;
        }

        @Override
        public void writeTo(StreamOutput out) throws IOException {
            out.writeStringCollection(scriptContextNames);
            out.writeOptionalWriteable(javascriptContextInfo);
        }

        @Override
        public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
            if (javascriptContextInfo == null) {
                builder.startObject();
                builder.field(CONTEXTS.getPreferredName(), scriptContextNames);
                builder.endObject();
            } else {
                javascriptContextInfo.toXContent(builder, params);
            }

            return builder;
        }
    }

    public static class TransportAction extends HandledTransportAction<Request, Response> {

        private final JavascriptScriptEngine javascriptScriptEngine;

        @Inject
        public TransportAction(
            TransportService transportService,
            ActionFilters actionFilters,
            JavascriptScriptEngine javascriptScriptEngine
        ) {
            super(
                INSTANCE.name(),
                transportService,
                actionFilters,
                (Writeable.Reader<Request>) Request::new,
                EsExecutors.DIRECT_EXECUTOR_SERVICE
            );
            this.javascriptScriptEngine = javascriptScriptEngine;
        }

        @Override
        protected void doExecute(Task task, Request request, ActionListener<Response> listener) {
            List<String> scriptContextNames;
            JavascriptContextInfo javascriptContextInfo;

            if (request.scriptContextName == null) {
                scriptContextNames = javascriptScriptEngine.getContextsToLookups()
                    .keySet()
                    .stream()
                    .map(v -> v.name)
                    .collect(Collectors.toList());
                javascriptContextInfo = null;
            } else {
                ScriptContext<?> scriptContext = null;
                JavascriptLookup javascriptLookup = null;

                for (Map.Entry<ScriptContext<?>, JavascriptLookup> contextLookupEntry : javascriptScriptEngine.getContextsToLookups()
                    .entrySet()) {
                    if (contextLookupEntry.getKey().name.equals(request.getScriptContextName())) {
                        scriptContext = contextLookupEntry.getKey();
                        javascriptLookup = contextLookupEntry.getValue();
                        break;
                    }
                }

                if (scriptContext == null || javascriptLookup == null) {
                    throw new IllegalArgumentException("script context [" + request.getScriptContextName() + "] not found");
                }

                scriptContextNames = Collections.emptyList();
                javascriptContextInfo = new JavascriptContextInfo(scriptContext, javascriptLookup);
            }

            listener.onResponse(new Response(scriptContextNames, javascriptContextInfo));
        }
    }

    public static class RestAction extends BaseRestHandler {

        @Override
        public List<Route> routes() {
            return List.of(new Route(GET, "/_scripts/javascript/_context"));
        }

        @Override
        public String getName() {
            return "_scripts_javascript_context";
        }

        @Override
        protected RestChannelConsumer prepareRequest(RestRequest restRequest, NodeClient client) {
            Request request = new Request();
            request.setScriptContextName(restRequest.param(SCRIPT_CONTEXT_NAME_PARAM));
            return channel -> client.executeLocally(INSTANCE, request, new RestToXContentListener<>(channel));
        }
    }
}
