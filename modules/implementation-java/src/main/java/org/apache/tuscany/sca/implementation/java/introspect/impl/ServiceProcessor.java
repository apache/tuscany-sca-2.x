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

import static org.apache.tuscany.sca.implementation.java.introspect.JavaIntrospectionHelper.getAllInterfaces;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.jws.WebService;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.implementation.java.IntrospectionException;
import org.apache.tuscany.sca.implementation.java.JavaElementImpl;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.implementation.java.introspect.BaseJavaClassVisitor;
import org.apache.tuscany.sca.implementation.java.introspect.JavaIntrospectionHelper;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.oasisopen.sca.ServiceReference;
import org.oasisopen.sca.annotation.Callback;
import org.oasisopen.sca.annotation.Remotable;

/**
 * Processes an {@link org.oasisopen.sca.annotation.Service} annotation and updates
 * the component type with corresponding {@link Service}s. Also processes
 * related {@link org.oasisopen.sca.annotation.Callback} annotations.
 * 
 * @version $Rev$ $Date$
 */
public class ServiceProcessor extends BaseJavaClassVisitor {
    
    public ServiceProcessor(AssemblyFactory assemblyFactory, JavaInterfaceFactory javaFactory) {
        super(assemblyFactory);
        this.javaInterfaceFactory = javaFactory;
    }
    
    public ServiceProcessor(ExtensionPointRegistry registry) {
        super(registry);
    }

    @Override
    public <T> void visitClass(Class<T> clazz, JavaImplementation type) throws IntrospectionException {
        org.oasisopen.sca.annotation.Service annotation = clazz.getAnnotation(org.oasisopen.sca.annotation.Service.class);
        if (annotation == null) {
            // scan interfaces for remotable
            Set<Class<?>> interfaces = getAllInterfaces(clazz);
            for (Class<?> interfaze : interfaces) {
                if (interfaze.isAnnotationPresent(Remotable.class) 
                    || interfaze.isAnnotationPresent(WebService.class)
                    || interfaze.isAnnotationPresent(Callback.class)
                    ) {
                    Service service;
                    try {
                        service = createService(clazz, interfaze, null);
                    } catch (InvalidInterfaceException e) {
                        throw new IntrospectionException(e);
                    }
                    type.getServices().add(service);
                }
            }
            return;
        }
        
        if (annotation.value().length == 0) {
            throw new IntrospectionException("[JCA90059] The array of interfaces or classes specified by the value attribute of the @Service annotation MUST contain at least one element");
        }
        Class<?>[] interfaces = annotation.value();
        if (annotation.names().length > 0) {
            if (annotation.names().length != interfaces.length) {
                throw new IntrospectionException("[JCA90050] The number of Strings in the names attribute array of the @Service annotation MUST match the number of elements in the value attribute array");
            }
            Set<String> names = new HashSet<String>();
            names.addAll(Arrays.asList(annotation.names()));
            if (names.size() != annotation.names().length) {
                throw new IntrospectionException("[JCA90060] The value of each element in the @Service names array MUST be unique amongst all the other element values in the array");
            }
        }

        //validate no scope on servce interface
        for (Class<?> iface : interfaces) {
            if (iface.getAnnotation(org.oasisopen.sca.annotation.Scope.class) != null) {
                throw new IntrospectionException("[JCA90041] @Scope annotation not allowed on service interface " + iface
                    .getName());
            }
        }
        
        //validate service methods implemented
        Method[] ms = clazz.getMethods();
        for (Class<?> iface : interfaces) {
            for (Method m : iface.getMethods()) {
                if (!hasMethod(m, ms)) {
                    throw new IntrospectionException("[JCA90042,JCI20002] Implementation missing service method " + m.getName() + " service interface " + iface.getName());
                }
            }
        }
        
        for (int i=0; i < interfaces.length; i++) {
            try {
                String name = (annotation.names().length > 0) ? annotation.names()[i] : null;
                Service service = createService(clazz, interfaces[i], name);
                type.getServices().add(service);
            } catch (InvalidInterfaceException e) {
                throw new IntrospectionException(e);
            }
        }
        
    }

    protected boolean hasMethod(Method m1, Method[] ms) {
        for (Method m2 : ms) {
            if (JavaIntrospectionHelper.exactMethodMatch(m1, m2)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void visitMethod(Method method, JavaImplementation type) throws IntrospectionException {

        Callback annotation = method.getAnnotation(Callback.class);
        if (annotation == null) {
            return;
        }
        
        if (!(annotation.value() == null || annotation.value() == Void.class)) {
            throw new IllegalCallbackReferenceException("[JCA90046] @Callback on field of method must not have any parameters: " + type.getName() + "." + method.getName());
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
        if (!(annotation.value() == null || annotation.value() == Void.class)) {
            throw new IllegalCallbackReferenceException("[JCA90046] @Callback on field of method must not have any parameters: " + type.getName() + "." + field.getName());
        }
        if(Modifier.isPrivate(field.getModifiers())) {
            throw new IllegalCallbackReferenceException("Illegal annotation @Callback found on "+field, field);
        }
        JavaElementImpl element = new JavaElementImpl(field);
        createCallback(type, element);
    }

    public Service createService(Class<?> clazz, Class<?> interfaze, String name) throws InvalidInterfaceException {
        Service service = assemblyFactory.createService();
        JavaInterfaceContract interfaceContract = javaInterfaceFactory.createJavaInterfaceContract();
        service.setInterfaceContract(interfaceContract);

        if (name == null) {
            service.setName(interfaze.getSimpleName());
        } else {
            service.setName(name);
        }

        JavaInterface callInterface = javaInterfaceFactory.createJavaInterface(interfaze);
        boolean remotable = clazz.getAnnotation(Remotable.class) != null;
        if (remotable){
            callInterface.setRemotable(true);
        }
        service.getInterfaceContract().setInterface(callInterface);
        
        if (callInterface.getCallbackClass() != null) {
            JavaInterface callbackInterface = javaInterfaceFactory.createJavaInterface(callInterface.getCallbackClass());
            if (remotable){
                callbackInterface.setRemotable(true);
            }
            service.getInterfaceContract().setCallbackInterface(callbackInterface);
        }
        return service;
    }
    
    /**
     * Utility methods
     */


    /**
     * @param type
     * @param element
     * @throws IllegalCallbackReferenceException
     */
    private static void createCallback(JavaImplementation type, JavaElementImpl element)
        throws IllegalCallbackReferenceException {
        Service callbackService = null;
        Class<?> callbackClass = element.getType();
        Type genericType = element.getGenericType();
        Class<?> baseType = callbackClass;
        if(ServiceReference.class.isAssignableFrom(baseType)) {
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
}
