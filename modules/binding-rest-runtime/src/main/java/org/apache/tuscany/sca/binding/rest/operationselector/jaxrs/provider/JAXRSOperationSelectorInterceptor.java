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

package org.apache.tuscany.sca.binding.rest.operationselector.jaxrs.provider;

import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import org.apache.tuscany.sca.common.http.HTTPContext;
import org.apache.tuscany.sca.common.http.HTTPUtil;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaOperation;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;

/**
 * JAXRS operation selector Interceptor.
 * 
 * @version $Rev$ $Date$
*/
public class JAXRSOperationSelectorInterceptor implements Interceptor {
    private ExtensionPointRegistry extensionPoints;
    private RuntimeEndpoint endpoint;
    
    private RuntimeComponentService service;
    private InterfaceContract interfaceContract;
    private List<Operation> serviceOperations;
    
    private Invoker next;

    public JAXRSOperationSelectorInterceptor(ExtensionPointRegistry extensionPoints, RuntimeEndpoint endpoint) {
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
            HTTPContext bindingContext = (HTTPContext) msg.getBindingContext();

            String path = URLDecoder.decode(HTTPUtil.getRequestPath(bindingContext.getHttpRequest()), "UTF-8");
            
            if(path.startsWith("/")) {
                path = path.substring(1);
            }

            List<Operation> operations = filterOperationsByHttpMethod(interfaceContract, bindingContext.getHttpRequest().getMethod());

            Operation operation = findOperation(path, operations);

            final JavaOperation javaOperation = (JavaOperation) operation;
            final Method method = javaOperation.getJavaMethod();
            
            if(path != null && path.length() > 0) {
                if(method.getAnnotation(Path.class) != null) {
                    msg.setBody(new Object[]{path});
                }
            }
            
            msg.setOperation(operation);

            return getNext().invoke(msg);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Find the operation from the component service contract
     * @param componentService
     * @param http_method
     * @return
     */
    private static List<Operation> filterOperationsByHttpMethod(InterfaceContract interfaceContract, String http_method) {
        List<Operation> operations = null;
        
        if(http_method.equalsIgnoreCase("get")) {
            operations = (List<Operation>) interfaceContract.getInterface().getAttributes().get(GET.class);
        }else if(http_method.equalsIgnoreCase("put")) {
            operations = (List<Operation>) interfaceContract.getInterface().getAttributes().get(PUT.class);
        }else if(http_method.equalsIgnoreCase("post")) {
            operations = (List<Operation>) interfaceContract.getInterface().getAttributes().get(POST.class);
        }else if(http_method.equalsIgnoreCase("delete")) {
            operations = (List<Operation>) interfaceContract.getInterface().getAttributes().get(DELETE.class);
        }
        
        return operations;
    }
    
    /**
     * Find the operation from the component service contract
     * @param componentService
     * @param http_method
     * @return
     */
    private Operation findOperation(String path, List<Operation> operations) {
        Operation operation = null;
        
        for(Operation op : operations) {
            final JavaOperation javaOperation = (JavaOperation) op;
            final Method method = javaOperation.getJavaMethod();
            
            if(path != null && path.length() > 0) {
                if(method.getAnnotation(Path.class) != null) {
                    operation = op;
                    break;
                }
            } else {
                if(method.getAnnotation(Path.class) == null) {
                    operation = op;
                    break;
                }
            }
        }
        
        return operation;
    }
}
