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

import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.wire.TargetInvoker;

import junit.framework.TestCase;
import org.apache.tuscany.core.mock.wire.MockTargetInvoker;
import org.easymock.EasyMock;
import org.easymock.IAnswer;

/**
 * @version $Rev$ $Date$
 */
public class AbstractLocalTargetInvokerTestCase extends TestCase {

    public void testInvokerWithInterceptor() throws Throwable {
        AbstractLocalTargetInvoker invoker = new MockTargetInvoker();
        Interceptor interceptor = EasyMock.createMock(Interceptor.class);
        interceptor.invoke(EasyMock.isA(Message.class));
        EasyMock.expectLastCall().andStubAnswer(new IAnswer<Object>() {
            public Object answer() throws Throwable {
                Message msg = (Message) EasyMock.getCurrentArguments()[0];
                if (msg.getTargetInvoker() == null) {
                    fail("Target invoker not set");
                }
                return null;
            }
        });
        EasyMock.replay(interceptor);
        InvocationChain chain = EasyMock.createMock(InvocationChain.class);
        EasyMock.expect(chain.getHeadInterceptor()).andReturn(interceptor);
        EasyMock.replay(chain);
        invoker.invoke(chain, EasyMock.createNiceMock(TargetInvoker.class), new MessageImpl());
        EasyMock.verify(chain);
        EasyMock.verify(interceptor);
    }

    public void testShortCircuitInvoke() throws Throwable {
        AbstractLocalTargetInvoker invoker = new MockTargetInvoker();
        TargetInvoker targetInvoker = EasyMock.createMock(TargetInvoker.class);
        EasyMock.expect(targetInvoker.invoke(EasyMock.isA(Message.class))).andReturn(new MessageImpl());
        EasyMock.replay(targetInvoker);
        InvocationChain chain = EasyMock.createMock(InvocationChain.class);
        EasyMock.expect(chain.getHeadInterceptor()).andReturn(null);
        EasyMock.replay(chain);
        invoker.invoke(chain, targetInvoker, new MessageImpl());
        EasyMock.verify(chain);
        EasyMock.verify(targetInvoker);
    }

}
