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
package org.apache.tuscany.sca.implementation.java.introspect.impl;

import static org.apache.tuscany.sca.implementation.java.introspect.impl.JavaIntrospectionHelper.getAllInterfaces;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Logger;

import javax.jws.WebService;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.implementation.java.IntrospectionException;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.implementation.java.impl.JavaElementImpl;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.osoa.sca.CallableReference;
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
    private static final Logger logger = Logger.getLogger(ServiceProcessor.class.getName());
    private JavaInterfaceFactory javaFactory;
    
    public ServiceProcessor(AssemblyFactory assemblyFactory, JavaInterfaceFactory javaFactory) {
        super(assemblyFactory);
        this.javaFactory = javaFactory;
    }

    @Override
    public <T> void visitClass(Class<T> clazz, JavaImplementation type) throws IntrospectionException {
        org.osoa.sca.annotations.Service annotation = clazz.getAnnotation(org.osoa.sca.annotations.Service.class);
        if (annotation == null) {
            // scan interfaces for remotable
            Set<Class> interfaces = getAllInterfaces(clazz);
            for (Class<?> interfaze : interfaces) {
                if (interfaze.isAnnotationPresent(Remotable.class) 
                    || interfaze.isAnnotationPresent(WebService.class)
                    || interfaze.isAnnotationPresent(Callback.class)
                    ) {
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
                //throw new IllegalServiceDefinitionException("No interfaces specified");
                logger.warning("Ignoring @Service annotation.  No interfaces specified. class = "+clazz.getName());
            } else {
                interfaces = new Class<?>[1];
                interfaces[0] = interfaze;
            }
        }
        for (Class<?> interfaze : interfaces) {
            try {
                Service service = createService(interfaze);
                type.getServices().add(service);
            } catch (InvalidInterfaceException e) {
                throw new IntrospectionException(e);
            }
        }
    }

    @Override
    public void visitMethod(Method method, JavaImplementation type) throws IntrospectionException {

        Callback annotation = method.getAnnotation(Callback.class);
        if (annotation == null) {
            return;
        }
        if(Modifier.isPrivate(method.getModifiers())) {
            throw new IllegalCallbackReferenceException("Illegal annotation @Callback found on "+method, method);
        }
        if (method.getParameterTypes().length != 1) {
            throw new IllegalCallbackReferenceException("Setter must have one parameter", method);
        }
        JavaElementImpl element = new JavaElementImpl(method, 0);
        createCallback(type, element);
    }

    @Override
    public void visitField(Field field, JavaImplementation type) throws IntrospectionException {

        Callback annotation = field.getAnnotation(Callback.class);
        if (annotation == null) {
            return;
        }
        if(Modifier.isPrivate(field.getModifiers())) {
            throw new IllegalCallbackReferenceException("Illegal annotation @Callback found on "+field, field);
        }
        JavaElementImpl element = new JavaElementImpl(field);
        createCallback(type, element);
    }

    /**
     * @param type
     * @param element
     * @throws IllegalCallbackReferenceException
     */
    private void createCallback(JavaImplementation type, JavaElementImpl element)
        throws IllegalCallbackReferenceException {
        Service callbackService = null;
        Class<?> callbackClass = element.getType();
        Type genericType = element.getGenericType();
        Class<?> baseType = callbackClass;
        if(CallableReference.class.isAssignableFrom(baseType)) {
            // @Callback protected CallableReference<MyCallback> callback;
            // The base type will be MyCallback
            baseType = JavaIntrospectionHelper.getBusinessInterface(baseType, genericType);
        }        
        for (Service service : type.getServices()) {
            JavaInterface javaInterface = (JavaInterface)service.getInterfaceContract().getCallbackInterface();
            if (javaInterface != null && baseType == javaInterface.getJavaClass()) {
                callbackService = service;
            }
        }
        if (callbackService == null) {
            throw new IllegalCallbackReferenceException("Callback type does not match a service callback interface: " + type.getName() );
        }
        if(type.getCallbackMembers().get(baseType.getName()) == null) {
            type.getCallbackMembers().put(baseType.getName(), new ArrayList<JavaElementImpl>());
        }
        type.getCallbackMembers().get(baseType.getName()).add(element);
    }

    public Service createService(Class<?> interfaze) throws InvalidInterfaceException {
        Service service = assemblyFactory.createService();
        JavaInterfaceContract interfaceContract = javaFactory.createJavaInterfaceContract();
        service.setInterfaceContract(interfaceContract);

        // create a relative URI
        service.setName(interfaze.getSimpleName());

        JavaInterface callInterface = javaFactory.createJavaInterface(interfaze);
        service.getInterfaceContract().setInterface(callInterface);
        if (callInterface.getCallbackClass() != null) {
            JavaInterface callbackInterface = javaFactory.createJavaInterface(callInterface.getCallbackClass());
            service.getInterfaceContract().setCallbackInterface(callbackInterface);
        }
        return service;
    }

}
