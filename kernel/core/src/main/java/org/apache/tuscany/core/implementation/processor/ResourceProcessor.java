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

import static org.apache.tuscany.core.util.JavaIntrospectionHelper.toPropertyName;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.implementation.java.ImplementationProcessorExtension;
import org.apache.tuscany.spi.implementation.java.JavaElement;
import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.implementation.java.Parameter;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.implementation.java.ProcessingException;
import org.apache.tuscany.spi.implementation.java.Resource;

/**
 * Processes an {@link @Resource} annotation, updating the component type with
 * corresponding {@link org.apache.tuscany.spi.implementation.java.Resource}
 * 
 * @version $Rev$ $Date$
 */
public class ResourceProcessor extends ImplementationProcessorExtension {

    public ResourceProcessor() {
    }

    public void visitMethod(Method method,
                            PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                            DeploymentContext context) throws ProcessingException {
        org.apache.tuscany.api.annotation.Resource annotation = method
            .getAnnotation(org.apache.tuscany.api.annotation.Resource.class);
        if (annotation == null) {
            return;
        }
        if (method.getParameterTypes().length != 1) {
            throw new IllegalResourceException("Resource setter must have one parameter", method.toString());
        }
        String name = annotation.name();
        if (name.length() < 1) {
            name = toPropertyName(method.getName());
        }
        if (type.getResources().get(name) != null) {
            throw new DuplicateResourceException(name);
        }

        String mappedName = annotation.mappedName();
        Resource<?> resource = createResource(name, new JavaElement(method, 0));
        resource.setOptional(annotation.optional());
        if (mappedName.length() > 0) {
            resource.setMappedName(mappedName);
        }
        type.add(resource);
    }

    public void visitField(Field field,
                           PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                           DeploymentContext context) throws ProcessingException {

        org.apache.tuscany.api.annotation.Resource annotation = field
            .getAnnotation(org.apache.tuscany.api.annotation.Resource.class);
        if (annotation == null) {
            return;
        }
        String name = annotation.name();
        if (name.length() < 1) {
            name = field.getName();
        }
        if (type.getResources().get(name) != null) {
            throw new DuplicateResourceException(name);
        }

        String mappedName = annotation.mappedName();

        Resource<?> resource = createResource(name, new JavaElement(field));
        resource.setOptional(annotation.optional());
        if (mappedName.length() > 0) {
            resource.setMappedName(mappedName);
        }
        type.add(resource);
    }

    @SuppressWarnings("unchecked")
    public <T> Resource<T> createResource(String name, JavaElement element) {
        return new Resource<T>(name, (Class<T>)element.getType(), (Member)element.getAnchor());
    }

    public void visitConstructorParameter(Parameter parameter,
                                          PojoComponentType<JavaMappedService, 
                                          JavaMappedReference, JavaMappedProperty<?>> type,
                                          DeploymentContext context) throws ProcessingException {
        org.apache.tuscany.api.annotation.Resource resourceAnnotation = parameter
            .getAnnotation(org.apache.tuscany.api.annotation.Resource.class);
        if (resourceAnnotation != null) {
            String name = resourceAnnotation.name();
            if ("".equals(name)) {
                name = parameter.getName();
            }
            if ("".equals(name)) {
                throw new InvalidResourceException("Missing resource name", parameter.toString());
            }

            if (!"".equals(parameter.getName()) && !name.equals(parameter.getName())) {
                throw new InvalidConstructorException("Mismatched resource name: " + parameter);
            }

            if (type.getResources().get(name) != null) {
                throw new DuplicateResourceException(name);
            }

            String mappedName = resourceAnnotation.mappedName();

            Resource<?> resource = createResource(name, parameter);
            resource.setOptional(resourceAnnotation.optional());
            if (mappedName.length() > 0) {
                resource.setMappedName(mappedName);
            }
            type.add(resource);
            parameter.setClassifer(org.apache.tuscany.api.annotation.Resource.class);
            parameter.setName(name);
        }
    }

}
