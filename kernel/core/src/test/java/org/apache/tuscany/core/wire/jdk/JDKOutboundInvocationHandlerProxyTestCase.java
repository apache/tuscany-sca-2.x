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
import java.net.URI;

import org.apache.tuscany.spi.idl.java.JavaInterfaceProcessorRegistry;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;

import junit.framework.TestCase;
import org.apache.tuscany.core.idl.java.JavaInterfaceProcessorRegistryImpl;
import org.apache.tuscany.core.wire.OutboundInvocationChainImpl;
import org.apache.tuscany.core.wire.OutboundWireImpl;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class JDKOutboundInvocationHandlerProxyTestCase extends TestCase {

    private JavaInterfaceProcessorRegistry registry = new JavaInterfaceProcessorRegistryImpl();
    private OutboundWire wire;
    private Method clientHello;
    private TargetInvoker targetInvoker;

    public void testDifferentInterface() throws Throwable {
        JDKOutboundInvocationHandler handler = new JDKOutboundInvocationHandler(Client.class, wire, null);
        handler.invoke(null, clientHello, null);
        EasyMock.verify(targetInvoker);
    }

    protected void setUp() throws Exception {
        super.setUp();
        wire = new OutboundWireImpl();
        JavaServiceContract contract = registry.introspect(Target.class);
        for (Operation<?> operation : contract.getOperations().values()) {
            OutboundInvocationChain chain = new OutboundInvocationChainImpl(operation);
            wire.addInvocationChain(operation, chain);
            targetInvoker = EasyMock.createMock(TargetInvoker.class);
            EasyMock.expect(targetInvoker.invokeTarget(EasyMock.isNull(), EasyMock.eq(TargetInvoker.NONE)))
                .andReturn(new MessageImpl());
            EasyMock.expect(targetInvoker.isCacheable()).andReturn(false);
            EasyMock.replay(targetInvoker);
            chain.setTargetInvoker(targetInvoker);
        }
        wire.setServiceContract(contract);
        wire.setUri(URI.create("foo#bar"));
        clientHello = Client.class.getMethod("hello");

    }

    private interface Target {
        String hello();
    }

    private interface Client {
        String hello();
    }

}
