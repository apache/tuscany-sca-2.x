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
package org.apache.tuscany.core.message.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.core.addressing.EndpointReference;
import org.apache.tuscany.core.invocation.MessageChannel;
import org.apache.tuscany.core.invocation.TargetInvoker;
import org.apache.tuscany.core.message.Message;

/**
 */
public class MessageImpl implements Message, MessageChannel {

    private String action;
    private Object body;
    private EndpointReference endpointReference;
    private EndpointReference faultTo;
    private EndpointReference from;
    private Map<String, Object> headers;
    private String messageID;
    private String operationName;
    private Message relatedCallbackMessage;
    private String relatesTo;
    private EndpointReference replyTo;
    private TargetInvoker invoker;
    private EndpointReference to;

    /**
     * Constructor
     */
    protected MessageImpl() {
        super();
    }

    /**
     * @see org.apache.tuscany.core.message.Message#getAction()
     */
    public String getAction() {
        return action;
    }

    /**
     * @see org.apache.tuscany.core.message.Message#getBody()
     */
    public Object getBody() {
        return body;
    }

    /**
     * @see org.apache.tuscany.core.message.Message#getEndpointReference()
     */
    public EndpointReference getEndpointReference() {
        return endpointReference;
    }

    /**
     * @see org.apache.tuscany.core.message.Message#getFaultTo()
     */
    public EndpointReference getFaultTo() {
        return faultTo;
    }

    /**
     * @see org.apache.tuscany.core.message.Message#getFrom()
     */
    public EndpointReference getFrom() {
        return from;
    }

    /**
     * @see org.apache.tuscany.core.message.Message#getHeaders()
     */
    public Map<String, Object> getHeaders() {
        if (headers==null)
            headers=new HashMap<String, Object>();
        return headers;
    }

    /**
     * @see org.apache.tuscany.core.message.Message#getMessageID()
     */
    public String getMessageID() {
        return messageID;
    }

    /**
     * @see org.apache.tuscany.core.message.Message#getOperationName()
     */
    public String getOperationName() {
        return operationName;
    }

    /**
     * @see org.apache.tuscany.core.message.Message#getRelatesTo()
     */
    public String getRelatesTo() {
        return relatesTo;
    }

    /**
     * @see org.apache.tuscany.core.message.Message#getReplyTo()
     */
    public EndpointReference getReplyTo() {
        return replyTo;
    }

    /**
     * @see org.apache.tuscany.core.message.Message#getTo()
     */
    public EndpointReference getTo() {
        return to;
    }

    /**
     * @see org.apache.tuscany.core.message.Message#isRequest()
     */
    public boolean isRequest() {
        return relatesTo==null;
    }

    /**
     * @see org.apache.tuscany.core.message.Message#isResponse()
     */
    public boolean isResponse() {
        return relatesTo!=null;
    }

    /**
     * @see org.apache.tuscany.core.message.Message#setAction(java.lang.String)
     */
    public void setAction(String action) {
        this.action=action;
    }

    /**
     * @see org.apache.tuscany.core.message.Message#setBody(java.lang.Object)
     */
    public void setBody(Object body) {
        this.body=body;
    }

    /**
     * @see org.apache.tuscany.core.message.Message#setEndpointReference(org.apache.tuscany.core.client.runtime.addressing.sdo.EndpointReference)
     */
    public void setEndpointReference(EndpointReference endpointReference) {
        this.endpointReference=endpointReference;
    }

    /**
     * @see org.apache.tuscany.core.message.Message#setFaultTo(org.apache.tuscany.core.client.runtime.addressing.sdo.EndpointReference)
     */
    public void setFaultTo(EndpointReference faultTo) {
        this.faultTo=faultTo;
    }

    /**
     * @see org.apache.tuscany.core.message.Message#setFrom(org.apache.tuscany.core.client.runtime.addressing.sdo.EndpointReference)
     */
    public void setFrom(EndpointReference from) {
        this.from=from;
    }

    /**
     * @see org.apache.tuscany.core.message.Message#setMessageID(java.lang.String)
     */
    public void setMessageID(String messageID) {
        this.messageID=messageID;
    }

    /**
     * @see org.apache.tuscany.core.message.Message#setOperationName(java.lang.String)
     */
    public void setOperationName(String operationName) {
        this.operationName=operationName;
    }

    /**
     * @see org.apache.tuscany.core.message.Message#setRelatesTo(java.lang.String)
     */
    public void setRelatesTo(String relatesTo) {
        this.relatesTo=relatesTo;
    }

    /**
     * @see org.apache.tuscany.core.message.Message#setReplyTo(org.apache.tuscany.core.client.runtime.addressing.sdo.EndpointReference)
     */
    public void setReplyTo(EndpointReference replyTo) {
        this.replyTo=replyTo;
    }

    /**
     * @see org.apache.tuscany.core.message.Message#setTo(org.apache.tuscany.core.client.runtime.addressing.sdo.EndpointReference)
     */
    public void setTo(EndpointReference to) {
        this.to=to;
    }

    /**
     * @see org.apache.tuscany.core.message.Message#getCallbackChannel()
     */
    public MessageChannel getCallbackChannel() {
        return this;
    }

    /**
     * @see org.apache.tuscany.core.invocation.MessageChannel#send(org.apache.tuscany.core.message.Message)
     */
    public void send(Message message) {
        relatedCallbackMessage = message;
    }

    /**
     * @see org.apache.tuscany.core.message.Message#getRelatedCallbackMessage()
     */
    public Message getRelatedCallbackMessage() {
        return relatedCallbackMessage;
    }
    
    /**
     * @see org.apache.tuscany.core.message.Message#setTargetInvoker(org.apache.tuscany.core.invocation.TargetInvoker)
     */
    public void setTargetInvoker(TargetInvoker invoker){
        this.invoker = invoker;
    }

    /**
     * @see org.apache.tuscany.core.message.Message#getTargetInvoker()
     */
    public TargetInvoker getTargetInvoker(){
        return invoker;
    }
}
