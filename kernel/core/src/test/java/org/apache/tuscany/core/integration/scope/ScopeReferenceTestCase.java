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
import java.net.URI;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.model.Scope;

import junit.framework.TestCase;

import org.apache.tuscany.core.component.scope.CompositeScopeContainer;
import org.apache.tuscany.core.component.scope.StatelessScopeContainer;
import org.apache.tuscany.core.integration.mock.MockFactory;
import org.apache.tuscany.core.mock.component.Source;
import org.apache.tuscany.core.mock.component.SourceImpl;
import org.apache.tuscany.core.mock.component.Target;
import org.apache.tuscany.core.mock.component.TargetImpl;
import org.apache.tuscany.core.util.JavaIntrospectionHelper;
import org.apache.tuscany.core.implementation.PojoWorkContextTunnel;

import org.easymock.EasyMock;

/**
 * Tests scoping is properly handled for service references
 *
 * @version $Rev$ $Date$
 */
public class ScopeReferenceTestCase extends TestCase {
    private Map<String, Member> members;
    private URI groupId;
    private ScopeContainer statelessScope;
    private ScopeContainer compositeScope;
    private WorkContext workContext;

    /**
     * Tests a composite-to-composite scoped wire
     */
    public void testCompositeToComposite() throws Exception {
        Map<String, AtomicComponent> contexts = MockFactory.createWiredComponents("source",
            SourceImpl.class,
            compositeScope, members,
            "target",
            Target.class,
            TargetImpl.class,
            compositeScope);
        AtomicComponent sourceComponent = contexts.get("source");
        AtomicComponent targetComponent = contexts.get("target");
        Source source = (Source) sourceComponent.getTargetInstance();
        Target target = (Target) targetComponent.getTargetInstance();
        PojoWorkContextTunnel.setThreadWorkContext(workContext);
        try {
            assertNull(source.getTarget().getString());
            assertNull(target.getString());
            target.setString("foo");
            assertTrue(Proxy.isProxyClass(source.getTarget().getClass()));
            assertEquals("foo", source.getTarget().getString());
        } finally {
            PojoWorkContextTunnel.setThreadWorkContext(null);
        }
    }

    /**
     * Tests a composite-to-session scoped wire is setup properly by the runtime
     */
/*
    public void testCompositeToSession() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        ScopeContainer compositeScope = new CompositeScopeContainer(null);
        compositeScope.start();
        compositeScope.createGroup(groupId);
        ScopeContainer sessionScope = new HttpSessionScopeContainer(ctx, null);
        sessionScope.start();
        sessionScope.createGroup(groupId);

        Map<String, AtomicComponent> contexts = MockFactory.createWiredComponents("source", SourceImpl.class,
            compositeScope, members, "target", Target.class, TargetImpl.class, sessionScope);
        compositeScope.onEvent(new ComponentStart(this, null));
        Object session1 = new Object();
        ctx.setIdentifier(Scope.SESSION, session1);
        sessionScope.onEvent(new HttpSessionStart(this, session1));
        AtomicComponent sourceComponent = contexts.get("source");
        AtomicComponent targetComponent = contexts.get("target");
        Source source = (Source) sourceComponent.getTargetInstance();
        Target target = (Target) targetComponent.getTargetInstance();
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

        Target target2 = (Target) targetComponent.getTargetInstance();
        assertFalse("foo".equals(target2.getString()));

        assertFalse("foo".equals(source.getTarget().getString()));
        source.getTarget().setString("bar");
        assertEquals("bar", target2.getString());
        assertEquals("bar", source.getTarget().getString());
        sessionScope.onEvent(new HttpSessionEnd(this, session2));

        ctx.clearIdentifier(Scope.SESSION);
        compositeScope.onEvent(new ComponentStop(this, null));
        sessionScope.stop();
        compositeScope.stop();
    }
*/

    /**
     * Tests a composite-to-request scoped wire
     */
/*
    public void testCompositeToRequest() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        ScopeContainer compositeScope = new CompositeScopeContainer(null);
        compositeScope.start();
        final ScopeContainer requestScope = new RequestScopeContainer(ctx, null);
        requestScope.start();

        Map<String, AtomicComponent> contexts = MockFactory.createWiredComponents("source", SourceImpl.class,
            compositeScope, members, "target", Target.class, TargetImpl.class, requestScope);
        compositeScope.onEvent(new ComponentStart(this, null));
        requestScope.onEvent(new RequestStart(this));

        AtomicComponent sourceComponent = contexts.get("source");
        final AtomicComponent targetComponent = contexts.get("target");
        final Source source = (Source) sourceComponent.getTargetInstance();
        Target target = (Target) targetComponent.getTargetInstance();
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
                    target2 = (Target) targetComponent.getTargetInstance();
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
        compositeScope.onEvent(new ComponentStop(this, null));
        requestScope.stop();
        compositeScope.stop();
    }
*/

