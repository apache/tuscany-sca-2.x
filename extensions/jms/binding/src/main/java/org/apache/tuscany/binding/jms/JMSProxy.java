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
import javax.naming.NamingException;

import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.MessageImpl;

public class JMSProxy implements MessageListener {

    protected Method operationMethod;
    protected JMSResourceFactory jmsResourceFactory;
    protected OperationAndDataBinding requestOperationAndDataBinding;
    protected OperationAndDataBinding responseOperationAndDataBinding;
    protected InboundWire inboundWire;
    protected String correlationScheme;

    public JMSProxy(InboundWire inboundWire,
                    JMSResourceFactory jmsResourceFactory,
                    OperationAndDataBinding requestOperationAndDataBinding,
                    OperationAndDataBinding responseOperationAndDataBinding,
                    String correlationScheme) throws NamingException {

        this.jmsResourceFactory = jmsResourceFactory;
        this.requestOperationAndDataBinding = requestOperationAndDataBinding;
        this.responseOperationAndDataBinding = responseOperationAndDataBinding;
        this.inboundWire = inboundWire;
        this.correlationScheme = correlationScheme;
    }

    public void onMessage(Message requestJMSMsg) {
        try {
            Object responsePayload = invokeService(requestJMSMsg);
            sendReply(requestJMSMsg, responsePayload);
        } catch (Exception e) {
            sendFaultReply(requestJMSMsg, e);
        }
    }

    protected Object invokeService(Message requestJMSMsg) throws JMSException {

        String operationName = requestOperationAndDataBinding.getOperationName(requestJMSMsg);
        Object requestPayload = requestOperationAndDataBinding.extractPayload(requestJMSMsg);

        Operation op = (Operation)inboundWire.getServiceContract().getOperations().get(operationName);
        InvocationChain chain = inboundWire.getInvocationChains().get(op);

        org.apache.tuscany.spi.wire.Message tuscanyRequestMsg = new MessageImpl();
        tuscanyRequestMsg.setTargetInvoker(chain.getTargetInvoker());
        tuscanyRequestMsg.setBody(requestPayload);

        org.apache.tuscany.spi.wire.Message tuscanyResponseMsg = null;

        Interceptor headInterceptor = chain.getHeadInterceptor();
        if (headInterceptor != null) {
            tuscanyResponseMsg = headInterceptor.invoke(tuscanyRequestMsg);
        }

        // TODO: what if headInterceptor is null?

        return tuscanyResponseMsg.getBody();
    }

    protected void sendReply(Message requestJMSMsg, Object responsePayload) {
        try {

            if (requestJMSMsg.getJMSReplyTo() == null) {
                // assume no reply is expected
                return;
            }

            Session session = jmsResourceFactory.createSession();
            Message replyJMSMsg = responseOperationAndDataBinding.createJMSMessage(session, responsePayload);

            replyJMSMsg.setJMSDeliveryMode(requestJMSMsg.getJMSDeliveryMode());
            replyJMSMsg.setJMSPriority(requestJMSMsg.getJMSPriority());

            if (correlationScheme == null || "RequestMsgIDToCorrelID".equalsIgnoreCase(correlationScheme)) {
                replyJMSMsg.setJMSCorrelationID(requestJMSMsg.getJMSMessageID());
            } else if ("RequestCorrelIDToCorrelID".equalsIgnoreCase(correlationScheme)) {
                replyJMSMsg.setJMSCorrelationID(requestJMSMsg.getJMSCorrelationID());
            }

            Destination destination = requestJMSMsg.getJMSReplyTo();
            MessageProducer producer = session.createProducer(destination);

            producer.send(replyJMSMsg);

            producer.close();
            session.close();

        } catch (JMSException e) {
            throw new JMSBindingException(e);
        } catch (NamingException e) {
            throw new JMSBindingException(e);
        }
    }

    protected void sendFaultReply(Message requestJMSMsg, Exception e) {
        sendReply(requestJMSMsg, new JMSBindingException("exception invoking JMS service", e));
    }

}
