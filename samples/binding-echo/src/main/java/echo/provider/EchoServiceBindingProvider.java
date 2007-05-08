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

import org.apache.tuscany.core.RuntimeComponent;
import org.apache.tuscany.core.RuntimeComponentService;
import org.apache.tuscany.core.RuntimeWire;
import org.apache.tuscany.interfacedef.InterfaceContract;
import org.apache.tuscany.invocation.InvocationChain;
import org.apache.tuscany.provider.ServiceBindingProvider;

import echo.EchoBinding;
import echo.server.EchoServer;
import echo.server.EchoService;

/**
 * Implementation of the Echo binding provider.
 * 
 * @version $Rev$ $Date$
 */
public class EchoServiceBindingProvider implements ServiceBindingProvider {
    
    private EchoBinding binding;
    
    public EchoServiceBindingProvider(EchoBinding binding) {
        this.binding = binding;
    }

    public InterfaceContract getBindingInterfaceContract(RuntimeComponentService service) {
        return service.getInterfaceContract();
    }

    public void start(RuntimeComponent component, RuntimeComponentService service) {

        RuntimeComponentService componentService = (RuntimeComponentService) service;
        RuntimeWire wire = componentService.getRuntimeWire(binding);
        InvocationChain chain = wire.getInvocationChains().get(0);
        
        // Register with the hosting server
        String uri = component.getURI() + "/" + binding.getName();
        EchoServer.getServer().register(uri, new EchoService(chain.getHeadInvoker()));
    }

    public void stop(RuntimeComponent component, RuntimeComponentService service) {
        
        // Unregister from the hosting server
        String uri = component.getURI() + "/" + binding.getName();
        EchoServer.getServer().unregister(uri);
    }

}
