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

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.model.Operation;
import static org.apache.tuscany.spi.model.Operation.NO_CONVERSATION;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * Verifies wire optimization analysis
 *
 * @version $$Rev$$ $$Date$$
 */
public class WireOptimizationTestCase extends TestCase {
    private Operation operation;

    public void foo() {
    }

    public void testSourceWireInterceptorOptimization() throws Exception {
        AtomicComponent component = EasyMock.createNiceMock(AtomicComponent.class);
        EasyMock.replay(component);
        OutboundWire wire = new OutboundWireImpl();
        wire.setContainer(component);
        OutboundInvocationChain chain = new OutboundInvocationChainImpl(operation);
        chain.addInterceptor(new OptimizableInterceptor());
        wire.addInvocationChain(operation, chain);
        assertTrue(WireUtils.isOptimizable(wire));
    }

    public void testSourceWireNonInterceptorOptimization() throws Exception {
        AtomicComponent component = EasyMock.createNiceMock(AtomicComponent.class);
        EasyMock.replay(component);
        OutboundWire wire = new OutboundWireImpl();
        wire.setContainer(component);
        OutboundInvocationChain chain = new OutboundInvocationChainImpl(operation);
        chain.addInterceptor(new NonOptimizableInterceptor());
        wire.addInvocationChain(operation, chain);
        assertFalse(WireUtils.isOptimizable(wire));
    }

    public void testTargetWireInterceptorOptimization() throws Exception {
        AtomicComponent component = EasyMock.createNiceMock(AtomicComponent.class);
        EasyMock.expect(component.isOptimizable()).andReturn(true);
        EasyMock.replay(component);
        InboundWire wire = new InboundWireImpl();
        wire.setContainer(component);
        InboundInvocationChain chain = new InboundInvocationChainImpl(operation);
        chain.addInterceptor(new OptimizableInterceptor());
        wire.addInvocationChain(operation, chain);
        assertTrue(WireUtils.isOptimizable(wire));

    }

    public void testTargetWireNoOptimizationNonAtomicContainer() throws Exception {
        Component component = EasyMock.createNiceMock(Component.class);
        EasyMock.expect(component.isOptimizable()).andReturn(true);
        EasyMock.replay(component);
        InboundWire wire = new InboundWireImpl();
        wire.setContainer(component);
        InboundInvocationChain chain = new InboundInvocationChainImpl(operation);
        chain.addInterceptor(new OptimizableInterceptor());
        wire.addInvocationChain(operation, chain);
        assertTrue(WireUtils.isOptimizable(wire));
    }

    public void testTargetWireNonInterceptorOptimization() throws Exception {
        AtomicComponent component = EasyMock.createNiceMock(AtomicComponent.class);
        EasyMock.replay(component);
        InboundWire wire = new InboundWireImpl();
        wire.setContainer(component);
        InboundInvocationChain chain = new InboundInvocationChainImpl(operation);
        chain.addInterceptor(new NonOptimizableInterceptor());
        wire.addInvocationChain(operation, chain);
        assertFalse(WireUtils.isOptimizable(wire));
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    protected void setUp() throws Exception {
        super.setUp();
        operation = new Operation<Type>("foo", null, null, null, false, null, NO_CONVERSATION);

    }

    private class OptimizableInterceptor implements Interceptor {

        public Message invoke(Message msg) {
            return null;
        }

        public void setNext(Interceptor next) {

        }

        public Interceptor getNext() {
            return null;
        }

        public boolean isOptimizable() {
            return true;
        }
    }

    private class NonOptimizableInterceptor implements Interceptor {

        public Message invoke(Message msg) {
            return null;
        }

        public void setNext(Interceptor next) {

        }

        public Interceptor getNext() {
            return null;
        }

        public boolean isOptimizable() {
            return false;
        }
    }

}
