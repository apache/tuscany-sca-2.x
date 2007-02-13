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
package org.apache.tuscany.core.wire;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.ComponentType;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.model.Multiplicity;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ReferenceTarget;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.model.ServiceDefinition;
import org.apache.tuscany.spi.policy.PolicyBuilderRegistry;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.IncompatibleServiceContractException;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.WireService;

import org.apache.tuscany.core.binding.local.LocalReferenceBinding;

/**
 * Base class for wire service extensions
 *
 * @version $Rev$ $Date$
 */
public abstract class WireServiceExtension implements WireService {
    protected PolicyBuilderRegistry policyRegistry;
    protected WorkContext context;

    protected WireServiceExtension(WorkContext context, PolicyBuilderRegistry policyRegistry) {
        this.policyRegistry = policyRegistry;
        this.context = context;
    }

    public OutboundInvocationChain createOutboundChain(Operation<?> operation) {
        return new OutboundInvocationChainImpl(operation);
    }

    public InboundInvocationChain createInboundChain(Operation<?> operation) {
        return new InboundInvocationChainImpl(operation);
    }

    public InboundWire createWire(ServiceDefinition service) {
        InboundWire wire = new InboundWireImpl();
        ServiceContract<?> contract = service.getServiceContract();
        wire.setServiceContract(contract);
        wire.setUri(service.getUri());
        for (Operation<?> operation : contract.getOperations().values()) {
            InboundInvocationChain chain = createInboundChain(operation);
            chain.addInterceptor(new InvokerInterceptor());
            wire.addInvocationChain(operation, chain);
        }
        if (contract.getCallbackName() != null) {
            wire.setCallbackReferenceName(service.getCallbackReferenceName());
        }
        return wire;
    }

    public void createWires(AtomicComponent component, ComponentDefinition<?> definition) {
        Implementation<?> implementation = definition.getImplementation();
        ComponentType<?, ?, ?> componentType = implementation.getComponentType();
        // create incoming service wires
        for (ServiceDefinition service : componentType.getServices().values()) {
            InboundWire wire = createWire(service);
            // JFM TODO refactor
            wire.setComponent(component);
            component.addInboundWire(wire);
        }
        // create outgoing reference wires
        for (ReferenceTarget referenceTarget : definition.getReferenceTargets().values()) {
            Map<String, ? extends ReferenceDefinition> references = componentType.getReferences();
            ReferenceDefinition mappedReference = references.get(referenceTarget.getReferenceName().getFragment());
            assert mappedReference != null;
            List<OutboundWire> wires = createWire(referenceTarget, mappedReference);
            Multiplicity multiplicity = mappedReference.getMultiplicity();
            if (multiplicity == Multiplicity.ZERO_ONE || multiplicity == Multiplicity.ONE_ONE) {
                // 0..1 or 1..1
                for (OutboundWire wire : wires) {
                    component.addOutboundWire(wire);
                }
            } else {
                // 0..N or 1..N
                component.addOutboundWires(wires);
            }
        }
    }

    public void createWires(ReferenceBinding referenceBinding, ServiceContract<?> contract, URI target) {
        InboundWire inboundWire = new InboundWireImpl(referenceBinding.getBindingType());
        inboundWire.setServiceContract(contract);
        inboundWire.setUri(referenceBinding.getUri());
        for (Operation<?> operation : contract.getOperations().values()) {
            InboundInvocationChain chain = createInboundChain(operation);
            inboundWire.addInvocationChain(operation, chain);
        }
        OutboundWire outboundWire = new OutboundWireImpl(referenceBinding.getBindingType());
        outboundWire.setUri(referenceBinding.getUri());
        outboundWire.setTargetUri(target);
        // [rfeng] Check if the Reference has the binding contract
        ServiceContract<?> bindingContract = referenceBinding.getBindingServiceContract();
        if (bindingContract == null) {
            bindingContract = contract;
        }
        outboundWire.setServiceContract(bindingContract);
        for (Operation<?> operation : bindingContract.getOperations().values()) {
            OutboundInvocationChain chain = createOutboundChain(operation);
            if (referenceBinding instanceof LocalReferenceBinding) {
                // Not ideal but the local binding case is special as its inbound and outbound wires are connected
                // before the outbound wire is connected to the reference target. This requires the binding outbound
                // chain to have an interceptor to connect to from the binding inbound chain. This outbound
                // interceptor will then be bridged to the head target interceptor
                chain.addInterceptor(new SynchronousBridgingInterceptor());
            } else {
                chain.addInterceptor(new InvokerInterceptor());
            }
            outboundWire.addInvocationChain(operation, chain);
        }
        // Add target callback chain to outbound wire
        if (contract.getCallbackName() != null) {
            outboundWire.setCallbackInterface(contract.getCallbackClass());
            for (Operation<?> operation : contract.getCallbackOperations().values()) {
                InboundInvocationChain callbackTargetChain = createInboundChain(operation);
                callbackTargetChain.addInterceptor(new InvokerInterceptor());
                outboundWire.addTargetCallbackInvocationChain(operation, callbackTargetChain);
            }
        }
        referenceBinding.setInboundWire(inboundWire);
        referenceBinding.setOutboundWire(outboundWire);
    }

