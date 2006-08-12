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

import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageHandler;

/**
 * Bridges between handlers in two {@link org.apache.tuscany.spi.wire.InboundInvocationChain}s
 *
 * @version $$Rev$$ $$Date$$
 */
public class BridgingHandler implements MessageHandler {
    private MessageHandler next;

    public BridgingHandler(MessageHandler next) {
        this.next = next;
    }

    public BridgingHandler() {
    }

    public boolean processMessage(Message message) {
        return next.processMessage(message);
    }

    public boolean isOptimizable() {
        return true;
    }

    public void setNext(MessageHandler next) {
        this.next = next;
    }

    public MessageHandler getNext() {
        return next;
    }
}
