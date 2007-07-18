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

import java.util.List;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.WireableBinding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderMonitor;
import org.apache.tuscany.sca.assembly.builder.Problem;
import org.apache.tuscany.sca.assembly.builder.impl.CompositeBuilderImpl;
import org.apache.tuscany.sca.core.invocation.InvocationChainImpl;
import org.apache.tuscany.sca.core.invocation.NonBlockingInterceptor;
import org.apache.tuscany.sca.interfacedef.IncompatibleInterfaceContractException;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.BindingProviderFactory;
import org.apache.tuscany.sca.provider.ImplementationProvider;
import org.apache.tuscany.sca.provider.ImplementationProviderFactory;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider2;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.provider.ServiceBindingProvider2;
import org.apache.tuscany.sca.runtime.EndpointReference;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.apache.tuscany.sca.runtime.RuntimeWireProcessor;
import org.apache.tuscany.sca.scope.ScopeRegistry;
import org.apache.tuscany.sca.scope.ScopedRuntimeComponent;
import org.apache.tuscany.sca.work.WorkScheduler;

/**
 * @version $Rev$ $Date$
 */
public class CompositeActivatorImpl implements CompositeActivator {

    private final AssemblyFactory assemblyFactory;
    private final SCABindingFactory scaBindingFactory;
    private final InterfaceContractMapper interfaceContractMapper;
    private final ScopeRegistry scopeRegistry;
    private final WorkScheduler workScheduler;
    private final RuntimeWireProcessor wireProcessor;
    private final ProviderFactoryExtensionPoint providerFactories;

    /**
     * @param assemblyFactory
     * @param interfaceContractMapper
     * @param workContext
     * @param workScheduler
     * @param wirePostProcessorRegistry
     */
    public CompositeActivatorImpl(AssemblyFactory assemblyFactory,
                                  SCABindingFactory scaBindingFactory,
                                  InterfaceContractMapper interfaceContractMapper,
                                  ScopeRegistry scopeRegistry,
                                  WorkScheduler workScheduler,
                                  RuntimeWireProcessor wireProcessor,
                                  ProviderFactoryExtensionPoint providerFactories) {
        super();
        this.assemblyFactory = assemblyFactory;
        this.scaBindingFactory = scaBindingFactory;
        this.interfaceContractMapper = interfaceContractMapper;
        this.scopeRegistry = scopeRegistry;
        this.workScheduler = workScheduler;
        this.wireProcessor = wireProcessor;
        this.providerFactories = providerFactories;
    }

    /**
     * Configure a composite
     * 
     * @param composite
     * @throws IncompatibleInterfaceContractException
     */
    @SuppressWarnings("unchecked")
    protected void configureComposite(Composite composite) throws IncompatibleInterfaceContractException {
        for (Component component : composite.getComponents()) {

            for (ComponentService service : component.getServices()) {
                createServiceBindingProviders((RuntimeComponent)component, (RuntimeComponentService)service, service
                    .getBindings());
                if (service.getCallback() != null) {
                    createServiceBindingProviders((RuntimeComponent)component,
                                                  (RuntimeComponentService)service,
                                                  service.getCallback().getBindings());
                }
            }

            for (ComponentReference reference : component.getReferences()) {
                createReferenceBindingProviders((RuntimeComponent)component,
                                                (RuntimeComponentReference)reference,
                                                reference.getBindings());
                if (reference.getCallback() != null) {
                    createReferenceBindingProviders((RuntimeComponent)component,
                                                    (RuntimeComponentReference)reference,
                                                    reference.getCallback().getBindings());
                }
            }

            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {
                configureComposite((Composite)implementation);
            } else if (implementation != null) {
                ImplementationProviderFactory providerFactory =
                    (ImplementationProviderFactory)providerFactories.getProviderFactory(implementation.getClass());
                if (providerFactory != null) {
                    ImplementationProvider implementationProvider =
                        providerFactory.createImplementationProvider((RuntimeComponent)component, implementation);
                    if (implementationProvider != null) {
                        ((RuntimeComponent)component).setImplementationProvider(implementationProvider);
                    }
                } else {
                    throw new IllegalStateException("Provider factory not found for class: " + implementation
                        .getClass().getName());
                }
                setScopeContainer(component);
            }
        }
    }

