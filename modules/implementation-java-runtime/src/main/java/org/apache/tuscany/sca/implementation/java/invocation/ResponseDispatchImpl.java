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

package org.apache.tuscany.sca.implementation.java.invocation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.context.CompositeContext;
import org.apache.tuscany.sca.context.ThreadMessageContext;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.factory.ObjectFactory;
import org.apache.tuscany.sca.core.invocation.AsyncFaultWrapper;
import org.apache.tuscany.sca.core.invocation.AsyncResponseHandler;
import org.apache.tuscany.sca.core.invocation.CallbackReferenceObjectFactory;
import org.apache.tuscany.sca.core.invocation.ExtensibleProxyFactory;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;
import org.oasisopen.sca.ResponseDispatch;
import org.oasisopen.sca.ServiceReference;

/**
 * Implementation of the ResponseDispatch interface of the OASIS SCA Java API
 * 
 * This is used for invocations of asynchronous services, where it is passed as a parameter on async service operations
 * and it provides the path by which the service implementation returns the response to the request, or a Fault
 * 
 * Note that this class is serializable and can be serialized, stored and deserialized by the service implementation
 *
 * @param <T> - type of the response message
 */
public class ResponseDispatchImpl<T> implements ResponseDispatch<T>, Serializable {

	/**
	 * Generated serialVersionUID value
	 */
	private static final long serialVersionUID = 300158355992568592L;
    private static String WS_MESSAGE_ID = "WS_MESSAGE_ID";
    private static String MESSAGE_ID = "MESSAGE_ID";
	
	// A latch used to ensure that the sendResponse() and sendFault() operations are used at most once
	// The latch is initialized with the value "false"
	private AtomicBoolean latch = new AtomicBoolean();
	
	private final Lock lock = new ReentrantLock();
    private final Condition completed  = lock.newCondition(); 
	
	// The result
	private volatile T response = null;
	private volatile Throwable fault = null; 
	
	private ExtensionPointRegistry registry;
	
	// Service Reference used for the callback
	private ServiceReference<AsyncResponseHandler<?>> callbackRef;
	private String messageID;
	
	public ResponseDispatchImpl( Message msg ) {
		super();
		callbackRef = getAsyncCallbackRef( msg );
		
		// TODO - why is WS stuff bleeding into general code?
    	messageID = (String) msg.getHeaders().get(MESSAGE_ID);
    	if (messageID == null){
    	    messageID = (String) msg.getHeaders().get(WS_MESSAGE_ID);
    	}
    	
	} // end constructor
	
	public static <T> ResponseDispatchImpl<T> newInstance( Class<T> type, Message msg ) {
		return new ResponseDispatchImpl<T>( msg );
	}
	
	/**
	 * Provide Context data for this ResponseDispatch that the service implementation can use
	 */
	public Map<String, Object> getContext() {
		return null;
	}

	/**
	 * Send a Fault.  Must only be invoked once for this ResponseDispatch object
	 * @param e - the Fault to send
	 * @throws IllegalStateException if either the sendResponse method or the sendFault method have been called previously
	 */
	public void sendFault(Throwable e) {
		if( sendOK() ) {
			lock.lock();
			try {
				fault = e;
				completed.signalAll();
			} finally {
				lock.unlock();
			} // end try
		} else {
			throw new IllegalStateException("sendResponse() or sendFault() has been called previously");
		} // end if
		// Now dispatch the response to the callback...
		AsyncResponseHandler<T> handler = (AsyncResponseHandler<T>) callbackRef.getService();
		setResponseHeaders();
		handler.setFault(new AsyncFaultWrapper(e));
	} // end method sendFault

	/**
	 * Send the response message.  Must only be invoked once for this ResponseDispatch object
	 * @throws IllegalStateException if either the sendResponse method or the sendFault method have been called previously
	 * @param res - the response message, which is of type T
	 */
	public void sendResponse(T res) {
		if( sendOK() ) {
			lock.lock();
			try {
				response = res;
				completed.signalAll();
			} finally {
				lock.unlock();
			} // end try
		} else {
			throw new IllegalStateException("sendResponse() or sendFault() has been called previously");
		} // end if
		// Now dispatch the response to the callback...
		AsyncResponseHandler<T> handler = (AsyncResponseHandler<T>) callbackRef.getService();
		setResponseHeaders();
		handler.setResponse(res);
	} // end method sendResponse
	
	public T get(long timeout, TimeUnit unit) throws Throwable {
		lock.lock();
		try {
			// wait for result to be available
			if( response == null && fault == null ) completed.await( timeout, unit);
			if( response != null ) return response;
			if( fault != null ) throw fault;
		} finally {
			lock.unlock();
		} // end try

		return null;
	} // end method get

	/**
	 * Indicates that sending a response is OK - this is a transactional
	 * query in that it also updates the state of this ResponseDispatch, so
	 * that it will return true once and once only
	 * @return - true if it is OK to send the response, false otherwise
	 */
	private boolean sendOK() {
		return latch.compareAndSet(false, true);
	}
	
	/**
	 * Creates a service reference for the async callback, based on information contained in the supplied message
	 * @param msg - the incoming message
	 * @return - a CallBackServiceReference
	 */
	@SuppressWarnings("unchecked")
	private ServiceReference<AsyncResponseHandler<?>> getAsyncCallbackRef( Message msg ) { 
    	RuntimeEndpointReference callbackEPR = (RuntimeEndpointReference) msg.getHeaders().get("ASYNC_CALLBACK");
    	if( callbackEPR == null ) return null;
    	
    	CompositeContext compositeContext = callbackEPR.getCompositeContext();
        registry = compositeContext.getExtensionPointRegistry();
    	ProxyFactory proxyFactory = ExtensibleProxyFactory.getInstance(registry);
    	List<EndpointReference> eprList = new ArrayList<EndpointReference>();
    	eprList.add(callbackEPR);
    	ObjectFactory<?> factory = new CallbackReferenceObjectFactory(AsyncResponseHandler.class, proxyFactory, eprList);
    	
    	return (ServiceReference<AsyncResponseHandler<?>>) factory.getInstance();
    	
    } // end method getAsyncCallbackEPR
	
	/**
	 * Sets the values of various headers in the response message
	 */
	private void setResponseHeaders() {
		// Is there an existing message context?
		Message msgContext = ThreadMessageContext.getMessageContext();
		if( msgContext == null ) {
			// Create a message context
			msgContext = getMessageFactory().createMessage();
		} // end if
		
		// Add in the header for the RelatesTo Message ID
		msgContext.getHeaders().put(WS_MESSAGE_ID, messageID);
		msgContext.getHeaders().put(MESSAGE_ID, messageID);
		
		ThreadMessageContext.setMessageContext(msgContext);
	} // end method setResponseHeaders
	
	private MessageFactory getMessageFactory() {
        FactoryExtensionPoint modelFactories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        return modelFactories.getFactory(MessageFactory.class);
	} // end method getMessageFactory
}
