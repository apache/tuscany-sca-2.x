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

import junit.framework.Assert;
import junit.framework.TestCase;
import org.osoa.sca.ServiceUnavailableException;

import org.apache.tuscany.core.monitor.impl.NullMonitorFactory;
import org.apache.tuscany.spi.monitor.MonitorFactory;
import org.apache.tuscany.core.builder.ContextFactoryBuilder;
import org.apache.tuscany.core.builder.ContextFactoryBuilderRegistry;
import org.apache.tuscany.core.client.BootstrapHelper;
import org.apache.tuscany.core.config.ConfigurationException;
import org.apache.tuscany.core.context.CompositeContext;
import org.apache.tuscany.core.context.Lifecycle;
import org.apache.tuscany.core.context.event.ModuleStart;
import org.apache.tuscany.core.context.event.ModuleStop;
import org.apache.tuscany.core.context.impl.CompositeContextImpl;
import org.apache.tuscany.core.mock.MockFactory;
import org.apache.tuscany.core.mock.component.ModuleScopeSystemComponent;
import org.apache.tuscany.core.mock.component.ModuleScopeSystemComponentImpl;
import org.apache.tuscany.core.system.assembly.SystemAssemblyFactory;
import org.apache.tuscany.core.system.assembly.impl.SystemAssemblyFactoryImpl;
import org.apache.tuscany.core.system.context.TestBuilder;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.tuscany.model.assembly.ExternalService;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.types.java.JavaServiceContract;

/**
 * Performs basic tests on the runtime context
 * 
 * @version $Rev$ $Date$
 */
public class RuntimeContextImplTestCase extends TestCase {

    private SystemAssemblyFactory systemFactory = new SystemAssemblyFactoryImpl();

    private ContextFactoryBuilderRegistry builderRegistry;

    private SystemAssemblyFactory factory;
    private MonitorFactory monitorFactory;
    private RuntimeContext runtime;

    /**
     * Tests explicit wiring of an external service to a system entry point that is wired to a child system module entry
     * point
     */
    public void testSystemExplicitWiring() throws Exception {
        CompositeContext root = runtime.getRootContext();
        Assert.assertNotNull(root);
        Assert.assertTrue(root.getLifecycleState() == Lifecycle.RUNNING);

        CompositeContext system = runtime.getSystemContext();
        Assert.assertNotNull(system);
        system.registerModelObject(MockFactory.createSystemModule());

        // register a child system context
        system.registerModelObject(MockFactory.createSystemCompositeComponent("system.child"));
        CompositeContext systemChild = (CompositeContext) system.getContext("system.child");
        systemChild.registerModelObject(MockFactory.createSystemChildModule());

        // register a top-level system entry point that exposes the child entry point
        EntryPoint ep = MockFactory.createEPSystemBinding("TestService2EP", ModuleScopeSystemComponent.class, "ref");
        ep.getBindings().add(systemFactory.createSystemBinding());
        Service service = systemFactory.createService();
        service.setName("system.child/TestService2EP");
        ep.getConfiguredReference().getTargetConfiguredServices().get(0).setPort(service);
        JavaServiceContract inter = systemFactory.createJavaServiceContract();
        inter.setInterface(ModuleScopeSystemComponentImpl.class);
        service.setServiceContract(inter);
        system.registerModelObject(ep);
        system.publish(new ModuleStart(this));
        Assert.assertNotNull(system.getContext("TestService1").getInstance(null));
        Assert.assertNotNull(system.getContext("TestService2EP").getInstance(null));

        // create a test module and wire an external service to the system entry point
        Component moduleComponent = MockFactory.createCompositeComponent("test.module");
        runtime.registerModelObject(moduleComponent);
        CompositeContextImpl moduleContext = (CompositeContextImpl) runtime.getContext("test.module");
        Assert.assertNotNull(moduleContext);
        ExternalService es = MockFactory.createESSystemBinding("TestService2ES", "tuscany.system/TestService2EP");
        moduleContext.registerModelObject(es);
        moduleContext.publish(new ModuleStart(this));
        Assert.assertNotNull(moduleContext.getContext("TestService2ES").getInstance(null));

        moduleContext.publish(new ModuleStop(this));
        system.publish(new ModuleStop(this));
    }

    /**
     * Tests autowiring an external service to a system entry point
     */
    public void testSystemAutoWiring() throws Exception {
        CompositeContext root = runtime.getRootContext();
        Assert.assertNotNull(root);
        Assert.assertTrue(root.getLifecycleState() == Lifecycle.RUNNING);

        CompositeContext system = runtime.getSystemContext();
        Assert.assertNotNull(system);
        system.registerModelObject(MockFactory.createSystemModule());

        // create a test module and wire an external service to the system entry point
        Component moduleComponent = MockFactory.createCompositeComponent("test.module");
        runtime.registerModelObject(moduleComponent);
        CompositeContextImpl moduleContext = (CompositeContextImpl) runtime.getContext("test.module");
        Assert.assertNotNull(moduleContext);
        ExternalService es = MockFactory.createAutowirableExternalService("TestService2ES", ModuleScopeSystemComponent.class);
        moduleContext.registerModelObject(es);

        system.publish(new ModuleStart(this));
        moduleContext.publish(new ModuleStart(this));
        // test that the autowire was resolved
        Assert.assertNotNull(moduleContext.getContext("TestService2ES").getInstance(null));

        moduleContext.publish(new ModuleStop(this));
        system.publish(new ModuleStop(this));
    }

