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
package org.apache.tuscany.core.invocation.impl;

import org.apache.tuscany.core.invocation.Interceptor;
import org.apache.tuscany.core.message.Message;
import org.apache.tuscany.core.message.channel.MessageChannel;

/**
 * An interceptor that first sends the invocation Message down its request channel then extracts the response from the message and
 * sends it down the response channel before returning it up the interceptor stack.
 * 
 * @version $Rev$ $Date$
 */
public class RequestResponseInterceptor implements Interceptor {
    private MessageChannel requestChannel;

    private MessageChannel responseChannel;

    /**
     * Construct an interceptor that sends messages down the supplied channels.
     * 
     * @param requestChannel the channel to send request messages down
     * @param responseChannel the channel to sent response messages down
     */
    public RequestResponseInterceptor(MessageChannel requestChannel, MessageChannel responseChannel) {
        this.requestChannel = requestChannel;
        this.responseChannel = responseChannel;
    }

    public Message invoke(Message requestMessage) {
        requestChannel.send(requestMessage);
        Message responseMessage = requestMessage.getRelatedCallbackMessage();
        responseChannel.send(responseMessage);
        return responseMessage;
    }

    public void setNext(Interceptor next) {
        throw new IllegalStateException("This interceptor must be the last one in an interceptor chain");
    }
}
