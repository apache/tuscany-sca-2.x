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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.WebFault;

import org.apache.tuscany.sca.interfacedef.java.impl.JavaInterfaceUtil;
import org.objectweb.asm.ClassWriter;

public class FaultBeanGenerator extends BaseBeanGenerator {
    public FaultBeanGenerator() {
        super();
    }

    protected BeanProperty[] getProperties(Class<? extends Throwable> exceptionClass) {
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
                Method getter = pd.getReadMethod();
                props.add(new BeanProperty("", field, getter.getReturnType(), getter.getGenericReturnType(), false));
            }
        }
        Collections.sort(props, new Comparator<BeanProperty>() {
            public int compare(BeanProperty o1, BeanProperty o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        return props.toArray(new BeanProperty[0]);
    }

    public byte[] generate(Class<? extends Throwable> exceptionClass) {
        String className = getFaultBeanName(exceptionClass);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        String classDescriptor = className.replace('.', '/');
        String classSignature = "L" + classDescriptor + ";";
        QName element = getElementName(exceptionClass);
        String namespace = element.getNamespaceURI();
        String name = element.getLocalPart();
        return defineClass(cw, classDescriptor, classSignature, namespace, name, getProperties(exceptionClass));
    }

    public Class<?> generate(Class<? extends Throwable> exceptionClass, GeneratedClassLoader cl) {
        synchronized (exceptionClass) {
            Class<?> faultBeanClass = generatedClasses.get(exceptionClass);
            if (faultBeanClass == null) {
                String className = getFaultBeanName(exceptionClass);
                String classDescriptor = className.replace('.', '/');
                String classSignature = "L" + classDescriptor + ";";
                QName element = getElementName(exceptionClass);
                String namespace = element.getNamespaceURI();
                String name = element.getLocalPart();
                faultBeanClass =
                    generate(classDescriptor, classSignature, namespace, name, getProperties(exceptionClass), cl);
                generatedClasses.put(exceptionClass, faultBeanClass);
            }
            return faultBeanClass;
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

        // FIXME: [rfeng] This is a workaround to avoid "Prohibited package name: java.lang.jaxws"
        if (pkg.startsWith("java.") || pkg.startsWith("javax.")) {
            pkg = "tuscany";
        }
        faultBeanName = (pkg + ".jaxws." + clsName + "Bean");
        return faultBeanName;
    }

    public static QName getElementName(Class<? extends Throwable> exceptionClass) {
        WebFault webFault = exceptionClass.getAnnotation(WebFault.class);
        String namespace = null;
        String name = null;
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
        return new QName(namespace, name);
    }

    public static Class<?> generateFaultBeanClass(Class<? extends Throwable> exceptionClass) {
        FaultBeanGenerator generator = new FaultBeanGenerator();
        GeneratedClassLoader cl = new GeneratedClassLoader(exceptionClass.getClassLoader());
        return generator.generate(exceptionClass, cl);
    }
}
