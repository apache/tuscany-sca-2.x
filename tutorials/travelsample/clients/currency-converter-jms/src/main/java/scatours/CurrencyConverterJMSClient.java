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
package scatours;

import java.io.ByteArrayInputStream;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.stream.XMLStreamException;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;

public class CurrencyConverterJMSClient {

    private static Connection activeMQConnection;
    private static Session activeMQSession;
    private static Destination requestDestination;
    private static MessageProducer activeMQProducer;
    private static Destination responseDestination;
    private static MessageConsumer consumer;
    private static OMFactory omFactory;

    public static void main(String[] args) throws JMSException, XMLStreamException {
        startActiveMQSession();

        OMElement request = omFactory.createOMElement("convert", "http://goodvaluetrips.com/", "ns2");
        request.addChild(createArg(0, "USD"));
        request.addChild(createArg(1, "GBP"));
        request.addChild(createArg(2, "100.0"));

        TextMessage message = activeMQSession.createTextMessage("convert");
        message.setStringProperty("scaOperationName", "convert");
        message.setJMSReplyTo(responseDestination);
        message.setText(request.toString());
        activeMQProducer.send(message);

        TextMessage response = (TextMessage)consumer.receive();
        StAXOMBuilder builder = new StAXOMBuilder(new ByteArrayInputStream(response.getText().getBytes()));
        OMText returnElement = (OMText)builder.getDocumentElement().getFirstOMChild();
        String returnValue = returnElement.getText();
        System.out.println("100 USD = " + returnValue + "GBP");

        stopActiveMQSession();
    }

    private static void startActiveMQSession() throws JMSException {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61619");

        activeMQConnection = connectionFactory.createConnection();
        activeMQConnection.start();

        activeMQSession = activeMQConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        requestDestination = activeMQSession.createQueue("RequestQueue");
        activeMQProducer = activeMQSession.createProducer(requestDestination);

        responseDestination = activeMQSession.createQueue("ResponseQueue");
        consumer = activeMQSession.createConsumer(responseDestination);

        omFactory = OMAbstractFactory.getOMFactory();
    }

    private static void stopActiveMQSession() throws JMSException {
        consumer.close();
        activeMQProducer.close();
        activeMQSession.close();
        activeMQConnection.close();
    }

    private static OMElement createArg(int argNumber, String value) {
        OMFactory omFactory = OMAbstractFactory.getOMFactory();
        OMElement arg = omFactory.createOMElement("arg" + argNumber, null);
        OMText senderOM = omFactory.createOMText(value);
        arg.addChild(senderOM);
        return arg;
    }
}
