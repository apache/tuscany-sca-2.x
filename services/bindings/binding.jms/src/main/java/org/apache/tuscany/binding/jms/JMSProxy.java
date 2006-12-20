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
package org.apache.tuscany.binding.jms;

import java.io.Serializable;
import java.io.StringReader;
import java.lang.reflect.Method;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.NamingException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.MessageImpl;

public class JMSProxy implements MessageListener{
	
	protected Method operationMethod;
    private JMSResourceFactory jmsResourceFactory; 
	private OperationSelector operationSelector;
	private InboundWire inboundWire;
        protected boolean xmlStyle;
    
	public JMSProxy(InboundWire inboundWire,JMSResourceFactory jmsResourceFactory,OperationSelector operationSelector, boolean xmlStyle) throws NamingException{		
		
		this.jmsResourceFactory = jmsResourceFactory;
		this.operationSelector = operationSelector;
		this.inboundWire = inboundWire;
                this.xmlStyle = xmlStyle;
	}   

	public void onMessage(Message msg){
		
		
        try{        
			        	
			String operationName = operationSelector.getOperationName(msg);
			Operation op = (Operation) inboundWire.getServiceContract().getOperations().get(operationName);
			
		    InvocationChain chain = inboundWire.getInvocationChains().get(op);
		    Interceptor headInterceptor = chain.getHeadInterceptor();		    
		    
		    org.apache.tuscany.spi.wire.Message tuscanyMsg = new MessageImpl();
		    tuscanyMsg.setBody(getPayload(msg));
		    tuscanyMsg.setTargetInvoker(chain.getTargetInvoker());
		    org.apache.tuscany.spi.wire.Message tuscanyResMsg = null;
		    
		    // again over here I expect the data binding interecptors to convert the 
		    // XML string into objects
		    if (headInterceptor != null){
		    	tuscanyResMsg = headInterceptor.invoke(tuscanyMsg);
		    }
		    	        
		    // if result is null then the method can be assumed as oneway
		    if (tuscanyResMsg != null && tuscanyResMsg.getBody() != null){
		    	sendReply(msg,operationName, fromPayload(tuscanyResMsg));
		    }

		} catch (JMSBindingException e) {
			throw new JMSBindingRuntimeException(e);
		} catch (JMSException e) {
			throw new JMSBindingRuntimeException(e);
		} catch (NamingException e) {
			throw new JMSBindingRuntimeException(e);
                } catch (Exception e) {
                    // TODO: need to not swallow these exceptions
                    e.printStackTrace();
                    throw new JMSBindingRuntimeException(e);
                }
                
	}

    private Object fromPayload(org.apache.tuscany.spi.wire.Message tuscanyResMsg) {
        if (xmlStyle) {
            OMElement omElement = (OMElement) tuscanyResMsg.getBody();
            omElement.build();
            return (String)omElement.toString();
        } else {
            Object o = tuscanyResMsg.getBody();
            return o;
        }
    }

    private Object[] getPayload(Message msg) throws JMSException {
        if (xmlStyle) {
            try {
                String xml = ((TextMessage)msg).getText();

                XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(new StringReader(xml));
                StAXOMBuilder builder = new StAXOMBuilder(reader);
                OMElement omElement = builder.getDocumentElement();

                return new Object[] {omElement};

            } catch (XMLStreamException e) {
                throw new RuntimeException(e);
            }
        } else {
            Object o = ((ObjectMessage)msg).getObject();
            return new Object[] {o};
        }
    }

	private void sendReply(Message reqMsg,String operationName,Object payload) throws JMSException, NamingException{
        
		Session session = jmsResourceFactory.createSession();

        Message message;
        if (xmlStyle) {
            message = jmsResourceFactory.createMessage(session);
            ((TextMessage)message).setText((String)payload);
        } else {
            message = jmsResourceFactory.createObjectMessage(session);
            ((ObjectMessage)message).setObject((Serializable)payload);
        }
    	
    	Destination destination = reqMsg.getJMSReplyTo();
        
        MessageProducer producer = session.createProducer(destination);
        producer.send(message);
        producer.close();
        session.close();
	}
}
