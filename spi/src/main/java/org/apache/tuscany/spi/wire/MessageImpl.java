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
package org.apache.tuscany.spi.wire;

/**
 * The default implementation of a message flowed through a wire during an invocation
 *
 * @version $Rev $Date
 */
public class MessageImpl implements Message, MessageChannel {

    private Object body;
    private Message relatedCallbackMessage;
    private TargetInvoker invoker;

    public MessageImpl() {
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public MessageChannel getCallbackChannel() {
        return this;
    }

    public void send(Message message) {
        relatedCallbackMessage = message;
    }

    public Message getRelatedCallbackMessage() {
        return relatedCallbackMessage;
    }

    public void setTargetInvoker(TargetInvoker invoker) {
        this.invoker = invoker;
    }

    public TargetInvoker getTargetInvoker() {
        return invoker;
    }
}
