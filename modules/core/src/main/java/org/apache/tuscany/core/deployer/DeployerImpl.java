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
package org.apache.tuscany.core.deployer;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;

import org.apache.tuscany.assembly.ComponentReference;
import org.apache.tuscany.assembly.ComponentService;
import org.apache.tuscany.assembly.Composite;
import org.apache.tuscany.assembly.CompositeReference;
import org.apache.tuscany.assembly.CompositeService;
import org.apache.tuscany.assembly.Multiplicity;
import org.apache.tuscany.assembly.SCABinding;
import org.apache.tuscany.assembly.impl.DefaultAssemblyFactory;
import org.apache.tuscany.core.builder.ComponentNotFoundException;
import org.apache.tuscany.core.builder.IncompatibleInterfacesException;
import org.apache.tuscany.core.builder.WireCreationException;
import org.apache.tuscany.core.wire.InvocationChainImpl;
import org.apache.tuscany.core.wire.InvokerInterceptor;
import org.apache.tuscany.core.wire.NonBlockingInterceptor;
import org.apache.tuscany.core.wire.WireImpl;
import org.apache.tuscany.core.wire.WireUtils;
import org.apache.tuscany.interfacedef.IncompatibleInterfaceContractException;
import org.apache.tuscany.interfacedef.InterfaceContract;
import org.apache.tuscany.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.interfacedef.impl.DefaultInterfaceContractMapper;
import org.apache.tuscany.spi.Scope;
import org.apache.tuscany.spi.builder.Builder;
import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.builder.BuilderInstantiationException;
import org.apache.tuscany.spi.builder.BuilderRegistry;
import org.apache.tuscany.spi.builder.WiringException;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.ComponentManager;
import org.apache.tuscany.spi.component.Invocable;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.component.RegistrationException;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.component.TargetInvokerCreationException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.deployer.Deployer;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.resolver.ResolutionException;
import org.apache.tuscany.spi.services.work.WorkScheduler;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.Wire;
import org.apache.tuscany.spi.wire.WirePostProcessorRegistry;

/**
 * Default implementation of Deployer.
 * 
 * @version $Rev$ $Date$
 */
public class DeployerImpl implements Deployer {
    private XMLInputFactory xmlFactory;
    private Builder builder;
    private ComponentManager componentManager;
    private ScopeRegistry scopeRegistry;
    private InterfaceContractMapper mapper = new DefaultInterfaceContractMapper();
    private WirePostProcessorRegistry postProcessorRegistry;
    private WorkScheduler workScheduler;
    private WorkContext workContext;

    public DeployerImpl(XMLInputFactory xmlFactory, Builder builder, ComponentManager componentManager, WorkScheduler workScheduler, WorkContext workContext) {
        this.xmlFactory = xmlFactory;
        this.builder = builder;
        this.componentManager = componentManager;
        this.workScheduler = workScheduler;
        this.workContext = workContext;
    }

    public DeployerImpl() {
        xmlFactory = XMLInputFactory.newInstance("javax.xml.stream.XMLInputFactory", getClass().getClassLoader());
    }

    public void setBuilder(BuilderRegistry builder) {
        this.builder = builder;
    }

    public void setComponentManager(ComponentManager componentManager) {
        this.componentManager = componentManager;
    }

    public void setScopeRegistry(ScopeRegistry scopeRegistry) {
        this.scopeRegistry = scopeRegistry;
    }

    public Collection<Component> deploy(Composite composite) throws BuilderException, ResolutionException {
        @SuppressWarnings("unchecked")
        // Create a deployment context
        ScopeContainer<URI> scopeContainer = scopeRegistry.getScopeContainer(Scope.COMPOSITE);
        URI groupId = URI.create(composite.getName().getLocalPart());
        URI componentId = URI.create("/");
        DeploymentContext deploymentContext = new RootDeploymentContext(null, groupId, componentId, xmlFactory,
                                                                        scopeContainer);

        // Create a default component implemented by the given composite
        org.apache.tuscany.assembly.Component componentDef = new DefaultAssemblyFactory().createComponent();
        componentDef.setName(composite.getName().getLocalPart());
        componentDef.setImplementation(composite);

        // Adjust the composite graph and wire the references with SCA bindings
        processSCABinding(composite);

        // Build runtime artifacts using the builders
        builder.build(componentDef, deploymentContext);

        // Register all components with the component manager
        Collection<Component> components = deploymentContext.getComponents().values();
        for (Component toRegister : components) {
            try {
                componentManager.register(toRegister);
            } catch (RegistrationException e) {
                throw new BuilderInstantiationException("Error registering component", e);
            }
        }

        // Connect components, services and references
        List<SCAObject> scaObjects = componentManager.getSCAObjects();
        for (int i = 0; i < scaObjects.size(); i++) {
            SCAObject scaObject = scaObjects.get(i);
            Object model = componentManager.getModelObject(Object.class, scaObject);
            if (model instanceof org.apache.tuscany.assembly.Component) {
                connect((Component)scaObject, (org.apache.tuscany.assembly.Component)model);
            } else if (model instanceof CompositeService) {
                try {
                    connect((Service)scaObject, (CompositeService)model);
                } catch (IncompatibleInterfaceContractException e) {
                    throw new IllegalStateException(e);
                }
            }
        }

        return components;
    }

