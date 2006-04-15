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

import org.apache.tuscany.core.wire.MessageChannel;
import org.apache.tuscany.core.wire.TargetInvoker;
import org.apache.tuscany.core.message.Message;

/**
 * The default implementation of an wire message
 *
 * @version $Rev $Date
 */
public class MessageImpl implements Message, MessageChannel {

    private Object body;
    private Message relatedCallbackMessage;
    private TargetInvoker invoker;

    protected MessageImpl() {
        super();
    }


    /**
     * @see org.apache.tuscany.core.message.Message#getBody()
     */
    public Object getBody() {
        return body;
    }

    /**
     * @see org.apache.tuscany.core.message.Message#setBody(java.lang.Object)
     */
    public void setBody(Object body) {
        this.body=body;
    }

    /**
     * @see org.apache.tuscany.core.message.Message#getCallbackChannel()
     */
    public MessageChannel getCallbackChannel() {
        return this;
    }

    /**
     * @see org.apache.tuscany.core.wire.MessageChannel#send(org.apache.tuscany.core.message.Message)
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
     * @see org.apache.tuscany.core.message.Message#setTargetInvoker(org.apache.tuscany.core.wire.TargetInvoker)
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
