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
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.NamingException;

import org.apache.axiom.om.OMElement;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.binding.jms.impl.JMSBinding;
import org.apache.tuscany.sca.binding.jms.impl.JMSBindingConstants;
import org.apache.tuscany.sca.binding.jms.impl.JMSBindingException;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.binding.ws.WebServiceBindingFactory;
import org.apache.tuscany.sca.binding.ws.wsdlgen.BindingWSDLGenerator;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
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
    private static final Logger logger = Logger.getLogger(JMSBindingServiceBindingProvider.class.getName());

    private RuntimeComponentService service;
    private Binding targetBinding;
    private JMSBinding jmsBinding;
    private JMSResourceFactoryImpl jmsResourceFactory;
    private MessageConsumer consumer;
    private WorkScheduler workScheduler;
    private boolean running;

    private Destination destination;

    private ExtensionPointRegistry extensionPoints;

    private RuntimeComponent component;
    private InterfaceContract wsdlInterfaceContract;

    public JMSBindingServiceBindingProvider(RuntimeComponent component, RuntimeComponentService service, Binding targetBinding, JMSBinding binding, WorkScheduler workScheduler, ExtensionPointRegistry extensionPoints) {
        this.component = component;
        this.service = service;
        this.jmsBinding = binding;
        this.workScheduler = workScheduler;
        this.targetBinding = targetBinding;
        this.extensionPoints = extensionPoints;

        jmsResourceFactory = new JMSResourceFactoryImpl(binding.getConnectionFactoryName(), binding.getInitialContextFactoryName(), binding.getJndiURL());

        if (jmsBinding.getDestinationName().equals(JMSBindingConstants.DEFAULT_DESTINATION_NAME)) {
            if (!service.isCallback()) {
                // use the SCA service name as the default destination name
                jmsBinding.setDestinationName(service.getName());
            }
        }

        if (XMLTextMessageProcessor.class.isAssignableFrom(JMSMessageProcessorUtil.getRequestMessageProcessor(jmsBinding).getClass())) {
            if (!isOnMessage()) {
                setXMLDataBinding(service);
            }
        }

    }
    
    protected boolean isOnMessage() {
        InterfaceContract ic = getBindingInterfaceContract();
        if (ic.getInterface().getOperations().size() != 1) {
            return false;
        }
        return "onMessage".equals(ic.getInterface().getOperations().get(0).getName());
    }

    protected void setXMLDataBinding(RuntimeComponentService service) {
        if (service.getInterfaceContract() != null) {
            WebServiceBindingFactory wsFactory = extensionPoints.getExtensionPoint(WebServiceBindingFactory.class);
            WebServiceBinding wsBinding = wsFactory.createWebServiceBinding();
            BindingWSDLGenerator.generateWSDL(component, service, wsBinding, extensionPoints, null);
            wsdlInterfaceContract = wsBinding.getBindingInterfaceContract();
            wsdlInterfaceContract.getInterface().resetDataBinding(OMElement.class.getName());
            
            // TODO: TUSCANY-xxx, section 5.2 "Default Data Binding" in the JMS binding spec  

//            try {
//                InterfaceContract ic = (InterfaceContract)service.getInterfaceContract().clone();
//                Interface ii = ic.getInterface();
//                if (ii.getOperations().size() == 1 && "onMessage".equals(ii.getOperations().get(0).getName())) {
//                    return;
//                }
//                ii = (Interface)ii.clone();
//                ii.resetDataBinding("org.apache.axiom.om.OMElement");
//                ic.setInterface(ii);
//                service.setInterfaceContract(ic);
//
//            } catch (CloneNotSupportedException e) {
//                throw new RuntimeException(e);
//            }
        }
    }

    public InterfaceContract getBindingInterfaceContract() {
        if (wsdlInterfaceContract != null) {
            return wsdlInterfaceContract;
        } else {
            return service.getInterfaceContract();
        }
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
            // if using an embedded broker then when shutting down Tuscany the broker may get closed
            // before this stop method is called. I can't see how to detect that so for now just
            // ignore the exception if the message is that the transport is already disposed
            if (!"Transport disposed.".equals(e.getMessage())) {
                throw new JMSBindingException("Error stopping JMSServiceBinding", e);
            }
        }
    }

    private void registerListerner() throws NamingException, JMSException {

        Session session = jmsResourceFactory.createSession();
        destination = lookupDestinationQueue();
        if (destination == null) {
            destination = session.createTemporaryQueue();
        }

        if (jmsBinding.getJMSSelector() != null) {
            consumer = session.createConsumer(destination, jmsBinding.getJMSSelector());
        } else {
            consumer = session.createConsumer(destination);
        }

        final JMSBindingListener listener = new JMSBindingListener(jmsBinding, jmsResourceFactory, service, targetBinding);
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
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        logger.log(Level.INFO, "JMS " + (service.isCallback() ? "callback service" : "service")
            + " '"
            + service.getName()
            + "' listening on destination "
            + ((destination instanceof Queue) ? ((Queue)destination).getQueueName() : ((Topic)destination).getTopicName()));
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
     * @throws JMSBindingException Failed to lookup JMS Queue. Probable cause is that the JMS queue's current existence/non-existence is not
     *                 compatible with the create mode specified on the binding
     */
    private Destination lookupDestinationQueue() throws NamingException, JMSBindingException {

        if (service.isCallback() && JMSBindingConstants.DEFAULT_DESTINATION_NAME.equals(jmsBinding.getDestinationName())) {
            // if its a callback service returning null indicates to use a temporary queue
            return null;
        }

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
    }
}
