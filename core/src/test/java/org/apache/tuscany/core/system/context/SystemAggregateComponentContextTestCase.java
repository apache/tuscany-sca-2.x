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

import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.core.builder.RuntimeConfigurationBuilder;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.context.ContextConstants;
import org.apache.tuscany.core.context.impl.AggregateContextImpl;
import org.apache.tuscany.core.context.impl.EventContextImpl;
import org.apache.tuscany.core.mock.MockConfigContext;
import org.apache.tuscany.core.mock.MockSystemAssemblyFactory;
import org.apache.tuscany.core.mock.component.ModuleScopeSystemComponent;
import org.apache.tuscany.core.mock.component.ModuleScopeSystemComponentImpl;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.tuscany.model.assembly.ScopeEnum;
import org.apache.tuscany.common.monitor.impl.NullMonitorFactory;

/**
 * Tests the system aggregate context
 * 
 * @version $Rev$ $Date$
 */
public class SystemAggregateComponentContextTestCase extends TestCase {

    public void testChildLocate() throws Exception {
        List<RuntimeConfigurationBuilder> builders  = MockSystemAssemblyFactory.createBuilders();

        SystemAggregateContextImpl system = new SystemAggregateContextImpl("system", null,
                null, new SystemScopeStrategy(), new EventContextImpl(), new MockConfigContext(builders), new NullMonitorFactory());
        system.start();

        Component aggregateComponent = MockSystemAssemblyFactory.createComponent("system.child",
                AggregateContextImpl.class.getName(), ContextConstants.AGGREGATE_SCOPE_ENUM);
        system.registerModelObject(aggregateComponent);
        AggregateContext childContext = (AggregateContext) system.getContext("system.child");
        Assert.assertNotNull(childContext);

        Component component = MockSystemAssemblyFactory.createComponent("TestService1", ModuleScopeSystemComponentImpl.class.getName(),
                ScopeEnum.MODULE_LITERAL);
        EntryPoint ep = MockSystemAssemblyFactory.createEntryPoint("TestService1EP", ModuleScopeSystemComponent.class,
                "TestService1", component);
        childContext.registerModelObject(component);
        childContext.registerModelObject(ep);
        childContext.fireEvent(EventContext.MODULE_START, null);
        Assert.assertNotNull(system.locateInstance("system.child/TestService1EP"));
        childContext.fireEvent(EventContext.MODULE_STOP, null);
        system.stop();
    }

    public void testAutowire() throws Exception {
        List<RuntimeConfigurationBuilder> builders  = MockSystemAssemblyFactory.createBuilders();
        SystemAggregateContextImpl system = new SystemAggregateContextImpl("system", null,
                null, new SystemScopeStrategy(), new EventContextImpl(), new MockConfigContext(builders), new NullMonitorFactory());

        Component component = MockSystemAssemblyFactory.createComponent("TestService1", ModuleScopeSystemComponentImpl.class
                .getName(), ScopeEnum.MODULE_LITERAL);
        EntryPoint ep = MockSystemAssemblyFactory.createEntryPoint("TestService1EP", ModuleScopeSystemComponent.class,
                "TestService1", component);
        system.registerModelObject(component);
        system.registerModelObject(ep);
        system.start();
        system.fireEvent(EventContext.MODULE_START, null);
        Assert.assertSame(system.locateInstance("TestService1EP"), system.resolveInstance(ModuleScopeSystemComponent.class));
        system.fireEvent(EventContext.MODULE_STOP, null);
        system.stop();
    }

    public void testAutowireRegisterAfterStart() throws Exception {
        List<RuntimeConfigurationBuilder> builders = MockSystemAssemblyFactory.createBuilders();

        SystemAggregateContextImpl system = new SystemAggregateContextImpl("system", null,
                null, new SystemScopeStrategy(), new EventContextImpl(), new MockConfigContext(builders), new NullMonitorFactory());

        Component component = MockSystemAssemblyFactory.createComponent("TestService1", ModuleScopeSystemComponentImpl.class
                .getName(), ScopeEnum.MODULE_LITERAL);
        system.registerModelObject(component);
        system.start();
        system.fireEvent(EventContext.MODULE_START, null);
        EntryPoint ep = MockSystemAssemblyFactory.createEntryPoint("TestService1EP", ModuleScopeSystemComponent.class,
                "TestService1", component);
        system.registerModelObject(ep);
        Assert.assertSame(system.locateInstance("TestService1EP"), system.resolveInstance(ModuleScopeSystemComponent.class));
        system.fireEvent(EventContext.MODULE_STOP, null);
        system.stop();
    }

    public void testAutowireModuleRegister() throws Exception {
        List<RuntimeConfigurationBuilder> builders  = MockSystemAssemblyFactory.createBuilders();

        SystemAggregateContextImpl system = new SystemAggregateContextImpl("system", null,
                null, new SystemScopeStrategy(), new EventContextImpl(), new MockConfigContext(builders), new NullMonitorFactory());
        system.registerModelObject(MockSystemAssemblyFactory.createSystemModule());
        system.start();
        system.fireEvent(EventContext.MODULE_START, null);
        Assert.assertSame(system.locateInstance("TestService1EP"), system.resolveInstance(ModuleScopeSystemComponent.class));
        system.fireEvent(EventContext.MODULE_STOP, null);
        system.stop();
    }

    public void testAutowireModuleRegisterAfterStart() throws Exception {
        List<RuntimeConfigurationBuilder> builders = MockSystemAssemblyFactory.createBuilders();
        SystemAggregateContextImpl system = new SystemAggregateContextImpl("system", null,
                null, new SystemScopeStrategy(), new EventContextImpl(), new MockConfigContext(builders), new NullMonitorFactory());
        system.start();
        system.fireEvent(EventContext.MODULE_START, null);
        system.registerModelObject(MockSystemAssemblyFactory.createSystemModule());
        Assert.assertSame(system.locateInstance("TestService1EP"), system.resolveInstance(ModuleScopeSystemComponent.class));
        system.fireEvent(EventContext.MODULE_STOP, null);
        system.stop();
    }


}
