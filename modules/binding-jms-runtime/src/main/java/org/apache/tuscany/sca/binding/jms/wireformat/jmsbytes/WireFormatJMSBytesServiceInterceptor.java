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
package org.apache.tuscany.sca.binding.jms.wireformat.jmsbytes;

import javax.jms.JMSException;
import javax.jms.Session;

import org.apache.tuscany.sca.assembly.WireFormat;
import org.apache.tuscany.sca.binding.jms.context.JMSBindingContext;
import org.apache.tuscany.sca.binding.jms.impl.JMSBinding;
import org.apache.tuscany.sca.binding.jms.impl.JMSBindingConstants;
import org.apache.tuscany.sca.binding.jms.impl.JMSBindingException;
import org.apache.tuscany.sca.binding.jms.provider.JMSMessageProcessor;
import org.apache.tuscany.sca.binding.jms.provider.JMSMessageProcessorUtil;
import org.apache.tuscany.sca.binding.jms.provider.JMSResourceFactory;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.RuntimeWire;

/**
 * Policy handler to handle PolicySet related to Logging with the QName
 * {http://tuscany.apache.org/xmlns/sca/1.0/impl/java}LoggingPolicy
 *
 * @version $Rev$ $Date$
 */
public class WireFormatJMSBytesServiceInterceptor implements Interceptor {
    private Invoker next;
    private RuntimeWire runtimeWire;
    private JMSResourceFactory jmsResourceFactory;
    private JMSBinding jmsBinding;
    private JMSMessageProcessor requestMessageProcessor;
    private JMSMessageProcessor responseMessageProcessor;
    private WireFormat requestWireFormat;
    private WireFormat responseWireFormat;
    private String correlationScheme;

    public WireFormatJMSBytesServiceInterceptor(JMSBinding jmsBinding, JMSResourceFactory jmsResourceFactory, RuntimeWire runtimeWire) {
        super();
        this.jmsBinding = jmsBinding;
        this.runtimeWire = runtimeWire;
        this.jmsResourceFactory = jmsResourceFactory;
        
        if (jmsBinding.getRequestWireFormat() instanceof WireFormatJMSBytes){
            this.requestWireFormat = jmsBinding.getRequestWireFormat();
            this.jmsBinding.setRequestMessageProcessorName(JMSBindingConstants.BYTES_MP_CLASSNAME);
        }
        
        if (jmsBinding.getResponseWireFormat() instanceof WireFormatJMSBytes){
            this.responseWireFormat = jmsBinding.getResponseWireFormat();
            this.jmsBinding.setResponseMessageProcessorName(JMSBindingConstants.BYTES_MP_CLASSNAME);
        }
        
        this.requestMessageProcessor = JMSMessageProcessorUtil.getRequestMessageProcessor(jmsBinding);
        this.responseMessageProcessor = JMSMessageProcessorUtil.getResponseMessageProcessor(jmsBinding);
        
        this.correlationScheme = jmsBinding.getCorrelationScheme();
    }

    public Message invoke(Message msg) {
        if (requestWireFormat != null){
            msg = invokeRequest(msg);
        }
        
        msg = getNext().invoke(msg);
        
        if (responseWireFormat != null){
            msg = invokeResponse(msg);
        }
        
        return msg;
    }
    
    public Message invokeRequest(Message msg) {
        // get the jms context
        JMSBindingContext context = (JMSBindingContext)msg.getHeaders().get(JMSBindingConstants.MSG_CTXT_POSITION);
        javax.jms.Message jmsMsg = context.getJmsMsg();

        Object requestPayload = requestMessageProcessor.extractPayloadFromJMSMessage(jmsMsg);
        msg.setBody(requestPayload);
                 
        return msg;
    }
    
    public Message invokeResponse(Message msg) {
        try {
            // get the jms context
            JMSBindingContext context = (JMSBindingContext)msg.getHeaders().get(JMSBindingConstants.MSG_CTXT_POSITION);
            javax.jms.Message requestJMSMsg = context.getJmsMsg();
            Session session = context.getJmsSession();

            javax.jms.Message responseJMSMsg;
            if (msg.isFault()) {
                responseJMSMsg = responseMessageProcessor.createFaultMessage(session, (Throwable)msg.getBody());
            } else {
                responseJMSMsg = responseMessageProcessor.insertPayloadIntoJMSMessage(session, msg.getBody());
            }
    
            responseJMSMsg.setJMSDeliveryMode(requestJMSMsg.getJMSDeliveryMode());
            responseJMSMsg.setJMSPriority(requestJMSMsg.getJMSPriority());
    
            if (correlationScheme == null || JMSBindingConstants.CORRELATE_MSG_ID.equalsIgnoreCase(correlationScheme)) {
                responseJMSMsg.setJMSCorrelationID(requestJMSMsg.getJMSMessageID());
            } else if (JMSBindingConstants.CORRELATE_CORRELATION_ID.equalsIgnoreCase(correlationScheme)) {
                responseJMSMsg.setJMSCorrelationID(requestJMSMsg.getJMSCorrelationID());
            }    
        
            msg.setBody(responseJMSMsg);
            
            return msg;
        } catch (JMSException e) {
            throw new JMSBindingException(e);
        } 
    }        

    public Invoker getNext() {
        return next;
    }

    public void setNext(Invoker next) {
        this.next = next;
    }
}
