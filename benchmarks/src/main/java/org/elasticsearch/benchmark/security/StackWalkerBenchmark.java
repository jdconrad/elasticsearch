/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */

package org.elasticsearch.benchmark.security;

import jdk.internal.reflect.CallerSensitive;

import jdk.internal.reflect.Reflection;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceClassVisitor;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.instrument.Instrumentation;
import java.lang.invoke.MethodType;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static java.lang.StackWalker.Option.RETAIN_CLASS_REFERENCE;

@Fork(1)
@Warmup(iterations = 1)
@Measurement(iterations = 5)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@OperationsPerInvocation(100000)
@State(Scope.Benchmark)
public class StackWalkerBenchmark { // extends SecurityManager {

/*    public interface TestBase {
        void test();
    }

    public static class Loader extends ClassLoader {

        public Loader(ClassLoader parent) {
            super(parent);
        }

        public Class<? extends TestBase> define(String className, byte[] bytes) {
            return defineClass(className, bytes, 0, bytes.length).asSubclass(TestBase.class);
        }
    }

    static {
        try {
            final int classFrames = org.objectweb.asm.ClassWriter.COMPUTE_FRAMES | org.objectweb.asm.ClassWriter.COMPUTE_MAXS;
            final int classAccess = Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER | Opcodes.ACC_FINAL;
            final String baseInterfaceName = TestBase.class.getName();
            final Type baseInterfaceType = Type.getType(TestBase.class);
            final String className = baseInterfaceName + "$Derived";
            final Type classType = Type.getObjectType(className.replace('.', '/'));

            final ClassWriter writer = new ClassWriter(classFrames);
            StringWriter output = new StringWriter();
            PrintWriter outputWriter = new PrintWriter(output);
            Textifier textifier = new Textifier();
            final TraceClassVisitor visitor = new TraceClassVisitor(writer, textifier, null);
            visitor.visit(
                Opcodes.V14,
                classAccess,
                classType.getInternalName(),
                null,
                Type.getType(Object.class).getInternalName(),//baseInterfaceType.getInternalName(),
                new String[] {baseInterfaceType.getInternalName()}
            );
            final Method init = new Method("<init>", MethodType.methodType(void.class).toMethodDescriptorString());
            final GeneratorAdapter constructor = new GeneratorAdapter(
                visitor.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null),
                Opcodes.ACC_PUBLIC,
                "<init>",
                "()V"
            );
            constructor.visitCode();
            constructor.loadThis();
            constructor.invokeConstructor(Type.getType(Object.class), init);
            constructor.returnValue();
            constructor.endMethod();
            final GeneratorAdapter adapter = new GeneratorAdapter(
                visitor.visitMethod(Opcodes.ACC_PUBLIC, "test", "()V", null, null),
                Opcodes.ACC_PUBLIC,
                "test",
                "()V"
            );
            adapter.visitAnnotation(Type.getDescriptor(CallerSensitive.class),true).visitEnd();
            adapter.visitCode();
            adapter.invokeStatic(Type.getType(Reflection.class), Method.getMethod(
                Reflection.class.getMethod("getCallerClass")
            ));
            adapter.returnValue();
            adapter.endMethod();
            visitor.visitEnd();
            final byte[] bytes = writer.toByteArray();
            //System.out.println(Arrays.toString(ClassLoader.getSystemClassLoader().getClass().getMethods()));
            //java.lang.reflect.Method method = ClassLoader.getSystemClassLoader().getClass().getMethod("defineClass", String.class, byte[].class, int.class, int.class);
            //method.setAccessible(true);
            //Class<? extends TestBase> clazz = (Class<? extends TestBase>)method.invoke(null, className, bytes, 0, bytes.length);
            Class<? extends TestBase> clazz = new Loader(StackWalkerBenchmark.class.getClassLoader()).define(className, bytes);
            instance = clazz.getConstructor().newInstance();
            textifier.print(outputWriter);
            //System.out.println(output.toString());
        } catch (Error e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }*/

    //private static TestBase instance;

//    public static final StackWalker WALKER = StackWalker.getInstance(RETAIN_CLASS_REFERENCE);
//
//    @Benchmark
//    public void benchmarkWalk() {
//        for (int i = 0; i < 100000; ++i) {
//            stackWalk(10);
//        }
//    }
//
//    private void stackWalk(int count) {
//        --count;
//
//        if (count > 0) {
//            stackWalk(count);
//        } else {
//            WALKER.walk(s ->
//                s.map(StackWalker.StackFrame::getDeclaringClass)
//                    .skip(2)
//                    .findFirst());
//        }
//    }
//
//    @Benchmark
//    public void benchmarkGetCallerClass() {
//        for (int i = 0; i < 100000; ++i) {
//            stackGetCallerClass(10);
//        }
//    }
//
//    private void stackGetCallerClass(int count) {
//        --count;
//
//        if (count > 0) {
//            stackWalk(count);
//        } else {
//            WALKER.getCallerClass();
//        }
//    }
//
//    @Benchmark
//    public void benchmarkGetSecurityContext() {
//        for (int i = 0; i < 100000; ++i) {
//            stackGetSecurityContext(10);
//        }
//    }
//
//    private void stackGetSecurityContext(int count) {
//        --count;
//
//        if (count > 0) {
//            stackGetSecurityContext(count);
//        } else {
//            this.getSecurityContext();
//        }
//    }

    @Benchmark
    public void benchmarkReflectionGetCallerClass() {
        for (int i = 0; i < 100000; ++i) {
            stackReflectionGetCallerClass(10);
        }
    }

    private char stackReflectionGetCallerClass(int count) {
        --count;

        if (count > 0) {
            return stackReflectionGetCallerClass(count);
        } else {
            return "a".charAt(0);
        }
    }
}
