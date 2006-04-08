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
package org.apache.tuscany.container.java.integration;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.container.java.builder.JavaContextFactoryBuilder;
import org.apache.tuscany.container.java.builder.JavaTargetWireBuilder;
import org.apache.tuscany.container.java.builder.MockHandlerBuilder;
import org.apache.tuscany.container.java.builder.MockInterceptorBuilder;
import org.apache.tuscany.container.java.invocation.mock.MockHandler;
import org.apache.tuscany.container.java.invocation.mock.MockSyncInterceptor;
import org.apache.tuscany.container.java.mock.MockFactory;
import org.apache.tuscany.container.java.mock.components.GenericComponent;
import org.apache.tuscany.core.builder.ContextFactoryBuilder;
import org.apache.tuscany.core.builder.impl.DefaultWireBuilder;
import org.apache.tuscany.core.context.CompositeContext;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.invocation.jdk.JDKProxyFactoryFactory;
import org.apache.tuscany.core.invocation.spi.ProxyFactoryFactory;
import org.apache.tuscany.core.message.MessageFactory;
import org.apache.tuscany.core.message.impl.MessageFactoryImpl;
import org.apache.tuscany.core.runtime.RuntimeContext;
import org.apache.tuscany.core.runtime.RuntimeContextImpl;
import org.apache.tuscany.core.system.builder.SystemContextFactoryBuilder;
import org.apache.tuscany.core.system.builder.SystemEntryPointBuilder;
import org.apache.tuscany.core.system.builder.SystemExternalServiceBuilder;

/**
 * Verifies that the composite context implementation and java component builders construct references properly
 * 
 * @version $Rev$ $Date$
 */
public class JavaBuilderContextIntegrationTestCase extends TestCase {

