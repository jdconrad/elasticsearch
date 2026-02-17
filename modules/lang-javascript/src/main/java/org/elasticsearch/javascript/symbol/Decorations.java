/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.javascript.symbol;

import org.elasticsearch.javascript.Def;
import org.elasticsearch.javascript.FunctionRef;
import org.elasticsearch.javascript.ir.IRNode;
import org.elasticsearch.javascript.lookup.JavascriptCast;
import org.elasticsearch.javascript.lookup.JavascriptClassBinding;
import org.elasticsearch.javascript.lookup.JavascriptConstructor;
import org.elasticsearch.javascript.lookup.JavascriptField;
import org.elasticsearch.javascript.lookup.JavascriptInstanceBinding;
import org.elasticsearch.javascript.lookup.JavascriptLookupUtility;
import org.elasticsearch.javascript.lookup.JavascriptMethod;
import org.elasticsearch.javascript.symbol.Decorator.Condition;
import org.elasticsearch.javascript.symbol.Decorator.Decoration;
import org.elasticsearch.javascript.symbol.FunctionTable.LocalFunction;
import org.elasticsearch.javascript.symbol.SemanticScope.Variable;

import java.util.List;
import java.util.Objects;

public class Decorations {

    // standard input for user statement nodes during semantic phase

    public interface LastSource extends Condition {

    }

    public interface BeginLoop extends Condition {

    }

    public interface InLoop extends Condition {

    }

    public interface LastLoop extends Condition {

    }

    // standard output for user statement nodes during semantic phase

    public interface MethodEscape extends Condition {

    }

    public interface LoopEscape extends Condition {

    }

    public interface AllEscape extends Condition {

    }

    public interface AnyContinue extends Condition {

    }

    public interface AnyBreak extends Condition {

    }

    // standard input for user expression nodes during semantic phase

    public interface Read extends Condition {

    }

    public interface Write extends Condition {

    }

    public record TargetType(Class<?> targetType) implements Decoration {

        public String getTargetCanonicalTypeName() {
            return JavascriptLookupUtility.typeToCanonicalTypeName(targetType);
        }
    }

    public interface Explicit extends Condition {

    }

    public interface Internal extends Condition {

    }

    // standard output for user expression node during semantic phase

    public record ValueType(Class<?> valueType) implements Decoration {

        public String getValueCanonicalTypeName() {
            return JavascriptLookupUtility.typeToCanonicalTypeName(valueType);
        }
    }

    public record StaticType(Class<?> staticType) implements Decoration {

        public String getStaticCanonicalTypeName() {
            return JavascriptLookupUtility.typeToCanonicalTypeName(staticType);
        }
    }

    public record PartialCanonicalTypeName(String partialCanonicalTypeName) implements Decoration {

    }

    public interface DefOptimized extends Condition {

    }

    // additional output acquired during the semantic process

    public interface ContinuousLoop extends Condition {

    }

    public interface Shortcut extends Condition {

    }

    public interface MapShortcut extends Condition {

    }

    public interface ListShortcut extends Condition {

    }

    public record ExpressionJavascriptCast(JavascriptCast expressionJavascriptCast) implements Decoration {}

    public record SemanticVariable(Variable semanticVariable) implements Decoration {}

    public record IterableJavascriptMethod(JavascriptMethod iterableJavascriptMethod) implements Decoration {}

    public record UnaryType(Class<?> unaryType) implements Decoration {

        public String getUnaryCanonicalTypeName() {
            return JavascriptLookupUtility.typeToCanonicalTypeName(unaryType);
        }
    }

    public record BinaryType(Class<?> binaryType) implements Decoration {

        public String getBinaryCanonicalTypeName() {
            return JavascriptLookupUtility.typeToCanonicalTypeName(binaryType);
        }
    }

    public record ShiftType(Class<?> shiftType) implements Decoration {

        public String getShiftCanonicalTypeName() {
            return JavascriptLookupUtility.typeToCanonicalTypeName(shiftType);
        }
    }

    public record ComparisonType(Class<?> comparisonType) implements Decoration {

