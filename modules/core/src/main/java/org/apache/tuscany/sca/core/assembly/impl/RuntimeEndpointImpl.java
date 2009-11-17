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

package org.apache.tuscany.sca.core.assembly.impl;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.CompositeReference;
import org.apache.tuscany.sca.assembly.CompositeService;
import org.apache.tuscany.sca.assembly.Contract;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.impl.EndpointImpl;
import org.apache.tuscany.sca.context.CompositeContext;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.core.invocation.ExtensibleWireProcessor;
import org.apache.tuscany.sca.core.invocation.NonBlockingInterceptor;
import org.apache.tuscany.sca.core.invocation.RuntimeInvoker;
import org.apache.tuscany.sca.core.invocation.impl.InvocationChainImpl;
import org.apache.tuscany.sca.core.invocation.impl.PhaseManager;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.invocation.Phase;
import org.apache.tuscany.sca.provider.BindingPolicyProvider;
import org.apache.tuscany.sca.provider.BindingProviderFactory;
import org.apache.tuscany.sca.provider.EndpointProvider;
import org.apache.tuscany.sca.provider.ImplementationProvider;
import org.apache.tuscany.sca.provider.PolicyProvider;
import org.apache.tuscany.sca.provider.PolicyProviderFactory;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.EndpointRegistry;
import org.apache.tuscany.sca.runtime.EndpointSerializer;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.apache.tuscany.sca.runtime.RuntimeWireProcessor;
import org.apache.tuscany.sca.runtime.RuntimeWireProcessorExtensionPoint;
import org.apache.tuscany.sca.work.WorkScheduler;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 * Runtime model for Endpoint that supports java serialization
 */
public class RuntimeEndpointImpl extends EndpointImpl implements RuntimeEndpoint {
    private transient CompositeContext compositeContext;
    private transient EndpointRegistry endpointRegistry;
    private transient RuntimeWireProcessor wireProcessor;
    private transient InterfaceContractMapper interfaceContractMapper;
    private transient WorkScheduler workScheduler;
    private transient PhaseManager phaseManager;
    private transient MessageFactory messageFactory;
    private transient RuntimeInvoker invoker;
    private transient ProviderFactoryExtensionPoint providerFactories;

    private transient List<InvocationChain> chains;
    private transient final Map<Operation, InvocationChain> invocationChainMap =
        new ConcurrentHashMap<Operation, InvocationChain>();
    private transient InvocationChain bindingInvocationChain;

    private transient ServiceBindingProvider bindingProvider;
    private transient List<PolicyProvider> policyProviders;
    private transient EndpointSerializer serializer;
    private String xml;

    protected InterfaceContract bindingInterfaceContract;
    protected InterfaceContract serviceInterfaceContract;

    /**
     * No-arg constructor for Java serilization
     */
    public RuntimeEndpointImpl() {
        super(null);
    }

    public RuntimeEndpointImpl(ExtensionPointRegistry registry) {
        super(registry);
    }

    public void bind(CompositeContext compositeContext) {
        this.compositeContext = compositeContext;
        bind(compositeContext.getExtensionPointRegistry(), compositeContext.getEndpointRegistry());
    }

    public void bind(ExtensionPointRegistry registry, EndpointRegistry endpointRegistry) {
        if (compositeContext == null) {
            compositeContext = new CompositeContext(registry, endpointRegistry);
        }
        this.registry = registry;
        this.endpointRegistry = endpointRegistry;
        UtilityExtensionPoint utilities = registry.getExtensionPoint(UtilityExtensionPoint.class);
        this.interfaceContractMapper = utilities.getUtility(InterfaceContractMapper.class);
        this.workScheduler = utilities.getUtility(WorkScheduler.class);
        this.wireProcessor =
            new ExtensibleWireProcessor(registry.getExtensionPoint(RuntimeWireProcessorExtensionPoint.class));

        this.messageFactory = registry.getExtensionPoint(FactoryExtensionPoint.class).getFactory(MessageFactory.class);
        this.invoker = new RuntimeInvoker(this.messageFactory, this);

        this.phaseManager = utilities.getUtility(PhaseManager.class);
        this.serializer = utilities.getUtility(EndpointSerializer.class);
        this.providerFactories = registry.getExtensionPoint(ProviderFactoryExtensionPoint.class);
    }

