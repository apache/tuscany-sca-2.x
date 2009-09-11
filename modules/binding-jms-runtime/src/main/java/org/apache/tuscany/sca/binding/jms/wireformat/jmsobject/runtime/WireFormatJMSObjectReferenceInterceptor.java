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
package org.apache.tuscany.sca.binding.jms.wireformat.jmsobject.runtime;


import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import javax.jms.JMSException;
import javax.jms.Session;

import org.apache.tuscany.sca.binding.jms.JMSBinding;
import org.apache.tuscany.sca.binding.jms.JMSBindingException;
import org.apache.tuscany.sca.binding.jms.context.JMSBindingContext;
import org.apache.tuscany.sca.binding.jms.provider.JMSMessageProcessor;
import org.apache.tuscany.sca.binding.jms.provider.JMSMessageProcessorUtil;
import org.apache.tuscany.sca.binding.jms.provider.JMSResourceFactory;
import org.apache.tuscany.sca.binding.jms.provider.ObjectMessageProcessor;
import org.apache.tuscany.sca.binding.jms.wireformat.WireFormatJMSObject;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.RuntimeWire;

/**
 *
 * @version $Rev$ $Date$
 */
public class WireFormatJMSObjectReferenceInterceptor implements Interceptor {

    private Invoker next;
    private RuntimeWire runtimeWire;
    private JMSResourceFactory jmsResourceFactory;
    private JMSBinding jmsBinding;
    private JMSMessageProcessor requestMessageProcessor;
    private JMSMessageProcessor responseMessageProcessor;

    private HashMap<String, String> singleArgMap;

    public WireFormatJMSObjectReferenceInterceptor(JMSBinding jmsBinding, JMSResourceFactory jmsResourceFactory, RuntimeWire runtimeWire, HashMap<String, String> hashMap) {
        super();
        this.jmsBinding = jmsBinding;
        this.runtimeWire = runtimeWire;
        this.jmsResourceFactory = jmsResourceFactory;
        this.requestMessageProcessor = JMSMessageProcessorUtil.getRequestMessageProcessor(jmsBinding);
        this.responseMessageProcessor = JMSMessageProcessorUtil.getResponseMessageProcessor(jmsBinding); 
        this.singleArgMap = hashMap;
    }

    public Message invoke(Message msg) {
        if (jmsBinding.getRequestWireFormat() instanceof WireFormatJMSObject){
            msg = invokeRequest(msg);
        }
        
        msg = getNext().invoke(msg);
        
        if (jmsBinding.getResponseWireFormat() instanceof WireFormatJMSObject){
            msg = invokeResponse(msg);
        }
        
        return msg;
    }
    
    public Message invokeRequest(Message msg) {
        try {
            // get the jms context
            JMSBindingContext context = msg.getBindingContext();
            Session session = context.getJmsSession();

            javax.jms.Message requestMsg;
                        
            // Tuscany automatically wraps operation arguments in an array before we
            // get to this point so here we need to decide how they are going to appear 
            // on the wire.
            //
            // If the operation has a single parameter and the user has set @wrapSingle=false
            // then
            //    send the single parameter out onto the wire unwrapped
            // else 
            //    send out the message as is
            //
            if (singleArgMap.get(msg.getOperation().getName()) == null) {
                requestMsg = requestMessageProcessor.insertPayloadIntoJMSMessage(session, msg.getBody());
            } else {
                // we know that wrapSinle is set to false here as the provider only
                // populates singleArgMap if it is set false
                requestMsg = ((ObjectMessageProcessor) requestMessageProcessor).createJMSMessageForSingleParamOperation(session, msg.getBody(), false);
            }

            msg.setBody(requestMsg);
            
            requestMsg.setJMSReplyTo(context.getReplyToDestination());
            
            return msg;
        } catch (JMSException e) {
            throw new JMSBindingException(e);
        } 
    }
    
    public Message invokeResponse(Message msg) {
        if (msg.getBody() != null){
            Object response = responseMessageProcessor.extractPayloadFromJMSMessage((javax.jms.Message)msg.getBody());
            
            if (response instanceof InvocationTargetException) {
                msg.setFaultBody(((InvocationTargetException) response).getCause());
            } else {
                if (response != null){
                    msg.setBody(response);
                } else {
                    msg.setBody(null);
                }
            }
        }

        return msg;
    }     

    public Invoker getNext() {
        return next;
    }

    public void setNext(Invoker next) {
        this.next = next;
    }
}
