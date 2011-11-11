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

package org.apache.tuscany.sca.binding.http.operationselector.provider;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.tuscany.sca.common.http.HTTPContext;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 * Sets the operation based on the request path.
 * 
 * From a url: http://localhost:8080/HelloworldComponent/Helloworld/sayHello?name=Petra
 * where the component is HelloworldComponent and the service is Helloworld
 * the path will be "/sayHello" so the operation is "sayHello".
 * 
 * TODO: we could also do something similar to how the JMS binding supports
 *       a single "onMessage" method to get all requests, so perhaps this could
 *       also support impls with method: service(HttpServletRequest, HttpServletResponse)
 */
public class HTTPDefaultServiceOperationSelectorInterceptor implements Interceptor {

    private Invoker next;
    private List<Operation> operations;
    
    public HTTPDefaultServiceOperationSelectorInterceptor(RuntimeEndpoint endpoint) {
        Interface serviceInterface = endpoint.getService().getInterfaceContract().getInterface();
        this.operations = serviceInterface.getOperations();
    }

    @Override
    public Message invoke(Message msg) {
        HTTPContext context = msg.getBindingContext();
        HttpServletRequest request = context.getHttpRequest();
        
        Operation operation = findOperation(request.getMethod());
        if(operation == null) {
            operation = findOperation("service");
        }
        
        if(operation == null) {
            throw new ServiceRuntimeException("No matching operation for " + request.getMethod());
        }
        msg.setOperation(operation);
        return next.invoke(msg);        
    }

    @Override
    public void setNext(Invoker next) {
        this.next = next;
    }

    @Override
    public Invoker getNext() {
        return next;
    }

    

    /**
     * Find the operation from the component service contract
     * @param componentService
     * @param method
     * @return
     */
    private Operation findOperation(String method) {
        if (method.contains(".")) {
            method = method.substring(method.lastIndexOf(".") + 1);
        }

        Operation result = null;
        for (Operation o : operations) {
            if (o.isDynamic())
                return o;
            if (o.getName().equalsIgnoreCase(method)) {
                result = o;
                break;
            }
        }

        return result;
    }
}
