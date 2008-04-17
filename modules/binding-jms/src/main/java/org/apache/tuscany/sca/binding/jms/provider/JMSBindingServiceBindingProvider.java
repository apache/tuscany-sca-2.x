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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.naming.NamingException;

import org.apache.tuscany.sca.binding.jms.impl.JMSBinding;
import org.apache.tuscany.sca.binding.jms.impl.JMSBindingConstants;
import org.apache.tuscany.sca.binding.jms.impl.JMSBindingException;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.work.WorkScheduler;

/**
 * Implementation of the JMS service binding provider.
 * 
 * @version $Rev$ $Date$
 */
public class JMSBindingServiceBindingProvider implements ServiceBindingProvider {
    private final static Logger logger = Logger.getLogger(JMSBindingServiceBindingProvider.class.getName());

    private RuntimeComponentService service;
    private JMSBinding jmsBinding;
    private JMSResourceFactory jmsResourceFactory;
    private MessageConsumer consumer;
    private WorkScheduler workScheduler;
    private boolean running;

    public JMSBindingServiceBindingProvider(RuntimeComponent component,
                                            RuntimeComponentService service,
                                            JMSBinding binding,
                                            WorkScheduler workScheduler) {
        this.service = service;
        this.jmsBinding = binding;
        this.workScheduler = workScheduler;

        jmsResourceFactory = new JMSResourceFactory(binding.getConnectionFactoryName(), binding.getInitialContextFactoryName(), binding.getJndiURL());

        if (jmsBinding.getDestinationName().equals(JMSBindingConstants.DEFAULT_DESTINATION_NAME)) {
            // use the SCA service name as the default destination name
            jmsBinding.setDestinationName(service.getName());
        }

        if (XMLTextMessageProcessor.class.isAssignableFrom(jmsBinding.getRequestMessageProcessor().getClass())) {
            setXMLDataBinding(service);
        }

    }

    protected void setXMLDataBinding(RuntimeComponentService service) {
        if (service.getInterfaceContract() != null) {
            try {
                InterfaceContract ic = (InterfaceContract)service.getInterfaceContract().clone();

                Interface ii = (Interface)ic.getInterface().clone();
                ii.resetDataBinding("org.apache.axiom.om.OMElement");
                ic.setInterface(ii);
                service.setInterfaceContract(ic);

            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public InterfaceContract getBindingInterfaceContract() {
        return service.getInterfaceContract();
    }

    public boolean supportsOneWayInvocation() {
        return true;
    }

    public void start() {
        this.running = true;

        try {
            registerListerner();
        } catch (Exception e) {
            throw new JMSBindingException("Error starting JMSServiceBinding", e);
        }
    }

    public void stop() {
        this.running = false;
        try {
            consumer.close();
            jmsResourceFactory.closeConnection();
        } catch (Exception e) {
            throw new JMSBindingException("Error stopping JMSServiceBinding", e);
        }
    }

    private void registerListerner() throws NamingException, JMSException {

        Session session = jmsResourceFactory.createSession();
        Destination destination = lookupDestinationQueue();

        consumer = session.createConsumer(destination);

        final JMSBindingListener listener = new JMSBindingListener(jmsBinding, jmsResourceFactory, service);
        try {

            consumer.setMessageListener(listener);
            jmsResourceFactory.startConnection();

        } catch (javax.jms.IllegalStateException e) {
            
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
                                   }
                               }});
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }});
        }
        logger.log(Level.INFO, "JMS service '" + service.getName() + "' listening on destination " + jmsBinding.getDestinationName());
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
     * @return The Destination queue.
     * @throws NamingException Failed to lookup JMS queue
     * @throws JMSBindingException Failed to lookup JMS Queue. Probable cause is that the JMS queue's current
     *             existence/non-existence is not compatible with the create mode specified on the binding
     */
    private Destination lookupDestinationQueue() throws NamingException, JMSBindingException {
        Destination destination = jmsResourceFactory.lookupDestination(jmsBinding.getDestinationName());

        String qCreateMode = jmsBinding.getDestinationCreate();
        if (qCreateMode.equals(JMSBindingConstants.CREATE_ALWAYS)) {
            // In this mode, the queue must not already exist as we are creating it
            if (destination != null) {
                throw new JMSBindingException("JMS Destination " + jmsBinding.getDestinationName()
                    + " already exists but has create mode of \""
                    + qCreateMode
                    + "\" while registering service "
                    + service.getName()
                    + " listener");
            }

            // Create the queue
            destination = jmsResourceFactory.createDestination(jmsBinding.getDestinationName());

        } else if (qCreateMode.equals(JMSBindingConstants.CREATE_IF_NOT_EXIST)) {
            // In this mode, the queue may nor may not exist. It will be created if it does not exist
            if (destination == null) {
                destination = jmsResourceFactory.createDestination(jmsBinding.getDestinationName());
            }

        } else if (qCreateMode.equals(JMSBindingConstants.CREATE_NEVER)) {
            // In this mode, the queue must have already been created.
            if (destination == null) {
                throw new JMSBindingException("JMS Destination " + jmsBinding.getDestinationName()
                    + " not found but create mode of \""
                    + qCreateMode
                    + "\" while registering service "
                    + service.getName()
                    + " listener");
            }
        }

        // Make sure we ended up with a queue
        if (destination == null) {
            throw new JMSBindingException("JMS Destination " + jmsBinding.getDestinationName()
                + " not found with create mode of \""
                + qCreateMode
                + "\" while registering service "
                + service.getName()
                + " listener");
        }

        return destination;
    }
}
