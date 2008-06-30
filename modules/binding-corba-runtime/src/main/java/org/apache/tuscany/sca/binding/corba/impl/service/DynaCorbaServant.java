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
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Operation;
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

    private static String[] DEFAULT_IDS = {"IDL:default:1.0"};
    private RuntimeComponentService service;
    private Binding binding;
    private String[] ids = DEFAULT_IDS;
    private Map<String, OperationTypes> operationsCache = new HashMap<String, OperationTypes>();

    public DynaCorbaServant(RuntimeComponentService service, Binding binding) throws RequestConfigurationException {
        this.service = service;
        this.binding = binding;
        cacheOperationTypes(service.getInterfaceContract().getInterface().getOperations());

    }

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
                operationsCache.put(operation.getName(), operationTypes);
            } catch (RequestConfigurationException e) {
                throw e;
            }
        }
    }

    public void setIds(String[] ids) {
        for (int i = 0; i < ids.length; i++) {
            if (ids[i] == null || ids[i].length() == 0) {
                this.ids = DEFAULT_IDS;
                return;
            }
        }
        this.ids = ids;
    }

    public OutputStream _invoke(String method, InputStream in, ResponseHandler rh) {

        Operation operation = null;

        List<Operation> operations = service.getInterfaceContract().getInterface().getOperations();
        // searching for proper operation
        for (Operation oper : operations) {
            if (oper.getName().equals(method)) {
                operation = oper;
                break;
            }
        }
        if (operation == null) {
            // operation wasn't found
            throw new org.omg.CORBA.BAD_OPERATION(0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
        } else {
            List<Object> inputInstances = new ArrayList<Object>();
            OperationTypes types = operationsCache.get(operation.getName());
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
                if (types.getOutputType() != null) {
                    OutputStream out = rh.createReply();
                    TypeTree tree = types.getOutputType();
                    TypeHelpersProxy.write(tree.getRootNode(), out, result);
                    return out;
                }
            } catch (InvocationTargetException ie) {
                // handling user exception
                try {
                    OutputStream out = rh.createExceptionReply();
                    Class<?> exceptionClass = ie.getTargetException().getClass();
                    TypeTree tree = TypeTreeCreator.createTypeTree(exceptionClass);
                    String exceptionId = Utils.getExceptionId(exceptionClass);
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

}
