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
package org.apache.tuscany.sca.binding.ejb;

import org.apache.tuscany.sca.binding.ejb.util.EJBHandler;
import org.apache.tuscany.sca.binding.ejb.util.NamingEndpoint;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;

/**
 * EJBTargetInvoker
 */
public class EJBTargetInvoker implements Invoker {

    private Operation operation;
    private String location;
    private Class serviceInterface;

    public EJBTargetInvoker(EJBBinding ejbBinding, Class serviceInterface, Operation operation) {
        this.serviceInterface = serviceInterface;
        this.location = ejbBinding.getUri();
        this.operation = operation;
    }

    public Message invoke(Message msg) {
        try {
            Object resp = doInvoke(msg.getBody());
            msg.setBody(resp);
        } catch (Throwable e) {
            e.printStackTrace();
            msg.setBody(e);
        }
        return msg;
    }

    /**
     * Invoke a EJB operation
     * 
     * @param payload
     * @return
     */
    public Object doInvoke(final Object payload) {

        // construct NamingendPoint
        NamingEndpoint endpoint = getNamingEndpoint();

        // lookup home and ejb stub
        EJBHandler ejbHandler = new EJBHandler(endpoint, serviceInterface);

        String methodName = operation.getName();

        // invoke business method on ejb
        Object response = ejbHandler.invoke(methodName, (Object[])payload);

        return response;
    }

    protected NamingEndpoint getNamingEndpoint() {
        return new NamingEndpoint(location);
    }

}
