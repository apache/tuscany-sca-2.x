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

import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.OutputStream;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLWriter;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.CompositeReference;
import org.apache.tuscany.sca.assembly.CompositeService;
import org.apache.tuscany.sca.assembly.Contract;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.builder.BindingBuilder;
import org.apache.tuscany.sca.assembly.builder.BuilderContext;
import org.apache.tuscany.sca.assembly.builder.BuilderExtensionPoint;
import org.apache.tuscany.sca.assembly.impl.EndpointImpl;
import org.apache.tuscany.sca.context.CompositeContext;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ValidatingXMLInputFactory;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistryLocator;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.core.assembly.RuntimeAssemblyFactory;
import org.apache.tuscany.sca.core.invocation.AsyncResponseService;
import org.apache.tuscany.sca.core.invocation.Constants;
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
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLDefinition;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterface;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterfaceContract;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.InterceptorAsync;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.InvokerAsyncResponse;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.invocation.Phase;
import org.apache.tuscany.sca.provider.BindingProviderFactory;
import org.apache.tuscany.sca.provider.EndpointAsyncProvider;
import org.apache.tuscany.sca.provider.EndpointProvider;
import org.apache.tuscany.sca.provider.ImplementationAsyncProvider;
import org.apache.tuscany.sca.provider.ImplementationProvider;
import org.apache.tuscany.sca.provider.OptimisingBindingProvider;
import org.apache.tuscany.sca.provider.PolicyProvider;
import org.apache.tuscany.sca.provider.PolicyProviderFactory;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.DomainRegistry;
import org.apache.tuscany.sca.runtime.DomainRegistryFactory;
import org.apache.tuscany.sca.runtime.EndpointSerializer;
import org.apache.tuscany.sca.runtime.ExtensibleDomainRegistryFactory;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;
import org.apache.tuscany.sca.runtime.RuntimeWireProcessor;
import org.apache.tuscany.sca.runtime.RuntimeWireProcessorExtensionPoint;
import org.apache.tuscany.sca.work.WorkScheduler;
import org.apache.tuscany.sca.xsd.XSDefinition;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaImport;
import org.apache.ws.commons.schema.XmlSchemaInclude;
import org.apache.ws.commons.schema.XmlSchemaObject;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 * Runtime model for Endpoint that supports java serialization
 */
public class RuntimeEndpointImpl extends EndpointImpl implements RuntimeEndpoint, Externalizable {
    private static final long serialVersionUID = 1L;
    private static final byte separator[] = {'_', 'X', '_'};
    
    private transient CompositeContext compositeContext;
    private transient RuntimeWireProcessor wireProcessor;
    private transient ProviderFactoryExtensionPoint providerFactories;
    private transient InterfaceContractMapper interfaceContractMapper;
    private transient WorkScheduler workScheduler;
    private transient PhaseManager phaseManager;
    private transient MessageFactory messageFactory;
    private transient RuntimeInvoker invoker;
    private transient EndpointSerializer serializer;

    private transient List<InvocationChain> chains;
    private transient Map<Operation, InvocationChain> invocationChainMap =
        new ConcurrentHashMap<Operation, InvocationChain>();
    private transient InvocationChain bindingInvocationChain;

    private transient ServiceBindingProvider bindingProvider;
    private transient List<PolicyProvider> policyProviders;
    private String xml;
    private String wsdl;
    private String wsdlCallback;

    protected InterfaceContract bindingInterfaceContract;
    protected InterfaceContract serviceInterfaceContract;
    
    private RuntimeEndpoint delegateEndpoint;
    
    /**
     * No-arg constructor for Java serialization
     */
    public RuntimeEndpointImpl() {
        super(null);
    }

    public RuntimeEndpointImpl(ExtensionPointRegistry registry) {
        super(registry);
    }

