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

package org.apache.tuscany.core.runtime;

import org.apache.tuscany.assembly.AssemblyFactory;
import org.apache.tuscany.assembly.Binding;
import org.apache.tuscany.assembly.Component;
import org.apache.tuscany.assembly.ComponentReference;
import org.apache.tuscany.assembly.ComponentService;
import org.apache.tuscany.assembly.Composite;
import org.apache.tuscany.assembly.Implementation;
import org.apache.tuscany.assembly.SCABinding;
import org.apache.tuscany.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.assembly.builder.CompositeBuilderMonitor;
import org.apache.tuscany.assembly.builder.Problem;
import org.apache.tuscany.assembly.builder.impl.DefaultCompositeBuilder;
import org.apache.tuscany.core.RuntimeComponent;
import org.apache.tuscany.core.RuntimeComponentReference;
import org.apache.tuscany.core.RuntimeComponentService;
import org.apache.tuscany.core.RuntimeWire;
import org.apache.tuscany.core.RuntimeWireProcessor;
import org.apache.tuscany.core.invocation.InvocationChainImpl;
import org.apache.tuscany.core.invocation.NonBlockingInterceptor;
import org.apache.tuscany.interfacedef.IncompatibleInterfaceContractException;
import org.apache.tuscany.interfacedef.InterfaceContract;
import org.apache.tuscany.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.invocation.InvocationChain;
import org.apache.tuscany.invocation.Invoker;
import org.apache.tuscany.provider.BindingProviderFactory;
import org.apache.tuscany.provider.ImplementationActivator;
import org.apache.tuscany.provider.ImplementationProvider;
import org.apache.tuscany.provider.ImplementationProviderFactory;
import org.apache.tuscany.provider.ReferenceBindingActivator;
import org.apache.tuscany.provider.ReferenceBindingProvider;
import org.apache.tuscany.provider.ScopedImplementationProvider;
import org.apache.tuscany.provider.ServiceBindingActivator;
import org.apache.tuscany.provider.ServiceBindingProvider;
import org.apache.tuscany.scope.Scope;
import org.apache.tuscany.scope.ScopeRegistry;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.work.WorkScheduler;

/**
 * @version $Rev$ $Date$
 */
public class DefaultCompositeActivator implements CompositeActivator {

