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
public class OutboundToInboundConnectTestCase extends TestCase {

    public void testNoInterceptorsNoHandlers() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        InboundInvocationChain inboundChain = setupTarget(null);
        OutboundInvocationChain outboundChain = setupSource(null);
        String[] val = new String[]{"foo"};
        TargetInvoker invoker = createNiceMock(TargetInvoker.class);
        expect(invoker.invokeTarget(EasyMock.eq(val))).andReturn(val);
        replay(invoker);
        connector.connect(outboundChain, inboundChain, invoker);
        inboundChain.prepare();
        assertEquals(val, outboundChain.getTargetInvoker().invokeTarget(val));
        verify(invoker);
    }


    /**
     * Verifies an invocation with a single source interceptor
     */
    public void testSourceInterceptor() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        MockSyncInterceptor interceptor = new MockSyncInterceptor();
        List<Interceptor> interceptors = new ArrayList<Interceptor>();
        interceptors.add(interceptor);
        OutboundInvocationChain outboundChain = setupSource(interceptors);
        InboundInvocationChain inboundChain = setupTarget(null);
        Message msg = new MessageImpl();
        TargetInvoker invoker = createNiceMock(TargetInvoker.class);
        expect(invoker.invoke(EasyMock.eq(msg))).andReturn(msg);
        replay(invoker);
        assertEquals(0, interceptor.getCount());
        connector.connect(outboundChain, inboundChain, invoker);
        inboundChain.prepare();
        msg.setTargetInvoker(outboundChain.getTargetInvoker());
        assertEquals(msg, outboundChain.getHeadInterceptor().invoke(msg));
        assertEquals(1, interceptor.getCount());
        verify(invoker);
    }

    /**
     * Verifies an invocation with a single target interceptor
     */
    public void testTargetInterceptor() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        MockSyncInterceptor interceptor = new MockSyncInterceptor();
        List<Interceptor> interceptors = new ArrayList<Interceptor>();
        interceptors.add(interceptor);
        OutboundInvocationChain outboundChain = setupSource(null);
        InboundInvocationChain inboundChain = setupTarget(interceptors);
        Message msg = new MessageImpl();
        TargetInvoker invoker = createNiceMock(TargetInvoker.class);
        expect(invoker.invoke(EasyMock.eq(msg))).andReturn(msg);
        replay(invoker);
        assertEquals(0, interceptor.getCount());
        connector.connect(outboundChain, inboundChain, invoker);
        inboundChain.prepare();
        msg.setTargetInvoker(outboundChain.getTargetInvoker());
        assertEquals(msg, outboundChain.getHeadInterceptor().invoke(msg));
        assertEquals(1, interceptor.getCount());
        verify(invoker);
    }

    /**
     * Verifies an invocation with a source and target interceptor
     */
    public void testSourceTargetInterceptor() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        MockSyncInterceptor sourceInterceptor = new MockSyncInterceptor();
        List<Interceptor> sourceInterceptors = new ArrayList<Interceptor>();
        sourceInterceptors.add(sourceInterceptor);
        MockSyncInterceptor targetInterceptor = new MockSyncInterceptor();
        List<Interceptor> targetInterceptors = new ArrayList<Interceptor>();
        targetInterceptors.add(targetInterceptor);
        OutboundInvocationChain outboundChain = setupSource(sourceInterceptors);
        InboundInvocationChain inboundChain = setupTarget(targetInterceptors);
        Message msg = new MessageImpl();
        TargetInvoker invoker = createNiceMock(TargetInvoker.class);
        expect(invoker.invoke(EasyMock.eq(msg))).andReturn(msg);
        replay(invoker);
        assertEquals(0, sourceInterceptor.getCount());
        assertEquals(0, targetInterceptor.getCount());
        connector.connect(outboundChain, inboundChain, invoker);
        inboundChain.prepare();
        msg.setTargetInvoker(outboundChain.getTargetInvoker());
        assertEquals(msg, outboundChain.getHeadInterceptor().invoke(msg));
        assertEquals(1, sourceInterceptor.getCount());
        assertEquals(1, targetInterceptor.getCount());
        verify(invoker);
    }


    public InboundInvocationChain setupTarget(List<Interceptor> interceptors) {
        JavaInterfaceProcessorRegistry registry = new JavaInterfaceProcessorRegistryImpl();
        ServiceContract<?> contract;
        try {
            contract = registry.introspect(SimpleTarget.class);
        } catch (InvalidServiceContractException e) {
            throw new AssertionError();
        }
        Operation operation = contract.getOperations().get("echo");
        InboundInvocationChainImpl chain = new InboundInvocationChainImpl(operation);
        if (interceptors != null) {
            for (Interceptor interceptor : interceptors) {
                chain.addInterceptor(interceptor);
            }
        }
        chain.addInterceptor(new InvokerInterceptor()); // add tail interceptor
        return chain;
    }

    public OutboundInvocationChain setupSource(List<Interceptor> interceptors) {
        JavaInterfaceProcessorRegistry registry = new JavaInterfaceProcessorRegistryImpl();
        ServiceContract<?> contract;
        try {
            contract = registry.introspect(SimpleTarget.class);
        } catch (InvalidServiceContractException e) {
            throw new AssertionError();
        }
        Operation operation = contract.getOperations().get("echo");
        OutboundInvocationChainImpl chain = new OutboundInvocationChainImpl(operation);
        if (interceptors != null) {
            for (Interceptor interceptor : interceptors) {
                chain.addInterceptor(interceptor);
            }
        }
        return chain;
    }

}
