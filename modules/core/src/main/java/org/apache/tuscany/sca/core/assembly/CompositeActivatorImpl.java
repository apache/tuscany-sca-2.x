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

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.WireableBinding;
import org.apache.tuscany.sca.context.RequestContextFactory;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.context.ComponentContextHelper;
import org.apache.tuscany.sca.core.context.ComponentContextImpl;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.core.scope.ScopeRegistry;
import org.apache.tuscany.sca.core.scope.ScopedRuntimeComponent;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.provider.BindingProviderFactory;
import org.apache.tuscany.sca.provider.ImplementationProvider;
import org.apache.tuscany.sca.provider.ImplementationProviderFactory;
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

    private final static String CALLBACK_PREFIX = "$callback$.";

    private final AssemblyFactory assemblyFactory;
    private final InterfaceContractMapper interfaceContractMapper;
    private final ScopeRegistry scopeRegistry;
    private final WorkScheduler workScheduler;
    private final RuntimeWireProcessor wireProcessor;
    private final ProviderFactoryExtensionPoint providerFactories;

    private final RequestContextFactory requestContextFactory;
    private final ProxyFactory proxyFactory;
    private final JavaInterfaceFactory javaInterfaceFactory;

    private final ComponentContextHelper componentContextHelper;

    private Composite domainComposite;

    /**
     * @param assemblyFactory
     * @param interfaceContractMapper
     * @param workContext
     * @param workScheduler
     * @param wirePostProcessorRegistry
     */
    public CompositeActivatorImpl(AssemblyFactory assemblyFactory,
                                  JavaInterfaceFactory javaInterfaceFactory,
                                  SCABindingFactory scaBindingFactory,
                                  InterfaceContractMapper interfaceContractMapper,
                                  ScopeRegistry scopeRegistry,
                                  WorkScheduler workScheduler,
                                  RuntimeWireProcessor wireProcessor,
                                  RequestContextFactory requestContextFactory,
                                  ProxyFactory proxyFactory,
                                  ProviderFactoryExtensionPoint providerFactories,
                                  StAXArtifactProcessorExtensionPoint processors) {
        this.assemblyFactory = assemblyFactory;
        this.interfaceContractMapper = interfaceContractMapper;
        this.scopeRegistry = scopeRegistry;
        this.workScheduler = workScheduler;
        this.wireProcessor = wireProcessor;
        this.providerFactories = providerFactories;
        this.javaInterfaceFactory = javaInterfaceFactory;
        this.requestContextFactory = requestContextFactory;
        this.proxyFactory = proxyFactory;
        this.componentContextHelper = new ComponentContextHelper(assemblyFactory, javaInterfaceFactory, processors);
    }

    /**
     * @see org.apache.tuscany.sca.core.assembly.CompositeActivator#activate(org.apache.tuscany.sca.runtime.RuntimeComponent, org.apache.tuscany.sca.runtime.RuntimeComponentReference)
     */
    public void activate(RuntimeComponent component, RuntimeComponentReference ref) {
        resolveTargets(ref);
        for (Binding binding : ref.getBindings()) {
            addReferenceBindingProvider(component, ref, binding);
            addReferenceWire(component, ref, binding);
            ReferenceBindingProvider provider = ref.getBindingProvider(binding);
            if (provider != null) {
                provider.start();
            }
        }
    }

    public void deactivate(RuntimeComponent component, RuntimeComponentReference ref) {
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
    private void addReferenceBindingProvider(RuntimeComponent component,
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
        } else {
            throw new IllegalStateException("Provider factory not found for class: " + binding.getClass().getName());
        }
    }

    /**
     * @param reference
     */
    private void resolveTargets(RuntimeComponentReference reference) {
        // Support for distributed domain follows

        // go over any targets that have not been resolved yet (as they are running on other nodes)
        // and try an resolve them remotely
        // TODO - this should work for any kind of wired binding but the only wireable binding 
        //        is currently the SCA binding so we assume that
        for (ComponentService service : reference.getTargets()) {
            if (service.isUnresolved()) {
                for (Binding binding : service.getBindings()) {
                    // TODO - we should look at all the bindings now associated with the 
                    //        unresolved target but we assume the SCA binding here as
                    //        its currently the only wireable one
                    if (binding instanceof WireableBinding) {
                        WireableBinding scaBinding = (WireableBinding)binding;

                        // clone the SCA binding and fill in service details 
                        // its cloned as each target 
                        SCABinding clonedSCABinding = null;
                        try {
                            clonedSCABinding = (SCABinding)((WireableBinding)scaBinding).clone();
                            clonedSCABinding.setURI(service.getName());
                            // wireable binding stuff needs to go. SCA binding uses it
                            // currently to get to the service to work out if the service
                            // is resolved. 
                            WireableBinding endpoint = ((WireableBinding)clonedSCABinding);
                            endpoint.setTargetComponentService(service);
                            //endpoint.setTargetComponent(component); - not known for unresolved target
                            //endpoint.setTargetBinding(serviceBinding); - not known for unresolved target

                            // add the cloned SCA binding to the reference as it will be used to look up the 
                            // provider later
                            reference.getBindings().remove(binding);
                            reference.getBindings().add(clonedSCABinding);
                        } catch (Exception e) {
                            // warning("The binding doesn't support clone: " + binding.getClass().getSimpleName(), binding);
                        }
                    } else {
                        throw new IllegalStateException(
                                                        "Unable to create a distributed SCA binding provider for reference: " + reference
                                                            .getName()
                                                            + " and target: "
                                                            + service.getName());
                    }
                }
            }
        }
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
        if (binding instanceof WireableBinding) {
            WireableBinding endpoint = (WireableBinding)binding;
            targetComponent = endpoint.getTargetComponent();
            targetComponentService = endpoint.getTargetComponentService();
            targetBinding = endpoint.getTargetBinding();
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
        InterfaceContract sourceContract =
            componentTypeRef == null ? reference.getInterfaceContract() : componentTypeRef.getInterfaceContract();
        sourceContract = sourceContract.makeUnidirectional(false);

        EndpointReference wireSource =
            new EndpointReferenceImpl((RuntimeComponent)refComponent, reference, refBinding, sourceContract);
        ComponentService callbackService = reference.getCallbackService();
        if (callbackService != null) {
            // select a reference callback binding to pass with invocations on this wire
            Binding callbackBinding = null;
            for (Binding binding : callbackService.getBindings()) {
                // first look for a callback binding whose name matches the reference binding name
                if (binding.getName().equals(refBinding.getName()) || binding.getName()
                    .equals(CALLBACK_PREFIX + refBinding.getName())) {
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

        RuntimeWire wire =
            new RuntimeWireImpl(wireSource, wireTarget, interfaceContractMapper, workScheduler, wireProcessor);
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
    }

    private void removeImplementationProvider(RuntimeComponent component) {
        component.setImplementationProvider(null);
    }

    /**
     * @param component
     * @param service
     * @param binding
     */
    private void addServiceBindingProvider(RuntimeComponent component, RuntimeComponentService service, Binding binding) {
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
        } else {
            throw new IllegalStateException("Provider factory not found for class: " + binding.getClass().getName());
        }
    }

    private void removeServiceBindingProvider(RuntimeComponent component,
                                              RuntimeComponentService service,
                                              Binding binding) {
        service.setBindingProvider(binding, null);
    }

    private void removeReferenceBindingProvider(RuntimeComponent component,
                                                RuntimeComponentReference reference,
                                                Binding binding) {
        reference.setBindingProvider(binding, null);
    }

    public void start(Composite composite) {
        for (Component component : composite.getComponents()) {
            start(component);
        }
    }

    public void stop(Composite composite) {
        for (Component component : composite.getComponents()) {
            stop(component);

        }

    }

    public void start(Component component) {
        RuntimeComponent runtimeComponent = ((RuntimeComponent)component);
        configureComponentContext(runtimeComponent);

        for (ComponentService service : component.getServices()) {
            for (Binding binding : service.getBindings()) {
                ServiceBindingProvider bindingProvider = ((RuntimeComponentService)service).getBindingProvider(binding);
                if (bindingProvider != null) {
                    bindingProvider.start();
                }
            }
            //            for (RuntimeWire wire : ((RuntimeComponentService)service).getRuntimeWires()) {
            //                wireProcessor.process(wire);
            //            }
        }

        for (ComponentReference reference : component.getReferences()) {
            ((RuntimeComponentReference)reference).setComponent(runtimeComponent);
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
        for (ComponentService service : component.getServices()) {
            for (Binding binding : service.getBindings()) {
                ServiceBindingProvider bindingProvider = ((RuntimeComponentService)service).getBindingProvider(binding);
                if (bindingProvider != null) {
                    bindingProvider.stop();
                }
            }
        }
        for (ComponentReference reference : component.getReferences()) {
            for (Binding binding : reference.getBindings()) {
                ReferenceBindingProvider bindingProvider =
                    ((RuntimeComponentReference)reference).getBindingProvider(binding);
                if (bindingProvider != null) {
                    bindingProvider.stop();
                }
            }
        }
        Implementation implementation = component.getImplementation();
        if (implementation instanceof Composite) {
            stop((Composite)implementation);
        } else {
            ImplementationProvider implementationProvider = ((RuntimeComponent)component).getImplementationProvider();
            if (implementationProvider != null) {
                implementationProvider.stop();
            }
        }

        if (component instanceof ScopedRuntimeComponent) {
            ScopedRuntimeComponent runtimeComponent = (ScopedRuntimeComponent)component;
            if (runtimeComponent.getScopeContainer() != null) {
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
            new RuntimeWireImpl(wireSource, wireTarget, interfaceContractMapper, workScheduler, wireProcessor);
        runtimeService.getRuntimeWires().add(wire);

        return wire;
    }

    public void activate(RuntimeComponent component, RuntimeComponentService service) {
        for (Binding binding : service.getBindings()) {
            addServiceBindingProvider(component, service, binding);
            addServiceWire(component, service, binding);
        }
    }

    public void deactivate(RuntimeComponent component, RuntimeComponentService service) {
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
        runtimeComponent.setScopeContainer(scopeRegistry.getScopeContainer(runtimeComponent));
    }

    private void removeScopeContainer(Component component) {
        if (!(component instanceof ScopedRuntimeComponent)) {
            return;
        }
        ScopedRuntimeComponent runtimeComponent = (ScopedRuntimeComponent)component;
        runtimeComponent.setScopeContainer(null);
    }

    public void activate(Composite composite) throws ActivationException {
        try {
            for (Component component : composite.getComponents()) {

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
            }
        } catch (Exception e) {
            throw new ActivationException(e);
        }
    }

    public void deactivate(Composite composite) throws ActivationException {
        try {
            for (Component component : composite.getComponents()) {
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
        String prefix = componentURI + "/";
        for (Component component : composite.getComponents()) {
            String uri = component.getURI();
            if (uri.equals(componentURI)) {
                return component;
            }
            if (componentURI.startsWith(prefix)) {
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

}
