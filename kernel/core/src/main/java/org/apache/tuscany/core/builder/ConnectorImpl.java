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
import java.util.Map;
import javax.xml.namespace.QName;

import org.osoa.sca.annotations.Constructor;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.builder.Connector;
import org.apache.tuscany.spi.builder.WiringException;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.Invocable;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.component.TargetInvokerCreationException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.ComponentType;
import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ReferenceTarget;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.model.ServiceDefinition;
import org.apache.tuscany.spi.model.physical.WireDefinition;
import org.apache.tuscany.spi.services.work.WorkScheduler;
import org.apache.tuscany.spi.util.UriHelper;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.Wire;
import org.apache.tuscany.spi.wire.WirePostProcessorRegistry;

import org.apache.tuscany.core.binding.local.LocalServiceBinding;
import org.apache.tuscany.core.component.ComponentManager;
import org.apache.tuscany.core.wire.InvocationChainImpl;
import org.apache.tuscany.core.wire.InvokerInterceptor;
import org.apache.tuscany.core.wire.NonBlockingInterceptor;
import org.apache.tuscany.core.wire.WireImpl;
import org.apache.tuscany.core.wire.WireUtils;

/**
 * The default connector implmentation
 *
 * @version $$Rev$$ $$Date$$
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
    public ConnectorImpl(
        @Autowire WirePostProcessorRegistry processorRegistry,
        @Autowire ComponentManager componentManager,
        @Autowire WorkScheduler scheduler,
        @Autowire WorkContext workContext) {
        this.postProcessorRegistry = processorRegistry;
        this.componentManager = componentManager;
        this.scheduler = scheduler;
        this.workContext = workContext;
    }


    public void connect(WireDefinition definition) throws WiringException {
        throw new UnsupportedOperationException();
    }

    public void connect(ComponentDefinition<? extends Implementation<?>> definition) throws WiringException {
        URI sourceUri = definition.getUri();
        Component source = componentManager.getComponent(sourceUri);
        if (source == null) {
            throw new ComponentNotFoundException("Source not found", sourceUri);
        }
        ComponentType<?, ?, ?> type = definition.getImplementation().getComponentType();
        if (type instanceof CompositeComponentType) {
            CompositeComponentType<?, ?, ?> compositeType = (CompositeComponentType<?, ?, ?>) type;
            for (ComponentDefinition<? extends Implementation<?>> child : compositeType.getComponents().values()) {
                connect(child);
            }
            for (ServiceDefinition child : compositeType.getServices().values()) {
                connect(child);
            }
            for (ReferenceDefinition child : compositeType.getReferences().values()) {
                connect(child);
            }
        }
        Map<String, ReferenceTarget> targets = definition.getReferenceTargets();
        for (ReferenceTarget referenceTarget : targets.values()) {
            List<Wire> wires = new ArrayList<Wire>();
            String refName = referenceTarget.getReferenceName().getFragment();
            ReferenceDefinition refDefinition = type.getReferences().get(refName);
            assert refDefinition != null;
            List<URI> uris = referenceTarget.getTargets();
            for (URI uri : uris) {
                URI targetUri = UriHelper.getDefragmentedName(uri);
                Component target = componentManager.getComponent(targetUri);
                if (target == null && !refDefinition.isRequired()) {
                    // a non-required reference, just skip
                    continue;
                }
                if (target == null) {
                    throw new ComponentNotFoundException("Target not found", targetUri);
                }
                String fragment = uri.getFragment();
                URI sourceURI = refDefinition.getUri();
                Wire wire = createWire(sourceURI, uri, refDefinition.getServiceContract(), Wire.LOCAL_BINDING);
                if (fragment == null) {
                    try {
                        // add target invokers
                        attachInvokers(wire, source, target);
                    } catch (TargetInvokerCreationException e) {
                        throw new WireCreationException("Error creating invoker", sourceUri, targetUri, e);
                    }
                } else {
                    if (target instanceof CompositeComponent) {
                        CompositeComponent composite = (CompositeComponent) target;
                        Service service = composite.getService(fragment);
                        if (service != null) {
                            if (service.getServiceBindings().isEmpty()) {
                                // for now, throw an assertion exception.
                                // We will need to choose bindings during allocation
                                throw new AssertionError();
                            }
                            ServiceBinding binding = service.getServiceBindings().get(0);
                            try {
                                // add target invokers
                                attachInvokers(wire, source, binding);
                            } catch (TargetInvokerCreationException e) {
                                throw new WireCreationException("Error creating invoker", sourceUri, targetUri, e);
                            }
                        }
                        Reference reference = composite.getReference(fragment);
                        if (reference != null) {
                            ReferenceBinding binding = reference.getReferenceBindings().get(0);
                            try {
                                // add target invokers
                                attachInvokers(wire, source, binding);
                            } catch (TargetInvokerCreationException e) {
                                throw new WireCreationException("Error creating invoker", sourceUri, targetUri, e);
                            }
                        } else if (refDefinition.isRequired()) {
                            throw new ComponentNotFoundException("Target not found", targetUri);
                        } else if (wire == null) {
                            continue;
                        }
                    } else {
                        // atomic component
                        try {
                            // add target invokers
                            attachInvokers(wire, source, target);
                        } catch (TargetInvokerCreationException e) {
                            throw new WireCreationException("Error creating invoker", sourceUri, targetUri, e);
                        }
                    }
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

    protected void connect(ServiceDefinition definition) throws WiringException {
        URI uri = definition.getUri();
        URI sourceUri = UriHelper.getDefragmentedName(uri);
        URI targetUri = UriHelper.getDefragmentedName(definition.getTarget());
        Component source = componentManager.getComponent(sourceUri);
        if (source == null) {
            throw new ComponentNotFoundException("Source not found", sourceUri);
        }
        if (!(source instanceof CompositeComponent)) {
            // this should not happen
            throw new InvalidSourceTypeException("Illegal source type", uri, targetUri);
        }
        CompositeComponent sourceComposite = (CompositeComponent) source;
        Service service = sourceComposite.getService(uri.getFragment());
        if (service == null) {
            throw new SourceServiceNotFoundException("Service not found on composite", uri);
        }
        Component target = componentManager.getComponent(targetUri);
        if (target == null) {
            throw new ComponentNotFoundException("Target not found", sourceUri);
        }
        ServiceContract<?> contract = definition.getServiceContract();
        if (target instanceof CompositeComponent) {
            String fragment = definition.getTarget().getFragment();
            CompositeComponent targetComposite = (CompositeComponent) target;
            Invocable invocable;
            Reference targetReference = targetComposite.getReference(fragment);
            if (targetReference == null) {
                Service targetService = targetComposite.getService(fragment);
                if (targetService == null) {
                    throw new TargetServiceNotFoundException("Service not found", sourceUri, definition.getTarget());
                }
                // TODO select binding in allocator
                if (targetService.getServiceBindings().isEmpty()) {
                    invocable = new LocalServiceBinding(service.getUri());
                } else {
                    invocable = targetService.getServiceBindings().get(0);
                }
            } else {
                if (targetReference.getReferenceBindings().isEmpty()) {
                    throw new NoBindingException("No binding specified for wire", sourceUri, targetUri);
                } else {
                    invocable = targetReference.getReferenceBindings().get(0);
                }
            }
            for (ServiceBinding binding : service.getServiceBindings()) {
                Wire wire = createWire(uri, targetUri, contract, binding.getBindingType());
                binding.setWire(wire);
                if (postProcessorRegistry != null) {
                    postProcessorRegistry.process(wire);
                }
                try {
                    attachInvokers(wire, binding, invocable);
                } catch (TargetInvokerCreationException e) {
                    throw new WireCreationException("Error creating invoker", sourceUri, targetUri, e);
                }
            }

        } else {
            // TODO if no binding, do local
            for (ServiceBinding binding : service.getServiceBindings()) {
                Wire wire = createWire(uri, targetUri, contract, binding.getBindingType());
                binding.setWire(wire);
                if (postProcessorRegistry != null) {
                    postProcessorRegistry.process(wire);
                }
                try {
                    attachInvokers(wire, binding, target);
                } catch (TargetInvokerCreationException e) {
                    throw new WireCreationException("Error creating invoker", sourceUri, targetUri, e);
                }
            }
        }
    }

    protected void connect(ReferenceDefinition definition) throws WiringException {
        URI uri = definition.getUri();
        URI sourceUri = UriHelper.getDefragmentedName(uri);
        Component source = componentManager.getComponent(sourceUri);
        if (source == null) {
            throw new ComponentNotFoundException("Source not found", sourceUri);
        }
        if (!(source instanceof CompositeComponent)) {
            throw new AssertionError("Illegal source type");
        }
        CompositeComponent sourceComposite = (CompositeComponent) source;
        Reference reference = sourceComposite.getReference(uri.getFragment());
        if (reference == null) {
            throw new SourceServiceNotFoundException("Reference not found on composite", uri);
        }

        for (ReferenceBinding binding : reference.getReferenceBindings()) {
            // create wire
            if (Wire.LOCAL_BINDING.equals(binding.getBindingType())) {
                URI targetUri = binding.getTargetUri();
                ServiceContract<?> contract = binding.getBindingServiceContract();
                QName type = binding.getBindingType();
                Wire wire = createWire(sourceUri, targetUri, contract, type);
                binding.setWire(wire);
                // wire local bindings to their targets
                Component target = componentManager.getComponent(UriHelper.getDefragmentedName(targetUri));
                if (target == null) {
                    throw new ComponentNotFoundException("Target not found", sourceUri);
                }
                if (target instanceof CompositeComponent) {
                    String fragment = targetUri.getFragment();
                    CompositeComponent targetComposite = (CompositeComponent) target;
                    Invocable invocable;
                    Reference targetReference = targetComposite.getReference(fragment);
                    if (targetReference == null) {
                        Service targetService = targetComposite.getService(fragment);
                        if (targetService == null) {
                            throw new TargetServiceNotFoundException("Service not found", sourceUri, targetUri);
                        }
                        // TODO select binding in allocator
                        if (targetService.getServiceBindings().isEmpty()) {
                            throw new NoBindingException("No binding specified for wire", sourceUri, targetUri);
                        } else {
                            invocable = targetService.getServiceBindings().get(0);
                        }
                    } else {
                        if (targetReference.getReferenceBindings().isEmpty()) {
                            throw new NoBindingException("No binding specified for wire", sourceUri, targetUri);
                        } else {
                            invocable = targetReference.getReferenceBindings().get(0);
                        }
                    }
                    try {
                        attachInvokers(wire, binding, invocable);
                    } catch (TargetInvokerCreationException e) {
                        throw new WireCreationException("Error creating invoker", sourceUri, targetUri, e);
                    }
                } else {
                    try {
                        attachInvokers(wire, binding, target);
                    } catch (TargetInvokerCreationException e) {
                        throw new WireCreationException("Error creating invoker", sourceUri, targetUri, e);
                    }
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

    protected Wire createWire(URI sourceURI, URI targetUri, ServiceContract<?> contract, QName bindingType) {
        Wire wire = new WireImpl(bindingType);
        wire.setSourceContract(contract);
        wire.setSourceUri(sourceURI);
        wire.setTargetUri(targetUri);
        for (Operation<?> operation : contract.getOperations().values()) {
            InvocationChain chain = new InvocationChainImpl(operation);
            if (operation.isNonBlocking()) {
                chain.addInterceptor(new NonBlockingInterceptor(scheduler, workContext));
            }
            chain.addInterceptor(new InvokerInterceptor());
            wire.addInvocationChain(operation, chain);

        }
        wire.setCallbackReferenceName(contract.getCallbackName());
        for (Operation<?> operation : contract.getCallbackOperations().values()) {
            InvocationChain chain = new InvocationChainImpl(operation);
            if (operation.isNonBlocking()) {
                chain.addInterceptor(new NonBlockingInterceptor(scheduler, workContext));
            }
            chain.addInterceptor(new InvokerInterceptor());
            wire.addCallbackInvocationChain(operation, chain);
        }
        return wire;
    }

    private void attachInvokers(Wire wire, Invocable source, Invocable target)
        throws TargetInvokerCreationException {
        for (InvocationChain chain : wire.getInvocationChains().values()) {
            String name = target.getUri().getFragment();
            chain.setTargetInvoker(target.createTargetInvoker(name, chain.getOperation()));
        }
        for (InvocationChain chain : wire.getCallbackInvocationChains().values()) {
            chain.setTargetInvoker(source.createTargetInvoker(null, chain.getOperation()));
        }
    }

    protected void optimize(Component source, Component target, Wire wire) {
        boolean optimizableScopes = isOptimizable(source.getScope(), target.getScope());
        if (optimizableScopes && target.isOptimizable() && WireUtils.isOptimizable(wire)) {
            wire.setOptimizable(true);
            wire.setTarget((AtomicComponent) target);
        } else {
            wire.setOptimizable(false);
        }
    }

    protected boolean isOptimizable(Scope pReferrer, Scope pReferee) {
        if (pReferrer == Scope.UNDEFINED
            || pReferee == Scope.UNDEFINED
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
        } else //noinspection SimplifiableIfStatement
            if (pReferrer == Scope.SYSTEM && pReferee == Scope.COMPOSITE) {
                // case where a service context points to a composite scoped component
                return true;
            } else {
                return pReferrer == Scope.COMPOSITE && pReferee == Scope.SYSTEM;
            }
    }
}