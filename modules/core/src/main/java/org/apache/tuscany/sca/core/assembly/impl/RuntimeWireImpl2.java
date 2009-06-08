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

import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Endpoint2;
import org.apache.tuscany.sca.assembly.EndpointReference2;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderExtensionPoint;
import org.apache.tuscany.sca.assembly.builder.EndpointReferenceBuilder;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.core.conversation.ConversationManager;
import org.apache.tuscany.sca.core.invocation.NonBlockingInterceptor;
import org.apache.tuscany.sca.core.invocation.RuntimeWireInvoker;
import org.apache.tuscany.sca.core.invocation.impl.InvocationChainImpl;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.invocation.Phase;
import org.apache.tuscany.sca.monitor.MonitorFactory;
import org.apache.tuscany.sca.provider.BindingProviderFactory;
import org.apache.tuscany.sca.provider.ImplementationProvider;
import org.apache.tuscany.sca.provider.PolicyProvider;
import org.apache.tuscany.sca.provider.PolicyProviderFactory;
import org.apache.tuscany.sca.provider.PolicyProviderRRB;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.provider.ReferenceBindingProviderRRB;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.provider.ServiceBindingProviderRRB;
import org.apache.tuscany.sca.runtime.EndpointReference;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.apache.tuscany.sca.runtime.RuntimeWireProcessor;
import org.apache.tuscany.sca.work.WorkScheduler;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 * @version $Rev$ $Date$
 */
public class RuntimeWireImpl2 implements RuntimeWire {
    
    private ExtensionPointRegistry extensionPoints;
    
    private Boolean isReferenceWire = false;
    private EndpointReference2 endpointReference;
    private Endpoint2 endpoint;

    private transient RuntimeWireProcessor wireProcessor;
    private transient InterfaceContractMapper interfaceContractMapper;
    private transient WorkScheduler workScheduler;
    private transient MessageFactory messageFactory;
    private transient ConversationManager conversationManager;
    private transient RuntimeWireInvoker invoker;

    // the following is a very simple cache that avoids re-cloning a wire
    // when consecutive callbacks to the same endpoint are made
    private EndpointReference lastCallback;
    private RuntimeWire cachedWire;
    private boolean wireReserved;
    private RuntimeWireImpl2 clonedFrom;

    private List<InvocationChain> chains;
    private InvocationChain bindingInvocationChain;
    
    private EndpointReferenceBuilder endpointReferenceBuilder;
    private final ProviderFactoryExtensionPoint providerFactories;

