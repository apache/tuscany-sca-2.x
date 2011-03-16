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

package org.apache.tuscany.sca.core.invocation;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.context.CompositeContext;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistryLocator;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.InvokerAsyncResponse;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.provider.EndpointAsyncProvider;
import org.apache.tuscany.sca.runtime.DomainRegistryFactory;
import org.apache.tuscany.sca.runtime.EndpointRegistry;
import org.apache.tuscany.sca.runtime.ExtensibleDomainRegistryFactory;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;

/**
 * A class that wraps the mechanics for sending async responses
 * and hides the decision about whether the response will be processed
 * natively or non-natively
 * 
 * This class is generic, based on the type of targetAddress information required by
 * the Binding that creates it
 */
public class AsyncResponseInvoker<T> implements InvokerAsyncResponse, Serializable {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -7992598227671386588L;
	
	private transient RuntimeEndpoint requestEndpoint;
    private transient RuntimeEndpointReference responseEndpointReference; 
    private T responseTargetAddress;
    private String relatesToMsgID;
    private String operationName;
    private transient MessageFactory messageFactory;
    private String bindingType = "";
    private boolean isNativeAsync;
    
    private String endpointURI;
    private String endpointReferenceURI;
    private String domainURI;

	private transient EndpointRegistry endpointRegistry;
	private transient ExtensionPointRegistry registry;
    
    public AsyncResponseInvoker(RuntimeEndpoint requestEndpoint,
			RuntimeEndpointReference responseEndpointReference,
			T responseTargetAddress, String relatesToMsgID, 
			String operationName, MessageFactory messageFactory) {
		super();
		this.requestEndpoint = requestEndpoint;
		this.responseEndpointReference = responseEndpointReference;
		this.responseTargetAddress = responseTargetAddress;
		this.relatesToMsgID = relatesToMsgID;
		this.operationName = operationName;
		this.messageFactory = messageFactory;
		
		CompositeContext context = null;
		if(requestEndpoint != null ) {
			endpointURI = requestEndpoint.getURI();
			context = requestEndpoint.getCompositeContext(); 
		} // end if
		if(responseEndpointReference != null ) {
			endpointReferenceURI = responseEndpointReference.getURI();
			context = responseEndpointReference.getCompositeContext();
		}
		
		if( context != null ) {
			domainURI = context.getDomainURI();
			registry = context.getExtensionPointRegistry();
		} // end if
		
        if ((requestEndpoint.getBindingProvider() instanceof EndpointAsyncProvider) &&
                (((EndpointAsyncProvider)requestEndpoint.getBindingProvider()).supportsNativeAsync())){
        	isNativeAsync = true;
        } else {
        	isNativeAsync = false;
        } // end if
	} // end constructor

    /** 
     * If you have a Tuscany message you can call this
     */
    public void invokeAsyncResponse(Message responseMessage) {
    	responseMessage.getHeaders().put(Constants.ASYNC_RESPONSE_INVOKER, this);
    	responseMessage.getHeaders().put(Constants.RELATES_TO, relatesToMsgID);
    	
        if (isNativeAsync){
            // process the response as a native async response
            requestEndpoint.invokeAsyncResponse(responseMessage);
        } else {
            // process the response as a non-native async response
            responseEndpointReference.invoke(responseMessage);
        }
    } // end method invokeAsyncReponse(Message)
    
    public T getResponseTargetAddress() {
		return responseTargetAddress;
	}

	public void setResponseTargetAddress(T responseTargetAddress) {
		this.responseTargetAddress = responseTargetAddress;
	}

	public String getRelatesToMsgID() {
		return relatesToMsgID;
	}

	public void setRelatesToMsgID(String relatesToMsgID) {
		this.relatesToMsgID = relatesToMsgID;
	}

	/**
     * Invokes the async response where the parameter is Java bean(s) 
     * - this method creates a Tuscany message
     * 
     * @param args the response data
     * @param headers - any header 
     */
    public void invokeAsyncResponse(Object args, Map<String, Object> headers) {
        
        Message msg = messageFactory.createMessage();

        msg.setOperation(getOperation( args ));
        
        // If this is not native async, then any Throwable is being passed as a parameter and
        // requires wrapping
        if( !isNativeAsync && args instanceof Throwable ) {
        	args = new AsyncFaultWrapper( (Throwable) args ); 
        } // end if
        
        // If this is not native async, then the message must contain an array of args since
        // this is what is expected when invoking an EPR for the async response...
        if( !isNativeAsync ) {
        	Object[] objs = new Object[1];
        	objs[0] = args;
        	args = objs;
        } // end if

        msg.setTo(requestEndpoint);
        msg.setFrom(responseEndpointReference);
        
        if( headers != null ) {
        	msg.getHeaders().putAll(headers);
        }
        
        if( args instanceof Throwable ) {
        	msg.setFaultBody(args);
        } else {
        	msg.setBody(args);
        } // end if
        
        invokeAsyncResponse(msg);
        
    } // end method invokeAsyncResponse(Object)

	private Operation getOperation( Object args ) {
		if( isNativeAsync ) {
			List<Operation> ops = requestEndpoint.getService().getInterfaceContract().getInterface().getOperations();
			for (Operation op : ops) {
				if( operationName.equals(op.getName()) ) return op;
			} // end for
			return null;
		} else {
			operationName = "setResponse";
			if( args instanceof Throwable ) { operationName = "setWrappedFault"; }
			List<Operation> ops = responseEndpointReference.getReference().getInterfaceContract().getInterface().getOperations();
			for (Operation op : ops) {
				if( operationName.equals(op.getName()) ) return op;
			} // end for
			return null;
		} // end if 
	} // end getOperation