    private void processSCABinding(Composite composite) {

        // Resolve all wires
        for (org.apache.tuscany.assembly.Component component : composite.getComponents()) {

            // Process composite components
            if (component.getImplementation() instanceof Composite) {
                for (ComponentReference componentReference : component.getReferences()) {

                    // Process component references with a default binding
                    if (componentReference.getBinding(SCABinding.class) != null) {

                        // Wire the promoted references inside the nested
                        // composite
                        CompositeReference compositeReference = (CompositeReference)componentReference.getReference();
                        if (compositeReference != null) {
                            for (ComponentReference promotedReference : compositeReference.getPromotedReferences()) {

                                // Add all the actual (promoted) targets to the
                                // promoted reference
                                for (ComponentService componentService : componentReference.getTargets()) {
                                    org.apache.tuscany.assembly.Service service = componentService.getService();
                                    if (service instanceof CompositeService) {
                                        CompositeService compositeService = (CompositeService)service;
                                        ComponentService promotedService = compositeService.getPromotedService();
                                        if (promotedService != null) {
                                            promotedReference.getTargets().add(promotedService);
                                        }
                                    } else {
                                        promotedReference.getTargets().add(componentService);
                                    }
                                }
                                promotedReference.promotedAs().clear();
                            }
                        }
                    }
                }
            }
        }
    }