    private final AssemblyFactory assemblyFactory;
    private final InterfaceContractMapper interfaceContractMapper;
    private final ScopeRegistry scopeRegistry;
    private final WorkContext workContext;
    private final WorkScheduler workScheduler;
    private final RuntimeWireProcessor wireProcessor;

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
                                     WorkContext workContext,
                                     WorkScheduler workScheduler,
                                     RuntimeWireProcessor wireProcessor) {
        super();
        this.assemblyFactory = assemblyFactory;
        this.interfaceContractMapper = interfaceContractMapper;
        this.scopeRegistry = scopeRegistry;
        this.workContext = workContext;
        this.workScheduler = workScheduler;
        this.wireProcessor = wireProcessor;
    }

    /**
     * Start a composite
     */
    protected void startComposite(Composite composite) {
        for (Component component : composite.getComponents()) {
            
            for (ComponentService service : component.getServices()) {
                for (Binding binding : service.getBindings()) {
                    if (binding instanceof BindingProviderFactory) {
                        ServiceBindingActivator bindingActivator = ((BindingProviderFactory)binding).createServiceBindingProvider();
                        if (bindingActivator != null) {
                            bindingActivator.start((RuntimeComponent)component, (RuntimeComponentService)service);
                        }
                    }
                }
            }
            for (ComponentReference reference : component.getReferences()) {
                for (Binding binding : reference.getBindings()) {
                    if (binding instanceof BindingProviderFactory) {
                        ReferenceBindingActivator bindingActivator = ((BindingProviderFactory)binding).createReferenceBindingProvider();
                        if (bindingActivator != null) {
                            bindingActivator.start((RuntimeComponent)component, (RuntimeComponentReference)reference);
                        }
                    }
                }
            }
            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {
                startComposite((Composite)implementation);
            } else if (implementation instanceof ImplementationProviderFactory) {
                ImplementationActivator implementationActivator = ((RuntimeComponent)component).getImplementationProvider();
                if (implementationActivator != null) {
                    implementationActivator.start((RuntimeComponent)component);
                }
            }

            if (component instanceof RuntimeComponent) {
                RuntimeComponent runtimeComponent = (RuntimeComponent)component;
                if (runtimeComponent.getScopeContainer() != null) {
                    runtimeComponent.getScopeContainer().start();
                }
            }
            
        }
    }

    /**
     * Configure a composite
     * 
     * @param composite
     * @throws IncompatibleInterfaceContractException 
     */
    protected void configureComposite(Composite composite) throws IncompatibleInterfaceContractException {
        for (Component component : composite.getComponents()) {

            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {
                configureComposite((Composite)implementation);
            } else if (implementation instanceof ImplementationProviderFactory) {
                ImplementationProvider implementationProvider = ((ImplementationProviderFactory)implementation).createImplementationProvider();
                if (implementationProvider != null) {
                    ((RuntimeComponent)component).setImplementationProvider(implementationProvider);
                    implementationProvider.configure((RuntimeComponent)component);
                }
                setScopeContainer(component);
            }
        }
    }

    /**
     * Stop a composite
     */
    public void stop(Composite composite) {
        for (Component component : composite.getComponents()) {
            
            for (ComponentService service : component.getServices()) {
                for (Binding binding : service.getBindings()) {
                    if (binding instanceof BindingProviderFactory) {
                        ServiceBindingActivator bindingActivator = ((BindingProviderFactory)binding).createServiceBindingProvider();
                        if (bindingActivator != null) {
                            bindingActivator.stop((RuntimeComponent)component, (RuntimeComponentService)service);
                        }
                    }
                }
            }
            for (ComponentReference reference : component.getReferences()) {
                for (Binding binding : reference.getBindings()) {
                    if (binding instanceof BindingProviderFactory) {
                        ReferenceBindingActivator bindingActivator = ((BindingProviderFactory)binding).createReferenceBindingProvider();
                        if (bindingActivator != null) {
                            bindingActivator.stop((RuntimeComponent)component, (RuntimeComponentReference)reference);
                        }
                    }
                }
            }
            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {
                startComposite((Composite)implementation);
            } else if (implementation instanceof ImplementationProviderFactory) {
                ImplementationActivator implementationActivator = ((RuntimeComponent)component).getImplementationProvider();
                if (implementationActivator != null) {
                    implementationActivator.stop((RuntimeComponent)component);
                }
            }

            if (component instanceof RuntimeComponent) {
                RuntimeComponent runtimeComponent = (RuntimeComponent)component;
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
        if (binding instanceof BindingProviderFactory) {
            ReferenceBindingProvider provider = ((BindingProviderFactory)binding).createReferenceBindingProvider();
            if (provider != null) {
                InterfaceContract bindingContract = provider
                    .getBindingInterfaceContract((RuntimeComponentReference)reference);
                if (bindingContract != null) {
                    interfaceContract = bindingContract;
                }
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
                    chain.addInterceptor(new NonBlockingInterceptor(workScheduler, workContext));
                }
                addBindingIntercepor(component, reference, binding, chain, operation, false);
                wire.getInvocationChains().add(chain);
            }
            if (sourceContract.getCallbackInterface() != null) {
                for (Operation operation : sourceContract.getCallbackInterface().getOperations()) {
                    Operation targetOperation = interfaceContractMapper.map(bindingContract.getCallbackInterface(),
                                                                            operation);
                    InvocationChain chain = new InvocationChainImpl(operation, targetOperation);
                    if (operation.isNonBlocking()) {
                        chain.addInterceptor(new NonBlockingInterceptor(workScheduler, workContext));
                    }
                    addBindingIntercepor(component, reference, binding, chain, operation, true);
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

            InterfaceContract targetContract = getInterfaceContract(target, service);

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
                    chain.addInterceptor(new NonBlockingInterceptor(workScheduler, workContext));
                }
                addBindingIntercepor(component, reference, binding, chain, operation, false);
                if (target != null) {
                    addImplementationInterceptor(target, service, chain, operation, false);
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
                        chain.addInterceptor(new NonBlockingInterceptor(workScheduler, workContext));
                    }
                    addBindingIntercepor(component, reference, binding, chain, operation, true);
                    addImplementationInterceptor(component, null, chain, operation, true);
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

        if (binding instanceof BindingProviderFactory) {
            ServiceBindingProvider provider = ((BindingProviderFactory)binding).createServiceBindingProvider();
            if (provider != null) {
                InterfaceContract bindingContract = provider.getBindingInterfaceContract((RuntimeComponentService)service);
                if (bindingContract != null) {
                    interfaceContract = bindingContract;
                }
            }
        }
        return interfaceContract;
    }

    private InterfaceContract getInterfaceContract(Component component, ComponentService service) {
        InterfaceContract interfaceContract = service.getInterfaceContract();

        Implementation implementation = component != null ? component.getImplementation() : null;
        if (implementation instanceof ImplementationProviderFactory) {
            ImplementationProvider implementationProvider = ((RuntimeComponent)component).getImplementationProvider();
            if (implementationProvider != null) {
                InterfaceContract implementationContract = implementationProvider.getImplementationInterfaceContract((RuntimeComponentService)service);
                if (implementationContract != null) {
                    interfaceContract = implementationContract;
                }
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

        InterfaceContract targetContract = service.getInterfaceContract();
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
                chain.addInterceptor(new NonBlockingInterceptor(workScheduler, workContext));
            }

            addImplementationInterceptor(component, service, chain, operation, false);
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
        Implementation implementation = component.getImplementation();
        if (implementation instanceof ImplementationProviderFactory) {
            ImplementationProvider provider = ((RuntimeComponent)component).getImplementationProvider();
            if (provider != null) {
                Invoker invoker = null;
                if (!isCallback) {
                    invoker = provider.createInvoker((RuntimeComponent)component,
                                                             (RuntimeComponentService)service,
                                                             operation);
                } else {
                    invoker = provider.createCallbackInvoker((RuntimeComponent)component, operation);
                }
                chain.addInvoker(invoker);
            }
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
    private void addBindingIntercepor(Component component,
                                      ComponentReference reference,
                                      Binding binding,
                                      InvocationChain chain,
                                      Operation operation,
                                      boolean isCallback) {
        if (binding instanceof BindingProviderFactory) {
            ReferenceBindingProvider provider = ((BindingProviderFactory)binding).createReferenceBindingProvider();
            if (provider != null) {
                Invoker invoker = provider.createInvoker((RuntimeComponent)component,
                                                                     (RuntimeComponentReference)reference,
                                                                     operation,
                                                                     isCallback);
                if (invoker != null) {
                    chain.addInvoker(invoker);
                }
            }
        }
    }

    /**
     * Get the scope for a component
     * 
     * @param component
     * @return
     */
    private Scope getScope(Component component) {
        ImplementationProvider implementationProvider = ((RuntimeComponent)component).getImplementationProvider();
        if (implementationProvider instanceof ScopedImplementationProvider) {
            ScopedImplementationProvider provider = (ScopedImplementationProvider)implementationProvider;
            Scope scope = provider.getScope();
            if (scope == null) {
                return Scope.STATELESS;
            }
        }
        return Scope.STATELESS;
    }
    
    private void setScopeContainer(Component component) {
        if (!(component instanceof RuntimeComponent)) {
            return;
        }
        RuntimeComponent runtimeComponent = (RuntimeComponent)component;
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

    /**
     * Activate a composite
     * 
     * @param composite
     * @throws IncompatibleInterfaceContractException
     */
    public void start(Composite composite) throws ActivationException {
        try {
            buildComposite(composite, assemblyFactory, interfaceContractMapper);
            configureComposite(composite);
            createRuntimeWires(composite);
            startComposite(composite);
        } catch (Exception e) {
            throw new ActivationException(e);
        }
    }

}
