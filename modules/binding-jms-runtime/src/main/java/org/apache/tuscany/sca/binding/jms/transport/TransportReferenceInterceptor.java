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

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.NamingException;

import org.apache.tuscany.sca.binding.jms.JMSBinding;
import org.apache.tuscany.sca.binding.jms.JMSBindingException;
import org.apache.tuscany.sca.binding.jms.context.JMSBindingContext;
import org.apache.tuscany.sca.binding.jms.provider.JMSResourceFactory;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeWire;

/**
 * Policy handler to handle PolicySet related to Logging with the QName
 * {http://tuscany.apache.org/xmlns/sca/1.0/impl/java}LoggingPolicy
 *
 * @version $Rev$ $Date$
 */
public class TransportReferenceInterceptor implements Interceptor {
      
    private Invoker next;
    private RuntimeWire runtimeWire;
    private JMSResourceFactory jmsResourceFactory;
    private JMSBinding jmsBinding;
    private RuntimeComponentReference reference;    

    public TransportReferenceInterceptor(JMSBinding jmsBinding, JMSResourceFactory jmsResourceFactory, RuntimeWire runtimeWire) {
        super();
        this.jmsBinding = jmsBinding;
        this.runtimeWire = runtimeWire;
        this.jmsResourceFactory = jmsResourceFactory;
        this.reference = (RuntimeComponentReference)runtimeWire.getEndpointReference().getInterfaceContract();        
    }
    
    public Message invoke(Message msg) {
        Message responseMsg = invokeRequest(msg);
        
        // get the jms context
        JMSBindingContext context = msg.getBindingContext();
        
        if (context.getReplyToDestination() == null) {
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
            if (jmsBinding.getOperationJMSTimeToLive(msg.getOperation().getName()) != null) {
                producer.setTimeToLive(jmsBinding.getOperationJMSTimeToLive(msg.getOperation().getName()));
            }
            Integer priority = jmsBinding.getOperationJMSPriority( opName );
            if (priority != null) {
               producer.setPriority(priority.intValue());
            }  
            Boolean deliveryModePersistent = jmsBinding.getOperationJMSDeliveryMode(opName);
            if (deliveryModePersistent != null) {
                if (deliveryModePersistent.booleanValue())
                    producer.setDeliveryMode(javax.jms.DeliveryMode.PERSISTENT);
                else
                    producer.setDeliveryMode(javax.jms.DeliveryMode.NON_PERSISTENT);
            }
            
            try {
                producer.send((javax.jms.Message)msg.getBody());
            } finally {
                producer.close();
            }
            return msg;
        } catch (JMSException e) {
            throw new JMSBindingException(e);
        } 
    }
    
    public Message invokeResponse(Message msg) {
        JMSBindingContext context = msg.getBindingContext();
        try {
            Session session = context.getJmsResponseSession();
            
            javax.jms.Message requestMessage = (javax.jms.Message)msg.getBody();
                      
            String msgSelector = "JMSCorrelationID = '" + 
                                 requestMessage.getJMSMessageID() + 
                                 "'";
            MessageConsumer consumer = session.createConsumer(context.getReplyToDestination(), msgSelector);  
            
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
}
