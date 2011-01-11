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
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.invocation.AsyncResponseInvoker;
import org.apache.tuscany.sca.core.invocation.InterceptorAsyncImpl;
import org.apache.tuscany.sca.databinding.Mediator;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.InvokerAsyncRequest;
import org.apache.tuscany.sca.invocation.InvokerAsyncResponse;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.invocation.Phase;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;


/**
 * @version $Rev$ $Date$
 */
public class SCABindingInvoker extends InterceptorAsyncImpl {
    private InvocationChain chain;
    private Mediator mediator;
    private Operation sourceOperation;
    private Operation targetOperation;
    private boolean passByValue;
    private RuntimeEndpointReference epr;
    private RuntimeEndpoint ep;
    private ExtensionPointRegistry registry;

    /**
     * Construct a SCABindingInvoker that delegates to the service invocation chain
     */
    public SCABindingInvoker(InvocationChain chain, Operation sourceOperation, Mediator mediator, 
    		boolean passByValue, RuntimeEndpointReference epr, ExtensionPointRegistry registry) {
        super();
        this.chain = chain;
        this.mediator = mediator;
        this.sourceOperation = sourceOperation;
        this.targetOperation = chain.getTargetOperation();
        this.passByValue = passByValue;
        this.epr = epr;
        this.ep = (RuntimeEndpoint)epr.getTargetEndpoint();
        this.registry = registry;
    }

    /**
     * @see org.apache.tuscany.sca.invocation.Interceptor#getNext()
     */
    public Invoker getNext() {
        return chain.getHeadInvoker(Phase.SERVICE_POLICY);
    }

    /**
     * @see org.apache.tuscany.sca.invocation.Interceptor#setNext(org.apache.tuscany.sca.invocation.Invoker)
     */
    public void setNext(Invoker next) {
        // NOOP
    }
    
    public Message processRequest(Message msg){
        if (passByValue) {
            msg.setBody(mediator.copyInput(msg.getBody(), sourceOperation, targetOperation));
        } // end if
         
        ep.getInvocationChains();
        if ( !ep.getCallbackEndpointReferences().isEmpty() ) {
            RuntimeEndpointReference asyncEPR = (RuntimeEndpointReference) ep.getCallbackEndpointReferences().get(0);
            // Place a link to the callback EPR into the message headers...
            msg.getHeaders().put("ASYNC_CALLBACK", asyncEPR );
        } // end if
        
        if( ep.isAsyncInvocation() ) {
            // Get the message ID 
            String msgID = (String)msg.getHeaders().get("MESSAGE_ID");
            
            String operationName = msg.getOperation().getName();
            
            // Create a response invoker and add it to the message headers
            AsyncResponseInvoker<RuntimeEndpointReference> respInvoker = 
            	new AsyncResponseInvoker<RuntimeEndpointReference>(ep, null, epr, msgID, operationName, getMessageFactory());
            respInvoker.setBindingType("SCA_LOCAL");
            msg.getHeaders().put("ASYNC_RESPONSE_INVOKER", respInvoker);
        } // end if
        
        return msg;
    } // end method processRequest
    
    /**
     * Regular (sync) processing of response message
     */
    public Message processResponse(Message msg){
        if (passByValue) {
            // Note source and target operation swapped so result is in source class loader
            if (msg.isFault()) {
                msg.setFaultBody(mediator.copyFault(msg.getBody(), sourceOperation, targetOperation));
            } else {
                if (sourceOperation.getOutputType() != null) {
                    msg.setBody(mediator.copyOutput(msg.getBody(), sourceOperation, targetOperation));
                } // end if
            } // end if
        } // end if
        
        return msg;
    } // end method processResponse
    
    public void invokeAsyncRequest(Message msg) throws Throwable {
    	try{ 
	        msg = processRequest(msg);
	        InvokerAsyncRequest theNext = (InvokerAsyncRequest)getNext();
	        if( theNext != null ) theNext.invokeAsyncRequest(msg);
	        postProcessRequest(msg);
    	} catch (Throwable e) {
    		postProcessRequest(msg, e);
    	} // end try
    } // end method invokeAsyncRequest
    
    public void invokeAsyncResponse(Message msg) {
        msg = processResponse(msg);
        
        // Handle async response Relates_To message ID value
        @SuppressWarnings("unchecked")
		AsyncResponseInvoker<RuntimeEndpointReference> respInvoker = 
        	(AsyncResponseInvoker<RuntimeEndpointReference>)msg.getHeaders().get("ASYNC_RESPONSE_INVOKER");
        // TODO - this deals with the Local case only - not distributed
        if( respInvoker != null && "SCA_LOCAL".equals(respInvoker.getBindingType()) ) {
	        RuntimeEndpointReference responseEPR = respInvoker.getResponseTargetAddress();
	        msg.setFrom(responseEPR);
        	String msgID = respInvoker.getRelatesToMsgID();
	        msg.getHeaders().put("RELATES_TO", msgID);
        } // end if
        
        InvokerAsyncResponse thePrevious = (InvokerAsyncResponse)getPrevious();
        if (thePrevious != null ) thePrevious.invokeAsyncResponse(msg);
    } // end method invokeAsyncResponse
    
    public boolean isLocalSCABIndingInvoker() {
        return true;
    }
    
	private MessageFactory getMessageFactory() {
		FactoryExtensionPoint modelFactories = registry.getExtensionPoint(FactoryExtensionPoint.class);
		return modelFactories.getFactory(MessageFactory.class);
	} // end method getMessageFactory

}
