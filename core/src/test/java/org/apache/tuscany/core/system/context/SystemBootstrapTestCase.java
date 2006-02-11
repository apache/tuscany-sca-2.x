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
import org.apache.tuscany.core.context.AutowireContext;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.context.ContextConstants;
import org.apache.tuscany.core.context.impl.AggregateContextImpl;
import org.apache.tuscany.core.mock.MockSystemAssemblyFactory;
import org.apache.tuscany.core.mock.component.GenericSystemComponent;
import org.apache.tuscany.core.mock.component.ModuleScopeSystemComponent;
import org.apache.tuscany.core.mock.component.ModuleScopeSystemComponentImpl;
import org.apache.tuscany.core.system.assembly.pojo.PojoSystemBinding;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.ExternalService;
import org.apache.tuscany.model.assembly.Module;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.assembly.pojo.PojoConfiguredService;
import org.apache.tuscany.model.assembly.pojo.PojoEntryPoint;
import org.apache.tuscany.model.assembly.pojo.PojoInterface;
import org.apache.tuscany.model.assembly.pojo.PojoInterfaceType;
import org.apache.tuscany.model.assembly.pojo.PojoJavaInterface;
import org.apache.tuscany.model.assembly.pojo.PojoService;
import org.apache.tuscany.common.monitor.impl.NullMonitorFactory;

/**
 * Tests bootstrapping a system module
 * 
 * @version $Rev$ $Date$
 */
public class SystemBootstrapTestCase extends TestCase {
    private List<RuntimeConfigurationBuilder> builders;

    /**
     * Simulates booting a runtime process
     */
    public void testBoot() throws Exception {
        RuntimeContext runtimeContext = new RuntimeContextImpl(new NullMonitorFactory(), builders);
        runtimeContext.start();

        AggregateContext systemContext = runtimeContext.getSystemContext();
        Assert.assertNotNull(systemContext);
        Module systemModule = MockSystemAssemblyFactory.createSystemModule();
       // MockSystemAssemblyFactory.buildModule(systemModule, systemContext);
        systemContext.registerModelObject(systemModule);

        // create a test module
        Component moduleComponent = MockSystemAssemblyFactory.createComponent("module", AggregateContextImpl.class
                .getName(), ContextConstants.AGGREGATE_SCOPE_ENUM);
        runtimeContext.registerModelObject(moduleComponent);
        AggregateContextImpl moduleContext = (AggregateContextImpl) runtimeContext.getContext("module");
        Assert.assertNotNull(moduleContext);
        ExternalService es = MockSystemAssemblyFactory.createExternalService("TestServiceES", "tuscany.system/TestService1EP");
        moduleContext.registerModelObject(es);

        // start the modules and test inter-module system wires
        systemContext.fireEvent(EventContext.MODULE_START, null);
        moduleContext.fireEvent(EventContext.MODULE_START, null);

        Assert.assertNotNull(systemContext.locateInstance("TestService1EP"));
        GenericSystemComponent testService = (GenericSystemComponent) systemContext.locateInstance("TestService1");
        Assert.assertNotNull(testService);
        GenericSystemComponent testES = (GenericSystemComponent) moduleContext.locateInstance("TestServiceES");
        Assert.assertNotNull(testES);
        Assert.assertSame(testService, testES);
    }