    /**
     * @param source
     * @param target
     * @param interfaceContractMapper 
     * @param workScheduler 
     * @param wireProcessor 
     * @param messageFactory 
     * @param conversationManager 
     */
    public RuntimeWireImpl2(ExtensionPointRegistry extensionPoints,
                            boolean isReferenceWire,
                            EndpointReference2 endpointReference,
                            Endpoint2 endpoint,
                            InterfaceContractMapper interfaceContractMapper,
                            WorkScheduler workScheduler,
                            RuntimeWireProcessor wireProcessor,
                            MessageFactory messageFactory,
                            ConversationManager conversationManager) {
        super();
        this.extensionPoints = extensionPoints;
        this.isReferenceWire = isReferenceWire;
        this.endpointReference = endpointReference;
        this.endpoint = endpoint;
        this.interfaceContractMapper = interfaceContractMapper;
        this.workScheduler = workScheduler;
        this.wireProcessor = wireProcessor;
        this.messageFactory = messageFactory;
        this.conversationManager = conversationManager;
        this.invoker = new RuntimeWireInvoker(this.messageFactory, this.conversationManager, this);
       
        UtilityExtensionPoint utilities = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);
        this.endpointReferenceBuilder = utilities.getUtility(EndpointReferenceBuilder.class);
        this.providerFactories = extensionPoints.getExtensionPoint(ProviderFactoryExtensionPoint.class);
    }

    public synchronized List<InvocationChain> getInvocationChains() {
        if (chains == null) {
            initInvocationChains();
        }
        return chains;
    }
    
    public synchronized InvocationChain getBindingInvocationChain() {
        if (bindingInvocationChain == null) {
            bindingInvocationChain = new InvocationChainImpl(null, null, isReferenceWire);
            if (isReferenceWire) {
                initReferenceBindingInvocationChains();
            } else {
                initServiceBindingInvocationChains();
            }
        }
        return bindingInvocationChain;
    }

    public InvocationChain getInvocationChain(Operation operation) {
        for (InvocationChain chain : getInvocationChains()) {
            Operation op = null;
            if (isReferenceWire) {
                op = chain.getSourceOperation();
            } else {
                op = chain.getTargetOperation();
            }
            if (interfaceContractMapper.isCompatible(operation, op, op.getInterface().isRemotable())) {
                return chain;
            }
        }
        return null;
    }
    
    public Object invoke(Message msg) throws InvocationTargetException {
        return getBindingInvocationChain().getHeadInvoker().invoke(msg);
    }    

    public Object invoke(Operation operation, Object[] args) throws InvocationTargetException {
        Message msg = messageFactory.createMessage();
        msg.setBody(args);
        return invoker.invoke(operation, msg);
    }

    public Object invoke(Operation operation, Message msg) throws InvocationTargetException {
        return invoker.invoke(operation, msg);
    }

    /**
     * Initialize the invocation chains
     */
    private void initInvocationChains() {
   
        chains = new ArrayList<InvocationChain>();
        InterfaceContract sourceContract = endpointReference.getInterfaceContract();

        if (isReferenceWire) {
            // It's the reference wire
            resolveEndpointReference();

            InterfaceContract targetContract = endpoint.getInterfaceContract();
            RuntimeComponentReference reference = (RuntimeComponentReference)endpointReference.getReference();
            Binding refBinding = endpointReference.getBinding();
            for (Operation operation : sourceContract.getInterface().getOperations()) {
                Operation targetOperation = interfaceContractMapper.map(targetContract.getInterface(), operation);
                if (targetOperation == null) {
                    throw new ServiceRuntimeException("No matching operation for " + operation.getName()
                        + " is found in reference "
                        + endpointReference.getComponent().getURI()
                        + "#"
                        + reference.getName());
                }
                InvocationChain chain = new InvocationChainImpl(operation, targetOperation, true);
                if (operation.isNonBlocking()) {
                    addNonBlockingInterceptor(reference, refBinding, chain);
                }
                addReferenceBindingInterceptor(reference, refBinding, chain, operation);
                chains.add(chain);
            }
            
        } else {
            // It's the service wire
            InterfaceContract targetContract = endpoint.getInterfaceContract();
            RuntimeComponentService service = (RuntimeComponentService)endpoint.getService();
            RuntimeComponent serviceComponent = (RuntimeComponent)endpoint.getComponent();
            Binding serviceBinding = endpoint.getBinding();
            for (Operation operation : sourceContract.getInterface().getOperations()) {
                Operation targetOperation = interfaceContractMapper.map(targetContract.getInterface(), operation);
                if (targetOperation == null) {
                    throw new ServiceRuntimeException("No matching operation for " + operation.getName()
                        + " is found in service "
                        + serviceComponent.getURI()
                        + "#"
                        + service.getName());
                }
                InvocationChain chain = new InvocationChainImpl(operation, targetOperation, false);
                if (operation.isNonBlocking()) {
                    addNonBlockingInterceptor(service, serviceBinding, chain);
                }
                addServiceBindingInterceptor(service, serviceBinding, chain, operation);
                addImplementationInterceptor(serviceComponent, service, chain, targetOperation);
                chains.add(chain);
            }
            
        }
        wireProcessor.process(this);
    }
    
    /**
     * This code used to be in the activator but has moved here as 
     * the endpoint reference may not now be resolved until the wire
     * is first used
     */
    private void resolveEndpointReference(){
        endpointReferenceBuilder.build(endpointReference, null);
        
        // set the endpoint based on the resolved endpoint
        endpoint = endpointReference.getTargetEndpoint();
                
        RuntimeComponentReference runtimeRef = (RuntimeComponentReference)endpointReference.getReference();
        
        if (runtimeRef.getBindingProvider(endpointReference.getBinding()) == null) {
            addReferenceBindingProvider((RuntimeComponent)endpointReference.getComponent(), 
                    runtimeRef, 
                    endpointReference.getBinding());
        }
        
        // start the binding provider   
        final ReferenceBindingProvider bindingProvider = runtimeRef.getBindingProvider(endpointReference.getBinding());
        
        if (bindingProvider != null) {
            // Allow bindings to add shutdown hooks. Requires RuntimePermission shutdownHooks in policy. 
            AccessController.doPrivileged(new PrivilegedAction<Object>() {
                public Object run() {
                    bindingProvider.start();
                    return null;
                  }
            });                       
        }
        
        InterfaceContract bindingContract = getInterfaceContract(endpointReference.getReference(), endpointReference.getBinding());
        Endpoint2 endpoint = endpointReference.getTargetEndpoint();
        endpoint.setInterfaceContract(bindingContract);
    }
    
    private ReferenceBindingProvider addReferenceBindingProvider(
            RuntimeComponent component, RuntimeComponentReference reference,
            Binding binding) {
        BindingProviderFactory providerFactory = (BindingProviderFactory) providerFactories
                .getProviderFactory(binding.getClass());
        if (providerFactory != null) {
            @SuppressWarnings("unchecked")
            ReferenceBindingProvider bindingProvider = providerFactory
                    .createReferenceBindingProvider(
                            (RuntimeComponent) component,
                            (RuntimeComponentReference) reference, binding);
            if (bindingProvider != null) {
                ((RuntimeComponentReference) reference).setBindingProvider(
                        binding, bindingProvider);
            }
            for (PolicyProviderFactory f : providerFactories
                    .getPolicyProviderFactories()) {
                PolicyProvider policyProvider = f
                        .createReferencePolicyProvider(component, reference,
                                binding);
                if (policyProvider != null) {
                    reference.addPolicyProvider(binding, policyProvider);
                }
            }

            return bindingProvider;
        } else {
            throw new IllegalStateException(
                    "Provider factory not found for class: "
                            + binding.getClass().getName());
        }
    }  
    
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
    
    private void initReferenceBindingInvocationChains() {
        RuntimeComponentReference reference = (RuntimeComponentReference)endpointReference.getReference();
        Binding referenceBinding = endpointReference.getBinding();
        
        // add the binding interceptors to the reference binding wire
        ReferenceBindingProvider provider = reference.getBindingProvider(referenceBinding);
        if ((provider != null) &&
            (provider instanceof ReferenceBindingProviderRRB)){
            ((ReferenceBindingProviderRRB)provider).configureBindingChain(this);
        }
        
        // add the policy interceptors to the service binding wire
        // find out which policies are active
        List<PolicyProvider> pps = ((RuntimeComponentReference)reference).getPolicyProviders(referenceBinding);
        if (pps != null) {
            for (PolicyProvider p : pps) {
                if (p instanceof PolicyProviderRRB) {
                    Interceptor interceptor = ((PolicyProviderRRB)p).createBindingInterceptor();
                    if (interceptor != null) {
                        bindingInvocationChain.addInterceptor(p.getPhase(), interceptor);
                    }
                }
            }
        }               
    }    
    
    private void initServiceBindingInvocationChains() {
        RuntimeComponentService service = (RuntimeComponentService)endpoint.getService();
        Binding serviceBinding = endpoint.getBinding();
        
        // add the binding interceptors to the service binding wire
        ServiceBindingProvider provider = service.getBindingProvider(serviceBinding);
        if ((provider != null) &&
            (provider instanceof ServiceBindingProviderRRB)){
            ((ServiceBindingProviderRRB)provider).configureBindingChain(this);
        }
        
        // add the policy interceptors to the service binding wire
        List<PolicyProvider> pps = ((RuntimeComponentService)service).getPolicyProviders(serviceBinding);
        if (pps != null) {
            for (PolicyProvider p : pps) {
                if (p instanceof PolicyProviderRRB) {
                    Interceptor interceptor = ((PolicyProviderRRB)p).createBindingInterceptor();
                    if (interceptor != null) {
                        bindingInvocationChain.addInterceptor(p.getPhase(), interceptor);
                    }
                }
            }
        }        
        
        
        // TODO - add something on the end of the wire to invoke the 
        //        invocation chain. Need to split out the runtime
        //        wire invoker into conversation, callback interceptors etc
        bindingInvocationChain.addInvoker(invoker);
        
    }

    // ===============================================================
    // TODO - EPR remove when we convert fully over to EndpointReference2
    
    // TODO - remove. Just here during development
    static EndpointReference epr;
    
    public EndpointReference getSource() {
        // TODO - EPR convert this into method that returns EndpointReference2
        
        // convert the source info into old endpoint reference format
        epr = new EndpointReferenceImpl((RuntimeComponent)endpointReference.getComponent(),
                                                          endpointReference.getReference(),
                                                          endpointReference.getBinding(),
                                                          endpointReference.getInterfaceContract());
        
        if (endpointReference.getCallbackEndpoint() != null){
            // convert the source callback endpoint into old endpoint reference format
            EndpointReference cepr;
            cepr = new EndpointReferenceImpl((RuntimeComponent)endpointReference.getComponent(),
                    endpointReference.getCallbackEndpoint().getService(),
                    endpointReference.getCallbackEndpoint().getBinding(),
                    endpointReference.getCallbackEndpoint().getInterfaceContract());
            epr.setCallbackEndpoint(cepr);
        }
        
        
        // TODO - somtimes used to reset the interface contract so we 
        //        copy it back in in the rebuild method below  
        return epr;
    }

    
    
    public EndpointReference getTarget() {
        // TODO - EPR convert this into method that returns Endpoint2
        
        // convert the target info into old endpoint reference format
        EndpointReference epr = new EndpointReferenceImpl((RuntimeComponent)endpoint.getComponent(),
                                                           endpoint.getService(),
                                                           endpoint.getBinding(),
                                                           endpoint.getInterfaceContract());
        return epr;
    }

    public void setTarget(EndpointReference target) {
        // TODO - can we use the idea of setTarget to rebuild the wire?

    }
    
    // ===================================================================

    public void rebuild() {
        // TODO - can we use the idea of setTarget to rebuild the wire?
        //        used at the moment by binding.sca when it resets the 
        //        source interface contract for local wires
        this.chains = null;
        
        // TODO - cheating here as I fixed the RuntimeComponentService code
        //        to call this when it resets the interface contract
        endpointReference.setInterfaceContract(epr.getInterfaceContract());
    }
    
    public EndpointReference2 getEndpointReference(){
        return endpointReference;
    }

    /**
     * Add the interceptor for a reference binding
     * 
     * @param reference
     * @param binding
     * @param chain
     * @param operation
     */
    private void addReferenceBindingInterceptor(ComponentReference reference,
                                                Binding binding,
                                                InvocationChain chain,
                                                Operation operation) {
        ReferenceBindingProvider provider = ((RuntimeComponentReference)reference).getBindingProvider(binding);
        if (provider != null) {
            Invoker invoker = provider.createInvoker(operation);
            if (invoker != null) {
                chain.addInvoker(invoker);
            }
        }
        List<PolicyProvider> pps = ((RuntimeComponentReference)reference).getPolicyProviders(binding);
        if (pps != null) {
            for (PolicyProvider p : pps) {
                Interceptor interceptor = p.createInterceptor(operation);
                if (interceptor != null) {
                    chain.addInterceptor(p.getPhase(), p.createInterceptor(operation));
                }
            }
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
    private void addServiceBindingInterceptor(ComponentService service,
                                              Binding binding,
                                              InvocationChain chain,
                                              Operation operation) {
        List<PolicyProvider> pps = ((RuntimeComponentService)service).getPolicyProviders(binding);
        if (pps != null) {
            for (PolicyProvider p : pps) {
                Interceptor interceptor = p.createInterceptor(operation);
                if (interceptor != null) {
                    chain.addInterceptor(p.getPhase(), p.createInterceptor(operation));
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
    private void addNonBlockingInterceptor(ComponentReference reference, Binding binding, InvocationChain chain) {
        ReferenceBindingProvider provider = ((RuntimeComponentReference)reference).getBindingProvider(binding);
        if (provider != null) {
            boolean supportsOneWayInvocation = provider.supportsOneWayInvocation();
            if (!supportsOneWayInvocation) {
                chain.addInterceptor(Phase.REFERENCE, new NonBlockingInterceptor(workScheduler));
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
    private void addNonBlockingInterceptor(ComponentService service, Binding binding, InvocationChain chain) {
        ServiceBindingProvider provider = ((RuntimeComponentService)service).getBindingProvider(binding);
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
        ImplementationProvider provider = ((RuntimeComponent)component).getImplementationProvider();
        if (provider != null) {
            Invoker invoker = null;
            invoker = provider.createInvoker((RuntimeComponentService)service, operation);
            chain.addInvoker(invoker);
        }
        List<PolicyProvider> pps = ((RuntimeComponent)component).getPolicyProviders();
        if (pps != null) {
            for (PolicyProvider p : pps) {
                Interceptor interceptor = p.createInterceptor(operation);
                if (interceptor != null) {
                    chain.addInterceptor(p.getPhase(), p.createInterceptor(operation));
                }
            }
        }
    }

    /**
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        RuntimeWireImpl2 copy = (RuntimeWireImpl2)super.clone();
        copy.endpointReference = (EndpointReference2)endpointReference.clone();
        copy.endpoint = copy.endpointReference.getTargetEndpoint();
        copy.invoker = new RuntimeWireInvoker(copy.messageFactory, copy.conversationManager, copy);
        copy.cachedWire = null; // TUSCANY-2630
        return copy;
    }

    /**
     * @return the conversationManager
     */
    public ConversationManager getConversationManager() {
        return conversationManager;
    }

    public synchronized RuntimeWire lookupCache(EndpointReference callback) {
        if (lastCallback != null && callback.getURI().equals(lastCallback.getURI()) && !wireReserved) {
            wireReserved = true;
            return cachedWire;
        } else {
            return null;
        }
    }

    public synchronized void addToCache(EndpointReference callback, RuntimeWire clonedWire) {
        ((RuntimeWireImpl2)clonedWire).setClonedFrom(this);
        lastCallback = callback;
        cachedWire = clonedWire;
        wireReserved = true;
    }

    public synchronized void releaseClonedWire(RuntimeWire wire) {
        if (cachedWire == wire) {
            wireReserved = false;
        }
    }

    public synchronized void releaseWire() {
        clonedFrom.releaseClonedWire(this);
    }

    private void setClonedFrom(RuntimeWireImpl2 wire) {
        clonedFrom = wire;
    }
}
