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

public abstract class Builder {

    protected final PainlessLookup lookup;
    protected final Location location;

    public Builder(PainlessLookup lookup, Location location) {
        this.lookup = lookup;
        this.location = location;
    }

    public final static class ClassBuilder extends Builder {

        public interface Build {
            void build(ClassBuilder cb);
        }

        protected ScriptClassInfo info;
        protected String name;
        protected String source;
        protected Printer debug;

        protected final List<FunctionBuilder> functions = new ArrayList<>();

        public ClassBuilder(PainlessLookup lookup, Location location) {
            super(lookup, location);
        }

        public ClassBuilder(PainlessLookup lookup, Location location,
                ScriptClassInfo info,
                String name,
                String source,
                Printer debug) {

            super(lookup, location);

            this.info = info;
            this.name = name;
            this.source = source;
            this.debug = debug;
        }

        public void setInfo(ScriptClassInfo info) {
            this.info = info;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public void setDebug(Printer debug) {
            this.debug = debug;
        }

        public ClassBuilder addFunction(FunctionBuilder.Build build) {
            FunctionBuilder builder = new FunctionBuilder(this.lookup, this.location);
            build.build(builder);
            functions.add(builder);
            return this;
        }

        public ClassBuilder addFunction(PainlessLookup lookup, Location location,
                String returnCanonicalTypeName,
                String functionName,
                List<String> canonicalTypeNameParameters,
                List<String> parameterNames,
                boolean synthetic,
            FunctionBuilder.Build build) {

            FunctionBuilder builder = new FunctionBuilder(this.lookup, this.location,
                returnCanonicalTypeName,
                functionName,
                canonicalTypeNameParameters,
                parameterNames,
                synthetic);
            build.build(builder);
            functions.add(builder);
            return this;
        }

        public SSource build() {
            SSource ssource = new SSource(location,
                info,
                name,
                source,
                debug);
            return ssource;
        }
    }

    public final static class FunctionBuilder extends Builder {

        public interface Build {
            void build(FunctionBuilder fb);
        }

        protected String returnCanonicalTypeName;
        protected String functionName;
        protected List<String> canonicalTypeNameParameters;
        protected List<String> parameterNames;
        protected boolean synthetic;

        public FunctionBuilder(PainlessLookup lookup, Location location) {
            super(lookup, location);
        }

        public FunctionBuilder(PainlessLookup lookup, Location location,
                String returnCanonicalTypeName,
                String functionName,
                List<String> canonicalTypeNameParameters,
                List<String> parameterNames,
                boolean synthetic) {

            super(lookup, location);

            this.returnCanonicalTypeName = returnCanonicalTypeName;
            this.functionName = functionName;
            this.canonicalTypeNameParameters = canonicalTypeNameParameters;
            this.parameterNames = parameterNames;
            this.synthetic = synthetic;
        }

        public void setReturnCanonicalTypeName(String returnCanonicalTypeName) {
            this.returnCanonicalTypeName = returnCanonicalTypeName;
        }

        public void setFunctionName(String functionName) {
            this.functionName = functionName;
        }

        public void setCanonicalTypeNameParameters(List<String> canonicalTypeNameParameters) {
            this.canonicalTypeNameParameters = canonicalTypeNameParameters;
        }

        public void setParameterNames(List<String> parameterNames) {
            this.parameterNames = parameterNames;
        }

        public void setSynthetic(boolean synthetic) {
            this.synthetic = synthetic;
        }

        public SFunction build() {
            SFunction sfunction = new SFunction(location, 
                returnCanonicalTypeName,
                functionName,
                canonicalTypeNameParameters,
                parameterNames,
                synthetic);
            return sfunction;
        }
    }

    public static class StatementBuilder extends Builder {

        public interface Build {
            void build(StatementBuilder fb);
        }

        Builder statement;

        public StatementBuilder(PainlessLookup lookup, Location location) {
            super(lookup, location);
        }


    }
}
