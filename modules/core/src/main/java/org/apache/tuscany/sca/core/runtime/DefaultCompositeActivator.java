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

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderMonitor;
import org.apache.tuscany.sca.assembly.builder.Problem;
import org.apache.tuscany.sca.assembly.builder.impl.DefaultCompositeBuilder;
import org.apache.tuscany.sca.core.RuntimeComponent;
import org.apache.tuscany.sca.core.RuntimeComponentReference;
import org.apache.tuscany.sca.core.RuntimeComponentService;
import org.apache.tuscany.sca.core.RuntimeWire;
import org.apache.tuscany.sca.core.RuntimeWireProcessor;
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
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.scope.ScopeRegistry;
import org.apache.tuscany.sca.scope.ScopedRuntimeComponent;
import org.apache.tuscany.sca.work.WorkScheduler;

/**
 * @version $Rev$ $Date$
 */
public class DefaultCompositeActivator implements CompositeActivator {

    private final AssemblyFactory assemblyFactory;
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
    public DefaultCompositeActivator(AssemblyFactory assemblyFactory,
                                     InterfaceContractMapper interfaceContractMapper,
                                     ScopeRegistry scopeRegistry,
                                     WorkScheduler workScheduler,
                                     RuntimeWireProcessor wireProcessor,
                                     ProviderFactoryExtensionPoint providerFactories) {
        super();
        this.assemblyFactory = assemblyFactory;
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
                for (Binding binding : service.getBindings()) {
                    BindingProviderFactory providerFactory = (BindingProviderFactory)providerFactories.getProviderFactory(binding.getClass());
                    if (providerFactory != null) {
                        ServiceBindingProvider bindingProvider =
                            providerFactory.createServiceBindingProvider((RuntimeComponent)component, (RuntimeComponentService)service, binding);
                        if (bindingProvider != null) {
                            ((RuntimeComponentService)service).setBindingProvider(binding, bindingProvider);
                        }
                    } else {
                        throw new IllegalStateException("Provider factory not found for class: " + binding.getClass().getName());
                    }
                }
            }
            for (ComponentReference reference : component.getReferences()) {
                for (Binding binding : reference.getBindings()) {
                    BindingProviderFactory providerFactory = (BindingProviderFactory)providerFactories.getProviderFactory(binding.getClass());
                    if (providerFactory != null) {
                        ReferenceBindingProvider bindingProvider =
                            providerFactory.createReferenceBindingProvider((RuntimeComponent)component, (RuntimeComponentReference)reference, binding);
                        if (bindingProvider != null) {
                            ((RuntimeComponentReference)reference).setBindingProvider(binding, bindingProvider);
                        }
                    } else {
                        throw new IllegalStateException("Provider factory not found for class: " + binding.getClass().getName());
                    }
                }
            }

            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {
                configureComposite((Composite)implementation);
            } else if (implementation != null) {
                ImplementationProviderFactory providerFactory = (ImplementationProviderFactory)providerFactories.getProviderFactory(implementation.getClass());
                if (providerFactory != null) {
                    ImplementationProvider implementationProvider =
                        providerFactory.createImplementationProvider((RuntimeComponent)component, implementation);
                    if (implementationProvider != null) {
                        ((RuntimeComponent)component).setImplementationProvider(implementationProvider);
                    }
                } else {
                    throw new IllegalStateException("Provider factory not found for class: " + implementation.getClass().getName());
                }
                setScopeContainer(component);
            }
        }
    }

    /**
     * Start a composite
     */
    protected void startComposite(Composite composite) {
        for (Component component : composite.getComponents()) {
            
            for (ComponentService service : component.getServices()) {
                for (Binding binding : service.getBindings()) {
                    ServiceBindingProvider bindingProvider = ((RuntimeComponentService)service).getBindingProvider(binding);
                    if (bindingProvider != null) {
                        bindingProvider.start();
                    }
                }
            }
            for (ComponentReference reference : component.getReferences()) {
                for (Binding binding : reference.getBindings()) {
                    ReferenceBindingProvider bindingProvider = ((RuntimeComponentReference)reference).getBindingProvider(binding);
                    if (bindingProvider != null) {
                        bindingProvider.start();
                    }
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
            
        }
    }

    public void stop(Composite composite) {
        for (Component component : composite.getComponents()) {
            
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
                    ReferenceBindingProvider bindingProvider = ((RuntimeComponentReference)reference).getBindingProvider(binding);
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
            
            
        }

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
                        createWires(component, reference, binding);
                    }
                }
                // Create inbound wires for the component services
                for (ComponentService service : component.getServices()) {
                    for (Binding binding : service.getBindings()) {
                        createWires(component, service, binding);
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
    private InterfaceContract getInterfaceContract(ComponentReference reference, Binding binding) {
        InterfaceContract interfaceContract = reference.getInterfaceContract();
        ReferenceBindingProvider provider = ((RuntimeComponentReference)reference).getBindingProvider(binding);
        if (provider != null) {
            InterfaceContract bindingContract = provider.getBindingInterfaceContract();
            if (bindingContract != null) {
                interfaceContract = bindingContract;
            }
        }
        return interfaceContract;
    }

    /**
     * Create the runtime wires for a reference binding
     * 
     * @param component
     * @param reference
     * @param binding
     */
    private void createWires(Component component, ComponentReference reference, Binding binding) {
        if (!(reference instanceof RuntimeComponentReference)) {
            return;
        }
        RuntimeComponentReference runtimeRef = (RuntimeComponentReference)reference;
        InterfaceContract bindingContract = getInterfaceContract(reference, binding);

        if (!(binding instanceof SCABinding)) {
            InterfaceContract sourceContract = reference.getInterfaceContract();

            // Component Reference --> External Service
            RuntimeWire.Source wireSource = new RuntimeWireImpl.SourceImpl((RuntimeComponent)component,
                                                                           (RuntimeComponentReference)reference,
                                                                           binding, sourceContract);

            RuntimeWire.Target wireTarget = new RuntimeWireImpl.TargetImpl(null, null, binding, bindingContract);
            RuntimeWire wire = new RuntimeWireImpl(wireSource, wireTarget);

            for (Operation operation : sourceContract.getInterface().getOperations()) {
                Operation targetOperation = interfaceContractMapper.map(bindingContract.getInterface(), operation);
                InvocationChain chain = new InvocationChainImpl(operation, targetOperation);
                if (operation.isNonBlocking()) {
                    chain.addInterceptor(new NonBlockingInterceptor(workScheduler));
                }
                addBindingInterceptor(component, reference, binding, chain, operation, false);
                wire.getInvocationChains().add(chain);
            }
            if (sourceContract.getCallbackInterface() != null) {
                for (Operation operation : sourceContract.getCallbackInterface().getOperations()) {
                    Operation targetOperation = interfaceContractMapper.map(bindingContract.getCallbackInterface(),
                                                                            operation);
                    InvocationChain chain = new InvocationChainImpl(operation, targetOperation);
                    if (operation.isNonBlocking()) {
                        chain.addInterceptor(new NonBlockingInterceptor(workScheduler));
                    }
                    addBindingInterceptor(component, reference, binding, chain, operation, true);
                    wire.getCallbackInvocationChains().add(chain);
                }
            }
            runtimeRef.addRuntimeWire(wire);
            wireProcessor.process(wire);
        }
        for (ComponentService service : reference.getTargets()) {
            Component target = null;
            SCABinding scaBinding = service.getBinding(SCABinding.class);
            if (scaBinding != null) {
                target = scaBinding.getComponent();
            }
            
            // FIXME: [rfeng] Ignore unresolved services
            if(service.isUnresolved()) {
                continue;
            }

            // FIXME: [rfeng] We might need a better way to get the impl interface contract
            InterfaceContract targetContract = service.getService().getInterfaceContract();

            RuntimeWire.Source wireSource = new RuntimeWireImpl.SourceImpl((RuntimeComponent)component,
                                                                           (RuntimeComponentReference)reference,
                                                                           binding, bindingContract);

            RuntimeWire.Target wireTarget = new RuntimeWireImpl.TargetImpl((RuntimeComponent)target,
                                                                           (RuntimeComponentService)service, binding,
                                                                           targetContract);

            RuntimeWire wire = new RuntimeWireImpl(wireSource, wireTarget);

            for (Operation operation : bindingContract.getInterface().getOperations()) {
                Operation targetOperation = interfaceContractMapper.map(targetContract.getInterface(), operation);
                InvocationChain chain = new InvocationChainImpl(operation, targetOperation);
                if (operation.isNonBlocking()) {
                    chain.addInterceptor(new NonBlockingInterceptor(workScheduler));
                }
                addBindingInterceptor(component, reference, binding, chain, operation, false);
                if (target != null) {
                    addImplementationInterceptor(target, service, chain, targetOperation, false);
                }
                wire.getInvocationChains().add(chain);
            }
            if (bindingContract.getCallbackInterface() != null) {
                if (reference.getName().startsWith("$self$.")) {
                    // No callback is needed
                    continue;
                }
                for (Operation operation : bindingContract.getCallbackInterface().getOperations()) {
                    Operation targetOperation = interfaceContractMapper.map(targetContract.getCallbackInterface(),
                                                                            operation);
                    InvocationChain chain = new InvocationChainImpl(operation, targetOperation);
                    if (operation.isNonBlocking()) {
                        chain.addInterceptor(new NonBlockingInterceptor(workScheduler));
                    }
                    addBindingInterceptor(component, reference, binding, chain, operation, true);
                    addImplementationInterceptor(component, null, chain, targetOperation, true);
                    wire.getCallbackInvocationChains().add(chain);
                }
            }

            runtimeRef.addRuntimeWire(wire);
            if (!wire.getCallbackInvocationChains().isEmpty()) {
                if (wire.getTarget().getComponentService() != null) {
                    wire.getTarget().getComponentService().addCallbackWire(wire);
                }
            }
            wireProcessor.process(wire);
        }
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
        return interfaceContract;
    }

    /**
     * Create runtime wires for a service binding
     * 
     * @param component
     * @param service
     * @param binding
     */
    private void createWires(Component component, ComponentService service, Binding binding) {
        if (!(service instanceof RuntimeComponentService)) {
            return;
        }
        RuntimeComponentService runtimeService = (RuntimeComponentService)service;

        // FIXME: [rfeng] We might need a better way to get the impl interface contract
        InterfaceContract targetContract = service.getService().getInterfaceContract();

        InterfaceContract sourceContract = getInterfaceContract(service, binding);

        RuntimeWire.Source wireSource = new RuntimeWireImpl.SourceImpl(null, null, binding, sourceContract);

        RuntimeWire.Target wireTarget = new RuntimeWireImpl.TargetImpl((RuntimeComponent)component,
                                                                       (RuntimeComponentService)service, binding,
                                                                       targetContract);

        RuntimeWire wire = new RuntimeWireImpl(wireSource, wireTarget);

        for (Operation operation : sourceContract.getInterface().getOperations()) {
            Operation targetOperation = interfaceContractMapper.map(targetContract.getInterface(), operation);
            InvocationChain chain = new InvocationChainImpl(operation, targetOperation);
            /* lresende */
            if (operation.isNonBlocking()) {
                chain.addInterceptor(new NonBlockingInterceptor(workScheduler));
            }

            addImplementationInterceptor(component, service, chain, targetOperation, false);
            wire.getInvocationChains().add(chain);
        }
        // if (sourceContract.getCallbackInterface() != null) {
        // for (Operation operation :
        // sourceContract.getCallbackInterface().getOperations()) {
        // Operation targetOperation =
        // interfaceContractMapper.map(targetContract.getCallbackInterface(),
        // operation);
        // InvocationChain chain = new InvocationChainImpl(operation,
        // targetOperation);
        // if (operation.isNonBlocking()) {
        // chain.addInterceptor(new NonBlockingInterceptor(workScheduler,
        // workContext));
        // }
        // addImplementationInterceptor(component, service, chain, operation,
        // true);
        // wire.getCallbackInvocationChains().add(chain);
        // }
        // }

        runtimeService.addRuntimeWire(wire);
        wireProcessor.process(wire);
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
                invoker = provider.createInvoker((RuntimeComponentService)service,operation);
            } else {
                invoker = provider.createCallbackInvoker(operation);
            }
            chain.addInvoker(invoker);
        }
    }

    /**
     * Add the interceptor for a binding
     * 
     * @param component
     * @param reference
     * @param binding
     * @param chain
     * @param operation
     * @param isCallback
     */
    private void addBindingInterceptor(Component component,
                                      ComponentReference reference,
                                      Binding binding,
                                      InvocationChain chain,
                                      Operation operation,
                                      boolean isCallback) {
        ReferenceBindingProvider provider = ((RuntimeComponentReference)reference).getBindingProvider(binding);
        if (provider != null) {
            Invoker invoker = provider.createInvoker(operation, isCallback);
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
    
    protected void buildComposite(Composite composite,
                                AssemblyFactory assemblyFactory,
                                InterfaceContractMapper interfaceContractMapper) throws CompositeBuilderException {

        CompositeBuilderMonitor monitor = new CompositeBuilderMonitor() {

            public void problem(Problem problem) {
                // Uncommenting the following two lines can be useful to detect
                // and troubleshoot SCA assembly XML composite configuration
                // problems.

                System.out.println("Composite assembly problem: " + problem.getMessage());
            }
        };

        DefaultCompositeBuilder builder = new DefaultCompositeBuilder(assemblyFactory, interfaceContractMapper, monitor);

        builder.build(composite);

        // if (!problems.isEmpty()) {
        // throw new VariantRuntimeException(new RuntimeException("Problems in
        // the composite..."));
        // }
    }

    public void activate(Composite composite) throws ActivationException {
        try {
            buildComposite(composite, assemblyFactory, interfaceContractMapper);
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

}
