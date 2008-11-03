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
import java.util.Map;

import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.NamingException;
import javax.security.auth.Subject;

import org.apache.tuscany.sca.binding.jms.context.JMSBindingContext;
import org.apache.tuscany.sca.binding.jms.impl.JMSBinding;
import org.apache.tuscany.sca.binding.jms.impl.JMSBindingConstants;
import org.apache.tuscany.sca.binding.jms.impl.JMSBindingException;
import org.apache.tuscany.sca.binding.jms.policy.authentication.token.JMSTokenAuthenticationPolicy;
import org.apache.tuscany.sca.binding.jms.policy.header.JMSHeaderPolicy;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.invocation.DataExchangeSemantics;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySetAttachPoint;
import org.apache.tuscany.sca.policy.SecurityUtil;
import org.apache.tuscany.sca.policy.authentication.token.TokenPrincipal;
import org.apache.tuscany.sca.runtime.ReferenceParameters;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.osoa.sca.ServiceRuntimeException;

/**
 * Invoker for the JMS binding.
 * 
 * @version $Rev$ $Date$
 */
public class RRBJMSBindingInvoker implements Invoker, DataExchangeSemantics {

    protected Operation operation;
    protected String operationName;

    protected JMSBinding jmsBinding;
    protected JMSResourceFactory jmsResourceFactory;
    protected JMSMessageProcessor requestMessageProcessor;
    protected JMSMessageProcessor responseMessageProcessor;
    protected Destination bindingRequestDest;
    protected Destination bindingReplyDest;
    protected RuntimeComponentReference reference;
    protected RuntimeWire runtimeWire;

    public RRBJMSBindingInvoker(JMSBinding jmsBinding, Operation operation, JMSResourceFactory jmsResourceFactory, RuntimeComponentReference reference) {

        this.operation = operation;
        operationName = operation.getName();

        this.jmsBinding = jmsBinding;
        this.jmsResourceFactory = jmsResourceFactory;
        this.reference = reference;
        this.runtimeWire = reference.getRuntimeWire(jmsBinding);
        this.requestMessageProcessor = JMSMessageProcessorUtil.getRequestMessageProcessor(jmsBinding);
        this.responseMessageProcessor = JMSMessageProcessorUtil.getResponseMessageProcessor(jmsBinding);
        
        try {
            bindingRequestDest = lookupDestination();
            bindingReplyDest = lookupResponseDestination();
        } catch (NamingException e) {
            throw new JMSBindingException(e);
        }
    }

    /**
     * Looks up the Destination Queue for the JMS Binding
     * 
     * @return The Destination Queue
     * @throws NamingException Failed to lookup Destination Queue
     * @throws JMSBindingException Failed to lookup Destination Queue
     * @see #lookupDestinationQueue(boolean)
     */
    protected Destination lookupDestination() throws NamingException, JMSBindingException {
        return lookupDestinationQueue(false);
    }

    /**
     * Looks up the Destination Response Queue for the JMS Binding
     * 
     * @return The Destination Response Queue
     * @throws NamingException Failed to lookup Destination Response Queue
     * @throws JMSBindingException Failed to lookup Destination Response Queue
     * @see #lookupDestinationQueue(boolean)
     */
    protected Destination lookupResponseDestination() throws NamingException, JMSBindingException {
        return lookupDestinationQueue(true);
    }

