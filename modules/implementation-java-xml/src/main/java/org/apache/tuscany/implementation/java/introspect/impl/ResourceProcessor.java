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

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import org.apache.tuscany.implementation.java.JavaImplementation;
import org.apache.tuscany.implementation.java.impl.JavaElementImpl;
import org.apache.tuscany.implementation.java.impl.JavaParameterImpl;
import org.apache.tuscany.implementation.java.impl.JavaResourceImpl;
import org.apache.tuscany.implementation.java.introspect.IntrospectionException;
import org.apache.tuscany.sca.assembly.AssemblyFactory;

/**
 * Processes an {@link @Resource} annotation, updating the component type with
 * corresponding {@link org.apache.tuscany.spi.implementation.java.JavaResourceImpl}
 * 
 * @version $Rev$ $Date$
 */
public class ResourceProcessor extends BaseJavaClassVisitor {
    
    public ResourceProcessor(AssemblyFactory factory) {
        super(factory);
    }

    public void visitMethod(Method method, JavaImplementation type) throws IntrospectionException {
        org.apache.tuscany.api.annotation.Resource annotation = method
            .getAnnotation(org.apache.tuscany.api.annotation.Resource.class);
        if (annotation == null) {
            return;
        }
        if (method.getParameterTypes().length != 1) {
            throw new IllegalResourceException("Resource setter must have one parameter", method);
        }
        String name = annotation.name();
        if (name.length() < 1) {
            name = JavaIntrospectionHelper.toPropertyName(method.getName());
        }
        if (type.getResources().get(name) != null) {
            throw new DuplicateResourceException(name);
        }

        String mappedName = annotation.mappedName();
        JavaResourceImpl resource = createResource(name, new JavaElementImpl(method, 0));
        resource.setOptional(annotation.optional());
        if (mappedName.length() > 0) {
            resource.setMappedName(mappedName);
        }
        type.getResources().put(resource.getName(), resource);
    }

    public void visitField(Field field, JavaImplementation type) throws IntrospectionException {

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

        JavaResourceImpl resource = createResource(name, new JavaElementImpl(field));
        resource.setOptional(annotation.optional());
        if (mappedName.length() > 0) {
            resource.setMappedName(mappedName);
        }
        type.getResources().put(resource.getName(), resource);
    }

    @SuppressWarnings("unchecked")
    public JavaResourceImpl createResource(String name, JavaElementImpl element) {
        element.setClassifer(org.apache.tuscany.api.annotation.Resource.class);
        element.setName(name);
        return new JavaResourceImpl(element);
    }

    public void visitConstructorParameter(JavaParameterImpl parameter, JavaImplementation type)
        throws IntrospectionException {
        org.apache.tuscany.api.annotation.Resource resourceAnnotation = parameter
            .getAnnotation(org.apache.tuscany.api.annotation.Resource.class);
        if (resourceAnnotation != null) {
            String name = resourceAnnotation.name();
            if ("".equals(name)) {
                name = parameter.getName();
            }
            if ("".equals(name)) {
                throw new InvalidResourceException("Missing resource name", (Member)parameter.getAnchor());
            }

            if (!"".equals(parameter.getName()) && !name.equals(parameter.getName())) {
                throw new InvalidConstructorException("Mismatched resource name: " + parameter);
            }

            if (type.getResources().get(name) != null) {
                throw new DuplicateResourceException(name);
            }

            String mappedName = resourceAnnotation.mappedName();

            JavaResourceImpl resource = createResource(name, parameter);
            resource.setOptional(resourceAnnotation.optional());
            if (mappedName.length() > 0) {
                resource.setMappedName(mappedName);
            }
            type.getResources().put(resource.getName(), resource);
        }
    }

}
