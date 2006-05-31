/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.core.wire;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

import junit.framework.TestCase;
import org.apache.tuscany.core.mock.wire.MockHandler;
import org.apache.tuscany.core.mock.wire.MockStaticInvoker;
import org.apache.tuscany.core.mock.wire.MockSyncInterceptor;
import org.apache.tuscany.core.util.MethodHashMap;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.ReferenceInvocationHandler;

/**
 * Tests handling of exceptions thrown during an outbound wire invocation
 *
 * @version $Rev: 377006 $ $Date: 2006-02-11 09:41:59 -0800 (Sat, 11 Feb 2006) $
 */
public class OutboundInvocationErrorTestCase extends TestCase {

    private Method checkedMethod;
    private Method runtimeMethod;

    public OutboundInvocationErrorTestCase() {
        super();
    }

    public OutboundInvocationErrorTestCase(String arg0) {
        super(arg0);
    }

    public void setUp() throws Exception {
        checkedMethod = TestBean.class.getDeclaredMethod("checkedException", (Class[]) null);
        runtimeMethod = TestBean.class.getDeclaredMethod("runtimeException", (Class[]) null);
        assertNotNull(checkedMethod);
        assertNotNull(runtimeMethod);
    }

    public void testCheckedException() throws Exception {
        Map<Method, OutboundInvocationChain> chains = new MethodHashMap<OutboundInvocationChain>();
        chains.put(checkedMethod, createChain(checkedMethod));
        ReferenceInvocationHandler handler = new ReferenceInvocationHandler(chains);
        try {
            TestBean proxy = (TestBean) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                    new Class[]{TestBean.class}, handler);
            proxy.checkedException();
        } catch (TestException e) {
            return;
        }
        fail(TestException.class.getName() + " should have been thrown");
    }

    public void testRuntimeException() throws Exception {
        Map<Method, OutboundInvocationChain> chains = new MethodHashMap<OutboundInvocationChain>();
        chains.put(runtimeMethod, createChain(runtimeMethod));
        ReferenceInvocationHandler handler = new ReferenceInvocationHandler(chains);
        try {
            TestBean proxy = (TestBean) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                    new Class[]{TestBean.class}, handler);
            proxy.runtimeException();
        } catch (TestRuntimeException e) {
            return;
        }
        fail(TestException.class.getName() + " should have been thrown");
    }

    private OutboundInvocationChain createChain(Method m) {
        MockStaticInvoker invoker = new MockStaticInvoker(m, new TestBeanImpl());
        OutboundInvocationChain chain = new OutboundInvocationChainImpl(m);
        chain.addInterceptor(new MockSyncInterceptor());
        chain.addRequestHandler(new MockHandler());
        chain.setTargetInvoker(invoker);
        chain.setTargetInterceptor(new InvokerInterceptor());
        chain.build();
        return chain;
    }

    public interface TestBean {

        public void checkedException() throws TestException;

        public void runtimeException() throws TestRuntimeException;

    }

    public class TestBeanImpl implements TestBean {

        public void checkedException() throws TestException {
            throw new TestException();
        }

        public void runtimeException() throws TestRuntimeException {
            throw new TestRuntimeException();
        }
    }

    public class TestException extends Exception {
    }

    public class TestRuntimeException extends RuntimeException {
    }

}
