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
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageHandler;

/**
 * A message handler that dispatches the message through an interceptor stack and the uses the response
 * channel to return the result.
 *
 * @version $Rev$ $Date$
 */
public class MessageDispatcher implements MessageHandler {
    private final Interceptor head;

    /**
     * Construct a handler that dispatches messages to an Interceptor stack.
     *
     * @param head the interceptor at the head of the stack
     */
    public MessageDispatcher(Interceptor head) {
        this.head = head;
    }

    public boolean processMessage(Message msg) {
        Message resp = head.invoke(msg);
        msg.getCallbackChannel().send(resp);
        return false;
    }

    public boolean isOptimizable() {
        return true;
    }

}
