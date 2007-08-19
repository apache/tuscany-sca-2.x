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

package org.apache.tuscany.sca.core.runtime;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

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
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.component.ComponentContextImpl;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.interfacedef.IncompatibleInterfaceContractException;
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
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.apache.tuscany.sca.runtime.RuntimeWireProcessor;
import org.apache.tuscany.sca.scope.ScopeRegistry;
import org.apache.tuscany.sca.scope.ScopedRuntimeComponent;
import org.apache.tuscany.sca.work.WorkScheduler;
import org.osoa.sca.ComponentContext;

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
    private final StAXArtifactProcessorExtensionPoint staxProcessors;

    private final RequestContextFactory requestContextFactory;
    private final ProxyFactory proxyFactory;
    private final JavaInterfaceFactory javaInterfaceFactory;

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
        this.staxProcessors = processors;
    }

    /**
     * Configure a composite
     * 
     * @param composite
     * @throws IncompatibleInterfaceContractException
     */
    private void addRuntimeProviders(Composite composite) throws IncompatibleInterfaceContractException {
        for (Component component : composite.getComponents()) {

            for (ComponentService service : component.getServices()) {
                addServiceBindingProviders((RuntimeComponent)component, (RuntimeComponentService)service, service
                    .getBindings());
            }

            // [rfeng] Defer this
            /*
            for (ComponentReference reference : component.getReferences()) {
                addReferenceBindingProviders((RuntimeComponent)component,
                                                (RuntimeComponentReference)reference,
                                                reference.getBindings());
            }
            */

            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {
                addRuntimeProviders((Composite)implementation);
            } else if (implementation != null) {
                addImplementationProvider((RuntimeComponent)component, implementation);
                addScopeContainer(component);
            }
        }
    }

    /**
     * Configure a composite
     * 
     * @param composite
     * @throws IncompatibleInterfaceContractException
     */
    private void removeRuntimeProviders(Composite composite) throws IncompatibleInterfaceContractException {
        for (Component component : composite.getComponents()) {

            for (ComponentService service : component.getServices()) {
                removeServiceBindingProviders((RuntimeComponent)component, (RuntimeComponentService)service, service
                    .getBindings());
            }

            for (ComponentReference reference : component.getReferences()) {
                removeReferenceBindingProviders((RuntimeComponent)component,
                                                (RuntimeComponentReference)reference,
                                                reference.getBindings());
            }

            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {
                removeRuntimeProviders((Composite)implementation);
            } else if (implementation != null) {
                removeImplementationProvider((RuntimeComponent)component);
                removeScopeContainer(component);
            }
        }
    }


    /**
     * @see org.apache.tuscany.sca.core.runtime.CompositeActivator#activate(org.apache.tuscany.sca.runtime.RuntimeComponent, org.apache.tuscany.sca.runtime.RuntimeComponentReference)
     */
    public void activate(RuntimeComponent component, RuntimeComponentReference ref) {
        addReferenceBindingProviders(component, ref, ref.getBindings());
        for (Binding binding : ref.getBindings()) {
            addReferenceWire(component, ref, binding);
            ReferenceBindingProvider provider = ref.getBindingProvider(binding);
            if (provider != null) {
                provider.start();
            }
        }
    }

    private void addReferenceBindingProviders(RuntimeComponent component,
                                              RuntimeComponentReference reference,
                                              List<Binding> bindings) {

        List<Binding> unresolvedTargetBindings = new ArrayList<Binding>();

        // create binding providers for all of the bindings for resolved targets
        // or for all of the bindings where no targets are specified
        for (Binding binding : bindings) {
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
                    if (binding instanceof SCABinding) {
                        SCABinding scaBinding = (SCABinding)binding;

                        BindingProviderFactory providerFactory =
                            (BindingProviderFactory)providerFactories.getProviderFactory(SCABinding.class);

                        if (providerFactory == null) {
                            throw new IllegalStateException("Provider factory not found for class: " + scaBinding
                                .getClass().getName());
                        }

                        // clone the SCA binding and fill in service details 
                        SCABinding clonedSCABinding = null;
                        try {
                            clonedSCABinding = (SCABinding)((WireableBinding)scaBinding).clone();
                            clonedSCABinding.setURI(service.getName());
                            ((WireableBinding)clonedSCABinding).setRemote(true);
                        } catch (Exception e) {
                            // warning("The binding doesn't support clone: " + binding.getClass().getSimpleName(), binding);
                        }

                        @SuppressWarnings("unchecked")
                        ReferenceBindingProvider bindingProvider =
                            providerFactory.createReferenceBindingProvider((RuntimeComponent)component,
                                                                           (RuntimeComponentReference)reference,
                                                                           clonedSCABinding);
                        if (bindingProvider != null) {
                            ((RuntimeComponentReference)reference)
                                .setBindingProvider(clonedSCABinding, bindingProvider);

                            // add the cloned SCA binding to the reference as it will be used to look up the 
                            // provider later
                            reference.getBindings().remove(binding);
                            reference.getBindings().add(clonedSCABinding);
                        } else {
                            throw new IllegalStateException(
                                                            "Unable to create a distributed SCA binding provider for reference: " + 
                                                             reference.getName()
                                                                + " and target: "
                                                                + service.getName());
                        }
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

    private void addServiceBindingProviders(RuntimeComponent component,
                                            RuntimeComponentService service,
                                            List<Binding> bindings) {
        for (Binding binding : bindings) {
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

        // support for distributed domain follows
        // TODO - roll into above code but keeping separate so that it is obvious 

        // If there is an SCA binding for the service add a second one marked as
        // remote in the case where the service interface is remotable. This
        // service binding provides a separate wire that will be active if 
        // this service is referenced remotely.  
        SCABinding clonedSCABinding = null;

        for (Binding binding : bindings) {
            if ((binding instanceof SCABinding) && (service.getInterfaceContract().getInterface().isRemotable())) {
                SCABinding scaBinding = (SCABinding)binding;

                BindingProviderFactory providerFactory =
                    (BindingProviderFactory)providerFactories.getProviderFactory(binding.getClass());
                if (providerFactory != null) {

                    // clone the SCA binding and fill in service details 
                    try {
                        clonedSCABinding = (SCABinding)((WireableBinding)scaBinding).clone();
                        ((WireableBinding)clonedSCABinding).setRemote(true);
                    } catch (Exception e) {
                        // warning("The binding doesn't support clone: " + binding.getClass().getSimpleName(), binding);
                    }

                    @SuppressWarnings("unchecked")
                    ServiceBindingProvider bindingProvider =
                        providerFactory.createServiceBindingProvider((RuntimeComponent)component,
                                                                     (RuntimeComponentService)service,
                                                                     clonedSCABinding);
                    if (bindingProvider != null) {
                        ((RuntimeComponentService)service).setBindingProvider(clonedSCABinding, bindingProvider);
                    }
                } else {
                    throw new IllegalStateException("Provider factory not found for class: " + binding.getClass()
                        .getName());
                }
            }
            // add the cloned SCA binding to the service as it will be used to look up the provider later
            if (clonedSCABinding != null) {
                service.getBindings().remove(binding);
                service.getBindings().add(clonedSCABinding);
            }
        }

    }

    private void removeServiceBindingProviders(RuntimeComponent component,
                                               RuntimeComponentService service,
                                               List<Binding> bindings) {
        for (Binding binding : bindings) {
            ((RuntimeComponentService)service).setBindingProvider(binding, null);
        }
    }

    private void removeReferenceBindingProviders(RuntimeComponent component,
                                                 RuntimeComponentReference reference,
                                                 List<Binding> bindings) {
        for (Binding binding : bindings) {
            ((RuntimeComponentReference)reference).setBindingProvider(binding, null);
        }
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
        ComponentContext componentContext =
            new ComponentContextImpl(this, assemblyFactory, proxyFactory, interfaceContractMapper,
                                     requestContextFactory, javaInterfaceFactory, runtimeComponent);
        runtimeComponent.setComponentContext(componentContext);

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
     * Create runtime wires for the composite
     * 
     * @param composite
     * @throws IncompatibleInterfaceContractException
     */
    private void addRuntimeWires(Composite composite) throws IncompatibleInterfaceContractException {
        for (Component component : composite.getComponents()) {
            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {
                // Recursively create runtime wires
                addRuntimeWires((Composite)implementation);
            } else {
                /* [rfeng] Defer this
                // Create outbound wires for the component references
                for (ComponentReference reference : component.getReferences()) {
                    for (Binding binding : reference.getBindings()) {
                        addReferenceWire(component, reference, binding);
                    }
                }
                */
                // Create inbound wires for the component services
                for (ComponentService service : component.getServices()) {
                    for (Binding binding : service.getBindings()) {
                        addServiceWire(component, service, binding);
                    }
                }
            }
        }
    }

    /**
     * Remove runtime wires for the composite
     * 
     * @param composite
     * @throws IncompatibleInterfaceContractException
     */
    private void removeRuntimeWires(Composite composite) throws IncompatibleInterfaceContractException {
        for (Component component : composite.getComponents()) {
            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {
                // Recursively remove runtime wires
                removeRuntimeWires((Composite)implementation);
            } else {
                // Remove outbound wires for the component references
                for (ComponentReference reference : component.getReferences()) {
                    removeReferenceWires(reference);
                }
                // Remove inbound wires for the component services
                for (ComponentService service : component.getServices()) {
                    removeServiceWires(service);
                }
            }
        }
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
            addRuntimeProviders(composite);
            addRuntimeWires(composite);
        } catch (Exception e) {
            throw new ActivationException(e);
        }
    }

    public void deactivate(Composite composite) throws ActivationException {
        try {
            removeRuntimeProviders(composite);
            removeRuntimeWires(composite);
        } catch (Exception e) {
            throw new ActivationException(e);
        }
    }

    public void write(Component component, ComponentReference reference, Writer writer) throws IOException {
        try {
            StAXArtifactProcessor<Composite> processor = staxProcessors.getProcessor(Composite.class);
            Composite composite = assemblyFactory.createComposite();
            composite.setName(new QName("http://tuscany.apache.org/xmlns/sca/1.0", "default"));
            Component comp = assemblyFactory.createComponent();
            comp.setName("default");
            comp.setURI(component.getURI());
            composite.getComponents().add(comp);
            comp.getReferences().add(reference);

            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            XMLStreamWriter streamWriter = outputFactory.createXMLStreamWriter(writer);
            processor.write(composite, streamWriter);
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    public String write(Component component, ComponentReference reference) throws IOException {
        StringWriter writer = new StringWriter();
        write(component, reference, writer);
        return writer.toString();
    }

    public Component read(Reader reader) throws IOException {
        try {
            StAXArtifactProcessor<Composite> processor = staxProcessors.getProcessor(Composite.class);
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLStreamReader streamReader = inputFactory.createXMLStreamReader(reader);
            Composite composite = processor.read(streamReader);
            return composite.getComponents().get(0);
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

}
