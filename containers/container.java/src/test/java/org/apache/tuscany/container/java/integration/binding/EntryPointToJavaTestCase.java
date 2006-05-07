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
package org.apache.tuscany.container.java.integration.binding;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.tuscany.container.java.invocation.mock.MockSyncInterceptor;
import org.apache.tuscany.core.context.event.HttpSessionEnd;
import org.apache.tuscany.core.context.event.ModuleStart;
import org.apache.tuscany.core.context.event.ModuleStop;
import org.apache.tuscany.core.context.event.RequestEnd;
import org.apache.tuscany.core.context.event.RequestStart;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Tests basic entry point functionality with Java components
 *
 * @version $Rev$ $Date$
 */
public class EntryPointToJavaTestCase extends TestCase {

    private Method hello;

    public void setUp() throws Exception {
//        hello = HelloWorldService.class.getMethod("hello", String.class);
    }

    /**
     * Tests creation and wire of an entry point wired to a module-scoped service offered by a Java component
     */
    public void testEPtoJavaModuleScopeInvoke() throws Throwable {
//        RuntimeContext runtime = MockFactory.registerFooBinding(MockFactory.createJavaRuntime());
//        PolicyBuilderRegistry registry = (PolicyBuilderRegistry) ((CompositeContext) runtime.getSystemContext().getContext(MockFactory.SYSTEM_CHILD))
//                .getContext(MockFactory.POLICY_BUILDER_REGISTRY).getInstance(null);
//
//        MockSyncInterceptor mockInterceptor = new MockSyncInterceptor();
//        MockInterceptorBuilder interceptorBuilder = new MockInterceptorBuilder(mockInterceptor, false);
//        registry.registerTargetBuilder(interceptorBuilder);
//        runtime.getRootContext().registerModelObject(MockFactory.createCompositeComponent("test.module"));
//        CompositeContext child = (CompositeContext) runtime.getRootContext().getContext("test.module");
//        child.registerModelObject(MockFactory.createModuleWithEntryPoint(Scope.MODULE));
//        child.publish(new ModuleStart(this));
//        Object id = new Object();
//        child.publish(new RequestStart(this, id));
//        EntryPointContext ctx = (EntryPointContext) child.getContext("source");
//        Assert.assertNotNull(ctx);
//        InvocationHandler handler = (InvocationHandler) ctx.getHandler();
//        Assert.assertEquals(0, mockInterceptor.getCount());
//        Object response = handler.invoke(null, hello, new Object[]{"foo"});
//        Assert.assertEquals("Hello foo", response);
//        Assert.assertEquals(1, mockInterceptor.getCount());
//        Object id2 = new Object();
//        child.publish(new RequestStart(this, id2));
//
//        // second request
//        Object id3 = new Object();
//        child.publish(new RequestStart(this, id3));
//        ctx = (EntryPointContext) child.getContext("source");
//        Assert.assertNotNull(ctx);
//        handler = (InvocationHandler) ctx.getHandler();
//        Assert.assertEquals(1, mockInterceptor.getCount());
//        response = handler.invoke(null, hello, new Object[]{"foo"});
//        Assert.assertEquals("Hello foo", response);
//        Assert.assertEquals(2, mockInterceptor.getCount());
//        HelloWorldService service1 = (HelloWorldService) child.getContext("target").getInstance(null);
//        Assert.assertEquals(2, service1.count());
//        child.publish(new RequestEnd(this, id3));
//
//        child.publish(new ModuleStop(this));
//        runtime.stop();
    }

