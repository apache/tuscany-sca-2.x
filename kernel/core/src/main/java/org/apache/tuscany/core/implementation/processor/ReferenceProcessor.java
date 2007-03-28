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
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Collection;

import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.idl.InvalidServiceContractException;
import org.apache.tuscany.spi.implementation.java.ImplementationProcessorExtension;
import org.apache.tuscany.spi.implementation.java.JavaElement;
import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.implementation.java.Parameter;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.implementation.java.ProcessingException;
import org.apache.tuscany.spi.model.Multiplicity;
import org.apache.tuscany.spi.model.ServiceContract;
import org.osoa.sca.annotations.Reference;

/**
 * Processes an {@link @Reference} annotation, updating the component type with
 * corresponding {@link
 * org.apache.tuscany.spi.implementation.java.JavaMappedReference}
 * 
 * @version $Rev$ $Date$
 */
public class ReferenceProcessor extends ImplementationProcessorExtension {

    public void visitMethod(Method method,
                            PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                            DeploymentContext context) throws ProcessingException {
        Reference annotation = method.getAnnotation(Reference.class);
        if (annotation == null) {
            return; // Not a reference annotation.
        }
        if (method.getParameterTypes().length != 1) {
            throw new IllegalReferenceException("Setter must have one parameter", method.toString());
        }
        String name = annotation.name();
        if ("".equals(name)) {
            name = toPropertyName(method.getName());
        }
        if (type.getReferences().get(name) != null) {
            throw new DuplicateReferenceException(name);
        }

        JavaElement element = new JavaElement(method, 0);
        JavaMappedReference reference = createReference(element, name);
        type.getReferences().put(name, reference);
    }

    public void visitField(Field field,
                           PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                           DeploymentContext context) throws ProcessingException {
        Reference annotation = field.getAnnotation(Reference.class);
        if (annotation == null) {
            return;
        }
        String name = annotation.name();
        if ("".equals(name)) {
            name = field.getName();
        }
        if (type.getReferences().get(name) != null) {
            throw new DuplicateReferenceException(name);
        }
        JavaElement element = new JavaElement(field);
        JavaMappedReference reference = createReference(element, name);
        type.getReferences().put(name, reference);
    }

    public void visitConstructorParameter(Parameter parameter,
                                          PojoComponentType<JavaMappedService, 
                                          JavaMappedReference, JavaMappedProperty<?>> type,
                                          DeploymentContext context) throws ProcessingException {
        Reference refAnnotation = parameter.getAnnotation(Reference.class);
        if (refAnnotation == null) {
            return;
        }
        String paramName = parameter.getName();
        String name = getReferenceName(paramName, parameter.getIndex(), refAnnotation.name());
        if (type.getReferences().get(name) != null) {
            throw new DuplicateReferenceException(name);
        }
        JavaMappedReference reference = createReference(parameter, name);
        type.getReferences().put(name, reference);
        parameter.setClassifer(Reference.class);
        parameter.setName(name);
    }

    private String getReferenceName(String paramName, int pos, String name) throws InvalidConstructorException {
        if ("".equals(name)) {
            name = paramName;
        }
        if ("".equals(name)) {
            return "_ref" + pos;
        }
        if (!"".equals(paramName) && !name.equals(paramName)) {
            throw new InvalidConstructorException("Mismatching names specified for reference parameter " + pos);
        } else {
            return name;
        }
    }

    private JavaMappedReference createReference(JavaElement element, String name) throws ProcessingException {
        JavaMappedReference reference = new JavaMappedReference();
        reference.setMember((Member)element.getAnchor());
        boolean required = false;
        Reference ref = element.getAnnotation(Reference.class);
        if (ref != null) {
            required = ref.required();
        }
        reference.setRequired(required);
        reference.setUri(URI.create("#" + name));
        ServiceContract contract;
        try {
            Class<?> rawType = element.getType();
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
            Type genericType = element.getGenericType();
            Class<?> baseType = getBaseType(rawType, genericType);
            contract = interfaceProcessorRegistry.introspect(baseType);
        } catch (InvalidServiceContractException e) {
            throw new ProcessingException(e);
        }
        reference.setServiceContract(contract);
        return reference;
    }
}