    protected void copyFrom(RuntimeEndpointImpl copy) {
        this.xml = copy.xml;

        this.component = copy.component;
        this.service = copy.service;
        this.interfaceContract = copy.interfaceContract;
        this.serviceInterfaceContract = copy.serviceInterfaceContract;

        this.binding = copy.binding;
        this.bindingInterfaceContract = copy.interfaceContract;
        this.bindingInvocationChain = copy.bindingInvocationChain;

        this.callbackEndpointReferences = copy.callbackEndpointReferences;

        this.requiredIntents = copy.requiredIntents;
        this.policySets = copy.policySets;

        this.uri = copy.uri;
        this.remote = copy.remote;
        this.unresolved = copy.unresolved;

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

    public void bind(ExtensionPointRegistry registry, DomainRegistry domainRegistry) {
        if (compositeContext == null) {
            compositeContext = new CompositeContext(registry, domainRegistry);
        }

        // if interfaceContractMapper is already initialized then all the rest will be too
        if (interfaceContractMapper != null) {
            return;
        }
        this.registry = registry;
        UtilityExtensionPoint utilities = registry.getExtensionPoint(UtilityExtensionPoint.class);
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
            bindingInvocationChain = new InvocationChainImpl(null, null, false, phaseManager, isAsyncInvocation());
            initServiceBindingInvocationChains();
        }
        
        // Init the operation invocation chains now. We know they will 
        // be needed as well as the binding invocation chain and this
        // makes the wire processors run
        getInvocationChains();
        
        return bindingInvocationChain;
    }

    /**
     * A dummy invocation chain representing null as ConcurrentHashMap doesn't allow null values
     */
    private static final InvocationChain NULL_CHAIN = new InvocationChainImpl(null, null, false, null, false);

    public InvocationChain getInvocationChain(Operation operation) {
        InvocationChain cached = invocationChainMap.get(operation);
        if (cached == null) {
            for (InvocationChain chain : getInvocationChains()) {
                Operation op = chain.getTargetOperation();

                // We used to check compatibility here but this is now validated when the 
                // chain is created. As the chain operations are the real interface types 
                // they may be incompatible just because they are described in different 
                // IDLs
                if (operation.getInterface().isRemotable()) {
                    if (operation.getName().equals(op.getName())) {
                        invocationChainMap.put(operation, chain);
                        return chain;
                    }
                    if (interfaceContractMapper.isCompatible(operation, op, Compatibility.SUBSET)) {
                        invocationChainMap.put(operation, chain);
                        return chain;
                    }
                } else {
                    // [rfeng] We need to run the compatibility check for local operations as they 
                    // can be overloaded
                    if (interfaceContractMapper.isCompatible(operation, op, Compatibility.SUBSET)) {
                        invocationChainMap.put(operation, chain);
                        return chain;
                    }
                }
            }
            // Cache it with the NULL_CHAIN to avoid NPE
            invocationChainMap.put(operation, NULL_CHAIN);
            return null;
        } else {
            if (cached == NULL_CHAIN) {
                cached = null;
            }
            return cached;
        }
    }

    public Message invoke(Message msg) {
    	// Deal with async callback
    	// Ensure invocation chains are built...
    	getInvocationChains();
    	// async callback handling
    	if( this.isAsyncInvocation() && !this.getCallbackEndpointReferences().isEmpty() ) {
    		RuntimeEndpointReference asyncEPR = (RuntimeEndpointReference) this.getCallbackEndpointReferences().get(0);
    		// Place a link to the callback EPR into the message headers...
    		msg.getHeaders().put(Constants.ASYNC_CALLBACK, asyncEPR );
    	} 
    	// end of async callback handling
        return invoker.invokeBinding(msg);
    }

    public Object invoke(Operation operation, Object[] args) throws InvocationTargetException {
        return invoker.invoke(operation, args);
    }

    public Message invoke(Operation operation, Message msg) {
        return invoker.invoke(operation, msg);
    }
    
    public void invokeAsync(Message msg){
        invoker.invokeBindingAsync(msg);
    } // end method invokeAsync(Message)
    
    public void invokeAsync(Operation operation, Message msg){
        msg.setOperation(operation);
        invoker.invokeAsync(msg);
    } // end method invokeAsync(Operation, Message)
    
    public void invokeAsyncResponse(Message msg){
        resolve();
        invoker.invokeAsyncResponse(msg);
    }

