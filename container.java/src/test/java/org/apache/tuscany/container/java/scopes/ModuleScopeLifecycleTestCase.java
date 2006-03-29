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
import org.apache.tuscany.container.java.mock.components.ModuleScopeDestroyOnlyComponent;
import org.apache.tuscany.container.java.mock.components.ModuleScopeEagerInitComponent;
import org.apache.tuscany.container.java.mock.components.ModuleScopeEagerInitDestroyComponent;
import org.apache.tuscany.container.java.mock.components.ModuleScopeInitDestroyComponent;
import org.apache.tuscany.container.java.mock.components.ModuleScopeInitOnlyComponent;
import org.apache.tuscany.core.builder.BuilderException;
import org.apache.tuscany.core.builder.ContextFactory;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.context.InstanceContext;
import org.apache.tuscany.core.context.impl.EventContextImpl;
import org.apache.tuscany.core.context.scope.ModuleScopeContext;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.assembly.SimpleComponent;

/**
 * Lifecycle unit tests for the Http session scope container
 * 
 * @version $Rev$ $Date$
 */
public class ModuleScopeLifecycleTestCase extends TestCase {

    public void testInitDestroy() throws Exception {
        EventContext ctx = new EventContextImpl();
        ModuleScopeContext scope = new ModuleScopeContext(ctx);
        scope.registerFactories(createComponents());
        scope.start();
        scope.onEvent(EventContext.MODULE_START, null);
        ModuleScopeInitDestroyComponent initDestroy = (ModuleScopeInitDestroyComponent) scope.getContext(
                "TestServiceInitDestroy").getInstance(null);
        Assert.assertNotNull(initDestroy);
        ModuleScopeInitOnlyComponent initOnly = (ModuleScopeInitOnlyComponent) scope.getContext("TestServiceInitOnly")
                .getInstance(null);
        Assert.assertNotNull(initOnly);
        ModuleScopeDestroyOnlyComponent destroyOnly = (ModuleScopeDestroyOnlyComponent) scope.getContext(
                "TestServiceDestroyOnly").getInstance(null);
        Assert.assertNotNull(destroyOnly);

        Assert.assertTrue(initDestroy.isInitialized());
        Assert.assertTrue(initOnly.isInitialized());
        Assert.assertFalse(initDestroy.isDestroyed());
        Assert.assertFalse(destroyOnly.isDestroyed());

        // expire module
        scope.onEvent(EventContext.MODULE_STOP, null);

        Assert.assertTrue(initDestroy.isDestroyed());
        Assert.assertTrue(destroyOnly.isDestroyed());

        scope.stop();
    }

    public void testEagerInit() throws Exception {
        EventContext ctx = new EventContextImpl();
        ModuleScopeContext scope = new ModuleScopeContext(ctx);
        scope.registerFactories(createEagerInitComponents());
        scope.start();
        scope.onEvent(EventContext.MODULE_START, null);
        ModuleScopeEagerInitDestroyComponent initDestroy = (ModuleScopeEagerInitDestroyComponent) scope.getContext(
                "TestServiceEagerInitDestroy").getInstance(null);
        Assert.assertNotNull(initDestroy);
        ModuleScopeEagerInitComponent initOnly = (ModuleScopeEagerInitComponent) scope
                .getContext("TestServiceEagerInit").getInstance(null);
        Assert.assertNotNull(initOnly);

        Assert.assertTrue(initDestroy.isInitialized());
        Assert.assertTrue(initOnly.isInitialized());
        Assert.assertFalse(initDestroy.isDestroyed());

        // expire module
        scope.onEvent(EventContext.MODULE_STOP, null);

        Assert.assertTrue(initDestroy.isDestroyed());

        scope.stop();

    }

    public void testDestroyOrder() throws Exception {
        EventContext ctx = new EventContextImpl();
        ModuleScopeContext scope = new ModuleScopeContext(ctx);
        scope.registerFactories(createOrderedInitComponents());
        scope.start();
        scope.onEvent(EventContext.MODULE_START, null);
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

        // expire module
        scope.onEvent(EventContext.MODULE_STOP, null);
        Assert.assertEquals(0, one.getNumberInstantiated());
        scope.stop();
    }

