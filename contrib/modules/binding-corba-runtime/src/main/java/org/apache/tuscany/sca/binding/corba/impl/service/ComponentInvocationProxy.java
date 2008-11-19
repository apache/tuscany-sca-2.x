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

import java.lang.annotation.Annotation;
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
 * @version $Rev$ $Date$
 * Invocation proxy for SCA components
 */
public class ComponentInvocationProxy implements InvocationProxy {

    private RuntimeWire wire;
    private Map<Method, Operation> methodOperationMapping;
    private Map<Operation, Method> operationMethodMapping;
    private Map<String, Method> operationsMap;
    private Map<Operation, OperationTypes> operationsCache = new HashMap<Operation, OperationTypes>();

    public ComponentInvocationProxy(RuntimeComponentService service, RuntimeWire wire, Class<?> javaClass)
        throws RequestConfigurationException {
        this.wire = wire;
        operationsMap = OperationMapper.mapOperationNameToMethod(javaClass);
        operationMethodMapping = OperationMapper.mapOperationToMethod(service.getInterfaceContract().getInterface().getOperations(), javaClass);
        methodOperationMapping = OperationMapper.mapMethodToOperation(service.getInterfaceContract().getInterface().getOperations(), javaClass);
        cacheOperationTypes(service.getInterfaceContract().getInterface().getOperations());
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
                    Annotation[] notes = operationMethodMapping.get(operation).getAnnotations();
                    TypeTree outputType =
                        TypeTreeCreator.createTypeTree(operation.getOutputType().getPhysical(), notes);
                    operationTypes.setOutputType(outputType);
                }
                // cache input types trees
                if (operation.getInputType() != null) {
                    Method method = operationMethodMapping.get(operation);
                    Annotation[][] notes = method.getParameterAnnotations();
                    int i = 0;
                    for (DataType<List<DataType<?>>> type : operation.getInputType().getLogical()) {
                        Class<?> forClass = type.getPhysical();
                        TypeTree inputType = TypeTreeCreator.createTypeTree(forClass, notes[i]);
                        inputInstances.add(inputType);
                        i++;
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