    /**
     * Navigate the component/componentType inheritance chain to find the leaf contract
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
        InterfaceContract targetContract = getComponentTypeServiceInterfaceContract();
        // setInterfaceContract(targetContract);
        validateServiceInterfaceCompatibility();
        for (Operation operation : sourceContract.getInterface().getOperations()) {
            Operation targetOperation = interfaceContractMapper.map(targetContract.getInterface(), operation);
            if (targetOperation == null) {
                throw new ServiceRuntimeException("No matching operation for " + operation.getName()
                    + " is found in service "
                    + serviceComponent.getURI()
                    + "#"
                    + service.getName());
            }
            InvocationChain chain = new InvocationChainImpl(operation, targetOperation, false, phaseManager, isAsyncInvocation());
            if (operation.isNonBlocking()) {
                addNonBlockingInterceptor(chain);
            }
            addServiceBindingInterceptor(chain, operation);
            addImplementationInterceptor(serviceComponent, service, chain, targetOperation);
            chains.add(chain);
            
            // Handle cases where the operation is an async server 
            if( targetOperation.isAsyncServer() ) {
            	createAsyncServerCallback();
            } // end if
        }

        wireProcessor.process(this);
        
        // If we have to support async and there is no binding chain
        // then set the response path to point directly to the 
        // binding provided async response handler
        if (isAsyncInvocation() && 
            bindingInvocationChain == null){
            // fix up the operation chain response path to point back to the 
            // binding provided async response handler
            ServiceBindingProvider serviceBindingProvider = getBindingProvider();
            if (serviceBindingProvider instanceof EndpointAsyncProvider){
                EndpointAsyncProvider asyncEndpointProvider = (EndpointAsyncProvider)serviceBindingProvider;
                InvokerAsyncResponse asyncResponseInvoker = asyncEndpointProvider.createAsyncResponseInvoker();
                
                for (InvocationChain chain : getInvocationChains()){
                    Invoker invoker = chain.getHeadInvoker();
                    if (invoker instanceof InterceptorAsync){
                        ((InterceptorAsync)invoker).setPrevious(asyncResponseInvoker);
                    } else {
                        //TODO - throw error once the old async code is removed
                    } // end if
                } // end for
            } else {
                // TODO - throw error once the old async code is removed
            } // end if
        } // end if
        
        ServiceBindingProvider provider = getBindingProvider();
        if ((provider != null) && (provider instanceof OptimisingBindingProvider)) {
        	//TODO - remove this comment once optimisation codepath is tested
            ((OptimisingBindingProvider)provider).optimiseBinding( this );
        } // end if

    } // end method initInvocationChains
    
    /**
     * Creates the async callback for this Endpoint, if it does not already exist
     * and stores it into the Endpoint
     */
    public void createAsyncServerCallback( ) {
    	// No need to create a callback if the Binding supports async natively...
    	if( hasNativeAsyncBinding(this) ) return;
    	
    	// Check to see if the callback already exists
    	if( asyncCallbackExists( this ) ) return;
    	
    	RuntimeEndpointReference asyncEPR = createAsyncEPR( this );
    	
    	// Store the new callback EPR into the Endpoint
    	this.getCallbackEndpointReferences().add(asyncEPR);
    	
    	// Also store the callback EPR into the DomainRegistry
    	DomainRegistry epReg = getEndpointRegistry( registry );
    	if( epReg != null ) epReg.addEndpointReference(asyncEPR);
    } // end method createAsyncServerCallback
    
    public RuntimeEndpointReference getAsyncServerCallback() {
    	
    	return (RuntimeEndpointReference) this.getCallbackEndpointReferences().get(0);
    } // end method getAsyncServerCallback
    
    
    /**
     * Indicates if a given endpoint has a Binding that supports native async invocation
     * @param endpoint - the endpoint
     * @return - true if the endpoint has a binding that supports native async, false otherwise
     */
    private boolean hasNativeAsyncBinding(RuntimeEndpoint endpoint) {
    	ServiceBindingProvider provider = endpoint.getBindingProvider();
    	if( provider instanceof EndpointAsyncProvider ) {
    		EndpointAsyncProvider asyncProvider = (EndpointAsyncProvider) provider;
    		if( asyncProvider.supportsNativeAsync()  ) return true;
    	} // end if
		return false;
	} // end method hasNativeAsyncBinding

