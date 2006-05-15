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
package org.apache.tuscany.core.wire.jdk;

import java.lang.reflect.Method;

import junit.framework.TestCase;
import org.apache.tuscany.core.wire.InvokerInterceptor;
import org.apache.tuscany.core.wire.SourceInvocationChainImpl;
import org.apache.tuscany.core.wire.TargetInvocationChainImpl;
import org.apache.tuscany.core.wire.mock.MockStaticInvoker;
import org.apache.tuscany.core.wire.mock.MockSyncInterceptor;
import org.apache.tuscany.core.wire.mock.SimpleTarget;
import org.apache.tuscany.core.wire.mock.SimpleTargetImpl;

public class JDKWireFactoryServiceTestCase extends TestCase {

    private Method hello;

    public JDKWireFactoryServiceTestCase(String arg0) {
        super(arg0);
    }

    public void setUp() throws Exception {
        hello = SimpleTarget.class.getMethod("hello", String.class);
    }

    public void testSourceWireFactory() throws Exception {
        SourceInvocationChainImpl source = new SourceInvocationChainImpl(hello);
        MockSyncInterceptor sourceInterceptor = new MockSyncInterceptor();
        source.addInterceptor(sourceInterceptor);
        source.setTargetInterceptor(new InvokerInterceptor());
        source.setTargetInvoker(new MockStaticInvoker(hello, new SimpleTargetImpl()));
        source.build();
        JDKSourceWire<SimpleTarget> factory = new JDKSourceWire<SimpleTarget>();
        factory.setReferenceName("foo");
        factory.addInvocationChain(hello, source);
        factory.setBusinessInterface(SimpleTarget.class);
        SimpleTarget instance = factory.createProxy();
        assertEquals("foo", instance.hello("foo"));
    }

    public void testTargetWireFactory() throws Exception {
        TargetInvocationChainImpl source = new TargetInvocationChainImpl(hello);
        MockSyncInterceptor sourceInterceptor = new MockSyncInterceptor();
        source.addInterceptor(sourceInterceptor);
        source.addInterceptor(new InvokerInterceptor());
        source.setTargetInvoker(new MockStaticInvoker(hello, new SimpleTargetImpl()));
        source.build();
        JDKTargetWire<SimpleTarget> factory = new JDKTargetWire<SimpleTarget>();
        factory.addInvocationChain(hello, source);
        factory.setBusinessInterface(SimpleTarget.class);
        SimpleTarget instance = factory.createProxy();
        assertEquals("foo", instance.hello("foo"));
    }
}
