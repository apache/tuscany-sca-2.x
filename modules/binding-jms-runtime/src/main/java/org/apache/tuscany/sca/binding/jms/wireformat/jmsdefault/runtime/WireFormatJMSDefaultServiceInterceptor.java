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
package org.apache.tuscany.sca.binding.jms.wireformat.jmsdefault.runtime;

import java.util.HashMap;
import java.util.List;

import javax.jms.BytesMessage;
import javax.jms.Session;

import org.apache.axiom.om.OMElement;
import org.apache.tuscany.sca.binding.jms.JMSBinding;
import org.apache.tuscany.sca.binding.jms.context.JMSBindingContext;
import org.apache.tuscany.sca.binding.jms.provider.DefaultMessageProcessor;
import org.apache.tuscany.sca.binding.jms.provider.JMSResourceFactory;
import org.apache.tuscany.sca.binding.jms.wireformat.WireFormatJMSDefault;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.RuntimeWire;

/**
 * Policy handler to handle PolicySet related to Logging with the QName {http://tuscany.apache.org/xmlns/sca/1.0/impl/java}LoggingPolicy
 * 
 * @version $Rev$ $Date$
 */
public class WireFormatJMSDefaultServiceInterceptor implements Interceptor {
    private Invoker next;
    private RuntimeWire runtimeWire;
    private JMSResourceFactory jmsResourceFactory;
    private JMSBinding jmsBinding;
    private DefaultMessageProcessor requestMessageProcessor;
    private DefaultMessageProcessor responseMessageProcessor;
    private HashMap<String,OMElement> inputWrapperMap;
    private HashMap<String, Boolean> outputWrapperMap;

    public WireFormatJMSDefaultServiceInterceptor(ExtensionPointRegistry registry, JMSBinding jmsBinding, JMSResourceFactory jmsResourceFactory, RuntimeWire runtimeWire, HashMap<String, OMElement> inputWrapperMap,
            HashMap<String, Boolean> outputWrapperMap) {
        super();
        this.jmsBinding = jmsBinding;
        this.runtimeWire = runtimeWire;
        this.jmsResourceFactory = jmsResourceFactory;
        // Note the default processor doesn't follow the normal processor pattern 
        // as it has to handle both text and bytes messages
        this.requestMessageProcessor = new DefaultMessageProcessor(jmsBinding, registry);
        this.responseMessageProcessor = new DefaultMessageProcessor(jmsBinding, registry);
        this.inputWrapperMap = inputWrapperMap;
        this.outputWrapperMap = outputWrapperMap;
        
    }

    public Message invoke(Message msg) {
        
        if (jmsBinding.getRequestWireFormat() instanceof WireFormatJMSDefault) {
            msg = invokeRequest(msg);
        }

        msg = getNext().invoke(msg);

        // if it's oneway return back
        Operation operation = msg.getOperation();
        if (operation != null && operation.isNonBlocking()) {
            return msg;
        }

        if (jmsBinding.getResponseWireFormat() instanceof WireFormatJMSDefault) {
            msg = invokeResponse(msg);
        }

        return msg;
    }

    public Message invokeRequest(Message msg) {
        // get the jms context
        JMSBindingContext context = msg.getBindingContext();
        javax.jms.Message jmsMsg = context.getJmsMsg();

        Operation op = msg.getOperation();
        List<DataType> inputDataTypes = op.getInputType().getLogical();

        Class<?> inputType = null;
        if (inputDataTypes.size() == 1) {
            inputType = inputDataTypes.get(0).getPhysical();
        }
        if (inputType != null && javax.jms.Message.class.isAssignableFrom(inputType)) {
            msg.setBody(new Object[] { jmsMsg });
            
            if (jmsMsg instanceof BytesMessage) {
                context.setUseBytesForWFJMSDefaultResponse(true);
            } else {
                context.setUseBytesForWFJMSDefaultResponse(false);
            }
        } else {

            // If there is only one arg we must add a wrapper if the operation is wrapper style
            OMElement wrapper = this.inputWrapperMap.get(msg.getOperation().getName());

            Object requestPayload;
            if (jmsMsg instanceof BytesMessage) {
                requestPayload = responseMessageProcessor.extractPayloadFromJMSBytesMessage(jmsMsg, wrapper);
                context.setUseBytesForWFJMSDefaultResponse(true);
            } else {
                requestPayload = responseMessageProcessor.extractPayloadFromJMSTextMessage(jmsMsg, wrapper );
                context.setUseBytesForWFJMSDefaultResponse(false);
            }

            msg.setBody(new Object[] { requestPayload });
        }

        return msg;

    }

    public Message invokeResponse(Message msg) {

        // get the jms context
        JMSBindingContext context = msg.getBindingContext();
        Session session = context.getJmsResponseSession();

        javax.jms.Message responseJMSMsg;
        
        boolean respondBytesMessage = context.isUseBytesForWFJMSDefaultResponse();
        
        if (msg.isFault()) {
            if (respondBytesMessage == true) {
                responseJMSMsg = requestMessageProcessor.createFaultJMSBytesMessage(session, (Throwable) msg.getBody());
            } else {
                responseJMSMsg = responseMessageProcessor.createFaultJMSTextMessage(session, (Throwable) msg.getBody());
            }
        } else {
            boolean unwrap = false;
            
            if (this.outputWrapperMap.get(msg.getOperation().getName()) != null){
                unwrap = this.outputWrapperMap.get(msg.getOperation().getName());
            }
            
            if (respondBytesMessage == true) {
                responseJMSMsg = requestMessageProcessor.insertPayloadIntoJMSBytesMessage(session, msg.getBody(), unwrap);
            } else {
                responseJMSMsg = requestMessageProcessor.insertPayloadIntoJMSTextMessage(session, msg.getBody(), unwrap);
            }
        }

        msg.setBody(responseJMSMsg);

        return msg;
    }

    public Invoker getNext() {
        return next;
    }

    public void setNext(Invoker next) {
        this.next = next;
    }
}
