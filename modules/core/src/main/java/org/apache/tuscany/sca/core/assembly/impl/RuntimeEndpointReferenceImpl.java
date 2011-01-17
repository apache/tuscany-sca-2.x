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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.CompositeReference;
import org.apache.tuscany.sca.assembly.CompositeService;
import org.apache.tuscany.sca.assembly.Contract;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.assembly.builder.BindingBuilder;
import org.apache.tuscany.sca.assembly.builder.BuilderContext;
import org.apache.tuscany.sca.assembly.builder.BuilderExtensionPoint;
import org.apache.tuscany.sca.assembly.impl.EndpointReferenceImpl;
import org.apache.tuscany.sca.context.CompositeContext;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.core.assembly.RuntimeAssemblyFactory;
import org.apache.tuscany.sca.core.invocation.AsyncResponseService;
import org.apache.tuscany.sca.core.invocation.ExtensibleWireProcessor;
import org.apache.tuscany.sca.core.invocation.NonBlockingInterceptor;
import org.apache.tuscany.sca.core.invocation.RuntimeInvoker;
import org.apache.tuscany.sca.core.invocation.impl.InvocationChainImpl;
import org.apache.tuscany.sca.core.invocation.impl.PhaseManager;
import org.apache.tuscany.sca.interfacedef.Compatibility;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterfaceContract;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.InterceptorAsync;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.InvokerAsyncResponse;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.invocation.Phase;
import org.apache.tuscany.sca.node.NodeFactory;
import org.apache.tuscany.sca.provider.BindingProviderFactory;
import org.apache.tuscany.sca.provider.EndpointReferenceProvider;
import org.apache.tuscany.sca.provider.ImplementationAsyncProvider;
import org.apache.tuscany.sca.provider.ImplementationProvider;
import org.apache.tuscany.sca.provider.PolicyProvider;
import org.apache.tuscany.sca.provider.PolicyProviderFactory;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.runtime.DomainRegistryFactory;
import org.apache.tuscany.sca.runtime.EndpointReferenceBinder;
import org.apache.tuscany.sca.runtime.EndpointRegistry;
import org.apache.tuscany.sca.runtime.EndpointSerializer;
import org.apache.tuscany.sca.runtime.ExtensibleDomainRegistryFactory;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;
import org.apache.tuscany.sca.runtime.RuntimeWireProcessor;
import org.apache.tuscany.sca.runtime.RuntimeWireProcessorExtensionPoint;
import org.apache.tuscany.sca.work.WorkScheduler;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 * Runtime model for Endpoint that supports java serialization
 */
public class RuntimeEndpointReferenceImpl extends EndpointReferenceImpl implements RuntimeEndpointReference, Externalizable {
    private transient CompositeContext compositeContext;
    private transient RuntimeWireProcessor wireProcessor;
    private transient InterfaceContractMapper interfaceContractMapper;
    private transient WorkScheduler workScheduler;
    private transient PhaseManager phaseManager;
    private transient MessageFactory messageFactory;
    private transient RuntimeInvoker invoker;
    private transient EndpointRegistry endpointRegistry;

    private transient List<InvocationChain> chains;
    private transient Map<Operation, InvocationChain> invocationChainMap =
        new ConcurrentHashMap<Operation, InvocationChain>();
    private transient InvocationChain bindingInvocationChain;

    private transient EndpointReferenceBinder eprBinder;
    private transient ReferenceBindingProvider bindingProvider;
    private transient ProviderFactoryExtensionPoint providerFactories;
    private transient List<PolicyProvider> policyProviders;
    private transient EndpointSerializer serializer;

    protected InterfaceContract bindingInterfaceContract;
    protected InterfaceContract referenceInterfaceContract;
    
    //protected InterfaceContract generatedReferenceWSDLInterfaceContract;
    
    private String xml;

    private boolean started;
    
    /**
     * No-arg constructor for Java serilization
     */
    public RuntimeEndpointReferenceImpl() {
        super(null);
    }

    public RuntimeEndpointReferenceImpl(ExtensionPointRegistry registry) {
        super(registry);
    }

