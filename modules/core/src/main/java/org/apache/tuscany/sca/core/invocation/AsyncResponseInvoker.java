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

import java.io.Serializable;

import org.apache.tuscany.sca.invocation.InvokerAsyncResponse;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.provider.EndpointAsyncProvider;
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
	
	RuntimeEndpoint requestEndpoint;
    RuntimeEndpointReference responseEndpointReference; 
    T responseTargetAddress;
    String relatesToMsgID;
    
    public AsyncResponseInvoker(RuntimeEndpoint requestEndpoint,
			RuntimeEndpointReference responseEndpointReference,
			T responseTargetAddress, String relatesToMsgID) {
		super();
		this.requestEndpoint = requestEndpoint;
		this.responseEndpointReference = responseEndpointReference;
		this.responseTargetAddress = responseTargetAddress;
		this.relatesToMsgID = relatesToMsgID;
	} // end constructor

    /** 
     * If you have a Tuscany message you can call this
     */
    public void invokeAsyncResponse(Message responseMessage) {
    	responseMessage.getHeaders().put("ASYNC_RESPONSE_INVOKER", this);
        if ((requestEndpoint.getBindingProvider() instanceof EndpointAsyncProvider) &&
             (((EndpointAsyncProvider)requestEndpoint.getBindingProvider()).supportsNativeAsync())){
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
     * If you have Java beans you can call this and we'll create
     * a Tuscany message
     * 
     * @param args the response data
     */
    public void invokeAsyncResponse(Object args) {
        // TODO - how to get at the code that translates from args to msg?
        
        // turn args into a message
        Message responseMessage = null;
        invokeAsyncResponse(responseMessage);
    } // end method invokeAsyncResponse(Object)
} // end class
