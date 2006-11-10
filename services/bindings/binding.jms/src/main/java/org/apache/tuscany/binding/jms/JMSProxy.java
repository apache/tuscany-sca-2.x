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

import java.lang.reflect.Method;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.NamingException;

import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.MessageImpl;

public class JMSProxy implements MessageListener{
	
	protected Method operationMethod;
    private JMSResourceFactory jmsResourceFactory; 
	private OperationSelector operationSelector;
	private InboundWire inboundWire;;
    
	public JMSProxy(InboundWire inboundWire,JMSResourceFactory jmsResourceFactory,OperationSelector operationSelector) throws NamingException{		
		
		this.jmsResourceFactory = jmsResourceFactory;
		this.operationSelector = operationSelector;
		this.inboundWire = inboundWire;
	}   

	public void onMessage(Message msg){
		
		
        try{        
			        	
			String operationName = operationSelector.getOperationName(msg);
			Operation op = (Operation) inboundWire.getServiceContract().getOperations().get(operationName);
			
		    InvocationChain chain = inboundWire.getInvocationChains().get(op);
		    Interceptor headInterceptor = chain.getHeadInterceptor();		    
		    
		    org.apache.tuscany.spi.wire.Message tuscanyMsg = new MessageImpl();
		    tuscanyMsg.setBody(new Object[]{((TextMessage)msg).getText()});
		    tuscanyMsg.setTargetInvoker(chain.getTargetInvoker());
		    org.apache.tuscany.spi.wire.Message tuscanyResMsg = null;
		    
		    // again over here I expect the data binding interecptors to convert the 
		    // XML string into objects
		    if (headInterceptor != null){
		    	tuscanyResMsg = headInterceptor.invoke(tuscanyMsg);
		    }
		    	        
		    // if result is null then the method can be assumed as oneway
		    if (tuscanyResMsg != null && tuscanyResMsg.getBody() != null){
		    	sendReply(msg,operationName, (String)tuscanyResMsg.getBody());
		    }
		    
		} catch (JMSBindingException e) {
			throw new JMSBindingRuntimeException(e);
		} catch (JMSException e) {
			throw new JMSBindingRuntimeException(e);
		} catch (NamingException e) {
			throw new JMSBindingRuntimeException(e);
		}
	}

	private void sendReply(Message reqMsg,String operationName,String payload) throws JMSException, NamingException{
        
		Session session = jmsResourceFactory.createSession();
    	
    	javax.jms.Message message = jmsResourceFactory.createMessage(session);
    	
        ((javax.jms.TextMessage)message).setText((String)payload);
    	
    	Destination destination = reqMsg.getJMSReplyTo();
        
        MessageProducer producer = session.createProducer(destination);
        producer.send(message);
        producer.close();
        session.close();
	}
}
