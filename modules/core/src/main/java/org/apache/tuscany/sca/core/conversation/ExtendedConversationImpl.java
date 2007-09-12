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


/**
 * 
 * @version $Rev$ $Date$
 */
public class ExtendedConversationImpl implements ExtendedConversation {
    private ConversationManager manager;
    private Object conversationID;
    private ConversationState state;

    /**
     * @param manager
     * @param conversationID
     * @param state
     */
    public ExtendedConversationImpl(ConversationManager manager, Object conversationID, ConversationState state) {
        super();
        this.manager = manager;
        this.conversationID = conversationID;
        this.state = state;
    }

    public void expire() {
        manager.expireConversation(conversationID);
    }

    public ConversationState getState() {
        return state;
    }

    public void end() {
        manager.endConversation(conversationID);
    }

    public Object getConversationID() {
        return conversationID;
    }

    /**
     * @param state the state to set
     */
    public void setState(ConversationState state) {
        this.state = state;
    }

    /**
     * @param conversationID the conversationID to set
     */
    public void setConversationID(Object conversationID) {
        if (state != ConversationState.ENDED) {
            throw new IllegalStateException("The state of conversation " + conversationID + " " + state);
        }
        this.conversationID = conversationID;
    }

}
