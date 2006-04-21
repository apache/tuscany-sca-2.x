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
import org.apache.tuscany.core.context.Context;
import org.apache.tuscany.core.context.event.RequestEnd;
import org.apache.tuscany.core.context.impl.EventContextImpl;
import org.apache.tuscany.core.context.scope.RequestScopeContext;
import org.apache.tuscany.core.wire.service.WireFactoryService;
import org.apache.tuscany.core.wire.service.DefaultWireFactoryService;
import org.apache.tuscany.core.wire.jdk.JDKProxyFactoryFactory;
import org.apache.tuscany.core.message.impl.MessageFactoryImpl;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.assembly.AtomicComponent;

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
        scope.registerFactories(createConfigurations());
        scope.start();

        // first request
        RequestScopeComponentImpl comp1 = (RequestScopeComponentImpl) scope.getContext("TestService1").getInstance(null);
        Assert.assertNotNull(comp1);
        Object id = new Object();
        scope.onEvent(new RequestEnd(this,id));

        // second request
        RequestScopeComponentImpl comp2 = (RequestScopeComponentImpl) scope.getContext("TestService1").getInstance(null);
        Assert.assertNotNull(comp2);
        Assert.assertNotSame(comp1, comp2);
        Object id2 = new Object();
        scope.onEvent(new RequestEnd(this,id2));

        scope.stop();
    }

    public void testRegisterContextBeforeRequest() throws Exception {
        EventContext ctx = new EventContextImpl();
        RequestScopeContext scope = new RequestScopeContext(ctx);
        scope.registerFactories(createConfigurations());
        scope.registerFactory(createConfiguration("NewTestService"));
        scope.start();
        RequestScopeComponent comp1 = (RequestScopeComponent) scope.getContext("TestService1").getInstance(null);
        Assert.assertNotNull(comp1);
        RequestScopeComponent comp2 = (RequestScopeComponent) scope.getContext("NewTestService").getInstance(null);
        Assert.assertNotNull(comp2);
        Object id = new Object();
        scope.onEvent(new RequestEnd(this,id));
        scope.stop();
    }

    public void testRegisterContextAfterRequest() throws Exception {
        EventContext ctx = new EventContextImpl();
        RequestScopeContext scope = new RequestScopeContext(ctx);
        scope.registerFactories(createConfigurations());
        scope.start();
        RequestScopeComponent comp1 = (RequestScopeComponent) scope.getContext("TestService1").getInstance(null);
        Assert.assertNotNull(comp1);
        scope.registerFactory(createConfiguration("NewTestService"));
        RequestScopeComponent comp2 = (RequestScopeComponent) scope.getContext("NewTestService").getInstance(null);
        Assert.assertNotNull(comp2);
        Object id = new Object();
        scope.onEvent(new RequestEnd(this,id));
        scope.stop();
    }

    /**
     * Tests setting no components in the scope
     */
    public void testSetNullComponents() throws Exception {
        EventContext ctx = new EventContextImpl();
        RequestScopeContext scope = new RequestScopeContext(ctx);
        scope.registerFactories(createConfigurations());
        scope.start();
        scope.stop();
    }

    public void testGetComponentByKey() throws Exception {
        EventContext ctx = new EventContextImpl();
        RequestScopeContext scope = new RequestScopeContext(ctx);
        scope.registerFactories(createConfigurations());
        scope.start();

        RequestScopeComponentImpl comp1 = (RequestScopeComponentImpl) scope.getContext("TestService1").getInstance(null);
        Assert.assertNotNull(comp1);
        Object id = new Object();
        scope.onEvent(new RequestEnd(this,id));

        // second request
        // should be null since the other context (thread) expired w/ onEvent(..)
        Assert.assertNull(scope.getContextByKey("TestService1", Thread.currentThread()));
        // Note should test better using concurrent threads to pull the instance

        scope.stop();
    }


    private List<ContextFactory<Context>> createConfigurations() throws BuilderException {
        AtomicComponent component = MockFactory.createComponent("TestService1", RequestScopeComponentImpl.class,
                Scope.REQUEST);
        WireFactoryService wireService = new DefaultWireFactoryService(new MessageFactoryImpl(), new JDKProxyFactoryFactory());
        JavaContextFactoryBuilder builder = new JavaContextFactoryBuilder(wireService);
        builder.build(component);
        List<ContextFactory<Context>> configs = new ArrayList<ContextFactory<Context>>();
        configs.add((ContextFactory<Context>) component.getContextFactory());
        return configs;
    }

    private ContextFactory<Context> createConfiguration(String name) throws BuilderException {
        AtomicComponent component = MockFactory.createComponent(name, RequestScopeComponentImpl.class,
                Scope.REQUEST);
        WireFactoryService wireService = new DefaultWireFactoryService(new MessageFactoryImpl(), new JDKProxyFactoryFactory());
        JavaContextFactoryBuilder builder = new JavaContextFactoryBuilder(wireService);
        builder.build(component);
        return (ContextFactory<Context>) component.getContextFactory();
    }

}
