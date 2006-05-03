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

import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.MessageChannel;
import org.apache.tuscany.spi.wire.Message;

/**
 * An interceptor that sends the wire Message down its request channel and does not expect a response.
 * 
 * @version $Rev$ $Date$
 */
public class OneWayInterceptor implements Interceptor {
    private MessageChannel requestChannel;

    /**
     * Construct an interceptor that sends messages down the supplied channel.
     * 
     * @param requestChannel the channel to send messages down
     */
    public OneWayInterceptor(MessageChannel requestChannel) {
        this.requestChannel = requestChannel;
    }

    public Message invoke(Message message) {
        requestChannel.send(message);
        return null;
    }

    public void setNext(Interceptor next) {
        throw new IllegalStateException("This interceptor must be the last one in an interceptor chain");
    }
}
