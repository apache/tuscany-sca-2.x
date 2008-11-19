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

import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.osoa.sca.Conversation;

/**
 * An extended interface over org.osoa.Conversation
 * 
 * @version $Rev$ $Date$
 */
public interface ExtendedConversation extends Conversation {
    /**
     * Get the state of a conversation
     * @return The state
     */
    ConversationState getState();

    /**
     * @param state the state to set
     */
    void setState(ConversationState state);

    /**
     * @param conversationID the conversationID to set
     */
    void setConversationID(Object conversationID);
    
    
    /**
     * will check whether this conversation has expired and update state if it has 
     * @return true if it has expired
     */
    boolean isExpired();
    
    /**
     * updates the last time this conversation was referenced
     */
    void updateLastReferencedTime();
    
    void initializeConversationAttributes(RuntimeComponent targetComponent);

    
    /**
     * @return true if the conversational attributes have been initialized
     */
    boolean conversationalAttributesInitialized();
}
