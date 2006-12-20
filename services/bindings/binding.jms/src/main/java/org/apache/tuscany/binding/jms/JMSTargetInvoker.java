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
import java.lang.reflect.InvocationTargetException;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.naming.NamingException;

import org.apache.tuscany.spi.wire.InvocationRuntimeException;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * Invoke a JMS reference.
 *
 * @version $Rev: 449970 $ $Date: 2006-09-26 06:05:35 -0400 (Tue, 26 Sep 2006) $
 */
public class JMSTargetInvoker implements TargetInvoker {
    private JMSBinding jmsBinding;
    private String operationName;
	private JMSResourceFactory jmsResourceFactory;
    private OperationSelector operationSelector;
    protected boolean xmlStyle;
    
    public JMSTargetInvoker(JMSResourceFactory jmsResourceFactory,JMSBinding jmsBinding, String operationName, OperationSelector operationSelector, boolean xmlStyle){
        this.jmsBinding = jmsBinding;
        this.jmsResourceFactory = jmsResourceFactory;
        this.operationName = operationName;
        this.operationSelector = operationSelector;
        this.xmlStyle = xmlStyle;
    }

    /* By the time I receive the args it should have been converted to
     * XML by the data binding framework.
     */
    public Message invoke(Message msg) throws InvocationRuntimeException {
        try {
        	Object[] args = (Object[])msg.getBody();
            Object resp = invokeTarget(args[0],(short)0);
            msg.setBody(resp);
        } catch (Exception e) {
            msg.setBody(e.getCause());
        }
        return msg;
    }

    public Object invokeTarget(Object payload,final short sequence)throws InvocationTargetException {
        try {
            
        	return sendReceiveMessage(payload);
        	
        } catch (Exception e) { // catch JMS specific error
                e.printStackTrace();
        	throw new AssertionError(e);
        }

    }

    public Object clone() throws CloneNotSupportedException {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    public boolean isOptimizable() {
        return false;
    }

    public boolean isCacheable() {
        return false;
    }

    public void setCacheable(boolean cacheable) {
    }
    
    private Object sendReceiveMessage(Object payload) throws JMSException, NamingException, JMSBindingException{
    	
    	Session session = jmsResourceFactory.createSession();

        javax.jms.Message message;
        if (xmlStyle) {
            message = jmsResourceFactory.createMessage(session);
            ((javax.jms.TextMessage)message).setText((String)payload);
        } else {
            message = jmsResourceFactory.createObjectMessage(session);
            ((javax.jms.ObjectMessage)message).setObject((Serializable)payload);
        }
    	
        operationSelector.setOperationName(operationName,message);  
        
    	Destination destination = jmsResourceFactory.lookupDestination(jmsBinding.getDestinationName());
        
        MessageProducer producer = session.createProducer(destination);
        
        // create a temporary queue and listen to the response
        //Destination replyDest = session.createQueue(jmsBinding.getDestinationName()+ "-reply");
        Destination replyDest = session.createTemporaryQueue();
        message.setJMSReplyTo(replyDest);
        
        producer.send(message);
        producer.close();
        
        MessageConsumer consumer = session.createConsumer(replyDest);
        jmsResourceFactory.startConnection();
        javax.jms.Message reply = consumer.receive(jmsBinding.getTimeToLive());        
        consumer.close();        
        session.close();
        
        return reply instanceof ObjectMessage ? ((javax.jms.ObjectMessage)reply).getObject() : ((javax.jms.TextMessage)reply).getText();
    }
}
