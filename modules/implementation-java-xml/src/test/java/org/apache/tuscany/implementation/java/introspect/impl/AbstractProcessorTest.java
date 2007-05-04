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
package org.apache.tuscany.implementation.java.introspect.impl;

import java.lang.reflect.Constructor;

import junit.framework.TestCase;

import org.apache.tuscany.assembly.AssemblyFactory;
import org.apache.tuscany.assembly.impl.DefaultAssemblyFactory;
import org.apache.tuscany.implementation.java.JavaImplementation;
import org.apache.tuscany.implementation.java.impl.JavaConstructorImpl;
import org.apache.tuscany.implementation.java.impl.JavaParameterImpl;
import org.apache.tuscany.implementation.java.introspect.IntrospectionException;
import org.apache.tuscany.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.interfacedef.java.impl.DefaultJavaInterfaceFactory;
import org.apache.tuscany.interfacedef.java.introspect.DefaultJavaInterfaceIntrospector;
import org.apache.tuscany.interfacedef.java.introspect.DefaultJavaInterfaceIntrospectorExtensionPoint;
import org.apache.tuscany.interfacedef.java.introspect.JavaInterfaceIntrospectorExtensionPoint;


/**
 * Base class to simulate the processor sequences
 * 
 * @version $Rev$ $Date$
 */
public class AbstractProcessorTest extends TestCase {
    protected AssemblyFactory factory;
    protected JavaInterfaceFactory javaFactory;
    protected ConstructorProcessor constructorProcessor;
    private ReferenceProcessor referenceProcessor;
    private PropertyProcessor propertyProcessor;
    private ResourceProcessor resourceProcessor;


    protected AbstractProcessorTest() {
        factory = new DefaultAssemblyFactory();
        javaFactory = new DefaultJavaInterfaceFactory();
        JavaInterfaceIntrospectorExtensionPoint visitors = new DefaultJavaInterfaceIntrospectorExtensionPoint();
        referenceProcessor = new ReferenceProcessor(factory, javaFactory, new DefaultJavaInterfaceIntrospector(javaFactory, visitors));
        propertyProcessor = new PropertyProcessor(factory);
        resourceProcessor = new ResourceProcessor(factory);
        constructorProcessor = new ConstructorProcessor(factory);
        referenceProcessor = new ReferenceProcessor(factory, javaFactory, new DefaultJavaInterfaceIntrospector(javaFactory, visitors));
        propertyProcessor = new PropertyProcessor(factory);
    }

    protected <T> void visitConstructor(Constructor<T> constructor,
                                        JavaImplementation type) throws IntrospectionException {
        constructorProcessor.visitConstructor(constructor, type);
        JavaConstructorImpl<?> definition = type.getConstructor();
        if (definition == null) {
            definition = new JavaConstructorImpl<T>(constructor);
            type.getConstructors().put(constructor, definition);
        }
        JavaParameterImpl[] parameters = definition.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            referenceProcessor.visitConstructorParameter(parameters[i], type);
            propertyProcessor.visitConstructorParameter(parameters[i], type);
            resourceProcessor.visitConstructorParameter(parameters[i], type);
            // monitorProcessor.visitConstructorParameter(parameters[i], type);
        }
    }
}
