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
package org.apache.tuscany.sca.binding.jms.context;

import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.tuscany.sca.binding.jms.JMSBindingException;
import org.apache.tuscany.sca.binding.jms.provider.JMSResourceFactory;


/**
 * Context that the JMS binding puts on the Tuscany wire
 *
 * @version $Rev$ $Date$
 */
public class JMSBindingContext {

    private Message jmsMsg;
    private Session jmsSession;
    private Session jmsResponseSession;
    private Destination requestDestination;
    private Destination replyToDestination;
    private JMSResourceFactory jmsResourceFactory;
    private long timeToLive;
    private boolean useBytesForWFJMSDefaultResponse;

    public Message getJmsMsg() {
        return jmsMsg;
    }
    
    public void setJmsMsg(Message jmsMsg) {
        this.jmsMsg = jmsMsg;
    }
    
    public synchronized Session getJmsSession() {
        if (jmsSession == null) {
            try {
                jmsSession = getJmsResourceFactory().createSession();
            } catch (Exception e) {
                throw new JMSBindingException(e);
            }
        }
        return jmsSession;
    }

    public synchronized void closeJmsSession() {
        if (jmsSession != null) {
            try {
                getJmsResourceFactory().closeSession(jmsSession);
            } catch (Exception e) {
                throw new JMSBindingException(e);
            } finally {
                jmsSession = null;
            }
        }
    }

    public synchronized Session getJmsResponseSession() {
        if (jmsResponseSession == null) {
            try {
                jmsResponseSession = getJmsResourceFactory().createResponseSession();
            } catch (Exception e) {
                throw new JMSBindingException(e);
            }
        }
        return jmsResponseSession;
    }

    public synchronized void closeJmsResponseSession() {
        if (jmsResponseSession != null) {
            try {
                getJmsResourceFactory().closeResponseSession(jmsResponseSession);
            } catch (Exception e) {
                throw new JMSBindingException(e);
            } finally {
                jmsResponseSession = null;
            }
        }
    }

    public Destination getRequestDestination() {
        return requestDestination;
    }
    
    public void setRequestDestination(Destination requestDestination) {
        this.requestDestination = requestDestination;
    }
    
    public Destination getReplyToDestination() {
        return replyToDestination;
    }
    
    public void setReplyToDestination(Destination replyToDestination) {
        this.replyToDestination = replyToDestination;
    }
    
    // TODO - difficult to get the resource factory into all the JMS providers
    //        so it's here for the moment
    public JMSResourceFactory getJmsResourceFactory() {
        return jmsResourceFactory;
    }
    
    public void setJmsResourceFactory(JMSResourceFactory jmsResourceFactory) {
        this.jmsResourceFactory = jmsResourceFactory;
    }
    
    public long getTimeToLive() {
		return timeToLive;
	}
    
    public void setTimeToLive(long timeToLive) {
		this.timeToLive = timeToLive;
	}

    public boolean isUseBytesForWFJMSDefaultResponse() {
        return useBytesForWFJMSDefaultResponse;
    }

    public void setUseBytesForWFJMSDefaultResponse(
            boolean useBytesForWFJMSDefaultResponse) {
        this.useBytesForWFJMSDefaultResponse = useBytesForWFJMSDefaultResponse;
    }
}