    public void testServiceNotFound() throws Exception {
        // create a test module
        Component moduleComponent = MockFactory.createCompositeComponent("module");
        runtime.registerModelObject(moduleComponent);
        CompositeContextImpl moduleContext = (CompositeContextImpl) runtime.getContext("module");
        moduleContext.publish(new ModuleStart(this));
        try {
            moduleContext.locateService("TestService");
            fail("Expected " + ServiceUnavailableException.class.getName());
        } catch (ServiceUnavailableException e) {
            // expected
        }
        moduleContext.publish(new ModuleStop(this));
    }

    public void testExternalServiceReferenceNotFound() throws Exception {
        CompositeContext system = runtime.getSystemContext();

        // create a test module
        Component moduleComponent = MockFactory.createCompositeComponent("module");
        runtime.registerModelObject(moduleComponent);
        CompositeContextImpl moduleContext = (CompositeContextImpl) runtime.getContext("module");
        ExternalService es = MockFactory.createESSystemBinding("TestServiceES", "tuscany.system/TestService1xEP");
        moduleContext.registerModelObject(es);

        // start the modules and test inter-module system wires
        system.publish(new ModuleStart(this));
        moduleContext.publish(new ModuleStart(this));
        try {
            moduleContext.locateService("TestServiceES");
            fail("Expected " + ServiceUnavailableException.class.getName());
        } catch (ServiceUnavailableException e) {
            // expected
        }
        moduleContext.publish(new ModuleStop(this));
        system.publish(new ModuleStop(this));
    }

    public void testEntryPointReferenceNotFound() throws Exception {
        // create a test module
        Component moduleComponent = MockFactory.createCompositeComponent("module");
        runtime.registerModelObject(moduleComponent);

        Component component = factory.createSystemComponent("NoService", ModuleScopeSystemComponent.class, ModuleScopeSystemComponentImpl.class, Scope.MODULE);
        // do not register the above component!

        CompositeContextImpl moduleContext = (CompositeContextImpl) runtime.getContext("module");
        EntryPoint epSystemBinding = MockFactory.createEPSystemBinding("TestServiceEP", ModuleScopeSystemComponent.class, "NoReference", component);
        moduleContext.registerModelObject(epSystemBinding);

        moduleContext.publish(new ModuleStart(this));
        try {
            moduleContext.locateService("TestServiceEP");
            fail("Expected " + ServiceUnavailableException.class.getName());
        } catch (ServiceUnavailableException e) {
            // expected
        }
        moduleContext.publish(new ModuleStop(this));
    }

    /**
     * Test two module components that have external services wired to entry points contained in each
     */
    public void testCircularWires() throws Exception {
        // create a test modules
        Component module1 = MockFactory.createCompositeComponent("module1");
        runtime.registerModelObject(module1);
        Component module2 = MockFactory.createCompositeComponent("module2");
        runtime.registerModelObject(module2);

        CompositeContextImpl moduleContext1 = (CompositeContextImpl) runtime.getContext("module1");
        CompositeContextImpl moduleContext2 = (CompositeContextImpl) runtime.getContext("module2");

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

        moduleContext1.publish(new ModuleStart(this));
        moduleContext2.publish(new ModuleStart(this));
        Assert.assertNotNull(moduleContext2.getContext("ExternalService2").getInstance(null));
        Assert.assertNotNull(moduleContext1.getContext("ExternalService1").getInstance(null));
    }

    /**
     * Tests that a circular reference between an external service in one module and an entry point in another is caught
     * as an error condition FIXME this must be implemented
     */
    public void testInterModuleCircularReference() throws Exception {
        // create a test modules
        Component module1 = MockFactory.createCompositeComponent("module1");
        runtime.registerModelObject(module1);
        Component module2 = MockFactory.createCompositeComponent("module2");
        runtime.registerModelObject(module2);

        CompositeContextImpl moduleContext1 = (CompositeContextImpl) runtime.getContext("module1");
        CompositeContextImpl moduleContext2 = (CompositeContextImpl) runtime.getContext("module2");
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


        CompositeContext system = runtime.getSystemContext();
        Component builder = factory.createSystemComponent("TestBuilder", ContextFactoryBuilder.class, TestBuilder.class, Scope.MODULE);
        system.registerModelObject(builder);
        system.publish(new ModuleStart(this));
        Component module1 = MockFactory.createCompositeComponent("module1");
        runtime.registerModelObject(module1);
        runtime.getContext("module1");
        Assert.assertTrue(((TestBuilder) system.getContext("TestBuilder").getInstance(null)).invoked());
        system.publish(new ModuleStop(this));
    }

    protected void setUp() throws Exception {
        super.setUp();
        monitorFactory = new NullMonitorFactory();
        builderRegistry = BootstrapHelper.bootstrapContextFactoryBuilders(monitorFactory);
        factory = new SystemAssemblyFactoryImpl();

        runtime = new RuntimeContextImpl(monitorFactory, builderRegistry, null);
        runtime.start();
    }

    protected void tearDown() throws Exception {
        runtime.stop();
        super.tearDown();
    }
}
