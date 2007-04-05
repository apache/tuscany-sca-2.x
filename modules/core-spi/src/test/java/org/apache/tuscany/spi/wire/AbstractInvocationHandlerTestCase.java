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
package org.apache.tuscany.spi.wire;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.LinkedList;

import junit.framework.TestCase;
import org.easymock.EasyMock;

import org.apache.tuscany.spi.component.WorkContext;

/**
 * @version $Rev$ $Date$
 */
public class AbstractInvocationHandlerTestCase extends TestCase {
    private WorkContext workContext;

    protected void setUp() throws Exception {
        super.setUp();
        workContext = EasyMock.createMock(WorkContext.class);
        EasyMock.expect(workContext.getCorrelationId()).andStubReturn(null);
        EasyMock.expect(workContext.getCallbackUris()).andStubReturn(null);
        EasyMock.replay(workContext);
    }

    public void testInvocation() throws Throwable {
        InvocationHandler handler = new InvocationHandler();
        Interceptor interceptor = new MockInterceptor();
        TargetInvoker invoker = EasyMock.createMock(TargetInvoker.class);
        EasyMock.replay(invoker);
        InvocationChain chain = EasyMock.createMock(InvocationChain.class);
        EasyMock.expect(chain.getHeadInterceptor()).andReturn(interceptor);
        EasyMock.replay(chain);
        Object resp = handler.invoke(chain, invoker, new String[]{"foo"}, null, new LinkedList<URI>(), workContext);
        assertEquals("response", resp);
    }

    public void testShortCircuitInvocation() throws Throwable {
        InvocationHandler handler = new InvocationHandler();
        TargetInvoker invoker = new MockInvoker();
        InvocationChain chain = EasyMock.createMock(InvocationChain.class);
        EasyMock.expect(chain.getHeadInterceptor()).andReturn(null);
        EasyMock.expect(chain.getTargetInvoker()).andReturn(invoker);
        EasyMock.replay(chain);
        Object resp = handler.invoke(chain, invoker, new String[]{"foo"}, null, new LinkedList<URI>(), workContext);
        assertEquals("response", resp);
    }


    private class MockInvoker implements TargetInvoker {

        public Object invokeTarget(final Object payload, final short sequence, WorkContext workContext) throws InvocationTargetException {
            assertEquals("foo", Array.get(payload, 0));
            return "response";
        }

        public Message invoke(Message msg) throws InvocationRuntimeException {
            fail();
            return null;
        }

        public boolean isCacheable() {
            return false;
        }

        public void setCacheable(boolean cacheable) {

        }

        public boolean isOptimizable() {
            return false;
        }

        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }

    private class InvocationHandler extends AbstractInvocationHandler {

        protected URI getFromAddress() {
            return URI.create("from");
        }

    }

    private class MockInterceptor implements Interceptor {

        public Message invoke(Message msg) {
            assertNotNull(msg.getCorrelationId());
            assertNotNull(msg.getTargetInvoker());
            assertNotNull(msg.getMessageId());
            assertNotNull(msg.getCallbackUris());
            assertEquals("foo", Array.get(msg.getBody(), 0));
            msg.setBody("response");
            return msg;
        }

        public void setNext(Interceptor next) {

        }

        public Interceptor getNext() {
            return null;
        }

        public boolean isOptimizable() {
            return false;
        }
    }


}
