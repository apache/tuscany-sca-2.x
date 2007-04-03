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

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.naming.NamingException;

import junit.framework.TestCase;

import org.easymock.EasyMock;

public class JMSProxyTestCase extends TestCase {

    /**
     * Tests the jms response msg has correlation id set to the request msg id
     */
    public void testOnMessageRequestMsgIDToCorrelID() throws NamingException, JMSException {

        String id = "123";

        MessageProducer producer = EasyMock.createNiceMock(MessageProducer.class);

        Session session = EasyMock.createNiceMock(Session.class);
        EasyMock.expect(session.createProducer(EasyMock.isA(Destination.class))).andReturn(producer);
        EasyMock.replay(session);

        ObjectMessage responseJMSMsg = EasyMock.createMock(ObjectMessage.class);
        responseJMSMsg.setJMSDeliveryMode(1);
        responseJMSMsg.setJMSPriority(1);
        responseJMSMsg.setJMSCorrelationID(id);
        EasyMock.replay(responseJMSMsg);

        JMSResourceFactory jmsResourceFactory = EasyMock.createMock(JMSResourceFactory.class);
        EasyMock.expect(jmsResourceFactory.createSession()).andReturn(session);

        OperationAndDataBinding odb = EasyMock.createMock(OperationAndDataBinding.class);
        EasyMock.expect(odb.createJMSMessage(EasyMock.eq(session), EasyMock.isA(Exception.class)))
            .andReturn(responseJMSMsg);
        EasyMock.replay(odb);

        JMSProxy jmsProxy = new JMSProxy(null, jmsResourceFactory, null, odb, null);

        Message requestJMSMsg = EasyMock.createMock(Message.class);
        EasyMock.expect(requestJMSMsg.getJMSReplyTo()).andReturn(new Destination() {
        });

        EasyMock.expect(requestJMSMsg.getJMSDeliveryMode()).andReturn(1);
        EasyMock.expect(requestJMSMsg.getJMSPriority()).andReturn(1);

        EasyMock.expect(requestJMSMsg.getJMSReplyTo()).andReturn(new Destination() {
        });
        EasyMock.expect(requestJMSMsg.getJMSMessageID()).andReturn(id);

        producer.send(EasyMock.isA(Message.class));
        EasyMock.replay(producer);

        EasyMock.replay(requestJMSMsg);
        EasyMock.replay(jmsResourceFactory);

        jmsProxy.onMessage(requestJMSMsg);

        EasyMock.verify(requestJMSMsg);
        EasyMock.verify(jmsResourceFactory);
        EasyMock.verify(producer);
        EasyMock.verify(session);
    }

}
