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
package org.apache.tuscany.core.wire.impl;

import org.apache.tuscany.spi.wire.MessageChannel;
import org.apache.tuscany.spi.wire.MessageHandler;
import org.apache.tuscany.spi.wire.Message;

import java.util.List;

/**
 * A channel comprising an ordered collection of message handlers.
 *
 *@see org.apache.tuscany.spi.wire.Message
 * @version $Rev$ $Date$
 */
public class MessageChannelImpl implements MessageChannel {

    private final List<MessageHandler> pipeline;

    //----------------------------------
    // Constructors
    //----------------------------------

    /**
     * Construct a new channel comprising the supplied list of handlers.
     *
     * @param pipeline the Handlers in the channel
     */
    public MessageChannelImpl(List<MessageHandler> pipeline) {
        this.pipeline = pipeline;
    }

    //----------------------------------
    // Methods
    //----------------------------------

    /**
     * Send a message down the channel. The message will be processed by all handlers
     * in order until one returns false to indicate processing is complete or all
     * handlers have been called.
     *
     * @param msg a Message to send down the channel
     */
    public void send(Message msg) {
        if (pipeline!=null) {
            for (MessageHandler handler : pipeline) {
                if (!handler.processMessage(msg)) {
                    break;
                }
            }
        }
    }
}