    public void createWires(ServiceBinding serviceBinding, ServiceContract<?> contract, String targetName) {
        InboundWire inboundWire = new InboundWireImpl(serviceBinding.getBindingType());
        // [rfeng] Check if the Reference has the serviceBinding contract
        ServiceContract<?> bindingContract = serviceBinding.getBindingServiceContract();
        if (bindingContract == null) {
            bindingContract = contract;
        }
        inboundWire.setServiceContract(bindingContract);
        inboundWire.setUri(serviceBinding.getUri());
        for (Operation<?> operation : bindingContract.getOperations().values()) {
            InboundInvocationChain inboundChain = createInboundChain(operation);
            inboundChain.addInterceptor(new SynchronousBridgingInterceptor());
            inboundWire.addInvocationChain(operation, inboundChain);
        }

        OutboundWire outboundWire = new OutboundWireImpl(serviceBinding.getBindingType());
        outboundWire.setServiceContract(contract);
        outboundWire.setUri(serviceBinding.getUri());
        outboundWire.setTargetUri(URI.create(targetName));

        for (Operation<?> operation : contract.getOperations().values()) {
            OutboundInvocationChain outboundChain = createOutboundChain(operation);
            outboundWire.addInvocationChain(operation, outboundChain);
        }

        // Add target callback chain to outbound wire
        if (contract.getCallbackName() != null) {
            outboundWire.setCallbackInterface(contract.getCallbackClass());
            for (Operation<?> operation : contract.getCallbackOperations().values()) {
                InboundInvocationChain callbackTargetChain = createInboundChain(operation);
                callbackTargetChain.addInterceptor(new InvokerInterceptor());
                outboundWire.addTargetCallbackInvocationChain(operation, callbackTargetChain);
            }
        }
        serviceBinding.setInboundWire(inboundWire);
        serviceBinding.setOutboundWire(outboundWire);
    }

    public boolean checkCompatibility(ServiceContract<?> source,
                                      ServiceContract<?> target,
                                      boolean ignoreCallback,
                                      boolean silent)
        throws IncompatibleServiceContractException {
        if (source == target) {
            // Shortcut for performance
            return true;
        }
        if (source.isRemotable() != target.isRemotable()) {
            if (!silent) {
                throw new IncompatibleServiceContractException("Remotable settings do not match", source, target);
            } else {
                return false;
            }
        }
        if (source.getInteractionScope() != target.getInteractionScope()) {
            if (!silent) {
                throw new IncompatibleServiceContractException("Interaction scopes do not match", source, target);
            } else {
                return false;
            }
        }

        for (Operation<?> operation : source.getOperations().values()) {
            Operation<?> targetOperation = target.getOperations().get(operation.getName());
            if (targetOperation == null) {
                if (!silent) {
                    throw new IncompatibleServiceContractException("Operation not found on target", source, target);
                } else {
                    return false;
                }
            }
            if (!operation.equals(targetOperation)) {
                if (!silent) {
                    throw new IncompatibleServiceContractException("Target operations are not compatible", source,
                        target);
                } else {
                    return false;
                }
            }
        }

        if (ignoreCallback) {
            return true;
        }

        for (Operation<?> operation : source.getCallbackOperations().values()) {
            Operation<?> targetOperation = target.getCallbackOperations().get(operation.getName());
            if (targetOperation == null) {
                if (!silent) {
                    throw new IncompatibleServiceContractException("Callback operation not found on target",
                        source,
                        target,
                        null,
                        targetOperation);
                } else {
                    return false;
                }
            }
            if (!operation.equals(targetOperation)) {
                if (!silent) {
                    throw new IncompatibleServiceContractException("Target callback operation is not compatible",
                        source,
                        target,
                        operation,
                        targetOperation);
                } else {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * Creates a wire for flowing outbound invocations from a reference
     *
     * @param target     the reference definition
     * @param definition the reference target configuration
     * @return the wire the outbound wire
     */
    protected List<OutboundWire> createWire(ReferenceTarget target, ReferenceDefinition definition) {
        ServiceContract<?> contract = definition.getServiceContract();
        List<OutboundWire> outboundWires = new ArrayList<OutboundWire>();
        // NOTE: it is possible that targets are empty (e.g. when a reference is not required). 
        // Return without creating wires
        for (URI uri : target.getTargets()) {
            OutboundWire wire = new OutboundWireImpl();
            wire.setServiceContract(contract);
            wire.setUri(target.getReferenceName());
            wire.setTargetUri(uri);
            for (Operation<?> operation : contract.getOperations().values()) {
                OutboundInvocationChain chain = createOutboundChain(operation);
                wire.addInvocationChain(operation, chain);

            }
            if (contract.getCallbackName() != null) {
                wire.setCallbackInterface(contract.getCallbackClass());
                for (Operation<?> operation : contract.getCallbackOperations().values()) {
                    InboundInvocationChain callbackTargetChain = createInboundChain(operation);
                    callbackTargetChain.addInterceptor(new InvokerInterceptor());
                    wire.addTargetCallbackInvocationChain(operation, callbackTargetChain);
                }
            }
            outboundWires.add(wire);
        }
        return outboundWires;
    }

}
