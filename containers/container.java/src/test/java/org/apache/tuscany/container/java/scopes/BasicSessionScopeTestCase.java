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
package org.apache.tuscany.container.java.scopes;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.container.java.builder.JavaContextFactoryBuilder;
import org.apache.tuscany.container.java.mock.MockFactory;
import org.apache.tuscany.container.java.mock.components.SessionScopeComponent;
import org.apache.tuscany.container.java.mock.components.SessionScopeComponentImpl;
import org.apache.tuscany.container.java.mock.components.SessionScopeInitDestroyComponent;
import org.apache.tuscany.core.builder.BuilderException;
import org.apache.tuscany.core.builder.ContextFactory;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.context.Context;
import org.apache.tuscany.core.context.event.RequestEnd;
import org.apache.tuscany.core.context.event.HttpSessionEnd;
import org.apache.tuscany.core.context.event.HttpSessionEvent;
import org.apache.tuscany.core.context.impl.EventContextImpl;
import org.apache.tuscany.core.context.scope.SessionScopeContext;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.assembly.AtomicComponent;

/**
 * Unit tests for the Http session scope container
 * 
 * @version $Rev$ $Date$
 */
public class BasicSessionScopeTestCase extends TestCase {

    /**
     * Tests instance identity is properly maintained
     */
    public void testInstanceManagement() throws Exception {
        EventContext ctx = new EventContextImpl();
        SessionScopeContext scope = new SessionScopeContext(ctx);
        scope.registerFactories(createConfigurations());
        scope.start();
        Object session = new Object();
        Object session2 = new Object();
        // first request
        ctx.setIdentifier(HttpSessionEvent.HTTP_IDENTIFIER,session);
        SessionScopeComponent comp1 = (SessionScopeComponent) scope.getContext("TestService1").getInstance(null);
        Assert.assertNotNull(comp1);
        ctx.clearIdentifier(HttpSessionEvent.HTTP_IDENTIFIER);

        // second request
        ctx.setIdentifier(HttpSessionEvent.HTTP_IDENTIFIER,session);
        SessionScopeComponent comp2 = (SessionScopeComponent) scope.getContext("TestService1").getInstance(null);
        Assert.assertNotNull(comp2);
        Assert.assertSame(comp1, comp2);
        ctx.clearIdentifier(HttpSessionEvent.HTTP_IDENTIFIER);

        // third request, different session
        ctx.setIdentifier(HttpSessionEvent.HTTP_IDENTIFIER,session2);
        SessionScopeComponent comp3 = (SessionScopeComponent) scope.getContext("TestService1").getInstance(null);
        Assert.assertNotNull(comp3);
        Assert.assertNotSame(comp1, comp3); // should be different instances
        ctx.clearIdentifier(HttpSessionEvent.HTTP_IDENTIFIER);

        scope.onEvent(new HttpSessionEnd(this,session));
        scope.onEvent(new HttpSessionEnd(this,session2));
        scope.stop();
    }

    /**
     * Tests setting no components in the scope
     */
    public void testSetNullComponents() throws Exception {
        EventContext ctx = new EventContextImpl();
        SessionScopeContext scope = new SessionScopeContext(ctx);
        scope.registerFactories(new ArrayList<ContextFactory<Context>>());
        scope.start();
        scope.stop();
    }

    public void testGetContextByKey() throws Exception {
        EventContext ctx = new EventContextImpl();
        SessionScopeContext scope = new SessionScopeContext(ctx);
        scope.registerFactories(createConfigurations());
        scope.start();
        Object session = new Object();
        Object session2 = new Object();

        // first request
        ctx.setIdentifier(HttpSessionEvent.HTTP_IDENTIFIER,session);
        SessionScopeComponent comp1 = (SessionScopeComponent) scope.getContext("TestService1").getInstance(null);
        Assert.assertNotNull(comp1);
        ctx.clearIdentifier(HttpSessionEvent.HTTP_IDENTIFIER);

        // second request, different session
        ctx.setIdentifier(HttpSessionEvent.HTTP_IDENTIFIER,session2);
        SessionScopeComponent comp2 = (SessionScopeComponent) scope.getContextByKey("TestService1", session)
                .getInstance(null);
        SessionScopeComponent comp3 = (SessionScopeComponent) scope.getContextByKey("TestService1", session)
                .getInstance(null);
        Assert.assertNotNull(comp2);
        Object id = new Object();
        scope.onEvent(new RequestEnd(this,id));
        Assert.assertSame(comp1, comp2); // should be same instances
        Assert.assertSame(comp2, comp3); // should not be same instances
        ctx.clearIdentifier(HttpSessionEvent.HTTP_IDENTIFIER);

        // shutdown sessions
        scope.onEvent(new HttpSessionEnd(this,session));
        scope.onEvent(new HttpSessionEnd(this,session2));

        scope.stop();
    }

