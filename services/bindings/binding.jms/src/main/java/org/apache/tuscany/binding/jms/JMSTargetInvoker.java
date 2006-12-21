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

import java.lang.reflect.InvocationTargetException;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.NamingException;

import org.apache.tuscany.spi.extension.TargetInvokerExtension;

/**
 * Invoke a JMS reference.
 * 
 * @version $Rev: 449970 $ $Date: 2006-09-26 06:05:35 -0400 (Tue, 26 Sep 2006) $
 */
public class JMSTargetInvoker extends TargetInvokerExtension {
    private JMSBinding jmsBinding;
    private String operationName;
    private JMSResourceFactory jmsResourceFactory;
    private OperationSelector operationSelector;
    protected Destination requestDest;
    protected Destination replyDest;

    public JMSTargetInvoker(JMSResourceFactory jmsResourceFactory,
                            JMSBinding jmsBinding,
                            String operationName,
                            OperationSelector operationSelector,
                            Destination requestDest,
                            Destination replyDest) {
        super(null, null, null);
        this.jmsBinding = jmsBinding;
        this.jmsResourceFactory = jmsResourceFactory;
        this.operationName = operationName;
        this.operationSelector = operationSelector;
        this.requestDest = requestDest;
        this.replyDest = replyDest;
    }

    public Object invokeTarget(Object payload, final short sequence) throws InvocationTargetException {
        try {

            return sendReceiveMessage((Object[])payload);

        } catch (Exception e) { // catch JMS specific error
            e.printStackTrace();
            throw new AssertionError(e);
        }

    }

    private Object sendReceiveMessage(Object[] payload) throws JMSException, NamingException, JMSBindingException {

        Session session = jmsResourceFactory.createSession();

        javax.jms.Message message = jmsResourceFactory.createMessage(session, payload);
        operationSelector.setOperationName(operationName, message);

        if (jmsBinding.getResponseDestinationName() == null) {
            replyDest = session.createTemporaryQueue();
        }

        message.setJMSReplyTo(replyDest);

        MessageProducer producer = session.createProducer(requestDest);

        producer.send(message);
        producer.close();

        String msgSelector = "JMSCorrelationID = '" + message.getJMSMessageID() + "'";
        
        MessageConsumer consumer = session.createConsumer(replyDest, msgSelector);
        jmsResourceFactory.startConnection();
        javax.jms.Message reply = consumer.receive(jmsBinding.getTimeToLive());
        consumer.close();
        session.close();

        return jmsResourceFactory.getMessagePayload(reply);
    }

}