    public JavaBuilderContextIntegrationTestCase(String arg0) {
        super(arg0);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testRefWithSourceInterceptor() throws Exception {
        MessageFactory msgFactory = new MessageFactoryImpl();

        List<ContextFactoryBuilder> builders = new ArrayList<ContextFactoryBuilder>();
        builders.add((new SystemContextFactoryBuilder(null)));
        builders.add(new SystemEntryPointBuilder());
        builders.add(new SystemExternalServiceBuilder());

        ProxyFactoryFactory proxyFactoryFactory =new JDKProxyFactoryFactory();
        
        JavaContextFactoryBuilder javaBuilder = new JavaContextFactoryBuilder();
        javaBuilder.setMessageFactory(msgFactory);
        javaBuilder.setProxyFactoryFactory(proxyFactoryFactory);

        MockSyncInterceptor mockInterceptor = new MockSyncInterceptor();
        MockInterceptorBuilder interceptorBuilder = new MockInterceptorBuilder(mockInterceptor, true);
        //HierarchicalBuilder refBuilder = new HierarchicalBuilder();
        //refBuilder.addBuilder(interceptorBuilder);
        javaBuilder.addPolicyBuilder(interceptorBuilder);
        builders.add(javaBuilder);

        DefaultWireBuilder defaultWireBuilder = new DefaultWireBuilder();

        RuntimeContext runtime = new RuntimeContextImpl(null, builders, defaultWireBuilder);
        runtime.addBuilder(new JavaTargetWireBuilder());
        runtime.start();
        runtime.getRootContext().registerModelObject(
                MockFactory.createCompositeComponent("test.module"));
        CompositeContext child = (CompositeContext) runtime.getRootContext().getContext("test.module");
        child.registerModelObject(MockFactory.createModule());
        child.fireEvent(EventContext.MODULE_START, null);
        GenericComponent source = (GenericComponent) child.getContext("source").getInstance(null);
        Assert.assertNotNull(source);
        source.getGenericComponent().getString();
        Assert.assertEquals(1, mockInterceptor.getCount());
        source.getGenericComponent().getString();
        Assert.assertEquals(2, mockInterceptor.getCount());
        child.fireEvent(EventContext.MODULE_STOP, null);
        runtime.stop();
    }

    public void testRefWithSourceInterceptorHandler() throws Exception {
        MessageFactory msgFactory = new MessageFactoryImpl();

        List<ContextFactoryBuilder> builders = new ArrayList<ContextFactoryBuilder>();
        builders.add((new SystemContextFactoryBuilder(null)));
        builders.add(new SystemEntryPointBuilder());
        builders.add(new SystemExternalServiceBuilder());

        JavaContextFactoryBuilder javaBuilder = new JavaContextFactoryBuilder();
        javaBuilder.setMessageFactory(msgFactory);
        javaBuilder.setProxyFactoryFactory(new JDKProxyFactoryFactory());

        MockSyncInterceptor mockInterceptor = new MockSyncInterceptor();
        MockInterceptorBuilder interceptorBuilder = new MockInterceptorBuilder(mockInterceptor, true);
        //HierarchicalBuilder refBuilder = new HierarchicalBuilder();
        //refBuilder.addBuilder(interceptorBuilder);
        MockHandler mockHandler = new MockHandler();
        MockHandlerBuilder handlerBuilder = new MockHandlerBuilder(mockHandler, true, true);
        //refBuilder.addBuilder(handlerBuilder);
        javaBuilder.addPolicyBuilder(interceptorBuilder);
        javaBuilder.addPolicyBuilder(handlerBuilder);

        //javaBuilder.setPolicyBuilder(refBuilder);
        builders.add(javaBuilder);

        DefaultWireBuilder defaultWireBuilder = new DefaultWireBuilder();
        RuntimeContext runtime = new RuntimeContextImpl(null, builders, defaultWireBuilder);
        runtime.addBuilder(new JavaTargetWireBuilder());
        runtime.start();
        runtime.getRootContext().registerModelObject(
                MockFactory.createCompositeComponent("test.module"));
        CompositeContext child = (CompositeContext) runtime.getRootContext().getContext("test.module");
        child.registerModelObject(MockFactory.createModule());
        child.fireEvent(EventContext.MODULE_START, null);
        GenericComponent source = (GenericComponent) child.getContext("source").getInstance(null);
        Assert.assertNotNull(source);
        source.getGenericComponent().getString();
        Assert.assertEquals(1, mockInterceptor.getCount());
        Assert.assertEquals(1, mockHandler.getCount());
        source.getGenericComponent().getString();
        Assert.assertEquals(2, mockInterceptor.getCount());
        Assert.assertEquals(2, mockHandler.getCount());
        child.fireEvent(EventContext.MODULE_STOP, null);
        runtime.stop();
    }

    public void testRefWithTargetInterceptorHandler() throws Exception {
        MessageFactory msgFactory = new MessageFactoryImpl();

        List<ContextFactoryBuilder> builders = new ArrayList<ContextFactoryBuilder>();
        builders.add((new SystemContextFactoryBuilder(null)));
        builders.add(new SystemEntryPointBuilder());
        builders.add(new SystemExternalServiceBuilder());

        JavaContextFactoryBuilder javaBuilder = new JavaContextFactoryBuilder();
        javaBuilder.setMessageFactory(msgFactory);
        javaBuilder.setProxyFactoryFactory(new JDKProxyFactoryFactory());

        MockSyncInterceptor mockInterceptor = new MockSyncInterceptor();
        MockInterceptorBuilder interceptorBuilder = new MockInterceptorBuilder(mockInterceptor, false);
        //HierarchicalBuilder refBuilder = new HierarchicalBuilder();
        //refBuilder.addBuilder(interceptorBuilder);
        MockHandler mockHandler = new MockHandler();
        MockHandlerBuilder handlerBuilder = new MockHandlerBuilder(mockHandler, false, true);
        //refBuilder.addBuilder(handlerBuilder);
        javaBuilder.addPolicyBuilder(interceptorBuilder);
        javaBuilder.addPolicyBuilder(handlerBuilder);

       // javaBuilder.setPolicyBuilder(refBuilder);
        builders.add(javaBuilder);

        DefaultWireBuilder defaultWireBuilder = new DefaultWireBuilder();

        RuntimeContext runtime = new RuntimeContextImpl(null, builders, defaultWireBuilder);
        runtime.addBuilder(new JavaTargetWireBuilder());
        runtime.start();
        runtime.getRootContext().registerModelObject(
                MockFactory.createCompositeComponent("test.module"));
        CompositeContext child = (CompositeContext) runtime.getRootContext().getContext("test.module");
        child.registerModelObject(MockFactory.createModule());
        child.fireEvent(EventContext.MODULE_START, null);
        GenericComponent source = (GenericComponent) child.getContext("source").getInstance(null);
        Assert.assertNotNull(source);
        source.getGenericComponent().getString();
        Assert.assertEquals(1, mockInterceptor.getCount());
        Assert.assertEquals(1, mockHandler.getCount());
        source.getGenericComponent().getString();
        Assert.assertEquals(2, mockInterceptor.getCount());
        Assert.assertEquals(2, mockHandler.getCount());
        child.fireEvent(EventContext.MODULE_STOP, null);
        runtime.stop();
    }

    public void testRefWithTargetInterceptor() throws Exception {
        MessageFactory msgFactory = new MessageFactoryImpl();

        List<ContextFactoryBuilder> builders = new ArrayList<ContextFactoryBuilder>();
        builders.add((new SystemContextFactoryBuilder(null)));
        builders.add(new SystemEntryPointBuilder());
        builders.add(new SystemExternalServiceBuilder());

        JavaContextFactoryBuilder javaBuilder = new JavaContextFactoryBuilder();
        javaBuilder.setMessageFactory(msgFactory);
        javaBuilder.setProxyFactoryFactory(new JDKProxyFactoryFactory());

        MockSyncInterceptor mockInterceptor = new MockSyncInterceptor();
        MockInterceptorBuilder interceptorBuilder = new MockInterceptorBuilder(mockInterceptor, false);
        //HierarchicalBuilder refBuilder = new HierarchicalBuilder();
        //refBuilder.addBuilder(interceptorBuilder);
        javaBuilder.addPolicyBuilder(interceptorBuilder);

        //javaBuilder.setPolicyBuilder(refBuilder);
        builders.add(javaBuilder);

        DefaultWireBuilder defaultWireBuilder = new DefaultWireBuilder();

        RuntimeContext runtime = new RuntimeContextImpl(null, builders, defaultWireBuilder);
        runtime.addBuilder(new JavaTargetWireBuilder());
        
        runtime.start();
        runtime.getRootContext().registerModelObject(
                MockFactory.createCompositeComponent("test.module"));
        CompositeContext child = (CompositeContext) runtime.getRootContext().getContext("test.module");
        child.registerModelObject(MockFactory.createModule());
        child.fireEvent(EventContext.MODULE_START, null);
        GenericComponent source = (GenericComponent) child.getContext("source").getInstance(null);
        Assert.assertNotNull(source);
        source.getGenericComponent().getString();
        Assert.assertEquals(1, mockInterceptor.getCount());
        source.getGenericComponent().getString();
        Assert.assertEquals(2, mockInterceptor.getCount());
        child.fireEvent(EventContext.MODULE_STOP, null);
        runtime.stop();
    }

}