    private void createServiceBindingProviders(RuntimeComponent component,
                                               RuntimeComponentService service,
                                               List<Binding> bindings) {
        for (Binding binding : bindings) {
            BindingProviderFactory providerFactory =
                (BindingProviderFactory)providerFactories.getProviderFactory(binding.getClass());
            if (providerFactory != null) {
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
    }

    private void createReferenceBindingProviders(RuntimeComponent component,
                                                 RuntimeComponentReference reference,
                                                 List<Binding> bindings) {
        for (Binding binding : bindings) {
            BindingProviderFactory providerFactory =
                (BindingProviderFactory)providerFactories.getProviderFactory(binding.getClass());
            if (providerFactory != null) {
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
    }

    /**
     * Start a composite
     */
    protected void startComposite(Composite composite) {
        for (Component component : composite.getComponents()) {
            startComponent(component);
        }
    }

    /**
     * Stop a composite
     */
    public void stopComposite(Composite composite) {
        for (Component component : composite.getComponents()) {
            stopComponent(component);

        }

    }

    /**
     * Start a component
     */
    public void startComponent(Component component) {

        for (ComponentService service : component.getServices()) {
            for (Binding binding : service.getBindings()) {
                ServiceBindingProvider bindingProvider = ((RuntimeComponentService)service).getBindingProvider(binding);
                if (bindingProvider != null) {
                    bindingProvider.start();
                }
            }
            if (service.getCallback() != null) {
                for (Binding binding : service.getCallback().getBindings()) {
                    ServiceBindingProvider bindingProvider =
                        ((RuntimeComponentService)service).getBindingProvider(binding);
                    if (bindingProvider != null) {
                        bindingProvider.start();
                    }
                }
            }
            for (RuntimeWire wire : ((RuntimeComponentService)service).getRuntimeWires()) {
                wireProcessor.process(wire);
            }
            for (RuntimeWire wire : ((RuntimeComponentService)service).getCallbackWires()) {
                wireProcessor.process(wire);
            }
        }
        for (ComponentReference reference : component.getReferences()) {
            for (Binding binding : reference.getBindings()) {
                ReferenceBindingProvider bindingProvider =
                    ((RuntimeComponentReference)reference).getBindingProvider(binding);
                if (bindingProvider != null) {
                    try {
                        bindingProvider.start();
                    } catch (RuntimeException e) {
                        // TODO: [rfeng] Ignore the self reference if a runtime exception happens
                        if (!reference.getName().startsWith("$self$.")) {
                            throw e;
                        }
                    }
                }
            }
            if (reference.getCallback() != null) {
                for (Binding binding : reference.getCallback().getBindings()) {
                    ReferenceBindingProvider bindingProvider =
                        ((RuntimeComponentReference)reference).getBindingProvider(binding);
                    if (bindingProvider != null) {
                        bindingProvider.start();
                    }
                }
            }
            for (RuntimeWire wire : ((RuntimeComponentReference)reference).getRuntimeWires()) {
                wireProcessor.process(wire);
            }
        }

        Implementation implementation = component.getImplementation();
        if (implementation instanceof Composite) {
            startComposite((Composite)implementation);
        } else {
            ImplementationProvider implementationProvider = ((RuntimeComponent)component).getImplementationProvider();
            if (implementationProvider != null) {
                implementationProvider.start();
            }
        }

        if (component instanceof ScopedRuntimeComponent) {
            ScopedRuntimeComponent runtimeComponent = (ScopedRuntimeComponent)component;
            if (runtimeComponent.getScopeContainer() != null) {
                runtimeComponent.getScopeContainer().start();
            }
        }

        ((RuntimeComponent)component).setStarted(true);
    }

    /**
     * Stop a component
     */
    public void stopComponent(Component component) {
        for (ComponentService service : component.getServices()) {
            for (Binding binding : service.getBindings()) {
                ServiceBindingProvider bindingProvider = ((RuntimeComponentService)service).getBindingProvider(binding);
                if (bindingProvider != null) {
                    bindingProvider.stop();
                }
            }
            if (service.getCallback() != null) {
                for (Binding binding : service.getCallback().getBindings()) {
                    ServiceBindingProvider bindingProvider =
                        ((RuntimeComponentService)service).getBindingProvider(binding);
                    if (bindingProvider != null) {
                        bindingProvider.stop();
                    }
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
            if (reference.getCallback() != null) {
                for (Binding binding : reference.getCallback().getBindings()) {
                    ReferenceBindingProvider bindingProvider =
                        ((RuntimeComponentReference)reference).getBindingProvider(binding);
                    if (bindingProvider != null) {
                        bindingProvider.stop();
                    }
                }
            }
        }
        Implementation implementation = component.getImplementation();
        if (implementation instanceof Composite) {
            stopComposite((Composite)implementation);
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
    protected void createRuntimeWires(Composite composite) throws IncompatibleInterfaceContractException {
        for (Component component : composite.getComponents()) {
            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {
                // Recursively create runtime wires
                createRuntimeWires((Composite)implementation);
            } else {
                // Create outbound wires for the component references
                for (ComponentReference reference : component.getReferences()) {
                    for (Binding binding : reference.getBindings()) {
                        createWires(component, reference, binding, false);
                    }
                    if (reference.getCallback() != null) {
                        for (Binding binding : reference.getCallback().getBindings()) {
                            createWires(component, reference, binding, true);
                        }
                    }
                }
                // Create inbound wires for the component services
                for (ComponentService service : component.getServices()) {
                    for (Binding binding : service.getBindings()) {
                        createWires(component, service, binding, false);
                    }
                    if (service.getCallback() != null) {
                        for (Binding binding : service.getCallback().getBindings()) {
                            if (binding instanceof WireableBinding) {
                                if (((WireableBinding)binding).getTargetComponent() != null) {
                                    continue;
                                }
                            }
                            createWires(component, service, binding, true);
                        }
                    }
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
    private InterfaceContract getInterfaceContract(ComponentReference reference, Binding binding, boolean isCallback) {
        InterfaceContract interfaceContract = reference.getInterfaceContract();
        ReferenceBindingProvider provider = ((RuntimeComponentReference)reference).getBindingProvider(binding);
        if (provider != null) {
            InterfaceContract bindingContract = provider.getBindingInterfaceContract();
            if (bindingContract != null) {
                interfaceContract = bindingContract;
            }
        }
        return interfaceContract.makeUnidirectional(isCallback);
    }

    /**
     * Create the runtime wires for a reference binding
     * 
     * @param component
     * @param reference
     * @param binding
     * @param isCallback
     */
    private void createWires(Component component, ComponentReference reference, Binding binding, boolean isCallback) {
        if (!(reference instanceof RuntimeComponentReference)) {
            return;
        }
        if ((!(binding instanceof WireableBinding)) || binding.getURI() != null || isCallback) {
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
            if (!isCallback) {
                createReferenceWire(reference,
                                    component,
                                    binding,
                                    targetComponentService,
                                    targetComponent,
                                    targetBinding,
                                    isCallback);
            } else {
                createReferenceWire(reference, component, binding, null, null, binding, true);
                if (targetComponentService != null) {
                    Binding serviceBinding = targetComponentService.getCallbackBinding(binding.getClass());
                    if (serviceBinding != null) {
                        createServiceWire(targetComponentService,
                                          targetComponent,
                                          serviceBinding,
                                          reference,
                                          component,
                                          binding,
                                          true);
                    }
                }

            }
        }
    }

    /**
     * Create a reference wire for a forward call or a callback
     * 
     * @param component
     * @param reference
     * @param referenceBinding
     * @param service
     * @param serviceBinding
     * @param isCallback
     */
    private RuntimeWire createReferenceWire(ComponentReference reference,
                                            Component refComponent,
                                            Binding refBinding,
                                            ComponentService service,
                                            Component serviceComponent,
                                            Binding serviceBinding,
                                            boolean isCallback) {
        RuntimeComponentReference runtimeRef = (RuntimeComponentReference)reference;
        InterfaceContract bindingContract = getInterfaceContract(reference, refBinding, isCallback);

        // Use the interface contract of the reference on the component type
        Reference componentTypeRef = reference.getReference();
        InterfaceContract sourceContract =
            componentTypeRef == null ? reference.getInterfaceContract() : componentTypeRef.getInterfaceContract();
        sourceContract = sourceContract.makeUnidirectional(isCallback);

        EndpointReference wireSource =
            new EndpointReferenceImpl((RuntimeComponent)refComponent, (RuntimeComponentReference)reference, refBinding,
                                      sourceContract);

        EndpointReference wireTarget =
            new EndpointReferenceImpl((RuntimeComponent)serviceComponent, (RuntimeComponentService)service,
                                      serviceBinding, bindingContract);

        RuntimeWire wire = new RuntimeWireImpl(wireSource, wireTarget);
        if (!isCallback) {
            for (Operation operation : sourceContract.getInterface().getOperations()) {
                Operation targetOperation = interfaceContractMapper.map(bindingContract.getInterface(), operation);
                InvocationChain chain = new InvocationChainImpl(operation, targetOperation);
                if (operation.isNonBlocking()) {
                    addNonBlockingInterceptor(reference, refBinding, chain);
                }
                addBindingInterceptor(reference, refBinding, chain, operation);
                wire.getInvocationChains().add(chain);
            }
        } else {
            for (Operation operation : bindingContract.getCallbackInterface().getOperations()) {
                Operation targetOperation =
                    interfaceContractMapper.map(sourceContract.getCallbackInterface(), operation);
                InvocationChain chain = new InvocationChainImpl(operation, targetOperation);
                if (!reference.getName().startsWith("$self$.")) {
                    addImplementationInterceptor(refComponent, null, chain, targetOperation, true);
                } else {
                    //FIXME: need to invoke the callback object
                }
                wire.addCallbackInvocationChain(chain);
            }
        }
        runtimeRef.getRuntimeWires().add(wire);
        return wire;
    }

    /**
     * Get the effective interface contract for the service binding
     * 
     * @param service
     * @param binding
     * @return
     */
    private InterfaceContract getInterfaceContract(ComponentService service, Binding binding, boolean isCallback) {
        InterfaceContract interfaceContract = service.getInterfaceContract();

        ServiceBindingProvider provider = ((RuntimeComponentService)service).getBindingProvider(binding);
        if (provider != null) {
            InterfaceContract bindingContract = provider.getBindingInterfaceContract();
            if (bindingContract != null) {
                interfaceContract = bindingContract;
            }
        }
        return interfaceContract.makeUnidirectional(isCallback);
    }

    /**
     * Create runtime wires for a service binding
     * 
     * @param component
     * @param service
     * @param binding
     * @param isCallback
     */
    private void createWires(Component component, ComponentService service, Binding binding, boolean isCallback) {
        if (!(service instanceof RuntimeComponentService)) {
            return;
        }
        RuntimeWire wire = createServiceWire(service, component, binding, null, null, binding, isCallback);

        //FIXME: need better way to create the source URI
        wire.getSource().setURI(binding.getURI());
    }

    /**
     * Create a service wire for a forward call or a callback
     * 
     * @param component
     * @param service
     * @param serviceBinding
     * @param reference
     * @param referenceBinding
     * @param isCallback
     */
    private RuntimeWire createServiceWire(ComponentService service,
                                          Component serviceComponent,
                                          Binding serviceBinding,
                                          ComponentReference reference,
                                          Component refComponent,
                                          Binding refBinding,
                                          boolean isCallback) {
        RuntimeComponentService runtimeService = (RuntimeComponentService)service;

        // FIXME: [rfeng] We might need a better way to get the impl interface contract
        InterfaceContract targetContract = service.getService().getInterfaceContract().makeUnidirectional(isCallback);

        InterfaceContract sourceContract = getInterfaceContract(service, serviceBinding, isCallback);

        EndpointReference wireSource =
            new EndpointReferenceImpl((RuntimeComponent)refComponent, (RuntimeComponentReference)reference, refBinding,
                                      sourceContract);

        EndpointReference wireTarget =
            new EndpointReferenceImpl((RuntimeComponent)serviceComponent, (RuntimeComponentService)service,
                                      serviceBinding, targetContract);

        RuntimeWire wire = new RuntimeWireImpl(wireSource, wireTarget);

        if (sourceContract.getInterface() != null) {
            for (Operation operation : sourceContract.getInterface().getOperations()) {
                Operation targetOperation = interfaceContractMapper.map(targetContract.getInterface(), operation);
                InvocationChain chain = new InvocationChainImpl(operation, targetOperation);
                addImplementationInterceptor(serviceComponent, service, chain, targetOperation, false);
                wire.getInvocationChains().add(chain);
            }
            runtimeService.getRuntimeWires().add(wire);
        }

        if (sourceContract.getCallbackInterface() != null) {
            for (Operation operation : targetContract.getCallbackInterface().getOperations()) {
                Operation targetOperation =
                    interfaceContractMapper.map(sourceContract.getCallbackInterface(), operation);
                InvocationChain chain = new InvocationChainImpl(operation, targetOperation);
                if (operation.isNonBlocking()) {
                    addNonBlockingCallbackInterceptor(service, serviceBinding, chain);
                }
                addBindingCallbackInterceptor(service, serviceBinding, chain, operation);
                wire.addCallbackInvocationChain(chain);
            }
            runtimeService.getCallbackWires().add(wire);
        }

        return wire;
    }

    /**
     * Add the interceptor for a component implementation
     * 
     * @param component
     * @param service
     * @param chain
     * @param operation
     * @param isCallback
     */
    private void addImplementationInterceptor(Component component,
                                              ComponentService service,
                                              InvocationChain chain,
                                              Operation operation,
                                              boolean isCallback) {
        ImplementationProvider provider = ((RuntimeComponent)component).getImplementationProvider();
        if (provider != null) {
            Invoker invoker = null;
            if (!isCallback) {
                invoker = provider.createInvoker((RuntimeComponentService)service, operation);
            } else {
                invoker = provider.createCallbackInvoker(operation);
            }
            chain.addInvoker(invoker);
        }
    }

    /**
     * Add the interceptor for a binding
     * 
     * @param reference
     * @param binding
     * @param chain
     * @param operation
     */
    private void addBindingInterceptor(ComponentReference reference,
                                       Binding binding,
                                       InvocationChain chain,
                                       Operation operation) {
        try {
            ReferenceBindingProvider provider = ((RuntimeComponentReference)reference).getBindingProvider(binding);
            if (provider != null) {
                Invoker invoker = null;
                if (provider instanceof ReferenceBindingProvider2) {
                    invoker = ((ReferenceBindingProvider2)provider).createInvoker(operation);
                } else {
                    // must be an old provider that only has the deprecated signature
                    invoker = provider.createInvoker(operation, false);
                }
                if (invoker != null) {
                    chain.addInvoker(invoker);
                }
            }
        } catch (RuntimeException e) {
            // TODO: [rfeng] Ignore the self reference if a runtime exception happens
            if (!reference.getName().startsWith("$self$.")) {
                throw e;
            }
        }
    }

    /**
     * Add a non-blocking interceptor if the reference binding needs it
     * 
     * @param reference
     * @param binding
     * @param chain
     */
    private void addNonBlockingInterceptor(ComponentReference reference, Binding binding, InvocationChain chain) {
        ReferenceBindingProvider provider = ((RuntimeComponentReference)reference).getBindingProvider(binding);
        if (provider != null) {
            boolean supportsAsyncOneWayInvocation = false;
            if (provider instanceof ReferenceBindingProvider2) {
                supportsAsyncOneWayInvocation = ((ReferenceBindingProvider2)provider).supportsAsyncOneWayInvocation();
            } else {
                // must be an old provider that doesn't have this method
            }
            if (!supportsAsyncOneWayInvocation) {
                chain.addInterceptor(new NonBlockingInterceptor(workScheduler));
            }
        }
    }

    /**
     * Add a non-blocking interceptor if the service binding needs it
     * 
     * @param service
     * @param binding
     * @param chain
     */
    private void addNonBlockingCallbackInterceptor(ComponentService service, Binding binding, InvocationChain chain) {
        ServiceBindingProvider provider = ((RuntimeComponentService)service).getBindingProvider(binding);
        if (provider != null) {
            boolean supportsAsyncOneWayInvocation = false;
            if (provider instanceof ServiceBindingProvider2) {
                supportsAsyncOneWayInvocation = ((ServiceBindingProvider2)provider).supportsAsyncOneWayInvocation();
            } else {
                // must be an old provider that doesn't have this method
            }
            if (!supportsAsyncOneWayInvocation) {
                chain.addInterceptor(new NonBlockingInterceptor(workScheduler));
            }
        }
    }

    /**
     * Add the interceptor for callbacks through a binding
     * 
     * @param component
     * @param service
     * @param binding
     * @param chain
     * @param operation
     */
    private void addBindingCallbackInterceptor(ComponentService service,
                                               Binding binding,
                                               InvocationChain chain,
                                               Operation operation) {
        ServiceBindingProvider provider = ((RuntimeComponentService)service).getBindingProvider(binding);
        if (provider != null) {
            Invoker invoker = null;
            if (provider instanceof ServiceBindingProvider2) {
                invoker = ((ServiceBindingProvider2)provider).createCallbackInvoker(operation);
            } else {
                // must be an old provider that does not support callbacks
            }
            if (invoker != null) {
                chain.addInvoker(invoker);
            }
        }
    }

    private void setScopeContainer(Component component) {
        if (!(component instanceof ScopedRuntimeComponent)) {
            return;
        }
        ScopedRuntimeComponent runtimeComponent = (ScopedRuntimeComponent)component;
        runtimeComponent.setScopeContainer(scopeRegistry.getScopeContainer(runtimeComponent));
    }

    protected void buildComposite(Composite composite) throws CompositeBuilderException {

        CompositeBuilderMonitor monitor = new CompositeBuilderMonitor() {

            public void problem(Problem problem) {
                // Uncommenting the following two lines can be useful to detect
                // and troubleshoot SCA assembly XML composite configuration
                // problems.

                System.out.println("Composite assembly problem: " + problem.getMessage());
            }
        };

        CompositeBuilderImpl builder =
            new CompositeBuilderImpl(assemblyFactory, scaBindingFactory, interfaceContractMapper, monitor);

        builder.build(composite);

        // if (!problems.isEmpty()) {
        // throw new VariantRuntimeException(new RuntimeException("Problems in
        // the composite..."));
        // }
    }

    public void activate(Composite composite) throws ActivationException {
        try {
            buildComposite(composite);
            configureComposite(composite);
            createRuntimeWires(composite);
        } catch (Exception e) {
            throw new ActivationException(e);
        }
    }

    public void deactivate(Composite composite) throws ActivationException {
    }

    public void start(Composite composite) throws ActivationException {
        try {
            startComposite(composite);
        } catch (Exception e) {
            throw new ActivationException(e);
        }
    }

    public void stop(Composite composite) throws ActivationException {
        try {
            stopComposite(composite);
        } catch (Exception e) {
            throw new ActivationException(e);
        }
    }

    public void start(Component component) throws ActivationException {
        try {
            startComponent(component);
        } catch (Exception e) {
            throw new ActivationException(e);
        }

    }

    public void stop(Component component) throws ActivationException {
        try {
            stopComponent(component);
        } catch (Exception e) {
            throw new ActivationException(e);
        }
    }
}
