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
package org.apache.tuscany.sca.invocation;

import org.apache.tuscany.sca.core.EndpointReference;
import org.apache.tuscany.sca.core.RuntimeComponentReference;
import org.apache.tuscany.sca.core.RuntimeComponentService;
import org.apache.tuscany.sca.core.RuntimeWire;
import org.apache.tuscany.sca.interfacedef.ConversationSequence;

/**
 * Represents a request, response, or exception flowing through a wire
 *
 * @version $Rev $Date
 */
public interface Message {

    /**
     * Returns the body of the message, which will be the payload or parameters associated with the wire
     */
    <T> T getBody();

    /**
     * Sets the body of the message.
     */
    <T> void setBody(T body);

    /**
     * Get the conversation id
     * @return
     */
    String getConversationID();

    /**
     * Set the conversation id
     * @param conversationId
     */
    void setConversationID(String conversationId);

    /**
     * Get the URI of the source reference
     * @return
     */
    EndpointReference<RuntimeComponentReference> getFrom();

    /**
     * 
     * @param from
     */
    void setFrom(EndpointReference<RuntimeComponentReference> from);

    /**
     * Get the URI of target service
     * @return
     */
    EndpointReference<RuntimeComponentService> getTo();

    /**
     * @param to
     */
    void setTo(EndpointReference<RuntimeComponentService> to);

    /**
     * Returns the id of the message
     */
    Object getMessageID();

    /**
     * Sets the id of the message
     */
    void setMessageID(Object messageId);

    /**
     * Returns the correlation id of the message or null if one is not available. Correlation ids are used by transports
     * for message routing.
     */
    Object getCorrelationID();

    /**
     * Sets the correlation id of the message. Correlation ids are used by transports for message routing.
     */
    void setCorrelationID(Object correlationId);

    /**
     * Determines if the message represents a fault/exception
     *
     * @return true if the message body is a fault object, false if the body is a normal payload
     */
    boolean isFault();

    /**
     * Set the message body with a fault object. After this method is called, isFault() returns true.
     *
     * @param fault The fault object represents an exception
     */
    <T> void setFaultBody(T fault);

    /**
     * Returns the conversational sequence the message is associated with, NONE, START, CONTINUE, or END on TargetInvoker}
     *
     * @return the conversational sequence the message is associated with
     */
    ConversationSequence getConversationSequence();

    /**
     * Returns the conversational sequence the message is associated with, NONE, START, CONTINUE, or END
     *
     * @param sequence the conversational sequence
     */
    void setConversationSequence(ConversationSequence sequence);

}
