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
package org.apache.tuscany.sca.binding.jms.mock;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.naming.NamingException;

import org.apache.tuscany.sca.host.jms.JMSResourceFactory;
import org.easymock.EasyMock;

/**
 * Mock JMSResourceFactory base class for testing purposes.
 */
public abstract class MockJMSResourceFactory implements JMSResourceFactory {

    /**
     * Throws UnsupportedOperationException
     */
    public void closeConnection() throws JMSException, NamingException {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates a Mock Destination
     * 
     * @param jndiName Ignored
     * @return A Mock Destination
     */
    public Destination createDestination(String jndiName) throws NamingException {
        final Destination d = EasyMock.createMock(Destination.class);
        EasyMock.replay(d);
        return d;
    }

    /**
     * Creates a Mock Session.
     * 
     * @return A Mock Session
     */
    public Session createSession() throws JMSException, NamingException {
        final Session session = EasyMock.createMock(Session.class);
        final MessageConsumer consumer = EasyMock.createMock(MessageConsumer.class);
        final MessageListener listener = EasyMock.createMock(MessageListener.class);
        EasyMock.expect(session.createConsumer((Destination)EasyMock.anyObject())).andReturn(consumer);
        consumer.setMessageListener((MessageListener)EasyMock.anyObject());
        EasyMock.replay(session);
        EasyMock.replay(consumer);
        EasyMock.replay(listener);
        return session;
    }

    /**
     * Throws UnsupportedOperationException
     */
    public Connection getConnection() throws NamingException, JMSException {
        throw new UnsupportedOperationException();
    }

    /**
     * Throws UnsupportedOperationException
     * 
     * @param jndiName Ignored
     */
    public Destination lookupDestination(String jndiName) throws NamingException {
        throw new UnsupportedOperationException();
    }

    /**
     * Does nothing
     */
    public void startConnection() throws JMSException, NamingException {
    }
}
