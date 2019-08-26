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

import org.elasticsearch.painless.ScriptClassInfo;
import org.elasticsearch.painless.lookup.PainlessLookup;
import org.elasticsearch.painless.node.ANode;
import org.elasticsearch.painless.node.SDeclaration;
import org.elasticsearch.painless.node.SDo;
import org.elasticsearch.painless.node.SEach;
import org.elasticsearch.painless.node.SFor;
import org.elasticsearch.painless.node.SFunction;
import org.elasticsearch.painless.node.SIf;
import org.elasticsearch.painless.node.SIfElse;
import org.elasticsearch.painless.node.SSource;
import org.elasticsearch.painless.node.STry;
import org.elasticsearch.painless.node.SWhile;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class VariableTablePass {

    public interface Visitor {
        void visit(ANode node, PainlessLookup lookup, VariableTable table);
    }

    public interface End {
        void end(ANode node, PainlessLookup lookup, VariableTable table);
    }

    protected final PainlessLookup lookup;
    private final Map<Class<? extends ANode>, Visitor> visitors;
    private final Map<Class<? extends ANode>, End> ends;

    public VariableTablePass(PainlessLookup lookup) {
        this(lookup, Collections.emptyMap(), Collections.emptyMap());
    }

    public VariableTablePass(PainlessLookup lookup,
            Map<Class<? extends ANode>, Visitor> visitors,
            Map<Class<? extends ANode>, End> ends) {

        this.lookup = lookup;

        Map<Class<? extends ANode>, Visitor> baseVisitors = buildBaseVisitors();
        baseVisitors.putAll(visitors);
        this.visitors = Collections.unmodifiableMap(baseVisitors);

        Map<Class<? extends ANode>, End> baseEnds = buildBaseEnds();
        baseEnds.putAll(ends);
        this.ends = Collections.unmodifiableMap(baseEnds);
    }

    private Map<Class<? extends ANode>,Visitor> buildBaseVisitors() {
        Map<Class<? extends ANode>, Visitor> visitors = new HashMap<>();

        visitors.put(SSource.class, (node, lookup, table) -> {
            SSource source = (SSource)node;

            table.pushScope(true);

            for (ScriptClassInfo.MethodArgument arg : source.scriptClassInfo.getExecuteArguments()) {
                table.defineVariable(null, arg.getClazz(), arg.getName(), true);
            }
        });

        visitors.put(SFunction.class, (node, lookup, table) -> {
            SFunction function = (SFunction)node;

            table.pushScope(true);

            for (int index = 0; index < function.typeParameters.size(); ++index) {
                Class<?> type = function.typeParameters.get(index);
                String name = function.paramNameStrs.get(index);

                table.defineVariable(node, type, name, true);
            }
        });

        visitors.put(SFor.class, (node, lookup, table) -> table.pushScope(false));

        visitors.put(SEach.class, (node, lookup, table) -> table.pushScope(false));

        visitors.put(SWhile.class, (node, lookup, table) -> table.pushScope(false));

        visitors.put(SDo.class, (node, lookup, table) -> table.pushScope(false));

        visitors.put(SIf.class, (node, lookup, table) -> table.pushScope(false));

        visitors.put(SIfElse.class, (node, lookup, table) -> table.pushScope(false));

        visitors.put(STry.class, (node, lookup, table) -> table.pushScope(false));

        visitors.put(SDeclaration.class, (node, lookup, table) -> {
            SDeclaration declaration = (SDeclaration)node;
            table.defineVariable(node, lookup.canonicalTypeNameToType(declaration.type), declaration.name, true);
        });

        /*visitors.put(EVariable.class, (node, lookup, table) -> {
            EVariable variable = (EVariable)node;
            variable.
        })*/

        return visitors;
    }

    private Map<Class<? extends ANode>, End> buildBaseEnds() {
        Map<Class<? extends ANode>, End> ends = new HashMap<>();

        ends.put(SFunction.class, (node, lookup, table) -> {
            table.setNodeScope(node.parent);
        });

        ends.put(SFor.class, (node, lookup, table) -> table.popNodeScope());

        ends.put(SEach.class, (node, lookup, table) -> table.popNodeScope());

        ends.put(SWhile.class, (node, lookup, table) -> table.popNodeScope());

        ends.put(SDo.class, (node, lookup, table) -> table.popNodeScope());

        ends.put(SIf.class, (node, lookup, table) -> table.popNodeScope());

        ends.put(SIfElse.class, (node, lookup, table) -> table.popNodeScope());

        ends.put(STry.class, (node, lookup, table) -> table.popNodeScope());

        return ends;
    }

    public VariableTable visit(ANode root) {
        VariableTable table = new VariableTable();
        visit(root, table);

        return table;
    }

    protected void visit(ANode parent, VariableTable table) {
        table.putNodeScope(parent);

        Visitor visitor = visitors.get(parent.getClass());

        if (visitor != null) {
            visitor.visit(parent, lookup, table);
        }

        for (ANode child : parent.children) {
            if (child != null) {
                visit(child, table);
            }
        }

        End end = ends.get(parent.getClass());

        if (end != null) {
            end.end(parent, lookup, table);
        }
    }
}
