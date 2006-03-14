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
import org.apache.tuscany.container.java.mock.components.SessionScopeDestroyOnlyComponent;
import org.apache.tuscany.container.java.mock.components.SessionScopeInitDestroyComponent;
import org.apache.tuscany.container.java.mock.components.SessionScopeInitOnlyComponent;
import org.apache.tuscany.core.builder.BuilderException;
import org.apache.tuscany.core.builder.ContextFactory;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.context.InstanceContext;
import org.apache.tuscany.core.context.impl.EventContextImpl;
import org.apache.tuscany.core.context.scope.RequestScopeContext;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.assembly.SimpleComponent;

/**
 * Lifecycle unit tests for the Http session scope container
 * 
 * @version $Rev$ $Date$
 */
public class RequestScopeLifecycleTestCase extends TestCase {

    /**
     * Tests instance identity is properly maintained
     */
    public void testInitDestroy() throws Exception {
        EventContext ctx = new EventContextImpl();
        RequestScopeContext scope = new RequestScopeContext(ctx);
        scope.registerFactorys(createComponents());
        scope.start();
        SessionScopeInitDestroyComponent initDestroy = (SessionScopeInitDestroyComponent) scope.getContext(
                "TestServiceInitDestroy").getInstance(null);
        Assert.assertNotNull(initDestroy);
        SessionScopeInitOnlyComponent initOnly = (SessionScopeInitOnlyComponent) scope.getContext("TestServiceInitOnly")
                .getInstance(null);
        Assert.assertNotNull(initOnly);
        SessionScopeDestroyOnlyComponent destroyOnly = (SessionScopeDestroyOnlyComponent) scope.getContext(
                "TestServiceDestroyOnly").getInstance(null);
        Assert.assertNotNull(destroyOnly);

        Assert.assertTrue(initDestroy.isInitialized());
        Assert.assertTrue(initOnly.isInitialized());
        Assert.assertFalse(initDestroy.isDestroyed());
        Assert.assertFalse(destroyOnly.isDestroyed());

        // end request
        scope.onEvent(EventContext.REQUEST_END, null);
        Assert.assertTrue(initDestroy.isDestroyed());
        Assert.assertTrue(destroyOnly.isDestroyed());

        scope.stop();
    }

    /**
     * Test instances destroyed in proper (i.e. reverse) order
     */
    public void testDestroyOrder() throws Exception {
        EventContext ctx = new EventContextImpl();
        RequestScopeContext scope = new RequestScopeContext(ctx);
        scope.registerFactorys(createOrderedInitComponents());
        scope.start();
        // request start
        OrderedInitPojo one = (OrderedInitPojo) scope.getContext("one").getInstance(null);
        Assert.assertNotNull(one);
        Assert.assertEquals(1, one.getNumberInstantiated());
        Assert.assertEquals(1, one.getInitOrder());

        OrderedInitPojo two = (OrderedInitPojo) scope.getContext("two").getInstance(null);
        Assert.assertNotNull(two);
        Assert.assertEquals(2, two.getNumberInstantiated());
        Assert.assertEquals(2, two.getInitOrder());

        OrderedInitPojo three = (OrderedInitPojo) scope.getContext("three").getInstance(null);
        Assert.assertNotNull(three);
        Assert.assertEquals(3, three.getNumberInstantiated());
        Assert.assertEquals(3, three.getInitOrder());

        // end request
        scope.onEvent(EventContext.REQUEST_END, null);

        Assert.assertEquals(0, one.getNumberInstantiated());
        scope.stop();
    }

    // ----------------------------------
    // Private methods
    // ----------------------------------

    JavaContextFactoryBuilder builder = new JavaContextFactoryBuilder();

    private List<ContextFactory<InstanceContext>> createComponents() throws NoSuchMethodException, BuilderException {
        SimpleComponent[] ca = new SimpleComponent[3];
        ca[0] = MockFactory.createComponent("TestServiceInitDestroy", SessionScopeInitDestroyComponent.class,
                Scope.REQUEST);
        ca[1] = MockFactory.createComponent("TestServiceInitOnly", SessionScopeInitOnlyComponent.class,
                Scope.REQUEST);
        ca[2] = MockFactory.createComponent("TestServiceDestroyOnly", SessionScopeDestroyOnlyComponent.class,
                Scope.REQUEST);
        List<ContextFactory<InstanceContext>> configs = new ArrayList();
        for (int i = 0; i < ca.length; i++) {
            builder.build(ca[i]);
            configs.add((ContextFactory<InstanceContext>) ca[i].getComponentImplementation().getContextFactory());

        }
        return configs;
    }

    private List<ContextFactory<InstanceContext>> createOrderedInitComponents() throws NoSuchMethodException,
            BuilderException {
        SimpleComponent[] ca = new SimpleComponent[3];
        ca[0] = MockFactory.createComponent("one", OrderedInitPojo.class, Scope.REQUEST);
        ca[1] = MockFactory.createComponent("two", OrderedInitPojo.class, Scope.REQUEST);
        ca[2] = MockFactory.createComponent("three", OrderedInitPojo.class, Scope.REQUEST);
        List<ContextFactory<InstanceContext>> configs = new ArrayList();
        for (int i = 0; i < ca.length; i++) {
            builder.build(ca[i]);
            configs.add((ContextFactory<InstanceContext>) ca[i].getComponentImplementation().getContextFactory());

        }
        return configs;
    }

}