    /**
     * Looks up the Destination Queue for the JMS Binding.
     * <p>
     * What happens in the look up will depend on the create mode specified for the JMS Binding:
     * <ul>
     * <li>always - the JMS queue is always created. It is an error if the queue already exists
     * <li>ifnotexist - the JMS queue is created if it does not exist. It is not an error if the queue already exists
     * <li>never - the JMS queue is never created. It is an error if the queue does not exist
     * </ul>
     * See the SCA JMS Binding specification for more information.
     * <p>
     * 
     * @param isReponseQueue <code>true</code> if we are creating a response queue. 
     *                       <code>false</code> if we are creating a request queue
     * @return The Destination queue.
     * @throws NamingException Failed to lookup JMS queue
     * @throws JMSBindingException Failed to lookup JMS Queue. Probable cause is that 
     *         the JMS queue's current existence/non-existence is not compatible with 
     *         the create mode specified on the binding
     */
    protected Destination lookupDestinationQueue(boolean isReponseQueue) throws NamingException, JMSBindingException {
        String queueName;
        String queueType;
        String qCreateMode;

        if (isReponseQueue) {
            queueName = jmsBinding.getResponseDestinationName();
            queueType = "JMS Response Destination ";
            qCreateMode = jmsBinding.getResponseDestinationCreate();
            if (JMSBindingConstants.DEFAULT_RESPONSE_DESTINATION_NAME.equals(queueName)) {
                return null;
            }
        } else {
            queueName = jmsBinding.getDestinationName();
            queueType = "JMS Destination ";
            qCreateMode = jmsBinding.getDestinationCreate();
        }

        Destination dest = jmsResourceFactory.lookupDestination(queueName);

        if (qCreateMode.equals(JMSBindingConstants.CREATE_ALWAYS)) {
            // In this mode, the queue must not already exist as we are creating it
            if (dest != null) {
                throw new JMSBindingException(queueType + queueName
                    + " already exists but has create mode of \""
                    + qCreateMode
                    + "\" while registering binding "
                    + jmsBinding.getName()
                    + " invoker");
            }
            // Create the queue
            dest = jmsResourceFactory.createDestination(queueName);

        } else if (qCreateMode.equals(JMSBindingConstants.CREATE_IF_NOT_EXIST)) {
            // In this mode, the queue may nor may not exist. It will be created if it does not exist
            if (dest == null) {
                dest = jmsResourceFactory.createDestination(queueName);
            }

        } else if (qCreateMode.equals(JMSBindingConstants.CREATE_NEVER)) {
            // In this mode, the queue must have already been created.
            if (dest == null) {
                throw new JMSBindingException(queueType + queueName
                    + " not found but create mode of \""
                    + qCreateMode
                    + "\" while registering binding "
                    + jmsBinding.getName()
                    + " invoker");
            }
        }

        // Make sure we ended up with a queue
        if (dest == null) {
            throw new JMSBindingException(queueType + queueName
                + " not found with create mode of \""
                + qCreateMode
                + "\" while registering binding "
                + jmsBinding.getName()
                + " invoker");
        }

        return dest;
    }

    public org.apache.tuscany.sca.invocation.Message invoke(org.apache.tuscany.sca.invocation.Message tuscanyMsg) {
        try {
            // create a jms session to cover the creation and sending of the message
            Session session = jmsResourceFactory.createSession();
            
            // populate the message context with JMS binding information
            JMSBindingContext context = new JMSBindingContext();
            tuscanyMsg.getHeaders().add(JMSBindingConstants.MSG_CTXT_POSITION, context);
            
            context.setJmsSession(session);
            context.setRequestDestination(getRequestDestination(tuscanyMsg, session));
            context.setReplyToDestination(getReplyToDestination(session));
            context.setJmsResourceFactory(jmsResourceFactory);
            
            try {
                tuscanyMsg = runtimeWire.getBindingInvocationChain().getHeadInvoker().invoke(tuscanyMsg);
            } catch (ServiceRuntimeException e) {
                if (e.getCause() instanceof InvocationTargetException) {
                    if ((e.getCause().getCause() instanceof RuntimeException)) {
                        tuscanyMsg.setFaultBody(e.getCause());
                    } else {
                        tuscanyMsg.setFaultBody(e.getCause().getCause());
                    }
                } else {
                    tuscanyMsg.setFaultBody(e);
                }
            } catch (IllegalStateException e) {
                tuscanyMsg.setFaultBody(e);
            } catch (Throwable e) {
                tuscanyMsg.setFaultBody(e);
            } finally {
                session.close();
            }
            
            return tuscanyMsg;
        } catch (Exception e) {
            throw new JMSBindingException(e);
        }   
    }
    
    protected Destination getRequestDestination(org.apache.tuscany.sca.invocation.Message tuscanyMsg, Session session) throws JMSBindingException, NamingException, JMSException {
        Destination requestDestination;
        if (reference.isCallback()) {
            String toURI = tuscanyMsg.getTo().getURI();
            if (toURI != null && toURI.startsWith("jms:")) {
                // the msg to uri contains the callback destination name 
                // this is an jms physical name not a jndi name so need to use session.createQueue
                requestDestination = session.createQueue(toURI.substring(4));
            } else {
                requestDestination = lookupDestination();
            }
        } else {
            requestDestination = bindingRequestDest;
        }

        return requestDestination;
    }    
    
    protected Destination getReplyToDestination(Session session) throws JMSException, JMSBindingException, NamingException {
        Destination replyToDest;
        if (operation.isNonBlocking()) {
            replyToDest = null;
        } else {
            if (bindingReplyDest != null) {
                replyToDest = bindingReplyDest;
            } else {
                replyToDest = session.createTemporaryQueue();
            }
        }
        return replyToDest;
    }  
    
    public boolean allowsPassByReference() {
        // JMS always pass by value
        return true;
    }

}
