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

import org.apache.tuscany.spi.idl.InvalidServiceContractException;
import org.apache.tuscany.spi.idl.java.JavaInterfaceProcessorRegistry;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.TargetInvoker;

import junit.framework.TestCase;
import org.apache.tuscany.core.idl.java.JavaInterfaceProcessorRegistryImpl;
import org.apache.tuscany.core.mock.component.SimpleTarget;
import org.apache.tuscany.core.mock.wire.MockSyncInterceptor;
import org.apache.tuscany.core.wire.InboundInvocationChainImpl;
import org.apache.tuscany.core.wire.InvokerInterceptor;
import org.apache.tuscany.core.wire.OutboundInvocationChainImpl;
import org.easymock.EasyMock;

/**
 * Verifies connection strategies between {@link org.apache.tuscany.spi.wire.OutboundInvocationChain}s and {@link
 * org.apache.tuscany.spi.wire.InboundInvocationChain}s
 *
 * @version $$Rev$$ $$Date$$
 */
public class InboundtoOutboundConnectTestCase extends TestCase {
    private Operation operation;
    private ConnectorImpl connector;

    public void testNoInterceptors() throws Exception {
        InboundInvocationChain inboundChain = new InboundInvocationChainImpl(operation);
        OutboundInvocationChain outboundChain = new OutboundInvocationChainImpl(operation);
        outboundChain.addInterceptor(new InvokerInterceptor());
        TargetInvoker invoker = EasyMock.createNiceMock(TargetInvoker.class);
        EasyMock.expect(invoker.invoke(EasyMock.isA(Message.class))).andReturn(new MessageImpl());
        EasyMock.replay(invoker);
        connector.connect(inboundChain, outboundChain);
        inboundChain.setTargetInvoker(invoker);
        inboundChain.prepare();
        Message msg = new MessageImpl();
        msg.setTargetInvoker(invoker);
        inboundChain.getHeadInterceptor().invoke(msg);
        EasyMock.verify(invoker);
    }


    /**
     * Verifies an invocation with a single source interceptor
     */
    public void testSourceInterceptor() throws Exception {
        MockSyncInterceptor interceptor = new MockSyncInterceptor();
        InboundInvocationChain inboundChain = new InboundInvocationChainImpl(operation);
        inboundChain.addInterceptor(interceptor);
        OutboundInvocationChain outboundChain = new OutboundInvocationChainImpl(operation);
        outboundChain.addInterceptor(new InvokerInterceptor());
        Message msg = new MessageImpl();
        TargetInvoker invoker = EasyMock.createNiceMock(TargetInvoker.class);
        EasyMock.expect(invoker.invoke(EasyMock.eq(msg))).andReturn(msg);
        EasyMock.replay(invoker);
        assertEquals(0, interceptor.getCount());
        connector.connect(inboundChain, outboundChain);
        inboundChain.setTargetInvoker(invoker);
        inboundChain.prepare();
        msg.setTargetInvoker(inboundChain.getTargetInvoker());
        assertEquals(msg, inboundChain.getHeadInterceptor().invoke(msg));
        assertEquals(1, interceptor.getCount());
        EasyMock.verify(invoker);
    }

    /**
     * Verifies an invocation with a single target interceptor
     */
    public void testTargetInterceptor() throws Exception {
        MockSyncInterceptor interceptor = new MockSyncInterceptor();
        InboundInvocationChain inboundChain = new InboundInvocationChainImpl(operation);
        inboundChain.addInterceptor(interceptor);
        OutboundInvocationChain outboundChain = new OutboundInvocationChainImpl(operation);
        outboundChain.addInterceptor(new InvokerInterceptor());
        Message msg = new MessageImpl();
        TargetInvoker invoker = EasyMock.createNiceMock(TargetInvoker.class);
        EasyMock.expect(invoker.invoke(EasyMock.eq(msg))).andReturn(msg);
        EasyMock.replay(invoker);
        assertEquals(0, interceptor.getCount());
        connector.connect(inboundChain, outboundChain);
        inboundChain.setTargetInvoker(invoker);
        inboundChain.prepare();
        msg.setTargetInvoker(inboundChain.getTargetInvoker());
        assertEquals(msg, inboundChain.getHeadInterceptor().invoke(msg));
        assertEquals(1, interceptor.getCount());
        EasyMock.verify(invoker);
    }

    /**
     * Verifies an invocation with a source and target interceptor
     */
    public void testSourceTargetInterceptor() throws Exception {
        MockSyncInterceptor sourceInterceptor = new MockSyncInterceptor();
        MockSyncInterceptor targetInterceptor = new MockSyncInterceptor();
        OutboundInvocationChain outboundChain = new OutboundInvocationChainImpl(operation);
        outboundChain.addInterceptor(sourceInterceptor);
        outboundChain.addInterceptor(new InvokerInterceptor());
        InboundInvocationChain inboundChain = new InboundInvocationChainImpl(operation);
        inboundChain.addInterceptor(targetInterceptor);
        Message msg = new MessageImpl();
        TargetInvoker invoker = EasyMock.createNiceMock(TargetInvoker.class);
        EasyMock.expect(invoker.invoke(EasyMock.eq(msg))).andReturn(msg);
        EasyMock.replay(invoker);
        assertEquals(0, sourceInterceptor.getCount());
        assertEquals(0, targetInterceptor.getCount());
        connector.connect(inboundChain, outboundChain);
        inboundChain.setTargetInvoker(invoker);
        inboundChain.prepare();
        msg.setTargetInvoker(inboundChain.getTargetInvoker());
        assertEquals(msg, inboundChain.getHeadInterceptor().invoke(msg));
        assertEquals(1, sourceInterceptor.getCount());
        assertEquals(1, targetInterceptor.getCount());
        EasyMock.verify(invoker);
    }

    public void testOptimizeSet() throws Exception {
        OutboundInvocationChain outboundChain = new OutboundInvocationChainImpl(operation);
        outboundChain.addInterceptor(new InvokerInterceptor());
        InboundInvocationChain inboundChain = new InboundInvocationChainImpl(operation);
        Message msg = new MessageImpl();
        TargetInvoker invoker = EasyMock.createNiceMock(TargetInvoker.class);
        EasyMock.expect(invoker.invoke(EasyMock.eq(msg))).andReturn(msg);
        EasyMock.replay(invoker);
        connector.connect(inboundChain, outboundChain);
        inboundChain.setTargetInvoker(invoker);
        inboundChain.prepare();
        msg.setTargetInvoker(inboundChain.getTargetInvoker());
        assertEquals(msg, inboundChain.getHeadInterceptor().invoke(msg));
        EasyMock.verify(invoker);
    }

    protected void setUp() throws Exception {
        super.setUp();
        connector = new ConnectorImpl();
        JavaInterfaceProcessorRegistry registry = new JavaInterfaceProcessorRegistryImpl();
        ServiceContract<?> contract;
        try {
            contract = registry.introspect(SimpleTarget.class);
        } catch (InvalidServiceContractException e) {
            throw new AssertionError();
        }
        operation = contract.getOperations().get("echo");
    }
}
