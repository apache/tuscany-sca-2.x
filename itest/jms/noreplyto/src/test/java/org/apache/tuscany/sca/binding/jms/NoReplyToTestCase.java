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
package org.apache.tuscany.sca.binding.jms;

import static org.junit.Assert.assertEquals;

import java.io.Serializable;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Uses a non-Tuscany JMS client to send/receive a JMS request to a Tuscany/SCA JMS service
 */
public class NoReplyToTestCase {

    private static SCADomain scaDomain;
    private Session session;
    private InitialContext context;
    private Connection connection;

    @Before
    public void init() {
        scaDomain = SCADomain.newInstance("http://localhost", "/", "service.composite");
    }

    @Test
    public void testHelloWorldCreate() throws Exception {
        sendJMSRequest();
        String response = receiveJMSResponse();
        assertEquals("jmsHello Petra", response);
    }

    private void sendJMSRequest() throws NamingException, JMSException {
        context = new InitialContext(new Properties());
        ConnectionFactory cf = (ConnectionFactory)context.lookup("ConnectionFactory");
        connection = cf.createConnection();
        connection.start();

        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination toDest = (Destination)context.lookup("RequestQueue");
        
        ObjectMessage m = session.createObjectMessage();
        m.setObject("Petra");
        
        MessageProducer producer = session.createProducer(toDest);
        producer.send(m);
    }

    private String receiveJMSResponse() throws NamingException, JMSException {
        Destination replyDest = (Destination)context.lookup("ResponseQueue");
        MessageConsumer consumer = session.createConsumer(replyDest);
        Message m = consumer.receive(5000);
        if (m == null) {
            throw new RuntimeException("No reply message received");
        }
        return ((ObjectMessage)m).getObject().toString();
    }

    @After
    public void end() throws JMSException {
        if (scaDomain != null) {
            scaDomain.close();
        }
        if (connection != null) {
            connection.close();
        }
    }
}
