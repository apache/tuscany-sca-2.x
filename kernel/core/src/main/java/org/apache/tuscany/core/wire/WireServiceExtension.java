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

import java.util.Map;

import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.ComponentType;
import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.model.Implementation;
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

import org.apache.tuscany.core.implementation.composite.CompositeReference;

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

    public OutboundWire createWire(ReferenceTarget reference, ReferenceDefinition def) {
        if (!def.isAutowire() && reference.getTargets().size() != 1) {
            //TODO multiplicity
            throw new UnsupportedOperationException();
        }
        ServiceContract<?> contract = def.getServiceContract();
        OutboundWire wire = new OutboundWireImpl();
        if (!def.isAutowire()) {
            QualifiedName qName = new QualifiedName(reference.getTargets().get(0).toString());
            wire.setTargetName(qName);
        } else {
            wire.setAutowire(true);
        }
        wire.setServiceContract(contract);
        wire.setReferenceName(reference.getReferenceName());
        for (Operation<?> operation : contract.getOperations().values()) {
            //TODO handle policy
            OutboundInvocationChain chain = createOutboundChain(operation);
            wire.addInvocationChain(operation, chain);

        }
        if (contract.getCallbackName() != null) {
            wire.setCallbackInterface(contract.getCallbackClass());
            for (Operation<?> operation : contract.getCallbackOperations().values()) {
                InboundInvocationChain callbackTargetChain = createInboundChain(operation);
                // TODO handle policy
                //TODO statement below could be cleaner
                callbackTargetChain.addInterceptor(new InvokerInterceptor());
                wire.addTargetCallbackInvocationChain(operation, callbackTargetChain);
            }
        }
        return wire;
    }

    public InboundWire createWire(ServiceDefinition service) {
        InboundWire wire = new InboundWireImpl();
        ServiceContract<?> contract = service.getServiceContract();
        wire.setServiceContract(contract);
        wire.setServiceName(service.getName());
        for (Operation<?> operation : contract.getOperations().values()) {
            InboundInvocationChain chain = createInboundChain(operation);
            // TODO handle policy
            chain.addInterceptor(new InvokerInterceptor());
            wire.addInvocationChain(operation, chain);
        }
        if (contract.getCallbackName() != null) {
            wire.setCallbackReferenceName(service.getCallbackReferenceName());
        }
        return wire;
    }

    public void createWires(Component component, ComponentDefinition<?> definition) {
        Implementation<?> implementation = definition.getImplementation();
        ComponentType<?, ?, ?> componentType = implementation.getComponentType();
        for (ServiceDefinition service : componentType.getServices().values()) {
            if (componentType instanceof CompositeComponentType<?, ?, ?>) {
                // If this is the case, then it means that component has already been returned
                // by CompositeBuilder and thus its children, in particular composite services,
                // have been registered
                CompositeComponent compositeComponent = (CompositeComponent) component;
                Service serviceChild;
                if (component.isSystem()) {
                    // FIXME JFM test
                    serviceChild = (Service) compositeComponent.getSystemChild(service.getName());
                } else {
                    serviceChild = (Service) compositeComponent.getChild(service.getName());
                }
                assert serviceChild != null;
            } else {
                InboundWire wire = createWire(service);
                wire.setContainer(component);
                component.addInboundWire(wire);
            }
        }

        for (ReferenceTarget referenceTarget : definition.getReferenceTargets().values()) {
            Map<String, ? extends ReferenceDefinition> references = componentType.getReferences();
            ReferenceDefinition mappedReference = references.get(referenceTarget.getReferenceName());
            assert mappedReference != null;
            OutboundWire wire = createWire(referenceTarget, mappedReference);
            wire.setContainer(component);
            component.addOutboundWire(wire);
            if (componentType instanceof CompositeComponentType<?, ?, ?>) {
                // If this is the case, then it means that component has already been returned
                // by CompositeBuilder and thus its children, in particular composite references,
                // have been registered
                CompositeComponent compositeComponent = (CompositeComponent) component;
                Reference reference = (Reference) compositeComponent.getChild(referenceTarget.getReferenceName());
                assert reference != null;
                if (reference instanceof CompositeReference) {
                    reference.setOutboundWire(wire);
                    // Notice that now the more immediate container of the wire is the composite reference
                    wire.setContainer(reference);
                }
            }
        }
    }

    public void createWires(Reference reference, ServiceContract<?> contract) {
        InboundWire inboundWire = new InboundWireImpl();
        inboundWire.setServiceContract(contract);
        inboundWire.setContainer(reference);
        for (Operation<?> operation : contract.getOperations().values()) {
            InboundInvocationChain chain = createInboundChain(operation);
            inboundWire.addInvocationChain(operation, chain);
        }
        OutboundWire outboundWire = new OutboundWireImpl();

        // [rfeng] Check if the Reference has the binding contract
        ServiceContract<?> bindingContract = reference.getBindingServiceContract();
        if (bindingContract == null) {
            bindingContract = contract;
        }
        outboundWire.setServiceContract(bindingContract);
        outboundWire.setContainer(reference);
        for (Operation<?> operation : bindingContract.getOperations().values()) {
            OutboundInvocationChain chain = createOutboundChain(operation);
            chain.addInterceptor(new InvokerInterceptor());
            outboundWire.addInvocationChain(operation, chain);
        }

        // Notice that we skip inboundWire.setCallbackReferenceName
        // First, an inbound wire's callbackReferenceName is only retrieved by JavaAtomicComponent
        // to create a callback injector based on the callback reference member; a composite reference
        // should not need to do that
        // Second, a reference definition does not have a callback reference name like a service
        // definition does
        reference.setInboundWire(inboundWire);
        reference.setOutboundWire(outboundWire);
    }

    public void createWires(ServiceBinding serviceBinding, String targetName, ServiceContract<?> contract) {
        InboundWire inboundWire = new InboundWireImpl();

        // [rfeng] Check if the Reference has the serviceBinding contract
        ServiceContract<?> bindingContract = serviceBinding.getBindingServiceContract();
        if (bindingContract == null) {
            bindingContract = contract;
        }
        inboundWire.setServiceContract(bindingContract);
        inboundWire.setContainer(serviceBinding);
        for (Operation<?> operation : bindingContract.getOperations().values()) {
            InboundInvocationChain inboundChain = createInboundChain(operation);
            // TODO JFM remove need for this
            inboundChain.addInterceptor(new SynchronousBridgingInterceptor());
            inboundWire.addInvocationChain(operation, inboundChain);
        }

        OutboundWire outboundWire = new OutboundWireImpl();
        outboundWire.setServiceContract(contract);
        outboundWire.setTargetName(new QualifiedName(targetName));
        outboundWire.setContainer(serviceBinding);

        for (Operation<?> operation : contract.getOperations().values()) {
            OutboundInvocationChain outboundChain = createOutboundChain(operation);
            outboundWire.addInvocationChain(operation, outboundChain);
        }

        // Add target callback chain to outbound wire
        if (contract.getCallbackName() != null) {
            outboundWire.setCallbackInterface(contract.getCallbackClass());
            for (Operation<?> operation : contract.getCallbackOperations().values()) {
                InboundInvocationChain callbackTargetChain = createInboundChain(operation);
                // TODO handle policy
                //TODO statement below could be cleaner
                callbackTargetChain.addInterceptor(new InvokerInterceptor());
                outboundWire.addTargetCallbackInvocationChain(operation, callbackTargetChain);
            }
        }
        serviceBinding.setInboundWire(inboundWire);
        serviceBinding.setOutboundWire(outboundWire);
    }

    /**
     * Compares two operations for wiring compatibility as defined by the SCA assembly specification, namely: <p/> <ol>
     * <li>compatibility for the individual method is defined as compatibility of the signature, that is method name,
     * input types, and output types MUST BE the same. <li>the order of the input and output types also MUST BE the
     * same. <li>the set of Faults and Exceptions expected by the source MUST BE the same or be a superset of those
     * specified by the service. </ol>
     *
     * @param source the source contract to compare
     * @param target the target contract to compare
     * @throws org.apache.tuscany.spi.wire.IncompatibleServiceContractException
     *          if the two contracts don't match
     */
    public void checkCompatibility(ServiceContract<?> source, ServiceContract<?> target, boolean ignoreCallback)
        throws IncompatibleServiceContractException {
        if (source == target) {
            // Shortcut for performance
            return;
        }
        if (source.isRemotable() != target.isRemotable()) {
            throw new IncompatibleServiceContractException("Remotable settings do not match", source, target);
        }
        if (source.getInteractionScope() != target.getInteractionScope()) {
            throw new IncompatibleServiceContractException("Interaction scopes settings do not match", source, target);
        }

        for (Operation<?> operation : source.getOperations().values()) {
            Operation<?> targetOperation = target.getOperations().get(operation.getName());
            if (targetOperation == null) {
                throw new IncompatibleServiceContractException("Operation not found on target", source, target);
            }
            if (!operation.equals(targetOperation)) {
                throw new IncompatibleServiceContractException("Target operations are not compatible", source, target);
            }
        }

        if (ignoreCallback) {
            return;
        }

        for (Operation<?> operation : source.getCallbackOperations().values()) {
            Operation<?> targetOperation = target.getCallbackOperations().get(operation.getName());
            if (targetOperation == null) {
                throw new IncompatibleServiceContractException("Callback operation not found on target",
                    source,
                    target,
                    null,
                    targetOperation);
            }
            if (!operation.equals(targetOperation)) {
                throw new IncompatibleServiceContractException("Target callback operation is not compatible",
                    source,
                    target,
                    operation,
                    targetOperation);
            }
        }
    }

}
