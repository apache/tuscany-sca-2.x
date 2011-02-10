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

package org.apache.tuscany.sca.binding.sca.provider;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.invocation.AsyncResponseInvoker;
import org.apache.tuscany.sca.invocation.InvokerAsyncResponse;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 * @version $Rev: 989157 $ $Date: 2010-08-25 16:02:01 +0100 (Wed, 25 Aug 2010) $
 */
public class SCABindingAsyncResponseInvoker implements InvokerAsyncResponse {

     public SCABindingAsyncResponseInvoker(ExtensionPointRegistry extensionPoints,
                                          RuntimeEndpointReference endpointReference) {
    }
    
    // TODO - this only works for the local case!
    @SuppressWarnings("unchecked")
	public void invokeAsyncResponse(Message msg) {
    	AsyncResponseInvoker<RuntimeEndpointReference> asyncInvoker = 
    		(AsyncResponseInvoker<RuntimeEndpointReference>)msg.getHeaders().get("ASYNC_RESPONSE_INVOKER");
    	RuntimeEndpointReference epr;
    	if( asyncInvoker != null ) {
    		epr = asyncInvoker.getResponseTargetAddress();
    	} else {
    		epr = (RuntimeEndpointReference)msg.getFrom();
    	} // end if
    	if( epr != null ) {
    		epr.invokeAsyncResponse(msg); 
    	} else {
    		throw new ServiceRuntimeException("SCABindingAsyncResponseInvoker - invokeAsyncResponse has null epr");
    	} // end if
        
    } // end method invokeAsyncResponse
}