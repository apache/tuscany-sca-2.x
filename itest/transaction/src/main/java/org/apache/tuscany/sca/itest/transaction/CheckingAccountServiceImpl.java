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

package org.apache.tuscany.sca.itest.transaction;

import java.io.File;
import java.util.Enumeration;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.broker.BrokerService;
import org.osoa.sca.ServiceRuntimeException;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

/**
 * @version $Rev$ $Date$
 */
@Service(AccountService.class)
@Scope("COMPOSITE")
public class CheckingAccountServiceImpl extends AccountServiceImpl {
    private static final String url = "tcp://localhost:61616";
    private BrokerService broker;
    private Queue queue;

    @Init
    public void init() throws Exception {
        broker = new BrokerService();
        broker.setBrokerName("localhost");
        broker.setPersistent(false);
        broker.addConnector(url);
        broker.start();

        ActiveMQConnectionFactory connFac = new ActiveMQConnectionFactory(url);
        Connection conn = connFac.createConnection();
        ActiveMQSession session = (ActiveMQSession)conn.createSession(true, Session.AUTO_ACKNOWLEDGE);
        queue = session.createQueue("CheckAccounts");
        MessageProducer producer = session.createProducer(queue);
        MapMessage map = session.createMapMessage();

        for (int i = 0; i < 3; i++) {
            String accountNumber = "C00" + (i + 1);
            float balance = (float)(1000.0 + Math.random() * 200.0);
            map.setStringProperty("accountNumber", accountNumber);
            map.setFloatProperty("balance", balance);

            map.setString("accountNumber", accountNumber);
            map.setFloat("balance", balance);

            producer.send(map);
        }
        session.commit();
        conn.close();
    }

    @Destroy
    public void destroy() throws Exception {
        if (broker != null) {
            broker.stop();
        }
    }

    @Override
    protected float load(String accountNumber) throws AccountNotFoundException {
        try {
            ActiveMQConnectionFactory connFac = new ActiveMQConnectionFactory(url);
            Connection conn = connFac.createConnection();
            conn.start();
            Session session = conn.createSession(true, Session.AUTO_ACKNOWLEDGE);
            QueueBrowser browser = session.createBrowser(queue, "accountNumber = '" + accountNumber + "'");
            Enumeration msgs = browser.getEnumeration();
            if (msgs.hasMoreElements()) {
                MapMessage msg = (MapMessage)msgs.nextElement();
                float balance = msg.getFloat("balance");
                conn.close();
                return balance;
            } else {
                conn.close();
                throw new AccountNotFoundException(accountNumber);
            }
        } catch (JMSException e) {
            throw new ServiceRuntimeException(e);
        }
    }

    @Override
    protected void save(String accountNumber, float balance) throws AccountNotFoundException {
        try {
            ActiveMQConnectionFactory connFac = new ActiveMQConnectionFactory(url);
            Connection conn = connFac.createConnection();
            conn.start();
            ActiveMQSession session = (ActiveMQSession)conn.createSession(true, Session.AUTO_ACKNOWLEDGE);
            MessageConsumer consumer = session.createConsumer(queue, "accountNumber = '" + accountNumber + "'");
            Message msg = consumer.receive(1000);
            if (msg == null) {
                conn.close();
                throw new AccountNotFoundException(accountNumber);
            }
            MapMessage map = session.createMapMessage();
            map.setStringProperty("accountNumber", accountNumber);
            map.setFloatProperty("balance", balance);

            map.setString("accountNumber", accountNumber);
            map.setFloat("balance", balance);

            MessageProducer producer = session.createProducer(queue);
            producer.send(map);
            conn.close();

        } catch (JMSException e) {
            throw new ServiceRuntimeException(e);
        }
    }

}
