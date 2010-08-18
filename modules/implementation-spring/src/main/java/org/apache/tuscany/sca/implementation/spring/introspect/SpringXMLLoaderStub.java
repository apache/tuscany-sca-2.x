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

package org.apache.tuscany.sca.implementation.spring.introspect;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.apache.tuscany.sca.implementation.spring.SpringBeanElement;
import org.apache.tuscany.sca.implementation.spring.SpringSCAPropertyElement;
import org.apache.tuscany.sca.implementation.spring.SpringSCAReferenceElement;
import org.apache.tuscany.sca.implementation.spring.SpringSCAServiceElement;

/**
 * This is the Tuscany side stub for the corresponding runtime tie class.
 * It enables the Tuscany code to invoke methods on a Spring context without
 * needing to know about any Spring classes. See the SpringContextTie class
 * in the implementation-spring-runtime module for what the tie does. 
 */
public class SpringXMLLoaderStub {
    private final static String TIE = "org.apache.tuscany.sca.implementation.spring.processor.SpringXMLLoaderTie";

    private static Method createApplicationContext;

    public SpringXMLLoaderStub() throws Exception {
        synchronized (SpringXMLLoaderStub.class) {
            if (createApplicationContext == null) {
                Class<?> tieClass = Class.forName(TIE, false, Thread.currentThread().getContextClassLoader());
                createApplicationContext =
                    tieClass.getMethod("createApplicationContext", Object.class, ClassLoader.class, List.class);
            }
        }
    }

    public Object createApplicationContext(Object scaParentContext, ClassLoader classLoader, List<URL> resources)
        throws Exception {
        return createApplicationContext.invoke(null, scaParentContext, classLoader, resources);
    }

    public void introspect(List<URL> resources,
                           List<SpringSCAServiceElement> serviceElements,
                           List<SpringSCAReferenceElement> referenceElements,
                           List<SpringSCAPropertyElement> propertyElements,
                           List<SpringBeanElement> beanElements) throws Exception {
        Object appContext = createApplicationContext(null, Thread.currentThread().getContextClassLoader(), resources);
        Class<?> cls = appContext.getClass();
        Method method = cls.getMethod("getElements", Class.class);
        SpringSCAServiceElement[] serviceArray =
            (SpringSCAServiceElement[])method.invoke(appContext, SpringSCAServiceElement.class);
        serviceElements.addAll(Arrays.asList(serviceArray));

        SpringSCAReferenceElement[] referenceArray =
            (SpringSCAReferenceElement[])method.invoke(appContext, SpringSCAReferenceElement.class);
        referenceElements.addAll(Arrays.asList(referenceArray));

        SpringSCAPropertyElement[] propertyArray =
            (SpringSCAPropertyElement[])method.invoke(appContext, SpringSCAPropertyElement.class);
        propertyElements.addAll(Arrays.asList(propertyArray));

        SpringBeanElement[] beanArray = (SpringBeanElement[])method.invoke(appContext, SpringBeanElement.class);
        beanElements.addAll(Arrays.asList(beanArray));
    }
}
