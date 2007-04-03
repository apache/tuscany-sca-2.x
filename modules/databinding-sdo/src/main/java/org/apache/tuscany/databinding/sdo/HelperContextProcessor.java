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
package org.apache.tuscany.databinding.sdo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.implementation.java.ImplementationProcessorExtension;
import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.implementation.java.ProcessingException;
import org.apache.tuscany.spi.implementation.java.Resource;
import org.osoa.sca.annotations.Reference;

import commonj.sdo.helper.HelperContext;

/**
 * Processes {@link @HelperContext} annotations on a component implementation
 * and adds a {@link JavaMappedProperty} to the component type which will be
 * used to inject the appropriate context
 * 
 * @version $Rev$ $Date$
 */
public class HelperContextProcessor extends ImplementationProcessorExtension {
    private HelperContextRegistry registry;
    
    /**
     * @param registry
     */
    public HelperContextProcessor(@Reference HelperContextRegistry registry) {
        super();
        this.registry = registry;
    }

    /**
     * Takes a setter or getter method name and converts it to a property name
     * according to JavaBean conventions. For example, <code>setFoo(var)</code>
     * is returned as property <code>foo<code>
     */
    public static String toPropertyName(String name) {
        if (!name.startsWith("set")) {
            return name;
        }
        return Character.toLowerCase(name.charAt(3)) + name.substring(4);
    }

    public void visitMethod(Method method,
                            PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                            DeploymentContext context) throws ProcessingException {
        if (!method.isAnnotationPresent(org.apache.tuscany.databinding.sdo.api.HelperContext.class)) {
            return;
        }
        if (method.getParameterTypes().length != 1) {
            throw new IllegalArgumentException("HelperContext setter must have one parameter: " + method);
        }
        Class<?> paramType = method.getParameterTypes()[0];
        if (HelperContext.class == paramType) {
            String name = toPropertyName(method.getName());
            Resource<HelperContext> resource = new Resource<HelperContext>(name, HelperContext.class, method);
            resource.setObjectFactory(new HelperContextFactory(context.getComponentId()));
            type.getResources().put(name, resource);
        }
    }

    public void visitField(Field field,
                           PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                           DeploymentContext context) throws ProcessingException {
        if (!field.isAnnotationPresent(org.apache.tuscany.databinding.sdo.api.HelperContext.class)) {
            return;
        }
        Class<?> paramType = field.getType();
        if (HelperContext.class == paramType) {
            String name = field.getName();
            Resource<HelperContext> resource = new Resource<HelperContext>(name, HelperContext.class, field);
            resource.setObjectFactory(new HelperContextFactory(context.getComponentId()));
            type.getResources().put(name, resource);
        }
    }

    private class HelperContextFactory implements ObjectFactory<HelperContext> {
        private URI id;

        public HelperContextFactory(URI id) {
            super();
            this.id = id;
        }

        public HelperContext getInstance() throws ObjectCreationException {
            return registry.getHelperContext(id);
        }

    }
}
