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
package org.apache.tuscany.container.java.integration.context;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import junit.framework.TestCase;
import org.apache.tuscany.container.java.mock.MockFactory;
import org.apache.tuscany.container.java.mock.components.Source;
import org.apache.tuscany.container.java.mock.components.SourceImpl;
import org.apache.tuscany.container.java.mock.components.Target;
import org.apache.tuscany.container.java.mock.components.TargetImpl;
import org.apache.tuscany.core.context.WorkContextImpl;
import org.apache.tuscany.core.context.event.HttpSessionEnd;
import org.apache.tuscany.core.context.event.HttpSessionStart;
import org.apache.tuscany.core.context.event.ModuleStart;
import org.apache.tuscany.core.context.event.ModuleStop;
import org.apache.tuscany.core.context.scope.HttpSessionScopeContext;
import org.apache.tuscany.core.context.scope.ModuleScopeContext;
import org.apache.tuscany.core.context.scope.RequestScopeContext;
import org.apache.tuscany.core.util.JavaIntrospectionHelper;
import org.apache.tuscany.spi.context.AtomicContext;
import org.apache.tuscany.spi.context.ScopeContext;
import org.apache.tuscany.spi.context.WorkContext;

/**
 * Tests scoping is properly handled for service references
 *
 * @version $Rev$ $Date$
 */
public class ScopeReferenceTestCase extends TestCase {

    private Map<String, Member> members;


    /**
     * Tests a module-to-module scoped wire
     */
    @SuppressWarnings("unchecked")
    public void testModuleToModule() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        ScopeContext<AtomicContext> scope = new ModuleScopeContext(ctx);
        scope.start();

