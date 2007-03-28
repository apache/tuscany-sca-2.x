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
package org.apache.tuscany.core.implementation.processor;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.implementation.java.ConstructorDefinition;
import org.apache.tuscany.spi.implementation.java.ImplementationProcessorExtension;
import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.implementation.java.Parameter;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.implementation.java.ProcessingException;

/**
 * Handles processing of a constructor decorated with
 * {@link org.osoa.sca.annotations.Constructor}
 * 
 * @version $Rev$ $Date$
 */
@SuppressWarnings("unchecked")
public class ConstructorProcessor extends ImplementationProcessorExtension {
    // private List<ImplementationProcessorExtension> paramProcessors = new
    // ArrayList<ImplementationProcessorExtension>();

    public ConstructorProcessor() {
        // paramProcessors.add(new ReferenceProcessor());
        // paramProcessors.add(new PropertyProcessor());
        // paramProcessors.add(new ResourceProcessor());
    }

    public <T> void visitClass(Class<T> clazz,
                               PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                               DeploymentContext context) throws ProcessingException {
        Constructor[] ctors = clazz.getConstructors();
        boolean found = false;
        for (Constructor constructor : ctors) {
            ConstructorDefinition<?> definition = new ConstructorDefinition(constructor);
            type.getConstructors().put(constructor, definition);
            if (constructor.getAnnotation(org.osoa.sca.annotations.Constructor.class) != null) {
                if (found) {
                    String name = constructor.getDeclaringClass().getName();
                    throw new DuplicateConstructorException("Multiple constructors marked with @Constructor", name);
                }
                found = true;
                type.setConstructorDefinition(definition);
            }
        }
    }

    public <T> void visitConstructor(Constructor<T> constructor,
                                     PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                                     DeploymentContext context) throws ProcessingException {
        org.osoa.sca.annotations.Constructor annotation = constructor
            .getAnnotation(org.osoa.sca.annotations.Constructor.class);
        if (annotation == null) {
            return;
        }
        ConstructorDefinition<?> definition = type.getConstructorDefinition();
        if (definition == null) {
            definition = new ConstructorDefinition(constructor);
            type.setConstructorDefinition(definition);
        }
        Parameter[] parameters = definition.getParameters();
        String[] value = annotation.value();
        boolean isDefault = value.length == 0 || (value.length == 1 && "".equals(value[0]));
        if (!isDefault && value.length != parameters.length) {
            throw new InvalidConstructorException("Nubmer of names in @Constructor doesn't match the actual parameters");
        }
        for (int i = 0; i < parameters.length; i++) {
            parameters[i].setName(i < value.length ? value[i] : "");
        }
        type.setConstructorDefinition(definition);
        // for (ImplementationProcessorExtension processor : paramProcessors) {
        // processor.setInterfaceProcessorRegistry(interfaceProcessorRegistry);
        // processor.setRegistry(registry);
        // processor.visitConstructor(constructor, type, context);
        // }
    }
}
