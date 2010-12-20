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

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.naming.NamingException;

import org.apache.tuscany.sca.binding.jms.JMSBinding;
import org.apache.tuscany.sca.binding.jms.JMSBindingConstants;
import org.apache.tuscany.sca.binding.jms.JMSBindingException;
import org.apache.tuscany.sca.binding.jms.context.JMSBindingContext;
import org.apache.tuscany.sca.core.invocation.InterceptorAsyncImpl;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.util.FaultException;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 * Invoker for the JMS binding.
 * 
 * @version $Rev$ $Date$
 */
public class RRBJMSBindingInvoker extends InterceptorAsyncImpl {

    protected Operation operation;
    protected String operationName;

    protected JMSBinding jmsBinding;
    protected JMSResourceFactory jmsResourceFactory;
    protected Destination bindingRequestDest;
    protected Destination bindingReplyDest;
    protected RuntimeEndpointReference endpointReference;

    public RRBJMSBindingInvoker(Operation operation, JMSResourceFactory jmsResourceFactory, RuntimeEndpointReference epr) {

        this.operation = operation;
        operationName = operation.getName();

        this.endpointReference = epr;
        this.jmsBinding = (JMSBinding) epr.getBinding();
        this.jmsResourceFactory = jmsResourceFactory;
       
        try {
            // If this is a callback reference, the destination is determined dynamically based on
            // properties of the inbound service request.  We should not look for or require a
            // statically-configured destination unless a message is received that does not have
            // the necessary properties.  
            bindingRequestDest = lookupDestination();
            bindingReplyDest = lookupResponseDestination();
        } catch (NamingException e) {
            throw new JMSBindingException(e);
        } // end try
    } // end constructor

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
            if (queueName == null) {
                return null;
            }
        } else {
            queueName = jmsBinding.getDestinationName();
            queueType = "JMS Destination ";
            qCreateMode = jmsBinding.getDestinationCreate();
        }

        // Remove jms:jndi: prefix if present
        if (queueName.startsWith("jms:jndi:")) {
            queueName = queueName.substring("jms:jndi:".length());
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
            // but don't create when using jms:jndi uri format
            if (dest == null && !"jndi".equals(jmsBinding.getDestinationType())) {
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
    } // end method lookupDestinationQueue
    
    /**
     * Get the next in the chain from the binding invocation chain
     */
    public Invoker getNext() {
        return (Invoker)endpointReference.getBindingInvocationChain().getHeadInvoker();
    } // end method getNext


    public org.apache.tuscany.sca.invocation.Message invoke(org.apache.tuscany.sca.invocation.Message tuscanyMsg) {
        try {
            // populate the message context with JMS binding information
            JMSBindingContext context = new JMSBindingContext();
            context.setJmsResourceFactory(jmsResourceFactory);
            tuscanyMsg.setBindingContext(context);
            
            // get a jms session to cover the creation and sending of the message
            Session session = context.getJmsSession();

            context.setRequestDestination(getRequestDestination(tuscanyMsg, session));
            context.setReplyToDestination(getReplyToDestination(session));
            
            try {
                tuscanyMsg = endpointReference.getBindingInvocationChain().getHeadInvoker().invoke(tuscanyMsg);
            } catch (ServiceRuntimeException e) {
                if (e.getCause() instanceof InvocationTargetException) {
                    if ((e.getCause().getCause() instanceof RuntimeException)) {
                        tuscanyMsg.setFaultBody(e.getCause());
                    } else {
                        tuscanyMsg.setFaultBody(((InvocationTargetException)e.getCause()).getTargetException());
                    }
                } else if (e.getCause() instanceof FaultException) {
                    tuscanyMsg.setFaultBody(e.getCause());
                } else {
                    tuscanyMsg.setFaultBody(e);
                }
            } catch (IllegalStateException e) {
                tuscanyMsg.setFaultBody(e);
            } catch (Throwable e) {
                tuscanyMsg.setFaultBody(e);
            } finally {
                context.closeJmsSession();
                if (jmsResourceFactory.isConnectionClosedAfterUse()) {
                    jmsResourceFactory.closeConnection();
                }
            }
            
            return tuscanyMsg;
        } catch (Exception e) {
            throw new JMSBindingException(e);
        }   
    }
    
    protected Destination getRequestDestination(org.apache.tuscany.sca.invocation.Message tuscanyMsg, Session session) throws JMSBindingException, NamingException, JMSException {
        Destination requestDestination;
//      if (!reference.isCallback()) { // TODO: 2.x migration, is this check needed?
//            String toURI = tuscanyMsg.getTo().getURI();
//            if (toURI != null && toURI.startsWith("jms:")) {
//                // the msg to uri contains the callback destination name 
//                // this is an jms physical name not a jndi name so need to use session.createQueue
//                requestDestination = session.createQueue(toURI.substring(4));
//            } else {
//                requestDestination = lookupDestination();
//            }
//        } else {
            requestDestination = bindingRequestDest;
//        }

        return requestDestination;
    }    
    
    protected Destination getReplyToDestination(Session session) throws JMSException, JMSBindingException, NamingException {
        Destination replyToDest;
        // [rfeng] If the oneway operation is part of bi-directional interface, the JMSReplyTo should be set
        if (operation.isNonBlocking() && endpointReference.getComponentReferenceInterfaceContract()
            .getCallbackInterface() == null) {
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

	/**
	 * Process forward request message
	 * @param tuscanyMsg - the request message
	 * @return the processed version of the request message
	 */
	public Message processRequest(Message tuscanyMsg) {
        try {
            // populate the message context with JMS binding information
            JMSBindingContext context = new JMSBindingContext();
            context.setJmsResourceFactory(jmsResourceFactory);
            tuscanyMsg.setBindingContext(context);
            
            // get a JMS session to cover the creation and sending of the message
            Session session = context.getJmsSession();

            context.setRequestDestination(getRequestDestination(tuscanyMsg, session));
            context.setReplyToDestination(getReplyToDestination(session));
            
            return tuscanyMsg;
        } catch (Exception e) {
            throw new JMSBindingException(e);
        } // end try   
	} // end method processRequest
	
	/**
	 * Post processing for a request message where an error occurred
	 * @param tuscanyMsg
	 * @return the post processed message
	 */
	public Message postProcessRequest(Message tuscanyMsg, Throwable e) {
		// Exception handling
        if ( e instanceof ServiceRuntimeException ) {
	        if (e.getCause() instanceof InvocationTargetException) {
	            if ((e.getCause().getCause() instanceof RuntimeException)) {
	                tuscanyMsg.setFaultBody(e.getCause());
	            } else {
	                tuscanyMsg.setFaultBody(((InvocationTargetException)e.getCause()).getTargetException());
	            } // end if
	        } else if (e.getCause() instanceof FaultException) {
	            tuscanyMsg.setFaultBody(e.getCause());
	        } else {
	            tuscanyMsg.setFaultBody(e);
	        } // end if
        } else {
        	tuscanyMsg.setFaultBody(e);
        } // end if 
        
        return postProcessRequest( tuscanyMsg );
	} // end method postProcessRequest
	
	/**
	 * General post processing for a request message
	 * - close out the JMS session & connection
	 * @param tuscanyMsg
     * @return the post processed message
	 */
	public Message postProcessRequest(Message tuscanyMsg) {
        // Close of JMS session
		try {
			JMSBindingContext context = tuscanyMsg.getBindingContext();
	        context.closeJmsSession();
	        if (jmsResourceFactory.isConnectionClosedAfterUse()) {
	            jmsResourceFactory.closeConnection();
	        } // end if
		} catch (JMSException ex) {
			throw new JMSBindingException(ex);
		} // end try
		return tuscanyMsg;
	} // end method postProcessRequest

    /**
     * Process response message
	 * @param tuscanyMsg - the response message
	 * @return the processed version of the response message
     */
	public Message processResponse(Message tuscanyMsg) {
		// For async handling, there is nothing to do here
		return tuscanyMsg;
	}  
    
}
