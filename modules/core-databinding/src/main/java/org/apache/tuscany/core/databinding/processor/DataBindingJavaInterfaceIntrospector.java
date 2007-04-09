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
package org.apache.tuscany.core.databinding.processor;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.interfacedef.DataType;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.interfacedef.OverloadedOperationException;
import org.apache.tuscany.spi.databinding.DataBinding;
import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.Conversational;
import org.osoa.sca.annotations.EndsConversation;
import org.osoa.sca.annotations.OneWay;
import org.osoa.sca.annotations.Remotable;

/**
 * Default implementation of an InterfaceJavaIntrospector.
 *
 * @version $Rev$ $Date$
 */
public class DataBindingJavaInterfaceIntrospector implements org.apache.tuscany.interfacedef.java.introspection.JavaInterfaceIntrospector {
    private static final String UNKNOWN_DATABINDING = null;

    public DataBindingJavaInterfaceIntrospector() {
    }

    public <T> JavaServiceContract<T> introspect(Class<T> type) throws InvalidServiceContractException {
        Class<?> callbackClass = null;
        Callback callback = type.getAnnotation(Callback.class);
        if (callback != null && !Void.class.equals(callback.value())) {
            callbackClass = callback.value();
        } else if (callback != null && Void.class.equals(callback.value())) {
            throw new IllegalArgumentException("No callback interface specified on annotation" + type.getName());
        }
        return introspect(type, callbackClass);
    }

    public <I, C> JavaServiceContract<I> introspect(Class<I> type, Class<C> callback)
        throws InvalidServiceContractException {
        JavaServiceContract<I> contract = new JavaServiceContract<I>();
        contract.setInterfaceName(getBaseName(type));
        contract.setInterfaceClass(type);
        boolean remotable = type.isAnnotationPresent(Remotable.class);
        contract.setRemotable(remotable);
        //Scope interactionScope = type.getAnnotation(Scope.class);
        boolean conversational = type.isAnnotationPresent(Conversational.class);
        contract.setConversational(conversational);
        contract.setOperations(getOperations(type, remotable, conversational));

        if (callback != null) {
            contract.setCallbackName(getBaseName(callback));
            contract.setCallbackClass(callback);
            contract.setCallbackOperations(getOperations(callback, remotable, conversational));
        }
        return contract;
    }

    private <T> Map<String, Operation<Type>> getOperations(Class<T> type, boolean remotable, boolean conversational)
        throws InvalidServiceContractException {
        Method[] methods = type.getMethods();
        Map<String, Operation<Type>> operations = new HashMap<String, Operation<Type>>(methods.length);
        for (Method method : methods) {
            String name = method.getName();
            if (remotable && operations.containsKey(name)) {
                throw new OverloadedOperationException(method);
            }

            Type returnType = method.getGenericReturnType();
            Type[] paramTypes = method.getGenericParameterTypes();
            Type[] faultTypes = method.getGenericExceptionTypes();
            boolean nonBlocking = method.isAnnotationPresent(OneWay.class);
            int conversationSequence = NO_CONVERSATION;
            if (method.isAnnotationPresent(EndsConversation.class)) {
                if (!conversational) {
                    throw new InvalidConversationalOperationException(
                        "Method is marked as end conversation but contract is not conversational",
                        method.getDeclaringClass().getName(),
                        method);
                }
                conversationSequence = CONVERSATION_END;
            } else if (conversational) {
                conversationSequence = Operation.CONVERSATION_CONTINUE;
            }

            DataType<Type> returnDataType = new DataType<Type>(UNKNOWN_DATABINDING, returnType, returnType);
            List<DataType<Type>> paramDataTypes = new ArrayList<DataType<Type>>(paramTypes.length);
            for (Type paramType : paramTypes) {
                paramDataTypes.add(new DataType<Type>(UNKNOWN_DATABINDING, paramType, paramType));
            }
            List<DataType<Type>> faultDataTypes = new ArrayList<DataType<Type>>(faultTypes.length);
            for (Type faultType : faultTypes) {
                faultDataTypes.add(new DataType<Type>(UNKNOWN_DATABINDING, faultType, faultType));
            }

            DataType<List<DataType<Type>>> inputType =
                new DataType<List<DataType<Type>>>(DataBinding.IDL_INPUT, Object[].class, paramDataTypes);
            Operation<Type> operation = new Operation<Type>(name,
                inputType,
                returnDataType,
                faultDataTypes,
                nonBlocking,
                UNKNOWN_DATABINDING,
                conversationSequence);
            operations.put(name, operation);
        }
        return operations;
    }
    
    /**
     * Returns the simple name of a class - i.e. the class name devoid of its package qualifier
     *
     * @param implClass the implmentation class
     */
    public static String getBaseName(Class<?> implClass) {
        String baseName = implClass.getName();
        int lastDot = baseName.lastIndexOf('.');
        if (lastDot != -1) {
            baseName = baseName.substring(lastDot + 1);
        }
        return baseName;
    }    

}
