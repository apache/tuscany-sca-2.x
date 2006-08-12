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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageHandler;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.TargetInvoker;

import org.apache.tuscany.core.mock.component.SimpleTarget;
import org.apache.tuscany.core.mock.wire.MockHandler;
import org.apache.tuscany.core.mock.wire.MockSyncInterceptor;
import org.apache.tuscany.core.wire.InboundInvocationChainImpl;
import org.apache.tuscany.core.wire.InvokerInterceptor;
import org.apache.tuscany.core.wire.OutboundInvocationChainImpl;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * Verifies connection strategies between {@link org.apache.tuscany.spi.wire.OutboundInvocationChain}s and {@link
 * org.apache.tuscany.spi.wire.InboundInvocationChain}s
 *
 * @version $$Rev$$ $$Date$$
 */
public class OutboundToInboundConnectTestCase extends MockObjectTestCase {

    @SuppressWarnings("unchecked")
    public void testNoInterceptorsNoHandlers() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        InboundInvocationChain inboundChain = setupTarget(null, null, null);
        OutboundInvocationChain outboundChain = setupSource(null, null, null);
        String[] val = new String[]{"foo"};
        Mock mock = mock(TargetInvoker.class);
        mock.expects(once()).method("invokeTarget").with(eq(val)).will(returnValue(val));
        TargetInvoker invoker = (TargetInvoker) mock.proxy();
        connector.connect(outboundChain, inboundChain, invoker);
        inboundChain.prepare();
        assertEquals(val, outboundChain.getTargetInvoker().invokeTarget(val));
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

        OutboundInvocationChain outboundChain = setupSource(interceptors, null, null);
        InboundInvocationChain inboundChain = setupTarget(null, null, null);
        Message msg = new MessageImpl();
        Mock mock = mock(TargetInvoker.class);
        mock.expects(once()).method("invoke").with(eq(msg)).will(returnValue(msg));
        TargetInvoker invoker = (TargetInvoker) mock.proxy();
        assertEquals(0, interceptor.getCount());
        connector.connect(outboundChain, inboundChain, invoker);
        inboundChain.prepare();
        msg.setTargetInvoker(outboundChain.getTargetInvoker());
        assertEquals(msg, outboundChain.getHeadInterceptor().invoke(msg));
        assertEquals(1, interceptor.getCount());
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

        OutboundInvocationChain outboundChain = setupSource(null, null, null);
        InboundInvocationChain inboundChain = setupTarget(interceptors, null, null);
        Message msg = new MessageImpl();
        Mock mock = mock(TargetInvoker.class);
        mock.expects(once()).method("invoke").with(eq(msg)).will(returnValue(msg));
        TargetInvoker invoker = (TargetInvoker) mock.proxy();
        assertEquals(0, interceptor.getCount());
        connector.connect(outboundChain, inboundChain, invoker);
        inboundChain.prepare();
        msg.setTargetInvoker(outboundChain.getTargetInvoker());
        assertEquals(msg, outboundChain.getHeadInterceptor().invoke(msg));
        assertEquals(1, interceptor.getCount());
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

