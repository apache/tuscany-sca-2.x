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
package org.apache.tuscany.core.runtime;

import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.common.monitor.impl.NullMonitorFactory;
import org.apache.tuscany.core.builder.RuntimeConfigurationBuilder;
import org.apache.tuscany.core.config.ConfigurationException;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.Context;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.context.impl.AggregateContextImpl;
import org.apache.tuscany.core.mock.MockFactory;
import org.apache.tuscany.core.mock.component.ModuleScopeSystemComponent;
import org.apache.tuscany.core.mock.component.ModuleScopeSystemComponentImpl;
import org.apache.tuscany.core.system.assembly.SystemAssemblyFactory;
import org.apache.tuscany.core.system.assembly.impl.SystemAssemblyFactoryImpl;
import org.apache.tuscany.core.system.context.TestBuilder;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.tuscany.model.assembly.ExternalService;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.types.java.JavaServiceContract;
import org.osoa.sca.ServiceUnavailableException;

/**
 * Performs basic tests on the runtime context
 * 
 * @version $Rev$ $Date$
 */
public class RuntimeContextImplTestCase extends TestCase {

    private SystemAssemblyFactory systemFactory = new SystemAssemblyFactoryImpl();

    private List<RuntimeConfigurationBuilder> builders;
    private SystemAssemblyFactory factory;

    /**
     * Tests explicit wiring of an external service to a system entry point that is wired to a child system module entry
     * point
     */
    public void testSystemExplicitWiring() throws Exception {
        RuntimeContext runtime = new RuntimeContextImpl(new NullMonitorFactory(), null, builders, null);
        runtime.start();

        AggregateContext root = runtime.getRootContext();
        Assert.assertNotNull(root);
        Assert.assertTrue(root.getLifecycleState() == Context.RUNNING);

        AggregateContext system = runtime.getSystemContext();
        Assert.assertNotNull(system);
        system.registerModelObject(MockFactory.createSystemModule());

        // register a child system context
        system.registerModelObject(MockFactory.createSystemAggregateComponent("system.child"));
        AggregateContext systemChild = (AggregateContext) system.getContext("system.child");
        systemChild.registerModelObject(MockFactory.createSystemChildModule());

        // register a top-level system entry point that exposes the child entry point
        EntryPoint ep = MockFactory.createEPSystemBinding("TestService2EP", ModuleScopeSystemComponent.class, "ref");
        ep.getBindings().add(systemFactory.createSystemBinding());
        Service service = systemFactory.createService();
        service.setName("system.child/TestService2EP");
        ((ConfiguredService) ep.getConfiguredReference().getTargetConfiguredServices().get(0)).setService(service);
        JavaServiceContract inter = systemFactory.createJavaServiceContract();
        inter.setInterface(ModuleScopeSystemComponentImpl.class);
        service.setServiceContract(inter);
        system.registerModelObject(ep);
        system.fireEvent(EventContext.MODULE_START, null);
        Assert.assertNotNull(system.locateInstance("TestService1"));
        Assert.assertNotNull(system.locateInstance("TestService2EP"));

        // create a test module and wire an external service to the system entry point
        Component moduleComponent = MockFactory.createAggregateComponent("test.module");
        runtime.registerModelObject(moduleComponent);
        AggregateContextImpl moduleContext = (AggregateContextImpl) runtime.getContext("test.module");
        Assert.assertNotNull(moduleContext);
        ExternalService es = MockFactory.createESSystemBinding("TestService2ES", "tuscany.system/TestService2EP");
        moduleContext.registerModelObject(es);
        moduleContext.fireEvent(EventContext.MODULE_START, null);
        Assert.assertNotNull(moduleContext.locateInstance("TestService2ES"));

        moduleContext.fireEvent(EventContext.MODULE_STOP, null);
        system.fireEvent(EventContext.MODULE_STOP, null);
        runtime.stop();
    }

