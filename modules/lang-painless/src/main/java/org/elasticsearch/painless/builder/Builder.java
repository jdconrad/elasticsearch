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

package org.elasticsearch.painless.builder;

import org.elasticsearch.painless.Location;
import org.elasticsearch.painless.ScriptClassInfo;
import org.elasticsearch.painless.lookup.PainlessLookup;
import org.elasticsearch.painless.node.AStatement;
import org.elasticsearch.painless.node.SFunction;
import org.elasticsearch.painless.node.SSource;
import org.objectweb.asm.util.Printer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class Builder {

    protected final PainlessLookup lookup;
    protected final Location location;

    public Builder(PainlessLookup lookup, Location location) {
        this.lookup = Objects.requireNonNull(lookup);
        this.location = Objects.requireNonNull(location);
    }

    protected void requireNull(String name, Object object) {
        if (object != null) {
            throw location.createError(new IllegalArgumentException(
                    getClass().getSimpleName() + " value [" + name + "] is already set to [" + object + "]"));
        }
    }

    protected <T> T requireNonNull(String name, T t) {
        if (t == null) {
            throw location.createError(new NullPointerException(
                    getClass().getSimpleName() + "value [" + name + "] cannot be set to null"));
        }

        return t;
    }

    protected <T> T requireNotSet(String name, T original, T target) {
        requireNull(name, original);
        return requireNonNull(name, target);
    }

    public final static class ClassBuilder extends Builder {

        public interface Build {
            void build(ClassBuilder cb);
        }

        protected ScriptClassInfo info = null;
        protected String name = null;
        protected String source = null;
        protected Printer debug = null;

        protected final List<FunctionBuilder> functions = new ArrayList<>();
        protected final List<StatementBuilder> statements = new ArrayList<>();

        public ClassBuilder(PainlessLookup lookup, Location location) {
            super(lookup, location);
        }

        public ClassBuilder set(ScriptClassInfo info, String name, String source, Printer debug) {
            this.info = requireNotSet("info", this.info, info);
            this.name = requireNotSet("name", this.name, name);
            this.source = requireNotSet("source", this.source, source);
            this.debug = requireNotSet("debug", this.debug, debug);
            return this;
        }

        public ClassBuilder setInfo(ScriptClassInfo info) {
            this.info = requireNotSet("info", this.info, info);
            return this;
        }

        public ClassBuilder setName(String name) {
            this.name = requireNotSet("name", this.name, name);
            return this;
        }

        public ClassBuilder setSource(String source) {
            this.source = requireNotSet("source", this.source, source);
            return this;
        }

        public ClassBuilder setDebug(Printer debug) {
            this.debug = requireNotSet("debug", this.debug, debug);
            return this;
        }

        public ClassBuilder addFunction(FunctionBuilder.Build build) {
            FunctionBuilder builder = new FunctionBuilder(this.lookup, this.location);
            build.build(builder);
            functions.add(builder);
            return this;
        }

        public ClassBuilder addStatement(StatementBuilder.Build build) {
            StatementBuilder builder = new StatementBuilder(this.lookup, this.location);
            build.build(builder);
            statements.add(builder);
            return this;
        }

        public SSource build() {
            requireNonNull("info", this.info);
            requireNonNull("name", this.name);
            requireNonNull("source", this.source);
            requireNonNull("debug", this.debug);
            SSource source = new SSource(this.location, this.info, this.name, this.source, this.debug);
            for (FunctionBuilder builder : this.functions) {
                source.addFunction(builder.build());
            }
            if (this.statements.isEmpty()) {
                throw location.createError(new IllegalArgumentException("class requires at least 1 statement"));
            }
            for (StatementBuilder builder : this.statements) {
                source.addStatement(builder.build());
            }
            return source;
        }
    }

    public final static class FunctionBuilder extends Builder {

        public interface Build {
            void build(FunctionBuilder fb);
        }

        protected String returnType = null;
        protected String functionName = null;
        protected List<Pair<String, String>> parameters = null;
        protected boolean parametersSet = false;
        protected Boolean isSynthetic = null;

        protected final List<StatementBuilder> statements = new ArrayList<>();

        public FunctionBuilder(PainlessLookup lookup, Location location) {
            super(lookup, location);
        }

        public FunctionBuilder set(String returnType, String functionName, List<Pair<String, String>> parameters, boolean isSynthetic) {
            this.returnType = requireNotSet("returnType", this.returnType, returnType);
            this.functionName = requireNotSet("functionName", this.functionName, functionName);
            this.parameters = requireNotSet("parameters", this.parameters, parameters);
            this.parametersSet = true;
            this.isSynthetic = requireNotSet("isSynthetic", this.isSynthetic, isSynthetic);
            return this;
        }

        public FunctionBuilder setReturnType(String returnType) {
            this.returnType = requireNotSet("returnType", this.returnType, returnType);
            return this;
        }

        public FunctionBuilder setFunctionName(String functionName) {
            this.functionName = requireNotSet("functionName", this.functionName, functionName);
            return this;
        }

        public FunctionBuilder setParameters(List<Pair<String, String>> parameters) {
            this.parameters = requireNotSet("parameters", this.parameters, parameters);
            this.parametersSet = true;
            return this;
        }

        public FunctionBuilder addParameter(Pair<String, String> parameter) {
            if (parametersSet) {
                requireNull("parameters", parameters);
            }
            if (parameters == null) {
                parameters = new ArrayList<>();
            }
            requireNonNull("parameter", parameter);
            parameters.add(parameter);
            return this;
        }

        public FunctionBuilder setSynthetic(boolean isSynthetic) {
            this.isSynthetic = requireNotSet("isSynthetic", this.isSynthetic, isSynthetic);
            return this;
        }

        public FunctionBuilder addStatement(StatementBuilder.Build build) {
            StatementBuilder builder = new StatementBuilder(this.lookup, this.location);
            build.build(builder);
            statements.add(builder);
            return this;
        }

        public SFunction build() {
            List<String> parameterTypes = new ArrayList<>();
            List<String> parameterNames = new ArrayList<>();
            for (Pair<String, String> parameter : this.parameters) {
                parameterTypes.add(parameter.first());
                parameterNames.add(parameter.second());
            }
            SFunction function = new SFunction(
                    this.location, this.returnType, this.functionName, parameterTypes, parameterNames, this.isSynthetic);
            if (this.statements.isEmpty()) {
                throw location.createError(new IllegalArgumentException("function requires at least 1 statement"));
            }
            for (StatementBuilder builder : this.statements) {
                function.addStatement(builder.build());
            }
            return function;
        }
    }

    public static class StatementBuilder extends Builder {

        public interface Build {
            void build(StatementBuilder fb);
        }

        public StatementBuilder(PainlessLookup lookup, Location location) {
            super(lookup, location);
        }

        public AStatement build() {
            return null;
        }
    }
}