	/**
     * Creates the Endpoint object for the async callback
     * @param endpoint - the endpoint which has the async server operations
     * @return the EndpointReference object representing the callback
     */
    private RuntimeEndpointReference createAsyncEPR( RuntimeEndpoint endpoint ){
    	CompositeContext compositeContext = endpoint.getCompositeContext();
    	RuntimeAssemblyFactory assemblyFactory = getAssemblyFactory( compositeContext );
        RuntimeEndpointReference epr = (RuntimeEndpointReference)assemblyFactory.createEndpointReference();
        epr.bind( compositeContext );
        
        // Create pseudo-component
        epr.setComponent(component);
        
        // Create pseudo-reference
        ComponentReference reference = assemblyFactory.createComponentReference();
    	  ExtensionPointRegistry registry = compositeContext.getExtensionPointRegistry();
        FactoryExtensionPoint modelFactories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        JavaInterfaceFactory javaInterfaceFactory = (JavaInterfaceFactory)modelFactories.getFactory(JavaInterfaceFactory.class);
        JavaInterfaceContract interfaceContract = javaInterfaceFactory.createJavaInterfaceContract();
        try {
			interfaceContract.setInterface(javaInterfaceFactory.createJavaInterface(AsyncResponseService.class));
		} catch (InvalidInterfaceException e1) {
			// Nothing to do here - will not happen
		} // end try
		reference.setInterfaceContract(interfaceContract);
        String referenceName = endpoint.getService().getName() + "_asyncCallback";
        reference.setName(referenceName);
        reference.setForCallback(true);
        // Add in "implementation" reference (really a dummy, but with correct interface)
        Reference implReference = assemblyFactory.createReference();
        implReference.setInterfaceContract(interfaceContract);
        implReference.setName(referenceName);
        implReference.setForCallback(true);
        
        reference.setReference(implReference);
        // Set the created ComponentReference into the EPR
        epr.setReference(reference);
        
        // Create a binding
		Binding binding = createMatchingBinding( endpoint.getBinding(), (RuntimeComponent)endpoint.getComponent(), reference, registry );			
		epr.setBinding(binding);
		
		// Need to establish policies here (binding has some...)
		epr.getRequiredIntents().addAll( endpoint.getRequiredIntents() );
		epr.getPolicySets().addAll( endpoint.getPolicySets() );
		
		// Attach a dummy endpoint to the epr
		RuntimeEndpoint ep = (RuntimeEndpoint)assemblyFactory.createEndpoint();
		ep.setUnresolved(false);
		epr.setTargetEndpoint(ep);
		//epr.setStatus(EndpointReference.Status.RESOLVED_BINDING);
		epr.setStatus(EndpointReference.Status.WIRED_TARGET_FOUND_AND_MATCHED);
		epr.setUnresolved(false);
		
		// Set the URI for the EPR
		String eprURI = endpoint.getComponent().getName() + "#reference-binding(" + referenceName + "/" + referenceName + ")";
		epr.setURI(eprURI);
        
    	return epr;
    } // end method RuntimeEndpointReference
    
    private boolean asyncCallbackExists( RuntimeEndpoint endpoint ) {
    	if( endpoint.getCallbackEndpointReferences().isEmpty() ) return false;
    	return true;
    } // end method asyncCallbackExists
    