    /**
     * Tests autowiring an external service to a system entry point
     */
    public void testSystemAutoWiring() throws Exception {
        RuntimeContext runtime = new RuntimeContextImpl(new NullMonitorFactory(), null, builders, null);
        runtime.start();

        AggregateContext root = runtime.getRootContext();
        Assert.assertNotNull(root);
        Assert.assertTrue(root.getLifecycleState() == Context.RUNNING);

        AggregateContext system = runtime.getSystemContext();
        Assert.assertNotNull(system);
        system.registerModelObject(MockFactory.createSystemModule());

        // create a test module and wire an external service to the system entry point
        Component moduleComponent = MockFactory.createAggregateComponent("test.module");
        runtime.registerModelObject(moduleComponent);
        AggregateContextImpl moduleContext = (AggregateContextImpl) runtime.getContext("test.module");
        Assert.assertNotNull(moduleContext);
        ExternalService es = MockFactory.createAutowirableExternalService("TestService2ES", ModuleScopeSystemComponent.class);
        moduleContext.registerModelObject(es);

        system.fireEvent(EventContext.MODULE_START, null);
        moduleContext.fireEvent(EventContext.MODULE_START, null);
        // test that the autowire was resolved
        Assert.assertNotNull(moduleContext.locateInstance("TestService2ES"));

        moduleContext.fireEvent(EventContext.MODULE_STOP, null);
        system.fireEvent(EventContext.MODULE_STOP, null);
        runtime.stop();
    }

    public void testServiceNotFound() throws Exception {
        RuntimeContext runtime = new RuntimeContextImpl(new NullMonitorFactory(), null, builders, null);
        runtime.start();

        // create a test module
        Component moduleComponent = MockFactory.createAggregateComponent("module");
        runtime.registerModelObject(moduleComponent);
        AggregateContextImpl moduleContext = (AggregateContextImpl) runtime.getContext("module");
        moduleContext.fireEvent(EventContext.MODULE_START, null);
        try {
            moduleContext.locateService("TestService");
            fail("Expected " + ServiceUnavailableException.class.getName());
        } catch (ServiceUnavailableException e) {
            // expected
        }
        moduleContext.fireEvent(EventContext.MODULE_STOP, null);
        runtime.stop();
    }

    public void testExternalServiceReferenceNotFound() throws Exception {
        RuntimeContext runtime = new RuntimeContextImpl(new NullMonitorFactory(), null, builders, null);
        runtime.start();
        AggregateContext system = runtime.getSystemContext();

        // create a test module
        Component moduleComponent = MockFactory.createAggregateComponent("module");
        runtime.registerModelObject(moduleComponent);
        AggregateContextImpl moduleContext = (AggregateContextImpl) runtime.getContext("module");
        ExternalService es = MockFactory.createESSystemBinding("TestServiceES", "tuscany.system/TestService1xEP");
        moduleContext.registerModelObject(es);

        // start the modules and test inter-module system wires
        system.fireEvent(EventContext.MODULE_START, null);
        moduleContext.fireEvent(EventContext.MODULE_START, null);
        try {
            moduleContext.locateService("TestServiceES");
            fail("Expected " + ServiceUnavailableException.class.getName());
        } catch (ServiceUnavailableException e) {
            // expected
        }
        moduleContext.fireEvent(EventContext.MODULE_STOP, null);
        system.fireEvent(EventContext.MODULE_STOP, null);
        runtime.stop();
    }

    public void testEntryPointReferenceNotFound() throws Exception {
        RuntimeContext runtime = new RuntimeContextImpl(new NullMonitorFactory(), null, builders, null);
        runtime.start();

        // create a test module
        Component moduleComponent = MockFactory.createAggregateComponent("module");
        runtime.registerModelObject(moduleComponent);

        Component component = factory.createSystemComponent("NoService", ModuleScopeSystemComponent.class, ModuleScopeSystemComponentImpl.class, Scope.MODULE);
        // do not register the above component!

        AggregateContextImpl moduleContext = (AggregateContextImpl) runtime.getContext("module");
        EntryPoint epSystemBinding = MockFactory.createEPSystemBinding("TestServiceEP", ModuleScopeSystemComponent.class, "NoReference", component);
        moduleContext.registerModelObject(epSystemBinding);

        moduleContext.fireEvent(EventContext.MODULE_START, null);
        try {
            moduleContext.locateService("TestServiceEP");
            fail("Expected " + ServiceUnavailableException.class.getName());
        } catch (ServiceUnavailableException e) {
            // expected
        }
        moduleContext.fireEvent(EventContext.MODULE_STOP, null);
        runtime.stop();
    }

