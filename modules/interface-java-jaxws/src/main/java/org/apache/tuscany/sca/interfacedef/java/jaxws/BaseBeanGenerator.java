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

package org.apache.tuscany.sca.interfacedef.java.jaxws;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class BaseBeanGenerator implements Opcodes {

    protected void defineClass(ClassWriter cw,
                               String classDescriptor,
                               String classSignature,
                               String namespace,
                               String name) {
        declareClass(cw, classDescriptor);
        annotateClass(cw, name, namespace);
        declareConstructor(cw, classSignature);
    }

    protected void declareProperty(ClassWriter cw,
                                   String classDescriptor,
                                   String classSignature,
                                   String propName,
                                   String propClassSignature,
                                   String propTypeSignature) {
        if (propClassSignature.equals(propTypeSignature)) {
            propTypeSignature = null;
        }
        declareField(cw, propName, propClassSignature, propTypeSignature);
        decalreGetter(cw, classDescriptor, classSignature, propName, propClassSignature, propTypeSignature);
        declareSetter(cw, classDescriptor, classSignature, propName, propClassSignature, propTypeSignature);
    }

    protected String getFieldName(String propName) {
        if ("return".equals(propName)) {
            return "_return";
        } else {
            return propName;
        }
    }

    protected void declareField(ClassWriter cw, String propName, String propClassSignature, String propTypeSignature) {
        FieldVisitor fv;
        AnnotationVisitor av0;
        fv = cw.visitField(ACC_PRIVATE, getFieldName(propName), propClassSignature, propTypeSignature, null);

        av0 = fv.visitAnnotation("Ljavax/xml/bind/annotation/XmlElement;", true);
        av0.visit("name", propName);
        av0.visit("namespace", "");
        av0.visitEnd();

        fv.visitEnd();
    }

    protected void declareSetter(ClassWriter cw,
                                 String classDescriptor,
                                 String classSignature,
                                 String propName,
                                 String propClassSignature,
                                 String propTypeSignature) {
        MethodVisitor mv =
            cw.visitMethod(ACC_PUBLIC,
                           "set" + capitalize(propName),
                           "(" + propClassSignature + ")V",
                           propTypeSignature == null ? null : "(" + propTypeSignature + ")V",
                           null);
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        // mv.visitLineNumber(57, l0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(CodeGenerationHelper.getLoadOPCode(propClassSignature), 1);
        mv.visitFieldInsn(PUTFIELD, classDescriptor, getFieldName(propName), propClassSignature);
        Label l1 = new Label();
        mv.visitLabel(l1);
        // mv.visitLineNumber(58, l1);
        mv.visitInsn(RETURN);
        Label l2 = new Label();
        mv.visitLabel(l2);
        mv.visitLocalVariable("this", classSignature, null, l0, l2, 0);
        mv.visitLocalVariable(getFieldName(propName), propClassSignature, propTypeSignature, l0, l2, 1);
        mv.visitMaxs(3, 3);
        mv.visitEnd();

    }

    protected void decalreGetter(ClassWriter cw,
                                 String classDescriptor,
                                 String classSignature,
                                 String propName,
                                 String propClassSignature,
                                 String propTypeSignature) {
        String getterName = ("B".equals(propClassSignature) ? "is" : "get") + capitalize(propName);
        MethodVisitor mv =
            cw.visitMethod(ACC_PUBLIC, getterName, "()" + propClassSignature, propTypeSignature == null ? null
                : "()" + propTypeSignature, null);
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        // mv.visitLineNumber(48, l0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, classDescriptor, getFieldName(propName), propClassSignature);
        mv.visitInsn(CodeGenerationHelper.getReturnOPCode(propClassSignature));
        Label l1 = new Label();
        mv.visitLabel(l1);
        mv.visitLocalVariable("this", classSignature, null, l0, l1, 0);
        mv.visitMaxs(2, 1);
        mv.visitEnd();
    }

    private static String capitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        } else {
            return Character.toUpperCase(name.charAt(0)) + name.substring(1);
        }
    }

    protected void declareConstructor(ClassWriter cw, String classSignature) {
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
        mv.visitLocalVariable("this", classSignature, null, l0, l1, 0);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }

    protected void declareClass(ClassWriter cw, String classDescriptor) {
        cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, classDescriptor, null, "java/lang/Object", null);
    }

    protected void annotateClass(ClassWriter cw, String name, String namespace) {
        AnnotationVisitor av0;
        // @XmlRootElement
        av0 = cw.visitAnnotation("Ljavax/xml/bind/annotation/XmlRootElement;", true);
        av0.visit("name", name);
        av0.visit("namespace", namespace);
        av0.visitEnd();
        // @XmlAccessorType
        av0 = cw.visitAnnotation("Ljavax/xml/bind/annotation/XmlAccessorType;", true);
        av0.visitEnum("value", "Ljavax/xml/bind/annotation/XmlAccessType;", "FIELD");
        av0.visitEnd();
        // @XmlType
        av0 = cw.visitAnnotation("Ljavax/xml/bind/annotation/XmlType;", true);
        av0.visit("name", name);
        av0.visit("namespace", namespace);
        av0.visitEnd();
    }

    public Class<?> generate(String classDescriptor,
                             String classSignature,
                             String namespace,
                             String name,
                             ClassLoader parent) {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        defineClass(cw, classDescriptor, classSignature, namespace, name);
        cw.visitEnd();
        String className = classDescriptor.replace('/', '.');
        GeneratedClassLoader cl = new GeneratedClassLoader(parent, className, cw.toByteArray());
        Class<?> generated = cl.getGeneratedClass();
        return generated;
    }

}