    public void unbind() {
        compositeContext = null;
        bindingInvocationChain = null;
        chains = null;
        bindingProvider = null;
        policyProviders = null;
        invocationChainMap.clear();
    }

    public synchronized List<InvocationChain> getInvocationChains() {
        if (chains == null) {
            initInvocationChains();
        }
        return chains;
    }

    public synchronized InvocationChain getBindingInvocationChain() {
        if (bindingInvocationChain == null) {
            bindingInvocationChain = new InvocationChainImpl(null, null, false, phaseManager);
            initServiceBindingInvocationChains();
        }
        return bindingInvocationChain;
    }

    public InvocationChain getInvocationChain(Operation operation) {
        InvocationChain cached = invocationChainMap.get(operation);
        if (cached == null) {
            for (InvocationChain chain : getInvocationChains()) {
                Operation op = chain.getTargetOperation();

                if (interfaceContractMapper.isCompatible(operation, op, op.getInterface().isRemotable())) {
                    invocationChainMap.put(operation, chain);
                    return chain;
                }
            }
            invocationChainMap.put(operation, null);
            return null;
        } else {
            return cached;
        }
    }

    public Message invoke(Message msg) {
        return invoker.invokeBinding(msg);
    }

    public Object invoke(Operation operation, Object[] args) throws InvocationTargetException {
        return invoker.invoke(operation, args);
    }

    public Message invoke(Operation operation, Message msg) {
        return invoker.invoke(operation, msg);
    }

    /**
     * Navigate the component/componentType inheritence chain to find the leaf contract
     * @param contract
     * @return
     */
    private Contract getLeafContract(Contract contract) {
        Contract prev = null;
        Contract current = contract;
        while (current != null) {
            prev = current;
            if (current instanceof ComponentReference) {
                current = ((ComponentReference)current).getReference();
            } else if (current instanceof CompositeReference) {
                current = ((CompositeReference)current).getPromotedReferences().get(0);
            } else if (current instanceof ComponentService) {
                current = ((ComponentService)current).getService();
            } else if (current instanceof CompositeService) {
                current = ((CompositeService)current).getPromotedService();
            } else {
                break;
            }
            if (current == null) {
                return prev;
            }
        }
        return current;
    }

    /**
     * Initialize the invocation chains
     */
    private void initInvocationChains() {
        chains = new ArrayList<InvocationChain>();
        InterfaceContract sourceContract = getBindingInterfaceContract();

        // It's the service wire
        RuntimeComponentService service = (RuntimeComponentService)getService();
        RuntimeComponent serviceComponent = (RuntimeComponent)getComponent();

        //InterfaceContract targetContract = getInterfaceContract();
        // TODO - EPR - why is this looking at the component types. The endpoint should have the right interface contract by this time
        InterfaceContract targetContract = getServiceInterfaceContract();
        setInterfaceContract(targetContract);
        for (Operation operation : sourceContract.getInterface().getOperations()) {
            Operation targetOperation = interfaceContractMapper.map(targetContract.getInterface(), operation);
            if (targetOperation == null) {
                throw new ServiceRuntimeException("No matching operation for " + operation.getName()
                    + " is found in service "
                    + serviceComponent.getURI()
                    + "#"
                    + service.getName());
            }
            InvocationChain chain = new InvocationChainImpl(operation, targetOperation, false, phaseManager);
            if (operation.isNonBlocking()) {
                addNonBlockingInterceptor(chain);
            }
            addServiceBindingInterceptor(chain, operation);
            addImplementationInterceptor(serviceComponent, service, chain, targetOperation);
            chains.add(chain);
        }

        wireProcessor.process(this);
    }

