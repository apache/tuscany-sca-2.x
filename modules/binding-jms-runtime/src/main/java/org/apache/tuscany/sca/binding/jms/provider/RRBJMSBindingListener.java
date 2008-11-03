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
package org.apache.tuscany.sca.binding.jms.provider;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.NamingException;
import javax.security.auth.Subject;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.binding.jms.context.JMSBindingContext;
import org.apache.tuscany.sca.binding.jms.impl.JMSBinding;
import org.apache.tuscany.sca.binding.jms.impl.JMSBindingConstants;
import org.apache.tuscany.sca.binding.jms.impl.JMSBindingException;
import org.apache.tuscany.sca.binding.jms.policy.authentication.token.JMSTokenAuthenticationPolicy;
import org.apache.tuscany.sca.core.assembly.EndpointReferenceImpl;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySetAttachPoint;
import org.apache.tuscany.sca.policy.SecurityUtil;
import org.apache.tuscany.sca.policy.authentication.token.TokenPrincipal;
import org.apache.tuscany.sca.runtime.EndpointReference;
import org.apache.tuscany.sca.runtime.ReferenceParameters;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

/**
 * TODO RRB experiement
 * Listener for the JMSBinding.
 * 
 * @version $Rev$ $Date$
 */
public class RRBJMSBindingListener implements MessageListener {

    private static final Logger logger = Logger.getLogger(RRBJMSBindingListener.class.getName());

    private JMSBinding jmsBinding;
    private Binding targetBinding;
    private JMSResourceFactory jmsResourceFactory;
    private RuntimeComponentService service;
    private JMSMessageProcessor requestMessageProcessor;
    private JMSMessageProcessor responseMessageProcessor;
    private String correlationScheme;
    private List<Operation> serviceOperations;
    private MessageFactory messageFactory;
    

    public RRBJMSBindingListener(JMSBinding jmsBinding, JMSResourceFactory jmsResourceFactory, RuntimeComponentService service, Binding targetBinding, MessageFactory messageFactory) throws NamingException {
        this.jmsBinding = jmsBinding;
        this.jmsResourceFactory = jmsResourceFactory;
        this.service = service;
        this.targetBinding = targetBinding;
        this.messageFactory = messageFactory;
        
        requestMessageProcessor = JMSMessageProcessorUtil.getRequestMessageProcessor(jmsBinding);
        responseMessageProcessor = JMSMessageProcessorUtil.getResponseMessageProcessor(jmsBinding);
        correlationScheme = jmsBinding.getCorrelationScheme();
        serviceOperations = service.getInterfaceContract().getInterface().getOperations();
        
    }

    public void onMessage(Message requestJMSMsg) {
        logger.log(Level.FINE, "JMS service '" + service.getName() + "' received message " + requestJMSMsg);
        try {
            invokeService(requestJMSMsg);
        } catch (Throwable e) {
            logger.log(Level.SEVERE, "Exception invoking service '" + service.getName(), e);
            sendFaultReply(requestJMSMsg, e);
        }
    }

    protected void invokeService(Message requestJMSMsg) throws JMSException, InvocationTargetException {

        try {
            // create the tuscany message
            org.apache.tuscany.sca.invocation.Message tuscanyMsg = messageFactory.createMessage();
            
            // populate the message context with JMS binding information
            JMSBindingContext context = new JMSBindingContext();
            tuscanyMsg.getHeaders().add(context);
            
            context.setJmsMsg(requestJMSMsg);
            context.setJmsSession(jmsResourceFactory.createSession());
            context.setReplyToDestination(requestJMSMsg.getJMSReplyTo());
            
            // set the message body
            tuscanyMsg.setBody(requestJMSMsg);
            
            // call the runtime wire
            InvocationChain chain = service.getRuntimeWire(targetBinding).getBindingInvocationChain();
            chain.getHeadInvoker().invoke(tuscanyMsg);
            
        } catch (NamingException e) {
            throw new JMSBindingException(e);
        }      
    }   

    /*
     * Send a message back if a fault has occurred
     */
    protected void sendFaultReply(Message requestJMSMsg, Object responsePayload) {
        try {

            if (requestJMSMsg.getJMSReplyTo() == null) {
                // assume no reply is expected
                if (responsePayload != null) {
                    logger.log(Level.FINE, "JMS service '" + service.getName() + "' dropped response as request has no replyTo");
                }
                return;
            }

            Session session = jmsResourceFactory.createSession();
            Message replyJMSMsg = responseMessageProcessor.createFaultMessage(session, (Throwable)responsePayload);
            replyJMSMsg.setJMSDeliveryMode(requestJMSMsg.getJMSDeliveryMode());
            replyJMSMsg.setJMSPriority(requestJMSMsg.getJMSPriority());

            if (correlationScheme == null || JMSBindingConstants.CORRELATE_MSG_ID.equalsIgnoreCase(correlationScheme)) {
                replyJMSMsg.setJMSCorrelationID(requestJMSMsg.getJMSMessageID());
            } else if (JMSBindingConstants.CORRELATE_CORRELATION_ID.equalsIgnoreCase(correlationScheme)) {
                replyJMSMsg.setJMSCorrelationID(requestJMSMsg.getJMSCorrelationID());
            }

            Destination destination = requestJMSMsg.getJMSReplyTo();
            MessageProducer producer = session.createProducer(destination);

            producer.send(replyJMSMsg);

            producer.close();
            session.close();

        } catch (JMSException e) {
            throw new JMSBindingException(e);
        } catch (NamingException e) {
            throw new JMSBindingException(e);
        }
    }

}
