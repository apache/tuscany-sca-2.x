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

package echo.provider;

import org.apache.tuscany.sca.core.RuntimeComponent;
import org.apache.tuscany.sca.core.RuntimeComponentService;
import org.apache.tuscany.sca.core.RuntimeWire;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;

import echo.EchoBinding;
import echo.server.EchoServer;
import echo.server.EchoService;

/**
 * Implementation of the Echo binding provider.
 * 
 * @version $Rev$ $Date$
 */
public class EchoServiceBindingProvider implements ServiceBindingProvider {
    
    private RuntimeComponent component;
    private RuntimeComponentService service;  
    private EchoBinding binding;
    private MessageFactory messageFactory;
    
    public EchoServiceBindingProvider(RuntimeComponent component,
                                      RuntimeComponentService service, EchoBinding binding, MessageFactory messageFactory) {
        this.component = component;
        this.service = service;
        this.binding = binding;
        this.messageFactory = messageFactory;
    }

    public InterfaceContract getBindingInterfaceContract() {
        return service.getInterfaceContract();
    }

    public void start() {

        RuntimeComponentService componentService = (RuntimeComponentService) service;
        RuntimeWire wire = componentService.getRuntimeWire(binding);
        InvocationChain chain = wire.getInvocationChains().get(0);
        
        // Register with the hosting server
        String uri = component.getURI() + "/" + binding.getName();
        EchoServer.getServer().register(uri, new EchoService(chain.getHeadInvoker(), messageFactory));
    }

    public void stop() {
        
        // Unregister from the hosting server
        String uri = component.getURI() + "/" + binding.getName();
        EchoServer.getServer().unregister(uri);
    }

}
