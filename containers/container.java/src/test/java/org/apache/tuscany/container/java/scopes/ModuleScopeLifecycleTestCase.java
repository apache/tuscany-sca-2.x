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
import org.apache.tuscany.core.builder.system.DefaultPolicyBuilderRegistry;
import org.apache.tuscany.core.config.ComponentTypeIntrospector;
import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.core.context.Context;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.context.event.ModuleStart;
import org.apache.tuscany.core.context.event.ModuleStop;
import org.apache.tuscany.core.context.impl.EventContextImpl;
import org.apache.tuscany.core.context.scope.ModuleScopeContext;
import org.apache.tuscany.core.message.impl.MessageFactoryImpl;
import org.apache.tuscany.core.wire.jdk.JDKWireFactoryFactory;
import org.apache.tuscany.core.wire.service.DefaultWireFactoryService;
import org.apache.tuscany.core.wire.service.WireFactoryService;
import org.apache.tuscany.model.assembly.AtomicComponent;
import org.apache.tuscany.model.assembly.ComponentType;
import org.apache.tuscany.model.assembly.Scope;

/**
 * Lifecycle unit tests for the module scope container
 *
 * @version $Rev$ $Date$
 */
public class ModuleScopeLifecycleTestCase extends TestCase {

    JavaContextFactoryBuilder builder;

    public void testInitDestroy() throws Exception {
        EventContext ctx = new EventContextImpl();
        ModuleScopeContext scope = new ModuleScopeContext(ctx);
        scope.registerFactories(createComponents());
        scope.start();
        scope.onEvent(new ModuleStart(this));
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
        scope.onEvent(new ModuleStop(this));

        Assert.assertTrue(initDestroy.isDestroyed());
        Assert.assertTrue(destroyOnly.isDestroyed());

        scope.stop();
    }

    public void testEagerInit() throws Exception {
        EventContext ctx = new EventContextImpl();
        ModuleScopeContext scope = new ModuleScopeContext(ctx);
        scope.registerFactories(createEagerInitComponents());
        scope.start();
        scope.onEvent(new ModuleStart(this));
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
        scope.onEvent(new ModuleStop(this));

        Assert.assertTrue(initDestroy.isDestroyed());

        scope.stop();

    }

    public void testDestroyOrder() throws Exception {
        EventContext ctx = new EventContextImpl();
        ModuleScopeContext scope = new ModuleScopeContext(ctx);
        scope.registerFactories(createOrderedInitComponents());
        scope.start();
        scope.onEvent(new ModuleStart(this));
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
        scope.onEvent(new ModuleStop(this));
        Assert.assertEquals(0, one.getNumberInstantiated());
        scope.stop();
    }

    public void testEagerInitDestroyOrder() throws Exception {
        EventContext ctx = new EventContextImpl();
        ModuleScopeContext scope = new ModuleScopeContext(ctx);
        scope.registerFactories(createOrderedEagerInitComponents());
        scope.start();
        scope.onEvent(new ModuleStart(this));
        OrderedEagerInitPojo one = (OrderedEagerInitPojo) scope.getContext("one").getInstance(null);
        Assert.assertNotNull(one);

        OrderedEagerInitPojo two = (OrderedEagerInitPojo) scope.getContext("two").getInstance(null);
        Assert.assertNotNull(two);

        OrderedEagerInitPojo three = (OrderedEagerInitPojo) scope.getContext("three").getInstance(null);
        Assert.assertNotNull(three);

        // expire module
        scope.onEvent(new ModuleStop(this));
        Assert.assertEquals(0, one.getNumberInstantiated());
        scope.stop();
    }


    private List<ContextFactory<Context>> createComponents() throws BuilderException, ConfigurationLoadException {
        AtomicComponent[] ca = new AtomicComponent[3];
        ca[0] = MockFactory.createComponent("TestServiceInitDestroy", ModuleScopeInitDestroyComponent.class,
                Scope.MODULE);
        ca[1] = MockFactory.createComponent("TestServiceInitOnly", ModuleScopeInitOnlyComponent.class,
                Scope.MODULE);
        ca[2] = MockFactory.createComponent("TestServiceDestroyOnly", ModuleScopeDestroyOnlyComponent.class,
                Scope.MODULE);
        List<ContextFactory<Context>> configs = new ArrayList<ContextFactory<Context>>();
        ComponentTypeIntrospector introspector = MockFactory.getIntrospector();
        ca[0].getImplementation().setComponentType(introspector.introspect(ModuleScopeInitDestroyComponent.class));
        ca[1].getImplementation().setComponentType(introspector.introspect(ModuleScopeInitOnlyComponent.class));
        ca[2].getImplementation().setComponentType(introspector.introspect(ModuleScopeDestroyOnlyComponent.class));
        for (AtomicComponent aCa : ca) {
            builder.build(aCa);
            configs.add((ContextFactory<Context>) aCa.getContextFactory());

        }
        return configs;
    }

