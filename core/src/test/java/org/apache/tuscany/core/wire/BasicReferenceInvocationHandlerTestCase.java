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

import java.lang.reflect.Method;

import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;

import org.apache.tuscany.core.mock.component.SimpleTarget;
import org.apache.tuscany.core.mock.component.SimpleTargetImpl;
import org.apache.tuscany.core.mock.wire.MockHandler;
import org.apache.tuscany.core.mock.wire.MockStaticInvoker;
import org.apache.tuscany.core.mock.wire.MockSyncInterceptor;
import org.apache.tuscany.core.wire.jdk.JDKOutboundInvocationHandler;
import org.jmock.MockObjectTestCase;

/**
 * @version $$Rev$$ $$Date$$
 */
public class BasicReferenceInvocationHandlerTestCase extends MockObjectTestCase {

    private Method echo;

    public void testHandlersInterceptorInvoke() throws Throwable {
        //Map<Method, OutboundInvocationChain> chains = new MethodHashMap<OutboundInvocationChain>();
        MockStaticInvoker invoker = new MockStaticInvoker(echo, new SimpleTargetImpl());
        OutboundInvocationChain chain = new OutboundInvocationChainImpl(echo);
        MockSyncInterceptor interceptor = new MockSyncInterceptor();
        chain.addInterceptor(interceptor);
        chain.setTargetInterceptor(new InvokerInterceptor());
        MockHandler requestHandler = new MockHandler();
        chain.addRequestHandler(requestHandler);
        MockHandler responseHandler = new MockHandler();
        chain.addResponseHandler(responseHandler);
        chain.setTargetInvoker(invoker);
        chain.prepare();
        //chains.put(echo, chain);
        OutboundWire wire = new OutboundWireImpl();
        wire.addInvocationChain(echo, chain);
        JDKOutboundInvocationHandler handler = new JDKOutboundInvocationHandler(wire);
        assertEquals("foo", handler.invoke(null, echo, new String[]{"foo"}));
        assertEquals(1, interceptor.getCount());
        assertEquals(1, requestHandler.getCount());
        assertEquals(1, responseHandler.getCount());
    }

    public void setUp() throws Exception {
        super.setUp();
        echo = SimpleTarget.class.getMethod("echo", String.class);
    }

}
