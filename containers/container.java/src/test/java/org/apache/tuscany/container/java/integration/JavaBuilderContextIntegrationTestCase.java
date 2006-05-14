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

import junit.framework.TestCase;
//import org.apache.tuscany.container.java.builder.JavaContextFactoryBuilder;
//import org.apache.tuscany.container.java.builder.JavaTargetWireBuilder;
//import org.apache.tuscany.container.java.builder.MockHandlerBuilder;
//import org.apache.tuscany.container.java.builder.MockInterceptorBuilder;
//import org.apache.tuscany.container.java.invocation.mock.MockHandler;
//import org.apache.tuscany.container.java.invocation.mock.MockSyncInterceptor;
//import org.apache.tuscany.container.java.mock.MockFactory;
//import org.apache.tuscany.container.java.mock.components.GenericComponent;
//import org.apache.tuscany.core.builder.ContextFactoryBuilderRegistry;
//import org.apache.tuscany.core.builder.impl.DefaultWireBuilder;
//import org.apache.tuscany.core.builder.system.DefaultPolicyBuilderRegistry;
//import org.apache.tuscany.core.builder.system.PolicyBuilderRegistry;
//import org.apache.tuscany.core.client.BootstrapHelper;
//import org.apache.tuscany.core.context.CompositeContext;
//import org.apache.tuscany.core.context.event.ModuleStart;
//import org.apache.tuscany.core.context.event.ModuleStop;
//import org.apache.tuscany.core.message.impl.MessageFactoryImpl;
//import org.apache.tuscany.core.runtime.RuntimeContext;
//import org.apache.tuscany.core.runtime.RuntimeContextImpl;
//import org.apache.tuscany.core.wire.jdk.JDKWireFactoryService;
//import org.apache.tuscany.core.wire.service.DefaultWireFactoryService;
//import org.apache.tuscany.core.wire.service.WireService;

/**
 * Verifies that the composite context implementation and java component builders construct references properly
 *
 * @version $Rev$ $Date$
 */
public class JavaBuilderContextIntegrationTestCase extends TestCase {
//    private ContextFactoryBuilderRegistry builderRegistry;
//    private DefaultWireBuilder defaultWireBuilder;
//    private NullMonitorFactory monitorFactory;

    public JavaBuilderContextIntegrationTestCase(String arg0) {
        super(arg0);
    }

    protected void setUp() throws Exception {
//        super.setUp();
//        monitorFactory = new NullMonitorFactory();
//        builderRegistry = BootstrapHelper.bootstrapContextFactoryBuilders(monitorFactory);
//        defaultWireBuilder = new DefaultWireBuilder();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testRefWithSourceInterceptor() throws Exception {
//        MockSyncInterceptor mockInterceptor = new MockSyncInterceptor();
//        MockInterceptorBuilder interceptorBuilder = new MockInterceptorBuilder(mockInterceptor, true);
//        PolicyBuilderRegistry policyRegistry = new DefaultPolicyBuilderRegistry();
//        policyRegistry.registerSourceBuilder(interceptorBuilder);
//        WireService wireFactory = new DefaultWireFactoryService(new MessageFactoryImpl(), new JDKWireFactoryService(), policyRegistry);
//        JavaContextFactoryBuilder javaBuilder = new JavaContextFactoryBuilder(wireFactory);
//
//        builderRegistry.register(javaBuilder);
//
//        RuntimeContext runtime = new RuntimeContextImpl(monitorFactory, builderRegistry, defaultWireBuilder);
//        runtime.addBuilder(new JavaTargetWireBuilder());
//        runtime.start();
//        runtime.getRootContext().registerModelObject(
//                MockFactory.createCompositeComponent("test.module"));
//        CompositeContext child = (CompositeContext) runtime.getRootContext().getContext("test.module");
//        child.registerModelObject(MockFactory.createModule());
//        child.publish(new ModuleStart(this));
//        GenericComponent source = (GenericComponent) child.getContext("source").getInstance(null);
//        Assert.assertNotNull(source);
//        source.getGenericComponent().getString();
//        Assert.assertEquals(1, mockInterceptor.getCount());
//        source.getGenericComponent().getString();
//        Assert.assertEquals(2, mockInterceptor.getCount());
//        child.publish(new ModuleStop(this));
//        runtime.stop();
    }

