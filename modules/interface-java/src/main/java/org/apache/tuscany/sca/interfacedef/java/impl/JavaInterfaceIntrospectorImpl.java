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
package org.apache.tuscany.sca.interfacedef.java.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.InvalidAnnotationException;
import org.apache.tuscany.sca.interfacedef.InvalidCallbackException;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.InvalidOperationException;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.OverloadedOperationException;
import org.apache.tuscany.sca.interfacedef.impl.DataTypeImpl;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.java.JavaOperation;
import org.apache.tuscany.sca.interfacedef.java.introspect.JavaInterfaceVisitor;
import org.apache.tuscany.sca.interfacedef.util.JavaXMLMapper;
import org.apache.tuscany.sca.interfacedef.util.XMLType;
import org.oasisopen.sca.annotation.OneWay;
import org.oasisopen.sca.annotation.Remotable;

/**
 * Default implementation of a Java interface introspector.
 * 
 * @version $Rev$ $Date$
 */
public class JavaInterfaceIntrospectorImpl {
    public static final String IDL_INPUT = "idl:input";

    private static final String UNKNOWN_DATABINDING = null;

    private JavaInterfaceFactory javaFactory = null;
    private List<JavaInterfaceVisitor> visitors = new ArrayList<JavaInterfaceVisitor>();
    private boolean loadedVisitors;

    public JavaInterfaceIntrospectorImpl(JavaInterfaceFactory javaFactory) {
        this.javaFactory = javaFactory;
    }

    public void introspectInterface(JavaInterface javaInterface, Class<?> clazz) throws InvalidInterfaceException {
        
        if(!loadedVisitors) {
            this.visitors = javaFactory.getInterfaceVisitors();
        }
        
        javaInterface.setJavaClass(clazz);

        boolean remotable = clazz.isAnnotationPresent(Remotable.class);

        // Consider @javax.ejb.Remote, java.rmi.Remote and javax.ejb.EJBObject
        // equivalent to @Remotable
        if (!remotable) {
            for (Annotation annotation : clazz.getAnnotations()) {
                if ("javax.ejb.Remote".equals(annotation.annotationType().getName())) {
                    remotable = true;
                    break;
                }
            }
        }
        if (!remotable) {
            for (Class<?> superInterface : clazz.getInterfaces()) {
                if (Remote.class == superInterface || "javax.ejb.EJBObject".equals(superInterface.getName())) {
                    remotable = true;
                    break;
                }
            }
        }
        
        if (remotable) {
            if (javaInterface.isRemotableSet() && javaInterface.isRemotable() == false) {
                throw new InvalidAnnotationException("@Remotable annotation present in a interface marked as not remotable in the SCDL", Remotable.class);
            }
        } else {
            if (javaInterface.isRemotableSet()) {
                remotable = javaInterface.isRemotable();
            }
        }

        javaInterface.setRemotable(remotable);

        Class<?> callbackClass = null;
        org.oasisopen.sca.annotation.Callback callback = clazz.getAnnotation(org.oasisopen.sca.annotation.Callback.class);
        if (callback != null && !Void.class.equals(callback.value())) {
            callbackClass = callback.value();
            if (remotable && !callbackClass.isAnnotationPresent(Remotable.class)) {
                throw new InvalidCallbackException("Callback " + callbackClass.getName() + 
                		" must be remotable on remotable interface " + clazz.getName());
            }
            if (!remotable && callbackClass.isAnnotationPresent(Remotable.class)) {
                throw new InvalidCallbackException("Callback" + callbackClass.getName() + 
                		" must not be remotable on local interface " + clazz.getName());
            }
        } else if (callback != null && Void.class.equals(callback.value())) {
            throw new InvalidCallbackException("No callback interface specified on callback annotation in " + clazz.getName());
        }

        javaInterface.setCallbackClass(callbackClass);

        String ns = JavaXMLMapper.getNamespace(clazz);
        javaInterface.getOperations().addAll(getOperations(clazz, remotable, ns));

        for (JavaInterfaceVisitor extension : visitors) {
            extension.visitInterface(javaInterface);
        }
    }

    private Class<?>[] getActualTypes(Type[] types, Class<?>[] rawTypes, Map<String, Type> typeBindings) {
        Class<?>[] actualTypes = new Class<?>[types.length];
        for (int i = 0; i < actualTypes.length; i++) {
            actualTypes[i] = getActualType(types[i], rawTypes[i], typeBindings);
        }
        return actualTypes;
    }

    private Class<?> getActualType(Type type, Class<?> rawType, Map<String, Type> typeBindings) {
        if (type instanceof TypeVariable<?>) {
            TypeVariable<?> typeVariable = (TypeVariable<?>)type;
            type = typeBindings.get(typeVariable.getName());
            if (type instanceof Class<?>) {
                return (Class<?>)type;
            }
        }
        return rawType;
    }

