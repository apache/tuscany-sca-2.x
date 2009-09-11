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

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.naming.NamingException;

public interface JMSResourceFactory {

    /*
     * This is a simple implementation where a connection is created per binding Ideally the resource factory should be
     * able to leverage the host environment to provide connection pooling if it can. E.g. if Tuscany is running inside 
     * an AppServer Then we could leverage the JMS resources it provides
     * 
     * @see org.apache.tuscany.binding.jms.JMSResourceFactory#getConnection()
     */
    public abstract Connection getConnection() throws NamingException, JMSException;

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.tuscany.binding.jms.JMSResourceFactory#createSession()
     */
    public abstract Session createSession() throws JMSException, NamingException;

    public abstract void closeSession(Session session) throws JMSException;

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.tuscany.binding.jms.JMSResourceFactory#startConnection()
     */
    public abstract void startConnection() throws JMSException, NamingException;

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.tuscany.binding.jms.JMSResourceFactory#closeConnection()
     */
    public abstract void closeConnection() throws JMSException;

    public abstract Destination lookupDestination(String destName) throws NamingException;

    /**
     * You can create a destination in ActiveMQ (and have it appear in JNDI) by putting "dynamicQueues/" in front of the queue name being looked up
     */
    public abstract Destination createDestination(String jndiName) throws NamingException;

    /*
     * This is a simple implementation where a connection is created per binding Ideally the resource factory should be
     * able to leverage the host environment to provide connection pooling if it can. E.g. if Tuscany is running inside 
     * an AppServer Then we could leverage the JMS resources it provides
     * 
     * @see org.apache.tuscany.binding.jms.JMSResourceFactory#getConnection()
     */
    public abstract Connection getResponseConnection() throws NamingException, JMSException;

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.tuscany.binding.jms.JMSResourceFactory#createSession()
     */
    public abstract Session createResponseSession() throws JMSException, NamingException;

    public abstract void closeResponseSession(Session session) throws JMSException;
    
    public abstract void closeResponseConnection() throws JMSException;

    /*
     * Indicates whether connections obtained using getConnection() or getResponseConnection()
     * must be closed after each use.  This is necessary in environments where connections are
     * shared with other users, or where connections cannot be held across transaction boundaries.
     */
    public abstract boolean isConnectionClosedAfterUse();
}
