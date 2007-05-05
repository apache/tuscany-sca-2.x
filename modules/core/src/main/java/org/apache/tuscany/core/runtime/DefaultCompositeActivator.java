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

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.assembly.AssemblyFactory;
import org.apache.tuscany.assembly.Base;
import org.apache.tuscany.assembly.Binding;
import org.apache.tuscany.assembly.Component;
import org.apache.tuscany.assembly.ComponentReference;
import org.apache.tuscany.assembly.ComponentService;
import org.apache.tuscany.assembly.Composite;
import org.apache.tuscany.assembly.Implementation;
import org.apache.tuscany.assembly.SCABinding;
import org.apache.tuscany.assembly.util.CompositeUtil;
import org.apache.tuscany.assembly.util.PrintUtil;
import org.apache.tuscany.core.ImplementationActivator;
import org.apache.tuscany.core.ImplementationProvider;
import org.apache.tuscany.core.ReferenceBindingActivator;
import org.apache.tuscany.core.ReferenceBindingProvider;
import org.apache.tuscany.core.RuntimeComponent;
import org.apache.tuscany.core.RuntimeComponentReference;
import org.apache.tuscany.core.RuntimeComponentService;
import org.apache.tuscany.core.RuntimeWire;
import org.apache.tuscany.core.ScopedImplementationProvider;
import org.apache.tuscany.core.ServiceBindingActivator;
import org.apache.tuscany.core.ServiceBindingProvider;
import org.apache.tuscany.core.WireProcessorExtensionPoint;
import org.apache.tuscany.core.wire.InvocationChainImpl;
import org.apache.tuscany.core.wire.NonBlockingInterceptor;
import org.apache.tuscany.interfacedef.IncompatibleInterfaceContractException;
import org.apache.tuscany.interfacedef.InterfaceContract;
import org.apache.tuscany.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.invocation.Interceptor;
import org.apache.tuscany.invocation.InvocationChain;
import org.apache.tuscany.scope.Scope;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.services.work.WorkScheduler;

/**
 * @version $Rev$ $Date$
 */
public class DefaultCompositeActivator implements CompositeActivator {

    private final AssemblyFactory assemblyFactory;
    private final InterfaceContractMapper interfaceContractMapper;
    private final WorkContext workContext;
    private final WorkScheduler workScheduler;
    private final WireProcessorExtensionPoint wireProcessorExtensionPoint;

    /**
     * @param assemblyFactory
     * @param interfaceContractMapper
     * @param workContext
     * @param workScheduler
     * @param wirePostProcessorRegistry
     */
    public DefaultCompositeActivator(AssemblyFactory assemblyFactory,
                                     InterfaceContractMapper interfaceContractMapper,
                                     WorkContext workContext,
                                     WorkScheduler workScheduler,
                                     WireProcessorExtensionPoint wireProcessorExtensionPoint) {
        super();
        this.assemblyFactory = assemblyFactory;
        this.interfaceContractMapper = interfaceContractMapper;
        this.workContext = workContext;
        this.workScheduler = workScheduler;
        this.wireProcessorExtensionPoint = wireProcessorExtensionPoint;
    }

    /**
     * Start a composite
     */
    public void start(Composite composite) {
        for (Component component : composite.getComponents()) {

            for (ComponentService service : component.getServices()) {
                for (Binding binding : service.getBindings()) {
                    if (binding instanceof ServiceBindingActivator) {
                        ServiceBindingActivator bindingActivator = (ServiceBindingActivator)binding;
                        bindingActivator.start(component, service);
                    }
                }
            }
            for (ComponentReference reference : component.getReferences()) {
                for (Binding binding : reference.getBindings()) {
                    if (binding instanceof ReferenceBindingActivator) {
                        ReferenceBindingActivator bindingActivator = (ReferenceBindingActivator)binding;
                        bindingActivator.start(component, reference);
                    }
                }
            }
            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {
                start((Composite)implementation);
            } else if (implementation instanceof ImplementationActivator) {
                ((ImplementationActivator)implementation).start((RuntimeComponent)component);
            }
        }

    }

