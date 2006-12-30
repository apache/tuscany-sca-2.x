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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.InvocationRuntimeException;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;

import junit.framework.TestCase;
import org.apache.tuscany.core.implementation.composite.ServiceImpl;
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
public class ConnectorImplTestCase extends TestCase {
    private static final String FOO_SERVICE = "FooService";
    private static final QualifiedName FOO_TARGET = new QualifiedName("target/FooService");
    private static final String RESPONSE = "response";

    private ConnectorImpl connector;
    private ServiceContract contract;
    private Operation<Type> operation;

    public void testConnectReferenceWires() throws Exception {
        InboundInvocationChain inboundChain = new InboundInvocationChainImpl(operation);
        InboundWire inboundWire = new InboundWireImpl();
        inboundWire.setServiceContract(contract);
        inboundWire.addInvocationChain(operation, inboundChain);

        OutboundInvocationChain outboundChain = new OutboundInvocationChainImpl(operation);
        outboundChain.addInterceptor(new LoopbackInterceptor());
        OutboundWire outboundWire = new OutboundWireImpl();
        outboundWire.setServiceContract(contract);
        outboundWire.setTargetName(FOO_TARGET);
        outboundWire.addInvocationChain(operation, outboundChain);

        Reference reference = EasyMock.createMock(Reference.class);
        EasyMock.expect(reference.getParent()).andReturn(null);
        EasyMock.expect(reference.createTargetInvoker(contract, operation)).andReturn(null);
        EasyMock.expect(reference.getInboundWire()).andReturn(inboundWire);
        EasyMock.expect(reference.getOutboundWire()).andReturn(outboundWire);
        EasyMock.expect(reference.isSystem()).andReturn(false);
        EasyMock.replay(reference);

        inboundWire.setContainer(reference);
        outboundWire.setContainer(reference);
        connector.connect(reference);

        EasyMock.verify(reference);
        Interceptor interceptor = inboundChain.getHeadInterceptor();
        assertTrue(interceptor instanceof SynchronousBridgingInterceptor);
        Message resp = interceptor.invoke(new MessageImpl());
        assertEquals(RESPONSE, resp.getBody());

    }

    public void testConnectSynchronousServiceWiresToAtomicTarget() throws Exception {
        AtomicComponent target = createAtomicTarget();

        // create the parent composite
        CompositeComponent parent = EasyMock.createMock(CompositeComponent.class);
        EasyMock.expect(parent.getChild("target")).andReturn(target);
        EasyMock.replay(parent);

        InboundInvocationChain inboundChain = new InboundInvocationChainImpl(operation);
        InboundWire inboundWire = new InboundWireImpl();
        inboundWire.addInvocationChain(operation, inboundChain);
        inboundWire.setServiceContract(contract);

        OutboundInvocationChain outboundChain = new OutboundInvocationChainImpl(operation);
        OutboundWire outboundWire = new OutboundWireImpl();
        outboundWire.setTargetName(FOO_TARGET);
        outboundWire.addInvocationChain(operation, outboundChain);
        outboundWire.setServiceContract(contract);

        // create the binding
        ServiceBinding binding = EasyMock.createMock(ServiceBinding.class);
        EasyMock.expect(binding.getName()).andReturn("source");
        binding.setService(EasyMock.isA(Service.class));
        EasyMock.expect(binding.isSystem()).andReturn(false).atLeastOnce();
        EasyMock.expect(binding.getInboundWire()).andReturn(inboundWire).atLeastOnce();
        EasyMock.expect(binding.getOutboundWire()).andReturn(outboundWire);
        EasyMock.expect(binding.getScope()).andReturn(Scope.SYSTEM);
        EasyMock.replay(binding);

        outboundWire.setContainer(binding);
        inboundWire.setContainer(binding);

        Service service = new ServiceImpl("foo", parent, null);
        service.addServiceBinding(binding);

        connector.connect(service);
        MessageImpl msg = new MessageImpl();
        msg.setTargetInvoker(inboundChain.getTargetInvoker());
        Message resp = inboundChain.getHeadInterceptor().invoke(msg);
        assertEquals(RESPONSE, resp.getBody());
        EasyMock.verify(binding);
    }

    public void testConnectNonBlockingServiceWiresToAtomicTarget() throws Exception {
        // JFM FIXME
    }

    public void testConnectCallbackServiceWiresToAtomicTarget() throws Exception {
        // JFM FIXME
    }

