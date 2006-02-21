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

import org.apache.tuscany.container.java.builder.JavaComponentContextBuilder2;
import org.apache.tuscany.container.java.mock.MockAssemblyFactory;
import org.apache.tuscany.container.java.mock.components.SessionScopeComponent;
import org.apache.tuscany.container.java.mock.components.SessionScopeComponentImpl;
import org.apache.tuscany.container.java.mock.components.SessionScopeInitDestroyComponent;
import org.apache.tuscany.core.builder.BuilderException;
import org.apache.tuscany.core.builder.RuntimeConfiguration;
import org.apache.tuscany.core.context.InstanceContext;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.context.impl.EventContextImpl;
import org.apache.tuscany.core.context.scope.HttpSessionScopeContext;
import org.apache.tuscany.model.assembly.ScopeEnum;
import org.apache.tuscany.model.assembly.SimpleComponent;

/**
 * Unit tests for the Http session scope container
 * 
 * @version $Rev$ $Date$
 */
public class BasicHttpSessionScopeTestCase extends TestCase {

    /**
     * Tests instance identity is properly maintained
     */
    public void testInstanceManagement() throws Exception {
        EventContext ctx = new EventContextImpl();
        HttpSessionScopeContext scope = new HttpSessionScopeContext(ctx);
        scope.registerConfigurations(createConfigurations());
        scope.start();
        Object session = new Object();
        Object session2 = new Object();
        // first request
        ctx.setIdentifier(EventContext.HTTP_SESSION, session);
        SessionScopeComponent comp1 = (SessionScopeComponent) scope.getContext("TestService1").getInstance(null);
        Assert.assertNotNull(comp1);
        ctx.clearIdentifier(EventContext.HTTP_SESSION);

        // second request
        ctx.setIdentifier(EventContext.HTTP_SESSION, session);
        SessionScopeComponent comp2 = (SessionScopeComponent) scope.getContext("TestService1").getInstance(null);
        Assert.assertNotNull(comp2);
        Assert.assertSame(comp1, comp2);
        ctx.clearIdentifier(EventContext.HTTP_SESSION);

        // third request, different session
        ctx.setIdentifier(EventContext.HTTP_SESSION, session2);
        SessionScopeComponent comp3 = (SessionScopeComponent) scope.getContext("TestService1").getInstance(null);
        Assert.assertNotNull(comp3);
        Assert.assertNotSame(comp1, comp3); // should be different instances
        ctx.clearIdentifier(EventContext.HTTP_SESSION);

        scope.onEvent(EventContext.SESSION_END, session);
        scope.onEvent(EventContext.SESSION_END, session2);

        scope.stop();
    }

    /**
     * Tests setting no components in the scope
     */
    public void testSetNullComponents() throws Exception {
        EventContext ctx = new EventContextImpl();
        HttpSessionScopeContext scope = new HttpSessionScopeContext(ctx);
        scope.registerConfigurations(new ArrayList<RuntimeConfiguration<InstanceContext>>());
        scope.start();
        scope.stop();
    }

    public void testGetContextByKey() throws Exception {
        EventContext ctx = new EventContextImpl();
        HttpSessionScopeContext scope = new HttpSessionScopeContext(ctx);
        scope.registerConfigurations(createConfigurations());
        scope.start();
        Object session = new Object();
        Object session2 = new Object();

        // first request
        ctx.setIdentifier(EventContext.HTTP_SESSION, session);
        SessionScopeComponent comp1 = (SessionScopeComponent) scope.getContext("TestService1").getInstance(null);
        Assert.assertNotNull(comp1);
        ctx.clearIdentifier(EventContext.HTTP_SESSION);

        // second request, different session
        ctx.setIdentifier(EventContext.HTTP_SESSION, session2);
        SessionScopeComponent comp2 = (SessionScopeComponent) scope.getContextByKey("TestService1", session)
                .getInstance(null);
        SessionScopeComponent comp3 = (SessionScopeComponent) scope.getContextByKey("TestService1", session)
                .getInstance(null);
        Assert.assertNotNull(comp2);
        scope.onEvent(EventContext.REQUEST_END, session2);
        Assert.assertSame(comp1, comp2); // should be same instances
        Assert.assertSame(comp2, comp3); // should not be same instances
        ctx.clearIdentifier(EventContext.HTTP_SESSION);

        // shutdown sessions
        scope.onEvent(EventContext.SESSION_END, session);
        scope.onEvent(EventContext.SESSION_END, session2);

        scope.stop();
    }

