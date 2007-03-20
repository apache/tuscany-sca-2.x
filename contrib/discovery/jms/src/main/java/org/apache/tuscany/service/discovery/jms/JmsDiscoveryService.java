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
package org.apache.tuscany.service.discovery.jms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.tuscany.spi.services.discovery.AbstractDiscoveryService;
import org.apache.tuscany.spi.services.discovery.DiscoveryException;
import org.apache.tuscany.spi.util.stax.StaxUtil;
import org.osoa.sca.annotations.Property;

/**
 * JMS based implementation of the discovery service. This class uses 
 * ActiveMQ specific API instead of JNDI based administered objects. This can 
 * be changed later if required.
 * 
 * @version $Revision$ $Date$
 */
public class JmsDiscoveryService extends AbstractDiscoveryService {

    // Connection factory
    private ConnectionFactory connectionFactory;

    // Underlying JMS connection
    private Connection connection;

    // Session used for reception
    private Session receiverSession;

    // Message consumer
    private MessageConsumer messageConsumer;

    // Topic to use
    private Topic topic;
    
    // String broker url
    private String brokerUrl = ActiveMQConnection.DEFAULT_BROKER_URL;

    /**
     * Injects the topic used for communication.
     * @param topic Topic used for communication.
     */
    @Property
    public void setTopic(String topic) {
        this.topic = new ActiveMQTopic(topic);
    }

    /**
     * Injects the broker URL.
     * @param brokerUrl Broker URL to use.
     */
    @Property
    public void setBrokerUrl(String brokerUrl) {
        this.brokerUrl = brokerUrl;
    }

    /**
     * Starts the service and sets up the message listener.
     */
    @Override
    protected synchronized void onStart() throws DiscoveryException {

        String runtimeId = getRuntimeInfo().getRuntimeId();

        try {
            
            connectionFactory = new ActiveMQConnectionFactory(brokerUrl);

            connection = connectionFactory.createConnection();
            connection.setExceptionListener(new ExceptionListener() {
                public void onException(JMSException jmsException) {
                    // Try restarting: TODO this may need further refinement
                    try {
                        onStop();
                    } catch (DiscoveryException ex) {
                        ex.printStackTrace();
                    }
                    try {
                        onStart();
                    } catch (DiscoveryException ex) {
                        ex.printStackTrace();
                    }
                }                
            });
            receiverSession = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);

            messageConsumer = receiverSession.createConsumer(topic);
            final MessageListener messageListener = new TuscanyMessageListener(this, runtimeId);
            messageConsumer.setMessageListener(messageListener);
            connection.start();

        } catch (JMSException ex) {
            throw new DiscoveryException(ex);
        }

    }

    /**
     * Closes the connection.
     */
    @Override
    protected synchronized void onStop() throws DiscoveryException {

        try {
            receiverSession.close();
        } catch (JMSException ex) {
            throw new DiscoveryException(ex);
        } finally {
            try {
                connection.close();                
            } catch (JMSException ex) {
                throw new DiscoveryException(ex);
            }
        }
        
    }

    /**
     * Sends the message.
     */
    public synchronized int sendMessage(String runtimeId, XMLStreamReader reader) throws DiscoveryException {

        try {

            String text = StaxUtil.serialize(reader);
            Session senderSession = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
            MessageProducer messageProducer = senderSession.createProducer(topic);

            TextMessage textMessage = senderSession.createTextMessage(text);
            
            textMessage.setStringProperty("runtimeId", runtimeId);
            messageProducer.send(textMessage);
            senderSession.commit();
            senderSession.close();

            return 1;

        } catch (XMLStreamException ex) {
            throw new DiscoveryException(ex);
        } catch (JMSException ex) {
            throw new DiscoveryException(ex);
        }
    }

}
