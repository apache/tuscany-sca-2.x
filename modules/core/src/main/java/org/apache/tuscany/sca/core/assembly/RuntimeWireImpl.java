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

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Contract;
import org.apache.tuscany.sca.core.invocation.InvocationChainImpl;
import org.apache.tuscany.sca.core.invocation.NonBlockingInterceptor;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.ImplementationProvider;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider2;
import org.apache.tuscany.sca.runtime.EndpointReference;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.apache.tuscany.sca.runtime.RuntimeWireProcessor;
import org.apache.tuscany.sca.work.WorkScheduler;

/**
 * @version $Rev$ $Date$
 */
public class RuntimeWireImpl implements RuntimeWire {
    private EndpointReference wireSource;
    private EndpointReference wireTarget;

    private transient RuntimeWireProcessor wireProcessor;
    private transient InterfaceContractMapper interfaceContractMapper;
    private transient WorkScheduler workScheduler;

    private List<InvocationChain> chains;

    /**
     * @param source
     * @param target
     * @param interfaceContractMapper 
     * @param workScheduler 
     * @param wireProcessor 
     */
    public RuntimeWireImpl(EndpointReference source,
                           EndpointReference target,
                           InterfaceContractMapper interfaceContractMapper,
                           WorkScheduler workScheduler,
                           RuntimeWireProcessor wireProcessor) {
        super();
        this.wireSource = source;
        this.wireTarget = target;
        this.interfaceContractMapper = interfaceContractMapper;
        this.workScheduler = workScheduler;
        this.wireProcessor = wireProcessor;
    }

    public synchronized List<InvocationChain> getInvocationChains() {
        if (chains == null) {
            initInvocationChains();
        }
        return chains;
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
                InvocationChain chain = new InvocationChainImpl(operation, targetOperation);
                if (operation.isNonBlocking()) {
                    addNonBlockingInterceptor(reference, refBinding, chain);
                }
                addBindingInterceptor(reference, refBinding, chain, operation);
                chains.add(chain);
            }
        } else {
            // It's the service wire
            RuntimeComponentService service = (RuntimeComponentService)wireTarget.getContract();
            RuntimeComponent serviceComponent = wireTarget.getComponent();
            for (Operation operation : sourceContract.getInterface().getOperations()) {
                Operation targetOperation = interfaceContractMapper.map(targetContract.getInterface(), operation);
                InvocationChain chain = new InvocationChainImpl(operation, targetOperation);
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
        this.wireTarget = target;
        this.chains = null;
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
    }

    /**
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        RuntimeWireImpl copy = (RuntimeWireImpl)super.clone();
        copy.wireSource = (EndpointReference)wireSource.clone();
        copy.wireTarget = (EndpointReference)wireTarget.clone();
        copy.chains = null;
        return copy;
    }
}
