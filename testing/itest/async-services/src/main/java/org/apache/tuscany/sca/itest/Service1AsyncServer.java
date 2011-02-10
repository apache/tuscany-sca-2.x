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
package org.apache.tuscany.sca.itest;

import org.oasisopen.sca.ResponseDispatch;
import org.oasisopen.sca.annotation.AsyncFault;
import org.oasisopen.sca.annotation.AsyncInvocation;
import org.oasisopen.sca.annotation.Remotable;

/**
 * Service1 service interface
 * - Asynchronous server version
 */
@AsyncInvocation
@Remotable
public interface Service1AsyncServer {
	
	/**
	 * Synchronous method for invoking testcase service
	 * @param input - input parameter as a String
	 * @return - output data as a String
	 * Listed here for documentation purposes - this is the operation that the async server operation maps to
	 */
	// public String operation1( String input );
	
	/**
	 * Async server version of the synchronous operation1 method
	 * @param input - input parameter as a String
	 * @param handler - the ResponseDispatch<String> handler used to send the response message (a String in this case)
	 */
	@AsyncFault( {BusinessFault1.class, BusinessFault2.class} )
	public void operation1Async( String input, ResponseDispatch<String> handler );

} // end interface Service1AsyncServer
