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

import java.util.List;

import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageChannel;
import org.apache.tuscany.spi.wire.MessageHandler;

/**
 * A channel comprising an ordered collection of message handlers.
 *
 * @version $Rev$ $Date$
 * @see org.apache.tuscany.spi.wire.Message
 */
public class MessageChannelImpl implements MessageChannel {

    private final List<MessageHandler> pipeline;

    /**
     * Construct a new channel comprising the supplied list of handlers.
     *
     * @param pipeline the Handlers in the channel
     */
    public MessageChannelImpl(List<MessageHandler> pipeline) {
        this.pipeline = pipeline;
    }

    /**
     * Send a message down the channel. The message will be processed by all handlers in order until one
     * returns false to indicate processing is complete or all handlers have been called.
     *
     * @param msg a Message to send down the channel
     */
    public void send(Message msg) {
        if (pipeline != null) {
            for (MessageHandler handler : pipeline) {
                if (!handler.processMessage(msg)) {
                    break;
                }
            }
        }
    }
}
