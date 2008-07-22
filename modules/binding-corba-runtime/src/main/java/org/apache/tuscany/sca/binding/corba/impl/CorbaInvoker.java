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

package org.apache.tuscany.sca.binding.corba.impl;

import java.lang.reflect.Method;
import java.util.Map;

import org.apache.tuscany.sca.binding.corba.impl.exceptions.RequestConfigurationException;
import org.apache.tuscany.sca.binding.corba.impl.reference.DynaCorbaRequest;
import org.apache.tuscany.sca.binding.corba.impl.reference.DynaCorbaResponse;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.omg.CORBA.Object;
import org.osoa.sca.ServiceRuntimeException;

/**
 * @version $Rev$ $Date$
 */
public class CorbaInvoker implements Invoker {

    private Object remoteObject;
    private Class<?> referenceClass;
    private Map<Method, String> operationsMap;
    private boolean scaBindingRules;

    public CorbaInvoker(Object remoteObject, Class<?> referenceClass, Map<Method, String> operationsMap, boolean scaBindingRules) {
        this.remoteObject = remoteObject;
        this.referenceClass = referenceClass;
        this.operationsMap = operationsMap;
        this.scaBindingRules = scaBindingRules;
    }

    /**
     * @see org.apache.tuscany.sca.invocation.Invoker#invoke(org.apache.tuscany.sca.invocation.Message)
     */
    public Message invoke(Message msg) {
        try {
            DynaCorbaRequest request = new DynaCorbaRequest(remoteObject, msg.getOperation().getName(), scaBindingRules);
            request.setReferenceClass(referenceClass);
            request.setOperationsMap(operationsMap);
            if (msg.getOperation().getOutputType() != null) {
                request.setOutputType(msg.getOperation().getOutputType().getPhysical());
            }
            java.lang.Object[] args = msg.getBody();
            if (args != null) {
                for (int i = 0; i < args.length; i++) {
                    request.addArgument(args[i]);
                }
            }
            if (msg.getOperation().getFaultTypes() != null) {
                for (DataType type : msg.getOperation().getFaultTypes()) {
                    request.addExceptionType(type.getPhysical());
                }
            }
            DynaCorbaResponse response = request.invoke();
            msg.setBody(response.getContent());
        } catch (RequestConfigurationException e) {
            throw new ServiceRuntimeException(e);
        } catch (Exception e) {
            msg.setFaultBody(e);
        }
        return msg;
    }

}
