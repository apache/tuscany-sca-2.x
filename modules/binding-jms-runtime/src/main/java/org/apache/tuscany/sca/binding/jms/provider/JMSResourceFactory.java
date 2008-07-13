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
package org.apache.tuscany.sca.binding.jms.provider;

import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.tuscany.sca.binding.jms.impl.JMSBindingException;

/**
 * Abstracts away any JMS provide specific feature from the JMS binding
 * 
 * @version $Rev$ $Date$
 */
public class JMSResourceFactory {

    private String initialContextFactoryName;
    private String connectionFactoryName = "ConnectionFactory";
    private String jndiURL;

    private Connection connection;
    private Context context;
    private boolean isConnectionStarted;

    public JMSResourceFactory(String connectionFactoryName, String initialContextFactoryName, String jndiURL) {
        if (connectionFactoryName != null && connectionFactoryName.trim().length() > 0) {
            this.connectionFactoryName = connectionFactoryName.trim();
        }
        if (initialContextFactoryName != null && initialContextFactoryName.trim().length() > 0) {
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
    public void closeConnection() throws JMSException {
        if (connection != null) {
            connection.close();
        }
    }

    private void createConnection() throws NamingException, JMSException {
        ConnectionFactory connectionFactory = (ConnectionFactory)jndiLookUp(connectionFactoryName);
        if (connectionFactory == null) {
            throw new JMSBindingException("connection factory not found: " + connectionFactoryName);
        }
        connection = connectionFactory.createConnection();
    }

    private synchronized Context getInitialContext() throws NamingException {
        if (context == null) {
            Properties props = new Properties();

            if (initialContextFactoryName != null) {
                props.setProperty(Context.INITIAL_CONTEXT_FACTORY, initialContextFactoryName);
            }
            if (jndiURL != null) {
                props.setProperty(Context.PROVIDER_URL, jndiURL);
            }

            initJREEnvironment(props);

            context = new InitialContext(props);
        }
        return context;
    }

    /**
     * If using the WAS JMS Client with a non-IBM JRE then an additional
     * environment property needs to be set to initialize the ORB correctly. 
     * See: http://www-1.ibm.com/support/docview.wss?uid=swg24012804
     */
    private void initJREEnvironment(Properties props) {
        if ("com.ibm.websphere.naming.WsnInitialContextFactory".equals(props.get(Context.INITIAL_CONTEXT_FACTORY))) {
            String vendor = System.getProperty("java.vendor");
            if (vendor == null || !vendor.contains("IBM")) {
                props.setProperty("com.ibm.CORBA.ORBInit", "com.ibm.ws.sib.client.ORB");
            }
        }
    }

    public Destination lookupDestination(String jndiName) throws NamingException {
        return (Destination)jndiLookUp(jndiName);
    }

    /**
     * You can create a destination in ActiveMQ (and have it appear in JNDI) by putting "dynamicQueues/" in front of the queue name being looked up
     */
    public Destination createDestination(String jndiName) throws NamingException {
        return lookupDestination("dynamicQueues/" + jndiName);
    }

    protected Object jndiLookUp(String name) {
        Object o = null;
        try {
            o = getInitialContext().lookup("java:comp/env/" + name);
        } catch (NamingException ex) {
            // ignore
        }
        if (o == null) {
            try {
                o = getInitialContext().lookup(name);
            } catch (NamingException ex) {
                // ignore
            }
        }
        return o;
    }
}
