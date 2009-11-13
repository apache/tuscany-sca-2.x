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
package org.apache.tuscany.sca.binding.jms.operationselector.jmsdefault.runtime;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.util.List;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.tuscany.sca.binding.jms.JMSBinding;
import org.apache.tuscany.sca.binding.jms.JMSBindingException;
import org.apache.tuscany.sca.binding.jms.context.JMSBindingContext;
import org.apache.tuscany.sca.binding.jms.provider.JMSMessageProcessor;
import org.apache.tuscany.sca.binding.jms.provider.JMSMessageProcessorUtil;
import org.apache.tuscany.sca.binding.jms.provider.JMSResourceFactory;
import org.apache.tuscany.sca.binding.jms.wireformat.WireFormatJMSDefault;
import org.apache.tuscany.sca.binding.jms.wireformat.WireFormatJMSTextXML;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeWire;

/**
 * Policy handler to handle PolicySet related to Logging with the QName
 * {http://tuscany.apache.org/xmlns/sca/1.0/impl/java}LoggingPolicy
 *
 * @version $Rev$ $Date$
 */
public class OperationSelectorJMSDefaultServiceInterceptor implements Interceptor {
    
    private static final String ON_MESSAGE_METHOD_NAME = "onMessage";
    
    private Invoker next;
    private RuntimeWire runtimeWire;
    private JMSResourceFactory jmsResourceFactory;
    private JMSBinding jmsBinding;
    private JMSMessageProcessor requestMessageProcessor;
    private JMSMessageProcessor responseMessageProcessor;
    private RuntimeComponentService service;
    private List<Operation> serviceOperations;
    

    public OperationSelectorJMSDefaultServiceInterceptor(ExtensionPointRegistry registry, JMSBinding jmsBinding, JMSResourceFactory jmsResourceFactory,
            RuntimeWire runtimeWire) {
        super();
        this.jmsBinding = jmsBinding;
        this.runtimeWire = runtimeWire;
        this.jmsResourceFactory = jmsResourceFactory;
        this.requestMessageProcessor = JMSMessageProcessorUtil.getRequestMessageProcessor(registry, jmsBinding);
        this.responseMessageProcessor = JMSMessageProcessorUtil.getResponseMessageProcessor(registry, jmsBinding);
        this.service = (RuntimeComponentService)runtimeWire.getEndpoint().getService();
        this.serviceOperations = service.getInterfaceContract().getInterface().getOperations();
    }
    
    public Message invoke(Message msg) {
        return next.invoke(invokeRequest(msg));
    }    
    
    public Message invokeRequest(Message msg) { 
            // get the jms context
            JMSBindingContext context = msg.getBindingContext();
            javax.jms.Message jmsMsg = context.getJmsMsg();
            
            String operationName = requestMessageProcessor.getOperationName(jmsMsg);
            Operation operation = getTargetOperation(operationName, jmsMsg);
            msg.setOperation(operation);

            return msg;
    }

    protected Operation getTargetOperation(String operationName, javax.jms.Message jmsMsg) {
        Operation operation = null;

        if (serviceOperations.size() == 1) {

            // SCA JMS Binding Specification - Rule 1.5.1 line 203
            operation = serviceOperations.get(0);

        } else if (operationName != null) {

            // SCA JMS Binding Specification - Rule 1.5.1 line 205
            for (Operation op : serviceOperations) {
                if (op.getName().equals(operationName)) {
                    operation = op;
                    break;
                }
            }
        } else if (jmsBinding.getRequestWireFormat() instanceof WireFormatJMSDefault
                || jmsBinding.getRequestWireFormat() instanceof WireFormatJMSTextXML) {

            OMElement rootElement;
            String operationFromPayload;

            try {
                if (jmsMsg instanceof TextMessage) {
                    String xmlPayload = ((TextMessage) jmsMsg).getText();

                    if (xmlPayload != null) {
                        XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(new StringReader(xmlPayload));
                        StAXOMBuilder builder = new StAXOMBuilder(reader);
                        rootElement = builder.getDocumentElement();
                        operationFromPayload = rootElement.getLocalName();
                        for (Operation op : serviceOperations) {
                            if (op.getName().equals(operationFromPayload)) {
                                operation = op;
                                break;
                            }
                        }
                    }
                } else if (jmsMsg instanceof BytesMessage) {
                    long noOfBytes = ((BytesMessage) jmsMsg).getBodyLength();
                    byte[] bytes = new byte[(int) noOfBytes];
                    ((BytesMessage) jmsMsg).readBytes(bytes);
                    ((BytesMessage) jmsMsg).reset();

                    if (bytes != null) {
                        XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(new ByteArrayInputStream(bytes));
                        StAXOMBuilder builder = new StAXOMBuilder(reader);
                        rootElement = builder.getDocumentElement();
                        operationFromPayload = rootElement.getLocalName();
                        for (Operation op : serviceOperations) {
                            if (op.getName().equals(operationFromPayload)) {
                                operation = op;
                                break;
                            }
                        }
                    }
                }

            } catch (XMLStreamException e) {
                //let's ignore this in case the client doesn't want to use a wrapped xml message
            } catch (JMSException e) {
                throw new JMSBindingException(e);
            }

            // If operation is still null we attempt the last rule
            if (operation == null) {

                // SCA JMS Binding Specification - Rule 1.5.1 line 207
                for (Operation op : serviceOperations) {
                    if (op.getName().equals(ON_MESSAGE_METHOD_NAME)) {
                        operation = op;
                        break;
                    }
                }
            }
        }
        
        if (operation == null) {
            throw new JMSBindingException("Can't find operation " + (operationName != null ? operationName : ON_MESSAGE_METHOD_NAME));
        }

        return operation;
    }
    
    public Invoker getNext() {
        return next;
    }

    public void setNext(Invoker next) {
        this.next = next;
    }    
   
}
