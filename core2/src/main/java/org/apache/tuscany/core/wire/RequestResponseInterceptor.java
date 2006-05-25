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
package org.apache.tuscany.core.wire;

import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.MessageChannel;
import org.apache.tuscany.spi.wire.Message;

/**
 * An interceptor that first sends a message down its request channel then extracts the response from the
 * message and sends it down the response channel before returning it up the interceptor stack.
 *
 * @version $Rev$ $Date$
 */
public class RequestResponseInterceptor implements Interceptor {

    private MessageChannel sourceRequestChannel;

    private MessageChannel sourceResponseChannel;

    private MessageChannel targetRequestChannel;

    private MessageChannel targetResponseChannel;

    /**
     * Creates an interceptor that sends messages down the supplied channels
     *
     * @param targetRequestChannel  the channel to send request messages down
     * @param targetResponseChannel the channel to sent response messages down
     */
    public RequestResponseInterceptor(MessageChannel sourceRequestChannel, MessageChannel targetRequestChannel,
                                      MessageChannel sourceResponseChannel, MessageChannel targetResponseChannel) {
        this.sourceRequestChannel = sourceRequestChannel;
        this.sourceResponseChannel = sourceResponseChannel;
        this.targetRequestChannel = targetRequestChannel;
        this.targetResponseChannel = targetResponseChannel;
    }

    public Message invoke(Message requestMessage) {
        if (sourceRequestChannel != null) {
            sourceRequestChannel.send(requestMessage);
        }
        if (targetRequestChannel != null) {
            targetRequestChannel.send(requestMessage);
        }
        Message responseMessage = requestMessage.getRelatedCallbackMessage();
        if (targetResponseChannel != null) {
            targetResponseChannel.send(responseMessage);
        }
        if (sourceResponseChannel != null) {
            sourceResponseChannel.send(responseMessage);
        }
        return responseMessage;
    }

    public void setNext(Interceptor next) {
        throw new IllegalStateException("This interceptor must be the last one in an interceptor chain");
    }

    public Interceptor getNext() {
        return null;
    }

    public boolean isOptimizable() {
        return true;
    }

}
