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
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.CompositeReference;
import org.apache.tuscany.sca.assembly.CompositeService;
import org.apache.tuscany.sca.assembly.Contract;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.assembly.impl.EndpointReferenceImpl;
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
import org.apache.tuscany.sca.provider.EndpointReferenceProvider;
import org.apache.tuscany.sca.provider.PolicyProvider;
import org.apache.tuscany.sca.provider.PolicyProviderFactory;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.runtime.EndpointReferenceBinder;
import org.apache.tuscany.sca.runtime.EndpointRegistry;
import org.apache.tuscany.sca.runtime.EndpointSerializer;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;
import org.apache.tuscany.sca.runtime.RuntimeWireProcessor;
import org.apache.tuscany.sca.runtime.RuntimeWireProcessorExtensionPoint;
import org.apache.tuscany.sca.work.WorkScheduler;
import org.oasisopen.sca.SCARuntimeException;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 * Runtime model for Endpoint that supports java serialization
 */
public class RuntimeEndpointReferenceImpl extends EndpointReferenceImpl implements RuntimeEndpointReference {
    private transient CompositeContext compositeContext;
    private transient RuntimeWireProcessor wireProcessor;
    private transient InterfaceContractMapper interfaceContractMapper;
    private transient WorkScheduler workScheduler;
    private transient PhaseManager phaseManager;
    private transient MessageFactory messageFactory;
    private transient RuntimeInvoker invoker;
    private transient EndpointRegistry endpointRegistry;

    private transient List<InvocationChain> chains;
    private transient final Map<Operation, InvocationChain> invocationChainMap =
        new ConcurrentHashMap<Operation, InvocationChain>();
    private transient InvocationChain bindingInvocationChain;

    private transient EndpointReferenceBinder eprBinder;
    private transient ReferenceBindingProvider bindingProvider;
    private transient ProviderFactoryExtensionPoint providerFactories;
    private transient List<PolicyProvider> policyProviders;
    private transient EndpointSerializer serializer;

    protected InterfaceContract bindingInterfaceContract;
    protected InterfaceContract referenceInterfaceContract;
    /**
     * No-arg constructor for Java serilization
     */
    public RuntimeEndpointReferenceImpl() {
        super(null);
    }

