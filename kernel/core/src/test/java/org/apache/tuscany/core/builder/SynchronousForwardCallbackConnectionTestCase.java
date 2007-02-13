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
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.WireService;

import junit.framework.TestCase;
import org.apache.tuscany.core.component.ComponentManager;
import org.apache.tuscany.core.component.ComponentManagerImpl;
import org.apache.tuscany.core.wire.InboundInvocationChainImpl;
import org.apache.tuscany.core.wire.InboundWireImpl;
import org.apache.tuscany.core.wire.OutboundInvocationChainImpl;
import org.apache.tuscany.core.wire.OutboundWireImpl;
import org.apache.tuscany.core.wire.jdk.JDKWireService;
import org.easymock.EasyMock;

/**
 * Verifies connections with synchronous forward and callback invocations
 *
 * @version $Rev$ $Date$
 */
public class SynchronousForwardCallbackConnectionTestCase extends TestCase {
    private Operation<Type> operation;
    private Operation<Type> callbackOperation;
    private ServiceContract<Type> contract;
    private ConnectorImpl connector;
    private ComponentManager componentManager;

    public void testSyncForwardAndCallbackAtomicToAtomic() throws Exception {
        URI targetUri = URI.create("target");
        URI targetUriFragment = URI.create("target#service");
        AtomicComponent target = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(target.getUri()).andReturn(targetUri).anyTimes();
        EasyMock.expect(target.createTargetInvoker(EasyMock.eq("service"),
            EasyMock.isA(Operation.class),
            EasyMock.isA(InboundWire.class))).andReturn(EasyMock.createNiceMock(TargetInvoker.class));
        EasyMock.replay(target);

        InboundWire inboundWire = new InboundWireImpl();
        inboundWire.setServiceContract(contract);
        inboundWire.setSourceUri(targetUriFragment);
        InboundInvocationChain inboundChain = new InboundInvocationChainImpl(operation);
        inboundChain.addInterceptor(new MockInterceptor());
        inboundWire.addInboundInvocationChain(operation, inboundChain);
        componentManager.register(target);

        AtomicComponent source = createSource();
        OutboundWire outboundWire = new OutboundWireImpl();
        outboundWire.setServiceContract(contract);
        outboundWire.setTargetUri(targetUriFragment);
        OutboundInvocationChain outboundChain = new OutboundInvocationChainImpl(operation);
        outboundWire.addOutboundInvocationChain(operation, outboundChain);

        InboundInvocationChain callbackInboundChain = new InboundInvocationChainImpl(callbackOperation);
        callbackInboundChain.addInterceptor(new MockInterceptor());
        outboundWire.addTargetCallbackInvocationChain(callbackOperation, callbackInboundChain);

        connector.connect(source, outboundWire, target, inboundWire, true);

        // test the forward request
        Message msg = new MessageImpl();
        msg.setBody("foo");
        Message ret = outboundChain.getHeadInterceptor().invoke(msg);
        assertEquals("foo", ret.getBody());

        // test the callback
        msg = new MessageImpl();
        msg.setBody("callback");
        Map<Operation<?>, OutboundInvocationChain> callbackChains =
            inboundWire.getSourceCallbackInvocationChains(URI.create("source"));
        OutboundInvocationChain callbackInvocationChain = callbackChains.get(callbackOperation);
        ret = callbackInvocationChain.getHeadInterceptor().invoke(msg);
        assertEquals("callback", ret.getBody());

        EasyMock.verify(source);
        EasyMock.verify(target);
    }

    public void testSyncForwardAndCallbackAtomicToReferenceBinding() throws Exception {
        URI targetUriFragment = URI.create("target#service");
        ReferenceBinding target = EasyMock.createMock(ReferenceBinding.class);
        EasyMock.expect(target.createTargetInvoker(EasyMock.isA(ServiceContract.class),
            EasyMock.isA(Operation.class))).andReturn(EasyMock.createNiceMock(TargetInvoker.class));
        EasyMock.replay(target);

        InboundWire inboundWire = new InboundWireImpl();
        inboundWire.setServiceContract(contract);
        InboundInvocationChain inboundChain = new InboundInvocationChainImpl(operation);
        inboundChain.addInterceptor(new MockInterceptor());
        inboundWire.addInboundInvocationChain(operation, inboundChain);
        inboundWire.setSourceUri(targetUriFragment);
        CompositeComponent parent = EasyMock.createMock(CompositeComponent.class);
        EasyMock.expect(parent.getTargetWire(EasyMock.eq("service"))).andReturn(inboundWire);

        AtomicComponent source = createSource();
        OutboundWire outboundWire = new OutboundWireImpl();
        outboundWire.setServiceContract(contract);
        OutboundInvocationChain outboundChain = new OutboundInvocationChainImpl(operation);
        outboundWire.addOutboundInvocationChain(operation, outboundChain);
        outboundWire.setTargetUri(targetUriFragment);

        InboundInvocationChain callbackInboundChain = new InboundInvocationChainImpl(callbackOperation);
        callbackInboundChain.addInterceptor(new MockInterceptor());
        outboundWire.addTargetCallbackInvocationChain(callbackOperation, callbackInboundChain);

        connector.connect(source, outboundWire, target, inboundWire, true);
        // test the forward request
        Message msg = new MessageImpl();
        msg.setBody("foo");
        Message ret = outboundChain.getHeadInterceptor().invoke(msg);
        assertEquals("foo", ret.getBody());

        // test the callback
        msg = new MessageImpl();
        msg.setBody("callback");
        Map<Operation<?>, OutboundInvocationChain> callbackChains =
            inboundWire.getSourceCallbackInvocationChains(URI.create("source"));
        OutboundInvocationChain callbackInvocationChain = callbackChains.get(callbackOperation);
        ret = callbackInvocationChain.getHeadInterceptor().invoke(msg);
        assertEquals("callback", ret.getBody());

        EasyMock.verify(source);
    }