    /**
     * Test two module components that have external services wired to entry points contained in each
     */
    public void testCircularWires() throws Exception {
        RuntimeContext runtime = new RuntimeContextImpl(new NullMonitorFactory(), null, builders, null);
        runtime.start();

        // create a test modules
        Component module1 = MockFactory.createAggregateComponent("module1");
        runtime.registerModelObject(module1);
        Component module2 = MockFactory.createAggregateComponent("module2");
        runtime.registerModelObject(module2);

        AggregateContextImpl moduleContext1 = (AggregateContextImpl) runtime.getContext("module1");
        AggregateContextImpl moduleContext2 = (AggregateContextImpl) runtime.getContext("module2");

        Component component1 = factory.createSystemComponent("Component1", ModuleScopeSystemComponent.class, ModuleScopeSystemComponentImpl.class, Scope.MODULE);
        EntryPoint entryPoint1 = MockFactory.createEPSystemBinding("EntryPoint1", ModuleScopeSystemComponent.class, "Component1", component1);
        ExternalService externalService1 = MockFactory.createESSystemBinding("ExternalService1", "module2/EntryPoint2");
        moduleContext1.registerModelObject(component1);
        moduleContext1.registerModelObject(entryPoint1);
        moduleContext1.registerModelObject(externalService1);

        Component component2 = factory.createSystemComponent("Component2", ModuleScopeSystemComponent.class, ModuleScopeSystemComponentImpl.class, Scope.MODULE);
        EntryPoint entryPoint2 = MockFactory.createEPSystemBinding("EntryPoint2", ModuleScopeSystemComponent.class, "Component2", component2);
        ExternalService externalService2 = MockFactory.createESSystemBinding("ExternalService2", "module1/EntryPoint1");
        moduleContext2.registerModelObject(component2);
        moduleContext2.registerModelObject(entryPoint2);
        moduleContext2.registerModelObject(externalService2);

        moduleContext1.fireEvent(EventContext.MODULE_START, null);
        moduleContext2.fireEvent(EventContext.MODULE_START, null);
        Assert.assertNotNull(moduleContext2.locateInstance("ExternalService2"));
        Assert.assertNotNull(moduleContext1.locateInstance("ExternalService1"));
        runtime.stop();
    }

    /**
     * Tests that a circular reference between an external service in one module and an entry point in another is caught
     * as an error condition FIXME this must be implemented
     */
    public void testInterModuleCircularReference() throws Exception {
        RuntimeContext runtime = new RuntimeContextImpl(new NullMonitorFactory(), null, builders, null);
        runtime.start();

        // create a test modules
        Component module1 = MockFactory.createAggregateComponent("module1");
        runtime.registerModelObject(module1);
        Component module2 = MockFactory.createAggregateComponent("module2");
        runtime.registerModelObject(module2);

        AggregateContextImpl moduleContext1 = (AggregateContextImpl) runtime.getContext("module1");
        AggregateContextImpl moduleContext2 = (AggregateContextImpl) runtime.getContext("module2");
        ExternalService externalService1 = MockFactory.createESSystemBinding("ExternalService1", "module2/EntryPoint2");
        EntryPoint entryPoint1 = MockFactory.createEPSystemBinding("EntryPoint1", ModuleScopeSystemComponent.class,
                "ExternalService1", externalService1);
        ExternalService externalService2 = MockFactory.createESSystemBinding("ExternalService2", "module1/EntryPoint1");
        EntryPoint entryPoint2 = MockFactory.createEPSystemBinding("EntryPoint2", ModuleScopeSystemComponent.class,
                "ExternalService2", externalService2);
        try {
            // FIXME this should throw a circular reference exception
            moduleContext1.registerModelObject(externalService1);
            moduleContext1.registerModelObject(entryPoint1);
            moduleContext2.registerModelObject(externalService2);
            moduleContext2.registerModelObject(entryPoint2);
            // FIXME implement fail("Expected " + ConfigurationException.class.getName());
        } catch (ConfigurationException e) {
            // expected
        }
    }

    public void testRuntimeBuilderAutowire() throws Exception {

        RuntimeContext runtime = new RuntimeContextImpl(new NullMonitorFactory(), null, builders, null);
        runtime.start();

        AggregateContext system = runtime.getSystemContext();
        Component builder = factory.createSystemComponent("TestBuilder", RuntimeConfigurationBuilder.class, TestBuilder.class, Scope.MODULE);
        system.registerModelObject(builder);
        system.fireEvent(EventContext.MODULE_START, null);
        Component module1 = MockFactory.createAggregateComponent("module1");
        runtime.registerModelObject(module1);
        runtime.getContext("module1");
        Assert.assertTrue(((TestBuilder) system.locateInstance("TestBuilder")).invoked());
        system.fireEvent(EventContext.MODULE_STOP, null);
        runtime.stop();

    }

    protected void setUp() throws Exception {
        super.setUp();
        builders = MockFactory.createSystemBuilders();
        factory = new SystemAssemblyFactoryImpl();
    }
}
