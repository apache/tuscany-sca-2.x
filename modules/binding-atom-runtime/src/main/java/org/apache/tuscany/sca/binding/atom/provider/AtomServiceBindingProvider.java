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

package org.apache.tuscany.sca.binding.atom.provider;

import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.binding.atom.AtomBinding;
import org.apache.tuscany.sca.databinding.Mediator;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterfaceContract;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeWire;

/**
 * Implementation of the Atom binding provider.
 *
 * @version $Rev$ $Date$
 */
class AtomServiceBindingProvider implements ServiceBindingProvider {

    private RuntimeComponentService service;
    private InterfaceContract serviceContract;
    private AtomBinding binding;
    private ServletHost servletHost;
    private MessageFactory messageFactory;
    private Mediator mediator;
    
    private String servletMapping;
    private String bindingURI;

    AtomServiceBindingProvider(RuntimeComponent component,
                                      RuntimeComponentService service,
                                      AtomBinding binding,
                                      ServletHost servletHost,
                                      MessageFactory messageFactory,
                                      Mediator mediator) {
        this.service = service;
        this.binding = binding;
        this.servletHost = servletHost;
        this.messageFactory = messageFactory;
        this.mediator = mediator;
        
        // TUSCANY-3166
        this.serviceContract = service.getInterfaceContract();
        if (this.serviceContract instanceof WSDLInterfaceContract) {
        	for (Service componentService : component.getImplementation().getServices()) {
        		if (componentService.getName().equals(service.getName())) {
        			this.serviceContract = (InterfaceContract) componentService.getInterfaceContract();
        			break;
        		}
        	}

        }      
    }

    public InterfaceContract getBindingInterfaceContract() {
        return serviceContract;
    }
    
    public boolean supportsOneWayInvocation() {
        return false;
    }

    public void start() {
        RuntimeComponentService componentService = (RuntimeComponentService)service;
        RuntimeWire wire = componentService.getRuntimeWire(binding);

        AtomBindingListenerServlet servlet =
            new AtomBindingListenerServlet(wire, messageFactory, mediator, binding.getTitle(), binding.getDescription());

        servletMapping = binding.getURI();
        if (!servletMapping.endsWith("/")) {
            servletMapping += "/";
        }
        if (!servletMapping.endsWith("*")) {
            servletMapping += "*";
        }
        servletHost.addServletMapping(servletMapping, servlet);
        
        bindingURI = binding.getURI();
        if (!bindingURI.endsWith("/")) {
            bindingURI += "/";
        }

        servletHost.addServletMapping(bindingURI, servlet);
    }

    public void stop() {
        servletHost.removeServletMapping(servletMapping);
        servletHost.removeServletMapping(bindingURI);
    }
}
