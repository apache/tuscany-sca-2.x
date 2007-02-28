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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Collection;

import org.osoa.sca.annotations.Reference;

import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.idl.InvalidServiceContractException;
import org.apache.tuscany.spi.idl.java.JavaInterfaceProcessorRegistry;
import org.apache.tuscany.spi.implementation.java.ImplementationProcessorExtension;
import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.implementation.java.ProcessingException;
import org.apache.tuscany.spi.model.Multiplicity;
import org.apache.tuscany.spi.model.ServiceContract;

import static org.apache.tuscany.core.util.JavaIntrospectionHelper.toPropertyName;

/**
 * Processes an {@link @Reference} annotation, updating the component type with corresponding {@link
 * org.apache.tuscany.spi.implementation.java.JavaMappedReference}
 *
 * @version $Rev$ $Date$
 */
public class ReferenceProcessor extends ImplementationProcessorExtension {

    private JavaInterfaceProcessorRegistry regsitry;

    public ReferenceProcessor(@Reference JavaInterfaceProcessorRegistry registry) {
        this.regsitry = registry;
    }

    public void visitMethod(
        Method method,
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
        DeploymentContext context) throws ProcessingException {
        Reference annotation = method.getAnnotation(Reference.class);
        if (annotation == null) {
            return; // Not a reference annotation.
        }
        if (method.getParameterTypes().length != 1) {
            throw new IllegalReferenceException("Setter must have one parameter", method.toString());
        }
        String name = null;
        if (annotation.name() != null && annotation.name().length() > 0) {
            name = annotation.name();
        }
        boolean required = annotation.required();
        if (name == null) {
            name = toPropertyName(method.getName());
        }
        if (type.getReferences().get(name) != null) {
            throw new DuplicateReferenceException(name);
        }

        JavaMappedReference reference = new JavaMappedReference();
        reference.setMember(method);
        reference.setRequired(required);
        reference.setUri(URI.create("#" + name));
        ServiceContract contract;
        try {
            Class<?> rawType = method.getParameterTypes()[0];
            if (rawType.isArray() || Collection.class.isAssignableFrom(rawType)) {
                if (required) {
                    reference.setMultiplicity(Multiplicity.ONE_N);
                } else {
                    reference.setMultiplicity(Multiplicity.ZERO_N);
                }
            } else {
                if (required) {
                    reference.setMultiplicity(Multiplicity.ONE_ONE);
                } else {
                    reference.setMultiplicity(Multiplicity.ZERO_ONE);
                }
            }
            Class<?> baseType = getBaseType(rawType, method.getGenericParameterTypes()[0]);
            contract = regsitry.introspect(baseType);
        } catch (InvalidServiceContractException e) {
            throw new ProcessingException(e);
        }
        reference.setServiceContract(contract);
        type.getReferences().put(name, reference);
    }

    public void visitField(
        Field field,
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
        DeploymentContext context) throws ProcessingException {
        Reference annotation = field.getAnnotation(Reference.class);
        if (annotation == null) {
            return;
        }
        String name = field.getName();
        if (annotation.name() != null) {
            name = annotation.name();
        }
        boolean required = annotation.required();
        if (name.length() == 0) {
            name = field.getName();
        }
        if (type.getReferences().get(name) != null) {
            throw new DuplicateReferenceException(name);
        }
        JavaMappedReference reference = new JavaMappedReference();
        reference.setMember(field);
        reference.setRequired(required);
        reference.setUri(URI.create("#" + name));
        ServiceContract contract;
        try {
            Class<?> rawType = field.getType();
            if (rawType.isArray() || Collection.class.isAssignableFrom(rawType)) {
                if (required) {
                    reference.setMultiplicity(Multiplicity.ONE_N);
                } else {
                    reference.setMultiplicity(Multiplicity.ZERO_N);
                }
            } else {
                if (required) {
                    reference.setMultiplicity(Multiplicity.ONE_ONE);
                } else {
                    reference.setMultiplicity(Multiplicity.ZERO_ONE);
                }
            }
            Class<?> baseType = getBaseType(rawType, field.getGenericType());
            contract = regsitry.introspect(baseType);
        } catch (InvalidServiceContractException e) {
            throw new ProcessingException(e);
        }
        reference.setServiceContract(contract);
        type.getReferences().put(name, reference);
    }

    public <T> void visitConstructor(
        Constructor<T> constructor,
        PojoComponentType<JavaMappedService,
            JavaMappedReference, JavaMappedProperty<?>> type,
        DeploymentContext context) throws ProcessingException {

    }
}
