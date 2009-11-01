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

package org.apache.tuscany.sca.binding.sca.rmi;

import java.util.logging.Logger;

import org.apache.tuscany.sca.assembly.DistributedSCABinding;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.binding.rmi.RMIBinding;
import org.apache.tuscany.sca.binding.rmi.RMIBindingFactory;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.BindingProviderFactory;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;

/**
 * The reference binding provider for the remote sca binding implementation. Relies on the 
 * binding-ws-axis implementation for sending messages to remote services so this provider
 * uses the ws-axis provider under the covers. 
 */
public class RMISCAReferenceBindingProvider implements ReferenceBindingProvider {

    private static final Logger logger = Logger.getLogger(RMISCAReferenceBindingProvider.class.getName());
    
    private SCABinding binding;
    private RMIBinding rmiBinding;
    private ReferenceBindingProvider referenceBindingProvider;

    public RMISCAReferenceBindingProvider(EndpointReference endpointReference,
                                            ExtensionPointRegistry extensionPoints) {

        FactoryExtensionPoint modelFactories = extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);

        this.binding = ((DistributedSCABinding)endpointReference.getBinding()).getSCABinding();
        
        rmiBinding = modelFactories.getFactory(RMIBindingFactory.class).createRMIBinding();
        rmiBinding.setName(this.binding.getName());         
       
        // create a copy of the endpoint reference but with the RMI binding in
        EndpointReference epr = null;
        try {
            epr = (EndpointReference)endpointReference.clone();
        } catch (Exception ex){
            // we know we can clone endpoint references
        }
        epr.setBinding(rmiBinding);
        
        // create the real RMI reference binding provider
        ProviderFactoryExtensionPoint providerFactories = extensionPoints.getExtensionPoint(ProviderFactoryExtensionPoint.class);
        BindingProviderFactory<?> providerFactory = (BindingProviderFactory<?>)providerFactories.getProviderFactory(RMIBinding.class);
        referenceBindingProvider = providerFactory.createReferenceBindingProvider(epr);
        logger.info("Reference using RMI SCA Binding: " + rmiBinding.getURI());
    }

    public InterfaceContract getBindingInterfaceContract() {
        return null;
    }

    public boolean supportsOneWayInvocation() {
        return false;
    }

    public Invoker createInvoker(Operation operation) {
        return referenceBindingProvider.createInvoker(operation);
    }   
    
    public SCABinding getSCABinding () {
        return binding;
    }  

    public void start() {
        referenceBindingProvider.start();
    }

    public void stop() {
        referenceBindingProvider.stop();
    }
}
