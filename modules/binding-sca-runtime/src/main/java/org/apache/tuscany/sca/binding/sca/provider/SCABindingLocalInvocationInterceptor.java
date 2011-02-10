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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tuscany.sca.core.invocation.AsyncResponseInvoker;
import org.apache.tuscany.sca.core.invocation.InterceptorAsyncImpl;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.InvokerAsyncResponse;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;

/**
 * Interceptor used by the SCA Binding on the service side chain to provide a mechanism for optimising
 * invocations when the reference and the service involved are both in the same JVM, and thus the need
 * to use a transport of any kind is unnecessary.
 * 
 */
public class SCABindingLocalInvocationInterceptor extends InterceptorAsyncImpl {
    private static final Logger logger = Logger.getLogger(SCABindingLocalInvocationInterceptor.class.getName());
      
    private Invoker next;

	private boolean skipPrevious;    
    
    public SCABindingLocalInvocationInterceptor() {
        super();
    } // end constructor
    
    public Message invoke(Message msg) {
        return next.invoke(msg);
    } // end method invoke   
    
    public Invoker getNext() {
        return next;
    } // end method getNext

    public void setNext(Invoker next) {
        this.next = next;
    } // end method setNext

    /**
     * Process request method is simply a passthrough
     */
	public Message processRequest(Message msg) {
		return msg ;
	} // end method processRequest

	
	/**
	 * Handle an async response
	 * - deals with the local SCA binding case only (at present)
	 * - in this case, extract the async response invoker from the message header and call the EPR
	 *   that is present in the invoker, which is in fact the local EPR from which the original forward
	 *   request came
	 */
    public void invokeAsyncResponse(Message msg) {
        @SuppressWarnings("unchecked")
		AsyncResponseInvoker<?> respInvoker = 
        	(AsyncResponseInvoker<?>)msg.getHeaders().get("ASYNC_RESPONSE_INVOKER");
        if( respInvoker != null && "SCA_LOCAL".equals(respInvoker.getBindingType()) ) {
        	// Handle the locally optimised case
	        RuntimeEndpointReference responseEPR = (RuntimeEndpointReference)respInvoker.getResponseTargetAddress();
	        msg.setFrom(responseEPR);
	        // Handle async response Relates_To message ID value
        	String msgID = respInvoker.getRelatesToMsgID();
	        msg.getHeaders().put("RELATES_TO", msgID);
	        
	        // Call the processing on the reference chain directly
	        responseEPR.invokeAsyncResponse(msg);
	        
	        // Prevent the response being processed by the rest of the service chain
	        return;
        } else {
        	// Carry on processing the response by the rest of the service chain
            InvokerAsyncResponse thePrevious = (InvokerAsyncResponse)getPrevious();
            if (thePrevious != null ) thePrevious.invokeAsyncResponse(msg);
            return;
        } // end if

    } // end method invokeAsyncResponse

	/**
	 * processResponse is not called during async response handling (all handled by invokeAsyncResponse)
	 * - this version is a dummy which does nothing.
	 */
	public Message processResponse(Message msg) {
		return msg;
	} // end method processResponse
   
} // end class 