	public void setBindingType(String bindingType) {
		this.bindingType = bindingType;
	} // end method setBindingType

	public String getBindingType() {
		return bindingType;
	} // end method getBindingType

    public RuntimeEndpoint getRequestEndpoint() {
	return this.requestEndpoint;
    }

    public RuntimeEndpointReference getResponseEndpointReference() {
	return this.responseEndpointReference;
    }

	public void setResponseEndpointReference(
			RuntimeEndpointReference responseEndpointReference) {
		this.responseEndpointReference = responseEndpointReference;
    }
	
    @SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
	    in.defaultReadObject();
	    
	    requestEndpoint = retrieveEndpoint(endpointURI);
	    responseEndpointReference = retrieveEndpointReference(endpointReferenceURI);
	    
	    messageFactory = getMessageFactory();
	    
	    if (responseTargetAddress instanceof EndpointReference){
	        // fix the target as in this case it will be an EPR
	        EndpointReference epr = (EndpointReference)responseTargetAddress;
	        responseTargetAddress = (T)retrieveEndpointReference(epr.getURI());
	    } // end if    
    } // end method readObject	

    /**
     * Gets a message factory
     * @return
     */
    private MessageFactory getMessageFactory() {
    	return registry.getExtensionPoint(FactoryExtensionPoint.class).getFactory(MessageFactory.class);
	} // end method getMessageFactory

	/**
     * Fetches the EndpointReference identified by an endpoint reference URI
     * @param uri - the URI of the endpoint reference
     * @return - the EndpointReference matching the supplied URI - null if no EPR is found which
     * matches the URI
     */
    private RuntimeEndpointReference retrieveEndpointReference(String uri) {
		if( uri == null ) return null;
    	if( endpointRegistry == null ) return null;
		List<EndpointReference> refs = endpointRegistry.findEndpointReferences( uri );
		// If there is more than EndpointReference with the uri...
		if( refs.isEmpty() ) return null;
		// TODO: what if there is more than 1 EPR with the given URI?
		return (RuntimeEndpointReference) refs.get(0);
	} // end method retrieveEndpointReference

	/**
     * Fetches the Endpoint identified by an endpoint URI
     * - the Endpoint is retrieved from the EndpointRegistry 
     * @param uri - the URI of the Endpoint
     * @return - the Endpoint corresponding to the URI, or null if no Endpoint is found which has the
     * supplied URI
     */
	private RuntimeEndpoint retrieveEndpoint(String uri) {
		if( uri == null ) return null;
		if( endpointRegistry == null ) endpointRegistry = getEndpointRegistry( uri );
		if( endpointRegistry == null ) return null;
		// TODO what if more than one Endpoint gets returned??
		return (RuntimeEndpoint) endpointRegistry.findEndpoint(uri).get(0);
	} // end method retrieveEndpoint

	/**
	 * Gets the EndpointRegistry which contains an Endpoint with the supplied URI
	 * @param uri - The URI of an Endpoint
	 * @return - the EndpointRegistry containing the Endpoint with the supplied URI - null if no
	 *           such EndpointRegistry can be found
	 */
	private EndpointRegistry getEndpointRegistry(String uri) {
		ExtensionPointRegistry registry   = null;
		EndpointRegistry endpointRegistry = null;
		
		CompositeContext context = CompositeContext.getCurrentCompositeContext();
		if( context == null && requestEndpoint != null ) context = requestEndpoint.getCompositeContext();
		if( context != null ) {
			registry = context.getExtensionPointRegistry();
			endpointRegistry = getEndpointRegistry( registry );
			if( endpointRegistry != null ) {
				this.registry = registry;
				return endpointRegistry;
			} // end if
		} // end if
		
		// Deal with the case where there is no context available
    	for(ExtensionPointRegistry r : ExtensionPointRegistryLocator.getExtensionPointRegistries()) {
                registry = r;
    		if( registry != null ) {
    			// Find the actual Endpoint in the EndpointRegistry
        		endpointRegistry = getEndpointRegistry( registry );
                
                if( endpointRegistry != null ) {
                    for( Endpoint endpoint : endpointRegistry.findEndpoint(uri) ) {
                    	// TODO: For the present, simply return the first registry with a matching endpoint
                    	this.registry = registry;
                    	return endpointRegistry;
                    } // end for
                } // end if 
    		} // end if
        } // end for
		
		return null;
	} // end method getEndpointRegistry
	
    /**
     * Get the EndpointRegistry
     * @param registry - the ExtensionPoint registry
     * @return the EndpointRegistry - will be null if the EndpointRegistry cannot be found
     */
    private EndpointRegistry getEndpointRegistry( ExtensionPointRegistry registry) {
        DomainRegistryFactory domainRegistryFactory = ExtensibleDomainRegistryFactory.getInstance(registry);
        
        if( domainRegistryFactory == null ) return null;
        
        // Find the first endpoint registry that matches the domain name 
        if( domainURI != null ) {
	        for( EndpointRegistry endpointRegistry : domainRegistryFactory.getEndpointRegistries() ) {
	        	if( domainURI.equals( endpointRegistry.getDomainURI() ) ) return endpointRegistry;
	        } // end for
        } // end if
        
        // if there was no domainName to match, simply return the first EndpointRegistry if there is one...
        
        if (domainRegistryFactory.getEndpointRegistries().size() > 0){
            EndpointRegistry endpointRegistry = (EndpointRegistry) domainRegistryFactory.getEndpointRegistries().toArray()[0];
            return endpointRegistry;
        } else {
            return null;
        }
        
    } // end method 
	
} // end class
