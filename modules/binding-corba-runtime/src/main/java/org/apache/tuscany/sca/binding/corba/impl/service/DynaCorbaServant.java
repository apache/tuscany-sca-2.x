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

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.binding.corba.impl.exceptions.RequestConfigurationException;
import org.apache.tuscany.sca.binding.corba.impl.types.TypeTree;
import org.apache.tuscany.sca.binding.corba.impl.types.TypeTreeCreator;
import org.apache.tuscany.sca.binding.corba.impl.types.util.TypeHelpersProxy;
import org.apache.tuscany.sca.binding.corba.impl.types.util.Utils;
import org.apache.tuscany.sca.binding.corba.impl.util.OperationMapper;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.InvokeHandler;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;

/**
 * General servant which provides target component implementation via CORBA
 */
public class DynaCorbaServant extends ObjectImpl implements InvokeHandler {

    private RuntimeComponentService service;
    private Binding binding;
    private String[] ids;
    private Map<Operation, OperationTypes> operationsCache = new HashMap<Operation, OperationTypes>();
    private Class<?> javaClass;
    private Map<String, Method> operationsMap;
    private Map<Method, Operation> methodOperationMapping;

    public DynaCorbaServant(RuntimeComponentService service, Binding binding) throws RequestConfigurationException {
        this.service = service;
        this.binding = binding;
        this.javaClass = ((JavaInterface)service.getInterfaceContract().getInterface()).getJavaClass();
        this.operationsMap = OperationMapper.mapOperationToMethod(javaClass);
        cacheOperationTypes(service.getInterfaceContract().getInterface().getOperations());
        createMethod2OperationMapping();
        setDefaultIds();
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
                    TypeTree outputType = TypeTreeCreator.createTypeTree(operation.getOutputType().getPhysical());
                    operationTypes.setOutputType(outputType);
                }
                // cache input types trees
                if (operation.getInputType() != null) {
                    for (DataType<List<DataType>> type : operation.getInputType().getLogical()) {
                        Class<?> forClass = type.getPhysical();
                        TypeTree inputType = TypeTreeCreator.createTypeTree(forClass);
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

    /**
     * Sets CORBA object ID
     * @param ids
     */
    public void setIds(String[] ids) {
        for (int i = 0; i < ids.length; i++) {
            if (ids[i] == null || ids[i].length() == 0) {
                // if invalid id was passed then set to default 
                setDefaultIds();
                return;
            }
        }
        this.ids = ids;
    }

    public OutputStream _invoke(String operationName, InputStream in, ResponseHandler rh) {
        Operation operation = null;
        Method method = operationsMap.get(operationName);
        // searching for proper operation
        operation = methodOperationMapping.get(method);
        if (operation == null) {
            // operation wasn't found
            throw new org.omg.CORBA.BAD_OPERATION(0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
        } else {
            List<Object> inputInstances = new ArrayList<Object>();
            OperationTypes types = operationsCache.get(operation);
            try {
                // retrieving in arguments
                for (TypeTree tree : types.getInputType()) {
                    Object o = TypeHelpersProxy.read(tree.getRootNode(), in);
                    inputInstances.add(o);
                }
            } catch (MARSHAL e) {
                // parameter passed by user was not compatible with Java to
                // Corba mapping
                throw new org.omg.CORBA.BAD_PARAM(0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
            }
            try {
                // invocation and sending result
                Object result = service.getRuntimeWire(binding).invoke(operation, inputInstances.toArray());
                OutputStream out = rh.createReply();
                if (types.getOutputType() != null) {
                    TypeTree tree = types.getOutputType();
                    TypeHelpersProxy.write(tree.getRootNode(), out, result);
                }
                return out;
            } catch (InvocationTargetException ie) {
                // handling user exception
                try {
                    OutputStream out = rh.createExceptionReply();
                    Class<?> exceptionClass = ie.getTargetException().getClass();
                    TypeTree tree = TypeTreeCreator.createTypeTree(exceptionClass);
                    String exceptionId = Utils.getTypeId(exceptionClass);
                    out.write_string(exceptionId);
                    TypeHelpersProxy.write(tree.getRootNode(), out, ie.getTargetException());
                    return out;
                } catch (Exception e) {
                    // TODO: raise remote exception - exception while handling
                    // target exception
                    e.printStackTrace();
                }
            } catch (Exception e) {
                // TODO: raise remote exception
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public String[] _ids() {
        return ids;
    }

    /**
     * Sets servant ID to default, based on Java class name
     */
    private void setDefaultIds() {
        String id = Utils.getTypeId(javaClass);
        this.ids = new String[] {id};
    }

}
