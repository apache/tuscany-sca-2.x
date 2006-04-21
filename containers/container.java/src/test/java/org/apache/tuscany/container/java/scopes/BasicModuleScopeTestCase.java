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

import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.tuscany.container.java.builder.JavaContextFactoryBuilder;
import org.apache.tuscany.container.java.mock.MockFactory;
import org.apache.tuscany.container.java.mock.components.ModuleScopeComponentImpl;
import org.apache.tuscany.container.java.mock.components.ModuleScopeInitDestroyComponent;
import org.apache.tuscany.core.builder.BuilderException;
import org.apache.tuscany.core.builder.ContextFactory;
import org.apache.tuscany.core.context.Context;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.context.event.ModuleStart;
import org.apache.tuscany.core.context.event.ModuleStop;
import org.apache.tuscany.core.context.impl.EventContextImpl;
import org.apache.tuscany.core.context.scope.ModuleScopeContext;
import org.apache.tuscany.core.wire.service.WireFactoryService;
import org.apache.tuscany.core.wire.service.DefaultWireFactoryService;
import org.apache.tuscany.core.wire.jdk.JDKProxyFactoryFactory;
import org.apache.tuscany.core.message.impl.MessageFactoryImpl;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.assembly.AtomicComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for the module scope container
 *
 * @version $Rev$ $Date$
 */
public class BasicModuleScopeTestCase extends TestCase {

    /**
     * Tests instance identity is properly maintained
     */
    public void testInstanceManagement() throws Exception {
        EventContext ctx = new EventContextImpl();
        ModuleScopeContext scope = new ModuleScopeContext(ctx);
        scope.registerFactories(createConfigurations());
        scope.start();
        // first request
        scope.onEvent(new ModuleStart(this));
        ModuleScopeComponentImpl comp1 = (ModuleScopeComponentImpl) scope.getContext("TestService1").getInstance(null);
        Assert.assertNotNull(comp1);
        // second request
        ModuleScopeComponentImpl comp2 = (ModuleScopeComponentImpl) scope.getContext("TestService1").getInstance(null);
        Assert.assertNotNull(comp2);
        Assert.assertSame(comp1, comp2);
        scope.onEvent(new ModuleStop(this));
        scope.stop();
    }

    public void testSetNullComponents() throws Exception {
        EventContext ctx = new EventContextImpl();
        ModuleScopeContext scope = new ModuleScopeContext(ctx);
        scope.registerFactories(createConfigurations());
        scope.start();
        scope.onEvent(new ModuleStart(this));
        scope.onEvent(new ModuleStop(this));
        scope.stop();
    }

    public void testRegisterContextBeforeStart() throws Exception {
        EventContext ctx = new EventContextImpl();
        ModuleScopeContext scope = new ModuleScopeContext(ctx);
        scope.registerFactories(createConfigurations());
        scope.start();
        scope.registerFactory(createConfiguration("NewTestService"));
        scope.onEvent(new ModuleStart(this));
        ModuleScopeInitDestroyComponent comp2 = (ModuleScopeInitDestroyComponent) scope.getContext("NewTestService").getInstance(null);
        Assert.assertNotNull(comp2);
        Assert.assertTrue(comp2.isInitialized());
        scope.onEvent(new ModuleStop(this));
        Assert.assertTrue(comp2.isDestroyed());
        scope.stop();
    }

    public void testRegisterContextAfterStart() throws Exception {
        EventContext ctx = new EventContextImpl();
        ModuleScopeContext scope = new ModuleScopeContext(ctx);
        scope.start();
        scope.registerFactory(createConfiguration("NewTestService"));
        scope.onEvent(new ModuleStart(this));
        scope.registerFactories(createConfigurations());
        ModuleScopeInitDestroyComponent comp2 = (ModuleScopeInitDestroyComponent) scope.getContext("NewTestService").getInstance(null);
        Assert.assertNotNull(comp2);
        Assert.assertTrue(comp2.isInitialized());
        scope.onEvent(new ModuleStop(this));
        Assert.assertTrue(comp2.isDestroyed());
        scope.stop();
    }


    private List<ContextFactory<Context>> createConfigurations() throws BuilderException {
        WireFactoryService wireService = new DefaultWireFactoryService(new MessageFactoryImpl(), new JDKProxyFactoryFactory());
        JavaContextFactoryBuilder builder = new JavaContextFactoryBuilder(wireService);
        AtomicComponent component = MockFactory.createComponent("TestService1", ModuleScopeComponentImpl.class, Scope.MODULE);
        builder.build(component);
        List<ContextFactory<Context>> configs = new ArrayList<ContextFactory<Context>>();
        configs.add((ContextFactory<Context>) component.getContextFactory());
        return configs;
    }

    private ContextFactory<Context> createConfiguration(String name) throws BuilderException {
        WireFactoryService wireService = new DefaultWireFactoryService(new MessageFactoryImpl(), new JDKProxyFactoryFactory());
        JavaContextFactoryBuilder builder = new JavaContextFactoryBuilder(wireService);
        AtomicComponent component = MockFactory.createComponent(name, ModuleScopeInitDestroyComponent.class,
                Scope.MODULE);
        builder.build(component);
        return (ContextFactory<Context>) component.getContextFactory();
    }
}
