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

package org.elasticsearch.painless.node;

import org.elasticsearch.painless.AnalyzerCaster;
import org.elasticsearch.painless.Location;
import org.elasticsearch.painless.Scope;
import org.elasticsearch.painless.ir.AccessNode;
import org.elasticsearch.painless.ir.ArrayLengthAccessNode;
import org.elasticsearch.painless.ir.ClassNode;
import org.elasticsearch.painless.ir.ExpressionNode;
import org.elasticsearch.painless.ir.LoadDefDotNode;
import org.elasticsearch.painless.ir.LoadFieldNode;
import org.elasticsearch.painless.ir.LoadListShortcutNode;
import org.elasticsearch.painless.ir.LoadMapShortcutNode;
import org.elasticsearch.painless.ir.LoadShortcutNode;
import org.elasticsearch.painless.ir.NullSafeSubNode;
import org.elasticsearch.painless.ir.StaticNode;
import org.elasticsearch.painless.ir.StoreDefDotNode;
import org.elasticsearch.painless.ir.StoreFieldNode;
import org.elasticsearch.painless.ir.StoreListShortcutNode;
import org.elasticsearch.painless.ir.StoreMapShortcutNode;
import org.elasticsearch.painless.ir.StoreShortcutNode;
import org.elasticsearch.painless.lookup.PainlessCast;
import org.elasticsearch.painless.lookup.PainlessField;
import org.elasticsearch.painless.lookup.PainlessLookupUtility;
import org.elasticsearch.painless.lookup.PainlessMethod;
import org.elasticsearch.painless.lookup.def;
import org.elasticsearch.painless.symbol.ScriptRoot;

import java.lang.reflect.Modifier;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.elasticsearch.painless.lookup.PainlessLookupUtility.typeToCanonicalTypeName;

/**
 * Represents a field load/store and defers to a child subnode.
 */
public class EDot extends AExpression {

    protected final AExpression prefix;
    protected final boolean nullSafe;
    protected final String value;

    public EDot(Location location, AExpression prefix, boolean nullSafe, String value) {
        super(location);

        this.prefix = Objects.requireNonNull(prefix);
        this.nullSafe = nullSafe;
        this.value = Objects.requireNonNull(value);
    }

