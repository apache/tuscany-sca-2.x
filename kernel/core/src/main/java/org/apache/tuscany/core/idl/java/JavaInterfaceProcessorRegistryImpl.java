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
import org.osoa.sca.annotations.Conversational;
import org.osoa.sca.annotations.EndsConversation;
import org.osoa.sca.annotations.OneWay;
import org.osoa.sca.annotations.Remotable;

import org.apache.tuscany.spi.idl.InvalidConversationalOperationException;
import org.apache.tuscany.spi.idl.InvalidServiceContractException;
import org.apache.tuscany.spi.idl.OverloadedOperationException;
import org.apache.tuscany.spi.idl.java.JavaInterfaceProcessor;
import org.apache.tuscany.spi.idl.java.JavaInterfaceProcessorRegistry;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.spi.model.DataType;
import org.apache.tuscany.spi.model.Operation;
import static org.apache.tuscany.spi.model.Operation.CONVERSATION_END;
import static org.apache.tuscany.spi.model.Operation.NO_CONVERSATION;

import static org.apache.tuscany.core.util.JavaIntrospectionHelper.getBaseName;

/**
 * Default implementation of an InterfaceJavaIntrospector.
 *
 * @version $Rev$ $Date$
 */
public class JavaInterfaceProcessorRegistryImpl implements JavaInterfaceProcessorRegistry {
    public static final String IDL_INPUT = "idl:input";

    private static final String UNKNOWN_DATABINDING = null;

    private List<JavaInterfaceProcessor> processors = new ArrayList<JavaInterfaceProcessor>();

    public JavaInterfaceProcessorRegistryImpl() {
    }

    public void registerProcessor(JavaInterfaceProcessor processor) {
        processors.add(processor);
    }

    public void unregisterProcessor(JavaInterfaceProcessor processor) {
        processors.remove(processor);
    }

    public <T> JavaServiceContract<T> introspect(Class<T> type) throws InvalidServiceContractException {
        Class<?> callbackClass = null;
        Callback callback = type.getAnnotation(Callback.class);
        if (callback != null && !Void.class.equals(callback.value())) {
            callbackClass = callback.value();
        } else if (callback != null && Void.class.equals(callback.value())) {
            throw new IllegalCallbackException("No callback interface specified on annotation", type.getName());
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

        for (JavaInterfaceProcessor processor : processors) {
            processor.visitInterface(type, callback, contract);
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
                new DataType<List<DataType<Type>>>(IDL_INPUT, Object[].class, paramDataTypes);
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

}
