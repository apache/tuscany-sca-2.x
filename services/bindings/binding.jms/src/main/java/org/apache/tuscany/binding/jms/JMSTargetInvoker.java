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
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.NamingException;

import org.apache.tuscany.spi.extension.TargetInvokerExtension;

/**
 * Invoke a JMS reference.
 */
public class JMSTargetInvoker extends TargetInvokerExtension {

    protected JMSBindingDefinition jmsBinding;
    protected JMSResourceFactory jmsResourceFactory;

    protected String operationName;
    protected OperationAndDataBinding requestOperationAndDataBinding;
    protected OperationAndDataBinding responseOperationAndDataBinding;

    protected Destination requestDest;
    protected Destination replyDest;

    public JMSTargetInvoker(JMSResourceFactory jmsResourceFactory,
                            JMSBindingDefinition jmsBinding,
                            String operationName,
                            OperationAndDataBinding requestOperationAndDataBinding,
                            OperationAndDataBinding responseOperationAndDataBinding,
                            Destination requestDest,
                            Destination replyDest) {
        super(null, null, null);
        this.jmsBinding = jmsBinding;
        this.jmsResourceFactory = jmsResourceFactory;
        this.operationName = operationName;
        this.requestOperationAndDataBinding = requestOperationAndDataBinding;
        this.responseOperationAndDataBinding = responseOperationAndDataBinding;
        this.requestDest = requestDest;
        this.replyDest = replyDest;
    }

    public Object invokeTarget(Object payload, final short sequence) throws InvocationTargetException {
        try {
            Session session = jmsResourceFactory.createSession();
            try {

                Destination replyToDest = (replyDest != null) ? replyDest : session.createTemporaryQueue();
                Message requestMsg = sendRequest((Object[])payload, session, replyToDest);
                Message replyMsg = receiveReply(session, replyToDest, requestMsg.getJMSMessageID());

                return responseOperationAndDataBinding.extractPayload(replyMsg);

            } finally {
                session.close();
            }
        } catch (JMSException e) {
            throw new InvocationTargetException(e);
        } catch (NamingException e) {
            throw new InvocationTargetException(e);
        }
    }

    protected Message sendRequest(Object[] payload, Session session, Destination replyToDest) throws JMSException {

        Message requestMsg = requestOperationAndDataBinding.createJMSMessage(session, payload);

        requestMsg.setJMSDeliveryMode(jmsBinding.getDeliveryMode());
        requestMsg.setJMSPriority(jmsBinding.getPriority());

        requestOperationAndDataBinding.setOperationName(operationName, requestMsg);
        requestMsg.setJMSReplyTo(replyToDest);

        MessageProducer producer = session.createProducer(requestDest);
        try {
            producer.send(requestMsg);
        } finally {
            producer.close();
        }
        return requestMsg;
    }

    protected Message receiveReply(Session session, Destination replyToDest, String requestMsgId) throws JMSException,
        NamingException {
        String msgSelector = "JMSCorrelationID = '" + requestMsgId + "'";
        MessageConsumer consumer = session.createConsumer(replyToDest, msgSelector);
        Message replyMsg;
        try {
            jmsResourceFactory.startConnection();
            replyMsg = consumer.receive(jmsBinding.getTimeToLive());
        } finally {
            consumer.close();
        }
        return replyMsg;
    }

}