    public void testRegisterContextBeforeSession() throws Exception {
        EventContext ctx = new EventContextImpl();
        HttpSessionScopeContext scope = new HttpSessionScopeContext(ctx);
        scope.registerConfigurations(createConfigurations());
        scope.start();
        Object session = new Object();
        scope.registerConfiguration(createConfiguration("NewTestService"));

        // first request
        ctx.setIdentifier(EventContext.HTTP_SESSION, session);

        SessionScopeInitDestroyComponent comp2 = (SessionScopeInitDestroyComponent) scope.getContext("NewTestService")
                .getInstance(null);
        Assert.assertNotNull(comp2);
        Assert.assertTrue(comp2.isInitialized());
        ctx.clearIdentifier(EventContext.HTTP_SESSION);

        // second request different session
        Object session2 = new Object();
        ctx.setIdentifier(EventContext.HTTP_SESSION, session2);
        SessionScopeInitDestroyComponent comp3 = (SessionScopeInitDestroyComponent) scope.getContext("NewTestService")
                .getInstance(null);
        Assert.assertNotNull(comp3);
        Assert.assertNotSame(comp2, comp3);
        Assert.assertTrue(comp3.isInitialized());
        ctx.clearIdentifier(EventContext.HTTP_SESSION);

        scope.onEvent(EventContext.SESSION_END, session);
        Assert.assertTrue(comp2.isDestroyed());

        scope.onEvent(EventContext.SESSION_END, session2);
        Assert.assertTrue(comp3.isDestroyed());
        scope.stop();
    }

    /**
     * Tests runtime context registration
     */
    public void testRegisterContextAfterSession() throws Exception {
        EventContext ctx = new EventContextImpl();
        HttpSessionScopeContext scope = new HttpSessionScopeContext(ctx);
        scope.registerConfigurations(createConfigurations());
        scope.start();
        Object session = new Object();

        // first request
        ctx.setIdentifier(EventContext.HTTP_SESSION, session);
        SessionScopeComponent comp1 = (SessionScopeComponent) scope.getContext("TestService1").getInstance(null);
        Assert.assertNotNull(comp1);
        ctx.clearIdentifier(EventContext.HTTP_SESSION);

        scope.registerConfiguration(createConfiguration("NewTestService"));

        // second request
        ctx.setIdentifier(EventContext.HTTP_SESSION, session);
        SessionScopeInitDestroyComponent comp2 = (SessionScopeInitDestroyComponent) scope.getContext("NewTestService")
                .getInstance(null);
        Assert.assertNotNull(comp2);
        Assert.assertTrue(comp2.isInitialized());
        ctx.clearIdentifier(EventContext.HTTP_SESSION);

        // third request different session
        Object session2 = new Object();
        ctx.setIdentifier(EventContext.HTTP_SESSION, session2);
        SessionScopeInitDestroyComponent comp3 = (SessionScopeInitDestroyComponent) scope.getContext("NewTestService")
                .getInstance(null);
        Assert.assertNotNull(comp3);
        Assert.assertNotSame(comp2, comp3);
        Assert.assertTrue(comp3.isInitialized());
        ctx.clearIdentifier(EventContext.HTTP_SESSION);

        scope.onEvent(EventContext.SESSION_END, session);
        Assert.assertTrue(comp2.isDestroyed());

        scope.onEvent(EventContext.SESSION_END, session2);
        Assert.assertTrue(comp3.isDestroyed());
        scope.stop();
    }

    // ----------------------------------
    // Private methods
    // ----------------------------------
    JavaComponentContextBuilder2 builder = new JavaComponentContextBuilder2();

    private List<RuntimeConfiguration<InstanceContext>> createConfigurations() throws NoSuchMethodException, BuilderException {
        SimpleComponent component = MockAssemblyFactory.createComponent("TestService1", SessionScopeComponentImpl.class, ScopeEnum.SESSION_LITERAL);
        builder.build(component, null);
        List<RuntimeConfiguration<InstanceContext>> configs = new ArrayList();
        configs.add((RuntimeConfiguration<InstanceContext>) component.getComponentImplementation().getRuntimeConfiguration());
        return configs;
    }

    private RuntimeConfiguration<InstanceContext> createConfiguration(String name) throws NoSuchMethodException, BuilderException {
        SimpleComponent component = MockAssemblyFactory.createComponent(name, SessionScopeInitDestroyComponent.class, ScopeEnum.SESSION_LITERAL);
        builder.build(component, null);
        return (RuntimeConfiguration<InstanceContext>) component.getComponentImplementation().getRuntimeConfiguration();
    }
}