    /**
     * Create a matching binding to a supplied binding
     * - the matching binding has the same binding type, but is for the supplied component and service
     * @param matchBinding - the binding to match
     * @param component - the component 
     * @param service - the service
     * @param registry - registry for extensions
     * @return - the matching binding, or null if it could not be created
     */
    @SuppressWarnings("unchecked")
	private Binding createMatchingBinding( Binding matchBinding, RuntimeComponent component, 
			                               ComponentReference reference, ExtensionPointRegistry registry ) {
    	// Since there is no simple way to obtain a Factory for a binding where the type is not known ahead of
    	// time, the process followed here is to generate the <binding.xxx/> XML element from the binding type QName
    	// and then read the XML using the processor for that XML...
    	QName bindingName = matchBinding.getType();
    	String bindingXML = "<ns1:" + bindingName.getLocalPart() + " xmlns:ns1='" + bindingName.getNamespaceURI() + "'/>";
    	
    	StAXArtifactProcessorExtensionPoint processors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
    	StAXArtifactProcessor<?> processor = (StAXArtifactProcessor<?>)processors.getProcessor(bindingName);
    	
    	FactoryExtensionPoint modelFactories = registry.getExtensionPoint(FactoryExtensionPoint.class);
    	ValidatingXMLInputFactory inputFactory = modelFactories.getFactory(ValidatingXMLInputFactory.class);    		
    	StreamSource source = new StreamSource( new StringReader(bindingXML) );
    	
    	ProcessorContext context = new ProcessorContext();
		try {
			XMLStreamReader reader = inputFactory.createXMLStreamReader(source);
			reader.next();
			Binding newBinding = (Binding) processor.read(reader, context );
			newBinding.setName(reference.getName());
			
			// Create a URI address for the callback based on the Component_Name/Reference_Name pattern
			//String callbackURI = "/" + component.getName() + "/" + reference.getName();
			//newBinding.setURI(callbackURI);
			
			BuilderExtensionPoint builders = registry.getExtensionPoint(BuilderExtensionPoint.class);
			BindingBuilder builder = builders.getBindingBuilder(newBinding.getType());
            if (builder != null) {
            	org.apache.tuscany.sca.assembly.builder.BuilderContext builderContext = new BuilderContext(registry);
            	builder.build(component, reference, newBinding, builderContext, true);
            } // end if
			
			return newBinding;
		} catch (ContributionReadException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
    	
    	return null;
    } // end method createMatchingBinding
    
    /**
     * Gets a RuntimeAssemblyFactory from the CompositeContext
     * @param compositeContext
     * @return the RuntimeAssemblyFactory
     */
    private RuntimeAssemblyFactory getAssemblyFactory( CompositeContext compositeContext ) {
    	ExtensionPointRegistry registry = compositeContext.getExtensionPointRegistry();
        FactoryExtensionPoint modelFactories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        return (RuntimeAssemblyFactory)modelFactories.getFactory(AssemblyFactory.class);
    } // end method RuntimeAssemblyFactory
    
    /**
     * Check that endpoint  has compatible interface at the component and binding ends. 
     * The user can specify the interfaces at both ends so there is a danger that they won't be compatible.
     */
    public void validateServiceInterfaceCompatibility() {
        
        InterfaceContract serviceContract = getComponentServiceInterfaceContract();
        InterfaceContract bindingContract = getBindingInterfaceContract();
                
        if ((serviceContract != bindingContract) && 
            (serviceContract != null) && (bindingContract != null)) {
           
            boolean bindingHasCallback = bindingContract.getCallbackInterface() != null;
            
            try {                                                         
               
                // Use the normalized contract if the interface types are different or if 
                // a normalized contract has been previously generate, for example, by virtue
                // of finding a JAXWS annotation on a Java class that references a WSDL file
                if (serviceContract.getClass() != bindingContract.getClass() ||
                    serviceContract.getNormalizedWSDLContract() != null ||
                    bindingContract.getNormalizedWSDLContract() != null) {
                    interfaceContractMapper.checkCompatibility(getGeneratedWSDLContract(serviceContract), 
                                                               getGeneratedWSDLContract(bindingContract), 
                                                               Compatibility.SUBSET, 
                                                               !bindingHasCallback, // ignore callbacks if binding doesn't have one 
                                                               false);
                } else {
                    interfaceContractMapper.checkCompatibility(serviceContract, 
                                                               bindingContract, 
                                                               Compatibility.SUBSET, 
                                                               !bindingHasCallback, // ignore callbacks if binding doesn't have one 
                                                               false);                   
                }  
            } catch (Exception ex){
                throw new ServiceRuntimeException("Component " +
                                                  this.getComponent().getName() +
                                                  " Service " +
                                                  getService().getName() +
                                                  " interface is incompatible with the interface of the service binding  - " + 
                                                  getBinding().getName() +
                                                  " - " + 
                                                  ex.getMessage() +
                                                  " - [" + this.toString() + "]");
            }
        }
                
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
                Interceptor interceptor = p.createBindingInterceptor();
                if (interceptor != null) {
                    bindingInvocationChain.addInterceptor(interceptor);
                } // end if
            } // end for
        } // end if
        
        // This is strategically placed before the RuntimeInvoker is added to the end of the
        // binding chain as the RuntimeInvoker doesn't need to take part in the response
        // processing and doesn't implement InvokerAsyncResponse
        ServiceBindingProvider serviceBindingProvider = getBindingProvider();
        if (isAsyncInvocation() &&
            serviceBindingProvider instanceof EndpointAsyncProvider &&
            ((EndpointAsyncProvider)serviceBindingProvider).supportsNativeAsync()){
            // fix up the invocation chains to point back to the 
            // binding chain so that async response messages 
            // are processed correctly
            for (InvocationChain chain : getInvocationChains()){
                Invoker invoker = chain.getHeadInvoker();
                ((InterceptorAsync)invoker).setPrevious((InvokerAsyncResponse)bindingInvocationChain.getTailInvoker());
            } // end for
            
            // fix up the binding chain response path to point back to the 
            // binding provided async response handler
            EndpointAsyncProvider asyncEndpointProvider = (EndpointAsyncProvider)serviceBindingProvider;
            InvokerAsyncResponse asyncResponseInvoker = asyncEndpointProvider.createAsyncResponseInvoker();
            ((InterceptorAsync)bindingInvocationChain.getHeadInvoker()).setPrevious(asyncResponseInvoker);
        } // end if
        