    public RuntimeEndpointReferenceImpl(ExtensionPointRegistry registry) {
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
        this.eprBinder = utilities.getUtility(EndpointReferenceBinder.class);
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

    public synchronized List<InvocationChain> getInvocationChains() {
        if (chains == null) {
            initInvocationChains();
        }
        return chains;
    }

    public synchronized InvocationChain getBindingInvocationChain() {
        if (bindingInvocationChain == null) {
            bindingInvocationChain = new InvocationChainImpl(null, null, true, phaseManager);
            initReferenceBindingInvocationChains();
        }
        return bindingInvocationChain;
    }

    public InvocationChain getInvocationChain(Operation operation) {
        InvocationChain cached = invocationChainMap.get(operation);
        if (cached == null) {
            for (InvocationChain chain : getInvocationChains()) {
                Operation op = chain.getSourceOperation();
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
        return invoker.invoke(msg);
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
        InterfaceContract sourceContract = getReferenceInterfaceContract();
        // TODO - EPR why is this looking at the component types. The endpoint reference should have the right interface contract by this time
        //InterfaceContract sourceContract = getLeafInterfaceContract(endpointReference);

        // It's the reference wire
        resolveEndpointReference();

        InterfaceContract targetContract = getBindingInterfaceContract();
        // TODO - EPR why is this looking at the component types. The endpoint should have the right interface contract by this time
        //InterfaceContract targetContract = getLeafInterfaceContract(endpoint);

        RuntimeComponentReference reference = (RuntimeComponentReference)getReference();
        for (Operation operation : sourceContract.getInterface().getOperations()) {
            Operation targetOperation = interfaceContractMapper.map(targetContract.getInterface(), operation);
            if (targetOperation == null) {
                throw new ServiceRuntimeException("No matching operation for " + operation.getName()
                    + " is found in reference "
                    + getComponent().getURI()
                    + "#"
                    + reference.getName());
            }
            InvocationChain chain = new InvocationChainImpl(operation, targetOperation, true, phaseManager);
            if (operation.isNonBlocking()) {
                addNonBlockingInterceptor(chain);
            }
            chains.add(chain);
            addReferenceBindingInterceptor(chain, operation);
        }

        wireProcessor.process(this);
    }

    /**
     * This code used to be in the activator but has moved here as
     * the endpoint reference may not now be resolved until the wire
     * is first used
     */
    private void resolveEndpointReference() {
        boolean ok = eprBinder.bind(endpointRegistry, this);
        if (!ok) {
            throw new SCARuntimeException("Unable to bind " + this);
        }

        // set the endpoint based on the resolved endpoint
        Endpoint endpoint = getTargetEndpoint();

        // start the binding provider
        final ReferenceBindingProvider bindingProvider = getBindingProvider();

        if (bindingProvider != null) {
            // Allow bindings to add shutdown hooks. Requires RuntimePermission shutdownHooks in policy.
            AccessController.doPrivileged(new PrivilegedAction<Object>() {
                public Object run() {
                    bindingProvider.start();
                    return null;
                }
            });
        }
        for (PolicyProvider policyProvider : getPolicyProviders()) {
            policyProvider.start();
        }

        // InterfaceContract bindingContract = getBindingInterfaceContract();
        // endpoint.setInterfaceContract(bindingContract);
    }


    private void initReferenceBindingInvocationChains() {

        // add the binding interceptors to the reference binding wire
        ReferenceBindingProvider provider = getBindingProvider();
        if ((provider != null) && (provider instanceof EndpointReferenceProvider)) {
            ((EndpointReferenceProvider)provider).configure();
        }

        // add the policy interceptors to the service binding wire
        // find out which policies are active
        for (PolicyProvider p : getPolicyProviders()) {
            if (p instanceof BindingPolicyProvider) {
                Interceptor interceptor = ((BindingPolicyProvider)p).createBindingInterceptor();
                if (interceptor != null) {
                    bindingInvocationChain.addInterceptor(interceptor);
                }
            }
        }
    }

    public void rebuild() {
        // TODO - can we use the idea of setTarget to rebuild the wire?
        //        used at the moment by binding.sca when it resets the
        //        source interface contract for local wires
        this.chains = null;

        setStatus(EndpointReference.NOT_CONFIGURED);

        // TODO - cheating here as I fixed the RuntimeComponentService code
        //        to call this when it resets the interface contract
        //endpointReference.setInterfaceContract(epr.getInterfaceContract());
    }

    /**
     * Add the interceptor for a reference binding
     *
     * @param reference
     * @param binding
     * @param chain
     * @param operation
     */
    private void addReferenceBindingInterceptor(InvocationChain chain, Operation operation) {
        ReferenceBindingProvider provider = getBindingProvider();
        if (provider != null) {
            Invoker invoker = provider.createInvoker(operation);
            if (invoker != null) {
                chain.addInvoker(invoker);
            }
        }
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
     * Add a non-blocking interceptor if the reference binding needs it
     *
     * @param reference
     * @param binding
     * @param chain
     */
    private void addNonBlockingInterceptor(InvocationChain chain) {
        ReferenceBindingProvider provider = getBindingProvider();
        if (provider != null) {
            boolean supportsOneWayInvocation = provider.supportsOneWayInvocation();
            if (!supportsOneWayInvocation) {
                chain.addInterceptor(Phase.REFERENCE, new NonBlockingInterceptor(workScheduler));
            }
        }
    }

    /**
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        RuntimeEndpointReferenceImpl copy = (RuntimeEndpointReferenceImpl)super.clone();
        copy.invoker = new RuntimeInvoker(copy.messageFactory, copy);
        return copy;
    }

    public boolean isOutOfDate() {
        return eprBinder.isOutOfDate(endpointRegistry, this);
    }

    public ReferenceBindingProvider getBindingProvider() {
        // For the case that binding.sca is implemented by another binding
        if (binding == null) {
            return null;
        }
        if (bindingProvider == null) {
            BindingProviderFactory factory =
                (BindingProviderFactory)providerFactories.getProviderFactory(getBinding().getClass());
            if (factory == null) {
                throw new ServiceRuntimeException("No provider factory is registered for binding " + getBinding()
                    .getType());
            }
            this.bindingProvider = factory.createReferenceBindingProvider(this);
        }
        return bindingProvider;
    }

    public void setBindingProvider(ReferenceBindingProvider bindingProvider) {
        this.bindingProvider = bindingProvider;
    }

    public synchronized List<PolicyProvider> getPolicyProviders() {
        if (policyProviders == null) {
            policyProviders = new ArrayList<PolicyProvider>();
            for (PolicyProviderFactory factory : providerFactories.getPolicyProviderFactories()) {
                PolicyProvider provider = factory.createReferencePolicyProvider(this);
                if (provider != null) {
                    policyProviders.add(provider);
                }
            }
        }
        return policyProviders;
    }

    public void unbind() {
        bindingInvocationChain = null;
        chains = null;
        bindingProvider = null;
        policyProviders = null;
        invocationChainMap.clear();
    }
    
    public Contract getContract() {
        resolve();
        return reference;
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

    public InterfaceContract getBindingInterfaceContract() {
        resolve();
        if (bindingInterfaceContract != null) {
            return bindingInterfaceContract;
        }
        bindingInterfaceContract = getBindingProvider().getBindingInterfaceContract();
        if (bindingInterfaceContract == null) {
            bindingInterfaceContract = getInterfaceContract();
        }
        if (bindingInterfaceContract == null) {
            bindingInterfaceContract = getReferenceInterfaceContract();
        }
        return bindingInterfaceContract;
    }

    public InterfaceContract getReferenceInterfaceContract() {
        resolve();
        if (referenceInterfaceContract != null) {
            return referenceInterfaceContract;
        }
        if (reference == null) {
            return getInterfaceContract();
        }
        referenceInterfaceContract = getLeafContract(reference).getInterfaceContract();
        if (referenceInterfaceContract == null) {
            referenceInterfaceContract = getInterfaceContract();
        }
        return referenceInterfaceContract;
    }
    public Object writeReplace() throws ObjectStreamException {
        return new EndpointReferenceProxy(getSerializer(), this);
    }

    public static class EndpointReferenceProxy implements Serializable {
        private static final long serialVersionUID = 6708978267158501975L;
        private String xml;

        /**
         * @param serializer
         */
        public EndpointReferenceProxy() {
            super();
        }

        /**
         * @param serializer
         */
        public EndpointReferenceProxy(EndpointSerializer serializer, EndpointReference endpointReference) {
            super();
            try {
                this.xml = serializer.write(endpointReference);
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
            EndpointReferenceBinder eprBinder = utilities.getUtility(EndpointReferenceBinder.class);
            try {
                RuntimeEndpointReference epr = (RuntimeEndpointReference) serializer.readEndpointReference(xml);
                epr.bind(context);
                eprBinder.bind(context.getEndpointRegistry(), epr);
                return epr;
            } catch (IOException e) {
                throw new ServiceRuntimeException(e);
            }
        }
    }


}
