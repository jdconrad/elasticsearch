/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.painless.phase;

import org.elasticsearch.core.Strings;
import org.elasticsearch.painless.ScriptClassInfo;
import org.elasticsearch.painless.ScriptClassInfo.MethodArgument;
import org.elasticsearch.painless.lookup.PainlessLookup;
import org.elasticsearch.painless.node.FunctionNode;
import org.elasticsearch.painless.symbol.FunctionTable;
import org.elasticsearch.painless.symbol.ScriptScope;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Resolves function type names and registers all functions in the {@link FunctionTable}
 * before semantic analysis runs. This phase only reads function signatures — it does
 * not recurse into function bodies.
 *
 * <p>For the special {@code execute} function the types are taken from
 * {@link ScriptClassInfo} (derived from the script interface) rather than from the
 * string type names written in source.
 */
public class SemanticHeaderPhase extends TreeTransformer {

    private final ScriptScope scriptScope;

    public SemanticHeaderPhase(NodeTraversals nodeTraversals, ScriptScope scriptScope) {
        super(nodeTraversals);
        this.scriptScope = scriptScope;

        on(FunctionNode.class, this::visitFunction);
    }

    private FunctionNode visitFunction(FunctionNode node) {
        if ("execute".equals(node.getName())) {
            return handleExecuteFunction(node);
        } else {
            return handleUserFunction(node);
        }
    }

    private FunctionNode handleExecuteFunction(FunctionNode node) {
        ScriptClassInfo scriptClassInfo = scriptScope.getScriptClassInfo();
        FunctionTable functionTable = scriptScope.getFunctionTable();
        String functionKey = FunctionTable.buildLocalFunctionKey(
            node.getName(), scriptClassInfo.getExecuteArguments().size()
        );

        if (functionTable.getFunction(functionKey) != null) {
            throw node.createError(
                new IllegalArgumentException(
                    "invalid function definition: found duplicate function [" + functionKey + "]."
                )
            );
        }

        Class<?> returnType = scriptClassInfo.getExecuteMethodReturnType();
        List<Class<?>> typeParameters = scriptClassInfo.getExecuteArguments()
            .stream()
            .map(MethodArgument::clazz)
            .collect(Collectors.toList());

        functionTable.addFunction(node.getName(), returnType, typeParameters, true, false);

        return node.withReturnType(returnType).withTypeParameters(typeParameters);
    }

    private FunctionNode handleUserFunction(FunctionNode node) {
        String functionName = node.getName();
        List<String> typeParameterNames = node.getTypeParameterNames();
        List<String> parameterNames = node.getParameterNames();
        int parameterCount = typeParameterNames.size();

        if (parameterCount != parameterNames.size()) {
            throw node.createError(
                new IllegalStateException(
                    Strings.format(
                        "invalid function definition: "
                            + "parameter types size [%d] is not equal to parameter names size [%d] for function [%s]",
                        typeParameterNames.size(),
                        parameterNames.size(),
                        functionName
                    )
                )
            );
        }

        FunctionTable functionTable = scriptScope.getFunctionTable();
        String functionKey = FunctionTable.buildLocalFunctionKey(functionName, parameterCount);

        if (functionTable.getFunction(functionKey) != null) {
            throw node.createError(
                new IllegalArgumentException(
                    "invalid function definition: found duplicate function [" + functionKey + "]."
                )
            );
        }

        PainlessLookup painlessLookup = scriptScope.getPainlessLookup();
        String returnTypeName = node.getReturnTypeName();
        Class<?> returnType = painlessLookup.canonicalTypeNameToType(returnTypeName);

        if (returnType == null) {
            throw node.createError(
                new IllegalArgumentException(
                    Strings.format(
                        "invalid function definition: return type [%s] not found for function [%s]",
                        returnTypeName,
                        functionKey
                    )
                )
            );
        }

        List<Class<?>> typeParameters = new ArrayList<>();

        for (String typeParamName : typeParameterNames) {
            Class<?> paramType = painlessLookup.canonicalTypeNameToType(typeParamName);

            if (paramType == null) {
                throw node.createError(
                    new IllegalArgumentException(
                        Strings.format(
                            "invalid function definition: parameter type [%s] not found for function [%s]",
                            typeParamName,
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
            node.isInternal(),
            node.isStatic()
        );

        return node.withReturnType(returnType).withTypeParameters(typeParameters);
    }
}
