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

import org.osoa.sca.annotations.OneWay;

import org.apache.tuscany.spi.idl.InvalidServiceContractException;
import org.apache.tuscany.spi.idl.OverloadedOperationException;
import org.apache.tuscany.spi.model.DataType;
import org.apache.tuscany.spi.model.Operation;

/**
 * Basic implementation of an InterfaceJavaIntrospector.
 *
 * @version $Rev$ $Date$
 */
public class InterfaceJavaIntrospectorImpl implements InterfaceJavaIntrospector {
    public <T> JavaServiceContract introspect(Class<T> type) throws InvalidServiceContractException {
        return introspect(type, null);
    }

    public <I, C> JavaServiceContract introspect(Class<I> type, Class<C> callback)
        throws InvalidServiceContractException {
        JavaServiceContract contract = new JavaServiceContract();

        contract.setInterfaceName(type.getName());
        contract.setInterfaceClass(type);
        contract.setOperations(getOperations(type));

        if (callback != null) {
            contract.setCallbackName(callback.getName());
            contract.setCallbackClass(callback);
            contract.setCallbacksOperations(getOperations(callback));
        }
        return contract;
    }

    private <T> Map<String, Operation<Type>> getOperations(Class<T> type) throws OverloadedOperationException {
        Method[] methods = type.getMethods();
        Map<String, Operation<Type>> operations = new HashMap<String, Operation<Type>>(methods.length);
        for (Method method : methods) {
            String name = method.getName();
            if (operations.containsKey(name)) {
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
