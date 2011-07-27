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

import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.binding.local.DefaultLocalSCAReferenceBindingProvider;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.EndpointReferenceAsyncProvider;
import org.apache.tuscany.sca.provider.EndpointReferenceProvider;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.provider.SCABindingMapper;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;

/**
 * The reference binding provider for the remote sca binding implementation. 
 */
public class DelegatingSCAReferenceBindingProvider implements EndpointReferenceAsyncProvider {

    private ReferenceBindingProvider provider;
    private RuntimeEndpointReference delegateEndpointReference;

    public DelegatingSCAReferenceBindingProvider(RuntimeEndpointReference endpointReference,
                                                 SCABindingMapper mapper) {
        delegateEndpointReference = mapper.map(endpointReference);
        if (delegateEndpointReference != null) {
            endpointReference.setDelegateEndpointReference(delegateEndpointReference);
            provider = delegateEndpointReference.getBindingProvider();
               
            // reset the EPR to binding.sca EPR because the local optimization assumes
            // this to be the case. 
            if (provider instanceof DefaultLocalSCAReferenceBindingProvider){
                ((DefaultLocalSCAReferenceBindingProvider)provider).setEndpointReference(endpointReference);
            }         
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
    
    @Override
    public boolean supportsNativeAsync() {
        if (provider instanceof EndpointReferenceAsyncProvider) {
            return ((EndpointReferenceAsyncProvider)provider).supportsNativeAsync();
        }
        
        return false;
    }
    
    public RuntimeEndpointReference getDelegateEndpointReference(){
        return delegateEndpointReference;
    }
}
