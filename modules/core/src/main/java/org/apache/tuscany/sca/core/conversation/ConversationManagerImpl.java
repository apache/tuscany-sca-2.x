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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @version $Rev$ $Date$
 */
public class ConversationManagerImpl implements ConversationManager {
    private List<ConversationListener> listeners = Collections.synchronizedList(new ArrayList<ConversationListener>());
    private Map<Object, ExtendedConversation> converations = new ConcurrentHashMap<Object, ExtendedConversation>();

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
            converations.remove(conversationID);
        } else {
            throw new IllegalStateException("Conversation " + conversationID + " doesn't exist.");
        }
    }

    public void expireConversation(Object conversationID) {
        ExtendedConversation conv = getConversation(conversationID);
        if (conv != null) {
            ((ExtendedConversationImpl)conv).setState(ConversationState.EXPIRED);
            for (ConversationListener listener : listeners) {
                listener.conversationExpired(conv);
            }
        } else {
            throw new IllegalStateException("Conversation " + conversationID + " doesn't exist.");
        }

    }

    /**
     * @see org.apache.tuscany.sca.core.conversation.ConversationManager#getConversation(java.lang.Object)
     */
    public ExtendedConversation getConversation(Object conversationID) {
        return converations.get(conversationID);
    }

    /**
     * @see org.apache.tuscany.sca.core.conversation.ConversationManager#removeListener(org.apache.tuscany.sca.core.conversation.ConversationListener)
     */
    public void removeListener(ConversationListener listener) {
        listeners.remove(listener);
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
        conversation = new ExtendedConversationImpl(this, conversationID, ConversationState.STARTED);
        converations.put(conversationID, conversation);
        for (ConversationListener listener : listeners) {
            listener.conversationStarted(conversation);
        }
        return conversation;
    }

}