    protected void copyFrom(RuntimeEndpointReferenceImpl copy) {
        this.xml = copy.xml;

        this.component = copy.component;
        this.reference = copy.reference;
        this.interfaceContract = copy.interfaceContract;
        this.referenceInterfaceContract = copy.referenceInterfaceContract;
        this.callbackEndpoint = copy.callbackEndpoint;
        this.targetEndpoint = copy.targetEndpoint;

        this.binding = copy.binding;
        this.bindingInterfaceContract = copy.interfaceContract;
        this.bindingInvocationChain = copy.bindingInvocationChain;

        this.requiredIntents = copy.requiredIntents;
        this.policySets = copy.policySets;

        this.uri = copy.uri;
        this.unresolved = copy.unresolved;
        this.status = copy.status;

        this.chains = copy.chains;
        this.invocationChainMap = copy.invocationChainMap;
        this.bindingProvider = copy.bindingProvider;
        this.policyProviders = copy.policyProviders;

        if (this.compositeContext == null && copy.compositeContext != null) {
            bind(copy.compositeContext);
        }
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
        this.invoker = new RuntimeInvoker(registry, this);

        this.phaseManager = utilities.getUtility(PhaseManager.class);
        this.serializer = utilities.getUtility(EndpointSerializer.class);
        this.providerFactories = registry.getExtensionPoint(ProviderFactoryExtensionPoint.class);
        
        this.builders = registry.getExtensionPoint(BuilderExtensionPoint.class);
        this.contractBuilder = builders.getContractBuilder();
    }

    public synchronized List<InvocationChain> getInvocationChains() {
        if (chains == null) {
            initInvocationChains();
        }
        return chains;
    }

    public synchronized InvocationChain getBindingInvocationChain() {
        if (bindingInvocationChain == null) {
            bindingInvocationChain = new InvocationChainImpl(null, null, true, phaseManager, isAsyncInvocation());
            initReferenceBindingInvocationChains();
        }
        return bindingInvocationChain;
    }

