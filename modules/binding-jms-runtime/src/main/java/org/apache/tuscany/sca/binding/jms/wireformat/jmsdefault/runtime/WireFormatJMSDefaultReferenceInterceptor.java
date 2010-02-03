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

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.xml.namespace.QName;

import org.apache.tuscany.sca.binding.jms.JMSBinding;
import org.apache.tuscany.sca.binding.jms.JMSBindingConstants;
import org.apache.tuscany.sca.binding.jms.JMSBindingException;
import org.apache.tuscany.sca.binding.jms.context.JMSBindingContext;
import org.apache.tuscany.sca.binding.jms.provider.DefaultMessageProcessor;
import org.apache.tuscany.sca.binding.jms.provider.JMSResourceFactory;
import org.apache.tuscany.sca.binding.jms.wireformat.WireFormatJMSDefault;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.util.FaultException;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;
import org.w3c.dom.Node;

/**
 * 
 * @version $Rev$ $Date$
 */
public class WireFormatJMSDefaultReferenceInterceptor implements Interceptor {

    private Invoker next;
    private RuntimeEndpointReference endpointReference;
    private JMSResourceFactory jmsResourceFactory;
    private JMSBinding jmsBinding;
    private DefaultMessageProcessor requestMessageProcessor;
    private DefaultMessageProcessor responseMessageProcessor;
    private HashMap<String, Boolean> inputWrapperMap;
    private HashMap<String, Node> outputWrapperMap;

    public WireFormatJMSDefaultReferenceInterceptor(ExtensionPointRegistry registry, JMSResourceFactory jmsResourceFactory, RuntimeEndpointReference endpointReference, HashMap<String, Boolean> inputWrapperMap,
            HashMap<String, Node> outputWrapperMap) {
        super();
        this.jmsBinding = (JMSBinding) endpointReference.getBinding();
        this.endpointReference = endpointReference;
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

        if (jmsBinding.getResponseWireFormat() instanceof WireFormatJMSDefault) {
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

            if (((WireFormatJMSDefault) jmsBinding.getRequestWireFormat()).isUseBytesMessage()) {
                requestMsg = requestMessageProcessor.insertPayloadIntoJMSBytesMessage(session, msg.getBody(), this.inputWrapperMap.get(msg.getOperation().getName()));
            } else {
                requestMsg = requestMessageProcessor.insertPayloadIntoJMSTextMessage(session, msg.getBody(), this.inputWrapperMap.get(msg.getOperation().getName()));
            }

            msg.setBody(requestMsg);

            requestMsg.setJMSReplyTo(context.getReplyToDestination());

            return msg;
        } catch (JMSException e) {
            throw new JMSBindingException(e);
        }
    }

    public Message invokeResponse(Message msg) {
        if (msg.getBody() != null) {
            javax.jms.Message jmsMsg = (javax.jms.Message) msg.getBody();

            Operation op = msg.getOperation();
            DataType outputDataType = op.getOutputType();

            Class<?> outputType = null;
            if (outputDataType != null) {
                outputType = outputDataType.getPhysical();
            }

            if (outputType != null && javax.jms.Message.class.isAssignableFrom(outputType)) {
                msg.setBody(jmsMsg);
            } else {

                Node wrapper = null;
                // if we have a fault no need to wrap the response
                try {
                    if (!jmsMsg.getBooleanProperty(JMSBindingConstants.FAULT_PROPERTY)) {
                        // If there is only one arg we must add a wrapper if the operation is wrapper style
                        wrapper = this.outputWrapperMap.get(msg.getOperation().getName());
                    }
                } catch (JMSException e) {
                    throw new JMSBindingException(e);
                }

                Object response;
                if (jmsMsg instanceof BytesMessage) {
                    response = responseMessageProcessor.extractPayloadFromJMSBytesMessage(jmsMsg, wrapper);
                } else {
                    response = responseMessageProcessor.extractPayloadFromJMSTextMessage(jmsMsg, wrapper);
                }

                if (response != null) {
                    msg.setBody(response);
                    try {
                        if (jmsMsg.getBooleanProperty(JMSBindingConstants.FAULT_PROPERTY)) {
                            FaultException e = new FaultException("remote exception", response);
                            Node om = ((Node)response).getFirstChild();
                            e.setFaultName(new QName(om.getNamespaceURI(), om.getLocalName()));
                            msg.setFaultBody(e);
                        }
                    } catch (JMSException e) {
                        throw new JMSBindingException(e);
                    }
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