    private <T> List<Operation> getOperations(Class<T> clazz,
                                              boolean remotable,
                                              String ns) throws InvalidInterfaceException {

        Set<Type> genericInterfaces = new HashSet<Type>();
        for (Type t : clazz.getGenericInterfaces()) {
            genericInterfaces.add(t);
        }
        Map<String, Type> typeBindings = new HashMap<String, Type>();
        for (Type genericInterface : genericInterfaces) {
            if (genericInterface instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType)genericInterface;
                TypeVariable<?>[] typeVariables = ((Class<?>)parameterizedType.getRawType()).getTypeParameters();
                Type[] typeArguments = parameterizedType.getActualTypeArguments();
                for (int i = 0; i < typeArguments.length; i++) {
                    typeBindings.put(typeVariables[i].getName(), typeArguments[i]);
                }
            }
        }

        Method[] methods = clazz.getMethods();
        List<Operation> operations = new ArrayList<Operation>(methods.length);
        Set<String> names = remotable ? new HashSet<String>() : null;
        for (Method method : methods) {
            if (method.getDeclaringClass() == Object.class) {
                // Skip the methods on the Object.class
                continue;
            }
            String name = method.getName();
            if (remotable && names.contains(name)) {
                throw new OverloadedOperationException(method);
            }
            if (remotable) {
                names.add(name);
            }

            Class<?> returnType = getActualType(method.getGenericReturnType(), method.getReturnType(), typeBindings);
            Class<?>[] parameterTypes =
                getActualTypes(method.getGenericParameterTypes(), method.getParameterTypes(), typeBindings);
            Class<?>[] faultTypes =
                getActualTypes(method.getGenericExceptionTypes(), method.getExceptionTypes(), typeBindings);

            boolean nonBlocking = method.isAnnotationPresent(OneWay.class);
            if (nonBlocking) {
                if (!(returnType == void.class)) {
                    throw new InvalidOperationException(
                                                        "Method should return 'void' when declared with an @OneWay annotation. " + method,
                                                        method);
                }
                if (!(faultTypes.length == 0)) {
                    throw new InvalidOperationException(
                                                        "Method should not declare exceptions with an @OneWay annotation. " + method,
                                                        method);
                }
            }

            // Set outputType to null for void
            XMLType xmlReturnType = new XMLType(new QName(ns, "return"), null);
            DataType<XMLType> returnDataType =
                returnType == void.class ? null : new DataTypeImpl<XMLType>(UNKNOWN_DATABINDING, returnType, method
                    .getGenericReturnType(), xmlReturnType);
            List<DataType> paramDataTypes = new ArrayList<DataType>(parameterTypes.length);
            Type[] genericParamTypes = method.getGenericParameterTypes();
            for (int i = 0; i < parameterTypes.length; i++) {
                Class<?> paramType = parameterTypes[i];
                XMLType xmlParamType = new XMLType(new QName(ns, "arg" + i), null);
                paramDataTypes.add(new DataTypeImpl<XMLType>(UNKNOWN_DATABINDING, paramType, genericParamTypes[i],
                                                             xmlParamType));
            }
            List<DataType> faultDataTypes = new ArrayList<DataType>(faultTypes.length);
            Type[] genericFaultTypes = method.getGenericExceptionTypes();
            for (int i = 0; i < faultTypes.length; i++) {
                Class<?> faultType = faultTypes[i];
                // Only add checked exceptions
                // JAXWS Specification v2.1 section 3.7 says RemoteException should not be mapped
                if (Exception.class.isAssignableFrom(faultType) && (!RuntimeException.class.isAssignableFrom(faultType))
                    && (!RemoteException.class.isAssignableFrom(faultType))) {
                    XMLType xmlFaultType = new XMLType(new QName(ns, faultType.getSimpleName()), null);
                    DataType<XMLType> faultDataType =
                        new DataTypeImpl<XMLType>(UNKNOWN_DATABINDING, faultType, genericFaultTypes[i], xmlFaultType);
                    faultDataTypes.add(new DataTypeImpl<DataType>(UNKNOWN_DATABINDING, faultType, genericFaultTypes[i],
                                                                  faultDataType));
                }
            }

            DataType<List<DataType>> inputType =
                new DataTypeImpl<List<DataType>>(IDL_INPUT, Object[].class, paramDataTypes);
            JavaOperation operation = new JavaOperationImpl();
            operation.setName(name);
            operation.setInputType(inputType);
            operation.setOutputType(returnDataType);
            operation.setFaultTypes(faultDataTypes);
            operation.setNonBlocking(nonBlocking);
            operation.setJavaMethod(method);
            operations.add(operation);
        }
        return operations;
    }

}
