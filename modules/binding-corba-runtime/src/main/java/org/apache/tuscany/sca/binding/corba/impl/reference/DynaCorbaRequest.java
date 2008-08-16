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

package org.apache.tuscany.sca.binding.corba.impl.reference;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.sca.binding.corba.impl.exceptions.CorbaException;
import org.apache.tuscany.sca.binding.corba.impl.exceptions.RequestConfigurationException;
import org.apache.tuscany.sca.binding.corba.impl.types.TypeTree;
import org.apache.tuscany.sca.binding.corba.impl.types.TypeTreeCreator;
import org.apache.tuscany.sca.binding.corba.impl.types.util.TypeHelpersProxy;
import org.apache.tuscany.sca.binding.corba.impl.types.util.Utils;
import org.apache.tuscany.sca.binding.corba.impl.util.MethodFinder;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.Object;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;

/**
 * @version $Rev$ $Date$ Represents single CORBA request
 */
public class DynaCorbaRequest {

    private TypeTree returnTree;
    private Map<String, TypeTree> exceptions = new HashMap<String, TypeTree>();
    private InputStream inputStream;
    private ObjectImpl remoteObject;
    private String operation;
    private List<java.lang.Object> arguments = new ArrayList<java.lang.Object>();
    private List<TypeTree> argumentsTypes = new ArrayList<TypeTree>();
    private Class<?> referenceClass;
    private Map<Method, String> operationsMap;
    
    /**
     * Creates request.
     * 
     * @param ObjectremoteObject remote object reference
     * @param operation operation to invoke
     * @param scaBindingRules apply SCA default binding mapping rules
     */
    public DynaCorbaRequest(Object remoteObject, String operation) {
        this.remoteObject = (ObjectImpl)remoteObject;
        this.operation = operation;
    }

    /**
     * Sets class which will be backed by this reference request 
     * @param referenceClass
     */
    public void setReferenceClass(Class<?> referenceClass) {
        this.referenceClass = referenceClass;
    }

    /**
     * Sets method to operation names mapping
     * @param operationsMap
     */
    public void setOperationsMap(Map<Method, String> operationsMap) {
        this.operationsMap = operationsMap;
    }

    /**
     * Adds operation argument - stores arguments and caches its TypeTree. Annotations will be set to null by default.
     * 
     * @param argument
     */
    public void addArgument(java.lang.Object argument) throws RequestConfigurationException {
        addArgument(argument, null);
    }
    
    /**
     * Adds operation argument - stores arguments and caches its TypeTree
     * 
     * @param argument
     */
    public void addArgument(java.lang.Object argument, Annotation[] notes) throws RequestConfigurationException {
        TypeTree tree = TypeTreeCreator.createTypeTree(argument.getClass(), notes);
        argumentsTypes.add(tree);
        arguments.add(argument);
    }

    /**
     * Passing stored arguments to CORBA communication output stream
     * 
     * @param outputStream
     * @throws RequestConfigurationException
     */
    private void passArguments(OutputStream outputStream) throws RequestConfigurationException {
        for (int i = 0; i < arguments.size(); i++) {
            TypeTree tree = argumentsTypes.get(i);
            TypeHelpersProxy.write(tree.getRootNode(), outputStream, arguments.get(i));
        }
    }

    /**
     * Sets return type for operation. Annotations will be set to null by default.
     * 
     * @param forClass
     */
    public void setOutputType(Class<?> forClass) throws RequestConfigurationException {
        setOutputType(forClass, null);
    }
    
    /**
     * Sets return type for operation
     * 
     * @param forClass
     */
    public void setOutputType(Class<?> forClass, Annotation[] notes) throws RequestConfigurationException {
        returnTree = TypeTreeCreator.createTypeTree(forClass, notes);
    }

    /**
     * Configures possible exceptions
     * 
     * @param forClass
     */
    public void addExceptionType(Class<?> forClass) throws RequestConfigurationException {
        TypeTree tree = TypeTreeCreator.createTypeTree(forClass, null);
        String exceptionId = Utils.getTypeId(forClass);
        exceptions.put(exceptionId, tree);
    }

    /**
     * Handles application excpeition.
     * 
     * @param ae occured exception
     * @throws Exception
     */
    private void handleApplicationException(ApplicationException ae) throws Exception {
        try {
            if (exceptions.size() == 0) {
                RequestConfigurationException exception =
                    new RequestConfigurationException(
                                                      "ApplicationException occured, but no exception type was specified.",
                                                      ae.getId());
                throw exception;
            }
            InputStream is = ae.getInputStream();
            String exceptionId = is.read_string();
            TypeTree tree = exceptions.get(exceptionId);
            if (tree == null) {
                RequestConfigurationException exception =
                    new RequestConfigurationException(
                                                      "ApplicationException occured, but no such exception was defined",
                                                      ae.getId());
                throw exception;
            } else {
                Exception ex = (Exception)TypeHelpersProxy.read(tree.getRootNode(), is);
                throw ex;
            }
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Handles exceptions generated by CORBA API
     * 
     * @param se
     */
    private void handleSystemException(SystemException se) throws Exception {
        if (se instanceof BAD_OPERATION) {
            throw new CorbaException("Bad operation name: " + operation, se);
        } else if (se instanceof BAD_PARAM) {
            throw new CorbaException("Bad parameter", se);
        } else {
            // TODO: handle more system exception types
            throw new CorbaException(se.getMessage(), se);
        }
    }

    /**
     * Gets operation name which is includes mapping rules
     * @return
     */
    private String getFinalOperationName() {
        String result = operation;
        if (referenceClass != null) {
            Class<?>[] argumentTypes = new Class<?>[arguments.size()];
            for (int i = 0; i < arguments.size(); i++) {
                argumentTypes[i] = arguments.get(i).getClass();
            }
            Method method = MethodFinder.findMethod(referenceClass, operation, argumentTypes);
            String newOperation = (String)operationsMap.get(method);
            if (newOperation != null) {
                result = newOperation;
            }
        }
        return result;
    }

    /**
     * Invokes previously configured request
     * 
     * @return
     */
    public DynaCorbaResponse invoke() throws Exception {
        DynaCorbaResponse response = new DynaCorbaResponse();
        String finalOperationName = getFinalOperationName();
        OutputStream outputStream = ((ObjectImpl)remoteObject)._request(finalOperationName, true);
        passArguments(outputStream);
        try {
            inputStream = remoteObject._invoke(outputStream);
            if (inputStream != null && returnTree != null) {
                response.setContent(TypeHelpersProxy.read(returnTree.getRootNode(), inputStream));
            }
        } catch (ApplicationException ae) {
            handleApplicationException(ae);
        } catch (SystemException se) {
            handleSystemException(se);
        } catch (Exception e) {
            throw e;
        } finally {
            release();
        }
        return response;
    }

    /**
     * Releases request resources
     */
    private void release() {
        remoteObject._releaseReply(inputStream);
    }

}