    public void testRuntimeBoot() throws Exception {
        RuntimeContext runtime = new RuntimeContextImpl(new NullMonitorFactory(), builders);
        runtime.start();
        runtime.getRootContext();

        AggregateContext system = runtime.getSystemContext();
        system.registerModelObject(MockSystemAssemblyFactory.createSystemModule());
        system.registerModelObject(MockSystemAssemblyFactory.createComponent("module2", SystemAggregateContextImpl.class
                .getName(), ContextConstants.AGGREGATE_SCOPE_ENUM));
        AggregateContext systemModule2 = (AggregateContext) system.getContext("module2");
        systemModule2.registerModelObject(MockSystemAssemblyFactory.createSystemChildModule());

        PojoEntryPoint ep = MockSystemAssemblyFactory.createEntryPoint("TestService2EP", ModuleScopeSystemComponent.class, "ref");
        ep.addBinding(new PojoSystemBinding());
        Service service = new PojoService();
        service.setName("module2/TestService2EP");
        ((PojoConfiguredService) ep.getConfiguredReference().getConfiguredServices().get(0)).setService(service);
        PojoInterface inter = new PojoJavaInterface();
        PojoInterfaceType interType = new PojoInterfaceType();
        interType.setInstanceClass(ModuleScopeSystemComponentImpl.class);
        inter.setInterfaceType(interType);
        service.setInterfaceContract(inter);
        ep.setInterfaceContract(inter);
        system.registerModelObject(ep);
        system.fireEvent(EventContext.MODULE_START, null);
        Assert.assertNotNull(system.locateInstance("TestService1"));
        Assert.assertNotNull(system.locateInstance("TestService2EP"));

        Assert.assertNotNull(((AutowireContext) system).resolveInstance(ModuleScopeSystemComponentImpl.class));
        // create a test module
        Component moduleComponent = MockSystemAssemblyFactory.createComponent("test.module", AggregateContextImpl.class
                .getName(), ContextConstants.AGGREGATE_SCOPE_ENUM);
        runtime.registerModelObject(moduleComponent);
        AggregateContextImpl moduleContext = (AggregateContextImpl) runtime.getContext("test.module");
        Assert.assertNotNull(moduleContext);
        ExternalService es = MockSystemAssemblyFactory.createExternalService("TestService2ES", "tuscany.system/TestService2EP");
        moduleContext.registerModelObject(es);
        moduleContext.fireEvent(EventContext.MODULE_START, null);
        Assert.assertNotNull(moduleContext.locateInstance("TestService2ES"));

        moduleContext.fireEvent(EventContext.MODULE_STOP, null);
        system.fireEvent(EventContext.MODULE_STOP, null);
        runtime.stop();
    }

//    public void testT() throws Exception{
//        
//        List<RuntimeConfigurationBuilder> builders = new ArrayList();
//        builders.add((new SystemComponentContextBuilder()));
//        builders.add(new SystemEntryPointBuilder());
//        builders.add(new SystemExternalServiceBuilder());
//
//        RuntimeContext runtimeContext = new RuntimeContextImpl(new NullMonitorFactory(), builders, null);
//        runtimeContext.start();
//        // create the system context
//        Component component = MockSystemAssemblyFactory.createComponent(RuntimeContext.SYSTEM,
//                SystemAggregateComponentContextImpl.class.getName(), ModuleConstants.AGGREGATE_SCOPE_ENUM);
//        runtimeContext.registerModelObject(component);
//        AggregateComponentContext systemContext = runtimeContext.getSystemContext();
//        Assert.assertNotNull(systemContext);
//        Module systemModule = MockSystemAssemblyFactory.createSystemModule();
//        systemContext.registerModelObject(systemModule);
//
//        // create a test module
//        Component moduleComponent = MockSystemAssemblyFactory.createComponent("module", AggregateComponentContextImpl.class
//                .getName(), ModuleConstants.AGGREGATE_SCOPE_ENUM);
//        runtimeContext.registerModelObject(moduleComponent);
//        AggregateComponentContextImpl moduleContext = (AggregateComponentContextImpl) runtimeContext.getContext("module");
//        Assert.assertNotNull(moduleContext);
//        ExternalService es = MockSystemAssemblyFactory.createExternalService("TestServiceES", "tuscany.system/TestService1xEP");
//        moduleContext.registerModelObject(es);
//
//        // start the modules and test inter-module system wires
//        systemContext.fireEvent(EventContext.MODULE_START, null);
//        moduleContext.fireEvent(EventContext.MODULE_START, null);
//
//        moduleContext.locateService("TestServiceES");
//    }


    protected void setUp() throws Exception {
        super.setUp();
        builders = MockSystemAssemblyFactory.createBuilders();
    }
}
