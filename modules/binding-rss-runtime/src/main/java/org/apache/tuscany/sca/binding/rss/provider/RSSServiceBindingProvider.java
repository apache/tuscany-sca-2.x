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

package org.apache.tuscany.sca.binding.rss.provider;

import org.apache.tuscany.sca.binding.rss.RSSBinding;
import org.apache.tuscany.sca.databinding.Mediator;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.Invocable;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;

/**
 * Implementation of the RSS binding provider.
 *
 * @version $Rev$ $Date$
 */
class RSSServiceBindingProvider implements ServiceBindingProvider {

    private RuntimeEndpoint endpoint;
    
    //private RuntimeComponentService service;
    private RSSBinding binding;
    
    private MessageFactory messageFactory;
    private Mediator mediator;
    private ServletHost servletHost;
    
    private String servletMapping;
    
    RSSServiceBindingProvider(RuntimeEndpoint endpoint,
                                     MessageFactory messageFactory,
                                     Mediator mediator,
                                     ServletHost servletHost) {
        this.endpoint = endpoint;
        
        //this.service = (RuntimeComponentService)endpoint.getService();
        this.binding = (RSSBinding) endpoint.getBinding();
        this.servletHost = servletHost;
        this.messageFactory = messageFactory;
        this.mediator = mediator;
    }

    public InterfaceContract getBindingInterfaceContract() {
        return endpoint.getComponentTypeServiceInterfaceContract();
    }
    
    public boolean supportsOneWayInvocation() {
        return false;
    }

    public void start() {
        Invocable wire = (RuntimeEndpoint) endpoint;

        RSSBindingListenerServlet servlet =
            new RSSBindingListenerServlet(wire, messageFactory, mediator);

        servletMapping = binding.getURI();
        if (!servletMapping.endsWith("/")) {
            servletMapping += "/";
        }
        if (!servletMapping.endsWith("*")) {
            servletMapping += "*";
        }
        servletHost.addServletMapping(servletMapping, servlet);

        // Save the actual binding URI in the binding
        //binding.setURI(servletHost.getURLMapping(binding.getURI()).toString());
    }

    public void stop() {
        servletHost.removeServletMapping(servletMapping);
    }

}
