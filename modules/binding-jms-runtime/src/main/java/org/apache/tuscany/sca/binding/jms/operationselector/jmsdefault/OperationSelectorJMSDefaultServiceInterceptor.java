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
package org.apache.tuscany.sca.binding.jms.operationselector.jmsdefault;

import java.util.List;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.tuscany.sca.binding.jms.impl.JMSBinding;
import org.apache.tuscany.sca.binding.jms.impl.JMSBindingConstants;
import org.apache.tuscany.sca.binding.jms.impl.JMSBindingException;
import org.apache.tuscany.sca.binding.jms.provider.JMSMessageProcessor;
import org.apache.tuscany.sca.binding.jms.provider.JMSMessageProcessorUtil;
import org.apache.tuscany.sca.binding.jms.provider.JMSResourceFactory;
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
    

    public OperationSelectorJMSDefaultServiceInterceptor(JMSBinding jmsBinding, JMSResourceFactory jmsResourceFactory, RuntimeWire runtimeWire) {
        super();
        this.jmsBinding = jmsBinding;
        this.runtimeWire = runtimeWire;
        this.jmsResourceFactory = jmsResourceFactory;
        this.requestMessageProcessor = JMSMessageProcessorUtil.getRequestMessageProcessor(jmsBinding);
        this.responseMessageProcessor = JMSMessageProcessorUtil.getResponseMessageProcessor(jmsBinding);
        this.service = (RuntimeComponentService)runtimeWire.getTarget().getContract();
        this.serviceOperations = service.getInterfaceContract().getInterface().getOperations();
    }
    
    public Message invoke(Message msg) {
        return invokeResponse(next.invoke(invokeRequest(msg)));
    }    
    
    public Message invokeRequest(Message msg) { 
        // get the jms message
        javax.jms.Message jmsMsg = (javax.jms.Message)msg.getHeaders().get(JMSBindingConstants.MSG_CTXT_JMSREQUESTMSG_POSITION);
        
        String operationName = requestMessageProcessor.getOperationName(jmsMsg);
        Operation operation = getTargetOperation(operationName);
        msg.setOperation(operation);
        
        return msg;
    } 
    
    public Message invokeResponse(Message msg) { 
        try {
            // get the jms message
            javax.jms.Message requestJMSMsg = (javax.jms.Message)msg.getHeaders().get(JMSBindingConstants.MSG_CTXT_JMSREQUESTMSG_POSITION);
            
            // get the jms session
            Session session = (Session)msg.getHeaders().get(JMSBindingConstants.MSG_CTXT_JMSSESSION_POSITION);
            
            Destination destination = requestJMSMsg.getJMSReplyTo();
            MessageProducer producer = session.createProducer(destination);
    
            producer.send((javax.jms.Message)msg.getBody());
    
            producer.close();
            session.close();
            
            return msg;
    
        } catch (JMSException e) {
            throw new JMSBindingException(e);
        } 
    }    
    
    protected Operation getTargetOperation(String operationName) {
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

        } else {

            // SCA JMS Binding Specification - Rule 1.5.1 line 207
            for (Operation op : serviceOperations) {
                if (op.getName().equals(ON_MESSAGE_METHOD_NAME)) {
                    operation = op;
                    break;
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
