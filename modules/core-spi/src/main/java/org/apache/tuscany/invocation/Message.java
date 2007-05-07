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
package org.apache.tuscany.invocation;

import java.util.LinkedList;

import org.apache.tuscany.core.RuntimeWire;
import org.apache.tuscany.spi.component.WorkContext;

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
     * Returns the context associated with this invocation.
     * @return the context associated with this invocation
     */
    WorkContext getWorkContext();

    /**
     * Sets the context associated with this invocation.
     * @param workContext the context associated with this invocation
     */
    void setWorkContext(WorkContext workContext);

    /**
     * Adds a callback wire to the ordered list of callbacks for the current invocation
     *
     * @param wire the callback wire
     */
    void pushCallbackWire(RuntimeWire wire);

    /**
     * Returns the ordered list of callback wires for the current invocation
     *
     * @return the ordered list of callback wires for the current invocation
     */
    LinkedList<RuntimeWire> getCallbackWires();

    /**
     * Sets the ordered list of callback wires for the current invocation
     *
     * @param wires the ordered list of callback wires for the current invocation
     */
    void setCallbackWires(LinkedList<RuntimeWire> wires);

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
     * Returns the conversational sequence the message is associated with, NONE, START, CONTINUE, or END on {@link
     * TargetInvoker}
     *
     * @return the conversational sequence the message is associated with
     */
    short getConversationSequence();

    /**
     * Returns the conversational sequence the message is associated with, NONE, START, CONTINUE, or END on {@link
     * TargetInvoker}
     *
     * @param sequence the conversational sequence
     */
    void setConversationSequence(short sequence);

}