    /**
     * Tests a composite-to-stateless scoped wire is setup properly by the runtime
     */
    public void testCompositeToStateless() throws Exception {
        Map<String, AtomicComponent> contexts = MockFactory.createWiredComponents("source", SourceImpl.class,
            compositeScope, members, "target", Target.class, TargetImpl.class, statelessScope);

        AtomicComponent sourceComponent = contexts.get("source");
        AtomicComponent targetComponent = contexts.get("target");
        Source source = (Source) sourceComponent.getTargetInstance();
        Target target = (Target) targetComponent.getTargetInstance();
        PojoWorkContextTunnel.setThreadWorkContext(workContext);
        try {
            assertTrue(Proxy.isProxyClass(source.getTarget().getClass()));
            assertNull(source.getTarget().getString());
            assertNull(target.getString());
            target.setString("foo");
            assertFalse("foo".equals(source.getTarget().getString()));
            Target target2 = (Target) targetComponent.getTargetInstance();
            assertFalse("foo".equals(target2.getString()));
            source.getTarget().setString("bar");
            assertFalse("bar".equals(source.getTarget().getString()));
        } finally {
            PojoWorkContextTunnel.setThreadWorkContext(null);
        }
    }


    /**
     * Tests a session-to-session scoped wire
     */
/*
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
        Source source = (Source) sourceComponent.getTargetInstance();
        Target target = (Target) targetComponent.getTargetInstance();
        source.getTarget().setString("foo");
        source.getTarget().setString("foo");
        assertEquals("foo", target.getString());

        ctx.clearIdentifier(Scope.SESSION);
        sessionScope.onEvent(new HttpSessionEnd(this, session1));

        //second session
        Object session2 = new Object();
        ctx.setIdentifier(Scope.SESSION, session2);
        sessionScope.onEvent(new HttpSessionStart(this, session2));

        Source source2 = (Source) sourceComponent.getTargetInstance();
        assertNotNull(source2);
        Target target2 = (Target) targetComponent.getTargetInstance();

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
*/


    /**
     * Tests a session-to-composite scoped wire
     */
/*
    public void testSessionToComposite() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        ScopeContainer compositeScope = new CompositeScopeContainer(null);
        compositeScope.start();
        ScopeContainer sessionScope = new HttpSessionScopeContainer(ctx, null);
        sessionScope.start();

        Map<String, AtomicComponent> contexts = MockFactory.createWiredComponents("source", SourceImpl.class,
            sessionScope, members, "target", Target.class, TargetImpl.class, compositeScope);
        compositeScope.onEvent(new ComponentStart(this, null));
        Object session1 = new Object();
        ctx.setIdentifier(Scope.SESSION, session1);
        sessionScope.onEvent(new HttpSessionStart(this, session1));
        AtomicComponent sourceComponent = contexts.get("source");
        AtomicComponent targetComponent = contexts.get("target");
        Source source = (Source) sourceComponent.getTargetInstance();
        Target target = (Target) targetComponent.getTargetInstance();
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

        Target target2 = (Target) targetComponent.getTargetInstance();
        Source source2 = (Source) sourceComponent.getTargetInstance();
        assertEquals("foo", target2.getString());
        assertEquals("foo", source2.getTarget().getString());
        source2.getTarget().setString("baz");
        assertEquals("baz", source2.getTarget().getString());
        assertEquals("baz", target2.getString());
        assertEquals("baz", target.getString());
        ctx.clearIdentifier(Scope.SESSION);
        sessionScope.onEvent(new HttpSessionEnd(this, session2));
        compositeScope.stop();
        sessionScope.stop();
    }
*/

    /**
     * Tests a session-to-request scoped wire is setup properly by the runtime
     */
/*
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
        final Source source = (Source) sourceComponent.getTargetInstance();
        Target target = (Target) targetComponent.getTargetInstance();
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
                    target2 = (Target) targetComponent.getTargetInstance();
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
*/