    /**
     * Verifies connecting a wire from an atomic component to a target atomic component with one synchronous operation
     */
    public void testConnectAtomicComponentToAtomicComponentSyncWire() throws Exception {

        AtomicComponent target = createAtomicTarget();
        // create the parent composite
        CompositeComponent parent = EasyMock.createMock(CompositeComponent.class);
        EasyMock.expect(parent.getChild("target")).andReturn(target);
        EasyMock.replay(parent);
        AtomicComponent source = createAtomicSource(parent);
        connector.connect(source);

        MessageImpl msg = new MessageImpl();
        Map<String, List<OutboundWire>> wires = source.getOutboundWires();
        OutboundWire wire = wires.get(FOO_SERVICE).get(0);
        OutboundInvocationChain chain = wire.getInvocationChains().get(operation);
        msg.setTargetInvoker(chain.getTargetInvoker());
        Message resp = chain.getHeadInterceptor().invoke(msg);
        assertEquals(RESPONSE, resp.getBody());
    }

    public void testConnectInboundAtomicComponentWires() throws Exception {
        // create the inbound wire and chain
        InboundInvocationChain chain = new InboundInvocationChainImpl(operation);
        chain.addInterceptor(new InvokerInterceptor());
        InboundWire wire = new InboundWireImpl();
        wire.setServiceContract(contract);
        wire.addInvocationChain(operation, chain);
        wire.setServiceName(FOO_SERVICE);
        List<InboundWire> wires = new ArrayList<InboundWire>();
        wires.add(wire);

        AtomicComponent source = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(source.getParent()).andReturn(null);
        source.getOutboundWires();
        EasyMock.expectLastCall().andReturn(Collections.emptyMap());
        source.getInboundWires();
        EasyMock.expectLastCall().andReturn(wires);
        source.createTargetInvoker(EasyMock.eq(FOO_SERVICE), EasyMock.eq(operation), (InboundWire) EasyMock.isNull());
        EasyMock.expectLastCall().andReturn(new MockInvoker());
        EasyMock.replay(source);

        connector.connect(source);
        Message msg = new MessageImpl();
        msg.setTargetInvoker(chain.getTargetInvoker());
        Message resp = chain.getHeadInterceptor().invoke(msg);
        assertEquals(RESPONSE, resp.getBody());
    }

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
        InboundWire inboundWire = new InboundWireImpl();

        OutboundWire outboundWire = EasyMock.createMock(OutboundWire.class);
        outboundWire.getInvocationChains();
        EasyMock.expectLastCall().andReturn(Collections.emptyMap());
        outboundWire.setTargetWire(inboundWire);
        EasyMock.expect(outboundWire.getContainer()).andReturn(null);
        EasyMock.expect(outboundWire.getServiceContract()).andReturn(null);
        EasyMock.replay(outboundWire);

