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

import static org.apache.tuscany.implementation.java.introspect.impl.JavaIntrospectionHelper.getAllInterfaces;
import static org.apache.tuscany.implementation.java.introspect.impl.JavaIntrospectionHelper.toPropertyName;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

import org.apache.tuscany.assembly.AssemblyFactory;
import org.apache.tuscany.assembly.Service;
import org.apache.tuscany.implementation.java.JavaImplementation;
import org.apache.tuscany.implementation.java.impl.JavaElementImpl;
import org.apache.tuscany.implementation.java.introspect.IntrospectionException;
import org.apache.tuscany.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.interfacedef.java.JavaInterface;
import org.apache.tuscany.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.interfacedef.java.introspect.JavaInterfaceIntrospector;
import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.Remotable;

/**
 * Processes an {@link org.osoa.sca.annotations.Service} annotation and updates
 * the component type with corresponding {@link Service}s. Also processes
 * related {@link org.osoa.sca.annotations.Callback} annotations.
 * 
 * @version $Rev$ $Date$
 */
public class ServiceProcessor extends BaseJavaClassVisitor {
    private JavaInterfaceIntrospector interfaceIntrospector;
    private JavaInterfaceFactory javaFactory;
    
    public ServiceProcessor(AssemblyFactory assemblyFactory, JavaInterfaceFactory javaFactory, JavaInterfaceIntrospector interfaceIntrospector) {
        super(assemblyFactory);
        this.javaFactory = javaFactory;
        this.interfaceIntrospector = interfaceIntrospector;
    }

    public <T> void visitClass(Class<T> clazz, JavaImplementation type) throws IntrospectionException {
        org.osoa.sca.annotations.Service annotation = clazz.getAnnotation(org.osoa.sca.annotations.Service.class);
        if (annotation == null) {
            // scan intefaces for remotable
            Set<Class> interfaces = getAllInterfaces(clazz);
            for (Class<?> interfaze : interfaces) {
                if (interfaze.isAnnotationPresent(Remotable.class) || interfaze.isAnnotationPresent(Callback.class)) {
                    Service service;
                    try {
                        service = createService(interfaze);
                    } catch (InvalidInterfaceException e) {
                        throw new IntrospectionException(e);
                    }
                    type.getServices().add(service);
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
                throw new InvalidServiceType("Service must be an interface", interfaze);
            }
            Service service;
            try {
                service = createService(interfaze);
            } catch (InvalidInterfaceException e) {
                throw new IntrospectionException(e);
            }
            type.getServices().add(service);
        }
    }

    public void visitMethod(Method method, JavaImplementation type) throws IntrospectionException {

        Callback annotation = method.getAnnotation(Callback.class);
        if (annotation == null) {
            return;
        }
        if (method.getParameterTypes().length != 1) {
            throw new IllegalCallbackReferenceException("Setter must have one parameter", method);
        }
        String name = toPropertyName(method.getName());
        Service callbackService = null;
        Class<?> callbackClass = method.getParameterTypes()[0];
        for (Service service : type.getServices()) {
            JavaInterface javaInterface = (JavaInterface)service.getInterfaceContract().getCallbackInterface();
            if (callbackClass == javaInterface.getJavaClass()) {
                callbackService = service;
            }
        }
        if (callbackService == null) {
            throw new IllegalCallbackReferenceException("Callback type does not match a service callback interface");
        }
        type.getCallbackMembers().put(callbackClass.getName(), new JavaElementImpl(method, 0));
    }

    public void visitField(Field field, JavaImplementation type) throws IntrospectionException {

        Callback annotation = field.getAnnotation(Callback.class);
        if (annotation == null) {
            return;
        }
        String name = field.getName();
        Service callbackService = null;
        Class<?> callbackClass = field.getType();
        for (Service service : type.getServices()) {
            JavaInterface javaInterface = (JavaInterface)service.getInterfaceContract().getCallbackInterface();
            if (callbackClass == javaInterface.getJavaClass()) {
                callbackService = service;
            }
        }
        if (callbackService == null) {
            throw new IllegalCallbackReferenceException("Callback type does not match a service callback interface");
        }
        type.getCallbackMembers().put(callbackClass.getName(), new JavaElementImpl(field));
    }

    public Service createService(Class<?> interfaze) throws InvalidInterfaceException {
        Service service = assemblyFactory.createService();
        JavaInterfaceContract interfaceContract = javaFactory.createJavaInterfaceContract();
        service.setInterfaceContract(interfaceContract);

        // create a relative URI
        service.setName(interfaze.getSimpleName());

        JavaInterface callInterface = interfaceIntrospector.introspect(interfaze);
        service.getInterfaceContract().setInterface(callInterface);
        if (callInterface.getCallbackClass() != null) {
            JavaInterface callbackInterface = interfaceIntrospector.introspect(callInterface.getCallbackClass());
            service.getInterfaceContract().setCallbackInterface(callbackInterface);
        }
        return service;
    }

}
