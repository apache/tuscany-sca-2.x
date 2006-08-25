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
package org.apache.tuscany.core.idl.java;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.OneWay;
import org.osoa.sca.annotations.Remotable;
import org.osoa.sca.annotations.Scope;

import org.apache.tuscany.spi.idl.InvalidServiceContractException;
import org.apache.tuscany.spi.idl.OverloadedOperationException;
import org.apache.tuscany.spi.model.DataType;
import org.apache.tuscany.spi.model.InteractionScope;
import org.apache.tuscany.spi.model.Operation;

import static org.apache.tuscany.core.util.JavaIntrospectionHelper.getBaseName;

/**
 * Basic implementation of an InterfaceJavaIntrospector.
 *
 * @version $Rev$ $Date$
 */
public class InterfaceJavaIntrospectorImpl implements InterfaceJavaIntrospector {
    public <T> JavaServiceContract introspect(Class<T> type) throws InvalidServiceContractException {
        Class<?> callbackClass = null;
        Callback callback = type.getAnnotation(Callback.class);
        if (callback != null && !Void.class.equals(callback.value())) {
            callbackClass = callback.value();
        } else if (callback != null && Void.class.equals(callback.value())) {
            IllegalCallbackException e =
                new IllegalCallbackException("Callback annotation must specify an interface on service type");
            e.setIdentifier(type.getName());
            throw e;
        }
        return introspect(type, callbackClass);
    }

    public <I, C> JavaServiceContract introspect(Class<I> type, Class<C> callback)
        throws InvalidServiceContractException {
        JavaServiceContract contract = new JavaServiceContract();

        contract.setInterfaceName(getBaseName(type));
        contract.setInterfaceClass(type);
        boolean remotable = type.isAnnotationPresent(Remotable.class);
        contract.setOperations(getOperations(type, remotable));

        if (callback != null) {
            contract.setCallbackName(getBaseName(callback));
            contract.setCallbackClass(callback);
            contract.setCallbacksOperations(getOperations(callback, remotable));
        }

        Scope interactionScope = type.getAnnotation(Scope.class);
        if (interactionScope == null) {
            contract.setInteractionScope(InteractionScope.NONCONVERSATIONAL);
        } else {
            if ("CONVERSATIONAL".equalsIgnoreCase(interactionScope.value())) {
                contract.setInteractionScope(InteractionScope.CONVERSATIONAL);
            } else {
                contract.setInteractionScope(InteractionScope.NONCONVERSATIONAL);
            }
        }
        return contract;
    }

    private <T> Map<String, Operation<Type>> getOperations(Class<T> type, boolean remotable)
        throws OverloadedOperationException {
        Method[] methods = type.getMethods();
        Map<String, Operation<Type>> operations = new HashMap<String, Operation<Type>>(methods.length);
        for (Method method : methods) {
            String name = method.getName();
            if (remotable && operations.containsKey(name)) {
                throw new OverloadedOperationException(method.toString());
            }

            Type returnType = method.getGenericReturnType();
            Type[] paramTypes = method.getGenericParameterTypes();
            Type[] faultTypes = method.getGenericExceptionTypes();
            boolean nonBlocking = method.isAnnotationPresent(OneWay.class);

            DataType<Type> returnDataType = new DataType<Type>(returnType, returnType);
            List<DataType<Type>> paramDataTypes = new ArrayList<DataType<Type>>(paramTypes.length);
            for (Type paramType : paramTypes) {
                paramDataTypes.add(new DataType<Type>(paramType, paramType));
            }
            List<DataType<Type>> faultDataTypes = new ArrayList<DataType<Type>>(faultTypes.length);
            for (Type faultType : faultTypes) {
                faultDataTypes.add(new DataType<Type>(faultType, faultType));
            }
            Operation<Type> operation =
                new Operation<Type>(name, returnDataType, paramDataTypes, faultDataTypes, nonBlocking);
            operations.put(name, operation);
        }
        return operations;
    }
}
