/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.painless.node;

import org.elasticsearch.painless.Location;
import org.elasticsearch.painless.lookup.PainlessClassBinding;
import org.elasticsearch.painless.lookup.PainlessInstanceBinding;
import org.elasticsearch.painless.lookup.PainlessMethod;
import org.elasticsearch.painless.symbol.FunctionTable.LocalFunction;

import java.util.List;

public final class InvokeCallMemberNode extends ArgumentsExpressionNode {

    private final LocalFunction localFunction;
    private final PainlessMethod thisMethod;
    private final PainlessMethod importedMethod;
    private final PainlessClassBinding classBinding;
    private final PainlessInstanceBinding instanceBinding;

    public InvokeCallMemberNode(Location location, List<ExpressionNode> argumentNodes,
                                LocalFunction localFunction, PainlessMethod thisMethod,
                                PainlessMethod importedMethod, PainlessClassBinding classBinding,
                                PainlessInstanceBinding instanceBinding, Class<?> expressionType) {
        super(location, argumentNodes, expressionType);
        this.localFunction = localFunction;
        this.thisMethod = thisMethod;
        this.importedMethod = importedMethod;
        this.classBinding = classBinding;
        this.instanceBinding = instanceBinding;
    }

    public LocalFunction getLocalFunction() { return localFunction; }
    public PainlessMethod getThisMethod() { return thisMethod; }
    public PainlessMethod getImportedMethod() { return importedMethod; }
    public PainlessClassBinding getClassBinding() { return classBinding; }
    public PainlessInstanceBinding getInstanceBinding() { return instanceBinding; }

    public InvokeCallMemberNode withArgumentNodes(List<ExpressionNode> argumentNodes) {
        return new InvokeCallMemberNode(getLocation(), argumentNodes, localFunction, thisMethod,
            importedMethod, classBinding, instanceBinding, getExpressionType());
    }

    public InvokeCallMemberNode withExpressionType(Class<?> expressionType) {
        return new InvokeCallMemberNode(getLocation(), getArgumentNodes(), localFunction, thisMethod,
            importedMethod, classBinding, instanceBinding, expressionType);
    }

    public InvokeCallMemberNode withLocalFunction(LocalFunction localFunction) {
        return new InvokeCallMemberNode(getLocation(), getArgumentNodes(), localFunction, thisMethod,
            importedMethod, classBinding, instanceBinding, getExpressionType());
    }

    public InvokeCallMemberNode withThisMethod(PainlessMethod thisMethod) {
        return new InvokeCallMemberNode(getLocation(), getArgumentNodes(), localFunction, thisMethod,
            importedMethod, classBinding, instanceBinding, getExpressionType());
    }

    public InvokeCallMemberNode withImportedMethod(PainlessMethod importedMethod) {
        return new InvokeCallMemberNode(getLocation(), getArgumentNodes(), localFunction, thisMethod,
            importedMethod, classBinding, instanceBinding, getExpressionType());
    }

    public InvokeCallMemberNode withClassBinding(PainlessClassBinding classBinding) {
        return new InvokeCallMemberNode(getLocation(), getArgumentNodes(), localFunction, thisMethod,
            importedMethod, classBinding, instanceBinding, getExpressionType());
    }

    public InvokeCallMemberNode withInstanceBinding(PainlessInstanceBinding instanceBinding) {
        return new InvokeCallMemberNode(getLocation(), getArgumentNodes(), localFunction, thisMethod,
            importedMethod, classBinding, instanceBinding, getExpressionType());
    }
}
