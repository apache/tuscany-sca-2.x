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
package org.apache.tuscany.sca.host.jms.activemq;

import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.tuscany.sca.host.jms.JMSResourceFactory;

/**
 * Abstracts away any JMS provide specific feature from the JMS binding
 */
public class JMSResourceFactoryImpl implements JMSResourceFactory {

    private static final String DEFAULT_ICFN = "org.apache.activemq.jndi.ActiveMQInitialContextFactory";
    private String initialContextFactoryName = DEFAULT_ICFN;
    private String connectionFactoryName = "ConnectionFactory";
    private String jndiURL = ActiveMQBroker.CONNECTOR_URL;

    private Connection connection;
    private Context context;
    private boolean isConnectionStarted;

    public JMSResourceFactoryImpl(String connectionFactoryName, String initialContextFactoryName, String jndiURL) {
        if (connectionFactoryName != null) {
            this.connectionFactoryName = connectionFactoryName.trim();
        }
        if (initialContextFactoryName != null) {
            this.initialContextFactoryName = initialContextFactoryName.trim();
        }
        if (jndiURL != null) {
            this.jndiURL = jndiURL.trim();
        }
    }

    /*
     * This is a simple implementation where a connection is created per binding Ideally the resource factory should be
     * able to leverage the host environment to provide connection pooling if it can. E.g. if Tuscany is running inside
     * an AppServer Then we could leverage the JMS resources it provides
     * 
     * @see org.apache.tuscany.binding.jms.JMSResourceFactory#getConnection()
     */
    public Connection getConnection() throws NamingException, JMSException {
        if (connection == null) {
            createConnection();
        }
        return connection;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.tuscany.binding.jms.JMSResourceFactory#createSession()
     */
    public Session createSession() throws JMSException, NamingException {
        return getConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.tuscany.binding.jms.JMSResourceFactory#startConnection()
     */
    public void startConnection() throws JMSException, NamingException {
        if (!isConnectionStarted) {
            getConnection().start();
            isConnectionStarted = true;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.tuscany.binding.jms.JMSResourceFactory#closeConnection()
     */
    public void closeConnection() throws JMSException, NamingException {
        if (connection != null) {
            connection.close();
        }
    }

    public Object startBroker() {
        if (isEmbedded()) {
            // ensure the broker has been started
            return ActiveMQModuleActivator.startBroker(jndiURL);
        }
        return null;
    }
    
    public void stopBroker(Object broker) {
        if(broker instanceof ActiveMQBroker) {
            ((ActiveMQBroker) broker).stop();
        }
    }

    /**
     * TODO: need to find a way to determine whether or not need to start the embedded broker
     * for now it always starts it if the activemq icf is being used with our default jndiurl
     */
    protected boolean isEmbedded() {
        try {
            getConnection();
            return false;
        } catch (Exception e) {
            return true;
        }
        // return DEFAULT_ICFN.equals(initialContextFactoryName) && ActiveMQBroker.CONNECTOR_URL.equals(jndiURL);
    }

    private void createConnection() throws NamingException, JMSException {
        ConnectionFactory connectionFactory = (ConnectionFactory)getInitialContext().lookup(connectionFactoryName);
        connection = connectionFactory.createConnection();
    }

    private synchronized Context getInitialContext() throws NamingException {
        if (context == null) {
            Properties props = new Properties();
            props.setProperty(Context.INITIAL_CONTEXT_FACTORY, initialContextFactoryName);
            props.setProperty(Context.PROVIDER_URL, jndiURL);

            context = new InitialContext(props);
        }
        return context;
    }

    public Destination lookupDestination(String jndiName) throws NamingException {
        Destination dest = null;

        try {
            dest = (Destination)getInitialContext().lookup(jndiName);
        } catch (NamingException ex) {

        }
        return dest;
    }

    /**
     * You can create a destination in ActiveMQ (and have it appear in JNDI) by putting "dynamicQueues/" in front of the
     * queue name being looked up
     */
    public Destination createDestination(String jndiName) throws NamingException {
        return lookupDestination("dynamicQueues/" + jndiName);
    }
}
