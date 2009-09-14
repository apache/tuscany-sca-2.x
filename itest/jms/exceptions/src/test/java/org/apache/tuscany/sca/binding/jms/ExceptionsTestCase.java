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

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
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
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * This shows how to test the JMS binding using a simple HelloWorld application.
 */
public class ExceptionsTestCase {

    private static SCADomain scaDomain;
    private Session session;
    private InitialContext context;
    private Connection connection;

    private static final String CHECKED_XML =
        "<ns2:CheckedExcpetion xmlns:ns2=\"http://jms.binding.sca.tuscany.apache.org/\"><message>foo</message></ns2:CheckedExcpetion>";
    private static final String CHECKED_NOARGS_XML = "<ns2:CheckedExcpetionNoArgs xmlns:ns2=\"http://jms.binding.sca.tuscany.apache.org/\" />";
    private static final String CHECKED_2ARGS_XML =
        "<ns2:CheckedExcpetion2Args xmlns:ns2=\"http://jms.binding.sca.tuscany.apache.org/\"><message>foo</message></ns2:CheckedExcpetion2Args>";
    private static final String CHECKED_CHAINED_XML =
        "<ns2:CheckedExcpetionChained xmlns:ns2=\"http://jms.binding.sca.tuscany.apache.org/\"><message>java.lang.Exception: bla</message></ns2:CheckedExcpetionChained>";

    @Before
    public void init() {
        scaDomain = SCADomain.newInstance("http://localhost", "/", "exceptions/service.composite");
    }

    @Test
    public void testTextChecked() throws NamingException, JMSException, SAXException, IOException {
        sendJMSTextRequest("throwChecked");
        Message m = receiveJMSResponse();
        assertXMLEqual(CHECKED_XML, ((TextMessage)m).getText());
    }

    @Test
    public void testTextCheckedNoArgs() throws NamingException, JMSException, SAXException, IOException {
        sendJMSTextRequest("throwCheckedNoArgs");
        Message m = receiveJMSResponse();
        assertXMLEqual(CHECKED_NOARGS_XML, ((TextMessage)m).getText());
    }

    @Test
    public void testTextChecked2Args() throws NamingException, JMSException, SAXException, IOException {
        sendJMSTextRequest("throwChecked2Args");
        Message m = receiveJMSResponse();
        assertXMLEqual(CHECKED_2ARGS_XML, ((TextMessage)m).getText());
    }

    @Test
    public void testTextCheckedChained() throws NamingException, JMSException, SAXException, IOException {
        sendJMSTextRequest("throwCheckedChained");
        Message m = receiveJMSResponse();
        assertXMLEqual(CHECKED_CHAINED_XML, ((TextMessage)m).getText());
    }

    @Test
    public void testTextUnChecked() throws NamingException, JMSException, SAXException, IOException {
        sendJMSTextRequest("throwUnChecked");
        Message m = receiveJMSResponse();
        
        // FIXME: what should the response message be for unchecked exceptions with wireFormat.textXML???
        Object o = ((ObjectMessage)m).getObject();
        assertTrue(o instanceof RuntimeException);
        assertTrue("java.lang.RuntimeException".equals(o.getClass().getName()));
        assertEquals("java.lang.RuntimeException: bla", ((RuntimeException)o).getMessage());
    }

    @Test
    public void testObjectChecked() throws NamingException, JMSException {
        sendJMSObjectRequest("throwChecked");
        Message m = receiveJMSResponse();
        Object o = ((ObjectMessage)m).getObject();
        assertTrue(o instanceof CheckedExcpetion);
        assertEquals("foo", ((CheckedExcpetion)o).getMessage());
    }

    @Test
    public void testObjectCheckedNoArgs() throws NamingException, JMSException {
        sendJMSObjectRequest("throwCheckedNoArgs");
        Message m = receiveJMSResponse();
        Object o = ((ObjectMessage)m).getObject();
        assertTrue(o instanceof CheckedExcpetionNoArgs);
    }

    @Test
    public void testObjectChecked2Args() throws NamingException, JMSException {
        sendJMSObjectRequest("throwChecked2Args");
        Message m = receiveJMSResponse();
        Object o = ((ObjectMessage)m).getObject();
        assertTrue(o instanceof CheckedExcpetion2Args);
        assertEquals("foo", ((CheckedExcpetion2Args)o).getMessage());
        assertEquals("bla", ((CheckedExcpetion2Args)o).getCause().getMessage());
    }

    @Test
    public void testObjectCheckedChained() throws NamingException, JMSException {
        sendJMSObjectRequest("throwCheckedChained");
        Message m = receiveJMSResponse();
        Object o = ((ObjectMessage)m).getObject();
        assertTrue(o instanceof CheckedExcpetionChained);
        assertEquals("bla", ((CheckedExcpetionChained)o).getCause().getMessage());
    }

    @Test
    public void testObjectUnChecked() throws NamingException, JMSException {
        sendJMSObjectRequest("throwUnChecked");
        Message m = receiveJMSResponse();
        Object o = ((ObjectMessage)m).getObject();
        assertTrue(o instanceof RuntimeException);
        assertTrue("java.lang.RuntimeException".equals(o.getClass().getName()));
        assertEquals("java.lang.RuntimeException: bla", ((RuntimeException)o).getMessage());
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

    private void sendJMSTextRequest(String operationName) throws NamingException, JMSException {
        context = new InitialContext(new Properties());
        ConnectionFactory cf = (ConnectionFactory)context.lookup("ConnectionFactory");
        connection = cf.createConnection();
        connection.start();

        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination toDest = (Destination)context.lookup("TextExceptionService");

        TextMessage m = session.createTextMessage();
        m.setStringProperty("scaOperationName", operationName);
        m.setJMSReplyTo((Destination)context.lookup("ResponseQueue"));
        m.setText("<_ns_:" + operationName + " xmlns:_ns_=\"http://jms.binding.sca.tuscany.apache.org/\" />");

        MessageProducer producer = session.createProducer(toDest);
        producer.send(m);
    }

    private void sendJMSObjectRequest(String operationName) throws NamingException, JMSException {
        context = new InitialContext(new Properties());
        ConnectionFactory cf = (ConnectionFactory)context.lookup("ConnectionFactory");
        connection = cf.createConnection();
        connection.start();

        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination toDest = (Destination)context.lookup("ObjectExceptionService");

        ObjectMessage m = session.createObjectMessage();
        m.setStringProperty("scaOperationName", operationName);
        m.setJMSReplyTo((Destination)context.lookup("ResponseQueue"));
        m.setObject(new Object[0]);

        MessageProducer producer = session.createProducer(toDest);
        producer.send(m);
    }

    private Message receiveJMSResponse() throws NamingException, JMSException {
        Destination replyDest = (Destination)context.lookup("ResponseQueue");
        MessageConsumer consumer = session.createConsumer(replyDest);
        Message m = consumer.receive(5000);
        if (m == null) {
            throw new RuntimeException("No reply message received");
        }
        return m;
    }
}
