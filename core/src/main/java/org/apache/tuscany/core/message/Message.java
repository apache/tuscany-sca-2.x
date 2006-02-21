/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.core.message;

import java.util.Map;

import org.apache.tuscany.core.addressing.EndpointReference;
import org.apache.tuscany.core.invocation.TargetInvoker;
import org.apache.tuscany.core.message.channel.MessageChannel;

/**
 * Represents a request, response, or exception for an invocation
 */
public interface Message {

    /**
     * Return any message headers associated with the invocation.
     */
    Map<String, Object> getHeaders();

    /**
     * Returns the body of the message, which will be the payload or parameters
     * associated with the invocation
     * FIXME what is different w/ getPayload()?
     */
    Object getBody();

    /**
     * Sets the body of the message.
     */
    void setBody(Object body);

    /**
     * Returns true if the message is a request message
     * FIXME is this still used?
     */
    boolean isRequest();

    /**
     * Returns true if the message is an inbound message
     * FIXME is this still used?
     */
    boolean isResponse();

    /**
     * Sets the To header
     * FIXME Javadoc
     */
    void setTo(EndpointReference to);

    /**
     * Returns the To header
     * FIXME Javadoc
     */
    EndpointReference getTo();

    /**
     * Sets the From header
     * FIXME Javadoc
     */
    void setFrom(EndpointReference from);

    /**
     * Returns the From header
     * FIXME Javadoc
     */
    EndpointReference getFrom();

    /**
     * Sets the message ID
     */
    void setMessageID(String messageID);

    /**
     * Returns the message ID
     */
    String getMessageID();

    /**
     * Sets the Action header
     * FIXME Javadoc
     */
    void setAction(String action);

    /**
     * Returns the Action header
     * FIXME Javadoc
     */
    String getAction();

    /**
     * Sets the ReplyTo header
     * FIXME Javadoc
     */
    void setReplyTo(EndpointReference replyTo);

    /**
     * Returns the ReplyTo header
     * FIXME Javadoc
     */
    EndpointReference getReplyTo();

    /**
     * Sets the RelatesTo header
     * FIXME Javadoc
     */
    void setRelatesTo(String relatesTo);

    /**
     * Returns the RelatesTo header
     * FIXME Javadoc
     */
    String getRelatesTo();

    /**
     * Sets the FaultTo header
     * FIXME Javadoc
     */
    void setFaultTo(EndpointReference faultTo);

    /**
     * Returns the FaultTo header
     * FIXME Javadoc
     */
    EndpointReference getFaultTo();

    /**
     * Sets the EndpointReference header
     * FIXME Javadoc
     */
    void setEndpointReference(EndpointReference endpointReference);

    /**
     * Returns the EndpointReference header
     * FIXME Javadoc
     */
    EndpointReference getEndpointReference();

    /**
     * Sets the operation name
     * FIXME Javadoc
     */
    void setOperationName(String operationName);

    /**
     * Returns the operation name
     * FIXME Javadoc
     */
    String getOperationName();

    /**
     * Returns the callback channel
     * FIXME Javadoc
     */
    MessageChannel getCallbackChannel();

    /**
     * Returns the related callback message
     * FIXME Javadoc
     */
    Message getRelatedCallbackMessage();
    
    //ADDED
    public void setTargetInvoker(TargetInvoker invoker);
    
    public TargetInvoker getTargetInvoker();
    
} // Message
