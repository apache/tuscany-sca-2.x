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
package org.apache.tuscany.sca.binding.jms.transport;

import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.NamingException;

import org.apache.tuscany.sca.binding.jms.JMSBinding;
import org.apache.tuscany.sca.binding.jms.JMSBindingConstants;
import org.apache.tuscany.sca.binding.jms.JMSBindingException;
import org.apache.tuscany.sca.binding.jms.context.JMSBindingContext;
import org.apache.tuscany.sca.binding.jms.provider.JMSResourceFactory;
import org.apache.tuscany.sca.core.invocation.InterceptorAsyncImpl;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;

/**
 * Policy handler to handle PolicySet related to Logging with the QName
 * {http://tuscany.apache.org/xmlns/sca/1.1/impl/java}LoggingPolicy
 *
 * @version $Rev$ $Date$
 */
public class TransportReferenceInterceptor extends InterceptorAsyncImpl {
      
    private Invoker next;    
    private JMSResourceFactory jmsResourceFactory;
    private JMSBinding jmsBinding;

    public TransportReferenceInterceptor(JMSBinding jmsBinding, JMSResourceFactory jmsResourceFactory, RuntimeEndpointReference runtimeWire) {
        super();
        this.jmsBinding = jmsBinding;        
        this.jmsResourceFactory = jmsResourceFactory;
    }
    
    public Message invoke(Message msg) {
        Message responseMsg = invokeRequest(msg);
        
        // get the jms context
        JMSBindingContext context = msg.getBindingContext();
        
        // [rfeng] For oneway operation as part of the bi-directional interface, the JMSReplyTo is present
        if (context.getReplyToDestination() == null || msg.getOperation().isNonBlocking()) {
            responseMsg.setBody(null);
        } else {
            responseMsg = invokeResponse(msg);
        }
        
        return responseMsg;
    }
    
    public Message invokeRequest(Message msg) {
        try {        
            // get the jms context
            JMSBindingContext context = msg.getBindingContext();
            Session session = context.getJmsSession();
            
            MessageProducer producer = session.createProducer(context.getRequestDestination());
    
            // Set JMS header attributes in producer, not message.
            String opName = msg.getOperation().getName();
            if (jmsBinding.getEffectiveJMSTimeToLive(opName) != null) {
                producer.setTimeToLive(jmsBinding.getEffectiveJMSTimeToLive(msg.getOperation().getName()).longValue());
            } 
            
            Integer priority = jmsBinding.getEffectiveJMSPriority(opName);
            if (priority != null) {
            	producer.setPriority(priority.intValue());
            }  
            
            Boolean deliveryModePersistent = jmsBinding.getEffectiveJMSDeliveryMode(opName);
            if (deliveryModePersistent != null) {
            	producer.setDeliveryMode( deliveryModePersistent ? DeliveryMode.PERSISTENT : DeliveryMode.NON_PERSISTENT);               
            }                      
            
            try {
                producer.send((javax.jms.Message)msg.getBody());
            } finally {
                producer.close();
            }
            return msg;
        } catch (JMSException e) {
            throw new JMSBindingException(e);
        } // end try
    } // end method invokeRequest
    
    public Message invokeResponse(Message msg) {
        JMSBindingContext context = msg.getBindingContext();
        try {
            Session session = context.getJmsResponseSession();
            
            javax.jms.Message requestMessage = (javax.jms.Message)msg.getBody();
                      
            String msgSelector = null;
            String correlationScheme = jmsBinding.getCorrelationScheme();
            if (correlationScheme == null || JMSBindingConstants.CORRELATE_MSG_ID.equalsIgnoreCase(correlationScheme)) {
                msgSelector = "JMSCorrelationID = '" + requestMessage.getJMSMessageID() + "'";
            } else if (JMSBindingConstants.CORRELATE_CORRELATION_ID.equalsIgnoreCase(correlationScheme)) {
                msgSelector = "JMSCorrelationID = '" + requestMessage.getJMSCorrelationID() + "'";
            }                

            MessageConsumer consumer;
            if (msgSelector != null) {
                consumer = session.createConsumer(context.getReplyToDestination(), msgSelector);  
            } else {
                consumer = session.createConsumer(context.getReplyToDestination());  
            }
            
            javax.jms.Message replyMsg;
            try {
                context.getJmsResourceFactory().startConnection();
                //jmsResourceFactory.startConnection();
                replyMsg = consumer.receive(context.getTimeToLive());
            } finally {
                consumer.close();
            }
            if (replyMsg == null) {
                throw new JMSBindingException("No reply message received on " + 
                                              context.getReplyToDestination() + 
                                              " for message id " + 
                                              requestMessage.getJMSMessageID());
            }
            
            msg.setBody(replyMsg);
            return msg;
        } catch (JMSException e) {
            throw new JMSBindingException(e);
        } catch (NamingException e) {
            throw new JMSBindingException(e);
        } finally {
            try {
                context.closeJmsResponseSession();
                if (jmsResourceFactory.isConnectionClosedAfterUse())
                    jmsResourceFactory.closeResponseConnection();
            } catch (JMSException e) {
            }
        }
    } 
    
    public Invoker getNext() {
        return next;
    }

    public void setNext(Invoker next) {
        this.next = next;
    }

	/**
	 * Process forward request message
	 * @param tuscanyMsg - the request message
	 * @return the processed version of the request message
	 */
	public Message processRequest(Message tuscanyMsg) {
		return invokeRequest(tuscanyMsg);
	} // end method processRequest

    /**
     * Process response message
	 * @param tuscanyMsg - the response message
	 * @return the processed version of the response message
     */
	public Message processResponse(Message tuscanyMsg) {
		// TODO Auto-generated method stub
		return tuscanyMsg;
	} // end method processResponse
}
