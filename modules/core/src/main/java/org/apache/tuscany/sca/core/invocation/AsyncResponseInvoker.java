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
import java.util.Iterator;
import java.util.List;

import org.apache.tuscany.sca.context.CompositeContext;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.InvokerAsyncResponse;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.provider.EndpointAsyncProvider;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;
import org.oasisopen.sca.ComponentContext;

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
	
	private RuntimeEndpoint requestEndpoint;
    private RuntimeEndpointReference responseEndpointReference; 
    private T responseTargetAddress;
    private String relatesToMsgID;
    private String operationName;
    private MessageFactory messageFactory;
    private String bindingType = "";
    
    public AsyncResponseInvoker(RuntimeEndpoint requestEndpoint,
			RuntimeEndpointReference responseEndpointReference,
			T responseTargetAddress, String relatesToMsgID, 
			String operationName, MessageFactory messageFactory) {
		super();
		this.requestEndpoint = requestEndpoint;
		this.responseEndpointReference = responseEndpointReference;
		this.responseTargetAddress = responseTargetAddress;
		this.relatesToMsgID = relatesToMsgID;
		this.operationName = operationName;
		this.messageFactory = messageFactory;
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
        
        Message msg = messageFactory.createMessage();

        msg.setOperation(getOperation());
        if( args instanceof Throwable ) {
        	msg.setFaultBody(args);
        } else {
        	msg.setBody(args);
        } // end if
        
        invokeAsyncResponse(msg);
        
    } // end method invokeAsyncResponse(Object)

	private Operation getOperation() {
		List<Operation> ops = requestEndpoint.getService().getInterfaceContract().getInterface().getOperations();
		for (Operation op : ops) {
			if( operationName.equals(op.getName()) ) return op;
		} // end for
		return null;
	} // end getOperation

	public void setBindingType(String bindingType) {
		this.bindingType = bindingType;
	} // end method setBindingType

	public String getBindingType() {
		return bindingType;
	} // end method getBindingType

} // end class
