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

import java.net.URL;

import org.apache.tuscany.sca.binding.http.HTTPResourceBinding;
import org.apache.tuscany.sca.host.http.DefaultResourceServlet;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeWire;

/**
 * Implementation of the Echo binding provider.
 */
public class HTTPResourceServiceBindingProvider implements ServiceBindingProvider {
    
    private RuntimeComponentService service;  
    private HTTPResourceBinding binding;
    private MessageFactory messageFactory;
    private ServletHost servletHost;
    private String uri; 
    
    public HTTPResourceServiceBindingProvider(RuntimeComponent component,
                                              RuntimeComponentService service,
                                              HTTPResourceBinding binding,
                                              MessageFactory messageFactory,
                                              ServletHost servletHost) {
        this.service = service;
        this.binding = binding;
        this.messageFactory = messageFactory;
        this.servletHost = servletHost;

        uri = binding.getURI();
        if (!uri.endsWith("/")) {
            uri += "/";
        }
        if (!uri.endsWith("*")) {
            uri += "*";
        }
    }

    public InterfaceContract getBindingInterfaceContract() {
        return service.getInterfaceContract();
    }

    public void start() {

        // Get the target component implementation, for now we are assuming
        // that it's an implementation.resource
        RuntimeComponentService componentService = (RuntimeComponentService) service;
        RuntimeWire wire = componentService.getRuntimeWire(binding);
        
        // Get the getLocationURL invoker
        Invoker getLocationInvoker = null;
        for (InvocationChain invocationChain : wire.getInvocationChains()) {
            String operationName = invocationChain.getSourceOperation().getName();
            if (operationName.equals("getLocationURL")) {
                getLocationInvoker = invocationChain.getHeadInvoker();
            }
        }
        if (getLocationInvoker == null) {
            throw new IllegalStateException("No getLocationURL operation found on target component");
        }

        // Get the location URL
        Message message = messageFactory.createMessage();
        message = getLocationInvoker.invoke(message);
        URL locationURL = message.getBody();
        
        // Register the default resource servlet with the servlet host
        DefaultResourceServlet resourceServlet = new DefaultResourceServlet(locationURL.toString());
        servletHost.addServletMapping(uri, resourceServlet);
        
    }

    public void stop() {
        
        // Unregister from the hosting server
        servletHost.removeServletMapping(uri);
    }

}