        Map<String, AtomicContext> contexts = MockFactory.createWiredContexts("source", SourceImpl.class, scope, members, "target", Target.class, TargetImpl.class, scope);
        scope.onEvent(new ModuleStart(this, null));
        AtomicContext<Source> sourceContext = (AtomicContext<Source>) contexts.get("source");
        AtomicContext<Target> targetContext = (AtomicContext<Target>) contexts.get("target");
        Source source = sourceContext.getService();
        Target target = targetContext.getService();
        assertNull(source.getTarget().getString());
        assertNull(target.getString());
        target.setString("foo");
        assertTrue(Proxy.isProxyClass(source.getTarget().getClass()));
        assertEquals("foo", source.getTarget().getString());
        scope.onEvent(new ModuleStop(this, null));
        scope.stop();
    }

    /**
     * Tests a module-to-session scoped wire is setup properly by the runtime
     */
    @SuppressWarnings("unchecked")
    public void testModuleToSession() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        ScopeContext<AtomicContext> moduleScope = new ModuleScopeContext(ctx);
        moduleScope.start();
        ScopeContext<AtomicContext> sessionScope = new HttpSessionScopeContext(ctx);
        sessionScope.start();

        Map<String, AtomicContext> contexts = MockFactory.createWiredContexts("source", SourceImpl.class,
                moduleScope, members, "target", Target.class, TargetImpl.class, sessionScope);
        moduleScope.onEvent(new ModuleStart(this, null));
        Object session1 = new Object();
        ctx.setIdentifier(HttpSessionScopeContext.HTTP_IDENTIFIER, session1);
        sessionScope.onEvent(new HttpSessionStart(this, session1));
        AtomicContext<Source> sourceContext = (AtomicContext<Source>) contexts.get("source");
        AtomicContext<Target> targetContext = (AtomicContext<Target>) contexts.get("target");
        Source source = sourceContext.getService();
        Target target = targetContext.getService();
        assertNull(source.getTarget().getString());
        assertNull(target.getString());
        target.setString("foo");
        assertTrue(Proxy.isProxyClass(source.getTarget().getClass()));
        assertEquals("foo", source.getTarget().getString());
        ctx.clearIdentifier(HttpSessionScopeContext.HTTP_IDENTIFIER);
        sessionScope.onEvent(new HttpSessionEnd(this, session1));

        //second session
        Object session2 = new Object();
        ctx.setIdentifier(HttpSessionScopeContext.HTTP_IDENTIFIER, session2);
        sessionScope.onEvent(new HttpSessionStart(this, session2));

        Target target2 = targetContext.getService();
        assertTrue(!"foo".equals(target2.getString()));

        assertTrue(!"foo".equals(source.getTarget().getString()));
        source.getTarget().setString("bar");
        assertEquals("bar", target2.getString());
        assertEquals("bar", source.getTarget().getString());
        sessionScope.onEvent(new HttpSessionEnd(this, session2));

        ctx.clearIdentifier(HttpSessionScopeContext.HTTP_IDENTIFIER);
        moduleScope.onEvent(new ModuleStop(this, null));
        sessionScope.stop();
        moduleScope.stop();
    }

    /**
     * Tests a module-to-request scoped wire
     */
    @SuppressWarnings("unchecked")
    public void testModuleToRequest() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        ScopeContext<AtomicContext> moduleScope = new ModuleScopeContext(ctx);
        moduleScope.start();
        ScopeContext<AtomicContext> requestScope = new RequestScopeContext(ctx);
        requestScope.start();

        Map<String, AtomicContext> contexts = MockFactory.createWiredContexts("source", SourceImpl.class,
                moduleScope, members, "target", Target.class, TargetImpl.class, requestScope);
        moduleScope.onEvent(new ModuleStart(this, null));

        AtomicContext<Source> sourceContext = (AtomicContext<Source>) contexts.get("source");
        final AtomicContext<Target> targetContext = (AtomicContext<Target>) contexts.get("target");
        final Source source = sourceContext.getService();
        Target target = targetContext.getService();
        assertNull(source.getTarget().getString());
        assertNull(target.getString());
        target.setString("foo");
        assertTrue(Proxy.isProxyClass(source.getTarget().getClass()));
        assertEquals("foo", source.getTarget().getString());

        // spin off another request
        Executor executor = Executors.newSingleThreadExecutor();
        FutureTask<Void> future = new FutureTask<Void>(new Runnable() {
            public void run() {
                Target target2 = targetContext.getService();
                assertTrue(!"foo".equals(target2.getString()));
                assertTrue(!"foo".equals(source.getTarget().getString()));
                source.getTarget().setString("bar");
                assertEquals("bar", target2.getString());
                assertEquals("bar", source.getTarget().getString());
            }
        }, null);
        executor.execute(future);
        future.get();
    }

    /**
     * Tests a module-to-stateless scoped wire is setup properly by the runtime
     */
    public void testModuleToStateless() throws Exception {
//        RuntimeContext runtime = MockFactory.createJavaRuntime();
//        Context ctx = runtime.getSystemContext().getContext("tuscany.system.child");
//        Assert.assertNotNull(ctx);
//        runtime.getRootContext().registerModelObject(MockFactory.createCompositeComponent("test"));
//        CompositeContext testCtx = (CompositeContext) runtime.getRootContext().getContext("test");
//        Assert.assertNotNull(testCtx);
//        testCtx.registerModelObject(MockFactory.createModule(Scope.MODULE,Scope.INSTANCE));
//        testCtx.publish(new ModuleStart(this));
//
//        // first request
//        Object id = new Object();
//        testCtx.publish(new RequestStart(this,id));
//        GenericComponent source = (GenericComponent)testCtx.getContext("source").getInstance(null);
//        Assert.assertNotNull(source);
//        GenericComponent target = (GenericComponent)testCtx.getContext("target").getInstance(null);
//        Assert.assertNotNull(target);
//        source.getGenericComponent().setString("foo");
//        Assert.assertTrue(!"foo".equals(target.getString()));
//        testCtx.publish(new RequestEnd(this,id));
//
//        //second request
//        Object id2 = new Object();
//        testCtx.publish(new RequestStart(this,id2));
//        GenericComponent target2 = (GenericComponent)testCtx.getContext("target").getInstance(null);
//        Assert.assertNotNull(target2);
//        Assert.assertTrue(!"foo".equals(target2.getString()));
//
//        Assert.assertTrue(!"foo".equals(source.getGenericComponent().getString()));
//        source.getGenericComponent().setString("bar");
//        Assert.assertTrue(!"bar".equals(target2.getString()));
    }

    /**
     * Tests a session-to-session scoped wire is setup properly by the runtime
     */
    public void testSessionToSession() throws Exception {
//        RuntimeContext runtime = MockFactory.createJavaRuntime();
//        Context ctx = runtime.getSystemContext().getContext("tuscany.system.child");
//        Assert.assertNotNull(ctx);
//        runtime.getRootContext().registerModelObject(MockFactory.createCompositeComponent("test"));
//        CompositeContext testCtx = (CompositeContext) runtime.getRootContext().getContext("test");
//        Assert.assertNotNull(testCtx);
//        testCtx.registerModelObject(MockFactory.createModule(Scope.SESSION,Scope.SESSION));
//        testCtx.publish(new ModuleStart(this));
//
//        // first session
//        Object session = new Object();
//        Object id = new Object();
//        testCtx.publish(new RequestStart(this,id));
//        testCtx.publish(new HttpSessionBound(this,session));
//        GenericComponent source = (GenericComponent)testCtx.getContext("source").getInstance(null);
//        Assert.assertNotNull(source);
//        GenericComponent target = (GenericComponent)testCtx.getContext("target").getInstance(null);
//        Assert.assertNotNull(target);
//        source.getGenericComponent().setString("foo");
//        source.getGenericComponent().setString("foo");
//        Assert.assertEquals("foo",target.getString());
//        testCtx.publish(new RequestEnd(this,id));
//
//        //second session
//        Object session2 = new Object();
//        Object id2 = new Object();
//        testCtx.publish(new RequestStart(this,id2));
//        testCtx.publish(new HttpSessionBound(this,session2));
//        GenericComponent source2 = (GenericComponent)testCtx.getContext("source").getInstance(null);
//        Assert.assertNotNull(source2);
//        GenericComponent target2 = (GenericComponent)testCtx.getContext("target").getInstance(null);
//
//        Assert.assertNotNull(target2);
//        Assert.assertEquals(null,target2.getString());
//        Assert.assertEquals(null,source2.getGenericComponent().getString());
//        source2.getGenericComponent().setString("baz");
//        Assert.assertEquals("baz",source2.getGenericComponent().getString());
//        Assert.assertEquals("baz",target2.getString());
//
//        testCtx.publish(new RequestEnd(this,id2));
//
    }


    /**
     * Tests a session-to-module scoped wire is setup properly by the runtime
     */
    public void testSessionToModule() throws Exception {
//        RuntimeContext runtime = MockFactory.createJavaRuntime();
//        Context ctx = runtime.getSystemContext().getContext("tuscany.system.child");
//        Assert.assertNotNull(ctx);
//        runtime.getRootContext().registerModelObject(MockFactory.createCompositeComponent("test"));
//        CompositeContext testCtx = (CompositeContext) runtime.getRootContext().getContext("test");
//        Assert.assertNotNull(testCtx);
//        testCtx.registerModelObject(MockFactory.createModule(Scope.SESSION,Scope.MODULE));
//        testCtx.publish(new ModuleStart(this));
//
//        // first session
//        Object session = new Object();
//        Object id = new Object();
//        testCtx.publish(new RequestStart(this,id));
//        testCtx.publish(new HttpSessionBound(this,session));
//        GenericComponent source = (GenericComponent)testCtx.getContext("source").getInstance(null);
//        Assert.assertNotNull(source);
//        GenericComponent target = (GenericComponent)testCtx.getContext("target").getInstance(null);
//        Assert.assertNotNull(target);
//        source.getGenericComponent().setString("foo");
//        source.getGenericComponent().setString("foo");
//        Assert.assertEquals("foo",target.getString());
//        testCtx.publish(new RequestEnd(this,id));
//
//        //second session
//        Object session2 = new Object();
//        Object id2 = new Object();
//        testCtx.publish(new RequestStart(this,id2));
//        testCtx.publish(new HttpSessionBound(this,session2));
//        GenericComponent source2 = (GenericComponent)testCtx.getContext("source").getInstance(null);
//        Assert.assertNotNull(source2);
//        GenericComponent target2 = (GenericComponent)testCtx.getContext("target").getInstance(null);
//
//        Assert.assertNotNull(target2);
//        Assert.assertEquals("foo",target2.getString());
//        Assert.assertEquals("foo",source2.getGenericComponent().getString());
//        source2.getGenericComponent().setString("baz");
//        Assert.assertEquals("baz",source2.getGenericComponent().getString());
//        Assert.assertEquals("baz",target2.getString());
//        Assert.assertEquals("baz",target.getString());
//
//        testCtx.publish(new RequestEnd(this,session2));

    }

    /**
     * Tests a session-to-request scoped wire is setup properly by the runtime
     */
    public void testSessionToRequest() throws Exception {
//        RuntimeContext runtime = MockFactory.createJavaRuntime();
//        Context ctx = runtime.getSystemContext().getContext("tuscany.system.child");
//        Assert.assertNotNull(ctx);
//        runtime.getRootContext().registerModelObject(MockFactory.createCompositeComponent("test"));
//        CompositeContext testCtx = (CompositeContext) runtime.getRootContext().getContext("test");
//        Assert.assertNotNull(testCtx);
//        testCtx.registerModelObject(MockFactory.createModule(Scope.SESSION,Scope.REQUEST));
//        testCtx.publish(new ModuleStart(this));
//
//        // first session
//        Object session = new Object();
//        Object id = new Object();
//        testCtx.publish(new RequestStart(this,id));
//        testCtx.publish(new HttpSessionBound(this,session));
//        GenericComponent source = (GenericComponent)testCtx.getContext("source").getInstance(null);
//        Assert.assertNotNull(source);
//        GenericComponent target = (GenericComponent)testCtx.getContext("target").getInstance(null);
//        Assert.assertNotNull(target);
//        source.getGenericComponent().setString("foo");
//        Assert.assertEquals("foo",target.getString());
//        testCtx.publish(new RequestEnd(this,session));
//
//        //second session
//        Object session2 = new Object();
//        Object id2 = new Object();
//        testCtx.publish(new RequestStart(this,id2));
//        testCtx.publish(new HttpSessionBound(this,session2));
//        GenericComponent source2 = (GenericComponent)testCtx.getContext("source").getInstance(null);
//        Assert.assertNotNull(source);
//        GenericComponent target2 = (GenericComponent)testCtx.getContext("target").getInstance(null);
//
//        Assert.assertNotNull(target2);
//        Assert.assertEquals(null,target2.getString());
//        source2.getGenericComponent().setString("baz");
//        Assert.assertEquals("baz",target2.getString());
//        Assert.assertEquals("baz",source2.getGenericComponent().getString());
//
//        Assert.assertEquals("foo",target.getString());
//        testCtx.publish(new RequestEnd(this,session));

    }


    /**
     * Tests a session-to-stateless scoped wire is setup properly by the runtime
     */
    public void testSessionToStateless() throws Exception {
//        RuntimeContext runtime = MockFactory.createJavaRuntime();
//        Context ctx = runtime.getSystemContext().getContext("tuscany.system.child");
//        Assert.assertNotNull(ctx);
//        runtime.getRootContext().registerModelObject(MockFactory.createCompositeComponent("test"));
//        CompositeContext testCtx = (CompositeContext) runtime.getRootContext().getContext("test");
//        Assert.assertNotNull(testCtx);
//        testCtx.registerModelObject(MockFactory.createModule(Scope.SESSION,Scope.INSTANCE));
//        testCtx.publish(new ModuleStart(this));
//
//        // first session
//        Object session = new Object();
//        Object id = new Object();
//        testCtx.publish(new RequestStart(this,id));
//        testCtx.publish(new HttpSessionBound(this,session));
//        GenericComponent source = (GenericComponent)testCtx.getContext("source").getInstance(null);
//        Assert.assertNotNull(source);
//        GenericComponent target = (GenericComponent)testCtx.getContext("target").getInstance(null);
//        Assert.assertNotNull(target);
//        source.getGenericComponent().setString("foo");
//        Assert.assertEquals(null,target.getString());
//        testCtx.publish(new RequestEnd(this,session));
//
//        //second session
//        Object session2 = new Object();
//        Object id2 = new Object();
//        testCtx.publish(new RequestStart(this,id2));
//        testCtx.publish(new HttpSessionBound(this,session2));
//        GenericComponent source2 = (GenericComponent)testCtx.getContext("source").getInstance(null);
//        Assert.assertNotNull(source);
//        GenericComponent target2 = (GenericComponent)testCtx.getContext("target").getInstance(null);
//
//        Assert.assertNotNull(target2);
//        Assert.assertEquals(null,target2.getString());
//        source2.getGenericComponent().setString("baz");
//        Assert.assertEquals(null,target2.getString()); //Note assumes no pooling
//        Assert.assertEquals(null,source2.getGenericComponent().getString());
//
//        Assert.assertEquals(null,target.getString()); //Note assumes no pooling
//        testCtx.publish(new RequestEnd(this,session));
//
    }

    /**
     * Tests a request-to-request scoped wire is setup properly by the runtime
     */
    public void testRequestToRequest() throws Exception {
//        RuntimeContext runtime = MockFactory.createJavaRuntime();
//        Context ctx = runtime.getSystemContext().getContext("tuscany.system.child");
//        Assert.assertNotNull(ctx);
//        runtime.getRootContext().registerModelObject(MockFactory.createCompositeComponent("test"));
//        CompositeContext testCtx = (CompositeContext) runtime.getRootContext().getContext("test");
//        Assert.assertNotNull(testCtx);
//        testCtx.registerModelObject(MockFactory.createModule(Scope.REQUEST,Scope.REQUEST));
//        testCtx.publish(new ModuleStart(this));
//
//        // first request
//        Object id = new Object();
//        testCtx.publish(new RequestStart(this,id));
//        GenericComponent source = (GenericComponent)testCtx.getContext("source").getInstance(null);
//        Assert.assertNotNull(source);
//        GenericComponent target = (GenericComponent)testCtx.getContext("target").getInstance(null);
//        Assert.assertNotNull(target);
//        source.getGenericComponent().setString("foo");
//        Assert.assertEquals("foo",target.getString());
//        testCtx.publish(new RequestEnd(this,new Object()));
//
//        //second request
//        Object id2 = new Object();
//        testCtx.publish(new RequestStart(this,id2));
//        GenericComponent source2 = (GenericComponent)testCtx.getContext("source").getInstance(null);
//        Assert.assertNotNull(source2);
//        GenericComponent target2 = (GenericComponent)testCtx.getContext("target").getInstance(null);
//
//        Assert.assertNotNull(target2);
//        Assert.assertEquals(null,target2.getString());
//        Assert.assertEquals(null,source2.getGenericComponent().getString());
//        source2.getGenericComponent().setString("baz");
//        Assert.assertEquals("baz",source2.getGenericComponent().getString());
//        Assert.assertEquals("baz",target2.getString());
//
//        testCtx.publish(new RequestEnd(this,new Object()));
    }

    /**
     * Tests a request-to-module scoped wire is setup properly by the runtime
     */
    public void testRequestToModule() throws Exception {
//        RuntimeContext runtime = MockFactory.createJavaRuntime();
//        Context ctx = runtime.getSystemContext().getContext("tuscany.system.child");
//        Assert.assertNotNull(ctx);
//        runtime.getRootContext().registerModelObject(MockFactory.createCompositeComponent("test"));
//        CompositeContext testCtx = (CompositeContext) runtime.getRootContext().getContext("test");
//        Assert.assertNotNull(testCtx);
//        testCtx.registerModelObject(MockFactory.createModule(Scope.REQUEST,Scope.MODULE));
//        testCtx.publish(new ModuleStart(this));
//
//        // first request
//        Object id = new Object();
//        testCtx.publish(new RequestStart(this,id));
//        GenericComponent source = (GenericComponent)testCtx.getContext("source").getInstance(null);
//        Assert.assertNotNull(source);
//        GenericComponent target = (GenericComponent)testCtx.getContext("target").getInstance(null);
//        Assert.assertNotNull(target);
//        source.getGenericComponent().setString("foo");
//        Assert.assertEquals("foo",target.getString());
//        testCtx.publish(new RequestEnd(this,new Object()));
//
//        //second request
//        Object id2 = new Object();
//        testCtx.publish(new RequestStart(this,id2));
//        GenericComponent source2 = (GenericComponent)testCtx.getContext("source").getInstance(null);
//        Assert.assertNotNull(source2);
//        GenericComponent target2 = (GenericComponent)testCtx.getContext("target").getInstance(null);
//
//        Assert.assertNotNull(target2);
//        Assert.assertEquals("foo",target2.getString());
//        Assert.assertEquals("foo",source2.getGenericComponent().getString());
//        source2.getGenericComponent().setString("baz");
//        Assert.assertEquals("baz",source2.getGenericComponent().getString());
//        Assert.assertEquals("baz",target2.getString());
//        Assert.assertEquals("baz",target.getString());
//
//        testCtx.publish(new RequestEnd(this,new Object()));
    }

    /**
     * Tests a request-to-session scoped wire is setup properly by the runtime
     */
    public void testRequestToSession() throws Exception {
//        RuntimeContext runtime = MockFactory.createJavaRuntime();
//        Context ctx = runtime.getSystemContext().getContext("tuscany.system.child");
//        Assert.assertNotNull(ctx);
//        runtime.getRootContext().registerModelObject(MockFactory.createCompositeComponent("test"));
//        CompositeContext testCtx = (CompositeContext) runtime.getRootContext().getContext("test");
//        Assert.assertNotNull(testCtx);
//        testCtx.registerModelObject(MockFactory.createModule(Scope.REQUEST,Scope.SESSION));
//        testCtx.publish(new ModuleStart(this));
//
//        // first session
//        Object session = new Object();
//        Object id = new Object();
//        testCtx.publish(new RequestStart(this,id));
//        testCtx.publish(new HttpSessionBound(this,session));
//        GenericComponent source = (GenericComponent)testCtx.getContext("source").getInstance(null);
//        Assert.assertNotNull(source);
//        GenericComponent target = (GenericComponent)testCtx.getContext("target").getInstance(null);
//        Assert.assertNotNull(target);
//        source.getGenericComponent().setString("foo");
//        Assert.assertEquals("foo",target.getString());
//        testCtx.publish(new RequestEnd(this,new Object()));
//
//        //second request for session
//        Object id2 = new Object();
//        testCtx.publish(new RequestStart(this,id2));
//        testCtx.publish(new HttpSessionBound(this,session));
//        GenericComponent targetR2 = (GenericComponent)testCtx.getContext("target").getInstance(null);
//        Assert.assertEquals("foo",targetR2.getString());
//        GenericComponent sourceR2 = (GenericComponent)testCtx.getContext("source").getInstance(null);
//        Assert.assertNotNull(sourceR2);
//        Assert.assertEquals("foo",sourceR2.getGenericComponent().getString());
//
//        testCtx.publish(new RequestEnd(this,new Object()));
//
//        //second session
//        Object session2 = new Object();
//        Object id3 = new Object();
//        testCtx.publish(new RequestStart(this,id3));
//        testCtx.publish(new HttpSessionBound(this,session2));
//        GenericComponent source2 = (GenericComponent)testCtx.getContext("source").getInstance(null);
//        Assert.assertNotNull(source2);
//        GenericComponent target2 = (GenericComponent)testCtx.getContext("target").getInstance(null);
//
//        Assert.assertNotNull(target2);
//        Assert.assertEquals(null,target2.getString());
//        Assert.assertEquals(null,source2.getGenericComponent().getString());
//        source2.getGenericComponent().setString("baz");
//        Assert.assertEquals("baz",source2.getGenericComponent().getString());
//        Assert.assertEquals("baz",target2.getString());
//
//        testCtx.publish(new RequestEnd(this,session2));
//        Object id4 = new Object();
//        testCtx.publish(new RequestStart(this,id4));
//        testCtx.publish(new HttpSessionBound(this,session));
//        testCtx.publish(new RequestEnd(this,session));

    }


    /**
     * Tests a request-to-stateless scoped wire is setup properly by the runtime
     */
    public void testRequestToStateless() throws Exception {
//        RuntimeContext runtime = MockFactory.createJavaRuntime();
//        Context ctx = runtime.getSystemContext().getContext("tuscany.system.child");
//        Assert.assertNotNull(ctx);
//        runtime.getRootContext().registerModelObject(MockFactory.createCompositeComponent("test"));
//        CompositeContext testCtx = (CompositeContext) runtime.getRootContext().getContext("test");
//        Assert.assertNotNull(testCtx);
//        testCtx.registerModelObject(MockFactory.createModule(Scope.REQUEST,Scope.INSTANCE));
//        testCtx.publish(new ModuleStart(this));
//
//        // first request
//        Object id = new Object();
//        testCtx.publish(new RequestStart(this,id));
//        GenericComponent source = (GenericComponent)testCtx.getContext("source").getInstance(null);
//        Assert.assertNotNull(source);
//        GenericComponent target = (GenericComponent)testCtx.getContext("target").getInstance(null);
//        Assert.assertNotNull(target);
//        source.getGenericComponent().setString("foo");
//        Assert.assertEquals(null,target.getString());
//        testCtx.publish(new RequestEnd(this,new Object()));
//
//        //second request
//        Object id2 = new Object();
//        testCtx.publish(new RequestStart(this,id2));
//        GenericComponent source2 = (GenericComponent)testCtx.getContext("source").getInstance(null);
//        Assert.assertNotNull(source2);
//        GenericComponent target2 = (GenericComponent)testCtx.getContext("target").getInstance(null);
//
//        Assert.assertNotNull(target2);
//        Assert.assertEquals(null,target2.getString());
//        Assert.assertEquals(null,source2.getGenericComponent().getString());
//        source2.getGenericComponent().setString("baz");
//        Assert.assertEquals(null,source2.getGenericComponent().getString());
//        Assert.assertEquals(null,target2.getString());
//
//        testCtx.publish(new RequestEnd(this,new Object()));
    }


    /**
     * Tests a stateless-to-stateless scoped wire is setup properly by the runtime
     */
    public void testStatelessToStateless() throws Exception {
//        RuntimeContext runtime = MockFactory.createJavaRuntime();
//        Context ctx = runtime.getSystemContext().getContext("tuscany.system.child");
//        Assert.assertNotNull(ctx);
//        runtime.getRootContext().registerModelObject(MockFactory.createCompositeComponent("test"));
//        CompositeContext testCtx = (CompositeContext) runtime.getRootContext().getContext("test");
//        Assert.assertNotNull(testCtx);
//        testCtx.registerModelObject(MockFactory.createModule(Scope.INSTANCE,Scope.INSTANCE));
//        testCtx.publish(new ModuleStart(this));
//
//        // first request
//        Object id = new Object();
//        testCtx.publish(new RequestStart(this,id));
//        GenericComponent source = (GenericComponent)testCtx.getContext("source").getInstance(null);
//        Assert.assertNotNull(source);
//        GenericComponent target = (GenericComponent)testCtx.getContext("target").getInstance(null);
//        Assert.assertNotNull(target);
//        source.getGenericComponent().setString("foo");
//        Assert.assertEquals(null,target.getString());
//        testCtx.publish(new RequestEnd(this,new Object()));
//
//        //second request
//        Object id2 = new Object();
//        testCtx.publish(new RequestStart(this,id2));
//        GenericComponent source2 = (GenericComponent)testCtx.getContext("source").getInstance(null);
//        Assert.assertNotNull(source2);
//        GenericComponent target2 = (GenericComponent)testCtx.getContext("target").getInstance(null);
//
//        Assert.assertNotNull(target2);
//        Assert.assertEquals(null,target2.getString());
//        Assert.assertEquals(null,source2.getGenericComponent().getString());
//        source2.getGenericComponent().setString("baz");
//        Assert.assertEquals(null,source2.getGenericComponent().getString());
//        Assert.assertEquals(null,target2.getString());
//
//        testCtx.publish(new RequestEnd(this,new Object()));
    }

    /**
     * Tests a stateless-to-request scoped wire is setup properly by the runtime
     */
    public void testStatelessToRequest() throws Exception {
//        RuntimeContext runtime = MockFactory.createJavaRuntime();
//        Context ctx = runtime.getSystemContext().getContext("tuscany.system.child");
//        Assert.assertNotNull(ctx);
//        runtime.getRootContext().registerModelObject(MockFactory.createCompositeComponent("test"));
//        CompositeContext testCtx = (CompositeContext) runtime.getRootContext().getContext("test");
//        Assert.assertNotNull(testCtx);
//        testCtx.registerModelObject(MockFactory.createModule(Scope.INSTANCE,Scope.REQUEST));
//        testCtx.publish(new ModuleStart(this));
//
//        // first request
//        Object id = new Object();
//        testCtx.publish(new RequestStart(this,id));
//        GenericComponent source = (GenericComponent)testCtx.getContext("source").getInstance(null);
//        Assert.assertNotNull(source);
//        GenericComponent target = (GenericComponent)testCtx.getContext("target").getInstance(null);
//        Assert.assertNotNull(target);
//        source.getGenericComponent().setString("foo");
//        Assert.assertEquals("foo",target.getString());
//        testCtx.publish(new RequestEnd(this,new Object()));
//
//        GenericComponent targetR1 = (GenericComponent)testCtx.getContext("target").getInstance(null);
//        Assert.assertNotNull(targetR1);
//        Assert.assertEquals("foo",target.getString());
//
//        //second request
//        Object id2 = new Object();
//        testCtx.publish(new RequestStart(this,id2));
//        GenericComponent source2 = (GenericComponent)testCtx.getContext("source").getInstance(null);
//        Assert.assertNotNull(source2);
//        GenericComponent target2 = (GenericComponent)testCtx.getContext("target").getInstance(null);
//
//        Assert.assertNotNull(target2);
//        Assert.assertEquals(null,target2.getString());
//        Assert.assertEquals(null,source2.getGenericComponent().getString());
//        source2.getGenericComponent().setString("baz");
//        Assert.assertEquals("baz",source2.getGenericComponent().getString());
//        Assert.assertEquals("baz",target2.getString());
//
//        testCtx.publish(new RequestEnd(this,new Object()));
    }

    /**
     * Tests a stateless-to-session scoped wire is setup properly by the runtime
     */
    public void testStatelessToSession() throws Exception {
//        RuntimeContext runtime = MockFactory.createJavaRuntime();
//        Context ctx = runtime.getSystemContext().getContext("tuscany.system.child");
//        Assert.assertNotNull(ctx);
//        runtime.getRootContext().registerModelObject(MockFactory.createCompositeComponent("test"));
//        CompositeContext testCtx = (CompositeContext) runtime.getRootContext().getContext("test");
//        Assert.assertNotNull(testCtx);
//        testCtx.registerModelObject(MockFactory.createModule(Scope.INSTANCE,Scope.SESSION));
//        testCtx.publish(new ModuleStart(this));
//
//        // first session
//        Object session = new Object();
//        Object id = new Object();
//        testCtx.publish(new RequestStart(this,id));
//        testCtx.publish(new HttpSessionBound(this,session));
//        GenericComponent source = (GenericComponent)testCtx.getContext("source").getInstance(null);
//        Assert.assertNotNull(source);
//        GenericComponent target = (GenericComponent)testCtx.getContext("target").getInstance(null);
//        Assert.assertNotNull(target);
//        source.getGenericComponent().setString("foo");
//        Assert.assertEquals("foo",target.getString());
//        testCtx.publish(new RequestEnd(this,new Object()));
//
//        //second request for session
//        Object id2 = new Object();
//        testCtx.publish(new RequestStart(this,id2));
//        testCtx.publish(new HttpSessionBound(this,session));
//        GenericComponent targetR2 = (GenericComponent)testCtx.getContext("target").getInstance(null);
//        Assert.assertEquals("foo",targetR2.getString());
//        GenericComponent sourceR2 = (GenericComponent)testCtx.getContext("source").getInstance(null);
//        Assert.assertNotNull(sourceR2);
//        Assert.assertEquals("foo",sourceR2.getGenericComponent().getString());
//
//        testCtx.publish(new RequestEnd(this,new Object()));
//
//        //second session
//        Object session2 = new Object();
//        Object id3 = new Object();
//        testCtx.publish(new RequestStart(this,id3));
//        testCtx.publish(new HttpSessionBound(this,session2));
//        GenericComponent source2 = (GenericComponent)testCtx.getContext("source").getInstance(null);
//        Assert.assertNotNull(source2);
//        GenericComponent target2 = (GenericComponent)testCtx.getContext("target").getInstance(null);
//
//        Assert.assertNotNull(target2);
//        Assert.assertEquals(null,target2.getString());
//        Assert.assertEquals(null,source2.getGenericComponent().getString());
//        source2.getGenericComponent().setString("baz");
//        Assert.assertEquals("baz",source2.getGenericComponent().getString());
//        Assert.assertEquals("baz",target2.getString());
//
//        testCtx.publish(new RequestEnd(this,session2));
//         Object id4 = new Object();
//        testCtx.publish(new RequestStart(this,id4));
//        testCtx.publish(new HttpSessionBound(this,session));
//        testCtx.publish(new RequestEnd(this,session));
//
    }


    /**
     * Tests a stateless-to-module scoped wire is setup properly by the runtime
     */
    public void testStatelessToModule() throws Exception {
//        RuntimeContext runtime = MockFactory.createJavaRuntime();
//        Context ctx = runtime.getSystemContext().getContext("tuscany.system.child");
//        Assert.assertNotNull(ctx);
//        runtime.getRootContext().registerModelObject(MockFactory.createCompositeComponent("test"));
//        CompositeContext testCtx = (CompositeContext) runtime.getRootContext().getContext("test");
//        Assert.assertNotNull(testCtx);
//        testCtx.registerModelObject(MockFactory.createModule(Scope.INSTANCE,Scope.MODULE));
//        testCtx.publish(new ModuleStart(this));
//
//        Object id = new Object();
//        testCtx.publish(new RequestStart(this,id));
//        GenericComponent source = (GenericComponent)testCtx.getContext("source").getInstance(null);
//        Assert.assertNotNull(source);
//        GenericComponent target = (GenericComponent)testCtx.getContext("target").getInstance(null);
//        Assert.assertNotNull(target);
//        source.getGenericComponent().setString("foo");
//        Assert.assertEquals("foo",target.getString());
//        testCtx.publish(new RequestEnd(this,new Object()));
//
//        //second session
//        Object id2 = new Object();
//        testCtx.publish(new RequestStart(this,id2));
//        GenericComponent source2 = (GenericComponent)testCtx.getContext("source").getInstance(null);
//        Assert.assertNotNull(source2);
//        GenericComponent target2 = (GenericComponent)testCtx.getContext("target").getInstance(null);
//
//        Assert.assertNotNull(target2);
//        Assert.assertEquals("foo",target2.getString());
//        Assert.assertEquals("foo",source2.getGenericComponent().getString());
//        source2.getGenericComponent().setString("baz");
//        Assert.assertEquals("baz",source2.getGenericComponent().getString());
//        Assert.assertEquals("baz",target2.getString());
//
//        testCtx.publish(new RequestEnd(this,new Object()));
//
    }

    protected void setUp() throws Exception {
        super.setUp();
        members = new HashMap<String, Member>();
        Method[] methods = SourceImpl.class.getMethods();
        for (Method method : methods) {
            if (method.getName().startsWith("set")) {
                members.put(JavaIntrospectionHelper.toPropertyName(method.getName()), method);
            }
        }
    }


}

