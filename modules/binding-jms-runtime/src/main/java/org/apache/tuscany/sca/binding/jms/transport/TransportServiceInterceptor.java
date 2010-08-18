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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.NamingException;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.binding.jms.JMSBinding;
import org.apache.tuscany.sca.binding.jms.JMSBindingConstants;
import org.apache.tuscany.sca.binding.jms.JMSBindingException;
import org.apache.tuscany.sca.binding.jms.context.JMSBindingContext;
import org.apache.tuscany.sca.binding.jms.provider.JMSMessageProcessor;
import org.apache.tuscany.sca.binding.jms.provider.JMSMessageProcessorUtil;
import org.apache.tuscany.sca.binding.jms.provider.JMSResourceFactory;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;

/**
 *
 * @version $Rev$ $Date$
 */
public class TransportServiceInterceptor implements Interceptor {
    private static final Logger logger = Logger.getLogger(TransportServiceInterceptor.class.getName());
      
    private Invoker next;
    private RuntimeEndpoint endpoint;
    private JMSResourceFactory jmsResourceFactory;
    private JMSBinding jmsBinding;
    private JMSMessageProcessor requestMessageProcessor;
    private JMSMessageProcessor responseMessageProcessor;
    private RuntimeComponentService service;
    private String correlationScheme;
    private AssemblyFactory assemblyFactory;
    

    public TransportServiceInterceptor(ExtensionPointRegistry registry, JMSBinding jmsBinding, JMSResourceFactory jmsResourceFactory, RuntimeEndpoint endpoint) {
        super();
        this.jmsBinding = jmsBinding;
        this.endpoint = endpoint;
        this.jmsResourceFactory = jmsResourceFactory;
        this.requestMessageProcessor = JMSMessageProcessorUtil.getRequestMessageProcessor(registry, jmsBinding);
        this.responseMessageProcessor = JMSMessageProcessorUtil.getResponseMessageProcessor(registry, jmsBinding);
        this.service = (RuntimeComponentService)endpoint.getService();
        this.correlationScheme = jmsBinding.getCorrelationScheme();
        FactoryExtensionPoint factories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        this.assemblyFactory = factories.getFactory(AssemblyFactory.class);
    }
    
    public Message invoke(Message msg) {
        try {
            return invokeResponse(next.invoke(invokeRequest(msg)));
        } catch (Throwable e) {
            logger.log(Level.SEVERE, "Exception invoking service '" + service.getName(), e);
            JMSBindingContext context = msg.getBindingContext();
            javax.jms.Message replyJMSMsg = responseMessageProcessor.createFaultMessage(context.getJmsResponseSession(), 
                                                                                        (Throwable)e);
            msg.setBody(replyJMSMsg);
            invokeResponse(msg);
            return msg;
        } finally {
            try {
                ((JMSBindingContext)msg.getBindingContext()).closeJmsResponseSession();
                if (jmsResourceFactory.isConnectionClosedAfterUse())
                    jmsResourceFactory.closeResponseConnection(); 
            } catch (JMSException e) {
            }
        }
    }    
    
    public Message invokeRequest(Message msg) { 
//        try {
            JMSBindingContext context = msg.getBindingContext();
            javax.jms.Message requestJMSMsg = context.getJmsMsg();

            EndpointReference from = assemblyFactory.createEndpointReference();
            Endpoint fromEndpoint = assemblyFactory.createEndpoint();
            from.setTargetEndpoint(fromEndpoint);
            from.setStatus(EndpointReference.Status.WIRED_TARGET_FOUND_AND_MATCHED);
            msg.setFrom(from);
            Endpoint callbackEndpoint = assemblyFactory.createEndpoint();
//            callbackEndpoint.setURI(callbackAddress); // TODO: is this needed? Seems to work without it
            callbackEndpoint.setUnresolved(true);
            from.setCallbackEndpoint(callbackEndpoint);

            return msg;
//        } catch (JMSException e) {
//            throw new JMSBindingException(e);
//        } 
    }
    
    public Message invokeResponse(Message msg) { 
        JMSBindingContext context = msg.getBindingContext();
        try {

            //if operation is oneway, return back.
            Operation operation = msg.getOperation();
            if (operation != null && operation.isNonBlocking()) {
                return msg;
            }

            Session session = context.getJmsResponseSession();
            javax.jms.Message requestJMSMsg = context.getJmsMsg();
            javax.jms.Message responseJMSMsg = msg.getBody();
            
            Destination replyDest = requestJMSMsg.getJMSReplyTo();
            if (replyDest == null) {
                if (jmsBinding.getResponseDestinationName() != null) {
                    try {
                        replyDest = jmsResourceFactory.lookupDestination(jmsBinding.getResponseDestinationName());
                    } catch (NamingException e) {
                        throw new JMSBindingException("Exception lookingup response destination", e);
                    }
                }
            }

            if (replyDest == null) {
                // assume no reply is expected
                if (msg.getBody() != null) {
                    logger.log(Level.FINE, "JMS service '" + service.getName() + "' dropped response as request has no replyTo");
                }
                return msg;
            }

            if (msg.getOperation() != null) {
                String operationName = msg.getOperation().getName();
                if (jmsBinding.getOperationJMSPriority(operationName) != null) {
                    responseJMSMsg.setJMSPriority(jmsBinding.getOperationJMSPriority(operationName));
                }
        
                if (jmsBinding.getOperationJMSType(operationName) != null) {
                    responseJMSMsg.setJMSType(jmsBinding.getOperationJMSType(operationName));
                }
                if (jmsBinding.getOperationJMSDeliveryMode(operationName) != null) {
                    if (jmsBinding.getOperationJMSDeliveryMode(operationName)) {
                        responseJMSMsg.setJMSDeliveryMode(DeliveryMode.PERSISTENT);
                    } else {
                        responseJMSMsg.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
                    }
                }
            }
    
            if (correlationScheme == null || 
                JMSBindingConstants.CORRELATE_MSG_ID.equalsIgnoreCase(correlationScheme)) {
                responseJMSMsg.setJMSCorrelationID(requestJMSMsg.getJMSMessageID());
            } else if (JMSBindingConstants.CORRELATE_CORRELATION_ID.equalsIgnoreCase(correlationScheme)) {
                responseJMSMsg.setJMSCorrelationID(requestJMSMsg.getJMSCorrelationID());
            }                
                       
            MessageProducer producer = session.createProducer(replyDest);
            
            // Set jms header attributes in producer, not message.
            int deliveryMode = requestJMSMsg.getJMSDeliveryMode();
            producer.setDeliveryMode(deliveryMode);
            int deliveryPriority = requestJMSMsg.getJMSPriority();
            producer.setPriority(deliveryPriority);
    
            producer.send((javax.jms.Message)msg.getBody());
    
            producer.close();
            
            return msg;
    
        } catch (JMSException e) {
            throw new JMSBindingException(e);
        } finally {
            context.closeJmsResponseSession();
        }
    }    
    
    public Invoker getNext() {
        return next;
    }

    public void setNext(Invoker next) {
        this.next = next;
    }    
   
}
