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
package org.apache.tuscany.core.implementation;

import static org.apache.tuscany.core.util.JavaIntrospectionHelper.getAllPublicAndProtectedFields;
import static org.apache.tuscany.core.util.JavaIntrospectionHelper.getAllUniquePublicProtectedMethods;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.implementation.java.ConstructorDefinition;
import org.apache.tuscany.spi.implementation.java.ImplementationProcessor;
import org.apache.tuscany.spi.implementation.java.IntrospectionRegistry;
import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.implementation.java.Parameter;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.implementation.java.ProcessingException;

import org.apache.tuscany.core.util.JavaIntrospectionHelper;

/**
 * Default implementation of the <code>IntrospectionRegistry</code>
 * 
 * @version $Rev$ $Date$
 */
public class IntrospectionRegistryImpl implements IntrospectionRegistry {

    private Monitor monitor;
    private List<ImplementationProcessor> processors = new ArrayList<ImplementationProcessor>();

    public IntrospectionRegistryImpl() {
    }

    public IntrospectionRegistryImpl(Monitor monitor) {
        this.monitor = monitor;
    }

    @org.apache.tuscany.api.annotation.Monitor
    public void setMonitor(Monitor monitor) {
        this.monitor = monitor;
    }

    public void registerProcessor(ImplementationProcessor processor) {
        monitor.register(processor);
        processors.add(processor);
    }

    public void unregisterProcessor(ImplementationProcessor processor) {
        monitor.unregister(processor);
        processors.remove(processor);
    }

    public PojoComponentType introspect(Class<?> clazz,
                                        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                                        DeploymentContext context) throws ProcessingException {
        for (ImplementationProcessor processor : processors) {
            processor.visitClass(clazz, type, context);
        }

        for (Constructor<?> constructor : clazz.getConstructors()) {
            for (ImplementationProcessor processor : processors) {
                processor.visitConstructor(constructor, type, context);
                // Assuming the visitClass or visitConstructor will populate the type.getConstructors
                ConstructorDefinition<?> definition = type.getConstructors().get(constructor);
                if (definition != null) {
                    for (Parameter p : definition.getParameters()) {
                        processor.visitConstructorParameter(p, type, context);
                    }
                }
            }
        }

        Set<Method> methods = getAllUniquePublicProtectedMethods(clazz);
        for (Method method : methods) {
            for (ImplementationProcessor processor : processors) {
                processor.visitMethod(method, type, context);
            }
        }

        Set<Field> fields = getAllPublicAndProtectedFields(clazz);
        for (Field field : fields) {
            for (ImplementationProcessor processor : processors) {
                processor.visitField(field, type, context);
            }
        }

        Class superClass = clazz.getSuperclass();
        if (superClass != null) {
            visitSuperClass(superClass, type, context);
        }

        for (ImplementationProcessor processor : processors) {
            processor.visitEnd(clazz, type, context);
        }
        return type;
    }

    private void visitSuperClass(Class<?> clazz,
                                 PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                                 DeploymentContext context) throws ProcessingException {
        if (!Object.class.equals(clazz)) {
            for (ImplementationProcessor processor : processors) {
                processor.visitSuperClass(clazz, type, context);
            }
            clazz = clazz.getSuperclass();
            if (clazz != null) {
                visitSuperClass(clazz, type, context);
            }
        }
    }

    public static interface Monitor {
        void register(ImplementationProcessor processor);

        void unregister(ImplementationProcessor processor);

        void processing(ImplementationProcessor processor);
    }
}