        connector.connect(outboundWire, inboundWire, true);
        EasyMock.verify(outboundWire);
    }

    public void testOutboundToInboundChainConnect() {
        TargetInvoker invoker = new MockInvoker();
        InboundInvocationChain inboundChain = new InboundInvocationChainImpl(operation);
        inboundChain.addInterceptor(new LoopbackInterceptor());

        OutboundInvocationChain outboundChain = new OutboundInvocationChainImpl(operation);

        connector.connect(outboundChain, inboundChain, invoker, false);
        Interceptor interceptor = outboundChain.getHeadInterceptor();
        assertTrue(interceptor instanceof SynchronousBridgingInterceptor);
        Message resp = interceptor.invoke(new MessageImpl());
        assertEquals(RESPONSE, resp.getBody());

    }

    public void testInboundToOutboundChainConnect() {
        InboundInvocationChain inboundChain = new InboundInvocationChainImpl(operation);

        OutboundInvocationChain outboundChain = new OutboundInvocationChainImpl(operation);
        outboundChain.addInterceptor(new LoopbackInterceptor());

        connector.connect(inboundChain, outboundChain);
        Interceptor interceptor = inboundChain.getHeadInterceptor();
        assertTrue(interceptor instanceof SynchronousBridgingInterceptor);
        Message resp = interceptor.invoke(new MessageImpl());
        assertEquals(RESPONSE, resp.getBody());
    }

    public void testOutboundWireToInboundReferenceTarget() throws Exception {
        AtomicComponent component = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(component.getName()).andReturn("foo");
        EasyMock.replay(component);

        Reference target = EasyMock.createMock(Reference.class);
        EasyMock.expect(target.createTargetInvoker(EasyMock.isA(ServiceContract.class), EasyMock.isA(Operation.class)))
            .andReturn(new MockInvoker());
        EasyMock.replay(target);

        InboundInvocationChain inboundChain = new InboundInvocationChainImpl(operation);
        inboundChain.addInterceptor(new LoopbackInterceptor());
        InboundWire targetWire = new InboundWireImpl();
        targetWire.setServiceContract(contract);
        targetWire.addInvocationChain(operation, inboundChain);
        targetWire.setContainer(target);

        // create the outbound wire and chain from the source component
        OutboundInvocationChain sourceChain = new OutboundInvocationChainImpl(operation);
        OutboundWire sourceWire = new OutboundWireImpl();
        sourceWire.setServiceContract(contract);
        sourceWire.setTargetName(FOO_TARGET);
        sourceWire.addInvocationChain(operation, sourceChain);
        sourceWire.setContainer(component);

        connector.connect(sourceWire, targetWire, false);
        Interceptor interceptor = sourceChain.getHeadInterceptor();
        assertTrue(interceptor instanceof SynchronousBridgingInterceptor);
        Message resp = interceptor.invoke(new MessageImpl());
        assertEquals(RESPONSE, resp.getBody());
    }

    protected void setUp() throws Exception {
        super.setUp();
        connector = new ConnectorImpl();
        contract = new JavaServiceContract(Foo.class);
        operation = new Operation<Type>("bar", null, null, null);
    }

    private interface Foo {
        String echo();
    }

    private AtomicComponent createAtomicTarget() throws Exception {
        InboundInvocationChain chain = new InboundInvocationChainImpl(operation);
        chain.addInterceptor(new InvokerInterceptor());
        InboundWire targetWire = new InboundWireImpl();
        targetWire.setServiceContract(contract);
        targetWire.addInvocationChain(operation, chain);

        // create the target
        AtomicComponent target = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(target.getScope()).andReturn(Scope.COMPOSITE);
        EasyMock.expect(target.isSystem()).andReturn(false).atLeastOnce();
        target.getInboundWire(EasyMock.eq(FOO_SERVICE));
        EasyMock.expectLastCall().andReturn(targetWire).atLeastOnce();
        target.createTargetInvoker(EasyMock.eq(FOO_SERVICE), EasyMock.eq(operation), EasyMock.eq(targetWire));
        MockInvoker mockInvoker = new MockInvoker();
        EasyMock.expectLastCall().andReturn(mockInvoker);
        EasyMock.replay(target);
        targetWire.setContainer(target);
        return target;
    }

    private AtomicComponent createAtomicSource(CompositeComponent parent) throws Exception {
        // create the outbound wire and chain from the source component
        OutboundInvocationChain outboundChain = new OutboundInvocationChainImpl(operation);

        OutboundWire outboundWire = new OutboundWireImpl();
        outboundWire.setTargetName(FOO_TARGET);
        outboundWire.setServiceContract(contract);
        outboundWire.addInvocationChain(operation, outboundChain);

        Map<String, List<OutboundWire>> outboundWires = new HashMap<String, List<OutboundWire>>();
        List<OutboundWire> list = new ArrayList<OutboundWire>();
        list.add(outboundWire);
        outboundWires.put(FOO_SERVICE, list);

        // create the source
        AtomicComponent source = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(source.isSystem()).andReturn(false).atLeastOnce();
        EasyMock.expect(source.getScope()).andReturn(Scope.COMPOSITE);
        EasyMock.expect(source.getParent()).andReturn(parent).atLeastOnce();
        EasyMock.expect(source.getOutboundWires()).andReturn(outboundWires).atLeastOnce();
        EasyMock.expect(source.getName()).andReturn("source").atLeastOnce();
        source.getInboundWires();
        EasyMock.expectLastCall().andReturn(Collections.emptyList());
        EasyMock.replay(source);

        outboundWire.setContainer(source);
        return source;
    }

    private static class MockInvoker implements TargetInvoker {
        public Object invokeTarget(final Object payload, final short sequence) throws InvocationTargetException {
            return null;
        }

        public Message invoke(Message msg) throws InvocationRuntimeException {
            Message resp = new MessageImpl();
            resp.setBody(RESPONSE);
            return resp;
        }

        public boolean isCacheable() {
            return false;
        }

        public void setCacheable(boolean cacheable) {

        }

        public boolean isOptimizable() {
            return false;
        }

        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }

    private static class LoopbackInterceptor implements Interceptor {
        public Message invoke(Message msg) {
            Message resp = new MessageImpl();
            resp.setBody(RESPONSE);
            return resp;
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
