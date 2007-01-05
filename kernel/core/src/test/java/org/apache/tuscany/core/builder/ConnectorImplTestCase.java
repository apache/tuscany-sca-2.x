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
package org.apache.tuscany.core.builder;

import java.lang.reflect.Type;
import java.util.Collections;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;

import org.apache.tuscany.core.wire.InboundInvocationChainImpl;
import org.apache.tuscany.core.wire.InboundWireImpl;
import org.apache.tuscany.core.wire.InvokerInterceptor;
import org.apache.tuscany.core.wire.OutboundInvocationChainImpl;
import org.apache.tuscany.core.wire.OutboundWireImpl;
import org.apache.tuscany.core.wire.SynchronousBridgingInterceptor;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class ConnectorImplTestCase extends AbstractConnectorImplTestCase {

    public void testConnectTargetNotFound() throws Exception {
        CompositeComponent parent = EasyMock.createMock(CompositeComponent.class);
        EasyMock.expect(parent.getName()).andReturn("parent");
        parent.getChild(EasyMock.isA(String.class));
        EasyMock.expectLastCall().andReturn(null);
        EasyMock.replay(parent);
        try {
            AtomicComponent source = createAtomicSource(parent);
            connector.connect(source);
            fail();
        } catch (TargetServiceNotFoundException e) {
            // expected
        }
    }

    public void testOutboundToInboundOptimization() throws Exception {
        AtomicComponent container = EasyMock.createNiceMock(AtomicComponent.class);
        EasyMock.expect(container.isSystem()).andReturn(true);
        EasyMock.replay(container);
        InboundWire inboundWire = new InboundWireImpl();
        inboundWire.setContainer(container);
        OutboundWire outboundWire = EasyMock.createMock(OutboundWire.class);
        outboundWire.setTargetWire(EasyMock.eq(inboundWire));
        EasyMock.expect(outboundWire.getContainer()).andReturn(container).atLeastOnce();
        EasyMock.replay(outboundWire);

        connector.connect(outboundWire, inboundWire, true);
        EasyMock.verify(outboundWire);
    }

    /**
     * Verifies that stateless targets with a destructor are not optimized as the destructor callback event must be
     * issued by the TargetInvoker after it dispatches to the target
     */
    public void testOutboundToInboundNoOptimizationBecauseStatelessDestructor() throws Exception {
        AtomicComponent container = EasyMock.createNiceMock(AtomicComponent.class);
        EasyMock.expect(container.isSystem()).andReturn(false);
        EasyMock.expect(container.getScope()).andReturn(Scope.STATELESS);
        EasyMock.expect(container.isDestroyable()).andReturn(true);
        EasyMock.replay(container);
        InboundWire inboundWire = new InboundWireImpl();
        inboundWire.setContainer(container);
        OutboundWire outboundWire = EasyMock.createMock(OutboundWire.class);
        EasyMock.expect(outboundWire.getServiceContract()).andReturn(contract);
        outboundWire.getInvocationChains();
        EasyMock.expectLastCall().andReturn(Collections.emptyMap()).atLeastOnce();
        outboundWire.getTargetCallbackInvocationChains();
        EasyMock.expectLastCall().andReturn(Collections.emptyMap()).atLeastOnce();
        EasyMock.expect(outboundWire.getContainer()).andReturn(container).atLeastOnce();
        EasyMock.replay(outboundWire);

        connector.connect(outboundWire, inboundWire, true);
        EasyMock.verify(outboundWire);
    }

    public void testOutboundToInboundNoOptimizationStatelessWithNoDestructor() throws Exception {
        AtomicComponent container = EasyMock.createNiceMock(AtomicComponent.class);
        EasyMock.expect(container.isSystem()).andReturn(false);
        EasyMock.expect(container.getScope()).andReturn(Scope.STATELESS);
        EasyMock.expect(container.isDestroyable()).andReturn(false);
        EasyMock.replay(container);
        InboundWire inboundWire = new InboundWireImpl();
        inboundWire.setContainer(container);
        OutboundWire outboundWire = EasyMock.createMock(OutboundWire.class);
        outboundWire.setTargetWire(EasyMock.eq(inboundWire));
        outboundWire.setOptimizable(EasyMock.eq(true));
        EasyMock.expect(outboundWire.getServiceContract()).andReturn(contract);
        EasyMock.expect(outboundWire.getContainer()).andReturn(container).atLeastOnce();
        outboundWire.getInvocationChains();
        EasyMock.expectLastCall().andReturn(Collections.emptyMap()).atLeastOnce();
        outboundWire.getTargetCallbackInvocationChains();
        EasyMock.expectLastCall().andReturn(Collections.emptyMap()).atLeastOnce();
        EasyMock.replay(outboundWire);

        connector.connect(outboundWire, inboundWire, true);
        EasyMock.verify(outboundWire);
    }

    public void testOutboundToInboundNoOptimizationNonAtomicTarget() throws Exception {
        ReferenceBinding container = EasyMock.createNiceMock(ReferenceBinding.class);
        EasyMock.expect(container.isSystem()).andReturn(false);
        EasyMock.replay(container);
        InboundWire inboundWire = new InboundWireImpl();
        inboundWire.setContainer(container);
        OutboundWire outboundWire = EasyMock.createMock(OutboundWire.class);
        EasyMock.expect(outboundWire.getServiceContract()).andReturn(contract);
        outboundWire.getInvocationChains();
        EasyMock.expectLastCall().andReturn(Collections.emptyMap()).atLeastOnce();
        outboundWire.getTargetCallbackInvocationChains();
        EasyMock.expectLastCall().andReturn(Collections.emptyMap()).atLeastOnce();
        EasyMock.expect(outboundWire.getContainer()).andReturn(container).atLeastOnce();
        EasyMock.replay(outboundWire);

        connector.connect(outboundWire, inboundWire, true);
        EasyMock.verify(outboundWire);
    }

    public void testOutboundToInboundChainConnect() throws Exception {
        TargetInvoker invoker = new MockInvoker();
        InboundInvocationChain inboundChain = new InboundInvocationChainImpl(operation);
        inboundChain.addInterceptor(new InvokerInterceptor());
        OutboundInvocationChain outboundChain = new OutboundInvocationChainImpl(operation);
        connector.connect(outboundChain, inboundChain, invoker, false);
        Interceptor interceptor = outboundChain.getHeadInterceptor();
        assertTrue(interceptor instanceof SynchronousBridgingInterceptor);
        MessageImpl msg = new MessageImpl();
        msg.setTargetInvoker(new MockInvoker());
        Message resp = interceptor.invoke(msg);
        assertEquals(RESPONSE, resp.getBody());
    }

    public void testOutboundToInboundChainConnectNoInboundInterceptor() {
        TargetInvoker invoker = new MockInvoker();
        InboundInvocationChain inboundChain = new InboundInvocationChainImpl(operation);
        OutboundInvocationChain outboundChain = new OutboundInvocationChainImpl(operation);
        try {
            connector.connect(outboundChain, inboundChain, invoker, false);
            fail();
        } catch (WireConnectException e) {
            // expected
        }
    }

    public void testInboundToOutboundChainConnect() throws Exception {
        InboundInvocationChain inboundChain = new InboundInvocationChainImpl(operation);
        OutboundInvocationChain outboundChain = new OutboundInvocationChainImpl(operation);
        outboundChain.addInterceptor(new InvokerInterceptor());
        connector.connect(inboundChain, outboundChain);
        Interceptor interceptor = inboundChain.getHeadInterceptor();
        assertTrue(interceptor instanceof SynchronousBridgingInterceptor);
        MessageImpl msg = new MessageImpl();
        msg.setTargetInvoker(new MockInvoker());
        Message resp = interceptor.invoke(msg);
        assertEquals(RESPONSE, resp.getBody());
    }

    public void testInboundToOutboundChainConnectNoOutboundInterceptors() throws Exception {
        InboundInvocationChain inboundChain = new InboundInvocationChainImpl(operation);
        OutboundInvocationChain outboundChain = new OutboundInvocationChainImpl(operation);
        try {
            connector.connect(inboundChain, outboundChain);
            fail();
        } catch (WireConnectException e) {
            // expected
        }
    }

    public void testInboundOutboundSystemWireOptimization() throws Exception {
        SCAObject container = EasyMock.createMock(SCAObject.class);
        EasyMock.expect(container.isSystem()).andReturn(true);
        EasyMock.replay(container);
        InboundWire inboundWire = EasyMock.createMock(InboundWire.class);
        inboundWire.setTargetWire(EasyMock.isA(OutboundWire.class));
        EasyMock.expect(inboundWire.getContainer()).andReturn(container).atLeastOnce();
        EasyMock.replay(inboundWire);
        OutboundWire outboundWire = new OutboundWireImpl();
        outboundWire.setContainer(container);
        connector.connect(inboundWire, outboundWire, true);
        EasyMock.verify(inboundWire);
        EasyMock.verify(container);
    }

    public void testOutboundInboundSystemWireOptimization() throws Exception {
        SCAObject container = EasyMock.createMock(SCAObject.class);
        EasyMock.expect(container.isSystem()).andReturn(true);
        EasyMock.replay(container);
        OutboundWire outboundWire = EasyMock.createMock(OutboundWire.class);
        outboundWire.setTargetWire(EasyMock.isA(InboundWire.class));
        EasyMock.expect(outboundWire.getContainer()).andReturn(container).atLeastOnce();
        EasyMock.replay(outboundWire);
        InboundWire inboundWire = new InboundWireImpl();
        inboundWire.setContainer(container);
        connector.connect(outboundWire, inboundWire, true);
        EasyMock.verify(outboundWire);
        EasyMock.verify(container);
    }

    public void testIncompatibleInboundOutboundWiresConnect() throws Exception {
        Operation<Type> operation = new Operation<Type>("bar", null, null, null);
        InboundWire inboundWire = new InboundWireImpl();
        inboundWire.addInvocationChain(operation, new InboundInvocationChainImpl(operation));
        OutboundWire outboundWire = new OutboundWireImpl();
        try {
            connector.connect(inboundWire, outboundWire, false);
            fail();
        } catch (IncompatibleInterfacesException e) {
            // expected
        }

    }

    public void testIncompatibleOutboundInboundWiresConnect() throws Exception {
        SCAObject container = EasyMock.createNiceMock(SCAObject.class);
        EasyMock.expect(container.isSystem()).andReturn(false);
        EasyMock.replay(container);
        Operation<Type> operation = new Operation<Type>("bar", null, null, null);
        InboundWire inboundWire = new InboundWireImpl();
        inboundWire.setContainer(container);
        OutboundWire outboundWire = new OutboundWireImpl();
        outboundWire.setContainer(container);
        outboundWire.addInvocationChain(operation, new OutboundInvocationChainImpl(operation));
        try {
            connector.connect(outboundWire, inboundWire, false);
            fail();
        } catch (IncompatibleInterfacesException e) {
            // expected
        }

    }

    public void testIsOptimizable() {
        assertTrue(connector.isOptimizable(Scope.STATELESS, Scope.STATELESS));
        assertTrue(connector.isOptimizable(Scope.STATELESS, Scope.COMPOSITE));
        assertFalse(connector.isOptimizable(Scope.STATELESS, Scope.CONVERSATION));
        assertTrue(connector.isOptimizable(Scope.STATELESS, Scope.REQUEST));
        assertTrue(connector.isOptimizable(Scope.STATELESS, Scope.SESSION));
        assertTrue(connector.isOptimizable(Scope.STATELESS, Scope.SYSTEM));

        assertTrue(connector.isOptimizable(Scope.COMPOSITE, Scope.COMPOSITE));
        assertFalse(connector.isOptimizable(Scope.COMPOSITE, Scope.CONVERSATION));
        assertFalse(connector.isOptimizable(Scope.COMPOSITE, Scope.REQUEST));
        assertFalse(connector.isOptimizable(Scope.COMPOSITE, Scope.SESSION));
        assertFalse(connector.isOptimizable(Scope.COMPOSITE, Scope.STATELESS));
        assertTrue(connector.isOptimizable(Scope.COMPOSITE, Scope.SYSTEM));

        assertFalse(connector.isOptimizable(Scope.CONVERSATION, Scope.COMPOSITE));
        assertFalse(connector.isOptimizable(Scope.CONVERSATION, Scope.CONVERSATION));
        assertFalse(connector.isOptimizable(Scope.CONVERSATION, Scope.REQUEST));
        assertFalse(connector.isOptimizable(Scope.CONVERSATION, Scope.SESSION));
        assertFalse(connector.isOptimizable(Scope.CONVERSATION, Scope.STATELESS));
        assertFalse(connector.isOptimizable(Scope.CONVERSATION, Scope.SYSTEM));

        assertTrue(connector.isOptimizable(Scope.REQUEST, Scope.COMPOSITE));
        assertFalse(connector.isOptimizable(Scope.REQUEST, Scope.CONVERSATION));
        assertTrue(connector.isOptimizable(Scope.REQUEST, Scope.REQUEST));
        assertTrue(connector.isOptimizable(Scope.REQUEST, Scope.SESSION));
        assertFalse(connector.isOptimizable(Scope.REQUEST, Scope.STATELESS));
        assertTrue(connector.isOptimizable(Scope.REQUEST, Scope.SYSTEM));

        assertTrue(connector.isOptimizable(Scope.SESSION, Scope.COMPOSITE));
        assertFalse(connector.isOptimizable(Scope.SESSION, Scope.CONVERSATION));
        assertFalse(connector.isOptimizable(Scope.SESSION, Scope.REQUEST));
        assertTrue(connector.isOptimizable(Scope.SESSION, Scope.SESSION));
        assertFalse(connector.isOptimizable(Scope.SESSION, Scope.STATELESS));
        assertTrue(connector.isOptimizable(Scope.SESSION, Scope.SYSTEM));

        assertTrue(connector.isOptimizable(Scope.SYSTEM, Scope.COMPOSITE));
        assertFalse(connector.isOptimizable(Scope.SYSTEM, Scope.CONVERSATION));
        assertFalse(connector.isOptimizable(Scope.SYSTEM, Scope.REQUEST));
        assertFalse(connector.isOptimizable(Scope.SYSTEM, Scope.SESSION));
        assertFalse(connector.isOptimizable(Scope.SYSTEM, Scope.STATELESS));
        assertTrue(connector.isOptimizable(Scope.SYSTEM, Scope.SYSTEM));

        assertFalse(connector.isOptimizable(Scope.UNDEFINED, Scope.COMPOSITE));
        assertFalse(connector.isOptimizable(Scope.UNDEFINED, Scope.CONVERSATION));
        assertFalse(connector.isOptimizable(Scope.UNDEFINED, Scope.REQUEST));
        assertFalse(connector.isOptimizable(Scope.UNDEFINED, Scope.SESSION));
        assertFalse(connector.isOptimizable(Scope.UNDEFINED, Scope.STATELESS));
        assertFalse(connector.isOptimizable(Scope.UNDEFINED, Scope.SYSTEM));

    }

    public void testInvalidConnectObject() throws Exception {
        try {
            connector.connect(EasyMock.createNiceMock(SCAObject.class));
            fail();
        } catch (AssertionError e) {
            // expected
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

}
