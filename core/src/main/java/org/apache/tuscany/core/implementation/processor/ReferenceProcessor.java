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

import org.osoa.sca.annotations.Reference;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.idl.InvalidServiceContractException;
import org.apache.tuscany.spi.implementation.java.ImplementationProcessorSupport;
import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.implementation.java.ProcessingException;
import org.apache.tuscany.spi.model.ServiceContract;

import org.apache.tuscany.core.idl.java.InterfaceJavaIntrospector;
import static org.apache.tuscany.core.util.JavaIntrospectionHelper.toPropertyName;

/**
 * Processes an {@link @Reference} annotation, updating the component type with corresponding {@link
 * org.apache.tuscany.spi.implementation.java.JavaMappedReference}
 *
 * @version $Rev$ $Date$
 */
public class ReferenceProcessor extends ImplementationProcessorSupport {

    private InterfaceJavaIntrospector implService;

    public ReferenceProcessor(@Autowire InterfaceJavaIntrospector implService) {
        this.implService = implService;
    }

    public void visitMethod(CompositeComponent<?> parent, Method method,
                            PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                            DeploymentContext context)
        throws ProcessingException {
        Reference annotation = method.getAnnotation(Reference.class);
        boolean autowire = method.getAnnotation(Autowire.class) != null;
        if (annotation == null && !autowire) {
            return;
        }
        if (method.getParameterTypes().length != 1) {
            IllegalReferenceException e = new IllegalReferenceException("Setter must have one parameter");
            e.setIdentifier(method.toString());
            throw e;
        }
        String name = null;
        boolean required = false;
        if (annotation != null) {
            if (annotation.name() != null && annotation.name().length() > 0) {
                name = annotation.name();
            }
            required = annotation.required();
        }
        if (name == null) {
            name = method.getName();
            if (method.getName().startsWith("set")) {
                name = toPropertyName(method.getName());
            }
        }
        if (type.getReferences().get(name) != null) {
            throw new DuplicateReferenceException(name);
        }
        JavaMappedReference reference = new JavaMappedReference();
        reference.setMember(method);
        reference.setAutowire(autowire);
        reference.setRequired(required);
        reference.setName(name);
        ServiceContract contract;
        try {
            contract = implService.introspect(method.getParameterTypes()[0]);
        } catch (InvalidServiceContractException e) {
            throw new ProcessingException(e);
        }
//        ServiceContract contract = new JavaServiceContract();
//        Class<?> interfaceType = method.getParameterTypes()[0];
//        String interfaceName = getBaseName(interfaceType);
//        contract.setInterfaceName(interfaceName);
//        contract.setInterfaceClass(interfaceType);
        reference.setServiceContract(contract);
        type.getReferences().put(name, reference);
//        try {
//            implService.processCallback(interfaceType, contract);
//        } catch (IllegalCallbackException e) {
//            throw new ProcessingException(e);
//        }
    }

    public void visitField(CompositeComponent<?> parent, Field field,
                           PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                           DeploymentContext context) throws ProcessingException {
        Reference annotation = field.getAnnotation(Reference.class);
        boolean autowire = field.getAnnotation(Autowire.class) != null;
        if (annotation == null && !autowire) {
            return;
        }
        String name = field.getName();
        boolean required = false;
        if (annotation != null) {
            if (annotation.name() != null) {
                name = annotation.name();
            }
            required = annotation.required();
        }
        if (name.length() == 0) {
            name = field.getName();
        }
        if (type.getReferences().get(name) != null) {
            throw new DuplicateReferenceException(name);
        }
        JavaMappedReference reference = new JavaMappedReference();
        reference.setMember(field);
        reference.setRequired(required);
        reference.setAutowire(autowire);
        reference.setName(name);
        ServiceContract contract;
        try {
            contract = implService.introspect(field.getType());
        } catch (InvalidServiceContractException e) {
            throw new ProcessingException(e);
        }

//        ServiceContract contract = new JavaServiceContract();
//        Class<?> interfaceType = field.getType();
//        String interfaceName = getBaseName(interfaceType);
//        contract.setInterfaceName(interfaceName);
//        contract.setInterfaceClass(interfaceType);
        reference.setServiceContract(contract);
        type.getReferences().put(name, reference);
//        try {
//            implService.processCallback(interfaceType, contract);
//        } catch (IllegalCallbackException e) {
//            throw new ProcessingException(e);
//        }

    }

    public void visitConstructor(CompositeComponent<?> parent, Constructor<?> constructor,
                                 PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                                 DeploymentContext context) throws ProcessingException {

    }


}