    private void initServiceBindingInvocationChains() {

        // add the binding interceptors to the service binding wire
        ServiceBindingProvider provider = getBindingProvider();
        if ((provider != null) && (provider instanceof EndpointProvider)) {
            ((EndpointProvider)provider).configure();
        }

        // add the policy interceptors to the service binding wire
        List<PolicyProvider> pps = getPolicyProviders();
        if (pps != null) {
            for (PolicyProvider p : pps) {
                if (p instanceof BindingPolicyProvider) {
                    Interceptor interceptor = ((BindingPolicyProvider)p).createBindingInterceptor();
                    if (interceptor != null) {
                        bindingInvocationChain.addInterceptor(interceptor);
                    }
                }
            }
        }

        // TODO - add something on the end of the wire to invoke the
        //        invocation chain. Need to split out the runtime
        //        wire invoker into conversation, callback interceptors etc
        bindingInvocationChain.addInvoker(invoker);

    }

    /**
     * Add the interceptor for a binding
     *
     * @param reference
     * @param binding
     * @param chain
     * @param operation
     */
    private void addServiceBindingInterceptor(InvocationChain chain, Operation operation) {
        List<PolicyProvider> pps = getPolicyProviders();
        if (pps != null) {
            for (PolicyProvider p : pps) {
                Interceptor interceptor = p.createInterceptor(operation);
                if (interceptor != null) {
                    chain.addInterceptor(p.createInterceptor(operation));
                }
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
    private void addNonBlockingInterceptor(InvocationChain chain) {
        ServiceBindingProvider provider = getBindingProvider();
        if (provider != null) {
            if (!provider.supportsOneWayInvocation()) {
                chain.addInterceptor(Phase.SERVICE, new NonBlockingInterceptor(workScheduler));
            }
        }
    }

    /**
     * Add the interceptor for a component implementation
     *
     * @param component
     * @param service
     * @param chain
     * @param operation
     */
    private void addImplementationInterceptor(Component component,
                                              ComponentService service,
                                              InvocationChain chain,
                                              Operation operation) {

        if (service.getService() instanceof CompositeService) {
            CompositeService compositeService = (CompositeService)service.getService();
            component = getPromotedComponent(compositeService);
            service = getPromotedComponentService(compositeService);
        }

        ImplementationProvider provider = ((RuntimeComponent)component).getImplementationProvider();

        if (provider != null) {
            Invoker invoker = null;
            invoker = provider.createInvoker((RuntimeComponentService)service, operation);
            chain.addInvoker(invoker);
        }
        // TODO - EPR - don't we need to get the policy from the right level in the 
        //              model rather than the leafmost level
        List<PolicyProvider> pps = ((RuntimeComponent)component).getPolicyProviders();
        if (pps != null) {
            for (PolicyProvider p : pps) {
                Interceptor interceptor = p.createInterceptor(operation);
                if (interceptor != null) {
                    chain.addInterceptor(p.createInterceptor(operation));
                }
            }
        }
    }

    /**
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        RuntimeEndpointImpl copy = (RuntimeEndpointImpl)super.clone();
        copy.invoker = new RuntimeInvoker(copy.messageFactory, copy);
        return copy;
    }

    /**
     * Follow a service promotion chain down to the inner most (non composite)
     * component service.
     * 
     * @param topCompositeService
     * @return
     */
    private ComponentService getPromotedComponentService(CompositeService compositeService) {
        ComponentService componentService = compositeService.getPromotedService();
        if (componentService != null) {
            Service service = componentService.getService();
            if (componentService.getName() != null && service instanceof CompositeService) {

                // Continue to follow the service promotion chain
                return getPromotedComponentService((CompositeService)service);

            } else {

                // Found a non-composite service
                return componentService;
            }
        } else {

            // No promoted service
            return null;
        }
    }

    /**
     * Follow a service promotion chain down to the innermost (non-composite) component.
     * 
     * @param compositeService
     * @return
     */
    private Component getPromotedComponent(CompositeService compositeService) {
        ComponentService componentService = compositeService.getPromotedService();
        if (componentService != null) {
            Service service = componentService.getService();
            if (componentService.getName() != null && service instanceof CompositeService) {

                // Continue to follow the service promotion chain
                return getPromotedComponent((CompositeService)service);

            } else {

                // Found a non-composite service
                return compositeService.getPromotedComponent();
            }
        } else {

            // No promoted service
            return null;
        }
    }

    public ServiceBindingProvider getBindingProvider() {
        if (bindingProvider == null) {
            BindingProviderFactory factory =
                (BindingProviderFactory)providerFactories.getProviderFactory(getBinding().getClass());
            if (factory == null) {
                throw new ServiceRuntimeException("No provider factory is registered for binding " + getBinding()
                    .getType());
            }
            this.bindingProvider = factory.createServiceBindingProvider(this);
        }
        return bindingProvider;
    }

    public synchronized List<PolicyProvider> getPolicyProviders() {
        if (policyProviders == null) {
            policyProviders = new ArrayList<PolicyProvider>();
            for (PolicyProviderFactory factory : providerFactories.getPolicyProviderFactories()) {
                PolicyProvider provider = factory.createServicePolicyProvider(this);
                if (provider != null) {
                    policyProviders.add(provider);
                }
            }
        }
        return policyProviders;
    }

    public void setBindingProvider(ServiceBindingProvider provider) {
        this.bindingProvider = provider;
    }

    public Contract getContract() {
        return service;
    }

    public CompositeContext getCompositeContext() {
        return compositeContext;
    }

    private synchronized EndpointSerializer getSerializer() {
        if (serializer == null) {
            if (registry != null) {
                serializer =
                    registry.getExtensionPoint(UtilityExtensionPoint.class).getUtility(EndpointSerializer.class);
            } else {
                throw new IllegalStateException("No extension registry is set");
            }
        }
        return serializer;
    }

    @Override
    protected void reset() {
        super.reset();
        this.xml = null;
    }

    @Override
    public void resolve() {
        if (component == null && xml != null) {
            try {
                getSerializer().read(this, xml);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
        super.resolve();
    }

    public InterfaceContract getBindingInterfaceContract() {
        resolve();
        if (bindingInterfaceContract != null) {
            return bindingInterfaceContract;
        }
        bindingInterfaceContract = getBindingProvider().getBindingInterfaceContract();
        if (bindingInterfaceContract == null && service != null) {
            bindingInterfaceContract = service.getInterfaceContract();
        }
        if (bindingInterfaceContract == null) {
            bindingInterfaceContract = getInterfaceContract();
        }
        return bindingInterfaceContract;
    }

    public InterfaceContract getServiceInterfaceContract() {
        resolve();
        if (serviceInterfaceContract != null) {
            return serviceInterfaceContract;
        }
        if (service == null) {
            return getInterfaceContract();
        }
        serviceInterfaceContract = getLeafContract(service).getInterfaceContract();
        if (serviceInterfaceContract == null) {
            serviceInterfaceContract = getInterfaceContract();
        }
        return serviceInterfaceContract;
    }
    
    public Object writeReplace() throws ObjectStreamException {
        return new EndpointProxy(getSerializer(), this);
    }

    public static class EndpointProxy implements Serializable {
        private static final long serialVersionUID = 6708978267158501975L;
        private String xml;

        /**
         * @param serializer
         */
        public EndpointProxy() {
            super();
        }

        /**
         * @param serializer
         */
        public EndpointProxy(EndpointSerializer serializer, Endpoint endpoint) {
            super();
            try {
                this.xml = serializer.write(endpoint);
            } catch (Exception e) {
                throw new ServiceRuntimeException(e);
            }
        }

        public Object readResolve() throws ObjectStreamException {
            CompositeContext context = CompositeContext.getCurrentCompositeContext();
            if (context == null) {
                throw new IllegalStateException("No context is available for deserializing the endpoint");
            }
            UtilityExtensionPoint utilities =
                context.getExtensionPointRegistry().getExtensionPoint(UtilityExtensionPoint.class);
            EndpointSerializer serializer = utilities.getUtility(EndpointSerializer.class);
            try {
                RuntimeEndpoint endpoint = (RuntimeEndpoint) serializer.readEndpoint(xml);
                endpoint.bind(context);
                return endpoint;
            } catch (IOException e) {
                throw new ServiceRuntimeException(e);
            }
        }
    }

}
