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
package org.apache.tuscany.sca.binding.jms.wireformat.jmsbytesxml.runtime;

import javax.jms.JMSException;
import javax.jms.Session;

import org.apache.tuscany.sca.binding.jms.JMSBinding;
import org.apache.tuscany.sca.binding.jms.JMSBindingConstants;
import org.apache.tuscany.sca.binding.jms.JMSBindingException;
import org.apache.tuscany.sca.binding.jms.context.JMSBindingContext;
import org.apache.tuscany.sca.binding.jms.provider.JMSMessageProcessor;
import org.apache.tuscany.sca.binding.jms.provider.JMSMessageProcessorUtil;
import org.apache.tuscany.sca.binding.jms.provider.JMSResourceFactory;
import org.apache.tuscany.sca.binding.jms.provider.xml.XMLHelper;
import org.apache.tuscany.sca.binding.jms.provider.xml.XMLHelperFactory;
import org.apache.tuscany.sca.binding.jms.wireformat.WireFormatJMSBytesXML;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.invocation.InterceptorAsyncImpl;
import org.apache.tuscany.sca.interfacedef.util.FaultException;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;


public class WireFormatJMSBytesXMLReferenceInterceptor extends InterceptorAsyncImpl {

    private Invoker next;
    private RuntimeEndpointReference endpointReference;
    private JMSResourceFactory jmsResourceFactory;
    private JMSBinding jmsBinding;
    private JMSMessageProcessor requestMessageProcessor;
    private JMSMessageProcessor responseMessageProcessor;
    private XMLHelper<?> xmlhelper;

    public WireFormatJMSBytesXMLReferenceInterceptor(ExtensionPointRegistry registry, JMSResourceFactory jmsResourceFactory, RuntimeEndpointReference endpointReference) {
        super();
        this.jmsBinding = (JMSBinding) endpointReference.getBinding();
        this.endpointReference = endpointReference;
        this.jmsResourceFactory = jmsResourceFactory;
        this.requestMessageProcessor = JMSMessageProcessorUtil.getRequestMessageProcessor(registry, jmsBinding);
        this.responseMessageProcessor = JMSMessageProcessorUtil.getResponseMessageProcessor(registry, jmsBinding); 
        this.xmlhelper = XMLHelperFactory.createXMLHelper(registry);
    }

    public Message invoke(Message msg) {
        if (jmsBinding.getRequestWireFormat() instanceof WireFormatJMSBytesXML){
            msg = invokeRequest(msg);
        }
        
        msg = getNext().invoke(msg);
        
        if (jmsBinding.getResponseWireFormat() instanceof WireFormatJMSBytesXML){
            msg = invokeResponse(msg);
        }
        
        return msg;
    }
    
    public Message invokeRequest(Message msg) {
        try {
            // get the jms context
            JMSBindingContext context = msg.getBindingContext();
            Session session = context.getJmsSession();
            
            javax.jms.Message requestMsg = requestMessageProcessor.insertPayloadIntoJMSMessage(session, msg.getBody());
            
            msg.setBody(requestMsg);
            
            requestMsg.setJMSReplyTo(context.getReplyToDestination());
            
            return msg;
        } catch (JMSException e) {
            throw new JMSBindingException(e);
        } 
    }
    
    public Message invokeResponse(Message msg) {
        if (msg.getBody() != null){
            javax.jms.Message jmsMsg = (javax.jms.Message)msg.getBody();
            Object response = responseMessageProcessor.extractPayloadFromJMSMessage(jmsMsg);
            if (response != null ){
                msg.setBody(response);
                try {
                    if (jmsMsg.getBooleanProperty(JMSBindingConstants.FAULT_PROPERTY)) {
                        FaultException e = new FaultException("remote exception", response);
                        xmlhelper.setFaultName(e, response);
                        msg.setFaultBody(e);
                    }
                } catch (JMSException e) {
                    throw new JMSBindingException(e);
                }
            } else {
                msg.setBody(null);
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
    
	public Message processRequest(Message msg) {
		return invokeRequest(msg);
	} // end method processRequest

	public Message processResponse(Message msg) {
		return invokeResponse(msg);
	} // end method processResponse

}
