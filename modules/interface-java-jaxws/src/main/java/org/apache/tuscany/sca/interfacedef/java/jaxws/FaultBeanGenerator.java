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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.xml.ws.WebFault;

import org.apache.tuscany.sca.interfacedef.java.impl.JavaInterfaceUtil;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;

public class FaultBeanGenerator extends BaseBeanGenerator {
    private final ClassWriter cw;
    private final Class<?> exceptionClass;
    private final String classDescriptor;
    private final String classSignature;
    private String namespace;
    private String name;
    private byte[] byteCode;
    private Class<?> faultBeanClass;

    private static final Map<Class<?>, Class<?>> generatedClasses =
        Collections.synchronizedMap(new WeakHashMap<Class<?>, Class<?>>());

    public FaultBeanGenerator(Class<? extends Throwable> exceptionClass) {
        super();
        this.cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        this.exceptionClass = exceptionClass;
        this.classDescriptor = getFaultBeanName(exceptionClass);
        this.classSignature = "L" + classDescriptor + ";";
        getElementName();
    }

    protected BeanProperty[] getProperties() {
        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(exceptionClass);
        } catch (IntrospectionException e) {
            throw new IllegalArgumentException(e);
        }
        List<BeanProperty> props = new ArrayList<BeanProperty>();
        for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
            if (pd.getReadMethod() != null) {
                String name = pd.getReadMethod().getName();
                if ("getClass".equals(name) || "getStackTrace".equals(name)
                    || "getCause".equals(name)
                    || "getLocalizedMessage".equals(name)) {
                    continue;
                }
                // Add the field
                String field = pd.getName();
                String desc = Type.getDescriptor(pd.getPropertyType());
                String genericDesc = CodeGenerationHelper.getSignature(pd.getReadMethod().getGenericReturnType());
                props.add(new BeanProperty(field, desc, genericDesc));
            }
        }
        Collections.sort(props, new Comparator<BeanProperty>() {
            public int compare(BeanProperty o1, BeanProperty o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        return props.toArray(new BeanProperty[0]);
    }

    public void generate() {
        if (byteCode == null) {
            byteCode = defineClass(cw, classDescriptor, classSignature, namespace, name, getProperties());
        }
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

    private void getElementName() {
        WebFault webFault = exceptionClass.getAnnotation(WebFault.class);
        if (webFault != null) {
            namespace = webFault.targetNamespace();
            name = webFault.name();
        }
        if (namespace == null) {
            namespace = JavaInterfaceUtil.getNamespace(exceptionClass);
        }
        if (name == null) {
            name = exceptionClass.getSimpleName();
        }
    }

    public Class<?> getFaultBeanClass() {
        if (faultBeanClass == null && byteCode != null) {
            faultBeanClass =
                new GeneratedClassLoader(exceptionClass.getClassLoader()).getGeneratedClass(classDescriptor
                    .replace('/', '.'), byteCode);
        }
        return faultBeanClass;
    }

    public String getClassName() {
        return classDescriptor.replace('/', '.');
    }

    public byte[] getByteCode() {
        return byteCode;
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
            return generator.getByteCode();
        }
    }
}
