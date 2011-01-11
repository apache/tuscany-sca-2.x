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

package org.apache.tuscany.sca.core.invocation.impl;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.core.invocation.AsyncFaultWrapper;
import org.apache.tuscany.sca.core.invocation.AsyncResponseHandler;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.policy.ExtensionType;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.provider.ImplementationProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

/**
 * A class intended to form the final link in the chain calling into a Future which represents
 * the response to an asynchronous service invocation
 * 
 * Most methods are dummies, required to fulfil the contracts for ImplementationProvider, Implementation
 * and Invoker, since this class collapses together the functions of these separate interfaces, due to its
 * specialized nature, where most of the function will never be used.
 * 
 * The class acts as the implementation object that terminates the chain - and also as the provider of the implementation.
 * The class accepts Future objects which represent individual invocations of forward operations on the async service
 * and expects that the responses it handles as invocations will carry the unique ID of one of the Future objects in the
 * message header.  On receipt of each message, the class seeks out the Future with that unique ID and completes the future
 * either with a response message or with a Fault.
 *
 * @param <V>  
 */
public class AsyncResponseHandlerImpl<V> implements AsyncResponseHandler<V>,
		ImplementationProvider, Implementation, Invoker {

	private ConcurrentHashMap< String, AsyncInvocationFutureImpl<?> > table = 
		new ConcurrentHashMap< String, AsyncInvocationFutureImpl<?> >();
	
	/**
	 * This class is its own invoker...
	 */
	public Invoker createInvoker(RuntimeComponentService service,
			Operation operation) {
		return this;
	}
	
	/**
	 * Add a future to this response handler
	 * @param future - the future
	 */
	public void addFuture( AsyncInvocationFutureImpl<?> future ) {
		// The Future is stored in the table indexed by its unique ID
		table.put(future.getUniqueID(), future);		
	} // end method addFuture

	public boolean supportsOneWayInvocation() {
		return true;
	}

	public void start() {}

	public void stop() {}

	public List<Operation> getOperations() {
		return null;
	}

	public QName getType() {
		return null;
	}

	public List<Property> getProperties() {
		return null;
	}

	public Property getProperty(String name) {
		return null;
	}

	public Reference getReference(String name) {
		return null;
	}

	public List<Reference> getReferences() {
		return null;
	}

	public Service getService(String name) {
		return null;
	}

	public List<Service> getServices() {
		return null;
	}

	public String getURI() {
		return null;
	}

	public void setURI(String uri) {}

	public boolean isUnresolved() {
		return false;
	}

	public void setUnresolved(boolean unresolved) {}

	public ExtensionType getExtensionType() {
		return null;
	}

	public List<PolicySet> getPolicySets() {
		return null;
	}

	public List<Intent> getRequiredIntents() {
		return null;
	}

	public void setExtensionType(ExtensionType type) {}

	public void setWrappedFault(AsyncFaultWrapper e) {}
	
	public void setFault(Throwable e) {}

	public void setResponse(V res) { }

	/**
	 * Method which is the termination for the invocation chain from the callback endpoint
	 * @param msg - the Tuscany message containing the response from the async service invocation
	 * which is either the Response message or an exception of some kind
	 */
    private static final String WS_MESSAGE_ID = "WS_MESSAGE_ID";
    public Message invoke(Message msg) {
		// Get the unique ID from the message header
		String idValue = (String)msg.getHeaders().get(WS_MESSAGE_ID);
		if (idValue == null){
		    idValue = (String)msg.getHeaders().get("MESSAGE_ID");
		}
		
		if( idValue == null ) { 
			System.out.println( "Async message ID not found ");
		} else {
			// Fetch the Future with that Unique ID
			AsyncInvocationFutureImpl future = table.get(idValue);
			if( future == null ) {
				System.out.println("Future not found for id: " + idValue);
			} else {	
				// Complete the Future with a Response message
				Object payload = msg.getBody();
				Object response;
				if( payload == null ) {
					System.out.println("Returned response message was null");
				} else {
		            if (payload.getClass().isArray()) {
		                response = ((Object[])payload)[0];
		            } else {
		                response = payload;
		            } // end if
		            if( response.getClass().equals(AsyncFaultWrapper.class)) {
		            	future.setWrappedFault((AsyncFaultWrapper) response );
		            } else {
		            	future.setResponse(response);
		            } // end if
				} // end if
			} // end if
		} // end if
		
		// Prepare an empty response message
		msg.setBody(null);
		return msg;
	} // end method invoke

} // end class 
