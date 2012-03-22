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
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.WebFault;

import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
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
                if ("getClass".equals(name) || "getStackTrace".equals(name) || "getSuppressed".equals(name)
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

    public byte[] generate(Class<? extends Throwable> exceptionClass, Operation operation) {
    	// The reflection code here allows for toleration of older versions of ASM.      
    	ClassWriter cw;
    	try {   		
    		Constructor<ClassWriter> c = ClassWriter.class.getConstructor(new Class[] {int.class});
    		Field f = ClassWriter.class.getField("COMPUTE_MAXS");
    		cw = c.newInstance(f.get(null)); 
    	} catch ( Exception ex ) {
    		try {
    			Constructor<ClassWriter> c = ClassWriter.class.getConstructor(new Class[] {boolean.class});
    			cw = c.newInstance(true);
    		} catch ( Exception ex2 ) {
    			throw new IllegalArgumentException(ex2);
    		}
    		
    	}  

        // TUSCANY-3283 - all generated classes (including exception) should go in the namespace
        //                of the interface not the namespace of the originating exception. 
        //                consequently we need to create a matching package name for the schema
        QName element = getElementName(exceptionClass, operation);
        String name = element.getLocalPart();
        String namespace = element.getNamespaceURI();
        
        String className = getFaultBeanName(exceptionClass, operation);
        String classDescriptor = className.replace('.', '/');
        String classSignature = "L" + classDescriptor + ";";

        return defineClass(cw, classDescriptor, classSignature, namespace, name, getProperties(exceptionClass));
    }

    public Class<?> generate(Class<? extends Throwable> exceptionClass, GeneratedClassLoader cl, Operation operation) {
        synchronized (exceptionClass) {
            QName element = getElementName(exceptionClass, operation);
            WeakReference<Class<?>> wr = generatedClasses.get(element);
            Class<?> faultBeanClass = null;
            if (wr != null){
                faultBeanClass = wr.get();
            }
            if (faultBeanClass == null) {
                
                // TUSCANY-3283 - all generated classes (including exception) should go in the namespace
                //                of the interface not the namespace of the originating exception. 
                //                consequently we need to create a matching package name for the schema
                String name = element.getLocalPart();
                String namespace = element.getNamespaceURI();
                
                String className = getFaultBeanName(exceptionClass, operation);
                String classDescriptor = className.replace('.', '/');
                String classSignature = "L" + classDescriptor + ";";

                faultBeanClass = generate(classDescriptor, classSignature, namespace, name, getProperties(exceptionClass), cl);
                generatedClasses.put(element, new WeakReference<Class<?>>(faultBeanClass));
            }
            return faultBeanClass;
        }
    }

    private static String getFaultBeanName(Class<?> exceptionClass, Operation operation) {
        // TUSCANY-3283 - all generated classes (including exception) should go in the namespace
        //                of the interface not the namespace of the originating exception. 
        //                consequently we need to create a matching package name for the schema
        String interfacePkg = null;
        if (operation != null && operation.getInterface() instanceof JavaInterface){
            interfacePkg = ((JavaInterface)operation.getInterface()).getJavaClass().getPackage().getName();
        }
        
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
        String pkg = null;
        if (interfacePkg != null){
            pkg = interfacePkg;
        } else {
            pkg = name.substring(0, index);
        }
        String clsName = name.substring(index + 1);

        // FIXME: [rfeng] This is a workaround to avoid "Prohibited package name: java.lang.jaxws"
        if (pkg.startsWith("java.") || pkg.startsWith("javax.")) {
            pkg = "tuscany";
        }
        faultBeanName = (pkg + ".jaxws." + clsName + "Bean");
        return faultBeanName;
    }

    public static QName getElementName(Class<? extends Throwable> exceptionClass, Operation operation) {
        WebFault webFault = exceptionClass.getAnnotation(WebFault.class);
        
        // TUSCANY-3283 - all generated classes (including exception) should go in the namespace
        //                of the interface not the namespace of the originating exception. 
        //                consequently we need to create a matching package name for the schema
        String namespace = null;
        if (operation != null && operation.getInterface() instanceof JavaInterface){
            namespace = ((JavaInterface)operation.getInterface()).getQName().getNamespaceURI();
        }
        
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
        return generator.generate(exceptionClass, cl, null);
    }
}
