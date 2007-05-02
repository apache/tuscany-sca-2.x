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

import java.lang.reflect.Method;
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
import org.apache.tuscany.assembly.Multiplicity;
import org.apache.tuscany.assembly.SCABinding;
import org.apache.tuscany.assembly.util.CompositeUtil;
import org.apache.tuscany.assembly.util.PrintUtil;
import org.apache.tuscany.core.ImplementationActivator;
import org.apache.tuscany.core.ImplementationProvider;
import org.apache.tuscany.core.ReferenceBindingActivator;
import org.apache.tuscany.core.ReferenceBindingProvider;
import org.apache.tuscany.core.RuntimeComponent;
import org.apache.tuscany.core.RuntimeComponentReference;
import org.apache.tuscany.core.RuntimeWire;
import org.apache.tuscany.core.ServiceBindingActivator;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.util.JavaIntrospectionHelper;
import org.apache.tuscany.core.wire.InvocationChainImpl;
import org.apache.tuscany.core.wire.NonBlockingInterceptor;
import org.apache.tuscany.core.work.Jsr237WorkScheduler;
import org.apache.tuscany.core.work.ThreadPoolWorkManager;
import org.apache.tuscany.interfacedef.IncompatibleInterfaceContractException;
import org.apache.tuscany.interfacedef.InterfaceContract;
import org.apache.tuscany.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.spi.Scope;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.services.work.WorkScheduler;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.WirePostProcessorRegistry;

/**
 * @version $Rev$ $Date$
 */
public class DefaultCompositeActivator implements CompositeActivator {

