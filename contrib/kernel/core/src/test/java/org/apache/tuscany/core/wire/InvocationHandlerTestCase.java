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
import java.net.URI;

import org.apache.tuscany.spi.idl.java.JavaIDLUtils;
import org.apache.tuscany.spi.idl.java.JavaInterfaceProcessorRegistry;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.Wire;
import org.apache.tuscany.spi.component.WorkContext;

import junit.framework.TestCase;
import org.apache.tuscany.core.idl.java.JavaInterfaceProcessorRegistryImpl;
import org.apache.tuscany.core.mock.component.SimpleTarget;
import org.apache.tuscany.core.mock.component.SimpleTargetImpl;
import org.apache.tuscany.core.mock.wire.MockStaticInvoker;
import org.apache.tuscany.core.mock.wire.MockSyncInterceptor;
import org.apache.tuscany.core.wire.jdk.JDKInvocationHandler;
import org.apache.tuscany.core.implementation.PojoWorkContextTunnel;

import org.easymock.EasyMock;

public class InvocationHandlerTestCase extends TestCase {

    private Method hello;
    private ServiceContract<?> contract;
    private WorkContext workContext;

    public InvocationHandlerTestCase() {
        super();
    }

    public InvocationHandlerTestCase(String arg0) {
        super(arg0);
    }

    public void testBasicInvoke() throws Throwable {
        Wire wire = new WireImpl();
        wire.setSourceUri(URI.create("#wire"));
        Operation operation = contract.getOperations().get("hello");
        wire.addInvocationChain(operation, createChain(operation));
        wire.setSourceContract(contract);
        JDKInvocationHandler handler = new JDKInvocationHandler(SimpleTarget.class, wire, workContext);
        assertEquals("foo", handler.invoke(hello, new Object[]{"foo"}));
    }

    public void testErrorInvoke() throws Throwable {
        Wire wire = new WireImpl();
        wire.setSourceUri(URI.create("#wire"));
        Operation operation = contract.getOperations().get("hello");
        wire.addInvocationChain(operation, createChain(operation));
        wire.setSourceContract(contract);
        JDKInvocationHandler handler = new JDKInvocationHandler(SimpleTarget.class, wire, workContext);
        try {
            handler.invoke(hello, new Object[]{});
            fail("Expected " + IllegalArgumentException.class.getName());
        } catch (IllegalArgumentException e) {
            // should throw
        }
    }

    public void testDirectErrorInvoke() throws Throwable {
        Operation operation = contract.getOperations().get("hello");
        InvocationChain source = new InvocationChainImpl(operation);
        MockStaticInvoker invoker = new MockStaticInvoker(hello, new SimpleTargetImpl());
        source.setTargetInvoker(invoker);

        Wire wire = new WireImpl();
        wire.setSourceUri(URI.create("#wire"));
        wire.setSourceContract(contract);
        wire.addInvocationChain(operation, source);
        JDKInvocationHandler handler = new JDKInvocationHandler(SimpleTarget.class, wire, workContext);
        try {
            assertEquals("foo", handler.invoke(hello, new Object[]{}));
            fail("Expected " + IllegalArgumentException.class.getName());
        } catch (IllegalArgumentException e) {
            // should throw
        }
    }

    public void testDirectInvoke() throws Throwable {
        Operation operation = contract.getOperations().get("hello");
        InvocationChain source = new InvocationChainImpl(operation);
        MockStaticInvoker invoker = new MockStaticInvoker(hello, new SimpleTargetImpl());
        source.setTargetInvoker(invoker);

        Wire wire = new WireImpl();
        wire.setSourceUri(URI.create("#wire"));
        wire.setSourceContract(contract);
        wire.addInvocationChain(operation, source);
        JDKInvocationHandler handler = new JDKInvocationHandler(SimpleTarget.class, wire, workContext);
        assertEquals("foo", handler.invoke(hello, new Object[]{"foo"}));
    }

    private InvocationChain createChain(Operation operation) {
        InvocationChain chain = new InvocationChainImpl(operation);
        MockSyncInterceptor targetInterceptor = new MockSyncInterceptor();
        chain.addInterceptor(targetInterceptor);
        chain.addInterceptor(new InvokerInterceptor());

        MockStaticInvoker invoker = new MockStaticInvoker(hello, new SimpleTargetImpl());
        chain.setTargetInvoker(invoker);
        return chain;
    }

    public void setUp() throws Exception {
        super.setUp();
        JavaInterfaceProcessorRegistry registry = new JavaInterfaceProcessorRegistryImpl();
        contract = registry.introspect(SimpleTarget.class);
        hello = SimpleTarget.class.getMethod("hello", String.class);
        workContext = EasyMock.createMock(WorkContext.class);
        EasyMock.expect(workContext.getCorrelationId()).andStubReturn(null);
        EasyMock.expect(workContext.getCallbackUris()).andStubReturn(null);
        EasyMock.replay(workContext);
        PojoWorkContextTunnel.setThreadWorkContext(workContext);
    }

    protected void tearDown() throws Exception {
        PojoWorkContextTunnel.setThreadWorkContext(null);
        super.tearDown();
    }
}
