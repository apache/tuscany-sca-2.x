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

import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.databinding.Mediator;
import org.apache.tuscany.sca.interfacedef.Compatibility;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.EndpointReferenceAsyncProvider;
import org.apache.tuscany.sca.provider.EndpointReferenceProvider;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;
import org.oasisopen.sca.ServiceRuntimeException;
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
public class RuntimeSCAReferenceBindingProvider implements EndpointReferenceAsyncProvider {

    private RuntimeEndpointReference endpointReference;
    private RuntimeComponent component;
    private RuntimeComponentReference reference;
    private SCABinding binding;
    private boolean remotable;
    private boolean started = false;

    private ReferenceBindingProvider distributedProvider;
    private Mediator mediator;
    private InterfaceContractMapper interfaceContractMapper;
    private SCABindingMapper scaBindingMapper;

    public RuntimeSCAReferenceBindingProvider(ExtensionPointRegistry extensionPoints,
                                              RuntimeEndpointReference endpointReference) {
        this.endpointReference = endpointReference;
        this.component = (RuntimeComponent)endpointReference.getComponent();
        this.reference = (RuntimeComponentReference)endpointReference.getReference();
        this.binding = (SCABinding)endpointReference.getBinding();

        UtilityExtensionPoint utilities = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);
        this.mediator = utilities.getUtility(Mediator.class);
        this.interfaceContractMapper = utilities.getUtility(InterfaceContractMapper.class);
        this.scaBindingMapper = utilities.getUtility(SCABindingMapper.class);
        remotable = isTargetRemote();
        getDistributedProvider();
    }

    private boolean isTargetRemote() {
        return endpointReference.getTargetEndpoint().isRemote();
    }

    private ReferenceBindingProvider getDistributedProvider() {

        if (remotable) {
            // initialize the remote provider if it hasn't been done already
            if (distributedProvider == null) {
                if (reference.getInterfaceContract() != null && !reference.getInterfaceContract().getInterface()
                    .isRemotable()) {
                    throw new ServiceRuntimeException("Reference interface not remotable for component: " + component
                        .getName()
                        + " and reference: "
                        + reference.getName());
                }

                if (scaBindingMapper.isRemotable()) {
                    distributedProvider =
                        new DelegatingSCAReferenceBindingProvider(endpointReference, scaBindingMapper);
                }
            }
        }

        return distributedProvider;
    }

    public InterfaceContract getBindingInterfaceContract() {
        if (remotable && distributedProvider != null) {
            return distributedProvider.getBindingInterfaceContract();
        } else {
            // Check if there is a target
            RuntimeEndpoint endpoint = (RuntimeEndpoint)endpointReference.getTargetEndpoint();
            if (endpoint != null) {
                return endpoint.getComponentTypeServiceInterfaceContract();
            } else {
                return endpointReference.getComponentTypeReferenceInterfaceContract();
            }
        }
    }

    public boolean supportsOneWayInvocation() {
        if (remotable && distributedProvider != null) {
            return distributedProvider.supportsOneWayInvocation();
        } else {
            return false;
        }
    }

    private Invoker getInvoker(RuntimeEndpointReference epr, Operation operation) {
        Endpoint target = epr.getTargetEndpoint();
        if (target != null) {
            RuntimeComponentService service = (RuntimeComponentService)target.getService();
            if (service != null) { // not a callback wire
                InvocationChain chain = ((RuntimeEndpoint)target).getInvocationChain(operation);

                boolean passByValue = false;
                Operation targetOp = chain.getTargetOperation();
                if (!operation.getInterface().isRemotable()) {
                    if (interfaceContractMapper.isCompatibleByReference(operation, targetOp, Compatibility.SUBSET)) {
                        passByValue = false;
                    }
                } else {
//                    boolean allowsPBR = chain.allowsPassByReference(); TODO: TUSCANY-3479 this breaks the conformance tests as it needs to consider _both_ ends
                    boolean allowsPBR = false;
                    if (allowsPBR && interfaceContractMapper.isCompatibleByReference(operation,
                                                                                     targetOp,
                                                                                     Compatibility.SUBSET)) {
                        passByValue = false;
                    } else if (interfaceContractMapper.isCompatibleByValue(operation, targetOp, Compatibility.SUBSET)) {
                        passByValue = true;
                    }
                }
                // it turns out that the chain source and target operations are the same, and are the operation 
                // from the target, not sure if thats by design or a bug. The SCA binding invoker needs to know 
                // the source and target class loaders so pass in the real source operation in the constructor 
                return chain == null ? null : new SCABindingInvoker(chain, operation, mediator, passByValue, epr);
            }
        }
        return null;
    }

    public Invoker createInvoker(Operation operation) {
        if (remotable && distributedProvider != null) {
            return distributedProvider.createInvoker(operation);
        } else {
            Invoker invoker = getInvoker(endpointReference, operation);
            if (invoker == null) {
                throw new ServiceUnavailableException(
                                                      "Unable to create SCA binding invoker for local target " + component
                                                          .getName()
                                                          + " reference "
                                                          + reference.getName()
                                                          + " (bindingURI="
                                                          + binding.getURI()
                                                          + " operation="
                                                          + operation.getName()
                                                          + ")");
            }
            return invoker;
        }
    }

    public void start() {
        if (started) {
            return;
        }
        if (distributedProvider != null) {
            distributedProvider.start();
        }
        started = true;
    }

    public void stop() {
        if (!started) {
            return;
        }

        try {
            if (distributedProvider != null) {
                distributedProvider.stop();
            }
        } finally {
            started = false;
        }
    }

    public void configure() {
        if (distributedProvider instanceof EndpointReferenceProvider) {
            ((EndpointReferenceProvider)distributedProvider).configure();
        }
    }
    
    public boolean supportsNativeAsync() {
        return true;
    }

}