        // Add the runtime invoker to the end of the binding chain. 
        // It mediates between the binding chain and selects the 
        // correct invocation chain based on the operation that's
        // been selected
        bindingInvocationChain.addInvoker(invoker); 
        
    } // end method initServiceBindingInvocationChains

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
                    chain.addInterceptor(interceptor);
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
            RuntimeComponentService runtimeService = (RuntimeComponentService)service;
            if (runtimeService.getName().endsWith("_asyncCallback")){
                if (provider instanceof ImplementationAsyncProvider){
                    invoker = (Invoker)((ImplementationAsyncProvider)provider).createAsyncResponseInvoker(operation);
                } else {
                    // TODO - This should be an error but taking account of the 
                    // existing non-native async support
                    invoker = provider.createInvoker((RuntimeComponentService)service, operation); 
/*                    
                    throw new ServiceRuntimeException("Component " +
                            this.getComponent().getName() +
                            " Service " +
                            getService().getName() +
                            " implementation provider doesn't implement ImplementationAsyncProvider but the implementation uses a " + 
                            "refrence interface with the asyncInvocation intent set" +
                            " - [" + this.toString() + "]");
*/
                }
            } else if (isAsyncInvocation() && 
                       provider instanceof ImplementationAsyncProvider){
                invoker = (Invoker)((ImplementationAsyncProvider)provider).createAsyncInvoker((RuntimeComponentService)service, operation);
            } else {
                invoker = provider.createInvoker((RuntimeComponentService)service, operation);
            }
            chain.addInvoker(invoker);
        }

        List<PolicyProvider> pps = ((RuntimeComponent)component).getPolicyProviders();
        if (pps != null) {
            for (PolicyProvider p : pps) {
                Interceptor interceptor = p.createInterceptor(operation);
                if (interceptor != null) {
                    chain.addInterceptor(interceptor);
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
        copy.invoker = new RuntimeInvoker(registry, copy);
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

    public synchronized ServiceBindingProvider getBindingProvider() {
        resolve();
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
        resolve();
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
        return getService();
    }

    public CompositeContext getCompositeContext() {
        return compositeContext;
    }

    @Override
    protected void reset() {
        super.reset();
        this.xml = null;
    }

    @Override
    protected synchronized void resolve() {
        if (xml != null && component == null) {
            if (compositeContext == null) {
                compositeContext = CompositeContext.getCurrentCompositeContext();
                if (compositeContext != null) {
                    bind(compositeContext);
                }
            } // end if
            if (serializer != null) {
                RuntimeEndpointImpl ep = (RuntimeEndpointImpl)serializer.readEndpoint(xml);
                copyFrom(ep);
            } else {
            	// In this case, we assume that we're running on a detached (non Tuscany) thread and
            	// as a result we need to connect back to the Tuscany environment...
                ExtensionPointRegistry registry = ExtensionPointRegistryLocator.getExtensionPointRegistry();
                if( registry != null ) {
                    this.registry = registry;
                    UtilityExtensionPoint utilities = registry.getExtensionPoint(UtilityExtensionPoint.class);
                    this.interfaceContractMapper = utilities.getUtility(InterfaceContractMapper.class);
                    this.serializer = utilities.getUtility(EndpointSerializer.class);
                    RuntimeEndpointImpl ep = (RuntimeEndpointImpl)serializer.readEndpoint(xml);
                    // Find the actual Endpoint in the DomainRegistry
                    ep = findActualEP( ep, registry );
                    if( ep != null ){
                        copyFrom( ep );
                    } // end if
                } // end if
            } // end if
            setNormalizedWSDLContract();
        } // end if
        super.resolve();
    } // end method resolve

    /**
     * Find the actual Endpoint in the DomainRegistry which corresponds to the configuration described
     * in a deserialized Endpoint 
     * @param ep The deserialized endpoint
     * @param registry - the main extension point Registry
     * @return the corresponding Endpoint from the DomainRegistry, or null if no match can be found
     */
    private RuntimeEndpointImpl findActualEP(RuntimeEndpointImpl ep,
			ExtensionPointRegistry registry) {
		DomainRegistry domainRegistry = getEndpointRegistry( registry );
        
        if( domainRegistry == null ) return null;
        
        for( Endpoint endpoint : domainRegistry.findEndpoint(ep.getURI()) ) {
        	// TODO: For the present, simply return the first matching endpoint
        	return (RuntimeEndpointImpl) endpoint;
        } // end for
        
		return null;
	} // end method findActualEP
    
    /**
     * Get the DomainRegistry
     * @param registry - the ExtensionPoint registry
     * @return the DomainRegistry - will be null if the DomainRegistry cannot be found
     */
    private DomainRegistry getEndpointRegistry( ExtensionPointRegistry registry) {
        DomainRegistryFactory domainRegistryFactory = ExtensibleDomainRegistryFactory.getInstance(registry);
        
        if( domainRegistryFactory == null ) return null;
        
        // TODO: For the moment, just use the first (and only!) DomainRegistry...
        DomainRegistry domainRegistry = (DomainRegistry) domainRegistryFactory.getEndpointRegistries().toArray()[0];
    	
    	return domainRegistry;
    } // end method 

	public InterfaceContract getBindingInterfaceContract() {
        resolve();
        if (bindingInterfaceContract != null) {
            return bindingInterfaceContract;
        }
        bindingInterfaceContract = getBindingProvider().getBindingInterfaceContract();
        if (bindingInterfaceContract == null) {
            bindingInterfaceContract = getComponentServiceInterfaceContract();
        }
        if (bindingInterfaceContract == null) {
            bindingInterfaceContract = getComponentTypeServiceInterfaceContract();
        }
        return bindingInterfaceContract;
    }

    public InterfaceContract getComponentTypeServiceInterfaceContract() {
        resolve();
        if (serviceInterfaceContract != null) {
            return serviceInterfaceContract;
        }
        if (service == null) {
            return getComponentServiceInterfaceContract();
        }
        serviceInterfaceContract = getLeafContract(service).getInterfaceContract();
        if (serviceInterfaceContract == null) {
            serviceInterfaceContract = getComponentServiceInterfaceContract();
        }
        return serviceInterfaceContract;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.uri = in.readUTF();
        this.xml = in.readUTF();
/*         
        this.wsdl = in.readUTF();
        this.wsdlCallback = in.readUTF();
*/
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
        
/*        
        if (wsdl == null) {
            wsdl = getWsdl();
        }
        out.writeUTF(wsdl);
        
        if (wsdlCallback == null) {
            wsdlCallback = getWsdlCallback();
        }
        out.writeUTF(wsdlCallback);
*/        
    }
    
    public String getAsXML() {
        if (xml == null) {
            this.xml = serializer.write(this);
        }
        return xml;
    }
    
    private String getWsdl() {       
        InterfaceContract ic = getComponentServiceInterfaceContract();
        if (ic == null || ic.getInterface() == null || !ic.getInterface().isRemotable()) {
            return "";
        }
        WSDLInterfaceContract wsdlIC = (WSDLInterfaceContract)getGeneratedWSDLContract(ic);
        if (wsdlIC == null) {
            return "";
        }
        
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        
        try {
            // write out a flattened WSDL along with XSD
            WSDLInterface wsdl = (WSDLInterface)wsdlIC.getInterface();
            WSDLDefinition wsdlDefinition = wsdl.getWsdlDefinition();
            writeWSDL(outStream, wsdlDefinition);      
        } catch (Exception e){
            throw new RuntimeException(e);
        }

        String wsdlString = outStream.toString();
                        
        return wsdlString;
    }
    
    private String getWsdlCallback() {       
        InterfaceContract ic = getComponentServiceInterfaceContract();
        if (ic == null || ic.getCallbackInterface() == null || !ic.getCallbackInterface().isRemotable()) {
            return "";
        }
        WSDLInterfaceContract wsdlIC = (WSDLInterfaceContract)getGeneratedWSDLContract(ic);
        if (wsdlIC == null) {
            return "";
        }
        
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        
        try {
            // write out a flattened Callback WSDL along with XSD
            WSDLInterface wsdl = (WSDLInterface)wsdlIC.getCallbackInterface();
            WSDLDefinition wsdlDefinition = wsdl.getWsdlDefinition();
            writeWSDL(outStream, wsdlDefinition);      
        } catch (Exception e){
            throw new RuntimeException(e);
        }

        String wsdlString = outStream.toString();
                        
        return wsdlString;
    }
    
    /**
     * Write the WSDL followed by all it's XSD 
     * 
     * @param outStream
     * @param wsdlDefinition
     * @throws IOException
     * @throws WSDLException
     */
    private void writeWSDL(OutputStream outStream, WSDLDefinition wsdlDefinition) throws IOException, WSDLException {
        Definition definition = wsdlDefinition.getDefinition();
        WSDLWriter writer = WSDLFactory.newInstance().newWSDLWriter();
        String baseURI = null;
        if (wsdlDefinition.getLocation() != null) {
            baseURI = wsdlDefinition.getLocation().toString();
        } else {
            baseURI = "generated.wsdl";
        }
        outStream.write(baseURI.getBytes());
        outStream.write(separator);            
        writer.writeWSDL(definition, outStream);
        for (WSDLDefinition importedWSDLDefintion : wsdlDefinition.getImportedDefinitions()){
            outStream.write(separator);
            baseURI = importedWSDLDefintion.getLocation().toString();
            outStream.write(baseURI.getBytes());
            outStream.write(separator);
            writer.writeWSDL(importedWSDLDefintion.getDefinition(), outStream);
        }
        for (XSDefinition xsdDefinition : wsdlDefinition.getXmlSchemas()){
            // we store a reference to the schema schema. We don't need to write that out.
            if (!xsdDefinition.getNamespace().equals("http://www.w3.org/2001/XMLSchema") &&
                xsdDefinition.getSchema() != null){
                writeSchema(outStream, xsdDefinition.getSchema());
            }
        }  
    }
    
    /**
     * Write an XSD
     * 
     * @param outStream
     * @param schema
     * @throws IOException
     */
    private void writeSchema(OutputStream outStream, XmlSchema schema) throws IOException {
        // TODO - this doesn't write schema in the non-namespace namespace
        if (schema != null &&
/*                
            schema.getTargetNamespace() != null &&
            !schema.getTargetNamespace().equals("http://www.w3.org/2001/XMLSchema") &&
*/            
            schema.getNamespaceContext() != null){ 
            outStream.write(separator);
            String baseURI = schema.getSourceURI();
            outStream.write(baseURI.getBytes());
            outStream.write(separator);
            schema.write(outStream);
            
            for (Iterator<?> i = schema.getIncludes().getIterator(); i.hasNext();) {
                XmlSchemaObject obj = (XmlSchemaObject)i.next();
                XmlSchema ext = null;
                if (obj instanceof XmlSchemaInclude) {
                    ext = ((XmlSchemaInclude)obj).getSchema();
                }
                if (obj instanceof XmlSchemaImport) {
                    ext = ((XmlSchemaImport)obj).getSchema();
                }
                writeSchema(outStream, ext);
            } 
        }
    }  
    
    private void setNormalizedWSDLContract() {
        if (wsdl == null || wsdl.length() < 1) {
            return;
        }
        InterfaceContract ic = getComponentServiceInterfaceContract();
        if (ic != null) {
            ic.setNormalizedWSDLContract(WSDLHelper.createWSDLInterfaceContract(registry, wsdl, wsdlCallback));
        }
    }

    public InterfaceContract getGeneratedWSDLContract(InterfaceContract interfaceContract) {

        if ( interfaceContract.getNormalizedWSDLContract() == null){
            if (getComponentServiceInterfaceContract() instanceof JavaInterfaceContract){
                if (contractBuilder == null){
                    throw new ServiceRuntimeException("Contract builder not found while calculating WSDL contract for " + this.toString());
                }
                contractBuilder.build(interfaceContract, null);
            }
        }
        
        return interfaceContract.getNormalizedWSDLContract();      
    }    
    
    @Override
    public RuntimeEndpoint getDelegateEndpoint() {
        return delegateEndpoint;
    }
    
    @Override
    public void setDelegateEndpoint(RuntimeEndpoint delegateEndpoint) {
        this.delegateEndpoint = delegateEndpoint;
    }
}