    private AssemblyFactory assemblyFactory;
    private InterfaceContractMapper interfaceContractMapper;
    private WorkContext workContext = new WorkContextImpl();
    private WorkScheduler workScheduler = new Jsr237WorkScheduler(new ThreadPoolWorkManager(10));
    private WirePostProcessorRegistry wirePostProcessorRegistry;

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
                                     WirePostProcessorRegistry wirePostProcessorRegistry) {
        super();
        this.assemblyFactory = assemblyFactory;
        this.interfaceContractMapper = interfaceContractMapper;
        this.workContext = workContext;
        this.workScheduler = workScheduler;
        this.wirePostProcessorRegistry = wirePostProcessorRegistry;
    }

    public static <T> T getContract(Object target, Class<T> interfaceClass) {
        if (interfaceClass.isInstance(target)) {
            return interfaceClass.cast(target);
        } else {
            try {
                String methodName = JavaIntrospectionHelper.toGetter(interfaceClass.getSimpleName());
                Method method = target.getClass().getMethod(methodName, new Class[] {});
                return interfaceClass.cast(method.invoke(target, new Object[] {}));
            } catch (Exception e) {
                return null;
            }
        }
    }

    public void start(Composite composite) {
        for (Component component : composite.getComponents()) {

            for (ComponentService service : component.getServices()) {
                for (Binding binding : service.getBindings()) {
                    if(binding instanceof ServiceBindingActivator) {
                        ServiceBindingActivator bindingActivator = (ServiceBindingActivator) binding;
                        bindingActivator.start(component, service);
                    }
                }
            }
            for (ComponentReference reference : component.getReferences()) {
                for (Binding binding : reference.getBindings()) {
                    if(binding instanceof ReferenceBindingActivator) {
                        ReferenceBindingActivator bindingActivator = (ReferenceBindingActivator) binding;
                        bindingActivator.start(component, reference);
                    }
                }
            }
            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {
                start((Composite)implementation);
            } else if(implementation instanceof ImplementationActivator) {
                ((ImplementationActivator) implementation).start((RuntimeComponent) component); 
            }
        }

    }

    public void stop(Composite composite) {
        for (Component component : composite.getComponents()) {

            for (ComponentService service : component.getServices()) {
                for (Binding binding : service.getBindings()) {
                    if(binding instanceof ServiceBindingActivator) {
                        ServiceBindingActivator bindingActivator = (ServiceBindingActivator) binding;
                        bindingActivator.stop(component, service);
                    }
                }
            }
            for (ComponentReference reference : component.getReferences()) {
                for (Binding binding : reference.getBindings()) {
                    if(binding instanceof ReferenceBindingActivator) {
                        ReferenceBindingActivator bindingActivator = (ReferenceBindingActivator) binding;
                        bindingActivator.stop(component, reference);
                    }
                }
            }
            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {
                start((Composite)implementation);
            } else if(implementation instanceof ImplementationActivator) {
                ((ImplementationActivator) implementation).stop((RuntimeComponent) component); 
            }
        }

    }    
    public void createRuntimeWires(Composite composite) throws IncompatibleInterfaceContractException {
        for (Component component : composite.getComponents()) {

            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {
                createRuntimeWires((Composite)implementation);
            } else {
                // createSelfReferences(component);
                for (ComponentReference reference : component.getReferences()) {
                    for (Binding binding : reference.getBindings()) {
                        createWires(component, reference, binding);
                    }
                }
            }
        }
    }

    public void createSelfReferences(Component component) {
        for (ComponentService service : component.getServices()) {
            ComponentReference ref = assemblyFactory.createComponentReference();
            ref.setName("$self$_" + service.getName());
            ref.getBindings().addAll(service.getBindings());
            ref.getTargets().add(service);
            ref.getPolicySets().addAll(service.getPolicySets());
            ref.getRequiredIntents().addAll(service.getRequiredIntents());
            ref.setInterfaceContract(service.getInterfaceContract());
            ref.setMultiplicity(Multiplicity.ONE_ONE);
            component.getReferences().add(ref);
        }
    }

    private void createWires(Component component, ComponentReference reference, Binding binding) {
        if (!(binding instanceof ReferenceBindingProvider)) {
            return;
        }

        RuntimeComponentReference wireSource = (RuntimeComponentReference)reference;
        ReferenceBindingProvider provider = (ReferenceBindingProvider)binding;
        for (ComponentService service : reference.getTargets()) {
            // FIXME: Need a way to find the owning component of a component
            // service
            Component target = null;
            SCABinding scaBinding = service.getBinding(SCABinding.class);
            if (scaBinding != null) {
                target = scaBinding.getComponent();
            }

            RuntimeWire wire = new RuntimeWireImpl(reference, service);

            InterfaceContract sourceContract = provider.getBindingInterfaceContract(reference);
            InterfaceContract targetContract = service.getInterfaceContract();
            for (Operation operation : sourceContract.getInterface().getOperations()) {
                Operation targetOperation = interfaceContractMapper.map(targetContract.getInterface(), operation);
                InvocationChain chain = new InvocationChainImpl(operation, targetOperation);
                /* lresende */
                if (operation.isNonBlocking()) {
                    chain.addInterceptor(new NonBlockingInterceptor(workScheduler, workContext));
                }

                addBindingIntercepor(component, reference, binding, chain, operation, false);

                if (target != null) {
                    addImplementationInterceptor(target, service, chain, operation, false);
                }
                wire.getInvocationChains().add(chain);
            }
            if (sourceContract.getCallbackInterface() != null) {
                for (Operation operation : sourceContract.getCallbackInterface().getOperations()) {
                    Operation targetOperation = interfaceContractMapper.map(targetContract.getCallbackInterface(),
                                                                            operation);
                    InvocationChain chain = new InvocationChainImpl(operation, targetOperation);
                    /* lresende */
                    if (operation.isNonBlocking()) {
                        chain.addInterceptor(new NonBlockingInterceptor(workScheduler, workContext));
                    }
                    addBindingIntercepor(component, reference, binding, chain, operation, true);
                    addImplementationInterceptor(target, service, chain, operation, false);
                    wire.getCallbackInvocationChains().add(chain);
                }
            }

            wireSource.addRuntimeWire(wire);
        }
    }

    private void addImplementationInterceptor(Component component,
                                              ComponentService service,
                                              InvocationChain chain,
                                              Operation operation,
                                              boolean isCallback) {
        if (component.getImplementation() instanceof ImplementationProvider) {
            ImplementationProvider factory = (ImplementationProvider)component.getImplementation();
            Interceptor interceptor = factory.createInterceptor((RuntimeComponent)component,
                                                                service,
                                                                operation,
                                                                isCallback);
            chain.addInterceptor(interceptor);
        }
    }

    private void addBindingIntercepor(Component component,
                                      ComponentReference reference,
                                      Binding binding,
                                      InvocationChain chain,
                                      Operation operation,
                                      boolean isCallback) {
        if (binding instanceof ReferenceBindingProvider) {
            ReferenceBindingProvider factory = (ReferenceBindingProvider)binding;
            Interceptor interceptor = factory.createInterceptor(component, reference, operation, isCallback);
            if (interceptor != null) {
                chain.addInterceptor(interceptor);
            }
        }
    }

    private Scope getScope(Component component) {
        Implementation impl = component.getImplementation();
        if (impl instanceof ImplementationProvider) {
            ImplementationProvider provider = (ImplementationProvider)impl;
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

        // Collect and fuse includes
        compositeUtil.fuseIncludes(composite, problems);

        // Expand nested composites
        compositeUtil.expandComposites(composite, problems);

        // Configure all components
        compositeUtil.configureComponents(composite, problems);

        // Wire the composite
        compositeUtil.wireComposite(composite, problems);

        // Activate composite services
        compositeUtil.activateCompositeServices(composite, problems);

        // Wire composite references
        compositeUtil.wireCompositeReferences(composite, problems);

        // if (!problems.isEmpty()) {
        // throw new VariantRuntimeException(new RuntimeException("Problems in
        // the composite..."));
        // }
    }

    public void activate(Composite composite) throws IncompatibleInterfaceContractException {
        wire(composite, assemblyFactory, interfaceContractMapper);
        createRuntimeWires(composite);
        start(composite);
    }

}
