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

import org.apache.tuscany.sca.binding.jms.JMSBindingException;

/**
 * Abstracts away any JMS provide specific feature from the JMS binding
 * 
 * @version $Rev$ $Date$
 */
public class JMSResourceFactoryImpl implements JMSResourceFactory {

    protected String initialContextFactoryName;
    protected String connectionFactoryName = "ConnectionFactory";
    protected String jndiURL;

    protected Connection connection;
    protected Context context;
    protected boolean isConnectionStarted;
    private Connection responseConnection;
    private String responseConnectionFactoryName;

    public JMSResourceFactoryImpl(String connectionFactoryName, String responseConnectionFactoryName, String initialContextFactoryName, String jndiURL) {
        if (connectionFactoryName != null && connectionFactoryName.trim().length() > 0) {
            this.connectionFactoryName = connectionFactoryName.trim();
        }
        if (responseConnectionFactoryName != null && responseConnectionFactoryName.trim().length() > 0) {
            this.responseConnectionFactoryName = responseConnectionFactoryName.trim();
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
     * @see org.apache.tuscany.sca.binding.jms.provider.JMSResourceFactory#getConnection()
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
     * @see org.apache.tuscany.sca.binding.jms.provider.JMSResourceFactory#createSession()
     */
    public Session createSession() throws JMSException, NamingException {
        return getConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.tuscany.sca.binding.jms.provider.JMSResourceFactory#closeSession(javax.jms.Session)
     */
    public void closeSession(Session session) throws JMSException {
        session.close();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.tuscany.sca.binding.jms.provider.JMSResourceFactory#startConnection()
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
     * @see org.apache.tuscany.sca.binding.jms.provider.JMSResourceFactory#closeConnection()
     */
    public void closeConnection() throws JMSException {
        if (connection != null) {
            try {
                connection.close();
            } catch (JMSException e) {
                // if using an embedded broker then when shutting down Tuscany the broker may get closed
                // before this stop method is called. I can't see how to detect that so for now just
                // ignore the exception if the message is that the transport is already disposed
                if (!e.getMessage().contains("disposed")) {
                    throw e;
                }
            }
        }
    }

    protected void createConnection() throws NamingException, JMSException {
        ConnectionFactory connectionFactory = (ConnectionFactory)jndiLookUp(connectionFactoryName);
        if (connectionFactory == null) {
            throw new JMSBindingException("connection factory not found: " + connectionFactoryName);
        }
        connection = connectionFactory.createConnection();
    }

    protected synchronized Context getInitialContext() throws NamingException {
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
    protected void initJREEnvironment(Properties props) {
        if ("com.ibm.websphere.naming.WsnInitialContextFactory".equals(props.get(Context.INITIAL_CONTEXT_FACTORY))) {
            String vendor = System.getProperty("java.vendor");
            if (vendor == null || !vendor.contains("IBM")) {
                props.setProperty("com.ibm.CORBA.ORBInit", "com.ibm.ws.sib.client.ORB");
            }
        }
    }

    /* (non-Javadoc)
     * @see org.apache.tuscany.sca.binding.jms.provider.JMSResourceFactory#lookupDestination(java.lang.String)
     */
    public Destination lookupDestination(String destName) throws NamingException {
        if (destName == null) {
            return null;
        }
        
        Destination dest = (Destination)jndiLookUp(destName);
        if (dest == null) {
            dest = lookupPhysical(destName);
        }
        return dest;
    }

    protected Destination lookupPhysical(String jndiName) {

        // TODO: the SCA JMS spec says a destination name may be a non-jndi plain destination name 
        
//        Session session = null;
//        try {
//
//            Destination dest;
//            session = createSession();
//            dest = session.createQueue(jndiName);
//            return dest;
//
//        } catch (JMSException e) {
//            throw new JMSBindingException(e);
//        } catch (NamingException e) {
//            throw new JMSBindingException(e);
//        } finally {
//            if (session != null) {
//                try {
//                    session.close();
//                } catch (JMSException e) {
//                    throw new JMSBindingException(e);
//                }
//            }
//        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.tuscany.sca.binding.jms.provider.JMSResourceFactory#createDestination(java.lang.String)
     */
    public Destination createDestination(String jndiName) throws NamingException {
        return lookupDestination("dynamicQueues/" + jndiName);
    }

    protected Object jndiLookUp(String name) {
        Object o = null;
        try {
            o = getInitialContext().lookup("java:comp/env/" + name);
        } catch (Exception ex) {
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

    public Session createResponseSession() throws JMSException, NamingException {
        return getResponseConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    public void closeResponseSession(Session session) throws JMSException {
        session.close();
    }

    public Connection getResponseConnection() throws NamingException, JMSException {
        if (responseConnection == null) {
            if (responseConnectionFactoryName != null) {
                ConnectionFactory connectionFactory = (ConnectionFactory)jndiLookUp(responseConnectionFactoryName);
                if (connectionFactory == null) {
                    throw new JMSBindingException("connection factory not found: " + responseConnectionFactoryName);
                }
                responseConnection = connectionFactory.createConnection();
            } else {
                // if no response connection is defined in the SCDL use the request connection
                responseConnection = getConnection();
            }
        }
        return responseConnection;
    }

    public void closeResponseConnection() throws JMSException {
        if (responseConnection != null && !responseConnection.equals(connection)) {
            try {
                responseConnection.close();
            } catch (JMSException e) {
                // if using an embedded broker then when shutting down Tuscany the broker may get closed
                // before this stop method is called. I can't see how to detect that so for now just
                // ignore the exception if the message is that the transport is already disposed
                if (!e.getMessage().contains("disposed")) {
                    throw e;
                }
            }
        }
    }

    public boolean isConnectionClosedAfterUse() {
        // It is assumed this resource factory is used in an environment
        // where the connection can be held for the life of the binding.
        return false;
    }

}
