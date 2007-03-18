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

/**
 * @version $$Rev$$ $$Date$$
 */
public class BasicReferenceInvocationHandlerTestCase extends TestCase {

    private Method echo;
    private WorkContext workContext;

    public void testInterceptorInvoke() throws Throwable {
        JavaInterfaceProcessorRegistry registry = new JavaInterfaceProcessorRegistryImpl();
        ServiceContract<?> contract = registry.introspect(SimpleTarget.class);
        Operation<?> operation = contract.getOperations().get("echo");
        MockStaticInvoker invoker = new MockStaticInvoker(echo, new SimpleTargetImpl());
        InvocationChain chain = new InvocationChainImpl(operation);
        MockSyncInterceptor interceptor = new MockSyncInterceptor();
        chain.addInterceptor(interceptor);
        chain.addInterceptor(new InvokerInterceptor());
        chain.setTargetInvoker(invoker);
        //chains.put(echo, chain);
        Wire wire = new WireImpl();
        wire.addInvocationChain(operation, chain);
        wire.setSourceContract(contract);
        wire.setSourceUri(URI.create("#wire"));
        JDKInvocationHandler handler = new JDKInvocationHandler(SimpleTarget.class, wire, workContext);
        PojoWorkContextTunnel.setThreadWorkContext(workContext);
        try {
            assertEquals("foo", handler.invoke(null, echo, new String[]{"foo"}));
            assertEquals(1, interceptor.getCount());
        } finally {
            PojoWorkContextTunnel.setThreadWorkContext(null);
        }
    }

    public void setUp() throws Exception {
        super.setUp();
        echo = SimpleTarget.class.getMethod("echo", String.class);
        workContext = EasyMock.createMock(WorkContext.class);
        EasyMock.expect(workContext.getCorrelationId()).andStubReturn(null);
        EasyMock.expect(workContext.getCallbackUris()).andStubReturn(null);
        EasyMock.replay(workContext);
    }

}
