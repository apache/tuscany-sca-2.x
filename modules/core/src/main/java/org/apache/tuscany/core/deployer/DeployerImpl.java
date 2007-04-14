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
import org.apache.tuscany.assembly.Implementation;
import org.apache.tuscany.assembly.Multiplicity;
import org.apache.tuscany.assembly.SCABinding;
import org.apache.tuscany.assembly.impl.DefaultAssemblyFactory;
import org.apache.tuscany.core.builder.ComponentNotFoundException;
import org.apache.tuscany.core.builder.IncompatibleInterfacesException;
import org.apache.tuscany.core.builder.WireCreationException;
import org.apache.tuscany.core.wire.InvocationChainImpl;
import org.apache.tuscany.core.wire.InvokerInterceptor;
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
import org.apache.tuscany.spi.deployer.Deployer;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.resolver.ResolutionException;
import org.apache.tuscany.spi.util.UriHelper;
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

    public DeployerImpl(XMLInputFactory xmlFactory, Builder builder, ComponentManager componentManager) {
        this.xmlFactory = xmlFactory;
        this.builder = builder;
        this.componentManager = componentManager;
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
            } else if (model instanceof CompositeReference) {
                try {
                    connect((Reference)scaObject, (CompositeReference)model);
                } catch (IncompatibleInterfaceContractException e) {
                    throw new IllegalStateException(e);
                }
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
        for (org.apache.tuscany.assembly.Component component: composite.getComponents()) {
            
            // Process composite components
            if (component.getImplementation() instanceof Composite) {
                for (ComponentReference componentReference: component.getReferences()) {
                    
                    // Process component references with a default binding
                    if (componentReference.getBinding(SCABinding.class) != null) {
                        
                        // Wire the promoted references inside the nested composite
                        CompositeReference compositeReference = (CompositeReference)componentReference.getReference();
                        if (compositeReference != null) {
                            for (ComponentReference promotedReference: compositeReference.getPromotedReferences()) {
                                
                                // Add all the actual (promoted) targets to the promoted reference 
                                for (ComponentService componentService: componentReference.getTargets()) {
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

    public static org.apache.tuscany.assembly.Reference getReference(Implementation type, String name) {
        for (org.apache.tuscany.assembly.Reference ref : type.getReferences()) {
            if (ref.getName().equals(name)) {
                return ref;
            }
        }
        return null;
    }

    public void connect(org.apache.tuscany.assembly.Component definition) throws WiringException {
        Component source = componentManager.getSCAObject(Component.class, definition);
        if (source == null) {
            throw new ComponentNotFoundException("Source not found", URI.create(definition.getName()));
        }

        for (ComponentReference ref : definition.getReferences()) {
            List<Wire> wires = new ArrayList<Wire>();
            String refName = ref.getName();
            org.apache.tuscany.assembly.Reference refDefinition = getReference(definition.getImplementation(), refName);
            assert refDefinition != null;
            List<ComponentService> services = ref.getTargets();
            for (ComponentService service : services) {
                org.apache.tuscany.assembly.Component targetCompoent =
                    service.getBinding(SCABinding.class).getComponent();
                Component target = componentManager.getSCAObject(Component.class, targetCompoent);
                URI targetUri = URI.create(target.getUri() + "#" + service.getName());
                if (target == null && (refDefinition.getMultiplicity() == Multiplicity.ZERO_ONE || refDefinition
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
                    wire =
                        createWire(sourceURI, targetUri, refDefinition.getInterfaceContract(), service.getService()
                            .getInterfaceContract(), Wire.LOCAL_BINDING);
                } catch (IncompatibleInterfaceContractException e1) {
                    throw new IncompatibleInterfacesException(sourceURI, targetUri, e1);
                }

                // If this component is implemented by a composite then
                // we need to attach the wires to the component in the
                // implementing composite that defines the references that
                // have been promoted to here. The composite component is not
                // itself wired as it plays no part in the runtime invocation
                // chain
                // We grab the nested component here, whose refefrences have
                // been promoted
                // and use it later on
                Component nestedComponentSource = null;

                if (definition.getImplementation() instanceof Composite) {
                    // Need to get the component from the composite. This is
                    // slightly tricky
                    // as we need to:
                    // Cast the reference to a composite reference
                    // Get the promoted references
                    List<ComponentReference> promotedReference =
                        ((CompositeReference)refDefinition).getPromotedReferences();
                    // For each promoted reference get the SCA binding
                    for (ComponentReference componentReference : promotedReference) {
                        SCABinding scaBinding = componentReference.getBinding(SCABinding.class);
                        // from the binding get the component
                        org.apache.tuscany.assembly.Component nestedComponent = scaBinding.getComponent();
                        // map the model component to the runtime component
                        nestedComponentSource = componentManager.getSCAObject(Component.class, nestedComponent);
                    }
                }

                // The same is true of the target for when callbacks are wired
                // or when the
                // target invoker is created. If the target is a composite
                // component go get
                // the component from the implementing composite whose service
                // we are targetting
                Component nestedComponentTarget = null;

                if (targetCompoent.getImplementation() instanceof Composite) {
                    // Need to get the component from the composite. Here we go:
                    // Get the implementation from the target component (this
                    // should be a composite)
                    List<org.apache.tuscany.assembly.Service> nestedServices = targetCompoent.getImplementation().getServices();
                    // Get the service from the implementation that matches the
                    // service we are processing
                    for (org.apache.tuscany.assembly.Service nestedService : nestedServices) {
                        if (nestedService.getName().equals(service.getName())) {
                            // Get the real service that this is promoted from
                            ComponentService promotedService = ((CompositeService)nestedService).getPromotedService();
                            // Get the SCA binding from the promoted service
                            SCABinding scaBinding = promotedService.getBinding(SCABinding.class);
                            // Get the component from the binding
                            org.apache.tuscany.assembly.Component nestedComponent = scaBinding.getComponent();
                            // Map this model component to the runtime component
                            nestedComponentTarget = componentManager.getSCAObject(Component.class, nestedComponent);
                        }
                    }
                }

                // add the required invokers to the wire created prviously. of
                // particluar imporantance is the target invoker that provides
                // the
                // bridge to the target service. We have to check
                try {
                    if (nestedComponentTarget == null) {
                        attachInvokers(refName, wire, source, target);
                    } else {
                        attachInvokers(refName, wire, source, nestedComponentTarget);
                    }

                } catch (TargetInvokerCreationException e) {
                    throw new WireCreationException("Error creating invoker", sourceURI, targetUri, e);
                }

                if (postProcessorRegistry != null) {
                    postProcessorRegistry.process(wire);
                }

                // TODO: Which components do we need to use for the optimize
                optimize(source, target, wire);

                // In the case of a composite component add the wire to the
                // component inside
                // the reference loop because there may be many references
                // promoted to the composite component from different components
                if (definition.getImplementation() instanceof Composite) {
                    nestedComponentSource.attachWire(wire);
                } else {
                    // add the wire to the collcetion that will be added
                    // en-masse to the
                    // source component later on
                    wires.add(wire);
                }

                // if there is a callback associated with the invocation chain
                // then this needs to be connected to the target. It has to be
                // done
                // inside the service/target loop because there may be multiple
                // targets for the reference
                if (!wire.getCallbackInvocationChains().isEmpty()) {
                    // as previously the target may be implemented by a
                    // composite
                    // so we need to find the target component within the
                    // composite
                    // to set the callback on if this is the case
                    if (targetCompoent.getImplementation() instanceof Composite) {
                        nestedComponentTarget.attachCallbackWire(wire);
                    } else {
                        target.attachCallbackWire(wire);
                    }
                }
            }

            // If this component is implemented by a composite then
            // the wires will already have been added to the appropriate
            // components in the code above
            if (definition.getImplementation() instanceof Composite) {
                // not sure we need to do anything else here
            } else {
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
                URI targetUri = binding.getTargetUri();
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

    protected void connect(Service service, CompositeService definition) throws WiringException,
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

    protected void connect(org.apache.tuscany.spi.component.Reference reference, CompositeReference definition)
        throws WiringException, IncompatibleInterfaceContractException {
        URI sourceUri = reference.getUri();
        for (ReferenceBinding binding : reference.getReferenceBindings()) {
            // create wire
            if (Wire.LOCAL_BINDING.equals(binding.getBindingType())) {
                URI targetUri = binding.getTargetUri();
                InterfaceContract contract = binding.getBindingInterfaceContract();
                if (contract == null) {
                    contract = definition.getInterfaceContract();
                }
                QName type = binding.getBindingType();
                Wire wire = createWire(targetUri, sourceUri, contract, definition.getInterfaceContract(), type);
                binding.setWire(wire);
                // wire local bindings to their targets
                Component target = componentManager.getComponent(UriHelper.getDefragmentedName(targetUri));
                if (target == null) {
                    throw new ComponentNotFoundException("Target not found", sourceUri);
                }
                try {
                    attachInvokers(sourceUri.getFragment(), wire, target, binding);
                } catch (TargetInvokerCreationException e) {
                    throw new WireCreationException("Error creating invoker", sourceUri, targetUri, e);
                }
            } else {
                InterfaceContract bindingContract = binding.getBindingInterfaceContract();
                if (bindingContract == null) {
                    bindingContract = definition.getInterfaceContract();
                }
                Wire wire = createWire(null, sourceUri, bindingContract, definition.getInterfaceContract(), binding
                    .getBindingType());
                if (postProcessorRegistry != null) {
                    postProcessorRegistry.process(wire);
                }
                binding.setWire(wire);
            }
        }
    }

    /**
     * Create a new wire connecting a source to a target.
     * @param sourceURI
     * @param targetUri
     * @param sourceContract
     * @param targetContract
     * @param bindingType
     * @return
     * @throws IncompatibleInterfaceContractException
     */
    protected Wire createWire(URI sourceURI,
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
            /*
             * if (operation.isNonBlocking()) { chain.addInterceptor(new
             * NonBlockingInterceptor(scheduler, workContext)); }
             */
            chain.addInterceptor(new InvokerInterceptor());
            wire.addInvocationChain(chain);

        }
        if (sourceContract.getCallbackInterface() != null) {
            for (Operation operation : sourceContract.getCallbackInterface().getOperations()) {
                Operation targetOperation = mapper.map(targetContract.getCallbackInterface(), operation);
                InvocationChain chain = new InvocationChainImpl(operation, targetOperation);
                /*
                 * if (operation.isNonBlocking()) { chain.addInterceptor(new
                 * NonBlockingInterceptor(scheduler, workContext)); }
                 */
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

    public void setWirePostProcessorRegistry(WirePostProcessorRegistry postProcessorRegistry) {
        this.postProcessorRegistry = postProcessorRegistry;
    }
}
