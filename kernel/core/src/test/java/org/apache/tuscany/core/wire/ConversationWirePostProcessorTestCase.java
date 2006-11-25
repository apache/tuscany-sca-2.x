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

import java.lang.reflect.Type;

import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.model.InteractionScope;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class ConversationWirePostProcessorTestCase extends TestCase {
    private ConversationWirePostProcessor processor;

    public void testOutboundToInboundEndConversation() {
        OutboundWire wire = new OutboundWireImpl();
        ServiceContract contract = new MockServiceContract();
        contract.setInteractionScope(InteractionScope.CONVERSATIONAL);
        wire.setServiceContract(contract);
        Operation<Type> operation = new Operation<Type>("foo", null, null, null);
        operation.setConversationSequence(Operation.CONVERSATION_END);
        OutboundInvocationChain chain = new OutboundInvocationChainImpl(operation);
        wire.addInvocationChain(operation, chain);
        processor.process(wire, null);
        assertTrue(chain.getHeadInterceptor() instanceof ConversationEndInterceptor);
    }

    public void testOutboundToInboundContinueConversation() {
        OutboundWire wire = new OutboundWireImpl();
        ServiceContract contract = new MockServiceContract();
        contract.setInteractionScope(InteractionScope.CONVERSATIONAL);
        wire.setServiceContract(contract);
        Operation<Type> operation = new Operation<Type>("foo", null, null, null);
        operation.setConversationSequence(Operation.CONVERSATION_CONTINUE);
        OutboundInvocationChain chain = new OutboundInvocationChainImpl(operation);
        wire.addInvocationChain(operation, chain);
        processor.process(wire, null);
        assertTrue(chain.getHeadInterceptor() instanceof ConversationSequenceInterceptor);
    }

    public void testOutboundToInboundNoConversation() {
        OutboundWire wire = new OutboundWireImpl();
        ServiceContract contract = new MockServiceContract();
        contract.setInteractionScope(InteractionScope.NONCONVERSATIONAL);
        wire.setServiceContract(contract);
        Operation<Type> operation = new Operation<Type>("foo", null, null, null);
        operation.setConversationSequence(Operation.NO_CONVERSATION);
        OutboundInvocationChain chain = new OutboundInvocationChainImpl(operation);
        wire.addInvocationChain(operation, chain);
        processor.process(wire, null);
        assertNull(chain.getHeadInterceptor());
    }

    protected void setUp() throws Exception {
        super.setUp();
        processor = new ConversationWirePostProcessor();
    }

    private class MockServiceContract extends ServiceContract<Object> {

    }
}