    /**
     * Tests creation and wire of an entry point wired to a session-scoped service offered by a Java component
     */
    public void testEPtoJavaSessionScopeInvoke() throws Throwable {
//        RuntimeContext runtime = MockFactory.registerFooBinding(MockFactory.createJavaRuntime());
//        PolicyBuilderRegistry registry = (PolicyBuilderRegistry) ((CompositeContext) runtime.getSystemContext().getContext(MockFactory.SYSTEM_CHILD))
//                .getContext(MockFactory.POLICY_BUILDER_REGISTRY).getInstance(null);
//        MockSyncInterceptor mockInterceptor = new MockSyncInterceptor();
//        MockInterceptorBuilder interceptorBuilder = new MockInterceptorBuilder(mockInterceptor, false);
//        registry.registerTargetBuilder(interceptorBuilder);
//        runtime.getRootContext().registerModelObject(MockFactory.createCompositeComponent("test.module"));
//        CompositeContext child = (CompositeContext) runtime.getRootContext().getContext("test.module");
//        child.registerModelObject(MockFactory.createModuleWithEntryPoint(Scope.SESSION));
//        child.publish(new ModuleStart(this));
//
//        // first session
//        Object session = new Object();
//        Object id = new Object();
//        child.publish(new RequestStart(this, id));
//        child.publish(new HttpSessionBound(this, session));
//
//        EntryPointContext ctx = (EntryPointContext) child.getContext("source");
//        Assert.assertNotNull(ctx);
//        InvocationHandler handler = (InvocationHandler) ctx.getHandler();
//        Assert.assertEquals(0, mockInterceptor.getCount());
//        Object response = handler.invoke(null, hello, new Object[]{"foo"});
//        Assert.assertEquals("Hello foo", response);
//        Assert.assertEquals(1, mockInterceptor.getCount());
//        child.publish(new RequestEnd(this, id));
//
//        Object id2 = new Object();
//        child.publish(new RequestStart(this, id2));
//        child.publish(new HttpSessionBound(this, session));
//        EntryPointContext ctx2 = (EntryPointContext) child.getContext("source");
//        Assert.assertNotNull(ctx2);
//        response = handler.invoke(null, hello, new Object[]{"foo"});
//        Assert.assertEquals("Hello foo", response);
//        Assert.assertEquals(2, mockInterceptor.getCount());
//        HelloWorldService service1 = (HelloWorldService) child.getContext("target").getInstance(null);
//        Assert.assertEquals(2, service1.count());
//        child.publish(new RequestEnd(this, id2));
//        child.publish(new HttpSessionEnd(this, session));
//
//        // second session
//        Object session2 = new Object();
//        child.publish(new RequestStart(this, new Object()));
//        child.publish(new HttpSessionBound(this, session2));
//
//        ctx = (EntryPointContext) child.getContext("source");
//        Assert.assertNotNull(ctx);
//        Assert.assertEquals(2, mockInterceptor.getCount());
//        response = handler.invoke(null, hello, new Object[]{"foo"});
//        Assert.assertEquals("Hello foo", response);
//        Assert.assertEquals(3, mockInterceptor.getCount());
//        child.publish(new HttpSessionBound(this, session2));
//
//        Object id3 = new Object();
//        child.publish(new RequestStart(this, id3));
//        child.publish(new HttpSessionBound(this, session2));
//        ctx2 = (EntryPointContext) child.getContext("source");
//        Assert.assertNotNull(ctx2);
//        response = handler.invoke(null, hello, new Object[]{"foo"});
//        Assert.assertEquals("Hello foo", response);
//        Assert.assertEquals(4, mockInterceptor.getCount());
//        HelloWorldService service2 = (HelloWorldService) child.getContext("target").getInstance(null);
//        Assert.assertEquals(2, service2.count());
//        Assert.assertEquals(2, service1.count()); //ensure sessions not crossed
//        child.publish(new RequestEnd(this, session2));
//        child.publish(new HttpSessionBound(this, session2));
//
//        child.publish(new ModuleStop(this));
//        runtime.stop();
    }


