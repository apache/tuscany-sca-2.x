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
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.WireableBinding;
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
    private void addRuntimeProviders(Composite composite) throws IncompatibleInterfaceContractException {
        for (Component component : composite.getComponents()) {

            for (ComponentService service : component.getServices()) {
                addServiceBindingProviders((RuntimeComponent)component, (RuntimeComponentService)service, service
                    .getBindings());
                if (service.getCallback() != null) {
                    addServiceBindingProviders((RuntimeComponent)component,
                                                  (RuntimeComponentService)service,
                                                  service.getCallback().getBindings());
                }
            }

            for (ComponentReference reference : component.getReferences()) {
                addReferenceBindingProviders((RuntimeComponent)component,
                                                (RuntimeComponentReference)reference,
                                                reference.getBindings());
                if (reference.getCallback() != null) {
                    addReferenceBindingProviders((RuntimeComponent)component,
                                                    (RuntimeComponentReference)reference,
                                                    reference.getCallback().getBindings());
                }
            }

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
                if (service.getCallback() != null) {
                    removeServiceBindingProviders((RuntimeComponent)component,
                                                  (RuntimeComponentService)service,
                                                  service.getCallback().getBindings());
                }
            }

            for (ComponentReference reference : component.getReferences()) {
                removeReferenceBindingProviders((RuntimeComponent)component,
                                                (RuntimeComponentReference)reference,
                                                reference.getBindings());
                if (reference.getCallback() != null) {
                    removeReferenceBindingProviders((RuntimeComponent)component,
                                                    (RuntimeComponentReference)reference,
                                                    reference.getCallback().getBindings());
                }
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
            throw new IllegalStateException("Provider factory not found for class: " + implementation.getClass().getName());
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
    }

    private void removeServiceBindingProviders(RuntimeComponent component,
                                                 RuntimeComponentService service,
                                                 List<Binding> bindings) {
          for (Binding binding : bindings) {
              ((RuntimeComponentService)service).setBindingProvider(binding, null);
          }
      }

    private void addReferenceBindingProviders(RuntimeComponent component,
                                                 RuntimeComponentReference reference,
                                                 List<Binding> bindings) {
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
            start((Composite)implementation);
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
    public void stop(Component component) {
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
                // Create outbound wires for the component references
                for (ComponentReference reference : component.getReferences()) {
                    for (Binding binding : reference.getBindings()) {
                        addReferenceWires(component, reference, binding, false);
                    }
                    if (reference.getCallback() != null) {
                        for (Binding binding : reference.getCallback().getBindings()) {
                            addReferenceWires(component, reference, binding, true);
                        }
                    }
                }
                // Create inbound wires for the component services
                for (ComponentService service : component.getServices()) {
                    for (Binding binding : service.getBindings()) {
                        addServiceWires(component, service, binding, false);
                    }
                    if (service.getCallback() != null) {
                        for (Binding binding : service.getCallback().getBindings()) {
                            if (binding instanceof WireableBinding) {
                                if (((WireableBinding)binding).getTargetComponent() != null) {
                                    continue;
                                }
                            }
                            addServiceWires(component, service, binding, true);
                        }
                    }
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
    private void removeRuntimeWires(Composite composite) throws IncompatibleInterfaceContractException {
        for (Component component : composite.getComponents()) {
            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {
                // Recursively remove runtime wires
                removeRuntimeWires((Composite)implementation);
            } else {
                // Remove outbound wires for the component references
                for (ComponentReference reference : component.getReferences()) {
                    for (Binding binding : reference.getBindings()) {
                        removeReferenceWires(component, reference, binding, false);
                    }
                    if (reference.getCallback() != null) {
                        for (Binding binding : reference.getCallback().getBindings()) {
                            removeReferenceWires(component, reference, binding, true);
                        }
                    }
                }
                // Remove inbound wires for the component services
                for (ComponentService service : component.getServices()) {
                    for (Binding binding : service.getBindings()) {
                        removeServiceWires(service, binding, false);
                    }
                    if (service.getCallback() != null) {
                        for (Binding binding : service.getCallback().getBindings()) {
                            if (binding instanceof WireableBinding) {
                                if (((WireableBinding)binding).getTargetComponent() != null) {
                                    continue;
                                }
                            }
                            removeServiceWires(service, binding, true);
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
    private void addReferenceWires(Component component, ComponentReference reference, Binding binding, boolean isCallback) {
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
                addReferenceWire(reference,
                                    component,
                                    binding,
                                    targetComponentService,
                                    targetComponent,
                                    targetBinding,
                                    isCallback);
            } else {
                addReferenceWire(reference, component, binding, null, null, binding, true);
                if (targetComponentService != null) {
                    Binding serviceBinding = targetComponentService.getCallbackBinding(binding.getClass());
                    if (serviceBinding != null) {
                        addServiceWire(targetComponentService,
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
     * Create the runtime wires for a reference binding
     * 
     * @param component
     * @param reference
     * @param binding
     * @param isCallback
     */
    private void removeReferenceWires(Component component, ComponentReference reference, Binding binding, boolean isCallback) {
        if (!(reference instanceof RuntimeComponentReference)) {
            return;
        }
        if ((!(binding instanceof WireableBinding)) || binding.getURI() != null || isCallback) {
            // create wire if binding has an endpoint
            ComponentService targetComponentService = null;
            if (!isCallback) {
                removeReferenceWire(reference);
            } else {
                removeReferenceWire(reference);
                if (targetComponentService != null) {
                    Binding serviceBinding = targetComponentService.getCallbackBinding(binding.getClass());
                    if (serviceBinding != null) {
                        removeServiceWire(targetComponentService);
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
    private RuntimeWire addReferenceWire(ComponentReference reference,
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
     * Remove a reference wire for a forward call or a callback
     * 
     * @param reference
     */
    private void removeReferenceWire(ComponentReference reference) {

        RuntimeComponentReference runtimeRef = (RuntimeComponentReference)reference;
        runtimeRef.getRuntimeWires().clear();
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
    private void addServiceWires(Component component, ComponentService service, Binding binding, boolean isCallback) {
        if (!(service instanceof RuntimeComponentService)) {
            return;
        }
        RuntimeWire wire = addServiceWire(service, component, binding, null, null, binding, isCallback);
    
        //FIXME: need to decide if this is the best way to create the source URI
        // The source URI is used by JDKCallbackInvocationHandler to find the callback wire
        // corresponding to the forward wire that was used to invoke the service.
        // This only works if the source URI is the same for the matched pair of forward and
        // callback wires.  The binding name seems a reasonable key to use for this match,
        // as it allows the user to control which callback binding should be selected.
        wire.getSource().setURI(binding.getName());
    }

    /**
     * Remove runtime wires for a service binding
     * 
     * @param component
     * @param service
     * @param binding
     * @param isCallback
     */
    private void removeServiceWires(ComponentService service, Binding binding, boolean isCallback) {
        if (!(service instanceof RuntimeComponentService)) {
            return;
        }
        removeServiceWire(service);
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
    private RuntimeWire addServiceWire(ComponentService service,
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
     * Remove a service wire for a forward call or a callback
     * 
     * @param service
     */
    private void removeServiceWire(ComponentService service) {
        RuntimeComponentService runtimeService = (RuntimeComponentService)service;

        runtimeService.getRuntimeWires().clear();
        runtimeService.getCallbackWires().clear();
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

}