        OutboundInvocationChain outboundChain = setupSource(sourceInterceptors, null, null);
        InboundInvocationChain inboundChain = setupTarget(targetInterceptors, null, null);
        Message msg = new MessageImpl();
        Mock mock = mock(TargetInvoker.class);
        mock.expects(once()).method("invoke").with(eq(msg)).will(returnValue(msg));
        TargetInvoker invoker = (TargetInvoker) mock.proxy();
        assertEquals(0, sourceInterceptor.getCount());
        assertEquals(0, targetInterceptor.getCount());
        connector.connect(outboundChain, inboundChain, invoker);
        inboundChain.prepare();
        msg.setTargetInvoker(outboundChain.getTargetInvoker());
        assertEquals(msg, outboundChain.getHeadInterceptor().invoke(msg));
        assertEquals(1, sourceInterceptor.getCount());
        assertEquals(1, targetInterceptor.getCount());
    }

    /**
     * Verifies an invocation with a source interceptor and a request handler
     */
    @SuppressWarnings("unchecked")
    public void testSourceInterceptorSourceRequestHandler() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        MockSyncInterceptor interceptor = new MockSyncInterceptor();
        List<Interceptor> interceptors = new ArrayList<Interceptor>();
        interceptors.add(interceptor);
        MockHandler handler = new MockHandler();
        List<MessageHandler> handlers = new ArrayList<MessageHandler>();
        handlers.add(handler);

        OutboundInvocationChain outboundChain = setupSource(interceptors, handlers, null);
        InboundInvocationChain inboundChain = setupTarget(null, null, null);
        Message msg = new MessageImpl();
        Mock mock = mock(TargetInvoker.class);
        mock.expects(once()).method("invoke").with(eq(msg)).will(returnValue(msg));
        TargetInvoker invoker = (TargetInvoker) mock.proxy();
        assertEquals(0, interceptor.getCount());
        assertEquals(0, handler.getCount());
        connector.connect(outboundChain, inboundChain, invoker);
        inboundChain.prepare();
        msg.setTargetInvoker(outboundChain.getTargetInvoker());
        assertEquals(msg, outboundChain.getHeadInterceptor().invoke(msg));
        assertEquals(1, interceptor.getCount());
        assertEquals(1, handler.getCount());
    }

    /**
     * Verifies an invocation with a target interceptor and a request handler
     */
    @SuppressWarnings("unchecked")
    public void testTargetInterceptorTargetRequestHandler() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        MockSyncInterceptor interceptor = new MockSyncInterceptor();
        List<Interceptor> interceptors = new ArrayList<Interceptor>();
        interceptors.add(interceptor);
        MockHandler handler = new MockHandler();
        List<MessageHandler> handlers = new ArrayList<MessageHandler>();
        handlers.add(handler);

        OutboundInvocationChain outboundChain = setupSource(null, null, null);
        InboundInvocationChain inboundChain = setupTarget(interceptors, handlers, null);
        Message msg = new MessageImpl();
        Mock mock = mock(TargetInvoker.class);
        mock.expects(once()).method("invoke").with(eq(msg)).will(returnValue(msg));
        TargetInvoker invoker = (TargetInvoker) mock.proxy();
        assertEquals(0, interceptor.getCount());
        assertEquals(0, handler.getCount());
        connector.connect(outboundChain, inboundChain, invoker);
        inboundChain.prepare();
        msg.setTargetInvoker(outboundChain.getTargetInvoker());
        assertEquals(msg, outboundChain.getHeadInterceptor().invoke(msg));
        assertEquals(1, interceptor.getCount());
        assertEquals(1, handler.getCount());
    }


    /**
     * Verifies an invocation with a source interceptor and response handler
     */
    @SuppressWarnings("unchecked")
    public void testSourceInterceptorSourceResponseHandler() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        MockSyncInterceptor interceptor = new MockSyncInterceptor();
        List<Interceptor> interceptors = new ArrayList<Interceptor>();
        interceptors.add(interceptor);
        MockHandler handler = new MockHandler();
        List<MessageHandler> handlers = new ArrayList<MessageHandler>();
        handlers.add(handler);

        OutboundInvocationChain outboundChain = setupSource(interceptors, null, handlers);
        InboundInvocationChain inboundChain = setupTarget(null, null, null);
        Message msg = new MessageImpl();
        Mock mock = mock(TargetInvoker.class);
        mock.expects(once()).method("invoke").with(eq(msg)).will(returnValue(msg));
        TargetInvoker invoker = (TargetInvoker) mock.proxy();
        assertEquals(0, interceptor.getCount());
        assertEquals(0, handler.getCount());
        connector.connect(outboundChain, inboundChain, invoker);
        inboundChain.prepare();
        msg.setTargetInvoker(outboundChain.getTargetInvoker());
        assertEquals(msg, outboundChain.getHeadInterceptor().invoke(msg));
        assertEquals(1, interceptor.getCount());
        assertEquals(1, handler.getCount());
    }

    /**
     * Verifies an invocation with a source interceptor and response handler
     */
    @SuppressWarnings("unchecked")
    public void testTargetInterceptorTargetResponseHandler() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        MockSyncInterceptor interceptor = new MockSyncInterceptor();
        List<Interceptor> interceptors = new ArrayList<Interceptor>();
        interceptors.add(interceptor);
        MockHandler handler = new MockHandler();
        List<MessageHandler> handlers = new ArrayList<MessageHandler>();
        handlers.add(handler);

        OutboundInvocationChain outboundChain = setupSource(null, null, null);
        InboundInvocationChain inboundChain = setupTarget(interceptors, null, handlers);
        Message msg = new MessageImpl();
        Mock mock = mock(TargetInvoker.class);
        mock.expects(once()).method("invoke").with(eq(msg)).will(returnValue(msg));
        TargetInvoker invoker = (TargetInvoker) mock.proxy();
        assertEquals(0, interceptor.getCount());
        assertEquals(0, handler.getCount());
        connector.connect(outboundChain, inboundChain, invoker);
        inboundChain.prepare();
        msg.setTargetInvoker(outboundChain.getTargetInvoker());
        assertEquals(msg, outboundChain.getHeadInterceptor().invoke(msg));
        assertEquals(1, interceptor.getCount());
        assertEquals(1, handler.getCount());
    }

    /**
     * Verifies an invocation with a source interceptor, request handler, and response handler
     */
    @SuppressWarnings("unchecked")
    public void testSourceInterceptorSourceRequestResponseHandler() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        MockSyncInterceptor interceptor = new MockSyncInterceptor();
        List<Interceptor> interceptors = new ArrayList<Interceptor>();
        interceptors.add(interceptor);
        MockHandler handler = new MockHandler();
        List<MessageHandler> handlers = new ArrayList<MessageHandler>();
        handlers.add(handler);

        OutboundInvocationChain outboundChain = setupSource(interceptors, handlers, handlers);
        InboundInvocationChain inboundChain = setupTarget(null, null, null);
        Message msg = new MessageImpl();
        Mock mock = mock(TargetInvoker.class);
        mock.expects(once()).method("invoke").with(eq(msg)).will(returnValue(msg));
        TargetInvoker invoker = (TargetInvoker) mock.proxy();
        assertEquals(0, interceptor.getCount());
        assertEquals(0, handler.getCount());
        connector.connect(outboundChain, inboundChain, invoker);
        inboundChain.prepare();
        msg.setTargetInvoker(outboundChain.getTargetInvoker());
        assertEquals(msg, outboundChain.getHeadInterceptor().invoke(msg));
        assertEquals(1, interceptor.getCount());
        assertEquals(2, handler.getCount());
    }

    /**
     * Verifies an invocation with a target interceptor, request handler, and response handler
     */
    @SuppressWarnings("unchecked")
    public void testTargetInterceptorTargetRequestResponseHandler() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        MockSyncInterceptor interceptor = new MockSyncInterceptor();
        List<Interceptor> interceptors = new ArrayList<Interceptor>();
        interceptors.add(interceptor);
        MockHandler handler = new MockHandler();
        List<MessageHandler> handlers = new ArrayList<MessageHandler>();
        handlers.add(handler);

        OutboundInvocationChain outboundChain = setupSource(null, null, handlers);
        InboundInvocationChain inboundChain = setupTarget(interceptors, handlers, null);
        Message msg = new MessageImpl();
        Mock mock = mock(TargetInvoker.class);
        mock.expects(once()).method("invoke").with(eq(msg)).will(returnValue(msg));
        TargetInvoker invoker = (TargetInvoker) mock.proxy();
        assertEquals(0, interceptor.getCount());
        assertEquals(0, handler.getCount());
        connector.connect(outboundChain, inboundChain, invoker);
        inboundChain.prepare();
        msg.setTargetInvoker(outboundChain.getTargetInvoker());
        assertEquals(msg, outboundChain.getHeadInterceptor().invoke(msg));
        assertEquals(1, interceptor.getCount());
        assertEquals(2, handler.getCount());
    }

    /**
     * Verifies an invocation with a source request handler and response handler
     */
    @SuppressWarnings("unchecked")
    public void testSourceRequestResponseHandler() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        MockHandler handler = new MockHandler();
        List<MessageHandler> handlers = new ArrayList<MessageHandler>();
        handlers.add(handler);

        OutboundInvocationChain outboundChain = setupSource(null, handlers, handlers);
        InboundInvocationChain inboundChain = setupTarget(null, null, null);
        Message msg = new MessageImpl();
        Mock mock = mock(TargetInvoker.class);
        mock.expects(once()).method("invoke").with(eq(msg)).will(returnValue(msg));
        TargetInvoker invoker = (TargetInvoker) mock.proxy();
        assertEquals(0, handler.getCount());
        connector.connect(outboundChain, inboundChain, invoker);
        inboundChain.prepare();
        msg.setTargetInvoker(outboundChain.getTargetInvoker());
        assertEquals(msg, outboundChain.getHeadInterceptor().invoke(msg));
        assertEquals(2, handler.getCount());
    }

    /**
     * Verifies an invocation with a target request handler and response handler
     */
    @SuppressWarnings("unchecked")
    public void testTargetRequestResponseHandler() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        MockHandler handler = new MockHandler();
        List<MessageHandler> handlers = new ArrayList<MessageHandler>();
        handlers.add(handler);

        OutboundInvocationChain outboundChain = setupSource(null, null, null);
        InboundInvocationChain inboundChain = setupTarget(null, handlers, handlers);
        Message msg = new MessageImpl();
        Mock mock = mock(TargetInvoker.class);
        mock.expects(once()).method("invoke").with(eq(msg)).will(returnValue(msg));
        TargetInvoker invoker = (TargetInvoker) mock.proxy();
        assertEquals(0, handler.getCount());
        connector.connect(outboundChain, inboundChain, invoker);
        inboundChain.prepare();
        msg.setTargetInvoker(outboundChain.getTargetInvoker());
        assertEquals(msg, outboundChain.getHeadInterceptor().invoke(msg));
        assertEquals(2, handler.getCount());
    }

    /**
     * Verifies an invocation with a single source request handler
     */
    @SuppressWarnings("unchecked")
    public void testSourceRequestHandler() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        MockHandler handler = new MockHandler();
        List<MessageHandler> handlers = new ArrayList<MessageHandler>();
        handlers.add(handler);

        OutboundInvocationChain outboundChain = setupSource(null, handlers, null);
        InboundInvocationChain inboundChain = setupTarget(null, null, null);
        Message msg = new MessageImpl();
        Mock mock = mock(TargetInvoker.class);
        mock.expects(once()).method("invoke").with(eq(msg)).will(returnValue(msg));
        TargetInvoker invoker = (TargetInvoker) mock.proxy();
        assertEquals(0, handler.getCount());
        connector.connect(outboundChain, inboundChain, invoker);
        inboundChain.prepare();
        msg.setTargetInvoker(outboundChain.getTargetInvoker());
        assertEquals(msg, outboundChain.getHeadInterceptor().invoke(msg));
        assertEquals(1, handler.getCount());
    }

    /**
     * Verifies an invocation with a single target request handler
     */
    @SuppressWarnings("unchecked")
    public void testTargetRequestHandler() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        MockHandler handler = new MockHandler();
        List<MessageHandler> handlers = new ArrayList<MessageHandler>();
        handlers.add(handler);

        OutboundInvocationChain outboundChain = setupSource(null, null, null);
        InboundInvocationChain inboundChain = setupTarget(null, handlers, null);
        Message msg = new MessageImpl();
        Mock mock = mock(TargetInvoker.class);
        mock.expects(once()).method("invoke").with(eq(msg)).will(returnValue(msg));
        TargetInvoker invoker = (TargetInvoker) mock.proxy();
        assertEquals(0, handler.getCount());
        connector.connect(outboundChain, inboundChain, invoker);
        inboundChain.prepare();
        msg.setTargetInvoker(outboundChain.getTargetInvoker());
        assertEquals(msg, outboundChain.getHeadInterceptor().invoke(msg));
        assertEquals(1, handler.getCount());
    }

    /**
     * Verifies an invocation with a single source response handler
     */
    @SuppressWarnings("unchecked")
    public void testSourceResponseHandler() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        MockHandler handler = new MockHandler();
        List<MessageHandler> handlers = new ArrayList<MessageHandler>();
        handlers.add(handler);

        OutboundInvocationChain outboundChain = setupSource(null, null, handlers);
        InboundInvocationChain inboundChain = setupTarget(null, null, null);
        Message msg = new MessageImpl();
        Mock mock = mock(TargetInvoker.class);
        mock.expects(once()).method("invoke").with(eq(msg)).will(returnValue(msg));
        TargetInvoker invoker = (TargetInvoker) mock.proxy();
        assertEquals(0, handler.getCount());
        connector.connect(outboundChain, inboundChain, invoker);
        inboundChain.prepare();
        msg.setTargetInvoker(outboundChain.getTargetInvoker());
        assertEquals(msg, outboundChain.getHeadInterceptor().invoke(msg));
        assertEquals(1, handler.getCount());
    }

    /**
     * Verifies an invocation with a single target response handler
     */
    @SuppressWarnings("unchecked")
    public void testTargetResponseHandler() throws Exception {
        ConnectorImpl connector = new ConnectorImpl();
        MockHandler handler = new MockHandler();
        List<MessageHandler> handlers = new ArrayList<MessageHandler>();
        handlers.add(handler);

        OutboundInvocationChain outboundChain = setupSource(null, null, null);
        InboundInvocationChain inboundChain = setupTarget(null, null, handlers);
        Message msg = new MessageImpl();
        Mock mock = mock(TargetInvoker.class);
        mock.expects(once()).method("invoke").with(eq(msg)).will(returnValue(msg));
        TargetInvoker invoker = (TargetInvoker) mock.proxy();
        assertEquals(0, handler.getCount());
        connector.connect(outboundChain, inboundChain, invoker);
        inboundChain.prepare();
        msg.setTargetInvoker(outboundChain.getTargetInvoker());
        assertEquals(msg, outboundChain.getHeadInterceptor().invoke(msg));
        assertEquals(1, handler.getCount());
    }


    public InboundInvocationChain setupTarget(List<Interceptor> interceptors,
                                              List<MessageHandler> requestHandlers,
                                              List<MessageHandler> responseHandlers) {

        Method echo;
        try {
            echo = SimpleTarget.class.getMethod("echo", String.class);
        } catch (NoSuchMethodException e) {
            throw new AssertionError();
        }
        InboundInvocationChainImpl chain = new InboundInvocationChainImpl(echo);
        if (interceptors != null) {
            for (Interceptor interceptor : interceptors) {
                chain.addInterceptor(interceptor);
            }
        }
        if (requestHandlers != null) {
            for (MessageHandler handler : requestHandlers) {
                chain.addRequestHandler(handler);
            }
        }
        if (responseHandlers != null) {
            for (MessageHandler handler : responseHandlers) {
                chain.addResponseHandler(handler);
            }
        }
        chain.addInterceptor(new InvokerInterceptor()); // add tail interceptor
        return chain;
    }

    public OutboundInvocationChain setupSource(List<Interceptor> interceptors,
                                               List<MessageHandler> requestHandlers,
                                               List<MessageHandler> responseHandlers) {

        Method echo;
        try {
            echo = SimpleTarget.class.getMethod("echo", String.class);
        } catch (NoSuchMethodException e) {
            throw new AssertionError();
        }
        OutboundInvocationChainImpl chain = new OutboundInvocationChainImpl(echo);
        if (interceptors != null) {
            for (Interceptor interceptor : interceptors) {
                chain.addInterceptor(interceptor);
            }
        }
        if (requestHandlers != null) {
            for (MessageHandler handler : requestHandlers) {
                chain.addRequestHandler(handler);
            }
        }
        if (responseHandlers != null) {
            for (MessageHandler handler : responseHandlers) {
                chain.addResponseHandler(handler);
            }
        }
        return chain;
    }

}
