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
package org.apache.tuscany.core.wire.jdk;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.spi.idl.InvalidServiceContractException;
import org.apache.tuscany.spi.idl.java.JavaInterfaceProcessorRegistry;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.InboundInvocationChain;

import org.apache.tuscany.core.idl.java.JavaInterfaceProcessorRegistryImpl;
import org.apache.tuscany.core.mock.component.SimpleTarget;
import org.apache.tuscany.core.mock.component.SimpleTargetImpl;
import org.apache.tuscany.core.mock.wire.MockStaticInvoker;
import org.apache.tuscany.core.mock.wire.MockSyncInterceptor;
import org.apache.tuscany.core.wire.InboundInvocationChainImpl;
import org.apache.tuscany.core.wire.InvokerInterceptor;
import org.jmock.MockObjectTestCase;

/**
 * Verifies invocations on inbound wires
 *
 * @version $$Rev$$ $$Date$$
 */
public class JDKInboundInvocationHandlerTestCase extends MockObjectTestCase {

    private Method echo;
    private Operation operation;

    public void testInterceptorInvoke() throws Throwable {
        Map<Method, InboundInvocationChain> chains = new HashMap<Method, InboundInvocationChain>();
        MockStaticInvoker invoker = new MockStaticInvoker(echo, new SimpleTargetImpl());
        InboundInvocationChain chain = new InboundInvocationChainImpl(operation);
        MockSyncInterceptor interceptor = new MockSyncInterceptor();
        chain.addInterceptor(interceptor);
        chain.addInterceptor(new InvokerInterceptor());
        chain.setTargetInvoker(invoker);
        chain.prepare();
        chains.put(echo, chain);
        JDKInboundInvocationHandler handler = new JDKInboundInvocationHandler(chains);
        assertEquals("foo", handler.invoke(echo, new String[]{"foo"}));
        assertEquals(1, interceptor.getCount());
    }


    public void testDirectErrorInvoke() throws Throwable {
        InboundInvocationChain source = new InboundInvocationChainImpl(operation);
        MockStaticInvoker invoker = new MockStaticInvoker(echo, new SimpleTargetImpl());
        source.setTargetInvoker(invoker);

        Map<Method, InboundInvocationChain> chains = new HashMap<Method, InboundInvocationChain>();
        chains.put(echo, source);
        JDKInboundInvocationHandler handler = new JDKInboundInvocationHandler(chains);
        try {
            assertEquals("foo", handler.invoke(echo, new Object[]{}));
            fail("Expected " + IllegalArgumentException.class.getName());
        } catch (IllegalArgumentException e) {
            // should throw
        }
    }

    public void testDirectInvoke() throws Throwable {
        InboundInvocationChain source = new InboundInvocationChainImpl(operation);
        MockStaticInvoker invoker = new MockStaticInvoker(echo, new SimpleTargetImpl());
        source.setTargetInvoker(invoker);

        Map<Method, InboundInvocationChain> chains = new HashMap<Method, InboundInvocationChain>();
        chains.put(echo, source);
        JDKInboundInvocationHandler handler = new JDKInboundInvocationHandler(chains);
        assertEquals("foo", handler.invoke(echo, new Object[]{"foo"}));
    }

    public void testToString() {
        JDKInboundInvocationHandler handler =
            new JDKInboundInvocationHandler(new HashMap<Method, InboundInvocationChain>());
        Foo foo = (Foo) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{Foo.class}, handler);
        assertNotNull(foo.toString());
    }

    public void testHashCode() {
        JDKInboundInvocationHandler handler =
            new JDKInboundInvocationHandler(new HashMap<Method, InboundInvocationChain>());
        Foo foo = (Foo) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{Foo.class}, handler);
        assertNotNull(foo.hashCode());
    }

    public void setUp() throws Exception {
        super.setUp();
        JavaInterfaceProcessorRegistry registry = new JavaInterfaceProcessorRegistryImpl();
        ServiceContract<?> contract;
        try {
            contract = registry.introspect(SimpleTarget.class);
        } catch (InvalidServiceContractException e) {
            throw new AssertionError();
        }
        operation = contract.getOperations().get("echo");
        echo = SimpleTarget.class.getMethod("echo", String.class);
    }

    private interface Foo {

    }

}
