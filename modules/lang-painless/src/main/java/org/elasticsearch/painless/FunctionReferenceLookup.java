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

package org.elasticsearch.painless;

import org.elasticsearch.painless.Locals.LocalMethod;
import org.elasticsearch.painless.lookup.PainlessClass;
import org.elasticsearch.painless.lookup.PainlessConstructor;
import org.elasticsearch.painless.lookup.PainlessLookup;
import org.elasticsearch.painless.lookup.PainlessLookupUtility;
import org.elasticsearch.painless.lookup.PainlessMethod;

import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.function.Function;

import static org.elasticsearch.painless.WriterConstants.CLASS_NAME;
import static org.objectweb.asm.Opcodes.H_INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.H_INVOKESTATIC;
import static org.objectweb.asm.Opcodes.H_INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.H_NEWINVOKESPECIAL;

public class FunctionReferenceLookup {

    /**
     * Creates a new FunctionReference which will resolve {@code type::call} from the whitelist.
     * @param painlessLookup the whitelist against which this script is being compiled
     * @param locals
     * @param targetClass functional interface type to implement.
     * @param typeName the left hand side of a method reference expression
     * @param methodName the right hand side of a method reference expression
     * @param numCaptures number of captured arguments
     */
    public static FunctionReference lookup(PainlessLookup painlessLookup, Locals locals,
            Class<?> targetClass, String typeName, String methodName, int numCaptures) {

        Objects.requireNonNull(painlessLookup);
        Objects.requireNonNull(targetClass);
        Objects.requireNonNull(typeName);
        Objects.requireNonNull(methodName);

        String targetClassName = PainlessLookupUtility.typeToCanonicalTypeName(targetClass);
        PainlessMethod interfaceMethod;

        try {
            interfaceMethod = painlessLookup.lookupFunctionalInterfacePainlessMethod(targetClass);
        } catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException("cannot convert function reference [" + typeName + "::" +  methodName + "] " +
                    "to a non-functional interface [" + targetClassName + "]", iae);
        }

        int typeParametersSize = interfaceMethod.typeParameters.size();

        String interfaceMethodName = interfaceMethod.javaMethod.getName();
        MethodType interfaceMethodType = interfaceMethod.methodType.dropParameterTypes(0, 1);
        String delegateClassName;
        boolean isDelegateInterface;
        int delegateInvokeType;
        String delegateMethodName;
        MethodType delegateMethodType;

        if ("this".equals(typeName))
            Objects.requireNonNull(locals);
        if ("new".equals(methodName)) {
            PainlessConstructor painlessConstructor;

            try {
                painlessConstructor = painlessLookup.lookupPainlessConstructor(typeName, typeParametersSize);
            } catch (IllegalArgumentException iae) {
                throw new IllegalArgumentException(
                        "function reference [" + typeName + "::new] matching [" + targetClassName + "] not found", iae);
            }

            delegateClassName = painlessConstructor.javaConstructor.getDeclaringClass().getName();
            isDelegateInterface = false;
            delegateInvokeType = H_NEWINVOKESPECIAL;
            delegateMethodName = PainlessLookupUtility.CONSTRUCTOR_NAME;
            delegateMethodType = painlessConstructor.methodType.dropParameterTypes(0, numCaptures);
        } else {
            PainlessMethod painlessMethod;

            try {
                painlessMethod = painlessLookup.lookupPainlessMethod(targetClassName, true, methodName, typeParametersSize);
            } catch (IllegalArgumentException staticIAE) {
                try {
                    painlessMethod = painlessLookup.lookupPainlessMethod(
                            targetClassName, false, methodName, numCaptures > 0 ? typeParametersSize : typeParametersSize - 1);
                } catch (IllegalArgumentException iae) {
                    throw new IllegalArgumentException(
                            "function reference [" + typeName + "::" + methodName + "] matching [" + targetClassName + "] not found", iae);
                }
            }

            delegateClassName = painlessMethod.javaMethod.getDeclaringClass().getName();
            isDelegateInterface = painlessMethod.javaMethod.getDeclaringClass().isInterface();

            if (Modifier.isStatic(painlessMethod.javaMethod.getModifiers())) {
                delegateInvokeType = H_INVOKESTATIC;
            } else if (isDelegateInterface) {
                delegateInvokeType = H_INVOKEINTERFACE;
            } else {
                delegateInvokeType = H_INVOKEVIRTUAL;
            }

            delegateMethodName = painlessMethod.javaMethod.getName();
            delegateMethodType = painlessMethod.methodType.dropParameterTypes(0, numCaptures);
        }

        MethodType factoryMethodType = MethodType.methodType(targetClass,
                delegateMethodType.dropParameterTypes(numCaptures, delegateMethodType.parameterCount()));

        return new FunctionReference(interfaceMethodName, interfaceMethodType,
                delegateClassName, isDelegateInterface, delegateInvokeType, delegateMethodName, delegateMethodType,
                factoryMethodType
        );
    }

    /**
     * Creates a new FunctionRef (already resolved)
     * @param expected functional interface type to implement
     * @param interfaceMethod functional interface method
     * @param delegateMethod implementation method
     * @param numCaptures number of captured arguments
     */
    public FunctionReferenceLookup(Class<?> expected, PainlessMethod interfaceMethod, LocalMethod delegateMethod, int numCaptures) {
        MethodType delegateMethodType = delegateMethod.methodType;

        this.interfaceMethodName = interfaceMethod.javaMethod.getName();
        this.factoryMethodType = MethodType.methodType(expected,
                delegateMethodType.dropParameterTypes(numCaptures, delegateMethodType.parameterCount()));
        this.interfaceMethodType = interfaceMethod.methodType.dropParameterTypes(0, 1);

        this.delegateClassName = CLASS_NAME;
        this.isDelegateInterface = false;
        this.delegateInvokeType = H_INVOKESTATIC;
        this.delegateMethodName = delegateMethod.name;
        this.delegateMethodType = delegateMethodType.dropParameterTypes(0, numCaptures);
    }

    /**
     * Creates a new FunctionRef (low level).
     * It is for runtime use only.
     */
    public FunctionReferenceLookup(Class<?> expected,
            PainlessMethod interfaceMethod, String delegateMethodName, MethodType delegateMethodType, int numCaptures) {
        this.interfaceMethodName = interfaceMethod.javaMethod.getName();
        this.factoryMethodType = MethodType.methodType(expected,
                delegateMethodType.dropParameterTypes(numCaptures, delegateMethodType.parameterCount()));
        this.interfaceMethodType = interfaceMethod.methodType.dropParameterTypes(0, 1);

        this.delegateClassName = CLASS_NAME;
        this.delegateInvokeType = H_INVOKESTATIC;
        this.delegateMethodName = delegateMethodName;
        this.delegateMethodType = delegateMethodType.dropParameterTypes(0, numCaptures);
        this.isDelegateInterface = false;
    }
}
