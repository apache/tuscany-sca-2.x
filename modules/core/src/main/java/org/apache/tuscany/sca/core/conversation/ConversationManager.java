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
 * The manager of conversations
 *
 * @version $Rev$ $Date$
 */
public interface ConversationManager {
    /**
     * @param conversationID
     * @return
     */
    ConversationExt startConversation(Object conversationID);

    /**
     * @param conversationID
     */
    void endConversation(Object conversationID);

    /**
     * @param conversationID
     * @return
     */
    ConversationExt getConversation(Object conversationID);

    /**
     * @param conversationID
     */
    void expireConversation(Object conversationID);

    /**
     * Add a listener to this conversation
     * @param listener
     */
    void addListener(ConversationListener listener);

    /**
     * Remove a listener from this conversation
     * @param listener
     */
    void removeListener(ConversationListener listener);

    /**
     * @return the default max age for a conversation
     */
    long getMaxAge();

    /**
     * @return the default max idle time for a conversation
     */
    long getMaxIdleTime();

}
