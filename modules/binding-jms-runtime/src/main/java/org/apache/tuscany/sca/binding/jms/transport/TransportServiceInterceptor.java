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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;
import javax.security.auth.Subject;

import org.apache.tuscany.sca.binding.jms.context.JMSBindingContext;
import org.apache.tuscany.sca.binding.jms.impl.JMSBinding;
import org.apache.tuscany.sca.binding.jms.impl.JMSBindingConstants;
import org.apache.tuscany.sca.binding.jms.impl.JMSBindingException;
import org.apache.tuscany.sca.binding.jms.provider.DefaultJMSBindingListener;
import org.apache.tuscany.sca.binding.jms.provider.JMSMessageProcessor;
import org.apache.tuscany.sca.binding.jms.provider.JMSMessageProcessorUtil;
import org.apache.tuscany.sca.binding.jms.provider.JMSResourceFactory;
import org.apache.tuscany.sca.core.assembly.EndpointReferenceImpl;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.policy.SecurityUtil;
import org.apache.tuscany.sca.policy.authentication.token.TokenPrincipal;
import org.apache.tuscany.sca.runtime.EndpointReference;
import org.apache.tuscany.sca.runtime.ReferenceParameters;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeWire;

/**
 * Policy handler to handle PolicySet related to Logging with the QName
 * {http://tuscany.apache.org/xmlns/sca/1.0/impl/java}LoggingPolicy
 *
 * @version $Rev$ $Date$
 */
public class TransportServiceInterceptor implements Interceptor {
    private static final Logger logger = Logger.getLogger(TransportServiceInterceptor.class.getName());
      
    private Invoker next;
    private RuntimeWire runtimeWire;
    private JMSResourceFactory jmsResourceFactory;
    private JMSBinding jmsBinding;
    private JMSMessageProcessor requestMessageProcessor;
    private JMSMessageProcessor responseMessageProcessor;
    private RuntimeComponentService service;
    

    public TransportServiceInterceptor(JMSBinding jmsBinding, JMSResourceFactory jmsResourceFactory, RuntimeWire runtimeWire) {
        super();
        this.jmsBinding = jmsBinding;
        this.runtimeWire = runtimeWire;
        this.jmsResourceFactory = jmsResourceFactory;
        this.requestMessageProcessor = JMSMessageProcessorUtil.getRequestMessageProcessor(jmsBinding);
        this.responseMessageProcessor = JMSMessageProcessorUtil.getResponseMessageProcessor(jmsBinding);
        this.service = (RuntimeComponentService)runtimeWire.getTarget().getContract();
    }
    
    public Message invoke(Message msg) {
        return invokeResponse(next.invoke(invokeRequest(msg)));
    }    
    
    public Message invokeRequest(Message msg) { 
        try {
            JMSBindingContext context = (JMSBindingContext)msg.getHeaders().get(JMSBindingConstants.MSG_CTXT_POSITION);
            javax.jms.Message requestJMSMsg = context.getJmsMsg();
            
            EndpointReference from = new EndpointReferenceImpl(null);
            msg.setFrom(from);
            from.setCallbackEndpoint(new EndpointReferenceImpl("/")); // TODO: whats this for?
            ReferenceParameters parameters = from.getReferenceParameters();
    
            String conversationID = requestJMSMsg.getStringProperty(JMSBindingConstants.CONVERSATION_ID_PROPERTY);
            if (conversationID != null) {
                parameters.setConversationID(conversationID);
            }
            
            return msg;
        } catch (JMSException e) {
            throw new JMSBindingException(e);
        }
    }
    
    public Message invokeResponse(Message msg) { 
        try {
            JMSBindingContext context = (JMSBindingContext)msg.getHeaders().get(JMSBindingConstants.MSG_CTXT_POSITION);
            Session session = context.getJmsSession();
            javax.jms.Message requestJMSMsg = context.getJmsMsg();

            if (requestJMSMsg.getJMSReplyTo() == null) {
                // assume no reply is expected
                if (msg.getBody() != null) {
                    logger.log(Level.FINE, "JMS service '" + service.getName() + "' dropped response as request has no replyTo");
                }
                return msg;
            }
                       
            MessageProducer producer = session.createProducer(context.getReplyToDestination());
    
            producer.send((javax.jms.Message)msg.getBody());
    
            producer.close();
            session.close();
            
            return msg;
    
        } catch (JMSException e) {
            throw new JMSBindingException(e);
        } 
    }    
    
    public Invoker getNext() {
        return next;
    }

    public void setNext(Invoker next) {
        this.next = next;
    }    
   
}
