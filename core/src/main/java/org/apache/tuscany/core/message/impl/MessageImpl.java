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

import java.util.Map;

import org.apache.tuscany.core.addressing.EndpointReference;
import org.apache.tuscany.core.addressing.sdo.EndpointReferenceElement;
import org.apache.tuscany.core.invocation.TargetInvoker;
import org.apache.tuscany.core.message.Message;
import org.apache.tuscany.core.message.channel.MessageChannel;
import org.apache.tuscany.core.message.sdo.impl.MessageElementImpl;

/**
 */
public class MessageImpl extends MessageElementImpl implements Message, MessageChannel {

    private Message relatedCallbackMessage;

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
        return getHeaderFields().getAction();
    }

    /**
     * @see org.apache.tuscany.core.message.Message#getBody()
     */
    public Object getBody() {
        return super.getBody();
    }

    /**
     * @see org.apache.tuscany.core.message.Message#getPayload()
     */
    public Object getPayload() {
        return super.getBody();
    }

    /**
     * @see org.apache.tuscany.core.message.Message#getEndpointReference()
     */
    public EndpointReference getEndpointReference() {
        return (EndpointReference) getHeaderFields().getEndpointReference();
    }

    /**
     * @see org.apache.tuscany.core.message.Message#getFaultTo()
     */
    public EndpointReference getFaultTo() {
        return (EndpointReference) getHeaderFields().getFaultTo();
    }

    /**
     * @see org.apache.tuscany.core.message.Message#getFrom()
     */
    public EndpointReference getFrom() {
        return (EndpointReference) getHeaderFields().getFrom();
    }

    /**
     * @see org.apache.tuscany.core.message.Message#getHeaders()
     */
    public Map<String, Object> getHeaders() {
        return super.getHeaders();
    }

    /**
     * @see org.apache.tuscany.core.message.Message#getMessageID()
     */
    public String getMessageID() {
        return getHeaderFields().getMessageID();
    }

    /**
     * @see org.apache.tuscany.core.message.Message#getOperationName()
     */
    public String getOperationName() {
        return super.getOperationName();
    }

    /**
     * @see org.apache.tuscany.core.message.Message#getRelatesTo()
     */
    public String getRelatesTo() {
        return getHeaderFields().getRelatesTo();
    }

    /**
     * @see org.apache.tuscany.core.message.Message#getReplyTo()
     */
    public EndpointReference getReplyTo() {
        return (EndpointReference) getHeaderFields().getReplyTo();
    }

    /**
     * @see org.apache.tuscany.core.message.Message#getTo()
     */
    public EndpointReference getTo() {
        return (EndpointReference) getHeaderFields().getTo();
    }

    /**
     * @see org.apache.tuscany.core.message.Message#isRequest()
     */
    public boolean isRequest() {
        return getHeaderFields().getRelatesTo() == null;
    }

    /**
     * @see org.apache.tuscany.core.message.Message#isResponse()
     */
    public boolean isResponse() {
        return getHeaderFields().getRelatesTo() != null;
    }

    /**
     * @see org.apache.tuscany.core.message.Message#setAction(java.lang.String)
     */
    public void setAction(String action) {
        getHeaderFields().setAction(action);
    }

    /**
     * @see org.apache.tuscany.core.message.Message#setBody(java.lang.Object)
     */
    public void setBody(Object body) {
        super.setBody(body);
    }

    /**
     * @see org.apache.tuscany.core.message.Message#setPayload(java.lang.Object)
     */
    public void setPayload(Object body) {
        super.setBody(body);
    }

    /**
     * @see org.apache.tuscany.core.message.Message#setEndpointReference(org.apache.tuscany.core.client.runtime.addressing.sdo.EndpointReference)
     */
    public void setEndpointReference(EndpointReference endpointReference) {
        getHeaderFields().setEndpointReference((EndpointReferenceElement) endpointReference);
    }

    /**
     * @see org.apache.tuscany.core.message.Message#setFaultTo(org.apache.tuscany.core.client.runtime.addressing.sdo.EndpointReference)
     */
    public void setFaultTo(EndpointReference faultTo) {
        getHeaderFields().setFaultTo((EndpointReferenceElement) faultTo);
    }

    /**
     * @see org.apache.tuscany.core.message.Message#setFrom(org.apache.tuscany.core.client.runtime.addressing.sdo.EndpointReference)
     */
    public void setFrom(EndpointReference from) {
        getHeaderFields().setFrom((EndpointReferenceElement) from);
    }

    /**
     * @see org.apache.tuscany.core.message.Message#setMessageID(java.lang.String)
     */
    public void setMessageID(String messageID) {
        getHeaderFields().setMessageID(messageID);
    }

    /**
     * @see org.apache.tuscany.core.message.Message#setOperationName(java.lang.String)
     */
    public void setOperationName(String operationName) {
        super.setOperationName(operationName);
    }

    /**
     * @see org.apache.tuscany.core.message.Message#setRelatesTo(java.lang.String)
     */
    public void setRelatesTo(String relatesTo) {
        getHeaderFields().setRelatesTo(relatesTo);
    }

    /**
     * @see org.apache.tuscany.core.message.Message#setReplyTo(org.apache.tuscany.core.client.runtime.addressing.sdo.EndpointReference)
     */
    public void setReplyTo(EndpointReference replyTo) {
        getHeaderFields().setReplyTo((EndpointReferenceElement) replyTo);
    }

    /**
     * @see org.apache.tuscany.core.message.Message#setTo(org.apache.tuscany.core.client.runtime.addressing.sdo.EndpointReference)
     */
    public void setTo(EndpointReference to) {
        getHeaderFields().setTo((EndpointReferenceElement) to);
    }

    /**
     * @see org.apache.tuscany.core.message.Message#getCallbackChannel()
     */
    public MessageChannel getCallbackChannel() {
        return this;
    }

    /**
     * @see org.apache.tuscany.core.message.channel.MessageChannel#send(org.apache.tuscany.core.message.Message)
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
    
    private TargetInvoker invoker;
    
    public void setTargetInvoker(TargetInvoker invoker){
        this.invoker = invoker;
    }

    public TargetInvoker getTargetInvoker(){
        return invoker;
    }
}
