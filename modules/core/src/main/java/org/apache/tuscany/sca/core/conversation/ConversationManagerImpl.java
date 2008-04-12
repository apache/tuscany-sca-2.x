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

package org.apache.tuscany.sca.core.conversation;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @version $Rev$ $Date$
 */
public class ConversationManagerImpl implements ConversationManager {
	
    private List<ConversationListener> listeners = Collections.synchronizedList(new ArrayList<ConversationListener>());
    private Map<Object, ExtendedConversation> conversations = new ConcurrentHashMap<Object, ExtendedConversation>();

    /**
     * the default max age. this is set to 1 hour
     */
    private static final long DEFAULT_MAX_AGE = 60 * 60 * 1000; ;
    
    /**
     * the default max idle time. this is set to 1 hour
     */
    private static final long DEFAULT_MAX_IDLE_TIME = 60 * 60 * 1000; 
    
    /**
     * the globally used max age
     */
    private final long maxAge;

    /**
     * the globally used max idle time
     */
    private final long maxIdleTime; 

    /**
     * the reaper thread
     */
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    
    /**
     * constructor
     */
    public ConversationManagerImpl() {
    	long mit = DEFAULT_MAX_IDLE_TIME;
    	long ma = DEFAULT_MAX_AGE;
    	
    	// Allow privileged access to read system property. Requires PropertyPermission in security
        // policy.
        String aProperty = AccessController.doPrivileged(new PrivilegedAction<String>() {
            public String run() {
                return System.getProperty("org.apache.tuscany.sca.core.scope.ConversationalScopeContainer.MaxIdleTime");
            }
        });
    	if (aProperty != null) {
    		try {
    			mit = (new Long(aProperty) * 1000);
    		} catch (NumberFormatException nfe) {
    			// Ignore
    		}
    	}

    	// Allow privileged access to read system property. Requires PropertyPermission in security
        // policy.
        aProperty = AccessController.doPrivileged(new PrivilegedAction<String>() {
            public String run() {
                return System.getProperty("org.apache.tuscany.sca.core.scope.ConversationalScopeContainer.MaxAge");
            }
        });
        if (aProperty != null) {
            try {
                ma = (new Long(aProperty) * 1000);
            } catch (NumberFormatException nfe) {
                // Ignore
            }
        }

        maxAge = ma;
        maxIdleTime = mit;
    }
    
    /**
     * @see org.apache.tuscany.sca.core.conversation.ConversationManager#addListener(org.apache.tuscany.sca.core.conversation.ConversationListener)
     */
    public void addListener(ConversationListener listener) {
        listeners.add(listener);
    }

    /**
     * @see org.apache.tuscany.sca.core.conversation.ConversationManager#endConversation(org.apache.tuscany.sca.core.conversation.ExtendedConversation)
     */
    public void endConversation(Object conversationID) {
        ExtendedConversation conv = getConversation(conversationID);
        if (conv != null) {
            conv.setState(ConversationState.ENDED);
            for (ConversationListener listener : listeners) {
                listener.conversationEnded(conv);
            }
            conv.setConversationID(null);
            conversations.remove(conversationID);
        } else {
            throw new IllegalStateException("Conversation " + conversationID + " doesn't exist.");
        }
    }

    public void expireConversation(Object conversationID) {
        ExtendedConversation conv = getConversation(conversationID);
        if (conv != null) {
            for (ConversationListener listener : listeners) {
                listener.conversationExpired(conv);
            }
            conversations.remove(conversationID);
        } else {
            throw new IllegalStateException("Conversation " + conversationID + " doesn't exist.");
        }

    }

    /**
     * @see org.apache.tuscany.sca.core.conversation.ConversationManager#getConversation(java.lang.Object)
     */
    public ExtendedConversation getConversation(Object conversationID) {
        return conversations.get(conversationID);
    }

    /**
     * @see org.apache.tuscany.sca.core.conversation.ConversationManager#removeListener(org.apache.tuscany.sca.core.conversation.ConversationListener)
     */
    public void removeListener(ConversationListener listener) {
        listeners.remove(listener);
    }

    /**
     * starts the reaper thread
     */
    public void scheduleConversation(ExtendedConversationImpl aConversation, long time)
    {
    	this.scheduler.schedule(aConversation, time, TimeUnit.MILLISECONDS);
    }

    /**
     * stops the reaper thread
     */
    public synchronized void stopReaper() {

        // Prevent the scheduler from submitting any additional reapers, 
    	// initiate an orderly shutdown if a reaper task is in progress. 
    	this.scheduler.shutdown();
    }
    

    /**
     * @see org.apache.tuscany.sca.core.conversation.ConversationManager#startConversation(java.lang.Object)
     */
    public ExtendedConversation startConversation(Object conversationID) {
    	
        if (conversationID == null) {
            conversationID = UUID.randomUUID().toString();
        }
        ExtendedConversation conversation = getConversation(conversationID);
        if (conversation != null && conversation.getState() != ConversationState.ENDED) {
            throw new IllegalStateException(conversation + " already exists.");
        }
                
        conversation = new ExtendedConversationImpl(
        		this, conversationID, ConversationState.STARTED);
        conversations.put(conversationID, conversation);
        for (ConversationListener listener : listeners) {
            listener.conversationStarted(conversation);
        }
        return conversation;
    }

    /**
     * return the default max idle time
     * @param impProvider the implementation Provider to extract any ConversationAttribute details
     */
    public long getMaxIdleTime() {
        return maxIdleTime;
    }

    /**
     * returns the default max age
     * @param impProvider the implementation Provider to extract any ConversationAttribute details
     */
    public long getMaxAge(){
        return maxAge;
    }
}
