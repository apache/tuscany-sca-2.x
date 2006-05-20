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
import org.apache.tuscany.container.java.mock.MockContextFactory;
import org.apache.tuscany.container.java.mock.components.Source;
import org.apache.tuscany.container.java.mock.components.SourceImpl;
import org.apache.tuscany.container.java.mock.components.Target;
import org.apache.tuscany.container.java.mock.components.TargetImpl;
import org.apache.tuscany.core.context.WorkContextImpl;
import org.apache.tuscany.core.context.event.HttpSessionEnd;
import org.apache.tuscany.core.context.event.HttpSessionStart;
import org.apache.tuscany.core.context.event.ModuleStart;
import org.apache.tuscany.core.context.event.ModuleStop;
import org.apache.tuscany.core.context.event.RequestEnd;
import org.apache.tuscany.core.context.event.RequestStart;
import org.apache.tuscany.core.context.scope.HttpSessionScopeContext;
import org.apache.tuscany.core.context.scope.ModuleScopeContext;
import org.apache.tuscany.core.context.scope.RequestScopeContext;
import org.apache.tuscany.core.context.scope.StatelessScopeContext;
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
        ScopeContext scope = new ModuleScopeContext(ctx);
        scope.start();

        Map<String, AtomicContext> contexts = MockContextFactory.createWiredContexts("source", SourceImpl.class, scope, members, "target", Target.class, TargetImpl.class, scope);
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
        ScopeContext moduleScope = new ModuleScopeContext(ctx);
        moduleScope.start();
        ScopeContext sessionScope = new HttpSessionScopeContext(ctx);
        sessionScope.start();

        Map<String, AtomicContext> contexts = MockContextFactory.createWiredContexts("source", SourceImpl.class,
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
        assertFalse("foo".equals(target2.getString()));

        assertFalse("foo".equals(source.getTarget().getString()));
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
        ScopeContext moduleScope = new ModuleScopeContext(ctx);
        moduleScope.start();
        final ScopeContext requestScope = new RequestScopeContext(ctx);
        requestScope.start();

        Map<String, AtomicContext> contexts = MockContextFactory.createWiredContexts("source", SourceImpl.class,
                moduleScope, members, "target", Target.class, TargetImpl.class, requestScope);
        moduleScope.onEvent(new ModuleStart(this, null));
        requestScope.onEvent(new RequestStart(this));

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
                requestScope.onEvent(new RequestStart(this));
                Target target2 = targetContext.getService();
                assertFalse("foo".equals(target2.getString()));
                assertFalse("foo".equals(source.getTarget().getString()));
                source.getTarget().setString("bar");
                assertEquals("bar", target2.getString());
                assertEquals("bar", source.getTarget().getString());
                requestScope.onEvent(new RequestEnd(this));
            }
        }, null);
        executor.execute(future);
        future.get();
        assertEquals("foo", source.getTarget().getString());
        requestScope.onEvent(new RequestEnd(this));
        moduleScope.onEvent(new ModuleStop(this,null));
        requestScope.stop();
        moduleScope.stop();
    }

    /**
     * Tests a module-to-stateless scoped wire is setup properly by the runtime
     */
    @SuppressWarnings("unchecked")
    public void testModuleToStateless() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        ScopeContext moduleScope = new ModuleScopeContext(ctx);
        moduleScope.start();
        ScopeContext statelessScope = new StatelessScopeContext(ctx);
        statelessScope.start();

        Map<String, AtomicContext> contexts = MockContextFactory.createWiredContexts("source", SourceImpl.class,
                moduleScope, members, "target", Target.class, TargetImpl.class, statelessScope);
        moduleScope.onEvent(new ModuleStart(this, null));

        AtomicContext<Source> sourceContext = (AtomicContext<Source>) contexts.get("source");
        AtomicContext<Target> targetContext = (AtomicContext<Target>) contexts.get("target");
        Source source = sourceContext.getService();
        Target target = targetContext.getService();
        assertTrue(Proxy.isProxyClass(source.getTarget().getClass()));
        assertNull(source.getTarget().getString());
        assertNull(target.getString());
        target.setString("foo");
        assertFalse("foo".equals(source.getTarget().getString()));
        Target target2 = targetContext.getService();
        assertFalse("foo".equals(target2.getString()));
        source.getTarget().setString("bar");
        assertFalse("bar".equals(source.getTarget().getString()));
        moduleScope.onEvent(new ModuleStop(this, null));
        moduleScope.stop();
        statelessScope.stop();
    }


    /**
     * Tests a session-to-session scoped wire
     */
    @SuppressWarnings("unchecked")
    public void testSessionToSession() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        ScopeContext sessionScope = new HttpSessionScopeContext(ctx);
        sessionScope.start();

        Map<String, AtomicContext> contexts = MockContextFactory.createWiredContexts("source", SourceImpl.class,
                sessionScope, members, "target", Target.class, TargetImpl.class, sessionScope);

        Object session1 = new Object();
        ctx.setIdentifier(HttpSessionScopeContext.HTTP_IDENTIFIER, session1);
        sessionScope.onEvent(new HttpSessionStart(this, session1));
        AtomicContext<Source> sourceContext = (AtomicContext<Source>) contexts.get("source");
        AtomicContext<Target> targetContext = (AtomicContext<Target>) contexts.get("target");
        Source source = sourceContext.getService();
        Target target = targetContext.getService();
        source.getTarget().setString("foo");
        source.getTarget().setString("foo");
        assertEquals("foo", target.getString());

        ctx.clearIdentifier(HttpSessionScopeContext.HTTP_IDENTIFIER);
        sessionScope.onEvent(new HttpSessionEnd(this, session1));

        //second session
        Object session2 = new Object();
        ctx.setIdentifier(HttpSessionScopeContext.HTTP_IDENTIFIER, session2);
        sessionScope.onEvent(new HttpSessionStart(this, session2));

        Source source2 = sourceContext.getService();
        assertNotNull(source2);
        Target target2 = targetContext.getService();

        assertNotNull(target2);
        assertNull(target2.getString());
        assertEquals(null, source2.getTarget().getString());
        source2.getTarget().setString("baz");
        assertEquals("baz", source2.getTarget().getString());
        assertEquals("baz", target2.getString());
        ctx.clearIdentifier(HttpSessionScopeContext.HTTP_IDENTIFIER);
        sessionScope.onEvent(new HttpSessionEnd(this, session2));
        sessionScope.stop();
    }


    /**
     * Tests a session-to-module scoped wire
     */
    @SuppressWarnings("unchecked")
    public void testSessionToModule() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        ScopeContext moduleScope = new ModuleScopeContext(ctx);
        moduleScope.start();
        ScopeContext sessionScope = new HttpSessionScopeContext(ctx);
        sessionScope.start();

        Map<String, AtomicContext> contexts = MockContextFactory.createWiredContexts("source", SourceImpl.class,
                sessionScope, members, "target", Target.class, TargetImpl.class, moduleScope);
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
        Source source2 = sourceContext.getService();
        assertEquals("foo", target2.getString());
        assertEquals("foo", source2.getTarget().getString());
        source2.getTarget().setString("baz");
        assertEquals("baz", source2.getTarget().getString());
        assertEquals("baz", target2.getString());
        assertEquals("baz", target.getString());
        ctx.clearIdentifier(HttpSessionScopeContext.HTTP_IDENTIFIER);
        sessionScope.onEvent(new HttpSessionEnd(this, session2));
        moduleScope.stop();
        sessionScope.stop();
    }

    /**
     * Tests a session-to-request scoped wire is setup properly by the runtime
     */
    @SuppressWarnings("unchecked")
    public void testSessionToRequest() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        final ScopeContext requestScope = new RequestScopeContext(ctx);
        requestScope.start();
        ScopeContext sessionScope = new HttpSessionScopeContext(ctx);
        sessionScope.start();

        Map<String, AtomicContext> contexts = MockContextFactory.createWiredContexts("source", SourceImpl.class,
                sessionScope, members, "target", Target.class, TargetImpl.class, requestScope);
        Object session1 = new Object();
        ctx.setIdentifier(HttpSessionScopeContext.HTTP_IDENTIFIER, session1);
        sessionScope.onEvent(new HttpSessionStart(this, session1));
        requestScope.onEvent(new RequestStart(this));
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
                requestScope.onEvent(new RequestStart(this));
                Target target2 = targetContext.getService();
                assertFalse("foo".equals(target2.getString()));
                assertFalse("foo".equals(source.getTarget().getString()));
                source.getTarget().setString("bar");
                assertEquals("bar", target2.getString());
                assertEquals("bar", source.getTarget().getString());
                requestScope.onEvent(new RequestEnd(this));
            }
        }, null);
        executor.execute(future);
        future.get();
        assertEquals("foo", source.getTarget().getString());
        requestScope.onEvent(new RequestEnd(this));
        ctx.clearIdentifier(HttpSessionScopeContext.HTTP_IDENTIFIER);
        sessionScope.onEvent(new HttpSessionEnd(this, session1));
        requestScope.stop();
        sessionScope.stop();
    }


    /**
     * Tests a session-to-stateless scoped wire is setup properly by the runtime
     */
    @SuppressWarnings("unchecked")
    public void testSessionToStateless() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        ScopeContext sessionScope = new HttpSessionScopeContext(ctx);
        sessionScope.start();
        ScopeContext statelessScope = new StatelessScopeContext(ctx);
        statelessScope.start();

        Map<String, AtomicContext> contexts = MockContextFactory.createWiredContexts("source", SourceImpl.class,
                sessionScope, members, "target", Target.class, TargetImpl.class, statelessScope);

        Object session1 = new Object();
        ctx.setIdentifier(HttpSessionScopeContext.HTTP_IDENTIFIER, session1);
        sessionScope.onEvent(new HttpSessionStart(this, session1));

        AtomicContext<Source> sourceContext = (AtomicContext<Source>) contexts.get("source");
        AtomicContext<Target> targetContext = (AtomicContext<Target>) contexts.get("target");
        Source source = sourceContext.getService();
        Target target = targetContext.getService();
        assertTrue(Proxy.isProxyClass(source.getTarget().getClass()));
        assertNull(source.getTarget().getString());
        assertNull(target.getString());
        target.setString("foo");
        assertFalse("foo".equals(source.getTarget().getString()));
        Target target2 = targetContext.getService();
        assertFalse("foo".equals(target2.getString()));
        source.getTarget().setString("bar");
        assertFalse("bar".equals(source.getTarget().getString()));

        ctx.clearIdentifier(HttpSessionScopeContext.HTTP_IDENTIFIER);
        sessionScope.onEvent(new HttpSessionEnd(this, session1));
        sessionScope.stop();
        statelessScope.stop();
    }

    /**
     * Tests a request-to-request scoped wire is setup properly by the runtime
     */
    @SuppressWarnings("unchecked")
    public void testRequestToRequest() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        final ScopeContext requestScope = new RequestScopeContext(ctx);
        requestScope.start();

        Map<String, AtomicContext> contexts = MockContextFactory.createWiredContexts("source", SourceImpl.class,
                requestScope, members, "target", Target.class, TargetImpl.class, requestScope);
        requestScope.onEvent(new RequestStart(this));

        final AtomicContext<Source> sourceContext = (AtomicContext<Source>) contexts.get("source");
        final AtomicContext<Target> targetContext = (AtomicContext<Target>) contexts.get("target");
        Source source = sourceContext.getService();
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
                requestScope.onEvent(new RequestStart(this));
                Source source2 = sourceContext.getService();
                Target target2 = targetContext.getService();
                assertFalse("foo".equals(target2.getString()));
                assertFalse("foo".equals(source2.getTarget().getString()));
                source2.getTarget().setString("bar");
                assertEquals("bar", target2.getString());
                assertEquals("bar", source2.getTarget().getString());
                requestScope.onEvent(new RequestEnd(this));
            }
        }, null);
        executor.execute(future);
        future.get();
        requestScope.onEvent(new RequestEnd(this));
        requestScope.stop();
    }

    /**
     * Tests a request-to-module scoped wire
     */
    @SuppressWarnings("unchecked")
    public void testRequestToModule() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        final ScopeContext requestScope = new RequestScopeContext(ctx);
        final ScopeContext moduleScope = new ModuleScopeContext(ctx);
        requestScope.start();
        moduleScope.start();
        moduleScope.onEvent(new ModuleStart(this, null));

        Map<String, AtomicContext> contexts = MockContextFactory.createWiredContexts("source", SourceImpl.class,
                requestScope, members, "target", Target.class, TargetImpl.class, moduleScope);
        requestScope.onEvent(new RequestStart(this));

        final AtomicContext<Source> sourceContext = (AtomicContext<Source>) contexts.get("source");
        final AtomicContext<Target> targetContext = (AtomicContext<Target>) contexts.get("target");
        Source source = sourceContext.getService();
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
                requestScope.onEvent(new RequestStart(this));
                Source source2 = sourceContext.getService();
                Target target2 = targetContext.getService();
                assertTrue("foo".equals(target2.getString()));
                assertTrue("foo".equals(source2.getTarget().getString()));
                source2.getTarget().setString("bar");
                assertEquals("bar", target2.getString());
                assertEquals("bar", source2.getTarget().getString());
                requestScope.onEvent(new RequestEnd(this));
            }
        }, null);
        executor.execute(future);
        future.get();
        assertEquals("bar", target.getString());

        requestScope.onEvent(new RequestEnd(this));
        requestScope.stop();
        moduleScope.onEvent(new ModuleStop(this, null));
        moduleScope.stop();
    }

    /**
     * Tests a request-to-session scoped wire is setup properly by the runtime
     */
    @SuppressWarnings("unchecked")
    public void testRequestToSession() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        final ScopeContext requestScope = new RequestScopeContext(ctx);
        final ScopeContext sessionScope = new HttpSessionScopeContext(ctx);
        requestScope.start();
        sessionScope.start();

        Object session1 = new Object();
        ctx.setIdentifier(HttpSessionScopeContext.HTTP_IDENTIFIER, session1);
        sessionScope.onEvent(new HttpSessionStart(this, session1));
        Map<String, AtomicContext> contexts = MockContextFactory.createWiredContexts("source", SourceImpl.class,
                requestScope, members, "target", Target.class, TargetImpl.class, sessionScope);

        final AtomicContext<Source> sourceContext = (AtomicContext<Source>) contexts.get("source");
        final AtomicContext<Target> targetContext = (AtomicContext<Target>) contexts.get("target");
        requestScope.onEvent(new RequestStart(this));
        Source source = sourceContext.getService();
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
                requestScope.onEvent(new RequestStart(this));
                Source source2 = sourceContext.getService();
                Target target2 = targetContext.getService();
                assertTrue("foo".equals(target2.getString()));
                assertTrue("foo".equals(source2.getTarget().getString()));
                source2.getTarget().setString("bar");
                assertEquals("bar", target2.getString());
                assertEquals("bar", source2.getTarget().getString());
                requestScope.onEvent(new RequestEnd(this));
            }
        }, null);
        executor.execute(future);
        future.get();
        assertEquals("bar", target.getString());

        requestScope.onEvent(new RequestEnd(this));
        requestScope.stop();
        ctx.clearIdentifier(HttpSessionScopeContext.HTTP_IDENTIFIER);
        sessionScope.onEvent(new HttpSessionEnd(this, session1));
        sessionScope.stop();
    }


    /**
     * Tests a request-to-stateless scoped wire is setup properly by the runtime
     */
    @SuppressWarnings("unchecked")
    public void testRequestToStateless() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        ScopeContext requestScope = new RequestScopeContext(ctx);
        requestScope.start();
        ScopeContext statelessScope = new StatelessScopeContext(ctx);
        statelessScope.start();

        Map<String, AtomicContext> contexts = MockContextFactory.createWiredContexts("source", SourceImpl.class,
                requestScope, members, "target", Target.class, TargetImpl.class, statelessScope);

        AtomicContext<Source> sourceContext = (AtomicContext<Source>) contexts.get("source");
        AtomicContext<Target> targetContext = (AtomicContext<Target>) contexts.get("target");
        requestScope.onEvent(new RequestStart(this));
        Source source = sourceContext.getService();
        Target target = targetContext.getService();
        assertTrue(Proxy.isProxyClass(source.getTarget().getClass()));
        assertNull(source.getTarget().getString());
        assertNull(target.getString());
        target.setString("foo");
        assertFalse("foo".equals(source.getTarget().getString()));
        Target target2 = targetContext.getService();
        assertFalse("foo".equals(target2.getString()));
        source.getTarget().setString("bar");
        assertFalse("bar".equals(source.getTarget().getString()));
        requestScope.onEvent(new RequestEnd(this));
        requestScope.stop();
        statelessScope.stop();
    }


    /**
     * Tests a stateless-to-stateless scoped wire is setup properly by the runtime
     */
    @SuppressWarnings("unchecked")
    public void testStatelessToStateless() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        ScopeContext statelessScope = new StatelessScopeContext(ctx);
        statelessScope.start();

        Map<String, AtomicContext> contexts = MockContextFactory.createWiredContexts("source", SourceImpl.class,
                statelessScope, members, "target", Target.class, TargetImpl.class, statelessScope);

        AtomicContext<Source> sourceContext = (AtomicContext<Source>) contexts.get("source");
        AtomicContext<Target> targetContext = (AtomicContext<Target>) contexts.get("target");
        Source source = sourceContext.getService();
        Target target = targetContext.getService();
        assertTrue(Proxy.isProxyClass(source.getTarget().getClass()));
        assertNull(source.getTarget().getString());
        assertNull(target.getString());
        target.setString("foo");
        assertFalse("foo".equals(source.getTarget().getString()));
        Target target2 = targetContext.getService();
        assertFalse("foo".equals(target2.getString()));
        source.getTarget().setString("bar");
        assertFalse("bar".equals(source.getTarget().getString()));
        statelessScope.stop();
    }

    /**
     * Tests a stateless-to-request scoped wire is setup properly by the runtime
     */
    @SuppressWarnings("unchecked")
    public void testStatelessToRequest() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        final ScopeContext requestScope = new RequestScopeContext(ctx);
        requestScope.start();
        ScopeContext statelessScope = new StatelessScopeContext(ctx);
        statelessScope.start();

        Map<String, AtomicContext> contexts = MockContextFactory.createWiredContexts("source", SourceImpl.class,
                statelessScope, members, "target", Target.class, TargetImpl.class, requestScope);
        requestScope.onEvent(new RequestStart(this));
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
                requestScope.onEvent(new RequestStart(this));
                Target target2 = targetContext.getService();
                assertFalse("foo".equals(target2.getString()));
                assertFalse("foo".equals(source.getTarget().getString()));
                source.getTarget().setString("bar");
                assertEquals("bar", target2.getString());
                assertEquals("bar", source.getTarget().getString());
                requestScope.onEvent(new RequestEnd(this));
            }
        }, null);
        executor.execute(future);
        future.get();
        requestScope.stop();
        statelessScope.stop();
    }

    /**
     * Tests a stateless-to-session scoped wire is setup properly by the runtime
     */
    @SuppressWarnings("unchecked")
    public void testStatelessToSession() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        ScopeContext statelessScope = new StatelessScopeContext(ctx);
        statelessScope.start();
        ScopeContext sessionScope = new HttpSessionScopeContext(ctx);
        sessionScope.start();

        Map<String, AtomicContext> contexts = MockContextFactory.createWiredContexts("source", SourceImpl.class,
                statelessScope, members, "target", Target.class, TargetImpl.class, sessionScope);
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
        assertFalse("foo".equals(target2.getString()));

        assertFalse("foo".equals(source.getTarget().getString()));
        source.getTarget().setString("bar");
        assertEquals("bar", target2.getString());
        assertEquals("bar", source.getTarget().getString());
        sessionScope.onEvent(new HttpSessionEnd(this, session2));

        ctx.clearIdentifier(HttpSessionScopeContext.HTTP_IDENTIFIER);
        sessionScope.stop();
        statelessScope.stop();
    }


    /**
     * Tests a stateless-to-module scoped wire is setup properly by the runtime
     */
    @SuppressWarnings("unchecked")
    public void testStatelessToModule() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        ScopeContext statelessScope = new StatelessScopeContext(ctx);
        statelessScope.start();
        ScopeContext moduleScope = new ModuleScopeContext(ctx);
        moduleScope.start();

        Map<String, AtomicContext> contexts = MockContextFactory.createWiredContexts("source", SourceImpl.class,
                statelessScope, members, "target", Target.class, TargetImpl.class, moduleScope);
        moduleScope.onEvent(new ModuleStart(this, null));
        AtomicContext<Source> sourceContext = (AtomicContext<Source>) contexts.get("source");
        AtomicContext<Target> targetContext = (AtomicContext<Target>) contexts.get("target");
        Source source = sourceContext.getService();
        Target target = targetContext.getService();
        assertNull(source.getTarget().getString());
        assertNull(target.getString());
        target.setString("foo");
        assertTrue(Proxy.isProxyClass(source.getTarget().getClass()));
        assertEquals("foo", source.getTarget().getString());

        //second session
        Object session2 = new Object();
        ctx.setIdentifier(HttpSessionScopeContext.HTTP_IDENTIFIER, session2);
        moduleScope.onEvent(new HttpSessionStart(this, session2));

        Target target2 = targetContext.getService();
        assertTrue("foo".equals(target2.getString()));

        assertTrue("foo".equals(source.getTarget().getString()));
        source.getTarget().setString("bar");
        assertEquals("bar", target2.getString());
        assertEquals("bar", source.getTarget().getString());

        moduleScope.onEvent(new ModuleStop(this, null));
        moduleScope.stop();
        statelessScope.stop();
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

