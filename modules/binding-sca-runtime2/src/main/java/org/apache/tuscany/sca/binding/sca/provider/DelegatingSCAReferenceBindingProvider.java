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

package org.apache.tuscany.sca.binding.sca.provider;

import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.EndpointReferenceProvider;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;

/**
 * The reference binding provider for the remote sca binding implementation. 
 */
public class DelegatingSCAReferenceBindingProvider implements EndpointReferenceProvider {

    private ReferenceBindingProvider provider;

    public DelegatingSCAReferenceBindingProvider(RuntimeEndpointReference endpointReference,
                                                 SCABindingMapper mapper) {
        RuntimeEndpointReference epr = mapper.map(endpointReference);
        if (epr != null) {
            provider = epr.getBindingProvider();
        }
    }

    public InterfaceContract getBindingInterfaceContract() {
        return provider.getBindingInterfaceContract();
    }

    public boolean supportsOneWayInvocation() {
        return provider.supportsOneWayInvocation();
    }

    public Invoker createInvoker(Operation operation) {
        return provider.createInvoker(operation);
    }

    public void start() {
        provider.start();
    }

    public void stop() {
        provider.stop();
    }

    public void configure() {
        if (provider instanceof EndpointReferenceProvider) {
            ((EndpointReferenceProvider)provider).configure();
        }
    }
}
