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
package org.apache.tuscany.core.builder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.tuscany.assembly.ComponentService;
import org.apache.tuscany.assembly.ComponentType;
import org.apache.tuscany.assembly.Composite;
import org.apache.tuscany.assembly.CompositeReference;
import org.apache.tuscany.assembly.CompositeService;
import org.apache.tuscany.assembly.Contract;
import org.apache.tuscany.assembly.Multiplicity;
import org.apache.tuscany.core.wire.InvocationChainImpl;
import org.apache.tuscany.core.wire.InvokerInterceptor;
import org.apache.tuscany.core.wire.NonBlockingInterceptor;
import org.apache.tuscany.core.wire.WireImpl;
import org.apache.tuscany.core.wire.WireUtils;
import org.apache.tuscany.idl.Operation;
import org.apache.tuscany.spi.Scope;
import org.apache.tuscany.spi.builder.Connector;
import org.apache.tuscany.spi.builder.WiringException;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.ComponentManager;
import org.apache.tuscany.spi.component.Invocable;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.component.TargetInvokerCreationException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.services.work.WorkScheduler;
import org.apache.tuscany.spi.util.UriHelper;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.Wire;
import org.apache.tuscany.spi.wire.WirePostProcessorRegistry;
import org.osoa.sca.annotations.Constructor;

/**
 * The default connector implmentation
 * 
 * @version $$Rev$$ $$Date: 2007-04-03 10:40:40 -0700 (Tue, 03 Apr
 *          2007) $$
 */
public class ConnectorImpl implements Connector {
    private WirePostProcessorRegistry postProcessorRegistry;
    private ComponentManager componentManager;
    private WorkContext workContext;
    private WorkScheduler scheduler;

    public ConnectorImpl(ComponentManager componentManager) {
        this.componentManager = componentManager;
    }

    @Constructor
    public ConnectorImpl(@org.osoa.sca.annotations.Reference
    WirePostProcessorRegistry processorRegistry, @org.osoa.sca.annotations.Reference
    ComponentManager componentManager, @org.osoa.sca.annotations.Reference
    WorkScheduler scheduler, @org.osoa.sca.annotations.Reference
    WorkContext workContext) {
        this.postProcessorRegistry = processorRegistry;
        this.componentManager = componentManager;
        this.scheduler = scheduler;
        this.workContext = workContext;
    }

    private org.apache.tuscany.assembly.Reference getReference(List<org.apache.tuscany.assembly.Reference> refs,
                                                               String name) {
        for (org.apache.tuscany.assembly.Reference ref : refs) {
            if (ref.getName().equals(name)) {
                return ref;
            }
        }
        return null;
    }