    public void testRegisterContextBeforeSession() throws Exception {
        EventContext ctx = new EventContextImpl();
        SessionScopeContext scope = new SessionScopeContext(ctx);
        scope.registerFactories(createConfigurations());
        scope.start();
        Object session = new Object();
        scope.registerFactory(createConfiguration("NewTestService"));

        // first request
        ctx.setIdentifier(HttpSessionEvent.HTTP_IDENTIFIER,session);

        SessionScopeInitDestroyComponent comp2 = (SessionScopeInitDestroyComponent) scope.getContext("NewTestService")
                .getInstance(null);
        Assert.assertNotNull(comp2);
        Assert.assertTrue(comp2.isInitialized());
        ctx.clearIdentifier(HttpSessionEvent.HTTP_IDENTIFIER);

        // second request different session
        Object session2 = new Object();
        ctx.setIdentifier(HttpSessionEvent.HTTP_IDENTIFIER,session2);
        SessionScopeInitDestroyComponent comp3 = (SessionScopeInitDestroyComponent) scope.getContext("NewTestService")
                .getInstance(null);
        Assert.assertNotNull(comp3);
        Assert.assertNotSame(comp2, comp3);
        Assert.assertTrue(comp3.isInitialized());
        ctx.clearIdentifier(HttpSessionEvent.HTTP_IDENTIFIER);

        scope.onEvent(new HttpSessionEnd(this,session));
        Assert.assertTrue(comp2.isDestroyed());

        scope.onEvent(new HttpSessionEnd(this,session2));
        Assert.assertTrue(comp3.isDestroyed());
        scope.stop();
    }

    /**
     * Tests runtime context registration
     */
    public void testRegisterContextAfterSession() throws Exception {
        EventContext ctx = new EventContextImpl();
        SessionScopeContext scope = new SessionScopeContext(ctx);
        scope.registerFactories(createConfigurations());
        scope.start();
        Object session = new Object();

        // first request
        ctx.setIdentifier(HttpSessionEvent.HTTP_IDENTIFIER,session);
        SessionScopeComponent comp1 = (SessionScopeComponent) scope.getContext("TestService1").getInstance(null);
        Assert.assertNotNull(comp1);
        ctx.clearIdentifier(HttpSessionEvent.HTTP_IDENTIFIER);

        scope.registerFactory(createConfiguration("NewTestService"));

        // second request
        ctx.setIdentifier(HttpSessionEvent.HTTP_IDENTIFIER,session);
        SessionScopeInitDestroyComponent comp2 = (SessionScopeInitDestroyComponent) scope.getContext("NewTestService")
                .getInstance(null);
        Assert.assertNotNull(comp2);
        Assert.assertTrue(comp2.isInitialized());
        ctx.clearIdentifier(HttpSessionEvent.HTTP_IDENTIFIER);

        // third request different session
        Object session2 = new Object();
        ctx.setIdentifier(HttpSessionEvent.HTTP_IDENTIFIER,session2);
        SessionScopeInitDestroyComponent comp3 = (SessionScopeInitDestroyComponent) scope.getContext("NewTestService")
                .getInstance(null);
        Assert.assertNotNull(comp3);
        Assert.assertNotSame(comp2, comp3);
        Assert.assertTrue(comp3.isInitialized());
        ctx.clearIdentifier(HttpSessionEvent.HTTP_IDENTIFIER);

        scope.onEvent(new HttpSessionEnd(this,session));
        Assert.assertTrue(comp2.isDestroyed());

        scope.onEvent(new HttpSessionEnd(this,session2));
        Assert.assertTrue(comp3.isDestroyed());
        scope.stop();
    }

    JavaContextFactoryBuilder builder = new JavaContextFactoryBuilder();

    private List<ContextFactory<Context>> createConfigurations() throws BuilderException {
        AtomicComponent component = MockFactory.createComponent("TestService1", SessionScopeComponentImpl.class, Scope.SESSION);
        builder.build(component);
        List<ContextFactory<Context>> configs = new ArrayList<ContextFactory<Context>>();
        configs.add((ContextFactory<Context>) component.getContextFactory());
        return configs;
    }

    private ContextFactory<Context> createConfiguration(String name) throws BuilderException {
        AtomicComponent component = MockFactory.createComponent(name, SessionScopeInitDestroyComponent.class, Scope.SESSION);
        builder.build(component);
        return (ContextFactory<Context>) component.getContextFactory();
    }
}
