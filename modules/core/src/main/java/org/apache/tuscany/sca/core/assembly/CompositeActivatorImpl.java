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

package org.apache.tuscany.sca.core.assembly;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.CompositeService;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.OptimizableBinding;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.context.RequestContextFactory;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.context.ComponentContextHelper;
import org.apache.tuscany.sca.core.context.ComponentContextImpl;
import org.apache.tuscany.sca.core.conversation.ConversationManager;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.core.scope.ConversationalScopeContainer;
import org.apache.tuscany.sca.core.scope.Scope;
import org.apache.tuscany.sca.core.scope.ScopeContainer;
import org.apache.tuscany.sca.core.scope.ScopeRegistry;
import org.apache.tuscany.sca.core.scope.ScopedRuntimeComponent;
import org.apache.tuscany.sca.endpointresolver.EndpointResolver;
import org.apache.tuscany.sca.endpointresolver.EndpointResolverFactory;
import org.apache.tuscany.sca.endpointresolver.EndpointResolverFactoryExtensionPoint;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.provider.BindingProviderFactory;
import org.apache.tuscany.sca.provider.ImplementationProvider;
import org.apache.tuscany.sca.provider.ImplementationProviderFactory;
import org.apache.tuscany.sca.provider.PolicyProvider;
import org.apache.tuscany.sca.provider.PolicyProviderFactory;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.EndpointReference;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentContext;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.apache.tuscany.sca.runtime.RuntimeWireProcessor;
import org.apache.tuscany.sca.work.WorkScheduler;

/**
 * @version $Rev$ $Date$
 */
public class CompositeActivatorImpl implements CompositeActivator {
    private static final Logger logger = Logger.getLogger(CompositeActivatorImpl.class.getName());

    private final AssemblyFactory assemblyFactory;
    private final MessageFactory messageFactory;
    private final InterfaceContractMapper interfaceContractMapper;
    private final ScopeRegistry scopeRegistry;
    private final WorkScheduler workScheduler;
    private final RuntimeWireProcessor wireProcessor;
    private final ProviderFactoryExtensionPoint providerFactories;
    private final EndpointResolverFactoryExtensionPoint endpointResolverFactories;

    private final RequestContextFactory requestContextFactory;
    private final ProxyFactory proxyFactory;
    private final JavaInterfaceFactory javaInterfaceFactory;
    private final ConversationManager conversationManager;

    private final ComponentContextHelper componentContextHelper;

    private Composite domainComposite;

    /**
     * @param assemblyFactory
     * @param interfaceContractMapper
     * @param workScheduler
     * @param conversationManager TODO
     * @param workContext
     * @param wirePostProcessorRegistry
     */
    public CompositeActivatorImpl(AssemblyFactory assemblyFactory,
                                  MessageFactory messageFactory,
                                  JavaInterfaceFactory javaInterfaceFactory,
                                  SCABindingFactory scaBindingFactory,
                                  InterfaceContractMapper interfaceContractMapper,
                                  ScopeRegistry scopeRegistry,
                                  WorkScheduler workScheduler,
                                  RuntimeWireProcessor wireProcessor,
                                  RequestContextFactory requestContextFactory,
                                  ProxyFactory proxyFactory,
                                  ProviderFactoryExtensionPoint providerFactories,
                                  EndpointResolverFactoryExtensionPoint endpointResolverFactories,
                                  StAXArtifactProcessorExtensionPoint processors,
                                  ConversationManager conversationManager) {
        this.assemblyFactory = assemblyFactory;
        this.messageFactory = messageFactory;
        this.interfaceContractMapper = interfaceContractMapper;
        this.scopeRegistry = scopeRegistry;
        this.workScheduler = workScheduler;
        this.wireProcessor = wireProcessor;
        this.providerFactories = providerFactories;
        this.endpointResolverFactories = endpointResolverFactories;
        this.javaInterfaceFactory = javaInterfaceFactory;
        this.requestContextFactory = requestContextFactory;
        this.proxyFactory = proxyFactory;
        this.conversationManager = conversationManager;
        this.componentContextHelper = new ComponentContextHelper(assemblyFactory, javaInterfaceFactory, processors);
    }

    /**
     * @see org.apache.tuscany.sca.core.assembly.CompositeActivator#activate(org.apache.tuscany.sca.runtime.RuntimeComponent, org.apache.tuscany.sca.runtime.RuntimeComponentReference)
     */
    public void activate(RuntimeComponent component, RuntimeComponentReference ref) {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Activating component reference: " + component.getURI() + "#" + ref.getName());
        }
        resolveTargets(ref);
        for (Binding binding : ref.getBindings()) {
            addReferenceBindingProvider(component, ref, binding);
        }
        