    public void connect(URI groupId, org.apache.tuscany.assembly.Component definition) throws WiringException {
        URI sourceUri = URI.create(groupId.toString() + "/" + definition.getName());
        Component source = componentManager.getComponent(sourceUri);
        if (source == null) {
            throw new ComponentNotFoundException("Source not found", sourceUri);
        }
        ComponentType type = definition.getImplementation();
        if (type instanceof Composite) {
            Composite composite = (Composite)type;
            for (org.apache.tuscany.assembly.Component child : composite.getComponents()) {
                connect(sourceUri, child);
            }
            for (org.apache.tuscany.assembly.Service child : composite.getServices()) {
                connect(groupId, (CompositeService)child);
            }
            for (org.apache.tuscany.assembly.Reference child : composite.getReferences()) {
                connect(groupId, (CompositeReference)child);
            }
        }

        for (org.apache.tuscany.assembly.Reference ref : definition.getReferences()) {
            List<Wire> wires = new ArrayList<Wire>();
            String refName = ref.getName();
            org.apache.tuscany.assembly.Reference refDefinition = getReference(type.getReferences(), refName);
            assert refDefinition != null;
            for (ComponentService service : ref.getTargets()) {
                URI targetUri = groupId;
                Component target = componentManager.getComponent(targetUri);
                boolean required = refDefinition.getMultiplicity() == Multiplicity.ONE_N || refDefinition
                                       .getMultiplicity() == Multiplicity.ONE_ONE;

                if (target == null && !required) {
                    // a non-required reference, just skip
                    continue;
                }
                if (target == null) {
                    throw new ComponentNotFoundException("Target not found", targetUri);
                }
                URI sourceURI = URI.create(groupId + "#" + refName);
                URI uri = URI.create(groupId + "#" + service.getName());
                Wire wire = createWire(sourceURI, uri, refDefinition, Wire.LOCAL_BINDING);
                try {
                    attachInvokers(service.getName(), wire, source, target);
                } catch (TargetInvokerCreationException e) {
                    throw new WireCreationException("Error creating invoker", sourceUri, targetUri, e);
                }
                if (postProcessorRegistry != null) {
                    postProcessorRegistry.process(wire);
                }
                optimize(source, target, wire);
                wires.add(wire);
                if (!wire.getCallbackInvocationChains().isEmpty()) {
                    target.attachCallbackWire(wire);
                }
            }
            if (wires.size() > 1) {
                // attach as a multiplicity
                source.attachWires(wires);
            } else if (wires.size() == 1) {
                // attach as a single wire
                Wire wire = wires.get(0);
                source.attachWire(wire);
            }
        }
    }

    protected void connect(URI groupId, CompositeService definition) throws WiringException {
        URI uri = URI.create(groupId + "#" + definition.getName());
        URI sourceUri = groupId;
        // FIXME: How to access the URI of the promoted service
        URI targetUri = URI.create(groupId + "#" + definition.getPromotedService().getName());
        URI baseTargetUri = UriHelper.getDefragmentedName(targetUri);
        Component source = componentManager.getComponent(sourceUri);
        if (source == null) {
            throw new ComponentNotFoundException("Source not found", sourceUri);
        }
        Service service = source.getService(uri.getFragment());
        if (service == null) {
            throw new SourceServiceNotFoundException("Service not found on composite", uri);
        }
        Component target = componentManager.getComponent(baseTargetUri);
        if (target == null) {
            throw new ComponentNotFoundException("Target not found", sourceUri);
        }
        Contract contract = definition;
        // TODO if no binding, do local
        for (ServiceBinding binding : service.getServiceBindings()) {
            Wire wire = createWire(uri, targetUri, contract, binding.getBindingType());
            binding.setWire(wire);
            if (postProcessorRegistry != null) {
                postProcessorRegistry.process(wire);
            }
            try {
                attachInvokers(definition.getName(), wire, binding, target);
            } catch (TargetInvokerCreationException e) {
                throw new WireCreationException("Error creating invoker", sourceUri, baseTargetUri, e);
            }
        }
    }

    protected void connect(URI groupId, CompositeReference definition) throws WiringException {
        URI sourceUri = groupId;
        Component source = componentManager.getComponent(sourceUri);
        if (source == null) {
            throw new ComponentNotFoundException("Source not found", sourceUri);
        }
        Reference reference = source.getReference(definition.getName());
        if (reference == null) {
            throw new SourceServiceNotFoundException("Reference not found on composite", groupId);
        }

        for (ReferenceBinding binding : reference.getReferenceBindings()) {
            // create wire
            if (Wire.LOCAL_BINDING.equals(binding.getBindingType())) {
                URI targetUri = binding.getTargetUri();
                Contract contract = binding.getBindingServiceContract();
                QName type = binding.getBindingType();
                Wire wire = createWire(sourceUri, targetUri, contract, type);
                binding.setWire(wire);
                // wire local bindings to their targets
                Component target = componentManager.getComponent(UriHelper.getDefragmentedName(targetUri));
                if (target == null) {
                    throw new ComponentNotFoundException("Target not found", sourceUri);
                }
                try {
                    attachInvokers(targetUri.getFragment(), wire, binding, target);
                } catch (TargetInvokerCreationException e) {
                    throw new WireCreationException("Error creating invoker", sourceUri, targetUri, e);
                }
            } else {
                Wire wire = createWire(sourceUri, null, binding.getBindingServiceContract(), binding.getBindingType());
                if (postProcessorRegistry != null) {
                    postProcessorRegistry.process(wire);
                }
                binding.setWire(wire);
            }
        }
    }