    public void testSyncForwardAndCallbackReferenceBindingToServiceBinding() throws Exception {
        URI targetUriFragment = URI.create("target#service");
        ReferenceBinding source = EasyMock.createMock(ReferenceBinding.class);
        URI uri = URI.create("source");
        EasyMock.expect(source.getUri()).andReturn(uri).atLeastOnce();

        EasyMock.expect(source.createTargetInvoker(EasyMock.isA(ServiceContract.class),
            EasyMock.isA(Operation.class))).andReturn(EasyMock.createNiceMock(TargetInvoker.class));
        EasyMock.replay(source);

        ServiceBinding target = EasyMock.createMock(ServiceBinding.class);
        EasyMock.expect(target.createTargetInvoker(EasyMock.isA(ServiceContract.class),
            EasyMock.isA(Operation.class))).andReturn(EasyMock.createNiceMock(TargetInvoker.class));
        EasyMock.replay(target);

        InboundWire inboundWire = new InboundWireImpl();
        inboundWire.setServiceContract(contract);
        inboundWire.setSourceUri(targetUriFragment);
        InboundInvocationChain inboundChain = new InboundInvocationChainImpl(operation);
        inboundChain.addInterceptor(new MockInterceptor());
        inboundWire.addInboundInvocationChain(operation, inboundChain);

        CompositeComponent parent = EasyMock.createMock(CompositeComponent.class);
        EasyMock.expect(parent.getTargetWire(EasyMock.eq("service"))).andReturn(inboundWire);

        OutboundWire outboundWire = new OutboundWireImpl();
        outboundWire.setServiceContract(contract);
        outboundWire.setTargetUri(targetUriFragment);
        OutboundInvocationChain outboundChain = new OutboundInvocationChainImpl(operation);
        outboundWire.addOutboundInvocationChain(operation, outboundChain);

        InboundInvocationChain callbackInboundChain = new InboundInvocationChainImpl(callbackOperation);
        callbackInboundChain.addInterceptor(new MockInterceptor());
        outboundWire.addTargetCallbackInvocationChain(callbackOperation, callbackInboundChain);

        connector.connect(source, outboundWire, target, inboundWire, true);
        // test the forward request
        Message msg = new MessageImpl();
        msg.setBody("foo");
        Message ret = outboundChain.getHeadInterceptor().invoke(msg);
        assertEquals("foo", ret.getBody());

        // test the callback
        msg = new MessageImpl();
        msg.setBody("callback");
        Map<Operation<?>, OutboundInvocationChain> callbackChains =
            inboundWire.getSourceCallbackInvocationChains(URI.create("source"));
        OutboundInvocationChain callbackInvocationChain = callbackChains.get(callbackOperation);
        ret = callbackInvocationChain.getHeadInterceptor().invoke(msg);
        assertEquals("callback", ret.getBody());

        EasyMock.verify(source);
        EasyMock.verify(target);
    }

    protected void setUp() throws Exception {
        super.setUp();
        WireService wireService = new JDKWireService(null, null);
        componentManager = new ComponentManagerImpl();
        connector = new ConnectorImpl(wireService, null, componentManager, null, null);
        operation = new Operation<Type>("bar", null, null, null);
        callbackOperation = new Operation<Type>("callback", null, null, null);
        contract = new JavaServiceContract();
        Map<String, Operation<Type>> ops = new HashMap<String, Operation<Type>>();
        ops.put("callback", callbackOperation);
        contract.setCallbackOperations(ops);
    }

    private AtomicComponent createSource() throws Exception {
        AtomicComponent component = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(component.getUri()).andReturn(URI.create("source")).atLeastOnce();
        EasyMock.expect(component.createTargetInvoker(EasyMock.eq("callback"),
            EasyMock.isA(Operation.class),
            (InboundWire) EasyMock.isNull())).andReturn(EasyMock.createNiceMock(TargetInvoker.class));
        EasyMock.replay(component);
        return component;
    }

    private class MockInterceptor implements Interceptor {

        public Message invoke(Message msg) {
            return msg;
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
