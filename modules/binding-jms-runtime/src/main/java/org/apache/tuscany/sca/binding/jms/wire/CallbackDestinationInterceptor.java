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

import java.util.List;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Topic;

import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.binding.jms.JMSBinding;
import org.apache.tuscany.sca.binding.jms.JMSBindingConstants;
import org.apache.tuscany.sca.binding.jms.JMSBindingException;
import org.apache.tuscany.sca.binding.jms.context.JMSBindingContext;
import org.apache.tuscany.sca.core.invocation.InterceptorAsyncImpl;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;

/**
 * JMS Binding Interceptor class that deals with a callback destination address on the service side
 *
 */
public class CallbackDestinationInterceptor extends InterceptorAsyncImpl {
    private Invoker next;
    private RuntimeComponentService service;
	private RuntimeEndpoint endpoint;
          
    public CallbackDestinationInterceptor(RuntimeEndpoint endpoint) {
        super();
        this.service = (RuntimeComponentService) endpoint.getService();
        this.endpoint = endpoint;
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
            // get the jms context
            JMSBindingContext context = msg.getBindingContext();
            javax.jms.Message jmsMsg = context.getJmsMsg(); 
            
            // Extract the Callback destination name header, if present
            String callbackdestName = jmsMsg.getStringProperty(JMSBindingConstants.CALLBACK_Q_PROPERTY);
         
            if (callbackdestName != null) {
            	// If present, strip any leading "jms:jndi:" string
                if (!callbackdestName.startsWith("jms:jndi:")) {
                    throw new JMSBindingException("message property " + JMSBindingConstants.CALLBACK_Q_PROPERTY + " does not start with 'jms:jndi:' found: " + callbackdestName);
                } else {
                    callbackdestName = callbackdestName.substring(9);
                } // end if
            } else {
            	// If there is no Callback destination name header present, but the service is a callback, use the JMS ReplyTo header
                if (service.getInterfaceContract().getCallbackInterface() != null) {
                    if ( ( jmsMsg.getJMSReplyTo() != null ) && msg.getOperation().isNonBlocking() ) {
                    	Destination replyTo = jmsMsg.getJMSReplyTo();
                    	if (replyTo != null) {
                    		callbackdestName = (replyTo instanceof Queue) ? ((Queue) replyTo).getQueueName() : ((Topic) replyTo).getTopicName();
                   	   	}
                    } // end if
                } // end if
            } // end if
                
            // Place the Callback destination name into the Callback EPRs for the service endpoint
            if (callbackdestName != null) {
            	List<EndpointReference> refs = endpoint.getCallbackEndpointReferences();
            	for (EndpointReference ref : refs ) {
            		if  (ref.getBinding() instanceof JMSBinding ) {
            			JMSBinding callbackBinding = (JMSBinding) ref.getBinding();
            			callbackBinding.setDestinationName(callbackdestName);
            		} // end if
            	} // end for
            } // end if  

// Callback ID not used at present            
//            String callbackID = jmsMsg.getStringProperty(JMSBindingConstants.CALLBACK_ID_PROPERTY);
//            if (callbackID != null) {
//                parameters.setCallbackID(callbackID);
//            }

        } catch (JMSException e) {
            throw new JMSBindingException(e);
        } // end try
        
        return msg;
    } // end method invokeRequest
    
	public Message processRequest(Message msg) {
		return invokeRequest(msg);
	} // end method processRequest

	public Message processResponse(Message msg) {
		return msg;
	} // end method processResponse
} // end class