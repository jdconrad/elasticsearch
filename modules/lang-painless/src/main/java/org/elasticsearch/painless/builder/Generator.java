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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class Generator {

    protected static class BuilderInfo {
        public String className;
        public String nodeType;
        public List<String> types = new ArrayList<>();
        public List<String> names = new ArrayList<>();
        public List<String> children = new ArrayList<>();
    }

    protected static class ChildInfo {
        List<String> types = new ArrayList<>();
        List<String> names = new ArrayList<>();
    }

    public static void main(String[] args) throws IOException {
        List<BuilderInfo> infoList = new ArrayList<>();
        Map<String, BuilderInfo> infoMap = new HashMap<>();

        try (LineNumberReader reader = new LineNumberReader(
                new InputStreamReader(Generator.class.getResourceAsStream("builders"), StandardCharsets.UTF_8))) {

            int state = 0;
            BuilderInfo info = null;

            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                line = line.replaceAll("\\s+","");

                if (state == 0) {
                    if (line.isEmpty()) {
                        continue;
                    }

                    info = new BuilderInfo();
                    String[] split = line.split(":");
                    info.className = split[0];
                    info.nodeType = split[1];

                    state = 1;
                } else if (state == 1) {
                    if (line.equals("no arguments")) {
                        state = 2;
                        continue;
                    }

                    String[] split0 = line.split(",");
                    for (int index = 0; index < split0.length; ++index) {
                        String[] split1 = split0[index].split(":");
                        info.types.add(split1[0]);
                        info.names.add(split1[1]);
                    }

                    state = 2;
                } else {
                    if (line.isEmpty()) {
                        infoList.add(info);
                        infoMap.put(info.className, info);
                        info = null;
                        state = 0;
                    } else {
                        info.children.add(line);
                    }
                }
            }
        }

        StringBuilder builder = new StringBuilder();
        // header
        builder.append("/*\n");
        builder.append("* Licensed to Elasticsearch under one or more contributor\n");
        builder.append("* license agreements. See the NOTICE file distributed with\n");
        builder.append("* this work for additional information regarding copyright\n");
        builder.append("* ownership. Elasticsearch licenses this file to you under\n");
        builder.append("* the Apache License, Version 2.0 (the \"License\"); you may\n");
        builder.append("* not use this file except in compliance with the License.\n");
        builder.append("* You may obtain a copy of the License at\n");
        builder.append("*\n");
        builder.append("*    http://www.apache.org/licenses/LICENSE-2.0\n");
        builder.append("*\n");
        builder.append("* Unless required by applicable law or agreed to in writing,\n");
        builder.append("* software distributed under the License is distributed on an\n");
        builder.append("* \"AS IS\" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY\n");
        builder.append("* KIND, either express or implied.  See the License for the\n");
        builder.append("* specific language governing permissions and limitations\n");
        builder.append("* under the License.\n");
        builder.append("*/\n");
        builder.append("\n");
        builder.append("package org.elasticsearch.painless.builder;\n");
        builder.append("\n");
        builder.append("import org.elasticsearch.painless.*;\n");
        builder.append("import org.elasticsearch.painless.lookup.*;\n");
        builder.append("import org.elasticsearch.painless.node.*;\n");
        builder.append("import org.objectweb.asm.util.*;\n");
        builder.append("\n");
        builder.append("import java.util.*;\n");
        builder.append("\n");
        builder.append("/** AUTO-GENERATED CODE; DO NOT MODIFY */\n");

        // begin outer class
        builder.append("public abstract class Builder {\n");
        // begin member variables
        builder.append("\n");
        builder.append("    protected final PainlessLookup lookup;\n");
        builder.append("    protected final Location location;\n");
        // end member variables
        builder.append("\n");
        // begin constructor
        builder.append("    public Builder(PainlessLookup lookup, Location location) {\n");
        builder.append("        this.lookup = lookup;\n");
        builder.append("        this.location = location;\n");
        builder.append("    }\n");
        //end constructor

        for (BuilderInfo info : infoList) {
            // begin inner class
            builder.append("\n");
            builder.append("    public final static class ");
            builder.append(info.className);
            builder.append("Builder extends Builder {\n");
            builder.append("\n");

            // begin builder interface
            builder.append("        public interface Build {\n");
            builder.append("            void build(");
            builder.append(info.className);
            builder.append("Builder ");
            builder.append(info.className.toLowerCase(Locale.ROOT).charAt(0));
            builder.append("b);\n");
            builder.append("        }\n");
            builder.append("\n");
            // end builder interface

            // begin member variables
            for (int index = 0; index < info.names.size(); ++index) {
                builder.append("        protected ");
                builder.append(info.types.get(index));
                builder.append(" ");
                builder.append(info.names.get(index));
                builder.append(";\n");
            }
            // end member variables
            builder.append("\n");

            // begin empty constructor
            builder.append("        public ");
            builder.append(info.className);
            builder.append("Builder(PainlessLookup lookup, Location location) {\n");
            builder.append("            super(lookup, location);\n");
            builder.append("        }\n");
            builder.append("\n");
            // end empty constructor

            if (info.names.isEmpty() == false) {
                // begin parameterized constructor header
                builder.append("        public ");
                builder.append(info.className);
                builder.append("Builder(PainlessLookup lookup, Location location,\n");
                for (int index = 0; index < info.names.size(); ++index) {
                    builder.append("                ");
                    builder.append(info.types.get(index));
                    builder.append(" ");
                    builder.append(info.names.get(index));

                    if (index + 1 < info.names.size()) {
                        builder.append(",\n");
                    }
                }
                // end parameterized constructor header
                builder.append(") {\n");
                builder.append("\n");
                builder.append("            super(lookup, location);\n");
                builder.append("\n");
                // begin parameterized constructor body
                for (int index = 0; index < info.names.size(); ++index) {
                    builder.append("            this.");
                    builder.append(info.names.get(index));
                    builder.append(" = ");
                    builder.append(info.names.get(index));
                    builder.append(";\n");
                }
                // end parameterized constructor body
                builder.append("        }\n");
            }

            // begin parameter set methods
            for (int index = 0; index < info.names.size(); ++index) {
                builder.append("\n");
                // begin parameter set method header
                builder.append("        public void set");
                builder.append(info.names.get(index).toUpperCase(Locale.ROOT).charAt(0));
                builder.append(info.names.get(index).substring(1));
                builder.append("(");
                builder.append(info.types.get(index));
                builder.append(" ");
                builder.append(info.names.get(index));
                // end parameter set method header
                builder.append(") {\n");
                // begin parameter set method body
                builder.append("            this.");
                builder.append(info.names.get(index));
                builder.append(" = ");
                builder.append(info.names.get(index));
                builder.append(";\n");
                builder.append("        }\n");
                // end parameter set method body
            }
            // end parameter set methods

            // begin methods to add children
            for (String string : info.children) {
                builder.append("\n");

                if (string.startsWith("0*")) {
                    String[] split = string.split(":");
                    BuilderInfo child = infoMap.get(split[1]);

                    // begin add 0* list
                    builder.append("        protected final List<");
                    builder.append(child.className);
                    builder.append("Builder> ");
                    builder.append(child.className.toLowerCase(Locale.ROOT).charAt(0));
                    builder.append(child.className.substring(1));
                    builder.append("s = new ArrayList<>();\n");
                    builder.append("\n");
                    // end add 0* list

                    /*public void addFunction(FunctionBuilder.Build function) {
                        FunctionBuilder builder = new FunctionBuilder(this.lookup, this.location);
                        function.build(builder);
                        functions.add(builder);
                    }*/

                    // being add builder
                    builder.append("        public ");
                    builder.append(info.className);
                    builder.append("Builder add");
                    builder.append(child.className);
                    builder.append("(");
                    builder.append(child.className);
                    builder.append("Builder.Build build) {\n");
                    builder.append("            ");
                    builder.append(child.className);
                    builder.append("Builder builder = new ");
                    builder.append(child.className);
                    builder.append("Builder(this.lookup, this.location);\n");
                    builder.append("            build.build(builder);\n");
                    builder.append("            ");
                    builder.append(child.className.toLowerCase(Locale.ROOT).charAt(0));
                    builder.append(child.className.substring(1));
                    builder.append("s.add(builder);\n");
                    builder.append("            return this;\n");
                    builder.append("        }\n");
                    // end add builder

                    // begin add parameterized builder
                    builder.append("\n");
                    builder.append("        public ");
                    builder.append(info.className);
                    builder.append("Builder add");
                    builder.append(child.className);
                    builder.append("(PainlessLookup lookup, Location location,\n");
                    for (int index = 0; index < child.names.size(); ++index) {
                        builder.append("                ");
                        builder.append(child.types.get(index));
                        builder.append(" ");
                        builder.append(child.names.get(index));
                        builder.append(",\n");
                    }
                    builder.append("            ");
                    builder.append(child.className);
                    builder.append("Builder.Build build) {\n");
                    builder.append("\n");
                    builder.append("            ");
                    builder.append(child.className);
                    builder.append("Builder builder = new ");
                    builder.append(child.className);
                    builder.append("Builder(this.lookup, this.location,\n");
                    for (int index = 0; index < child.names.size(); ++index) {
                        builder.append("                ");
                        builder.append(child.names.get(index));

                        if (index + 1 < child.names.size()) {
                            builder.append(",\n");
                        }
                    }
                    builder.append(");\n");
                    builder.append("            build.build(builder);\n");
                    builder.append("            ");
                    builder.append(child.className.toLowerCase(Locale.ROOT).charAt(0));
                    builder.append(child.className.substring(1));
                    builder.append("s.add(builder);\n");
                    builder.append("            return this;\n");
                    builder.append("        }\n");
                    // end add parameterized builder
                }
            }
            // end methods to have children

            // begin build method
            builder.append("\n");
            builder.append("        public ");
            builder.append(info.nodeType);
            builder.append(" build() {\n");
            builder.append("            ");
            builder.append(info.nodeType);
            builder.append(" ");
            builder.append(info.nodeType.toLowerCase(Locale.ROOT));
            builder.append(" = new ");
            builder.append(info.nodeType);
            builder.append("(location, \n");
            for (int index = 0; index < info.names.size(); ++index) {
                builder.append("                ");
                builder.append(info.names.get(index));
                if (index + 1 < info.names.size()) {
                    builder.append(",\n");
                }
            }

            builder.append(");\n");
            builder.append("            return ");
            builder.append(info.nodeType.toLowerCase(Locale.ROOT));
            builder.append(";\n");
            builder.append("        }\n");
            // end build method

            // end inner class
            builder.append("    }\n");
        }

        // end outer class
        builder.append("}\n");

        String current = System.getProperty("user.dir");
        Files.write(Path.of(current + "/src/main/java/org/elasticsearch/painless/builder/Builder.java"),
                builder.toString().getBytes(Charset.defaultCharset()),
                StandardOpenOption.CREATE, StandardOpenOption.WRITE);
    }
}