    /**
     * Tests a session-to-stateless scoped wire is setup properly by the runtime
     */
/*
    public void testSessionToStateless() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        ScopeContainer sessionScope = new HttpSessionScopeContainer(ctx, null);
        sessionScope.start();
        ScopeContainer statelessScope = new StatelessScopeContainer(null);
        statelessScope.start();

        Map<String, AtomicComponent> contexts = MockFactory.createWiredComponents("source", SourceImpl.class,
            sessionScope, members, "target", Target.class, TargetImpl.class, statelessScope);

        Object session1 = new Object();
        ctx.setIdentifier(Scope.SESSION, session1);
        sessionScope.onEvent(new HttpSessionStart(this, session1));

        AtomicComponent sourceComponent = contexts.get("source");
        AtomicComponent targetComponent = contexts.get("target");
        Source source = (Source) sourceComponent.getTargetInstance();
        Target target = (Target) targetComponent.getTargetInstance();
        assertTrue(Proxy.isProxyClass(source.getTarget().getClass()));
        assertNull(source.getTarget().getString());
        assertNull(target.getString());
        target.setString("foo");
        assertFalse("foo".equals(source.getTarget().getString()));
        Target target2 = (Target) targetComponent.getTargetInstance();
        assertFalse("foo".equals(target2.getString()));
        source.getTarget().setString("bar");
        assertFalse("bar".equals(source.getTarget().getString()));

        ctx.clearIdentifier(Scope.SESSION);
        sessionScope.onEvent(new HttpSessionEnd(this, session1));
        sessionScope.stop();
        statelessScope.stop();
    }
*/

    /**
     * Tests a request-to-request scoped wire is setup properly by the runtime
     */
/*
    public void testRequestToRequest() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        final ScopeContainer requestScope = new RequestScopeContainer(ctx, null);
        requestScope.start();

        Map<String, AtomicComponent> contexts = MockFactory.createWiredComponents("source", SourceImpl.class,
            requestScope, members, "target", Target.class, TargetImpl.class, requestScope);
        requestScope.onEvent(new RequestStart(this));

        final AtomicComponent sourceComponent = contexts.get("source");
        final AtomicComponent targetComponent = contexts.get("target");
        Source source = (Source) sourceComponent.getTargetInstance();
        Target target = (Target) targetComponent.getTargetInstance();
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
                    source2 = (Source) sourceComponent.getTargetInstance();
                    target2 = (Target) targetComponent.getTargetInstance();
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
*/

    /**
     * Tests a request-to-composite scoped wire
     */
/*
    public void testRequestToComposite() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        final ScopeContainer requestScope = new RequestScopeContainer(ctx, null);
        final ScopeContainer compositeScope = new CompositeScopeContainer(null);
        requestScope.start();
        compositeScope.start();
        compositeScope.onEvent(new ComponentStart(this, null));

        Map<String, AtomicComponent> contexts = MockFactory.createWiredComponents("source", SourceImpl.class,
            requestScope, members, "target", Target.class, TargetImpl.class, compositeScope);
        requestScope.onEvent(new RequestStart(this));

        final AtomicComponent sourceComponent = contexts.get("source");
        final AtomicComponent targetComponent = contexts.get("target");
        Source source = (Source) sourceComponent.getTargetInstance();
        Target target = (Target) targetComponent.getTargetInstance();
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
                    source2 = (Source) sourceComponent.getTargetInstance();
                    target2 = (Target) targetComponent.getTargetInstance();
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
        compositeScope.onEvent(new ComponentStop(this, null));
        compositeScope.stop();
    }
*/

    /**
     * Tests a request-to-session scoped wire is setup properly by the runtime
     */
/*
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
        Source source = (Source) sourceComponent.getTargetInstance();
        Target target = (Target) targetComponent.getTargetInstance();
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
                    source2 = (Source) sourceComponent.getTargetInstance();
                    target2 = (Target) targetComponent.getTargetInstance();
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
*/


    /**
     * Tests a request-to-stateless scoped wire is setup properly by the runtime
     */
/*
    public void testRequestToStateless() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        ScopeContainer requestScope = new RequestScopeContainer(ctx, null);
        requestScope.start();
        ScopeContainer statelessScope = new StatelessScopeContainer(null);
        statelessScope.start();

        Map<String, AtomicComponent> contexts = MockFactory.createWiredComponents("source", SourceImpl.class,
            requestScope, members, "target", Target.class, TargetImpl.class, statelessScope);

        AtomicComponent sourceComponent = contexts.get("source");
        AtomicComponent targetComponent = contexts.get("target");
        requestScope.onEvent(new RequestStart(this));
        Source source = (Source) sourceComponent.getTargetInstance();
        Target target = (Target) targetComponent.getTargetInstance();
        assertTrue(Proxy.isProxyClass(source.getTarget().getClass()));
        assertNull(source.getTarget().getString());
        assertNull(target.getString());
        target.setString("foo");
        assertFalse("foo".equals(source.getTarget().getString()));
        Target target2 = (Target) targetComponent.getTargetInstance();
        assertFalse("foo".equals(target2.getString()));
        source.getTarget().setString("bar");
        assertFalse("bar".equals(source.getTarget().getString()));
        requestScope.onEvent(new RequestEnd(this));
        requestScope.stop();
        statelessScope.stop();
    }
*/


