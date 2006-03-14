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
import org.apache.tuscany.container.java.mock.components.RequestScopeComponent;
import org.apache.tuscany.container.java.mock.components.RequestScopeComponentImpl;
import org.apache.tuscany.core.builder.BuilderException;
import org.apache.tuscany.core.builder.ContextFactory;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.context.InstanceContext;
import org.apache.tuscany.core.context.impl.EventContextImpl;
import org.apache.tuscany.core.context.scope.RequestScopeContext;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.assembly.SimpleComponent;

/**
 * Unit tests for the request scope container
 * 
 * @version $Rev$ $Date$
 */
public class BasicRequestScopeTestCase extends TestCase {

    /**
     * Tests instance identity is properly maintained
     */
    public void testInstanceManagement() throws Exception {
        EventContext ctx = new EventContextImpl();
        RequestScopeContext scope = new RequestScopeContext(ctx);
        scope.registerFactorys(createConfigurations());
        scope.start();

        // first request
        RequestScopeComponentImpl comp1 = (RequestScopeComponentImpl) scope.getContext("TestService1").getInstance(null);
        Assert.assertNotNull(comp1);
        scope.onEvent(EventContext.REQUEST_END, null);

        // second request
        RequestScopeComponentImpl comp2 = (RequestScopeComponentImpl) scope.getContext("TestService1").getInstance(null);
        Assert.assertNotNull(comp2);
        Assert.assertNotSame(comp1, comp2);
        scope.onEvent(EventContext.REQUEST_END, null);

        scope.stop();
    }

    public void testRegisterContextBeforeRequest() throws Exception {
        EventContext ctx = new EventContextImpl();
        RequestScopeContext scope = new RequestScopeContext(ctx);
        scope.registerFactorys(createConfigurations());
        scope.registerFactory(createConfiguration("NewTestService"));
        scope.start();
        RequestScopeComponent comp1 = (RequestScopeComponent) scope.getContext("TestService1").getInstance(null);
        Assert.assertNotNull(comp1);
        RequestScopeComponent comp2 = (RequestScopeComponent) scope.getContext("NewTestService").getInstance(null);
        Assert.assertNotNull(comp2);
        scope.onEvent(EventContext.REQUEST_END, null);
        scope.stop();
    }

    public void testRegisterContextAfterRequest() throws Exception {
        EventContext ctx = new EventContextImpl();
        RequestScopeContext scope = new RequestScopeContext(ctx);
        scope.registerFactorys(createConfigurations());
        scope.start();
        RequestScopeComponent comp1 = (RequestScopeComponent) scope.getContext("TestService1").getInstance(null);
        Assert.assertNotNull(comp1);
        scope.registerFactory(createConfiguration("NewTestService"));
        RequestScopeComponent comp2 = (RequestScopeComponent) scope.getContext("NewTestService").getInstance(null);
        Assert.assertNotNull(comp2);
        scope.onEvent(EventContext.REQUEST_END, null);
        scope.stop();
    }

    /**
     * Tests setting no components in the scope
     */
    public void testSetNullComponents() throws Exception {
        EventContext ctx = new EventContextImpl();
        RequestScopeContext scope = new RequestScopeContext(ctx);
        scope.registerFactorys(createConfigurations());
        scope.start();
        scope.stop();
    }

    public void testGetComponentByKey() throws Exception {
        EventContext ctx = new EventContextImpl();
        RequestScopeContext scope = new RequestScopeContext(ctx);
        scope.registerFactorys(createConfigurations());
        scope.start();

        RequestScopeComponentImpl comp1 = (RequestScopeComponentImpl) scope.getContext("TestService1").getInstance(null);
        Assert.assertNotNull(comp1);
        scope.onEvent(EventContext.REQUEST_END, null);

        // second request
        // should be null since the other context (thread) expired w/ onEvent(..)
        Assert.assertNull((RequestScopeComponentImpl) scope.getContextByKey("TestService1", Thread.currentThread()));
        // Note should test better using concurrent threads to pull the instance

        scope.stop();
    }

    // ----------------------------------
    // Private methods
    // ----------------------------------

    JavaContextFactoryBuilder builder = new JavaContextFactoryBuilder();

    private List<ContextFactory<InstanceContext>> createConfigurations() throws NoSuchMethodException, BuilderException {
        SimpleComponent component = MockFactory.createComponent("TestService1", RequestScopeComponentImpl.class,
                Scope.REQUEST);
        builder.build(component);
        List<ContextFactory<InstanceContext>> configs = new ArrayList();
        configs.add((ContextFactory<InstanceContext>) component.getComponentImplementation().getContextFactory());
        return configs;
    }

    private ContextFactory<InstanceContext> createConfiguration(String name) throws NoSuchMethodException,
            BuilderException {
        SimpleComponent component = MockFactory.createComponent(name, RequestScopeComponentImpl.class,
                Scope.REQUEST);
        builder.build(component);
        return (ContextFactory<InstanceContext>) component.getComponentImplementation().getContextFactory();
    }

}
