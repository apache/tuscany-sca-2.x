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

import java.util.logging.Logger;

import org.apache.tuscany.sca.assembly.DistributedSCABinding;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.BindingProviderFactory;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;
import org.oasisopen.sca.ServiceUnavailableException;

/**
 * The sca reference binding provider mediates between the twin requirements of
 * local sca bindings and remote sca bindings. In the local case is does
 * very little. When the sca binding model is set as being remote (because a
 * reference target can't be resolved in the current model) this binding will
 * try and create a remote connection to it
 *
 * @version $Rev$ $Date$
 */
public class RuntimeSCAReferenceBindingProvider implements ReferenceBindingProvider {

    private static final Logger logger = Logger.getLogger(RuntimeSCAReferenceBindingProvider.class.getName());

    private RuntimeEndpointReference endpointReference;
    private RuntimeComponent component;
    private RuntimeComponentReference reference;
    private SCABinding binding;
    private boolean started = false;

    private BindingProviderFactory<DistributedSCABinding> distributedProviderFactory = null;
    private ReferenceBindingProvider distributedProvider = null;
    private SCABindingFactory scaBindingFactory;

    public RuntimeSCAReferenceBindingProvider(ExtensionPointRegistry extensionPoints,
                                              RuntimeEndpointReference endpointReference) {
        this.endpointReference = endpointReference;
        this.component = (RuntimeComponent)endpointReference.getComponent();
        this.reference = (RuntimeComponentReference)endpointReference.getReference();
        this.binding = (SCABinding)endpointReference.getBinding();
        this.scaBindingFactory =
            extensionPoints.getExtensionPoint(FactoryExtensionPoint.class).getFactory(SCABindingFactory.class);

        // look to see if a distributed SCA binding implementation has
        // been included on the classpath. This will be needed by the
        // provider itself to do it's thing
        ProviderFactoryExtensionPoint factoryExtensionPoint =
            extensionPoints.getExtensionPoint(ProviderFactoryExtensionPoint.class);
        distributedProviderFactory =
            (BindingProviderFactory<DistributedSCABinding>)factoryExtensionPoint
                .getProviderFactory(DistributedSCABinding.class);

    }

    public boolean isTargetRemote() {
        boolean targetIsRemote = false;

        // The decision is based on the results of the wiring process in the assembly model
        // and there are three possibilities
        // 1 - target service is running in a separate node in a separate JVM
        // 2 - target service is running in a separate node in the same JVM
        // 3 - target service is running in the same node
        
        // TODO - EPR - the method needs to be able to indicate the three cases
        

        if (RemoteBindingHelper.isTargetRemote()) {
        	// TODO - EPR - what is this RemoteBindingHelper for?
            if (reference.getInterfaceContract() != null) {
                targetIsRemote = reference.getInterfaceContract().getInterface().isRemotable();
            } else {
                targetIsRemote = true;
            }
        } if ( (endpointReference.isRemote()) &&
               (endpointReference.getTargetEndpoint().isRemote())){
        	// case 1
            targetIsRemote = true;
        } else if (endpointReference.isRemote()) {
        	// case 2
        	targetIsRemote = false;
        } else {
        	// case 3
        	targetIsRemote = false;
        }
        return targetIsRemote;
    }

    private ReferenceBindingProvider getDistributedProvider() {

        if (isTargetRemote()) {
            // initialize the remote provider if it hasn't been done already
            if (distributedProvider == null) {
                if (reference.getInterfaceContract() != null && !reference.getInterfaceContract().getInterface().isRemotable()) {
                    throw new IllegalStateException("Reference interface not remotable for component: " + component
                        .getName()
                        + " and reference: "
                        + reference.getName());
                }

                if (distributedProviderFactory == null) {
                    throw new IllegalStateException("No distributed SCA binding available for component: " + component
                        .getName()
                        + " and reference: "
                        + reference.getName());
                }

                // create the remote provider
                DistributedSCABinding distributedBinding = scaBindingFactory.createDistributedSCABinding();
                distributedBinding.setSCABinding(binding);
                
                // create a copy of the endpoint reference and change the binding
                RuntimeEndpointReference epr = null;
                try {
                    epr = (RuntimeEndpointReference)endpointReference.clone();
                } catch (Exception ex) {
                    // we know we can clone endpoint references
                }
                epr.setBinding(distributedBinding);

                distributedProvider =
                    distributedProviderFactory.createReferenceBindingProvider(epr);
            }
        }

        return distributedProvider;
    }

    public InterfaceContract getBindingInterfaceContract() {
        if (isTargetRemote()) {
            return getDistributedProvider().getBindingInterfaceContract();
        } else {
            // Check if there is a target
            RuntimeEndpoint endpoint = (RuntimeEndpoint)endpointReference.getTargetEndpoint();
            if (endpoint != null) {
                // Use the target binding interface contract
                return endpoint.getBindingInterfaceContract();
            } else {
                return endpointReference.getReferenceInterfaceContract();
            }
        }
    }

    public boolean supportsOneWayInvocation() {
        if (isTargetRemote()) {
            return getDistributedProvider().supportsOneWayInvocation();
        } else {
            return false;
        }
    }

    private Invoker getInvoker(RuntimeEndpointReference epr, Operation operation) {
        Endpoint target = epr.getTargetEndpoint();
        if (target != null) {
            RuntimeComponentService service = (RuntimeComponentService)target.getService();
            if (service != null) { // not a callback wire
                InvocationChain chain = ((RuntimeEndpoint) target).getInvocationChain(operation);
                return chain == null ? null : new SCABindingInvoker(chain);
            }
        }
        return null;
    }

    public Invoker createInvoker(Operation operation) {
        if (isTargetRemote()) {
            return getDistributedProvider().createInvoker(operation);
        } else {
            Invoker invoker = getInvoker(endpointReference, operation);
            if (invoker == null) {
                throw new ServiceUnavailableException("Unable to create SCA binding invoker for local target " + component.getName()
                    + " reference "
                    + reference.getName()
                    + " (bindingURI="
                    + binding.getURI()
                    + " operation="
                    + operation.getName()
                    + ")" );
            }
            return invoker;
        }
    }

    public void start() {
        if (started) {
            return;
        } else {
            started = true;
        }

        if (getDistributedProvider() != null) {
            distributedProvider.start();
        }
    }

    public void stop() {
        if (!started) {
            return;
        } else {
            started = false;
        }

        if (getDistributedProvider() != null) {
            distributedProvider.stop();
        }
    }

}
