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

/**
 * A class which is used to wrap an Exception of any type thrown by an asynchronous service operation and
 * which is returned through a separate one-way message sent asynchronously from the server to the client.
 *
 */
public class AsyncFaultWrapper {
	
	private String faultClassName = null;
	private Exception e = null;
	
	public AsyncFaultWrapper() {
		super();
	}
	
	public AsyncFaultWrapper( Exception e ) {
		super();
		storeFault( e );
	}
	
	public void storeFault( Exception e ) {
		faultClassName = e.getClass().getCanonicalName();
		this.e = e;
	}
	
	public Exception retrieveFault( ) {
		if( e != null ) return e;
		System.out.println( "Tried to retrieve Exception reom AsyncFaultWrapper: " + faultClassName);
		return null;
	}

}
