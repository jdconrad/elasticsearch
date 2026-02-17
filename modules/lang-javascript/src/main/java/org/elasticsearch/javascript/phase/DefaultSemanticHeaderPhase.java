/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.javascript.phase;

import org.elasticsearch.core.Strings;
import org.elasticsearch.javascript.lookup.JavascriptLookup;
import org.elasticsearch.javascript.node.SClass;
import org.elasticsearch.javascript.node.SFunction;
import org.elasticsearch.javascript.symbol.FunctionTable;
import org.elasticsearch.javascript.symbol.ScriptScope;

import java.util.ArrayList;
import java.util.List;

public class DefaultSemanticHeaderPhase extends UserTreeBaseVisitor<ScriptScope> {

    @Override
    public void visitClass(SClass userClassNode, ScriptScope scriptScope) {
        for (SFunction userFunctionNode : userClassNode.getFunctionNodes()) {
            visitFunction(userFunctionNode, scriptScope);
        }
    }

    @Override
    public void visitFunction(SFunction userFunctionNode, ScriptScope scriptScope) {
        String functionName = userFunctionNode.getFunctionName();
        List<String> canonicalTypeNameParameters = userFunctionNode.getCanonicalTypeNameParameters();
        List<String> parameterNames = userFunctionNode.getParameterNames();
        int parameterCount = canonicalTypeNameParameters.size();

        if (parameterCount != parameterNames.size()) {
            throw userFunctionNode.createError(
                new IllegalStateException(
                    Strings.format(
                        "invalid function definition: "
                            + "parameter types size [%d] is not equal to parameter names size [%d] for function [%s]",
                        canonicalTypeNameParameters.size(),
                        parameterNames.size(),
                        functionName
                    )
                )
            );
        }

        FunctionTable functionTable = scriptScope.getFunctionTable();
        String functionKey = FunctionTable.buildLocalFunctionKey(functionName, canonicalTypeNameParameters.size());

        if (functionTable.getFunction(functionKey) != null) {
            throw userFunctionNode.createError(
                new IllegalArgumentException("invalid function definition: " + "found duplicate function [" + functionKey + "].")
            );
        }

        JavascriptLookup javascriptLookup = scriptScope.getJavascriptLookup();
        String returnCanonicalTypeName = userFunctionNode.getReturnCanonicalTypeName();
        Class<?> returnType = javascriptLookup.canonicalTypeNameToType(returnCanonicalTypeName);

        if (returnType == null) {
            throw userFunctionNode.createError(
                new IllegalArgumentException(
                    Strings.format(
                        "invalid function definition: return type [%s] not found for function [%s]",
                        returnCanonicalTypeName,
                        functionKey
                    )
                )
            );
        }

        List<Class<?>> typeParameters = new ArrayList<>();

        for (String typeParameter : canonicalTypeNameParameters) {
            Class<?> paramType = javascriptLookup.canonicalTypeNameToType(typeParameter);

            if (paramType == null) {
                throw userFunctionNode.createError(
                    new IllegalArgumentException(
                        Strings.format(
                            "invalid function definition: parameter type [%s] not found for function [%s]",
                            typeParameter,
                            functionKey
                        )
                    )
                );
            }

            typeParameters.add(paramType);
        }

        functionTable.addMangledFunction(
            functionName,
            returnType,
            typeParameters,
            userFunctionNode.isInternal(),
            userFunctionNode.isStatic()
        );
    }
}