        for (Endpoint endpoint : ref.getEndpoints()){
            // TODO - source component should be set in the builder but the 
            //        way the builder is written it's difficult to get at it
            endpoint.setSourceComponent(component);
            
            addEndpointResolver(component, ref, endpoint);
        }
    }

    public void start(RuntimeComponent component, RuntimeComponentReference ref) {
        synchronized (ref) {
            resolveTargets(ref);
            for (Binding binding : ref.getBindings()) {
                ReferenceBindingProvider provider = ref.getBindingProvider(binding);
                if (provider == null) {
                    provider = addReferenceBindingProvider(component, ref, binding);
                }
                if (provider != null) {
                    provider.start();
                }
                addReferenceWire(component, ref, binding);
            }
            
            // targets now have an endpoint representation. We can use this to 
            // look for unresolved endpoints using dummy wires for late resolution
            for (Endpoint endpoint : ref.getEndpoints()){
                addReferenceEndpointWire(component, ref, endpoint);
            }
        }
    }

    public void stop(Component component, ComponentReference reference) {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Stopping component reference: " + component.getURI() + "#" + reference.getName());
        }
        RuntimeComponentReference runtimeRef = ((RuntimeComponentReference)reference);
        for (Binding binding : reference.getBindings()) {
            ReferenceBindingProvider bindingProvider = runtimeRef.getBindingProvider(binding);
            if (bindingProvider != null) {
                bindingProvider.stop();
            }
        }
    }

    public void deactivate(RuntimeComponent component, RuntimeComponentReference ref) {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Deactivating component reference: " + component.getURI() + "#" + ref.getName());
        }
        removeReferenceWires(ref);
        for (Binding binding : ref.getBindings()) {
            removeReferenceBindingProvider(component, ref, binding);
        }

    }

    /**
     * @param component
     * @param reference
     * @param binding
     */
    private EndpointResolver addEndpointResolver(RuntimeComponent component,
                                                 RuntimeComponentReference reference,
                                                 Endpoint endpoint){
        
        // only create endpoint resolvers for unresolved endpoints currently
        // this will also prevent a wire from being created later
        if (!endpoint.isUnresolved()){
            return null;
        }
        
        // This souldn't happen as the endpoint resolver extension point is in core-spi but 
        // just in case returning null here will mean that no wire is created and calling 
        // the reference will fail with NPE
        if (endpointResolverFactories == null){
            return null;
        }
        
        EndpointResolverFactory<Endpoint> resolverFactory =
            (EndpointResolverFactory<Endpoint>)endpointResolverFactories.getEndpointResolverFactory(endpoint.getClass());
        
        if (resolverFactory != null) {
            @SuppressWarnings("unchecked")
            EndpointResolver endpointResolver =
                resolverFactory.createEndpointResolver(endpoint, null);
            if (endpointResolver != null) {
                ((RuntimeComponentReference)reference).setEndpointResolver(endpoint, endpointResolver);
            }
            
            return endpointResolver;
        } else {
            // TODO - for the time being allow the lack of an endpoint provider to be the 
            //        switch to turn off endpoint processing
            return null;
            //throw new IllegalStateException("Endpoint provider factory not found for class: " + endpoint.getClass().getName());
        }
    }
    
    public void addReferenceBindingProviderForEndpoint(Endpoint endpoint){
        addReferenceBindingProvider((RuntimeComponent)endpoint.getSourceComponent(),
                                    (RuntimeComponentReference)endpoint.getSourceComponentReference(),
                                    endpoint.getSourceBinding());
    }

    /**
     * @param component
     * @param reference
     * @param binding
     */
    private ReferenceBindingProvider addReferenceBindingProvider(RuntimeComponent component,
                                                                 RuntimeComponentReference reference,
                                                                 Binding binding) {
        BindingProviderFactory providerFactory =
            (BindingProviderFactory)providerFactories.getProviderFactory(binding.getClass());
        if (providerFactory != null) {
            @SuppressWarnings("unchecked")
            ReferenceBindingProvider bindingProvider =
                providerFactory.createReferenceBindingProvider((RuntimeComponent)component,
                                                               (RuntimeComponentReference)reference,
                                                               binding);
            if (bindingProvider != null) {
                ((RuntimeComponentReference)reference).setBindingProvider(binding, bindingProvider);
            }
            for (PolicyProviderFactory f : providerFactories.getPolicyProviderFactories()) {
                PolicyProvider policyProvider = f.createReferencePolicyProvider(component, reference, binding);
                if (policyProvider != null) {
                    reference.addPolicyProvider(binding, policyProvider);
                }
            }

            return bindingProvider;
        } else {
            throw new IllegalStateException("Provider factory not found for class: " + binding.getClass().getName());
        }
    }

    /**
     * @param reference
     */
    private void resolveTargets(RuntimeComponentReference reference) {
        // The code that used to be here to resolved unresolved targets is now 
        // at the bottom of BaseWireBuilder.connectComponentReferences()
    }
    
    /**
     * Create the runtime wires for a reference endpoint. Currently this method
     * only deals with the late binding case and creates a dummy wire that 
     * will use the Endpoint to resolve the target at the point when the 
     * wire chains are created.
     * 
     * @param component
     * @param reference
     * @param binding
     */
    private void addReferenceEndpointWire(Component component, ComponentReference reference, Endpoint endpoint) {
        // only deal with unresolved endpoints as, to prevent breaking changes, targets that are resolved
        // at build time are still represented as bindings in the binding list
        if (((RuntimeComponentReference)reference).getEndpointResolver(endpoint) == null){ 
            // no endpoint provider has previously been created so don't create the 
            // wire
            return;
        }

        // TODO: TUSCANY-2580: avoid NPE if the InterfaceCOntract is null
        Reference ctref = endpoint.getSourceComponentReference().getReference();
        if (ctref != null && ctref.getInterfaceContract() == null) {
            ctref.setInterfaceContract(reference.getInterfaceContract());
        }
        
        RuntimeWire wire = new EndpointWireImpl(endpoint, this);
        
        RuntimeComponentReference runtimeRef = (RuntimeComponentReference)reference;
        runtimeRef.getRuntimeWires().add(wire);
    }
    

    /**
     * Create the runtime wires for a reference binding
     * 
     * @param component
     * @param reference
     * @param binding
     */
    private void addReferenceWire(Component component, ComponentReference reference, Binding binding) {
        if (!(reference instanceof RuntimeComponentReference)) {
            return;
        }
        
        // create wire if binding has an endpoint
        Component targetComponent = null;
        ComponentService targetComponentService = null;
        Binding targetBinding = null;
    
        if (binding instanceof OptimizableBinding) {
            OptimizableBinding endpoint = (OptimizableBinding)binding;
            targetComponent = endpoint.getTargetComponent();
            targetComponentService = endpoint.getTargetComponentService();
            targetBinding = endpoint.getTargetBinding();
            // FIXME: TUSCANY-2136, For unresolved binding, don't add wire. Is it the right solution?
            if (!reference.isCallback() && binding.getURI() == null && targetComponentService == null) {
                return;
            }
        }

        // create a forward wire, either static or dynamic
        addReferenceWire(component, reference, binding, targetComponent, targetComponentService, targetBinding);

        /*
        // if static forward wire (not from self-reference), try to create a static callback wire 
        if (targetComponentService != null && !reference.getName().startsWith("$self$.")) {
            ComponentReference callbackReference = targetComponentService.getCallbackReference();
            if (callbackReference != null) {
                Binding callbackBinding = null;
                Binding callbackServiceBinding = null;
                // select a service callback binding that can be wired back to this component
                for (Binding refBinding : callbackReference.getBindings()) {
                    // first look for a callback binding whose name matches the target binding name
                    if (refBinding.getName().equals(targetBinding.getName())) {
                        callbackBinding = refBinding;
                        break;
                    }
                }
                // see if there is a matching reference callback binding 
                if (callbackBinding != null) {
                    callbackServiceBinding = reference.getCallbackService().getBinding(callbackBinding.getClass());
                }
                // if there isn't an end-to-end match, try again based on target binding type
                if (callbackBinding == null || callbackServiceBinding == null) {
                    callbackBinding = callbackReference.getBinding(targetBinding.getClass());
                    if (callbackBinding != null) {
                        callbackServiceBinding = reference.getCallbackService().getBinding(callbackBinding.getClass());
                    }
                }
                if (callbackBinding != null && callbackServiceBinding != null) {
                    // end-to-end match, so create a static callback wire as well as the static forward wire
        
                    addReferenceWire(targetComponent, callbackReference, callbackBinding, component, reference
                        .getCallbackService(), callbackServiceBinding);
                } else {
                    // no end-to-end match, so do not create a static callback wire
                }
            }
        }
        */
    }

    public void addReferenceWireForEndpoint(Endpoint endpoint){
        addReferenceWire(endpoint.getSourceComponent(),
                         endpoint.getSourceComponentReference(),
                         endpoint.getSourceBinding(),
                         endpoint.getTargetComponent(),
                         endpoint.getTargetComponentService(),
                         endpoint.getTargetBinding());
    }
    /**
     * Create a reference wire for a forward call or a callback
     * @param reference
     * @param service
     * @param serviceBinding
     * @param component
     * @param referenceBinding
     */
    private RuntimeWire addReferenceWire(Component refComponent,
                                         ComponentReference reference,
                                         Binding refBinding,
                                         Component serviceComponent,
                                         ComponentService service,
                                         Binding serviceBinding) {
        RuntimeComponentReference runtimeRef = (RuntimeComponentReference)reference;
        InterfaceContract bindingContract = getInterfaceContract(reference, refBinding);

        // Use the interface contract of the reference on the component type
        Reference componentTypeRef = reference.getReference();

        InterfaceContract sourceContract;
        if (componentTypeRef == null || componentTypeRef.getInterfaceContract() == null) {
            sourceContract = reference.getInterfaceContract();
        } else {
            sourceContract = componentTypeRef.getInterfaceContract();
        }

        sourceContract = sourceContract.makeUnidirectional(false);

        EndpointReference wireSource =
            new EndpointReferenceImpl((RuntimeComponent)refComponent, reference, refBinding, sourceContract);
        ComponentService callbackService = reference.getCallbackService();
        if (callbackService != null) {
            // select a reference callback binding to pass with invocations on this wire
            Binding callbackBinding = null;
            for (Binding binding : callbackService.getBindings()) {
                // first look for a callback binding whose name matches the reference binding name
            	if (refBinding.getName().startsWith(binding.getName())) {
                    callbackBinding = binding;
                    break;
                }
            }
            // if no callback binding found, try again based on reference binding type
            if (callbackBinding == null) {
                callbackBinding = callbackService.getBinding(refBinding.getClass());
            }
            InterfaceContract callbackContract = callbackService.getInterfaceContract();
            EndpointReference callbackEndpoint =
                new EndpointReferenceImpl((RuntimeComponent)refComponent, callbackService, callbackBinding,
                                          callbackContract);
            wireSource.setCallbackEndpoint(callbackEndpoint);
        }

        EndpointReference wireTarget =
            new EndpointReferenceImpl((RuntimeComponent)serviceComponent, service, serviceBinding, bindingContract);
        
        // TUSCANY-2029 - We should use the URI of the serviceBinding because the target may be a Component in a
        // nested composite.
        if (serviceBinding != null) {
            wireTarget.setURI(serviceBinding.getURI());
        }

        RuntimeWire wire =
            new RuntimeWireImpl(wireSource, wireTarget, interfaceContractMapper, workScheduler, wireProcessor,
                                messageFactory, conversationManager);
        runtimeRef.getRuntimeWires().add(wire);

        return wire;
    }

    private void addImplementationProvider(RuntimeComponent component, Implementation implementation) {
        ImplementationProviderFactory providerFactory =
            (ImplementationProviderFactory)providerFactories.getProviderFactory(implementation.getClass());
        if (providerFactory != null) {
            @SuppressWarnings("unchecked")
            ImplementationProvider implementationProvider =
                providerFactory.createImplementationProvider(component, implementation);
            if (implementationProvider != null) {
                component.setImplementationProvider(implementationProvider);
            }
        } else {
            throw new IllegalStateException("Provider factory not found for class: " + implementation.getClass()
                .getName());
        }
        for (PolicyProviderFactory f : providerFactories.getPolicyProviderFactories()) {
            PolicyProvider policyProvider = f.createImplementationPolicyProvider(component, implementation);
            if (policyProvider != null) {
                component.addPolicyProvider(policyProvider);
            }
        }
        
    }

    private void removeImplementationProvider(RuntimeComponent component) {
        component.setImplementationProvider(null);
        component.getPolicyProviders().clear();
    }

    /**
     * @param component
     * @param service
     * @param binding
     */
    private ServiceBindingProvider addServiceBindingProvider(RuntimeComponent component,
                                                             RuntimeComponentService service,
                                                             Binding binding) {
        BindingProviderFactory providerFactory =
            (BindingProviderFactory)providerFactories.getProviderFactory(binding.getClass());
        if (providerFactory != null) {
            @SuppressWarnings("unchecked")
            ServiceBindingProvider bindingProvider =
                providerFactory.createServiceBindingProvider((RuntimeComponent)component,
                                                             (RuntimeComponentService)service,
                                                             binding);
            if (bindingProvider != null) {
                ((RuntimeComponentService)service).setBindingProvider(binding, bindingProvider);
            }
            for (PolicyProviderFactory f : providerFactories.getPolicyProviderFactories()) {
                PolicyProvider policyProvider = f.createServicePolicyProvider(component, service, binding);
                if (policyProvider != null) {
                    service.addPolicyProvider(binding, policyProvider);
                }
            }
            return bindingProvider;
        } else {
            throw new IllegalStateException("Provider factory not found for class: " + binding.getClass().getName());
        }
    }

    private void removeServiceBindingProvider(RuntimeComponent component,
                                              RuntimeComponentService service,
                                              Binding binding) {
        service.setBindingProvider(binding, null);
        for (Binding b : service.getBindings()) {
            List<PolicyProvider> pps = service.getPolicyProviders(b);
            if (pps != null) {
                pps.clear();
            }
        }
    }

    private void removeReferenceBindingProvider(RuntimeComponent component,
                                                RuntimeComponentReference reference,
                                                Binding binding) {
        reference.setBindingProvider(binding, null);
        for (Binding b : reference.getBindings()) {
            List<PolicyProvider> pps = reference.getPolicyProviders(b);
            if (pps != null) {
                pps.clear();
            }
        }
    }

    public void start(Composite composite) {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Starting composite: " + composite.getName());
        }
        for (Component component : composite.getComponents()) {
            start(component);
        }
    }

    public void stop(Composite composite) {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Stopping composite: " + composite.getName());
        }
        for (final Component component : composite.getComponents()) {
            stop(component);
        }
    }

    public void start(Component component) {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Starting component: " + component.getURI());
        }
        RuntimeComponent runtimeComponent = ((RuntimeComponent)component);
        if(runtimeComponent.isStarted()) {
        	return;
        }
        
        configureComponentContext(runtimeComponent);

        for (ComponentReference reference : component.getReferences()) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Starting component reference: " + component.getURI() + "#" + reference.getName());
            }
            RuntimeComponentReference runtimeRef = ((RuntimeComponentReference)reference);
            runtimeRef.setComponent(runtimeComponent);
            
            for (Endpoint endpoint : reference.getEndpoints()) {
                final EndpointResolver endpointResolver = runtimeRef.getEndpointResolver(endpoint);
                if (endpointResolver != null) {
                    // Allow endpoint resolvers to do any startup reference manipulation
                    AccessController.doPrivileged(new PrivilegedAction<Object>() {
                        public Object run() {
                            endpointResolver.start();
                            return null;
                          }
                    });                       
                }
            }
            
            for (Binding binding : reference.getBindings()) {
                final ReferenceBindingProvider bindingProvider = runtimeRef.getBindingProvider(binding);
                if (bindingProvider != null) {
                    // Allow bindings to add shutdown hooks. Requires RuntimePermission shutdownHooks in policy. 
                    AccessController.doPrivileged(new PrivilegedAction<Object>() {
                        public Object run() {
                            bindingProvider.start();
                            return null;
                          }
                    });                       
                }
            }
        }

        for (ComponentService service : component.getServices()) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Starting component service: " + component.getURI() + "#" + service.getName());
            }
            RuntimeComponentService runtimeService = (RuntimeComponentService)service;
            for (Binding binding : service.getBindings()) {
                final ServiceBindingProvider bindingProvider = runtimeService.getBindingProvider(binding);
                if (bindingProvider != null) {
                    // bindingProvider.start();
                    // Allow bindings to add shutdown hooks. Requires RuntimePermission shutdownHooks in policy. 
                    AccessController.doPrivileged(new PrivilegedAction<Object>() {
                        public Object run() {
                            bindingProvider.start();
                            return null;
                          }
                    });                       
                }
            }
        }

        Implementation implementation = component.getImplementation();
        if (implementation instanceof Composite) {
            start((Composite)implementation);
        } else {
            ImplementationProvider implementationProvider = runtimeComponent.getImplementationProvider();
            if (implementationProvider != null) {
                implementationProvider.start();
            }
        }

        if (component instanceof ScopedRuntimeComponent) {
            ScopedRuntimeComponent scopedRuntimeComponent = (ScopedRuntimeComponent)component;
            if (scopedRuntimeComponent.getScopeContainer() != null) {
                scopedRuntimeComponent.getScopeContainer().start();
            }
        }

        runtimeComponent.setStarted(true);
    }

    /**
     * @param runtimeComponent
     */
    public void configureComponentContext(RuntimeComponent runtimeComponent) {
        RuntimeComponentContext componentContext =
            new ComponentContextImpl(this, assemblyFactory, proxyFactory, interfaceContractMapper,
                                     requestContextFactory, javaInterfaceFactory, runtimeComponent);
        runtimeComponent.setComponentContext(componentContext);
    }

    /**
     * Stop a component
     */
    public void stop(Component component) {
    	if (!((RuntimeComponent)component).isStarted()) {
    		return;
        }
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Stopping component: " + component.getURI());
        }
        for (ComponentService service : component.getServices()) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Stopping component service: " + component.getURI() + "#" + service.getName());
            }
            for (Binding binding : service.getBindings()) {
                final ServiceBindingProvider bindingProvider = ((RuntimeComponentService)service).getBindingProvider(binding);
                if (bindingProvider != null) {
                    // Allow bindings to read properties. Requires PropertyPermission read in security policy. 
                    AccessController.doPrivileged(new PrivilegedAction<Object>() {
                        public Object run() {
                            bindingProvider.stop();
                            return null;
                          }
                    });                       
                }
            }
        }
        for (ComponentReference reference : component.getReferences()) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Starting component reference: " + component.getURI() + "#" + reference.getName());
            }
            RuntimeComponentReference runtimeRef = ((RuntimeComponentReference)reference);
            
            for (Binding binding : reference.getBindings()) {
                final ReferenceBindingProvider bindingProvider = runtimeRef.getBindingProvider(binding);
                if (bindingProvider != null) {
                    // Allow bindings to read properties. Requires PropertyPermission read in security policy. 
                    AccessController.doPrivileged(new PrivilegedAction<Object>() {
                        public Object run() {
                            bindingProvider.stop();
                            return null;
                          }
                    });                       
                }
            } 
            
            for (Endpoint endpoint : reference.getEndpoints()) {
                final EndpointResolver endpointResolver = runtimeRef.getEndpointResolver(endpoint);
                if (endpointResolver != null) {
                    // Allow endpoint resolvers to do any shutdown reference manipulation
                    AccessController.doPrivileged(new PrivilegedAction<Object>() {
                        public Object run() {
                            endpointResolver.stop();
                            return null;
                          }
                    });                       
                }
            }             
        }
        Implementation implementation = component.getImplementation();
        if (implementation instanceof Composite) {
            stop((Composite)implementation);
        } else {
            final ImplementationProvider implementationProvider = ((RuntimeComponent)component).getImplementationProvider();
            if (implementationProvider != null) {
                // Allow bindings to read properties. Requires PropertyPermission read in security policy. 
                AccessController.doPrivileged(new PrivilegedAction<Object>() {
                    public Object run() {
                        implementationProvider.stop();
                        return null;
                      }
                });                       
            }
        }

        if (component instanceof ScopedRuntimeComponent) {
            ScopedRuntimeComponent runtimeComponent = (ScopedRuntimeComponent)component;
            if (runtimeComponent.getScopeContainer() != null && 
            		runtimeComponent.getScopeContainer().getLifecycleState() != ScopeContainer.STOPPED) {
                runtimeComponent.getScopeContainer().stop();
            }
        }

        ((RuntimeComponent)component).setStarted(false);
    }

    /**
     * Get the effective interface contract for a reference binding
     * 
     * @param reference
     * @param binding
     * @return
     */
    private InterfaceContract getInterfaceContract(ComponentReference reference, Binding binding) {
        InterfaceContract interfaceContract = reference.getInterfaceContract();
        ReferenceBindingProvider provider = ((RuntimeComponentReference)reference).getBindingProvider(binding);
        if (provider != null) {
            InterfaceContract bindingContract = provider.getBindingInterfaceContract();
            if (bindingContract != null) {
                interfaceContract = bindingContract;
            }
        }
        return interfaceContract.makeUnidirectional(false);
    }

    /**
     * Remove the runtime wires for a reference binding
     * @param reference
     */
    private void removeReferenceWires(ComponentReference reference) {
        if (!(reference instanceof RuntimeComponentReference)) {
            return;
        }
        // [rfeng] Comment out the following statements to avoid the on-demand activation
        // RuntimeComponentReference runtimeRef = (RuntimeComponentReference)reference;
        // runtimeRef.getRuntimeWires().clear();
    }

    /**
     * Get the effective interface contract for the service binding
     * 
     * @param service
     * @param binding
     * @return
     */
    private InterfaceContract getInterfaceContract(ComponentService service, Binding binding) {
        InterfaceContract interfaceContract = service.getInterfaceContract();

        ServiceBindingProvider provider = ((RuntimeComponentService)service).getBindingProvider(binding);
        if (provider != null) {
            InterfaceContract bindingContract = provider.getBindingInterfaceContract();
            if (bindingContract != null) {
                interfaceContract = bindingContract;
            }
        }
        return interfaceContract.makeUnidirectional(false);
    }

    /**
     * Remove runtime wires for a service binding
     * 
     * @param component
     * @param service
     */
    private void removeServiceWires(ComponentService service) {
        if (!(service instanceof RuntimeComponentService)) {
            return;
        }
        RuntimeComponentService runtimeService = (RuntimeComponentService)service;
        runtimeService.getRuntimeWires().clear();
    }

    /**
     * Create a service wire for a forward call or a callback
     * @param service
     * @param serviceBinding
     * @param reference
     * @param component
     * @param referenceBinding
     */
    private RuntimeWire addServiceWire(Component serviceComponent, ComponentService service, Binding serviceBinding) {
        if (!(service instanceof RuntimeComponentService)) {
            return null;
        }
        RuntimeComponentService runtimeService = (RuntimeComponentService)service;

        // FIXME: [rfeng] We might need a better way to get the impl interface contract
        Service targetService = service.getService();
        if (targetService == null) {
            targetService = service;
        }
        InterfaceContract targetContract = targetService.getInterfaceContract().makeUnidirectional(false);

        InterfaceContract sourceContract = getInterfaceContract(service, serviceBinding);

        EndpointReference wireSource = new EndpointReferenceImpl(null, null, serviceBinding, sourceContract);

        EndpointReference wireTarget =
            new EndpointReferenceImpl((RuntimeComponent)serviceComponent, (RuntimeComponentService)service,
                                      serviceBinding, targetContract);

        RuntimeWire wire =
            new RuntimeWireImpl(wireSource, wireTarget, interfaceContractMapper, workScheduler, wireProcessor,
                                messageFactory, conversationManager);
        runtimeService.getRuntimeWires().add(wire);

        return wire;
    }

    public void activate(RuntimeComponent component, RuntimeComponentService service) {
        if (service.getService() == null) {
            if (logger.isLoggable(Level.WARNING)) {
                logger.warning("Skipping component service not defined in the component type: " + component.getURI()
                    + "#"
                    + service.getName());
            }
            return;
        }
        if (service.getService() instanceof CompositeService) {
            return;
        }
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Activating component service: " + component.getURI() + "#" + service.getName());
        }

        for (Binding binding : service.getBindings()) {
            addServiceBindingProvider(component, service, binding);
            addServiceWire(component, service, binding);
        }
    }

    public void deactivate(RuntimeComponent component, RuntimeComponentService service) {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Deactivating component service: " + component.getURI() + "#" + service.getName());
        }
        removeServiceWires(service);
        for (Binding binding : service.getBindings()) {
            removeServiceBindingProvider(component, service, binding);
        }
    }

    private void addScopeContainer(Component component) {
        if (!(component instanceof ScopedRuntimeComponent)) {
            return;
        }
        ScopedRuntimeComponent runtimeComponent = (ScopedRuntimeComponent)component;
        ScopeContainer scopeContainer = scopeRegistry.getScopeContainer(runtimeComponent);
        if (scopeContainer != null && scopeContainer.getScope() == Scope.CONVERSATION) {
            conversationManager.addListener((ConversationalScopeContainer)scopeContainer);
        }
        runtimeComponent.setScopeContainer(scopeContainer);
    }

    private void removeScopeContainer(Component component) {
        if (!(component instanceof ScopedRuntimeComponent)) {
            return;
        }
        ScopedRuntimeComponent runtimeComponent = (ScopedRuntimeComponent)component;
        ScopeContainer scopeContainer = runtimeComponent.getScopeContainer();
        if(scopeContainer != null && scopeContainer.getScope() == Scope.CONVERSATION) {
            conversationManager.removeListener((ConversationalScopeContainer) scopeContainer);
        }        
        runtimeComponent.setScopeContainer(null);
    }
    
    public void activateComponent(Component component)
            throws ActivationException {
        try {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Activating component: " + component.getURI());
            }

            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {
                activate((Composite) implementation);
            } else if (implementation != null) {
                addImplementationProvider((RuntimeComponent) component,
                        implementation);
                addScopeContainer(component);
            }

            for (ComponentService service : component.getServices()) {
                activate((RuntimeComponent) component,
                        (RuntimeComponentService) service);
            }

            for (ComponentReference reference : component.getReferences()) {
                activate((RuntimeComponent) component,
                        (RuntimeComponentReference) reference);
            }
        } catch (Exception e) {
            throw new ActivationException(e);
        }
    }

    public void deactivateComponent(Component component)
            throws ActivationException {
        try {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Deactivating component: " + component.getURI());
            }
            for (ComponentService service : component.getServices()) {
                deactivate((RuntimeComponent) component,
                        (RuntimeComponentService) service);
            }

            for (ComponentReference reference : component.getReferences()) {
                deactivate((RuntimeComponent) component,
                        (RuntimeComponentReference) reference);
            }

            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {
                deactivate((Composite) implementation);
            } else if (implementation != null) {
                removeImplementationProvider((RuntimeComponent) component);
                removeScopeContainer(component);
            }
        } catch (Exception e) {
            throw new ActivationException(e);
        }
    }    

    public void activate(Composite composite) throws ActivationException {
        try {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Activating composite: " + composite.getName());
            }
            for (Component component : composite.getComponents()) {
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("Activating component: " + component.getURI());
                }

                Implementation implementation = component.getImplementation();
                if (implementation instanceof Composite) {
                    activate((Composite)implementation);
                } else if (implementation != null) {
                    addImplementationProvider((RuntimeComponent)component, implementation);
                    addScopeContainer(component);
                }

                for (ComponentService service : component.getServices()) {
                    activate((RuntimeComponent)component, (RuntimeComponentService)service);
                }

                for (ComponentReference reference : component.getReferences()) {
                    activate((RuntimeComponent)component, (RuntimeComponentReference)reference);
                }
            }
        } catch (Exception e) {
            throw new ActivationException(e);
        }
    }

    public void deactivate(Composite composite) throws ActivationException {
        try {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Deactivating composite: " + composite.getName());
            }
            for (Component component : composite.getComponents()) {
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("Deactivating component: " + component.getURI());
                }
                for (ComponentService service : component.getServices()) {
                    deactivate((RuntimeComponent)component, (RuntimeComponentService)service);
                }

                for (ComponentReference reference : component.getReferences()) {
                    deactivate((RuntimeComponent)component, (RuntimeComponentReference)reference);
                }

                Implementation implementation = component.getImplementation();
                if (implementation instanceof Composite) {
                    deactivate((Composite)implementation);
                } else if (implementation != null) {
                    removeImplementationProvider((RuntimeComponent)component);
                    removeScopeContainer(component);
                }
            }
        } catch (Exception e) {
            throw new ActivationException(e);
        }
    }

    /**
     * @return the referenceHelper
     */
    public ComponentContextHelper getComponentContextHelper() {
        return componentContextHelper;
    }

    /**
     * @return the proxyFactory
     */
    public ProxyFactory getProxyFactory() {
        return proxyFactory;
    }

    /**
     * @return the domainComposite
     */
    public Composite getDomainComposite() {
        return domainComposite;
    }

    /**
     * @param domainComposite the domainComposite to set
     */
    public void setDomainComposite(Composite domainComposite) {
        this.domainComposite = domainComposite;
    }

    public Component resolve(String componentURI) {
        for (Composite composite : domainComposite.getIncludes()) {
            Component component = resolve(composite, componentURI);
            if (component != null) {
                return component;
            }
        }
        return null;
    }

    public Component resolve(Composite composite, String componentURI) {
        for (Component component : composite.getComponents()) {
            String uri = component.getURI();
            if (uri.equals(componentURI)) {
                return component;
            }
            if (componentURI.startsWith(uri)) {
                Implementation implementation = component.getImplementation();
                if (!(implementation instanceof Composite)) {
                    return null;
                }
                return resolve((Composite)implementation, componentURI);
            }
        }
        return null;
    }

    /**
     * @return the javaInterfaceFactory
     */
    public JavaInterfaceFactory getJavaInterfaceFactory() {
        return javaInterfaceFactory;
    }

    /**
     * @return the conversationManager
     */
    public ConversationManager getConversationManager() {
        return conversationManager;
    }
    
}