    public InvocationChain getInvocationChain(Operation operation) {
        InvocationChain cached = invocationChainMap.get(operation);
        if (cached == null) {
            for (InvocationChain chain : getInvocationChains()) {
                Operation op = chain.getSourceOperation();
                
                // We used to check compatibility here but this is now validated when the 
                // chain is created. As the chain operations are the real interface types 
                // they may be incompatible just because they are described in different 
                // IDLs
                if (operation.getName().equals(op.getName())) {
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
    
    public void invokeAsync(Operation operation, Message msg){
        msg.setOperation(operation);
        invoker.invokeAsync(msg);
    }
    
    public void invokeAsync(Message msg){
        invoker.invokeAsync(msg);
    }
    
    public void invokeAsyncResponse(Message msg){
    	// If there is a Binding Chain, invoke it first...
        InvocationChain chain = this.getBindingInvocationChain();
        if( chain != null ) {
        	Invoker tailInvoker = chain.getTailInvoker();
        	if (tailInvoker != null) {
        		((InvokerAsyncResponse)tailInvoker).invokeAsyncResponse(msg);
			} // end if
        } // end if
        
        chain = this.getInvocationChain(msg.getOperation());
        Invoker tailInvoker = chain.getTailInvoker();
        ((InvokerAsyncResponse)tailInvoker).invokeAsyncResponse(msg);   
    } // end method invokeAsyncResponse     

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
        InterfaceContract sourceContract = getComponentTypeReferenceInterfaceContract();
        // TODO - EPR why is this looking at the component types. The endpoint reference should have the right interface contract by this time
        //InterfaceContract sourceContract = getLeafInterfaceContract(endpointReference);

        // It's the reference wire
        resolveEndpointReference();

        InterfaceContract targetContract = getBindingInterfaceContract();
        // TODO - EPR why is this looking at the component types. The endpoint should have the right interface contract by this time
        //InterfaceContract targetContract = getLeafInterfaceContract(endpoint);

        if (sourceContract == null && targetContract != null) {
            // TODO: until the web component introspection is brought up
            try {
                sourceContract = (InterfaceContract)targetContract.clone();
            } catch (CloneNotSupportedException e) {
                throw new IllegalStateException(e);
            }
        }
        
        validateReferenceInterfaceCompatibility();

        List<InvocationChain> chainList = new ArrayList<InvocationChain>();
        if(sourceContract != null && targetContract != null) {
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
                InvocationChain chain = new InvocationChainImpl(operation, targetOperation, true, phaseManager, isAsyncInvocation());
                if (operation.isNonBlocking()) {
                    addNonBlockingInterceptor(chain);
                }
                chainList.add(chain);
                addReferenceBindingInterceptor(chain, operation);
            }            
        }

        // Set the chains until it's fully populated. If we initialize too early, any exception could
        // leave this endpoint reference in a wrong state with an empty chain.
        chains = chainList;
        wireProcessor.process(this);
        
        if (isAsyncInvocation()){
            // Fix up all of the operation chain response paths to point back to the implementation provided
            // async response handler
            //ImplementationProvider implementationProvider = ((RuntimeComponent)getComponent()).getImplementationProvider();
        	RuntimeComponentReference theReference = (RuntimeComponentReference)this.getReference();
        	RuntimeComponent theComponent = theReference.getComponent();
            ImplementationProvider implementationProvider = theComponent.getImplementationProvider();
            if (implementationProvider instanceof ImplementationAsyncProvider){
                for (InvocationChain chain : getInvocationChains()){
                    InvokerAsyncResponse asyncResponseInvoker = ((ImplementationAsyncProvider)implementationProvider).createAsyncResponseInvoker(chain.getSourceOperation());
                    if (chain.getHeadInvoker() instanceof InterceptorAsync){
                        ((InterceptorAsync)chain.getHeadInvoker()).setPrevious(asyncResponseInvoker);
                    } else {
                        //TODO - throw error once the old async code is removed
                    } // end if
                } // end for
            } // end if
        } // end if
    } // end method initInvocationChains
    
    /**
     * Check that endpoint reference has compatible interface at the component and binding ends. 
     * The user can specify the interfaces at both ends so there is a danger that they won't be compatible.
     * There is checking in the activator but of course endpoint references may not have a binding assigned
     * until final resolution. 
     */
    public void validateReferenceInterfaceCompatibility() {

        InterfaceContract referenceContract = getComponentReferenceInterfaceContract();
        InterfaceContract bindingContract =  getBindingInterfaceContract();
        
       if ((referenceContract != null) &&
           (bindingContract != null)){
                      
           boolean bindingHasCallback = bindingContract.getCallbackInterface() != null;

           try {
               // Use the normalized contract if the interface types are different or if 
               // a normalized contract has been previously generate, for example, by virtue
               // of finding a JAXWS annotation on a Java class that references a WSDL file
               if (referenceContract.getClass() != bindingContract.getClass() ||
                   referenceContract.getNormalizedWSDLContract() != null ||
                   bindingContract.getNormalizedWSDLContract() != null) {
                   interfaceContractMapper.checkCompatibility(getGeneratedWSDLContract(referenceContract), 
                                                              getGeneratedWSDLContract(bindingContract), 
                                                              Compatibility.SUBSET, 
                                                              !bindingHasCallback, // ignore callbacks if binding doesn't have one 
                                                              false);
               } else {
                   interfaceContractMapper.checkCompatibility(referenceContract, 
                                                              bindingContract, 
                                                              Compatibility.SUBSET, 
                                                              !bindingHasCallback, // ignore callbacks if binding doesn't have one 
                                                              false);                   
               }  
           } catch (Exception ex){
               throw new ServiceRuntimeException("Component " +
                                                 this.getComponent().getName() +
                                                 " Reference " +
                                                 getReference().getName() +
                                                 " interface is incompatible with the interface of the reference binding " + 
                                                 getBinding().getName() + 
                                                 " - " + 
                                                 ex.getMessage() +
                                                 " - [" + this.toString() + "]");
           }
        }  
    }     

    /**
     * This code used to be in the activator but has moved here as
     * the endpoint reference may not now be resolved until the wire
     * is first used
     */
    private void resolveEndpointReference() {
        resolve();

        eprBinder.bindRunTime(endpointRegistry, this);

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

        started = true;
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
           Interceptor interceptor = p.createBindingInterceptor();
            if (interceptor != null) {
                bindingInvocationChain.addInterceptor(interceptor);
            }
        }
    }

    public void rebuild() {
        // TODO - can we use the idea of setTarget to rebuild the wire?
        //        used at the moment by binding.sca when it resets the
        //        source interface contract for local wires
        this.chains = null;

        if (getStatus() == EndpointReference.Status.WIRED_TARGET_FOUND_AND_MATCHED){
            setStatus(EndpointReference.Status.NOT_CONFIGURED);
        }

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
                Interceptor interceptor = p.createBindingInterceptor();
                if (interceptor != null) {
                    chain.addInterceptor(interceptor);
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
        copy.invoker = new RuntimeInvoker(registry, copy);
        return copy;
    }

    public boolean isOutOfDate() {
        resolve();
        return eprBinder.isOutOfDate(endpointRegistry, this);
    }

    public synchronized ReferenceBindingProvider getBindingProvider() {
        resolve();
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
        resolve();
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
        return getReference();
    }

    public CompositeContext getCompositeContext() {
        return compositeContext;
    }

    public InterfaceContract getBindingInterfaceContract() {
        resolve();
        if (bindingInterfaceContract != null) {
            return bindingInterfaceContract;
        }
        ReferenceBindingProvider provider = getBindingProvider();
        if (provider != null) {
            bindingInterfaceContract = provider.getBindingInterfaceContract();
        }
        if (bindingInterfaceContract == null) {
            bindingInterfaceContract = getComponentReferenceInterfaceContract();
        }
        if (bindingInterfaceContract == null) {
            bindingInterfaceContract = getComponentTypeReferenceInterfaceContract();
        }
        return bindingInterfaceContract;
    }

    public InterfaceContract getComponentTypeReferenceInterfaceContract() {
        resolve();
        if (referenceInterfaceContract != null) {
            return referenceInterfaceContract;
        }
        if (reference == null) {
            return getComponentReferenceInterfaceContract();
        }
        referenceInterfaceContract = getLeafContract(reference).getInterfaceContract();
        if (referenceInterfaceContract == null) {
            referenceInterfaceContract = getComponentReferenceInterfaceContract();
        }
        return referenceInterfaceContract;
    }

    @Override
    protected synchronized void resolve() {
        if (xml != null && component == null) {
            if (compositeContext == null) {
                compositeContext = CompositeContext.getCurrentCompositeContext();
                if (compositeContext != null) {
                    bind(compositeContext);
                }
            }
            if (serializer != null) {
                RuntimeEndpointReferenceImpl epr = (RuntimeEndpointReferenceImpl)serializer.readEndpointReference(xml);
                copyFrom(epr);
            } else {
            	// In this case, we assume that we're running on a detached (non Tuscany) thread and
            	// as a result we need to connect back to the Tuscany environment...
            	for( NodeFactory factory : NodeFactory.getNodeFactories() ) {
            		ExtensionPointRegistry registry = factory.getExtensionPointRegistry();
            		if( registry != null ) {
            			this.registry = registry;
            			UtilityExtensionPoint utilities = registry.getExtensionPoint(UtilityExtensionPoint.class);
                        this.serializer = utilities.getUtility(EndpointSerializer.class);
                        RuntimeEndpointReferenceImpl epr = (RuntimeEndpointReferenceImpl)serializer.readEndpointReference(xml);
                        // Find the actual Endpoint in the EndpointRegistry
                        epr = findActualEPR( epr, registry );
                        
                        if( epr != null ){
                        	copyFrom( epr );
                        	break;
                        } // end if
            		} // end if
                } // end for
            } // end if            
        }
        super.resolve();
    } // end method resolve
    
    /**
     * Find the actual EndpointReference in the EndpointRegistry which corresponds to the configuration described
     * in a deserialized EndpointReference 
     * @param ep The deserialized endpointReference
     * @param registry - the main extension point Registry
     * @return the corresponding EndpointReference from the EndpointRegistry, or null if no match can be found
     */
    private RuntimeEndpointReferenceImpl findActualEPR(RuntimeEndpointReferenceImpl epr,
			ExtensionPointRegistry registry) {
		// Get the EndpointRegistry
        DomainRegistryFactory domainRegistryFactory = ExtensibleDomainRegistryFactory.getInstance(registry);
        if( domainRegistryFactory == null ) return null;
        
        // TODO: For the moment, just use the first (and only!) EndpointRegistry...
        EndpointRegistry endpointRegistry = (EndpointRegistry) domainRegistryFactory.getEndpointRegistries().toArray()[0];
        if( endpointRegistry == null ) return null;
        
        for( EndpointReference epReference : endpointRegistry.getEndpointReferences() ) {
        	// TODO: For the present, simply return the first matching endpointReference
        	if( epReference.getURI().equals(epr.getURI()) ) {
        	    return (RuntimeEndpointReferenceImpl) epReference;
        	} // end if
        } // end for
        
		return null;
	} // end method findActualEPR

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.uri = in.readUTF();
        this.xml = in.readUTF();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(getURI());
        if (serializer == null && xml != null) {
            out.writeUTF(xml);
        } else {
            if (serializer != null) {
                out.writeUTF(serializer.write(this));
            } else {
                throw new IllegalStateException("No serializer is configured");
            }
        }
    }

    public boolean isStarted() {
        return started;
    }
    
    public InterfaceContract getGeneratedWSDLContract(InterfaceContract interfaceContract) {

        if ( interfaceContract.getNormalizedWSDLContract() == null){
            if (getComponentReferenceInterfaceContract() instanceof JavaInterfaceContract){
                if (contractBuilder == null){
                    throw new ServiceRuntimeException("Contract builder not found while calculating WSDL contract for " + this.toString());
                }
                contractBuilder.build(interfaceContract, null);
            }
        }
        
        return interfaceContract.getNormalizedWSDLContract();      
    }   
    
    public void createAsyncCallbackEndpoint(){
        CompositeContext compositeContext = getCompositeContext();
        FactoryExtensionPoint modelFactories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        RuntimeAssemblyFactory assemblyFactory = (RuntimeAssemblyFactory)modelFactories.getFactory(AssemblyFactory.class);
        
        RuntimeEndpoint endpoint = (RuntimeEndpoint)assemblyFactory.createEndpoint();
        endpoint.bind(compositeContext);
        endpoint.setComponent(getComponent());

        // Create pseudo-service
        ComponentService service = assemblyFactory.createComponentService();
        JavaInterfaceFactory javaInterfaceFactory =
            (JavaInterfaceFactory)modelFactories.getFactory(JavaInterfaceFactory.class);
        JavaInterfaceContract interfaceContract = javaInterfaceFactory.createJavaInterfaceContract();
        try {
            interfaceContract.setInterface(javaInterfaceFactory.createJavaInterface(AsyncResponseService.class));
        } catch (InvalidInterfaceException e1) {
            // Nothing to do here - will not happen
        } // end try
        
        service.setInterfaceContract(interfaceContract);
        
        String serviceName = getReference().getName() + "_asyncCallback";
        service.setName(serviceName);
        service.getEndpoints().add(endpoint);
        service.setForCallback(true);
        endpoint.setService(service);
        
        // Set pseudo-service onto the component
        getComponent().getServices().add(service);
        
        // if the reference has a WSDL contract reset the response endpoint to be WSDL also
        InterfaceContract referenceInterfaceContract = getComponentTypeReferenceInterfaceContract();
        if (referenceInterfaceContract instanceof WSDLInterfaceContract){
            WSDLInterfaceContract wsdlInterfaceContract = (WSDLInterfaceContract)endpoint.getGeneratedWSDLContract(interfaceContract);
            service.setInterfaceContract(wsdlInterfaceContract);
        }         

        // Create a binding
        // Mike had to go via the XML but I don't remember why
        Binding binding = null;
        try {
            binding = (Binding)getBinding().clone();
        } catch (Exception ex){
            // 
        }
        String callbackURI = "/" + component.getName() + "/" + service.getName();
        binding.setURI(callbackURI);
        
        BuilderExtensionPoint builders = registry.getExtensionPoint(BuilderExtensionPoint.class);
        BindingBuilder builder = builders.getBindingBuilder(binding.getType());
        if (builder != null) {
            org.apache.tuscany.sca.assembly.builder.BuilderContext builderContext = new BuilderContext(registry);
            builder.build(component, service, binding, builderContext, true);
        } // end if
        
        endpoint.setBinding(binding);

        // Need to establish policies here (binding has some...)
        endpoint.getRequiredIntents().addAll(getRequiredIntents());
        endpoint.getPolicySets().addAll(getPolicySets());
        String epURI = getComponent().getName() + "#service-binding(" + serviceName + "/" + serviceName + ")";
        endpoint.setURI(epURI);
        endpoint.setUnresolved(false);
        
        setCallbackEndpoint(endpoint);
    }
}
