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

package org.apache.tuscany.sca.binding.feed.provider;

import org.apache.tuscany.sca.binding.feed.FeedBinding;
import org.apache.tuscany.sca.http.ServletHost;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeWire;

/**
 * Implementation of the Feed binding provider.
 */
public class FeedServiceBindingProvider implements ServiceBindingProvider {

    private RuntimeComponent component;
    private RuntimeComponentService service;
    private FeedBinding binding;
    private ServletHost servletHost;
    private MessageFactory messageFactory;
    private String uri;

    public FeedServiceBindingProvider(RuntimeComponent component,
                                      RuntimeComponentService service,
                                      FeedBinding binding,
                                      ServletHost servletHost,
                                      MessageFactory messageFactory) {
        this.component = component;
        this.service = service;
        this.binding = binding;
        this.servletHost = servletHost;
        this.messageFactory = messageFactory;
        uri = binding.getURI();
        if (uri == null) {
            uri = "/" + this.component.getName();
        }
    }

    public InterfaceContract getBindingInterfaceContract() {
        return service.getInterfaceContract();
    }

    public void start() {
        RuntimeComponentService componentService = (RuntimeComponentService) service;
        RuntimeWire wire = componentService.getRuntimeWire(binding);
        InvocationChain chain = wire.getInvocationChains().get(0);
        Invoker invoker = chain.getHeadInvoker(); 

        FeedBindingListener servlet =
            new FeedBindingListener(invoker, messageFactory, binding.getFeedType());

        servletHost.addServletMapping(uri, servlet);
    }

    public void stop() {
        servletHost.removeServletMapping(uri);
    }

}
