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

    public static Class<?> generateRootResourceClass(Class<?> interfaze, String path) throws Exception {
        GeneratedClassLoader classLoader = new GeneratedClassLoader(interfaze.getClassLoader());
        String interfaceName = interfaze.getName();
        int index = interfaze.getName().lastIndexOf('.');
        String className =
            interfaceName.substring(0, index) + ".Generated" + interfaceName.substring(index + 1) + "Impl";

        byte[] content = generate(interfaze, path);
        Class<?> cls = classLoader.getGeneratedClass(className, content);
        return cls;
    }

    public static byte[] generate(Class<?> interfaze, String path) throws Exception {
        String interfaceName = Type.getInternalName(interfaze);
        int index = interfaceName.lastIndexOf('/');
        String className =
            interfaceName.substring(0, index) + "/Generated" + interfaceName.substring(index + 1) + "Impl";

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        declareClass(cw, interfaceName, className);

        annotatePath(cw, path);
        declareField(cw, interfaceName);
        declareConstructor(cw, className);

        for (Method method : interfaze.getMethods()) {
            if (!(method.getDeclaringClass() == Object.class)) {
                generateMethod(cw, interfaceName, className, method);
            }
        }
        cw.visitEnd();

        return cw.toByteArray();
    }

    // public <ReturnType> method(<Type0> arg0, ..., <TypeN> argN) throws <ExpectionType0>, ..., <ExceptionTypeK>
    private static void generateMethod(ClassWriter cw, String interfaceName, String className, Method method) {
        String methodDescriptor = Type.getMethodDescriptor(method);

        MethodVisitor mv =
            cw.visitMethod(ACC_PUBLIC, method.getName(), methodDescriptor, null, getExceptionInternalNames(method));
        mv.visitCode();
        mv.visitFieldInsn(GETSTATIC, className, DELEGATE_FIELD, getSignature(interfaceName));
        Class<?>[] paramTypes = method.getParameterTypes();
        for (int i = 0; i < paramTypes.length; i++) {
            String signature = Type.getDescriptor(paramTypes[i]);
            mv.visitVarInsn(CodeGenerationHelper.getLoadOPCode(signature), i + 1);
        }
        mv.visitMethodInsn(INVOKEINTERFACE, interfaceName, method.getName(), methodDescriptor);

        Class<?> returnType = method.getReturnType();
        mv.visitInsn(CodeGenerationHelper.getReturnOPCode(Type.getDescriptor(returnType)));
        int size = paramTypes.length + 1;
        mv.visitMaxs(size, size);
        mv.visitEnd();
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
}
