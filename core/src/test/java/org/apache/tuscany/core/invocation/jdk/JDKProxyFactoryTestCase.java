/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.invocation.jdk;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.invocation.InvocationConfiguration;
import org.apache.tuscany.core.invocation.ProxyConfiguration;
import org.apache.tuscany.core.invocation.impl.InvokerInterceptor;
import org.apache.tuscany.core.invocation.mock.MockStaticInvoker;
import org.apache.tuscany.core.invocation.mock.MockSyncInterceptor;
import org.apache.tuscany.core.invocation.mock.SimpleTarget;
import org.apache.tuscany.core.invocation.mock.SimpleTargetImpl;
import org.apache.tuscany.core.message.impl.MessageFactoryImpl;

public class JDKProxyFactoryTestCase extends TestCase {

    private Method hello;

    private Method goodbye;

    public JDKProxyFactoryTestCase(String arg0) {
        super(arg0);
    }

    public void setUp() throws Exception {
        hello = SimpleTarget.class.getMethod("hello", new Class[] { String.class });
        goodbye = SimpleTarget.class.getMethod("goodbye", new Class[] { String.class });
    }

    public void testProxyFactory() throws Exception {
        InvocationConfiguration source = new InvocationConfiguration(hello);
        MockSyncInterceptor sourceInterceptor = new MockSyncInterceptor();
        source.addSourceInterceptor(sourceInterceptor);
        source.addTargetInterceptor(new InvokerInterceptor());
        source.setTargetInvoker(new MockStaticInvoker(hello, new SimpleTargetImpl()));
        source.build();
        Map<Method, InvocationConfiguration> configs = new HashMap();
        configs.put(hello, source);
        ProxyConfiguration config = new ProxyConfiguration(new QualifiedName("foo"), configs, Thread.currentThread()
                .getContextClassLoader(), new MessageFactoryImpl());
        JDKProxyFactory factory = new JDKProxyFactory();
        factory.setProxyConfiguration(config);
        factory.setBusinessInterface(SimpleTarget.class);
        factory.initialize();
        SimpleTarget instance = (SimpleTarget) factory.createProxy();
        Assert.assertEquals("foo",instance.hello("foo"));
    }
}