    public void connect(Component source, org.apache.tuscany.assembly.Component definition) throws WiringException {

        if (definition.getImplementation() instanceof Composite) {
            // FIXME: Should we connect recusively?
            return;
        }

        for (ComponentReference componentReference : definition.getReferences()) {
            List<Wire> wires = new ArrayList<Wire>();
            String refName = componentReference.getName();
            List<CompositeReference> promoted = componentReference.promotedAs();
            if (!promoted.isEmpty()) {
                // TODO: Assume a component reference can only be promoted by at
                // most one composite reference
                CompositeReference compositeReference = promoted.get(0);
                Reference target = componentManager.getSCAObject(Reference.class, compositeReference);
                // FIXME: Assume we only have one binding
                ReferenceBinding binding = target.getReferenceBindings().get(0);
                URI targetUri = binding.getUri();
                InterfaceContract contract = compositeReference.getInterfaceContract();
                QName type = binding.getBindingType();
                URI sourceUri = URI.create(source.getUri() + "#" + refName);
                Wire wire;
                try {
                    wire = createWire(sourceUri, targetUri, componentReference.getInterfaceContract(), contract, type);
                } catch (IncompatibleInterfaceContractException e1) {
                    throw new IllegalStateException(e1);
                }
                binding.setWire(wire);
                try {
                    attachInvokers(targetUri.getFragment(), wire, source, binding);
                } catch (TargetInvokerCreationException e) {
                    throw new WireCreationException("Error creating invoker", sourceUri, targetUri, e);
                }
                wires.add(wire);
            } else {
                List<ComponentService> services = componentReference.getTargets();
                for (ComponentService service : services) {
                    org.apache.tuscany.assembly.Component targetCompoent = service.getBinding(SCABinding.class)
                        .getComponent();
                    Component target = componentManager.getSCAObject(Component.class, targetCompoent);
                    URI targetUri = URI.create(target.getUri() + "#" + service.getName());
                    if (target == null && (componentReference.getMultiplicity() == Multiplicity.ZERO_ONE || componentReference
                            .getMultiplicity() == Multiplicity.ZERO_N)) {
                        // a non-required reference, just skip
                        continue;
                    }
                    if (target == null) {
                        throw new ComponentNotFoundException("Target not found", targetUri);
                    }
                    URI sourceURI = URI.create(source.getUri() + "#" + refName);
                    Wire wire;
                    try {
                        wire = createWire(sourceURI, targetUri, componentReference.getInterfaceContract(), service
                            .getService().getInterfaceContract(), Wire.LOCAL_BINDING);
                    } catch (IncompatibleInterfaceContractException e1) {
                        throw new IncompatibleInterfacesException(sourceURI, targetUri, e1);
                    }
                    try {
                        attachInvokers(refName, wire, source, target);
                    } catch (TargetInvokerCreationException e) {
                        throw new WireCreationException("Error creating invoker", sourceURI, targetUri, e);
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

    private void connect(Service service, CompositeService definition) throws WiringException,
        IncompatibleInterfaceContractException {
        SCABinding scaBinding = definition.getPromotedService().getBinding(SCABinding.class);
        org.apache.tuscany.assembly.Component targetComponent = scaBinding.getComponent();

        Component target = componentManager.getSCAObject(Component.class, targetComponent);
        if (target == null) {
            throw new ComponentNotFoundException("Target not found", URI.create(targetComponent.getName()));
        }
        URI sourceURI = service.getUri();
        URI targetURI = URI.create(target.getUri() + "#" + definition.getPromotedService().getName());
        InterfaceContract sourceContract = definition.getInterfaceContract();
        InterfaceContract targetContract = definition.getPromotedService().getService().getInterfaceContract();
        if (sourceContract == null) {
            sourceContract = targetContract;
        }

        // TODO if no binding, do local
        for (ServiceBinding binding : service.getServiceBindings()) {
            Wire wire = createWire(sourceURI, targetURI, sourceContract, targetContract, binding.getBindingType());
            binding.setWire(wire);
            if (postProcessorRegistry != null) {
                postProcessorRegistry.process(wire);
            }
            try {
                attachInvokers(definition.getPromotedService().getName(), wire, binding, target);
            } catch (TargetInvokerCreationException e) {
                throw new WireCreationException("Error creating invoker", sourceURI, targetURI, e);
            }
        }
    }

    /**
     * Create a new wire connecting a source to a target.
     * 
     * @param sourceURI
     * @param targetUri
     * @param sourceContract
     * @param targetContract
     * @param bindingType
     * @return
     * @throws IncompatibleInterfaceContractException
     */
    private Wire createWire(URI sourceURI,
                              URI targetUri,
                              InterfaceContract sourceContract,
                              InterfaceContract targetContract,
                              QName bindingType) throws IncompatibleInterfaceContractException {
        Wire wire = new WireImpl(bindingType);
        wire.setSourceContract(sourceContract);
        wire.setTargetContract(targetContract);
        wire.setSourceUri(sourceURI);
        wire.setTargetUri(targetUri);

        mapper.checkCompatibility(sourceContract, targetContract, false, false);
        for (Operation operation : sourceContract.getInterface().getOperations()) {
            Operation targetOperation = mapper.map(targetContract.getInterface(), operation);
            InvocationChain chain = new InvocationChainImpl(operation, targetOperation);
               /* lresende */
               if (operation.isNonBlocking()) { 
                   chain.addInterceptor(new NonBlockingInterceptor(workScheduler, workContext)); }
               /* lresende */
            chain.addInterceptor(new InvokerInterceptor());
            wire.addInvocationChain(chain);

        }
        if (sourceContract.getCallbackInterface() != null) {
            for (Operation operation : sourceContract.getCallbackInterface().getOperations()) {
                Operation targetOperation = mapper.map(targetContract.getCallbackInterface(), operation);
                InvocationChain chain = new InvocationChainImpl(operation, targetOperation);
                   /* lresende */
                   if (operation.isNonBlocking()) { 
                       chain.addInterceptor(new NonBlockingInterceptor(workScheduler, workContext)); }
                   /* lresende */
                chain.addInterceptor(new InvokerInterceptor());
                wire.addCallbackInvocationChain(chain);
            }
        }
        return wire;
    }

    private void attachInvokers(String name, Wire wire, Invocable source, Invocable target)
        throws TargetInvokerCreationException {
        for (InvocationChain chain : wire.getInvocationChains()) {
            chain.setTargetInvoker(target.createTargetInvoker(name, chain.getTargetOperation(), false));
        }
        for (InvocationChain chain : wire.getCallbackInvocationChains()) {
            chain.setTargetInvoker(source.createTargetInvoker(null, chain.getTargetOperation(), true));
        }
    }

    private void optimize(Component source, Component target, Wire wire) {
        boolean optimizableScopes = isOptimizable(source.getScope(), target.getScope());
        if (optimizableScopes && target.isOptimizable() && WireUtils.isOptimizable(wire)) {
            wire.setOptimizable(true);
            wire.setTarget((AtomicComponent)target);
        } else {
            wire.setOptimizable(false);
        }
    }

    private boolean isOptimizable(Scope pReferrer, Scope pReferee) {
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

    public void setWirePostProcessorRegistry(WirePostProcessorRegistry postProcessorRegistry) {
        this.postProcessorRegistry = postProcessorRegistry;
    }
}
