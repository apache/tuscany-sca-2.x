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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.ws.WebFault;

import org.apache.tuscany.sca.interfacedef.java.impl.JavaInterfaceUtil;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class FaultBeanGenerator implements Opcodes {
    private final ClassWriter writer;
    private final Class<?> exceptionClass;
    private final String className;
    private final String classDescriptor;
    private byte[] content;
    private Class<?> faultBeanClass;

    private static final Map<Class<?>, Class<?>> generatedClasses =
        Collections.synchronizedMap(new WeakHashMap<Class<?>, Class<?>>());

    public FaultBeanGenerator(Class<? extends Throwable> exceptionClass) {
        super();
        this.writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        this.exceptionClass = exceptionClass;
        this.className = getFaultBeanName(exceptionClass);
        this.classDescriptor = "L" + className + ";";
    }

    protected List<PropertyDescriptor> getProperties() {
        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(exceptionClass);
        } catch (IntrospectionException e) {
            throw new IllegalArgumentException(e);
        }
        List<PropertyDescriptor> props = new ArrayList<PropertyDescriptor>();
        for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
            if (pd.getReadMethod() != null) {
                String name = pd.getReadMethod().getName();
                if ("getClass".equals(name) || "getStackTrace".equals(name)
                    || "getCause".equals(name)
                    || "getLocalizedMessage".equals(name)) {
                    continue;
                }
                props.add(pd);
            }
        }
        Collections.sort(props, new Comparator<PropertyDescriptor>() {
            public int compare(PropertyDescriptor o1, PropertyDescriptor o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        return props;
    }

    public void generate() {
        if (content == null) {
            visit();
            List<PropertyDescriptor> props = getProperties();
            int size = props.size();
            String[] propOrder = new String[size];
            for (int i = 0; i < size; i++) {
                propOrder[i] = props.get(i).getName();
            }
            addJAXBAnnotations(propOrder);
            for (PropertyDescriptor pd : props) {
                visitProperty(pd);
            }
            visitEnd();
        }
    }

    protected void visit() {
        writer.visit(V1_5, ACC_PUBLIC + ACC_FINAL + ACC_SUPER, className, null, "java/lang/Object", null);
    }

    private static String getFaultBeanName(Class<?> exceptionClass) {
        String faultBeanName = null;
        WebFault webFault = exceptionClass.getAnnotation(WebFault.class);
        if (webFault != null) {
            faultBeanName = webFault.faultBean();
            if (!"".equals(faultBeanName)) {
                return faultBeanName;
            }
        }

        String name = exceptionClass.getName();
        int index = name.lastIndexOf('.');
        String pkg = name.substring(0, index);
        String clsName = name.substring(index + 1);

        faultBeanName = (pkg + ".jaxws." + clsName + "Bean").replace('.', '/');
        return faultBeanName;
    }

    private String getSetterName(PropertyDescriptor pd) {
        String name = pd.getName();
        return "set" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    private String getGetterName(PropertyDescriptor pd) {
        String name = pd.getName();
        String prefix = pd.getPropertyType() == boolean.class ? "is" : "get";
        return prefix + Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    protected void visitProperty(PropertyDescriptor pd) {
        Method getter = pd.getReadMethod();
        if (getter == null) {
            return;
        }
        String name = getter.getName();

        if ("getClass".equals(name) || "getStackTrace".equals(name)
            || "getCause".equals(name)
            || "getLocalizedMessage".equals(name)) {
            return;
        }

        // Add the field
        String field = pd.getName();
        String desc = Type.getDescriptor(pd.getPropertyType());
        FieldVisitor fv = writer.visitField(ACC_PRIVATE, field, desc, null, null);
        fv.visitEnd();

        String getterName = getGetterName(pd);
        String getterDesc = Type.getMethodDescriptor(Type.getType(pd.getPropertyType()), new Type[0]);

        // Add the getter
        MethodVisitor mv = writer.visitMethod(ACC_PUBLIC, getterName, getterDesc, null, null);
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, className, field, desc);
        mv.visitInsn(CodeGenerationHelper.getReturnOPCode(desc));
        Label l1 = new Label();
        mv.visitLabel(l1);
        mv.visitLocalVariable("this", classDescriptor, null, l0, l1, 0);
        mv.visitMaxs(1, 1);
        mv.visitEnd();

        String setterName = getSetterName(pd);
        String setterDesc =
            Type.getMethodDescriptor(Type.getType(void.class), new Type[] {Type.getType(pd.getPropertyType())});
        mv = writer.visitMethod(ACC_PUBLIC, setterName, setterDesc, null, null);

        mv.visitCode();
        l0 = new Label();
        mv.visitLabel(l0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(CodeGenerationHelper.getLoadOPCode(desc), 1);
        mv.visitFieldInsn(PUTFIELD, className, field, desc);
        l1 = new Label();
        mv.visitLabel(l1);
        mv.visitInsn(RETURN);
        Label l2 = new Label();
        mv.visitLabel(l2);
        mv.visitLocalVariable("this", classDescriptor, null, l0, l2, 0);
        mv.visitLocalVariable(field, desc, null, l0, l2, 1);
        mv.visitMaxs(2, 2);
        mv.visitEnd();

    }

    protected void visitEnd() {
        addDefaultConstructor();
        writer.visitEnd();
        content = writer.toByteArray();
    }

    protected void addDefaultConstructor() {
        MethodVisitor mv = writer.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        Label label0 = new Label();
        mv.visitLabel(label0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
        Label label1 = new Label();
        mv.visitLabel(label1);
        mv.visitInsn(RETURN);
        Label lable2 = new Label();
        mv.visitLabel(lable2);
        mv.visitLocalVariable("this", classDescriptor, null, label0, lable2, 0);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }

    protected void addJAXBAnnotations(String[] propOrder) {
        WebFault webFault = exceptionClass.getAnnotation(WebFault.class);
        String ns = null, name = null;
        if (webFault != null) {
            ns = webFault.targetNamespace();
            name = webFault.name();
        }
        if (ns == null) {
            ns = JavaInterfaceUtil.getNamespace(exceptionClass);
        }
        if (name == null) {
            name = exceptionClass.getSimpleName();
        }
        String desc = Type.getDescriptor(XmlRootElement.class);
        AnnotationVisitor av = writer.visitAnnotation(desc, true);
        av.visit("namespace", ns);
        av.visit("name", name);
        av.visitEnd();

        desc = Type.getDescriptor(XmlType.class);
        av = writer.visitAnnotation(desc, true);
        av.visit("namespace", ns);
        av.visit("name", name);
        AnnotationVisitor pv = av.visitArray("propOrder");
        for (String p : propOrder) {
            pv.visit(null, p);
        }
        pv.visitEnd();
        av.visitEnd();

        desc = Type.getDescriptor(XmlAccessorType.class);
        av = writer.visitAnnotation(desc, true);
        av.visitEnum("value", Type.getDescriptor(XmlAccessType.class), "FIELD");
        av.visitEnd();

    }

    public Class<?> getFaultBeanClass() {
        if (faultBeanClass == null && content != null) {
            faultBeanClass =
                new GeneratedClassLoader(exceptionClass.getClassLoader(), className.replace('/', '.'), content)
                    .getGeneratedClass();
        }
        return faultBeanClass;
    }

    public String getClassName() {
        return className.replace('/', '.');
    }

    public byte[] getContent() {
        return content;
    }

    public static Class<?> generateFaultBeanClass(Class<? extends Throwable> exceptionClass) throws IOException {
        synchronized (exceptionClass) {
            Class<?> faultBeanClass = generatedClasses.get(exceptionClass);
            if (faultBeanClass == null) {
                FaultBeanGenerator generator = new FaultBeanGenerator(exceptionClass);
                generator.generate();
                faultBeanClass = generator.getFaultBeanClass();
                generatedClasses.put(exceptionClass, faultBeanClass);
            }
            return faultBeanClass;
        }
    }

    public static byte[] generateFaultBeanClassRep(Class<? extends Throwable> exceptionClass) throws IOException {
        synchronized (exceptionClass) {
            FaultBeanGenerator generator = new FaultBeanGenerator(exceptionClass);
            generator.generate();
            return generator.getContent();
        }
    }
}
