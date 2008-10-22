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

package org.apache.tuscany.sca.core.assembly;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Contract;
import org.apache.tuscany.sca.core.conversation.ConversationManager;
import org.apache.tuscany.sca.core.invocation.InvocationChainImpl;
import org.apache.tuscany.sca.core.invocation.NonBlockingInterceptor;
import org.apache.tuscany.sca.core.invocation.RuntimeWireInvoker;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.invocation.Phase;
import org.apache.tuscany.sca.provider.ImplementationProvider;
import org.apache.tuscany.sca.provider.PolicyProvider;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.EndpointReference;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.apache.tuscany.sca.runtime.RuntimeWireProcessor;
import org.apache.tuscany.sca.work.WorkScheduler;
import org.osoa.sca.ServiceRuntimeException;

/**
 * @version $Rev$ $Date$
 */
public class RuntimeWireImpl implements RuntimeWire {
    private EndpointReference wireSource;
    private EndpointReference wireTarget;

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
    private RuntimeWireImpl clonedFrom;

    private List<InvocationChain> chains;
    private InvocationChain binidngInvocationChain;

    /**
     * @param source
     * @param target
     * @param interfaceContractMapper 
     * @param workScheduler 
     * @param wireProcessor 
     * @param messageFactory 
     * @param conversationManager 
     */
    public RuntimeWireImpl(EndpointReference source,
                           EndpointReference target,
                           InterfaceContractMapper interfaceContractMapper,
                           WorkScheduler workScheduler,
                           RuntimeWireProcessor wireProcessor,
                           MessageFactory messageFactory,
                           ConversationManager conversationManager) {
        super();
        this.wireSource = source;
        this.wireTarget = target;
        this.interfaceContractMapper = interfaceContractMapper;
        this.workScheduler = workScheduler;
        this.wireProcessor = wireProcessor;
        this.messageFactory = messageFactory;
        this.conversationManager = conversationManager;
        this.invoker = new RuntimeWireInvoker(this.messageFactory, this.conversationManager, this);
    }

    public synchronized List<InvocationChain> getInvocationChains() {
        if (chains == null) {
            initInvocationChains();
        }
        return chains;
    }
    
    public synchronized InvocationChain getBindingInvocationChain() {
        if (binidngInvocationChain == null) {
            Contract source = wireSource.getContract();
            if (source instanceof RuntimeComponentReference) {
                binidngInvocationChain = new InvocationChainImpl(null, null, true);
            } else {
                binidngInvocationChain = new InvocationChainImpl(null, null, false);
            }
        }
        return binidngInvocationChain;
    }

    public InvocationChain getInvocationChain(Operation operation) {
        for (InvocationChain chain : getInvocationChains()) {
            Operation op = null;
            if (wireSource.getContract() != null) {
                // Reference chain
                op = chain.getSourceOperation();
            } else {
                // Service chain
                op = chain.getTargetOperation();
            }
            if (interfaceContractMapper.isCompatible(operation, op, op.getInterface().isRemotable())) {
                return chain;
            }
        }
        return null;
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
        InterfaceContract sourceContract = wireSource.getInterfaceContract();
        InterfaceContract targetContract = wireTarget.getInterfaceContract();

        Contract source = wireSource.getContract();
        if (source instanceof RuntimeComponentReference) {
            // It's the reference wire
            RuntimeComponentReference reference = (RuntimeComponentReference)wireSource.getContract();
            Binding refBinding = wireSource.getBinding();
            for (Operation operation : sourceContract.getInterface().getOperations()) {
                Operation targetOperation = interfaceContractMapper.map(targetContract.getInterface(), operation);
                if (targetOperation == null) {
                    throw new ServiceRuntimeException("No matching operation for " + operation.getName()
                        + " is found in reference "
                        + wireSource.getComponent().getURI()
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
            RuntimeComponentService service = (RuntimeComponentService)wireTarget.getContract();
            RuntimeComponent serviceComponent = wireTarget.getComponent();
            Binding serviceBinding = wireTarget.getBinding();
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

    public EndpointReference getSource() {
        return wireSource;
    }

    public EndpointReference getTarget() {
        return wireTarget;
    }

    public void setTarget(EndpointReference target) {
        if (this.wireTarget != target) {
            rebuild();
        }
        this.wireTarget = target;
    }

    public void rebuild() {
        this.chains = null;
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
        RuntimeWireImpl copy = (RuntimeWireImpl)super.clone();
        copy.wireSource = (EndpointReference)wireSource.clone();
        copy.wireTarget = (EndpointReference)wireTarget.clone();
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
        ((RuntimeWireImpl)clonedWire).setClonedFrom(this);
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

    private void setClonedFrom(RuntimeWireImpl wire) {
        clonedFrom = wire;
    }
}
