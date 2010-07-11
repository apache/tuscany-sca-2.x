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

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.xml.ws.Response;

import org.apache.tuscany.sca.core.invocation.AsyncFaultWrapper;
import org.apache.tuscany.sca.core.invocation.AsyncResponseHandler;

/**
 * A class which provides an Implementation of a Future<V> and Response<V> for use with the JAXWS defined client
 * asynchronous APIs.
 * 
 * This implementation class provides the interfaces for use by the client code, but also provides methods for the
 * Tuscany system code to set the result of the asynchronous service invocation, both Regular and Fault responses. 
 * 
 * This class is constructed to be fully thread-safe
 *
 * @param <V> - this is the type of the response message from the invoked service.
 */
public class AsyncInvocationFutureImpl<V> implements Future<V>, Response<V>, AsyncResponseHandler<V> {
	
	// Lock for handling the completion of this Future
	private final Lock lock = new ReentrantLock();
    private final Condition isDone  = lock.newCondition(); 
    
	// The result
	private volatile V response = null;
	private volatile Throwable fault = null; 
	
	private String uniqueID = UUID.randomUUID().toString();
	
	private ClassLoader classLoader = null;
	
	protected AsyncInvocationFutureImpl() {
		super();
	} // end constructor
	
	/**
	 * Public constructor for AsyncInvocationFutureImpl - newInstance is necessary in order to enable the Type variable
	 * to be set for the class instances
	 * @param <V> - the type of the response from the asynchronously invoked service
	 * @param type - the type of the AsyncInvocationFutureImpl expressed as a parameter
	 * @param classLoader - the classloader used for the business interface to which this Future applies
	 * @return - an instance of AsyncInvocationFutureImpl<V>
	 */
	public static <V> AsyncInvocationFutureImpl<V> newInstance( Class<V> type, ClassLoader classLoader ) {
		AsyncInvocationFutureImpl<V> future = new AsyncInvocationFutureImpl<V>();
		future.setClassLoader( classLoader );
		return future;
	}

	/**
	 * Cancels the asynchronous process
	 * - not possible in this version, so always returns false
	 */
	public boolean cancel(boolean mayInterruptIfRunning) {
		return false;
	}

	/**
	 * Gets the response value returned by the asynchronous process
	 * - waits forever
	 * @return - the response value of type V
	 * @throws InterruptedException if the get() method was interrupted while waiting for the async process to finish
	 * @throws ExecutionException if the async process threw an exception - the exception thrown is nested
	 */
	public V get() throws InterruptedException, ExecutionException {
		try {
			V response = get(Long.MAX_VALUE, TimeUnit.SECONDS);
			return response;
		} catch (TimeoutException t) {
			throw new InterruptedException("Timed out waiting for Future to complete");
		} // end try
	} // end method get()

	/**
	 * Gets the response value returned by the asynchronous process
	 * @return - the response value of type V
	 * @throws InterruptedException if the get() method was interrupted while waiting for the async process to finish
	 * @throws ExecutionException if the async process threw an exception - the exception thrown is nested
	 * @throws TimeoutException if the get() method timed out waiting for the async process to finish
	 */
	public V get(long timeout, TimeUnit unit) throws InterruptedException,
			ExecutionException, TimeoutException {
		lock.lock();
		try {
			// wait for result to be available
			if( notSetYet() ) isDone.await( timeout, unit);
			if( response != null ) return response;
			if( fault != null ) throw new ExecutionException( fault );
			throw new TimeoutException("get on this Future timed out");
		} finally {
			lock.unlock();
		} // end try

	} // end method get(long timeout, TimeUnit unit)

	/**
	 * Indicates if the asynchronous process has been cancelled
	 * - not possible in this version so always returns false
	 */
	public boolean isCancelled() {
		return false;
	}

	/**
	 * Indicates if the asynchronous process is completed
	 * @return - true if the process is completed, false otherwise
	 */
	public boolean isDone() {
		lock.lock();
		try {
			return !notSetYet();
		} finally {
			lock.unlock();
		} // end try
	} // end method isDone
	
	/**
	 * Async process completed with a Fault.  Must only be invoked once
	 * @param e - the Fault to send
	 * @throws IllegalStateException if either the setResponse method or the setFault method have been called previously
	 */
	public void setFault(AsyncFaultWrapper w) {

		ClassLoader tccl = Thread.currentThread().getContextClassLoader();
		Throwable e;
		try {
			 // Set the TCCL to the classloader of the business interface
            Thread.currentThread().setContextClassLoader(this.getClassLoader());
			e = w.retrieveFault();
		} finally {
			Thread.currentThread().setContextClassLoader(tccl);
		} // end try
		
		if( e == null ) throw new IllegalArgumentException("AsyncFaultWrapper did not return an Exception");
		lock.lock();
		try {
			if( notSetYet() ) {
				fault = e;
				isDone.signalAll();
			} else {
				throw new IllegalStateException("setResponse() or setFault() has been called previously");
			} // end if 
		} finally {
			lock.unlock();
		} // end try

	} // end method setFault

	/**
	 * Async process completed with a response message.  Must only be invoked once
	 * @throws IllegalStateException if either the setResponse method or the setFault method have been called previously
	 * @param res - the response message, which is of type V
	 */
	public void setResponse(V res) {
		
		lock.lock();
		try {
			if( notSetYet() ) {
				response = res;
				isDone.signalAll();
			} else {
				throw new IllegalStateException("setResponse() or setFault() has been called previously");
			}
		} finally {
			lock.unlock();
		} // end try
			
	} // end method setResponse
	
	/**
	 * Gets the unique ID of this future as a String
	 */
	public String getUniqueID() { return uniqueID; }

	/**
	 * Indicates that setting a response value is OK - can only set the response value or fault once
	 * @return - true if it is OK to set the response, false otherwise
	 */
	private boolean notSetYet() {
		return ( response == null && fault == null );
	}
	
	/**
	 * Returns the JAXWS context for the response
	 * @return - a Map containing the context
	 */
	public Map<String, Object> getContext() {
		// Intentionally returns null
		return null;
	}
	
	/**
	 * Gets the classloader associated with the business interface to which this Future relates
	 * @return the ClassLoader of the business interface
	 */
	public ClassLoader getClassLoader() {
		return classLoader;
	}

	/**
	 * Sets the classloader associated with the business interface to which this Future relates
	 * @param classLoader - the classloader of the business interface
	 */
	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}


} // end class AsyncInvocationFutureImpl
