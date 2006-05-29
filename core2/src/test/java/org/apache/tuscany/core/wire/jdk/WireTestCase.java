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
import org.apache.tuscany.core.wire.OutboundInvocationChainImpl;
import org.apache.tuscany.core.wire.InboundInvocationChainImpl;
import org.apache.tuscany.core.mock.wire.MockStaticInvoker;
import org.apache.tuscany.core.mock.wire.MockSyncInterceptor;
import org.apache.tuscany.core.mock.component.SimpleTarget;
import org.apache.tuscany.core.mock.component.SimpleTargetImpl;

public class WireTestCase extends TestCase {

    private Method hello;

    public WireTestCase(String arg0) {
        super(arg0);
    }

    public void setUp() throws Exception {
        hello = SimpleTarget.class.getMethod("hello", String.class);
    }

    public void testReferenceWire() throws Exception {
        OutboundInvocationChainImpl source = new OutboundInvocationChainImpl(hello);
        MockSyncInterceptor sourceInterceptor = new MockSyncInterceptor();
        source.addInterceptor(sourceInterceptor);
        source.setTargetInterceptor(new InvokerInterceptor());
        source.setTargetInvoker(new MockStaticInvoker(hello, new SimpleTargetImpl()));
        source.build();
        JDKOutboundWire<SimpleTarget> wire = new JDKOutboundWire<SimpleTarget>();
        wire.setReferenceName("foo");
        wire.addInvocationChain(hello, source);
        wire.setBusinessInterface(SimpleTarget.class);
        SimpleTarget instance = wire.getTargetService();
        assertEquals("foo", instance.hello("foo"));
    }

    public void testServiceWire() throws Exception {
        InboundInvocationChainImpl chain = new InboundInvocationChainImpl(hello);
        MockSyncInterceptor sourceInterceptor = new MockSyncInterceptor();
        chain.addInterceptor(sourceInterceptor);
        chain.addInterceptor(new InvokerInterceptor());
        chain.setTargetInvoker(new MockStaticInvoker(hello, new SimpleTargetImpl()));
        chain.build();
        JDKInboundWire<SimpleTarget> wire = new JDKInboundWire<SimpleTarget>();
        wire.addInvocationChain(hello, chain);
        wire.setBusinessInterface(SimpleTarget.class);
        SimpleTarget instance = wire.getTargetService();
        assertEquals("foo", instance.hello("foo"));
    }
}
