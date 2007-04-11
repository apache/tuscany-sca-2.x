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
import org.apache.tuscany.implementation.java.impl.ConstructorDefinition;
import org.apache.tuscany.implementation.java.impl.JavaImplementationDefinition;
import org.apache.tuscany.implementation.java.impl.Parameter;
import org.apache.tuscany.implementation.java.introspect.ProcessingException;
import org.apache.tuscany.implementation.java.introspect.impl.ConstructorProcessor;
import org.apache.tuscany.implementation.java.introspect.impl.PropertyProcessor;
import org.apache.tuscany.implementation.java.introspect.impl.ReferenceProcessor;
import org.apache.tuscany.implementation.java.introspect.impl.ResourceProcessor;
import org.apache.tuscany.interfacedef.java.introspect.DefaultJavaInterfaceIntrospector;


/**
 * Base class to simulate the processor sequences
 * 
 * @version $Rev$ $Date$
 */
public class AbstractProcessorTest extends TestCase {
    protected AssemblyFactory factory = new DefaultAssemblyFactory();
    protected ConstructorProcessor constructorProcessor;
    private ReferenceProcessor referenceProcessor = new ReferenceProcessor();
    private PropertyProcessor propertyProcessor = new PropertyProcessor();
    private ResourceProcessor resourceProcessor = new ResourceProcessor();
    // private MonitorProcessor monitorProcessor = new MonitorProcessor(new NullMonitorFactory());


    protected AbstractProcessorTest() {
        constructorProcessor = new ConstructorProcessor();
        referenceProcessor = new ReferenceProcessor();
        referenceProcessor.setInterfaceVisitorExtensionPoint(new DefaultJavaInterfaceIntrospector());
        propertyProcessor = new PropertyProcessor();
    }

    protected <T> void visitConstructor(Constructor<T> constructor,
                                        JavaImplementationDefinition type) throws ProcessingException {
        constructorProcessor.visitConstructor(constructor, type);
        ConstructorDefinition<?> definition = type.getConstructorDefinition();
        if (definition == null) {
            definition = new ConstructorDefinition<T>(constructor);
            type.getConstructors().put(constructor, definition);
        }
        Parameter[] parameters = definition.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            referenceProcessor.visitConstructorParameter(parameters[i], type);
            propertyProcessor.visitConstructorParameter(parameters[i], type);
            resourceProcessor.visitConstructorParameter(parameters[i], type);
            // monitorProcessor.visitConstructorParameter(parameters[i], type);
        }
    }
}
