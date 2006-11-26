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

import java.util.Map;

import org.apache.tuscany.spi.model.InteractionScope;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.WirePostProcessorExtension;

/**
 * Adds conversation-related interceptors to service invocations chains. A {@link ConversationEndInterceptor} is
 * inserted on a chain for an operation marked as ending conversation. A {@link ConversationSequenceInterceptor} is
 * inserted into all other invocation chains.
 *
 * @version $Rev$ $Date$
 */
public class ConversationWirePostProcessor extends WirePostProcessorExtension {

    public void process(OutboundWire source, InboundWire target) {
        if (!InteractionScope.CONVERSATIONAL.equals(source.getServiceContract().getInteractionScope())) {
            return;
        }
        // the sequence interceptor is shared across all chains for a service
        ConversationSequenceInterceptor sequenceInterceptor = new ConversationSequenceInterceptor();
        for (Map.Entry<Operation<?>, OutboundInvocationChain> entry : source.getInvocationChains().entrySet()) {
            int sequence = entry.getKey().getConversationSequence();
            if (sequence == Operation.CONVERSATION_END) {
                entry.getValue().addInterceptor(0, new ConversationEndInterceptor());
            } else if (sequence == Operation.CONVERSATION_CONTINUE) {
                entry.getValue().addInterceptor(0, sequenceInterceptor);
            }
        }
    }

    public void process(InboundWire source, OutboundWire target) {
        // do nothing
    }
}
