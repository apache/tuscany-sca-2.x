/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.message.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.core.addressing.EndpointReference;
import org.apache.tuscany.core.message.Message;
import org.apache.tuscany.core.message.channel.MessageChannel;

/**
 * Implements a <code>Message</code> as a plain Java object
 * 
 * @version $Rev$ $Date$
 */
public class PojoMessageImpl implements Message, MessageChannel {

    public PojoMessageImpl() {
    }

    private Map<String, Object> headers;

    public Map<String, Object> getHeaders() {
        if (headers == null) {
            headers = new HashMap();
        }
        return headers;
    }

    private Object body;

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    private Object payload;

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    private boolean request;

    public boolean isRequest() {
        return request;
    }

    private boolean response;

    public boolean isResponse() {
        return response;
    }

    private EndpointReference to;
    
    public void setTo(EndpointReference to) {
        this.to=to;
    }

    public EndpointReference getTo() {
        return to;
    }

    private EndpointReference from;
    public void setFrom(EndpointReference from) {
        this.from=from;
    }

    public EndpointReference getFrom() {
        return from;
    }

    private String messageID;
    
    public void setMessageID(String messageID) {
        this.messageID=messageID;
    }

    public String getMessageID() {
        return messageID;
    }

    private String action;
    
    public void setAction(String action) {
        this.action=action;
    }

    public String getAction() {
        return action;
    }

    private EndpointReference replyTo;
    
    public void setReplyTo(EndpointReference replyTo) {
        this.replyTo = replyTo;
    }

    public EndpointReference getReplyTo() {
        return replyTo;
    }

    private String relatesTo;
    
    public void setRelatesTo(String relatesTo) {
        this.relatesTo=relatesTo;
    }

    public String getRelatesTo() {
        return relatesTo;
    }

    private EndpointReference faultTo;
    
    public void setFaultTo(EndpointReference faultTo) {
        this.faultTo=faultTo;
    }

    public EndpointReference getFaultTo() {
        return faultTo;
    }

    private EndpointReference endpointReference;
    
    public void setEndpointReference(EndpointReference endpointReference) {
        this.endpointReference=endpointReference;
    }

    public EndpointReference getEndpointReference() {
        return endpointReference;
    }

    private String operationName;
    
    public void setOperationName(String operationName) {
        this.operationName=operationName;
    }

    public String getOperationName() {
        return operationName;
    }

    public MessageChannel getCallbackChannel() {
        return this;
    }

    private Message callbackMessage;
    
    public Message getRelatedCallbackMessage() {
        return callbackMessage;
    }

    public void send(Message message) {
        callbackMessage = message;
    }

}