    @Override
    Output analyze(ClassNode classNode, ScriptRoot scriptRoot, Scope scope, Input input) {
        if (input.read == false && input.write == false) {
            throw createError(new IllegalArgumentException("not a statement: result of dot operator [.] not used"));
        }

        Output output = new Output();
        Output prefixOutput = prefix.analyze(classNode, scriptRoot, scope, new Input());

        if (prefixOutput.partialCanonicalTypeName != null) {
            if (output.isStaticType) {
                throw createError(new IllegalArgumentException("value required: " +
                        "instead found unexpected type [" + PainlessLookupUtility.typeToCanonicalTypeName(output.actual) + "]"));
            }

            String canonicalTypeName = prefixOutput.partialCanonicalTypeName + "." + value;
            Class<?> type = scriptRoot.getPainlessLookup().canonicalTypeNameToType(canonicalTypeName);

            if (type == null) {
                output.partialCanonicalTypeName = canonicalTypeName;
            } else {
                if (input.write) {
                    throw createError(new IllegalArgumentException("invalid assignment: " +
                            "cannot write a value to a static type [" + PainlessLookupUtility.typeToCanonicalTypeName(type) + "]"));
                }

                if (input.read == false) {
                    throw createError(new IllegalArgumentException(
                            "not a statement: static type [" + PainlessLookupUtility.typeToCanonicalTypeName(type) + "] not used"));
                }

                output.actual = type;
                output.isStaticType = true;

                StaticNode staticNode = new StaticNode();

                staticNode.setLocation(location);
                staticNode.setExpressionType(output.actual);

                output.expressionNode = staticNode;
            }
        } else {
            Class<?> targetType = prefixOutput.actual;
            String targetCanonicalTypeName = PainlessLookupUtility.typeToCanonicalTypeName(targetType);

            ExpressionNode expressionNode = null;

            if (prefixOutput.actual.isArray()) {
                if (output.isStaticType) {
                    throw createError(new IllegalArgumentException("value required: " +
                            "instead found unexpected type [" + PainlessLookupUtility.typeToCanonicalTypeName(output.actual) + "]"));
                }

                if ("length".equals(value)) {
                    if (input.write) {
                        throw createError(new IllegalArgumentException(
                                "invalid assignment: cannot assign a value write to read-only field [length] for an array."));
                    }

                    output.actual = int.class;
                } else {
                    throw createError(new IllegalArgumentException(
                            "Field [" + value + "] does not exist for type [" + targetCanonicalTypeName + "]."));
                }

                ArrayLengthAccessNode arrayLengthNode = new ArrayLengthAccessNode();
                arrayLengthNode.setLocation(location);
                arrayLengthNode.setExpressionType(output.actual);
                expressionNode = arrayLengthNode;
            } else if (prefixOutput.actual == def.class) {
                if (output.isStaticType) {
                    throw createError(new IllegalArgumentException("value required: " +
                            "instead found unexpected type [" + PainlessLookupUtility.typeToCanonicalTypeName(output.actual) + "]"));
                }

                // TODO: remove ZonedDateTime exception when JodaCompatibleDateTime is removed
                output.actual =
                        input.expected == null || input.expected == ZonedDateTime.class || input.explicit ? def.class : input.expected;
                output.isDefOptimized = true;

                if (input.write) {
                    StoreDefDotNode storeDefDotNode = new StoreDefDotNode();
                    storeDefDotNode.setLocation(location);
                    storeDefDotNode.setExpressionType(input.read ? output.actual : void.class);
                    storeDefDotNode.setValue(value);
                    expressionNode = storeDefDotNode;
                } else {
                    LoadDefDotNode loadDefDotNode = new LoadDefDotNode();
                    loadDefDotNode.setLocation(location);
                    loadDefDotNode.setExpressionType(output.actual);
                    loadDefDotNode.setValue(value);
                    expressionNode = loadDefDotNode;
                }
            } else {
                PainlessField field =
                        scriptRoot.getPainlessLookup().lookupPainlessField(prefixOutput.actual, prefixOutput.isStaticType, value);

                if (field == null) {
                    PainlessMethod getter;
                    PainlessMethod setter;

                    getter = scriptRoot.getPainlessLookup().lookupPainlessMethod(prefixOutput.actual, false,
                            "get" + Character.toUpperCase(value.charAt(0)) + value.substring(1), 0);

                    if (getter == null) {
                        getter = scriptRoot.getPainlessLookup().lookupPainlessMethod(prefixOutput.actual, false,
                                "is" + Character.toUpperCase(value.charAt(0)) + value.substring(1), 0);
                    }

                    setter = scriptRoot.getPainlessLookup().lookupPainlessMethod(prefixOutput.actual, false,
                            "set" + Character.toUpperCase(value.charAt(0)) + value.substring(1), 0);

                    if (getter != null || setter != null) {
                        if (getter != null && (getter.returnType == void.class || !getter.typeParameters.isEmpty())) {
                            throw createError(new IllegalArgumentException(
                                    "Illegal get shortcut on field [" + value + "] for type [" + targetCanonicalTypeName + "]."));
                        }

                        if (setter != null && (setter.returnType != void.class || setter.typeParameters.size() != 1)) {
                            throw createError(new IllegalArgumentException(
                                    "Illegal set shortcut on field [" + value + "] for type [" + targetCanonicalTypeName + "]."));
                        }

                        if (getter != null && setter != null && setter.typeParameters.get(0) != getter.returnType) {
                            throw createError(new IllegalArgumentException("Shortcut argument types must match."));
                        }

                        if ((input.read == false || getter != null) && (input.write == false || setter != null)) {
                            output.actual = setter != null ? setter.typeParameters.get(0) : getter.returnType;
                        } else {
                            throw createError(new IllegalArgumentException(
                                    "Illegal shortcut on field [" + value + "] for type [" + targetCanonicalTypeName + "]."));
                        }

                        if (input.write) {
                            StoreShortcutNode storeShortcutNode = new StoreShortcutNode();
                            storeShortcutNode.setLocation(location);
                            storeShortcutNode.setExpressionType(input.read ? output.actual : void.class);
                            storeShortcutNode.setSetter(setter);
                            expressionNode = storeShortcutNode;
                        } else {
                            LoadShortcutNode loadShortcutNode = new LoadShortcutNode();
                            loadShortcutNode.setLocation(location);
                            loadShortcutNode.setExpressionType(output.actual);
                            loadShortcutNode.setGetter(getter);
                            expressionNode = loadShortcutNode;
                        }
                    } else {
                        if (Map.class.isAssignableFrom(prefixOutput.actual)) {
                            getter = scriptRoot.getPainlessLookup().lookupPainlessMethod(targetType, false, "get", 1);
                            setter = scriptRoot.getPainlessLookup().lookupPainlessMethod(targetType, false, "put", 2);

                            if (getter != null && (getter.returnType == void.class || getter.typeParameters.size() != 1)) {
                                throw createError(new IllegalArgumentException(
                                        "Illegal map get shortcut for type [" + targetCanonicalTypeName + "]."));
                            }

                            if (setter != null && setter.typeParameters.size() != 2) {
                                throw createError(new IllegalArgumentException(
                                        "Illegal map set shortcut for type [" + targetCanonicalTypeName + "]."));
                            }

                            if (getter != null && setter != null && (!getter.typeParameters.get(0).equals(setter.typeParameters.get(0)) ||
                                    !getter.returnType.equals(setter.typeParameters.get(1)))) {
                                throw createError(new IllegalArgumentException("Shortcut argument types must match."));
                            }

                            Output indexOutput;
                            PainlessCast indexCast;

                            if ((input.read || input.write)
                                    && (input.read == false || getter != null) && (input.write == false || setter != null)) {
                                Input indexInput = new Input();
                                indexInput.expected = setter != null ? setter.typeParameters.get(0) : getter.typeParameters.get(0);
                                EString index = new EString(location, value);
                                indexOutput = index.analyze(classNode, scriptRoot, scope, indexInput);
                                indexCast = AnalyzerCaster.getLegalCast(index.location,
                                        indexOutput.actual, indexInput.expected, indexInput.explicit, indexInput.internal);

                                output.actual = setter != null ? setter.typeParameters.get(1) : getter.returnType;
                            } else {
                                throw createError(new IllegalArgumentException(
                                        "Illegal map shortcut for type [" + targetCanonicalTypeName + "]."));
                            }

                            if (input.write) {
                                StoreMapShortcutNode storeMapShortcutNode = new StoreMapShortcutNode();
                                storeMapShortcutNode.setLocation(location);
                                storeMapShortcutNode.setExpressionType(input.read ? output.actual : void.class);
                                storeMapShortcutNode.setSetter(setter);
                                storeMapShortcutNode.setIndexNode(cast(indexOutput.expressionNode, indexCast));
                                expressionNode = storeMapShortcutNode;
                            } else {
                                LoadMapShortcutNode loadMapShortcutNode = new LoadMapShortcutNode();
                                loadMapShortcutNode.setLocation(location);
                                loadMapShortcutNode.setExpressionType(output.actual);
                                loadMapShortcutNode.setGetter(getter);
                                loadMapShortcutNode.setIndexNode(cast(indexOutput.expressionNode, indexCast));
                                expressionNode = loadMapShortcutNode;
                            }
                        }

                        if (List.class.isAssignableFrom(prefixOutput.actual)) {
                            getter = scriptRoot.getPainlessLookup().lookupPainlessMethod(targetType, false, "get", 1);
                            setter = scriptRoot.getPainlessLookup().lookupPainlessMethod(targetType, false, "set", 2);

                            if (getter != null && (getter.returnType == void.class || getter.typeParameters.size() != 1 ||
                                    getter.typeParameters.get(0) != int.class)) {
                                throw createError(new IllegalArgumentException(
                                        "Illegal list get shortcut for type [" + targetCanonicalTypeName + "]."));
                            }

                            if (setter != null && (setter.typeParameters.size() != 2 || setter.typeParameters.get(0) != int.class)) {
                                throw createError(new IllegalArgumentException(
                                        "Illegal list set shortcut for type [" + targetCanonicalTypeName + "]."));
                            }

                            if (getter != null && setter != null && (!getter.typeParameters.get(0).equals(setter.typeParameters.get(0))
                                    || !getter.returnType.equals(setter.typeParameters.get(1)))) {
                                throw createError(new IllegalArgumentException("Shortcut argument types must match."));
                            }

                            Output indexOutput;
                            PainlessCast indexCast;

                            if ((input.read || input.write)
                                    && (input.read == false || getter != null) && (input.write == false || setter != null)) {
                                ENumeric index = new ENumeric(location, value, 10);
                                Input indexInput = new Input();
                                indexInput.expected = int.class;
                                indexOutput = index.analyze(classNode, scriptRoot, scope, indexInput);
                                indexCast = AnalyzerCaster.getLegalCast(index.location,
                                        indexOutput.actual, indexInput.expected, indexInput.explicit, indexInput.internal);

                                output.actual = setter != null ? setter.typeParameters.get(1) : getter.returnType;
                            } else {
                                throw createError(new IllegalArgumentException(
                                        "Illegal list shortcut for type [" + targetCanonicalTypeName + "]."));
                            }

                            if (input.write) {
                                StoreListShortcutNode storeListShortcutNode = new StoreListShortcutNode();
                                storeListShortcutNode.setLocation(location);
                                storeListShortcutNode.setExpressionType(input.read  ? output.actual : void.class);
                                storeListShortcutNode.setSetter(setter);
                                storeListShortcutNode.setIndexNode(cast(indexOutput.expressionNode, indexCast));
                                expressionNode = storeListShortcutNode;
                            } else {
                                LoadListShortcutNode loadListShortcutNode = new LoadListShortcutNode();
                                loadListShortcutNode.setLocation(location);
                                loadListShortcutNode.setExpressionType(output.actual);
                                loadListShortcutNode.setGetter(getter);
                                loadListShortcutNode.setIndexNode(cast(indexOutput.expressionNode, indexCast));
                                expressionNode = loadListShortcutNode;
                            }
                        }
                    }

                    if (expressionNode == null) {
                        throw createError(new IllegalArgumentException(
                                "field [" + typeToCanonicalTypeName(prefixOutput.actual) + ", " + value + "] not found"));
                    }
                } else {
                    if (input.write && Modifier.isFinal(field.javaField.getModifiers())) {
                        throw createError(new IllegalArgumentException(
                                "invalid assignment: cannot assign a value to read-only field [" + field.javaField.getName() + "]"));
                    }

                    output.actual = field.typeParameter;

                    if (input.write) {
                        StoreFieldNode storeFieldNode = new StoreFieldNode();
                        storeFieldNode.setLocation(location);
                        storeFieldNode.setExpressionType(input.write ? output.actual : void.class);
                        storeFieldNode.setField(field);
                        expressionNode = storeFieldNode;
                    } else {
                        LoadFieldNode loadFieldNode = new LoadFieldNode();
                        loadFieldNode.setLocation(location);
                        loadFieldNode.setExpressionType(output.actual);
                        loadFieldNode.setField(field);
                        expressionNode = loadFieldNode;
                    }
                }
            }

            if (nullSafe) {
                if (input.write) {
                    throw createError(new IllegalArgumentException(
                            "invalid assignment: cannot assign a value to a null safe operation [?.]"));
                }

                if (output.actual.isPrimitive()) {
                    throw new IllegalArgumentException("Result of null safe operator must be nullable");
                }

                NullSafeSubNode nullSafeSubNode = new NullSafeSubNode();
                nullSafeSubNode.setChildNode(expressionNode);
                nullSafeSubNode.setLocation(location);
                nullSafeSubNode.setExpressionType(output.actual);
                expressionNode = nullSafeSubNode;
            }

            AccessNode accessNode = new AccessNode();
            accessNode.setLeftNode(prefixOutput.expressionNode);
            accessNode.setRightNode(expressionNode);
            accessNode.setLocation(location);
            accessNode.setExpressionType(expressionNode.getExpressionType());
            output.expressionNode = accessNode;
        }

        return output;
    }
}