    public void testRefWithSourceInterceptorHandler() throws Exception {
//        MockSyncInterceptor mockInterceptor = new MockSyncInterceptor();
//        MockInterceptorBuilder interceptorBuilder = new MockInterceptorBuilder(mockInterceptor, true);
//        MockHandler mockHandler = new MockHandler();
//        MockHandlerBuilder handlerBuilder = new MockHandlerBuilder(mockHandler, true, true);
//        PolicyBuilderRegistry policyRegistry = new DefaultPolicyBuilderRegistry();
//        policyRegistry.registerSourceBuilder(interceptorBuilder);
//        policyRegistry.registerSourceBuilder(handlerBuilder);
//        WireService wireFactory = new DefaultWireFactoryService(new MessageFactoryImpl(), new JDKWireFactoryService(), policyRegistry);
//        JavaContextFactoryBuilder javaBuilder = new JavaContextFactoryBuilder(wireFactory);
//
//        builderRegistry.register(javaBuilder);
//        RuntimeContext runtime = new RuntimeContextImpl(monitorFactory, builderRegistry, defaultWireBuilder);
//        runtime.addBuilder(new JavaTargetWireBuilder());
//        runtime.start();
//        runtime.getRootContext().registerModelObject(
//                MockFactory.createCompositeComponent("test.module"));
//        CompositeContext child = (CompositeContext) runtime.getRootContext().getContext("test.module");
//        child.registerModelObject(MockFactory.createModule());
//        child.publish(new ModuleStart(this));
//        GenericComponent source = (GenericComponent) child.getContext("source").getInstance(null);
//        Assert.assertNotNull(source);
//        source.getGenericComponent().getString();
//        Assert.assertEquals(1, mockInterceptor.getCount());
//        Assert.assertEquals(1, mockHandler.getCount());
//        source.getGenericComponent().getString();
//        Assert.assertEquals(2, mockInterceptor.getCount());
//        Assert.assertEquals(2, mockHandler.getCount());
//        child.publish(new ModuleStop(this));
//        runtime.stop();
    }

    public void testRefWithTargetInterceptorHandler() throws Exception {
//        MockSyncInterceptor mockInterceptor = new MockSyncInterceptor();
//        MockInterceptorBuilder interceptorBuilder = new MockInterceptorBuilder(mockInterceptor, false);
//        MockHandler mockHandler = new MockHandler();
//        MockHandlerBuilder handlerBuilder = new MockHandlerBuilder(mockHandler, false, true);
//        PolicyBuilderRegistry policyRegistry = new DefaultPolicyBuilderRegistry();
//        policyRegistry.registerSourceBuilder(interceptorBuilder);
//        policyRegistry.registerSourceBuilder(handlerBuilder);
//        WireService wireFactory = new DefaultWireFactoryService(new MessageFactoryImpl(), new JDKWireFactoryService(), policyRegistry);
//        JavaContextFactoryBuilder javaBuilder = new JavaContextFactoryBuilder(wireFactory);
//
//        builderRegistry.register(javaBuilder);
//
//        RuntimeContext runtime = new RuntimeContextImpl(monitorFactory, builderRegistry, defaultWireBuilder);
//        runtime.addBuilder(new JavaTargetWireBuilder());
//        runtime.start();
//        runtime.getRootContext().registerModelObject(
//                MockFactory.createCompositeComponent("test.module"));
//        CompositeContext child = (CompositeContext) runtime.getRootContext().getContext("test.module");
//        child.registerModelObject(MockFactory.createModule());
//        child.publish(new ModuleStart(this));
//        GenericComponent source = (GenericComponent) child.getContext("source").getInstance(null);
//        Assert.assertNotNull(source);
//        source.getGenericComponent().getString();
//        Assert.assertEquals(1, mockInterceptor.getCount());
//        Assert.assertEquals(1, mockHandler.getCount());
//        source.getGenericComponent().getString();
//        Assert.assertEquals(2, mockInterceptor.getCount());
//        Assert.assertEquals(2, mockHandler.getCount());
//        child.publish(new ModuleStop(this));
//        runtime.stop();
    }

    public void testRefWithTargetInterceptor() throws Exception {
//        MockSyncInterceptor mockInterceptor = new MockSyncInterceptor();
//        MockInterceptorBuilder interceptorBuilder = new MockInterceptorBuilder(mockInterceptor, false);
//        PolicyBuilderRegistry policyRegistry = new DefaultPolicyBuilderRegistry();
//        policyRegistry.registerSourceBuilder(interceptorBuilder);
//        WireService wireFactory = new DefaultWireFactoryService(new MessageFactoryImpl(), new JDKWireFactoryService(), policyRegistry);
//        JavaContextFactoryBuilder javaBuilder = new JavaContextFactoryBuilder(wireFactory);
//
//        builderRegistry.register(javaBuilder);
//
//        RuntimeContext runtime = new RuntimeContextImpl(monitorFactory, builderRegistry, defaultWireBuilder);
//        runtime.addBuilder(new JavaTargetWireBuilder());
//
//        runtime.start();
//        runtime.getRootContext().registerModelObject(
//                MockFactory.createCompositeComponent("test.module"));
//        CompositeContext child = (CompositeContext) runtime.getRootContext().getContext("test.module");
//        child.registerModelObject(MockFactory.createModule());
//        child.publish(new ModuleStart(this));
//        GenericComponent source = (GenericComponent) child.getContext("source").getInstance(null);
//        Assert.assertNotNull(source);
//        source.getGenericComponent().getString();
//        Assert.assertEquals(1, mockInterceptor.getCount());
//        source.getGenericComponent().getString();
//        Assert.assertEquals(2, mockInterceptor.getCount());
//        child.publish(new ModuleStop(this));
//        runtime.stop();
    }

}
