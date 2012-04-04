/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.tuscany.sca.interfacedef.java.jaxrs;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class RootResourceClassGenerator implements Opcodes {

    private static final String DELEGATE_FIELD = "delegate";

    public static Class<?> generateRootResourceClass(Class<?> interfaze, String path, String consumes, String produces)
        throws Exception {
        if (!interfaze.isInterface()) {
            throw new IllegalArgumentException(interfaze + " is not an interface.");
        }
        GeneratedClassLoader classLoader = new GeneratedClassLoader(interfaze.getClassLoader());
        String interfaceName = interfaze.getName();
        int index = interfaze.getName().lastIndexOf('.');
        String className =
            interfaceName.substring(0, index) + ".Generated" + interfaceName.substring(index + 1) + "Impl";

        final byte[] content = generate(interfaze, path, consumes, produces);
        Class<?> cls = classLoader.getGeneratedClass(className, content);
        return cls;
    }

    public static void injectProxy(Class<?> generatedResourceClass, Object proxy) throws Exception {
        Field field = generatedResourceClass.getField("delegate");
        field.set(null, proxy);
    }

    public static byte[] generate(Class<?> interfaze, String path, String consumes, String produces) throws Exception {
        String interfaceName = Type.getInternalName(interfaze);
        int index = interfaceName.lastIndexOf('/');
        String className =
            interfaceName.substring(0, index) + "/Generated" + interfaceName.substring(index + 1) + "Impl";

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        declareClass(cw, interfaceName, className);

        annotatePath(cw, path);
        annotateContentTypes(cw, consumes, produces);
        declareField(cw, interfaceName);
        declareConstructor(cw, className);

        for (Method method : interfaze.getMethods()) {
            if (!(method.getDeclaringClass() == Object.class)) {
                generateMethod(cw, interfaceName, className, method, consumes, produces);
            }
        }
        cw.visitEnd();

        return cw.toByteArray();
    }

    // public <ReturnType> method(<Type0> arg0, ..., <TypeN> argN) throws <ExpectionType0>, ..., <ExceptionTypeK>
    private static void generateMethod(ClassWriter cw,
                                       String interfaceName,
                                       String className,
                                       Method method,
                                       String consumes,
                                       String produces) {
        String methodDescriptor = Type.getMethodDescriptor(method);

        String signatureString = getSignature(method);

        MethodVisitor mv =
            cw.visitMethod(ACC_PUBLIC,
                           method.getName(),
                           methodDescriptor,
                           signatureString,
                           getExceptionInternalNames(method));

        mv.visitCode();
        mv.visitFieldInsn(GETSTATIC, className, DELEGATE_FIELD, getSignature(interfaceName));
        Class<?>[] paramTypes = method.getParameterTypes();
        int index = 1;
        for (int i = 0; i < paramTypes.length; i++) {
            String signature = Type.getDescriptor(paramTypes[i]);
            mv.visitVarInsn(CodeGenerationHelper.getLoadOPCode(signature), index);
            if(paramTypes[i] == long.class || paramTypes[i] == double.class) {
                index+=2; // Increase the index by 2 for the 64bit numbers
            } else {
                index++;
            }
        }
        mv.visitMethodInsn(INVOKEINTERFACE, interfaceName, method.getName(), methodDescriptor);

        Class<?> returnType = method.getReturnType();
        mv.visitInsn(CodeGenerationHelper.getReturnOPCode(Type.getDescriptor(returnType)));
        int size = paramTypes.length + 1;
        mv.visitMaxs(size, size);
        mv.visitEnd();
    }

    /**
     * [rfeng] A hack to get the generic method signature
     * @param method
     * @return
     */
    private static String getSignature(Method method) {
        try {
            Field field = method.getClass().getDeclaredField("signature");
            field.setAccessible(true);
            return (String)field.get(method);
        } catch (Throwable e) {
            return null;
        }
    }

    private static String[] getExceptionInternalNames(Method method) {
        Class<?>[] types = method.getExceptionTypes();
        if (types.length == 0) {
            return null;
        }
        String[] names = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            names[i] = Type.getInternalName(types[i]);
        }
        return names;
    }

    private static String getSignature(String interfaceName) {
        return "L" + interfaceName + ";";
    }

    private static void declareConstructor(ClassWriter cw, String className) {
        MethodVisitor mv;
        mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        // mv.visitLineNumber(37, l0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
        mv.visitInsn(RETURN);
        Label l1 = new Label();
        mv.visitLabel(l1);
        mv.visitLocalVariable("this", getSignature(className), null, l0, l1, 0);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }

    // public static <Interface> delegate;
    private static void declareField(ClassWriter cw, String interfaceName) {
        FieldVisitor fv =
            cw.visitField(ACC_PUBLIC + ACC_STATIC, DELEGATE_FIELD, getSignature(interfaceName), null, null);
        fv.visitEnd();
    }

    // public class _<Interface>Impl implements <Interface>
    private static void declareClass(ClassWriter cw, String interfaceName, String className) {
        cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object", new String[] {interfaceName});
    }

    // @Path(<path>)
    private static void annotatePath(ClassWriter cw, String path) {
        AnnotationVisitor av = cw.visitAnnotation("Ljavax/ws/rs/Path;", true);
        av.visit("value", path);
        av.visitEnd();
    }

    // @Consumes(<contentTypes>)
    // @Provides(<contentTypes>)
    private static void annotateContentTypes(ClassWriter cw, String consumes, String produces) {
        AnnotationVisitor av = null;
        if (consumes != null) {
            av = cw.visitAnnotation("Ljavax/ws/rs/Consumes;", true);
            AnnotationVisitor av1 = av.visitArray("value");
            for (String s : consumes.split("(,| )")) {
                av1.visit(null, s.trim());
            }
            av1.visitEnd();
            av.visitEnd();
        }
        if (produces != null) {
            av = cw.visitAnnotation("Ljavax/ws/rs/Produces;", true);
            AnnotationVisitor av1 = av.visitArray("value");
            for (String s : produces.split("(,| )")) {
                av1.visit(null, s.trim());
            }
            av1.visitEnd();
            av.visitEnd();
        }
    }

    // @Consumes(<contentTypes>)
    // @Provides(<contentTypes>)
    private static void annotateContentTypes(MethodVisitor mv, String consumes, String produces) {
        AnnotationVisitor av = null;
        if (consumes != null) {
            av = mv.visitAnnotation("Ljavax/ws/rs/Consumes;", true);
            AnnotationVisitor av1 = av.visitArray("value");
            for (String s : consumes.split("(,| )")) {
                av1.visit(null, s.trim());
            }
            av1.visitEnd();
            av.visitEnd();
        }
        if (produces != null) {
            av = mv.visitAnnotation("Ljavax/ws/rs/Produces;", true);
            AnnotationVisitor av1 = av.visitArray("value");
            for (String s : produces.split("(,| )")) {
                av1.visit(null, s.trim());
            }
            av1.visitEnd();
            av.visitEnd();
        }
    }
}