    private List<ContextFactory<Context>> createEagerInitComponents() throws
            BuilderException, ConfigurationLoadException {
        AtomicComponent[] ca = new AtomicComponent[2];
        ca[0] = MockFactory.createComponent("TestServiceEagerInitDestroy", ModuleScopeEagerInitDestroyComponent.class,
                Scope.MODULE);
        ca[1] = MockFactory.createComponent("TestServiceEagerInit", ModuleScopeEagerInitComponent.class,
                Scope.MODULE);
        List<ContextFactory<Context>> configs = new ArrayList<ContextFactory<Context>>();
        ComponentTypeIntrospector introspector = MockFactory.getIntrospector();
        ComponentType type = introspector.introspect(OrderedInitPojo.class);
        ca[0].getImplementation().setComponentType(introspector.introspect(ModuleScopeEagerInitDestroyComponent.class));
        ca[1].getImplementation().setComponentType(introspector.introspect(ModuleScopeEagerInitComponent.class));
        for (AtomicComponent aCa : ca) {
            builder.build(aCa);
            configs.add((ContextFactory<Context>) aCa.getContextFactory());
        }
        return configs;
    }

    private List<ContextFactory<Context>> createOrderedInitComponents() throws
            BuilderException, ConfigurationLoadException {
        AtomicComponent[] ca = new AtomicComponent[3];
        ca[0] = MockFactory.createComponent("one", OrderedInitPojo.class, Scope.MODULE);
        ca[1] = MockFactory.createComponent("two", OrderedInitPojo.class, Scope.MODULE);
        ca[2] = MockFactory.createComponent("three", OrderedInitPojo.class, Scope.MODULE);
        List<ContextFactory<Context>> configs = new ArrayList<ContextFactory<Context>>();
        ComponentTypeIntrospector introspector = MockFactory.getIntrospector();
        ComponentType type = introspector.introspect(OrderedInitPojo.class);
        ca[0].getImplementation().setComponentType(type);
        ca[1].getImplementation().setComponentType(type);
        ca[2].getImplementation().setComponentType(type);
        for (AtomicComponent aCa : ca) {
            builder.build(aCa);
            configs.add((ContextFactory<Context>) aCa.getContextFactory());
        }
        return configs;
    }

    private List<ContextFactory<Context>> createOrderedEagerInitComponents() throws
            BuilderException, ConfigurationLoadException {
        AtomicComponent[] ca = new AtomicComponent[3];
        ca[0] = MockFactory.createComponent("one", OrderedEagerInitPojo.class, Scope.MODULE);
        ca[1] = MockFactory.createComponent("two", OrderedEagerInitPojo.class, Scope.MODULE);
        ca[2] = MockFactory.createComponent("three", OrderedEagerInitPojo.class, Scope.MODULE);
        ComponentTypeIntrospector introspector = MockFactory.getIntrospector();
        ComponentType type = introspector.introspect(OrderedEagerInitPojo.class);
        ca[0].getImplementation().setComponentType(type);
        ca[1].getImplementation().setComponentType(type);
        ca[2].getImplementation().setComponentType(type);
        List<ContextFactory<Context>> configs = new ArrayList<ContextFactory<Context>>();
        for (AtomicComponent aCa : ca) {
            builder.build(aCa);
            configs.add((ContextFactory<Context>) aCa.getContextFactory());

        }
        return configs;
    }

    protected void setUp() throws Exception {
        super.setUp();
        WireFactoryService wireService = new DefaultWireFactoryService(new MessageFactoryImpl(), new JDKWireFactoryFactory(), new DefaultPolicyBuilderRegistry());
        builder = new JavaContextFactoryBuilder(wireService);
    }
}
