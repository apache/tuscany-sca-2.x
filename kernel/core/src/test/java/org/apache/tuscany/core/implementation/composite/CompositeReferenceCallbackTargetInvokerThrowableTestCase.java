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
package org.apache.tuscany.core.implementation.composite;

import java.lang.reflect.Type;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.IAnswer;

/**
 * @version $Rev$ $Date$
 */
public class CompositeReferenceCallbackTargetInvokerThrowableTestCase extends TestCase {
    private InboundWire wire;
    private Message message;
    private OutboundInvocationChain chain;
    private Interceptor head;
    private CompositeReferenceCallbackTargetInvoker invoker;

    /**
     * Verfies an exception thrown in the target is propagated to the client correctly
     */
    public void testThrowableTargetInvocation() throws Exception {
        Message response = invoker.invoke(message);
        assertTrue(response.isFault());
        Object body = response.getBody();
        if (!(body instanceof UndeclaredThrowableException)) {
            fail(); // EasyMock wraps the Throwable in an UndeclaredThrowableException
        }
        UndeclaredThrowableException e = (UndeclaredThrowableException) body;
        assertTrue(InsidiousException.class.equals(e.getUndeclaredThrowable().getClass()));
        EasyMock.verify(wire);
        EasyMock.verify(chain);
        EasyMock.verify(head);
    }

    @SuppressWarnings("unchecked")
    protected void setUp() throws Exception {
        super.setUp();
        Object targetAddress = new Object();
        message = new MessageImpl();
        message.pushFromAddress(targetAddress);
        message.setBody("foo");
        Message response = new MessageImpl();
        response.setBody("response");
        Operation<Type> operation = new Operation<Type>("echo", null, null, null);
        head = EasyMock.createMock(Interceptor.class);
        EasyMock.expect(head.invoke(EasyMock.isA(Message.class))).andStubAnswer(new IAnswer() {
            public Object answer() throws Throwable {
                throw new InsidiousException();   // andThrow() does not seem to work here
            }
        });
        EasyMock.replay(head);
        chain = EasyMock.createMock(OutboundInvocationChain.class);
        EasyMock.expect(chain.getTargetInvoker()).andReturn(null);
        EasyMock.expect(chain.getHeadInterceptor()).andReturn(head);
        EasyMock.replay(chain);
        Map<Operation<?>, OutboundInvocationChain> chains = new HashMap<Operation<?>, OutboundInvocationChain>();
        chains.put(operation, chain);
        wire = EasyMock.createMock(InboundWire.class);
        EasyMock.expect(wire.getSourceCallbackInvocationChains(targetAddress)).andReturn(chains);
        EasyMock.replay(wire);
        invoker = new CompositeReferenceCallbackTargetInvoker(operation, wire);
    }

    private class InsidiousException extends Throwable {

    }


}
