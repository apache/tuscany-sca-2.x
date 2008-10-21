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
import java.util.ArrayList;
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
import org.apache.tuscany.sca.binding.jms.impl.JMSBinding;
import org.apache.tuscany.sca.binding.jms.impl.JMSBindingConstants;
import org.apache.tuscany.sca.binding.jms.impl.JMSBindingException;
import org.apache.tuscany.sca.binding.jms.policy.authentication.token.JMSTokenAuthenticationPolicy;
import org.apache.tuscany.sca.core.assembly.EndpointReferenceImpl;
import org.apache.tuscany.sca.core.invocation.MessageImpl;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.BindingInterceptor;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySetAttachPoint;
import org.apache.tuscany.sca.policy.SecurityUtil;
import org.apache.tuscany.sca.policy.authentication.token.TokenPrincipal;
import org.apache.tuscany.sca.provider.ServiceBindingProviderRRB;
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

    private static final String ON_MESSAGE_METHOD_NAME = "onMessage";
    private JMSBinding jmsBinding;
    private Binding targetBinding;
    private JMSResourceFactory jmsResourceFactory;
    private RuntimeComponentService service;
    private JMSMessageProcessor requestMessageProcessor;
    private JMSMessageProcessor responseMessageProcessor;
    private String correlationScheme;
    private List<Operation> serviceOperations;
    protected JMSTokenAuthenticationPolicy jmsTokenAuthenticationPolicy = null;
    
    /*
     * TODO binding chains could be treated generically (RuntimeWire?) but are
     *      here just now for experimental convenience
     */
    private List<Invoker> bindingRequestChain;
    private List<Invoker> bindingResponseChain;

    public RRBJMSBindingListener(JMSBinding jmsBinding, JMSResourceFactory jmsResourceFactory, RuntimeComponentService service, Binding targetBinding) throws NamingException {
        this.jmsBinding = jmsBinding;
        this.jmsResourceFactory = jmsResourceFactory;
        this.service = service;
        this.targetBinding = targetBinding;
        requestMessageProcessor = JMSMessageProcessorUtil.getRequestMessageProcessor(jmsBinding);
        responseMessageProcessor = JMSMessageProcessorUtil.getResponseMessageProcessor(jmsBinding);
        correlationScheme = jmsBinding.getCorrelationScheme();
        serviceOperations = service.getInterfaceContract().getInterface().getOperations();
        
        // find out which policies are active
        if (jmsBinding instanceof PolicySetAttachPoint) {
            List<PolicySet> policySets = ((PolicySetAttachPoint)jmsBinding).getApplicablePolicySets();
            for (PolicySet ps : policySets) {
                for (Object p : ps.getPolicies()) {
                    if (JMSTokenAuthenticationPolicy.class.isInstance(p)) {
                        jmsTokenAuthenticationPolicy = (JMSTokenAuthenticationPolicy)p;
                    }else {
                        // etc. check for other types of policy being present
                    }
                }
            }
        } 
        
        // Set up request/response chains for RRB
        bindingRequestChain = new ArrayList<Invoker>();
        bindingResponseChain = new ArrayList<Invoker>();
        
        ServiceBindingProviderRRB provider = (ServiceBindingProviderRRB)service.getBindingProvider(jmsBinding);
        provider.configureServiceBindingRequestChain(bindingRequestChain, service.getRuntimeWire(targetBinding));
        provider.configureServiceBindingResponseChain(bindingResponseChain, service.getRuntimeWire(targetBinding));

    }

    public void onMessage(Message requestJMSMsg) {
        logger.log(Level.FINE, "JMS service '" + service.getName() + "' received message " + requestJMSMsg);
        try {
            invokeService(requestJMSMsg);
            //sendReply(requestJMSMsg, responsePayload, false);
        } catch (Throwable e) {
            logger.log(Level.SEVERE, "Exception invoking service '" + service.getName(), e);
            sendReply(requestJMSMsg, e, true);
        }
    }

    /**
     * TODO RRB Experiment. Explicity call all the binding interceptors on the wire. Looking at
     *      how to handle requests and responses independently. The binding wire should/could
     *      be handled generically outside of this binding class but it's here for the momement
     *      while we look at what form it takes
     *      
     * Turn the JMS message back into a Tuscany message and invoke the target component
     * 
     * @param requestJMSMsg
     * @return
     * @throws JMSException
     * @throws InvocationTargetException
     */
    protected void invokeService(Message requestJMSMsg) throws JMSException, InvocationTargetException {

        // create the tuscany message
        MessageImpl tuscanyMsg = new MessageImpl();
        
        // populate the message context with JMS binding information
        tuscanyMsg.getHeaders().add(requestJMSMsg);
        
        // call the request wire
        for (Invoker invoker : bindingRequestChain){
           ((BindingInterceptor)invoker).invokeRequest(tuscanyMsg);
        }
        
        // call the runtime wire
        setHeaderProperties(requestJMSMsg, tuscanyMsg, tuscanyMsg.getOperation());
        Object response = service.getRuntimeWire(targetBinding).invoke(tuscanyMsg.getOperation(), tuscanyMsg);
        
        tuscanyMsg.setBody(response);
        
        if (requestJMSMsg.getJMSReplyTo() == null) {
            // assume no reply is expected
            if (response != null) {
                logger.log(Level.FINE, "JMS service '" + service.getName() + "' dropped response as request has no replyTo");
            }
            return;
        }
        
        // call the response wire
        for (Invoker invoker : bindingResponseChain){
            ((BindingInterceptor)invoker).invokeResponse(tuscanyMsg);
        }
    }

    /**
     * TODO - RRB experiment. Needs refactoring
     */
    protected void setHeaderProperties(javax.jms.Message requestJMSMsg, MessageImpl tuscanyMsg, Operation operation) throws JMSException {

        EndpointReference from = new EndpointReferenceImpl(null);
        tuscanyMsg.setFrom(from);
        from.setCallbackEndpoint(new EndpointReferenceImpl("/")); // TODO: whats this for?
        ReferenceParameters parameters = from.getReferenceParameters();

        String conversationID = requestJMSMsg.getStringProperty(JMSBindingConstants.CONVERSATION_ID_PROPERTY);
        if (conversationID != null) {
            parameters.setConversationID(conversationID);
        }

        if (service.getInterfaceContract().getCallbackInterface() != null) {

            String callbackdestName = requestJMSMsg.getStringProperty(JMSBindingConstants.CALLBACK_Q_PROPERTY);
            if (callbackdestName == null && operation.isNonBlocking()) {
                // if the request has a replyTo but this service operation is oneway but the service uses callbacks
                // then use the replyTo as the callback destination
                Destination replyTo = requestJMSMsg.getJMSReplyTo();
                if (replyTo != null) {
                    callbackdestName = (replyTo instanceof Queue) ? ((Queue)replyTo).getQueueName() : ((Topic)replyTo).getTopicName();
                }
            }

            if (callbackdestName != null) {
                // append "jms:" to make it an absolute uri so the invoker can determine it came in on the request
                // as otherwise the invoker should use the uri from the service callback binding
                parameters.setCallbackReference(new EndpointReferenceImpl("jms:" + callbackdestName));
            }

            String callbackID = requestJMSMsg.getStringProperty(JMSBindingConstants.CALLBACK_ID_PROPERTY);
            if (callbackID != null) {
                parameters.setCallbackID(callbackID);
            }
        }
        
       
        if (jmsTokenAuthenticationPolicy != null) {
            String token = requestJMSMsg.getStringProperty(jmsTokenAuthenticationPolicy.getTokenName().toString());
            
            Subject subject = SecurityUtil.getSubject(tuscanyMsg);
            TokenPrincipal principal = SecurityUtil.getPrincipal(subject, TokenPrincipal.class);
            
            if (principal == null){
                principal = new TokenPrincipal(token);
                subject.getPrincipals().add(principal);
            }

        }
    }    

    /*
     * TODO RRB experiment. Still used to handle errors. Needs refactoring
     */
    protected void sendReply(Message requestJMSMsg, Object responsePayload, boolean isFault) {
        try {

            if (requestJMSMsg.getJMSReplyTo() == null) {
                // assume no reply is expected
                if (responsePayload != null) {
                    logger.log(Level.FINE, "JMS service '" + service.getName() + "' dropped response as request has no replyTo");
                }
                return;
            }

            Session session = jmsResourceFactory.createSession();
            Message replyJMSMsg;
            if (isFault) {
                replyJMSMsg = responseMessageProcessor.createFaultMessage(session, (Throwable)responsePayload);
            } else {
                replyJMSMsg = responseMessageProcessor.insertPayloadIntoJMSMessage(session, responsePayload);
            }

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
