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
import java.util.HashSet;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.NamingException;

import org.apache.activemq.broker.BrokerService;
import org.apache.tuscany.sca.binding.jms.provider.JMSResourceFactory;
import org.apache.tuscany.sca.binding.jms.provider.JMSResourceFactoryImpl;
import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * This shows how to test the JMS binding using a simple HelloWorld application.
 */
public class NonSCAClientTestCase {

    private static SCADomain scaDomain;

    @Before
    public void init() throws Exception {
        startBroker();
        scaDomain = SCADomain.newInstance("http://localhost", "/", "nonSCAclient/service.composite");
        OneWayServiceImpl.name = null;
    }

    @Test
    public void testXMLText() throws Exception {

        synchronized (OneWayServiceImpl.mutex) {

            sendTextMessage("dynamicQueues/OneWayService", "<ns2:sayHello xmlns:ns2=\"http://jms.binding.sca.tuscany.apache.org/\"><arg0>Petra</arg0></ns2:sayHello>");

            // wait up to 10 seconds but it will likely be a lot less
            // as the service invocation will wake this up earlier
            if (OneWayServiceImpl.name == null) {
                OneWayServiceImpl.mutex.wait(10000);
            }
        }
        assertEquals("Petra", OneWayServiceImpl.name);
    }

    @Test
    public void testText() throws Exception {

        synchronized (OneWayServiceImpl.mutex) {

            sendTextMessage("dynamicQueues/TextDest", "Petra");

            // wait up to 10 seconds but it will likely be a lot less
            // as the service invocation will wake this up earlier
            OneWayServiceImpl.mutex.wait(10000);
        }
        assertEquals("Petra", OneWayServiceImpl.name);
    }

    @Test
    public void testObject() throws Exception {

        Set<Integer> arg = new HashSet<Integer>();
        arg.add(new Integer(3));

        synchronized (OneWayObjectServiceImpl.mutex) {

            sendObjectMessage("dynamicQueues/ObjectDest", arg);

            // wait up to 10 seconds but it will likely be a lot less
            // as the service invocation will wake this up earlier
            OneWayObjectServiceImpl.mutex.wait(10000);
        }
        assertEquals(arg, OneWayObjectServiceImpl.name);
    }

    @After
    public void end() throws Exception {
        if (scaDomain != null) {
            scaDomain.close();
        }
        stopBroker();
    }

    private BrokerService broker;
    protected void startBroker() throws Exception {
        broker = new BrokerService();
        broker.setPersistent(false);
        broker.setUseJmx(false);
        broker.addConnector("tcp://localhost:61623");
        broker.start();
    }
    protected void stopBroker() throws Exception {
        if (broker != null) {
            broker.stop();
        }
    }

    protected static void sendTextMessage(String destName, String payload) throws JMSException, NamingException {
        JMSResourceFactory rf = new JMSResourceFactoryImpl(null, null, null, "tcp://localhost:61623");
        Session session = rf.getConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);
        MessageProducer p = session.createProducer(rf.lookupDestination(destName));
        rf.getConnection().start();
        session.run();
        p.send(session.createTextMessage(payload));
        rf.closeConnection();
    }

    protected static void sendObjectMessage(String destName, Object payload) throws JMSException, NamingException {
        JMSResourceFactory rf = new JMSResourceFactoryImpl(null, null, null, "tcp://localhost:61623");
        Session session = rf.getConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);
        MessageProducer p = session.createProducer(rf.lookupDestination(destName));
        rf.getConnection().start();
        session.run();
        p.send(session.createObjectMessage((Serializable)payload));
        rf.closeConnection();
    }
}
