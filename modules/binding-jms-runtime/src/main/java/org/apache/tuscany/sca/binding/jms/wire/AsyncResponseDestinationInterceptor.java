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
package org.apache.tuscany.sca.binding.jms.wire;

import java.util.Iterator;
import java.util.List;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Topic;
import javax.naming.NamingException;

import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.binding.jms.JMSBinding;
import org.apache.tuscany.sca.binding.jms.JMSBindingConstants;
import org.apache.tuscany.sca.binding.jms.JMSBindingException;
import org.apache.tuscany.sca.binding.jms.context.JMSBindingContext;
import org.apache.tuscany.sca.binding.jms.provider.JMSResourceFactory;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.invocation.AsyncResponseInvoker;
import org.apache.tuscany.sca.core.invocation.InterceptorAsyncImpl;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;

/**
 * JMS Binding Interceptor class that deals with destination address & ID for an async response on the service side
 *
 */
public class AsyncResponseDestinationInterceptor extends InterceptorAsyncImpl {
    private Invoker next;
    private RuntimeComponentService service;
	private RuntimeEndpoint endpoint;
	private ExtensionPointRegistry registry;
          
    public AsyncResponseDestinationInterceptor(RuntimeEndpoint endpoint, ExtensionPointRegistry registry) {
        super();
        this.service = (RuntimeComponentService) endpoint.getService();
        this.endpoint = endpoint;
        this.registry = registry;
    }

    public Invoker getNext() {
        return next;
    }

    public void setNext(Invoker next) {
        this.next = next;
    }

    public Message invoke(Message msg) {
        return next.invoke(invokeRequest(msg));
    }

    /**
     * Handle an invocation request messaage
     * @param msg the message
     * @return the updated message
     */
    public Message invokeRequest(Message msg) {
        try {
            // Get the JMS context
            JMSBindingContext context = msg.getBindingContext();
            javax.jms.Message jmsMsg = context.getJmsMsg(); 
            
            // Extract the Callback destination name header, if present
            String asyncRespAddr = jmsMsg.getStringProperty(JMSBindingConstants.CALLBACK_Q_PROPERTY);
         
            if (asyncRespAddr != null) {
            	asyncRespAddr = stripJMSPrefix( asyncRespAddr );
            } else {
            	// If there is no Callback destination name header present, but the service is async, use the JMS ReplyTo header
                if ( isAsync(service) ) {
                    if ( ( jmsMsg.getJMSReplyTo() != null ) ) {
                    	Destination replyTo = jmsMsg.getJMSReplyTo();
                    	if (replyTo != null) {
                    		asyncRespAddr = (replyTo instanceof Queue) ? ((Queue) replyTo).getQueueName() : ((Topic) replyTo).getTopicName();
                   	   	}
                    } // end if
                } // end if
            } // end if
            
            // If there is no response address, we're done
            if( asyncRespAddr == null ) return msg;

            // Get the message ID - assume that the interceptor for obtaining the message ID is earlier in the chain
            // than this interceptor
            String msgID = (String)msg.getHeaders().get("MESSAGE_ID");
            
            String operationName = msg.getOperation().getName();
            
            // Create a response invoker and add it to the message headers
            AsyncResponseInvoker<String> respInvoker = 
            	new AsyncResponseInvoker<String>(endpoint, null, asyncRespAddr, msgID, operationName, getMessageFactory());
            msg.getHeaders().put("ASYNC_RESPONSE_INVOKER", respInvoker);

        } catch (JMSException e) {
            throw new JMSBindingException(e);
        } // end try
        
        return msg;
    } // end method invokeRequest
    
	public Message processRequest(Message msg) {
		return invokeRequest(msg);
	} // end method processRequest

	/**
	 * Process a response message
	 * - if it is an async response, there will be a header "ASYNC_RESPONSE_INVOKER" which contains
	 *   the address of the JMS queue to reply to plus a message ID of the original message
	 *   These values are used to create the Destination for the JMS message and to add a "RELATES_TO"
	 *   header which is sent with the response message to allow the client to correlate the response
	 *   to the original request
	 * @param msg - the Tuscany message
	 * @returns - the updated Tuscany message
	 */
	public Message processResponse(Message msg) {
		@SuppressWarnings("unchecked")
		AsyncResponseInvoker<String> respInvoker = (AsyncResponseInvoker<String>)msg.getHeaders().get("ASYNC_RESPONSE_INVOKER");
		if ( respInvoker == null ) return msg;
		
		String responseAddress = respInvoker.getResponseTargetAddress();
		String relatedMsg = respInvoker.getRelatesToMsgID();
		
        // Get the JMS context
        JMSBindingContext context = msg.getBindingContext();
        JMSResourceFactory jmsResourceFactory = context.getJmsResourceFactory();
        Destination dest;
		try {
			dest = jmsResourceFactory.lookupDestination(responseAddress);
			if( dest == null ) {
				dest = jmsResourceFactory.createDestination(responseAddress);
			} // end if
		} catch (NamingException e) {
			throw new JMSBindingException(e);
		}
        context.setReplyToDestination(dest);
        msg.getHeaders().put("RELATES_TO", relatedMsg);

		return msg;
	} // end method processResponse
    
    /**
     * Utility that strips the leading "jms:jndi:" prefix from a JMS address
     * @param asyncRespAddr - the JMS address 
     * @return - the JMS address with the prefix removed
     * @throws JMSBindingException if the JMS address does not have a prefix
     */
    private String stripJMSPrefix(String asyncRespAddr) {
    	// If present, strip any leading "jms:jndi:" string
        if (!asyncRespAddr.startsWith("jms:jndi:")) {
            throw new JMSBindingException("message property " + JMSBindingConstants.CALLBACK_Q_PROPERTY + " does not start with 'jms:jndi:' found: " + asyncRespAddr);
        } else {
        	return asyncRespAddr.substring(9);
        } // end if
	} // end method stripJMSPrefix

	/**
     * Determines if a service has an interface that is async invocation
     * @param service - the service
     * @return true if the service is async, false otherwise
     */
    private boolean isAsync( RuntimeComponentService service ) {
    	service.getInterfaceContract().getInterface();
    	
    	Iterator<Intent> intents = service.getRequiredIntents().iterator();
    	while ( intents.hasNext() ) {
    		Intent intent = intents.next();
    		if ( intent.getName().getLocalPart().equals("asyncInvocation") ) return true;
    	} // end while
    	
    	intents = service.getInterfaceContract().getInterface().getRequiredIntents().iterator();
    	while ( intents.hasNext() ) {
    		Intent intent = intents.next();
    		if ( intent.getName().getLocalPart().equals("asyncInvocation") ) return true;
    	} // end while
    	return false;
    } // end method isAsync
    
	private MessageFactory getMessageFactory() {
		FactoryExtensionPoint modelFactories = registry.getExtensionPoint(FactoryExtensionPoint.class);
		return modelFactories.getFactory(MessageFactory.class);
	} // end method getMessageFactory
	
} // end class