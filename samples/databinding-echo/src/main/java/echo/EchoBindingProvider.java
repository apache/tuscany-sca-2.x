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

package echo;

import java.net.URI;

import org.apache.tuscany.assembly.Component;
import org.apache.tuscany.assembly.ComponentReference;
import org.apache.tuscany.assembly.ComponentService;
import org.apache.tuscany.core.ReferenceBindingActivator;
import org.apache.tuscany.core.ReferenceBindingProvider;
import org.apache.tuscany.core.RuntimeComponentService;
import org.apache.tuscany.core.RuntimeWire;
import org.apache.tuscany.core.ServiceBindingActivator;
import org.apache.tuscany.core.ServiceBindingProvider;
import org.apache.tuscany.interfacedef.InterfaceContract;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.InvocationChain;

/**
 * Implementation of the Echo binding provider.
 * 
 * @version $Rev$ $Date$
 */
public class EchoBindingProvider extends EchoBindingImpl implements ReferenceBindingActivator,
    ReferenceBindingProvider, ServiceBindingActivator, ServiceBindingProvider {

    public Interceptor createInterceptor(Component component,
                                         ComponentReference reference,
                                         Operation operation,
                                         boolean isCallback) {
        if (isCallback) {
            throw new UnsupportedOperationException();
        } else {
            return new EchoBindingInterceptor();
        }
    }

    public InterfaceContract getBindingInterfaceContract(ComponentReference reference) {
        return reference.getInterfaceContract();
    }

    public void start(Component component, ComponentReference reference) {
    }

    public void stop(Component component, ComponentReference reference) {
    }

    public InterfaceContract getBindingInterfaceContract(ComponentService service) {
        return service.getInterfaceContract();
    }

    public void start(Component component, ComponentService service) {
        URI uri = URI.create(component.getURI() + "/" + getName());
        setURI(uri.toString());
        RuntimeComponentService componentService = (RuntimeComponentService) service;
        RuntimeWire wire = componentService.getRuntimeWires().get(0);
        InvocationChain chain = wire.getInvocationChains().get(0);
        // Register with the hosting server
        EchoServer.getServer().register(new EchoService(chain.getHeadInterceptor()), uri);
    }

    public void stop(Component component, ComponentService service) {
        // Register with the hosting server
        EchoServer.getServer().unregister(URI.create(getURI()));
    }

    public Object clone() {
        return this;
    }
}
