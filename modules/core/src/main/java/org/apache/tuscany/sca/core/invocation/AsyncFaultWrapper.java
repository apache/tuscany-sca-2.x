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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * A class which is used to wrap an Exception of any type thrown by an asynchronous service operation and
 * which is returned through a separate one-way message sent asynchronously from the server to the client.
 *
 */
public class AsyncFaultWrapper {
	
	private String faultClassName = null;
	private String faultMessage = null;
	private AsyncFaultWrapper containedFault = null;
	

	public AsyncFaultWrapper() {
		super();
	}
	
	/**
	 * Constructor which creates an AsyncFaultWrapper which wraps the supplied Throwable
	 * @param e - a Throwable which is wrapped by this AsyncFaultWrapper
	 */
	public AsyncFaultWrapper( Throwable e ) {
		super();
		storeFault( e );
	}
	
	/**
	 * Stores a given Throwable in this AsyncFaultWrapper
	 * If the supplied Throwable itself contains an embedded Throwable ("cause"), this is recursively
	 * wrapped by a nested AsyncFaultWrapper
	 * @param e - the Throwable
	 */
	public void storeFault( Throwable e ) {
		setFaultClassName( e.getClass().getCanonicalName() );
		setFaultMessage( e.getMessage() );
		Throwable cause = e.getCause();
		if( cause != null ) setContainedFault( new AsyncFaultWrapper( cause ) );
	}
	
	/**
	 * Retrieves the Throwable wrapped by this AsyncFaultWrapper
	 * 
	 * Note: When this method is invoked, the method attempts to instantiate an instance of the wrapped Throwable.
	 * It does this using the Thread Context Class Loader (TCCL) - the caller *MUST* ensure that the TCCL has access
	 * to the class of the wrapped Throwable and also to the classes of any nested Throwables.  If this is not done,
	 * a ClassNotFound exception is thrown
	 * 
	 * @return - the Throwable wrapped by this AsyncFaultWrapper - the Throwable will contain any nested Throwable(s)
	 * in its cause property
	 * @throws ClassNotFound exception, if the class of the wrapped Throwable is not accessible from the TCCL
	 */
	public Throwable retrieveFault( ) {
		try {
			ClassLoader tccl = Thread.currentThread().getContextClassLoader();
			Class<?> faultClass = tccl.loadClass(faultClassName);
			Class<Throwable> xclass = (Class<Throwable>) faultClass; 
			if( containedFault != null ) {
				// If there is a nested fault, retrieve this recursively
				Constructor cons = xclass.getConstructor(String.class, Throwable.class);
				return (Throwable) cons.newInstance(faultMessage, getContainedFault().retrieveFault());
			} else {
			    try {
				    Constructor cons = xclass.getConstructor(String.class);
				    return (Throwable) cons.newInstance(faultMessage);
				} catch (NoSuchMethodException e) {
				    Constructor cons = xclass.getConstructor();
				    return (Throwable) cons.newInstance();
				}
			} // end if
		} catch (Exception e) {
			return e;
		} // end try
	} // end method retrieveFault
	
	public void setFaultClassName( String name ) { this.faultClassName = name; }
	public String getFaultClassName() { return this.faultClassName; }
	
	public String getFaultMessage() { return faultMessage; }
	public void setFaultMessage(String faultMessage) { this.faultMessage = faultMessage; }
	
	public AsyncFaultWrapper getContainedFault() { return containedFault; }
	public void setContainedFault(AsyncFaultWrapper containedFault) { this.containedFault = containedFault; }

} // end class AsyncFaultWrapper