    /**
     * Tests creation and wire of an entry point wired to a module-scoped service offered by a Java component
     */
    public void testEPtoJavaStatelessInvoke() throws Throwable {
//        RuntimeContext runtime = MockFactory.registerFooBinding(MockFactory.createJavaRuntime());
//        PolicyBuilderRegistry registry = (PolicyBuilderRegistry) ((CompositeContext) runtime.getSystemContext().getContext(MockFactory.SYSTEM_CHILD))
//                .getContext(MockFactory.POLICY_BUILDER_REGISTRY).getInstance(null);
//        MockSyncInterceptor mockInterceptor = new MockSyncInterceptor();
//        MockInterceptorBuilder interceptorBuilder = new MockInterceptorBuilder(mockInterceptor, false);
//        registry.registerTargetBuilder(interceptorBuilder);
//        runtime.getRootContext().registerModelObject(MockFactory.createCompositeComponent("test.module"));
//        CompositeContext child = (CompositeContext) runtime.getRootContext().getContext("test.module");
//        child.registerModelObject(MockFactory.createModuleWithEntryPoint(Scope.INSTANCE));
//        child.publish(new ModuleStart(this));
//        Object id = new Object();
//        child.publish(new RequestStart(this, id));
//        EntryPointContext ctx = (EntryPointContext) child.getContext("source");
//        Assert.assertNotNull(ctx);
//        InvocationHandler handler = (InvocationHandler) ctx.getHandler();
//        Assert.assertEquals(0, mockInterceptor.getCount());
//        Object response = handler.invoke(null, hello, new Object[]{"foo"});
//        Assert.assertEquals("Hello foo", response);
//        Assert.assertEquals(1, mockInterceptor.getCount());
//        child.publish(new RequestEnd(this, id));
//
//        // second request
//        Object id2 = new Object();
//        child.publish(new RequestStart(this, id2));
//        ctx = (EntryPointContext) child.getContext("source");
//        Assert.assertNotNull(ctx);
//        handler = (InvocationHandler) ctx.getHandler();
//        Assert.assertEquals(1, mockInterceptor.getCount());
//        response = handler.invoke(null, hello, new Object[]{"foo"});
//        Assert.assertEquals("Hello foo", response);
//        Assert.assertEquals(2, mockInterceptor.getCount());
//        HelloWorldService service1 = (HelloWorldService) child.getContext("target").getInstance(null);
//        Assert.assertEquals(0, service1.count());
//        child.publish(new RequestEnd(this, id));
//
//        child.publish(new ModuleStop(this));
//        runtime.stop();
    }

    public void testEPtoJavaRequestInvoke() throws Throwable {
//        RuntimeContext runtime = MockFactory.registerFooBinding(MockFactory.createJavaRuntime());
//        PolicyBuilderRegistry registry = (PolicyBuilderRegistry) ((CompositeContext) runtime.getSystemContext().getContext(MockFactory.SYSTEM_CHILD))
//                .getContext(MockFactory.POLICY_BUILDER_REGISTRY).getInstance(null);
//        MockSyncInterceptor mockInterceptor = new MockSyncInterceptor();
//        MockInterceptorBuilder interceptorBuilder = new MockInterceptorBuilder(mockInterceptor, false);
//        registry.registerTargetBuilder(interceptorBuilder);
//        runtime.getRootContext().registerModelObject(MockFactory.createCompositeComponent("test.module"));
//        CompositeContext child = (CompositeContext) runtime.getRootContext().getContext("test.module");
//        child.registerModelObject(MockFactory.createModuleWithEntryPoint(Scope.REQUEST));
//        child.publish(new ModuleStart(this));
//        Object id = new Object();
//        child.publish(new RequestStart(this, id));
//        EntryPointContext ctx = (EntryPointContext) child.getContext("source");
//        Assert.assertNotNull(ctx);
//        InvocationHandler handler = (InvocationHandler) ctx.getHandler();
//        Assert.assertEquals(0, mockInterceptor.getCount());
//        Object response = handler.invoke(null, hello, new Object[]{"foo"});
//        Assert.assertEquals("Hello foo", response);
//        Assert.assertEquals(1, mockInterceptor.getCount());
//
//        ctx = (EntryPointContext) child.getContext("source");
//        Assert.assertNotNull(ctx);
//        handler = (InvocationHandler) ctx.getHandler();
//        response = handler.invoke(null, hello, new Object[]{"foo"});
//        HelloWorldService service1 = (HelloWorldService) child.getContext("target").getInstance(null);
//        Assert.assertEquals(2, service1.count());
//
//        child.publish(new RequestEnd(this, id));
//
//        // second request
//        Object id2 = new Object();
//        child.publish(new RequestStart(this, id2));
//        ctx = (EntryPointContext) child.getContext("source");
//        Assert.assertNotNull(ctx);
//        handler = (InvocationHandler) ctx.getHandler();
//        Assert.assertEquals(2, mockInterceptor.getCount());
//        response = handler.invoke(null, hello, new Object[]{"foo"});
//        Assert.assertEquals("Hello foo", response);
//        Assert.assertEquals(3, mockInterceptor.getCount());
//        HelloWorldService service2 = (HelloWorldService) child.getContext("target").getInstance(null);
//        Assert.assertEquals(1, service2.count());
//        child.publish(new RequestEnd(this, id2));
//
//        child.publish(new ModuleStop(this));
//        runtime.stop();
    }

}
