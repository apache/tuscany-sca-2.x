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

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.spi.idl.InvalidServiceContractException;
import org.apache.tuscany.spi.idl.java.JavaInterfaceProcessorRegistry;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.Interceptor;
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
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

/**
 * Verifies connection strategies between {@link org.apache.tuscany.spi.wire.OutboundInvocationChain}s and {@link
 * org.apache.tuscany.spi.wire.InboundInvocationChain}s
 *
 * @version $$Rev$$ $$Date$$
 */
public class InboundtoOutboundConnectTestCase extends TestCase {
    private Operation operation;

    @SuppressWarnings("unchecked")
    public void testNoInterceptorsNoHandlers() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        InboundInvocationChain inboundChain = setupInbound(null);
        OutboundInvocationChain outboundChain = setupOutbound(null);
        String[] val = new String[]{"foo"};
        TargetInvoker invoker = createNiceMock(TargetInvoker.class);
        expect(invoker.invokeTarget(EasyMock.eq(val))).andReturn(val);
        replay(invoker);
        connector.connect(inboundChain, outboundChain);
        inboundChain.setTargetInvoker(invoker);
        inboundChain.prepare();
        inboundChain.getTargetInvoker().invokeTarget(val);
        verify(invoker);
    }


    /**
     * Verifies an invocation with a single source interceptor
     */
    @SuppressWarnings("unchecked")
    public void testSourceInterceptor() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        MockSyncInterceptor interceptor = new MockSyncInterceptor();
        List<Interceptor> interceptors = new ArrayList<Interceptor>();
        interceptors.add(interceptor);

        InboundInvocationChain inboundChain = setupInbound(interceptors);
        OutboundInvocationChain outboundChain = setupOutbound(null);
        Message msg = new MessageImpl();
        TargetInvoker invoker = createNiceMock(TargetInvoker.class);
        expect(invoker.invoke(EasyMock.eq(msg))).andReturn(msg);
        replay(invoker);
        assertEquals(0, interceptor.getCount());
        connector.connect(inboundChain, outboundChain);
        inboundChain.setTargetInvoker(invoker);
        inboundChain.prepare();
        msg.setTargetInvoker(inboundChain.getTargetInvoker());
        assertEquals(msg, inboundChain.getHeadInterceptor().invoke(msg));
        assertEquals(1, interceptor.getCount());
        verify(invoker);
    }

    /**
     * Verifies an invocation with a single target interceptor
     */
    @SuppressWarnings("unchecked")
    public void testTargetInterceptor() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        MockSyncInterceptor interceptor = new MockSyncInterceptor();
        List<Interceptor> interceptors = new ArrayList<Interceptor>();
        interceptors.add(interceptor);

        InboundInvocationChain inboundChain = setupInbound(interceptors);
        OutboundInvocationChain outboundChain = setupOutbound(null);
        Message msg = new MessageImpl();
        TargetInvoker invoker = createNiceMock(TargetInvoker.class);
        expect(invoker.invoke(EasyMock.eq(msg))).andReturn(msg);
        replay(invoker);
        assertEquals(0, interceptor.getCount());
        connector.connect(inboundChain, outboundChain);
        inboundChain.setTargetInvoker(invoker);
        inboundChain.prepare();
        msg.setTargetInvoker(inboundChain.getTargetInvoker());
        assertEquals(msg, inboundChain.getHeadInterceptor().invoke(msg));
        assertEquals(1, interceptor.getCount());
        verify(invoker);
    }

    /**
     * Verifies an invocation with a source and target interceptor
     */
    @SuppressWarnings("unchecked")
    public void testSourceTargetInterceptor() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        MockSyncInterceptor sourceInterceptor = new MockSyncInterceptor();
        List<Interceptor> sourceInterceptors = new ArrayList<Interceptor>();
        sourceInterceptors.add(sourceInterceptor);
        MockSyncInterceptor targetInterceptor = new MockSyncInterceptor();
        List<Interceptor> targetInterceptors = new ArrayList<Interceptor>();
        targetInterceptors.add(targetInterceptor);

        OutboundInvocationChain outboundChain = setupOutbound(sourceInterceptors);
        InboundInvocationChain inboundChain = setupInbound(targetInterceptors);
        Message msg = new MessageImpl();
        TargetInvoker invoker = createNiceMock(TargetInvoker.class);
        expect(invoker.invoke(EasyMock.eq(msg))).andReturn(msg);
        replay(invoker);
        assertEquals(0, sourceInterceptor.getCount());
        assertEquals(0, targetInterceptor.getCount());
        connector.connect(inboundChain, outboundChain);
        inboundChain.setTargetInvoker(invoker);
        inboundChain.prepare();
        msg.setTargetInvoker(inboundChain.getTargetInvoker());
        assertEquals(msg, inboundChain.getHeadInterceptor().invoke(msg));
        assertEquals(1, sourceInterceptor.getCount());
        assertEquals(1, targetInterceptor.getCount());
        verify(invoker);
    }

    public InboundInvocationChain setupInbound(List<Interceptor> interceptors) {

        InboundInvocationChainImpl chain = new InboundInvocationChainImpl(operation);
        if (interceptors != null) {
            for (Interceptor interceptor : interceptors) {
                chain.addInterceptor(interceptor);
            }
        }
        return chain;
    }

    public OutboundInvocationChain setupOutbound(List<Interceptor> interceptors) {

        OutboundInvocationChainImpl chain = new OutboundInvocationChainImpl(operation);
        if (interceptors != null) {
            for (Interceptor interceptor : interceptors) {
                chain.addInterceptor(interceptor);
            }
        }
        chain.addInterceptor(new InvokerInterceptor()); // add tail interceptor
        return chain;
    }

    protected void setUp() throws Exception {
        super.setUp();
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
