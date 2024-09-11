// To compile and run:
// javac --add-exports java.base/jdk.internal.org.objectweb.asm=ALL-UNNAMED --add-exports java.base/jdk.internal.org.objectweb.asm.commons=ALL-UNNAMED --add-exports java.base/jdk.internal.org.objectweb.asm.util=ALL-UNNAMED --add-exports java.base/jdk.internal.reflect=ALL-UNNAMED testagent/TestAgent.java
// jar cfm testagent.jar agentmanifest testagent/TestAgent.class testagent/TestAgent\$Transformer.class testagent/TestAgent\$Visitor.class testagent/TestAgent\$Adapter.class
// gw -p benchmarks run --args 'StackWalkerBenchmark'

package testagent;

import java.io.StringWriter;
import java.io.PrintWriter;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.Arrays;

import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.commons.*;
import jdk.internal.org.objectweb.asm.util.*;
import jdk.internal.reflect.CallerSensitive;
import jdk.internal.reflect.Reflection;

public class TestAgent {

    public static class Transformer implements ClassFileTransformer {

        @Override
        public byte[] transform(
                ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
            System.out.println("transforming [" + className + "]");
            if (className.equals("java/lang/String")) {
                StringWriter output = new StringWriter();
                PrintWriter outputWriter = new PrintWriter(output);
                Textifier textifier = new Textifier();
                ClassReader reader = new ClassReader(classfileBuffer);
                ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
                TraceClassVisitor visitor = new TraceClassVisitor(writer, textifier, null);
                reader.accept(new Visitor(visitor), ClassReader.EXPAND_FRAMES);
                textifier.print(outputWriter);
                //System.out.println(output.toString());
                System.out.println("length: " + classfileBuffer.length);
                return writer.toByteArray();
            }
            return null;
        }
    }

    public static class Visitor extends ClassVisitor {
    
        public Visitor(ClassVisitor visitor) {
            super(Opcodes.ASM8, visitor);
            System.out.println("visitor instantiated");
        }

        @Override
        public MethodVisitor visitMethod(int methodAccess, String methodName, String methodDesc, String signature, String[] exceptions) {
            //System.out.println("method: " + methodName);
            if (methodName.equals("charAt")) {
                System.out.println("found charAt method");
                MethodVisitor methodVisitor = cv.visitMethod(methodAccess, methodName, methodDesc, signature, exceptions);
                return new Adapter(Opcodes.ASM8, methodVisitor, methodAccess, methodName, methodDesc);
            }
            else {
               return super.visitMethod(methodAccess, methodName, methodDesc, signature, exceptions);
            }
        }
    }

    public static class Adapter extends AdviceAdapter {
    
        public Adapter(int api, MethodVisitor methodVisitor, int access, String name, String descriptor) {
            super(api, methodVisitor, access, name, descriptor);
        }

        @Override
        protected void onMethodEnter() {
            System.out.println("entering method!!!");
            visitAnnotation(Type.getDescriptor(CallerSensitive.class), true).visitEnd();
            try {
                invokeStatic(Type.getType(Reflection.class), Method.getMethod(Reflection.class.getMethod("getCallerClass")));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            pop();
        }
    }

    public static void premain(String agentArgs, Instrumentation inst) throws Exception {
        ClassLoader sloader = ClassLoader.getSystemClassLoader();
        /*Class<?>[] sclasses = inst.getInitiatedClasses(sloader);
        System.out.println("System loaded classes:");
        System.out.println(Arrays.toString(sclasses));
        ClassLoader ploader = ClassLoader.getPlatformClassLoader();
        Class<?>[] pclasses = inst.getInitiatedClasses(ploader);
        System.out.println("\nPlatform loaded classes:");
        System.out.println(Arrays.toString(pclasses));*/
        Transformer transformer = new Transformer();
        inst.addTransformer(transformer, true);
        inst.retransformClasses(String.class);
        inst.removeTransformer(transformer);
    }
}
