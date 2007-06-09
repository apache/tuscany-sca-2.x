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
package org.apache.tuscany.binding.ejb.java2idl;

import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * IDL Operation.
 */
public class OperationType extends IDLType {

    /**
     * The Method that this OperationType is mapping.
     */
    private Method method;
    /**
     * The mapped exceptions of this operation.
     */
    private ExceptionType[] mappedExceptions;
    /**
     * The parameters of this operation.
     */
    private ParameterType[] parameters;

    OperationType(Method method) {
        super(method.getName());
        this.method = method;
        // Check if valid return type, IF it is a remote interface.
        Class retCls = method.getReturnType();
        if (retCls.isInterface() && Remote.class.isAssignableFrom(retCls))
            IDLUtil.isValidRMIIIOP(retCls);
        // Analyze exceptions
        Class[] ex = method.getExceptionTypes();
        boolean gotRemoteException = false;
        ArrayList a = new ArrayList();
        for (int i = 0; i < ex.length; ++i) {
            if (ex[i].isAssignableFrom(java.rmi.RemoteException.class))
                gotRemoteException = true;
            if (Exception.class.isAssignableFrom(ex[i]) && !RuntimeException.class.isAssignableFrom(ex[i])
                && !RemoteException.class.isAssignableFrom(ex[i]))
                a.add(ExceptionType.getExceptionType(ex[i])); // map this
        }
        if (!gotRemoteException && Remote.class.isAssignableFrom(method.getDeclaringClass()))
            throw new IDLViolationException(
                                            "All interface methods must throw java.rmi.RemoteException, " + "or a superclass of java.rmi.RemoteException, but method "
                                                + getJavaName()
                                                + " of interface "
                                                + method.getDeclaringClass().getName()
                                                + " does not.", "1.2.3");
        mappedExceptions = new ExceptionType[a.size()];
        mappedExceptions = (ExceptionType[])a.toArray(mappedExceptions);
        // Analyze parameters
        Class[] params = method.getParameterTypes();
        parameters = new ParameterType[params.length];
        for (int i = 0; i < params.length; ++i) {
            parameters[i] = new ParameterType("param" + (i + 1), params[i]);
        }
    }

    /**
     * Return my Java return type.
     */
    public Class getReturnType() {
        return method.getReturnType();
    }

    /**
     * Return my mapped Method.
     */
    public Method getMethod() {
        return method;
    }

    /**
     * Return my mapped exceptions.
     */
    public ExceptionType[] getMappedExceptions() {
        return (ExceptionType[])mappedExceptions.clone();
    }

    /**
     * Return my parameters.
     */
    public ParameterType[] getParameters() {
        return (ParameterType[])parameters.clone();
    }
}
