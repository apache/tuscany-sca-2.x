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
import java.util.Map;

import junit.framework.TestCase;
import org.apache.tuscany.core.util.MethodHashMap;
import org.apache.tuscany.core.wire.mock.MockStaticInvoker;
import org.apache.tuscany.core.wire.mock.MockSyncInterceptor;
import org.apache.tuscany.core.wire.mock.SimpleTarget;
import org.apache.tuscany.core.wire.mock.SimpleTargetImpl;
import org.apache.tuscany.core.wire.SourceInvocationConfigurationImpl;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.core.wire.InvokerInterceptor;
import org.apache.tuscany.core.wire.TargetInvocationConfigurationImpl;
import org.apache.tuscany.core.wire.WireSourceConfigurationImpl;
import org.apache.tuscany.core.wire.WireTargetConfigurationImpl;
import org.apache.tuscany.spi.wire.SourceInvocationConfiguration;
import org.apache.tuscany.spi.wire.TargetInvocationConfiguration;
import org.apache.tuscany.spi.wire.WireSourceConfiguration;
import org.apache.tuscany.spi.wire.WireTargetConfiguration;

public class JDKWireFactoryFactoryTestCase extends TestCase {

    private Method hello;

    public JDKWireFactoryFactoryTestCase(String arg0) {
        super(arg0);
    }

    public void setUp() throws Exception {
        hello = SimpleTarget.class.getMethod("hello", String.class);
    }

    public void testSourceWireFactory() throws Exception {
        SourceInvocationConfigurationImpl source = new SourceInvocationConfigurationImpl(hello);
        MockSyncInterceptor sourceInterceptor = new MockSyncInterceptor();
        source.addInterceptor(sourceInterceptor);
        source.setTargetInterceptor(new InvokerInterceptor());
        source.setTargetInvoker(new MockStaticInvoker(hello, new SimpleTargetImpl()));
        source.build();
        Map<Method, SourceInvocationConfiguration> configs = new MethodHashMap<SourceInvocationConfiguration>();
        configs.put(hello, source);
        WireSourceConfiguration config = new WireSourceConfigurationImpl("foo", new QualifiedName("foo"), configs);
        JDKSourceWireFactory<SimpleTarget> factory = new JDKSourceWireFactory<SimpleTarget>();
        factory.setConfiguration(config);
        factory.setBusinessInterface(SimpleTarget.class);
        factory.initialize();
        SimpleTarget instance = factory.createProxy();
        assertEquals("foo", instance.hello("foo"));
    }

    public void testTargetWireFactory() throws Exception {
        TargetInvocationConfigurationImpl source = new TargetInvocationConfigurationImpl(hello);
        MockSyncInterceptor sourceInterceptor = new MockSyncInterceptor();
        source.addInterceptor(sourceInterceptor);
        source.addInterceptor(new InvokerInterceptor());
        source.setTargetInvoker(new MockStaticInvoker(hello, new SimpleTargetImpl()));
        source.build();
        Map<Method, TargetInvocationConfiguration> configs = new MethodHashMap<TargetInvocationConfiguration>();
        configs.put(hello, source);
        WireTargetConfiguration config = new WireTargetConfigurationImpl("Foo", configs);
        JDKTargetWireFactory<SimpleTarget> factory = new JDKTargetWireFactory<SimpleTarget>();
        factory.setConfiguration(config);
        factory.setBusinessInterface(SimpleTarget.class);
        factory.initialize();
        SimpleTarget instance = factory.createProxy();
        assertEquals("foo", instance.hello("foo"));
    }
}