    /**
     * Tests a stateless-to-stateless scoped wire is setup properly by the runtime
     */
    public void testStatelessToStateless() throws Exception {
        Map<String, AtomicComponent> contexts = MockFactory.createWiredComponents("source", SourceImpl.class,
            statelessScope, members, "target", Target.class, TargetImpl.class, statelessScope);

        AtomicComponent sourceComponent = contexts.get("source");
        AtomicComponent targetComponent = contexts.get("target");
        Source source = (Source) sourceComponent.getTargetInstance();
        Target target = (Target) targetComponent.getTargetInstance();
        PojoWorkContextTunnel.setThreadWorkContext(workContext);
        try {
            assertTrue(Proxy.isProxyClass(source.getTarget().getClass()));
            assertNull(source.getTarget().getString());
            assertNull(target.getString());
            target.setString("foo");
            assertFalse("foo".equals(source.getTarget().getString()));
            Target target2 = (Target) targetComponent.getTargetInstance();
            assertFalse("foo".equals(target2.getString()));
            source.getTarget().setString("bar");
            assertFalse("bar".equals(source.getTarget().getString()));
        } finally {
            PojoWorkContextTunnel.setThreadWorkContext(null);
        }
    }

    /**
     * Tests a stateless-to-request scoped wire is setup properly by the runtime
     */
/*
    public void testStatelessToRequest() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        final ScopeContainer requestScope = new RequestScopeContainer(ctx, null);
        requestScope.start();
        ScopeContainer statelessScope = new StatelessScopeContainer(null);
        statelessScope.start();

        Map<String, AtomicComponent> contexts = MockFactory.createWiredComponents("source", SourceImpl.class,
            statelessScope, members, "target", Target.class, TargetImpl.class, requestScope);
        requestScope.onEvent(new RequestStart(this));
        AtomicComponent sourceComponent = contexts.get("source");
        final AtomicComponent targetComponent = contexts.get("target");
        final Source source = (Source) sourceComponent.getTargetInstance();
        Target target = (Target) targetComponent.getTargetInstance();
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
                    target2 = (Target) targetComponent.getTargetInstance();
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
*/

    /**
     * Tests a stateless-to-session scoped wire is setup properly by the runtime
     */
/*
    public void testStatelessToSession() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        ScopeContainer statelessScope = new StatelessScopeContainer(null);
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
        Source source = (Source) sourceComponent.getTargetInstance();
        Target target = (Target) targetComponent.getTargetInstance();
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

        Target target2 = (Target) targetComponent.getTargetInstance();
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
*/


    /**
     * Tests a stateless-to-composite scoped wire is setup properly by the runtime
     */
    public void testStatelessToComposite() throws Exception {
        Map<String, AtomicComponent> contexts = MockFactory.createWiredComponents("source", SourceImpl.class,
            statelessScope, members, "target", Target.class, TargetImpl.class, compositeScope);
        AtomicComponent sourceComponent = contexts.get("source");
        AtomicComponent targetComponent = contexts.get("target");
        Source source = (Source) sourceComponent.getTargetInstance();
        Target target = (Target) targetComponent.getTargetInstance();
        PojoWorkContextTunnel.setThreadWorkContext(workContext);
        try {
            assertNull(source.getTarget().getString());
            assertNull(target.getString());
            target.setString("foo");
            assertTrue(Proxy.isProxyClass(source.getTarget().getClass()));
            assertEquals("foo", source.getTarget().getString());
        } finally {
            PojoWorkContextTunnel.setThreadWorkContext(null);
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        groupId = URI.create("composite");
        members = new HashMap<String, Member>();
        Method[] methods = SourceImpl.class.getMethods();
        for (Method method : methods) {
            if (method.getName().startsWith("set")) {
                members.put(JavaIntrospectionHelper.toPropertyName(method.getName()), method);
            }
        }

        statelessScope = new StatelessScopeContainer(null);
        statelessScope.start();
        compositeScope = new CompositeScopeContainer(null);
        compositeScope.start();
        compositeScope.startContext(groupId, groupId);

        workContext = EasyMock.createMock(WorkContext.class);
        EasyMock.expect(workContext.getIdentifier(Scope.COMPOSITE)).andStubReturn(URI.create("composite"));
        EasyMock.expect(workContext.getIdentifier(Scope.STATELESS)).andStubReturn(null);
        EasyMock.expect(workContext.getCorrelationId()).andStubReturn(null);
        EasyMock.expect(workContext.getCallbackUris()).andStubReturn(null);
        EasyMock.replay(workContext);
    }


}

