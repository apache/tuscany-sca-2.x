/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.system.context;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.tuscany.common.monitor.impl.NullMonitorFactory;
import org.apache.tuscany.core.builder.ContextFactoryBuilder;
import org.apache.tuscany.core.context.CompositeContext;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.context.impl.EventContextImpl;
import org.apache.tuscany.core.mock.MockConfigContext;
import org.apache.tuscany.core.mock.MockFactory;
import org.apache.tuscany.core.mock.component.ModuleScopeSystemComponent;
import org.apache.tuscany.core.mock.component.ModuleScopeSystemComponentImpl;
import org.apache.tuscany.core.system.assembly.SystemAssemblyFactory;
import org.apache.tuscany.core.system.assembly.impl.SystemAssemblyFactoryImpl;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.tuscany.model.assembly.Scope;

import java.util.List;

/**
 * Tests the system composite context
 * 
 * @version $Rev$ $Date$
 */
public class SystemCompositeComponentContextTestCase extends TestCase {
    private SystemAssemblyFactory factory;
    private SystemCompositeContextImpl system;

    public void testChildLocate() throws Exception {
        system.start();
        Component compositeComponent = MockFactory.createCompositeComponent("system.child");
        system.registerModelObject(compositeComponent);
        CompositeContext childContext = (CompositeContext) system.getContext("system.child");
        Assert.assertNotNull(childContext);

        Component component = factory.createSystemComponent("TestService1", ModuleScopeSystemComponent.class, ModuleScopeSystemComponentImpl.class, Scope.MODULE);
        EntryPoint ep = MockFactory.createEPSystemBinding("TestService1EP", ModuleScopeSystemComponent.class, "TestService1", component);
        childContext.registerModelObject(component);
        childContext.registerModelObject(ep);
        childContext.fireEvent(EventContext.MODULE_START, null);
        Assert.assertNotNull(system.getContext("system.child").getInstance(new QualifiedName("./TestService1EP")));
        childContext.fireEvent(EventContext.MODULE_STOP, null);
    }

    public void testAutowireRegisterBeforeStart() throws Exception {
        Component component = factory.createSystemComponent("TestService1", ModuleScopeSystemComponent.class, ModuleScopeSystemComponentImpl.class, Scope.MODULE);
        EntryPoint ep = MockFactory.createEPSystemBinding("TestService1EP", ModuleScopeSystemComponent.class, "TestService1", component);
        system.registerModelObject(component);
        system.registerModelObject(ep);
        system.start();
        system.fireEvent(EventContext.MODULE_START, null);
        Assert.assertSame(system.getContext("TestService1EP").getInstance(null), system.resolveInstance(ModuleScopeSystemComponent.class));
    }

    public void testAutowireRegisterAfterStart() throws Exception {
        Component component = factory.createSystemComponent("TestService1", ModuleScopeSystemComponent.class, ModuleScopeSystemComponentImpl.class, Scope.MODULE);
        system.registerModelObject(component);
        system.start();
        system.fireEvent(EventContext.MODULE_START, null);
        EntryPoint ep = MockFactory.createEPSystemBinding("TestService1EP", ModuleScopeSystemComponent.class, "TestService1", component);
        system.registerModelObject(ep);
        Assert.assertSame(system.getContext("TestService1EP").getInstance(null), system.resolveInstance(ModuleScopeSystemComponent.class));
    }

    public void testAutowireModuleRegisterBeforeStart() throws Exception {
        system.registerModelObject(MockFactory.createSystemModule());
        system.start();
        system.fireEvent(EventContext.MODULE_START, null);
        Assert.assertSame(system.getContext("TestService1EP").getInstance(null), system.resolveInstance(ModuleScopeSystemComponent.class));
    }

    public void testAutowireModuleRegisterAfterStart() throws Exception {
        system.start();
        system.fireEvent(EventContext.MODULE_START, null);
        system.registerModelObject(MockFactory.createSystemModule());
        Assert.assertSame(system.getContext("TestService1EP").getInstance(null), system.resolveInstance(ModuleScopeSystemComponent.class));
    }

    protected void setUp() throws Exception {
        super.setUp();
        factory = new SystemAssemblyFactoryImpl();
        List<ContextFactoryBuilder> builders = MockFactory.createSystemBuilders();

        system = new SystemCompositeContextImpl("system", null, null, new SystemScopeStrategy(),
                new EventContextImpl(), new MockConfigContext(builders), new NullMonitorFactory());
    }

    protected void tearDown() throws Exception {
        system.fireEvent(EventContext.MODULE_STOP, null);
        system.stop();
        super.tearDown();
    }
}