    /**
     * Configure a composite
     * 
     * @param composite
     */
    public void configure(Composite composite) {
        for (Component component : composite.getComponents()) {

            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {
                configure((Composite)implementation);
            } else if (implementation instanceof ImplementationProvider) {
                ((ImplementationProvider)implementation).configure((RuntimeComponent)component);
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
                    if (binding instanceof ServiceBindingActivator) {
                        ServiceBindingActivator bindingActivator = (ServiceBindingActivator)binding;
                        bindingActivator.stop(component, service);
                    }
                }
            }
            for (ComponentReference reference : component.getReferences()) {
                for (Binding binding : reference.getBindings()) {
                    if (binding instanceof ReferenceBindingActivator) {
                        ReferenceBindingActivator bindingActivator = (ReferenceBindingActivator)binding;
                        bindingActivator.stop(component, reference);
                    }
                }
            }
            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {
                start((Composite)implementation);
            } else if (implementation instanceof ImplementationActivator) {
                ((ImplementationActivator)implementation).stop((RuntimeComponent)component);
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
        InterfaceContract sourceContract = reference.getInterfaceContract();
        if (binding instanceof ReferenceBindingProvider) {
            ReferenceBindingProvider provider = (ReferenceBindingProvider)binding;
            InterfaceContract bindingContract = provider.getBindingInterfaceContract(reference);
            if (bindingContract != null) {
                sourceContract = bindingContract;
            }
        }
        return sourceContract;
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
            wireProcessorExtensionPoint.process(wire);
        }
        for (ComponentService service : reference.getTargets()) {
            Component target = null;
            SCABinding scaBinding = service.getBinding(SCABinding.class);
            if (scaBinding != null) {
                target = scaBinding.getComponent();
            }

            InterfaceContract targetContract = service.getInterfaceContract();

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
                if(reference.getName().startsWith("$self$.")) {
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
            wireProcessorExtensionPoint.process(wire);
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
        InterfaceContract sourceContract = service.getInterfaceContract();

        if (binding instanceof ServiceBindingProvider) {
            ServiceBindingProvider provider = (ServiceBindingProvider)binding;
            InterfaceContract bindingContract = provider.getBindingInterfaceContract(service);
            if (bindingContract != null) {
                sourceContract = bindingContract;
            }
        }
        return sourceContract;
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
//        if (sourceContract.getCallbackInterface() != null) {
//            for (Operation operation : sourceContract.getCallbackInterface().getOperations()) {
//                Operation targetOperation = interfaceContractMapper.map(targetContract.getCallbackInterface(),
//                                                                        operation);
//                InvocationChain chain = new InvocationChainImpl(operation, targetOperation);
//                if (operation.isNonBlocking()) {
//                    chain.addInterceptor(new NonBlockingInterceptor(workScheduler, workContext));
//                }
//                addImplementationInterceptor(component, service, chain, operation, true);
//                wire.getCallbackInvocationChains().add(chain);
//            }
//        }

        runtimeService.addRuntimeWire(wire);
        wireProcessorExtensionPoint.process(wire);
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
        if (component.getImplementation() instanceof ImplementationProvider) {
            ImplementationProvider provider = (ImplementationProvider)component.getImplementation();
            Interceptor interceptor = null;
            if (!isCallback) {
                interceptor = provider.createInterceptor((RuntimeComponent)component, service, operation);
            } else {
                interceptor = provider.createCallbackInterceptor((RuntimeComponent)component, operation);
            }
            chain.addInterceptor(interceptor);
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
        if (binding instanceof ReferenceBindingProvider) {
            ReferenceBindingProvider provider = (ReferenceBindingProvider)binding;
            Interceptor interceptor = provider.createInterceptor(component, reference, operation, isCallback);
            if (interceptor != null) {
                chain.addInterceptor(interceptor);
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
        Implementation impl = component.getImplementation();
        if (impl instanceof ScopedImplementationProvider) {
            ScopedImplementationProvider provider = (ScopedImplementationProvider)impl;
            Scope scope = provider.getScope();
            if (scope == null) {
                return Scope.STATELESS;
            }
        }
        return Scope.STATELESS;
    }

    private void optimize(Component source, Component target, RuntimeWire wire) {
        boolean optimizableScopes = isOptimizable(getScope(source), getScope(target));
        if (optimizableScopes && isOptimizable(wire)) {
            // wire.setOptimizable(true);
        } else {
            // wire.setOptimizable(false);
        }
    }

    /**
     * Determines if the given wire is optimizable, i.e. its invocation chains
     * may be bypassed during an invocation. This is typically calculated during
     * the connect phase to optimize away invocation chains.
     * 
     * @param wire the wire
     * @return true if the wire is optimizable
     */
    public static boolean isOptimizable(RuntimeWire wire) {
        for (InvocationChain chain : wire.getInvocationChains()) {
            if (chain.getHeadInterceptor() != null) {
                Interceptor current = chain.getHeadInterceptor();
                if (current == null) {
                    break;
                }
                while (current != null) {
                    if (!current.isOptimizable()) {
                        return false;
                    }
                    current = current.getNext();
                }
            }
        }
        // if there is a callback, the wire is never optimizable since the
        // callback target needs to be disambiguated
        return wire.getCallbackInvocationChains().isEmpty();
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

    private void wire(Composite composite,
                      AssemblyFactory assemblyFactory,
                      InterfaceContractMapper interfaceContractMapper) {
        CompositeUtil compositeUtil = new CompositeUtil(assemblyFactory, interfaceContractMapper);

        List<Base> problems = new ArrayList<Base>() {
            private static final long serialVersionUID = 4819831446590718923L;

            @Override
            public boolean add(Base o) {
                // TODO Use a monitor to report configuration problems

                // Uncommenting the following two lines can be useful to detect
                // and troubleshoot SCA assembly XML composite configuration
                // problems.

                System.err.println("Composite configuration problem:");
                new PrintUtil(System.err).print(o);
                return super.add(o);
            }
        };

        compositeUtil.configureAndWire(composite, problems);

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
    public void activate(Composite composite) throws IncompatibleInterfaceContractException {
        wire(composite, assemblyFactory, interfaceContractMapper);
        configure(composite);
        createRuntimeWires(composite);
        start(composite);
    }

}
