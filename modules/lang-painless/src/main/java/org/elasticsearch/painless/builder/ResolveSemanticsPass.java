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

import org.elasticsearch.painless.node.ANode;
import org.elasticsearch.painless.node.EAssignment;
import org.elasticsearch.painless.node.EBinary;
import org.elasticsearch.painless.node.EBool;
import org.elasticsearch.painless.node.EBoolean;
import org.elasticsearch.painless.node.ECallLocal;
import org.elasticsearch.painless.node.ECapturingFunctionRef;
import org.elasticsearch.painless.node.EComp;
import org.elasticsearch.painless.node.EConditional;
import org.elasticsearch.painless.node.EConstant;
import org.elasticsearch.painless.node.EDecimal;
import org.elasticsearch.painless.node.EDirectCallInvoke;
import org.elasticsearch.painless.node.EDirectFieldAccess;
import org.elasticsearch.painless.node.EElvis;
import org.elasticsearch.painless.node.EExplicit;
import org.elasticsearch.painless.node.EFunctionRef;
import org.elasticsearch.painless.node.EInstanceof;
import org.elasticsearch.painless.node.ELambda;
import org.elasticsearch.painless.node.EListInit;
import org.elasticsearch.painless.node.EMapInit;
import org.elasticsearch.painless.node.ENewArray;
import org.elasticsearch.painless.node.ENewArrayFunctionRef;
import org.elasticsearch.painless.node.ENewObj;
import org.elasticsearch.painless.node.ENull;
import org.elasticsearch.painless.node.ENumeric;
import org.elasticsearch.painless.node.ERegex;
import org.elasticsearch.painless.node.EStatic;
import org.elasticsearch.painless.node.EString;
import org.elasticsearch.painless.node.EThis;
import org.elasticsearch.painless.node.EUnary;
import org.elasticsearch.painless.node.EUsed;
import org.elasticsearch.painless.node.EVariable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ResolveSemanticsPass implements SemanticPass {

    public interface Visitor {
        void visit(ANode node, SymbolTable table, Map<String, Object> data);
    }

    public interface Between {
        void visit(ANode node, ANode child, int index, SymbolTable table, Map<String, Object> data);
    }

    private final Map<Class<? extends ANode>, Visitor> enters;
    private final Map<Class<? extends ANode>, Between> befores;
    private final Map<Class<? extends ANode>, Between> afters;
    private final Map<Class<? extends ANode>, Visitor> exits;

    public ResolveSemanticsPass() {
        this(Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap());
    }

    public ResolveSemanticsPass(
            Map<Class<? extends ANode>, Visitor> enters,
            Map<Class<? extends ANode>, Between> befores,
            Map<Class<? extends ANode>, Between> afters,
            Map<Class<? extends ANode>, Visitor> exits) {

        Map<Class<? extends ANode>, Visitor> baseEnters = buildBaseEnters();
        baseEnters.putAll(enters);
        this.enters = Collections.unmodifiableMap(baseEnters);

        Map<Class<? extends ANode>, Between> baseBefores = buildBaseBefores();
        baseBefores.putAll(befores);
        this.befores = Collections.unmodifiableMap(baseBefores);

        Map<Class<? extends ANode>, Between> baseAfters = buildBaseAfters();
        baseAfters.putAll(afters);
        this.afters = Collections.unmodifiableMap(baseAfters);

        Map<Class<? extends ANode>, Visitor> baseExits = buildBaseExits();
        baseExits.putAll(exits);
        this.exits = Collections.unmodifiableMap(baseExits);
    }

    protected Map<Class<? extends ANode>, Visitor> buildBaseEnters() {
        Map<Class<? extends ANode>, Visitor> baseEnters = new HashMap<>();

        baseEnters.put( EAssignment.class          , EAssignment::enter          );
        baseEnters.put( EBinary.class              , EBinary::enter              );
        baseEnters.put( EBool.class                , EBool::enter                );
        baseEnters.put( EBoolean.class             , EBoolean::enter             );
        baseEnters.put( ECallLocal.class           , ECallLocal::enter           );
        baseEnters.put( EConditional.class         , EConditional::enter         );
        baseEnters.put( EDecimal.class             , EDecimal::enter             );
        baseEnters.put( EElvis.class               , EElvis::enter               );
        baseEnters.put( EExplicit.class            , EExplicit::enter            );
        baseEnters.put( EInstanceof.class          , EInstanceof::enter          );
        baseEnters.put( ELambda.class              , ELambda::enter              );
        baseEnters.put( EListInit.class            , EListInit::enter            );
        baseEnters.put( EMapInit.class             , EMapInit::enter             );
        baseEnters.put( ENewArray.class            , ENewArray::enter            );
        baseEnters.put( ENewArrayFunctionRef.class , ENewArrayFunctionRef::enter );
        baseEnters.put( ENewObj.class              , ENewObj::enter              );
        baseEnters.put( ENull.class                , ENull::enter                );
        baseEnters.put( ENumeric.class             , ENumeric::enter             );
        baseEnters.put( ERegex.class               , ERegex::enter               );
        baseEnters.put( EStatic.class              , EStatic::enter              );
        baseEnters.put( EString.class              , EString::enter              );
        baseEnters.put( EThis.class                , EThis::enter                );
        baseEnters.put( EUnary.class               , EUnary::enter               );
        baseEnters.put( EUsed.class                , EUsed::enter                );
        baseEnters.put( EVariable.class            , EVariable::enter            );

        return baseEnters;
    }

    protected Map<Class<? extends ANode>, Between> buildBaseBefores() {
        Map<Class<? extends ANode>, Between> baseBefores = new HashMap<>();

        baseBefores.put( EAssignment.class       , EAssignment::before       );
        baseBefores.put( ECallLocal.class        , ECallLocal::before        );
        baseBefores.put( EDirectCallInvoke.class , EDirectCallInvoke::before );
        baseBefores.put( EListInit.class         , EListInit::before         );
        baseBefores.put( EMapInit.class          , EMapInit::before          );
        baseBefores.put( ENewArray.class         , ENewArray::before         );
        baseBefores.put( ENewObj.class           , ENewObj::before           );

        return baseBefores;
    }

    protected Map<Class<? extends ANode>, Between> buildBaseAfters() {
        Map<Class<? extends ANode>, Between> baseAfters = new HashMap<>();

        baseAfters.put( ECallLocal.class        , ECallLocal::after        );
        baseAfters.put( EDirectCallInvoke.class , EDirectCallInvoke::after );
        baseAfters.put( EListInit.class         , EListInit::after         );
        baseAfters.put( EMapInit.class          , EMapInit::after          );
        baseAfters.put( ENewArray.class         , ENewArray::after         );
        baseAfters.put( ENewObj.class           , ENewObj::after           );

        return baseAfters;
    }

    protected Map<Class<? extends ANode>, Visitor> buildBaseExits() {
        Map<Class<? extends ANode>, Visitor> baseExits = new HashMap<>();

        baseExits.put( EAssignment.class           , EAssignment::exit           );
        baseExits.put( EBinary.class               , EBinary::exit               );
        baseExits.put( EBool.class                 , EBool::exit                 );
        baseExits.put( ECapturingFunctionRef.class , ECapturingFunctionRef::exit );
        baseExits.put( EComp.class                 , EComp::exit                 );
        baseExits.put( EConditional.class          , EConditional::exit          );
        baseExits.put( EConstant.class             , EConstant::exit             );
        baseExits.put( EDirectCallInvoke.class     , EDirectCallInvoke::exit     );
        baseExits.put( EDirectFieldAccess.class    , EDirectFieldAccess::exit    );
        baseExits.put( EElvis.class                , EElvis::exit                );
        baseExits.put( EExplicit.class             , EExplicit::exit             );
        baseExits.put( EFunctionRef.class          , EFunctionRef::exit          );
        baseExits.put( EInstanceof.class           , EInstanceof::exit           );
        baseExits.put( EUnary.class                , EUnary::exit                );

        return baseExits;
    }

    @Override
    public void pass(ANode root, SymbolTable table, Map<String, Object> data) {
        visit(root, table, data);
    }

    protected void visit(ANode node, SymbolTable table, Map<String, Object> data) {
        Visitor enter = enters.get(node.getClass());

        if (enter != null) {
            enter.visit(node, table, data);
        }

        int index = 0;
        Between before = befores.get(node.getClass());
        Between after = afters.get(node.getClass());

        for (ANode child : node.children) {
            if (before != null) {
                before.visit(node, child, index, table, data);
            }

            if (child != null) {
                visit(child, table, data);
            }

            if (after != null) {
                after.visit(node, child, index, table, data);
            }

            ++index;
        }

        Visitor exit = exits.get(node.getClass());

        if (exit != null) {
            exit.visit(node, table, data);
        }
    }
}
