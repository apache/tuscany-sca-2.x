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

import org.apache.tuscany.sca.binding.feed.RSSBinding;
import org.apache.tuscany.sca.http.ServletHost;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeWire;

/**
 * Implementation of the RSS binding provider.
 */
public class RSSServiceBindingProvider implements ServiceBindingProvider {

    private RuntimeComponent component;
    private RuntimeComponentService service;
    private RSSBinding binding;
    private ServletHost servletHost;
    private MessageFactory messageFactory;
    private String uri;

    public RSSServiceBindingProvider(RuntimeComponent component,
                                     RuntimeComponentService service,
                                     RSSBinding binding,
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
        RuntimeComponentService componentService = (RuntimeComponentService)service;
        RuntimeWire wire = componentService.getRuntimeWire(binding);

        ResourceCollectionBindingListener servlet =
            new ResourceCollectionBindingListener(wire, messageFactory, "rss_2.0");

        servletHost.addServletMapping(uri, servlet);
    }

    public void stop() {
        servletHost.removeServletMapping(uri);
    }

}
