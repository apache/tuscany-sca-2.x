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
package org.apache.tuscany.spi.wire;

import java.util.Stack;

/**
 * Represents a request, response, or exception flowing through a wire
 *
 * @version $Rev $Date
 */
public interface Message {

    /**
     * Returns the body of the message, which will be the payload or parameters associated with the wire
     */
    Object getBody();

    /**
     * Sets the body of the message.
     */
    void setBody(Object body);
    /**
     * Sets the target invoker to dispatch to when the message passes through the request side of the invocation chain
     */
    void setTargetInvoker(TargetInvoker invoker);

    /**
     * Sets the target invoker to dispatch to when the message passes through the request side of the invocation chain
     */
    TargetInvoker getTargetInvoker();

    /**
     * Returns the latest 'address' of the SCAObject where this message originated
     */
    Object popFromAddress();

    /**
     * Adds the latest 'address' of the SCAObject where this message originated
     */
    void pushFromAddress(Object fromAddress);
    
    /**
     * Returns the stack of SCAObject addresses
     */
    Stack<Object> getCallbackRoutingChain();
    
    /**
     * Sets the stack of SCAObject addresses
     */
    void setCallbackRoutingChain(Stack<Object> fromAddresses);

    /**
     * Returns the id of the message
     */
    Object getMessageId();

    /**
     * Sets the id of the message
     */
    void setMessageId(Object messageId);

    /**
     * Returns the correlation id of the message
     */
    Object getCorrelationId();

    /**
     * Sets the correlation id of the message
     */
    void setCorrelationId(Object correlationId);
    
    /**
     * Test if the message represents a fault/exception
     * @return true if the message body is a fault object, false is the body is a normal payload
     */
    boolean isFault();

    /**
     * Set the message body with a fault object. After this method is called, isFault() returns true.
     * @param fault The fault object represents an exception
     */
    void setBodyWithFault(Object fault);
}
