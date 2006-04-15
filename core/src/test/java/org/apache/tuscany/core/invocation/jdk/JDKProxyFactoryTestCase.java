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

import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.invocation.InvocationConfiguration;
import org.apache.tuscany.core.invocation.MethodHashMap;
import org.apache.tuscany.core.invocation.WireConfiguration;
import org.apache.tuscany.core.invocation.impl.InvokerInterceptor;
import org.apache.tuscany.core.invocation.mock.MockStaticInvoker;
import org.apache.tuscany.core.invocation.mock.MockSyncInterceptor;
import org.apache.tuscany.core.invocation.mock.SimpleTarget;
import org.apache.tuscany.core.invocation.mock.SimpleTargetImpl;
import org.apache.tuscany.core.message.impl.MessageFactoryImpl;

import java.lang.reflect.Method;
import java.util.Map;

public class JDKProxyFactoryTestCase extends TestCase {

    private Method hello;

    public JDKProxyFactoryTestCase(String arg0) {
        super(arg0);
    }

    public void setUp() throws Exception {
        hello = SimpleTarget.class.getMethod("hello", String.class);
    }

    public void testProxyFactory() throws Exception {
        InvocationConfiguration source = new InvocationConfiguration(hello);
        MockSyncInterceptor sourceInterceptor = new MockSyncInterceptor();
        source.addSourceInterceptor(sourceInterceptor);
        source.addTargetInterceptor(new InvokerInterceptor());
        source.setTargetInvoker(new MockStaticInvoker(hello, new SimpleTargetImpl()));
        source.build();
        Map<Method, InvocationConfiguration> configs = new MethodHashMap();
        configs.put(hello, source);
        WireConfiguration config = new WireConfiguration(new QualifiedName("foo"), configs, Thread.currentThread()
                .getContextClassLoader(), new MessageFactoryImpl());
        JDKProxyFactory factory = new JDKProxyFactory();
        factory.setProxyConfiguration(config);
        factory.setBusinessInterface(SimpleTarget.class);
        factory.initialize();
        SimpleTarget instance = (SimpleTarget) factory.createProxy();
        Assert.assertEquals("foo",instance.hello("foo"));
    }
}
