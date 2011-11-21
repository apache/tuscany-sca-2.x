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

import org.apache.tuscany.sca.invocation.InvokerAsyncResponse;

public interface JDKAsyncResponseInvoker extends InvokerAsyncResponse {
	
	/**
	 * Registers an Async response, which provides an ID which identifies a given response
	 * and an object which can handle the response
	 * @param id - the ID
	 * @param responseHandler - the response handler object
	 */
	public void registerAsyncResponse( String id, Object responseHandler );

	/**
	 * Returns the registered async response for a given ID
	 * @param id - the ID
	 * @return responseHandler - the response handler object
	 */
	public Object getAsyncResponse( String id );

} // end interface JDKAsyncResponseInvoker
