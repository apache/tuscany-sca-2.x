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
package scatours.smsgateway;

import javax.jms.Connection;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;

public class SMSGatewayJMSServiceBootstrap {

    public static void main(String[] args) throws Exception {
        final BrokerService jmsBroker = new BrokerService();
        jmsBroker.setPersistent(false);
        jmsBroker.setUseJmx(false);
        jmsBroker.addConnector("tcp://localhost:61619");
        jmsBroker.start();

        System.out.println("Publishing SMS Gateway Service as a JMS service: tcp://localhost:61619");
        System.out.println("Press Ctrl^C to terminate...");

        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61619");
        Connection connection = connectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        SMSGatewayImpl impl = new SMSGatewayImpl(session);
        impl.start();
    }
}