    public void testEagerInitDestroyOrder() throws Exception {
        EventContext ctx = new EventContextImpl();
        ModuleScopeContext scope = new ModuleScopeContext(ctx);
        scope.registerFactories(createOrderedEagerInitComponents());
        scope.start();
        scope.onEvent(EventContext.MODULE_START, null);
        OrderedEagerInitPojo one = (OrderedEagerInitPojo) scope.getContext("one").getInstance(null);
        Assert.assertNotNull(one);

        OrderedEagerInitPojo two = (OrderedEagerInitPojo) scope.getContext("two").getInstance(null);
        Assert.assertNotNull(two);

        OrderedEagerInitPojo three = (OrderedEagerInitPojo) scope.getContext("three").getInstance(null);
        Assert.assertNotNull(three);

        // expire module
        scope.onEvent(EventContext.MODULE_STOP, null);
        Assert.assertEquals(0, one.getNumberInstantiated());
        scope.stop();
    }

    // ----------------------------------
    // Private methods
    // ----------------------------------

    JavaContextFactoryBuilder builder = new JavaContextFactoryBuilder();

    private List<ContextFactory<InstanceContext>> createComponents() throws NoSuchMethodException, BuilderException {
        SimpleComponent[] ca = new SimpleComponent[3];
        ca[0] = MockFactory.createComponent("TestServiceInitDestroy", ModuleScopeInitDestroyComponent.class,
                Scope.MODULE);
        ca[1] = MockFactory.createComponent("TestServiceInitOnly", ModuleScopeInitOnlyComponent.class,
                Scope.MODULE);
        ca[2] = MockFactory.createComponent("TestServiceDestroyOnly", ModuleScopeDestroyOnlyComponent.class,
                Scope.MODULE);
        List<ContextFactory<InstanceContext>> configs = new ArrayList();
        for (int i = 0; i < ca.length; i++) {
            builder.build(ca[i]);
            configs.add((ContextFactory<InstanceContext>) ca[i].getComponentImplementation()
                    .getContextFactory());

        }
        return configs;
    }

    private List<ContextFactory<InstanceContext>> createEagerInitComponents() throws NoSuchMethodException,
            BuilderException {
        SimpleComponent[] ca = new SimpleComponent[2];
        ca[0] = MockFactory.createComponent("TestServiceEagerInitDestroy", ModuleScopeEagerInitDestroyComponent.class,
                Scope.MODULE);
        ca[1] = MockFactory.createComponent("TestServiceEagerInit", ModuleScopeEagerInitComponent.class,
                Scope.MODULE);
        List<ContextFactory<InstanceContext>> configs = new ArrayList();
        for (int i = 0; i < ca.length; i++) {
            builder.build(ca[i]);
            configs.add((ContextFactory<InstanceContext>) ca[i].getComponentImplementation()
                    .getContextFactory());

        }
        return configs;
    }

    private List<ContextFactory<InstanceContext>> createOrderedInitComponents() throws NoSuchMethodException,
            BuilderException {
        SimpleComponent[] ca = new SimpleComponent[3];
        ca[0] = MockFactory.createComponent("one", OrderedInitPojo.class, Scope.MODULE);
        ca[1] = MockFactory.createComponent("two", OrderedInitPojo.class, Scope.MODULE);
        ca[2] = MockFactory.createComponent("three", OrderedInitPojo.class, Scope.MODULE);
        List<ContextFactory<InstanceContext>> configs = new ArrayList();
        for (int i = 0; i < ca.length; i++) {
            builder.build(ca[i]);
            configs.add((ContextFactory<InstanceContext>) ca[i].getComponentImplementation()
                    .getContextFactory());

        }
        return configs;
    }

    private List<ContextFactory<InstanceContext>> createOrderedEagerInitComponents() throws NoSuchMethodException,
            BuilderException {
        SimpleComponent[] ca = new SimpleComponent[3];
        ca[0] = MockFactory.createComponent("one", OrderedEagerInitPojo.class, Scope.MODULE);
        ca[1] = MockFactory.createComponent("two", OrderedEagerInitPojo.class, Scope.MODULE);
        ca[2] = MockFactory.createComponent("three", OrderedEagerInitPojo.class, Scope.MODULE);
        List<ContextFactory<InstanceContext>> configs = new ArrayList();
        for (int i = 0; i < ca.length; i++) {
            builder.build(ca[i]);
            configs.add((ContextFactory<InstanceContext>) ca[i].getComponentImplementation()
                    .getContextFactory());

        }
        return configs;
    }
}
