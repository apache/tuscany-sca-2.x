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
package org.apache.tuscany.core.binding.local;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.tuscany.idl.Operation;
import org.apache.tuscany.idl.impl.OperationImpl;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.wire.Wire;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class LocalCallbackTargetInvokerTestCase extends TestCase {
    private Wire wire;
    private Message message;
    private InvocationChain chain;
    private Interceptor head;
    private LocalCallbackTargetInvoker invoker;

    /**
     * Verfies the normal execution path through a callback
     */
    public void testNormalPathMessageInvocation() throws Exception {
        Message response = invoker.invoke(message);
        assertEquals("response", response.getBody());
        EasyMock.verify(wire);
        EasyMock.verify(chain);
        EasyMock.verify(head);
    }

    protected void setUp() throws Exception {
        super.setUp();
        URI targetAddress = URI.create("from");
        message = new MessageImpl();
        message.pushCallbackUri(targetAddress);
        message.setBody("foo");
        Message response = new MessageImpl();
        response.setBody("response");
        Operation operation = new OperationImpl();
        operation.setName("echo");
        head = EasyMock.createMock(Interceptor.class);
        EasyMock.expect(head.invoke(EasyMock.isA(Message.class))).andReturn(response);
        EasyMock.replay(head);
        chain = EasyMock.createMock(InvocationChain.class);
        EasyMock.expect(chain.getTargetInvoker()).andReturn(null);
        EasyMock.expect(chain.getHeadInterceptor()).andReturn(head);
        EasyMock.replay(chain);
        Map<Operation, InvocationChain> chains = new HashMap<Operation, InvocationChain>();
        chains.put(operation, chain);
        wire = EasyMock.createMock(Wire.class);
        EasyMock.expect(wire.getCallbackInvocationChains()).andReturn(chains);
        EasyMock.replay(wire);

        invoker = new LocalCallbackTargetInvoker(operation, wire);
    }


}
