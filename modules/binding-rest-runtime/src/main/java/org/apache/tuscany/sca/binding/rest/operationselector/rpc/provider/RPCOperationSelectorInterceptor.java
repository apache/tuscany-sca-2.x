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

package org.apache.tuscany.sca.binding.rest.operationselector.rpc.provider;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.QueryParam;

import org.apache.tuscany.sca.common.http.HTTPContext;
import org.apache.tuscany.sca.common.http.HTTPUtil;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaOperation;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;

/**
 * RPC operation selector Interceptor.
 * 
 * @version $Rev$ $Date$
*/
public class RPCOperationSelectorInterceptor implements Interceptor {
    private ExtensionPointRegistry extensionPoints;
    private RuntimeEndpoint endpoint;

    private RuntimeComponentService service;
    private InterfaceContract interfaceContract;
    private List<Operation> serviceOperations;

    private Invoker next;

    public RPCOperationSelectorInterceptor(ExtensionPointRegistry extensionPoints, RuntimeEndpoint endpoint) {
        this.extensionPoints = extensionPoints;
        this.endpoint = endpoint;

        this.service = (RuntimeComponentService)endpoint.getService();
        this.interfaceContract = service.getInterfaceContract();
        this.serviceOperations = service.getInterfaceContract().getInterface().getOperations();
    }

    public Invoker getNext() {
        return next;
    }

    public void setNext(Invoker next) {
        this.next = next;
    }

    public Message invoke(Message msg) {
        try {
            HTTPContext bindingContext = (HTTPContext)msg.getBindingContext();

            String path = URLDecoder.decode(HTTPUtil.getRequestPath(bindingContext.getHttpRequest()), "UTF-8");

            if (path.startsWith("/")) {
                path = path.substring(1);
            }

            String operationName = bindingContext.getHttpRequest().getParameter("method");
            Operation operation = findOperation( operationName );
            
            if (operation == null) {
                throw new RuntimeException("Invalid Operation '" + operationName + "'" );
            }

            final JavaOperation javaOperation = (JavaOperation)operation;
            final Method method = javaOperation.getJavaMethod();

            List<DataType> operationInputType = operation.getInputType().getLogical();
            int size = operationInputType.size();
            for (int i = 0; i < size; i++) {
                System.out.println(operationInputType.get(i));
            }
            
            List<Object> messageParameters = new ArrayList<Object>();
            for(int i=0; i<method.getParameterTypes().length; i++) {
                for(Annotation annotation : method.getParameterAnnotations()[i]) {
                    if (annotation instanceof QueryParam) {
                        QueryParam queryParam = (QueryParam) annotation;
                        String name = queryParam.value();
                        String[] values = bindingContext.getHttpRequest().getParameterValues(name);
                        if(values.length == 1) {
                            messageParameters.add(values[0]);
                        } else {
                            messageParameters.add(values);
                        }
                        
                    }
                }
            }
            
            Object[] body = new Object[messageParameters.size()];
            messageParameters.toArray(body);
            
            msg.setBody(body);
            msg.setOperation(operation);

            return getNext().invoke(msg);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
    
        List<Operation> operations = endpoint.getComponentServiceInterfaceContract().getInterface().getOperations(); 

        Operation result = null;
        for (Operation o : operations) {
            if (o.getName().equalsIgnoreCase(method)) {
                result = o;
                break;
            }
        }

        return result;
    } 
}
