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

import org.elasticsearch.painless.Globals;
import org.elasticsearch.painless.Location;
import org.elasticsearch.painless.MethodWriter;
import org.elasticsearch.painless.SimpleChecksAdapter;
import org.elasticsearch.painless.WriterConstants;
import org.elasticsearch.painless.builder.SymbolTable;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.TraceClassVisitor;

import java.lang.invoke.MethodType;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.util.Collections.emptyList;
import static org.elasticsearch.painless.WriterConstants.BASE_INTERFACE_TYPE;
import static org.elasticsearch.painless.WriterConstants.BITSET_TYPE;
import static org.elasticsearch.painless.WriterConstants.CLASS_TYPE;
import static org.elasticsearch.painless.WriterConstants.DEFINITION_TYPE;
import static org.elasticsearch.painless.WriterConstants.DEF_BOOTSTRAP_DELEGATE_METHOD;
import static org.elasticsearch.painless.WriterConstants.DEF_BOOTSTRAP_DELEGATE_TYPE;
import static org.elasticsearch.painless.WriterConstants.DEF_BOOTSTRAP_METHOD;
import static org.elasticsearch.painless.WriterConstants.GET_NAME_METHOD;
import static org.elasticsearch.painless.WriterConstants.GET_SOURCE_METHOD;
import static org.elasticsearch.painless.WriterConstants.GET_STATEMENTS_METHOD;
import static org.elasticsearch.painless.WriterConstants.MAP_TYPE;
import static org.elasticsearch.painless.WriterConstants.STRING_TYPE;

/**
 * The root of all Painless trees.  Contains a series of statements.
 */
public final class SSource extends AStatement {

    private final String name;
    private final String sourceText;
    private final Class<?> baseClass;
    private final Printer debugStream;

    //private CompilerSettings settings;

    //private Locals mainMethod;
    private byte[] bytes;

    public SSource(Location location, String name, String sourceText, Class<?> baseClass, Printer debugStream) {
        super(location);

        this.name = Objects.requireNonNull(name);
        this.sourceText = Objects.requireNonNull(sourceText);
        this.baseClass = Objects.requireNonNull(baseClass);
        this.debugStream = debugStream;
    }

    /*public void analyze(PainlessLookup painlessLookup) {
        Map<String, LocalMethod> methods = new HashMap<>();

        for (ANode child : children) {
            if (child instanceof SFunction) {
                SFunction function = (SFunction)child;
                function.generateSignature();
                if (function.synthetic == false) {
                    String key = Locals.buildLocalMethodKey(function.name, function.children.get(1).children.size());

                    Class<?> returnType = ((DTypeClass)function.children.get(0)).type;
                    List<Class<?>> typeParameters = new ArrayList<>();

                    for (int i = 0; i < function.children.get(1).children.size(); i++) {
                        SDeclaration parameter = (SDeclaration)function.children.get(1).children.get(i);
                        typeParameters.add(((DTypeClass)parameter.children.get(0)).type);
                    }

                    methods.put(key, new LocalMethod(function.name, returnType, typeParameters, function.methodType));
                }
            } else {
                break;
            }
        }

        Locals locals = Locals.newProgramScope(baseClass, painlessLookup, methods.values());
        analyze(locals);
    }*/

    @Override
    void analyze(SymbolTable table) {
        for (ANode child : children) {
            if (child instanceof SFunction) {
                SFunction function = (SFunction)child;
                function.analyze(table);
            } else {
                break;
            }
        }
    }

