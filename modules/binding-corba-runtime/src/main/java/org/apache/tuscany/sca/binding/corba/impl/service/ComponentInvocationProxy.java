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

package org.apache.tuscany.sca.binding.corba.impl.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.sca.binding.corba.impl.exceptions.RequestConfigurationException;
import org.apache.tuscany.sca.binding.corba.impl.types.TypeTree;
import org.apache.tuscany.sca.binding.corba.impl.types.TypeTreeCreator;
import org.apache.tuscany.sca.binding.corba.impl.util.OperationMapper;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeWire;

/**
 * Invocation proxy for SCA components
 */
public class ComponentInvocationProxy implements InvocationProxy {

    private RuntimeWire wire;
    private RuntimeComponentService service;
    private Map<Method, Operation> methodOperationMapping;
    private Map<String, Method> operationsMap;
    private Map<Operation, OperationTypes> operationsCache = new HashMap<Operation, OperationTypes>();
    private Class<?> javaClass;

    public ComponentInvocationProxy(RuntimeComponentService service, RuntimeWire wire, Class<?> javaClass)
        throws RequestConfigurationException {
        this.wire = wire;
        this.service = service;
        this.javaClass = javaClass;
        operationsMap = OperationMapper.mapOperationToMethod(javaClass);
        createMethod2OperationMapping();
        cacheOperationTypes(service.getInterfaceContract().getInterface().getOperations());
    }

    /**
     * Maps Java methods to Tuscany operations
     */
    private void createMethod2OperationMapping() {
        // for every operation find all methods with the same name, then
        // compare operations and methods parameters
        this.methodOperationMapping = new HashMap<Method, Operation>();
        for (Operation operation : service.getInterfaceContract().getInterface().getOperations()) {
            List<DataType> inputTypes = operation.getInputType().getLogical();
            Method[] methods = javaClass.getMethods();
            for (int i = 0; i < methods.length; i++) {
                if (methods[i].getName().equals(operation.getName()) && inputTypes.size() == methods[i]
                    .getParameterTypes().length) {
                    Class<?>[] parameterTypes = methods[i].getParameterTypes();
                    int j = 0;
                    boolean parameterMatch = true;
                    for (DataType dataType : inputTypes) {
                        if (!dataType.getPhysical().equals(parameterTypes[j])) {
                            parameterMatch = false;
                            break;
                        }
                        j++;
                    }
                    if (parameterMatch) {
                        // match found
                        methodOperationMapping.put(methods[i], operation);
                        break;
                    }
                }
            }

        }
    }

    /**
     * Caches TypeTree for every operation in backed component
     * 
     * @param operations
     * @throws RequestConfigurationException
     */
    private void cacheOperationTypes(List<Operation> operations) throws RequestConfigurationException {
        for (Operation operation : operations) {
            try {
                OperationTypes operationTypes = new OperationTypes();
                List<TypeTree> inputInstances = new ArrayList<TypeTree>();
                // cache output type tree
                if (operation.getOutputType() != null && operation.getOutputType().getPhysical() != null
                    && !operation.getOutputType().getPhysical().equals(void.class)) {
                    TypeTree outputType =
                        TypeTreeCreator.createTypeTree(operation.getOutputType().getPhysical(), false);
                    operationTypes.setOutputType(outputType);
                }
                // cache input types trees
                if (operation.getInputType() != null) {
                    for (DataType<List<DataType>> type : operation.getInputType().getLogical()) {
                        Class<?> forClass = type.getPhysical();
                        TypeTree inputType = TypeTreeCreator.createTypeTree(forClass, false);
                        inputInstances.add(inputType);
                    }

                }
                operationTypes.setInputType(inputInstances);
                operationsCache.put(operation, operationTypes);
            } catch (RequestConfigurationException e) {
                throw e;
            }
        }
    }

    private Operation getOperation4Name(String operationName) {
        Method method = operationsMap.get(operationName);
        return methodOperationMapping.get(method);
    }

    public OperationTypes getOperationTypes(String operationName) {
        return operationsCache.get(getOperation4Name(operationName));
    }

    public Object invoke(String operationName, List<Object> arguments) throws InvocationException {
        Object result = null;
        try {
            result = wire.invoke(getOperation4Name(operationName), arguments.toArray());
        } catch (InvocationTargetException e) {
            InvocationException exception = new InvocationException(e.getCause());
            throw exception;
        }
        return result;
    }

}
