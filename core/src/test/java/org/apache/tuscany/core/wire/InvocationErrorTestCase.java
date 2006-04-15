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

import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.tuscany.core.wire.impl.InvokerInterceptor;
import org.apache.tuscany.core.wire.jdk.JDKInvocationHandler;
import org.apache.tuscany.core.wire.mock.MockHandler;
import org.apache.tuscany.core.wire.mock.MockStaticInvoker;
import org.apache.tuscany.core.wire.mock.MockSyncInterceptor;
import org.apache.tuscany.core.message.impl.MessageFactoryImpl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * Tests handling of exceptions thrown during an wire
 * 
 * @version $Rev: 377006 $ $Date: 2006-02-11 09:41:59 -0800 (Sat, 11 Feb 2006) $
 */
public class InvocationErrorTestCase extends TestCase {

    private Method checkedMethod;
    private Method runtimeMethod;

    public InvocationErrorTestCase() {
        super();
    }

    public InvocationErrorTestCase(String arg0) {
        super(arg0);
    }

    public void setUp() throws Exception {
        checkedMethod = TestBean.class.getDeclaredMethod("checkedException", (Class[]) null);
        runtimeMethod = TestBean.class.getDeclaredMethod("runtimeException", (Class[]) null);
        Assert.assertNotNull(checkedMethod);
        Assert.assertNotNull(runtimeMethod);
    }

    public void testCheckedException() throws Exception {
        Map<Method, InvocationConfiguration> config = new MethodHashMap();
        config.put(checkedMethod, getConfiguration(checkedMethod));
        InvocationHandler handler = new JDKInvocationHandler(new MessageFactoryImpl(), config);
        try {
            TestBean proxy = (TestBean) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                    new Class[]{TestBean.class}, handler);
            proxy.checkedException();
        } catch (TestException e) {
            return;
        }
        Assert.fail(TestException.class.getName() + " should have been thrown");
    }

    public void testRuntimeException() throws Exception {
        Map<Method, InvocationConfiguration> config = new MethodHashMap();
        config.put(runtimeMethod, getConfiguration(runtimeMethod));
        InvocationHandler handler = new JDKInvocationHandler(new MessageFactoryImpl(), config);
        try {
            TestBean proxy = (TestBean) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                    new Class[]{TestBean.class}, handler);
            proxy.runtimeException();
        } catch (TestRuntimeException e) {
            return;
        }
        Assert.fail(TestException.class.getName() + " should have been thrown");
    }

    private InvocationConfiguration getConfiguration(Method m) {
        MockStaticInvoker invoker = new MockStaticInvoker(m, new TestBeanImpl());
        InvocationConfiguration invocationConfiguration=new InvocationConfiguration(m);
        invocationConfiguration.addSourceInterceptor(new MockSyncInterceptor());
        invocationConfiguration.addRequestHandler(new MockHandler());
        invocationConfiguration.setTargetInvoker(invoker);
        invocationConfiguration.addTargetInterceptor(new InvokerInterceptor());
        invocationConfiguration.build();
        return invocationConfiguration;
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
