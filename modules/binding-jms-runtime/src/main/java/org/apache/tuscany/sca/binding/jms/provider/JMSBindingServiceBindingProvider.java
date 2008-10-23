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

import java.util.List;
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

import org.apache.axiom.om.OMElement;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.OperationSelector;
import org.apache.tuscany.sca.assembly.WireFormat;
import org.apache.tuscany.sca.binding.jms.impl.JMSBinding;
import org.apache.tuscany.sca.binding.jms.impl.JMSBindingConstants;
import org.apache.tuscany.sca.binding.jms.impl.JMSBindingException;
import org.apache.tuscany.sca.binding.jms.operationselector.jmsdefault.OperationSelectorJMSDefault;
import org.apache.tuscany.sca.binding.jms.operationselector.jmsdefault.OperationSelectorJMSDefaultReferenceInterceptor;
import org.apache.tuscany.sca.binding.jms.operationselector.jmsdefault.OperationSelectorJMSDefaultServiceInterceptor;
import org.apache.tuscany.sca.binding.jms.wireformat.jmsdefault.WireFormatJMSDefault;
import org.apache.tuscany.sca.binding.jms.wireformat.jmsdefault.WireFormatJMSDefaultReferenceInterceptor;
import org.apache.tuscany.sca.binding.jms.wireformat.jmsdefault.WireFormatJMSDefaultServiceInterceptor;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.binding.ws.WebServiceBindingFactory;
import org.apache.tuscany.sca.binding.ws.wsdlgen.BindingWSDLGenerator;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.invocation.Phase;
import org.apache.tuscany.sca.provider.BindingProviderFactory;
import org.apache.tuscany.sca.provider.OperationSelectorProvider;
import org.apache.tuscany.sca.provider.OperationSelectorProviderFactory;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.provider.ServiceBindingProviderRRB;
import org.apache.tuscany.sca.provider.WireFormatProvider;
import org.apache.tuscany.sca.provider.WireFormatProviderFactory;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.apache.tuscany.sca.work.WorkScheduler;

/**
 * Implementation of the JMS service binding provider.
 * 
 * @version $Rev$ $Date$
 */
public class JMSBindingServiceBindingProvider implements ServiceBindingProviderRRB {
    private static final Logger logger = Logger.getLogger(JMSBindingServiceBindingProvider.class.getName());

    private RuntimeComponentService service;
    private Binding targetBinding;
    private JMSBinding jmsBinding;
    private JMSResourceFactory jmsResourceFactory;
    private MessageConsumer consumer;
    private WorkScheduler workScheduler;
    private boolean running;

    private Destination destination;

    private ExtensionPointRegistry extensionPoints;

    private RuntimeComponent component;
    private InterfaceContract wsdlInterfaceContract;
    

    private ProviderFactoryExtensionPoint providerFactories;
    private ModelFactoryExtensionPoint modelFactories;
    
    private MessageFactory messageFactory;
    
    private OperationSelectorProviderFactory operationSelectorProviderFactory;
    private OperationSelectorProvider operationSelectorProvider;
    
    private WireFormatProviderFactory requestWireFormatProviderFactory;
    private WireFormatProvider requestWireFormatProvider;
    
    private WireFormatProviderFactory responseWireFormatProviderFactory;
    private WireFormatProvider responseWireFormatProvider;

    public JMSBindingServiceBindingProvider(RuntimeComponent component, RuntimeComponentService service, Binding targetBinding, JMSBinding binding, WorkScheduler workScheduler, ExtensionPointRegistry extensionPoints, JMSResourceFactory jmsResourceFactory) {
        this.component = component;
        this.service = service;
        this.jmsBinding = binding;
        this.workScheduler = workScheduler;
        this.targetBinding = targetBinding;
        this.extensionPoints = extensionPoints;
        this.jmsResourceFactory = jmsResourceFactory;

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
        
        // Get Message factory
        modelFactories = extensionPoints.getExtensionPoint(ModelFactoryExtensionPoint.class);
        messageFactory = modelFactories.getFactory(MessageFactory.class);

        // Get the factories/providers for operation selection
        
        // if no operation selector is specified then assume the default
        if (jmsBinding.getOperationSelector() == null){
            jmsBinding.setOperationSelector(new OperationSelectorJMSDefault());
        }
        
        this.providerFactories = extensionPoints.getExtensionPoint(ProviderFactoryExtensionPoint.class);
        this.operationSelectorProviderFactory =
            (OperationSelectorProviderFactory)providerFactories.getProviderFactory(jmsBinding.getOperationSelector().getClass());
        this.operationSelectorProvider = operationSelectorProviderFactory.createServiceOperationSelectorProvider(component, service, jmsBinding);
        
        // Get the factories/providers for wire format
        
        // if no request wire format specified then assume the default
        if (jmsBinding.getRequestWireFormat() == null){
            jmsBinding.setRequestWireFormat(new WireFormatJMSDefault());
        }
        
        // if no response wire format specific then assume the default
        if (jmsBinding.getResponseWireFormat() == null){
            jmsBinding.setResponseWireFormat(new WireFormatJMSDefault());
         }
        
        this.requestWireFormatProviderFactory = 
            (WireFormatProviderFactory)providerFactories.getProviderFactory(jmsBinding.getRequestWireFormat().getClass());
        this.requestWireFormatProvider = requestWireFormatProviderFactory.createServiceWireFormatProvider(component, service, jmsBinding);
        
        this.responseWireFormatProviderFactory = 
            (WireFormatProviderFactory)providerFactories.getProviderFactory(jmsBinding.getResponseWireFormat().getClass());
        this.responseWireFormatProvider = responseWireFormatProviderFactory.createServiceWireFormatProvider(component, service, jmsBinding);
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

        MessageListener tmpListener = null;
        
        /*
         * TODO a test to allow RRB experiments to take place without breaking everything else
         *      RRB stuff only happens if you add a wireFormat to a composite file
         */
        if (jmsBinding.getRequestWireFormat() != null ){
            tmpListener = new RRBJMSBindingListener(jmsBinding, jmsResourceFactory, service, targetBinding, messageFactory);
        } else {
            tmpListener = new DefaultJMSBindingListener(jmsBinding, jmsResourceFactory, service, targetBinding);
        }
        
        final MessageListener listener = tmpListener;
        
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
    
    /*
     * RRB test methods
     * Interceptor selection is hard coded to the default here but of course should
     * pick up the appropriate interceptor based on wireFormat and operationSelector 
     * elements in the SCDL
     */
    public void configureBindingChain(RuntimeWire runtimeWire) {
        
        InvocationChain bindingChain = runtimeWire.getBindingInvocationChain();
        
        // add operation selector interceptor
        bindingChain.addInterceptor(operationSelectorProvider.getPhase(), operationSelectorProvider.createInterceptor());
        
        // add request wire format
        bindingChain.addInterceptor(requestWireFormatProvider.getPhase(), requestWireFormatProvider.createInterceptor());
        
        // add response wire format, but only add it if it's different from the request
        if (!jmsBinding.getRequestWireFormat().equals(jmsBinding.getResponseWireFormat())){
            bindingChain.addInterceptor(responseWireFormatProvider.getPhase(), responseWireFormatProvider.createInterceptor());
        }
    }
}
