/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.context;

import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.common.monitor.impl.NullMonitorFactory;
import org.apache.tuscany.core.builder.RuntimeConfigurationBuilder;
import org.apache.tuscany.core.context.impl.AggregateContextImpl;
import org.apache.tuscany.core.context.impl.EventContextImpl;
import org.apache.tuscany.core.context.scope.DefaultScopeStrategy;
import org.apache.tuscany.core.mock.MockConfigContext;
import org.apache.tuscany.core.mock.MockSystemAssemblyFactory;
import org.apache.tuscany.core.mock.component.GenericSystemComponent;
import org.apache.tuscany.core.mock.component.ModuleScopeSystemComponent;
import org.apache.tuscany.core.mock.component.ModuleScopeSystemComponentImpl;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.tuscany.model.assembly.Module;
import org.apache.tuscany.model.assembly.Scope;

/**
 * Tests registration of model objects for an aggregate context
 * 
 * @version $Rev$ $Date$
 */
public class AggregateComponentContextRegisterTestCase extends TestCase {

    public void testModuleRegistration() throws Exception {
        AggregateContext moduleContext = createContext();
        Module module = MockSystemAssemblyFactory.createSystemModule();
        moduleContext.registerModelObject(module);
        moduleContext.start();
        moduleContext.fireEvent(EventContext.MODULE_START, null);
        GenericSystemComponent component = (GenericSystemComponent) moduleContext.locateInstance("TestService1");
        Assert.assertNotNull(component);
        GenericSystemComponent ep = (GenericSystemComponent) moduleContext.locateInstance("TestService1EP");
        Assert.assertNotNull(ep);
        moduleContext.fireEvent(EventContext.MODULE_STOP, null);
        moduleContext.stop();
    }

    public void testModuleRegistrationAfterStart() throws Exception {
        AggregateContext moduleContext = createContext();
        moduleContext.start();
        Module module = MockSystemAssemblyFactory.createSystemModule();
        moduleContext.registerModelObject(module);
        moduleContext.fireEvent(EventContext.MODULE_START, null);
        GenericSystemComponent component = (GenericSystemComponent) moduleContext.locateInstance("TestService1");
        Assert.assertNotNull(component);
        GenericSystemComponent ep = (GenericSystemComponent) moduleContext.locateInstance("TestService1EP");
        Assert.assertNotNull(ep);
        moduleContext.fireEvent(EventContext.MODULE_STOP, null);
        moduleContext.stop();
    }

    public void testRegistration() throws Exception {
        AggregateContext moduleContext = createContext();
        Component component = MockSystemAssemblyFactory.createComponent("TestService1", ModuleScopeSystemComponentImpl.class
                .getName(), Scope.MODULE);
        moduleContext.registerModelObject(component);
        EntryPoint ep = MockSystemAssemblyFactory.createEntryPoint("TestService1EP", ModuleScopeSystemComponent.class,
                "TestService1", component);
        moduleContext.registerModelObject(ep);
        moduleContext.start();
        moduleContext.fireEvent(EventContext.MODULE_START, null);
        GenericSystemComponent test = (GenericSystemComponent) moduleContext.locateInstance("TestService1");
        Assert.assertNotNull(test);
        GenericSystemComponent testEP = (GenericSystemComponent) moduleContext.locateInstance("TestService1EP");
        Assert.assertNotNull(testEP);
        moduleContext.fireEvent(EventContext.MODULE_STOP, null);
        moduleContext.stop();
    }

    public void testRegistrationAfterStart() throws Exception {
        AggregateContext moduleContext = createContext();
        Component component = MockSystemAssemblyFactory.createComponent("TestService1", ModuleScopeSystemComponentImpl.class
                .getName(), Scope.MODULE);
        moduleContext.start();
        moduleContext.registerModelObject(component);
        EntryPoint ep = MockSystemAssemblyFactory.createEntryPoint("TestService1EP", ModuleScopeSystemComponent.class,
                "TestService1", component);
        moduleContext.registerModelObject(ep);
        moduleContext.fireEvent(EventContext.MODULE_START, null);
        GenericSystemComponent test = (GenericSystemComponent) moduleContext.locateInstance("TestService1");
        Assert.assertNotNull(test);
        GenericSystemComponent testEP = (GenericSystemComponent) moduleContext.locateInstance("TestService1EP");
        Assert.assertNotNull(testEP);
        moduleContext.fireEvent(EventContext.MODULE_STOP, null);
        moduleContext.stop();
    }

    public void testEPRegistrationAfterModuleStart() throws Exception {
        AggregateContext moduleContext = createContext();
        Component component = MockSystemAssemblyFactory.createComponent("TestService1", ModuleScopeSystemComponentImpl.class
                .getName(), Scope.MODULE);
        moduleContext.start();
        moduleContext.registerModelObject(component);
        moduleContext.fireEvent(EventContext.MODULE_START, null);
        GenericSystemComponent test = (GenericSystemComponent) moduleContext.locateInstance("TestService1");
        Assert.assertNotNull(test);
        EntryPoint ep = MockSystemAssemblyFactory.createEntryPoint("TestService1EP", ModuleScopeSystemComponent.class,
                "TestService1", component);
        moduleContext.registerModelObject(ep);
        GenericSystemComponent testEP = (GenericSystemComponent) moduleContext.locateInstance("TestService1EP");
        Assert.assertNotNull(testEP);
        moduleContext.fireEvent(EventContext.MODULE_STOP, null);
        moduleContext.stop();
    }

    protected AggregateContext createContext() {
        List<RuntimeConfigurationBuilder> builders = MockSystemAssemblyFactory.createBuilders();
        return new AggregateContextImpl(
                "test.context",
                null,
                new DefaultScopeStrategy(),
                new EventContextImpl(),
                new MockConfigContext(builders),
                new NullMonitorFactory());
    }
}