    protected Wire createWire(URI sourceURI, URI targetUri, Contract contract, QName bindingType) {
        Wire wire = new WireImpl(bindingType);
        wire.setSourceContract(contract);
        wire.setTargetContract(contract);
        wire.setSourceUri(sourceURI);
        wire.setTargetUri(targetUri);
        for (Operation operation : contract.getInterface().getOperations()) {
            InvocationChain chain = new InvocationChainImpl(operation);
            if (operation.isNonBlocking()) {
                chain.addInterceptor(new NonBlockingInterceptor(scheduler, workContext));
            }
            chain.addInterceptor(new InvokerInterceptor());
            wire.addInvocationChain(operation, chain);

        }
        for (Operation operation : contract.getCallbackInterface().getOperations()) {
            InvocationChain chain = new InvocationChainImpl(operation);
            if (operation.isNonBlocking()) {
                chain.addInterceptor(new NonBlockingInterceptor(scheduler, workContext));
            }
            chain.addInterceptor(new InvokerInterceptor());
            wire.addCallbackInvocationChain(operation, chain);
        }
        return wire;
    }

    /**
     * @Deprecated
     */
    private void attachInvokers(String name, Wire wire, Invocable source, Invocable target)
        throws TargetInvokerCreationException {
        // TODO section will deleted be replaced when we cut-over to the
        // physical marshallers
        for (InvocationChain chain : wire.getInvocationChains().values()) {
            chain.setTargetInvoker(target.createTargetInvoker(name, chain.getOperation(), false));
        }
        for (InvocationChain chain : wire.getCallbackInvocationChains().values()) {
            chain.setTargetInvoker(source.createTargetInvoker(null, chain.getOperation(), true));
        }
    }

    /**
     * @Deprecated
     */
    protected void optimize(Component source, Component target, Wire wire) {
        boolean optimizableScopes = isOptimizable(source.getScope(), target.getScope());
        if (optimizableScopes && target.isOptimizable() && WireUtils.isOptimizable(wire)) {
            wire.setOptimizable(true);
            wire.setTarget((AtomicComponent)target);
        } else {
            wire.setOptimizable(false);
        }
    }

    protected boolean isOptimizable(Scope pReferrer, Scope pReferee) {
        if (pReferrer == Scope.UNDEFINED || pReferee == Scope.UNDEFINED
            || pReferrer == Scope.CONVERSATION
            || pReferee == Scope.CONVERSATION) {
            return false;
        }
        if (pReferee == pReferrer) {
            return true;
        } else if (pReferrer == Scope.STATELESS) {
            return true;
        } else if (pReferee == Scope.STATELESS) {
            return false;
        } else if (pReferrer == Scope.REQUEST && pReferee == Scope.SESSION) {
            return true;
        } else if (pReferrer == Scope.REQUEST && pReferee == Scope.COMPOSITE) {
            return true;
        } else if (pReferrer == Scope.REQUEST && pReferee == Scope.SYSTEM) {
            return true;
        } else if (pReferrer == Scope.SESSION && pReferee == Scope.COMPOSITE) {
            return true;
        } else if (pReferrer == Scope.SESSION && pReferee == Scope.SYSTEM) {
            return true;
        } else // noinspection SimplifiableIfStatement
        if (pReferrer == Scope.SYSTEM && pReferee == Scope.COMPOSITE) {
            // case where a service context points to a composite scoped
            // component
            return true;
        } else {
            return pReferrer == Scope.COMPOSITE && pReferee == Scope.SYSTEM;
        }
    }
}
