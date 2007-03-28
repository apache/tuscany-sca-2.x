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

import static org.apache.tuscany.core.util.JavaIntrospectionHelper.getAllInterfaces;
import static org.apache.tuscany.core.util.JavaIntrospectionHelper.toPropertyName;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Set;

import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.idl.InvalidServiceContractException;
import org.apache.tuscany.spi.implementation.java.ImplementationProcessorExtension;
import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.implementation.java.ProcessingException;
import org.apache.tuscany.spi.model.ServiceContract;
import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.Remotable;

/**
 * Processes an {@link org.osoa.sca.annotations.Service} annotation and updates
 * the component type with corresponding {@link JavaMappedService}s. Also
 * processes related {@link org.osoa.sca.annotations.Callback} annotations.
 * 
 * @version $Rev$ $Date$
 */
public class ServiceProcessor extends ImplementationProcessorExtension {

    public <T> void visitClass(Class<T> clazz,
                               PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                               DeploymentContext context) throws ProcessingException {
        org.osoa.sca.annotations.Service annotation = clazz.getAnnotation(org.osoa.sca.annotations.Service.class);
        if (annotation == null) {
            // scan intefaces for remotable
            Set<Class> interfaces = getAllInterfaces(clazz);
            for (Class<?> interfaze : interfaces) {
                if (interfaze.isAnnotationPresent(Remotable.class) || interfaze.isAnnotationPresent(Callback.class)) {
                    JavaMappedService service;
                    try {
                        service = createService(interfaze);
                    } catch (InvalidServiceContractException e) {
                        throw new ProcessingException(e);
                    }
                    type.getServices().put(service.getUri().getFragment(), service);
                }
            }
            return;
        }
        Class<?>[] interfaces = annotation.interfaces();
        if (interfaces.length == 0) {
            Class<?> interfaze = annotation.value();
            if (Void.class.equals(interfaze)) {
                throw new IllegalServiceDefinitionException("No interfaces specified");
            } else {
                interfaces = new Class<?>[1];
                interfaces[0] = interfaze;
            }
        }
        for (Class<?> interfaze : interfaces) {
            if (!interfaze.isInterface()) {
                throw new InvalidServiceType("Service must be an interface", interfaze.getName());
            }
            JavaMappedService service;
            try {
                service = createService(interfaze);
            } catch (InvalidServiceContractException e) {
                throw new ProcessingException(e);
            }
            type.getServices().put(service.getUri().getFragment(), service);
        }
    }

    public void visitMethod(Method method,
                            PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                            DeploymentContext context) throws ProcessingException {

        Callback annotation = method.getAnnotation(Callback.class);
        if (annotation == null) {
            return;
        }
        if (method.getParameterTypes().length != 1) {
            throw new IllegalCallbackReferenceException("Setter must have one parameter", method.toString());
        }
        String name = toPropertyName(method.getName());
        JavaMappedService callbackService = null;
        Class<?> callbackClass = method.getParameterTypes()[0];
        for (JavaMappedService service : type.getServices().values()) {
            ServiceContract serviceContract = service.getServiceContract();
            if (serviceContract.getCallbackClass().equals(callbackClass)) {
                callbackService = service;
            }
        }
        if (callbackService == null) {
            throw new IllegalCallbackReferenceException("Callback type does not match a service callback interface");
        }
        callbackService.setCallbackReferenceName(name);
        callbackService.setCallbackMember(method);
    }

    public void visitField(Field field,
                           PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                           DeploymentContext context) throws ProcessingException {

        Callback annotation = field.getAnnotation(Callback.class);
        if (annotation == null) {
            return;
        }
        String name = field.getName();
        JavaMappedService callbacksService = null;
        Class<?> callbackClass = field.getType();
        for (JavaMappedService service : type.getServices().values()) {
            ServiceContract serviceContract = service.getServiceContract();
            if (serviceContract.getCallbackClass().equals(callbackClass)) {
                callbacksService = service;
            }
        }
        if (callbacksService == null) {
            throw new IllegalCallbackReferenceException("Callback type does not match a service callback interface");
        }
        callbacksService.setCallbackReferenceName(name);
        callbacksService.setCallbackMember(field);
    }

    public JavaMappedService createService(Class<?> interfaze) throws InvalidServiceContractException {
        JavaMappedService service = new JavaMappedService();
        // create a relative URI
        service.setUri(URI.create("#" + interfaze.getSimpleName()));
        service.setRemotable(interfaze.getAnnotation(Remotable.class) != null);
        ServiceContract<?> contract = interfaceProcessorRegistry.introspect(interfaze);
        service.setServiceContract(contract);
        return service;
    }

}
