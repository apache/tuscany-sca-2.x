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

package org.apache.tuscany.sca.binding.jms.host;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.NamingException;
import javax.resource.spi.ActivationSpec;

import org.apache.tuscany.sca.binding.jms.JMSBinding;
import org.apache.tuscany.sca.binding.jms.JMSBindingConstants;
import org.apache.tuscany.sca.binding.jms.JMSBindingException;
import org.apache.tuscany.sca.binding.jms.provider.JMSResourceFactory;
import org.apache.tuscany.sca.work.WorkScheduler;

/**
 * Implementation of a JMS response queue handler for async responses
 * 
 * @version $Rev$ $Date$
 */
public class AsyncResponseJMSServiceListener implements JMSServiceListener {
    private static final Logger logger = Logger.getLogger(AsyncResponseJMSServiceListener.class.getName());

    private MessageListener listener;
    private String serviceName;
    private JMSBinding jmsBinding;
    private WorkScheduler workScheduler;

    private JMSResourceFactory jmsResourceFactory;
    private MessageConsumer consumer;
    private boolean running;

    private Destination destination;

    public AsyncResponseJMSServiceListener(MessageListener listener, String serviceName, 
    		JMSBinding jmsBinding, WorkScheduler workScheduler, JMSResourceFactory rf) {
        this.listener = listener;
        this.serviceName = serviceName;
        this.jmsBinding = jmsBinding;
        this.workScheduler = workScheduler;
        this.jmsResourceFactory = rf;
    }
    
    public void start() {
        this.running = true;

        try {
            registerListener();
        } catch (Exception e) {
            if (e instanceof JMSBindingException) throw (JMSBindingException)e;
            throw new JMSBindingException("Error starting JMSAsyncResponse endpoint", e);
        }
    } // end start

    public void stop() {
        this.running = false;
        try {
            consumer.close();
            jmsResourceFactory.closeConnection();
            jmsResourceFactory.closeResponseConnection();
        } catch (Exception e) {
            // if using an embedded broker then when shutting down Tuscany the broker may get closed
            // before this stop method is called. I can't see how to detect that so for now just
            // ignore the exception if the message is that the transport is already disposed
            if ((e.getMessage() == null) || !e.getMessage().contains("disposed")) {
                throw new JMSBindingException("Error stopping JMSServiceBinding", e);
            }
        }
    } // end stop

    private void registerListener() throws NamingException, JMSException {

        Session session = jmsResourceFactory.createSession();
        lookupActivationSpec();
        destination = lookupQueue( jmsBinding.getResponseDestinationName() );
        if (destination == null) {
        	throw new JMSBindingException("Unable to create Async Response queue");
        } // end if

        if (jmsBinding.getJMSSelector() != null) {
            consumer = session.createConsumer(destination, jmsBinding.getJMSSelector());
        } else {
            consumer = session.createConsumer(destination);
        } // end if

        try {

            consumer.setMessageListener(listener);
            jmsResourceFactory.startConnection();

        } catch (javax.jms.JMSException e) {

            // setMessageListener not allowed in JEE container so use Tuscany threads

            jmsResourceFactory.startConnection();
            workScheduler.scheduleWork(new Runnable() {
                public void run() {
                    try {
                        while (running) {
                            final Message msg = consumer.receive();
                            workScheduler.scheduleWork(new Runnable() {
                                public void run() {
                                    try {
                                        listener.onMessage(msg);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    } // end try
                                } // end method run
                            }); 
                        } // end while
                    } catch (Exception e) {
                        e.printStackTrace();
                    } // end try
                } // end method run
            });
        } // end try
        logger.log(Level.INFO, "JMS AsyncResponse handler '"
            + serviceName
            + "' listening on destination "
            + ((destination instanceof Queue) ? ((Queue)destination).getQueueName() : ((Topic)destination).getTopicName()));
    } // end method registerListener

