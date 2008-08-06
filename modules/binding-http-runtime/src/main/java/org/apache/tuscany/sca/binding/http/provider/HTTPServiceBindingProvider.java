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

package org.apache.tuscany.sca.binding.http.provider;

import javax.servlet.Servlet;

import org.apache.tuscany.sca.binding.http.HTTPBinding;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeWire;

/**
 * Implementation of an HTTP binding provider.
 *
 * @version $Rev$ $Date$
 */
public class HTTPServiceBindingProvider implements ServiceBindingProvider {
    
    private RuntimeComponentService service;  
    private HTTPBinding binding;
    private MessageFactory messageFactory;
    private ServletHost servletHost;
    private String servletMapping;
    
    public HTTPServiceBindingProvider(RuntimeComponent component,
                                              RuntimeComponentService service,
                                              HTTPBinding binding,
                                              MessageFactory messageFactory,
                                              ServletHost servletHost) {
        this.service = service;
        this.binding = binding;
        this.messageFactory = messageFactory;
        this.servletHost = servletHost;
    }

    public void start() {
        
        // Get the invokers for the supported operations
        RuntimeComponentService componentService = (RuntimeComponentService) service;
        RuntimeWire wire = componentService.getRuntimeWire(binding);
        Servlet servlet = null;
        for (InvocationChain invocationChain : wire.getInvocationChains()) {
            Operation operation = invocationChain.getTargetOperation();
            String operationName = operation.getName();
            if (operationName.equals("get")) {
                Invoker getInvoker = invocationChain.getHeadInvoker();
                servlet = new HTTPGetListenerServlet(getInvoker, messageFactory);
                break;
            } else if (operationName.equals("service")) {
                Invoker serviceInvoker = invocationChain.getHeadInvoker();
                servlet = new HTTPServiceListenerServlet(serviceInvoker, messageFactory);
                break;
            }
        }
        if (servlet == null) {
            throw new IllegalStateException("No get or service method found on the service");
        }
                
        // Create our HTTP service listener Servlet and register it with the
        // Servlet host
        servletMapping = binding.getURI();
        if (!servletMapping.endsWith("/")) {
            servletMapping += "/";
        }
        if (!servletMapping.endsWith("*")) {
            servletMapping += "*";
        }
        servletHost.addServletMapping(servletMapping, servlet);
    }

    public void stop() {
        
        // Unregister the Servlet from the Servlet host
        servletHost.removeServletMapping(servletMapping);
    }

    public InterfaceContract getBindingInterfaceContract() {
        return null;
    }
    
    public boolean supportsOneWayInvocation() {
        return false;
    }

}