    public Map<String, Object> write(SymbolTable table) {
        // Create the ClassWriter.

        int classFrames = ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS;
        int classAccess = Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER | Opcodes.ACC_FINAL;
        String interfaceBase = BASE_INTERFACE_TYPE.getInternalName();
        String className = CLASS_TYPE.getInternalName();
        String[] classInterfaces = new String[] { interfaceBase };

        ClassWriter writer = new ClassWriter(classFrames);
        ClassVisitor visitor = writer;

        // if picky is enabled, turn on some checks. instead of VerifyError at the end, you get a helpful stacktrace.
        if (table.settings().isPicky()) {
            visitor = new SimpleChecksAdapter(visitor);
        }

        if (debugStream != null) {
            visitor = new TraceClassVisitor(visitor, debugStream, null);
        }
        visitor.visit(WriterConstants.CLASS_VERSION, classAccess, className, null,
            Type.getType(baseClass).getInternalName(), classInterfaces);
        visitor.visitSource(Location.computeSourceName(name), null);

        BitSet statements = new BitSet(sourceText.length());
        final MethodWriter clinit = new MethodWriter(Opcodes.ACC_STATIC,
                WriterConstants.CLINIT, visitor, statements, table.settings());
        clinit.visitCode();
        Globals globals = new Globals(visitor, clinit, statements);

        // Write the a method to bootstrap def calls
        MethodWriter bootstrapDef = new MethodWriter(Opcodes.ACC_STATIC | Opcodes.ACC_VARARGS, DEF_BOOTSTRAP_METHOD, visitor,
                globals.getStatements(), table.settings());
        bootstrapDef.visitCode();
        bootstrapDef.getStatic(CLASS_TYPE, "$DEFINITION", DEFINITION_TYPE);
        bootstrapDef.getStatic(CLASS_TYPE, "$LOCALS", MAP_TYPE);
        bootstrapDef.loadArgs();
        bootstrapDef.invokeStatic(DEF_BOOTSTRAP_DELEGATE_TYPE, DEF_BOOTSTRAP_DELEGATE_METHOD);
        bootstrapDef.returnValue();
        bootstrapDef.endMethod();

        // Write static variables for name, source and statements used for writing exception messages
        visitor.visitField(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "$NAME", STRING_TYPE.getDescriptor(), null, null).visitEnd();
        visitor.visitField(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "$SOURCE", STRING_TYPE.getDescriptor(), null, null).visitEnd();
        visitor.visitField(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "$STATEMENTS", BITSET_TYPE.getDescriptor(), null, null).visitEnd();

        // Write the static variables used by the method to bootstrap def calls
        visitor.visitField(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "$DEFINITION", DEFINITION_TYPE.getDescriptor(), null, null).visitEnd();
        visitor.visitField(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "$LOCALS", MAP_TYPE.getDescriptor(), null, null).visitEnd();

        org.objectweb.asm.commons.Method init;

        if (baseClass.getConstructors().length == 0) {
            init = new org.objectweb.asm.commons.Method("<init>", MethodType.methodType(void.class).toMethodDescriptorString());
        } else {
            init = new org.objectweb.asm.commons.Method("<init>", MethodType.methodType(void.class,
                baseClass.getConstructors()[0].getParameterTypes()).toMethodDescriptorString());
        }

        // Write the constructor:
        MethodWriter constructor = new MethodWriter(Opcodes.ACC_PUBLIC, init, visitor, globals.getStatements(), table.settings());
        constructor.visitCode();
        constructor.loadThis();
        constructor.loadArgs();
        constructor.invokeConstructor(Type.getType(baseClass), init);
        constructor.returnValue();
        constructor.endMethod();

        // Write a method to get static variable source
        MethodWriter nameMethod = new MethodWriter(Opcodes.ACC_PUBLIC, GET_NAME_METHOD, visitor, globals.getStatements(), table.settings());
        nameMethod.visitCode();
        nameMethod.getStatic(CLASS_TYPE, "$NAME", STRING_TYPE);
        nameMethod.returnValue();
        nameMethod.endMethod();

        // Write a method to get static variable source
        MethodWriter sourceMethod =
                new MethodWriter(Opcodes.ACC_PUBLIC, GET_SOURCE_METHOD, visitor, globals.getStatements(), table.settings());
        sourceMethod.visitCode();
        sourceMethod.getStatic(CLASS_TYPE, "$SOURCE", STRING_TYPE);
        sourceMethod.returnValue();
        sourceMethod.endMethod();

        // Write a method to get static variable statements
        MethodWriter statementsMethod =
            new MethodWriter(Opcodes.ACC_PUBLIC, GET_STATEMENTS_METHOD, visitor, globals.getStatements(), table.settings());
        statementsMethod.visitCode();
        statementsMethod.getStatic(CLASS_TYPE, "$STATEMENTS", BITSET_TYPE);
        statementsMethod.returnValue();
        statementsMethod.endMethod();

        // Write all functions:
        for (ANode child : children) {
            if (child instanceof SFunction) {
                ((SFunction)child).write(visitor, globals);
            } else {
                break;
            }
        }

        clinit.returnValue();
        clinit.endMethod();

        // End writing the class and store the generated bytes.

        visitor.visitEnd();
        bytes = writer.toByteArray();

        Map<String, Object> statics = new HashMap<>(globals.statics);
        statics.put("$LOCALS", table.functions());
        return statics;
    }

    @Override
    void write(MethodWriter writer, Globals globals) {

    }

    public byte[] getBytes() {
        return bytes;
    }

    @Override
    public String toString() {
        return multilineToString(emptyList(), children);
    }
}