    // Stub code for ActivationSpec support that throws appropriate errors
    private void lookupActivationSpec() {        
        if ( jmsBinding.getActivationSpecName() != null )  {
        	String createMode = jmsBinding.getActivationSpecCreate();
        	if ( JMSBindingConstants.CREATE_ALWAYS.equals(createMode) ) {
        		ActivationSpec spec = jmsResourceFactory.lookupActivationSpec(jmsBinding.getActivationSpecName());
        		if ( spec != null ) {
        			throw new JMSBindingException("ActivationSpec specifies create mode of \"always\" but resource already exists.");
        		}
        		throw new JMSBindingException("Can not create ActivationSpec");
        	} else if ( JMSBindingConstants.CREATE_IF_NOT_EXIST.equals(createMode)) {
        		ActivationSpec spec = jmsResourceFactory.lookupActivationSpec(jmsBinding.getActivationSpecName());
        		if ( spec == null ) {
        			throw new JMSBindingException("Can not create ActivationSpec");
        		}
        	} else if ( JMSBindingConstants.CREATE_NEVER.equals(createMode)) {
        		ActivationSpec spec = jmsResourceFactory.lookupActivationSpec(jmsBinding.getActivationSpecName());
        		if ( spec == null )
        			throw new JMSBindingException("ActivationSpec specifies create mode of \"never\" but resource does not exist at jndiName " + jmsBinding.getActivationSpecName());
        			
        	} // end if
        } // end if
	} // end method lookupActivationSpec

	/**
     * Looks up the Async Response Queue for the JMS Binding.
     * <p>
     * What happens in the look up will depend on the create mode specified for the JMS Binding:
     * <ul>
     * <li>always - the JMS queue is always created. It is an error if the queue already exists
     * <li>ifnotexist - the JMS queue is created if it does not exist. It is not an error if the queue already exists
     * <li>never - the JMS queue is never created. It is an error if the queue does not exist
     * </ul>
     * See the SCA JMS Binding specification for more information.
     * <p>
     * @param queueName - the name of the Async Response queue
     * @return The Async Response queue.
     * @throws NamingException Failed to lookup JMS queue
     * @throws JMSBindingException Failed to lookup JMS Queue. Probable cause is that the JMS queue's current existence/non-existence is not
     *                 compatible with the create mode specified on the binding
     */
    private Destination lookupQueue(String queueName ) throws NamingException, JMSBindingException {
    	
        Destination destination = jmsResourceFactory.lookupDestination(queueName);

        String qCreateMode = jmsBinding.getDestinationCreate();
        if (qCreateMode.equals(JMSBindingConstants.CREATE_ALWAYS)) {
            // In this mode, the queue must not already exist as we are creating it
            if (destination != null) {
                throw new JMSBindingException("JMS Destination " + queueName
                    + " already exists but has create mode of \""
                    + qCreateMode
                    + "\" while registering service "
                    + serviceName
                    + " listener");
            } // end if

            // Create the queue
            destination = jmsResourceFactory.createDestination(queueName);

        } else if (qCreateMode.equals(JMSBindingConstants.CREATE_IF_NOT_EXIST)) {
            // In this mode, the queue may nor may not exist. It will be created if it does not exist
            // but don't create when using jms:jndi uri format
            if (destination == null && !"jndi".equals(jmsBinding.getDestinationType())) {
                destination = jmsResourceFactory.createDestination(queueName);
            } // end if

        } else if (qCreateMode.equals(JMSBindingConstants.CREATE_NEVER)) {
            // In this mode, the queue must have already been created.
            if (destination == null) {
                throw new JMSBindingException("JMS Destination " + queueName
                    + " not found but create mode of \""
                    + qCreateMode
                    + "\" while registering service "
                    + serviceName
                    + " listener");
            } // end if
        } // end if

        // Make sure we ended up with a queue
        if (destination == null) {
            throw new JMSBindingException("JMS Destination " + queueName
                + " not found with create mode of \""
                + qCreateMode
                + "\" while registering service "
                + serviceName
                + " listener");
        } // end if

        // Make sure its the expected type (queue or topic)
        String type = (destination instanceof Queue) ? JMSBindingConstants.DESTINATION_TYPE_QUEUE : JMSBindingConstants.DESTINATION_TYPE_TOPIC;
        if ("jndi".equals(jmsBinding.getDestinationType())) {
            jmsBinding.setDestinationType(type);            
        } else {
            if (!type.equals(jmsBinding.getDestinationType())) {
                throw new JMSBindingException("JMS Destination " + queueName
                                              + " expecting type of " 
                                              + jmsBinding.getDestinationType()
                                              + " but found "
                                              + type
                                              + " while registering service "
                                              + serviceName
                                              + " listener");
            } // end if
        } // end if
        
        return destination;
    } // end method lookupDestinationQueue(String)

	public String getDestinationName() {
        try {
            if (destination instanceof Queue) {
                return ((Queue)destination).getQueueName();
            } else if (destination instanceof Topic) {
                return ((Topic)destination).getTopicName();
            } else {
                return null;
            }
        } catch (JMSException e) {
            throw new JMSBindingException(e);
        }
    } // end method getDestinationName

} // end class AsyncResponseJMSServiceListener
