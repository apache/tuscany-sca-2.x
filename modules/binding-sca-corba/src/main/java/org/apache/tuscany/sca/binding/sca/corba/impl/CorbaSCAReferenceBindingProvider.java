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

package org.apache.tuscany.sca.binding.sca.corba.impl;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.binding.corba.impl.CorbaInvoker;
import org.apache.tuscany.sca.binding.corba.impl.util.OperationMapper;
import org.apache.tuscany.sca.host.corba.CorbaHost;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.omg.CORBA.Object;

/**
 * Reference binding provider for SCA default binding over CORBA binding
 */
public class CorbaSCAReferenceBindingProvider implements ReferenceBindingProvider {

    private static final Logger logger = Logger.getLogger(CorbaSCAReferenceBindingProvider.class.getName());

    private SCABinding binding;
    private CorbaHost host;
    private RuntimeComponentReference reference;
    private Object remoteObject;
    private Class<?> referenceClass;
    private Map<Method, String> operationsMap = null;

    public CorbaSCAReferenceBindingProvider(SCABinding binding,
                                            CorbaHost host,
                                            RuntimeComponentReference reference) {
        this.binding = binding;
        this.host = host;
        this.reference = reference;
        this.referenceClass = ((JavaInterface)reference.getInterfaceContract().getInterface()).getJavaClass();
        operationsMap = OperationMapper.mapMethodToOperation(referenceClass);
    }

    public InterfaceContract getBindingInterfaceContract() {
        return reference.getInterfaceContract();
    }

    public boolean supportsOneWayInvocation() {
        return false;
    }

    public Invoker createInvoker(Operation operation) {
        try {
            if (remoteObject == null) {
                remoteObject = host.lookup(binding.getURI());
            }
            return new CorbaInvoker(remoteObject, referenceClass, operationsMap, true);
        } catch (Exception e) {
            logger.warning(e.getMessage());
        }
        return null;
    }

    public void start() {

    }

    public void stop() {

    }

}
