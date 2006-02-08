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
package org.apache.tuscany.container.java.invocation.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.container.java.handler.StaticJavaComponentTargetInvoker;
import org.apache.tuscany.container.java.invocation.mock.MockHandler;
import org.apache.tuscany.container.java.invocation.mock.MockJavaOperationType;
import org.apache.tuscany.container.java.invocation.mock.MockSyncInterceptor;
import org.apache.tuscany.container.java.invocation.mock.SimpleTarget;
import org.apache.tuscany.container.java.invocation.mock.SimpleTargetImpl;
import org.apache.tuscany.core.invocation.InvocationConfiguration;
import org.apache.tuscany.core.invocation.jdk.JDKInvocationHandler;
import org.apache.tuscany.core.message.impl.MessageFactoryImpl;
import org.apache.tuscany.model.types.OperationType;

/**
 * Tests handling of invocations
 * 
 * @version $Rev$ $Date$
 */
public class JDKInvocationHandlerTestCase extends TestCase {

    private Method hello;
    private Method goodbye;

    public void setUp() throws Exception {
        hello = SimpleTarget.class.getMethod("hello", new Class[]{String.class});
        goodbye = SimpleTarget.class.getMethod("goodbye", new Class[]{String.class});
    }

    public void testCreation() throws Exception {
        Map<Method, InvocationConfiguration> config = new HashMap();
        config.put(hello, getConfiguration(hello));
        config.put(goodbye, getConfiguration(goodbye));
        InvocationHandler handler = new JDKInvocationHandler(new MessageFactoryImpl(), config);
        SimpleTarget proxy = (SimpleTarget) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{SimpleTarget.class}, handler);
        Assert.assertEquals("hello", proxy.hello("hello"));
    }

    public void testInterceptorsOnly() throws Exception {
        Map<Method, InvocationConfiguration> config = new HashMap();
        OperationType operation = new MockJavaOperationType(hello);
        StaticJavaComponentTargetInvoker invoker = new StaticJavaComponentTargetInvoker(hello, new SimpleTargetImpl());
        InvocationConfiguration invocationConfiguration = new InvocationConfiguration(operation);
        invocationConfiguration.addSourceInterceptor(new MockSyncInterceptor());
        invocationConfiguration.addTargetInterceptor(new MockSyncInterceptor());
        invocationConfiguration.setTargetInvoker(invoker);
        invocationConfiguration.build();
        InvocationConfiguration helloConfig = invocationConfiguration;
        config.put(hello, helloConfig);
        InvocationHandler handler = new JDKInvocationHandler(new MessageFactoryImpl(), config);
        SimpleTarget proxy = (SimpleTarget) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{SimpleTarget.class}, handler);
        Assert.assertEquals("hello", proxy.hello("hello"));
    }

    public void testSourceInterceptoOnly() throws Exception {
        Map<Method, InvocationConfiguration> config = new HashMap();
        OperationType operation = new MockJavaOperationType(hello);
        StaticJavaComponentTargetInvoker invoker = new StaticJavaComponentTargetInvoker(hello, new SimpleTargetImpl());
        InvocationConfiguration invocationConfiguration = new InvocationConfiguration(operation);
        invocationConfiguration.addSourceInterceptor(new MockSyncInterceptor());
        invocationConfiguration.setTargetInvoker(invoker);
        invocationConfiguration.build();
        InvocationConfiguration helloConfig = invocationConfiguration;
        config.put(hello, helloConfig);
        InvocationHandler handler = new JDKInvocationHandler(new MessageFactoryImpl(), config);
        SimpleTarget proxy = (SimpleTarget) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{SimpleTarget.class}, handler);
        Assert.assertEquals("hello", proxy.hello("hello"));
    }

    public void testTargetInterceptorOnly() throws Exception {
        Map<Method, InvocationConfiguration> config = new HashMap();
        OperationType operation = new MockJavaOperationType(hello);
        StaticJavaComponentTargetInvoker invoker = new StaticJavaComponentTargetInvoker(hello, new SimpleTargetImpl());
        InvocationConfiguration invocationConfiguration = new InvocationConfiguration(operation);
        invocationConfiguration.addTargetInterceptor(new MockSyncInterceptor());
        invocationConfiguration.setTargetInvoker(invoker);
        invocationConfiguration.build();
        InvocationConfiguration helloConfig = invocationConfiguration;
        config.put(hello, helloConfig);
        InvocationHandler handler = new JDKInvocationHandler(new MessageFactoryImpl(), config);
        SimpleTarget proxy = (SimpleTarget) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{SimpleTarget.class}, handler);
        Assert.assertEquals("hello", proxy.hello("hello"));
    }

    public void testHandlerAndTargetInterceptor() throws Exception {
        Map<Method, InvocationConfiguration> config = new HashMap();
        OperationType operation = new MockJavaOperationType(hello);
        StaticJavaComponentTargetInvoker invoker = new StaticJavaComponentTargetInvoker(hello, new SimpleTargetImpl());
        InvocationConfiguration invocationConfiguration = new InvocationConfiguration(operation);
        invocationConfiguration.addRequestHandler(new MockHandler());
        invocationConfiguration.addTargetInterceptor(new MockSyncInterceptor());
        invocationConfiguration.setTargetInvoker(invoker);
        invocationConfiguration.build();
        InvocationConfiguration helloConfig = invocationConfiguration;
        config.put(hello, helloConfig);
        InvocationHandler handler = new JDKInvocationHandler(new MessageFactoryImpl(), config);
        SimpleTarget proxy = (SimpleTarget) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{SimpleTarget.class}, handler);
        Assert.assertEquals("hello", proxy.hello("hello"));
    }

    private InvocationConfiguration getConfiguration(Method m) {
        OperationType operation = new MockJavaOperationType(m);
        StaticJavaComponentTargetInvoker invoker = new StaticJavaComponentTargetInvoker(m, new SimpleTargetImpl());
        InvocationConfiguration invocationConfiguration = new InvocationConfiguration(operation);
        invocationConfiguration.addRequestHandler(new MockHandler());
        invocationConfiguration.addSourceInterceptor(new MockSyncInterceptor());
        invocationConfiguration.setTargetInvoker(invoker);
        invocationConfiguration.build();
        return invocationConfiguration;
    }
}
