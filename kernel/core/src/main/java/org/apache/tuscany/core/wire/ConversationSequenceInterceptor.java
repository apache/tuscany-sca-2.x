/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tuscany.core.wire;

import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * An interceptor placed on the client side of an invocation chain to handle setting of conversational context.
 * Specifically, this interceptor tracks the conversational sequence, for example if the conversation has started or a
 * new one needs to be initiated.
 * <p/>
 * Note that this interceptor must be shared accross all invocation chains for a service reference but it may not be
 * shared accross client instances as it maintains state specific to the client.
 *
 * @version $Rev$ $Date$
 */
public class ConversationSequenceInterceptor implements Interceptor {
    private boolean conversationStarted;
    private Interceptor next;

    public Interceptor getNext() {
        return next;
    }

    public void setNext(Interceptor next) {
        this.next = next;
    }

    public boolean isOptimizable() {
        return false;
    }

    public Message invoke(Message msg) {
        if (conversationStarted) {
            msg.setConversationSequence(TargetInvoker.CONTINUE);
        } else {
            conversationStarted = true;
            msg.setConversationSequence(TargetInvoker.START);
        }
        return next.invoke(msg);
    }
}
