/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.apache.tuscany.core.integration.scope;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.model.Scope;

import junit.framework.TestCase;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.component.event.CompositeStart;
import org.apache.tuscany.core.component.event.CompositeStop;
import org.apache.tuscany.core.component.event.HttpSessionEnd;
import org.apache.tuscany.core.component.event.HttpSessionStart;
import org.apache.tuscany.core.component.event.RequestEnd;
import org.apache.tuscany.core.component.event.RequestStart;
import org.apache.tuscany.core.component.scope.HttpSessionScopeContainer;
import org.apache.tuscany.core.component.scope.ModuleScopeContainer;
import org.apache.tuscany.core.component.scope.RequestScopeContainer;
import org.apache.tuscany.core.component.scope.StatelessScopeContainer;
import org.apache.tuscany.core.integration.mock.MockFactory;
import org.apache.tuscany.core.mock.component.Source;
import org.apache.tuscany.core.mock.component.SourceImpl;
import org.apache.tuscany.core.mock.component.Target;
import org.apache.tuscany.core.mock.component.TargetImpl;
import org.apache.tuscany.core.util.JavaIntrospectionHelper;

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
    public void testModuleToModule() throws Exception {
        ScopeContainer scope = new ModuleScopeContainer(null);
        scope.start();

        Map<String, AtomicComponent> contexts = MockFactory.createWiredComponents("source", SourceImpl.class,
            scope, members, "target", Target.class, TargetImpl.class, scope);
        scope.onEvent(new CompositeStart(this, null));
        AtomicComponent sourceComponent = contexts.get("source");
        AtomicComponent targetComponent = contexts.get("target");
        Source source = (Source) sourceComponent.getServiceInstance();
        Target target = (Target) targetComponent.getServiceInstance();
        assertNull(source.getTarget().getString());
        assertNull(target.getString());
        target.setString("foo");
        assertTrue(Proxy.isProxyClass(source.getTarget().getClass()));
        assertEquals("foo", source.getTarget().getString());
        scope.onEvent(new CompositeStop(this, null));
        scope.stop();
    }

    /**
     * Tests a module-to-session scoped wire is setup properly by the runtime
     */
    public void testModuleToSession() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        ScopeContainer moduleScope = new ModuleScopeContainer(null);
        moduleScope.start();
        ScopeContainer sessionScope = new HttpSessionScopeContainer(ctx, null);
        sessionScope.start();

        Map<String, AtomicComponent> contexts = MockFactory.createWiredComponents("source", SourceImpl.class,
            moduleScope, members, "target", Target.class, TargetImpl.class, sessionScope);
        moduleScope.onEvent(new CompositeStart(this, null));
        Object session1 = new Object();
        ctx.setIdentifier(Scope.SESSION, session1);
        sessionScope.onEvent(new HttpSessionStart(this, session1));
        AtomicComponent sourceComponent = contexts.get("source");
        AtomicComponent targetComponent = contexts.get("target");
        Source source = (Source) sourceComponent.getServiceInstance();
        Target target = (Target) targetComponent.getServiceInstance();
        assertNull(source.getTarget().getString());
        assertNull(target.getString());
        target.setString("foo");
        assertTrue(Proxy.isProxyClass(source.getTarget().getClass()));
        assertEquals("foo", source.getTarget().getString());
        ctx.clearIdentifier(Scope.SESSION);
        sessionScope.onEvent(new HttpSessionEnd(this, session1));

        //second session
        Object session2 = new Object();
        ctx.setIdentifier(Scope.SESSION, session2);
        sessionScope.onEvent(new HttpSessionStart(this, session2));

        Target target2 = (Target) targetComponent.getServiceInstance();
        assertFalse("foo".equals(target2.getString()));

        assertFalse("foo".equals(source.getTarget().getString()));
        source.getTarget().setString("bar");
        assertEquals("bar", target2.getString());
        assertEquals("bar", source.getTarget().getString());
        sessionScope.onEvent(new HttpSessionEnd(this, session2));

        ctx.clearIdentifier(Scope.SESSION);
        moduleScope.onEvent(new CompositeStop(this, null));
        sessionScope.stop();
        moduleScope.stop();
    }

    /**
     * Tests a module-to-request scoped wire
     */
    public void testModuleToRequest() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        ScopeContainer moduleScope = new ModuleScopeContainer(null);
        moduleScope.start();
        final ScopeContainer requestScope = new RequestScopeContainer(ctx, null);
        requestScope.start();

        Map<String, AtomicComponent> contexts = MockFactory.createWiredComponents("source", SourceImpl.class,
            moduleScope, members, "target", Target.class, TargetImpl.class, requestScope);
        moduleScope.onEvent(new CompositeStart(this, null));
        requestScope.onEvent(new RequestStart(this));

        AtomicComponent sourceComponent = contexts.get("source");
        final AtomicComponent targetComponent = contexts.get("target");
        final Source source = (Source) sourceComponent.getServiceInstance();
        Target target = (Target) targetComponent.getServiceInstance();
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
                Target target2 = null;
                try {
                    target2 = (Target) targetComponent.getServiceInstance();
                } catch (TargetException e) {
                    fail(e.getMessage());
                }
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
        moduleScope.onEvent(new CompositeStop(this, null));
        requestScope.stop();
        moduleScope.stop();
    }

    /**
     * Tests a module-to-stateless scoped wire is setup properly by the runtime
     */
    public void testModuleToStateless() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        ScopeContainer moduleScope = new ModuleScopeContainer(null);
        moduleScope.start();
        ScopeContainer statelessScope = new StatelessScopeContainer(ctx, null);
        statelessScope.start();

        Map<String, AtomicComponent> contexts = MockFactory.createWiredComponents("source", SourceImpl.class,
            moduleScope, members, "target", Target.class, TargetImpl.class, statelessScope);
        moduleScope.onEvent(new CompositeStart(this, null));

        AtomicComponent sourceComponent = contexts.get("source");
        AtomicComponent targetComponent = contexts.get("target");
        Source source = (Source) sourceComponent.getServiceInstance();
        Target target = (Target) targetComponent.getServiceInstance();
        assertTrue(Proxy.isProxyClass(source.getTarget().getClass()));
        assertNull(source.getTarget().getString());
        assertNull(target.getString());
        target.setString("foo");
        assertFalse("foo".equals(source.getTarget().getString()));
        Target target2 = (Target) targetComponent.getServiceInstance();
        assertFalse("foo".equals(target2.getString()));
        source.getTarget().setString("bar");
        assertFalse("bar".equals(source.getTarget().getString()));
        moduleScope.onEvent(new CompositeStop(this, null));
        moduleScope.stop();
        statelessScope.stop();
    }


    /**
     * Tests a session-to-session scoped wire
     */
    public void testSessionToSession() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        ScopeContainer sessionScope = new HttpSessionScopeContainer(ctx, null);
        sessionScope.start();

        Map<String, AtomicComponent> contexts = MockFactory.createWiredComponents("source", SourceImpl.class,
            sessionScope, members, "target", Target.class, TargetImpl.class, sessionScope);

        Object session1 = new Object();
        ctx.setIdentifier(Scope.SESSION, session1);
        sessionScope.onEvent(new HttpSessionStart(this, session1));
        AtomicComponent sourceComponent = contexts.get("source");
        AtomicComponent targetComponent = contexts.get("target");
        Source source = (Source) sourceComponent.getServiceInstance();
        Target target = (Target) targetComponent.getServiceInstance();
        source.getTarget().setString("foo");
        source.getTarget().setString("foo");
        assertEquals("foo", target.getString());

        ctx.clearIdentifier(Scope.SESSION);
        sessionScope.onEvent(new HttpSessionEnd(this, session1));

        //second session
        Object session2 = new Object();
        ctx.setIdentifier(Scope.SESSION, session2);
        sessionScope.onEvent(new HttpSessionStart(this, session2));

        Source source2 = (Source) sourceComponent.getServiceInstance();
        assertNotNull(source2);
        Target target2 = (Target) targetComponent.getServiceInstance();

        assertNotNull(target2);
        assertNull(target2.getString());
        assertEquals(null, source2.getTarget().getString());
        source2.getTarget().setString("baz");
        assertEquals("baz", source2.getTarget().getString());
        assertEquals("baz", target2.getString());
        ctx.clearIdentifier(Scope.SESSION);
        sessionScope.onEvent(new HttpSessionEnd(this, session2));
        sessionScope.stop();
    }


    /**
     * Tests a session-to-module scoped wire
     */
    public void testSessionToModule() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        ScopeContainer moduleScope = new ModuleScopeContainer(null);
        moduleScope.start();
        ScopeContainer sessionScope = new HttpSessionScopeContainer(ctx, null);
        sessionScope.start();

        Map<String, AtomicComponent> contexts = MockFactory.createWiredComponents("source", SourceImpl.class,
            sessionScope, members, "target", Target.class, TargetImpl.class, moduleScope);
        moduleScope.onEvent(new CompositeStart(this, null));
        Object session1 = new Object();
        ctx.setIdentifier(Scope.SESSION, session1);
        sessionScope.onEvent(new HttpSessionStart(this, session1));
        AtomicComponent sourceComponent = contexts.get("source");
        AtomicComponent targetComponent = contexts.get("target");
        Source source = (Source) sourceComponent.getServiceInstance();
        Target target = (Target) targetComponent.getServiceInstance();
        assertNull(source.getTarget().getString());
        assertNull(target.getString());
        target.setString("foo");
        assertTrue(Proxy.isProxyClass(source.getTarget().getClass()));
        assertEquals("foo", source.getTarget().getString());
        ctx.clearIdentifier(Scope.SESSION);
        sessionScope.onEvent(new HttpSessionEnd(this, session1));

        //second session
        Object session2 = new Object();
        ctx.setIdentifier(Scope.SESSION, session2);
        sessionScope.onEvent(new HttpSessionStart(this, session2));

        Target target2 = (Target) targetComponent.getServiceInstance();
        Source source2 = (Source) sourceComponent.getServiceInstance();
        assertEquals("foo", target2.getString());
        assertEquals("foo", source2.getTarget().getString());
        source2.getTarget().setString("baz");
        assertEquals("baz", source2.getTarget().getString());
        assertEquals("baz", target2.getString());
        assertEquals("baz", target.getString());
        ctx.clearIdentifier(Scope.SESSION);
        sessionScope.onEvent(new HttpSessionEnd(this, session2));
        moduleScope.stop();
        sessionScope.stop();
    }

    /**
     * Tests a session-to-request scoped wire is setup properly by the runtime
     */
    public void testSessionToRequest() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        final ScopeContainer requestScope = new RequestScopeContainer(ctx, null);
        requestScope.start();
        ScopeContainer sessionScope = new HttpSessionScopeContainer(ctx, null);
        sessionScope.start();

        Map<String, AtomicComponent> contexts = MockFactory.createWiredComponents("source", SourceImpl.class,
            sessionScope, members, "target", Target.class, TargetImpl.class, requestScope);
        Object session1 = new Object();
        ctx.setIdentifier(Scope.SESSION, session1);
        sessionScope.onEvent(new HttpSessionStart(this, session1));
        requestScope.onEvent(new RequestStart(this));
        AtomicComponent sourceComponent = contexts.get("source");
        final AtomicComponent targetComponent = contexts.get("target");
        final Source source = (Source) sourceComponent.getServiceInstance();
        Target target = (Target) targetComponent.getServiceInstance();
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
                Target target2 = null;
                try {
                    target2 = (Target) targetComponent.getServiceInstance();
                } catch (TargetException e) {
                    fail(e.getMessage());
                }
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
        ctx.clearIdentifier(Scope.SESSION);
        sessionScope.onEvent(new HttpSessionEnd(this, session1));
        requestScope.stop();
        sessionScope.stop();
    }


    /**
     * Tests a session-to-stateless scoped wire is setup properly by the runtime
     */
    public void testSessionToStateless() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        ScopeContainer sessionScope = new HttpSessionScopeContainer(ctx, null);
        sessionScope.start();
        ScopeContainer statelessScope = new StatelessScopeContainer(ctx, null);
        statelessScope.start();

        Map<String, AtomicComponent> contexts = MockFactory.createWiredComponents("source", SourceImpl.class,
            sessionScope, members, "target", Target.class, TargetImpl.class, statelessScope);

        Object session1 = new Object();
        ctx.setIdentifier(Scope.SESSION, session1);
        sessionScope.onEvent(new HttpSessionStart(this, session1));

        AtomicComponent sourceComponent = contexts.get("source");
        AtomicComponent targetComponent = contexts.get("target");
        Source source = (Source) sourceComponent.getServiceInstance();
        Target target = (Target) targetComponent.getServiceInstance();
        assertTrue(Proxy.isProxyClass(source.getTarget().getClass()));
        assertNull(source.getTarget().getString());
        assertNull(target.getString());
        target.setString("foo");
        assertFalse("foo".equals(source.getTarget().getString()));
        Target target2 = (Target) targetComponent.getServiceInstance();
        assertFalse("foo".equals(target2.getString()));
        source.getTarget().setString("bar");
        assertFalse("bar".equals(source.getTarget().getString()));

        ctx.clearIdentifier(Scope.SESSION);
        sessionScope.onEvent(new HttpSessionEnd(this, session1));
        sessionScope.stop();
        statelessScope.stop();
    }

    /**
     * Tests a request-to-request scoped wire is setup properly by the runtime
     */
    public void testRequestToRequest() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        final ScopeContainer requestScope = new RequestScopeContainer(ctx, null);
        requestScope.start();

        Map<String, AtomicComponent> contexts = MockFactory.createWiredComponents("source", SourceImpl.class,
            requestScope, members, "target", Target.class, TargetImpl.class, requestScope);
        requestScope.onEvent(new RequestStart(this));

        final AtomicComponent sourceComponent = contexts.get("source");
        final AtomicComponent targetComponent = contexts.get("target");
        Source source = (Source) sourceComponent.getServiceInstance();
        Target target = (Target) targetComponent.getServiceInstance();
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
                Source source2 = null;
                Target target2 = null;
                try {
                    source2 = (Source) sourceComponent.getServiceInstance();
                    target2 = (Target) targetComponent.getServiceInstance();
                } catch (TargetException e) {
                    fail(e.getMessage());
                }
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
    public void testRequestToModule() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        final ScopeContainer requestScope = new RequestScopeContainer(ctx, null);
        final ScopeContainer moduleScope = new ModuleScopeContainer(null);
        requestScope.start();
        moduleScope.start();
        moduleScope.onEvent(new CompositeStart(this, null));

        Map<String, AtomicComponent> contexts = MockFactory.createWiredComponents("source", SourceImpl.class,
            requestScope, members, "target", Target.class, TargetImpl.class, moduleScope);
        requestScope.onEvent(new RequestStart(this));

        final AtomicComponent sourceComponent = contexts.get("source");
        final AtomicComponent targetComponent = contexts.get("target");
        Source source = (Source) sourceComponent.getServiceInstance();
        Target target = (Target) targetComponent.getServiceInstance();
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
                Source source2 = null;
                Target target2 = null;
                try {
                    source2 = (Source) sourceComponent.getServiceInstance();
                    target2 = (Target) targetComponent.getServiceInstance();
                } catch (TargetException e) {
                    fail(e.getMessage());
                }
                assertEquals("foo", target2.getString());
                assertEquals("foo", source2.getTarget().getString());
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
        moduleScope.onEvent(new CompositeStop(this, null));
        moduleScope.stop();
    }

    /**
     * Tests a request-to-session scoped wire is setup properly by the runtime
     */
    public void testRequestToSession() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        final ScopeContainer requestScope = new RequestScopeContainer(ctx, null);
        final ScopeContainer sessionScope = new HttpSessionScopeContainer(ctx, null);
        requestScope.start();
        sessionScope.start();

        Object session1 = new Object();
        ctx.setIdentifier(Scope.SESSION, session1);
        sessionScope.onEvent(new HttpSessionStart(this, session1));
        Map<String, AtomicComponent> contexts = MockFactory.createWiredComponents("source", SourceImpl.class,
            requestScope, members, "target", Target.class, TargetImpl.class, sessionScope);

        final AtomicComponent sourceComponent = contexts.get("source");
        final AtomicComponent targetComponent = contexts.get("target");
        requestScope.onEvent(new RequestStart(this));
        Source source = (Source) sourceComponent.getServiceInstance();
        Target target = (Target) targetComponent.getServiceInstance();
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
                Source source2 = null;
                Target target2 = null;
                try {
                    source2 = (Source) sourceComponent.getServiceInstance();
                    target2 = (Target) targetComponent.getServiceInstance();
                } catch (TargetException e) {
                    fail(e.getMessage());
                }
                assertEquals("foo", target2.getString());
                assertEquals("foo", source2.getTarget().getString());
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
        ctx.clearIdentifier(Scope.SESSION);
        sessionScope.onEvent(new HttpSessionEnd(this, session1));
        sessionScope.stop();
    }


    /**
     * Tests a request-to-stateless scoped wire is setup properly by the runtime
     */
    public void testRequestToStateless() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        ScopeContainer requestScope = new RequestScopeContainer(ctx, null);
        requestScope.start();
        ScopeContainer statelessScope = new StatelessScopeContainer(ctx, null);
        statelessScope.start();

        Map<String, AtomicComponent> contexts = MockFactory.createWiredComponents("source", SourceImpl.class,
            requestScope, members, "target", Target.class, TargetImpl.class, statelessScope);

        AtomicComponent sourceComponent = contexts.get("source");
        AtomicComponent targetComponent = contexts.get("target");
        requestScope.onEvent(new RequestStart(this));
        Source source = (Source) sourceComponent.getServiceInstance();
        Target target = (Target) targetComponent.getServiceInstance();
        assertTrue(Proxy.isProxyClass(source.getTarget().getClass()));
        assertNull(source.getTarget().getString());
        assertNull(target.getString());
        target.setString("foo");
        assertFalse("foo".equals(source.getTarget().getString()));
        Target target2 = (Target) targetComponent.getServiceInstance();
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
    public void testStatelessToStateless() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        ScopeContainer statelessScope = new StatelessScopeContainer(ctx, null);
        statelessScope.start();

        Map<String, AtomicComponent> contexts = MockFactory.createWiredComponents("source", SourceImpl.class,
            statelessScope, members, "target", Target.class, TargetImpl.class, statelessScope);

        AtomicComponent sourceComponent = contexts.get("source");
        AtomicComponent targetComponent = contexts.get("target");
        Source source = (Source) sourceComponent.getServiceInstance();
        Target target = (Target) targetComponent.getServiceInstance();
        assertTrue(Proxy.isProxyClass(source.getTarget().getClass()));
        assertNull(source.getTarget().getString());
        assertNull(target.getString());
        target.setString("foo");
        assertFalse("foo".equals(source.getTarget().getString()));
        Target target2 = (Target) targetComponent.getServiceInstance();
        assertFalse("foo".equals(target2.getString()));
        source.getTarget().setString("bar");
        assertFalse("bar".equals(source.getTarget().getString()));
        statelessScope.stop();
    }

    /**
     * Tests a stateless-to-request scoped wire is setup properly by the runtime
     */
    public void testStatelessToRequest() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        final ScopeContainer requestScope = new RequestScopeContainer(ctx, null);
        requestScope.start();
        ScopeContainer statelessScope = new StatelessScopeContainer(ctx, null);
        statelessScope.start();

        Map<String, AtomicComponent> contexts = MockFactory.createWiredComponents("source", SourceImpl.class,
            statelessScope, members, "target", Target.class, TargetImpl.class, requestScope);
        requestScope.onEvent(new RequestStart(this));
        AtomicComponent sourceComponent = contexts.get("source");
        final AtomicComponent targetComponent = contexts.get("target");
        final Source source = (Source) sourceComponent.getServiceInstance();
        Target target = (Target) targetComponent.getServiceInstance();
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
                Target target2 = null;
                try {
                    target2 = (Target) targetComponent.getServiceInstance();
                } catch (TargetException e) {
                    fail(e.getMessage());
                }
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
    public void testStatelessToSession() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        ScopeContainer statelessScope = new StatelessScopeContainer(ctx, null);
        statelessScope.start();
        ScopeContainer sessionScope = new HttpSessionScopeContainer(ctx, null);
        sessionScope.start();

        Map<String, AtomicComponent> contexts = MockFactory.createWiredComponents("source", SourceImpl.class,
            statelessScope, members, "target", Target.class, TargetImpl.class, sessionScope);
        Object session1 = new Object();
        ctx.setIdentifier(Scope.SESSION, session1);
        sessionScope.onEvent(new HttpSessionStart(this, session1));
        AtomicComponent sourceComponent = contexts.get("source");
        AtomicComponent targetComponent = contexts.get("target");
        Source source = (Source) sourceComponent.getServiceInstance();
        Target target = (Target) targetComponent.getServiceInstance();
        assertNull(source.getTarget().getString());
        assertNull(target.getString());
        target.setString("foo");
        assertTrue(Proxy.isProxyClass(source.getTarget().getClass()));
        assertEquals("foo", source.getTarget().getString());
        ctx.clearIdentifier(Scope.SESSION);
        sessionScope.onEvent(new HttpSessionEnd(this, session1));

        //second session
        Object session2 = new Object();
        ctx.setIdentifier(Scope.SESSION, session2);
        sessionScope.onEvent(new HttpSessionStart(this, session2));

        Target target2 = (Target) targetComponent.getServiceInstance();
        assertFalse("foo".equals(target2.getString()));

        assertFalse("foo".equals(source.getTarget().getString()));
        source.getTarget().setString("bar");
        assertEquals("bar", target2.getString());
        assertEquals("bar", source.getTarget().getString());
        sessionScope.onEvent(new HttpSessionEnd(this, session2));

        ctx.clearIdentifier(Scope.SESSION);
        sessionScope.stop();
        statelessScope.stop();
    }


    /**
     * Tests a stateless-to-module scoped wire is setup properly by the runtime
     */
    public void testStatelessToModule() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        ScopeContainer statelessScope = new StatelessScopeContainer(ctx, null);
        statelessScope.start();
        ScopeContainer moduleScope = new ModuleScopeContainer(null);
        moduleScope.start();

        Map<String, AtomicComponent> contexts = MockFactory.createWiredComponents("source", SourceImpl.class,
            statelessScope, members, "target", Target.class, TargetImpl.class, moduleScope);
        moduleScope.onEvent(new CompositeStart(this, null));
        AtomicComponent sourceComponent = contexts.get("source");
        AtomicComponent targetComponent = contexts.get("target");
        Source source = (Source) sourceComponent.getServiceInstance();
        Target target = (Target) targetComponent.getServiceInstance();
        assertNull(source.getTarget().getString());
        assertNull(target.getString());
        target.setString("foo");
        assertTrue(Proxy.isProxyClass(source.getTarget().getClass()));
        assertEquals("foo", source.getTarget().getString());

        //second session
        Object session2 = new Object();
        ctx.setIdentifier(Scope.SESSION, session2);
        moduleScope.onEvent(new HttpSessionStart(this, session2));

        Target target2 = (Target) targetComponent.getServiceInstance();
        assertEquals("foo", target2.getString());

        assertEquals("foo", source.getTarget().getString());
        source.getTarget().setString("bar");
        assertEquals("bar", target2.getString());
        assertEquals("bar", source.getTarget().getString());

        moduleScope.onEvent(new CompositeStop(this, null));
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