        public String getComparisonCanonicalTypeName() {
            return JavascriptLookupUtility.typeToCanonicalTypeName(comparisonType);
        }
    }

    public record CompoundType(Class<?> compoundType) implements Decoration {

        public String getCompoundCanonicalTypeName() {
            return JavascriptLookupUtility.typeToCanonicalTypeName(compoundType);
        }
    }

    public record UpcastJavascriptCast(JavascriptCast upcastJavascriptCast) implements Decoration {}

    public record DowncastJavascriptCast(JavascriptCast downcastJavascriptCast) implements Decoration {

        public DowncastJavascriptCast(JavascriptCast downcastJavascriptCast) {
            this.downcastJavascriptCast = Objects.requireNonNull(downcastJavascriptCast);
        }
    }

    public record StandardJavascriptField(JavascriptField standardJavascriptField) implements Decoration {}

    public record StandardJavascriptConstructor(JavascriptConstructor standardJavascriptConstructor) implements Decoration {}

    public record StandardJavascriptMethod(JavascriptMethod standardJavascriptMethod) implements Decoration {}

    public interface DynamicInvocation extends Condition {}

    public record GetterJavascriptMethod(JavascriptMethod getterJavascriptMethod) implements Decoration {}

    public record SetterJavascriptMethod(JavascriptMethod setterJavascriptMethod) implements Decoration {}

    public record StandardConstant(Object standardConstant) implements Decoration {}

    public record StandardLocalFunction(LocalFunction localFunction) implements Decoration {}

    public record ThisJavascriptMethod(JavascriptMethod thisJavascriptMethod) implements Decoration {}

    public record StandardJavascriptClassBinding(JavascriptClassBinding javascriptClassBinding) implements Decoration {}

    public record StandardJavascriptInstanceBinding(JavascriptInstanceBinding javascriptInstanceBinding) implements Decoration {

    }

    public record MethodNameDecoration(String methodName) implements Decoration {

    }

    public record ReturnType(Class<?> returnType) implements Decoration {

        public String getReturnCanonicalTypeName() {
            return JavascriptLookupUtility.typeToCanonicalTypeName(returnType);
        }
    }

    public record TypeParameters(List<Class<?>> typeParameters) implements Decoration {

        public TypeParameters(List<Class<?>> typeParameters) {
            this.typeParameters = List.copyOf(typeParameters);
        }
    }

    public record ParameterNames(List<String> parameterNames) implements Decoration {

        public ParameterNames(List<String> parameterNames) {
            this.parameterNames = List.copyOf(parameterNames);
        }
    }

    public record ReferenceDecoration(FunctionRef reference) implements Decoration {}

    public record EncodingDecoration(Def.Encoding encoding) implements Decoration {

        public static EncodingDecoration of(boolean isStatic, boolean needsInstance, String symbol, String methodName, int captures) {
            return new EncodingDecoration(new Def.Encoding(isStatic, needsInstance, symbol, methodName, captures));
        }
    }

    public record CapturesDecoration(List<Variable> captures) implements Decoration {

        public CapturesDecoration(List<Variable> captures) {
            this.captures = List.copyOf(captures);
        }

        public List<Variable> captures() {
            return captures;
        }
    }

    public interface CaptureBox extends Condition {

    }

    public record InstanceType(Class<?> instanceType) implements Decoration {

        public String getInstanceCanonicalTypeName() {
            return JavascriptLookupUtility.typeToCanonicalTypeName(instanceType);
        }
    }

    public interface Negate extends Condition {

    }

    public interface Compound extends Condition {

    }

    public record AccessDepth(int accessDepth) implements Decoration {}

    // standard output for user tree to ir tree phase

    public record IRNodeDecoration(IRNode irNode) implements Decoration {}

    public record Converter(LocalFunction converter) implements Decoration {}

    // collect additional information about where doc is used

    public interface IsDocument extends Condition {

    }

    // Does the lambda need to capture the enclosing instance?
    public interface InstanceCapturingLambda extends Condition {

    }

    // Does the function reference need to capture the enclosing instance?
    public interface InstanceCapturingFunctionRef extends Condition {

    }
}
