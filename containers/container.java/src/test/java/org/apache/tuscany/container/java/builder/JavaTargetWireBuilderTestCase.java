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
package org.apache.tuscany.container.java.builder;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.container.java.config.JavaContextFactory;
import org.apache.tuscany.container.java.invocation.mock.MockHandler;
import org.apache.tuscany.container.java.invocation.mock.MockSyncInterceptor;
import org.apache.tuscany.container.java.invocation.mock.SimpleTarget;
import org.apache.tuscany.container.java.invocation.mock.SimpleTargetImpl;
import org.apache.tuscany.container.java.mock.MockScopeContext;
import org.apache.tuscany.core.builder.impl.DefaultWireBuilder;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.wire.MethodHashMap;
import org.apache.tuscany.core.wire.impl.InvokerInterceptor;
import org.apache.tuscany.core.wire.jdk.JDKWireFactoryFactory;
import org.apache.tuscany.core.wire.WireSourceConfiguration;
import org.apache.tuscany.core.wire.WireTargetConfiguration;
import org.apache.tuscany.core.wire.SourceInvocationConfiguration;
import org.apache.tuscany.core.wire.TargetInvocationConfiguration;
import org.apache.tuscany.core.wire.TargetWireFactory;
import org.apache.tuscany.core.wire.SourceWireFactory;
import org.apache.tuscany.core.message.Message;
import org.apache.tuscany.core.message.MessageFactory;
import org.apache.tuscany.core.message.impl.MessageFactoryImpl;

public class JavaTargetWireBuilderTestCase extends TestCase {

    private Method hello;

    public JavaTargetWireBuilderTestCase() {
    }

    public JavaTargetWireBuilderTestCase(String arg0) {
        super(arg0);
    }

    public void setUp() throws Exception {
        hello = SimpleTarget.class.getMethod("hello", String.class);
    }
    
    
    /**
     * Tests basic wiring of a source to a target, including handlers and interceptors
     */
    public void testInvocation() throws Exception {
        MessageFactory msgFactory = new MessageFactoryImpl();

        SourceInvocationConfiguration source = new SourceInvocationConfiguration(hello);
        MockHandler sourceRequestHandler = new MockHandler();
        MockHandler sourceResponseHandler = new MockHandler();
        MockSyncInterceptor sourceInterceptor = new MockSyncInterceptor();
        source.addRequestHandler(sourceRequestHandler);
        source.addResponseHandler(sourceResponseHandler);
        source.addInterceptor(sourceInterceptor);

        SourceWireFactory sourceFactory = new JDKWireFactoryFactory().createSourceWireFactory();
        Map<Method, SourceInvocationConfiguration> sourceInvocationConfigs = new MethodHashMap<SourceInvocationConfiguration>();
        sourceInvocationConfigs.put(hello, source);
        WireSourceConfiguration sourceConfig = new WireSourceConfiguration("foo",new QualifiedName("target/SimpleTarget"),
                sourceInvocationConfigs, Thread.currentThread().getContextClassLoader(), msgFactory);
        sourceFactory.setConfiguration(sourceConfig);
        sourceFactory.setBusinessInterface(SimpleTarget.class);
        
        TargetInvocationConfiguration target = new TargetInvocationConfiguration(hello);
        MockHandler targetRequestHandler = new MockHandler();
        MockHandler targetResponseHandler = new MockHandler();
        MockSyncInterceptor targetInterceptor = new MockSyncInterceptor();
        target.addRequestHandler(targetRequestHandler);
        target.addResponseHandler(targetResponseHandler);
        target.addInterceptor(targetInterceptor);
        target.addInterceptor(new InvokerInterceptor());

        TargetWireFactory targetFactory = new JDKWireFactoryFactory().createTargetWireFactory();
        Map<Method, TargetInvocationConfiguration> targetInvocationConfigs = new MethodHashMap<TargetInvocationConfiguration>();
        targetInvocationConfigs.put(hello, target);
        WireTargetConfiguration targetConfig = new WireTargetConfiguration(new QualifiedName("target/SimpleTarget"),
                targetInvocationConfigs, Thread.currentThread().getContextClassLoader(), msgFactory);
        targetFactory.setConfiguration(targetConfig);
        targetFactory.setBusinessInterface(SimpleTarget.class);

        // bootstrap a scope container with the target in it
        Map<String,Object> instances = new HashMap<String,Object>();
        SimpleTarget simpleTarget = new SimpleTargetImpl();
        instances.put("target",simpleTarget);
        MockScopeContext scopeCtx = new MockScopeContext(instances);

        // connect the source to the target
        DefaultWireBuilder builder = new DefaultWireBuilder();
        builder.addWireBuilder(new JavaTargetWireBuilder());
        
        builder.connect(sourceFactory, targetFactory, JavaContextFactory.class, true, scopeCtx);
        source.build();
        target.build();
        Assert.assertNotNull(source.getTargetInvoker());
        
        Message msg = msgFactory.createMessage();
        msg.setBody("foo");
        msg.setTargetInvoker(source.getTargetInvoker());
        Message response = source.getHeadInterceptor().invoke(msg);
        Assert.assertEquals("foo", response.getBody());
        Assert.assertEquals(1, sourceRequestHandler.getCount());
        Assert.assertEquals(1, sourceResponseHandler.getCount());
        Assert.assertEquals(1, sourceInterceptor.getCount());
        Assert.assertEquals(1, targetRequestHandler.getCount());
        Assert.assertEquals(1, targetResponseHandler.getCount());
        Assert.assertEquals(1, targetInterceptor.getCount());
    }


}

