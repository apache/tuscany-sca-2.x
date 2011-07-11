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

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.EndpointReferenceAsyncProvider;
import org.apache.tuscany.sca.provider.EndpointReferenceProvider;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.provider.SCABindingMapper;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;

/**
 * The sca reference binding provider mediates between the twin requirements of
 * local sca bindings and remote sca bindings. In the local case is does
 * very little. When the sca binding model is set as being remote (because a
 * reference target can't be resolved in the current model) this binding will
 * try and create a remote connection to it
 *
 * @version $Rev$ $Date$
 */
public class RuntimeSCAReferenceBindingProvider implements EndpointReferenceAsyncProvider {

    private RuntimeEndpointReference endpointReference;
    private boolean started = false;

    private ReferenceBindingProvider delegatingBindingProvider;
    private SCABindingMapper scaBindingMapper;

    public RuntimeSCAReferenceBindingProvider(ExtensionPointRegistry extensionPoints,
                                              RuntimeEndpointReference endpointReference) {
    	this.endpointReference = endpointReference;

        UtilityExtensionPoint utilities = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);
        this.scaBindingMapper = utilities.getUtility(SCABindingMapper.class);
        getDelegatingProvider();
    }

    private ReferenceBindingProvider getDelegatingProvider() {
        if (delegatingBindingProvider == null) {
            delegatingBindingProvider = new DelegatingSCAReferenceBindingProvider(endpointReference, scaBindingMapper);
        }

        return delegatingBindingProvider;
    }

    public InterfaceContract getBindingInterfaceContract() {
        return delegatingBindingProvider.getBindingInterfaceContract();
    }

    public boolean supportsOneWayInvocation() {
        return delegatingBindingProvider.supportsOneWayInvocation();
    }

    public Invoker createInvoker(Operation operation) {
        return delegatingBindingProvider.createInvoker(operation);
    }

    public void start() {
        if (started) {
            return;
        }
        getDelegatingProvider().start();
        started = true;
    }

    public void stop() {
        if (!started) {
            return;
        }

        try {
            getDelegatingProvider().stop();
        } finally {
            started = false;
        }
    }

    public void configure() {
        if (getDelegatingProvider() instanceof EndpointReferenceProvider) {
            ((EndpointReferenceProvider)getDelegatingProvider()).configure();
        }
    }
    
    public boolean supportsNativeAsync() {
        return ((EndpointReferenceAsyncProvider)delegatingBindingProvider).supportsNativeAsync();
    }
    
    public RuntimeEndpointReference getDelegateEndpointReference(){
        return ((DelegatingSCAReferenceBindingProvider)delegatingBindingProvider).getDelegateEndpointReference();
    }

}
