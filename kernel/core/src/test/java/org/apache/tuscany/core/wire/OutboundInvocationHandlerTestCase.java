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

import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.idl.java.JavaInterfaceProcessorRegistry;
import org.apache.tuscany.spi.idl.java.JavaIDLUtils;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;

import junit.framework.TestCase;
import org.apache.tuscany.core.mock.component.SimpleTarget;
import org.apache.tuscany.core.mock.component.SimpleTargetImpl;
import org.apache.tuscany.core.mock.wire.MockHandler;
import org.apache.tuscany.core.mock.wire.MockStaticInvoker;
import org.apache.tuscany.core.mock.wire.MockSyncInterceptor;
import org.apache.tuscany.core.wire.jdk.JDKOutboundInvocationHandler;
import org.apache.tuscany.core.idl.java.JavaInterfaceProcessorRegistryImpl;

public class OutboundInvocationHandlerTestCase extends TestCase {

    private Method hello;
    private ServiceContract<?> contract;

    public OutboundInvocationHandlerTestCase() {
        super();
    }

    public OutboundInvocationHandlerTestCase(String arg0) {
        super(arg0);
    }

    public void setUp() throws Exception {
        super.setUp();
        JavaInterfaceProcessorRegistry registry = new JavaInterfaceProcessorRegistryImpl();
        contract = registry.introspect(SimpleTarget.class);
        hello = SimpleTarget.class.getMethod("hello", String.class);
    }

    public void testBasicInvoke() throws Throwable {
        OutboundWire wire = new OutboundWireImpl();
        Operation operation = contract.getOperations().get("hello");
        wire.addInvocationChain(operation, createChain(operation));
        wire.setServiceContract(contract);
        JDKOutboundInvocationHandler handler = new JDKOutboundInvocationHandler(wire);
        assertEquals("foo", handler.invoke(hello, new Object[]{"foo"}));
    }

    public void testErrorInvoke() throws Throwable {
        OutboundWire wire = new OutboundWireImpl();
        Operation operation = contract.getOperations().get("hello");
        wire.addInvocationChain(operation, createChain(operation));
        wire.setServiceContract(contract);
        JDKOutboundInvocationHandler handler = new JDKOutboundInvocationHandler(wire);
        try {
            handler.invoke(hello, new Object[]{});
            fail("Expected " + IllegalArgumentException.class.getName());
        } catch (IllegalArgumentException e) {
            // should throw
        }
    }

    public void testDirectErrorInvoke() throws Throwable {
        Operation operation = contract.getOperations().get("hello");
        OutboundInvocationChain source = new OutboundInvocationChainImpl(operation);
        MockStaticInvoker invoker = new MockStaticInvoker(hello, new SimpleTargetImpl());
        source.setTargetInvoker(invoker);

        OutboundWire wire = new OutboundWireImpl();
        wire.setServiceContract(contract);
        wire.addInvocationChain(operation, source);
        JDKOutboundInvocationHandler handler = new JDKOutboundInvocationHandler(wire);
        try {
            assertEquals("foo", handler.invoke(hello, new Object[]{}));
            fail("Expected " + IllegalArgumentException.class.getName());
        } catch (IllegalArgumentException e) {
            // should throw
        }
    }

    public void testDirectInvoke() throws Throwable {
        Operation operation = contract.getOperations().get("hello");
        OutboundInvocationChain source = new OutboundInvocationChainImpl(operation);
        MockStaticInvoker invoker = new MockStaticInvoker(hello, new SimpleTargetImpl());
        source.setTargetInvoker(invoker);

        OutboundWire wire = new OutboundWireImpl();
        wire.setServiceContract(contract);
        wire.addInvocationChain(operation, source);
        JDKOutboundInvocationHandler handler = new JDKOutboundInvocationHandler(wire);
        assertEquals("foo", handler.invoke(hello, new Object[]{"foo"}));
    }

    private OutboundInvocationChain createChain(Operation operation) {
        OutboundInvocationChain source = new OutboundInvocationChainImpl(operation);
        MockHandler sourceRequestHandler = new MockHandler();
        MockHandler sourceResponseHandler = new MockHandler();
        MockSyncInterceptor sourceInterceptor = new MockSyncInterceptor();
        source.addRequestHandler(sourceRequestHandler);
        source.addResponseHandler(sourceResponseHandler);
        source.addInterceptor(sourceInterceptor);

        InboundInvocationChain target = new InboundInvocationChainImpl(operation);
        MockHandler targetRequestHandler = new MockHandler();
        MockHandler targetResponseHandler = new MockHandler();
        MockSyncInterceptor targetInterceptor = new MockSyncInterceptor();
        target.addRequestHandler(targetRequestHandler);
        target.addResponseHandler(targetResponseHandler);
        target.addInterceptor(targetInterceptor);
        target.addInterceptor(new InvokerInterceptor());

        // connect the source to the target
        source.setTargetRequestChannel(new MessageChannelImpl(target.getRequestHandlers()));
        source.setTargetResponseChannel(new MessageChannelImpl(target.getResponseHandlers()));
        source.prepare();
        target.prepare();
        Method method = JavaIDLUtils.findMethod(operation, SimpleTarget.class.getMethods());
        MockStaticInvoker invoker = new MockStaticInvoker(method, new SimpleTargetImpl());
        source.setTargetInvoker(invoker);
        return source;
    }
}
